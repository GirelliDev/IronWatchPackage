package com.girellidev.ironwatchadmin

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

class DashboardActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CompanyAdapter

    private val serverhost = "192.168.0.101"
    private val serverport = 9999

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
            finish() // ou limpar token e voltar pra login
        }

        fetchCompanies()
    }

    private fun fetchCompanies() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Socket(serverhost, serverport).use { socket ->
                    val writer = OutputStreamWriter(socket.getOutputStream())
                    val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

                    val request = JSONObject()
                    request.put("token", "Colocar_a_merda_do_token_aqui_gordao")
                    request.put("action", "list-companies")
                    writer.write(request.toString() + "\n")
                    writer.flush()

                    val response = reader.readLine()
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
}
