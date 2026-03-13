package com.girellidev.ironwatchadmin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

class LoginActivity : AppCompatActivity() {

    private lateinit var edtLogin: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button

    private val serverHost = "181.215.45.62"
    private val serverPort = 5555

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        edtLogin = findViewById(R.id.edtLogin)
        edtPassword = findViewById(R.id.edtPassword)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val login = edtLogin.text.toString().trim()
            val password = edtPassword.text.toString()

            if (login.isBlank()) {
                toast("Digite o login")
                return@setOnClickListener
            }

            if (password.isBlank()) {
                toast("Digite a senha")
                return@setOnClickListener
            }

            authenticate(login, password)
        }
    }

    private fun authenticate(login: String, password: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            var socket: Socket? = null
            var writer: OutputStreamWriter? = null
            var reader: BufferedReader? = null

            try {
                withContext(Dispatchers.Main) {
                    btnLogin.isEnabled = false
                    btnLogin.text = "Entrando..."
                }

                socket = Socket(serverHost, serverPort)
                writer = OutputStreamWriter(socket.getOutputStream())
                reader = BufferedReader(InputStreamReader(socket.getInputStream()))

                val request = JSONObject().apply {
                    put("action", "AUTH_LOGIN")
                    put("login", login)
                    put("password", password)
                }

                writer.write("${request}\n")
                writer.flush()

                val responseLine = reader.readLine()

                if (responseLine.isNullOrBlank()) {
                    toast("Servidor não respondeu")
                    return@launch
                }

                val response = JSONObject(responseLine)

                val success = response.optBoolean("success", false)
                val token = response.optString("token", "")
                val message = response.optString("message", "Falha no login")

                if (success && token.isNotBlank() && token != "null") {
                    withContext(Dispatchers.Main) {
                        val intent = Intent(this@LoginActivity, DashboardActivity::class.java).apply {
                            putExtra("login", login)
                            putExtra("password", password)
                            putExtra("token", token)
                        }
                        startActivity(intent)
                        finish()
                    }
                } else {
                    toast(message)
                }

            } catch (e: Exception) {
                toast("Erro no login: ${e.message}")
            } finally {
                try { reader?.close() } catch (_: Exception) {}
                try { writer?.close() } catch (_: Exception) {}
                try { socket?.close() } catch (_: Exception) {}

                withContext(Dispatchers.Main) {
                    btnLogin.isEnabled = true
                    btnLogin.text = "Entrar"
                }
            }
        }
    }

    private suspend fun toast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
        }
    }
}