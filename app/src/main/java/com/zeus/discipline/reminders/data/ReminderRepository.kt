
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

    fun getAllReminders(): Flow<List<Reminder>> =
        reminderDao.getAllReminders().map { list ->
            list.map(ReminderMapper::toModel)
        }

    fun getPendingReminders(): Flow<List<Reminder>> =
        reminderDao.getPendingReminders().map { list ->
            list.map(ReminderMapper::toModel)
        }

    fun getCompletedReminders(): Flow<List<Reminder>> =
        reminderDao.getCompletedReminders().map { list ->
            list.map(ReminderMapper::toModel)
        }

    fun getImportantReminders(): Flow<List<Reminder>> =
        reminderDao.getImportantReminders().map { list ->
            list.map(ReminderMapper::toModel)
        }

    fun getRemindersWithoutAlerts(): Flow<List<Reminder>> =
        reminderDao.getRemindersWithoutAlerts().map { list ->
            list.map(ReminderMapper::toModel)
        }

    fun getRemindersByCategory(
        categoryId: String
    ): Flow<List<Reminder>> =
        reminderDao.getRemindersByCategory(categoryId).map { list ->
            list.map(ReminderMapper::toModel)
        }

    suspend fun getReminderById(id: String): Reminder? =
        reminderDao.getReminderById(id)?.let {
            ReminderMapper.toModel(it)
        }

    suspend fun addReminder(reminder: Reminder) {
        reminderDao.insertReminder(ReminderMapper.toEntity(reminder))
    }

    suspend fun updateReminder(reminder: Reminder) {
        reminderDao.updateReminder(ReminderMapper.toEntity(reminder))
    }

    suspend fun setCompleted(reminder: Reminder, completed: Boolean) {
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

    suspend fun deleteReminder(id: String) {
        reminderDao.deleteReminderById(id)
    }

    fun getAllCategories(): Flow<List<ReminderCategory>> =
        categoryDao.getAllCategories().map { list ->
            list.map(ReminderMapper::categoryToModel)
        }

    suspend fun addCategory(category: ReminderCategory) {
        categoryDao.insertCategory(
            ReminderMapper.categoryToEntity(category)
        )
    }

    suspend fun renameCategory(id: String, newName: String) {
        categoryDao.renameCategory(id, newName.trim())
    }

    suspend fun updateCategoryOrder(id: String, order: Int) {
        categoryDao.updateCategoryOrder(id, order)
    }

    suspend fun deleteCategory(id: String) {
        categoryDao.deleteCategory(id)
    }
}
