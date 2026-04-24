# Esquema do Banco (resumo)

Arquivo fonte: `IronWatchServer/db/IronWatchServer.sql`

Tabelas principais (resumo):
- empresas: dados da empresa, chave_api, limite de dispositivos, is_active
- usuarios: referência empresa, login, password_hash, session_token, role, active
- sessoes_admin: sessões com token, expira_em, ativo
- codigos: códigos de convite/registro, expira_em, usado
- consultas: agendamentos/consultas vinculadas à empresa
- dispositivos: dispositivos registrados por empresa
- ultima_mensagem_usuario: registro da última mensagem por empresa/numero
- empresa_ai_config: configuração de AI por empresa (provider, api_key_encrypted)
- chat_message: histórico de mensagens de chat (role, content)

Observações
- Chaves estrangeiras com ON DELETE CASCADE onde aplicável.
- Campos de data com DEFAULT CURRENT_TIMESTAMP.
- `session_token` em `usuarios` existe mas há também `sessoes_admin` tabelado; prefer usar `sessoes_admin` para rastrear sessões ativas.

Links: [[02-server_pt]] [[07-message-schema_pt]]
