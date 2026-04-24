# App de Administração — IronWatchAdmin (Android)

Local: `IronWatchAdmin/app/`
Estrutura: módulo Android Gradle (`build.gradle.kts`, `src/`).

Configuração de build
- Host/porta do servidor definidos via `buildConfigField` (SERVER_HOST, SERVER_PORT).
- Pode sobrescrever com variáveis ambiente: `SERVER_HOST_BUILD`, `SERVER_PORT_BUILD`.

Execução
- Abrir projeto no Android Studio
- Executar módulo `app` (Run → Run 'app')

Notas de desenvolvimento
- Manter segredos fora do repositório; usar variantes de build ou segredos no CI.
- Ver `proguard-rules.pro` para ofuscação em release.

Links: [[01-architecture_pt]] [[04-how-to-run_pt]]
