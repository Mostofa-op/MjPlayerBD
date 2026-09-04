package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoHistoryDao {
    @Query("SELECT * FROM video_history ORDER BY lastPlayedTimestamp DESC")
    fun getAllHistory(): Flow<List<VideoHistoryEntity>>

    @Query("SELECT * FROM video_history WHERE isFavorite = 1 ORDER BY lastPlayedTimestamp DESC")
    fun getFavorites(): Flow<List<VideoHistoryEntity>>

    @Query("SELECT * FROM video_history WHERE uriString = :uri LIMIT 1")
    suspend fun getByUri(uri: String): VideoHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: VideoHistoryEntity)

    @Query("UPDATE video_history SET lastPositionMs = :position, lastPlayedTimestamp = :timestamp WHERE uriString = :uri")
    suspend fun updateProgress(uri: String, position: Long, timestamp: Long)

    @Query("UPDATE video_history SET isFavorite = :isFavorite WHERE uriString = :uri")
    suspend fun updateFavorite(uri: String, isFavorite: Boolean)

    @Query("DELETE FROM video_history WHERE uriString = :uri")
    suspend fun delete(uri: String)

    @Query("DELETE FROM video_history")
    suspend fun clearAll()
}
