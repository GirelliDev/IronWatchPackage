# 🛡️ IronWatch Ecosystem

Plataforma de gerenciamento remoto e controle empresarial desenvolvida por **GirelliDev**.

O **IronWatch** é um ecossistema modular que conecta administradores, clientes e servidores através de uma infraestrutura centralizada e segura.

O sistema foi projetado para ser **simples, rápido e robusto**, utilizando comunicação direta via **TCP Socket** e autenticação baseada em **tokens temporários**.

---

# 🌍 Arquitetura do Sistema

O ecossistema é composto por quatro módulos principais.

### 🧠 IronWatch Server

Servidor central responsável por:

* autenticação de usuários
* geração e validação de tokens
* gerenciamento de sessões
* comunicação entre clientes e administradores
* acesso ao banco de dados

---

### 📱 IronWatch Admin (Android)

Aplicativo utilizado por administradores para:

* gerenciamento de empresas
* controle de usuários
* envio de comandos
* monitoramento em tempo real

---

### 👤 IronWatch User (Android)

Aplicativo utilizado pelo cliente final.

Funcionalidades previstas:

* notificações
* calendário
* agendamentos
* comunicação com a empresa

---

### 💻 IronWatch Desktop Client

Cliente desktop simplificado que permite:

* comunicação direta com o servidor
* execução de comandos remotos
* visualização de informações básicas

---

# ⚙️ Tecnologias Utilizadas

| Camada         | Tecnologia         |
| -------------- | ------------------ |
| Backend        | Java / TCP Socket  |
| Mobile         | Android (Kotlin)   |
| Desktop        | Python             |
| Banco de dados | MySQL              |
| Comunicação    | TCP + JSON         |
| Segurança      | Tokens temporários |

---

# 🧠 Fluxo de Comunicação

```
Admin App
     │
     │ TCP
     ▼
IronWatch Server
     │
     │ TCP
     ▼
User App / Desktop Client
```

Todos os clientes se conectam diretamente ao **IronWatch Server**, que atua como ponto central de autenticação e controle.

---

# 🔐 Autenticação

O sistema utiliza **tokens temporários de sessão**.

Fluxo básico:

1. Usuário envia login
2. Servidor valida credenciais
3. Servidor gera token de sessão
4. Cliente utiliza token nas requisições seguintes

Cada token possui:

* tempo de vida configurável (TTL)
* associação com usuário
* validação no servidor

---

# 📦 Estrutura de Comunicação (JSON)

Exemplo de requisição enviada ao servidor:

```json
{
  "token": "HudUvq",
  "action": "list-companies"
}
```

Resposta do servidor:

```json
{
  "success": true,
  "companies": [
    { "name": "TechCorp", "is_active": 1 },
    { "name": "MedPlus", "is_active": 0 }
  ]
}
```

---

# 🚀 Executando o Servidor

### Compilar

```bash
mvn clean package
```

### Executar

```bash
java -jar IronWatchServer.jar
```

---

# 📱 Executando o App Android

1. Abra o projeto no **Android Studio**
2. Configure o endereço do servidor

```kotlin
private val serverhost = "SEU_IP_DO_SERVIDOR"
```

3. Execute o projeto

```
Run → Run 'app'
```

---

# 💻 Executando o Cliente Desktop

Instalar dependências:

```bash
pip install -r requirements.txt
```

Executar:

```bash
python IronWatchClient.py
```

Gerar executável:

```bash
pyinstaller --onefile IronWatchClient.py
```

---

# 🧾 Estrutura do Projeto

```
IronWatch
│
├── IronWatchServer
│
├── IronWatchAdmin
│
├── IronWatchUser
│
└── IronWatchClient
```

---

# 🧰 Logs

Logs do servidor são exibidos no console e podem ser redirecionados para arquivo.

Exemplo:

```
[INFO] Server started
[INFO] Client connected
[INFO] Session created
```

---

# 📜 Licença

Projeto distribuído sob licença **MIT**.

---

# 👤 Autor

**GirelliDev**

GitHub
https://github.com/GirelliDev

Sistema desenvolvido no Brasil, com filosofia de engenharia inspirada em robustez e simplicidade.
