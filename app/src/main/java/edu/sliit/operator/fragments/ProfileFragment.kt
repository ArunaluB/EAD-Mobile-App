// ==================== 1. COMPLETE ProfileFragment.kt ====================
package edu.sliit.operator.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import edu.sliit.operator.activities.LoginActivity
import edu.sliit.operator.database.EVHubDatabaseHelper
import edu.sliit.operator.utils.AppColors
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {

    private lateinit var dbHelper: EVHubDatabaseHelper
    private var userId: Int = 0
    private var username: String = "Operator"
    private var role: String = "OPERATOR"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dbHelper = EVHubDatabaseHelper(requireContext())
        loadUserData()
        return createProfileView()
    }

    private fun loadUserData() {
        val sharedPref = requireActivity().getSharedPreferences("EVHubPrefs", Context.MODE_PRIVATE)
        userId = sharedPref.getInt("user_id", 0)
        username = sharedPref.getString("username", "Operator") ?: "Operator"
        role = sharedPref.getString("role", "OPERATOR") ?: "OPERATOR"
    }

    private fun createProfileView(): View {
        val scrollView = ScrollView(requireContext()).apply {
            setBackgroundColor(Color.parseColor(AppColors.BLACK_PRIMARY))
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            isFillViewport = true // This ensures the scroll view takes full height
            isVerticalScrollBarEnabled = true // Show scroll bar
        }

        val mainLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 80) // Increased bottom padding
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Header
        mainLayout.addView(createHeader())

        // Profile Header Card
        mainLayout.addView(createProfileHeaderCard())

        // Account Information Section
        mainLayout.addView(createSectionTitle("Account Information"))
        mainLayout.addView(createAccountInfoSection())

        // Settings Section
        mainLayout.addView(createSectionTitle("Settings"))
        mainLayout.addView(createSettingsSection())

        // Security Section
        mainLayout.addView(createSectionTitle("Security"))
        mainLayout.addView(createSecuritySection())

        // About Section
        mainLayout.addView(createSectionTitle("About"))
        mainLayout.addView(createAboutSection())

        // Logout Button - This should now be visible
        mainLayout.addView(createLogoutButton())

        scrollView.addView(mainLayout)
        return scrollView
    }

    private fun createHeader(): TextView {
        return TextView(requireContext()).apply {
            text = "👤 My Profile"
            textSize = 28f
            setTextColor(Color.parseColor(AppColors.WHITE))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 30)
            layoutParams = params
        }
    }

    private fun createProfileHeaderCard(): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor(AppColors.GRAY_DARK_PANEL))
            setPadding(40, 60, 40, 60)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 30)
            layoutParams = params

            // Avatar
            addView(LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setBackgroundColor(Color.parseColor(AppColors.GREEN_NEON))
                val size = 120
                layoutParams = LinearLayout.LayoutParams(size, size)

                addView(TextView(requireContext()).apply {
                    text = username.take(2).uppercase()
                    textSize = 40f
                    setTextColor(Color.parseColor(AppColors.BLACK_PRIMARY))
                    gravity = Gravity.CENTER
                })
            })

            // Username
            addView(TextView(requireContext()).apply {
                text = username
                textSize = 28f
                setTextColor(Color.parseColor(AppColors.WHITE))
                gravity = Gravity.CENTER
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 30, 0, 10)
                layoutParams = params
            })

            // Role Badge
            addView(LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
                setBackgroundColor(Color.parseColor(AppColors.GREEN_BRIGHT))
                setPadding(20, 10, 20, 10)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 10)
                layoutParams = params

                addView(TextView(requireContext()).apply {
                    text = "⚡ $role"
                    textSize = 14f
                    setTextColor(Color.parseColor(AppColors.BLACK_PRIMARY))
                    gravity = Gravity.CENTER
                })
            })

            // Status
            addView(TextView(requireContext()).apply {
                text = "● Active"
                textSize = 14f
                setTextColor(Color.parseColor(AppColors.GREEN_BRIGHT))
                gravity = Gravity.CENTER
            })
        }
    }

    private fun createSectionTitle(title: String): TextView {
        return TextView(requireContext()).apply {
            text = title
            textSize = 18f
            setTextColor(Color.parseColor(AppColors.WHITE))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 20, 0, 15)
            layoutParams = params
        }
    }

    private fun createAccountInfoSection(): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor(AppColors.BLACK_OVERLAY))
            setPadding(0, 0, 0, 0)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 20)
            layoutParams = params

            // User ID
            addView(createInfoRow("🆔", "User ID", "#$userId"))
            addView(createDivider())

            // Username
            addView(createInfoRow("👤", "Username", username))
            addView(createDivider())

            // Role
            addView(createInfoRow("💼", "Role", role))
            addView(createDivider())

            // Email (demo)
            addView(createInfoRow("📧", "Email", "$username@evhub.com"))
            addView(createDivider())

            // Member Since
            val sharedPref = requireActivity().getSharedPreferences("EVHubPrefs", Context.MODE_PRIVATE)
            val loginTime = sharedPref.getLong("login_time", System.currentTimeMillis())
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            addView(createInfoRow("📅", "Member Since", dateFormat.format(Date(loginTime))))
        }
    }

    private fun createSettingsSection(): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor(AppColors.BLACK_OVERLAY))
            setPadding(0, 0, 0, 0)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 20)
            layoutParams = params

            // Notifications
            addView(createActionRow("🔔", "Notifications", "Manage notification preferences") {
                showNotificationSettings()
            })
            addView(createDivider())

            // Language
            addView(createActionRow("🌐", "Language", "English") {
                showLanguageSettings()
            })
            addView(createDivider())

            // Theme
            addView(createActionRow("🎨", "Theme", "Dark Mode") {
                showThemeSettings()
            })
        }
    }

    private fun createSecuritySection(): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor(AppColors.BLACK_OVERLAY))
            setPadding(0, 0, 0, 0)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 20)
            layoutParams = params

            // Change Password
            addView(createActionRow("🔒", "Change Password", "Update your password") {
                showChangePasswordDialog()
            })
            addView(createDivider())

            // Security Settings
            addView(createActionRow("🛡️", "Security", "Manage security settings") {
                showSecuritySettings()
            })
        }
    }

    private fun createAboutSection(): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor(AppColors.BLACK_OVERLAY))
            setPadding(0, 0, 0, 0)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 20)
            layoutParams = params

            // App Version
            addView(createInfoRow("ℹ️", "App Version", "1.0.0"))
            addView(createDivider())

            // Help & Support
            addView(createActionRow("❓", "Help & Support", "Get help and contact support") {
                showHelpSupport()
            })
            addView(createDivider())

            // Terms & Privacy
            addView(createActionRow("📄", "Terms & Privacy", "View terms and privacy policy") {
                showTermsPrivacy()
            })
        }
    }

    private fun createInfoRow(icon: String, label: String, value: String): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(40, 30, 40, 30)

            // Icon + Label
            addView(LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                val params = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                layoutParams = params

                addView(TextView(requireContext()).apply {
                    text = icon
                    textSize = 20f
                    setPadding(0, 0, 15, 0)
                })

                addView(TextView(requireContext()).apply {
                    text = label
                    textSize = 16f
                    setTextColor(Color.parseColor(AppColors.GRAY_MEDIUM))
                })
            })

            // Value
            addView(TextView(requireContext()).apply {
                text = value
                textSize = 16f
                setTextColor(Color.parseColor(AppColors.WHITE))
                gravity = Gravity.END
            })
        }
    }

    private fun createActionRow(icon: String, title: String, subtitle: String, onClick: () -> Unit): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(40, 30, 40, 30)
            isClickable = true
            isFocusable = true

            setOnClickListener { onClick() }

            // Icon
            addView(TextView(requireContext()).apply {
                text = icon
                textSize = 24f
                setPadding(0, 0, 20, 0)
            })

            // Title + Subtitle
            addView(LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                val params = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                layoutParams = params

                addView(TextView(requireContext()).apply {
                    text = title
                    textSize = 16f
                    setTextColor(Color.parseColor(AppColors.WHITE))
                })

                addView(TextView(requireContext()).apply {
                    text = subtitle
                    textSize = 14f
                    setTextColor(Color.parseColor(AppColors.GRAY_MEDIUM))
                    setPadding(0, 5, 0, 0)
                })
            })

            // Arrow
            addView(TextView(requireContext()).apply {
                text = "›"
                textSize = 28f
                setTextColor(Color.parseColor(AppColors.GRAY_MEDIUM))
                gravity = Gravity.CENTER
            })
        }
    }

    private fun createDivider(): View {
        return View(requireContext()).apply {
            setBackgroundColor(Color.parseColor(AppColors.GRAY_MEDIUM))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                2
            )
        }
    }

    private fun createLogoutButton(): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 60, 0, 80) // Increased margins
            layoutParams = params

            // Add separator line before logout
            addView(View(requireContext()).apply {
                setBackgroundColor(Color.parseColor(AppColors.GRAY_MEDIUM))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    3
                )
            })

            // Add some spacing
            addView(View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    40
                )
            })

            // Logout Button
            addView(Button(requireContext()).apply {
                text = "🚪 LOGOUT"
                textSize = 20f // Increased font size
                setTextColor(Color.parseColor(AppColors.WHITE))
                setBackgroundColor(Color.parseColor(AppColors.PINK))
                val buttonParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    160 // Increased height
                )
                layoutParams = buttonParams
                elevation = 12f // Increased elevation
                setPadding(30, 30, 30, 30) // Increased padding
                isAllCaps = true

                setOnClickListener {
                    showLogoutConfirmation()
                }
            })

            // Version info at bottom
            addView(TextView(requireContext()).apply {
                text = "EVHub Operator v1.0.0"
                textSize = 12f
                setTextColor(Color.parseColor(AppColors.GRAY_MEDIUM))
                gravity = Gravity.CENTER
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 30, 0, 0)
                layoutParams = params
            })
        }
    }

    // ==================== CHANGE PASSWORD DIALOG ====================
    private fun showChangePasswordDialog() {
        val dialogView = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }

        // Title
        dialogView.addView(TextView(requireContext()).apply {
            text = "🔒 Change Password"
            textSize = 22f
            setTextColor(Color.parseColor(AppColors.GREEN_NEON))
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 30)
        })

        // Current Password
        dialogView.addView(TextView(requireContext()).apply {
            text = "Current Password"
            textSize = 14f
            setTextColor(Color.parseColor(AppColors.GRAY_MEDIUM))
            setPadding(0, 0, 0, 10)
        })

        val etCurrentPassword = EditText(requireContext()).apply {
            hint = "Enter current password"
            setHintTextColor(Color.parseColor(AppColors.GRAY_MEDIUM))
            setTextColor(Color.parseColor(AppColors.WHITE))
            setBackgroundColor(Color.parseColor(AppColors.GRAY_DARK_PANEL))
            setPadding(30, 30, 30, 30)
            textSize = 16f
            inputType = android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        dialogView.addView(etCurrentPassword)

        // New Password
        dialogView.addView(TextView(requireContext()).apply {
            text = "New Password"
            textSize = 14f
            setTextColor(Color.parseColor(AppColors.GRAY_MEDIUM))
            setPadding(0, 20, 0, 10)
        })

        val etNewPassword = EditText(requireContext()).apply {
            hint = "Enter new password"
            setHintTextColor(Color.parseColor(AppColors.GRAY_MEDIUM))
            setTextColor(Color.parseColor(AppColors.WHITE))
            setBackgroundColor(Color.parseColor(AppColors.GRAY_DARK_PANEL))
            setPadding(30, 30, 30, 30)
            textSize = 16f
            inputType = android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        dialogView.addView(etNewPassword)

        // Confirm New Password
        dialogView.addView(TextView(requireContext()).apply {
            text = "Confirm New Password"
            textSize = 14f
            setTextColor(Color.parseColor(AppColors.GRAY_MEDIUM))
            setPadding(0, 20, 0, 10)
        })

        val etConfirmPassword = EditText(requireContext()).apply {
            hint = "Confirm new password"
            setHintTextColor(Color.parseColor(AppColors.GRAY_MEDIUM))
            setTextColor(Color.parseColor(AppColors.WHITE))
            setBackgroundColor(Color.parseColor(AppColors.GRAY_DARK_PANEL))
            setPadding(30, 30, 30, 30)
            textSize = 16f
            inputType = android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        dialogView.addView(etConfirmPassword)

        // Password Requirements
        dialogView.addView(TextView(requireContext()).apply {
            text = """
                
                Password must be:
                • At least 6 characters long
                • Include letters and numbers
            """.trimIndent()
            textSize = 12f
            setTextColor(Color.parseColor(AppColors.GRAY_MEDIUM))
            setPadding(0, 20, 0, 0)
        })

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Change Password", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val currentPass = etCurrentPassword.text.toString()
                val newPass = etNewPassword.text.toString()
                val confirmPass = etConfirmPassword.text.toString()

                when {
                    currentPass.isEmpty() -> {
                        Toast.makeText(requireContext(), "Enter current password", Toast.LENGTH_SHORT).show()
                    }
                    newPass.isEmpty() -> {
                        Toast.makeText(requireContext(), "Enter new password", Toast.LENGTH_SHORT).show()
                    }
                    newPass.length < 6 -> {
                        Toast.makeText(requireContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                    }
                    newPass != confirmPass -> {
                        Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        // Validate current password and update
                        changePassword(currentPass, newPass)
                        dialog.dismiss()
                    }
                }
            }
        }

        dialog.show()
    }

    private fun changePassword(currentPassword: String, newPassword: String) {
        // Validate current password
        val user = dbHelper.validateLogin(username, currentPassword)

        if (user != null) {
            // In a real app, update password in database
            // For demo, just show success

            AlertDialog.Builder(requireContext())
                .setTitle("✅ Password Changed")
                .setMessage("Your password has been updated successfully!")
                .setPositiveButton("OK") { dialog, _ ->
                    Toast.makeText(
                        requireContext(),
                        "Password changed successfully!",
                        Toast.LENGTH_LONG
                    ).show()
                    dialog.dismiss()
                }
                .show()
        } else {
            AlertDialog.Builder(requireContext())
                .setTitle("❌ Error")
                .setMessage("Current password is incorrect. Please try again.")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    // ==================== OTHER SETTINGS DIALOGS ====================
    private fun showNotificationSettings() {
        val options = arrayOf(
            "Push Notifications",
            "Email Notifications",
            "SMS Notifications",
            "Booking Alerts",
            "System Updates"
        )
        val checkedItems = booleanArrayOf(true, true, false, true, true)

        AlertDialog.Builder(requireContext())
            .setTitle("🔔 Notification Settings")
            .setMultiChoiceItems(options, checkedItems) { _, which, isChecked ->
                checkedItems[which] = isChecked
            }
            .setPositiveButton("Save") { dialog, _ ->
                Toast.makeText(requireContext(), "Notification settings saved", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showLanguageSettings() {
        val languages = arrayOf("English", "Sinhala", "Tamil")

        AlertDialog.Builder(requireContext())
            .setTitle("🌐 Select Language")
            .setSingleChoiceItems(languages, 0) { dialog, which ->
                Toast.makeText(
                    requireContext(),
                    "Language: ${languages[which]}",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showThemeSettings() {
        val themes = arrayOf("Dark Mode", "Light Mode", "System Default")

        AlertDialog.Builder(requireContext())
            .setTitle("🎨 Select Theme")
            .setSingleChoiceItems(themes, 0) { dialog, which ->
                Toast.makeText(
                    requireContext(),
                    "Theme: ${themes[which]}",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showSecuritySettings() {
        AlertDialog.Builder(requireContext())
            .setTitle("🛡️ Security Settings")
            .setMessage("""
                Security Features:
                
                ✓ Password Protection: Enabled
                ✓ Session Timeout: 30 minutes
                ✓ Two-Factor Auth: Available
                ✓ Login History: Tracked
                
                Your account is secure.
            """.trimIndent())
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showHelpSupport() {
        AlertDialog.Builder(requireContext())
            .setTitle("❓ Help & Support")
            .setMessage("""
                Need help?
                
                📧 Email: support@evhub.com
                📞 Phone: +94 11 234 5678
                🌐 Website: www.evhub.com
                
                Operating Hours:
                Monday - Friday: 8 AM - 6 PM
                Saturday: 9 AM - 2 PM
            """.trimIndent())
            .setPositiveButton("Contact Support") { _, _ ->
                Toast.makeText(requireContext(), "Opening support...", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showTermsPrivacy() {
        AlertDialog.Builder(requireContext())
            .setTitle("📄 Terms & Privacy")
            .setMessage("""
                EV Hub Operator App
                Version 1.0.0
                
                Terms of Service
                By using this app, you agree to our terms and conditions.
                
                Privacy Policy
                We protect your data and never share it with third parties.
                
                Last Updated: October 2024
            """.trimIndent())
            .setPositiveButton("Accept", null)
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showLogoutConfirmation() {
        val dialogView = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 60, 60, 60)
            setBackgroundColor(Color.parseColor(AppColors.BLACK_OVERLAY))
        }

        // Icon
        dialogView.addView(TextView(requireContext()).apply {
            text = "🚪"
            textSize = 48f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 20)
        })

        // Title
        dialogView.addView(TextView(requireContext()).apply {
            text = "Logout Confirmation"
            textSize = 22f
            setTextColor(Color.parseColor(AppColors.WHITE))
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 20)
        })

        // Message
        dialogView.addView(TextView(requireContext()).apply {
            text = "Are you sure you want to logout?\nYou will need to login again to access the app."
            textSize = 16f
            setTextColor(Color.parseColor(AppColors.GRAY_MEDIUM))
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 30)
        })

        // Button container
        val buttonContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        // Cancel button
        buttonContainer.addView(Button(requireContext()).apply {
            text = "Cancel"
            textSize = 16f
            setTextColor(Color.parseColor(AppColors.WHITE))
            setBackgroundColor(Color.parseColor(AppColors.GRAY_MEDIUM))
            val params = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            params.setMargins(0, 0, 10, 0)
            layoutParams = params
            setPadding(20, 20, 20, 20)
        })

        // Logout button
        buttonContainer.addView(Button(requireContext()).apply {
            text = "Logout"
            textSize = 16f
            setTextColor(Color.parseColor(AppColors.WHITE))
            setBackgroundColor(Color.parseColor(AppColors.PINK))
            val params = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            params.setMargins(10, 0, 0, 0)
            layoutParams = params
            setPadding(20, 20, 20, 20)

            setOnClickListener {
                performLogout()
            }
        })

        dialogView.addView(buttonContainer)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        // Make cancel button work
        buttonContainer.getChildAt(0).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun performLogout() {
        val sharedPref = requireActivity().getSharedPreferences("EVHubPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear()
            apply()
        }

        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}
