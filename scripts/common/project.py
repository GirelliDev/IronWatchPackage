from common.utils import normalize_key
import os

PROJECT_TODO = os.getenv("PROJECT_TODO", "A Fazer")
PROJECT_IN_PROGRESS = os.getenv("PROJECT_IN_PROGRESS", "Em andamento")
PROJECT_DONE = os.getenv("PROJECT_DONE", "Concluído")

def is_done_status(status):
    return normalize_key(status) in {
        normalize_key(PROJECT_DONE),
        "done",
        "concluido",
    }

def is_todo_status(status):
    return normalize_key(status) in {
        normalize_key(PROJECT_TODO),
        "todo",
        "afazer",
    }

def is_in_progress_status(status):
    return normalize_key(status) in {
        normalize_key(PROJECT_IN_PROGRESS),
        "inprogress",
        "emandamento",
    }
