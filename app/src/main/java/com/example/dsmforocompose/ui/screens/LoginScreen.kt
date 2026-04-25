package com.example.dsmforocompose.ui.screens

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.dsmforocompose.data.AppRepository
import java.util.Locale

@Composable
fun LoginScreen(
    repository: AppRepository,
    onLoginSuccess: (String) -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var feedback by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Login y Registro",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val normalizedEmail = email.trim().lowercase(Locale.getDefault())

                feedback = when {
                    normalizedEmail.isBlank() -> "El correo no puede estar vacío."
                    !Patterns.EMAIL_ADDRESS.matcher(normalizedEmail).matches() -> "Formato de correo inválido."
                    password.isBlank() -> "La contraseña no puede estar vacía."
                    repository.validateLogin(normalizedEmail, password) -> {
                        onLoginSuccess(normalizedEmail)
                        ""
                    }
                    else -> "Credenciales no encontradas en el archivo de usuarios."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                val normalizedEmail = email.trim().lowercase(Locale.getDefault())

                feedback = when {
                    normalizedEmail.isBlank() -> "El correo no puede estar vacío."
                    !Patterns.EMAIL_ADDRESS.matcher(normalizedEmail).matches() -> "Formato de correo inválido."
                    password.isBlank() -> "La contraseña no puede estar vacía."
                    repository.registerUser(normalizedEmail, password) -> "Usuario registrado correctamente."
                    else -> "No se pudo registrar. El correo ya existe o los datos son inválidos."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (feedback.isNotBlank()) {
            Text(
                text = feedback,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = {
            email = "admin@demo.com"
            password = "123456"
            feedback = "Usuario de prueba cargado. Puedes iniciar sesión con esos datos."
        }) {
            Text("Cargar usuario de prueba")
        }
    }
}