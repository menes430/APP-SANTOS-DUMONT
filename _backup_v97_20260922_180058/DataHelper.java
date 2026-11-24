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
    public static final String H1 = "⏰ 7h";
    public static final String H2 = "⏰ 8h";
    public static final String H3 = "⏰ 9h";
    public static final String H_INT = "⏰ 9:40-10:10";
    public static final String H4 = "⏰ 10:10";
    public static final String H5 = "⏰ 11:40";

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
    // GRADE COMPLETA (turma → dia → aulas)
    // Fonte: lista de professores enviada pelo Tavin
    // ==========================================
    private static final Map<String, Map<String, List<Aula>>> GRADE = new HashMap<>();

    static {
        // ==========================================
        // 1A MAT
        // ==========================================
        Map<String, List<Aula>> t1A = new LinkedHashMap<>();

        t1A.put("Segunda", mk(
                a(H1, "MAT", "Ana Paula", "1A"),
                a(H2, "MAT", "Ana Paula", "1A"),
                a(H3, "BIO", "Dayse", "1A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "HIS", "Roberto", "1A"),
                a(H5, "FÍS", "Herbert", "1A")
        ));

        t1A.put("Terça", mk(
                a(H1, "PORT", "Rozi", "1A"),
                a(H2, "PORT", "Rozi", "1A"),
                a(H3, "EF", "Adriano", "1A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PIT", "Adriano", "1A"),
                a(H5, "PIT", "Adriano", "1A")
        ));

        t1A.put("Quarta", mk(
                a(H1, "FIL", "Márcia", "1A"),
                a(H2, "MAT", "Ana Paula", "1A"),
                a(H3, "MAT", "Ana Paula", "1A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "HIS", "Roberto", "1A"),
                a(H5, "ING", "Robson", "1A")
        ));

        t1A.put("Quinta", mk(
                a(H1, "PORT", "Rozi", "1A"),
                a(H2, "PORT", "Rozi", "1A"),
                a(H3, "ART", "Henrique", "1A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "SOC", "Ana Luiza", "1A"),
                a(H5, "FÍS", "Herbert", "1A")
        ));

        t1A.put("Sexta", mk(
                a(H1, "TAC", "Dayse", "1A"),
                a(H2, "TAC", "Dayse", "1A"),
                a(H3, "HIS", "Roberto", "1A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "GEO", "Moral", "1A"),
                a(H5, "PDV", "Thayse", "1A")
        ));

        GRADE.put("1A MAT", t1A);

        // ==========================================
        // 1B MAT
        // ==========================================
        Map<String, List<Aula>> t1B = new LinkedHashMap<>();

        t1B.put("Segunda", mk(
                a(H1, "MAT", "Ana Paula", "1B"),
                a(H2, "MAT", "Ana Paula", "1B"),
                a(H3, "BIO", "Dayse", "1B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "FÍS", "Herbert", "1B"),
                a(H5, "TAC", "Dayse", "1B")
        ));

        t1B.put("Terça", mk(
                a(H1, "ART", "Thayse", "1B"),
                a(H2, "MAT", "Ana Paula", "1B"),
                a(H3, "MAT", "Ana Paula", "1B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PORT", "Rozi", "1B"),
                a(H5, "PORT", "Rozi", "1B")
        ));

        t1B.put("Quarta", mk(
                a(H1, "PDV", "Ana Paula", "1B"),
                a(H2, "PIT", "Débora", "1B"),
                a(H3, "PIT", "Débora", "1B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "BIO", "Thays", "1B"),
                a(H5, "BIO", "Thays", "1B")
        ));

        t1B.put("Quinta", mk(
                a(H1, "QUI", "Heloíza", "1B"),
                a(H2, "FIL", "Márcia", "1B"),
                a(H3, "PORT", "Rozi", "1B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PORT", "Rozi", "1B"),
                a(H5, "MAT", "Ana Paula", "1B")
        ));

        t1B.put("Sexta", mk(
                a(H1, "ING", "Robson", "1B"),
                a(H2, "MAT", "Ana Paula", "1B"),
                a(H3, "MAT", "Ana Paula", "1B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "TAC", "Dayse", "1B"),
                a(H5, "HIS", "Roberto", "1B")
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
                a(H5, "MAT", "Ana Paula", "2A")
        ));

        t2A.put("Terça", mk(
                a(H1, "MAT", "Ana Paula", "2A"),
                a(H2, "ART", "Thayse", "2A"),
                a(H3, "ING", "Claudia", "2A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "MAT", "Ana Paula", "2A"),
                a(H5, "MAT", "Ana Paula", "2A")
        ));

        t2A.put("Quarta", mk(
                a(H1, "PORT", "Luciano", "2A"),
                a(H2, "PORT", "Luciano", "2A"),
                a(H3, "PDV", "Roberto", "2A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "EF", "Suyllane", "2A"),
                a(H5, "APROF", "Cássia", "2A")
        ));

        t2A.put("Quinta", mk(
                a(H1, "HIS", "Roberto", "2A"),
                a(H2, "QUI", "Jhosi", "2A"),
                a(H3, "SOC", "Ana Luiza", "2A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PRAT", "Josenildo", "2A"),
                a(H5, "PRAT", "Josenildo", "2A")
        ));

        t2A.put("Sexta", mk(
                a(H1, "GEO", "Moral", "2A"),
                a(H2, "QUI", "Jhosi", "2A"),
                a(H3, "FIL", "Márcia", "2A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "MAT", "Cássia", "2A"),
                a(H5, "MAT", "Cássia", "2A")
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
                a(H5, "PRAT", "Heloíza", "2B")
        ));

        t2B.put("Terça", mk(
                a(H1, "MAT", "Jailson", "2B"),
                a(H2, "MAT", "Jailson", "2B"),
                a(H3, "GEO", "Moral", "2B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "FÍS", "Camila", "2B"),
                a(H5, "ART", "Thayse", "2B")
        ));

        t2B.put("Quarta", mk(
                a(H1, "APROF", "Jailson", "2B"),
                a(H2, "EF", "Suyllane", "2B"),
                a(H3, "BIO", "Thays", "2B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PORT", "Danilo", "2B"),
                a(H5, "PORT", "Danilo", "2B")
        ));

        t2B.put("Quinta", mk(
                a(H1, "MAT", "Jailson", "2B"),
                a(H2, "HIS", "Roberto", "2B"),
                a(H3, "QUI", "Jhosi", "2B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "APROF", "Jailson", "2B"),
                a(H5, "MAT", "Jailson", "2B")
        ));

        t2B.put("Sexta", mk(
                a(H1, "SOC", "Ana Luiza", "2B"),
                a(H2, "QUI", "Jhosi", "2B"),
                a(H3, "MAT", "Cássia", "2B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PORT", "Danilo", "2B"),
                a(H5, "PORT", "Danilo", "2B")
        ));

        GRADE.put("2B MAT", t2B);

        // ==========================================
        // 2C MAT
        // ==========================================
        Map<String, List<Aula>> t2C = new LinkedHashMap<>();

        t2C.put("Segunda", mk(
                a(H1, "ART", "Cícera", "2C"),
                a(H2, "EF", "Suyllane", "2C"),
                a(H3, "MAT", "Jailson", "2C"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "MAT", "Jailson", "2C"),
                a(H5, "MAT", "Jailson", "2C")
        ));

        t2C.put("Terça", mk(
                a(H1, "ING", "Claudia", "2C"),
                a(H2, "APROF", "Moral", "2C"),
                a(H3, "BIO", "Dayse", "2C"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "FÍS", "Camila", "2C"),
                a(H5, "APROF", "Moral", "2C")
        ));

        t2C.put("Quarta", mk(
                a(H1, "PDV", "Débora", "2C"),
                a(H2, "PORT", "Danilo", "2C"),
                a(H3, "PORT", "Danilo", "2C"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PRAT", "Robson", "2C"),
                a(H5, "EF", "Suyllane", "2C")
        ));

        t2C.put("Quinta", mk(
                a(H1, "QUI", "Jhosi", "2C"),
                a(H2, "MAT", "Jailson", "2C"),
                a(H3, "HIS", "Roberto", "2C"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PORT", "Danilo", "2C"),
                a(H5, "PORT", "Danilo", "2C")
        ));

        t2C.put("Sexta", mk(
                a(H1, "SOC", "Ana Luiza", "2C"),
                a(H2, "FIL", "Márcia", "2C"),
                a(H3, "GEO", "Moral", "2C"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "QUI", "Jhosi", "2C"),
                a(H5, "PRAT", "Robson", "2C")
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
                a(H4, "MAT", "Jailson", "2D"),
                a(H5, "MAT", "Jailson", "2D")
        ));

        t2D.put("Terça", mk(
                a(H1, "GEO", "Moral", "2D"),
                a(H2, "ING", "Claudia", "2D"),
                a(H3, "MAT", "Jailson", "2D"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PDV", "Thayse", "2D"),
                a(H5, "PRAT", "Robson", "2D")
        ));

        t2D.put("Quarta", mk(
                a(H1, "FIL", "Márcia", "2D"),
                a(H2, "MAT", "Jailson", "2D"),
                a(H3, "MAT", "Jailson", "2D"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "MAT", "Jailson", "2D"),
                a(H5, "HIS", "Roberto", "2D")
        ));

        t2D.put("Quinta", mk(
                a(H1, "PORT", "Danilo", "2D"),
                a(H2, "PORT", "Danilo", "2D"),
                a(H3, "MAT", "Jailson", "2D"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "ART", "Henrique", "2D"),
                a(H5, "PRAT", "Robson", "2D")
        ));

        t2D.put("Sexta", mk(
                a(H1, "PORT", "Danilo", "2D"),
                a(H2, "PORT", "Danilo", "2D"),
                a(H3, "BIO", "Dayse", "2D"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PRAT", "Robson", "2D"),
                a(H5, "SOC", "Ana Luiza", "2D")
        ));

        GRADE.put("2D MAT", t2D);

        // ==========================================
        // 3A MAT
        // ==========================================
        Map<String, List<Aula>> t3A = new LinkedHashMap<>();

        t3A.put("Segunda", mk(
                a(H1, "PORT", "Judy", "3A"),
                a(H2, "PORT", "Judy", "3A"),
                a(H3, "PORT", "Judy", "3A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PORT", "Judy", "3A"),
                a(H5, "PDV", "Judy", "3A")
        ));

        t3A.put("Terça", mk(
                a(H1, "BIO", "Dayse", "3A"),
                a(H2, "ING", "Robson", "3A"),
                a(H3, "ART", "Thayse", "3A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PDV", "Moral", "3A"),
                a(H5, "FÍS", "Camila", "3A")
        ));

        t3A.put("Quarta", mk(
                a(H1, "PORT", "Judy", "3A"),
                a(H2, "PORT", "Judy", "3A"),
                a(H3, "MAT", "Cássia", "3A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "MAT", "Cássia", "3A"),
                a(H5, "GEO", "Jéssica", "3A")
        ));

        t3A.put("Quinta", mk(
                a(H1, "FIL", "Márcia", "3A"),
                a(H2, "SOC", "Ana Luiza", "3A"),
                a(H3, "PRAT", "Robson", "3A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PRAT", "Robson", "3A"),
                a(H5, "QUI", "Jhosi", "3A")
        ));

        t3A.put("Sexta", mk(
                a(H1, "MAT", "Cássia", "3A"),
                a(H2, "MAT", "Cássia", "3A"),
                a(H3, "ING", "Robson", "3A"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "HIS", "Niedja", "3A"),
                a(H5, "EF", "Douglas", "3A")
        ));

        GRADE.put("3A MAT", t3A);

        // ==========================================
        // 3B MAT
        // ==========================================
        Map<String, List<Aula>> t3B = new LinkedHashMap<>();

        t3B.put("Segunda", mk(
                a(H1, "PORT", "Judy", "3B"),
                a(H2, "PORT", "Judy", "3B"),
                a(H3, "APROF", "Judy", "3B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PORT", "Judy", "3B"),
                a(H5, "PDV", "Judy", "3B")
        ));

        t3B.put("Terça", mk(
                a(H1, "FÍS", "Camila", "3B"),
                a(H2, "BIO", "Dayse", "3B"),
                a(H3, "APROF", "Dayse", "3B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "APROF", "Dayse", "3B"),
                a(H5, "APROF", "Dayse", "3B")
        ));

        t3B.put("Quarta", mk(
                a(H1, "MAT", "Cássia", "3B"),
                a(H2, "MAT", "Cássia", "3B"),
                a(H3, "GEO", "Jéssica", "3B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PORT", "Judy", "3B"),
                a(H5, "PORT", "Judy", "3B")
        ));

        t3B.put("Quinta", mk(
                a(H1, "SOC", "Ana Luiza", "3B"),
                a(H2, "ART", "Henrique", "3B"),
                a(H3, "PRAT", "Heloíza", "3B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "QUI", "Jhosi", "3B"),
                a(H5, "FIL", "Márcia", "3B")
        ));

        t3B.put("Sexta", mk(
                a(H1, "EF", "Douglas", "3B"),
                a(H2, "ING", "Robson", "3B"),
                a(H3, "MAT", "Cássia", "3B"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "MAT", "Cássia", "3B"),
                a(H5, "HIS", "Niedja", "3B")
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
                a(H5, "PORT", "Luciano", "3C")
        ));

        t3C.put("Terça", mk(
                a(H1, "BIO", "Dayse", "3C"),
                a(H2, "ING", "Robson", "3C"),
                a(H3, "APROF", "Dayse", "3C"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "MAT", "Jailson", "3C"),
                a(H5, "MAT", "Jailson", "3C")
        ));

        t3C.put("Quarta", mk(
                a(H1, "APROF", "Jéssica", "3C"),
                a(H2, "APROF", "Jéssica", "3C"),
                a(H3, "PORT", "Luciano", "3C"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "GEO", "Jéssica", "3C"),
                a(H5, "PORT", "Luciano", "3C")
        ));

        t3C.put("Quinta", mk(
                a(H1, "ART", "Henrique", "3C"),
                a(H2, "ING", "Robson", "3C"),
                a(H3, "PRAT", "Márcia", "3C"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "PRAT", "Márcia", "3C"),
                a(H5, "APROF", "Márcia", "3C")
        ));

        t3C.put("Sexta", mk(
                a(H1, "QUI", "Jhosi", "3C"),
                a(H2, "FIL", "Márcia", "3C"),
                a(H3, "PDV", "Douglas", "3C"),
                a(H_INT, "INTERVALO", "-", "-"),
                a(H4, "EF", "Douglas", "3C"),
                a(H5, "SOC", "Ana Luiza", "3C")
        ));

        GRADE.put("3C MAT", t3C);
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
        Map<String, ProfessorModel> porNome = new LinkedHashMap<>();

        for (String turma : GRADE.keySet()) {
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
        List<HorarioComSala> result = new ArrayList<>();

        for (String turma : GRADE.keySet()) {
            List<Aula> aulas = GRADE.get(turma).get(dia);
            if (aulas == null) continue;

            for (Aula a : aulas) {
                if (a.isSemAula()) continue;
                if (!professor.equals(a.getProfessor())) continue;
                result.add(new HorarioComSala(a.getHorario(), a.getDisciplina(), turma));
            }
        }
        return result;
    }

    // ==========================================
    // CARDÁPIO
    // ==========================================
    public static List<CardapioItem> getCardapioSemana(int semana) {
        List<CardapioItem> lista = new ArrayList<>();
        String[] dias = {"Segunda", "Terça", "Quarta", "Quinta", "Sexta"};
        String[][] pratos = {
                {"CUSCUZ", "MACARRONADA", "RISOTO", "SOPA", "MUNGUNZÁ"},
                {"ARROZ+FRANGO", "FEIJÃO+TILÁPIA", "MACARRÃO", "SOPA", "PIZZA"},
                {"CUSCUZ", "GALINHADA", "ARROZ+CARNE", "SOPA", "HAMBÚRGUER"},
                {"TAPIOCA", "ARROZ+PEIXE", "MACARRONADA", "SOPA", "CUSCUZ"}
        };
        String[] pratosSemana = pratos[(semana - 1) % pratos.length];
        for (int i = 0; i < dias.length; i++) {
            lista.add(new CardapioItem(dias[i], pratosSemana[i],
                    230 + (i * 20), 9 + i, 6 + i, 34 + (i * 3), 148 + (i * 10), semana));
        }
        return lista;
    }
}