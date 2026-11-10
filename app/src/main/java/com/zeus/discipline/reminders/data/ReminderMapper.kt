package com.zeus.discipline.reminders.data

import com.zeus.discipline.reminders.model.*
import org.json.JSONArray
import org.json.JSONObject

object ReminderMapper {

    fun toEntity(reminder: Reminder): ReminderEntity {
        return ReminderEntity(
            id = reminder.id,
            title = reminder.title,
            notes = reminder.notes,
            categoryId = reminder.categoryId,
            dueAtMillis = reminder.dueAtMillis,
            isAllDay = reminder.isAllDay,
            priority = reminder.priority.name,
            alertType = reminder.alertType.name,
            repeatRuleJson = reminder.repeatRule?.let {
                repeatRuleToJson(it)
            },
            locationJson = reminder.location?.let {
                locationToJson(it)
            },
            isCompleted = reminder.isCompleted,
            completedAtMillis = reminder.completedAtMillis,
            createdAtMillis = reminder.createdAtMillis,
            updatedAtMillis = reminder.updatedAtMillis
        )
    }

    fun toModel(entity: ReminderEntity): Reminder {
        return Reminder(
            id = entity.id,
            title = entity.title,
            notes = entity.notes,
            categoryId = entity.categoryId,
            dueAtMillis = entity.dueAtMillis,
            isAllDay = entity.isAllDay,
            priority = enumValueOrDefault(
                entity.priority,
                ReminderPriority.NORMAL
            ),
            alertType = enumValueOrDefault(
                entity.alertType,
                ReminderAlertType.NOTIFICATION
            ),
            repeatRule = entity.repeatRuleJson?.let {
                repeatRuleFromJson(it)
            },
            location = entity.locationJson?.let {
                locationFromJson(it)
            },
            isCompleted = entity.isCompleted,
            completedAtMillis = entity.completedAtMillis,
            createdAtMillis = entity.createdAtMillis,
            updatedAtMillis = entity.updatedAtMillis
        )
    }

    fun categoryToEntity(
        category: ReminderCategory
    ): ReminderCategoryEntity {
        return ReminderCategoryEntity(
            id = category.id,
            name = category.name,
            createdAtMillis = category.createdAtMillis,
            sortOrder = category.sortOrder
        )
    }

    fun categoryToModel(
        entity: ReminderCategoryEntity
    ): ReminderCategory {
        return ReminderCategory(
            id = entity.id,
            name = entity.name,
            createdAtMillis = entity.createdAtMillis,
            sortOrder = entity.sortOrder
        )
    }

    private inline fun <reified T : Enum<T>> enumValueOrDefault(
        value: String,
        default: T
    ): T {
        return runCatching {
            enumValueOf<T>(value)
        }.getOrDefault(default)
    }

    private fun repeatRuleToJson(rule: RepeatRule): String {
        return JSONObject().apply {
            put("frequency", rule.frequency.name)
            put("interval", rule.interval)
            put("daysOfWeek", JSONArray(rule.daysOfWeek))
            put("endType", rule.endType.name)
            put("maxOccurrences", rule.maxOccurrences ?: JSONObject.NULL)
            put("endAtMillis", rule.endAtMillis ?: JSONObject.NULL)
        }.toString()
    }

    private fun repeatRuleFromJson(json: String): RepeatRule? {
        return runCatching {
            val obj = JSONObject(json)
            val days = obj.optJSONArray("daysOfWeek") ?: JSONArray()

            RepeatRule(
                frequency = enumValueOrDefault(
                    obj.getString("frequency"),
                    RepeatFrequency.DAILY
                ),
                interval = obj.optInt("interval", 1).coerceAtLeast(1),
                daysOfWeek = (0 until days.length()).map {
                    days.getInt(it)
                },
                endType = enumValueOrDefault(
                    obj.optString("endType", "NEVER"),
                    RepeatEndType.NEVER
                ),
                maxOccurrences = if (
                    obj.has("maxOccurrences") &&
                    !obj.isNull("maxOccurrences")
                ) obj.optInt("maxOccurrences") else null,
                endAtMillis = if (
                    obj.has("endAtMillis") &&
                    !obj.isNull("endAtMillis")
                ) obj.optLong("endAtMillis") else null
            )
        }.getOrNull()
    }

    private fun locationToJson(location: ReminderLocation): String {
        return JSONObject().apply {
            put("placeName", location.placeName)
            put("latitude", location.latitude)
            put("longitude", location.longitude)
            put("radiusMeters", location.radiusMeters.toDouble())
            put("triggerType", location.triggerType.name)
        }.toString()
    }

    private fun locationFromJson(json: String): ReminderLocation? {
        return runCatching {
            val obj = JSONObject(json)

            ReminderLocation(
                placeName = obj.getString("placeName"),
                latitude = obj.getDouble("latitude"),
                longitude = obj.getDouble("longitude"),
                radiusMeters = obj.optDouble(
                    "radiusMeters",
                    150.0
                ).toFloat(),
                triggerType = enumValueOrDefault(
                    obj.optString("triggerType", "ARRIVAL"),
                    LocationTriggerType.ARRIVAL
                )
            )
        }.getOrNull()
    }
}
