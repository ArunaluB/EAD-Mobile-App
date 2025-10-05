package edu.sliit.operator.models

// Slot Status Enum
enum class SlotStatus {
    AVAILABLE,   // Green - Can book
    BOOKED,      // Market color (Orange) - Currently booked
    OCCUPIED     // Red - In use/charging
}