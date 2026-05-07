2026-04-23 - Portable Node embedding

O que foi feito
- Adicionado scripts para baixar e instalar Node localmente em `portable_node/`.
- Scripts:
  - portable_node_setup.bat
  - portable_node_setup.ps1
  - portable_node_run.bat
  - portable_node_README.md
- Adicionado package.json placeholder para instalar dependências locais.

Arquivos alterados/criados
- Criado: portable_node_setup.bat
- Criado: portable_node_setup.ps1
- Criado: portable_node_run.bat
- Criado: portable_node_README.md
- Criado: package.json
- Criado: obsidian/2026-04-23-portable-node.md (este arquivo)

Por que foi feito
- Usuário pediu solução portátil do Copilot CLI que funcione sem Node pré-instalado. Embutir Node localmente fornece ambiente controlado para instalar/rodar pacotes Node-based.

Como testar
1. Abrir terminal no diretório do repositório.
2. Executar: `portable_node_setup.bat` (pode exigir PowerShell/Invoke-WebRequest).
3. Se `package.json` estiver com dependências, arquivos serão instalados em `portable_node_packages`.
4. Executar: `portable_node_run.bat path\to\script.js` ou `portable_node_run.bat %USERPROFILE%\.npx\somebinary`.

Próximos passos
- Vendorizar versão específica Node + checksum
- Integrar com portable_copilot bootstrap to prefer copilot binary else use node-based implementation
- Optionally add script to install copilot-cli npm package if published
