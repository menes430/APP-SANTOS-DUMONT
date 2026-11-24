package com.santosdumont.models;

public class CardHomeModel {
    private final String id;          // identificador unico (ex: "trabalhos")
    private final String titulo;
    private final String subtitulo;
    private final int corFundo;       // cor em hex (0xFF...)
    private final int corTexto;
    private final String icone;       // nome do drawable (ex: "ic_book")

    public CardHomeModel(String id, String titulo, String subtitulo,
                         int corFundo, int corTexto, String icone) {
        this.id = id;
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.corFundo = corFundo;
        this.corTexto = corTexto;
        this.icone = icone;
    }

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getSubtitulo() { return subtitulo; }
    public int getCorFundo() { return corFundo; }
    public int getCorTexto() { return corTexto; }
    public String getIcone() { return icone; }
}