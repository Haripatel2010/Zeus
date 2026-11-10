
package com.zeus.discipline.reminders.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderCategoryDao {

    @Query("SELECT * FROM reminder_categories ORDER BY sortOrder ASC")
    fun getAllCategories(): Flow<List<ReminderCategoryEntity>>

    @Query("SELECT * FROM reminder_categories WHERE id = :categoryId LIMIT 1")
    suspend fun getCategoryById(categoryId: String): ReminderCategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: ReminderCategoryEntity)

    @Update
    suspend fun updateCategory(category: ReminderCategoryEntity)

    @Query("UPDATE reminder_categories SET name = :newName WHERE id = :categoryId")
    suspend fun renameCategory(categoryId: String, newName: String)

    @Query("UPDATE reminder_categories SET sortOrder = :newOrder WHERE id = :categoryId")
    suspend fun updateCategoryOrder(categoryId: String, newOrder: Int)

    @Query("DELETE FROM reminder_categories WHERE id = :categoryId")
    suspend fun deleteCategory(categoryId: String)

    @Query("SELECT COUNT(*) FROM reminder_categories")
    suspend fun getCategoryCount(): Int
}
