import net from 'net'

const ADMIN_PASSWORD = 'Abaitolado ou não?' // Senha suprema ☭

const adminSessions = new Map()
let lastAdminMessage = null

export function startAdminTCP(port) {
  const server = net.createServer(socket => {
    console.log('[ADMIN] Conectado:', socket.remoteAddress)

    adminSessions.set(socket, {
      buffer: '',
      braceCount: 0,
      authenticated: false,
      lastSeen: Date.now()
    })

    socket.on('data', chunk => {
      const session = adminSessions.get(socket)
      if (!session) return

      const text = chunk.toString('utf8')

      for (const char of text) {
        if (char === '{') session.braceCount++
        if (char === '}') session.braceCount--
        session.buffer += char

        if (session.braceCount === 0 && session.buffer.trim()) {
          try {
            const msg = JSON.parse(session.buffer)

            if (msg.cmd === 'login') {
              if (msg.payload?.senha !== ADMIN_PASSWORD) {
                console.log('[ADMIN] NYET ❌ senha errada')
                socket.write(JSON.stringify({ ok: false, error: 'nyet' }) + '\n')
              } else {
                session.authenticated = true
                console.log('[ADMIN] AUTH OK ☭')
                socket.write(JSON.stringify({ ok: true }) + '\n')
              }
            } else if (!session.authenticated) {
              socket.write(JSON.stringify({ ok: false, error: 'not authenticated' }) + '\n')
            } else {
              lastAdminMessage = {
                ip: socket.remoteAddress,
                payload: msg,
                at: new Date().toISOString()
              }

              console.log('[ADMIN] COMANDO:', msg)
              socket.write(JSON.stringify({ ok: true }) + '\n')
            }
          } catch {
            socket.write(JSON.stringify({ ok: false, error: 'json inválido' }) + '\n')
          }

          session.buffer = ''
          session.braceCount = 0
        }
      }
    })

    socket.on('close', () => {
      adminSessions.delete(socket)
      console.log('[ADMIN] Desconectado')
    })
  })

  server.listen(port, () => {
    console.log(`[ADMIN] TCP aberto em ${port}`)
  })
}

// 🔴 EXPORT QUE TU ESQUECEU, ANIMAL
export function getAdminSessions() {
  return adminSessions
}

export function getLastAdminMessage() {
  return lastAdminMessage
}
