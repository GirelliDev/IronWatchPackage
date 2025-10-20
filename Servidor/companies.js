const db = require('./db');
const crypto = require('crypto');

async function addCompany(nome, email, qtdDisp = 5) {
    const apiKey = crypto.randomBytes(16).toString('hex');
    await db.execute("INSERT INTO Empresas (Nome, API_KEY, QuantidadeDispositivos, Email) VALUES (?,?,?,?)", [nome, apiKey, qtdDisp, email]);
    return apiKey;
}

async function listCompanies() {
    const [rows] = await db.query("SELECT * FROM Empresas");
    return rows;
}

module.exports = {
    addCompany,
    listCompanies
};