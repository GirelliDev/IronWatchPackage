-- DB principal (já deves ter algumas tabelas, aqui estão as necessárias/novas)
CREATE DATABASE IF NOT EXISTS ia_v3;

USE ia_v3;

-- empresas, dispositivos, mensagens_recebidas, lastmessage_user, logs (se já tens, adapte)
CREATE TABLE IF NOT EXISTS empresas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    api_key_enc TEXT NOT NULL,
    device_limit INT NOT NULL DEFAULT 10,
    email_responsavel VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS dispositivos (
    disp_id INT AUTO_INCREMENT PRIMARY KEY,
    empresa_id INT NOT NULL,
    mac_or_ip VARCHAR(128),
    tipo VARCHAR(64),
    token VARCHAR(512),
    ativo BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (empresa_id) REFERENCES empresas (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS mensagens_recebidas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    empresaid INT NOT NULL,
    numero VARCHAR(64),
    nome VARCHAR(255),
    texto LONGTEXT,
    tipo ENUM('audio', 'texto', 'media') DEFAULT 'texto',
    data DATETIME DEFAULT CURRENT_TIMESTAMP,
    processed BOOLEAN DEFAULT FALSE,
    response_id BIGINT NULL,
    FOREIGN KEY (empresaid) REFERENCES empresas (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS lastmessage_user (
    userid BIGINT PRIMARY KEY,
    empresaid INT NOT NULL,
    numero VARCHAR(64),
    data DATETIME,
    FOREIGN KEY (empresaid) REFERENCES empresas (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    empresaid INT NULL,
    disp_id INT NULL,
    nivel ENUM(
        'INFO',
        'WARN',
        'ERROR',
        'SEC'
    ) DEFAULT 'INFO',
    mensagem TEXT,
    meta JSON,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- fila persistente: jobs
CREATE TABLE IF NOT EXISTS jobs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    empresaid INT NOT NULL,
    disp_id INT NULL,
    mensagem_id BIGINT NULL,
    tipo ENUM(
        'transcribe',
        'openai',
        'deliver'
    ) NOT NULL,
    payload JSON,
    status ENUM(
        'pending',
        'processing',
        'done',
        'failed'
    ) DEFAULT 'pending',
    attempts INT DEFAULT 0,
    last_error TEXT,
    locked_by VARCHAR(128) NULL,
    locked_at DATETIME NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);