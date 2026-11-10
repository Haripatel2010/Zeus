
package com.zeus.discipline

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val screen = TextView(this).apply {
            text = "ZEUS\n\nYour discipline starts here."
            textSize = 26f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(8, 11, 25))
            gravity = Gravity.CENTER
        }

        setContentView(screen)
    }
}
