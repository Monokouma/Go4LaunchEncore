package com.despaircorp.data.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.despaircorp.data.room.dao.UserPreferencesDao
import com.despaircorp.data.room.entities.UserPreferencesDataEntity


@Database(
    entities = [
        UserPreferencesDataEntity::class,
    ],
    version = 1,
    exportSchema = false
)
public abstract class Go4LunchRoomDatabase : RoomDatabase() {
    
    abstract fun userPreferencesDao(): UserPreferencesDao
    
    
    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: Go4LunchRoomDatabase? = null
        
        fun getDatabase(context: Context): Go4LunchRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    Go4LunchRoomDatabase::class.java,
                    "go_for_lunch_database"
                ).build()
                
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
    
}