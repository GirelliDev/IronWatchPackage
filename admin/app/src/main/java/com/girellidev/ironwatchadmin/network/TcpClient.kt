package com.girellidev.ironwatchadmin.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.net.Socket

class TcpClient(private val ip: String, private val port: Int) {
    private var socket: Socket? = null
    private var reader: BufferedReader? = null
    private var writer: BufferedWriter? = null

    suspend fun connect(): Boolean = withContext(Dispatchers.IO) {
        try {
            socket = Socket(ip, port)
            reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))
            writer = BufferedWriter(OutputStreamWriter(socket!!.getOutputStream()))
            true
        } catch (e: Exception) { e.printStackTrace(); false }
    }

    suspend fun sendCommand(command: String): String = withContext(Dispatchers.IO) {
        writer?.write("$command\n")
        writer?.flush()
        reader?.readLine() ?: ""
    }

    fun disconnect() {
        try { reader?.close(); writer?.close(); socket?.close() } catch (_: Exception) {}
    }
}
