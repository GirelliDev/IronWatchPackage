import asyncio
import random
import string
import time
import json
from typing import Optional, List
import aiomysql
from datetime import datetime

# ---------- VARIÁVEIS GLOBAIS ----------
APP_TOKEN: Optional[str] = None
EXPIRA_EM: float = 0.0
TOKEN_TTL = 5 * 60  # 5 minutos
REQUEST_LOG: List[str] = []
db_pool: Optional[aiomysql.Pool] = None
connected_clients: set = set()  # Clientes conectados para túnel de token

DEFAULT_HOST = "0.0.0.0"
DEFAULT_PORT = 9999

# ---------- FUNÇÕES AUXILIARES ----------
def gen_pair_code() -> str:
    """Gera código aleatório de 6 caracteres"""
    chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
    return ''.join(random.choice(chars) for _ in range(6))

async def enviar_token_todos(token: str):
    """Envia token novo para todos os clientes conectados"""
    mensagem = f"TOKEN:{token}\n"
    for client in list(connected_clients):
        try:
            client.write(mensagem.encode())
            await client.drain()
            print(f"[DEBUUUUUUG] Token enviado para cliente")
        except Exception as e:
            print(f"[DEBUUUUUUG] Falha ao enviar token: {e}")
            connected_clients.discard(client)

def gerar_token():
    """Gera novo token e dispara envio para todos os clientes"""
    global APP_TOKEN, EXPIRA_EM
    APP_TOKEN = gen_pair_code()
    EXPIRA_EM = time.time() + TOKEN_TTL
    print(f"[DEBUUUUUUG] Novo token: {APP_TOKEN} (expira em {time.strftime('%H:%M:%S', time.localtime(EXPIRA_EM))})")
    asyncio.create_task(enviar_token_todos(APP_TOKEN))

# ---------- LOOP DE TOKEN ----------
async def token_loop():
    gerar_token()
    while True:
        await asyncio.sleep(1)
        if time.time() >= EXPIRA_EM:
            gerar_token()

# ---------- BANCO DE DADOS ----------
async def init_db():
    global db_pool
    db_pool = await aiomysql.create_pool(
        host='localhost',
        user='root',
        password='',
        db='ironwatchv1',
        minsize=1,
        maxsize=10
    )
    print("[DEBUUUUUUG] Conectado ao MySQL")

# ---------- DISPOSITIVOS ----------
async def registrar_dispositivo(data: dict, addr):
    ip = addr[0] if isinstance(addr, tuple) else str(addr)
    mac = data.get("mac_address")
    nome_host = data.get("hostname")
    async with db_pool.acquire() as conn:
        async with conn.cursor() as cur:
            await cur.execute("""
                INSERT INTO Dispositivos (nome_host, mac_address, ip_local, ultimo_login, token_usado)
                VALUES (%s,%s,%s,%s,%s)
                ON DUPLICATE KEY UPDATE
                    ultimo_login = VALUES(ultimo_login),
                    token_usado = VALUES(token_usado)
            """, (nome_host, mac, ip, datetime.now(), APP_TOKEN))
            await conn.commit()
            print(f"[DEBUUUUUUG] Dispositivo registrado: {nome_host} ({mac}) - {ip}")

# ---------- CRUD EMPRESA ----------
async def add_company(data: dict) -> int:
    async with db_pool.acquire() as conn:
        async with conn.cursor() as cur:
            sql = "INSERT INTO Empresas (Nome, Email, PromptIA, API_KEY, is_active) VALUES (%s,%s,%s,%s,1)"
            await cur.execute(sql, (
                data.get("name"),
                data.get("email"),
                data.get("promptIA"),
                data.get("apiKey")
            ))
            empresa_id = cur.lastrowid
            messages = [
                ("bem_vindo", data.get("welcomeMsg", "")),
                ("lembrete", data.get("reminderMsg", "")),
                ("confirmacao", data.get("confirmMsg", "")),
                ("confirmado", data.get("confirmedMsg", "")),
            ]
            for tipo, texto in messages:
                await cur.execute(
                    "INSERT INTO Mensagens_Placeholder (EmpresaID, Texto, Tipo) VALUES (%s,%s,%s)",
                    (empresa_id, texto, tipo)
                )
            await conn.commit()
            print(f"[DEBUUUUUUG] Empresa criada: {data.get('name')} (ID: {empresa_id})")
            return empresa_id

async def list_companies():
    async with db_pool.acquire() as conn:
        async with conn.cursor(aiomysql.DictCursor) as cur:
            await cur.execute("SELECT * FROM Empresas")
            return await cur.fetchall()

