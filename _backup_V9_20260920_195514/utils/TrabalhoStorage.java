package com.santosdumont.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.santosdumont.models.TrabalhoModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TrabalhoStorage {

    private static final String PREFS = "trabalhos_prefs";
    private static final String KEY = "trabalhos_json";

    public static List<TrabalhoModel> listar(Context ctx) {
        List<TrabalhoModel> lista = new ArrayList<>();
        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String json = sp.getString(KEY, "[]");
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                TrabalhoModel t = new TrabalhoModel();
                t.id = o.optString("id");
                t.nome = o.optString("nome");
                t.data = o.optString("data");
                t.hora = o.optString("hora");
                t.descricao = o.optString("descricao");
                t.imagemUri = o.optString("imagemUri", null);
                t.alarmeAtivo = o.optBoolean("alarmeAtivo", true);
                t.notifDiaSimDiaNao = o.optBoolean("notifDiaSimDiaNao", true);
                t.notifUltimaSemana = o.optBoolean("notifUltimaSemana", true);
                t.notifPosAlarme = o.optBoolean("notifPosAlarme", false);
                t.diasPosAlarme = o.optInt("diasPosAlarme", 3);
                lista.add(t);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static void salvar(Context ctx, List<TrabalhoModel> lista) {
        JSONArray arr = new JSONArray();
        try {
            for (TrabalhoModel t : lista) {
                JSONObject o = new JSONObject();
                o.put("id", t.id);
                o.put("nome", t.nome);
                o.put("data", t.data);
                o.put("hora", t.hora);
                o.put("descricao", t.descricao);
                o.put("imagemUri", t.imagemUri == null ? "" : t.imagemUri);
                o.put("alarmeAtivo", t.alarmeAtivo);
                o.put("notifDiaSimDiaNao", t.notifDiaSimDiaNao);
                o.put("notifUltimaSemana", t.notifUltimaSemana);
                o.put("notifPosAlarme", t.notifPosAlarme);
                o.put("diasPosAlarme", t.diasPosAlarme);
                arr.put(o);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        sp.edit().putString(KEY, arr.toString()).apply();
    }

    public static void adicionar(Context ctx, TrabalhoModel t) {
        List<TrabalhoModel> lista = listar(ctx);
        lista.add(t);
        salvar(ctx, lista);
    }
}