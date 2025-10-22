// sa.js — Servidor SuperAdmin V2
// Porta: 9999
// Funções: autenticação via token do app SA, criar empresas com mensagens, gerar PairCodes

const net = require('net');
const mysql = require('mysql2/promise');
const readline = require('readline');

// ---------- Config ----------
const PORT = 9999;
const APP_TOKEN = 'IronWatchSA'; // token do app SA que deve conectar

// ---------- DB ----------
const db = mysql.createPool({
    host: 'localhost',
    user: 'root',
    password: 'Colocar_a_merda_do_token_aqui_gordao',
    database: 'ironwatchv1',
    waitForConnections: true,
    connectionLimit: 10,
});

// ---------- Debug ----------
function log(...args) { console.log('[SA]', ...args); }

// ---------- Utils ----------
function genPairCode() { return Math.floor(100000 + Math.random() * 900000).toString(); }

// ---------- Empresas ----------
async function addCompany(data) {
    const { nome, email, apiKey, promptIA, mensagemBemVindo, mensagemLembrete, mensagemConfirmacao, mensagemConfirmado } = data;

    // 1. Criar empresa
    const [res] = await db.execute(
        "INSERT INTO Empresas (Nome, Email, PromptIA, API_KEY) VALUES (?,?,?,?)",
        [nome, email, promptIA, apiKey]
    );
    const empresaId = res.insertId;

    // 2. Criar mensagens obrigatórias
    const mensagens = [
        { texto: mensagemBemVindo, tipo: 'bem_vindo' },
        { texto: mensagemLembrete, tipo: 'lembrete' },
        { texto: mensagemConfirmacao, tipo: 'confirmacao' },
        { texto: mensagemConfirmado, tipo: 'confirmado' }
    ];

    for (const m of mensagens) {
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

// ---------- PairCodes ----------
const pendingCodes = {}; // code => {empresaId, tipo:'device', expiresAt, timer}

function addPending(code, empresaId, ttlSeconds=600) {
    if (pendingCodes[code] && pendingCodes[code].timer) clearTimeout(pendingCodes[code].timer);
    const expiresAt = Date.now() + ttlSeconds*1000;
    const timer = setTimeout(() => {
        delete pendingCodes[code];
        log(`PairCode ${code} expirou.`);
    }, ttlSeconds*1000);
    pendingCodes[code] = { empresaId, tipo:'device', expiresAt, timer };
    log(`Gerado PairCode ${code} para empresa ${empresaId} (válido ${ttlSeconds}s)`);
    return code;
}

function usePending(code) {
    const p = pendingCodes[code];
    if (!p) return null;
    clearTimeout(p.timer);
    delete pendingCodes[code];
    return p;
}

// ---------- Servidor TCP ----------
const server = net.createServer(socket => {
    log('Nova conexão de', socket.remoteAddress, socket.remotePort);

    let stage = 0; // 0 = espera token, 1 = autenticado
    let auth = false;

    socket.setEncoding('utf8');
    socket.write("Envie seu token do app SA:\n");

    let bufferPartial = '';
    socket.on('data', async chunk => {
        bufferPartial += chunk.toString();
        const lines = bufferPartial.split(/\r?\n/);
        if (!bufferPartial.endsWith('\n')) bufferPartial = lines.pop(); else bufferPartial='';

        for (const raw of lines) {
            const input = raw.trim();
            if (!input) continue;

            if (stage === 0) {
                if (input === APP_TOKEN) {
                    socket.write("Autenticado com sucesso.\n");
                    auth = true; stage=1;
                    log(`App SA autenticado de ${socket.remoteAddress}`);
                    socket.write("Comandos:\n1 - criar empresa\n2 - listar empresas\n3 - gerar PairCode\n0 - sair\n");
                } else {
                    socket.write("Token inválido. Conexão encerrada.\n");
                    socket.destroy();
                    log('Token inválido de', socket.remoteAddress);
                    return;
                }
                continue;
            }

            if (stage === 1 && auth) {
                switch(input) {
                    case '0': socket.write("Saindo...\n"); socket.destroy(); log('SA desconectado'); return;

                    case '1': { // Criar empresa
                        socket.write("Aguardando dados da empresa (JSON obrigatório com todos os campos):\n");
                        const jsonStr = await questionAsyncSocket(socket);
                        try {
                            const data = JSON.parse(jsonStr);
                            const empresaId = await addCompany(data);
                            socket.write(`Empresa criada com sucesso! ID: ${empresaId}\n`);
                        } catch(e) {
                            socket.write(`Erro ao criar empresa: ${e.message}\n`);
                        }
                        break;
                    }

                    case '2': { // Listar empresas
                        const rows = await listCompanies();
                        socket.write(JSON.stringify(rows,null,2)+'\n');
                        break;
                    }

                    case '3': { // Gerar PairCode
                        socket.write("EmpresaID para gerar PairCode: ");
                        const empId = await questionAsyncSocket(socket);
                        const code = genPairCode();
                        addPending(code, empId);
                        socket.write(`PairCode gerado: ${code}\n`);
                        break;
                    }

                    default: socket.write("Comando inválido\n");
                }
            }
        }
    });

    socket.on('end', () => log('Conexão encerrada de', socket.remoteAddress));
    socket.on('error', err => log('Erro socket:', err.message));
});

// ---------- Helper para perguntas async via socket ----------
function questionAsyncSocket(socket){
    return new Promise(resolve=>{
        const rl = readline.createInterface({input: socket, output: socket});
        rl.once('line', answer=>{
            rl.close();
            resolve(answer.trim());
        });
    });
}

// ---------- Start ----------
server.listen(PORT,'0.0.0.0', ()=>{
    log(`Servidor SA iniciado em porta ${PORT} — Insira este token no app: ${APP_TOKEN}`);
});
