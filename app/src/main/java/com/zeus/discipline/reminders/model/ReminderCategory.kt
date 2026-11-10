
package com.zeus.discipline.reminders.model

import java.util.UUID

enum class ReminderSmartList {
    ALL,
    TODAY,
    SCHEDULED,
    IMPORTANT,
    PLACE,
    NO_ALERT,
    SELECTED,
    COMPLETED
}

data class ReminderCategory(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val sortOrder: Int = 0
)

object DefaultReminderCategories {

    val names = listOf(
        "Personal",
        "Study",
        "Work",
        "Health",
        "Shopping"
    )

    fun createDefaults(): List<ReminderCategory> {
        return names.mapIndexed { index, name ->
            ReminderCategory(
                name = name,
                sortOrder = index
            )
        }
    }
}
