# IronWatch Admin – Manual Interno (Aplicativo Privado)

**Observação importante:** O Admin é um aplicativo proprietário **privado**. Não é disponibilizado aos clientes/usuários. Acesso restrito apenas a:

- Proprietário do sistema (Girelli Dev),
- Sócios autorizados,
- Pessoal de extrema confiança (доверенный — doverennyy).

## Finalidade

O Admin serve como painel de controle mestre do IronWatch, com poderes de:

- Monitorar e gerenciar todas as empresas/instâncias hospedadas no sistema.
- Acessar logs e conversas sob monitoramento (quando autorizado por contrato).
- Criar/editar/remover contatos e eventos em qualquer instância.
- Forçar bloqueio ou parada de serviços de qualquer cliente/empresa em casos autorizados.
- Visualizar estatísticas globais e executar scripts de manutenção remota.

## Acesso e Autorização

- A obtenção de acesso ao Admin requer autorização formal e assinatura de termo de responsabilidade.
- Cada credencial deve ser individual, com autenticação forte (2FA) e registro de auditoria (todas ações ficam logadas).
- Recomendado: dupla aprovação (two-person rule) para ações críticas como “parar tudo”.

## Escopo de Ação

- O Admin tem **acesso global**: ele pode operar em nível de tenant (empresa) e em nível global.
- A ação de “parar” (shutdown/lock) é irreversível sem reautorização e será aplicada conforme política interna/contrato.
- Todas as intervenções realizadas via Admin devem ser justificadas e registradas no log de auditoria.

## Segurança e Compliance

- Credenciais do Admin são confidenciais e monitoradas.
- Uso indevido do Admin resulta em revogação imediata de acesso e medidas legais.
- Operações sensíveis devem ser executadas preferencialmente em janelas de manutenção aprovadas.

## Procedimentos de Emergência

- Em caso de comprometimento ou comportamento malicioso de um tenant, o Admin pode:
  1. Aplicar contenção (isolar instância).
  2. Aplicar bloqueio temporário.
  3. Acionar procedimento de recuperação/remediação conforme plano de resposta.
- Todas as ações de emergência registradas e reportadas ao dono do sistema.

---

IronWatch Admin = ferramenta de controle total. Uso restrito, responsabilidade máxima.
