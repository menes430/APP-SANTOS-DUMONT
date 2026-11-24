package com.santosdumont.models;

import java.util.ArrayList;
import java.util.List;

public class ProfessorModel {
    private String nome;
    private List<String> disciplinas;
    private List<String> turmas;

    public ProfessorModel(String nome) {
        this.nome = nome;
        this.disciplinas = new ArrayList<>();
        this.turmas = new ArrayList<>();
    }

    public void addDisciplina(String disciplina) { this.disciplinas.add(disciplina); }
    public void addTurma(String turma) { this.turmas.add(turma); }

    public String getNome() { return nome; }
    public List<String> getDisciplinas() { return disciplinas; }
    public List<String> getTurmas() { return turmas; }

    public String getDisciplinasStr() {
        return String.join(", ", disciplinas);
    }
}