package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [VideoHistoryEntity::class], version = 1, exportSchema = false)
abstract class LiquidPlayerDatabase : RoomDatabase() {
    abstract fun videoHistoryDao(): VideoHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: LiquidPlayerDatabase? = null

        fun getDatabase(context: Context): LiquidPlayerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LiquidPlayerDatabase::class.java,
                    "liquid_player.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
