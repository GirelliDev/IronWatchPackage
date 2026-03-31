package com.girellidev.ironwatchadmin

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.girellidev.ironwatchadmin.config.ServerConfig
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var btnScanQr: Button
    private lateinit var scanner: GmsBarcodeScanner
    private lateinit var edtLogin: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtToken: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = getSharedPreferences("ironwatch_admin", MODE_PRIVATE)

        val savedHost = prefs.getString("server_host", BuildConfig.SERVER_HOST) ?: BuildConfig.SERVER_HOST
        val savedPort = prefs.getInt("server_port", BuildConfig.SERVER_PORT)
        ServerConfig.initialize(savedHost, savedPort)

        val savedToken = prefs.getString("auth_token", null)
        if (!savedToken.isNullOrBlank()) {
            abrirDashboard(savedToken)
            return
        }

        setContentView(R.layout.activity_main)

        edtLogin = findViewById(R.id.edtLogin)
        edtPassword = findViewById(R.id.edtPassword)
        edtToken = findViewById(R.id.edtToken)
        btnLogin = findViewById(R.id.btnLogin)
        btnScanQr = findViewById(R.id.btnScanQr)

        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()

        scanner = GmsBarcodeScanning.getClient(this, options)

        btnScanQr.setOnClickListener {
            iniciarScanner()
        }

        btnLogin.setOnClickListener {
            val login = edtLogin.text.toString().trim()
            val password = edtPassword.text.toString().trim()
            val token = edtToken.text.toString().trim()

            if (login.isBlank()) {
                toast("Digite o login")
                return@setOnClickListener
            }

            if (password.isBlank()) {
                toast("Digite a senha")
                return@setOnClickListener
            }

            autenticar(login, password, token)
        }
    }

    private fun iniciarScanner() {
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                val raw = barcode.rawValue ?: return@addOnSuccessListener

                try {
                    val json = JSONObject(raw)

                    val login = json.optString("login", "")
                    val password = json.optString("password", "")
                    val code = json.optString("code", "")

                    edtLogin.setText(login)
                    edtPassword.setText(password)
                    edtToken.setText(code)

                    toast("QR carregado")
                } catch (e: Exception) {
                    toast("QR inválido")
                }
            }
            .addOnCanceledListener {
                toast("Scanner cancelado")
            }
            .addOnFailureListener { e ->
                toast("Erro ao abrir scanner: ${e.message}")
            }
    }

    private fun autenticar(login: String, password: String, tokenInformado: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            var socket: Socket? = null
            var writer: OutputStreamWriter? = null
            var reader: BufferedReader? = null

            try {
                withContext(Dispatchers.Main) {
                    btnLogin.isEnabled = false
                    btnLogin.text = "Entrando..."
                }

                socket = Socket(ServerConfig.SERVER_HOST, ServerConfig.SERVER_PORT)
                writer = OutputStreamWriter(socket.getOutputStream())
                reader = BufferedReader(InputStreamReader(socket.getInputStream()))

                val request = JSONObject().apply {
                    put("action", "AUTH_LOGIN")
                    put("login", login)
                    put("password", password)

                    if (tokenInformado.isNotBlank()) {
                        put("token", tokenInformado)
                    }
                }

                writer.write("$request\n")
                writer.flush()

                val responseLine = reader.readLine()

                if (responseLine.isNullOrBlank()) {
                    withContext(Dispatchers.Main) {
                        toast("Servidor não respondeu")
                    }
                    return@launch
                }

                val response = JSONObject(responseLine)
                val success = response.optBoolean("success", false)
                val message = response.optString("message", "Falha na autenticação")
                val tokenRecebido = response.optString("token", "").trim()

                withContext(Dispatchers.Main) {
                    if (success) {
                        val tokenFinal = when {
                            tokenRecebido.isNotBlank() && tokenRecebido != "null" -> tokenRecebido
                            tokenInformado.isNotBlank() -> tokenInformado
                            else -> ""
                        }

                        if (tokenFinal.isBlank()) {
                            toast("Login OK, mas nenhum token foi recebido")
                            return@withContext
                        }

                        salvarToken(tokenFinal)
                        toast("Acesso liberado")
                        abrirDashboard(tokenFinal)
                    } else {
                        toast(message)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    toast("Erro ao conectar: ${e.message}")
                }
            } finally {
                try { reader?.close() } catch (_: Exception) {}
                try { writer?.close() } catch (_: Exception) {}
                try { socket?.close() } catch (_: Exception) {}

                withContext(Dispatchers.Main) {
                    btnLogin.isEnabled = true
                    btnLogin.text = "ENTRAR"
                }
            }
        }
    }

    private fun salvarToken(token: String) {
        prefs.edit()
            .putString("auth_token", token)
            .apply()
    }

    private fun abrirDashboard(token: String) {
        val intent = Intent(this, DashboardActivity::class.java).apply {
            putExtra("token", token)
        }
        startActivity(intent)
        finish()
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}