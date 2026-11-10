
package com.zeus.discipline.reminders.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminder_categories")
data class ReminderCategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val createdAtMillis: Long,
    val sortOrder: Int
)
