package com.girellidev.ironwatchadmin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
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
    private lateinit var btnAddEmpresa: ImageButton

    private val serverHost = "181.215.45.62"
    private val serverPort = 5555

    private var socket: Socket? = null
    private var writer: OutputStreamWriter? = null
    private var reader: BufferedReader? = null

    @Volatile
    private var currentToken: String = ""

    private var listenerJob: Job? = null
    private var isConnecting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        currentToken = intent.getStringExtra("token") ?: ""

        if (currentToken.isBlank()) {
            Toast.makeText(this, "Token inválido", Toast.LENGTH_SHORT).show()
            voltarProLogin()
            return
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        recyclerView = findViewById(R.id.recyclerView)
        btnAddEmpresa = findViewById(R.id.btnAddEmpresa)

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = CompanyAdapter { company ->
            abrirDashboardEmpresa(company)
        }

        recyclerView.adapter = adapter

        findViewById<ImageView>(R.id.menuButton).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        btnAddEmpresa.setOnClickListener {
            val intent = Intent(this, CreateCompanyActivity::class.java).apply {
                putExtra("token", currentToken)
            }
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnRefresh).setOnClickListener {
            requestCompanies()
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            getSharedPreferences("ironwatch_admin", MODE_PRIVATE)
                .edit()
                .remove("auth_token")
                .apply()

            disconnect()
            voltarProLogin()
        }

        startConnection()
    }

    private fun abrirDashboardEmpresa(company: Company) {
        val intent = Intent(this, CompanyDashboardActivity::class.java).apply {
            putExtra("token", currentToken)
            putExtra("company_id", company.id)
            putExtra("company_name", company.nome)
            putExtra("company_is_active", company.isActive)
        }
        startActivity(intent)
    }

    private fun startConnection() {
        if (isConnecting) return

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                isConnecting = true
                disconnect()

                socket = Socket(serverHost, serverPort)
                writer = OutputStreamWriter(socket!!.getOutputStream())
                reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))

                log("Conectado ao servidor")
                startListener()
                requestCompanies()

            } catch (e: Exception) {
                showToast("Falha na conexão: ${e.message}")
            } finally {
                isConnecting = false
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
            if (!trimmed.startsWith("{")) {
                log("Mensagem não tratada: $trimmed")
                return
            }

            val json = JSONObject(trimmed)

            if (json.optBoolean("success", false) && json.has("companies")) {
                val companies = parseCompanies(json.getJSONArray("companies"))
                withContext(Dispatchers.Main) {
                    adapter.setCompanies(companies)
                }
                return
            }

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

            val id = when {
                c.has("id") -> c.optInt("id", -1)
                c.has("ID") -> c.optInt("ID", -1)
                c.has("empresaId") -> c.optInt("empresaId", -1)
                c.has("empresa_id") -> c.optInt("empresa_id", -1)
                else -> -1
            }

            val nome = when {
                c.has("Nome") -> c.optString("Nome", "Sem nome")
                c.has("nome") -> c.optString("nome", "Sem nome")
                c.has("name") -> c.optString("name", "Sem nome")
                c.has("razaoSocial") -> c.optString("razaoSocial", "Sem nome")
                else -> "Sem nome"
            }

            val isActive = when {
                c.has("is_active") -> c.optInt("is_active", 0)
                c.has("isActive") -> c.optInt("isActive", 0)
                c.has("active") -> {
                    val activeValue = c.opt("active")
                    when (activeValue) {
                        is Boolean -> if (activeValue) 1 else 0
                        is Int -> activeValue
                        is String -> if (activeValue == "1" || activeValue.equals("true", true)) 1 else 0
                        else -> 0
                    }
                }
                else -> 0
            }

            companies.add(
                Company(
                    id = id,
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
                if (socket == null || socket!!.isClosed || writer == null || reader == null) {
                    showToast("Reconectando ao servidor...")
                    startConnection()
                    return@launch
                }

                if (currentToken.isBlank()) {
                    showToast("Token inválido")
                    return@launch
                }

                val request = JSONObject().apply {
                    put("token", currentToken)
                    put("action", "LIST_COMPANIES")
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

    private fun voltarProLogin() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
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
            listenerJob = null
            reader = null
            writer = null
            socket = null
        }
    }

    override fun onDestroy() {
        disconnect()
        super.onDestroy()
    }
}