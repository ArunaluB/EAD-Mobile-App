package edu.sliit.operator.models

data class ScannedBooking(
    val bookingId: String,
    val stationId: String,
    val stationName: String,
    val slotNumber: Int,
    val customerName: String,
    val reservationTime: String,
    val timestamp: Long,
    var currentStatus: String = "BOOKED"  // Can be changed after scan
)
