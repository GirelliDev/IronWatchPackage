# 🔒 Guia de Segurança - IronWatchPackage

## Configuração de Credenciais

### ❌ NÃO FAÇA
```java
// ❌ NUNCA hardcode credenciais
String password = "DNMQTDC";
String ipServer = "181.215.45.62";
```

### ✅ FAÇA
```java
// ✅ Use variáveis de ambiente
String password = System.getenv("DB_PASSWORD");
String ipServer = System.getenv("SERVER_HOST");
```

## Setup para Desenvolvimento

### 1. Configure as variáveis de ambiente
```bash
cp .env.example .env
# Edite .env com seus valores reais
nano .env
```

### 2. Backend (Java)
```bash
cd IronWatchServer
# As credenciais serão lidas de .env
mvn clean install
mvn spring-boot:run
```

### 3. Android
Defina em `build.gradle`:
```gradle
buildTypes {
    debug {
        buildConfigField "String", "SERVER_HOST", "\"${System.getenv("SERVER_HOST_BUILD") ?: "localhost"}\""
        buildConfigField "Integer", "SERVER_PORT", "${System.getenv("SERVER_PORT_BUILD") ?: "5555"}"
    }
}
```

## Banco de Dados

### SSL/TLS Habilitado
- Configuração: `DB_USE_SSL=true` (padrão)
- Certificado: Configure com `javax.net.ssl.trustStore`
- Permissões: `allowPublicKeyRetrieval=false` (default)

### Variáveis necessárias
```
DB_HOST=localhost
DB_PORT=3306
DB_NAME=gds_ironwatch
DB_USER=ironwatch
DB_PASSWORD=<senha_forte>
DB_USE_SSL=true
```

## Comunicação TCP

⚠️ **TODO**: Implementar TLS para comunicação TCP Cliente-Servidor
- Usar `SSLSocket` em vez de `Socket`
- Gerar certificados auto-assinados para desenvolvimento
- Usar certificados válidos em produção

## Deploy em Produção

1. **Credenciais**
   - Use gerenciador de secrets (AWS Secrets Manager, HashiCorp Vault, etc)
   - Nunca commit `.env` em produção
   - Rotate credenciais regularmente

2. **SSL/TLS**
   - Certificados validados (Let's Encrypt, DigiCert, etc)
   - HTTPS/TLS obrigatório
   - HSTS headers

3. **Logging**
   - Use framework de logging (SLF4J/Log4j)
   - Não registre senhas ou tokens
   - Armazene logs com segurança

4. **Firewall**
   - Restrinja acesso ao porta TCP 5555 apenas de IPs autorizados
   - Use VPN ou SSH tunneling para acesso remoto

## Checklist de Segurança

- [x] Credenciais movidas para variáveis de ambiente
- [x] Arquivo com possível token removido
- [x] SSL/TLS habilitado no MySQL
- [x] IPs parametrizados (não hardcoded)
- [ ] TLS implementado para comunicação TCP
- [ ] Framework de logging implementado
- [ ] Testes de segurança (OWASP Top 10)
- [ ] Code review realizado
- [ ] Dependências auditadas (npm audit, OWASP dependency-check)

## Contato

Para questões de segurança, abra uma issue privada ou entre em contato através de security@ironwatch.dev
