package com.corelabs.ironwatchuser

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

class MainActivity : AppCompatActivity() {

    private lateinit var campoUsuario: EditText
    private lateinit var campoSenha: EditText
    private lateinit var botaoLogin: Button
    private lateinit var textoErro: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        campoUsuario = findViewById(R.id.campoUsuario)
        campoSenha = findViewById(R.id.campoSenha)
        botaoLogin = findViewById(R.id.botaoLogin)
        textoErro = findViewById(R.id.textoErro)

        botaoLogin.setOnClickListener {

            val usuario = campoUsuario.text.toString().trim()
            val senha = campoSenha.text.toString().trim()

            textoErro.text = ""

            // =========================
            // VALIDAÇÕES
            // =========================

            if (usuario.isEmpty()) {
                campoUsuario.error = "Digite o usuário"
                campoUsuario.requestFocus()
                return@setOnClickListener
            }

            if (senha.isEmpty()) {
                campoSenha.error = "Digite a senha"
                campoSenha.requestFocus()
                return@setOnClickListener
            }
            // =========================
            // JSON PARA O SERVIDOR
            // =========================

            val json = JSONObject().apply {
                put("action", "AUTH_LOGIN")
                put("login", usuario)
                put("password", senha)
            }.toString()

            enviarLogin(json)
        }
    }

    // =========================
    // ENVIO SOCKET
    // =========================

    private fun enviarLogin(json: String) {

        CoroutineScope(Dispatchers.IO).launch {

            var socket: Socket? = null
            var writer: OutputStreamWriter? = null
            var reader: BufferedReader? = null

            try {

                withContext(Dispatchers.Main) {
                    botaoLogin.isEnabled = false
                    botaoLogin.text = "Entrando..."
                }

                // 🔥 CONEXÃO COM O SERVIDOR
                socket = Socket(ServerConfig.SERVER_HOST, ServerConfig.SERVER_PORT)

                writer = OutputStreamWriter(socket.getOutputStream())
                reader = BufferedReader(InputStreamReader(socket.getInputStream()))

                // ENVIA JSON + QUEBRA DE LINHA
                writer.write("$json\n")
                writer.flush()

                // RESPOSTA DO SERVIDOR
                val responseLine = reader.readLine()

                if (responseLine.isNullOrBlank()) {
                    withContext(Dispatchers.Main) {
                        textoErro.text = "Servidor não respondeu"
                    }
                    return@launch
                }

                val response = JSONObject(responseLine)

                val success = response.optBoolean("success", false)
                val message = response.optString("message", "Erro")
                val token = response.optString("token", "")

                withContext(Dispatchers.Main) {

                    if (success) {

                        Toast.makeText(
                            this@MainActivity,
                            "Login OK",
                            Toast.LENGTH_SHORT
                        ).show()

                        // aqui você pode abrir outra activity
                        // ou salvar token se quiser

                    } else {
                        textoErro.text = message
                    }
                }

            } catch (e: Exception) {

                withContext(Dispatchers.Main) {
                    textoErro.text = "Erro: ${e.message}"
                }

            } finally {

                try { reader?.close() } catch (_: Exception) {}
                try { writer?.close() } catch (_: Exception) {}
                try { socket?.close() } catch (_: Exception) {}

                withContext(Dispatchers.Main) {
                    botaoLogin.isEnabled = true
                    botaoLogin.text = "LOGIN"
                }
            }
        }
    }
}