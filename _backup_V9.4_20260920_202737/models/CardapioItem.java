package com.santosdumont.models;

public class CardapioItem {
    private String diaSemana;
    private String nomePrato;
    private int energiaKcal;
    private int proteinas;
    private int lipidos;
    private int carboidratos;
    private int sodioMg;
    private int semana;

    public CardapioItem(String diaSemana, String nomePrato, int energiaKcal,
                        int proteinas, int lipidos, int carboidratos, int sodioMg, int semana) {
        this.diaSemana = diaSemana;
        this.nomePrato = nomePrato;
        this.energiaKcal = energiaKcal;
        this.proteinas = proteinas;
        this.lipidos = lipidos;
        this.carboidratos = carboidratos;
        this.sodioMg = sodioMg;
        this.semana = semana;
    }

    public String getDiaSemana() { return diaSemana; }
    public String getNomePrato() { return nomePrato; }
    public int getEnergiaKcal() { return energiaKcal; }
    public int getProteinas() { return proteinas; }
    public int getLipidos() { return lipidos; }
    public int getCarboidratos() { return carboidratos; }
    public int getSodioMg() { return sodioMg; }
    public int getSemana() { return semana; }
}