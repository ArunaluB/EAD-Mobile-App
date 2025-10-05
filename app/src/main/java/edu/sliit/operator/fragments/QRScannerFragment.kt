package edu.sliit.operator.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import edu.sliit.operator.models.ScannedBooking
import edu.sliit.operator.utils.AppColors
import org.json.JSONObject

class QRScannerFragment : Fragment() {

    private var barcodeView: DecoratedBarcodeView? = null
    private var hasCameraPermission = false
    private var isScanning = false
    private lateinit var scannerContainer: FrameLayout
    private lateinit var resultContainer: LinearLayout
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var testButton: Button
    private lateinit var statusText: TextView
    private var currentBooking: ScannedBooking? = null
    private var lastScanTime = 0L
    private var scanCount = 0

    companion object {
        private const val TAG = "QRScannerFragment"
        private const val SCAN_DELAY_MS = 500L // Reduced from 2000ms for faster scanning
        private const val RADIO_BOOKED = 1
        private const val RADIO_CHECKED_IN = 2
        private const val RADIO_CHARGING = 3
        private const val RADIO_COMPLETED = 4
        private const val RADIO_CANCELLED = 5
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) {
            updateStatus("✅ Camera permission granted", AppColors.GREEN_NEON)
            startScanning()
        } else {
            updateStatus("❌ Camera permission denied", AppColors.PINK)
            showPermissionDeniedMessage()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return createModernScannerView()
    }

    private fun createModernScannerView(): View {
        val scrollView = ScrollView(requireContext()).apply {
            setBackgroundColor(Color.parseColor("#0A0E27")) // Deep navy background
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val mainLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }

        // Modern Header with gradient background
        mainLayout.addView(createModernHeader())

        // Instructions Card
        mainLayout.addView(createModernInstructions())

        // Status Indicator
        statusText = TextView(requireContext()).apply {
            text = "📱 Ready to scan QR codes"
            textSize = 16f
            setTextColor(Color.parseColor("#00FF88"))
            gravity = Gravity.CENTER
            setPadding(28, 24, 28, 24)
            setBackgroundColor(Color.parseColor("#0F1624"))
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 20, 0, 20)
            layoutParams = params
            // Add glow effect
            setShadowLayer(15f, 0f, 0f, Color.parseColor("#00FF88"))
        }
        mainLayout.addView(statusText)

