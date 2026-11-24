package com.santosdumont.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.santosdumont.models.Aula;
import com.santosdumont.models.CardapioItem;
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

    // ==========================================
    // TURMAS
    // ==========================================
    public static List<Sala> getTurmas() {
        List<Sala> lista = new ArrayList<>();
        lista.add(new Sala("1A MAT", ""));
        lista.add(new Sala("1B MAT", ""));
        lista.add(new Sala("2A MAT", ""));
        lista.add(new Sala("2B MAT", ""));
        lista.add(new Sala("3A MAT", ""));
        lista.add(new Sala("3B MAT", ""));
        return lista;
    }

    // ==========================================
    // PADRÃO DE HORÁRIOS (hardcoded)
    // ==========================================
    private static Map<String, List<Aula>> padrao() {
        Map<String, List<Aula>> h = new LinkedHashMap<>();

        List<Aula> seg = new ArrayList<>();
        seg.add(new Aula("7h", "ART", "Prof. Cícera"));
        seg.add(new Aula("8h", "MAT", "Prof. Jailson"));
        seg.add(new Aula("9h", "PORT", "Prof. Niedja"));
        seg.add(new Aula("9:40-10:10", "INTERVALO", "-"));
        seg.add(new Aula("10:10", "HIST", "Prof. Thayse"));
        seg.add(new Aula("11:40", "SAÍDA", "-"));
        h.put("Segunda", seg);

        List<Aula> ter = new ArrayList<>();
        ter.add(new Aula("7h", "MAT", "Prof. Jailson"));
        ter.add(new Aula("8h", "BIO", "Prof. Heloiza"));
        ter.add(new Aula("9h", "QUI", "Prof. Suyllane"));
        ter.add(new Aula("9:40-10:10", "INTERVALO", "-"));
        ter.add(new Aula("10:10", "FIS", "Prof. Jhosi"));
        ter.add(new Aula("11:40", "SAÍDA", "-"));
        h.put("Terça", ter);

        List<Aula> qua = new ArrayList<>();
        qua.add(new Aula("7h", "PORT", "Prof. Niedja"));
        qua.add(new Aula("8h", "GEO", "Prof. Josenildo"));
        qua.add(new Aula("9h", "FIL", "Prof. Thayse"));
        qua.add(new Aula("9:40-10:10", "INTERVALO", "-"));
        qua.add(new Aula("10:10", "SOC", "Prof. Thayse"));
        qua.add(new Aula("11:40", "SAÍDA", "-"));
        h.put("Quarta", qua);

        List<Aula> qui = new ArrayList<>();
        qui.add(new Aula("7h", "MAT", "Prof. Jailson"));
        qui.add(new Aula("8h", "ART", "Prof. Cícera"));
        qui.add(new Aula("9h", "ING", "Prof. Suyllane"));
        qui.add(new Aula("9:40-10:10", "INTERVALO", "-"));
        qui.add(new Aula("10:10", "EDF", "Prof. Jhosi"));
        qui.add(new Aula("11:40", "SAÍDA", "-"));
        h.put("Quinta", qui);

        List<Aula> sex = new ArrayList<>();
        sex.add(new Aula("7h", "HIST", "Prof. Thayse"));
        sex.add(new Aula("8h", "GEO", "Prof. Josenildo"));
        sex.add(new Aula("9h", "MAT", "Prof. Jailson"));
        sex.add(new Aula("9:40-10:10", "INTERVALO", "-"));
        sex.add(new Aula("10:10", "PORT", "Prof. Niedja"));
        sex.add(new Aula("11:40", "SAÍDA", "-"));
        h.put("Sexta", sex);

        return h;
    }

    // ==========================================
    // JSON <-> MAP
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
                    Aula a = new Aula(
                            o.optString("horario"),
                            o.optString("disciplina"),
                            o.optString("professor"),
                            o.optBoolean("semAula", false),
                            o.optBoolean("textoLivre", false)
                    );
                    aulas.add(a);
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
    public static Map<String, List<Aula>> getTodos(Context ctx) {
        Map<String, List<Aula>> doJson = lerJSON(ctx);
        if (doJson != null && !doJson.isEmpty()) return doJson;
        return padrao();
    }

    public static List<Aula> getHorarios(Context ctx, String turma, String dia) {
        Map<String, List<Aula>> todos = getTodos(ctx);
        List<Aula> aulas = todos.get(dia);
        return aulas == null ? new ArrayList<>() : aulas;
    }

    /** Atualiza uma aula específica e persiste */
    public static void atualizarAula(Context ctx, String dia, int posicao, Aula nova) {
        Map<String, List<Aula>> todos = getTodos(ctx);
        List<Aula> aulas = todos.get(dia);
        if (aulas == null) return;
        if (posicao < 0 || posicao >= aulas.size()) return;
        aulas.set(posicao, nova);
        salvar(ctx, todos);
    }

    /** Reseta uma aula específica para o padrão */
    public static void resetarAula(Context ctx, String dia, int posicao) {
        Map<String, List<Aula>> todos = getTodos(ctx);
        Map<String, List<Aula>> padraoMapa = padrao();

        List<Aula> aulasAtuais = todos.get(dia);
        List<Aula> aulasPadrao = padraoMapa.get(dia);

        if (aulasAtuais == null || aulasPadrao == null) return;
        if (posicao < 0 || posicao >= aulasAtuais.size() || posicao >= aulasPadrao.size()) return;

        aulasAtuais.set(posicao, aulasPadrao.get(posicao));
        salvar(ctx, todos);
    }

    /** Reseta todos os horários */
    public static void resetarTudo(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        sp.edit().remove(KEY).apply();
    }

    // ==========================================
    // PROFESSORES
    // ==========================================
    public static Map<String, List<ProfessorModel>> getProfessoresPorDia(Context ctx) {
        Map<String, List<ProfessorModel>> mapa = new LinkedHashMap<>();
        Map<String, List<Aula>> todos = getTodos(ctx);

        String[] dias = {"Segunda", "Terça", "Quarta", "Quinta", "Sexta"};
        for (String dia : dias) {
            List<Aula> aulas = todos.get(dia);
            if (aulas == null) continue;

            Map<String, ProfessorModel> porNome = new LinkedHashMap<>();
            for (Aula a : aulas) {
                if (a.isSemAula()) continue;
                String nome = a.getProfessor();
                if (nome == null || nome.isEmpty() || nome.equals("-")) continue;
                if (a.getDisciplina().equalsIgnoreCase("INTERVALO") ||
                    a.getDisciplina().equalsIgnoreCase("SAÍDA") ||
                    a.getDisciplina().equalsIgnoreCase("SAIDA")) continue;

                if (!porNome.containsKey(nome)) {
                    List<String> disc = new ArrayList<>();
                    disc.add(a.getDisciplina());
                    List<String> turmas = new ArrayList<>();
                    turmas.add("1A MAT");
                    porNome.put(nome, new ProfessorModel(nome, disc, turmas));
                } else {
                    ProfessorModel p = porNome.get(nome);
                    if (!p.getDisciplinas().contains(a.getDisciplina())) {
                        p.getDisciplinas().add(a.getDisciplina());
                    }
                }
            }
            mapa.put(dia, new ArrayList<>(porNome.values()));
        }
        return mapa;
    }

    public static List<Aula> getHorariosProfessorPorDia(Context ctx, String nome, String dia) {
        List<Aula> result = new ArrayList<>();
        List<Aula> aulas = getTodos(ctx).get(dia);
        if (aulas == null) return result;
        for (Aula a : aulas) {
            if (a.isSemAula()) continue;
            if (a.getProfessor().equalsIgnoreCase(nome)) result.add(a);
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

    /** Retorna a aula padrão do DataHelper para um dia/posição */
    public static Aula getAulaPadrao(String dia, int posicao) {
        Map<String, List<Aula>> p = padrao();
        List<Aula> aulas = p.get(dia);
        if (aulas == null || posicao < 0 || posicao >= aulas.size()) return null;
        return aulas.get(posicao);
    }
}