package com.example.ironwatchuser

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        val campoUsuario = findViewById<EditText>(R.id.campoUsuario)
        val campoSenha = findViewById<EditText>(R.id.campoSenha)
        val botaoLogin = findViewById<Button>(R.id.botaoLogin)
        val textoErro = findViewById<TextView>(R.id.textoErro)

        botaoLogin.setOnClickListener {

            val usuario = campoUsuario.text.toString().trim()
            val senha = campoSenha.text.toString().trim()

            textoErro.text = ""

            // =========================
            // TRATAMENTO DE ERROS
            // =========================

            if (usuario.isEmpty()) {
                campoUsuario.error = "Digite o usuário"
                campoUsuario.requestFocus()
                return@setOnClickListener
            }

            if (senha.isEmpty()) {
                campoSenha.error = "Digite a senha"
                campoSenha.requestFocus()
                return@setOnClickListener
            }

            if (senha.length < 3) {
                campoSenha.error = "Senha muito curta"
                campoSenha.requestFocus()
                return@setOnClickListener
            }

            // =========================
            // LOGIN TEMPORÁRIO
            // =========================

            val usuarioCorreto = "teste"
            val senhaCorreta = "123"

            if (usuario == usuarioCorreto && senha == senhaCorreta) {

                Toast.makeText(
                    this,
                    "Login realizado com sucesso!",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                textoErro.text = "Usuário ou senha incorretos"

            }
        }
    }
}