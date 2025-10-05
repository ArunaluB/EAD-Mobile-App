package edu.sliit.operator.models

// Booking Data for QR Code
data class BookingQRData(
    val bookingId: String,
    val stationId: String,
    val stationName: String,
    val slotNumber: Int,
    val customerName: String,
    val reservationTime: String,
    val timestamp: Long
) {
    // Convert to JSON string for QR code
    fun toQRString(): String {
        return """
            {
                "bookingId": "$bookingId",
                "stationId": "$stationId",
                "stationName": "$stationName",
                "slotNumber": $slotNumber,
                "customerName": "$customerName",
                "reservationTime": "$reservationTime",
                "timestamp": $timestamp
            }
        """.trimIndent()
    }
}