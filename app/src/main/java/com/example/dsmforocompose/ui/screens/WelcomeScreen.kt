package com.example.dsmforocompose.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WelcomeScreen(
    email: String,
    onGoToGrades: () -> Unit,
    onGoToHistory: () -> Unit,
    onLogout: () -> Unit
) {
    val userName = email.substringBefore("@").ifBlank { email }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Pantalla de bienvenida",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Bienvenido, $userName",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Correo: $email",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onGoToGrades,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ir a ingreso de notas")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onGoToHistory,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ir al historial")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onLogout) {
            Text("Cerrar sesión")
        }
    }
}