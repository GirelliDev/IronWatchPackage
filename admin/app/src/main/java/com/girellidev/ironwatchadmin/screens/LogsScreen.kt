package com.girellidev.ironwatchadmin.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LogsScreen() {
    val logs = listOf("Servidor iniciado", "Empresa A criada", "PairCode gerado") // TODO: bind com ViewModel

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp).background(Color(0xFF121212)), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(logs.size) { idx ->
            Text(logs[idx], color=Color.White, fontSize=14.sp)
        }
    }
}
