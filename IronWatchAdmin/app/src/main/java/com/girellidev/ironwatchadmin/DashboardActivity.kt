package com.girellidev.ironwatchadmin

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

class DashboardActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CompanyAdapter

    private val serverHost = "181.215.45.62"
    private val serverPort = 5555

    private var socket: Socket? = null
    private var writer: OutputStreamWriter? = null
    private var reader: BufferedReader? = null

    @Volatile
    private var currentToken: String = ""

    private var listenerJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        drawerLayout = findViewById(R.id.drawer_layout)
        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CompanyAdapter()
        recyclerView.adapter = adapter

        findViewById<ImageView>(R.id.menuButton).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        findViewById<Button>(R.id.btnRefresh).setOnClickListener {
            requestCompanies()
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            disconnect()
            finish()
        }

        startConnection()
    }

    private fun startConnection() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                socket = Socket(serverHost, serverPort)
                writer = OutputStreamWriter(socket!!.getOutputStream())
                reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))

                log("Conectado ao servidor")

                startListener()

                sendJson(
                    JSONObject().apply {
                        put("action", "new-token")
                    }
                )

            } catch (e: Exception) {
                showToast("Falha na conexão: ${e.message}")
            }
        }
    }

    private fun startListener() {
        listenerJob?.cancel()

        listenerJob = lifecycleScope.launch(Dispatchers.IO) {
            try {
                while (true) {
                    val line = reader?.readLine() ?: break
                    handleServerMessage(line)
                }
            } catch (e: Exception) {
                log("Listener encerrado: ${e.message}")
                if (!isFinishing && !isDestroyed) {
                    showToast("Conexão encerrada: ${e.message}")
                }
            }
        }
    }

    private suspend fun handleServerMessage(line: String) {
        val trimmed = line.trim()
        log("SERVER -> $trimmed")

        try {
            // Caso servidor responda no formato legado: TOKEN:abc123
            if (trimmed.startsWith("TOKEN:")) {
                val newToken = trimmed.removePrefix("TOKEN:").trim()
                currentToken = newToken

                showToast("Token atualizado")
                delay(200)
                requestCompanies()
                return
            }

            if (!trimmed.startsWith("{")) {
                log("Mensagem não tratada: $trimmed")
                return
            }

            val json = JSONObject(trimmed)

            // Token em JSON
            if (json.has("token") && currentToken.isBlank()) {
                currentToken = json.optString("token", "")
                if (currentToken.isNotBlank()) {
                    showToast("Token atualizado")
                    delay(200)
                    requestCompanies()
                    return
                }
            }

            // Lista de empresas em companies
            if (json.optBoolean("success", false) && json.has("companies")) {
                val companies = parseCompanies(json.getJSONArray("companies"))
                withContext(Dispatchers.Main) {
                    adapter.setCompanies(companies)
                }
                return
            }

            // Caso venha dentro de data.companies
            if (json.optBoolean("success", false) && json.has("data")) {
                val data = json.optJSONObject("data")
                if (data != null && data.has("companies")) {
                    val companies = parseCompanies(data.getJSONArray("companies"))
                    withContext(Dispatchers.Main) {
                        adapter.setCompanies(companies)
                    }
                    return
                }
            }

            if (json.has("message")) {
                val message = json.optString("message", "Resposta recebida")
                showToast(message)
                return
            }

            log("JSON recebido sem tratamento específico")
        } catch (e: Exception) {
            log("Erro ao processar mensagem: ${e.message}")
        }
    }

    private fun parseCompanies(companiesJson: JSONArray): List<Company> {
        val companies = mutableListOf<Company>()

        for (i in 0 until companiesJson.length()) {
            val c = companiesJson.getJSONObject(i)

            val nome = when {
                c.has("Nome") -> c.optString("Nome", "Sem nome")
                c.has("nome") -> c.optString("nome", "Sem nome")
                c.has("name") -> c.optString("name", "Sem nome")
                else -> "Sem nome"
            }

            val isActive = when {
                c.has("is_active") -> c.optInt("is_active", 0)
                c.has("active") -> if (c.optBoolean("active", false)) 1 else 0
                else -> 0
            }

            companies.add(
                Company(
                    nome = nome,
                    isActive = isActive
                )
            )
        }

        return companies
    }

    private fun requestCompanies() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                if (currentToken.isBlank()) {
                    showToast("Aguardando token do servidor...")
                    return@launch
                }

                val request = JSONObject().apply {
                    put("token", currentToken)
                    put("action", "list-companies")
                }

                sendJson(request)

            } catch (e: Exception) {
                showToast("Erro ao solicitar empresas: ${e.message}")
            }
        }
    }

    private suspend fun sendJson(json: JSONObject) {
        sendRaw(json.toString())
    }

    private suspend fun sendRaw(text: String) {
        try {
            writer?.write("$text\n")
            writer?.flush()
            log("CLIENT -> $text")
        } catch (e: Exception) {
            showToast("Falha ao enviar: ${e.message}")
        }
    }

    private suspend fun showToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(this@DashboardActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun log(message: String) {
        println("[DashboardActivity] $message")
    }

    private fun disconnect() {
        try {
            listenerJob?.cancel()
            reader?.close()
            writer?.close()
            socket?.close()
        } catch (_: Exception) {
        } finally {
            reader = null
            writer = null
            socket = null
            currentToken = ""
        }
    }

    override fun onDestroy() {
        disconnect()
        super.onDestroy()
    }
}