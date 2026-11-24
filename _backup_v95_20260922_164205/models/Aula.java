package com.santosdumont.models;

public class Aula {
    private final String horario;
    private String disciplina;
    private String professor;
    private boolean semAula;
    private boolean textoLivre;

    public Aula(String horario, String disciplina, String professor) {
        this.horario = horario;
        this.disciplina = disciplina;
        this.professor = professor;
        this.semAula = false;
        this.textoLivre = false;
    }

    public Aula(String horario, String disciplina, String professor,
                boolean semAula, boolean textoLivre) {
        this.horario = horario;
        this.disciplina = disciplina;
        this.professor = professor;
        this.semAula = semAula;
        this.textoLivre = textoLivre;
    }

    public String getHorario() { return horario == null ? "" : horario; }
    public String getDisciplina() { return disciplina == null ? "" : disciplina; }
    public String getProfessor() { return professor == null ? "" : professor; }
    public boolean isSemAula() { return semAula; }
    public boolean isTextoLivre() { return textoLivre; }

    public void setDisciplina(String d) { this.disciplina = d; }
    public void setProfessor(String p) { this.professor = p; }
    public void setSemAula(boolean s) { this.semAula = s; }
    public void setTextoLivre(boolean t) { this.textoLivre = t; }
}