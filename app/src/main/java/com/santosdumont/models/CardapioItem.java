package com.santosdumont.models;

public class CardapioItem {
    private final String diaSemana;
    private final String nomePrato;
    private final String complementos;
    private final int energiaKcal;
    private final int proteinas;
    private final int lipidos;
    private final int carboidratos;
    private final int sodioMg;
    private final int semana;

    public CardapioItem(String diaSemana, String nomePrato, String complementos,
                        int energiaKcal, int proteinas, int lipidos,
                        int carboidratos, int sodioMg, int semana) {
        this.diaSemana = diaSemana;
        this.nomePrato = nomePrato;
        this.complementos = complementos;
        this.energiaKcal = energiaKcal;
        this.proteinas = proteinas;
        this.lipidos = lipidos;
        this.carboidratos = carboidratos;
        this.sodioMg = sodioMg;
        this.semana = semana;
    }

    public String getDiaSemana() { return diaSemana; }
    public String getNomePrato() { return nomePrato; }
    public String getComplementos() { return complementos; }
    public int getEnergiaKcal() { return energiaKcal; }
    public int getProteinas() { return proteinas; }
    public int getLipidos() { return lipidos; }
    public int getCarboidratos() { return carboidratos; }
    public int getSodioMg() { return sodioMg; }
    public int getSemana() { return semana; }
}