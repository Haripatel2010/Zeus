package com.zeus.discipline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var task by remember { mutableStateOf("") }
            var tasks by remember { mutableStateOf(listOf<String>()) }
            var water by remember { mutableIntStateOf(0) }

            val bg = Color(0xFF080B16)
            val glass = Color(0xFF171D30)
            val blue = Color(0xFF648CFF)

            MaterialTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(bg)
                        .padding(20.dp)
                ) {
                    Spacer(modifier = Modifier.height(25.dp))

                    Text(
                        "Welcome, Hari",
                        color = Color.White,
                        fontSize = 28.sp
                    )

                    Text(
                        "What's the plan today?",
                        color = Color.LightGray,
                        fontSize = 15.sp
                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = glass)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Pending tasks", color = Color.LightGray)
                                Text(
                                    "${tasks.size}",
                                    color = Color.White,
                                    fontSize = 28.sp
                                )
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = glass)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Water 💧", color = Color.LightGray)
                                Text(
                                    "${water} ml",
                                    color = Color.White,
                                    fontSize = 28.sp
                                )
                                TextButton(onClick = { water += 250 }) {
                                    Text("+ 250 ml", color = blue)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(25.dp))

                    Text(
                        "Something New ✨",
                        color = Color.White,
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = glass)
                    ) {
                        Text(
                            "Small progress every day creates big results.",
                            color = Color.White,
                            modifier = Modifier.padding(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(25.dp))

                    Text("Today's tasks", color = Color.White, fontSize = 20.sp)

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = task,
                        onValueChange = { task = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Add a new task...") },
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            if (task.isNotBlank()) {
                                tasks = tasks + task
                                task = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = blue
                        )
                    ) {
                        Text("Add task")
                    }

                    tasks.forEach { item ->
                        Text(
                            "• $item",
                            color = Color.White,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
            }
        }
    }
}
