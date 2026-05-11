import sys

def main():
    mode = sys.argv[1] if len(sys.argv) > 1 else "manager"
    print(f"[MANAGER] Running mode={mode}")

if __name__ == "__main__":
    main()
