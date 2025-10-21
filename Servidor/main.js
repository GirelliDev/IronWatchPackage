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

function genPairCode(isSuper = false) {
    if (isSuper) return '1' + Math.floor(100000 + Math.random() * 900000).toString();
    return (Math.floor(200000 + Math.random() * 799999)).toString();
}

function questionAsync(prompt) {
    return new Promise(resolve => rl.question(prompt, ans => resolve(ans.trim().toLowerCase() === 'exit' ? null : ans.trim())));
}

function maybeRandomError(prob = 0.05) {
    if (Math.random() < prob) throw new Error("Erro interno aleatório (teste de robustez)");
}

let pendingCodes = {}; // { ipPort: { code, tipo, timer } }

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
            const rows = await listCompanies();
            console.table(rows);
            return mainMenu();
        }
        case '3': {
            const empId = await questionAsync("Empresa ID: ");
            if (!empId) return mainMenu();
            const tipo = await questionAsync("Tipo (mobile/pc/other): ");
            if (!tipo) return mainMenu();
            const pair = genPairCode(false);
            const [res] = await db.execute("INSERT INTO Dispositivos (EmpresaID, MacIp, Tipo, PairCode, Paired) VALUES (?,?,?,?,0)", [empId, null, tipo, pair]);
            console.log("Dispositivo criado. DispId:", res.insertId, "PairCode:", pair);
            return mainMenu();
        }
        case '4': {
            const rows = await db.query("SELECT * FROM Dispositivos");
            console.table(rows);
            return mainMenu();
        }
        case '5': {
            const pair = genPairCode(true);
            pendingCodes[`CLI:${Date.now()}`] = {
                code: pair,
                tipo: 'superadmin',
                timer: setTimeout(() => {
                    delete pendingCodes[`CLI:${Date.now()}`];
                }, 5 * 60 * 1000)
            };
            console.log(`=== SuperAdmin Code ===\n${pair}\nValido por 5 minutos\n===================`);
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

    socket.setEncoding('utf8');
    socket.write("Envie seu código:\n");

    socket.on('data', async chunk => {
        const input = chunk.toString().trim();
        if (!input) return;

        try {
            maybeRandomError(0.05);
        } catch (e) {
            socket.write("Erro aleatório\n");
            return;
        }

        const pendingEntryKey = Object.keys(pendingCodes).find(k => pendingCodes[k].code === input);
        if (!pendingEntryKey) {
            socket.write("Código inválido\n");
            return;
        }

        const entry = pendingCodes[pendingEntryKey];

        if (entry.tipo === 'superadmin') {
            await db.execute("INSERT INTO SuperAdminDevices (PairCode, Paired, MacIp) VALUES (?,?,?)", [input, 1, `${ip}:${port}`]);
            clearTimeout(entry.timer);
            delete pendingCodes[pendingEntryKey];
            socket.write("SuperAdmin registrado com sucesso\n");
        } else if (entry.tipo === 'device') {
            const [devRows] = await db.query("SELECT * FROM Dispositivos WHERE PairCode=? AND Paired=0 LIMIT 1", [input]);
            if (devRows.length === 0) {
                socket.write("PairCode inválido ou já usado\n");
                return;
            }
            const dev = devRows[0];
            const macIp = `${ip}:${port}`;
            await db.execute("UPDATE Dispositivos SET Paired=1, MacIp=? WHERE DispId=?", [macIp, dev.DispId]);
            clearTimeout(entry.timer);
            delete pendingCodes[pendingEntryKey];
            socket.write(`Pareado com sucesso. ID: ${dev.DispId}\n`);
        } else {
            socket.write("Tipo de código desconhecido\n");
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