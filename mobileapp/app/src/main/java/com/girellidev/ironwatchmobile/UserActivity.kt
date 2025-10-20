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

class UserActivity : ComponentActivity() {

    var autoReplyEnabled by mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IronWatchMobileTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Painel do Usuário", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Responder mensagens automaticamente")
                            Switch(checked = autoReplyEnabled, onCheckedChange = { autoReplyEnabled = it })
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { /* Mostrar analytics */ }, modifier = Modifier.fillMaxWidth()) {
                            Text("Ver Analytics")
                        }
                    }
                }
            }
        }
    }
}
