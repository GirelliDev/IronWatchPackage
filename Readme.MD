# 🛡️ IronWatch Ecosystem
> Plataforma de gerenciamento remoto e controle empresarial desenvolvida por **GirelliDev**.  
> Um sistema modular, seguro e escalável que conecta administradores, clientes e servidores em uma única infraestrutura.

---

## 🌍 Visão Geral

O **IronWatch** é um ecossistema de monitoramento e administração composto por **cinco módulos principais**, todos interligados:

1. 🧠 **Servidor SuperAdmin** — núcleo central que autentica e gerencia tokens de acesso.  
2. ⚙️ **Servidor Principal (UserServer)** — responsável por lidar com comandos de clientes, status e dados.  
3. 📱 **Aplicativo Android Admin** — interface de controle e gerenciamento móvel para administradores.  
4. 👤 **Aplicativo Android Usuário** — app voltado ao cliente final, com notificações, calendário e agendamentos.  
5. 💻 **Aplicativo PC Usuário** — versão desktop simplificada para controle e comunicação direta com o servidor.

O sistema utiliza **comunicação socket TCP**, **tokens temporários** e **validação em tempo real** entre os módulos.

---


## ⚙️ Tecnologias Utilizadas

| Componente | Tecnologia |
|-------------|-------------|
| Backend | Python 3.10+, AsyncIO, aiomysql, socket |
| Mobile (Admin) | Android (Kotlin, Retrofit, RecyclerView) |
| Mobile (Usuário) | Android (Kotlin, Jetpack) |
| Desktop (Usuário) | Python + CustomTkinter / Electron (dependendo da versão) |
| Banco de Dados | MySQL |
| Comunicação | TCP + JSON |

---

## 🧠 Fluxo de Comunicação

[Admin App] ⇄ [handler] ⇄ [SuperAdmin Server] ⇄ [Main Server] ⇄ [handler] ⇄ [User App / PC App]

- **SuperAdmin** gera e distribui tokens temporários para cada conexão.  
- **UserServer** recebe comandos autenticados e envia atualizações em tempo real.  
- **Aplicativos (Admin/Usuário)** se conectam via socket, trocando mensagens em formato JSON.  

---

## 🚀 Instalação e Execução

### 🧠 Servidor SuperAdmin
```bash
cd server
pip install -r requirements.txt
python SuperAdmin.py

⚙️ Servidor Normal (UserServer)
python UserServer.py

📱 Aplicativo Android (Admin e Usuário)

Abra no Android Studio.

Configure o serverhost com o IP do servidor.

Compile e instale no dispositivo (Run > Run 'app').

💻 Aplicativo PC (Usuário)
pip install -r requirements.txt
python IronWatchClient.py


Ou gere executável:

pyinstaller --onefile --icon=assets/logo.ico IronWatchClient.py

🔐 Autenticação e Segurança

Tokens são gerados e renovados automaticamente pelo SuperAdmin.

Cada token tem tempo de vida (TTL) configurável.

Comunicação via JSON criptografado (AES opcional).

Logs detalhados no console e arquivo ironwatch.log.

🧾 Estrutura de Dados (JSON)

Exemplo de requisição cliente → servidor:

{
  "token": "HudUvq",
  "action": "list-companies"
}


Resposta:

{
  "success": true,
  "companies": [
    { "Nome": "TechCorp", "is_active": 1 },
    { "Nome": "MedPlus", "is_active": 0 }
  ]
}

🛠️ Compilação
Servidor → EXE (Windows)
pyinstaller --onefile --icon=logo.ico SuperAdmin.py

Aplicativo Android

Build padrão via Gradle (Build > Build Bundle(s) / APK(s)).

🧩 Estrutura Modular
Módulo	Função
SuperAdmin	Gerencia tokens e autenticações
UserServer	Lida com comandos de clientes e dados
Admin App	Interface de gerenciamento
User App	Interface do cliente final
PC Client	Controle desktop do sistema
🧰 Logs e Debug

Modo de debug ativado com:

python SuperAdmin.py --debug


Mostra tokens, conexões e falhas em tempo real.

📜 Licença

Distribuído sob a licença MIT.
Criado com ✊ disciplina soviética por GirelliDev.

📞 Contato

GirelliDev
🌐 GitHub: https://github.com/GirelliDev

📧 Email: suporte@girellidev.dev

🇧🇷 Sistema desenvolvido no Brasil, com alma russa.
