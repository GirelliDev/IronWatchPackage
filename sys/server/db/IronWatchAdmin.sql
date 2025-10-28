-- DROP E RECRIAÇÃO DAS BASES
DROP DATABASE IF EXISTS ironwatchadmin;

DROP DATABASE IF EXISTS ironwatch;

CREATE DATABASE ironwatchadmin DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

CREATE DATABASE ironwatch DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

-- =========================================
-- BANCO ADMIN
-- =========================================
USE ironwatchadmin;

CREATE TABLE dispositivosadmin (
    id INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ip VARCHAR(100) NOT NULL,
    nome VARCHAR(100) NOT NULL DEFAULT 'GirelliDev',
    senha VARCHAR(100) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) DEFAULT CHARSET = utf8;

CREATE TABLE logsadmin (
    id INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id INT(10) NOT NULL,
    action VARCHAR(255) NOT NULL DEFAULT 'Codigo Bugou',
    quando DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) DEFAULT CHARSET = utf8;

CREATE TABLE logs (
    id INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    empresaid INT(10) NOT NULL,
    mensagem VARCHAR(255) NOT NULL,
    respostaia VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) DEFAULT CHARSET = utf8;

-- =========================================
-- BANCO PRINCIPAL (IRONWATCH)
-- =========================================
USE ironwatch;

CREATE TABLE empresas (
    id INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    razaosocial VARCHAR(255) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    email VARCHAR(255) NOT NULL,
    chave_api VARCHAR(255) NOT NULL,
    promptia VARCHAR(255) NOT NULL,
    endereco VARCHAR(255) NOT NULL,
    mensagem_bemvindo VARCHAR(255) NOT NULL DEFAULT 'Olá {user}, Seja bem-vindo ao sistema GirelliDev',
    mensagem_lembrete VARCHAR(255) NOT NULL DEFAULT 'Olá {user}, Você tem uma Consulta marcada em {horario}',
    mensagem_confirmar VARCHAR(255) NOT NULL DEFAULT 'Antes de Confirmar sua consulta, Confirme as informações:\n data:{data}\n horario:{horario}\n motivo:{motivo}\n Está tudo certo?',
    mensagem_confirmado VARCHAR(255) NOT NULL DEFAULT 'Confirmado!\n Guarde essas informações:\n nome:{user}\n data:{data}\n horario:{horario}\n motivo:{motivo}\n Nos vemos em {dias} dias',
    dispositivos INT(2) NOT NULL DEFAULT 5,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active INT(1) NOT NULL DEFAULT 1
) DEFAULT CHARSET = utf8;

CREATE TABLE dispositivos (
    id INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    empresaid INT(10) NOT NULL,
    code_used VARCHAR(6) NOT NULL,
    ip VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (empresaid) REFERENCES empresas (id) ON DELETE CASCADE
) DEFAULT CHARSET = utf8;

CREATE TABLE codigos (
    code VARCHAR(6) NOT NULL PRIMARY KEY,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) DEFAULT CHARSET = utf8;

CREATE TABLE ultima_mensagem_usuario (
    id INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    empresaid INT(10) NOT NULL,
    numero VARCHAR(50) NOT NULL,
    data TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (empresaid) REFERENCES empresas (id) ON DELETE CASCADE
) DEFAULT CHARSET = utf8;

CREATE TABLE consultas (
    id INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    userid INT(10) NOT NULL,
    empresaid INT(10) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    motivo TEXT,
    data_marcado DATETIME,
    data_consulta DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (empresaid) REFERENCES empresas (id) ON DELETE CASCADE
) DEFAULT CHARSET = utf8;

CREATE TABLE logs (
    id INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    empresaid INT(10) NOT NULL,
    mensagem VARCHAR(255) NOT NULL,
    respostaia VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (empresaid) REFERENCES empresas (id) ON DELETE CASCADE
) DEFAULT CHARSET = utf8;

-- =========================================
-- ÍNDICES
-- =========================================
CREATE INDEX idx_logs_empresa_id ON logs (empresaid);

CREATE INDEX idx_empresas_id ON empresas (id);

CREATE INDEX idx_dispositivos_emp ON dispositivos (empresaid);

CREATE INDEX idx_lastmsg_emp ON ultima_mensagem_usuario (empresaid);

CREATE INDEX idx_consultas_emp ON consultas (empresaid);