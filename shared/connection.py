async def init_dbadmin():
    global db_pool_admin
    try:
        db_pool_admin = await aiomysql.create_pool(
            host='localhost', user='root', password='', db='ironwatchadmin',
            minsize=1, maxsize=10
        )
        print("[MYSQL ADMIN] Conectado ao Banco de Dados")
    except Exception as e:
        print(f"[ERRO FATAL] Falha ao conectar ao banco ADMIN: {e}")
        input("Pressione qualquer tecla para encerrar...")
        sys.exit(1)

async def init_dbuser():
    global db_pool_user
    try:
        db_pool_user = await aiomysql.create_pool(
            host='localhost', user='root', password='', db='ironwatch',
            minsize=1, maxsize=10
        )
        print("[MYSQL USER] Conectado ao Banco de Dados")
    except Exception as e:
        print(f"[ERRO FATAL] Falha ao conectar ao banco USER: {e}")
        input("Pressione qualquer tecla para encerrar...")
        sys.exit(1)
