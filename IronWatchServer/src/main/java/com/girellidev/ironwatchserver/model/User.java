package com.girellidev.ironwatchserver.model;

public class User {

    private int id;
    private String login;
    private String passwordHash;
    private int role;
    private int empresaId;
    private boolean active;

    public User() {
    }

    public User(int id, String login, String passwordHash, int role, int empresaId, boolean active) {
        this.id = id;
        this.login = login;
        this.passwordHash = passwordHash;
        this.role = role;
        this.empresaId = empresaId;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public int getRole() {
        return role;
    }

    public int getEmpresaId() {
        return empresaId;
    }

    public boolean isActive() {
        return active;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public void setEmpresaId(int empresaId) {
        this.empresaId = empresaId;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}