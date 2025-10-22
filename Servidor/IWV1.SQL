-- Criar banco
CREATE DATABASE IF NOT EXISTS ironwatchv1 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ironwatchv1;

-- Tabela de Empresas
CREATE TABLE IF NOT EXISTS Empresas (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    Nome VARCHAR(255) NOT NULL,
    API_KEY VARCHAR(255) NOT NULL,
    QuantidadeDispositivos INT DEFAULT 5,
    Email VARCHAR(255) NOT NULL,
    PromptIA TEXT NOT NULL,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `is_active` enum('0', '1') DEFAULT 1
);

-- Tabela de Dispositivos
CREATE TABLE IF NOT EXISTS Dispositivos (
    DispId INT AUTO_INCREMENT PRIMARY KEY,
    EmpresaID INT NOT NULL,
    MacIp VARCHAR(100),
    Tipo VARCHAR(50),
    PairCode VARCHAR(6),
    Paired TINYINT(1) DEFAULT 0,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (EmpresaID) REFERENCES Empresas (ID) ON DELETE CASCADE
);

-- Tabela de Mensagens Placeholder (de exemplo: bem-vindo, lembrete, etc)
CREATE TABLE IF NOT EXISTS Mensagens_Placeholder (
    MensagemId INT AUTO_INCREMENT PRIMARY KEY,
    EmpresaID INT NOT NULL,
    Texto TEXT NOT NULL,
    Tipo VARCHAR(50) NOT NULL,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (EmpresaID) REFERENCES Empresas (ID) ON DELETE CASCADE
);

-- Tabela de Mensagens Recebidas
CREATE TABLE IF NOT EXISTS Mensagens_Recebidas (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    EmpresaID INT NOT NULL,
    Numero VARCHAR(50) NOT NULL,
    Nome VARCHAR(255),
    Texto TEXT,
    Data TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (EmpresaID) REFERENCES Empresas (ID) ON DELETE CASCADE
);

-- Tabela LastMessage_User
CREATE TABLE IF NOT EXISTS LastMessage_User (
    UserID INT AUTO_INCREMENT PRIMARY KEY,
    EmpresaID INT NOT NULL,
    Numero VARCHAR(50) NOT NULL,
    Data TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (EmpresaID) REFERENCES Empresas (ID) ON DELETE CASCADE
);

-- Tabela de Consultas
CREATE TABLE IF NOT EXISTS Consultas (
    ConsID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NOT NULL,
    EmpresaID INT NOT NULL,
    Nome VARCHAR(255) NOT NULL,
    Motivo TEXT,
    DataMarcado DATETIME,
    DataConsulta DATETIME,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (EmpresaID) REFERENCES Empresas (ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS SuperAdminDevices (
    SuperId INT AUTO_INCREMENT PRIMARY KEY,
    MacIp VARCHAR(100) DEFAULT NULL,
    PairCode VARCHAR(10) NOT NULL,
    Paired TINYINT(1) DEFAULT 0,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (EmpresaID) REFERENCES Empresas (ID) ON DELETE CASCADE
);

-- Indexes para performance
CREATE INDEX idx_empresas_id ON Empresas (ID);

CREATE INDEX idx_dispositivos_emp ON Dispositivos (EmpresaID);

CREATE INDEX idx_msgs_emp ON Mensagens_Recebidas (EmpresaID);

CREATE INDEX idx_lastmsg_emp ON LastMessage_User (EmpresaID);

CREATE INDEX idx_consultas_emp ON Consultas (EmpresaID);