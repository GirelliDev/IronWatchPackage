package com.girellidev.ironwatchadmin

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

object TcpClient {

    private const val SERVER_HOST = "181.215.45.62"
    private const val SERVER_PORT = 5555

    fun send(message: String): String {
        Socket(SERVER_HOST, SERVER_PORT).use { socket ->
            val writer = OutputStreamWriter(socket.getOutputStream())
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

            writer.write("$message\n")
            writer.flush()

            return reader.readLine() ?: """{"success":false,"message":"Resposta vazia do servidor"}"""
        }
    }
}