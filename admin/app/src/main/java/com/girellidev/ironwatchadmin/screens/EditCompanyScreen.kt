package com.girellidev.ironwatchadmin.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun EditCompanyScreen() {
    // Placeholder para dropdown de seleção de empresa e edição
    var selectedCompany by remember { mutableStateOf("") }
    var newName by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).background(Color(0xFF121212))) {
        Text("Editar Empresa", color = Color.White)
        Spacer(Modifier.height(8.dp))
        BasicTextField(value=newName, onValueChange={newName=it}, modifier=Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Button(onClick={ /* TODO: enviar edição para servidor SA */ }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF9b0000))) {
            Text("Salvar Alterações")
        }
    }
}
