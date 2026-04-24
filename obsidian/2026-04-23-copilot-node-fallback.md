2026-04-23 - Copilot wrapper updated with Node fallback

O que foi feito
- Atualizado portable_copilot_run.bat para tentar baixar binário oficial, e se não encontrar, usar fallback baseado em Node local.
- Fallback usa `portable_node\current\node.exe` e instala `@githubnext/copilot-cli` em `portable_node_packages` quando necessário.

Arquivos alterados/criados
- Alterado: portable_copilot_run.bat
- Criado: obsidian/2026-04-23-copilot-node-fallback.md (este arquivo)

Por que foi feito
- Garantir que usuário consiga executar Copilot CLI mesmo sem binário pré-baixado nem Node global. Fallback instala pacote Node localmente usando portable Node.

Como testar
1. Se tiver copilot.exe: execute `portable_copilot_run.bat --help` e observe execução.
2. Sem copilot.exe, com portable_node instalado: execute `portable_copilot_run.bat --help`. Script instalará `@githubnext/copilot-cli` em `portable_node_packages` e tentará rodá-lo.
3. Se portable_node não instalado: execute `portable_node_setup.bat` primeiro.

Notas
- Installer requires internet on first run.
- Consider adding checksum verification for downloads.
