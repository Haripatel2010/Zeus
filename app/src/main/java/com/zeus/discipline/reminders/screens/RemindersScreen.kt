
package com.zeus.discipline.reminders.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.zeus.discipline.reminders.ReminderViewModel
import com.zeus.discipline.reminders.model.Reminder

@Composable
fun RemindersScreen(
    viewModel: ReminderViewModel
) {
    val reminders by viewModel.reminders.collectAsState()
    var title by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Reminders",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("New reminder") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                val cleanTitle = title.trim()

                if (cleanTitle.isNotEmpty()) {
                    viewModel.addReminder(
                        Reminder(title = cleanTitle)
                    )
                    title = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank()
        ) {
            Text("Add reminder")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Your reminders (${reminders.size})",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (reminders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No reminders yet")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = reminders,
                    key = { it.id }
                ) { reminder ->

                    ReminderRow(
                        reminder = reminder,
                        onComplete = { completed ->
                            viewModel.completeReminder(
                                reminder,
                                completed
                            )
                        },
                        onDelete = {
                            viewModel.deleteReminder(reminder.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ReminderRow(
    reminder: Reminder,
    onComplete: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = reminder.isCompleted,
                onCheckedChange = onComplete
            )

            Text(
                text = reminder.title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (reminder.isCompleted) {
                    TextDecoration.LineThrough
                } else {
                    TextDecoration.None
                }
            )

            TextButton(onClick = onDelete) {
                Text("Delete")
            }
        }
    }
}
