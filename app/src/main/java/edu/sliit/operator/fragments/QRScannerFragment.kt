package edu.sliit.operator.fragments

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
import edu.sliit.operator.utils.AppColors

class QRScannerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return createScannerView()
    }

    private fun createScannerView(): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor(AppColors.BLACK_PRIMARY))
            setPadding(60, 100, 60, 100)

            addView(TextView(requireContext()).apply {
                text = "📷"
                textSize = 80f
                gravity = Gravity.CENTER
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 40)
                layoutParams = params
            })

            addView(TextView(requireContext()).apply {
                text = "QR Code Scanner"
                textSize = 28f
                setTextColor(Color.parseColor(AppColors.WHITE))
                gravity = Gravity.CENTER
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 20)
                layoutParams = params
            })

            addView(TextView(requireContext()).apply {
                text = "Scan customer QR codes to\ncheck-in or complete bookings"
                textSize = 16f
                setTextColor(Color.parseColor(AppColors.GRAY_MEDIUM))
                gravity = Gravity.CENTER
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 60)
                layoutParams = params
            })

            addView(Button(requireContext()).apply {
                text = "START SCANNING"
                textSize = 18f
                setTextColor(Color.parseColor(AppColors.BLACK_PRIMARY))
                setBackgroundColor(Color.parseColor(AppColors.GREEN_NEON))
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.height = 140
                params.setMargins(0, 0, 0, 20)
                layoutParams = params
            })

            addView(TextView(requireContext()).apply {
                text = "Enter code manually"
                textSize = 14f
                setTextColor(Color.parseColor(AppColors.BLUE))
                gravity = Gravity.CENTER
            })
        }
    }
}