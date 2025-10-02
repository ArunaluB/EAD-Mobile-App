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
            setBackgroundColor(Color.parseColor(AppColors.BLACK_PRIMARY))
        }

        val mainLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }

        mainLayout.addView(createHeader())
        mainLayout.addView(createWelcomeCard())
        mainLayout.addView(createStatsSection())
        mainLayout.addView(createScheduleSection())

        scrollView.addView(mainLayout)
        return scrollView
    }

    private fun createHeader(): TextView {
        return TextView(requireContext()).apply {
            text = "⚡ Dashboard"
            textSize = 28f
            setTextColor(Color.parseColor(AppColors.WHITE))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 40)
            layoutParams = params
        }
    }

    private fun createWelcomeCard(): LinearLayout {
        val username = requireActivity()
            .getSharedPreferences("EVHubPrefs", Context.MODE_PRIVATE)
            .getString("username", "Operator") ?: "Operator"

        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor(AppColors.GRAY_DARK_PANEL))
            setPadding(40, 40, 40, 40)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 30)
            layoutParams = params

            addView(TextView(requireContext()).apply {
                text = "Welcome back, $username!"
                textSize = 20f
                setTextColor(Color.parseColor(AppColors.GREEN_NEON))
            })

            addView(TextView(requireContext()).apply {
                text = "Ready to manage your charging stations"
                textSize = 14f
                setTextColor(Color.parseColor(AppColors.GRAY_MEDIUM))
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 10, 0, 0)
                layoutParams = params
            })
        }
    }

    private fun createStatsSection(): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 30)
            layoutParams = params

            addView(TextView(requireContext()).apply {
                text = "Today's Overview"
                textSize = 18f
                setTextColor(Color.parseColor(AppColors.WHITE))
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 20)
                layoutParams = params
            })

            val statsRow = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
            }
            statsRow.addView(createStatCard("8", "Upcoming", AppColors.BLUE))
            statsRow.addView(createStatCard("3", "Pending", AppColors.PINK))
            addView(statsRow)

            val statsRow2 = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
            }
            statsRow2.addView(createStatCard("12", "Completed", AppColors.GREEN_BRIGHT))
            statsRow2.addView(createStatCard("85%", "Utilization", AppColors.PURPLE))
            addView(statsRow2)
        }
    }

    private fun createStatCard(value: String, label: String, color: String): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor(AppColors.BLACK_OVERLAY))
            setPadding(30, 30, 30, 30)
            gravity = Gravity.CENTER
            val params = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            params.setMargins(5, 5, 5, 5)
            layoutParams = params

            addView(TextView(requireContext()).apply {
                text = value
                textSize = 32f
                setTextColor(Color.parseColor(color))
                gravity = Gravity.CENTER
            })

            addView(TextView(requireContext()).apply {
                text = label
                textSize = 12f
                setTextColor(Color.parseColor(AppColors.GRAY_MEDIUM))
                gravity = Gravity.CENTER
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 10, 0, 0)
                layoutParams = params
            })
        }
    }

    private fun createScheduleSection(): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL

            addView(TextView(requireContext()).apply {
                text = "Next 4 Hours"
                textSize = 18f
                setTextColor(Color.parseColor(AppColors.WHITE))
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 20)
                layoutParams = params
            })

            addView(createBookingItem("10:00 AM", "Station A - Slot 1", "John Doe", "Approved"))
            addView(createBookingItem("11:30 AM", "Station B - Slot 2", "Jane Smith", "Pending"))
            addView(createBookingItem("01:00 PM", "Station A - Slot 3", "Mike Johnson", "Approved"))
        }
    }

    private fun createBookingItem(time: String, station: String, name: String, status: String): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(Color.parseColor(AppColors.GRAY_DARK_PANEL))
            setPadding(30, 30, 30, 30)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 15)
            layoutParams = params

            addView(TextView(requireContext()).apply {
                text = time
                textSize = 14f
                setTextColor(Color.parseColor(AppColors.GREEN_NEON))
                val params = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                layoutParams = params
            })

            val detailsLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                val params = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    2f
                )
                layoutParams = params
            }

            detailsLayout.addView(TextView(requireContext()).apply {
                text = station
                textSize = 14f
                setTextColor(Color.parseColor(AppColors.WHITE))
            })

            detailsLayout.addView(TextView(requireContext()).apply {
                text = name
                textSize = 12f
                setTextColor(Color.parseColor(AppColors.GRAY_MEDIUM))
            })

            addView(detailsLayout)

            addView(TextView(requireContext()).apply {
                text = status
                textSize = 12f
                val statusColor = if (status == "Approved") AppColors.GREEN_BRIGHT else AppColors.PINK
                setTextColor(Color.parseColor(statusColor))
                gravity = Gravity.END
                val params = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                layoutParams = params
            })
        }
    }
}