package com.girellidev.ironwatchserver.model;

import java.time.LocalDateTime;

public class Session {

    private int id;
    private int usuarioId;
    private String token;
    private LocalDateTime expiraEm;
    private boolean ativo;

    public Session() {
    }

    public Session(int id, int usuarioId, String token, LocalDateTime expiraEm, boolean ativo) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.token = token;
        this.expiraEm = expiraEm;
        this.ativo = ativo;
    }

    public int getId() {
        return id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getExpiraEm() {
        return expiraEm;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setExpiraEm(LocalDateTime expiraEm) {
        this.expiraEm = expiraEm;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}