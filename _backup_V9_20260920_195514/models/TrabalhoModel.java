package com.santosdumont.models;

public class TrabalhoModel {
    public String id;
    public String nome;
    public String data;
    public String hora;
    public String descricao;
    public String imagemUri;

    public boolean alarmeAtivo;
    public boolean notifDiaSimDiaNao;
    public boolean notifUltimaSemana;
    public boolean notifPosAlarme;
    public int diasPosAlarme;

    public TrabalhoModel() {
        this.diasPosAlarme = 3;
        this.notifDiaSimDiaNao = true;
        this.notifUltimaSemana = true;
        this.alarmeAtivo = true;
    }
}