package com.zeus.discipline.reminders.data

import androidx.room.RoomDatabase

abstract class ReminderDatabase : RoomDatabase() {

    abstract fun reminderDao(): ReminderDao

    abstract fun reminderCategoryDao(): ReminderCategoryDao
}
