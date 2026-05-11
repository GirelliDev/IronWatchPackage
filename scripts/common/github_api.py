import os
import requests

GITHUB_TOKEN = os.getenv("TOKEN_GITHUB", "")

def github_headers():
    return {
        "Authorization": f"Bearer {GITHUB_TOKEN}",
        "Accept": "application/vnd.github+json",
        "User-Agent": "IronWatch-Actions",
    }

def github_graphql(query, variables=None):
    r = requests.post(
        "https://api.github.com/graphql",
        headers=github_headers(),
        json={"query": query, "variables": variables or {}},
        timeout=60,
    )
    r.raise_for_status()
    data = r.json()

    if data.get("errors"):
        raise RuntimeError(data["errors"])

    return data["data"]
