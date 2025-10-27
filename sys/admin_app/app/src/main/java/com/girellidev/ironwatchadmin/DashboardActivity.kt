package com.girellidev.ironwatchadmin

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.*
import java.net.Socket

class DashboardActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CompanyAdapter

    private val serverHost = "181.215.45.46"
    private val serverPort = 9999

    // conexão persistente
    private var socket: Socket? = null
    private var writer: OutputStreamWriter? = null
    private var reader: BufferedReader? = null

    // token atualizado pelo túnel
    @Volatile private var currentToken: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        drawerLayout = findViewById(R.id.drawer_layout)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CompanyAdapter()
        recyclerView.adapter = adapter

        findViewById<ImageView>(R.id.menuButton).setOnClickListener {
            drawerLayout.open()
        }

        findViewById<Button>(R.id.btnRefresh).setOnClickListener {
            fetchCompanies()
        }

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            socket?.close()
            finish()
        }

        // inicia o túnel
        startConnection()
    }

    private fun startConnection() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                socket = Socket(serverHost, serverPort)
                writer = OutputStreamWriter(socket!!.getOutputStream())
                reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))

                println("[CLIENTE] Conectado ao servidor")

                // inicia listener do túnel
                launch {
                    listenForServerMessages()
                }

                // pede o token inicial logo ao conectar
                sendRaw(JSONObject().apply {
                    put("action", "new-token")
                }.toString())

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DashboardActivity, "Falha na conexão: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private suspend fun listenForServerMessages() {
        try {
            while (true) {
                val line = reader?.readLine() ?: break

                // verifica se é token enviado pelo túnel
                if (line.startsWith("TOKEN:")) {
                    val novoToken = line.removePrefix("TOKEN:").trim()
                    currentToken = novoToken
                    println("[TOKEN] Atualizado: $currentToken")
                    continue
                }

                // outras respostas (JSON)
                if (line.trim().startsWith("{")) {
                    println("[SERVER] $line")
                }
            }
        } catch (e: Exception) {
            println("[ERRO] Listener túnel: ${e.message}")
        }
    }

    private fun sendRaw(text: String) {
        try {
            writer?.write(text + "\n")
            writer?.flush()
        } catch (e: Exception) {
            println("[ERRO] Falha ao enviar: ${e.message}")
        }
    }

    private fun fetchCompanies() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (currentToken.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@DashboardActivity,
                            "Aguardando token do servidor...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return@launch
                }

                val request = JSONObject().apply {
                    put("token", currentToken)
                    put("action", "list-companies")
                }

                sendRaw(request.toString())

                val response = reader?.readLine() ?: return@launch
                val json = JSONObject(response)

                if (json.getBoolean("success")) {
                    val companiesJson = json.getJSONArray("companies")
                    val companies = mutableListOf<Company>()
                    for (i in 0 until companiesJson.length()) {
                        val c = companiesJson.getJSONObject(i)
                        companies.add(Company(c.getString("Nome"), c.getInt("is_active")))
                    }

                    withContext(Dispatchers.Main) {
                        adapter.setCompanies(companies)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@DashboardActivity,
                            "Erro: ${json.getString("message")}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@DashboardActivity,
                        "Erro ao conectar: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        socket?.close()
    }
}
