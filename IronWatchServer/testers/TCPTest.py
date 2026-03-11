import json
import socket
import sys
from typing import Any, Dict, Optional


HOST = "181.215.45.62"
PORT = 5555

LOGIN = "girellidev"
PASSWORD = "Kv13013+"


def send_tcp(payload: str, host: str = HOST, port: int = PORT, timeout: int = 15) -> str:
    with socket.create_connection((host, port), timeout=timeout) as sock:
        sock.settimeout(timeout)

        data = (payload + "\n").encode("utf-8")
        sock.sendall(data)

        chunks = []
        while True:
            try:
                part = sock.recv(4096)
                if not part:
                    break
                chunks.append(part)
                if b"\n" in part:
                    break
            except socket.timeout:
                break

        response = b"".join(chunks).decode("utf-8", errors="replace").strip()
        return response


def send_json(data: Dict[str, Any], host: str = HOST, port: int = PORT) -> Dict[str, Any]:
    raw = json.dumps(data, ensure_ascii=False)
    print(f"\n[>] Enviando JSON: {raw}")

    response = send_tcp(raw, host, port)
    print(f"[<] Resposta bruta: {response}")

    try:
        parsed = json.loads(response)
        return parsed
    except json.JSONDecodeError:
        return {
            "success": False,
            "message": "Resposta nao veio em JSON valido",
            "raw": response
        }


def send_legacy(command: str, host: str = HOST, port: int = PORT) -> str:
    print(f"\n[>] Enviando legado: {command}")
    response = send_tcp(command, host, port)
    print(f"[<] Resposta bruta: {response}")
    return response


def test_ping_json() -> bool:
    result = send_json({"action": "PING"})
    ok = bool(result.get("success")) and result.get("message") == "PONG"
    print(f"[PING JSON] {'OK' if ok else 'FALHOU'}")
    return ok


def test_ping_legacy() -> bool:
    result = send_legacy("PING")
    ok = result == "PONG"
    print(f"[PING LEGACY] {'OK' if ok else 'FALHOU'}")
    return ok


def test_login(login: str, password: str) -> Optional[str]:
    result = send_json({
        "action": "AUTH_LOGIN",
        "login": login,
        "password": password
    })

    if not result.get("success"):
        print(f"[LOGIN] FALHOU: {result.get('message')}")
        return None

    token = result.get("token")
    if not token:
        print("[LOGIN] FALHOU: token nao retornado")
        return None

    print(f"[LOGIN] OK - Token: {token}")
    return token


def test_session_validate(token: str) -> bool:
    result = send_json({
        "action": "SESSION_VALIDATE",
        "token": token
    })

    ok = bool(result.get("success"))
    print(f"[SESSION_VALIDATE] {'OK' if ok else 'FALHOU'} - {result.get('message')}")
    return ok


def test_chat_send(token: str, message: str) -> bool:
    result = send_json({
        "action": "CHAT_SEND",
        "token": token,
        "message": message
    })

    if not result.get("success"):
        print(f"[CHAT_SEND] FALHOU: {result.get('message')}")
        return False

    print("[CHAT_SEND] OK")
    print(f"[IA] {result.get('data')}")
    return True


def main() -> int:
    print("======================================")
    print(" IronWatch TCP Tester")
    print("======================================")
    print(f"Host: {HOST}")
    print(f"Port: {PORT}")

    ping_json_ok = test_ping_json()
    ping_legacy_ok = test_ping_legacy()

    token = test_login(LOGIN, PASSWORD)
    if not token:
        print("\n[FINAL] Falhou no login. Nao adianta continuar, tovarishch (camarada).")
        return 1

    session_ok = test_session_validate(token)
    if not session_ok:
        print("\n[FINAL] Sessao invalidou logo depois do login. Tem rato nesse porao.")
        return 1

    chat_ok = test_chat_send(token, "Me responda apenas com: TESTE_OK")

    print("\n======================================")
    print(" RESUMO")
    print("======================================")
    print(f"PING JSON:   {'OK' if ping_json_ok else 'FALHOU'}")
    print(f"PING LEGACY: {'OK' if ping_legacy_ok else 'FALHOU'}")
    print(f"LOGIN:       {'OK' if token else 'FALHOU'}")
    print(f"SESSAO:      {'OK' if session_ok else 'FALHOU'}")
    print(f"CHAT_SEND:   {'OK' if chat_ok else 'FALHOU'}")

    if all([ping_json_ok, ping_legacy_ok, token is not None, session_ok, chat_ok]):
        print("\n[FINAL] Sistema operacional. A usina nao explodiu.")
        return 0

    print("\n[FINAL] Tem modulo capenga ainda.")
    return 1


if __name__ == "__main__":
    sys.exit(main())