package edu.sliit.operator.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import edu.sliit.operator.database.EVHubDatabaseHelper
import edu.sliit.operator.utils.AppColors

class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: EVHubDatabaseHelper
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var cbRememberMe: CheckBox
    private lateinit var btnLogin: Button
    private lateinit var tvError: TextView
    private lateinit var tvSignUp: TextView

    companion object {
        private const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "LoginActivity created")

        dbHelper = EVHubDatabaseHelper(this)
        dbHelper.printAllUsers()

        // Check if user is already logged in
        val sharedPref = getSharedPreferences("EVHubPrefs", Context.MODE_PRIVATE)
        val savedUsername = sharedPref.getString("username", null)
        val rememberMe = sharedPref.getBoolean("remember_me", false)

        if (rememberMe && savedUsername != null) {
            Log.d(TAG, "User already logged in: $savedUsername")
            navigateToDashboard(savedUsername)
            return
        }

        setContentView(createLoginView())
        setupListeners()
    }

    private fun createLoginView(): View {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor(AppColors.BLACK_PRIMARY))
            setPadding(60, 100, 60, 100)
            gravity = Gravity.CENTER
        }

        TextView(this).apply {
            text = "⚡ EV HUB"
            textSize = 42f
            setTextColor(Color.parseColor(AppColors.GREEN_NEON))
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 80)
            layout.addView(this)
        }

        TextView(this).apply {
            text = "Operator Login"
            textSize = 20f
            setTextColor(Color.parseColor(AppColors.WHITE))
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 60)
            layout.addView(this)
        }

        etUsername = EditText(this).apply {
            hint = "Username"
            setHintTextColor(Color.parseColor(AppColors.GRAY_MEDIUM))
            setTextColor(Color.parseColor(AppColors.WHITE))
            setBackgroundColor(Color.parseColor(AppColors.GRAY_DARK_PANEL))
            setPadding(40, 40, 40, 40)
            textSize = 16f
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 30)
            layoutParams = params
            layout.addView(this)
        }

        etPassword = EditText(this).apply {
            hint = "Password"
            setHintTextColor(Color.parseColor(AppColors.GRAY_MEDIUM))
            setTextColor(Color.parseColor(AppColors.WHITE))
            setBackgroundColor(Color.parseColor(AppColors.GRAY_DARK_PANEL))
            setPadding(40, 40, 40, 40)
            textSize = 16f
            inputType = android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 30)
            layoutParams = params
            layout.addView(this)
        }

        cbRememberMe = CheckBox(this).apply {
            text = "Remember me"
            setTextColor(Color.parseColor(AppColors.GRAY_MEDIUM))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 40)
            layoutParams = params
            layout.addView(this)
        }

        tvError = TextView(this).apply {
            setTextColor(Color.parseColor(AppColors.PINK))
            textSize = 14f
            visibility = View.GONE
            gravity = Gravity.CENTER
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 20)
            layoutParams = params
            layout.addView(this)
        }

        btnLogin = Button(this).apply {
            text = "LOGIN"
            textSize = 18f
            setTextColor(Color.parseColor(AppColors.BLACK_PRIMARY))
            setBackgroundColor(Color.parseColor(AppColors.GREEN_NEON))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.height = 140
            params.setMargins(0, 0, 0, 40)
            layoutParams = params
            layout.addView(this)
        }

        tvSignUp = TextView(this).apply {
            text = "Don't have an account? Sign Up"
            setTextColor(Color.parseColor(AppColors.BLUE))
            textSize = 14f
            gravity = Gravity.CENTER
            layout.addView(this)
        }

        return layout
    }

    private fun setupListeners() {
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            Log.d(TAG, "Login button clicked")
            Log.d(TAG, "Username: $username")

            if (validateInput(username, password)) {
                performLogin(username, password)
            }
        }

        tvSignUp.setOnClickListener {
            Log.d(TAG, "Navigating to SignUp")
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun validateInput(username: String, password: String): Boolean {
        return when {
            username.isEmpty() -> {
                showError("Username is required")
                Log.d(TAG, "Validation failed: Empty username")
                false
            }
            password.isEmpty() -> {
                showError("Password is required")
                Log.d(TAG, "Validation failed: Empty password")
                false
            }
            else -> {
                Log.d(TAG, "✓ Validation passed")
                true
            }
        }
    }

    private fun performLogin(username: String, password: String) {
        Log.d(TAG, "=== Starting Login Process ===")

        val user = dbHelper.validateLogin(username, password)

        if (user != null) {
            Log.d(TAG, "✓ Login successful!")
            Log.d(TAG, "  User ID: ${user.id}")
            Log.d(TAG, "  Username: ${user.username}")
            Log.d(TAG, "  Role: ${user.role}")

            // Save session
            val sharedPref = getSharedPreferences("EVHubPrefs", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("username", username)
                putInt("user_id", user.id)
                putString("role", user.role)
                putBoolean("remember_me", cbRememberMe.isChecked)
                putLong("login_time", System.currentTimeMillis())
                apply()
            }

            Log.d(TAG, "Session saved to SharedPreferences")

            // Navigate to Dashboard
            navigateToDashboard(username)
        } else {
            showError("Invalid username or password")
            Log.d(TAG, "✗ Login failed: Invalid credentials")
        }
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
        Log.d(TAG, "Error shown: $message")
    }

    private fun navigateToDashboard(username: String) {
        try {
            Log.d(TAG, "=== Attempting to navigate to MainActivity ===")

            // Show welcome message
            Toast.makeText(this, "Welcome, $username!", Toast.LENGTH_SHORT).show()

            // Create explicit intent to MainActivity
            val intent = Intent(this, MainActivity::class.java).apply {
                // Clear back stack so user can't go back to login
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                // Pass username to MainActivity
                putExtra("username", username)
            }

            Log.d(TAG, "Intent created for MainActivity")
            Log.d(TAG, "Starting MainActivity...")

            // Start MainActivity
            startActivity(intent)

            Log.d(TAG, "MainActivity started successfully")

            // Finish LoginActivity
            finish()

        } catch (e: Exception) {
            Log.e(TAG, "✗ Error navigating to MainActivity: ${e.message}")
            Log.e(TAG, "Stack trace: ", e)
            Toast.makeText(
                this,
                "Error opening dashboard. Please try again.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}