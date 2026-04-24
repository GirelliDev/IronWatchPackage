2026-04-23 - Portable Copilot CLI

O que foi feito
- Adicionado bootstrap portátil para GitHub Copilot CLI. Scripts criados no repositório raiz:
  - portable_copilot_run.bat
  - portable_copilot_bootstrap.ps1
  - portable_copilot_README.md
- Scripts baixam release Windows x64 oficial em tempo de execução e extraem para `portable_copilot/`.

Arquivos alterados/criados
- Criado: portable_copilot_run.bat
- Criado: portable_copilot_bootstrap.ps1
- Criado: portable_copilot_README.md
- Criado: obsidian/2026-04-23-portable-copilot.md (este arquivo)

Por que foi feito
- Usuário pediu package portátil que rode sem Node instalado.
- Baixar binário oficial evita necessidade de embutir Node e instalar dependências.

Como testar
1. Abrir terminal no diretório do repositório.
2. Executar: `portable_copilot_run.bat --help` ou `powershell -File .\portable_copilot_bootstrap.ps1 -- --help`
3. Na primeira execução, script baixará binário e o armazenará em `portable_copilot/`.
4. Verificar saída e executar comandos do `copilot` normalmente.

Links úteis
- https://github.com/github/copilot-cli/releases

Próximos passos
- Adicionar verificação de checksum da release antes de executar
- Criar versão cross-platform (Linux/macOS) e empacotar em zip/installer
- Se preferir variante Node-based, embutir Node e `npm install` localmente
