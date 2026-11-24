package com.santosdumont.models;

import java.util.ArrayList;
import java.util.List;

public class ProfessorModel {
    private final String nome;
    private final List<String> disciplinas;
    private final List<String> turmas;

    // Construtor 1 arg (compatibilidade)
    public ProfessorModel(String nome) {
        this.nome = nome;
        this.disciplinas = new ArrayList<>();
        this.turmas = new ArrayList<>();
    }

    // Construtor 3 args (novo)
    public ProfessorModel(String nome, List<String> disciplinas, List<String> turmas) {
        this.nome = nome;
        this.disciplinas = disciplinas == null ? new ArrayList<>() : disciplinas;
        this.turmas = turmas == null ? new ArrayList<>() : turmas;
    }

    public String getNome() { return nome == null ? "" : nome; }
    public List<String> getDisciplinas() { return disciplinas; }
    public List<String> getTurmas() { return turmas; }

    public String getDisciplinasStr() {
        if (disciplinas.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < disciplinas.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(disciplinas.get(i));
        }
        return sb.toString();
    }

    public String getTurmasStr() {
        if (turmas.isEmpty()) return "";
        return turmas.size() + (turmas.size() == 1 ? " turma" : " turmas");
    }
}