
package com.zeus.discipline.reminders.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderCategoryDao {

    // Observe all categories in display order
    @Query("SELECT * FROM reminder_categories ORDER BY sortOrder ASC")
    fun getAllCategories(): Flow<List<ReminderCategoryEntity>>

    // Find a category
    @Query("SELECT * FROM reminder_categories WHERE id = :categoryId LIMIT 1")
    suspend fun getCategoryById(categoryId: String): ReminderCategoryEntity?

    // Create or replace a category
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: ReminderCategoryEntity)

    // Update category
    @Update
    suspend fun updateCategory(category: ReminderCategoryEntity)

    // Rename category
    @Query("UPDATE reminder_categories SET name = :newName WHERE id = :categoryId")
    suspend fun renameCategory(categoryId: String, newName: String)

    // Change category order
    @Query("UPDATE reminder_categories SET sortOrder = :newOrder WHERE id = :categoryId")
    suspend fun updateCategoryOrder(categoryId: String, newOrder: Int)

    // Delete category
    @Query("DELETE FROM reminder_categories WHERE id = :categoryId")
    suspend fun deleteCategory(categoryId: String)

    // Count categories
    @Query("SELECT COUNT(*) FROM reminder_categories")
    suspend fun getCategoryCount(): Int
}
