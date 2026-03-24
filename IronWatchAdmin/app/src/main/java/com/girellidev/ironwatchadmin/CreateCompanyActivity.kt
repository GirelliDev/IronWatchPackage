package com.girellidev.ironwatchadmin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import kotlin.concurrent.thread

class CreateCompanyActivity : AppCompatActivity() {

    private lateinit var edtCompanyName: EditText
    private lateinit var edtRazaoSocial: EditText
    private lateinit var edtTelefone: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtEndereco: EditText
    private lateinit var edtDispositivosMax: EditText
    private lateinit var edtApiKey: EditText
    private lateinit var edtPromptIA: EditText
    private lateinit var switchActive: Switch
    private lateinit var btnSalvarEmpresa: Button

    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_company)

        edtCompanyName = findViewById(R.id.edtCompanyName)
        edtRazaoSocial = findViewById(R.id.edtRazaoSocial)
        edtTelefone = findViewById(R.id.edtTelefone)
        edtEmail = findViewById(R.id.edtEmail)
        edtEndereco = findViewById(R.id.edtEndereco)
        edtDispositivosMax = findViewById(R.id.edtDispositivosMax)
        edtApiKey = findViewById(R.id.edtApiKey)
        edtPromptIA = findViewById(R.id.edtPromptIA)
        switchActive = findViewById(R.id.switchActive)
        btnSalvarEmpresa = findViewById(R.id.btnSalvarEmpresa)

        token = intent.getStringExtra("token") ?: ""

        if (token.isBlank()) {
            Toast.makeText(this, "Token inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        btnSalvarEmpresa.setOnClickListener {
            salvarEmpresa()
        }
    }

    private fun salvarEmpresa() {
        val nome = edtCompanyName.text.toString().trim()
        val razaoSocial = edtRazaoSocial.text.toString().trim()
        val telefone = edtTelefone.text.toString().trim()
        val email = edtEmail.text.toString().trim()
        val endereco = edtEndereco.text.toString().trim()
        val dispositivosMaxTexto = edtDispositivosMax.text.toString().trim()
        val apiKey = edtApiKey.text.toString().trim()
        val promptIA = edtPromptIA.text.toString().trim()
        val isActive = if (switchActive.isChecked) 1 else 0

        if (nome.isBlank()) {
            Toast.makeText(this, "Digite o nome da empresa", Toast.LENGTH_SHORT).show()
            return
        }

        if (razaoSocial.isBlank()) {
            Toast.makeText(this, "Digite a razão social", Toast.LENGTH_SHORT).show()
            return
        }

        if (telefone.isBlank()) {
            Toast.makeText(this, "Digite o telefone", Toast.LENGTH_SHORT).show()
            return
        }

        if (email.isBlank()) {
            Toast.makeText(this, "Digite o email", Toast.LENGTH_SHORT).show()
            return
        }

        if (endereco.isBlank()) {
            Toast.makeText(this, "Digite o endereço", Toast.LENGTH_SHORT).show()
            return
        }

        if (dispositivosMaxTexto.isBlank()) {
            Toast.makeText(this, "Digite o máximo de dispositivos", Toast.LENGTH_SHORT).show()
            return
        }

        val dispositivosMax = dispositivosMaxTexto.toIntOrNull()
        if (dispositivosMax == null || dispositivosMax <= 0) {
            Toast.makeText(this, "Máximo de dispositivos inválido", Toast.LENGTH_SHORT).show()
            return
        }

        if (apiKey.isBlank()) {
            Toast.makeText(this, "Digite a chave API", Toast.LENGTH_SHORT).show()
            return
        }

        thread {
            try {
                val request = JSONObject().apply {
                    put("action", "COMPANY_CREATE")
                    put("token", token)
                    put("nome", nome)
                    put("razaosocial", razaoSocial)
                    put("telefone", telefone)
                    put("email", email)
                    put("endereco", endereco)
                    put("dispositivos_max", dispositivosMax)
                    put("chave_api", apiKey)
                    put("promptia", promptIA)
                    put("is_active", isActive)
                }

                val response = TcpClient.send(request.toString())
                val json = JSONObject(response)

                runOnUiThread {
                    if (json.optBoolean("success", false)) {
                        Toast.makeText(
                            this,
                            json.optString("message", "Empresa criada com sucesso"),
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            json.optString("message", "Falha ao criar empresa"),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Erro ao criar empresa: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}