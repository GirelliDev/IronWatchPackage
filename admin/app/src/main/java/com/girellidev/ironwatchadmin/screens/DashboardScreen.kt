package com.girellidev.ironwatchadmin.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.girellidev.ironwatchadmin.models.Company
import com.girellidev.ironwatchadmin.viewmodel.AdminViewModel

@Composable
fun DashboardScreen(viewModel: AdminViewModel = AdminViewModel()) {
    val companies by viewModel.companies.collectAsState()
    viewModel.fetchCompanies()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Dashboard", color = Color.White, fontSize = 24.sp) }
        items(companies) { c ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().background(Color(0xFF1E1E1E)).padding(12.dp)
            ) {
                Box(
                    modifier = Modifier.size(16.dp).background(if (c.active) Color.Green else Color.Red, CircleShape)
                )
                Spacer(Modifier.width(8.dp))
                Text(c.name, color = Color.White, fontSize = 18.sp)
            }
        }
    }
}
