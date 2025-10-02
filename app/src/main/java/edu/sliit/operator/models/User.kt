package edu.sliit.operator.models



data class User(
    val id: Int = 0,
    val username: String,
    val password: String,
    val role: String = "OPERATOR",
    val stationAssignments: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val lastSync: Long? = null
)