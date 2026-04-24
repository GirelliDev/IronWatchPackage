# Servidor — IronWatchServer

Local: `IronWatchServer/`
Arquivos principais: `pom.xml`, `iniciar.sh`, `src/`, `db/`, `testers/`.

Build
- `mvn clean package`
- Saída: `target/ironwatch-server.jar`

Execução
- `java -jar target/ironwatch-server.jar`
- Variáveis DB podem ser passadas via ambiente: `DB_HOST=... DB_USER=... java -jar ...`

Configurações e segredos
- Usar variáveis de ambiente ou `.env`. Ver `ENVIRONMENT_SETUP.md`.
- Tokens: servidor gera e valida tokens temporários; TTL configurável.

Notas de desenvolvimento
- Logs vão para console por padrão; redirecionar quando necessário.
- `db/` contém esquema SQL e tabelas principais.
- `iniciar.sh` script auxiliar para Unix-like.

Checagens rápidas
- Conferir dependências em `pom.xml` e versão Java.
- Rodar testes unitários/integrados em `src/test`.

Links: [[01-architecture_pt]] [[04-how-to-run_pt]] [[copilot-actions]]
