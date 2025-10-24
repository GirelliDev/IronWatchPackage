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

    private val serverhost = "192.168.0.101"
    private val serverport = 9999

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val codigoInput = findViewById<EditText>(R.id.codigoInput)
        val btnEnviar = findViewById<Button>(R.id.btnEnviar)

        btnEnviar.setOnClickListener {
            val codigo = codigoInput.text.toString().trim()
            if (codigo.isEmpty()) {
                Toast.makeText(this, "Digite o código", Toast.LENGTH_SHORT).show()
            } else {
                enviarCodigo(codigo)
            }
        }
    }

    private fun enviarCodigo(codigo: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Socket(serverhost, serverport).use { socket ->
                    val writer = OutputStreamWriter(socket.getOutputStream())
                    val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                    writer.write("$codigo\n")
                    writer.flush()

                    // lê resposta
                    val response = reader.readLine()

                    withContext(Dispatchers.Main) {
                        if (response.contains("new-token")) {
                            // abriu dashboard
                            val intent = Intent(this@MainActivity, DashboardActivity::class.java)
                            startActivity(intent)
                            finish() // fecha login
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Resposta do servidor: $response",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    println("Enviado: $codigo | Recebido: $response")
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
