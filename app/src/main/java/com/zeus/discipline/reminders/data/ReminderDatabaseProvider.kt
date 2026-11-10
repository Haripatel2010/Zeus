
package com.zeus.discipline.reminders.data

import android.content.Context

object ReminderDatabaseProvider {

    @Volatile
    private var repositoryInstance: ReminderRepository? = null

    fun getRepository(context: Context): ReminderRepository {
        return repositoryInstance ?: synchronized(this) {
            repositoryInstance ?: ReminderRepository(
                ReminderDatabase.getDatabase(context)
            ).also {
                repositoryInstance = it
            }
        }
    }
}
