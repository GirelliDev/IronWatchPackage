import crypto from 'crypto'

const CODE_TTL = 60 * 1000        // 1 minuto
const TOKEN_TTL = 2 * 60 * 60 * 1000 // 2 horas

let currentCode = null
let codeExpiresAt = 0

const tokens = new Map()
// tokenHash -> { expiresAt, ip }

function randomCode() {
  return Math.floor(100000 + Math.random() * 900000).toString()
}

function hash(value) {
  return crypto.createHash('sha256').update(value).digest('hex')
}

export function generateRotatingCode() {
  currentCode = randomCode()
  codeExpiresAt = Date.now() + CODE_TTL

  console.log('[AUTH] Código rotativo:', currentCode)
  return currentCode
}

export function consumeCode(code, ip) {
  if (!currentCode) return null
  if (Date.now() > codeExpiresAt) return null
  if (code !== currentCode) return null

  // invalida código
  currentCode = null
  codeExpiresAt = 0

  const token = crypto.randomBytes(32).toString('hex')
  const tokenHash = hash(token)

  tokens.set(tokenHash, {
    expiresAt: Date.now() + TOKEN_TTL,
    ip
  })

  return token
}

export function validateToken(token, ip) {
  if (!token) return false

  const tokenHash = hash(token)
  const session = tokens.get(tokenHash)

  if (!session) return false
  if (Date.now() > session.expiresAt) {
    tokens.delete(tokenHash)
    return false
  }

  // opcional: travar por IP
  if (session.ip !== ip) return false

  return true
}
