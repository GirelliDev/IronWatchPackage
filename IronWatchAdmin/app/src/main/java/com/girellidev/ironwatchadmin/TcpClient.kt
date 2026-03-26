package com.girellidev.ironwatchadmin

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import com.girellidev.ironwatchadmin.config.ServerConfig

object TcpClient {

    fun send(message: String): String {
        Socket(ServerConfig.SERVER_HOST, ServerConfig.SERVER_PORT).use { socket ->
            val writer = OutputStreamWriter(socket.getOutputStream())
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

            writer.write("$message\n")
            writer.flush()

            return reader.readLine() ?: """{"success":false,"message":"Resposta vazia do servidor"}"""
        }
    }
}