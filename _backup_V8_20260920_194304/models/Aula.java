package com.santosdumont.models;

public class Aula {
    private String horario;
    private String disciplina;
    private String professor;

    public Aula(String horario, String disciplina, String professor) {
        this.horario = horario;
        this.disciplina = disciplina;
        this.professor = professor;
    }

    public String getHorario() { return horario; }
    public String getDisciplina() { return disciplina; }
    public String getProfessor() { return professor; }
}