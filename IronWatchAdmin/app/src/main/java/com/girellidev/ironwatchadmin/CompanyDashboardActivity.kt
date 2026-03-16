package com.girellidev.ironwatchadmin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CompanyDashboardActivity : AppCompatActivity() {

    private lateinit var txtTituloEmpresa: TextView
    private lateinit var txtRequestsCount: TextView
    private lateinit var txtConsultasCount: TextView
    private lateinit var txtSalvosCount: TextView
    private lateinit var btnVerCrud: Button

    private var companyId: Int = -1
    private var companyName: String = "Empresa"
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_dashboard)

        txtTituloEmpresa = findViewById(R.id.txtTituloEmpresa)
        txtRequestsCount = findViewById(R.id.txtRequestsCount)
        txtConsultasCount = findViewById(R.id.txtConsultasCount)
        txtSalvosCount = findViewById(R.id.txtSalvosCount)
        btnVerCrud = findViewById(R.id.btnVerCrud)

        token = intent.getStringExtra("token") ?: ""
        companyId = intent.getIntExtra("company_id", -1)
        companyName = intent.getStringExtra("company_name") ?: "Empresa"

        if (companyId == -1 || token.isBlank()) {
            Toast.makeText(this, "Dados da empresa inválidos", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        txtTituloEmpresa.text = "Dashboard - $companyName"

        carregarDadosDashboard()

        btnVerCrud.setOnClickListener {
            val intent = Intent(this, CompanyCrudActivity::class.java).apply {
                putExtra("token", token)
                putExtra("company_id", companyId)
                putExtra("company_name", companyName)
            }
            startActivity(intent)
        }
    }

    private fun carregarDadosDashboard() {
        txtRequestsCount.text = "128"
        txtConsultasCount.text = "42"
        txtSalvosCount.text = "315"
    }
}