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

## 🧩 Estrutura do Projeto

IronWatch/
├── /server/
│ ├── SuperAdmin.py # Servidor principal de autenticação e tokens
│ ├── UserServer.py # Servidor de usuários e comandos
│ ├── config.json # Configurações do servidor
│ └── requirements.txt
│
├── /client_admin/
│ ├── app/
│ │ ├── DashboardActivity.kt
│ │ ├── LoginActivity.kt
│ │ └── adapters/
│ └── AndroidManifest.xml
│
├── /client_user/
│ ├── app/
│ │ ├── MainActivity.kt
│ │ ├── CalendarFragment.kt
│ │ ├── ChatFragment.kt
│ │ └── models/
│ └── AndroidManifest.xml
│
├── /pc_user/
│ ├── IronWatchClient.py
│ ├── assets/
│ │ └── logo.ico
│ └── requirements.txt
│
├── /docs/
│ ├── endpoints.md
│ ├── architecture.md
│ └── changelog.md
│
└── README.md

yaml
Copiar código

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

[Admin App] ⇄ [SuperAdmin Server] ⇄ [UserServer] ⇄ [User App / PC App]

yaml
Copiar código

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
bash
Copiar código
python UserServer.py
📱 Aplicativo Android (Admin e Usuário)
Abra no Android Studio.

Configure o serverhost com o IP do servidor.

Compile e instale no dispositivo (Run > Run 'app').

💻 Aplicativo PC (Usuário)
bash
Copiar código
pip install -r requirements.txt
python IronWatchClient.py
Ou gere executável:

bash
Copiar código
pyinstaller --onefile --icon=assets/logo.ico IronWatchClient.py
🔐 Autenticação e Segurança
Tokens são gerados e renovados automaticamente pelo SuperAdmin.

Cada token tem tempo de vida (TTL) configurável.

Comunicação via JSON criptografado (AES opcional).

Logs detalhados no console e arquivo ironwatch.log.

🧾 Estrutura de Dados (JSON)
Exemplo de requisição cliente → servidor:

json
Copiar código
{
  "token": "HudUvq",
  "action": "list-companies"
}
Resposta:

json
Copiar código
{
  "success": true,
  "companies": [
    { "Nome": "TechCorp", "is_active": 1 },
    { "Nome": "MedPlus", "is_active": 0 }
  ]
}
🛠️ Compilação
Servidor → EXE (Windows)
bash
Copiar código
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

bash
Copiar código
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

yaml
Copiar código

---

Quer que eu gere também o `docs/architecture.md` explicando como os servidores e apps se comunicam em nível de evento (ex: `"new_token"`, `"list-companies"`, `"command"`, `"response"`) pra deixar o repositório completo estilo projeto de produção?






Você disse:
faz pra eu copiar e colar. tu envia e fica tudo bugado



O ChatGPT disse:
понятно, Girelli — vou te mandar o README.md completo, sem bug, já formatado, pronto pra colar no GitHub direto.
Sem emoji quebrado, sem linha torta, Markdown puro. Aqui vai:

markdown
Copiar código
# IronWatch Ecosystem
> Plataforma de gerenciamento remoto e controle empresarial desenvolvida por **GirelliDev**.  
> Um sistema modular, seguro e escalável que conecta administradores, clientes e servidores em uma única infraestrutura.

---

## Visão Geral

O **IronWatch** é um ecossistema de monitoramento e administração composto por **cinco módulos principais**, todos interligados:

1. **Servidor SuperAdmin** — núcleo central que autentica e gerencia tokens de acesso.  
2. **Servidor Normal (UserServer)** — responsável por lidar com comandos de clientes, status e dados.  
3. **Aplicativo Android Admin** — interface móvel para controle e gerenciamento de empresas e dispositivos.  
4. **Aplicativo Android Usuário** — app voltado ao cliente final, com notificações e agendamentos.  
5. **Aplicativo PC Usuário** — versão desktop simplificada para comunicação direta com o servidor.

O sistema utiliza **comunicação TCP socket**, **tokens temporários** e **validação em tempo real** entre os módulos.

---

## Estrutura do Projeto

