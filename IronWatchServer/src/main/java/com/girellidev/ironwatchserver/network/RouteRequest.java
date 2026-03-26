package com.girellidev.ironwatchserver.network;

public class RouteRequest {

    private String action;
    private String token;

    private String login;
    private String password;

    private Integer role;
    private Integer empresaId;

    private String message;

    private Integer companyId;
    private String nome;
    private Integer isActive;

    // novos campos empresa
    private String razaosocial;
    private String telefone;
    private String email;
    private String endereco;
    private Integer dispositivosMax;
    private String promptia;

    // AI config
    private String aiProvider;
    private String aiModel;
    private String aiApiKey;
    private Integer aiActive;

    public String getAction() { return action; }
    public String getToken() { return token; }

    public String getLogin() { return login; }
    public String getPassword() { return password; }

    public Integer getRole() { return role; }
    public Integer getEmpresaId() { return empresaId; }

    public String getMessage() { return message; }

    public Integer getCompanyId() { return companyId; }
    public String getNome() { return nome; }
    public Integer getIsActive() { return isActive; }

    public String getRazaosocial() { return razaosocial; }
    public String getTelefone() { return telefone; }
    public String getEmail() { return email; }
    public String getEndereco() { return endereco; }
    public Integer getDispositivosMax() { return dispositivosMax; }
    public String getPromptia() { return promptia; }

    public String getAiProvider() { return aiProvider; }
    public String getAiModel() { return aiModel; }
    public String getAiApiKey() { return aiApiKey; }
    public Integer getAiActive() { return aiActive; }
}