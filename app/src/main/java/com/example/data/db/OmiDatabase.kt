package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [OmiRuleEntity::class], version = 1, exportSchema = false)
abstract class OmiDatabase : RoomDatabase() {

    abstract fun omiRuleDao(): OmiRuleDao

    companion object {
        @Volatile
        private var INSTANCE: OmiDatabase? = null

        fun getDatabase(context: Context): OmiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OmiDatabase::class.java,
                    "omi_game_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
