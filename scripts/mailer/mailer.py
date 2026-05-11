import sys

def run_morning():
    print("[MAILER] Morning report")

def run_evening():
    print("[MAILER] Evening report")

if __name__ == "__main__":
    mode = sys.argv[1] if len(sys.argv) > 1 else "morning"

    if mode == "morning":
        run_morning()
    elif mode == "evening":
        run_evening()
