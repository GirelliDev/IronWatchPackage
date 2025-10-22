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
fun CreateCompanyScreen() {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var apiKey by remember { mutableStateOf("") }
    var promptIA by remember { mutableStateOf("") }
    var welcomeMsg by remember { mutableStateOf("") }
    var reminderMsg by remember { mutableStateOf("") }
    var confirmMsg by remember { mutableStateOf("") }
    var confirmedMsg by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).background(Color(0xFF121212))) {
        Text("Criar Empresa", color = Color.White)
        Spacer(Modifier.height(8.dp))
        BasicTextField(value=name, onValueChange={name=it}, modifier=Modifier.fillMaxWidth())
        BasicTextField(value=email, onValueChange={email=it}, modifier=Modifier.fillMaxWidth())
        BasicTextField(value=apiKey, onValueChange={apiKey=it}, modifier=Modifier.fillMaxWidth())
        BasicTextField(value=promptIA, onValueChange={promptIA=it}, modifier=Modifier.fillMaxWidth())
        BasicTextField(value=welcomeMsg, onValueChange={welcomeMsg=it}, modifier=Modifier.fillMaxWidth())
        BasicTextField(value=reminderMsg, onValueChange={reminderMsg=it}, modifier=Modifier.fillMaxWidth())
        BasicTextField(value=confirmMsg, onValueChange={confirmMsg=it}, modifier=Modifier.fillMaxWidth())
        BasicTextField(value=confirmedMsg, onValueChange={confirmedMsg=it}, modifier=Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Button(onClick={ /* TODO: conectar e enviar dados ao servidor SA */ }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF9b0000))) {
            Text("Criar Empresa")
        }
    }
}
