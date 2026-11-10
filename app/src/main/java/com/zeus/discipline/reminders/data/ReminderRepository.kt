
package com.zeus.discipline.reminders.data

import com.zeus.discipline.reminders.model.Reminder
import com.zeus.discipline.reminders.model.ReminderCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReminderRepository(
    private val database: ReminderDatabase
) {

    private val reminderDao = database.reminderDao()
    private val categoryDao = database.reminderCategoryDao()

    // All reminders
    fun getAllReminders(): Flow<List<Reminder>> {
        return reminderDao.getAllReminders().map { list ->
            list.map(ReminderMapper::toModel)
        }
    }

    // Pending reminders
    fun getPendingReminders(): Flow<List<Reminder>> {
        return reminderDao.getPendingReminders().map { list ->
            list.map(ReminderMapper::toModel)
        }
    }

    // Completed reminders
    fun getCompletedReminders(): Flow<List<Reminder>> {
        return reminderDao.getCompletedReminders().map { list ->
            list.map(ReminderMapper::toModel)
        }
    }

    // Important reminders
    fun getImportantReminders(): Flow<List<Reminder>> {
        return reminderDao.getImportantReminders().map { list ->
            list.map(ReminderMapper::toModel)
        }
    }

    // Reminders without alerts
    fun getRemindersWithoutAlerts(): Flow<List<Reminder>> {
        return reminderDao.getRemindersWithoutAlerts().map { list ->
            list.map(ReminderMapper::toModel)
        }
    }

    // Reminders by category
    fun getRemindersByCategory(
        categoryId: String
    ): Flow<List<Reminder>> {
        return reminderDao.getRemindersByCategory(categoryId).map { list ->
            list.map(ReminderMapper::toModel)
        }
    }

    // Find one reminder
    suspend fun getReminderById(id: String): Reminder? {
        return reminderDao.getReminderById(id)?.let {
            ReminderMapper.toModel(it)
        }
    }

    // Save a new reminder
    suspend fun addReminder(reminder: Reminder) {
        reminderDao.insertReminder(
            ReminderMapper.toEntity(reminder)
        )
    }

    // Update a reminder
    suspend fun updateReminder(reminder: Reminder) {
        reminderDao.updateReminder(
            ReminderMapper.toEntity(reminder)
        )
    }

    // Mark complete or incomplete
    suspend fun setCompleted(
        reminder: Reminder,
        completed: Boolean
    ) {
        val updated = reminder.copy(
            isCompleted = completed,
            completedAtMillis = if (completed) {
                System.currentTimeMillis()
            } else {
                null
            },
            updatedAtMillis = System.currentTimeMillis()
        )

        updateReminder(updated)
    }

    // Delete reminder
    suspend fun deleteReminder(id: String) {
        reminderDao.deleteReminderById(id)
    }

    // Observe categories
    fun getAllCategories(): Flow<List<ReminderCategory>> {
        return categoryDao.getAllCategories().map { list ->
            list.map(ReminderMapper::categoryToModel)
        }
    }

    // Add category
    suspend fun addCategory(category: ReminderCategory) {
        categoryDao.insertCategory(
            ReminderMapper.categoryToEntity(category)
        )
    }

    // Rename category
    suspend fun renameCategory(id: String, newName: String) {
        categoryDao.renameCategory(id, newName.trim())
    }

    // Update category order
    suspend fun updateCategoryOrder(id: String, order: Int) {
        categoryDao.updateCategoryOrder(id, order)
    }

    // Delete category
    suspend fun deleteCategory(id: String) {
        categoryDao.deleteCategory(id)
    }
}
