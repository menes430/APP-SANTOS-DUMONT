package com.santosdumont.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.santosdumont.models.Aula;
import com.santosdumont.models.CardapioItem;
import com.santosdumont.models.HorarioComSala;
import com.santosdumont.models.ProfessorModel;
import com.santosdumont.models.Sala;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataHelper {

    private static final String PREFS = "horarios_prefs";
    private static final String KEY = "horarios_json";

    // Horários fixos do dia
    public static final String H1 = " 7:00";
    public static final String H2 = " 8:00";
    public static final String H3 = " 9:00";
    public static final String H_INT = " 9:50-10:10";
    public static final String H4 = " 10:10";
    public static final String H5 = " 11:00";
    public static final String H_SAIDA = " 11:50";

    // ==========================================
    // SALAS (9)
    // ==========================================
    public static List<Sala> getTurmas() {
        List<Sala> lista = new ArrayList<>();
        lista.add(new Sala("1A MAT", ""));
        lista.add(new Sala("1B MAT", ""));
        lista.add(new Sala("2A MAT", ""));
        lista.add(new Sala("2B MAT", ""));
        lista.add(new Sala("2C MAT", ""));
        lista.add(new Sala("2D MAT", ""));
        lista.add(new Sala("3A MAT", ""));
        lista.add(new Sala("3B MAT", ""));
        lista.add(new Sala("3C MAT", ""));

        // Turmas da TARDE (VES)
        lista.add(new Sala("1A VES", ""));
        lista.add(new Sala("1B VES", ""));
        lista.add(new Sala("1C VES", ""));
        lista.add(new Sala("1D VES", ""));
        lista.add(new Sala("2A VES", ""));
        lista.add(new Sala("2B VES", ""));
        lista.add(new Sala("3A VES", ""));
        lista.add(new Sala("3B VES", ""));
        lista.add(new Sala("3C VES", ""));
        return lista;
    }

    // ==========================================
// PADRÃO DE HORÁRIOS POR TURMA
    // ==========================================
    private static Map<String, List<Aula>> padrao() {
        Map<String, List<Aula>> h = new LinkedHashMap<>();
        String[] dias = {"Segunda", "Terça", "Quarta", "Quinta", "Sexta"};
        for (String d : dias) {
            h.put(d, new ArrayList<>());
        }
        return h;
    }

    // ==========================================
// GRADE COMPLETA (turma -> dia -> aulas)
    // Fonte: lista de professores enviada pelo Tavin
    // ==========================================
    private static final Map<String, Map<String, List<Aula>>> GRADE = new HashMap<>();

    static {
        // ==========================================
        // 1A MAT
        // ==========================================
        Map<String, List<Aula>> t1A = new LinkedHashMap<>();

        t1A.put("Segunda", mk(
                a(H1, "BIO", "Dayse", "1A"),
                a(H2, "BIO", "Dayse", "1A"),
                a(H3, "MAT", "Ana Paula", "1A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "MAT", "Ana Paula", "1A"),
                a(H5, "FÍS", "Herbert", "1A"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t1A.put("Terça", mk(
                a(H1, "PORT", "Rozi", "1A"),
                a(H2, "PORT", "Rozi", "1A"),
                a(H3, "EF", "Adriano", "1A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PIT", "Adriano", "1A"),
                a(H5, "PIT", "Adriano", "1A"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t1A.put("Quarta", mk(
                a(H1, "FIL", "Márcia", "1A"),
                a(H2, "MAT", "Ana Paula", "1A"),
                a(H3, "MAT", "Ana Paula", "1A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "HIS", "Roberto", "1A"),
                a(H5, "ING", "Robson", "1A"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t1A.put("Quinta", mk(
                a(H1, "PORT", "Rozi", "1A"),
                a(H2, "PORT", "Rozi", "1A"),
                a(H3, "ART", "Henrique", "1A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "SOC", "Ana Luiza", "1A"),
                a(H5, "QUI", "Heloíza", "1A"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t1A.put("Sexta", mk(
                a(H1, "TAC", "Dayse", "1A"),
                a(H2, "TAC", "Dayse", "1A"),
                a(H3, "SEM AULA", "-", "-"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "GEO", "Moral", "1A"),
                a(H5, "PDV", "Thayse", "1A"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        GRADE.put("1A MAT", t1A);

        // ==========================================
        // 1B MAT
        // ==========================================
        Map<String, List<Aula>> t1B = new LinkedHashMap<>();

        t1B.put("Segunda", mk(
                a(H1, "MAT", "Ana Paula", "1B"),
                a(H2, "MAT", "Ana Paula", "1B"),
                a(H3, "SEM AULA", "-", "-"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "FÍS", "Herbert", "1B"),
                a(H5, "TAC", "Dayse", "1B"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t1B.put("Terça", mk(
                a(H1, "ART", "Thayse", "1B"),
                a(H2, "MAT", "Ana Paula", "1B"),
                a(H3, "MAT", "Ana Paula", "1B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PORT", "Rozi", "1B"),
                a(H5, "PORT", "Rozi", "1B"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t1B.put("Quarta", mk(
                a(H1, "PDV", "Ana Paula", "1B"),
                a(H2, "PIT", "Débora", "1B"),
                a(H3, "PIT", "Débora", "1B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "BIO", "Thays", "1B"),
                a(H5, "BIO", "Thays", "1B"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t1B.put("Quinta", mk(
                a(H1, "QUI", "Heloíza", "1B"),
                a(H2, "FIL", "Márcia", "1B"),
                a(H3, "PORT", "Rozi", "1B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PORT", "Rozi", "1B"),
                a(H5, "HIS", "Roberto", "1B"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t1B.put("Sexta", mk(
                a(H1, "ING", "Robson", "1B"),
                a(H2, "EF", "Douglas", "1B"),
                a(H3, "SOC", "Ana Luiza", "1B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "TAC", "Dayse", "1B"),
                a(H5, "GEO", "Moral", "1B"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        GRADE.put("1B MAT", t1B);

        // ==========================================
        // 2A MAT
        // ==========================================
        Map<String, List<Aula>> t2A = new LinkedHashMap<>();

        t2A.put("Segunda", mk(
                a(H1, "FÍS", "Camila", "2A"),
                a(H2, "PORT", "Luciano", "2A"),
                a(H3, "PORT", "Luciano", "2A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "BIO", "Dayse", "2A"),
                a(H5, "MAT", "Ana Paula", "2A"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t2A.put("Terça", mk(
                a(H1, "MAT", "Ana Paula", "2A"),
                a(H2, "ART", "Thayse", "2A"),
                a(H3, "ING", "Claudia", "2A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "MAT", "Ana Paula", "2A"),
                a(H5, "MAT", "Ana Paula", "2A"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t2A.put("Quarta", mk(
                a(H1, "PORT", "Luciano", "2A"),
                a(H2, "PORT", "Luciano", "2A"),
                a(H3, "PDV", "Roberto", "2A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "EF", "Suyllane", "2A"),
                a(H5, "APROF", "Cássia", "2A"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t2A.put("Quinta", mk(
                a(H1, "HIS", "Roberto", "2A"),
                a(H2, "QUI", "Jhosi", "2A"),
                a(H3, "SOC", "Ana Luiza", "2A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PRAT", "Josenildo", "2A"),
                a(H5, "PRAT", "Josenildo", "2A"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t2A.put("Sexta", mk(
                a(H1, "GEO", "Moral", "2A"),
                a(H2, "QUI", "Jhosi", "2A"),
                a(H3, "FIL", "Márcia", "2A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "SEM AULA", "-", "2A"),
                a(H5, "APROF", "C\u00e1ssia", "2A"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        GRADE.put("2A MAT", t2A);

        // ==========================================
        // 2B MAT
        // ==========================================
        Map<String, List<Aula>> t2B = new LinkedHashMap<>();

        t2B.put("Segunda", mk(
                a(H1, "FÍS", "Camila", "2B"),
                a(H2, "FÍS", "Camila", "2B"),
                a(H3, "PDV", "Herbert", "2B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PRAT", "Heloíza", "2B"),
                a(H5, "PRAT", "Heloíza", "2B"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t2B.put("Terça", mk(
                a(H1, "MAT", "Jailson", "2B"),
                a(H2, "MAT", "Jailson", "2B"),
                a(H3, "GEO", "Moral", "2B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "ING", "Claudia", "2B"),
                a(H5, "ART", "Thayse", "2B"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t2B.put("Quarta", mk(
                a(H1, "APROF", "Jailson", "2B"),
                a(H2, "EF", "Suyllane", "2B"),
                a(H3, "BIO", "Thays", "2B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PORT", "Danilo", "2B"),
                a(H5, "PORT", "Danilo", "2B"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t2B.put("Quinta", mk(
                a(H1, "MAT", "Jailson", "2B"),
                a(H2, "HIS", "Roberto", "2B"),
                a(H3, "QUI", "Jhosi", "2B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "APROF", "Jailson", "2B"),
                a(H5, "MAT", "Jailson", "2B"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t2B.put("Sexta", mk(
                a(H1, "FIL", "Márcia", "2B"),
                a(H2, "SOC", "Ana Luiza", "2B"),
                a(H3, "QUI", "Jhosi", "2B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PORT", "Danilo", "2B"),
                a(H5, "PORT", "Danilo", "2B"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        GRADE.put("2B MAT", t2B);

        // ==========================================
        // 2C MAT
        // ==========================================
        Map<String, List<Aula>> t2C = new LinkedHashMap<>();

        t2C.put("Segunda", mk(
                a(H1, "ART", "Cícera", "2C"),
                a(H2, "SEM AULA", "-", "-"),
                a(H3, "MAT", "Jailson", "2C"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "MAT", "Jailson", "2C"),
                a(H5, "MAT", "Jailson", "2C"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t2C.put("Terça", mk(
                a(H1, "ING", "Claudia", "2C"),
                a(H2, "APROF", "Moral", "2C"),
                a(H3, "BIO", "Dayse", "2C"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "FÍS", "Camila", "2C"),
                a(H5, "APROF", "Moral", "2C"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t2C.put("Quarta", mk(
                a(H1, "PDV", "Débora", "2C"),
                a(H2, "PORT", "Danilo", "2C"),
                a(H3, "PORT", "Danilo", "2C"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PRAT", "Robson", "2C"),
                a(H5, "EF", "Suyllane", "2C"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t2C.put("Quinta", mk(
                a(H1, "QUI", "Jhosi", "2C"),
                a(H2, "MAT", "Jailson", "2C"),
                a(H3, "HIS", "Roberto", "2C"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PORT", "Danilo", "2C"),
                a(H5, "PORT", "Danilo", "2C"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t2C.put("Sexta", mk(
                a(H1, "SOC", "Ana Luiza", "2C"),
                a(H2, "FIL", "Márcia", "2C"),
                a(H3, "GEO", "Moral", "2C"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "QUI", "Jhosi", "2C"),
                a(H5, "PRAT", "Robson", "2C"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        GRADE.put("2C MAT", t2C);

        // ==========================================
        // 2D MAT
        // ==========================================
        Map<String, List<Aula>> t2D = new LinkedHashMap<>();

        t2D.put("Segunda", mk(
                a(H1, "QUI", "Heloíza", "2D"),
                a(H2, "QUI", "Heloíza", "2D"),
                a(H3, "FÍS", "Camila", "2D"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "APROF", "Niedja", "2D"),
                a(H5, "APROF", "Niedja", "2D"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t2D.put("Terça", mk(
                a(H1, "GEO", "Moral", "2D"),
                a(H2, "ING", "Claudia", "2D"),
                a(H3, "MAT", "Jailson", "2D"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PDV", "Thayse", "2D"),
                a(H5, "SEM AULA", "-", "-"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t2D.put("Quarta", mk(
                a(H1, "EF", "Suylane", "2D"),
                a(H2, "FIL", "Márcia", "2D"),
                a(H3, "MAT", "Jailson", "2D"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "MAT", "Jailson", "2D"),
                a(H5, "HIS", "Roberto", "2D"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t2D.put("Quinta", mk(
                a(H1, "PORT", "Danilo", "2D"),
                a(H2, "PORT", "Danilo", "2D"),
                a(H3, "MAT", "Jailson", "2D"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "ART", "Henrique", "2D"),
                a(H5, "PRAT", "Robson", "2D"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t2D.put("Sexta", mk(
                a(H1, "PORT", "Danilo", "2D"),
                a(H2, "PORT", "Danilo", "2D"),
                a(H3, "BIO", "Dayse", "2D"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PRAT", "Robson", "2D"),
                a(H5, "SOC", "Ana Luiza", "2D"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        GRADE.put("2D MAT", t2D);

        // ==========================================
        // 3A MAT
        // ==========================================
        Map<String, List<Aula>> t3A = new LinkedHashMap<>();

        t3A.put("Segunda", mk(
                a(H1, "APROF", "Niedja", "3A"),
                a(H2, "APROF", "Niedja", "3A"),
                a(H3, "PORT", "Judy", "3A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PORT", "Judy", "3A"),
                a(H5, "F\u00cdS", "Camila", "3A"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t3A.put("Terça", mk(
                a(H1, "BIO", "Dayse", "3A"),
                a(H2, "SEM AULA", "-", "-"),
                a(H3, "ART", "Thayse", "3A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PDV", "Moral", "3A"),
                a(H5, "FÍS", "Camila", "3A"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t3A.put("Quarta", mk(
                a(H1, "PORT", "Judy", "3A"),
                a(H2, "PORT", "Judy", "3A"),
                a(H3, "MAT", "Cássia", "3A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "MAT", "Cássia", "3A"),
                a(H5, "GEO", "Jéssica", "3A"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t3A.put("Quinta", mk(
                a(H1, "FIL", "Márcia", "3A"),
                a(H2, "SOC", "Ana Luiza", "3A"),
                a(H3, "PRAT", "Robson", "3A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PRAT", "Robson", "3A"),
                a(H5, "QUI", "Jhosi", "3A"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t3A.put("Sexta", mk(
                a(H1, "MAT", "Cássia", "3A"),
                a(H2, "MAT", "Cássia", "3A"),
                a(H3, "ING", "Robson", "3A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "HIS", "Niedja", "3A"),
                a(H5, "EF", "Douglas", "3A"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        GRADE.put("3A MAT", t3A);

        // ==========================================
        // 3B MAT
        // ==========================================
        Map<String, List<Aula>> t3B = new LinkedHashMap<>();

        t3B.put("Segunda", mk(
                a(H1, "PORT", "Judy", "3B"),
                a(H2, "PORT", "Judy", "3B"),
                a(H3, "PRAT", "Heloíza", "3B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "FÍS", "Camila", "3B"),
                a(H5, "PDV", "Judy", "3B"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t3B.put("Terça", mk(
                a(H1, "SEM AULA", "-", "-"),
                a(H2, "BIO", "Dayse", "3B"),
                a(H3, "FÍS", "Camila", "3B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "APROF", "Dayse", "3B"),
                a(H5, "APROF", "Dayse", "3B"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t3B.put("Quarta", mk(
                a(H1, "MAT", "Cássia", "3B"),
                a(H2, "MAT", "Cássia", "3B"),
                a(H3, "GEO", "Jéssica", "3B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PORT", "Judy", "3B"),
                a(H5, "PORT", "Judy", "3B"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t3B.put("Quinta", mk(
                a(H1, "SOC", "Ana Luiza", "3B"),
                a(H2, "ART", "Henrique", "3B"),
                a(H3, "PRAT", "Heloíza", "3B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "QUI", "Jhosi", "3B"),
                a(H5, "FIL", "Márcia", "3B"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t3B.put("Sexta", mk(
                a(H1, "EF", "Douglas", "3B"),
                a(H2, "ING", "Robson", "3B"),
                a(H3, "MAT", "Cássia", "3B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "MAT", "Cássia", "3B"),
                a(H5, "HIS", "Niedja", "3B"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        GRADE.put("3B MAT", t3B);

        // ==========================================
        // 3C MAT
        // ==========================================
        Map<String, List<Aula>> t3C = new LinkedHashMap<>();

        t3C.put("Segunda", mk(
                a(H1, "MAT", "Jailson", "3C"),
                a(H2, "MAT", "Jailson", "3C"),
                a(H3, "BIO", "Dayse", "3C"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PORT", "Luciano", "3C"),
                a(H5, "PORT", "Luciano", "3C"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t3C.put("Terça", mk(
                a(H1, "FÍS", "Camila", "3C"),
                a(H2, "FÍS", "Camila", "3C"),
                a(H3, "SEM AULA", "-", "-"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "MAT", "Jailson", "3C"),
                a(H5, "MAT", "Jailson", "3C"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t3C.put("Quarta", mk(
                a(H1, "APROF", "Jéssica", "3C"),
                a(H2, "APROF", "Jéssica", "3C"),
                a(H3, "PORT", "Luciano", "3C"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "GEO", "Jéssica", "3C"),
                a(H5, "PORT", "Luciano", "3C"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t3C.put("Quinta", mk(
                a(H1, "ART", "Henrique", "3C"),
                a(H2, "ING", "Robson", "3C"),
                a(H3, "PRAT", "Márcia", "3C"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PRAT", "Márcia", "3C"),
                a(H5, "FIL", "Márcia", "3C"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        t3C.put("Sexta", mk(
                a(H1, "QUI", "Jhosi", "3C"),
                a(H2, "HIS", "Moral", "3C"),
                a(H3, "PDV", "Douglas", "3C"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "EF", "Douglas", "3C"),
                a(H5, "SOC", "Ana Luiza", "3C"),
                a(H_SAIDA, "SAÍDA", "-", "-")
        ));

        GRADE.put("3C MAT", t3C);

        // ============================================================
        // CARREGA GRADE DAS TURMAS VES (VESPERTINO)
        // ============================================================
        carregarGradeVES();
    }

    // Helpers para encurtar a definição
    private static Aula a(String h, String d, String p, String sala) {
        return new Aula(h, d, p, sala);
    }
    private static List<Aula> mk(Aula... aulas) {
        List<Aula> l = new ArrayList<>();
        for (Aula a : aulas) l.add(a);
        return l;
    }

    // ==========================================
    // JSON (customização do usuário)
    // ==========================================
    private static Map<String, List<Aula>> lerJSON(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String json = sp.getString(KEY, null);
        if (json == null || json.isEmpty()) return null;
        try {
            Map<String, List<Aula>> mapa = new LinkedHashMap<>();
            JSONObject root = new JSONObject(json);
            java.util.Iterator<String> keys = root.keys();
            while (keys.hasNext()) {
                String dia = keys.next();
                JSONArray arr = root.getJSONArray(dia);
                List<Aula> aulas = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    Aula au = new Aula(
                            o.optString("horario"),
                            o.optString("disciplina"),
                            o.optString("professor"),
                            o.optString("sala"),
                            o.optBoolean("semAula", false),
                            o.optBoolean("textoLivre", false)
                    );
                    aulas.add(au);
                }
                mapa.put(dia, aulas);
            }
            return mapa;
        } catch (JSONException e) {
            return null;
        }
    }

    public static void salvar(Context ctx, Map<String, List<Aula>> mapa) {
        try {
            JSONObject root = new JSONObject();
            for (Map.Entry<String, List<Aula>> e : mapa.entrySet()) {
                JSONArray arr = new JSONArray();
                for (Aula a : e.getValue()) {
                    JSONObject o = new JSONObject();
                    o.put("horario", a.getHorario());
                    o.put("disciplina", a.getDisciplina());
                    o.put("professor", a.getProfessor());
                    o.put("sala", a.getSala());
                    o.put("semAula", a.isSemAula());
                    o.put("textoLivre", a.isTextoLivre());
                    arr.put(o);
                }
                root.put(e.getKey(), arr);
            }
            SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            sp.edit().putString(KEY, root.toString()).apply();
        } catch (JSONException ignored) {}
    }

    // ==========================================
    // API PRINCIPAL
    // ==========================================
    public static Map<String, List<Aula>> getTodos(Context ctx, String turma) {
// 1) Customização salva?
        Map<String, List<Aula>> doJson = lerJSON(ctx);
        if (doJson != null && !doJson.isEmpty()) return doJson;

        // 2) Grade padrão
        Map<String, List<Aula>> g = GRADE.get(turma);
        if (g != null) return g;

        // 3) Fallback
        return padrao();
    }

    // N18: Centraliza o nome das materias (apenas APROF e PDV ficam resumidas)
    public static String getNomeMateriaCompleto(String sigla) {
        if (sigla == null) return "";
        String disc = sigla.trim().toUpperCase();
        switch (disc) {
            case "MAT": return "Matemática";
            case "PORT": return "Português";
            case "ART": return "Arte";
            case "HIS": return "História";
            case "GEO": return "Geografia";
            case "FIS": case "FÍS": return "Física";
            case "QUI": return "Química";
            case "BIO": return "Biologia";
            case "ING": return "Inglês";
            case "EDF": case "EF": return "Educação Física";
            case "FIL": return "Filosofia";
            case "SOC": return "Sociologia";
            case "APROF": return "APROF";
            case "PDV": return "PDV";
            default: return sigla; // Se for texto livre, mantem o que o usuario digitou
        }
    }

    public static List<Aula> getHorarios(Context ctx, String turma, String dia) {
        List<Aula> aulas = getTodos(ctx, turma).get(dia);
        return aulas == null ? new ArrayList<>() : aulas;
    }

    public static void atualizarAula(Context ctx, String turma, String dia, int posicao, Aula nova) {
        Map<String, List<Aula>> todos = getTodos(ctx, turma);
        List<Aula> aulas = todos.get(dia);
        if (aulas == null) return;
        if (posicao < 0 || posicao >= aulas.size()) return;
        aulas.set(posicao, nova);
        salvar(ctx, todos);
    }

    public static void resetarAula(Context ctx, String turma, String dia, int posicao) {
        Map<String, List<Aula>> padraoMapa = GRADE.get(turma);
        if (padraoMapa == null) return;
        List<Aula> aulasPadrao = padraoMapa.get(dia);
        if (aulasPadrao == null) return;
        if (posicao < 0 || posicao >= aulasPadrao.size()) return;

        Map<String, List<Aula>> todos = getTodos(ctx, turma);
        List<Aula> aulas = todos.get(dia);
        if (aulas == null) return;
        aulas.set(posicao, aulasPadrao.get(posicao));
        salvar(ctx, todos);
    }

    public static void migrarChavesAntigas(Context ctx) {
        try {
            SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            String json = sp.getString(KEY, null);
            if (json == null || json.isEmpty()) return;
            String jsonCorrigido = json.replace("Ter\u00e7a", "Terça");
            if (!jsonCorrigido.equals(json)) {
                sp.edit().putString(KEY, jsonCorrigido).commit();
            }
        } catch (Exception ignored) {}
    }

    public static void resetarTudo(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        sp.edit().remove(KEY).apply();
    }

    public static Aula getAulaPadrao(String turma, String dia, int posicao) {
        Map<String, List<Aula>> g = GRADE.get(turma);
        if (g == null) return null;
        List<Aula> aulas = g.get(dia);
        if (aulas == null || posicao < 0 || posicao >= aulas.size()) return null;
        return aulas.get(posicao);
    }

    // ==========================================
    // PROFESSORES (todos do dia, de todas as turmas)
    // ==========================================
    public static List<ProfessorModel> getProfessoresDoDia(Context ctx, String dia) {
        return getProfessoresDoDia(ctx, dia, null);
    }

    public static List<ProfessorModel> getProfessoresDoDia(Context ctx, String dia, String turno) {
        Map<String, ProfessorModel> porNome = new LinkedHashMap<>();

        for (String turma : GRADE.keySet()) {
            if (turno != null) {
                if ("manha".equals(turno) && !turma.endsWith("MAT")) continue;
                if ("tarde".equals(turno) && !turma.endsWith("VES")) continue;
            }
            List<Aula> aulas = GRADE.get(turma).get(dia);
            if (aulas == null) continue;

            for (Aula a : aulas) {
                if (a.isSemAula()) continue;
                if (a.getDisciplina().equalsIgnoreCase("INTERVALO")) continue;
                if (a.getDisciplina().equalsIgnoreCase("SAÍDA")) continue;

                String nome = a.getProfessor();
                if (nome == null || nome.isEmpty() || nome.equals("-")) continue;

                if (!porNome.containsKey(nome)) {
                    List<String> disc = new ArrayList<>();
                    disc.add(a.getDisciplina());
                    List<String> turmas = new ArrayList<>();
                    turmas.add(turma);
                    porNome.put(nome, new ProfessorModel(nome, disc, turmas));
                } else {
                    ProfessorModel p = porNome.get(nome);
                    if (!p.getDisciplinas().contains(a.getDisciplina())) {
                        p.getDisciplinas().add(a.getDisciplina());
                    }
                    if (!p.getTurmas().contains(turma)) {
                        p.getTurmas().add(turma);
                    }
                }
            }
        }

        return new ArrayList<>(porNome.values());
    }

    // ==========================================
// HORÁRIOS DO PROFESSOR NO DIA
    // ==========================================
    public static List<HorarioComSala> getHorariosProfessor(Context ctx, String professor, String dia) {
        return getHorariosProfessor(ctx, professor, dia, null);
    }

    public static List<HorarioComSala> getHorariosProfessor(Context ctx, String professor, String dia, String turno) {
        List<HorarioComSala> result = new ArrayList<>();

        for (String turma : GRADE.keySet()) {
            if (turno != null) {
                if ("manha".equals(turno) && !turma.endsWith("MAT")) continue;
                if ("tarde".equals(turno) && !turma.endsWith("VES")) continue;
            }
            List<Aula> aulas = GRADE.get(turma).get(dia);
            if (aulas == null) continue;

            for (Aula a : aulas) {
                if (a.isSemAula()) continue;
                if (a.getDisciplina().equalsIgnoreCase("INTERVALO")) continue;
                if (a.getDisciplina().equalsIgnoreCase("SAÍDA")) continue;
                if (!professor.equals(a.getProfessor())) continue;
                result.add(new HorarioComSala(a.getHorario(), a.getDisciplina(), turma));
            }
        }

// Ordena por horário
        java.util.Collections.sort(result, (h1, h2) -> {
            int m1 = minutoDoHorario(h1.horario);
            int m2 = minutoDoHorario(h2.horario);
            return Integer.compare(m1, m2);
        });

        return result;
    }

    private static int minutoDoHorario(String h) {
        if (h == null) return 9999;
        try {
            String s = h.replace("⏰", "").trim();
// Pega só o primeiro horário se for "9:50-10:10"
            if (s.contains("-")) s = s.split("-")[0].trim();
            if (s.contains("h")) s = s.replace("h", ":00");
            String[] p = s.split(":");
            int hora = Integer.parseInt(p[0].trim());
            int min = p.length > 1 ? Integer.parseInt(p[1].trim()) : 0;
            return hora * 60 + min;
        } catch (Exception e) {
            return 9999;
        }
    }

    // ==========================================
// CARDÁPIO
    // ==========================================
        public static List<CardapioItem> getCardapioSemana(int semana) {
        List<CardapioItem> lista = new ArrayList<>();

        if (semana == 1) {
            lista.add(new CardapioItem("Segunda", "Cuscuz",
                    "Ovos coloridos (cebola, pimentao, tomate)",
                    320, 12, 8, 48, 380, 1));
            lista.add(new CardapioItem("Terça", "Macarronada a bolonhesa",
                    "Carne moida e soja",
                    450, 20, 12, 65, 620, 1));
            lista.add(new CardapioItem("Quarta", "Sanduiche especial",
                    "Pao, ovos, cenoura, alface + Suco de caju",
                    380, 15, 12, 50, 580, 1));
            lista.add(new CardapioItem("Quinta", "Risoto de frango",
                    "Arroz, frango + Banana",
                    420, 22, 10, 58, 520, 1));
            lista.add(new CardapioItem("Sexta", "Mungunza com canela",
                    "Goiaba",
                    350, 10, 6, 65, 280, 1));
        }

        if (semana == 2) {
            lista.add(new CardapioItem("Segunda", "Vitamina de banana",
                    "Biscoito doce + Laranja",
                    280, 8, 6, 48, 150, 2));
            lista.add(new CardapioItem("Terça", "Macaxeira cozida",
                    "Ovos mexidos",
                    380, 14, 9, 60, 320, 2));
            lista.add(new CardapioItem("Quarta", "Cuscuz",
                    "Carne moida e soja",
                    400, 18, 10, 55, 480, 2));
            lista.add(new CardapioItem("Quinta", "Sopa de legumes com frango",
                    "Chuchu, batata, cenoura, abobora, repolho, macarrao, frango",
                    320, 18, 8, 42, 680, 2));
            lista.add(new CardapioItem("Sexta", "Sanduiche especial",
                    "Pao, ovos, cenoura, alface + Suco de acerola + Melancia",
                    420, 16, 13, 55, 600, 2));
        }

        if (semana == 3) {
            lista.add(new CardapioItem("Segunda", "Macarronada a bolonhesa",
                    "Carne moida e soja",
                    450, 20, 12, 65, 620, 3));
            lista.add(new CardapioItem("Terça", "Batata doce",
                    "Carne moida e soja + Goiaba",
                    410, 19, 11, 58, 540, 3));
            lista.add(new CardapioItem("Quarta", "Sopa de legumes com carne bovina",
                    "Chuchu, batata, cenoura, abobora, repolho, macarrao, carne bovina",
                    360, 22, 10, 45, 720, 3));
            lista.add(new CardapioItem("Quinta", "Cuscuz",
                    "Ovos coloridos (cebola, pimentao, tomate)",
                    320, 12, 8, 48, 380, 3));
            lista.add(new CardapioItem("Sexta", "Bebida lactea",
                    "Biscoito salgado + Banana",
                    300, 10, 8, 48, 280, 3));
        }

        if (semana == 4) {
            lista.add(new CardapioItem("Segunda", "Cuscuz",
                    "Ovos coloridos (cebola, pimentao, tomate)",
                    320, 12, 8, 48, 380, 4));
            lista.add(new CardapioItem("Terça", "Sopa de legumes com carne bovina",
                    "Chuchu, batata, cenoura, abobora, repolho, macarrao, carne bovina",
                    360, 22, 10, 45, 720, 4));
            lista.add(new CardapioItem("Quarta", "Risoto de frango",
                    "Laranja",
                    420, 22, 10, 58, 520, 4));
            lista.add(new CardapioItem("Quinta", "Mungunza com canela",
                    "Melancia",
                    350, 10, 6, 65, 280, 4));
            lista.add(new CardapioItem("Sexta", "Arrumadinho de frango",
                    "Feijao fradinho, farinha, frango + Vinagrete (tomate, cebola, coentro)",
                    480, 25, 14, 62, 750, 4));
        }

        return lista;
    }

    // ============================================================
    // SEMANA ATUAL DO CARDAPIO
    // ============================================================

    public static final String PREF_SEMANA_REF = "cardapio_semana_ref";
    public static final String PREF_SEMANA_DATA = "cardapio_semana_data";

    /**
     * Retorna a semana atual (1-4) baseado na data de referencia salva.
     * Se nunca foi definida, retorna 1.
     */
    public static int getSemanaAtual(android.content.Context ctx) {
        android.content.SharedPreferences prefs =
                ctx.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE);

        int semanaRef = prefs.getInt(PREF_SEMANA_REF, 0);
        long dataRef = prefs.getLong(PREF_SEMANA_DATA, 0);

        if (semanaRef == 0 || dataRef == 0) {
            return 1; // nunca definida
        }

        long agora = System.currentTimeMillis();
        long diffMs = agora - dataRef;
        long diffDias = diffMs / (1000L * 60L * 60L * 24L);
        int semanasPassadas = (int)(diffDias / 7);

        int semanaAtual = ((semanaRef - 1 + semanasPassadas) % 4) + 1;
        return semanaAtual;
    }

    /**
     * Salva qual e' a semana de referencia AGORA.
     */
    public static void setSemanaReferencia(android.content.Context ctx, int semana) {
        android.content.SharedPreferences prefs =
                ctx.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE);
        prefs.edit()
                .putInt(PREF_SEMANA_REF, semana)
                .putLong(PREF_SEMANA_DATA, System.currentTimeMillis())
                .apply();
    }

    /**
     * Retorna a merenda do dia atual, baseada na semana atual.
     * Retorna null se nao tiver (fim de semana).
     */
    public static String getMerendaDoDia(android.content.Context ctx) {
        int semana = getSemanaAtual(ctx);
        java.util.List<CardapioItem> itens = getCardapioSemana(semana);

        // Descobre o nome do dia da semana (Segunda a Sexta)
        String diaHoje;
        int dow = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK);
        switch (dow) {
            case java.util.Calendar.MONDAY:    diaHoje = "Segunda"; break;
            case java.util.Calendar.TUESDAY:   diaHoje = "Terça";   break;
            case java.util.Calendar.WEDNESDAY: diaHoje = "Quarta";  break;
            case java.util.Calendar.THURSDAY:  diaHoje = "Quinta";  break;
            case java.util.Calendar.FRIDAY:    diaHoje = "Sexta";   break;
            default: return null;
        }

        for (CardapioItem item : itens) {
            if (item.getDiaSemana() != null && item.getDiaSemana().toLowerCase().startsWith(
                    diaHoje.toLowerCase().substring(0, 3))) {
                return item.getNomePrato();
            }
        }
        return null;
    }

    // ============================================================
    // CORES POR MATERIA + PROFESSOR
    // ============================================================
    public static int getCorMateria(String materia, String professor) {
        if (materia == null) return 0xFF2A2A2A;
        if (professor == null) professor = "";

        String m = materia.toUpperCase().trim();
        String p = professor.trim();

        // BIO
        if (m.equals("BIO")) {
            if (p.equals("Dayse"))   return 0xFFFFDC64;
            if (p.equals("Thays"))   return 0xFFFFBE8C;
            if (p.equals("Eraldo"))  return 0xFFE1A58C;
        }
        // MAT
        if (m.equals("MAT")) {
            if (p.equals("Ana Paula")) return 0xFF4BA5DC;
            if (p.equals("Jailson"))   return 0xFF3769BE;
            if (p.equals("C\u00e1ssia")) return 0xFF5AB4DC;
            if (p.equals("C\u00c1SSIA")) return 0xFF5AB4DC;
            if (p.equals("Widjane"))   return 0xFFAFB9B4;
            if (p.equals("Emerson"))   return 0xFF4B9BCD;
            if (p.equals("Josenildo")) return 0xFF82AF7D;
            if (p.equals("\u00c1lvaro")) return 0xFF377DB9;
        }
        // F\u00cdS
        if (m.equals("F\u00cdS") || m.equals("FIS")) {
            if (p.equals("Camila"))  return 0xFFC87391;
            if (p.equals("Herbert")) return 0xFFAF788C;
            if (p.equals("Widjane")) return 0xFFB47D91;
        }
        // ART
        if (m.equals("ART")) {
            if (p.equals("C\u00edcera"))   return 0xFFDC9173;
            if (p.equals("Thayse"))    return 0xFFF5AF87;
            if (p.equals("Henrique"))  return 0xFFE1A050;
        }
        // QUI
        if (m.equals("QUI") || m.equals("QU\u00cd")) {
            if (p.equals("Helo\u00edza")) return 0xFFBED7A0;
            if (p.equals("Jhosi"))    return 0xFFA5C869;
            if (p.equals("Caio"))     return 0xFF419BB4;
            if (p.equals("Erivaldo")) return 0xFF9BBE7D;
        }
        // APROF
        if (m.equals("APROF")) {
            if (p.equals("Niedja"))   return 0xFF8CBE7D;
            if (p.equals("Dayse"))    return 0xFFCDD27D;
            if (p.equals("Moral"))    return 0xFF2D64AF;
            if (p.equals("C\u00e1ssia") || p.equals("C\u00c1SSIA")) return 0xFF5F9BD2;
            if (p.equals("J\u00e9ssica")) return 0xFFAACDA5;
            if (p.equals("Jailson"))  return 0xFF647DCD;
            if (p.equals("Erivaldo")) return 0xFF87B46E;
        }
        // PORT
        if (m.equals("PORT")) {
            if (p.equals("Judy"))     return 0xFFAFAFDC;
            if (p.equals("Luciano"))  return 0xFFDC9187;
            if (p.equals("Rozi"))     return 0xFFD77D73;
            if (p.equals("Danilo"))   return 0xFFCD7D73;
            if (p.equals("Silvana"))  return 0xFF5A9BC8;
        }
        // PDV
        if (m.equals("PDV")) {
            if (p.equals("Herbert"))   return 0xFF8796CD;
            if (p.equals("Judy"))      return 0xFFDCAAB9;
            if (p.equals("Thayse"))    return 0xFFB4A5D7;
            if (p.equals("Ana Paula")) return 0xFF91A5D7;
            if (p.equals("D\u00e9bora"))  return 0xFFAF82CD;
            if (p.equals("Douglas"))   return 0xFF649BD2;
            if (p.equals("Roberto"))   return 0xFF64AFDC;
            if (p.equals("Silvana"))   return 0xFF509BD2;
            if (p.equals("\u00c1lvaro"))  return 0xFF2873AF;
            if (p.equals("Cl\u00e1udia"))  return 0xFFCDAA64;
        }
        // PRAT
        if (m.equals("PRAT")) {
            if (p.equals("Helo\u00edza")) return 0xFFAFD2AF;
            if (p.equals("Robson"))   return 0xFFEB9B69;
            if (p.equals("Josenildo") || p.equals("Jos\u00e9 Nildo")) return 0xFF96BE87;
            if (p.equals("Camila"))   return 0xFFC8879B;
            if (p.equals("Ana Luiza")) return 0xFFB47DAF;
            if (p.equals("\u00c1lvaro"))  return 0xFF69A0C8;
            if (p.equals("Cl\u00e1udia"))  return 0xFFE1B978;
        }
        // TAC
        if (m.equals("TAC")) {
            if (p.equals("Dayse"))   return 0xFFF5CD55;
            if (p.equals("Danilo"))  return 0xFFCD9182;
            if (p.equals("Moral"))   return 0xFF2D87B9;
        }
        // ING
        if (m.equals("ING")) {
            if (p.equals("Cl\u00e1udia") || p.equals("Claudia")) return 0xFFEBC364;
            if (p.equals("Robson"))  return 0xFFDC9173;
        }
        // GEO
        if (m.equals("GEO")) {
            if (p.equals("Moral"))   return 0xFF649BBE;
            if (p.equals("J\u00e9ssica")) return 0xFF9BCDA0;
        }
        // EF
        if (m.equals("EF")) {
            if (p.equals("Adriano"))  return 0xFFBEC3B4;
            if (p.equals("Suyllane") || p.equals("Suylane")) return 0xFF7DC3CD;
            if (p.equals("Douglas"))  return 0xFF739BC3;
        }
        // PIT
        if (m.equals("PIT")) {
            if (p.equals("Adriano")) return 0xFFAFB9AA;
            if (p.equals("D\u00e9bora"))  return 0xFFC89BB9;
            if (p.equals("Robson"))  return 0xFFC37D87;
            if (p.equals("Douglas")) return 0xFF417DBE;
            if (p.equals("Suyllane")) return 0xFF73A5CD;
        }
        // FIL
        if (m.equals("FIL")) {
            if (p.equals("M\u00e1rcia")) return 0xFFDCCD9B;
        }
        // HIS
        if (m.equals("HIS")) {
            if (p.equals("Roberto")) return 0xFF78A569;
            if (p.equals("Moral"))   return 0xFF507DAF;
            if (p.equals("Niedja"))  return 0xFF78A569;
        }
        // SOC
        if (m.equals("SOC")) {
            if (p.equals("Ana Luiza")) return 0xFFAF7DB9;
        }

        // Fallback: cor neutra
        if (m.equals("SEM AULA") || m.equals("INTERVALO") || m.equals("SAIDA") || m.equals("SA\u00cdDA")) {
            return 0xFF2A2A2A;
        }

        // Se chegou aqui, retorna cinza padrao
        return 0xFF3A3A3A;
    }

    // ============================================================
    // FILTRA TURMAS POR TURNO (manha / tarde)
    // ============================================================
    public static List<Sala> getTurmasPorTurno(String turno) {
        List<Sala> todas = getTurmas();
        List<Sala> filtradas = new ArrayList<>();
        if (turno == null) turno = "manha";

        for (Sala s : todas) {
            String nome = s.getNome();
            if (nome == null) continue;
            if ("manha".equals(turno) && nome.endsWith("MAT")) {
                filtradas.add(s);
            } else if ("tarde".equals(turno) && nome.endsWith("VES")) {
                filtradas.add(s);
            }
        }
        return filtradas;
    }

    // ============================================================
    // CONVERTE HORARIO MANHA -> TARDE (se for turma VES)
    // ============================================================
    public static String getHorarioPorTurno(String horarioManha, String turma) {
        if (horarioManha == null || turma == null) return horarioManha;
        if (!turma.endsWith("VES")) return horarioManha;  // eh MAT -> retorna o da manha

        String h = horarioManha.trim();

        if (h.equals("7:00"))        return " 13:00";
        if (h.equals("8:00"))        return " 13:50";
        if (h.equals("9:00"))        return " 14:40";
        if (h.equals("9:50-10:10"))  return " 15:30-15:50";
        if (h.equals("10:10"))       return " 15:50";
        if (h.equals("11:00"))       return " 16:40";
        if (h.equals("11:50"))       return " 17:30";

        return horarioManha;  // se nao achou, retorna original
    }

    // ============================================================
    // AJUSTA TODAS AS AULAS DE ACORDO COM O TURNO DA TURMA
    // ============================================================
    public static List<Aula> ajustarHorariosPorTurno(List<Aula> aulas, String turma) {
        if (aulas == null || turma == null) return aulas;
        if (!turma.endsWith("VES")) return aulas;

        List<Aula> ajustadas = new ArrayList<>();
        for (Aula a : aulas) {
            String novoHorario = getHorarioPorTurno(a.getHorario(), turma);
            Aula nova = new Aula(
                    novoHorario,
                    a.getDisciplina(),
                    a.getProfessor(),
                    a.getSala(),
                    a.isSemAula(),
                    a.isTextoLivre()
            );
            ajustadas.add(nova);
        }
        return ajustadas;
    }

    // ============================================================
    // GRADE DAS TURMAS VES (VESPERTINO)
    // Horarios convertidos automaticamente por ajustarHorariosPorTurno()
    // ============================================================
    private static void carregarGradeVES() {
        // ==========================================
        // 1A VES
        // ==========================================
        Map<String, List<Aula>> t1AVES = new LinkedHashMap<>();
        t1AVES.put("Segunda", mk(
                a(H1, "BIO", "Eraldo", "1A"),
                a(H2, "BIO", "Eraldo", "1A"),
                a(H3, "PORT", "Judy", "1A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PORT", "Judy", "1A"),
                a(H5, "PDV", "Alvaro", "1A"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t1AVES.put("Terça", mk(
                a(H1, "MAT", "Emerson", "1A"),
                a(H2, "MAT", "Emerson", "1A"),
                a(H3, "SEM AULA", "-", "-"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "QUI", "Caio", "1A"),
                a(H5, "PDV", "Alvaro", "1A"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t1AVES.put("Quarta", mk(
                a(H1, "PORT", "Judy", "1A"),
                a(H2, "PORT", "Judy", "1A"),
                a(H3, "HIS", "Roberto", "1A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "MAT", "Alvaro", "1A"),
                a(H5, "MAT", "Alvaro", "1A"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t1AVES.put("Quinta", mk(
                a(H1, "FIL", "Marcia", "1A"),
                a(H2, "GEO", "Moral", "1A"),
                a(H3, "EF", "Adriano", "1A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "TAC", "Danilo", "1A"),
                a(H5, "TAC", "Danilo", "1A"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t1AVES.put("Sexta", mk(
                a(H1, "ING", "Robson", "1A"),
                a(H2, "ART", "Henrique", "1A"),
                a(H3, "SOC", "Ana Luiza", "1A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PIT", "Douglas", "1A"),
                a(H5, "PIT", "Douglas", "1A"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        GRADE.put("1A VES", t1AVES);

        // ==========================================
        // 1B VES
        // ==========================================
        Map<String, List<Aula>> t1BVES = new LinkedHashMap<>();
        t1BVES.put("Segunda", mk(
                a(H1, "PORT", "Judy", "1B"),
                a(H2, "PORT", "Judy", "1B"),
                a(H3, "FIS", "Herbert", "1B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "BIO", "Eraldo", "1B"),
                a(H5, "BIO", "Eraldo", "1B"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t1BVES.put("Terça", mk(
                a(H1, "GEO", "Moral", "1B"),
                a(H2, "EF", "Adriano", "1B"),
                a(H3, "MAT", "Alvaro", "1B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "MAT", "Alvaro", "1B"),
                a(H5, "QUI", "Caio", "1B"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t1BVES.put("Quarta", mk(
                a(H1, "MAT", "Alvaro", "1B"),
                a(H2, "MAT", "Alvaro", "1B"),
                a(H3, "PORT", "Judy", "1B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PORT", "Judy", "1B"),
                a(H5, "SEM AULA", "-", "-"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t1BVES.put("Quinta", mk(
                a(H1, "TAC", "Danilo", "1B"),
                a(H2, "PIT", "Robson", "1B"),
                a(H3, "SOC", "Ana Luiza", "1B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "HIS", "Roberto", "1B"),
                a(H5, "PIT", "Robson", "1B"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t1BVES.put("Sexta", mk(
                a(H1, "PDV", "Thayse", "1B"),
                a(H2, "ING", "Robson", "1B"),
                a(H3, "TAC", "Danilo", "1B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "ART", "Henrique", "1B"),
                a(H5, "FIL", "Marcia", "1B"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        GRADE.put("1B VES", t1BVES);

        // ==========================================
        // 1C VES
        // ==========================================
        Map<String, List<Aula>> t1CVES = new LinkedHashMap<>();
        t1CVES.put("Segunda", mk(
                a(H1, "SEM AULA", "-", "-"),
                a(H2, "MAT", "Emerson", "1C"),
                a(H3, "MAT", "Emerson", "1C"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "FIS", "Herbert", "1C"),
                a(H5, "PDV", "Claudia", "1C"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t1CVES.put("Terça", mk(
                a(H1, "QUI", "Caio", "1C"),
                a(H2, "TAC", "Moral", "1C"),
                a(H3, "MAT", "Emerson", "1C"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "MAT", "Emerson", "1C"),
                a(H5, "EF", "Adriano", "1C"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t1CVES.put("Quarta", mk(
                a(H1, "GEO", "Moral", "1C"),
                a(H2, "BIO", "Thays", "1C"),
                a(H3, "BIO", "Thays", "1C"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "HIS", "Roberto", "1C"),
                a(H5, "SOC", "Ana Luiza", "1C"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t1CVES.put("Quinta", mk(
                a(H1, "TAC", "Moral", "1C"),
                a(H2, "PORT", "Danilo", "1C"),
                a(H3, "PORT", "Danilo", "1C"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "ING", "Robson", "1C"),
                a(H5, "FIL", "Marcia", "1C"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t1CVES.put("Sexta", mk(
                a(H1, "PORT", "Danilo", "1C"),
                a(H2, "PORT", "Danilo", "1C"),
                a(H3, "PIT", "Robson", "1C"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PIT", "Robson", "1C"),
                a(H5, "ART", "Henrique", "1C"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        GRADE.put("1C VES", t1CVES);

        // ==========================================
        // 1D VES
        // ==========================================
        Map<String, List<Aula>> t1DVES = new LinkedHashMap<>();
        t1DVES.put("Segunda", mk(
                a(H1, "MAT", "Widjane", "1D"),
                a(H2, "FIS", "Herbert", "1D"),
                a(H3, "TAC", "Dayse", "1D"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "MAT", "Widjane", "1D"),
                a(H5, "MAT", "Widjane", "1D"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t1DVES.put("Terça", mk(
                a(H1, "MAT", "Widjane", "1D"),
                a(H2, "QUI", "Caio", "1D"),
                a(H3, "EF", "Adriano", "1D"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "GEO", "Moral", "1D"),
                a(H5, "TAC", "Dayse", "1D"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t1DVES.put("Quarta", mk(
                a(H1, "PIT", "Suyllane", "1D"),
                a(H2, "PIT", "Suyllane", "1D"),
                a(H3, "SOC", "Ana Luiza", "1D"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "BIO", "Thays", "1D"),
                a(H5, "BIO", "Thays", "1D"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t1DVES.put("Quinta", mk(
                a(H1, "PORT", "Luciano", "1D"),
                a(H2, "PORT", "Danilo", "1D"),
                a(H3, "ING", "Robson", "1D"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "ART", "Henrique", "1D"),
                a(H5, "HIS", "Roberto", "1D"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t1DVES.put("Sexta", mk(
                a(H1, "SEM AULA", "-", "-"),
                a(H2, "SEM AULA", "-", "-"),
                a(H3, "FIL", "Marcia", "1D"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PDV", "Thayse", "1D"),
                a(H5, "SEM AULA", "-", "-"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        GRADE.put("1D VES", t1DVES);

        // ==========================================
        // 2A VES
        // ==========================================
        Map<String, List<Aula>> t2AVES = new LinkedHashMap<>();
        t2AVES.put("Segunda", mk(
                a(H1, "FIS", "Herbert", "2A"),
                a(H2, "APROF", "Cassia", "2A"),
                a(H3, "PORT", "Silvana", "2A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PORT", "Silvana", "2A"),
                a(H5, "MAT", "Josenildo", "2A"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t2AVES.put("Terça", mk(
                a(H1, "MAT", "Josenildo", "2A"),
                a(H2, "MAT", "Josenildo", "2A"),
                a(H3, "APROF", "Cassia", "2A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "EF", "Adriano", "2A"),
                a(H5, "MAT", "Josenildo", "2A"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t2AVES.put("Quarta", mk(
                a(H1, "BIO", "Thays", "2A"),
                a(H2, "GEO", "Moral", "2A"),
                a(H3, "PRAT", "Alvaro", "2A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "SOC", "Ana Luiza", "2A"),
                a(H5, "ING", "Claudia", "2A"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t2AVES.put("Quinta", mk(
                a(H1, "ART", "Henrique", "2A"),
                a(H2, "HIS", "Roberto", "2A"),
                a(H3, "FIL", "Marcia", "2A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "MAT", "Josenildo", "2A"),
                a(H5, "PORT", "Silvana", "2A"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t2AVES.put("Sexta", mk(
                a(H1, "QUI", "Jhosi", "2A"),
                a(H2, "QUI", "Jhosi", "2A"),
                a(H3, "PDV", "Silvana", "2A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PORT", "Silvana", "2A"),
                a(H5, "SEM AULA", "-", "-"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        GRADE.put("2A VES", t2AVES);

        // ==========================================
        // 2B VES
        // ==========================================
        Map<String, List<Aula>> t2BVES = new LinkedHashMap<>();
        t2BVES.put("Segunda", mk(
                a(H1, "PORT", "Silvana", "2B"),
                a(H2, "PORT", "Silvana", "2B"),
                a(H3, "ING", "Claudia", "2B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "BIO", "Dayse", "2B"),
                a(H5, "SEM AULA", "-", "-"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t2BVES.put("Terça", mk(
                a(H1, "EF", "Adriano", "2B"),
                a(H2, "FIS", "Widjane", "2B"),
                a(H3, "MAT", "Josenildo", "2B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "MAT", "Josenildo", "2B"),
                a(H5, "GEO", "Moral", "2B"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t2BVES.put("Quarta", mk(
                a(H1, "PRAT", "Ana Luiza", "2B"),
                a(H2, "PRAT", "Ana Luiza", "2B"),
                a(H3, "APROF", "Moral", "2B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "APROF", "Moral", "2B"),
                a(H5, "HIS", "Roberto", "2B"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t2BVES.put("Quinta", mk(
                a(H1, "MAT", "Josenildo", "2B"),
                a(H2, "SOC", "Ana Luiza", "2B"),
                a(H3, "PORT", "Silvana", "2B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PORT", "Silvana", "2B"),
                a(H5, "MAT", "Josenildo", "2B"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t2BVES.put("Sexta", mk(
                a(H1, "FIL", "Marcia", "2B"),
                a(H2, "PDV", "Thayse", "2B"),
                a(H3, "ART", "Henrique", "2B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "QUI", "Jhosi", "2B"),
                a(H5, "QUI", "Jhosi", "2B"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        GRADE.put("2B VES", t2BVES);

        // ==========================================
        // 3A VES
        // ==========================================
        Map<String, List<Aula>> t3AVES = new LinkedHashMap<>();
        t3AVES.put("Segunda", mk(
                a(H1, "FIS", "Camila", "3A"),
                a(H2, "FIS", "Camila", "3A"),
                a(H3, "PORT", "Luciano", "3A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "MAT", "Cassia", "3A"),
                a(H5, "MAT", "Cassia", "3A"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t3AVES.put("Terça", mk(
                a(H1, "PRAT", "Camila", "3A"),
                a(H2, "PRAT", "Camila", "3A"),
                a(H3, "BIO", "Dayse", "3A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "MAT", "Cassia", "3A"),
                a(H5, "MAT", "Cassia", "3A"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t3AVES.put("Quarta", mk(
                a(H1, "ING", "Claudia", "3A"),
                a(H2, "GEO", "Jessica", "3A"),
                a(H3, "PORT", "Luciano", "3A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PORT", "Luciano", "3A"),
                a(H5, "QUI", "Erivaldo", "3A"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t3AVES.put("Quinta", mk(
                a(H1, "PORT", "Luciano", "3A"),
                a(H2, "EF", "Adriano", "3A"),
                a(H3, "ART", "Henrique", "3A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "APROF", "Erivaldo", "3A"),
                a(H5, "APROF", "Erivaldo", "3A"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t3AVES.put("Sexta", mk(
                a(H1, "SOC", "Ana Luiza", "3A"),
                a(H2, "FIL", "Marcia", "3A"),
                a(H3, "PDV", "Douglas", "3A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "SEM AULA", "-", "-"),
                a(H5, "HIS", "Moral", "3A"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        GRADE.put("3A VES", t3AVES);

        // ==========================================
        // 3B VES
        // ==========================================
        Map<String, List<Aula>> t3BVES = new LinkedHashMap<>();
        t3BVES.put("Segunda", mk(
                a(H1, "MAT", "Cassia", "3B"),
                a(H2, "BIO", "Dayse", "3B"),
                a(H3, "MAT", "Cassia", "3B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PRAT", "Camila", "3B"),
                a(H5, "PRAT", "Camila", "3B"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t3BVES.put("Terça", mk(
                a(H1, "MAT", "Cassia", "3B"),
                a(H2, "MAT", "Cassia", "3B"),
                a(H3, "HIS", "Moral", "3B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "FIS", "Camila", "3B"),
                a(H5, "FIS", "Camila", "3B"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t3BVES.put("Quarta", mk(
                a(H1, "PORT", "Luciano", "3B"),
                a(H2, "PORT", "Luciano", "3B"),
                a(H3, "APROF", "Erivaldo", "3B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "APROF", "Erivaldo", "3B"),
                a(H5, "GEO", "Jessica", "3B"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t3BVES.put("Quinta", mk(
                a(H1, "QUI", "Erivaldo", "3B"),
                a(H2, "PORT", "Luciano", "3B"),
                a(H3, "PORT", "Luciano", "3B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "EF", "Adriano", "3B"),
                a(H5, "ART", "Henrique", "3B"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t3BVES.put("Sexta", mk(
                a(H1, "SEM AULA", "-", "-"),
                a(H2, "SOC", "Ana Luiza", "3B"),
                a(H3, "PDV", "Thayse", "3B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "FIL", "Marcia", "3B"),
                a(H5, "ING", "Robson", "3B"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        GRADE.put("3B VES", t3BVES);

        // ==========================================
        // 3C VES
        // ==========================================
        Map<String, List<Aula>> t3CVES = new LinkedHashMap<>();
        t3CVES.put("Segunda", mk(
                a(H1, "PORT", "Luciano", "3C"),
                a(H2, "PORT", "Luciano", "3C"),
                a(H3, "FIS", "Camila", "3C"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "ING", "Claudia", "3C"),
                a(H5, "MAT", "Emerson", "3C"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t3CVES.put("Terça", mk(
                a(H1, "MAT", "Emerson", "3C"),
                a(H2, "MAT", "Emerson", "3C"),
                a(H3, "FIS", "Camila", "3C"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "BIO", "Dayse", "3C"),
                a(H5, "MAT", "Emerson", "3C"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t3CVES.put("Quarta", mk(
                a(H1, "APROF", "Jessica", "3C"),
                a(H2, "PRAT", "Claudia", "3C"),
                a(H3, "PRAT", "Claudia", "3C"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "APROF", "Jessica", "3C"),
                a(H5, "GEO", "Moral", "3C"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t3CVES.put("Quinta", mk(
                a(H1, "SOC", "Ana Luiza", "3C"),
                a(H2, "FIL", "Marcia", "3C"),
                a(H3, "QUI", "Erivaldo", "3C"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PORT", "Luciano", "3C"),
                a(H5, "PORT", "Luciano", "3C"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        t3CVES.put("Sexta", mk(
                a(H1, "ART", "Henrique", "3C"),
                a(H2, "EF", "Douglas", "3C"),
                a(H3, "SEM AULA", "-", "-"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PDV", "Douglas", "3C"),
                a(H5, "PDV", "Thayse", "3C"),
                a(H_SAIDA, "SAIDA", "-", "-")
        ));
        GRADE.put("3C VES", t3CVES);
    }
}