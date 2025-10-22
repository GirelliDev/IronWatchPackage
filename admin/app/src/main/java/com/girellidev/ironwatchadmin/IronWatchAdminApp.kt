package com.girellidev.ironwatchadmin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.girellidev.ironwatchadmin.screens.AddDeviceScreen
import com.girellidev.ironwatchadmin.screens.CreateCompanyScreen
import com.girellidev.ironwatchadmin.screens.DashboardScreen
import com.girellidev.ironwatchadmin.screens.EditCompanyScreen
import kotlinx.coroutines.launch

// ---------- TELA ENUM ----------
sealed class Screen {
    object Dashboard : Screen()
    object CreateCompany : Screen()
    object EditCompany : Screen()
    object AddDevices : Screen()
}

// ---------- APP PRINCIPAL ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IronWatchAdminApp() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent { screen ->
                currentScreen = screen
                scope.launch { drawerState.close() }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("IronWatch Admin", color = Color.White) },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFF121212)),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                ScreenContent(currentScreen)
            }
        }
    }
}

// ---------- MENU LATERAL ----------
@Composable
fun DrawerContent(onMenuClick: (Screen) -> Unit) {
    Column(
        modifier = Modifier
            .width(240.dp)
            .fillMaxHeight()
            .background(Color(0xFF1E1E1E))
            .padding(16.dp)
    ) {
        Text("Menu", color = Color.White, fontSize = 22.sp)
        Spacer(Modifier.height(20.dp))
        DrawerItem("Dashboard") { onMenuClick(Screen.Dashboard) }
        DrawerItem("Criar Empresa") { onMenuClick(Screen.CreateCompany) }
        DrawerItem("Editar Empresa") { onMenuClick(Screen.EditCompany) }
        DrawerItem("Adicionar Dispositivos") { onMenuClick(Screen.AddDevices) }
    }
}

@Composable
fun DrawerItem(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        color = Color.White,
        fontSize = 18.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable { onClick() }
    )
}

// ---------- CONTEÚDO DAS TELAS ----------
@Composable
fun ScreenContent(screen: Screen) {
    when (screen) {
        is Screen.Dashboard -> DashboardScreen()
        is Screen.CreateCompany -> CreateCompanyScreen()
        is Screen.EditCompany -> EditCompanyScreen()
        is Screen.AddDevices -> AddDeviceScreen()
    }
}

// ---------- PREVIEW ----------
@Preview(showBackground = true)
@Composable
fun PreviewIronWatchAdminApp() {
    IronWatchAdminApp()
}