        // Scanner Container with rounded corners
        scannerContainer = FrameLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1000
            ).apply {
                setMargins(0, 0, 0, 24)
            }
            setBackgroundColor(Color.parseColor("#000000"))
            visibility = View.GONE
        }
        mainLayout.addView(scannerContainer)

        // Result Container
        resultContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            visibility = View.GONE
        }
        mainLayout.addView(resultContainer)

        // Action Buttons
        val buttonLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams = params
        }

        startButton = createModernButton("START SCANNING", AppColors.GREEN_NEON, true)
        startButton.setOnClickListener { checkPermissionAndScan() }
        buttonLayout.addView(startButton)

        stopButton = createModernButton("STOP SCANNING", AppColors.PINK, false)
        stopButton.setOnClickListener { stopScanning() }
        stopButton.visibility = View.GONE
        buttonLayout.addView(stopButton)

        mainLayout.addView(buttonLayout)

        // Test Button
        testButton = Button(requireContext()).apply {
            text = "🧪 TEST MODE - Preview Sample Booking"
            textSize = 15f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#FFFFFF"))
            setBackgroundColor(Color.parseColor("#7B2CBF"))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.height = 130
            params.setMargins(0, 24, 0, 0)
            layoutParams = params
            setShadowLayer(15f, 0f, 4f, Color.parseColor("#7B2CBF"))
            setOnClickListener { testWithSampleData() }
        }
        mainLayout.addView(testButton)

        scrollView.addView(mainLayout)
        return scrollView
    }

    private fun createModernHeader(): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            // Gradient-like effect with layered colors
            setBackgroundColor(Color.parseColor("#1A1F3A"))
            setPadding(32, 40, 32, 40)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 24)
            layoutParams = params

            // Animated camera icon with glow effect
            addView(TextView(requireContext()).apply {
                text = "�"
                textSize = 64f
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, 16)
                // Add shadow effect
                setShadowLayer(20f, 0f, 0f, Color.parseColor("#00D9FF"))
            })

            addView(TextView(requireContext()).apply {
                text = "QR Scanner Pro"
                textSize = 32f
                setTextColor(Color.parseColor("#FFFFFF"))
                gravity = Gravity.CENTER
                setShadowLayer(10f, 0f, 2f, Color.parseColor("#000000"))
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            })

            addView(TextView(requireContext()).apply {
                text = "Instant Booking Recognition"
                textSize = 15f
                setTextColor(Color.parseColor("#00D9FF"))
                gravity = Gravity.CENTER
                setPadding(0, 12, 0, 0)
            })

            addView(TextView(requireContext()).apply {
                text = "━━━━━━━━━━━━"
                textSize = 12f
                setTextColor(Color.parseColor("#00D9FF"))
                gravity = Gravity.CENTER
                setPadding(0, 16, 0, 0)
            })
        }
    }

    private fun createModernInstructions(): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#16213E"))
            setPadding(28, 28, 28, 28)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 20)
            layoutParams = params

            addView(TextView(requireContext()).apply {
                text = "✨ Quick Start Guide"
                textSize = 20f
                setTextColor(Color.parseColor("#00D9FF"))
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setPadding(0, 0, 0, 20)
            })

            val instructions = listOf(
                Pair("🚀", "Tap the glowing START button below"),
                Pair("🎯", "Aim camera at QR code center"),
                Pair("⚡", "Auto-detect in 0.5 seconds"),
                Pair("✅", "View instant booking details"),
                Pair("🧪", "Try TEST mode to preview")
            )

            instructions.forEach { (icon, text) ->
                addView(LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(0, 8, 0, 8)

                    addView(TextView(requireContext()).apply {
                        this.text = icon
                        textSize = 20f
                        setPadding(0, 0, 16, 0)
                        gravity = Gravity.CENTER_VERTICAL
                    })

                    addView(TextView(requireContext()).apply {
                        this.text = text
                        textSize = 15f
                        setTextColor(Color.parseColor("#E0E0E0"))
                        gravity = Gravity.CENTER_VERTICAL
                    })
                })
            }
        }
    }

    private fun createModernButton(label: String, color: String, isStart: Boolean): Button {
        return Button(requireContext()).apply {
            text = label
            textSize = 17f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor(if (isStart) "#0A0E27" else "#FFFFFF"))
            setBackgroundColor(Color.parseColor(if (isStart) "#00FF88" else "#FF3366"))
            val params = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            params.height = 150
            params.setMargins(if (isStart) 0 else 12, 0, if (isStart) 12 else 0, 0)
            layoutParams = params
            // Add shadow/glow effect
            setShadowLayer(20f, 0f, 4f, Color.parseColor(if (isStart) "#00FF88" else "#FF3366"))
        }
    }

    private fun updateStatus(message: String, color: String = AppColors.GREEN_NEON) {
        activity?.runOnUiThread {
            statusText.text = message
            val displayColor = when (color) {
                AppColors.GREEN_NEON, AppColors.GREEN_BRIGHT -> "#00FF88"
                AppColors.PINK -> "#FF3366"
                AppColors.BLUE -> "#00D9FF"
                AppColors.GRAY_MEDIUM -> "#8B9DC3"
                else -> color
            }
            statusText.setTextColor(Color.parseColor(displayColor))
            statusText.setShadowLayer(15f, 0f, 0f, Color.parseColor(displayColor))
            Log.d(TAG, "STATUS: $message")
        }
    }

    private fun checkPermissionAndScan() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                hasCameraPermission = true
                startScanning()
            }
            else -> {
                updateStatus("⏳ Requesting camera permission...", AppColors.BLUE)
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun startScanning() {
        try {
            Log.d(TAG, "========== STARTING SCANNER ==========")

            scannerContainer.visibility = View.VISIBLE
            resultContainer.visibility = View.GONE
            startButton.visibility = View.GONE
            stopButton.visibility = View.VISIBLE
            scanCount = 0

            updateStatus("📷 Initializing camera...", AppColors.BLUE)

            // Use DecoratedBarcodeView for better UI
            barcodeView = DecoratedBarcodeView(requireContext()).apply {
                // Configure for QR codes only
                val formats = listOf(BarcodeFormat.QR_CODE)
                barcodeView.decoderFactory = DefaultDecoderFactory(formats)

                // Set viewfinder for better scanning
                statusView.text = "Point camera at QR code"
                statusView.visibility = View.VISIBLE

                Log.d(TAG, "✅ Scanner configured for QR_CODE only")

                // Decode callback - IMPORTANT: Use barcodeView.decodeContinuous
                barcodeView.decodeContinuous(object : BarcodeCallback {
                    override fun barcodeResult(result: BarcodeResult?) {
                        scanCount++
                        Log.d(TAG, "🔍 Scan attempt #$scanCount")

                        if (result != null && result.text != null && result.text.isNotEmpty()) {
                            val currentTime = System.currentTimeMillis()

                            // Reduce delay for faster scanning
                            if (currentTime - lastScanTime > 500L) {
                                lastScanTime = currentTime

                                Log.d(TAG, "✅✅✅ QR CODE DETECTED! ✅✅✅")
                                Log.d(TAG, "Content: ${result.text}")

                                updateStatus("✅ QR Code found! Processing...", AppColors.GREEN_BRIGHT)
                                handleScanResult(result.text)
                            } else {
                                Log.d(TAG, "⏱️ Scan throttled (too soon)")
                            }
                        } else {
                            Log.d(TAG, "⚠️ Empty or null result")
                        }
                    }

                    override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
                        if (resultPoints != null && resultPoints.isNotEmpty()) {
                            updateStatus("👁️ Detecting QR code... (${resultPoints.size} markers)", AppColors.BLUE)
                            Log.d(TAG, "👁️ Detecting: ${resultPoints.size} points found")
                        }
                    }
                })

                Log.d(TAG, "📷 Starting camera preview")
                resume()
            }

            scannerContainer.removeAllViews()
            scannerContainer.addView(barcodeView, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            ))

            isScanning = true
            updateStatus("📷 Camera active - Point at QR code", AppColors.GREEN_NEON)

            Toast.makeText(requireContext(), "Camera ready! Point at QR code", Toast.LENGTH_LONG).show()

            Log.d(TAG, "========== SCANNER READY ==========")

        } catch (e: Exception) {
            Log.e(TAG, "❌❌❌ SCANNER ERROR ❌❌❌", e)
            updateStatus("❌ Camera error: ${e.message}", AppColors.PINK)
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()

            startButton.visibility = View.VISIBLE
            stopButton.visibility = View.GONE
            scannerContainer.visibility = View.GONE
        }
    }

    private fun stopScanning() {
        try {
            Log.d(TAG, "Stopping scanner")

            barcodeView?.pause()
            barcodeView = null
            scannerContainer.removeAllViews()
            scannerContainer.visibility = View.GONE
            isScanning = false

            startButton.visibility = View.VISIBLE
            stopButton.visibility = View.GONE

            updateStatus("⏸️ Scanner stopped - Ready to scan again", AppColors.GRAY_MEDIUM)
            Toast.makeText(requireContext(), "Scanner stopped", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping scanner", e)
        }
    }

    private fun testWithSampleData() {
        val sampleQR = """{
            "bookingId": "BK-TEST-12345",
            "stationId": "ST-NORTH-001",
            "stationName": "North City Charging Hub",
            "slotNumber": 7,
            "customerName": "John Doe",
            "reservationTime": "2025-10-04 15:30",
            "timestamp": 1728054600000,
            "currentStatus": "BOOKED"
        }"""

        Log.d(TAG, "🧪 Testing with sample booking data")
        updateStatus("🧪 Loading test data...", AppColors.BLUE)
        handleScanResult(sampleQR)
    }

    private fun handleScanResult(qrContent: String) {
        Log.d(TAG, "========== PROCESSING QR CODE ==========")
        Log.d(TAG, "Raw content length: ${qrContent.length} chars")

        activity?.runOnUiThread {
            if (isScanning) {
                stopScanning()
            }

            try {
                val booking = parseBookingQR(qrContent)
                Log.d(TAG, "✅ Successfully parsed booking")
                Log.d(TAG, "Booking ID: ${booking.bookingId}")
                Log.d(TAG, "Customer: ${booking.customerName}")

                updateStatus("✅ Success! Showing booking details...", AppColors.GREEN_BRIGHT)
                showBookingDetails(booking)

            } catch (e: Exception) {
                Log.e(TAG, "❌ Parse error: ${e.message}", e)
                updateStatus("❌ Invalid QR code format", AppColors.PINK)

                AlertDialog.Builder(requireContext())
                    .setTitle("❌ Invalid QR Code")
                    .setMessage("The scanned QR code is not a valid booking code.\n\nError: ${e.message}")
                    .setPositiveButton("Scan Again") { dialog, _ ->
                        startScanning()
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        startButton.visibility = View.VISIBLE
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    private fun parseBookingQR(qrContent: String): ScannedBooking {
        val json = JSONObject(qrContent.trim())

        return ScannedBooking(
            bookingId = json.getString("bookingId"),
            stationId = json.getString("stationId"),
            stationName = json.getString("stationName"),
            slotNumber = json.getInt("slotNumber"),
            customerName = json.getString("customerName"),
            reservationTime = json.getString("reservationTime"),
            timestamp = json.getLong("timestamp"),
            currentStatus = json.optString("currentStatus", "BOOKED")
        )
    }

    private fun showBookingDetails(booking: ScannedBooking) {
        currentBooking = booking
        resultContainer.removeAllViews()
        resultContainer.visibility = View.VISIBLE
        updateStatus("✅ Booking loaded successfully", AppColors.GREEN_BRIGHT)

        // Success banner
        resultContainer.addView(LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#00D9FF"))
            setPadding(36, 36, 36, 36)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 24)
            layoutParams = params

            addView(TextView(requireContext()).apply {
                text = "✨"
                textSize = 56f
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, 12)
            })

            addView(TextView(requireContext()).apply {
                text = "SUCCESS!"
                textSize = 28f
                setTextColor(Color.parseColor("#0A0E27"))
                gravity = Gravity.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setShadowLayer(5f, 0f, 2f, Color.parseColor("#FFFFFF"))
            })

            addView(TextView(requireContext()).apply {
                text = "QR Code Scanned Successfully"
                textSize = 16f
                setTextColor(Color.parseColor("#0F1624"))
                gravity = Gravity.CENTER
                setPadding(0, 8, 0, 0)
            })
        })

        resultContainer.addView(createModernBookingCard(booking))
        resultContainer.addView(createModernStatusSection(booking))
        resultContainer.addView(createModernActionButtons(booking))
    }

    private fun createModernBookingCard(booking: ScannedBooking): LinearLayout {
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
                text = "📋 Booking Details"
                textSize = 22f
                setTextColor(Color.parseColor("#00D9FF"))
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setPadding(0, 0, 0, 24)
                setShadowLayer(10f, 0f, 2f, Color.parseColor("#00D9FF"))
            })

            addView(createModernDetailRow("🆔 Booking ID", booking.bookingId))
            addView(createDivider())
            addView(createModernDetailRow("🏢 Charging Station", booking.stationName))
            addView(createDivider())
            addView(createModernDetailRow("📍 Slot Number", "#${booking.slotNumber}"))
            addView(createDivider())
            addView(createModernDetailRow("👤 Customer Name", booking.customerName))
            addView(createDivider())
            addView(createModernDetailRow("⏰ Reserved Time", booking.reservationTime))
            addView(createDivider())
            addView(createModernDetailRow("📊 Current Status", booking.currentStatus, "#00FF88"))
        }
    }

    private fun createDivider(): View {
        return View(requireContext()).apply {
            setBackgroundColor(Color.parseColor("#2A3555"))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                2
            )
            params.setMargins(0, 16, 0, 16)
            layoutParams = params
        }
    }

    private fun createModernDetailRow(label: String, value: String, valueColor: String = "#FFFFFF"): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 8, 0, 8)

            addView(TextView(requireContext()).apply {
                text = label
                textSize = 14f
                setTextColor(Color.parseColor("#8B9DC3"))
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            })

            addView(TextView(requireContext()).apply {
                text = value
                textSize = 18f
                setTextColor(Color.parseColor(valueColor))
                setPadding(0, 8, 0, 0)
            })
        }
    }

    private fun createModernStatusSection(booking: ScannedBooking): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#16213E"))
            setPadding(32, 32, 32, 32)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 24)
            layoutParams = params

            addView(TextView(requireContext()).apply {
                text = "🔄 Update Booking Status"
                textSize = 20f
                setTextColor(Color.parseColor("#00D9FF"))
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setPadding(0, 0, 0, 24)
            })

            val statusGroup = RadioGroup(requireContext()).apply {
                orientation = RadioGroup.VERTICAL
                setPadding(0, 12, 0, 12)
            }

            val statuses = listOf(
                Triple(RADIO_BOOKED, "🟡 BOOKED", "BOOKED"),
                Triple(RADIO_CHECKED_IN, "🟢 CHECKED IN", "CHECKED_IN"),
                Triple(RADIO_CHARGING, "⚡ CHARGING", "CHARGING"),
                Triple(RADIO_COMPLETED, "✅ COMPLETED", "COMPLETED"),
                Triple(RADIO_CANCELLED, "❌ CANCELLED", "CANCELLED")
            )

            statuses.forEach { (id, text, status) ->
                statusGroup.addView(RadioButton(requireContext()).apply {
                    this.text = text
                    textSize = 17f
                    setTextColor(Color.parseColor("#FFFFFF"))
                    isChecked = booking.currentStatus == status
                    this.id = id
                    setPadding(16, 14, 16, 14)
                    
                    // Highlight checked item
                    if (isChecked) {
                        setBackgroundColor(Color.parseColor("#2A3555"))
                    }
                })
            }

            statusGroup.setOnCheckedChangeListener { _, checkedId ->
                booking.currentStatus = when (checkedId) {
                    RADIO_BOOKED -> "BOOKED"
                    RADIO_CHECKED_IN -> "CHECKED_IN"
                    RADIO_CHARGING -> "CHARGING"
                    RADIO_COMPLETED -> "COMPLETED"
                    RADIO_CANCELLED -> "CANCELLED"
                    else -> "BOOKED"
                }
                
                // Update background for selected item
                for (i in 0 until statusGroup.childCount) {
                    val radio = statusGroup.getChildAt(i) as RadioButton
                    if (radio.id == checkedId) {
                        radio.setBackgroundColor(Color.parseColor("#2A3555"))
                    } else {
                        radio.setBackgroundColor(Color.TRANSPARENT)
                    }
                }
                
                Log.d(TAG, "Status changed to: ${booking.currentStatus}")
            }

            addView(statusGroup)
        }
    }

    private fun createModernActionButtons(booking: ScannedBooking): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 24, 0, 0)
            layoutParams = params

            addView(Button(requireContext()).apply {
                text = "✅ CONFIRM UPDATE"
                textSize = 16f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setTextColor(Color.parseColor("#0A0E27"))
                setBackgroundColor(Color.parseColor("#00FF88"))
                val params = LinearLayout.LayoutParams(0, 150, 1f)
                params.setMargins(0, 0, 12, 0)
                layoutParams = params
                setShadowLayer(20f, 0f, 4f, Color.parseColor("#00FF88"))
                setOnClickListener { confirmStatusChange(booking) }
            })

            addView(Button(requireContext()).apply {
                text = "� SCAN NEXT"
                textSize = 16f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setTextColor(Color.parseColor("#FFFFFF"))
                setBackgroundColor(Color.parseColor("#00D9FF"))
                val params = LinearLayout.LayoutParams(0, 150, 1f)
                params.setMargins(12, 0, 0, 0)
                layoutParams = params
                setShadowLayer(20f, 0f, 4f, Color.parseColor("#00D9FF"))
                setOnClickListener {
                    resultContainer.visibility = View.GONE
                    startScanning()
                }
            })
        }
    }

    private fun confirmStatusChange(booking: ScannedBooking) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Status Update")
            .setMessage("Update booking status to:\n\n${booking.currentStatus}")
            .setPositiveButton("Confirm") { dialog, _ ->
                updateBookingStatus(booking)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun updateBookingStatus(booking: ScannedBooking) {
        // TODO: Add your API call here to update status in backend

        AlertDialog.Builder(requireContext())
            .setTitle("✅ Status Updated!")
            .setMessage("Booking: ${booking.bookingId}\nNew Status: ${booking.currentStatus}")
            .setPositiveButton("Done") { dialog, _ ->
                resultContainer.visibility = View.GONE
                startButton.visibility = View.VISIBLE
                updateStatus("✅ Ready for next scan", AppColors.GREEN_NEON)
                dialog.dismiss()
            }
            .setNeutralButton("Scan More") { dialog, _ ->
                resultContainer.visibility = View.GONE
                startScanning()
                dialog.dismiss()
            }
            .show()

        Log.d(TAG, "✅ Status updated: ${booking.bookingId} -> ${booking.currentStatus}")
    }

    private fun showPermissionDeniedMessage() {
        AlertDialog.Builder(requireContext())
            .setTitle("Camera Permission Required")
            .setMessage("Please enable camera permission in your device settings to scan QR codes.")
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume - Resuming scanner if active")
        if (isScanning) {
            barcodeView?.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause - Pausing scanner")
        barcodeView?.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView - Cleaning up scanner")
        barcodeView?.pause()
        barcodeView = null
    }
}