Portable Copilot CLI bootstrap

Objetivo:
- Permitir executar GitHub Copilot CLI sem Node instalado globalmente.
- Script baixa release Windows x64 oficial e executa.

Arquivos:
- portable_copilot_run.bat  -> legacy wrapper for Windows (kept for backward compatibility)
- portable_copilot_launcher.bat -> improved launcher: download binary then fallback to Node-based copilot using portable_node
- portable_copilot_bootstrap.ps1 -> PowerShell wrapper
- diretório criado no primeiro uso: portable_copilot/ (contém binário copilot.exe)

Uso:
- Windows cmd: double-click portable_copilot_run.bat ou `portable_copilot_run.bat <args>`
- PowerShell: `.\portable_copilot_bootstrap.ps1 -- <args>`

Notas:
- Requer internet no primeiro execução para baixar release.
- Para plataformas não-Windows, adapte script ou usar binários correspondentes.

Próximos passos:
- Adicionar assinatura/verificação de checksum do binário
- Optional: embutir Node/npm e instalar pacote npm se preferir versão Node-based
