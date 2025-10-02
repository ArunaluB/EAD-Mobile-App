package edu.sliit.operator.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import edu.sliit.operator.database.EVHubDatabaseHelper
import edu.sliit.operator.models.User
import edu.sliit.operator.utils.AppColors

class SignUpActivity : AppCompatActivity() {

    private lateinit var dbHelper: EVHubDatabaseHelper
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var tvError: TextView
    private lateinit var tvLogin: TextView

    companion object {
        private const val TAG = "SignUpActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = EVHubDatabaseHelper(this)
        setContentView(createSignUpView())
        setupListeners()
    }

    private fun createSignUpView(): View {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor(AppColors.BLACK_PRIMARY))
            setPadding(60, 80, 60, 80)
            gravity = Gravity.CENTER
        }

        TextView(this).apply {
            text = "⚡ EV HUB"
            textSize = 42f
            setTextColor(Color.parseColor(AppColors.GREEN_NEON))
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 60)
            layout.addView(this)
        }

        TextView(this).apply {
            text = "Create Operator Account"
            textSize = 20f
            setTextColor(Color.parseColor(AppColors.WHITE))
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 50)
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

        etConfirmPassword = EditText(this).apply {
            hint = "Confirm Password"
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

        btnSignUp = Button(this).apply {
            text = "SIGN UP"
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

        tvLogin = TextView(this).apply {
            text = "Already have an account? Login"
            setTextColor(Color.parseColor(AppColors.BLUE))
            textSize = 14f
            gravity = Gravity.CENTER
            layout.addView(this)
        }

        return layout
    }

    private fun setupListeners() {
        btnSignUp.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (validateInput(username, password, confirmPassword)) {
                performSignUp(username, password)
            }
        }

        tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun validateInput(username: String, password: String, confirmPassword: String): Boolean {
        return when {
            username.isEmpty() -> {
                showError("Username is required")
                false
            }
            username.length < 4 -> {
                showError("Username must be at least 4 characters")
                false
            }
            password.isEmpty() -> {
                showError("Password is required")
                false
            }
            password.length < 6 -> {
                showError("Password must be at least 6 characters")
                false
            }
            password != confirmPassword -> {
                showError("Passwords do not match")
                false
            }
            else -> true
        }
    }

    private fun performSignUp(username: String, password: String) {
        if (dbHelper.usernameExists(username)) {
            showError("Username already exists")
            return
        }

        val user = User(
            username = username,
            password = password,
            role = "OPERATOR",
            createdAt = System.currentTimeMillis()
        )

        val result = dbHelper.insertOperator(user)

        if (result != -1L) {
            Toast.makeText(
                this,
                "Account created successfully!",
                Toast.LENGTH_LONG
            ).show()
            finish()
        } else {
            showError("Failed to create account. Please try again.")
        }
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
    }
}