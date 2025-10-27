import asyncio
import random
import string
import time
import json
from typing import Optional, List
import aiomysql
from datetime import datetime

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
