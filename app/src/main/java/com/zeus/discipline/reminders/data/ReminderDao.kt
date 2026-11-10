
package com.zeus.discipline.reminders.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    // Observe all reminders
    @Query("SELECT * FROM reminders ORDER BY createdAtMillis DESC")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    // Observe pending reminders
    @Query("SELECT * FROM reminders WHERE isCompleted = 0 ORDER BY dueAtMillis ASC")
    fun getPendingReminders(): Flow<List<ReminderEntity>>

    // Observe completed reminders
    @Query("SELECT * FROM reminders WHERE isCompleted = 1 ORDER BY completedAtMillis DESC")
    fun getCompletedReminders(): Flow<List<ReminderEntity>>

    // Find one reminder
    @Query("SELECT * FROM reminders WHERE id = :reminderId LIMIT 1")
    suspend fun getReminderById(reminderId: String): ReminderEntity?

    // Find reminders by category
    @Query("SELECT * FROM reminders WHERE categoryId = :categoryId ORDER BY dueAtMillis ASC")
    fun getRemindersByCategory(categoryId: String): Flow<List<ReminderEntity>>

    // Find important reminders
    @Query("SELECT * FROM reminders WHERE priority = 'IMPORTANT' AND isCompleted = 0 ORDER BY dueAtMillis ASC")
    fun getImportantReminders(): Flow<List<ReminderEntity>>

    // Find reminders without alerts
    @Query("SELECT * FROM reminders WHERE alertType = 'NONE' AND isCompleted = 0")
    fun getRemindersWithoutAlerts(): Flow<List<ReminderEntity>>

    // Insert or replace reminder
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity)

    // Update reminder
    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    // Delete reminder
    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    // Delete by ID
    @Query("DELETE FROM reminders WHERE id = :reminderId")
    suspend fun deleteReminderById(reminderId: String)

    // Delete all reminders
    @Query("DELETE FROM reminders")
    suspend fun deleteAllReminders()

    // Count all reminders
    @Query("SELECT COUNT(*) FROM reminders")
    fun getReminderCount(): Flow<Int>
}
