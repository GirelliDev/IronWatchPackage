// main.js
// Servidor TCP multi-empresa com pareamento por código (pairing code)
// Uso: node main.js
// Requisitos: módulos locais: config.js, auth.js, db.js, companies.js, messages.js, ai.js, analytics.js, utils.js

const net = require('net');
const readline = require('readline');
const crypto = require('crypto');

const {
    PORT,
    MENU_COLOR,
    MENU_RESET,
    MAX_CONNECTIONS_PER_IP
} = require('./config');
const {
    generateCode
} = require('./auth');
const db = require('./db');
const {
    addCompany,
    listCompanies
} = require('./companies'); // usa functions existentes
const {
    logReceived
} = require('./messages');
const {
    queryOpenAI
} = require('./ai');
const {
    sendAnalytics
} = require('./analytics');
const {
    rateLimit,
    resetRateLimit,
    printMenu
} = require('./utils');

const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

// util local para gerar PairCode numérico de 6 dígitos
function genPairCode() {
    return Math.floor(100000 + Math.random() * 900000).toString();
}

// --- CLI
async function mainMenu() {
    printMenu("IronWatch V1", [
        "Adicionar empresa",
        "Listar empresas",
        "Adicionar dispositivo (gera PairCode)",
        "Listar dispositivos",
        "Sair"
    ]);

    rl.question("Escolha opção: ", async opt => {
        switch (opt) {
            case '1':
                rl.question("Nome da empresa: ", async nome => {
                    const apiKey = await addCompany(nome, 'email@empresa.com');
                    console.log("Empresa adicionada! API Key:", apiKey);
                    setTimeout(mainMenu, 800);
                });
                break;
            case '2':
                console.table(await listCompanies());
                setTimeout(mainMenu, 800);
                break;
            case '3':
                // Adicionar dispositivo: gera PairCode e salva Paired=0
                rl.question("Empresa ID: ", async empId => {
                    rl.question("Tipo (mobile/pc/other): ", async tipo => {
                        try {
                            const pair = genPairCode();
                            // Insere dispositivo com PairCode e Paired=0; MacIp pode ser nulo inicialmente
                            const [res] = await db.execute(
                                "INSERT INTO Dispositivos (EmpresaID, MacIp, Tipo, PairCode, Paired) VALUES (?,?,?,?,0)",
                                [empId, null, tipo, pair]
                            );
                            console.log("Dispositivo criado. DispId:", res.insertId);
                            console.log("PairCode (enviar pro app):", pair);
                            console.log("O app deve enviar EmpresaID então PairCode pra parear.");
                        } catch (err) {
                            console.error("Erro adicionando dispositivo:", err.message);
                        }
                        setTimeout(mainMenu, 1200);
                    });
                });
                break;
            case '4':
                try {
                    const [rows] = await db.query("SELECT DispId, EmpresaID, MacIp, Tipo, PairCode, Paired FROM Dispositivos");
                    console.table(rows);
                } catch (err) {
                    console.error("Erro listando dispositivos:", err.message);
                }
                setTimeout(mainMenu, 800);
                break;
            case '5':
            case '0':
                process.exit(0);
            default:
                setTimeout(mainMenu, 800);
        }
    });
}

