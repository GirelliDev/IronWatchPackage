package com.girellidev.ironwatchmobile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.PrintWriter
import java.net.Socket

@Composable
fun CodeScreen(
    serverIp: String = "192.168.0.100",
    serverPort: Int = 5500,
    onCodeSuccess: (tipo: String) -> Unit = {},
    onCodeError: (String) -> Unit = {}
) {
    var code by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    suspend fun sendCodeToServer(code: String) {
        withContext(Dispatchers.IO) {
            try {
                Socket(serverIp, serverPort).use { socket ->
                    val writer = PrintWriter(socket.getOutputStream(), true)
                    val reader = socket.getInputStream().bufferedReader()

                    // envia código pro servidor, tipo não importa, servidor valida pendingCodes
                    writer.println(code)

                    // lê resposta do servidor
                    val response = reader.readLine() ?: "Nenhuma resposta do servidor"

                    when {
                        response.contains("SuperAdmin registrado com sucesso", ignoreCase = true) -> {
                            onCodeSuccess("superadmin")
                            message = "SuperAdmin confirmado!"
                        }
                        response.contains("Pareado com sucesso", ignoreCase = true) -> {
                            onCodeSuccess("device")
                            message = "Dispositivo confirmado!"
                        }
                        else -> {
                            onCodeError(response)
                            message = "Falha: $response"
                        }
                    }
                }
            } catch (e: Exception) {
                onCodeError(e.message ?: "Erro desconhecido")
                message = "Erro ao conectar: ${e.message}"
            } finally {
                loading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Insira seu código para Continuar", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = code,
            onValueChange = { code = it },
            placeholder = { Text("Código") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (code.isNotBlank() && !loading) {
                    loading = true
                    message = ""
                    LaunchedEffect(code) { sendCodeToServer(code) }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading
        ) {
            Text(if (loading) "Enviando..." else "Continuar")
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (message.isNotEmpty()) {
            Text(message, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
