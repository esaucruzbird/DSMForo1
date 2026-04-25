package com.example.dsmforocompose.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.dsmforocompose.data.AppRepository
import java.util.Locale
import kotlin.math.abs

@Composable
fun GradesScreen(
    repository: AppRepository,
    email: String,
    onBackToWelcome: () -> Unit,
    onLogout: () -> Unit
) {
    var subject by remember { mutableStateOf("") }
    val noteStates = remember { List(5) { mutableStateOf("") } }
    val weightStates = remember { mutableStateListOf("20", "20", "20", "20", "20") }

    var resultText by remember { mutableStateOf("") }
    var statusText by remember { mutableStateOf("") }
    var feedbackText by remember { mutableStateOf("") }

    val currentWeightSum = weightStates.sumOf { parseDecimalOrZero(it) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ingreso de Notas",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = subject,
            onValueChange = { subject = it },
            label = { Text("Asignatura") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Cada nota tiene su ponderación editable. La suma ideal es 100%.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        repeat(5) { index ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = noteStates[index].value,
                            onValueChange = { noteStates[index].value = sanitizeDecimalInput(it) },
                            label = { Text("Nota ${index + 1}") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = weightStates[index],
                            onValueChange = { weightStates[index] = sanitizeDecimalInput(it) },
                            label = { Text("Ponderación %") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.width(140.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        Text(
            text = "Suma actual de ponderaciones: ${formatDecimal(currentWeightSum)}%",
            style = MaterialTheme.typography.bodyMedium
        )

        if (abs(currentWeightSum - 100.0) > 0.001) {
            Text(
                text = "Aviso: la suma no es 100%. El cálculo se hará con las ponderaciones ingresadas.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val subjectValue = subject.trim()
                if (subjectValue.isBlank()) {
                    feedbackText = "La asignatura no puede estar vacía."
                    return@Button
                }

                val notes = mutableListOf<Double>()
                val weights = mutableListOf<Double>()

                for (i in 0 until 5) {
                    val note = parseDecimal(noteStates[i].value)
                    if (note == null || note < 0.0) {
                        feedbackText = "La nota ${i + 1} no es válida."
                        return@Button
                    }

                    val weight = parseDecimal(weightStates[i])
                    if (weight == null || weight < 0.0) {
                        feedbackText = "La ponderación ${i + 1} no es válida."
                        return@Button
                    }

                    notes.add(note)
                    weights.add(weight)
                }

                val totalWeight = weights.sum()
                if (totalWeight <= 0.0) {
                    feedbackText = "La suma de las ponderaciones debe ser mayor que cero."
                    return@Button
                }

                val weightedSum = notes.zip(weights).sumOf { (note, weight) -> note * weight }
                val average = weightedSum / totalWeight
                val passed = average >= 6.0

                repository.saveGradeRecord(
                    userEmail = email,
                    subject = subjectValue,
                    notes = notes,
                    weights = weights,
                    average = average,
                    passed = passed
                )

                resultText = "Promedio: ${formatDecimal(average)}"
                statusText = if (passed) "El estudiante aprobó." else "El estudiante reprobó."
                feedbackText = "Registro guardado en el archivo de notas."
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calcular promedio y guardar")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                subject = ""
                for (i in 0 until 5) {
                    noteStates[i].value = ""
                    weightStates[i] = "20"
                }
                resultText = ""
                statusText = ""
                feedbackText = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Limpiar campos")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onBackToWelcome,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver a bienvenida")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (resultText.isNotBlank()) {
            Text(
                text = resultText,
                style = MaterialTheme.typography.headlineSmall
            )
        }

        if (statusText.isNotBlank()) {
            Text(
                text = statusText,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        if (feedbackText.isNotBlank()) {
            Text(
                text = feedbackText,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun sanitizeDecimalInput(text: String): String {
    val normalized = text.replace(',', '.')
    val builder = StringBuilder()
    var dotUsed = false

    normalized.forEach { ch ->
        when {
            ch.isDigit() -> builder.append(ch)
            ch == '.' && !dotUsed -> {
                builder.append(ch)
                dotUsed = true
            }
        }
    }

    return builder.toString()
}

private fun parseDecimal(text: String): Double? {
    val normalized = text.trim().replace(',', '.')
    return normalized.toDoubleOrNull()
}

private fun parseDecimalOrZero(text: String): Double {
    return parseDecimal(text) ?: 0.0
}

private fun formatDecimal(value: Double): String {
    return String.format(Locale.getDefault(), "%.2f", value)
}