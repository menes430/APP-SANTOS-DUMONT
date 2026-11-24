package com.santosdumont.models;

public class IdeiaModel {

    private final String emoji;
    private final String titulo;
    private final String descricao;

    public IdeiaModel(String emoji, String titulo, String descricao) {
        this.emoji = emoji;
        this.titulo = titulo;
        this.descricao = descricao;
    }

    public String getEmoji() { return emoji; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
}