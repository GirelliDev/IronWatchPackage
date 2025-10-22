package com.girellidev.ironwatchadmin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.girellidev.ironwatchadmin.models.Company
import com.girellidev.ironwatchadmin.screens.DashboardScreen

@Composable
fun IronWatchAdminApp() {
    var menuVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("IronWatch Admin") },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF121212))
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { menuVisible = !menuVisible }, containerColor = Color(0xFF9b0000)) {
                Text("≡")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row {
                if (menuVisible) {
                    MenuPanel()
                }
                DashboardScreen()
            }
        }
    }
}

@Composable
fun MenuPanel() {
    Column(
        modifier = Modifier
            .width(200.dp)
            .fillMaxHeight()
            .background(Color(0xFF1E1E1E))
            .padding(16.dp)
    ) {
        Text("Menu", color = Color.White, fontSize = 20.sp)
        Spacer(Modifier.height(16.dp))
        Text("Criar Empresa", color = Color.White)
        Spacer(Modifier.height(8.dp))
        Text("Editar Empresa", color = Color.White)
        Spacer(Modifier.height(8.dp))
        Text("Adicionar Dispositivos", color = Color.White)
        Spacer(Modifier.height(8.dp))
        Text("Logs", color = Color.White)
    }
}
