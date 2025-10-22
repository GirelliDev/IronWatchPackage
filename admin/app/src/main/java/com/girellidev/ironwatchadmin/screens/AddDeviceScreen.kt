package com.girellidev.ironwatchadmin.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AddDeviceScreen() {
    var empresaId by remember { mutableStateOf("") }
    var pairCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFF121212))
    ) {
        Text("Adicionar Dispositivos", color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))

        BasicTextField(
            value = empresaId,
            onValueChange = { empresaId = it },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1E1E))
                .padding(8.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                // TODO: gerar paircode via SA
                pairCode = "123456"
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9b0000))
        ) {
            Text("Gerar PairCode", color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("PairCode: $pairCode", color = Color.White)
    }
}
