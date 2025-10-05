package edu.sliit.operator.models

data class Slot(
    val slotId: String,
    val slotNumber: Int,
    val status: SlotStatus,
    val customerName: String? = null,
    val customerNIC: String? = null,
    val bookingId: String? = null,
    val reservationTime: String? = null,
    val vehicleNumber: String? = null,
    val qrCodeData: String? = null  // ← NEW: QR code data
)