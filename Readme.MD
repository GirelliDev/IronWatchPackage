# 🛡️ IronWatch Ecosystem
> Plataforma de gerenciamento remoto e controle empresarial desenvolvida por **GirelliDev**.  
> Um sistema modular, seguro e escalável que conecta administradores, clientes e servidores em uma única infraestrutura.

---

## 🌍 Visão Geral

O **IronWatch** é um ecossistema de monitoramento e administração composto por **cinco módulos principais**, todos interligados:

1. 🧠 **Servidor SuperAdmin** — núcleo central que autentica e gerencia tokens de acesso.  
2. ⚙️ **Servidor Principal (UserServer)** — responsável por lidar com comandos de clientes, status e dados.  
3. 📱 **Aplicativo Android Admin** — interface de controle e gerenciamento móvel para administradores.  
4. 👤 **Aplicativo Android Usuário** — app voltado ao cliente final, com notificações, calendário e agendamentos.  
5. 💻 **Aplicativo PC Usuário** — versão desktop simplificada para controle e comunicação direta com o servidor.

O sistema utiliza **comunicação socket TCP**, **tokens temporários** e **validação em tempo real** entre os módulos.

---

## 🧩 Estrutura do Projeto
