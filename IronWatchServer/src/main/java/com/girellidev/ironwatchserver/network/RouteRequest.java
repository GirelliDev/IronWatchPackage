package com.girellidev.ironwatchserver.network;

public class RouteRequest {

    private String action;
    private String token;

    private String login;
    private String password;

    private Integer role;
    private Integer empresaId;

    private String message;

    // company
    private Integer company_id;
    private String nome;
    private Integer is_active;

    // empresa
    private String razaosocial;
    private String telefone;
    private String email;
    private String endereco;
    private Integer dispositivos_max;
    private String promptia;

    // AI config
    private String aiProvider;
    private String aiModel;
    private String aiApiKey;
    private String chave_api;
    private Integer aiActive;

    public String getAction() { return action; }
    public String getToken() { return token; }

    public String getLogin() { return login; }
    public String getPassword() { return password; }

    public Integer getRole() { return role; }
    public Integer getEmpresaId() { return empresaId; }

    public String getMessage() { return message; }

    public Integer getCompanyId() { return company_id; }
    public String getNome() { return nome; }
    public Integer getIsActive() { return is_active; }

    public String getRazaosocial() { return razaosocial; }
    public String getTelefone() { return telefone; }
    public String getEmail() { return email; }
    public String getEndereco() { return endereco; }
    public Integer getDispositivosMax() { return dispositivos_max; }
    public String getPromptia() { return promptia; }

    public String getAiProvider() { return aiProvider; }
    public String getAiModel() { return aiModel; }

    public String getAiApiKey() {
        if (aiApiKey != null && !aiApiKey.isBlank()) {
            return aiApiKey;
        }

        if (chave_api != null && !chave_api.isBlank()) {
            return chave_api;
        }

        return null;
    }

    public Integer getAiActive() { return aiActive; }
}