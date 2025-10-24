import customtkinter as ctk

# Tema global
ctk.set_appearance_mode("dark")  # ou "light", "system"
ctk.set_default_color_theme("blue")  # ou "green", "dark-blue"

# Janela principal
janela = ctk.CTk()
janela.title("IronWatch Admin")
janela.geometry("600x400")

# Cabeçalho
header = ctk.CTkFrame(janela, height=60, corner_radius=0, fg_color="#2b2b2b")
header.pack(fill="x")
titulo = ctk.CTkLabel(header, text="Painel Principal", font=("Arial", 24, "bold"))
titulo.pack(pady=10)

# Conteúdo
conteudo = ctk.CTkFrame(janela, corner_radius=15)
conteudo.pack(fill="both", expand=True, padx=20, pady=20)

# Botões
botao1 = ctk.CTkButton(conteudo, text="Ação 1", width=200)
botao1.grid(row=0, column=0, padx=10, pady=10)

botao2 = ctk.CTkButton(conteudo, text="Ação 2", width=200)
botao2.grid(row=0, column=1, padx=10, pady=10)

# Entrada de texto
entrada = ctk.CTkEntry(conteudo, placeholder_text="Digite algo aqui...")
entrada.grid(row=1, column=0, columnspan=2, pady=20, sticky="ew")

janela.mainloop()
