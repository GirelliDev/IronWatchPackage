import { getAdminSessions, getLastAdminMessage } from './admin.tcp.js'

let lastSessionCount = null
let lastMessageHash = null

function hash(obj) {
  return JSON.stringify(obj)
}

export function startAdminMonitor() {
  setInterval(() => {
    const sessions = getAdminSessions()
    const lastMessage = getLastAdminMessage()

    // sessões mudaram
    if (sessions.size !== lastSessionCount) {
      console.log(`[MONITOR] Sessões ativas: ${sessions.size}`)
      lastSessionCount = sessions.size
    }

    // mensagem mudou
    const currentHash = lastMessage ? hash(lastMessage) : null
    if (currentHash && currentHash !== lastMessageHash) {
      console.log('[MONITOR] Novo JSON recebido:')
      console.dir(lastMessage, { depth: null })
      lastMessageHash = currentHash
    }
  }, 3000)
}
