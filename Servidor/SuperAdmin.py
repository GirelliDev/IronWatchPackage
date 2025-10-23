import asyncio
import random
import string
import time
import json
from typing import Optional, List
import aiomysql 

APP_TOKEN: Optional[str] = None
EXPIRA_EM: float = 0.0

# ---------- Config ----------
DEFAULT_HOST = "0.0.0.0"
DEFAULT_PORT = 9999
TOKEN_TTL = 5 * 60  # 5 minutos

# ---------- Logs / Histórico ----------
REQUEST_LOG: List[str] = [] 
#CODES_GENERATED: List[str] = []  

# ---------- DB ----------
db_pool: Optional[aiomysql.Pool] = None

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
    print("[DB] Conectado ao MySQL")

# ---------- Gerador de token ----------
def gen_pair_code() -> str:
    chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
    return ''.join(random.choice(chars) for _ in range(6))

def gerar_token():
    global APP_TOKEN, EXPIRA_EM
    APP_TOKEN = gen_pair_code()
    EXPIRA_EM = time.time() + TOKEN_TTL
    #CODES_GENERATED.append(APP_TOKEN)
    print(f"[TOKEN] Novo token: {APP_TOKEN} (expira em {time.strftime('%H:%M:%S', time.localtime(EXPIRA_EM))})")
    #print(f"[HIST] Tokens gerados: {CODES_GENERATED}")

async def token_loop():
    gerar_token()
    while True:
        await asyncio.sleep(1)
        if time.time() >= EXPIRA_EM:
            gerar_token()

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
                ("confirmado", data.get("confirmedMsg", ""))
            ]
            for tipo, texto in messages:
                await cur.execute(
                    "INSERT INTO Mensagens_Placeholder (EmpresaID, Texto, Tipo) VALUES (%s,%s,%s)",
                    (empresa_id, texto, tipo)
                )
            await conn.commit()
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
                if ph["Tipo"] == "bem_vindo":
                    empresa["welcomeMsg"] = ph["Texto"]
                elif ph["Tipo"] == "lembrete":
                    empresa["reminderMsg"] = ph["Texto"]
                elif ph["Tipo"] == "confirmacao":
                    empresa["confirmMsg"] = ph["Texto"]
                elif ph["Tipo"] == "confirmado":
                    empresa["confirmedMsg"] = ph["Texto"]
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
                    data.get("id")
                )
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
            return True


async def handle_client(reader: asyncio.StreamReader, writer: asyncio.StreamWriter):
    addr = writer.get_extra_info('peername')
    print(f"[CONEXÃO] {addr}")

    
    if APP_TOKEN is not None:
        writer.write((json.dumps({"action":"new-token","token":APP_TOKEN})+"\n").encode())
        await writer.drain()

    try:
        while not reader.at_eof():
            data = await reader.readline()
            if not data:
                break
            s = data.decode(errors="ignore").rstrip()
            log_entry = f"{time.strftime('%Y-%m-%d %H:%M:%S')} - {addr} - {s}"
            REQUEST_LOG.append(log_entry)
            print(f"[RECV] {log_entry}")

            
            try:
                msg = json.loads(s)
            except Exception:
                writer.write(json.dumps({"success": False, "message": "JSON inválido"})+"\n".encode())
                await writer.drain()
                continue

           
            if msg.get("token") != APP_TOKEN:
                writer.write(json.dumps({"success": False, "message": "Token inválido"})+"\n".encode())
                await writer.drain()
                continue

            
            try:
                action = msg.get("action")
                data_payload = msg.get("data", {})
                response = {"success": True}

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

                writer.write((json.dumps(response)+"\n").encode())
                await writer.drain()
            except Exception as e:
                writer.write(json.dumps({"success": False, "message": str(e)})+"\n".encode())
                await writer.drain()
    except Exception as e:
        print(f"[ERRO] client {addr}: {e}")
    finally:
        writer.close()
        await writer.wait_closed()
        print(f"[DESCONEXÃO] {addr}")

# ---------- API para iniciar servidor ----------
async def start_server_async(host=DEFAULT_HOST, port=DEFAULT_PORT):
    await init_db()
    asyncio.create_task(token_loop())
    server = await asyncio.start_server(handle_client, host, port)
    print(f"[SERVER] Servindo em {', '.join(str(s.getsockname()) for s in server.sockets or [])}")
    async with server:
        await server.serve_forever()

def start_server(host=DEFAULT_HOST, port=DEFAULT_PORT):
    asyncio.run(start_server_async(host, port))

# ---------- Execução direta ----------
if __name__ == "__main__":
    print("[BOOT] Iniciando Servidor SuperAdmin IronWatch | GirelliDev")
    start_server()
