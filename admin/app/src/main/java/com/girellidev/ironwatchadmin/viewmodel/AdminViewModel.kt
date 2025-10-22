package com.girellidev.ironwatchadmin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.girellidev.ironwatchadmin.models.Company
import com.girellidev.ironwatchadmin.network.ServerConfig
import com.girellidev.ironwatchadmin.network.TcpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {

    private val mainClient = TcpClient(ServerConfig.MAIN_IP, ServerConfig.MAIN_PORT)
    private val saClient = TcpClient(ServerConfig.SA_IP, ServerConfig.SA_PORT)

    private val _companies = MutableStateFlow<List<Company>>(emptyList())
    val companies: StateFlow<List<Company>> = _companies

    private val _logs = MutableStateFlow<List<String>>(emptyList())
    val logs: StateFlow<List<String>> = _logs

    fun fetchCompanies() {
        viewModelScope.launch(Dispatchers.IO) {
            mainClient.connect()
            val resp = mainClient.sendCommand("LIST_COMPANIES")
            val list = resp.split("|").mapNotNull {
                val parts = it.split(",")
                if (parts.size >= 3) Company(parts[0], parts[1], parts[2]=="1") else null
            }
            _companies.value = list
            mainClient.disconnect()
        }
    }

    fun log(msg: String) { _logs.value = _logs.value + msg }
}
