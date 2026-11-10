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

class MainActivity : Activity() {

    private val prefs by lazy {
        getSharedPreferences("zeus_profile", Context.MODE_PRIVATE)
    }

    private val bg = Color.rgb(8, 11, 25)
    private val card = Color.rgb(20, 29, 49)
    private val blue = Color.rgb(75, 125, 255)
    private val white = Color.WHITE
    private val muted = Color.rgb(165, 180, 210)

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun roundedBackground(color: Int): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(18).toFloat()
        }
    }

    private fun text(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(null, Typeface.BOLD)
        }
    }

    private fun field(hint: String, type: Int): EditText {
        return EditText(this).apply {
            this.hint = hint
            inputType = type
            textSize = 16f
            setTextColor(white)
            setHintTextColor(muted)
            setSingleLine(true)
            setPadding(dp(16), dp(12), dp(16), dp(12))
            background = roundedBackground(card)
            minimumHeight = dp(54)
        }
    }

    private fun addSpace(layout: LinearLayout, height: Int) {
        layout.addView(View(this), LinearLayout.LayoutParams(1, dp(height)))
    }

    private fun addLabel(layout: LinearLayout, value: String) {
        layout.addView(text(value, 13f, muted, true))
        addSpace(layout, 7)
    }

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

    private fun showOnboarding() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(30), dp(24), dp(30))
            setBackgroundColor(bg)
        }

        val scroll = ScrollView(this).apply {
            addView(root)
            isFillViewport = true
        }

        root.addView(text("ZEUS", 34f, white, true))
        addSpace(root, 8)
        root.addView(text("DISCIPLINE IS POWER.", 12f, blue, true))
        addSpace(root, 38)

        root.addView(text("Let's get to know you.", 25f, white, true))
        addSpace(root, 8)
        root.addView(text(
            "Create your personal profile to get started.",
            14f, muted
        ))
        addSpace(root, 30)

        addLabel(root, "FULL NAME")
        val name = field(
            "Enter your full name",
            InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_FLAG_CAP_WORDS
        )
        name.setText(prefs.getString("full_name", ""))
        root.addView(name)
        addSpace(root, 22)

        addLabel(root, "HEIGHT (CM)")
        val height = field(
            "Enter height in centimetres",
            InputType.TYPE_CLASS_NUMBER or
                InputType.TYPE_NUMBER_FLAG_DECIMAL
        )
        if (prefs.contains("height_cm")) {
            height.setText(prefs.getFloat("height_cm", 0f).toString())
        }
        root.addView(height)
        addSpace(root, 22)

        addLabel(root, "WEIGHT (KG)")
        val weight = field(
            "Enter weight in kilograms",
            InputType.TYPE_CLASS_NUMBER or
                InputType.TYPE_NUMBER_FLAG_DECIMAL
        )
        if (prefs.contains("weight_kg")) {
            weight.setText(prefs.getFloat("weight_kg", 0f).toString())
        }
        root.addView(weight)
        addSpace(root, 22)

        addLabel(root, "GENDER")

        val genderGroup = RadioGroup(this).apply {
            orientation = RadioGroup.HORIZONTAL
            setPadding(dp(12), dp(8), dp(12), dp(8))
            background = roundedBackground(card)
        }

        val male = RadioButton(this).apply {
            id = View.generateViewId()
            text = "Male"
            textSize = 16f
            setTextColor(white)
            buttonTintList =
                android.content.res.ColorStateList.valueOf(blue)
            isChecked = true
        }

        genderGroup.addView(male)
        genderGroup.check(male.id)
        root.addView(genderGroup)

        addSpace(root, 32)

        val save = Button(this).apply {
            text = "CONTINUE  →"
            textSize = 15f
            setTextColor(white)
            isAllCaps = false
            background = roundedBackground(blue)
            minimumHeight = dp(55)
        }

        root.addView(save)

        addSpace(root, 18)
        root.addView(text(
            "Your profile is stored privately on this device.",
            12f, muted
        ))

        save.setOnClickListener {
            val n = name.text.toString().trim()
            val h = height.text.toString().toFloatOrNull()
            val w = weight.text.toString().toFloatOrNull()

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

            prefs.edit()
                .putString("full_name", n)
                .putFloat("height_cm", h)
                .putFloat("weight_kg", w)
                .putString("gender", "Male")
                .putBoolean("profile_saved", true)
                .apply()

            showHome()
        }

        setContentView(scroll)
    }

    private fun showHome() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(30), dp(22), dp(20))
            setBackgroundColor(bg)
        }

        val scroll = ScrollView(this).apply {
            addView(root)
            isFillViewport = true
        }

        root.addView(text("ZEUS", 30f, white, true))
        addSpace(root, 22)

        root.addView(text(
            "Welcome, ${prefs.getString("full_name", "User")}!",
            26f, white, true
        ))

        addSpace(root, 6)
        root.addView(text("What's the plan today?", 15f, muted))
        addSpace(root, 24)

        val stats = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        fun statCard(title: String, value: String): LinearLayout {
            return LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(16), dp(18), dp(12), dp(18))
                background = roundedBackground(card)
                addView(text(title, 12f, muted, true))
                addSpace(this, 10)
                addView(text(value, 25f, white, true))
            }
        }

        stats.addView(
            statCard("WATER INTAKE", "0 / 8"),
            LinearLayout.LayoutParams(0, dp(110), 1f).apply {
                marginEnd = dp(8)
            }
        )

        stats.addView(
            statCard("PENDING TASKS", "0"),
            LinearLayout.LayoutParams(0, dp(110), 1f).apply {
                marginStart = dp(8)
            }
        )

        root.addView(stats)
        addSpace(root, 28)

        root.addView(text("TODAY'S TASKS", 17f, white, true))
        addSpace(root, 12)

        val taskBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(18))
            background = roundedBackground(card)

            addView(text("No tasks added yet.", 15f, muted))
            addSpace(this, 8)
            addView(text(
                "Your daily goals will appear here.",
                12f, muted
            ))
        }

        root.addView(taskBox)
        addSpace(root, 26)

        root.addView(text("SOMETHING NEW", 17f, white, true))
        addSpace(root, 12)

        val newBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(18))
            background = roundedBackground(card)

            addView(text(
                "Your next challenge awaits.",
                16f, white, true
            ))
            addSpace(this, 8)
            addView(text(
                "Stay curious. Learn something every day.",
                13f, muted
            ))
        }

        root.addView(newBox)
        addSpace(root, 24)

        val profileButton = Button(this).apply {
            text = "VIEW PROFILE"
            isAllCaps = false
            setTextColor(white)
            background = roundedBackground(blue)
        }

        profileButton.setOnClickListener {
            showProfile()
        }

        root.addView(profileButton)
        setContentView(scroll)
    }

    private fun showProfile() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(30), dp(24), dp(30))
            setBackgroundColor(bg)
        }

        root.addView(text("YOUR PROFILE", 28f, white, true))
        addSpace(root, 28)

        root.addView(text(
            "Name: ${prefs.getString("full_name", "")}",
            17f, white
        ))
        addSpace(root, 16)

        root.addView(text(
            "Height: ${prefs.getFloat("height_cm", 0f)} cm",
            17f, white
        ))
        addSpace(root, 16)

        root.addView(text(
            "Weight: ${prefs.getFloat("weight_kg", 0f)} kg",
            17f, white
        ))
        addSpace(root, 16)

        root.addView(text(
            "Gender: ${prefs.getString("gender", "Male")}",
            17f, white
        ))
        addSpace(root, 30)

        val edit = Button(this).apply {
            text = "EDIT PROFILE"
            isAllCaps = false
            setTextColor(white)
            background = roundedBackground(blue)
        }

        edit.setOnClickListener {
            showOnboarding()
        }

        root.addView(edit)
        addSpace(root, 12)

        val back = Button(this).apply {
            text = "BACK TO HOME"
            isAllCaps = false
            setTextColor(white)
            background = roundedBackground(card)
        }

        back.setOnClickListener {
            showHome()
        }

        root.addView(back)
        setContentView(root)
    }
}
