// @ts-nocheck
document.addEventListener("DOMContentLoaded", () => {
    const SERVER_IP = "localhost";
    const SERVER_PORT = "9999";
    const sidebar = document.querySelector(".sidebar");
    const content = document.querySelector(".content");
    const btn = document.querySelector(".menu-btn");
    const pageContainer = document.getElementById("page-container");

    const token = localStorage.getItem("appToken");
    if (!token) {
        renderLogin();
    } else {
        initApp();
    }

    function renderLogin() {
        document.body.innerHTML = `
            <div class="login-container">
                <div class="login-box">
                    <h2>Primeiro Acesso</h2>
                    <p>Insira o App Token para acessar o sistema.</p>
                    <input type="password" id="tokenInput" placeholder="App Token">
                    <button id="loginBtn">Entrar</button>
                </div>
            </div>
        `;
        document.getElementById("loginBtn").addEventListener("click", () => {
            const tokenValue = document.getElementById("tokenInput").value.trim();
            if (!tokenValue) {
                alert("Digite o token para continuar.");
                return;
            }
            localStorage.setItem("appToken", tokenValue);
            location.reload();
        });
    }

    function initApp() {
        btn.addEventListener("click", () => {
            sidebar.classList.toggle("open");
            sidebar.classList.toggle("closed");
            content.classList.toggle("full");
        });

        const menuLinks = document.querySelectorAll(".sidebar nav a");
        menuLinks.forEach(link => {
            link.addEventListener("click", e => {
                e.preventDefault();
                renderPage(link.dataset.page);
                if (window.innerWidth <= 768) {
                    sidebar.classList.remove("open");
                    sidebar.classList.add("closed");
                    content.classList.add("full");
                }
            });
        });

        renderPage("dashboard");
    }

    function renderPage(page) {
        switch (page) {
            case "dashboard":
                pageContainer.innerHTML = dashboardPage();
                setTimeout(loadCompanies, 50);
                break;
            case "create-company":
                pageContainer.innerHTML = createCompanyPage();
                break;
            case "edit-company":
                pageContainer.innerHTML = editCompanyPage();
                setTimeout(loadCompaniesForEdit, 50);
                break;
            case "add-devices":
                pageContainer.innerHTML = addDevicesPage();
                break;
            default:
                pageContainer.innerHTML = "<h2>Bem-vindo ao IronWatch Admin</h2>";
        }
    }

    function dashboardPage() {
        return `
            <h2>Dashboard</h2>
            <div id="companies-list" style="display:flex;flex-wrap:wrap;gap:10px;"></div>
        `;
    }

    async function loadCompanies() {
        const container = document.getElementById("companies-list");
        if (!container) return;
        try {
            const res = await fetch(`http://${SERVER_IP}:${SERVER_PORT}/list-companies`, {
                headers: {
                    "Authorization": `Bearer ${localStorage.getItem("appToken")}`
                }
            });
            const companies = await res.json();
            container.innerHTML = "";
            companies.forEach(c => {
                const div = document.createElement("div");
                div.style.background = "#1e1e1e";
                div.style.padding = "10px 15px";
                div.style.borderRadius = "8px";
                div.style.minWidth = "200px";
                div.style.position = "relative";
                div.innerHTML = `<strong>${c.Nome}</strong>`;
                const dot = document.createElement("div");
                dot.style.width = "10px";
                dot.style.height = "10px";
                dot.style.borderRadius = "50%";
                dot.style.position = "absolute";
                dot.style.top = "50%";
                dot.style.right = "10px";
                dot.style.transform = "translateY(-50%)";
                dot.style.background = c.is_active == 1 ? "green" : "red";
                div.appendChild(dot);
                container.appendChild(div);
            });
        } catch {
            container.innerHTML = "<p>Erro ao carregar empresas.</p>";
        }
    }

    function createCompanyPage() {
        return `
            <h2>Criar Empresa</h2>
            <form id="create-company-form">
                <input id="name" placeholder="Nome" required>
                <input id="email" type="email" placeholder="Email" required>
                <input id="apiKey" placeholder="API Key" required>
                <textarea id="promptIA" placeholder="Prompt IA"></textarea>
                <textarea id="welcomeMsg" placeholder="Mensagem de Boas-vindas"></textarea>
                <textarea id="reminderMsg" placeholder="Mensagem de Lembrete"></textarea>
                <textarea id="confirmMsg" placeholder="Mensagem de Confirmação"></textarea>
                <textarea id="confirmedMsg" placeholder="Mensagem Confirmada"></textarea>
                <button type="submit">Criar</button>
            </form>
        `;
    }

    function editCompanyPage() {
        return `
            <h2>Editar Empresa</h2>
            <select id="companySelect"><option>Carregando...</option></select>
            <div id="editFormContainer" style="margin-top:20px;"></div>
        `;
    }

    async function loadCompaniesForEdit() {
        const select = document.getElementById("companySelect");
        try {
            const res = await fetch(`http://${SERVER_IP}:${SERVER_PORT}/list-companies`, {
                headers: {
                    "Authorization": `Bearer ${localStorage.getItem("appToken")}`
                }
            });
            const companies = await res.json();
            select.innerHTML = `<option value="">Selecione uma empresa</option>` +
                companies.map(c => `<option value="${c.id}">${c.Nome}</option>`).join("");

            select.addEventListener("change", async () => {
                const id = select.value;
                if (!id) return document.getElementById("editFormContainer").innerHTML = "";

                const resp = await fetch(`http://${SERVER_IP}:${SERVER_PORT}/get-company-full/${id}`, {
                    headers: {
                        "Authorization": `Bearer ${localStorage.getItem("appToken")}`
                    }
                });
                const company = await resp.json();
                renderEditForm(company);
            });
        } catch {
            select.innerHTML = `<option>Erro ao carregar</option>`;
        }
    }

    function renderEditForm(company) {
        const container = document.getElementById("editFormContainer");
        container.innerHTML = `
            <input id="editName" value="${company.Nome}" placeholder="Nome">
            <input id="editEmail" type="email" value="${company.Email}" placeholder="Email">
            <input id="editApiKey" value="${company.API_KEY || ''}" placeholder="API Key">
            <textarea id="editPromptIA" placeholder="Prompt IA">${company.PromptIA || ''}</textarea>
            <textarea id="editWelcomeMsg" placeholder="Mensagem de Boas-vindas">${company.welcomeMsg || ''}</textarea>
            <textarea id="editReminderMsg" placeholder="Mensagem de Lembrete">${company.reminderMsg || ''}</textarea>
            <textarea id="editConfirmMsg" placeholder="Mensagem de Confirmação">${company.confirmMsg || ''}</textarea>
            <textarea id="editConfirmedMsg" placeholder="Mensagem Confirmada">${company.confirmedMsg || ''}</textarea>

            <label style="display:flex;align-items:center;gap:10px;">
                <span>Ativa:</span>
                <label class="switch">
                    <input type="checkbox" id="editActive" ${company.is_active == 1 ? "checked" : ""}>
                    <span class="slider"></span>
                </label>
            </label>

            <button id="saveBtn">Salvar</button>
        `;

        const activeSwitch = document.getElementById("editActive");

        document.getElementById("saveBtn").addEventListener("click", async () => {
            const updated = {
                id: company.id,
                Nome: document.getElementById("editName").value,
                Email: document.getElementById("editEmail").value,
                ApiKey: document.getElementById("editApiKey").value,
                PromptIA: document.getElementById("editPromptIA").value,
                welcomeMsg: document.getElementById("editWelcomeMsg").value,
                reminderMsg: document.getElementById("editReminderMsg").value,
                confirmMsg: document.getElementById("editConfirmMsg").value,
                confirmedMsg: document.getElementById("editConfirmedMsg").value,
                is_active: activeSwitch.checked ? 1 : 0
            };

            try {
                const resp = await fetch(`http://${SERVER_IP}:${SERVER_PORT}/update-company`, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${localStorage.getItem("appToken")}`
                    },
                    body: JSON.stringify(updated)
                });
                const r = await resp.json();
                alert(r.success ? "Empresa atualizada!" : "Erro: " + r.message);
                renderPage("dashboard");
                setTimeout(loadCompanies, 50);
            } catch {
                alert("Falha ao atualizar empresa.");
            }
        });
    }

    function addDevicesPage() {
        return `<h2>Adicionar Dispositivos</h2>`;
    }

    document.addEventListener("submit", async e => {
        if (e.target.id === "create-company-form") {
            e.preventDefault();
            const data = {
                name: document.getElementById("name").value,
                email: document.getElementById("email").value,
                apiKey: document.getElementById("apiKey").value,
                promptIA: document.getElementById("promptIA").value,
                welcomeMsg: document.getElementById("welcomeMsg").value,
                reminderMsg: document.getElementById("reminderMsg").value,
                confirmMsg: document.getElementById("confirmMsg").value,
                confirmedMsg: document.getElementById("confirmedMsg").value
            };
            const res = await fetch(`http://${SERVER_IP}:${SERVER_PORT}/create-company`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${localStorage.getItem("appToken")}`
                },
                body: JSON.stringify(data)
            });
            const r = await res.json();
            alert(r.success ? "Empresa criada!" : "Erro: " + r.message);
            renderPage("dashboard");
            setTimeout(loadCompanies, 50);
        }
    });
});