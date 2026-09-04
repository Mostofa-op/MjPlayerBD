package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.ai.AiVideoEngine
import com.example.data.model.VideoItem
import com.example.data.repository.VideoRepository
import com.example.player.LiquidPlayerManager
import com.example.ui.library.LibraryScreen
import com.example.ui.library.LibraryViewModel
import com.example.ui.player.VideoPlayerScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var aiEngine: AiVideoEngine
    private lateinit var playerManager: LiquidPlayerManager
    private lateinit var repository: VideoRepository
    private lateinit var libraryViewModel: LibraryViewModel

    private var intentVideoToPlay by mutableStateOf<VideoItem?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        aiEngine = AiVideoEngine()
        playerManager = LiquidPlayerManager(this, aiEngine)
        repository = VideoRepository(this)
        libraryViewModel = LibraryViewModel(repository)

        handleIntent(intent)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF060913)
                ) {
                    var currentPlayingVideo by remember { mutableStateOf<VideoItem?>(null) }
                    val scope = rememberCoroutineScope()

                    // Handle video from external intent (e.g. user clicked a video in file manager)
                    LaunchedEffect(intentVideoToPlay) {
                        intentVideoToPlay?.let {
                            currentPlayingVideo = it
                            intentVideoToPlay = null
                        }
                    }

                    if (currentPlayingVideo != null) {
                        val activeVideo = currentPlayingVideo!!
                        BackHandler {
                            val curPos = playerManager.uiState.value.currentPositionMs
                            scope.launch {
                                repository.saveProgress(activeVideo, curPos)
                            }
                            playerManager.togglePlayPause()
                            currentPlayingVideo = null
                        }

                        VideoPlayerScreen(
                            playerManager = playerManager,
                            video = activeVideo,
                            onBack = {
                                val curPos = playerManager.uiState.value.currentPositionMs
                                scope.launch {
                                    repository.saveProgress(activeVideo, curPos)
                                }
                                playerManager.togglePlayPause()
                                currentPlayingVideo = null
                            }
                        )
                    } else {
                        LibraryScreen(
                            viewModel = libraryViewModel,
                            onVideoSelected = { video ->
                                currentPlayingVideo = video
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val uri = intent?.data ?: return
        lifecycleScope.launch {
            val video = repository.createVideoFromUri(uri)
            intentVideoToPlay = video
        }
    }

    override fun onStop() {
        super.onStop()
        val uiState = playerManager.uiState.value
        if (!uiState.isBackgroundPlayEnabled) {
            playerManager.getPlayer()?.pause()
        }
        playerManager.activeVideo?.let { video ->
            lifecycleScope.launch {
                repository.saveProgress(video, uiState.currentPositionMs)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerManager.release()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme { Greeting("Android") }
}

