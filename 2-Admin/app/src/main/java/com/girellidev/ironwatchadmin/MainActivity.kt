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
import androidx.core.content.edit

class MainActivity : AppCompatActivity() {

    private val serverHost = "10.209.254.202"
    private val serverPort = 9999

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔹 se já tem sessão válida, pula login
        if (sessaoValida()) {
            irParaDashboard()
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
                enviarCodigo(codigo)
            }
        }
    }

    // ===============================
    // LOGIN COM CÓDIGO ROTATIVO
    // ===============================
    private fun enviarCodigo(codigo: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Socket(serverHost, serverPort).use { socket ->
                    val writer = OutputStreamWriter(socket.getOutputStream(), Charsets.UTF_8)
                    val reader = BufferedReader(
                        InputStreamReader(socket.getInputStream(), Charsets.UTF_8)
                    )

                    val json = """
                    {
                      "cmd": "login",
                      "payload": {
                        "code": "$codigo"
                      }
                    }
                    """.trimIndent()

                    writer.write(json + "\n")
                    writer.flush()

                    val response = reader.readLine() ?: ""

                    withContext(Dispatchers.Main) {
                        if (response.contains("\"ok\":true") && response.contains("\"token\"")) {
                            val token = extrairToken(response)

                            if (token.isNotEmpty()) {
                                salvarSessao(token)
                                irParaDashboard()
                            } else {
                                erroLogin()
                            }
                        } else {
                            erroLogin()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "Erro de conexão",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // ===============================
    // SESSÃO
    // ===============================
    private fun salvarSessao(token: String) {
        val expiresAt = System.currentTimeMillis() + (2 * 60 * 60 * 1000) // 2h

        getSharedPreferences("ironwatch_session", MODE_PRIVATE)
            .edit {
                putString("token", token)
                    .putLong("expiresAt", expiresAt)
            }
    }

    private fun sessaoValida(): Boolean {
        val prefs = getSharedPreferences("ironwatch_session", MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val expiresAt = prefs.getLong("expiresAt", 0)

        return token != null && System.currentTimeMillis() < expiresAt
    }

    fun limparSessao() {
        getSharedPreferences("ironwatch_session", MODE_PRIVATE)
            .edit {
                clear()
            }
    }

    // ===============================
    // UTIL
    // ===============================
    private fun extrairToken(json: String): String {
        val regex = """"token"\s*:\s*"([^"]+)"""".toRegex()
        return regex.find(json)?.groupValues?.get(1) ?: ""
    }

    private fun erroLogin() {
        Toast.makeText(
            this,
            "Código inválido ou expirado",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun irParaDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}
