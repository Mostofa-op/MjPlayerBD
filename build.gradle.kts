import java.io.File

android {
    compileSdk = 34 // আপনার প্রজেক্টের Target/Compile SDK অনুযায়ী রাখুন

    defaultConfig {
        applicationId = "com.mjplayer.bd" // আপনার অ্যাপের Package Name
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    signingConfigs {
        create("release") {
            // CI/CD (GitHub Actions) থেকে Environment Variables চেক করবে
            val keystoreFilePath = System.getenv("KEYSTORE_FILE")
            
            if (keystoreFilePath != null && File(keystoreFilePath).exists()) {
                // GitHub Actions থেকে রিলিজ বিল্ড হলে
                storeFile = File(keystoreFilePath)
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
            } else if (file("release.keystore").exists()) {
                // লোকাল পিসিতে থাকলে
                storeFile = file("release.keystore")
                storePassword = System.getenv("KEYSTORE_PASSWORD") ?: "your_local_password"
                keyAlias = System.getenv("KEY_ALIAS") ?: "your_local_alias"
                keyPassword = System.getenv("KEY_PASSWORD") ?: "your_local_key_password"
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Release build-এ তৈরি করা signingConfig সেট করা হলো
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
