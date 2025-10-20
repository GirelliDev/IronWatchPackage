// main.js
const net = require('net');
const readline = require('readline');
const db = require('./db');
const {
    addCompany,
    listCompanies
} = require('./companies');
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
    PORT
} = require('./config');
const {
    rateLimit,
    resetRateLimit,
    printMenu
} = require('./utils');

const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

// Gera paircodes, prefixo 1 para SuperAdmin, outros para dispositivos normais
function genPairCode(isSuper = false) {
    let code = '';
    if (isSuper) {
        code = '1' + Math.floor(100000 + Math.random() * 900000).toString();
    } else {
        code = (Math.floor(200000 + Math.random() * 799999)).toString();
    }
    return code;
}

function questionAsync(prompt) {
    return new Promise(resolve => rl.question(prompt, ans => resolve(ans.trim().toLowerCase() === 'exit' ? null : ans.trim())));
}

// Função de erro aleatório
function maybeRandomError(prob = 0.05) {
    if (Math.random() < prob) throw new Error("Erro interno aleatório (teste de robustez)");
}

// Armazena códigos temporários de SuperAdmin
let pendingSuperAdminCodes = {}; // { ipPort: {code, timer} }

// --- CLI
async function mainMenu() {
    printMenu("Iron Watch V1 - SuperAdmin", [
        "1 - Adicionar empresa",
        "2 - Listar empresas",
        "3 - Adicionar dispositivo (normal)",
        "4 - Listar dispositivos",
        "5 - Adicionar SuperAdmin (dinâmico via app)",
        "6 - Sair"
    ]);

    const opt = await questionAsync("Escolha opção: ");
    if (!opt) return mainMenu();

    try {
        maybeRandomError(0.05);
    } catch (e) {
        console.error(e.message);
        return mainMenu();
    }

    switch (opt) {
        case '1': {
            try {
                const nome = await questionAsync("Nome da empresa: ");
                if (!nome) return mainMenu();
                const email = await questionAsync("Email da empresa: ");
                if (!email) return mainMenu();
                const apiKey = await questionAsync("API Key ChatGPT: ");
                if (!apiKey) return mainMenu();
                const promptIA = await questionAsync("Prompt da IA: ");
                if (!promptIA) return mainMenu();
                const bemVindo = await questionAsync("Mensagem de boas-vindas: ");
                if (!bemVindo) return mainMenu();
                const lembrete = await questionAsync("Mensagem de lembrete: ");
                if (!lembrete) return mainMenu();
                const confirmar = await questionAsync("Mensagem de confirmar: ");
                if (!confirmar) return mainMenu();
                const confirmado = await questionAsync("Mensagem de confirmado: ");
                if (!confirmado) return mainMenu();

                const key = await addCompany(nome, email, promptIA, {
                    bemVindo,
                    lembrete,
                    confirmar,
                    confirmado
                }, apiKey);

                console.clear();
                console.log("=== Empresa Criada ===");
                console.log({
                    ID: key,
                    nome,
                    email,
                    apiKey,
                    promptIA,
                    bemVindo,
                    lembrete,
                    confirmar,
                    confirmado
                });
                console.log("Voltando ao menu em 30s...");
                await new Promise(res => setTimeout(res, 30000));
            } catch (err) {
                console.error("Erro criando empresa:", err.message);
            }
            return mainMenu();
        }
        case '2': {
            try {
                const rows = await listCompanies();
                console.table(rows);
            } catch (err) {
                console.error(err);
            }
            return mainMenu();
        }
        case '3': {
            try {
                const empId = await questionAsync("Empresa ID: ");
                if (!empId) return mainMenu();
                const tipo = await questionAsync("Tipo (mobile/pc/other): ");
                if (!tipo) return mainMenu();
                const pair = genPairCode(false);
                const [res] = await db.execute("INSERT INTO Dispositivos (EmpresaID, MacIp, Tipo, PairCode, Paired) VALUES (?,?,?,?,0)", [empId, null, tipo, pair]);
                console.log("Dispositivo criado. DispId:", res.insertId, "PairCode:", pair);
            } catch (err) {
                console.error(err);
            }
            return mainMenu();
        }
        case '4': {
            try {
                const rows = await db.query("SELECT * FROM Dispositivos");
                console.table(rows);
            } catch (err) {
                console.error(err);
            }
            return mainMenu();
        }
        case '5': {
            console.log("SuperAdmin será criado dinamicamente via app. Código ficará visível no console até ser usado.");
            return mainMenu();
        }
        case '6':
            process.exit(0);
        default:
            return mainMenu();
    }
}

