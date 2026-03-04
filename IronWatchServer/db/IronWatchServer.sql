DROP DATABASE IF EXISTS GDS_IronWatch;
CREATE DATABASE GDS_IronWatch CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE GDS_IronWatch;

-- ============================
-- TABELA EMPRESAS
-- ============================
CREATE TABLE empresas (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    razaosocial VARCHAR(255) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    email VARCHAR(255) NOT NULL,
    chave_api VARCHAR(255) NOT NULL UNIQUE,
    promptia TEXT,
    endereco VARCHAR(255) NOT NULL,
    dispositivos_max INT UNSIGNED NOT NULL DEFAULT 5,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================
-- TABELA USUÁRIOS
-- ============================
CREATE TABLE usuarios (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    empresa_id INT UNSIGNED NOT NULL,
    login VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nome VARCHAR(100) NOT NULL DEFAULT 'Sem Nome',
    session_token VARCHAR(255),
    role TINYINT UNSIGNED NOT NULL DEFAULT 0,
    active TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (empresa_id) REFERENCES empresas(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================
-- TABELA SESSOES_ADMIN
-- ============================
CREATE TABLE sessoes_admin (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT UNSIGNED NOT NULL,
    token VARCHAR(255) NOT NULL,
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expira_em DATETIME NOT NULL,
    ativo TINYINT(1) NOT NULL DEFAULT 1,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================
-- TABELA CODIGOS
-- ============================
CREATE TABLE codigos (
    code CHAR(6) NOT NULL PRIMARY KEY,
    tipo VARCHAR(50) NOT NULL,
    empresa_id INT UNSIGNED,
    usado TINYINT(1) NOT NULL DEFAULT 0,
    expira_em DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (empresa_id) REFERENCES empresas(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================
-- TABELA CONSULTAS
-- ============================
CREATE TABLE consultas (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    empresa_id INT UNSIGNED NOT NULL,
    nome VARCHAR(255) NOT NULL,
    motivo TEXT,
    data_marcado DATETIME,
    data_consulta DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (empresa_id) REFERENCES empresas(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================
-- TABELA DISPOSITIVOS
-- ============================
CREATE TABLE dispositivos (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    empresa_id INT UNSIGNED NOT NULL,
    code_used CHAR(6),
    ip VARCHAR(45) NOT NULL,
    ativo TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (empresa_id) REFERENCES empresas(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================
-- TABELA ULTIMA_MENSAGEM_USUARIO
-- ============================
CREATE TABLE ultima_mensagem_usuario (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    empresa_id INT UNSIGNED NOT NULL,
    numero VARCHAR(50) NOT NULL,
    data DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (empresa_id) REFERENCES empresas(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;