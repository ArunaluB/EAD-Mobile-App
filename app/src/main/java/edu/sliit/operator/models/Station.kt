package edu.sliit.operator.models

data class Station(
    val id: String,
    val name: String,
    val location: String,
    val type: String, // "AC" or "DC"
    val totalSlots: Int,
    val slots: List<Slot>
)