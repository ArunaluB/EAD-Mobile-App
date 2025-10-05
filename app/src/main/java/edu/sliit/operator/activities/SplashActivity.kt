package edu.sliit.operator.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import edu.sliit.operator.utils.AppColors

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DELAY = 2500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createModernSplashView())

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, SPLASH_DELAY)
    }

    private fun createModernSplashView(): View {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#0A0E27")) // Deep navy
            gravity = Gravity.CENTER
            setPadding(40, 40, 40, 40)

            // Animated lightning icon
            addView(TextView(this@SplashActivity).apply {
                text = "⚡"
                textSize = 120f
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, 32)
                setShadowLayer(40f, 0f, 0f, Color.parseColor("#00FF88"))
            })

            // App title
            addView(TextView(this@SplashActivity).apply {
                text = "EV HUB"
                textSize = 56f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setTextColor(Color.parseColor("#FFFFFF"))
                gravity = Gravity.CENTER
                setShadowLayer(20f, 0f, 4f, Color.parseColor("#00FF88"))
                setPadding(0, 0, 0, 16)
            })

            // Subtitle
            addView(TextView(this@SplashActivity).apply {
                text = "Charging Station Management"
                textSize = 18f
                setTextColor(Color.parseColor("#00D9FF"))
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, 60)
            })

            // Loading indicator
            addView(TextView(this@SplashActivity).apply {
                text = "━━━━━━━━━━━━"
                textSize = 16f
                setTextColor(Color.parseColor("#00FF88"))
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, 12)
            })

            addView(TextView(this@SplashActivity).apply {
                text = "Loading..."
                textSize = 14f
                setTextColor(Color.parseColor("#8B9DC3"))
                gravity = Gravity.CENTER
            })
        }
    }
}