
package com.zeus.discipline.reminders.model

import java.util.UUID

enum class ReminderPriority {
    NORMAL,
    IMPORTANT
}

enum class ReminderAlertType {
    NOTIFICATION,
    SILENT,
    NONE
}

enum class RepeatFrequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY,
    CUSTOM
}

enum class RepeatEndType {
    NEVER,
    AFTER_OCCURRENCES,
    ON_DATE
}

enum class LocationTriggerType {
    ARRIVAL,
    DEPARTURE
}

data class RepeatRule(
    val frequency: RepeatFrequency,
    val interval: Int = 1,
    val daysOfWeek: List<Int> = emptyList(),
    val endType: RepeatEndType = RepeatEndType.NEVER,
    val maxOccurrences: Int? = null,
    val endAtMillis: Long? = null
)

data class ReminderLocation(
    val placeName: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Float = 150f,
    val triggerType: LocationTriggerType
)

data class Reminder(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val notes: String = "",
    val categoryId: String? = null,
    val dueAtMillis: Long? = null,
    val isAllDay: Boolean = false,
    val priority: ReminderPriority = ReminderPriority.NORMAL,
    val alertType: ReminderAlertType = ReminderAlertType.NOTIFICATION,
    val repeatRule: RepeatRule? = null,
    val location: ReminderLocation? = null,
    val isCompleted: Boolean = false,
    val completedAtMillis: Long? = null,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val updatedAtMillis: Long = System.currentTimeMillis()
)
