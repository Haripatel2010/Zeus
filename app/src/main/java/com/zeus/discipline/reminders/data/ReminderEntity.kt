
package com.zeus.discipline.reminders.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(

    @PrimaryKey
    val id: String,

    val title: String,

    val notes: String,

    val categoryId: String?,

    val dueAtMillis: Long?,

    val isAllDay: Boolean,

    val priority: String,

    val alertType: String,

    val repeatRuleJson: String?,

    val locationJson: String?,

    val isCompleted: Boolean,

    val completedAtMillis: Long?,

    val createdAtMillis: Long,

    val updatedAtMillis: Long
)
