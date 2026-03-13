package com.girellidev.ironwatchserver.dto;

public class CompanyStatusDTO {

    private String Nome;
    private int is_active;

    public CompanyStatusDTO() {
    }

    public CompanyStatusDTO(String nome, int is_active) {
        this.Nome = nome;
        this.is_active = is_active;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        this.Nome = nome;
    }

    public int getIs_active() {
        return is_active;
    }

    public void setIs_active(int is_active) {
        this.is_active = is_active;
    }
}