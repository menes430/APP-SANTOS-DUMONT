package com.santosdumont.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Lista de feriados nacionais, estaduais (AL) e municipais (Maceió).
 * Formato da chave: "MM-DD" (mês-dia, com zero à esquerda).
 * Para feriados móveis, a chave é calculada pelo ano.
 */
public class FeriadosHelper {

    // Feriados fixos (MM-DD)
    private static final Map<String, String> FIXOS = new HashMap<>();
    static {
        // Nacionais
        FIXOS.put("01-01", "Ano Novo");
        FIXOS.put("04-21", "Tiradentes");
        FIXOS.put("05-01", "Dia do Trabalho");
        FIXOS.put("09-07", "Independência");
        FIXOS.put("10-12", "N. S. Aparecida");
        FIXOS.put("11-02", "Finados");
        FIXOS.put("11-15", "Proclamação da República");
        FIXOS.put("12-25", "Natal");
        // Estaduais (AL)
        FIXOS.put("09-16", "Emancipação de AL");
        FIXOS.put("06-24", "São João");
        // Municipais (Maceió)
        FIXOS.put("12-05", "N. S. dos Prazeres");
    }

    // Feriados móveis por ano (MM-DD)
    private static final Map<Integer, Map<String, String>> MOVEIS = new HashMap<>();
    static {
        // 2026
        Map<String, String> m2026 = new HashMap<>();
        m2026.put("02-17", "Carnaval");
        m2026.put("04-03", "Sexta-feira Santa");
        m2026.put("06-04", "Corpus Christi");
        MOVEIS.put(2026, m2026);
        // 2027
        Map<String, String> m2027 = new HashMap<>();
        m2027.put("02-09", "Carnaval");
        m2027.put("03-26", "Sexta-feira Santa");
        m2027.put("05-27", "Corpus Christi");
        MOVEIS.put(2027, m2027);
        // 2028
        Map<String, String> m2028 = new HashMap<>();
        m2028.put("02-29", "Carnaval");
        m2028.put("04-14", "Sexta-feira Santa");
        m2028.put("06-15", "Corpus Christi");
        MOVEIS.put(2028, m2028);
    }

    /**
     * Retorna o nome do feriado para a data, ou null se não for feriado.
     */
    public static String getFeriado(int ano, int mes, int dia) {
        String chave = String.format("%02d-%02d", mes + 1, dia);
        // Verifica os móveis primeiro (podem sobrescrever)
        Map<String, String> moveisAno = MOVEIS.get(ano);
        if (moveisAno != null && moveisAno.containsKey(chave)) {
            return moveisAno.get(chave);
        }
        return FIXOS.get(chave);
    }
}
