# shared/types.py
from typing import TypedDict, Literal

class User(TypedDict):
    id: str
    name: str
    role: Literal['admin', 'user', 'guest']