// --- Servidor TCP com fluxo de pareamento
const server = net.createServer(socket => {
    const ip = socket.remoteAddress;
    const port = socket.remotePort;

    if (!rateLimit(ip)) {
        console.log("Bloqueado excesso de conexões:", ip);
        socket.write("Too many connections from your IP. Disconnecting.\n");
        socket.destroy();
        return;
    }

    // estado de handshake
    let stage = 0; // 0 = esperar EmpresaID, 1 = esperar PairCode, 2 = pareado / autenticado
    let empresaId = null;
    let pairedDevice = null; // row da tabela Dispositivos quando pareado
    let companyApiKey = null;
    let bufferPartial = '';

    socket.setEncoding('utf8');
    socket.write("Envie EmpresaID (apenas o número) e pressione Enter:\n");

    socket.on('data', async chunk => {
        // Algumas conexões podem enviar CRLF, juntar buffer
        bufferPartial += chunk.toString();
        // processa por linhas
        const lines = bufferPartial.split(/\r?\n/);
        // mantém última linha se não terminou com newline
        if (!bufferPartial.endsWith('\n') && !bufferPartial.endsWith('\r\n')) {
            bufferPartial = lines.pop();
        } else {
            bufferPartial = '';
        }

        for (const raw of lines) {
            const input = raw.trim();
            if (input.length === 0) continue;

            try {
                if (stage === 0) {
                    // recebe EmpresaID
                    const [rows] = await db.query("SELECT * FROM Empresas WHERE ID = ?", [input]);
                    if (rows.length === 0) {
                        socket.write("Empresa não encontrada. Desconectando.\n");
                        socket.destroy();
                        resetRateLimit(ip);
                        return;
                    }
                    empresaId = rows[0].ID;
                    companyApiKey = rows[0].API_KEY;
                    stage = 1;
                    socket.write("Empresa encontrada. Agora envie PairCode (6 dígitos):\n");
                    continue;
                }

                if (stage === 1) {
                    // recebe PairCode
                    const pairCode = input;
                    // procura dispositivo com EmpresaID, PairCode e Paired = 0
                    const [devRows] = await db.query(
                        "SELECT * FROM Dispositivos WHERE EmpresaID = ? AND PairCode = ? AND Paired = 0 LIMIT 1",
                        [empresaId, pairCode]
                    );

                    if (devRows.length === 0) {
                        socket.write("PairCode inválido ou já usado. Desconectando.\n");
                        socket.destroy();
                        resetRateLimit(ip);
                        return;
                    }

                    // OK — marca como pareado (Paired=1) e grava MacIp (ip:port aqui) e timestamp
                    const disp = devRows[0];
                    const macIp = `${ip}:${port}`;
                    await db.execute(
                        "UPDATE Dispositivos SET Paired = 1, MacIp = ? WHERE DispId = ?",
                        [macIp, disp.DispId]
                    );

                    pairedDevice = Object.assign({}, disp, {
                        MacIp: macIp,
                        Paired: 1
                    });
                    stage = 2;
                    socket.write(`Pareado com sucesso. Dispositivo ID: ${disp.DispId}\n`);
                    console.log(`Pareado: Empresa=${empresaId} DispId=${disp.DispId} IP=${macIp}`);

                    // registra log de pareamento em Mensagens_Recebidas (opcional)
                    await db.execute(
                        "INSERT INTO Mensagens_Recebidas (empresaid, numero, nome, data) VALUES (?,?,?,NOW())",
                        [empresaId, macIp, 'PAIRING']
                    );

                    // pronto: segue pra receber mensagens do dispositivo
                    socket.write("Agora envie mensagens para a IA. Cada linha será processada.\n");
                    continue;
                }

                if (stage === 2) {
                    // Dispositivo já pareado, processa input como mensagem a IA
                    const texto = input;

                    // pega API key da empresa (já carregada mas recarregar para garantir)
                    const [rows] = await db.query("SELECT API_KEY FROM Empresas WHERE ID = ?", [empresaId]);
                    if (rows.length === 0) {
                        socket.write("Erro: API key da empresa não encontrada. Desconectando.\n");
                        socket.destroy();
                        resetRateLimit(ip);
                        return;
                    }
                    const apiKey = rows[0].API_KEY || companyApiKey || process.env.OPENAI_KEY;

                    // consulta OpenAI (usa apiKey da empresa)
                    let aiResp = "";
                    try {
                        aiResp = await queryOpenAI(texto, apiKey);
                    } catch (err) {
                        console.error("Erro OpenAI:", err.message);
                        socket.write("Erro consultando IA: " + err.message + "\n");
                        continue;
                    }

                    // responde pro dispositivo
                    socket.write(`IA: ${aiResp}\n`);

                    // log no banco e analytics
                    try {
                        await logReceived(empresaId, pairedDevice ? pairedDevice.MacIp : ip, 'Device', texto);
                    } catch (e) {
                        console.error("Erro logReceived:", e.message);
                    }

                    try {
                        await sendAnalytics({
                            empresaId,
                            ip: pairedDevice ? pairedDevice.MacIp : ip,
                            texto,
                            aiResp,
                            timestamp: new Date()
                        });
                    } catch (e) {
                        console.error("Erro sendAnalytics:", e.message);
                    }

                    continue;
                }
            } catch (err) {
                console.error("Erro no fluxo de conexão:", err.message);
                socket.write("Erro interno do servidor. Desconectando.\n");
                socket.destroy();
                resetRateLimit(ip);
                return;
            }
        } // fim for lines
    });

    socket.on('end', () => {
        resetRateLimit(ip);
        console.log("Conexão encerrada:", ip);
    });

    socket.on('error', err => {
        resetRateLimit(ip);
        console.log("Erro socket:", err.message);
    });
});

server.on('error', err => {
    console.error("Erro no servidor TCP:", err.message);
});

server.listen(PORT, () => {
    console.log(`Servidor rodando na porta ${PORT}`);
    mainMenu();
});