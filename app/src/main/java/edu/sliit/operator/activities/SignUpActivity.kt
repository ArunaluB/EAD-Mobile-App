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
        val scrollView = ScrollView(this).apply {
            setBackgroundColor(Color.parseColor("#0A0E27")) // Deep navy
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 60, 48, 80)
            gravity = Gravity.CENTER
        }

        // Logo section
        LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 50)

            addView(TextView(this@SignUpActivity).apply {
                text = "⚡"
                textSize = 80f
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, 20)
                setShadowLayer(30f, 0f, 0f, Color.parseColor("#00FF88"))
            })

            addView(TextView(this@SignUpActivity).apply {
                text = "EV HUB"
                textSize = 48f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setTextColor(Color.parseColor("#FFFFFF"))
                gravity = Gravity.CENTER
                setShadowLayer(15f, 0f, 3f, Color.parseColor("#00FF88"))
                setPadding(0, 0, 0, 12)
            })

            addView(TextView(this@SignUpActivity).apply {
                text = "━━━━━━━━━━━━"
                textSize = 14f
                setTextColor(Color.parseColor("#00D9FF"))
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, 12)
            })

            addView(TextView(this@SignUpActivity).apply {
                text = "Create Operator Account"
                textSize = 18f
                setTextColor(Color.parseColor("#00D9FF"))
                gravity = Gravity.CENTER
            })

            layout.addView(this)
        }

        // Sign up form card
        LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#1A1F3A"))
            setPadding(32, 40, 32, 40)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 24)
            layoutParams = params

            addView(TextView(this@SignUpActivity).apply {
                text = "✨ Register New Account"
                textSize = 24f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setTextColor(Color.parseColor("#00D9FF"))
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, 32)
            })

            etUsername = EditText(this@SignUpActivity).apply {
                hint = "👤 Choose Username"
                setHintTextColor(Color.parseColor("#6B7A99"))
                setTextColor(Color.parseColor("#FFFFFF"))
                setBackgroundColor(Color.parseColor("#0F1624"))
                setPadding(44, 44, 44, 44)
                textSize = 17f
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 20)
                layoutParams = params
            }
            addView(etUsername)

            etPassword = EditText(this@SignUpActivity).apply {
                hint = "🔒 Create Password"
                setHintTextColor(Color.parseColor("#6B7A99"))
                setTextColor(Color.parseColor("#FFFFFF"))
                setBackgroundColor(Color.parseColor("#0F1624"))
                setPadding(44, 44, 44, 44)
                textSize = 17f
                inputType = android.text.InputType.TYPE_CLASS_TEXT or
                        android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 20)
                layoutParams = params
            }
            addView(etPassword)

            etConfirmPassword = EditText(this@SignUpActivity).apply {
                hint = "🔐 Confirm Password"
                setHintTextColor(Color.parseColor("#6B7A99"))
                setTextColor(Color.parseColor("#FFFFFF"))
                setBackgroundColor(Color.parseColor("#0F1624"))
                setPadding(44, 44, 44, 44)
                textSize = 17f
                inputType = android.text.InputType.TYPE_CLASS_TEXT or
                        android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 20)
                layoutParams = params
            }
            addView(etConfirmPassword)

            // Password requirements
            addView(TextView(this@SignUpActivity).apply {
                text = """
                    📋 Password Requirements:
                    • Minimum 6 characters
                    • Must include letters and numbers
                """.trimIndent()
                textSize = 13f
                setTextColor(Color.parseColor("#8B9DC3"))
                setPadding(20, 0, 20, 20)
            })

            layout.addView(this)
        }

        tvError = TextView(this).apply {
            setTextColor(Color.parseColor("#FF3366"))
            textSize = 15f
            visibility = View.GONE
            gravity = Gravity.CENTER
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setBackgroundColor(Color.parseColor("#2A1520"))
            setPadding(20, 16, 20, 16)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 20)
            layoutParams = params
            layout.addView(this)
        }

        btnSignUp = Button(this).apply {
            text = "CREATE ACCOUNT"
            textSize = 19f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#0A0E27"))
            setBackgroundColor(Color.parseColor("#00FF88"))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.height = 160
            params.setMargins(0, 0, 0, 24)
            layoutParams = params
            setShadowLayer(25f, 0f, 5f, Color.parseColor("#00FF88"))
            layout.addView(this)
        }

        tvLogin = TextView(this).apply {
            text = "Already have an account? Login"
            setTextColor(Color.parseColor("#00D9FF"))
            textSize = 15f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(0, 16, 0, 16)
            layout.addView(this)
        }

        scrollView.addView(layout)
        return scrollView
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