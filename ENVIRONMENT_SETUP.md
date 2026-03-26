# 🚀 Setup de Variáveis de Ambiente

Este documento explica como configurar as variáveis de ambiente após clonar/usar o repositório.

## 1️⃣ Configuração Inicial

### No diretório raiz do projeto:

```bash
# Copie o arquivo de exemplo
cp .env.example .env

# Edite com suas configurações reais
nano .env  # ou seu editor preferido
```

### Valores esperados em `.env`:

```bash
# Banco de Dados MySQL
DB_HOST=seu_host_bd              # Ex: localhost ou db.seu-dominio.com
DB_PORT=3306                     # Porta padrão MySQL
DB_NAME=gds_ironwatch            # Nome do banco
DB_USER=seu_usuario              # Usuário do BD
DB_PASSWORD=senha_super_forte    # Use uma senha forte!
DB_USE_SSL=true                  # ⚠️ IMPORTANTE: Sempre true em produção

# Servidor TCP
SERVER_HOST=seu_servidor         # Ex: localhost ou api.seu-dominio.com
SERVER_PORT=5555                 # Porta do servidor Java

# Android Build
SERVER_HOST_BUILD=seu_servidor   # Mesmo valor de SERVER_HOST
SERVER_PORT_BUILD=5555           # Mesmo valor de SERVER_PORT
```

## 2️⃣ Backend (IronWatchServer - Java)

### Opção A: Via variáveis de ambiente (recomendado)

```bash
# Exportar variáveis
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=gds_ironwatch
export DB_USER=ironwatch
export DB_PASSWORD=sua_senha_aqui
export DB_USE_SSL=true

# Executar Maven
cd IronWatchServer
mvn clean install
mvn spring-boot:run
```

### Opção B: Via arquivo .env (arquivo de configuração)

O sistema lerá automaticamente do arquivo `.env` se existir.

## 3️⃣ Android (IronWatchAdmin)

### Configurar buildTypes em `build.gradle`:

```gradle
buildTypes {
    debug {
        buildConfigField "String", "SERVER_HOST", "\"${System.getenv("SERVER_HOST_BUILD") ?: "localhost"}\""
        buildConfigField "int", "SERVER_PORT", "${System.getenv("SERVER_PORT_BUILD")?.toInteger() ?: 5555}"
    }
    release {
        buildConfigField "String", "SERVER_HOST", "\"${System.getenv("SERVER_HOST_BUILD") ?: "localhost"}\""
        buildConfigField "int", "SERVER_PORT", "${System.getenv("SERVER_PORT_BUILD")?.toInteger() ?: 5555}"
    }
}
```

Exemplo com valores reais:
```gradle
buildTypes {
    debug {
        buildConfigField "String", "SERVER_HOST", "\"192.168.1.100\""
        buildConfigField "int", "SERVER_PORT", "5555"
    }
    release {
        buildConfigField "String", "SERVER_HOST", "\"api.prod.com\""
        buildConfigField "int", "SERVER_PORT", "5555"
    }
}
```

## 4️⃣ Segurança - Importante!

### ⚠️ NÃO FAÇA:

```bash
# ❌ Nunca commita .env com dados sensíveis
git add .env
git commit -m "add .env"  # NÃO FAÇA ISSO!

# ❌ Nunca exponha credenciais em código
String password = "sua_senha";
```

### ✅ FAÇA:

```bash
# ✅ Mantenha .env apenas localmente (já está em .gitignore)
cat .env  # Você vê seu ambiente local
git status .env  # Git ignora (verificado)

# ✅ Use em produção através de:
# - Variáveis de sistema
# - Secrets manager (AWS, HashiCorp Vault)
# - Arquivo protegido sem versionamento
```

## 5️⃣ Testando a Configuração

### Backend:
```bash
cd IronWatchServer
mvn clean package
java -jar target/ironwatch-server.jar
# Deve conectar ao BD usando valores de .env
```

### Android:
```bash
cd IronWatchAdmin
./gradlew build  # Lerá SERVER_HOST_BUILD e SERVER_PORT_BUILD
# Instale no emulador/dispositivo e teste conexão
```

## 6️⃣ Deploy em Produção

### Servidor Linux:

```bash
# 1. Clone o repositório (sem .env)
git clone https://seu-repo.git
cd IronWatchPackage

# 2. Configure as variáveis de produção
export DB_HOST=prod-db.servidor.com
export DB_USER=prod_user
export DB_PASSWORD=$(aws secretsmanager get-secret-value --secret-id db-password | jq -r .SecretString)
export DB_USE_SSL=true
export SERVER_HOST=api.prod.com

# 3. Build e run
cd IronWatchServer
mvn clean package -DskipTests
java -jar target/ironwatch-server.jar

# 4. (Recomendado) Use Docker ou systemd para gerenciar o serviço
```

### Docker (opcional):

```dockerfile
FROM openjdk:11-jre-slim

ENV DB_HOST=mysql-db
ENV DB_PORT=3306
ENV DB_USER=ironwatch
ENV DB_PASSWORD=${DB_PASSWORD}
ENV DB_USE_SSL=true

COPY IronWatchServer/target/ironwatch-server.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
docker run -e DB_PASSWORD=senha_segura \
           -e DB_HOST=mysql \
           --link mysql:mysql \
           ironwatch-app
```

## 📝 Checklist

- [ ] `.env` criado a partir de `.env.example`
- [ ] `.env` adicionado ao `.gitignore` (não commitar!)
- [ ] Credenciais BD configuradas corretamente
- [ ] SSL/TLS habilitado (`DB_USE_SSL=true`)
- [ ] Server host/port configurados
- [ ] Testado conexão Backend ↔ BD
- [ ] Testado conexão Android ↔ Backend
- [ ] Em produção: secrets vêm de secure storage, não .env

## ❓ Dúvidas?

Veja `SECURITY.md` para mais informações sobre segurança.
