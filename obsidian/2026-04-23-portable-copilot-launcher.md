2026-04-23 - Portable Copilot launcher added

O que foi feito
- Criado: portable_copilot_launcher.bat. Mais robusto: tenta baixar binário oficial, se falhar tenta fallback baseado em Node local (portable_node).
- Atualizado: portable_copilot_README.md para documentar novo launcher.

Como testar
- Executar: `portable_copilot_launcher.bat --help`
- Cenários:
  - Com copilot.exe em portable_copilot/: executa binário
  - Sem copilot.exe mas com portable_node/: instala @githubnext/copilot-cli em portable_node_packages e o executa
  - Sem portable_node/: instrução para rodar portable_node_setup.bat

Próximos passos
- Substituir portable_copilot_run.bat automaticamente ou remover legado quando confirmado.
- Adicionar checksums para downloads.
