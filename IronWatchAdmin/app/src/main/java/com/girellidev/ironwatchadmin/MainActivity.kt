package com.girellidev.ironwatchadmin

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

class MainActivity : AppCompatActivity() {

    private val serverHost = "181.215.45.62"
    private val serverPort = 5555

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = getSharedPreferences("ironwatch_admin", MODE_PRIVATE)

        // Se já tiver token salvo, entra direto
        val savedToken = prefs.getString("auth_token", null)
        if (!savedToken.isNullOrBlank()) {
            abrirDashboard()
            return
        }

        setContentView(R.layout.activity_main)

        val codigoInput = findViewById<EditText>(R.id.codigoInput)
        val btnEnviar = findViewById<Button>(R.id.btnEnviar)

        btnEnviar.setOnClickListener {
            val codigo = codigoInput.text.toString().trim()

            if (codigo.isEmpty()) {
                Toast.makeText(this, "Digite o código", Toast.LENGTH_SHORT).show()
            } else {
                autenticarCodigo(codigo)
            }
        }
    }

    private fun autenticarCodigo(codigo: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Socket(serverHost, serverPort).use { socket ->
                    val writer = OutputStreamWriter(socket.getOutputStream())
                    val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

                    val payload = "AUTH|$codigo\n"
                    writer.write(payload)
                    writer.flush()

                    val response = reader.readLine()

                    withContext(Dispatchers.Main) {
                        if (response.isNullOrBlank()) {
                            Toast.makeText(
                                this@MainActivity,
                                "Servidor não respondeu",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@withContext
                        }

                        if (response.startsWith("OK|")) {
                            val token = response.substringAfter("OK|").trim()

                            salvarToken(token)

                            Toast.makeText(
                                this@MainActivity,
                                "Acesso liberado",
                                Toast.LENGTH_SHORT
                            ).show()

                            abrirDashboard()
                        } else if (response == "FAILED") {
                            Toast.makeText(
                                this@MainActivity,
                                "Código inválido",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Resposta inesperada: $response",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    println("Enviado: AUTH|$codigo | Recebido: $response")
                }
            } catch (e: Exception) {
                e.printStackTrace()

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "Erro ao conectar: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun salvarToken(token: String) {
        prefs.edit()
            .putString("auth_token", token)
            .apply()
    }

    private fun abrirDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}