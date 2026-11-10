package com.zeus.discipline

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.content.Context
import android.widget.*
import android.text.InputType
import android.graphics.drawable.GradientDrawable

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

    private fun background(color: Int): GradientDrawable {
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

    private fun field(
        hint: String,
        type: Int
    ): EditText {
        return EditText(this).apply {
            this.hint = hint
            inputType = type
            textSize = 16f
            setTextColor(white)
            setHintTextColor(muted)
            setSingleLine(true)
            setPadding(dp(16), dp(12), dp(16), dp(12))
            background = background(card)
            minimumHeight = dp(54)
        }
    }

    private fun addSpace(
        layout: LinearLayout,
        height: Int
    ) {
        layout.addView(View(this), LinearLayout.LayoutParams(
            1, dp(height)
        ))
    }

    private fun addLabel(
        layout: LinearLayout,
        value: String
    ) {
        layout.addView(
            text(value, 13f, muted, true)
        )
        addSpace(layout, 7)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        window.decorView.systemUiVisibility = 0

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
        root.addView(
            text("Create your personal profile to get started.",
                14f, muted)
        )
        addSpace(root, 30)

        addLabel(root, "FULL NAME")

        val name = field(
            "Enter your full name",
            InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_FLAG_CAP_WORDS
        )
        root.addView(name)
        addSpace(root, 22)

        addLabel(root, "HEIGHT (CM)")

        val height = field(
            "Enter height in centimetres",
            InputType.TYPE_CLASS_NUMBER or
                InputType.TYPE_NUMBER_FLAG_DECIMAL
        )
        root.addView(height)
        addSpace(root, 22)

        addLabel(root, "WEIGHT (KG)")

        val weight = field(
            "Enter weight in kilograms",
            InputType.TYPE_CLASS_NUMBER or
                InputType.TYPE_NUMBER_FLAG_DECIMAL
        )
        root.addView(weight)
        addSpace(root, 22)

        addLabel(root, "GENDER")

        val genderGroup = RadioGroup(this).apply {
            orientation = RadioGroup.HORIZONTAL
            setPadding(dp(12), dp(8), dp(12), dp(8))
            background = background(card)
        }

        val male = RadioButton(this).apply {
            text = "Male"
            textSize = 16f
            setTextColor(white)
            buttonTintList = android.content.res.ColorStateList.valueOf(blue)
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
            background = background(blue)
            minimumHeight = dp(55)
        }

        root.addView(save)

        addSpace(root, 18)
        root.addView(
            text("Your profile is stored privately on this device.",
                12f, muted)
        )

        save.setOnClickListener {
            val n = name.text.toString().trim()
            val h = height.text.toString().toFloatOrNull()
            val w = weight.text.toString().toFloatOrNull()

            if (n.isEmpty()) {
                name.error = "Enter your full name"
                name.requestFocus()
                return@setOnClickListener
            }

            if (h == null || h <= 0 || h > 300) {
                height.error = "Enter a valid height"
                height.requestFocus()
                return@setOnClickListener
            }

            if (w == null || w <= 0 || w > 500) {
                weight.error = "Enter a valid weight"
                weight.requestFocus()
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
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(26), dp(30), dp(26), dp(30))
            setBackgroundColor(bg)
        }

        root.addView(text("ZEUS", 36f, white, true))
        addSpace(root, 14)

        root.addView(
            text(
                "Welcome, ${prefs.getString("full_name", "User")}!",
                25f, white, true
            )
        )

        addSpace(root, 8)
        root.addView(
            text("Your discipline starts here.",
                15f, muted)
        )

        addSpace(root, 35)

        val profile = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(20), dp(20), dp(20))
            background = background(card)
        }

        profile.addView(text("YOUR PROFILE", 14f, blue, true))
        addSpace(profile, 18)

        profile.addView(
            text("Name: ${prefs.getString("full_name", "")}",
                16f, white)
        )
        addSpace(profile, 12)

        profile.addView(
            text("Height: ${prefs.getFloat("height_cm", 0f)} cm",
                16f, white)
        )
        addSpace(profile, 12)

        profile.addView(
            text("Weight: ${prefs.getFloat("weight_kg", 0f)} kg",
                16f, white)
        )
        addSpace(profile, 12)

        profile.addView(
            text("Gender: ${prefs.getString("gender", "Male")}",
                16f, white)
        )

        root.addView(profile)
        addSpace(root, 24)

        val edit = Button(this).apply {
            text = "EDIT PROFILE"
            isAllCaps = false
            setTextColor(white)
            background = background(blue)
        }

        edit.setOnClickListener {
            prefs.edit().putBoolean("profile_saved", false).apply()
            showOnboarding()
        }

        root.addView(edit)
        setContentView(root)
    }
}
