# 🛡️ IronWatch Ecosystem

Plataforma de gerenciamento remoto e controle empresarial desenvolvida por **GirelliDev**.

O **IronWatch**, apelidado de **SageKiller** é um ecossistema modular que conecta administradores, clientes e servidores através de uma infraestrutura centralizada e segura.

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

# 🔐 Segurança

Este projeto passou por uma auditoria de segurança. Confira os detalhes em:

- **[SECURITY.md](SECURITY.md)** - Guia de boas práticas e segurança
- **[ENVIRONMENT_SETUP.md](ENVIRONMENT_SETUP.md)** - Configuração segura de variáveis de ambiente

### ✅ Implementações Recentes

- ✅ Credenciais movidas para variáveis de ambiente
- ✅ SSL/TLS habilitado no MySQL
- ✅ IPs parametrizados (não hardcoded)
- ✅ Arquivo suspeito removido do versionamento

---

## ⚙️ Configuração Inicial (IMPORTANTE!)

Antes de executar, configure as variáveis de ambiente:

```bash
# 1. Copie o arquivo de exemplo
cp .env.example .env

# 2. Edite com suas credenciais reais
nano .env

# Variáveis necessárias:
# DB_HOST=localhost
# DB_PORT=3306
# DB_USER=seu_usuario
# DB_PASSWORD=sua_senha_forte
# DB_USE_SSL=true
```

**⚠️ SEGURANÇA**: Nunca commit o arquivo `.env` com dados sensíveis. Já está em `.gitignore`.

Para mais detalhes, veja [ENVIRONMENT_SETUP.md](ENVIRONMENT_SETUP.md) e [SECURITY.md](SECURITY.md).

## Compilar

```bash
cd IronWatchServer
mvn clean package
```

## Executar

```bash
java -jar target/ironwatch-server.jar
```

Ou com variáveis específicas:

```bash
DB_HOST=db.example.com DB_USER=prod_user DB_PASSWORD=senha java -jar target/ironwatch-server.jar
```

---

# 📱 Executando o App Android

## ⚙️ Configuração Inicial (IMPORTANTE!)

1. Configure as variáveis de ambiente para o build:

```bash
export SERVER_HOST_BUILD=seu_servidor.com
export SERVER_PORT_BUILD=5555
```

2. Ou edite diretamente em `build.gradle` do módulo `app`:

```gradle
buildTypes {
    debug {
        buildConfigField "String", "SERVER_HOST", "\"192.168.1.100\""
        buildConfigField "int", "SERVER_PORT", "5555"
    }
    release {
        buildConfigField "String", "SERVER_HOST", "\"api.seu-dominio.com\""
        buildConfigField "int", "SERVER_PORT", "5555"
    }
}
```

**✅ Agora**: Host e porta são definidos em tempo de build, não mais hardcoded!

## Executar

1. Abra o projeto em **Android Studio**
2. Execute o projeto:

```
Run → Run 'app'
```

Para mais detalhes sobre configuração, veja [ENVIRONMENT_SETUP.md](ENVIRONMENT_SETUP.md).

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
