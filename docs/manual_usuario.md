# IronWatch Usuário – Manual (Resumo das Funcionalidades e Limitações)

## Status do Produto

- **Mobile (app-user-mobile):** irá fornecer visualização e _analytics_ de uso (implementação pendente).
- **Desktop (app-user-desktop):** fornecerá estatísticas completas, relatórios e avaliações de usuários (implementação pendente).
- Ambos os apps do usuário serão entregues em versões futuras conforme roadmap.

## Funcionalidades previstas (quando implementado)

- Visualização de mensagens monitoradas relacionadas ao tenant.
- Recebimento de notificações e lembretes.
- Criação de novos contatos na base do tenant (com regras de validação).
- Inserção de eventos no calendário do tenant.
- Envio de métricas básicas de uso para o proprietário (agregadas), visando manutenção e melhoria do sistema.

## Privacidade e Monitoramento

- O sistema coleta métricas e estatísticas de uso; estatísticas mais detalhadas (e avaliações) estarão disponíveis no app de PC.
- Cliente deve concordar contractualmente com coleta e envio de métricas ao proprietário.
- Dados sensíveis devem ser tratados conforme cláusulas de confidencialidade do contrato.

## Bloqueio por inadimplência

- Caso o contrato/assinatura seja suspenso ou cancelado, funcionalidades ficam bloqueadas automaticamente até regularização.
- Dados do tenant são preservados, mas acessos operacionais ficam desabilitados.

## Observações Técnicas

- Implementações mobile/desktop seguirão padrões de segurança: comunicação via TLS, tokens temporários e armazenamento local mínimo.
- Todas as features dependem de servidores e integrações (ex.: WhatsApp gateway) implementados no backend.

---

App do usuário = interface para operar dentro do escopo do tenant. Admin é separado e privado.
