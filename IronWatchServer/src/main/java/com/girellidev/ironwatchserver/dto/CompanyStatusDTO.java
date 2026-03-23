package com.girellidev.ironwatchserver.dto;

public class CompanyStatusDTO {

    private int id;
    private String nome;
    private int isActive;

    public CompanyStatusDTO(int id, String nome, int isActive) {
        this.id = id;
        this.nome = nome;
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public int getIsActive() {
        return isActive;
    }
}