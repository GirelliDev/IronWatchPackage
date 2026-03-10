package com.girellidev.ironwatchserver.core;

public class Usuario {

    private String username;
    private String senhaHash;
    private String permissao;
    private boolean ativo;

    public Usuario(String username, String senhaHash, String permissao, boolean ativo) {
        this.username = username;
        this.senhaHash = senhaHash;
        this.permissao = permissao;
        this.ativo = ativo;
    }

    public String getUsername() {
        return username;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public String getPermissao() {
        return permissao;
    }
    public class Permissao {

    public static final int CLIENTE = 1;
    public static final int EMPRESA = 2;
    public static final int ADMIN = 3;
    public static final int MASTER = 4;

}

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}