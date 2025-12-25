import { getAdminSessions, getLastAdminMessage } from './admin.tcp.js'

export function startAdminMonitor() {
  setInterval(() => {
    const sessions = getAdminSessions()

    console.log('[MONITOR] Sessões ativas:', sessions.size)
    console.log('=============================')
    console.log('ÚLTIMO JSON RECEBIDO:')
    console.dir(getLastAdminMessage(), { depth: null })
    console.log('=============================')
  }, 3000)
}
