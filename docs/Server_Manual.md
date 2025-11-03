# IronWatch Servidor – Guia Técnico

## Estrutura

- `/api` → endpoints REST.
- `/database` → esquemas e migrações.
- `/modules` → notificações, agenda, WhatsApp.
- `/middlewares` → autenticação, logging, bloqueios.
- `/shared` → funções e tipos comuns.

## Instalação e Deploy

1. Configurar ambiente (Python/Node, Docker, banco de dados PostgreSQL).
2. Criar `.env` com variáveis fornecidas pelo Girelli Dev.
3. Executar: `docker-compose up -d`.
4. Verificar logs em `/server/logs`.

## Segurança e Garantia

- JWT para autenticação.
- HTTPS obrigatório.
- Bloqueio automático para clientes inadimplentes.
- Suporte e correções incluídas no contrato mensal.
