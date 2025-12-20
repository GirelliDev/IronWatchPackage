import net from 'net'

const adminSessions = new Map()
// key: socket
// value: { data }

export function startAdminTCP(port) {
  const server = net.createServer(socket => {
    console.log('[ADMIN] Conectado:', socket.remoteAddress)

    adminSessions.set(socket, {
      data: null
    })

    socket.on('data', raw => {
      try {
        const msg = JSON.parse(raw.toString())

        // só guarda
        adminSessions.get(socket).data = msg
      } catch {
        socket.write(JSON.stringify({ error: 'JSON inválido' }))
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

// 🔴 ponto crucial
export function getAdminSessions() {
  return adminSessions
}
