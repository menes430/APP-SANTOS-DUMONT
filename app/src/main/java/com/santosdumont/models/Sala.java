package com.santosdumont.models;

public class Sala {
    private String nome;
    private String professor;

    public Sala(String nome, String professor) {
        this.nome = nome;
        this.professor = professor;
    }

    public String getNome() { return nome; }
    public String getProfessor() { return professor; }

    @Override
    public String toString() { return nome; }
}