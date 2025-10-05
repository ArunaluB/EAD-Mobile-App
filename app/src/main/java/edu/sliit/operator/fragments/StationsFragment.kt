package edu.sliit.operator.fragments

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import edu.sliit.operator.models.Slot
import edu.sliit.operator.models.SlotStatus
import edu.sliit.operator.models.Station
import edu.sliit.operator.models.BookingQRData
import edu.sliit.operator.utils.AppColors
import edu.sliit.operator.utils.QRCodeGenerator
import java.text.SimpleDateFormat
import java.util.*

class StationsFragment : Fragment() {

    private lateinit var stationSpinner: Spinner
    private lateinit var slotsGridContainer: LinearLayout
    private lateinit var stationInfoText: TextView
    private lateinit var legendContainer: LinearLayout

    private var stations = mutableListOf<Station>()
    private var currentStation: Station? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loadDemoStations()
        return createStationView()
    }

    private fun createStationView(): View {
        val scrollView = ScrollView(requireContext()).apply {
            setBackgroundColor(Color.parseColor(AppColors.BLACK_PRIMARY))
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val mainLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }

        mainLayout.addView(createHeader())
        mainLayout.addView(createStationSelector())
        mainLayout.addView(createStationInfo())
        mainLayout.addView(createLegend())

        slotsGridContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 30, 0, 0)
            layoutParams = params
        }
        mainLayout.addView(slotsGridContainer)

        if (stations.isNotEmpty()) {
            currentStation = stations[0]
            updateSlotsGrid()
        }

        scrollView.addView(mainLayout)
        return scrollView
    }

    private fun createHeader(): TextView {
        return TextView(requireContext()).apply {
            text = "🏢 Stations & Slots"
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

    private fun createStationSelector(): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor(AppColors.GRAY_DARK_PANEL))
            setPadding(30, 30, 30, 30)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 20)
            layoutParams = params

            addView(TextView(requireContext()).apply {
                text = "Select Station"
                textSize = 16f
                setTextColor(Color.parseColor(AppColors.WHITE))
                setPadding(0, 0, 0, 15)
            })

            stationSpinner = Spinner(requireContext()).apply {
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    stations.map { it.name }
                )
                this.adapter = adapter
                setBackgroundColor(Color.parseColor(AppColors.BLACK_OVERLAY))
                setPadding(20, 20, 20, 20)

                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        currentStation = stations[position]
                        updateStationInfo()
                        updateSlotsGrid()
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
            addView(stationSpinner)
        }
    }

    private fun createStationInfo(): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor(AppColors.BLACK_OVERLAY))
            setPadding(30, 30, 30, 30)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 20)
            layoutParams = params

            stationInfoText = TextView(requireContext()).apply {
                textSize = 14f
                setTextColor(Color.parseColor(AppColors.WHITE))
            }
            addView(stationInfoText)
        }
    }

    private fun createLegend(): LinearLayout {
        legendContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor(AppColors.GRAY_DARK_PANEL))
            setPadding(30, 30, 30, 30)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 20)
            layoutParams = params

            addView(TextView(requireContext()).apply {
                text = "Slot Status Legend"
                textSize = 16f
                setTextColor(Color.parseColor(AppColors.WHITE))
                setPadding(0, 0, 0, 15)
            })

            addView(createLegendItem("🟢", "Available - Tap to book", AppColors.GREEN_BRIGHT))
            addView(createLegendItem("🟡", "Booked - View QR Code", "#FFA500"))
            addView(createLegendItem("🔴", "Occupied - In use", AppColors.PINK))
        }
        return legendContainer
    }

    private fun createLegendItem(icon: String, label: String, color: String): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 10, 0, 10)

            addView(TextView(requireContext()).apply {
                text = icon
                textSize = 20f
                setPadding(0, 0, 20, 0)
            })

            addView(TextView(requireContext()).apply {
                text = label
                textSize = 14f
                setTextColor(Color.parseColor(color))
            })
        }
    }

    private fun updateStationInfo() {
        currentStation?.let { station ->
            val available = station.slots.count { it.status == SlotStatus.AVAILABLE }
            val booked = station.slots.count { it.status == SlotStatus.BOOKED }
            val occupied = station.slots.count { it.status == SlotStatus.OCCUPIED }

            stationInfoText.text = """
                📍 Location: ${station.location}
                ⚡ Type: ${station.type} Charging
                📊 Total Slots: ${station.totalSlots}
                🟢 Available: $available | 🟡 Booked: $booked | 🔴 Occupied: $occupied
            """.trimIndent()
        }
    }

    private fun updateSlotsGrid() {
        slotsGridContainer.removeAllViews()

        currentStation?.let { station ->
            updateStationInfo()

            val slotsPerRow = 4
            var currentRow: LinearLayout? = null

            station.slots.forEachIndexed { index, slot ->
                if (index % slotsPerRow == 0) {
                    currentRow = LinearLayout(requireContext()).apply {
                        orientation = LinearLayout.HORIZONTAL
                        gravity = Gravity.CENTER
                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.setMargins(0, 0, 0, 20)
                        layoutParams = params
                        slotsGridContainer.addView(this)
                    }
                }
                currentRow?.addView(createSlotView(slot))
            }
        }
    }

    private fun createSlotView(slot: Slot): LinearLayout {
        val slotColor = when (slot.status) {
            SlotStatus.AVAILABLE -> AppColors.GREEN_BRIGHT
            SlotStatus.BOOKED -> "#FFA500"
            SlotStatus.OCCUPIED -> AppColors.PINK
        }

        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor(slotColor))
            val params = LinearLayout.LayoutParams(0, 180, 1f)
            params.setMargins(10, 10, 10, 10)
            layoutParams = params
            setPadding(15, 15, 15, 15)
            isClickable = true
            isFocusable = true

            addView(TextView(requireContext()).apply {
                text = slot.slotNumber.toString()
                textSize = 24f
                setTextColor(Color.parseColor(AppColors.BLACK_PRIMARY))
                gravity = Gravity.CENTER
            })

            addView(TextView(requireContext()).apply {
                text = when (slot.status) {
                    SlotStatus.AVAILABLE -> "✓"
                    SlotStatus.BOOKED -> "📅"
                    SlotStatus.OCCUPIED -> "⚡"
                }
                textSize = 20f
                gravity = Gravity.CENTER
                setPadding(0, 5, 0, 0)
            })

            setOnClickListener { handleSlotClick(slot) }
        }
    }

    private fun handleSlotClick(slot: Slot) {
        when (slot.status) {
            SlotStatus.AVAILABLE -> showBookingDialog(slot)
            SlotStatus.BOOKED -> showSlotDetailsWithQR(slot)
            SlotStatus.OCCUPIED -> showSlotDetails(slot, "Occupied Slot")
        }
    }

    private fun showBookingDialog(slot: Slot) {
        val dialogView = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)

            addView(TextView(requireContext()).apply {
                text = "Quick Book Slot ${slot.slotNumber}"
                textSize = 20f
                setTextColor(Color.parseColor(AppColors.GREEN_NEON))
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, 30)
            })

            addView(TextView(requireContext()).apply {
                text = """
                    Station: ${currentStation?.name}
                    Slot: #${slot.slotNumber}
                    Type: ${currentStation?.type} Charging
                    
                    Do you want to book this slot?
                """.trimIndent()
                textSize = 16f
                setPadding(0, 10, 0, 20)
            })
        }

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Book Now") { dialog, _ ->
                bookSlotAndGenerateQR(slot)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    // ==================== NEW: Book Slot and Generate QR Code ====================
    private fun bookSlotAndGenerateQR(slot: Slot) {
        currentStation?.let { station ->
            // Generate booking ID
            val bookingId = "BK-${station.id}-${slot.slotNumber}-${System.currentTimeMillis()}"

            // Get current time
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val reservationTime = dateFormat.format(Date())

            // Create QR data
            val qrData = BookingQRData(
                bookingId = bookingId,
                stationId = station.id,
                stationName = station.name,
                slotNumber = slot.slotNumber,
                customerName = "Walk-in Customer",
                reservationTime = reservationTime,
                timestamp = System.currentTimeMillis()
            )

            // Generate QR code string
            val qrCodeString = qrData.toQRString()

            // Update slot with booking info
            val updatedSlots = station.slots.map {
                if (it.slotId == slot.slotId) {
                    it.copy(
                        status = SlotStatus.BOOKED,
                        customerName = "Walk-in Customer",
                        reservationTime = reservationTime,
                        bookingId = bookingId,
                        qrCodeData = qrCodeString  // ← Store QR data
                    )
                } else {
                    it
                }
            }

            currentStation = station.copy(slots = updatedSlots)
            updateStationsData()
            updateSlotsGrid()

            // Show QR code dialog
            showQRCodeDialog(qrData, qrCodeString)
        }
    }

    // ==================== NEW: Show QR Code Dialog ====================
    private fun showQRCodeDialog(qrData: BookingQRData, qrCodeString: String) {
        val dialogView = ScrollView(requireContext()).apply {
            setPadding(40, 40, 40, 40)
        }

        val contentLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER

            // Success header
            addView(TextView(requireContext()).apply {
                text = "✅ Booking Successful!"
                textSize = 24f
                setTextColor(Color.parseColor(AppColors.GREEN_NEON))
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, 30)
            })

            // Booking details
            addView(TextView(requireContext()).apply {
                text = """
                    Booking ID: ${qrData.bookingId}
                    Station: ${qrData.stationName}
                    Slot: #${qrData.slotNumber}
                    Time: ${qrData.reservationTime}
                """.trimIndent()
                textSize = 14f
                setTextColor(Color.parseColor(AppColors.WHITE))
                setPadding(20, 0, 20, 30)
            })

            // QR Code title
            addView(TextView(requireContext()).apply {
                text = "QR Code for Check-in"
                textSize = 18f
                setTextColor(Color.parseColor(AppColors.WHITE))
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, 20)
            })

            // Generate and display QR code
            val qrBitmap = QRCodeGenerator.generateQRCode(qrCodeString, 400, 400)
            if (qrBitmap != null) {
                addView(ImageView(requireContext()).apply {
                    setImageBitmap(qrBitmap)
                    val params = LinearLayout.LayoutParams(400, 400)
                    params.gravity = Gravity.CENTER
                    layoutParams = params
                    setPadding(20, 20, 20, 20)
                    setBackgroundColor(Color.WHITE)
                })
            }

            // Instructions
            addView(TextView(requireContext()).apply {
                text = """
                    
                    📱 Save or screenshot this QR code
                    🔍 Show it at the station for check-in
                    ⚡ Start charging your vehicle
                """.trimIndent()
                textSize = 14f
                setTextColor(Color.parseColor(AppColors.GRAY_MEDIUM))
                gravity = Gravity.CENTER
                setPadding(20, 30, 20, 0)
            })
        }

        dialogView.addView(contentLayout)

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Done") { dialog, _ ->
                Toast.makeText(
                    requireContext(),
                    "Slot booked! Show QR code at station.",
                    Toast.LENGTH_LONG
                ).show()
                dialog.dismiss()
            }
            .setNeutralButton("View Details") { dialog, _ ->
                // Find the booked slot and show details
                currentStation?.slots?.find { it.bookingId == qrData.bookingId }?.let { bookedSlot ->
                    showSlotDetailsWithQR(bookedSlot)
                }
                dialog.dismiss()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    // ==================== NEW: Show Slot Details with QR Code ====================
    private fun showSlotDetailsWithQR(slot: Slot) {
        val dialogView = ScrollView(requireContext()).apply {
            setPadding(40, 40, 40, 40)
        }

        val contentLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER

            addView(TextView(requireContext()).apply {
                text = "Booked Slot Details"
                textSize = 20f
                setTextColor(Color.parseColor("#FFA500"))
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, 30)
            })

            addView(TextView(requireContext()).apply {
                text = """
                    Booking ID: ${slot.bookingId}
                    Slot: #${slot.slotNumber}
                    Customer: ${slot.customerName}
                    Reserved: ${slot.reservationTime}
                    Station: ${currentStation?.name}
                """.trimIndent()
                textSize = 16f
                setTextColor(Color.parseColor(AppColors.WHITE))
                setPadding(20, 0, 20, 30)
            })

            // Show QR code if available
            slot.qrCodeData?.let { qrData ->
                addView(TextView(requireContext()).apply {
                    text = "QR Code for Check-in"
                    textSize = 18f
                    setTextColor(Color.parseColor(AppColors.WHITE))
                    gravity = Gravity.CENTER
                    setPadding(0, 0, 0, 20)
                })

                val qrBitmap = QRCodeGenerator.generateQRCode(qrData, 400, 400)
                if (qrBitmap != null) {
                    addView(ImageView(requireContext()).apply {
                        setImageBitmap(qrBitmap)
                        val params = LinearLayout.LayoutParams(400, 400)
                        params.gravity = Gravity.CENTER
                        layoutParams = params
                        setPadding(20, 20, 20, 20)
                        setBackgroundColor(Color.WHITE)
                    })
                }
            }
        }

        dialogView.addView(contentLayout)

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun showSlotDetails(slot: Slot, title: String) {
        val dialogView = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)

            addView(TextView(requireContext()).apply {
                text = title
                textSize = 20f
                setTextColor(Color.parseColor(AppColors.PINK))
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, 30)
            })

            addView(TextView(requireContext()).apply {
                text = """
                    Slot: #${slot.slotNumber}
                    Customer: ${slot.customerName ?: "N/A"}
                    Vehicle: ${slot.vehicleNumber ?: "N/A"}
                    Time: ${slot.reservationTime ?: "N/A"}
                """.trimIndent()
                textSize = 16f
                setPadding(0, 10, 0, 10)
            })
        }

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun updateStationsData() {
        currentStation?.let { updated ->
            val index = stations.indexOfFirst { it.id == updated.id }
            if (index != -1) {
                stations[index] = updated
            }
        }
    }

    private fun loadDemoStations() {
        stations = mutableListOf(
            Station(
                id = "ST001",
                name = "Station A - Main Campus",
                location = "Building A, Ground Floor",
                type = "AC",
                totalSlots = 12,
                slots = createDemoSlots(12, "ST001")
            ),
            Station(
                id = "ST002",
                name = "Station B - Parking Lot",
                location = "Outdoor Parking Area",
                type = "DC",
                totalSlots = 8,
                slots = createDemoSlots(8, "ST002")
            ),
            Station(
                id = "ST003",
                name = "Station C - East Wing",
                location = "Building C, Basement",
                type = "AC",
                totalSlots = 16,
                slots = createDemoSlots(16, "ST003")
            )
        )
    }

    private fun createDemoSlots(count: Int, stationId: String): List<Slot> {
        return (1..count).map { num ->
            val status = when {
                num % 5 == 0 -> SlotStatus.OCCUPIED
                num % 3 == 0 -> SlotStatus.BOOKED
                else -> SlotStatus.AVAILABLE
            }

            Slot(
                slotId = "${stationId}-S${num.toString().padStart(3, '0')}",
                slotNumber = num,
                status = status,
                customerName = if (status != SlotStatus.AVAILABLE) "Customer $num" else null,
                customerNIC = if (status != SlotStatus.AVAILABLE) "NIC${num}001" else null,
                bookingId = if (status != SlotStatus.AVAILABLE) "BK-${stationId}-$num" else null,
                reservationTime = if (status != SlotStatus.AVAILABLE) "10:${num.toString().padStart(2, '0')} AM" else null,
                vehicleNumber = if (status == SlotStatus.OCCUPIED) "CAR-${num}23" else null,
                qrCodeData = if (status == SlotStatus.BOOKED) "Demo QR for slot $num" else null
            )
        }
    }
}
