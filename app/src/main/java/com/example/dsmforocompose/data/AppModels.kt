package com.example.dsmforocompose.data

data class UserAccount(
    val email: String,
    val password: String
)

data class GradeRecord(
    val timestamp: String,
    val userEmail: String,
    val subject: String,
    val notes: List<Double>,
    val weights: List<Double>,
    val average: Double,
    val passed: Boolean
)