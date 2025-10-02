package edu.sliit.operator.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import edu.sliit.operator.activities.LoginActivity
import edu.sliit.operator.utils.AppColors

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor(AppColors.BLACK_PRIMARY))
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setPadding(60, 60, 60, 60)

            addView(TextView(requireContext()).apply {
                text = "👤"
                textSize = 80f
                gravity = Gravity.CENTER
            })

            // Get username
            val username = try {
                requireActivity()
                    .getSharedPreferences("EVHubPrefs", Context.MODE_PRIVATE)
                    .getString("username", "Operator") ?: "Operator"
            } catch (e: Exception) {
                "Operator"
            }

            addView(TextView(requireContext()).apply {
                text = username
                textSize = 28f
                gravity = Gravity.CENTER
                setTextColor(Color.parseColor(AppColors.WHITE))
                setPadding(0, 40, 0, 20)
            })

            addView(TextView(requireContext()).apply {
                text = "OPERATOR"
                textSize = 16f
                gravity = Gravity.CENTER
                setTextColor(Color.parseColor(AppColors.GREEN_NEON))
                setPadding(0, 0, 0, 60)
            })

            // Logout button
            addView(Button(requireContext()).apply {
                text = "LOGOUT"
                textSize = 18f
                setTextColor(Color.parseColor(AppColors.WHITE))
                setBackgroundColor(Color.parseColor(AppColors.PINK))
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    140
                )
                layoutParams = params

                setOnClickListener {
                    try {
                        // Clear session
                        val sharedPref = requireActivity()
                            .getSharedPreferences("EVHubPrefs", Context.MODE_PRIVATE)
                        sharedPref.edit().clear().apply()

                        // Go to login
                        val intent = Intent(requireActivity(), LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        requireActivity().finish()
                    } catch (e: Exception) {
                        // Ignore error
                    }
                }
            })
        }
    }
}
