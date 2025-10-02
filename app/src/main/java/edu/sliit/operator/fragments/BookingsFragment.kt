package edu.sliit.operator.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import edu.sliit.operator.utils.AppColors

class BookingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return createBookingsView()
    }

    private fun createBookingsView(): View {
        val scrollView = ScrollView(requireContext()).apply {
            setBackgroundColor(Color.parseColor(AppColors.BLACK_PRIMARY))
        }

        val mainLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }

        mainLayout.addView(TextView(requireContext()).apply {
            text = "📋 Bookings"
            textSize = 28f
            setTextColor(Color.parseColor(AppColors.WHITE))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 30)
            layoutParams = params
        })

        val filterRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 30)
            layoutParams = params
        }

        filterRow.addView(createFilterButton("All", true))
        filterRow.addView(createFilterButton("Pending", false))
        filterRow.addView(createFilterButton("Approved", false))
        mainLayout.addView(filterRow)

        mainLayout.addView(createBookingCard("BK-001", "John Doe", "Station A - Slot 1", "Today 10:00 AM", "Approved"))
        mainLayout.addView(createBookingCard("BK-002", "Jane Smith", "Station B - Slot 2", "Today 11:30 AM", "Pending"))
        mainLayout.addView(createBookingCard("BK-003", "Mike Johnson", "Station A - Slot 3", "Today 01:00 PM", "Approved"))

        scrollView.addView(mainLayout)
        return scrollView
    }

    private fun createFilterButton(text: String, isSelected: Boolean): Button {
        return Button(requireContext()).apply {
            this.text = text
            textSize = 14f
            val bgColor = if (isSelected) AppColors.GREEN_NEON else AppColors.GRAY_DARK_PANEL
            val txtColor = if (isSelected) AppColors.BLACK_PRIMARY else AppColors.WHITE
            setBackgroundColor(Color.parseColor(bgColor))
            setTextColor(Color.parseColor(txtColor))
            val params = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            params.setMargins(5, 0, 5, 0)
            layoutParams = params
        }
    }

    private fun createBookingCard(id: String, name: String, station: String, time: String, status: String): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor(AppColors.GRAY_DARK_PANEL))
            setPadding(40, 40, 40, 40)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 20)
            layoutParams = params

            val headerRow = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
            }

            headerRow.addView(TextView(requireContext()).apply {
                text = id
                textSize = 16f
                setTextColor(Color.parseColor(AppColors.GREEN_NEON))
                val params = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                layoutParams = params
            })

            headerRow.addView(TextView(requireContext()).apply {
                text = status
                textSize = 12f
                val statusColor = when (status) {
                    "Approved" -> AppColors.GREEN_BRIGHT
                    "Pending" -> AppColors.PINK
                    else -> AppColors.GRAY_MEDIUM
                }
                setTextColor(Color.parseColor(statusColor))
                gravity = Gravity.END
            })

            addView(headerRow)

            addView(TextView(requireContext()).apply {
                text = name
                textSize = 18f
                setTextColor(Color.parseColor(AppColors.WHITE))
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 15, 0, 5)
                layoutParams = params
            })

            addView(TextView(requireContext()).apply {
                text = station
                textSize = 14f
                setTextColor(Color.parseColor(AppColors.GRAY_MEDIUM))
            })

            addView(TextView(requireContext()).apply {
                text = "⏰ $time"
                textSize = 14f
                setTextColor(Color.parseColor(AppColors.BLUE))
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 10, 0, 0)
                layoutParams = params
            })
        }
    }
}