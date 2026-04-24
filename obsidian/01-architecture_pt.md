# Arquitetura — IronWatch

Resumo:
- Ecossistema modular: Servidor (central), Admin (Android), Usuário (Android), Cliente Desktop (Python).
- Comunicação: Sockets TCP com payload JSON.
- Autenticação: tokens de sessão curtos (TTL configurável) validados pelo servidor.
- Banco de dados: MySQL central.

Componentes:
- IronWatchServer — autenticação, gerenciamento de sessões, roteamento entre clientes.
- IronWatchAdmin — app Android para administradores (gerenciar empresas, usuários, comandos).
- IronWatchUser — app Android para usuários finais (notificações, calendário, agendamentos).
- IronWatchClient — cliente desktop em Python para comandos diretos.

Fluxo:
Admin ↔ TCP ↔ Server ↔ TCP ↔ Usuário/Desktop

Locais de código:
- Server: `IronWatchServer/` (pom.xml, src/, iniciar.sh)
- Admin: `IronWatchAdmin/app/`
- App mobile raiz: `app/`
- Cliente: script Python referenciado no README

Links: [[02-server_pt]] [[03-admin-android_pt]] [[04-how-to-run_pt]]
