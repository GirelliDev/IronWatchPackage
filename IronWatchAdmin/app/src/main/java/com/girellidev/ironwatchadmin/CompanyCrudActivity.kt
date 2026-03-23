package com.girellidev.ironwatchadmin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import kotlin.concurrent.thread

class CompanyCrudActivity : AppCompatActivity() {

    private lateinit var btnEditarEmpresa: Button
    private lateinit var btnGerarCodigo: Button
    private lateinit var btnToggleAtiva: Button
    private lateinit var btnApagarEmpresa: Button

    private var companyId: Int = -1
    private var companyName: String = "Empresa"
    private var token: String = ""
    private var isActive: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_crud)

        btnEditarEmpresa = findViewById(R.id.btnEditarEmpresa)
        btnGerarCodigo = findViewById(R.id.btnGerarCodigo)
        btnToggleAtiva = findViewById(R.id.btnToggleAtiva)
        btnApagarEmpresa = findViewById(R.id.btnApagarEmpresa)

        token = intent.getStringExtra("token") ?: ""
        companyId = intent.getIntExtra("company_id", -1)
        companyName = intent.getStringExtra("company_name") ?: "Empresa"
        isActive = intent.getIntExtra("company_is_active", 0)

        if (token.isBlank() || companyId == -1) {
            Toast.makeText(this, "Dados inválidos para o CRUD", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        atualizarTextoBotaoStatus()

        btnEditarEmpresa.setOnClickListener {
            val intent = Intent(this, EditCompanyActivity::class.java).apply {
                putExtra("token", token)
                putExtra("company_id", companyId)
                putExtra("company_name", companyName)
                putExtra("company_is_active", isActive)
            }
            startActivity(intent)
        }

        btnGerarCodigo.setOnClickListener {
            gerarCodigoEmpresa()
        }

        btnToggleAtiva.setOnClickListener {
            confirmarToggleEmpresa()
        }

        btnApagarEmpresa.setOnClickListener {
            confirmarApagarEmpresa()
        }
    }

    private fun atualizarTextoBotaoStatus() {
        btnToggleAtiva.text = if (isActive == 1) {
            "Desativar Empresa"
        } else {
            "Ativar Empresa"
        }
    }

    private fun gerarCodigoEmpresa() {
        thread {
            try {
                val request = JSONObject().apply {
                    put("action", "COMPANY_GENERATE_CODE")
                    put("token", token)
                    put("company_id", companyId)
                }

                val response = TcpClient.send(request.toString())
                val json = JSONObject(response)

                runOnUiThread {
                    if (json.optBoolean("success", false)) {
                        val data = json.optJSONObject("data")
                        val codigo = data?.optString("code", "N/A") ?: "N/A"
                        val validade = data?.optString("expires_at", "N/A") ?: "N/A"
                        val empresa = data?.optString("company_name", companyName) ?: companyName

                        AlertDialog.Builder(this)
                            .setTitle("Código Gerado")
                            .setMessage(
                                "Empresa: $empresa\n\n" +
                                "Código: $codigo\n\n" +
                                "Validade: $validade"
                            )
                            .setPositiveButton("OK", null)
                            .show()
                    } else {
                        Toast.makeText(
                            this,
                            json.optString("message", "Falha ao gerar código"),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Erro ao gerar código: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun confirmarToggleEmpresa() {
        val acao = if (isActive == 1) "desativar" else "ativar"

        AlertDialog.Builder(this)
            .setTitle("${acao.replaceFirstChar { it.uppercase() }} empresa")
            .setMessage("Deseja realmente $acao a empresa \"$companyName\"?")
            .setPositiveButton("Sim") { _, _ ->
                toggleEmpresa()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun toggleEmpresa() {
        thread {
            try {
                val novoStatus = if (isActive == 1) 0 else 1

                val request = JSONObject().apply {
                    put("action", "COMPANY_SET_ACTIVE")
                    put("token", token)
                    put("company_id", companyId)
                    put("is_active", novoStatus)
                }

                val response = TcpClient.send(request.toString())
                val json = JSONObject(response)

                runOnUiThread {
                    if (json.optBoolean("success", false)) {
                        isActive = novoStatus
                        atualizarTextoBotaoStatus()
                        Toast.makeText(
                            this,
                            json.optString("message", "Status atualizado"),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this,
                            json.optString("message", "Falha ao atualizar status"),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Erro ao atualizar status: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun confirmarApagarEmpresa() {
        AlertDialog.Builder(this)
            .setTitle("Apagar empresa")
            .setMessage("Deseja realmente apagar a empresa \"$companyName\"?\n\nEssa ação não deve ser desfeita.")
            .setPositiveButton("Apagar") { _, _ ->
                apagarEmpresa()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun apagarEmpresa() {
        thread {
            try {
                val request = JSONObject().apply {
                    put("action", "COMPANY_DELETE")
                    put("token", token)
                    put("company_id", companyId)
                }

                val response = TcpClient.send(request.toString())
                val json = JSONObject(response)

                runOnUiThread {
                    if (json.optBoolean("success", false)) {
                        Toast.makeText(
                            this,
                            json.optString("message", "Empresa apagada com sucesso"),
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            json.optString("message", "Falha ao apagar empresa"),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Erro ao apagar empresa: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}