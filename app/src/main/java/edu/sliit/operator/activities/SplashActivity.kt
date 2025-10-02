package edu.sliit.operator.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import edu.sliit.operator.utils.AppColors

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DELAY = 2500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createSplashView())

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, SPLASH_DELAY)
    }

    private fun createSplashView(): View {
        return TextView(this).apply {
            text = "⚡ EV HUB"
            textSize = 48f
            setTextColor(Color.parseColor(AppColors.GREEN_NEON))
            setBackgroundColor(Color.parseColor(AppColors.BLACK_PRIMARY))
            gravity = android.view.Gravity.CENTER
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }
    }
}