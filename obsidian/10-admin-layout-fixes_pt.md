# Correções de Layout — IronWatchAdmin (Android)

Data: 2026-04-23T22:37:00-03:00
Tipo: Alteração (UI/layout)
Autor: Copilot

Resumo (1 linha): Tornadas responsivas as telas de login e main, removidos widths fixos e aplicado marginStart/marginEnd para suportar diferentes tamanhos de tela.

Arquivos alterados:
- IronWatchAdmin/app/src/main/res/layout/activity_login.xml
- IronWatchAdmin/app/src/main/res/layout/activity_main.xml

O que foi mudado:
- Substituídos valores fixos de largura (ex: 360dp, 367dp, 354dp) por `match_parent` com `layout_marginStart`/`layout_marginEnd` (24dp).
- Mantidas paddings internos, elevations e estilos visuais.
- Preservado DrawerLayout width (280dp) e tamanhos de ícones, que são apropriados para nav drawer.

Motivo:
- Larguras fixas causavam cortes/overflow em telas menores e aparência estreita em telas maiores. Uso de `match_parent` + margens garante comportamento adaptativo sem reescrever layouts.

Como validar localmente:
1. Abrir projeto `IronWatchAdmin` no Android Studio.
2. Trocar device preview para vários tamanhos (Pixel 4, Nexus 5, Tablet) e checar telas `activity_login` e `activity_main`.
3. Rodar: Gradle -> assembleDebug (ou executar app em emulador físico). Verificar que elementos não ultrapassam bordas e que scroll funciona em telas pequenas.

Comandos sugeridos (não executados):
- ./gradlew :IronWatchAdmin:app:assembleDebug
- ./gradlew :IronWatchAdmin:app:lint

Impacto:
- UI responsiva em diferentes dispositivos.
- Baixo risco funcional (mudança apenas em XML de layout).

Rollback:
- Reverter alterações nos arquivos XML ou restaurar commit anterior.

Próximos passos:
- Executar lint/assemble no CI.
- Revisar outros layouts com dimensões fixas (ex: activity_dashboard, activity_company_crud) e padronizar margens/dimensões via dimens.xml.
- Criar dimens.xml com valores escaláveis (spacing_small/medium/large) para consistência.

Tags: #ui #android #layout #fix
