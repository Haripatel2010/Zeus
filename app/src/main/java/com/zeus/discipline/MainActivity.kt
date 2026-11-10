package com.zeus.discipline

import android.app.Activity
import android.os.Bundle
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.*
import android.text.InputType
import org.json.JSONArray
import org.json.JSONObject

data class ZeusTask(
    val id: Long,
    val title: String,
    val completed: Boolean = false
)

class MainActivity : Activity() {

    private val prefs by lazy {
        getSharedPreferences("zeus_profile", Context.MODE_PRIVATE)
    }

    private val bg = Color.rgb(5, 8, 22)
    private val white = Color.WHITE
    private val muted = Color.rgb(165, 180, 210)
    private val blue = Color.rgb(90, 140, 255)
    private val violet = Color.rgb(145, 100, 255)

    private fun dp(v: Int): Int =
        (v * resources.displayMetrics.density).toInt()

    // LIQUID GLASS BACKGROUND

    private fun glass(): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(
                Color.argb(55, 125, 155, 255),
                Color.argb(28, 80, 100, 180),
                Color.argb(45, 120, 90, 200)
            )
        ).apply {
            cornerRadius = dp(24).toFloat()
            setStroke(dp(1), Color.argb(75, 190, 205, 255))
        }
    }

    private fun solid(color: Int): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(20).toFloat()
        }
    }

    private fun pageBackground(): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(
                Color.rgb(12, 17, 43),
                Color.rgb(5, 8, 22),
                Color.rgb(24, 12, 48)
            )
        )
    }

    private fun label(
        value: String,
        size: Float,
        color: Int = white,
        bold: Boolean = false
    ): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(null, Typeface.BOLD)
        }
    }

    private fun space(layout: LinearLayout, height: Int) {
        layout.addView(
            View(this),
            LinearLayout.LayoutParams(1, dp(height))
        )
    }

    private fun input(hint: String, type: Int): EditText {
        return EditText(this).apply {
            this.hint = hint
            inputType = type
            textSize = 16f
            setTextColor(white)
            setHintTextColor(muted)
            setSingleLine(true)
            setPadding(dp(16), dp(12), dp(16), dp(12))
            background = glass()
            minimumHeight = dp(55)
        }
    }

    private fun glassButton(
        title: String,
        filled: Boolean = false
    ): Button {
        return Button(this).apply {
            text = title
            isAllCaps = false
            textSize = 14f
            setTextColor(white)
            background = if (filled) solid(blue) else glass()
            minimumHeight = dp(52)
        }
    }

    private fun page(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(28), dp(22), dp(28))
            background = pageBackground()
        }
    }

    private fun glassCard(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(18))
            background = glass()
            elevation = dp(4).toFloat()
        }
    }

    // PROFILE STORAGE

    private fun saveProfile(
        name: String,
        height: Float,
        weight: Float,
        gender: String
    ) {
        prefs.edit()
            .putString("full_name", name)
            .putFloat("height_cm", height)
            .putFloat("weight_kg", weight)
            .putString("gender", gender)
            .putBoolean("profile_saved", true)
            .apply()
    }

    // TASK STORAGE

    private fun loadTasks(): MutableList<ZeusTask> {
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
        } catch (e: Exception) {
            // Invalid data: show an empty task list.
        }

        return result
    }

    private fun saveTasks(tasks: List<ZeusTask>) {
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

    // APP START

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = bg
        window.navigationBarColor = bg

        if (prefs.getBoolean("profile_saved", false)) {
            showHome()
        } else {
            showOnboarding()
        }
    }

    // ONBOARDING

    private fun showOnboarding() {
        val root = page()

        val scroll = ScrollView(this).apply {
            addView(root)
            isFillViewport = true
        }

        root.addView(label("ZEUS", 38f, white, true))
        space(root, 6)
        root.addView(label("DISCIPLINE IS POWER", 12f, blue, true))
        space(root, 38)

        val welcome = glassCard()
        welcome.addView(label("Let's get to know you.", 25f, white, true))
        space(welcome, 8)
        welcome.addView(label(
            "Your journey starts with a personal profile.",
            14f, muted
        ))
        root.addView(welcome)

        space(root, 25)
        root.addView(label("FULL NAME", 12f, muted, true))
        space(root, 8)

        val name = input(
            "Enter your full name",
            InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_FLAG_CAP_WORDS
        )
        name.setText(prefs.getString("full_name", ""))
        root.addView(name)

        space(root, 20)
        root.addView(label("HEIGHT (CM)", 12f, muted, true))
        space(root, 8)

        val height = input(
            "Height in centimetres",
            InputType.TYPE_CLASS_NUMBER or
                InputType.TYPE_NUMBER_FLAG_DECIMAL
        )

        if (prefs.contains("height_cm")) {
            height.setText(prefs.getFloat("height_cm", 0f).toString())
        }
        root.addView(height)

        space(root, 20)
        root.addView(label("WEIGHT (KG)", 12f, muted, true))
        space(root, 8)

        val weight = input(
            "Weight in kilograms",
            InputType.TYPE_CLASS_NUMBER or
                InputType.TYPE_NUMBER_FLAG_DECIMAL
        )

        if (prefs.contains("weight_kg")) {
            weight.setText(prefs.getFloat("weight_kg", 0f).toString())
        }
        root.addView(weight)

        space(root, 20)
        root.addView(label("GENDER", 12f, muted, true))
        space(root, 8)

        val genderGroup = RadioGroup(this).apply {
            orientation = RadioGroup.VERTICAL
            setPadding(dp(12), dp(10), dp(12), dp(10))
            background = glass()
        }

        val savedGender = prefs.getString("gender", "Male")

        val genderOptions = listOf("Male", "Female", "Other")

        genderOptions.forEach { option ->
            val radio = RadioButton(this).apply {
                id = View.generateViewId()
                text = option
                textSize = 15f
                setTextColor(white)
                buttonTintList =
                    android.content.res.ColorStateList.valueOf(blue)
            }

            genderGroup.addView(radio)

            if (option == savedGender) {
                genderGroup.check(radio.id)
            }
        }

        if (genderGroup.checkedRadioButtonId == -1) {
            genderGroup.check(genderGroup.getChildAt(0).id)
        }

        root.addView(genderGroup)

        space(root, 30)

        val continueButton = glassButton("CONTINUE  →", true)
        root.addView(continueButton)

        space(root, 18)
        root.addView(label(
            "Your profile is saved locally on this device.",
            12f, muted
        ))

        continueButton.setOnClickListener {
            val n = name.text.toString().trim()
            val h = height.text.toString().toFloatOrNull()
            val w = weight.text.toString().toFloatOrNull()

            val selectedId = genderGroup.checkedRadioButtonId

            if (n.isEmpty()) {
                name.error = "Enter your full name"
                return@setOnClickListener
            }

            if (h == null || h <= 0 || h > 300) {
                height.error = "Enter a valid height"
                return@setOnClickListener
            }

            if (w == null || w <= 0 || w > 500) {
                weight.error = "Enter a valid weight"
                return@setOnClickListener
            }

            if (selectedId == -1) {
                Toast.makeText(this, "Select your gender", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedGender =
                findViewById<RadioButton>(selectedId).text.toString()

            saveProfile(n, h, w, selectedGender)
            showHome()
        }

        setContentView(scroll)
    }

    // HOME DASHBOARD

    private fun showHome() {
        val tasks = loadTasks()
        val root = page()

        val scroll = ScrollView(this).apply {
            addView(root)
            isFillViewport = true
        }

        root.addView(label("ZEUS", 30f, white, true))
        space(root, 24)

        root.addView(label(
            "Welcome, ${prefs.getString("full_name", "User")} 👋",
            25f, white, true
        ))

        space(root, 7)
        root.addView(label("What's the plan today?", 14f, muted))
        space(root, 25)

        // STAT CARDS

        val stats = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val water = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(17), dp(14), dp(17))
            background = glass()
        }

        val waterCount = prefs.getInt("water_count", 0)

        water.addView(label("WATER INTAKE", 11f, muted, true))
        space(water, 12)
        water.addView(label("$waterCount / 8 GLASSES", 19f, white, true))
        space(water, 12)

        val waterButton = glassButton("+  ADD WATER")
        water.addView(waterButton)

        waterButton.setOnClickListener {
            val newCount = (prefs.getInt("water_count", 0) + 1).coerceAtMost(8)
            prefs.edit().putInt("water_count", newCount).apply()
            showHome()
        }

        stats.addView(
            water,
            LinearLayout.LayoutParams(0, -2, 1f).apply {
                marginEnd = dp(6)
            }
        )

        val pending = tasks.count { !it.completed }

        val pendingCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(17), dp(14), dp(17))
            background = glass()
        }

        pendingCard.addView(label("PENDING TASKS", 11f, muted, true))
        space(pendingCard, 12)
        pendingCard.addView(label(pending.toString(), 30f, white, true))
        space(pendingCard, 12)
        pendingCard.addView(label("Keep going.", 12f, muted))

        stats.addView(
            pendingCard,
            LinearLayout.LayoutParams(0, -2, 1f).apply {
                marginStart = dp(6)
            }
        )

        root.addView(stats)
        space(root, 30)

        // TASK SECTION

        root.addView(label("TODAY'S TASKS", 18f, white, true))
        space(root, 12)

        val addRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val taskInput = input(
            "Add a new task...",
            InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        )

        addRow.addView(
            taskInput,
            LinearLayout.LayoutParams(0, dp(55), 1f).apply {
                marginEnd = dp(8)
            }
        )

        val addButton = glassButton("+", true)
        addRow.addView(addButton, LinearLayout.LayoutParams(dp(60), dp(55)))

        root.addView(addRow)
        space(root, 14)

        val taskBox = glassCard()

        if (tasks.isEmpty()) {
            taskBox.addView(label("No tasks yet.", 15f, muted))
            space(taskBox, 8)
            taskBox.addView(label(
                "Add your first task above.",
                12f, muted
            ))
        } else {
            tasks.forEach { task ->

                val row = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                }

                val check = CheckBox(this).apply {
                    text = task.title
                    textSize = 14f
                    setTextColor(white)
                    isChecked = task.completed
                    buttonTintList =
                        android.content.res.ColorStateList.valueOf(blue)
                }

                row.addView(
                    check,
                    LinearLayout.LayoutParams(0, -2, 1f)
                )

                val delete = glassButton("×")
                row.addView(delete, LinearLayout.LayoutParams(dp(48), dp(45)))

                taskBox.addView(row)

                check.setOnCheckedChangeListener { _, checked ->
                    val updated = tasks.map {
                        if (it.id == task.id) it.copy(completed = checked)
                        else it
                    }
                    saveTasks(updated)
                    showHome()
                }

                delete.setOnClickListener {
                    saveTasks(tasks.filterNot { it.id == task.id })
                    showHome()
                }

                space(taskBox, 5)
            }
        }

        root.addView(taskBox)

        addButton.setOnClickListener {
            val title = taskInput.text.toString().trim()

            if (title.isEmpty()) {
                taskInput.error = "Enter a task"
                return@setOnClickListener
            }

            val updated = tasks.toMutableList()
            updated.add(
                ZeusTask(
                    System.currentTimeMillis(),
                    title
                )
            )

            saveTasks(updated)
            showHome()
        }

        space(root, 28)

        // SOMETHING NEW

        root.addView(label("SOMETHING NEW", 18f, white, true))
        space(root, 12)

        val newCard = glassCard()
        newCard.addView(label(
            "Your next challenge awaits.",
            17f, white, true
        ))
        space(newCard, 8)
        newCard.addView(label(
            "Stay curious. Learn something every day.",
            13f, muted
        ))

        root.addView(newCard)
        space(root, 28)

        val profileButton = glassButton("MY PROFILE")
        root.addView(profileButton)

        profileButton.setOnClickListener {
            showProfile()
        }

        setContentView(scroll)
    }

    // PROFILE SCREEN

    private fun showProfile() {
        val root = page()
        val scroll = ScrollView(this).apply {
            addView(root)
        }

        root.addView(label("MY PROFILE", 30f, white, true))
        space(root, 25)

        val profile = glassCard()

        profile.addView(label(
            prefs.getString("full_name", "") ?: "",
            23f, white, true
        ))
        space(profile, 18)

        profile.addView(label(
            "HEIGHT  •  ${prefs.getFloat("height_cm", 0f)} cm",
            15f, muted
        ))
        space(profile, 14)

        profile.addView(label(
            "WEIGHT  •  ${prefs.getFloat("weight_kg", 0f)} kg",
            15f, muted
        ))
        space(profile, 14)

        profile.addView(label(
            "GENDER  •  ${prefs.getString("gender", "Male")}",
            15f, muted
        ))

        root.addView(profile)
        space(root, 22)

        val edit = glassButton("EDIT PROFILE", true)
        root.addView(edit)

        edit.setOnClickListener {
            showOnboarding()
        }

        space(root, 12)

        val back = glassButton("BACK TO HOME")
        root.addView(back)

        back.setOnClickListener {
            showHome()
        }

        setContentView(scroll)
    }
}