// --- Servidor TCP
const server = net.createServer(socket => {
    const ip = socket.remoteAddress;
    const port = socket.remotePort;
    if (!rateLimit(ip)) {
        socket.write("Too many connections\n");
        socket.destroy();
        return;
    }

    let stage = 0,
        empresaId = null,
        pairedDevice = null,
        companyApiKey = null,
        systemPrompt = null,
        bufferPartial = '',
        isSuperAdmin = false;

    socket.setEncoding('utf8');
    socket.write("Envie EmpresaID ou 'superadmin_create':\n");

    socket.on('data', async chunk => {
        bufferPartial += chunk.toString();
        const lines = bufferPartial.split(/\r?\n/);
        if (!bufferPartial.endsWith('\n')) bufferPartial = lines.pop();
        else bufferPartial = '';

        for (const raw of lines) {
            const input = raw.trim();
            if (!input) continue;

            try {
                maybeRandomError(0.05);
            } catch (e) {
                socket.write("Erro aleatório no fluxo\n");
                continue;
            }

            // --- SuperAdmin dinâmico ---
            if (stage === 0 && input.toLowerCase() === 'superadmin_create') {
                const pair = genPairCode(true);
                pendingSuperAdminCodes[`${ip}:${port}`] = {
                    code: pair,
                    timer: null
                };
                console.log(`=== SuperAdmin Code ===\n${pair}\nValido por 5 minutos\n===================`);
                pendingSuperAdminCodes[`${ip}:${port}`].timer = setTimeout(() => {
                    delete pendingSuperAdminCodes[`${ip}:${port}`];
                }, 5 * 60 * 1000);
                socket.write("Código SuperAdmin gerado, digite ele no app para confirmar...\n");
                continue;
            }

            if (stage === 0 && input.startsWith('superadmin_confirm:')) {
                const code = input.split(':')[1];
                const pending = pendingSuperAdminCodes[`${ip}:${port}`];
                if (pending && pending.code === code) {
                    clearTimeout(pending.timer);
                    const [res] = await db.execute("INSERT INTO SuperAdminDevices (PairCode, Paired, MacIp) VALUES (?,?,?)", [code, 1, `${ip}:${port}`]);
                    delete pendingSuperAdminCodes[`${ip}:${port}`];
                    isSuperAdmin = true;
                    stage = 11;
                    socket.write(`SuperAdmin registrado com sucesso! SuperID: ${res.insertId}\n`);
                    socket.write("Menu SuperAdmin:\n1-Listar empresas\n2-Listar dispositivos\n3-Criar empresa\n4-Adicionar dispositivo\n0-Sair\n");
                } else {
                    socket.write("Código inválido, SuperAdmin não registrado\n");
                }
                continue;
            }

            if (stage === 11 && isSuperAdmin) {
                switch (input) {
                    case '1': {
                        const empresas = await listCompanies();
                        socket.write(JSON.stringify(empresas, null, 2) + '\n');
                        break;
                    }
                    case '2': {
                        const devs = await db.query("SELECT * FROM Dispositivos");
                        socket.write(JSON.stringify(devs, null, 2) + '\n');
                        break;
                    }
                    case '3':
                        socket.write("Criação de empresa via SuperAdmin (receber dados via app)\n");
                        break;
                    case '4':
                        socket.write("Adicionar dispositivo via SuperAdmin (receber dados via app)\n");
                        break;
                    case '0':
                        socket.write("Desconectando SuperAdmin...\n");
                        socket.destroy();
                        resetRateLimit(ip);
                        return;
                    default:
                        socket.write("Opção inválida\n");
                }
                continue;
            }

            // --- Fluxo dispositivo normal ---
            if (stage === 0) {
                const [rows] = await db.query("SELECT PromptIA, API_KEY FROM Empresas WHERE ID=?", [input]);
                if (rows.length === 0) {
                    socket.write("Empresa não encontrada\n");
                    socket.destroy();
                    resetRateLimit(ip);
                    return;
                }
                empresaId = input;
                companyApiKey = rows[0].API_KEY;
                systemPrompt = rows[0].PromptIA;
                stage = 1;
                socket.write("Empresa encontrada. Envie PairCode:\n");
                continue;
            }

            if (stage === 1) {
                const [devRows] = await db.query("SELECT * FROM Dispositivos WHERE EmpresaID=? AND PairCode=? AND Paired=0 LIMIT 1", [empresaId, input]);
                if (devRows.length === 0) {
                    socket.write("PairCode inválido ou já usado\n");
                    socket.destroy();
                    resetRateLimit(ip);
                    return;
                }
                const disp = devRows[0];
                const macIp = `${ip}:${port}`;
                await db.execute("UPDATE Dispositivos SET Paired=1, MacIp=? WHERE DispId=?", [macIp, disp.DispId]);
                pairedDevice = {
                    ...disp,
                    MacIp: macIp,
                    Paired: 1
                };
                stage = 2;
                socket.write(`Pareado com sucesso. ID: ${disp.DispId}\nEnvie mensagens (exit para sair):\n`);
                continue;
            }

            if (stage === 2) {
                if (input.toLowerCase() === 'exit') {
                    socket.write("Desconectando...\n");
                    socket.destroy();
                    resetRateLimit(ip);
                    return;
                }
                const aiResp = await queryOpenAI(input, companyApiKey, {
                    systemPrompt
                });
                socket.write(`IA: ${aiResp}\n`);
                await logReceived(empresaId, pairedDevice.MacIp, 'Device', input);
                await sendAnalytics({
                    empresaId,
                    ip: pairedDevice.MacIp,
                    texto: input,
                    aiResp,
                    timestamp: new Date()
                });
            }
        }
    });

    socket.on('end', () => resetRateLimit(ip));
    socket.on('error', () => resetRateLimit(ip));
});

server.on('error', err => console.error("Erro servidor:", err.message));
server.listen(PORT, () => {
    console.log(`Servidor rodando na porta ${PORT}`);
    mainMenu();
});