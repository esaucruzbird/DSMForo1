package com.example.dsmforocompose.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dsmforocompose.data.AppRepository
import com.example.dsmforocompose.data.GradeRecord
import java.util.Locale
//import kotlin.math.abs

@Composable
fun HistoryScreen(
    repository: AppRepository,
    email: String,
    onBackToWelcome: () -> Unit,
    onLogout: () -> Unit
) {
    var userEmails by remember { mutableStateOf(listOf<String>()) }
    var records by remember { mutableStateOf(listOf<GradeRecord>()) }

    LaunchedEffect(Unit) {
        userEmails = repository.loadUserEmails()
        records = repository.loadGradeHistory().asReversed()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Historial",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Usuario actual: ${email.substringBefore("@")}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Usuarios registrados",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (userEmails.isEmpty()) {
            Text(text = "No hay usuarios registrados.")
        } else {
            userEmails.forEach { user ->
                Text(text = "• $user")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Historial de notas",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (records.isEmpty()) {
            Text(text = "No hay registros de notas todavía.")
        } else {
            records.forEach { record ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Fecha: ${record.timestamp}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Correo: ${record.userEmail}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Asignatura: ${record.subject}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Notas y ponderaciones: ${formatNotes(record)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Promedio: ${formatDecimal(record.average)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = if (record.passed) "Estado: Aprobado" else "Estado: Reprobado",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onBackToWelcome,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver a bienvenida")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
    }
}

private fun formatNotes(record: GradeRecord): String {
    return record.notes.zip(record.weights).joinToString(" | ") { (note, weight) ->
        "${formatDecimal(note)} (${formatDecimal(weight)}%)"
    }
}

private fun formatDecimal(value: Double): String {
    return String.format(Locale.getDefault(), "%.2f", value)
}