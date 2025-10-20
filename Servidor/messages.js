const db = require('./db');

async function addPlaceholder(empresaId, texto, tipo) {
    await db.execute("INSERT INTO Mensagens_Placeholder (empresaid, texto, tipo) VALUES (?,?,?)", [empresaId, texto, tipo]);
}

async function logReceived(empresaId, numero, nome, texto) {
    await db.execute("INSERT INTO Mensagens_Recebidas (empresaid, numero, nome, data) VALUES (?,?,?,NOW())", [empresaId, numero, nome]);
}

module.exports = {
    addPlaceholder,
    logReceived
};