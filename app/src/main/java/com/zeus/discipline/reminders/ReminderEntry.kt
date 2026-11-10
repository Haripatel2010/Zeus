
package com.zeus.discipline.reminders

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zeus.discipline.reminders.data.ReminderDatabaseProvider
import com.zeus.discipline.reminders.screens.RemindersScreen

@Composable
fun ReminderEntry() {
    val context = LocalContext.current

    val repository = ReminderDatabaseProvider.getRepository(context)

    val reminderViewModel: ReminderViewModel = viewModel(
        factory = ReminderViewModelFactory(repository)
    )

    RemindersScreen(
        viewModel = reminderViewModel
    )
}
