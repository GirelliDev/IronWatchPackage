package com.girellidev.ironwatchadmin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import kotlin.concurrent.thread

class EditCompanyActivity : AppCompatActivity() {

    private lateinit var edtCompanyName: EditText
    private lateinit var switchActive: Switch
    private lateinit var btnSalvarEmpresa: Button

    private var token: String = ""
    private var companyId: Int = -1
    private var companyName: String = ""
    private var isActive: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_company)

        edtCompanyName = findViewById(R.id.edtCompanyName)
        switchActive = findViewById(R.id.switchActive)
        btnSalvarEmpresa = findViewById(R.id.btnSalvarEmpresa)

        token = intent.getStringExtra("token") ?: ""
        companyId = intent.getIntExtra("company_id", -1)
        companyName = intent.getStringExtra("company_name") ?: ""
        isActive = intent.getIntExtra("company_is_active", 0)

        if (token.isBlank() || companyId == -1) {
            Toast.makeText(this, "Dados inválidos", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        edtCompanyName.setText(companyName)
        switchActive.isChecked = isActive == 1

        btnSalvarEmpresa.setOnClickListener {
            salvarEmpresa()
        }
    }

    private fun salvarEmpresa() {
        val novoNome = edtCompanyName.text.toString().trim()
        val novoStatus = if (switchActive.isChecked) 1 else 0

        if (novoNome.isBlank()) {
            Toast.makeText(this, "Digite o nome da empresa", Toast.LENGTH_SHORT).show()
            return
        }

        thread {
            try {
                val request = JSONObject().apply {
                    put("action", "COMPANY_UPDATE")
                    put("token", token)
                    put("company_id", companyId)
                    put("nome", novoNome)
                    put("is_active", novoStatus)
                }

                val response = TcpClient.send(request.toString())
                val json = JSONObject(response)

                runOnUiThread {
                    if (json.optBoolean("success", false)) {
                        Toast.makeText(
                            this,
                            json.optString("message", "Empresa atualizada com sucesso"),
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            json.optString("message", "Falha ao atualizar empresa"),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Erro ao salvar empresa: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}