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
            setBackgroundColor(Color.parseColor("#0A0E27")) // Deep navy
        }

        val mainLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }

        // Modern Header
        mainLayout.addView(LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 28)
            layoutParams = params

            addView(TextView(requireContext()).apply {
                text = "📋"
                textSize = 40f
                setPadding(0, 0, 16, 0)
                setShadowLayer(20f, 0f, 0f, Color.parseColor("#00D9FF"))
            })

            addView(TextView(requireContext()).apply {
                text = "Bookings"
                textSize = 32f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setTextColor(Color.parseColor("#FFFFFF"))
                setShadowLayer(10f, 0f, 2f, Color.parseColor("#000000"))
            })
        })

        // Modern Filter Row
        val filterRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 24)
            layoutParams = params
        }

        filterRow.addView(createModernFilterButton("All", true))
        filterRow.addView(createModernFilterButton("Pending", false))
        filterRow.addView(createModernFilterButton("Approved", false))
        mainLayout.addView(filterRow)

        // Booking Cards
        mainLayout.addView(createModernBookingCard("BK-001", "John Doe", "Station A - Slot 1", "Today 10:00 AM", "Approved"))
        mainLayout.addView(createModernBookingCard("BK-002", "Jane Smith", "Station B - Slot 2", "Today 11:30 AM", "Pending"))
        mainLayout.addView(createModernBookingCard("BK-003", "Mike Johnson", "Station A - Slot 3", "Today 01:00 PM", "Approved"))
        mainLayout.addView(createModernBookingCard("BK-004", "Sarah Wilson", "Station C - Slot 5", "Today 02:30 PM", "Pending"))
        mainLayout.addView(createModernBookingCard("BK-005", "David Brown", "Station B - Slot 4", "Today 04:00 PM", "Approved"))

        scrollView.addView(mainLayout)
        return scrollView
    }

    private fun createModernFilterButton(text: String, isSelected: Boolean): Button {
        return Button(requireContext()).apply {
            this.text = text
            textSize = 15f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            val bgColor = if (isSelected) "#00D9FF" else "#16213E"
            val txtColor = if (isSelected) "#0A0E27" else "#FFFFFF"
            setBackgroundColor(Color.parseColor(bgColor))
            setTextColor(Color.parseColor(txtColor))
            val params = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            params.height = 110
            params.setMargins(6, 0, 6, 0)
            layoutParams = params
            if (isSelected) {
                setShadowLayer(15f, 0f, 0f, Color.parseColor("#00D9FF"))
            }
        }
    }

    private fun createModernBookingCard(id: String, name: String, station: String, time: String, status: String): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#1A1F3A"))
            setPadding(28, 28, 28, 28)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 16)
            layoutParams = params

            // Header Row: ID + Status Badge
            val headerRow = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, 0, 0, 16)
            }

            headerRow.addView(TextView(requireContext()).apply {
                text = "🆔 $id"
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

            val statusColor = when (status) {
                "Approved" -> "#00FF88"
                "Pending" -> "#FF3366"
                else -> "#8B9DC3"
            }
            val statusIcon = when (status) {
                "Approved" -> "✅"
                "Pending" -> "⏳"
                else -> "●"
            }
            
            headerRow.addView(LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                setBackgroundColor(Color.parseColor(statusColor))
                setPadding(16, 8, 16, 8)
                gravity = Gravity.CENTER

                addView(TextView(requireContext()).apply {
                    text = "$statusIcon $status"
                    textSize = 13f
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    setTextColor(Color.parseColor("#0A0E27"))
                })
            })

            addView(headerRow)

            // Divider
            addView(View(requireContext()).apply {
                setBackgroundColor(Color.parseColor("#2A3555"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    2
                ).apply {
                    setMargins(0, 0, 0, 16)
                }
            })

            // Customer Name
            addView(TextView(requireContext()).apply {
                text = "👤 $name"
                textSize = 20f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setTextColor(Color.parseColor("#FFFFFF"))
                setPadding(0, 0, 0, 12)
            })

            // Station Info
            addView(TextView(requireContext()).apply {
                text = "🏢 $station"
                textSize = 16f
                setTextColor(Color.parseColor("#8B9DC3"))
                setPadding(0, 0, 0, 12)
            })

            // Time Info
            addView(TextView(requireContext()).apply {
                text = "⏰ $time"
                textSize = 16f
                setTextColor(Color.parseColor("#00D9FF"))
            })
        }
    }
}