# shared/utils.py
import hashlib
from datetime import datetime

def hash_string(s: str) -> str:
    return hashlib.sha256(s.encode()).hexdigest()

def format_date(dt: datetime, fmt="%Y-%m-%d %H:%M:%S") -> str:
    return dt.strftime(fmt)
