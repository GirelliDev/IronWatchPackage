# Como executar — Rápido

Pré-requisitos
- Java (versão compatível), Maven, Android Studio, Python 3, pip
- Instância MySQL disponível

Servidor
1. Configurar variáveis de ambiente ou copiar `.env.example` → `.env` (DB_HOST, DB_USER, DB_PASSWORD, DB_USE_SSL)
2. `cd IronWatchServer`
3. `mvn clean package`
4. `java -jar target/ironwatch-server.jar`

App Admin (Android)
1. Abrir `IronWatchAdmin` no Android Studio
2. Configurar `SERVER_HOST` / `SERVER_PORT` em buildConfig ou variáveis de ambiente
3. Executar módulo `app`

Cliente Desktop
- `pip install -r requirements.txt`
- `python IronWatchClient.py`

Notas
- Porta TCP padrão: 5555 (verificar no código)
- Usar TLS/SSH tunneling em produção; não expor DB diretamente

Links: [[02-server_pt]] [[03-admin-android_pt]] [[05-dependencies-and-tech_pt]]
