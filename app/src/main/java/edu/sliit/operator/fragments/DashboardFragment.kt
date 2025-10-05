package edu.sliit.operator.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import edu.sliit.operator.utils.AppColors

class DashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return createDashboardView()
    }

    private fun createDashboardView(): View {
        val scrollView = ScrollView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#0A0E27")) // Deep navy
        }

        val mainLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }

        mainLayout.addView(createModernHeader())
        mainLayout.addView(createModernWelcomeCard())
        mainLayout.addView(createModernStatsSection())
        mainLayout.addView(createModernScheduleSection())

        scrollView.addView(mainLayout)
        return scrollView
    }

    private fun createModernHeader(): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 28)
            layoutParams = params

            addView(TextView(requireContext()).apply {
                text = "⚡"
                textSize = 40f
                setPadding(0, 0, 16, 0)
                setShadowLayer(20f, 0f, 0f, Color.parseColor("#00FF88"))
            })

            addView(TextView(requireContext()).apply {
                text = "Dashboard"
                textSize = 32f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setTextColor(Color.parseColor("#FFFFFF"))
                setShadowLayer(10f, 0f, 2f, Color.parseColor("#000000"))
            })
        }
    }

    private fun createModernWelcomeCard(): LinearLayout {
        val username = requireActivity()
            .getSharedPreferences("EVHubPrefs", Context.MODE_PRIVATE)
            .getString("username", "Operator") ?: "Operator"

        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#1A1F3A"))
            setPadding(32, 32, 32, 32)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 24)
            layoutParams = params

            addView(TextView(requireContext()).apply {
                text = "👋 Welcome Back!"
                textSize = 24f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setTextColor(Color.parseColor("#00D9FF"))
                setPadding(0, 0, 0, 12)
            })

            addView(TextView(requireContext()).apply {
                text = username
                textSize = 20f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setTextColor(Color.parseColor("#FFFFFF"))
                setPadding(0, 0, 0, 12)
            })

            addView(TextView(requireContext()).apply {
                text = "Ready to manage your charging stations"
                textSize = 15f
                setTextColor(Color.parseColor("#8B9DC3"))
            })
        }
    }

    private fun createModernStatsSection(): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 28)
            layoutParams = params

            addView(TextView(requireContext()).apply {
                text = "📊 Today's Overview"
                textSize = 22f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setTextColor(Color.parseColor("#FFFFFF"))
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 20)
                layoutParams = params
            })

            val statsRow = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 12)
                layoutParams = params
            }
            statsRow.addView(createModernStatCard("8", "Upcoming", "#00D9FF", "🔜"))
            statsRow.addView(createModernStatCard("3", "Pending", "#FF3366", "⏳"))
            addView(statsRow)

            val statsRow2 = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
            }
            statsRow2.addView(createModernStatCard("12", "Completed", "#00FF88", "✅"))
            statsRow2.addView(createModernStatCard("85%", "Utilization", "#7B2CBF", "📈"))
            addView(statsRow2)
        }
    }

    private fun createModernStatCard(value: String, label: String, color: String, icon: String): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#16213E"))
            setPadding(24, 28, 24, 28)
            gravity = Gravity.CENTER
            val params = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            params.setMargins(6, 6, 6, 6)
            layoutParams = params

            addView(TextView(requireContext()).apply {
                text = icon
                textSize = 36f
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, 12)
            })

            addView(TextView(requireContext()).apply {
                text = value
                textSize = 36f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setTextColor(Color.parseColor(color))
                gravity = Gravity.CENTER
                setShadowLayer(15f, 0f, 0f, Color.parseColor(color))
                setPadding(0, 0, 0, 8)
            })

            addView(TextView(requireContext()).apply {
                text = label
                textSize = 13f
                setTextColor(Color.parseColor("#8B9DC3"))
                gravity = Gravity.CENTER
            })
        }
    }

    private fun createModernScheduleSection(): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL

            addView(TextView(requireContext()).apply {
                text = "🕐 Next 4 Hours"
                textSize = 22f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setTextColor(Color.parseColor("#FFFFFF"))
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 20)
                layoutParams = params
            })

            addView(createModernBookingItem("10:00 AM", "Station A - Slot 1", "John Doe", "Approved"))
            addView(createModernBookingItem("11:30 AM", "Station B - Slot 2", "Jane Smith", "Pending"))
            addView(createModernBookingItem("01:00 PM", "Station A - Slot 3", "Mike Johnson", "Approved"))
        }
    }

    private fun createModernBookingItem(time: String, station: String, name: String, status: String): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#1A1F3A"))
            setPadding(24, 24, 24, 24)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 12)
            layoutParams = params

            // Time and status row
            val topRow = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 0, 0, 12)
            }

            topRow.addView(TextView(requireContext()).apply {
                text = "⏰ $time"
                textSize = 16f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setTextColor(Color.parseColor("#00D9FF"))
                val params = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                layoutParams = params
            })

            val statusColor = if (status == "Approved") "#00FF88" else "#FF3366"
            val statusIcon = if (status == "Approved") "✅" else "⏳"
            topRow.addView(TextView(requireContext()).apply {
                text = "$statusIcon $status"
                textSize = 14f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setTextColor(Color.parseColor(statusColor))
                gravity = Gravity.END
            })

            addView(topRow)

            // Station info
            addView(TextView(requireContext()).apply {
                text = "🏢 $station"
                textSize = 15f
                setTextColor(Color.parseColor("#FFFFFF"))
                setPadding(0, 0, 0, 8)
            })

            // Customer name
            addView(TextView(requireContext()).apply {
                text = "👤 $name"
                textSize = 14f
                setTextColor(Color.parseColor("#8B9DC3"))
            })
        }
    }
}