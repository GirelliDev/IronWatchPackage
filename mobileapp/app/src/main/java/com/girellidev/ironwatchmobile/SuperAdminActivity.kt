package com.girellidev.ironwatchmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.girellidev.ironwatchmobile.ui.theme.IronWatchMobileTheme

class SuperAdminActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IronWatchMobileTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Menu SuperAdmin", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { /* Listar empresas */ }, modifier = Modifier.fillMaxWidth()) { Text("Listar empresas") }
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { /* Listar dispositivos */ }, modifier = Modifier.fillMaxWidth()) { Text("Listar dispositivos") }
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { /* Criar empresa */ }, modifier = Modifier.fillMaxWidth()) { Text("Criar empresa") }
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { /* Adicionar dispositivo */ }, modifier = Modifier.fillMaxWidth()) { Text("Adicionar dispositivo") }
                    }
                }
            }
        }
    }
}
