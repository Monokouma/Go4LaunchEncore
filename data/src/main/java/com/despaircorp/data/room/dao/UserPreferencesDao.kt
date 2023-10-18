package com.despaircorp.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.despaircorp.data.room.entities.UserPreferencesDataEntity
import com.despaircorp.domain.room.model.NotificationsStateEnum
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferencesDao {
    @Query("SELECT EXISTS(SELECT * FROM user_preferences_table)")
    fun exist(): Boolean
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserPreferencesEntity(userPreferencesEntity: UserPreferencesDataEntity): Long
    
    @Query("SELECT * FROM user_preferences_table")
    fun getUserPreferencesAsFlow(): Flow<UserPreferencesDataEntity>
    
    @Query("SELECT * FROM user_preferences_table")
    fun getUserPreferences(): UserPreferencesDataEntity
    
    @Query("UPDATE user_preferences_table SET isNotificationEnable=:isNotificationEnabled")
    suspend fun updateUserPreferencesNotification(isNotificationEnabled: NotificationsStateEnum): Int
}