
package com.zeus.discipline

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.chrisbanes.haze.HazeState
import com.zeus.discipline.components.LiquidGlassBackground
import com.zeus.discipline.components.LiquidGlassCard
import com.zeus.discipline.reminders.ReminderEntry
import com.zeus.discipline.theme.ZeusTheme
import org.json.JSONArray
import org.json.JSONObject

data class ZeusTask(
    val id: Long,
    val title: String,
    val completed: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = android.graphics.Color.rgb(5, 5, 5)
        window.navigationBarColor = android.graphics.Color.rgb(5, 5, 5)

        val prefs = getSharedPreferences(
            "zeus_profile",
            Context.MODE_PRIVATE
        )

        setContent {
            ZeusTheme {
                ZeusApp(prefs)
            }
        }
    }
}

@Composable
fun ZeusApp(prefs: android.content.SharedPreferences) {
    val hazeState = remember { HazeState() }

    var profileSaved by remember {
        mutableStateOf(prefs.getBoolean("profile_saved", false))
    }

    var screen by remember {
        mutableStateOf(if (profileSaved) "home" else "onboarding")
    }

    var tasks by remember {
        mutableStateOf(loadZeusTasks(prefs))
    }

    var waterCount by remember {
        mutableIntStateOf(prefs.getInt("water_count", 0))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF252525),
                        Color(0xFF101010),
                        Color(0xFF050505),
                        Color(0xFF181818)
                    )
                )
            )
    ) {
        LiquidGlassBackground(
            hazeState = hazeState,
            modifier = Modifier.fillMaxSize()
        ) {
            when (screen) {
                "onboarding" -> OnboardingScreen(
                    hazeState = hazeState,
                    prefs = prefs,
                    onContinue = {
                        profileSaved = true
                        screen = "home"
                    }
                )

                "home" -> HomeScreen(
                    hazeState = hazeState,
                    prefs = prefs,
                    tasks = tasks,
                    waterCount = waterCount,
                    onWaterAdd = {
                        waterCount = (waterCount + 1).coerceAtMost(8)
                        prefs.edit()
                            .putInt("water_count", waterCount)
                            .apply()
                    },
                    onTasksChange = {
                        tasks = it
                        saveZeusTasks(prefs, tasks)
                    },
                    onProfile = {
                        screen = "profile"
                    },
                    onReminders = {
                        screen = "reminders"
                    }
                )

                "profile" -> ProfileScreen(
                    hazeState = hazeState,
                    prefs = prefs,
                    onBack = {
                        screen = "home"
                    },
                    onEdit = {
                        screen = "onboarding"
                    }
                )

                "reminders" -> Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    TextButton(
                        onClick = {
                            screen = "home"
                        }
                    ) {
                        Text(
                            "← BACK TO HOME",
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        ReminderEntry()
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingScreen(
    hazeState: HazeState,
    prefs: android.content.SharedPreferences,
    onContinue: () -> Unit
) {
    var name by remember {
        mutableStateOf(prefs.getString("full_name", "") ?: "")
    }

    var height by remember {
        mutableStateOf(
            if (prefs.contains("height_cm"))
                prefs.getFloat("height_cm", 0f).toString()
            else ""
        )
    }

    var weight by remember {
        mutableStateOf(
            if (prefs.contains("weight_kg"))
                prefs.getFloat("weight_kg", 0f).toString()
            else ""
        )
    }

    var gender by remember {
        mutableStateOf(prefs.getString("gender", "Male") ?: "Male")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Spacer(Modifier.height(18.dp))

        Text(
            "ZEUS",
            fontSize = 38.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Text(
            "DISCIPLINE IS POWER",
            fontSize = 12.sp,
            color = Color.LightGray
        )

        Spacer(Modifier.height(30.dp))

        LiquidGlassCard(
            hazeState,
            Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    "Let's get to know you.",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Your journey starts with a personal profile.",
                    color = Color.LightGray
                )
            }
        }

        Spacer(Modifier.height(26.dp))

        GlassInput(
            "FULL NAME",
            name,
            { name = it },
            "Enter your full name",
            KeyboardType.Text
        )

        GlassInput(
            "HEIGHT (CM)",
            height,
            { height = it },
            "Height in centimetres",
            KeyboardType.Decimal
        )

        GlassInput(
            "WEIGHT (KG)",
            weight,
            { weight = it },
            "Weight in kilograms",
            KeyboardType.Decimal
        )

        Spacer(Modifier.height(12.dp))

        Text(
            "GENDER",
            color = Color.LightGray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        LiquidGlassCard(
            hazeState,
            Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(12.dp)) {
                listOf("Male", "Female", "Other").forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = gender == option,
                            onClick = {
                                gender = option
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color.White,
                                unselectedColor = Color.Gray
                            )
                        )

                        Text(
                            option,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        Button(
            onClick = {
                val h = height.toFloatOrNull()
                val w = weight.toFloatOrNull()

                when {
                    name.isBlank() -> return@Button
                    h == null || h <= 0 || h > 300 -> return@Button
                    w == null || w <= 0 || w > 500 -> return@Button
                }

                prefs.edit()
                    .putString("full_name", name.trim())
                    .putFloat("height_cm", h)
                    .putFloat("weight_kg", w)
                    .putString("gender", gender)
                    .putBoolean("profile_saved", true)
                    .apply()

                onContinue()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {
            Text(
                "CONTINUE  →",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(15.dp))

        Text(
            "Your profile is saved locally on this device.",
            color = Color.Gray,
            fontSize = 12.sp
        )

        Spacer(Modifier.height(25.dp))
    }
}

@Composable
fun HomeScreen(
    hazeState: HazeState,
    prefs: android.content.SharedPreferences,
    tasks: List<ZeusTask>,
    waterCount: Int,
    onWaterAdd: () -> Unit,
    onTasksChange: (List<ZeusTask>) -> Unit,
    onProfile: () -> Unit,
    onReminders: () -> Unit
) {
    val name = prefs.getString("full_name", "User") ?: "User"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(22.dp)
    ) {
        Spacer(Modifier.height(12.dp))

        Text(
            "ZEUS",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(Modifier.height(22.dp))

        Text(
            "Welcome, $name 👋",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(Modifier.height(6.dp))

        Text(
            "What's the plan today?",
            fontSize = 14.sp,
            color = Color.LightGray
        )

        Spacer(Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LiquidGlassCard(
                hazeState,
                Modifier.weight(1f)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        "WATER INTAKE",
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "$waterCount / 8",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        "GLASSES",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onWaterAdd,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text("+ ADD")
                    }
                }
            }

            LiquidGlassCard(
                hazeState,
                Modifier.weight(1f)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        "PENDING TASKS",
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        tasks.count { !it.completed }.toString(),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "Keep going.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        Text(
            "TODAY'S TASKS",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(Modifier.height(12.dp))

        var newTask by remember {
            mutableStateOf("")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newTask,
                onValueChange = {
                    newTask = it
                },
                placeholder = {
                    Text("Add a new task...")
                },
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.Gray,
                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray,
                    cursorColor = Color.White
                )
            )

            Spacer(Modifier.width(8.dp))

            Button(
                onClick = {
                    if (newTask.isNotBlank()) {
                        onTasksChange(
                            tasks + ZeusTask(
                                System.currentTimeMillis(),
                                newTask.trim()
                            )
                        )
                        newTask = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Text("+")
            }
        }

        Spacer(Modifier.height(14.dp))

        LiquidGlassCard(
            hazeState,
            Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(12.dp)) {
                if (tasks.isEmpty()) {
                    Text(
                        "No tasks yet. Add your first task above.",
                        color = Color.LightGray
                    )
                } else {
                    tasks.forEach { task ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = task.completed,
                                onCheckedChange = { checked ->
                                    onTasksChange(
                                        tasks.map {
                                            if (it.id == task.id)
                                                it.copy(completed = checked)
                                            else it
                                        }
                                    )
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color.White,
                                    checkmarkColor = Color.Black,
                                    uncheckedColor = Color.Gray
                                )
                            )

                            Text(
                                task.title,
                                modifier = Modifier.weight(1f),
                                color = if (task.completed)
                                    Color.Gray
                                else
                                    Color.White
                            )

                            TextButton(
                                onClick = {
                                    onTasksChange(
                                        tasks.filterNot {
                                            it.id == task.id
                                        }
                                    )
                                }
                            ) {
                                Text(
                                    "×",
                                    color = Color.White,
                                    fontSize = 20.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        Text(
            "SOMETHING NEW",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(Modifier.height(12.dp))

        LiquidGlassCard(
            hazeState,
            Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(18.dp)) {
                Text(
                    "Your next challenge awaits.",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Stay curious. Learn something every day.",
                    color = Color.LightGray,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        OutlinedButton(
            onClick = onProfile,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White
            )
        ) {
            Text("MY PROFILE")
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onReminders,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White
            )
        ) {
            Text("MY REMINDERS")
        }

        Spacer(Modifier.height(30.dp))
    }
}

@Composable
fun ProfileScreen(
    hazeState: HazeState,
    prefs: android.content.SharedPreferences,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            "MY PROFILE",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(Modifier.height(24.dp))

        LiquidGlassCard(
            hazeState,
            Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(20.dp)) {
                
                Text(
                    prefs.getString("full_name", "") ?: "",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(Modifier.height(18.dp))

                Text(
                    "HEIGHT • ${prefs.getFloat("height_cm", 0f)} cm",
                    color = Color.LightGray
                )

                Spacer(Modifier.height(14.dp))

                Text(
                    "WEIGHT • ${prefs.getFloat("weight_kg", 0f)} kg",
                    color = Color.LightGray
                )

                Spacer(Modifier.height(14.dp))

                Text(
                    "GENDER • ${prefs.getString("gender", "Male")}",
                    color = Color.LightGray
                )
            }
        }

        Spacer(Modifier.height(22.dp))

        Button(
            onClick = onEdit,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {
            Text("EDIT PROFILE")
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White
            )
        ) {
            Text("BACK TO HOME")
        }
    }
}

@Composable
fun GlassInput(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    keyboardType: KeyboardType
) {
    Text(
        title,
        color = Color.LightGray,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
    )

    Spacer(Modifier.height(7.dp))

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(hint) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.Gray,
            focusedPlaceholderColor = Color.Gray,
            unfocusedPlaceholderColor = Color.Gray,
            cursorColor = Color.White
        )
    )

    Spacer(Modifier.height(18.dp))
}

fun loadZeusTasks(
    prefs: android.content.SharedPreferences
): List<ZeusTask> {
    val result = mutableListOf<ZeusTask>()

    try {
        val json = JSONArray(
            prefs.getString("tasks_json", "[]")
        )

        for (i in 0 until json.length()) {
            val item = json.getJSONObject(i)

            result.add(
                ZeusTask(
                    item.getLong("id"),
                    item.getString("title"),
                    item.getBoolean("completed")
                )
            )
        }
    } catch (_: Exception) {
    }

    return result
}

fun saveZeusTasks(
    prefs: android.content.SharedPreferences,
    tasks: List<ZeusTask>
) {
    val json = JSONArray()

    tasks.forEach {
        val item = JSONObject()

        item.put("id", it.id)
        item.put("title", it.title)
        item.put("completed", it.completed)

        json.put(item)
    }

    prefs.edit()
        .putString("tasks_json", json.toString())
        .apply()
}

                

                
