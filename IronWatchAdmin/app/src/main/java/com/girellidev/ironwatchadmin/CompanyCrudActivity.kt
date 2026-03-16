package com.girellidev.ironwatchadmin

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CompanyCrudActivity : AppCompatActivity() {

    private lateinit var txtTituloCrud: TextView
    private lateinit var btnCriar: Button
    private lateinit var btnListar: Button
    private lateinit var btnEditar: Button
    private lateinit var btnExcluir: Button

    private var companyId: Int = -1
    private var companyName: String = "Empresa"
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_crud)

        txtTituloCrud = findViewById(R.id.txtTituloCrud)
        btnCriar = findViewById(R.id.btnCriar)
        btnListar = findViewById(R.id.btnListar)
        btnEditar = findViewById(R.id.btnEditar)
        btnExcluir = findViewById(R.id.btnExcluir)

        token = intent.getStringExtra("token") ?: ""
        companyId = intent.getIntExtra("company_id", -1)
        companyName = intent.getStringExtra("company_name") ?: "Empresa"

        if (token.isBlank() || companyId == -1) {
            Toast.makeText(this, "Dados inválidos para o CRUD", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        txtTituloCrud.text = "CRUD - $companyName"

        btnCriar.setOnClickListener {
            Toast.makeText(this, "Criar registro da empresa ID: $companyId", Toast.LENGTH_SHORT).show()
        }

        btnListar.setOnClickListener {
            Toast.makeText(this, "Listar registros da empresa ID: $companyId", Toast.LENGTH_SHORT).show()
        }

        btnEditar.setOnClickListener {
            Toast.makeText(this, "Editar registro da empresa ID: $companyId", Toast.LENGTH_SHORT).show()
        }

        btnExcluir.setOnClickListener {
            Toast.makeText(this, "Excluir registro da empresa ID: $companyId", Toast.LENGTH_SHORT).show()
        }
    }
}   