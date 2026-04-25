package com.example.dsmforocompose.data

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppRepository(private val context: Context) {

    private val usersFile = File(context.filesDir, "users.txt")
    private val gradesFile = File(context.filesDir, "grades.txt")
    private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun ensureSeedData() {
        if (!usersFile.exists()) {
            usersFile.parentFile?.mkdirs()
            usersFile.writeText("admin@demo.com|123456\n")
        }

        if (!gradesFile.exists()) {
            gradesFile.writeText("")
        }
    }

    fun registerUser(email: String, password: String): Boolean {
        val normalizedEmail = email.trim().lowercase(Locale.getDefault())

        if (normalizedEmail.isBlank() || password.isBlank()) return false

        val users = loadUsers()
        if (users.any { it.email.equals(normalizedEmail, ignoreCase = true) }) {
            return false
        }

        usersFile.appendText("$normalizedEmail|$password\n")
        return true
    }

    fun validateLogin(email: String, password: String): Boolean {
        val normalizedEmail = email.trim().lowercase(Locale.getDefault())

        return loadUsers().any {
            it.email.equals(normalizedEmail, ignoreCase = true) && it.password == password
        }
    }

    fun loadUsers(): List<UserAccount> {
        if (!usersFile.exists()) return emptyList()

        return usersFile.readLines().mapNotNull { line ->
            val clean = line.trim()
            if (clean.isBlank()) return@mapNotNull null

            val parts = clean.split('|', limit = 2)
            if (parts.size < 2) return@mapNotNull null

            val email = parts[0].trim()
            val password = parts[1]

            if (email.isBlank() || password.isBlank()) null else UserAccount(email, password)
        }
    }

    fun loadUserEmails(): List<String> {
        return loadUsers().map { it.email }
    }

    fun saveGradeRecord(
        userEmail: String,
        subject: String,
        notes: List<Double>,
        weights: List<Double>,
        average: Double,
        passed: Boolean
    ) {
        val timestamp = formatter.format(Date())
        val safeSubject = subject.trim().replace("|", "/")
        val normalizedEmail = userEmail.trim().lowercase(Locale.getDefault())

        val notesAndWeights = notes.zip(weights).joinToString(";") { (note, weight) ->
            "${note},${weight}"
        }

        val line = listOf(
            timestamp,
            normalizedEmail,
            safeSubject,
            notesAndWeights,
            average.toString(),
            if (passed) "APROBADO" else "REPROBADO"
        ).joinToString("|")

        gradesFile.appendText(line + "\n")
    }

    fun loadGradeHistory(): List<GradeRecord> {
        if (!gradesFile.exists()) return emptyList()

        return gradesFile.readLines().mapNotNull { line ->
            val clean = line.trim()
            if (clean.isBlank()) return@mapNotNull null

            val parts = clean.split('|', limit = 6)
            if (parts.size < 6) return@mapNotNull null

            val timestamp = parts[0]
            val userEmail = parts[1]
            val subject = parts[2]
            val notesWeightsPart = parts[3]
            val average = parts[4].toDoubleOrNull() ?: return@mapNotNull null
            val passed = parts[5].contains("APROBADO", ignoreCase = true)

            val pairs = notesWeightsPart.split(";").mapNotNull { pair ->
                val xy = pair.split(',', limit = 2)
                if (xy.size < 2) return@mapNotNull null

                val note = xy[0].toDoubleOrNull() ?: return@mapNotNull null
                val weight = xy[1].toDoubleOrNull() ?: return@mapNotNull null

                note to weight
            }

            if (pairs.size != 5) return@mapNotNull null

            GradeRecord(
                timestamp = timestamp,
                userEmail = userEmail,
                subject = subject,
                notes = pairs.map { it.first },
                weights = pairs.map { it.second },
                average = average,
                passed = passed
            )
        }
    }
}