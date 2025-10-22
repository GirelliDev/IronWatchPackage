// sa-http-full.js — Servidor SA HTTP puro Node
const http = require('http');
const mysql = require('mysql2/promise');

const APP_TOKEN = 'IronWatchSA';
const PORT = 9999;

// ---------- DB ----------
const db = mysql.createPool({
    host: 'localhost',
    user: 'root',
    password: '',
    database: 'ironwatchv1',
    waitForConnections: true,
    connectionLimit: 10,
});

// ---------- Utils ----------
function genPairCode() {
    return Math.floor(100000 + Math.random() * 900000).toString();
}

// ---------- Empresas ----------
async function addCompany(data) {
    const {
        name,
        email,
        apiKey,
        promptIA,
        welcomeMsg,
        reminderMsg,
        confirmMsg,
        confirmedMsg
    } = data;

    const [res] = await db.execute(
        "INSERT INTO Empresas (Nome, Email, PromptIA, API_KEY, is_active) VALUES (?,?,?,?,1)",
        [name, email, promptIA, apiKey]
    );
    const empresaId = res.insertId;

    const messages = [{
            texto: welcomeMsg,
            tipo: 'bem_vindo'
        },
        {
            texto: reminderMsg,
            tipo: 'lembrete'
        },
        {
            texto: confirmMsg,
            tipo: 'confirmacao'
        },
        {
            texto: confirmedMsg,
            tipo: 'confirmado'
        }
    ];

    for (const m of messages) {
        await db.execute(
            "INSERT INTO Mensagens_Placeholder (EmpresaID, Texto, Tipo) VALUES (?,?,?)",
            [empresaId, m.texto, m.tipo]
        );
    }

    return empresaId;
}

async function listCompanies() {
    const [rows] = await db.query("SELECT * FROM Empresas");
    return rows;
}

async function getCompanyFull(id) {
    const [
        [empresa]
    ] = await db.query("SELECT * FROM Empresas WHERE id = ?", [id]);
    if (!empresa) return null;

    const [placeholders] = await db.query(
        "SELECT Tipo, Texto FROM Mensagens_Placeholder WHERE EmpresaID = ?",
        [id]
    );

    const result = {
        ...empresa
    };
    for (const ph of placeholders) {
        if (ph.Tipo === 'bem_vindo') result.welcomeMsg = ph.Texto;
        if (ph.Tipo === 'lembrete') result.reminderMsg = ph.Texto;
        if (ph.Tipo === 'confirmacao') result.confirmMsg = ph.Texto;
        if (ph.Tipo === 'confirmado') result.confirmedMsg = ph.Texto;
    }
    return result;
}

async function updateCompany(data) {
    const {
        id,
        name,
        email,
        apiKey,
        promptIA,
        welcomeMsg,
        reminderMsg,
        confirmMsg,
        confirmedMsg,
        is_active
    } = data;
    await db.execute(
        "UPDATE Empresas SET Nome=?, Email=?, API_KEY=?, PromptIA=?, is_active=? WHERE id=?",
        [name, email, apiKey, promptIA, is_active, id]
    );

    const placeholders = [{
            tipo: 'bem_vindo',
            texto: welcomeMsg
        },
        {
            tipo: 'lembrete',
            texto: reminderMsg
        },
        {
            tipo: 'confirmacao',
            texto: confirmMsg
        },
        {
            tipo: 'confirmado',
            texto: confirmedMsg
        },
    ];

    for (const ph of placeholders) {
        await db.execute(
            "UPDATE Mensagens_Placeholder SET Texto=? WHERE EmpresaID=? AND Tipo=?",
            [ph.texto, id, ph.tipo]
        );
    }

    return true;
}

// ---------- HTTP Server ----------
const server = http.createServer(async (req, res) => {
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');

    if (req.method === 'OPTIONS') {
        res.writeHead(204);
        res.end();
        return;
    }

    const auth = req.headers['authorization'];
    if (!auth || auth !== `Bearer ${APP_TOKEN}`) {
        res.writeHead(401, {
            'Content-Type': 'application/json'
        });
        res.end(JSON.stringify({
            error: 'Token inválido'
        }));
        return;
    }

    let body = '';
    req.on('data', chunk => body += chunk);
    req.on('end', async () => {
        try {
            if (req.method === 'POST' && req.url === '/create-company') {
                const data = JSON.parse(body);
                const empresaId = await addCompany(data);
                res.writeHead(200, {
                    'Content-Type': 'application/json'
                });
                res.end(JSON.stringify({
                    success: true,
                    empresaId
                }));

            } else if (req.method === 'GET' && req.url === '/list-companies') {
                const rows = await listCompanies();
                res.writeHead(200, {
                    'Content-Type': 'application/json'
                });
                res.end(JSON.stringify(rows));

            } else if (req.method === 'GET' && req.url.startsWith('/get-company-full/')) {
                const id = req.url.split('/').pop();
                const empresa = await getCompanyFull(id);
                res.writeHead(200, {
                    'Content-Type': 'application/json'
                });
                res.end(JSON.stringify(empresa || {}));

            } else if (req.method === 'POST' && req.url === '/update-company') {
                const data = JSON.parse(body);
                await updateCompany(data);
                res.writeHead(200, {
                    'Content-Type': 'application/json'
                });
                res.end(JSON.stringify({
                    success: true
                }));

            } else {
                res.writeHead(404, {
                    'Content-Type': 'application/json'
                });
                res.end(JSON.stringify({
                    error: 'Rota não encontrada'
                }));
            }
        } catch (err) {
            res.writeHead(500, {
                'Content-Type': 'application/json'
            });
            res.end(JSON.stringify({
                success: false,
                message: err.message
            }));
        }
    });
});

server.listen(PORT, () => console.log(`[SA HTTP] Servidor rodando na porta ${PORT}, token: ${APP_TOKEN}`));