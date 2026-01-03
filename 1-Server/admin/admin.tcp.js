import net from 'net'
import {
  generateRotatingCode,
  consumeCode,
  validateToken
} from './admin.auth.js'

const adminSessions = new Map()
let lastAdminMessage = null

export function startAdminTCP(port) {
  generateRotatingCode()

  const server = net.createServer(socket => {
    const ip = socket.remoteAddress
    console.log('[ADMIN] Conectado:', ip)

    adminSessions.set(socket, {
      buffer: '',
      braceCount: 0,
      authenticated: false
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

            // LOGIN COM CÓDIGO
            if (msg.cmd === 'login') {
              const token = consumeCode(msg.payload?.code, ip)

              if (!token) {
                console.log('[AUTH] Tentativa inválida de', ip)
                socket.write(JSON.stringify({ ok: false }) + '\n')
              } else {
                session.authenticated = true
                session.token = token

                console.log('[AUTH] Sessão criada', ip)
                socket.write(JSON.stringify({ ok: true, token }) + '\n')
              }
            }

            // COMANDO NORMAL
            else {
              if (!validateToken(msg.token, ip)) {
                socket.write(JSON.stringify({ ok: false }) + '\n')
                return
              }

              lastAdminMessage = {
                ip,
                payload: msg,
                at: new Date().toISOString()
              }

              socket.write(JSON.stringify({ ok: true }) + '\n')
            }
          } catch {
            socket.write(JSON.stringify({ ok: false }) + '\n')
          }

          session.buffer = ''
          session.braceCount = 0
        }
      }
    })

    socket.on('close', () => {
      adminSessions.delete(socket)
      console.log('[ADMIN] Desconectado:', ip)
    })
  })

  server.listen(port, () => {
    console.log(`[ADMIN] TCP aberto em ${port}`)
  })
}

export function getAdminSessions() {
  return adminSessions
}

export function getLastAdminMessage() {
  return lastAdminMessage
}
