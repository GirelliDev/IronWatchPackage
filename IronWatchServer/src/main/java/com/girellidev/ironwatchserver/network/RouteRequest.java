package com.girellidev.ironwatchserver.network;

public class RouteRequest {

    private String action;

    private String login;
    private String password;

    private String token;
    private String message;

    private Integer role;
    private Integer empresaId;

    public RouteRequest() {
    }

    public String getAction() {
        return action;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }

    public Integer getRole() {
        return role;
    }

    public Integer getEmpresaId() {
        return empresaId;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public void setEmpresaId(Integer empresaId) {
        this.empresaId = empresaId;
    }
}