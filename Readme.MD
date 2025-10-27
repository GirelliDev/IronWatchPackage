# 🛡️ IronWatch Ecosystem
> Plataforma de gerenciamento remoto e controle empresarial desenvolvida por **GirelliDev**.  
> Um sistema modular, seguro e escalável que conecta administradores, clientes e servidores em uma única infraestrutura.

---

## 🌍 Visão Geral

O **IronWatch** é um ecossistema de monitoramento e administração composto por **cinco módulos principais**, todos interligados:

1. 🧠 **Servidor Admin / Principal ** — núcleo central que autentica e gerencia tokens de acesso, linka todo o sistema de aplicativos com só um backend.   
2. 📱 **Aplicativo Android Admin** — interface de controle e gerenciamento móvel para administradores.  
3. 👤 **Aplicativo Android Usuário** — app voltado ao cliente final, com notificações, calendário e agendamentos.  
4. 💻 **Aplicativo PC Usuário** — versão desktop simplificada para controle e comunicação direta com o servidor.

O sistema utiliza **comunicação socket TCP**, **tokens temporários** e **validação em tempo real** entre os módulos.

---

## 🧩 Estrutura do Projeto

IronWatch/
├── /server/
│ ├── SuperAdmin.py # Servidor principal de autenticação e tokens
│ ├── UserServer.py # Servidor de usuários e comandos
│ ├── config.json # Configurações do servidor
│ └── requirements.txt
│
├── /client_admin/
│ ├── app/
│ │ ├── mais pastas.. quando finalizar coloco
│ 
│ 
│ 
│
├── /client_user/
│ ├── app/
│ │ ├── MainActivity.kt
│ │ ├── CalendarFragment.kt
│ │ ├── ChatFragment.kt
│ │ └── models/
│ └── AndroidManifest.xml
│
├── /pc_user/
│ ├── IronWatchClient.py
│ ├── assets/
│ │ └── logo.ico
│ └── requirements.txt
│
├── /docs/
│ ├── documentacão.pdf
│ ├── manual.pdf
│ └── changelog.md
│
└── README.md
