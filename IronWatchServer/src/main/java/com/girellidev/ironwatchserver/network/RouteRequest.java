package com.girellidev.ironwatchserver.network;

public class RouteRequest {

    private String action;

    private String login;
    private String password;

    private String token;
    private String message;

    private Integer role;
    private Integer empresaId;

    // Novos campos para CRUD de empresa
    private Integer company_id;
    private String nome;
    private Integer is_active;

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

    public Integer getCompanyId() {
        return company_id;
    }

    public String getNome() {
        return nome;
    }

    public Integer getIsActive() {
        return is_active;
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

    public void setCompanyId(Integer companyId) {
        this.company_id = companyId;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setIsActive(Integer isActive) {
        this.is_active = isActive;
    }

    // Setters extras pra aceitar mapeamento snake_case direto, se o parser usar nome exato
    public void setCompany_id(Integer company_id) {
        this.company_id = company_id;
    }

    public void setIs_active(Integer is_active) {
        this.is_active = is_active;
    }
}