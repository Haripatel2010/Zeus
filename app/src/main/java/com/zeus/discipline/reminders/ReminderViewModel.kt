
package com.zeus.discipline.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeus.discipline.reminders.data.ReminderRepository
import com.zeus.discipline.reminders.model.Reminder
import com.zeus.discipline.reminders.model.ReminderCategory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReminderViewModel(
    private val repository: ReminderRepository
) : ViewModel() {

    val reminders = repository.getAllReminders()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val categories = repository.getAllCategories()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun addReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.addReminder(reminder)
        }
    }

    fun updateReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.updateReminder(reminder)
        }
    }

    fun completeReminder(reminder: Reminder, completed: Boolean) {
        viewModelScope.launch {
            repository.setCompleted(reminder, completed)
        }
    }

    fun deleteReminder(id: String) {
        viewModelScope.launch {
            repository.deleteReminder(id)
        }
    }

    fun addCategory(category: ReminderCategory) {
        viewModelScope.launch {
            repository.addCategory(category)
        }
    }

    fun renameCategory(id: String, name: String) {
        viewModelScope.launch {
            repository.renameCategory(id, name)
        }
    }

    fun deleteCategory(id: String) {
        viewModelScope.launch {
            repository.deleteCategory(id)
        }
    }
}
