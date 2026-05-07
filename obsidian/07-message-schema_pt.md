# Esquema de Mensagens (JSON)

Resumo
- Comunicação via JSON sobre TCP.
- Mensagens comuns: requisição (RouteRequest) e resposta (RouteResponse).

RouteRequest (camada de rede)
- Campos observados:
  - token: string | token de sessão (opcional em login)
  - action: string | nome da ação/exemplo: "list-companies", "login"
  - login/password/code/etc: dependendo da ação, payload em campos específicos
  - data: object | payload arbitrário quando necessário

Exemplo de requisição:
{
  "token": "HudUvq",
  "action": "list-companies"
}

RouteResponse
- Campos observados:
  - success: boolean
  - message: string
  - token: string (quando resposta de login)
  - data: object | payload com resultados

Exemplo de resposta com token:
{
  "success": true,
  "message": "Autenticado",
  "token": "...",
  "data": {...}
}

Boas práticas
- Validar campo `action` antes de executar lógica.
- Verificar `token` quando ação requer autenticação; retornar erro padronizado quando ausente.
- Definir esquema mínimo por ação (campos obrigatórios/optativos).

Links: [[02-server_pt]] [[08-db-schema_pt]]
