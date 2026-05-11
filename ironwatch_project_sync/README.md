# IronWatch Project Sync

Este pacote adiciona um sincronizador separado para o GitHub Project 4.

Fluxo:
1. Lê commits novos do repo.
2. Detecta subissues concluídas por referência em commit, tipo `fix #123`, `closes #123`, `done #123`, ou por IA quando habilitada.
3. Move subissue para Done.
4. Fecha a issue no GitHub.
5. Reabre subissue que estiver Todo mas fechada.
6. Fecha parent issue quando todas as subissues dela estiverem Done e closed.
7. O mailer existente lê o Project já atualizado no envio das 17h.

Arquivos:
- `ironwatch_project_sync.py`
- `ironwatch-project-sync.service`
- `ironwatch-project-sync.timer`