IronWatch/
├── server/
│ ├── SuperAdmin.py # Servidor principal de autenticação e tokens
│ ├── UserServer.py # Servidor de usuários e comandos
│ ├── config.json # Configurações do servidor
│ └── requirements.txt
│
├── client_admin/
│ ├── app/
│ │ ├── DashboardActivity.kt
│ │ ├── LoginActivity.kt
│ │ └── adapters/
│ └── AndroidManifest.xml
│
├── client_user/
│ ├── app/
│ │ ├── MainActivity.kt
│ │ ├── CalendarFragment.kt
│ │ ├── ChatFragment.kt
│ │ └── models/
│ └── AndroidManifest.xml
│
├── pc_user/
│ ├── IronWatchClient.py
│ ├── assets/
│ │ └── logo.ico
│ └── requirements.txt
│
├── docs/
│ ├── endpoints.md
│ ├── architecture.md
│ └── changelog.md
│
└── README.md

yaml
Copiar código

---

## Tecnologias Utilizadas

| Componente | Tecnologia |
|-------------|-------------|
| Backend | Python 3.10+, AsyncIO, aiomysql, socket |
| Mobile (Admin) | Android (Kotlin, Retrofit, RecyclerView) |
| Mobile (Usuário) | Android (Kotlin, Jetpack) |
| Desktop (Usuário) | Python + CustomTkinter / Electron |
| Banco de Dados | MySQL |
| Comunicação | TCP + JSON |

---

## Fluxo de Comunicação

[Admin App] ⇄ [SuperAdmin Server] ⇄ [UserServer] ⇄ [User App / PC App]

yaml
Copiar código

- **SuperAdmin** gera e distribui tokens temporários.  
- **UserServer** recebe comandos autenticados e envia respostas.  
- **Aplicativos (Admin e Usuário)** se conectam via socket e trocam mensagens JSON.  

---

## Instalação e Execução

### Servidor SuperAdmin

```bash
cd server
pip install -r requirements.txt
python SuperAdmin.py
Servidor Normal (UserServer)
bash
Copiar código
python UserServer.py
Aplicativo Android (Admin e Usuário)
Abra no Android Studio.

Configure o IP do servidor em serverhost.

Compile e instale (Run > Run 'app').

Aplicativo PC (Usuário)
bash
Copiar código
pip install -r requirements.txt
python IronWatchClient.py
Gerar executável:

bash
Copiar código
pyinstaller --onefile --icon=assets/logo.ico IronWatchClient.py
Autenticação e Segurança
Tokens gerados automaticamente pelo SuperAdmin.

Tempo de vida configurável (TTL).

Comunicação via JSON criptografado (AES opcional).

Logs detalhados no console e em ironwatch.log.

Estrutura de Dados (JSON)
Exemplo de requisição cliente → servidor:

json
Copiar código
{
  "token": "HudUvq",
  "action": "list-companies"
}
Resposta:

json
Copiar código
{
  "success": true,
  "companies": [
    { "Nome": "TechCorp", "is_active": 1 },
    { "Nome": "MedPlus", "is_active": 0 }
  ]
}
Compilação
Servidor → Executável (Windows)
bash
Copiar código
pyinstaller --onefile --icon=logo.ico SuperAdmin.py
Aplicativos Android
Compilar via Gradle:

java
Copiar código
Build > Build Bundle(s) / APK(s)
Estrutura Modular
Módulo	Função
SuperAdmin	Gerencia tokens e autenticação
UserServer	Processa comandos e dados
Admin App	Interface de gerenciamento
User App	Interface do cliente
PC Client	Controle desktop

Logs e Debug
Modo debug:

bash
Copiar código
python SuperAdmin.py --debug
Exibe tokens, conexões e ações em tempo real.

Licença
Distribuído sob a licença MIT.
Desenvolvido com disciplina soviética por GirelliDev.

Contato
Autor: GirelliDev
GitHub: https://github.com/GirelliDev
Email: suporte@girellidev.dev
Local: Brasil 🇧🇷

yaml
Copiar código

---

quer que eu já monte o `docs/architecture.md` também, explicando o fluxo de mensagens entre SuperAdmin, UserServer e os apps (com exemplos reais dos pacotes JSON)? Isso complementa o repositório e deixa ele “nível documentação oficial”