async def get_company_full(id: int):
    async with db_pool.acquire() as conn:
        async with conn.cursor(aiomysql.DictCursor) as cur:
            await cur.execute("SELECT * FROM Empresas WHERE id=%s", (id,))
            empresa = await cur.fetchone()
            if not empresa:
                return None
            await cur.execute("SELECT Tipo, Texto FROM Mensagens_Placeholder WHERE EmpresaID=%s", (id,))
            placeholders = await cur.fetchall()
            for ph in placeholders:
                empresa[ph["Tipo"]] = ph["Texto"]
            return empresa

async def update_company(data: dict) -> bool:
    async with db_pool.acquire() as conn:
        async with conn.cursor() as cur:
            await cur.execute(
                "UPDATE Empresas SET Nome=%s, Email=%s, API_KEY=%s, PromptIA=%s, is_active=%s WHERE id=%s",
                (
                    data.get("name"),
                    data.get("email"),
                    data.get("apiKey"),
                    data.get("promptIA"),
                    data.get("is_active", 0),
                    data.get("id"),
                ),
            )
            messages = [
                ("bem_vindo", data.get("welcomeMsg", "")),
                ("lembrete", data.get("reminderMsg", "")),
                ("confirmacao", data.get("confirmMsg", "")),
                ("confirmado", data.get("confirmedMsg", "")),
            ]
            for tipo, texto in messages:
                await cur.execute(
                    "UPDATE Mensagens_Placeholder SET Texto=%s WHERE EmpresaID=%s AND Tipo=%s",
                    (texto, data.get("id"), tipo)
                )
            await conn.commit()
            print(f"[DEBUUUUUUG] Empresa atualizada: {data.get('name')} (ID: {data.get('id')})")
            return True

# ---------- CLIENTES ----------
async def handle_client(reader: asyncio.StreamReader, writer: asyncio.StreamWriter):
    addr = writer.get_extra_info("peername")
    connected_clients.add(writer)
    print(f"[DEBUUUUUUG] Cliente conectado: {addr}")

    try:
        while not reader.at_eof():
            data = await reader.readline()
            if not data:
                break
            s = data.decode(errors="ignore").rstrip()
            REQUEST_LOG.append(f"{datetime.now()} - {addr} - {s}")

            # Tentar JSON
            try:
                msg = json.loads(s)
                is_json = True
            except json.JSONDecodeError:
                is_json = False

            if not is_json:
                # apenas token cru
                if s == APP_TOKEN:
                    writer.write(f"TOKEN_OK\n".encode())
                    await writer.drain()
                    print(f"[DEBUUUUUUG] Token cru válido recebido de {addr}")
                else:
                    writer.write(f"ERRO: Token inválido\n".encode())
                    await writer.drain()
                    print(f"[DEBUUUUUUG] Token cru inválido de {addr}")
                continue

            # token via JSON
            if msg.get("action") == "new-token":
                writer.write(f"TOKEN:{APP_TOKEN}\n".encode())
                await writer.drain()
                print(f"[DEBUUUUUUG] Token enviado manualmente para {addr}")
                continue

            if msg.get("token") != APP_TOKEN:
                writer.write("ERRO: Token inválido\n".encode())
                await writer.drain()
                print(f"[DEBUUUUUUG] Token inválido de {addr}")
                continue

            # registrar dispositivo
            if "device" in msg:
                await registrar_dispositivo(msg["device"], addr)

            # ações
            action = msg.get("action")
            data_payload = msg.get("data", {})
            response = {"success": True}

            try:
                if action == "list-companies":
                    response["companies"] = await list_companies()
                elif action == "create-company":
                    response["empresaId"] = await add_company(data_payload)
                elif action == "get-company-full":
                    response["empresa"] = await get_company_full(data_payload.get("id"))
                elif action == "update-company":
                    await update_company(data_payload)
                else:
                    response = {"success": False, "message": "Ação desconhecida"}

                writer.write((json.dumps(response) + "\n").encode())
                await writer.drain()
                print(f"[DEBUUUUUUG] Action processada: {action} para {addr}")
            except Exception as e:
                writer.write((json.dumps({"success": False, "message": str(e)}) + "\n").encode())
                await writer.drain()
                print(f"[DEBUUUUUUG] Erro ao processar action {action}: {e}")
    finally:
        connected_clients.discard(writer)
        writer.close()
        await writer.wait_closed()
        print(f"[DEBUUUUUUG] Cliente desconectado: {addr}")

# ---------- INICIALIZAÇÃO ----------
async def start_server_async(host=DEFAULT_HOST, port=DEFAULT_PORT):
    await init_db()
    asyncio.create_task(token_loop())
    server = await asyncio.start_server(handle_client, host, port)
    print(f"[DEBUUUUUUG] Servidor rodando em {host}:{port}")
    async with server:
        await server.serve_forever()

def start_server(host=DEFAULT_HOST, port=DEFAULT_PORT):
    asyncio.run(start_server_async(host, port))

if __name__ == "__main__":
    print("[DEBUUUUUUG] Iniciando Servidor SuperAdmin IronWatch | GirelliDev")
    start_server()
