const db = require('./db');

async function addDevice(empresaId, macIp, tipo) {
    await db.execute("INSERT INTO Dispositivos (EmpresaID, MacIp, Tipo) VALUES (?,?,?)", [empresaId, macIp, tipo]);
}

async function listDevices() {
    const [rows] = await db.query("SELECT * FROM Dispositivos");
    return rows;
}

module.exports = {
    addDevice,
    listDevices
};