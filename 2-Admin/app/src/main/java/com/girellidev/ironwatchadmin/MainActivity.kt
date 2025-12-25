package com.girellidev.ironwatchadmin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

class MainActivity : AppCompatActivity() {

    private val serverhost = "192.168.1.12"
    private val serverport = 9999

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val senhaInput = findViewById<EditText>(R.id.codigoInput)
        val btnEnviar = findViewById<Button>(R.id.btnEnviar)

        btnEnviar.setOnClickListener {
            val senha = senhaInput.text.toString().trim()
            if (senha.isEmpty()) {
                Toast.makeText(this, "Digite a Senha", Toast.LENGTH_SHORT).show()
            } else {
                enviarSenha(senha)
            }
        }
    }
private fun enviarSenha(senha: String) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            Socket(serverhost, serverport).use { socket ->
                val writer = OutputStreamWriter(socket.getOutputStream(), Charsets.UTF_8)
                val reader = BufferedReader(InputStreamReader(socket.getInputStream(), Charsets.UTF_8))

                val json = """
                {
                  "cmd": "login",
                  "payload": {
                    "senha": "$senha"
                  }
                }
                """.trimIndent()

                // ENVIA JSON + delimitador
                writer.write(json + "\n")
                writer.flush()

                // LÊ resposta do servidor
                val response = reader.readLine() ?: ""

                withContext(Dispatchers.Main) {
                    if (response.contains("\"ok\":true")) {
                        val intent = Intent(this@MainActivity, DashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Resposta do servidor: $response",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                println("JSON enviado:")
                println(json)
                println("Resposta recebida:")
                println(response)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@MainActivity,
                    "Erro ao conectar: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

}
