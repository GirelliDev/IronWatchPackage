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

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}