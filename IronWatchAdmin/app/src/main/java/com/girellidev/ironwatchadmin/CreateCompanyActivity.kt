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
    private lateinit var switchActive: Switch
    private lateinit var btnCriarEmpresa: Button

    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_company)

        edtCompanyName = findViewById(R.id.edtCompanyName)
        switchActive = findViewById(R.id.switchActive)
        btnCriarEmpresa = findViewById(R.id.btnCriarEmpresa)

        token = intent.getStringExtra("token") ?: ""

        if (token.isBlank()) {
            Toast.makeText(this, "Token inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        btnCriarEmpresa.setOnClickListener {
            criarEmpresa()
        }
    }

    private fun criarEmpresa() {
        val nome = edtCompanyName.text.toString().trim()
        val isActive = if (switchActive.isChecked) 1 else 0

        if (nome.isBlank()) {
            Toast.makeText(this, "Digite o nome da empresa", Toast.LENGTH_SHORT).show()
            return
        }

        thread {
            try {
                val request = JSONObject().apply {
                    put("action", "COMPANY_CREATE")
                    put("token", token)
                    put("nome", nome)
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
                    Toast.makeText(this, "Erro ao criar empresa: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}