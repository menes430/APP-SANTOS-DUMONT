package com.santosdumont.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * Envia os dados coletados pro Firebase Firestore.
 * 1 documento por usuario (nome + turma).
 * Eventos separados por data.
 */
public class FirebaseUploader {

    private static final long INTERVALO_ENVIO_MS = 6 * 60 * 60 * 1000L; // 6 horas
    private static final String PREF_ULTIMO_ENVIO = "ultimo_envio_firebase";

    // ============================================================
    // DESATIVADO: envio automatico de 6h
    // Agora o envio e 1x/dia as 23:59 via AulaAlarmReceiver
    // ============================================================
    public static boolean enviarSeNecessario(Context ctx) {
        // Desativado — o AulaAlarmReceiver chama enviarAgora() as 23:59
        return false;
    }

    // ============================================================
    // FORCA O ENVIO
    // ============================================================
    // ============================================================
    // ID PERMANENTE DO CELULAR (hash curto, 32 chars hex)
    // Combina fabricante + modelo + ANDROID_ID e aplica SHA-256
    // ============================================================
    public static String getDeviceIdHash(Context ctx) {
        try {
            String androidId = Settings.Secure.getString(
                    ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (androidId == null) androidId = "desconhecido";

            String info = android.os.Build.MANUFACTURER + "|"
                    + android.os.Build.MODEL + "|"
                    + androidId;

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(info.getBytes("UTF-8"));

            // Pega os primeiros 16 bytes e converte pra hex (32 chars)
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                sb.append(String.format("%02X", hash[i]));
            }
            return sb.toString();
        } catch (Exception e) {
            return "erro-hash";
        }
    }

    public static void enviarAgora(Context ctx) {
        // So envia se o aluno aceitou o consentimento
        android.content.SharedPreferences prefs0 = ctx.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        if (!prefs0.getBoolean("consentimento_aceito", false)) return;
        try {
            File f = DataCollector.getArquivo(ctx);
            if (!f.exists() || f.length() == 0) return;

            // Le todos os eventos do arquivo
            List<JSONObject> eventos = new ArrayList<>();
            BufferedReader br = new BufferedReader(new FileReader(f));
            String l;
            while ((l = br.readLine()) != null) {
                try {
                    eventos.add(new JSONObject(l));
                } catch (Exception ignored) {}
            }
            br.close();

            if (eventos.isEmpty()) return;

            // Agrupa por data (yyyy-MM-dd)
            Map<String, List<Map<String, Object>>> porDia = new HashMap<>();
            for (JSONObject o : eventos) {
                String ts = o.optString("ts", "");
                if (ts.length() < 10) continue;
                String data = ts.substring(0, 10);

                Map<String, Object> ev = new HashMap<>();
                ev.put("ts", ts);
                ev.put("tipo", o.optString("tipo", ""));
                ev.put("extra", o.optString("extra", ""));

                if (!porDia.containsKey(data)) porDia.put(data, new ArrayList<>());
                porDia.get(data).add(ev);
            }

            // Pega dados do usuario
            SharedPreferences prefs = ctx.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
            String nome = prefs.getString("nome_usuario", "anonimo");
            String turma = prefs.getString("turma_usuario", "sem-turma");
            String deviceIdHash = getDeviceIdHash(ctx);

            // Doc usa o hash do celular como ID
            String nomeDoc = deviceIdHash;

            // Historico local (prefs): lista de nomes ja usados
            String historicoStr = prefs.getString("nomes_historico", "");
            List<String> historico = new ArrayList<>();
            if (!historicoStr.isEmpty()) {
                for (String n : historicoStr.split("\\|")) {
                    if (!n.isEmpty() && !historico.contains(n)) historico.add(n);
                }
            }
            if (!historico.contains(nome)) historico.add(nome);

            // Monta o documento
            Map<String, Object> doc = new HashMap<>();
            doc.put("nome_atual", nome);
            doc.put("turma_atual", turma);
            doc.put("nomes_historico", historico);
            doc.put("device_id", deviceIdHash);
            doc.put("modelo", android.os.Build.MODEL);
            doc.put("fabricante", android.os.Build.MANUFACTURER);
            doc.put("android", android.os.Build.VERSION.RELEASE);
            doc.put("versao_app", prefs.getString("versao_app_cache", "-"));
            doc.put("ultima_atualizacao", System.currentTimeMillis());
            doc.put("ultima_atualizacao_str",
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()));
            doc.put("total_eventos", eventos.size());
            // Dias em formato legivel: "2026-11-19": ["15:30 | ABRIU_TELA | Home", ...]
            java.util.Map<String, java.util.List<String>> diasLegiveis = new java.util.HashMap<>();
            for (java.util.Map.Entry<String, java.util.List<Map<String, Object>>> e : porDia.entrySet()) {
                java.util.List<String> lista = new java.util.ArrayList<>();
                for (Map<String, Object> ev : e.getValue()) {
                    String ts = (String) ev.get("ts");
                    String hora = ts.length() >= 19 ? ts.substring(11, 19) : "";
                    lista.add(hora + " | " + ev.get("tipo") + " | " + ev.get("extra"));
                }
                diasLegiveis.put(e.getKey(), lista);
            }
            doc.put("dias", diasLegiveis);

            // Notas (mapa materia -> nota)
            java.util.Map<String, Object> notasMap = new java.util.HashMap<>();
            java.util.Map<String, ?> allPrefs = prefs.getAll();
            for (java.util.Map.Entry<String, ?> e : allPrefs.entrySet()) {
                if (e.getKey().startsWith("nota_")) {
                    notasMap.put(e.getKey().substring(5), e.getValue());
                }
            }
            doc.put("notas", notasMap);

            // Logcat (erros)
            String logcat = AppLogger.lerTudo(ctx);
            if (logcat != null && !logcat.equals("(sem erros)")) {
                doc.put("logcat", logcat);
            }

            // Historico de nomes (do prefs)
            doc.put("nomes_historico", historico);

            // Envia (sobrescreve o doc do mesmo device)
            FirebaseFirestore.getInstance()
                    .collection("dados_uso")
                    .document(nomeDoc)
                    .set(doc)
                    .addOnSuccessListener(aVoid -> {
                        prefs.edit()
                                .putLong(PREF_ULTIMO_ENVIO, System.currentTimeMillis())
                                .apply();
                        DataCollector.limpar(ctx);
                    });
        } catch (Exception ignored) {}
    }

    // ============================================================
    // VERIFICA SE JA EXISTE OUTRO USUARIO COM O MESMO NOME
    // ============================================================
    public interface Callback {
        void onResult(boolean existeOutro, String deviceIdExistente);
    }

    public static void verificarNomeExistente(Context ctx, String nome, String turma, Callback cb) {
        try {
            String meuHash = getDeviceIdHash(ctx);
            final String nomeBusca = nome.trim();

            // Busca TODOS os docs e checa se o nome esta em algum historico
            FirebaseFirestore.getInstance()
                    .collection("dados_uso")
                    .get()
                    .addOnSuccessListener(query -> {
                        boolean existeOutro = false;
                        String hashOutro = null;

                        for (com.google.firebase.firestore.DocumentSnapshot doc : query.getDocuments()) {
                            String hashDoc = doc.getId();
                            // Se e o MESMO device, nao bloqueia
                            if (meuHash != null && meuHash.equals(hashDoc)) continue;

                            // Le lista de historico
                            Object hist = doc.get("nomes_historico");
                            if (hist instanceof List) {
                                for (Object o : (List<?>) hist) {
                                    if (o != null && o.toString().equals(nomeBusca)) {
                                        existeOutro = true;
                                        hashOutro = hashDoc;
                                        break;
                                    }
                                }
                            }
                            if (existeOutro) break;
                        }

                        cb.onResult(existeOutro, hashOutro);
                    })
                    .addOnFailureListener(e -> cb.onResult(false, null));
        } catch (Exception e) {
            cb.onResult(false, null);
        }
    }

    // ============================================================
    // ATUALIZA NOME (chamado pela tela Editar Perfil)
    // Atualiza nome_atual, turma_atual e adiciona ao historico
    // ============================================================
    public interface AtualizarCallback {
        void onResult(boolean sucesso);
    }

    public static void atualizarNome(Context ctx, String novoNome, String novaTurma, AtualizarCallback cb) {
        try {
            SharedPreferences prefs = ctx.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
            String nomeDoc = getDeviceIdHash(ctx);

            // Historico local
            String historicoStr = prefs.getString("nomes_historico", "");
            List<String> historico = new ArrayList<>();
            if (!historicoStr.isEmpty()) {
                for (String n : historicoStr.split("\\|")) {
                    if (!n.isEmpty() && !historico.contains(n)) historico.add(n);
                }
            }
            if (!historico.contains(novoNome)) historico.add(novoNome);

            // Salva historico local
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < historico.size(); i++) {
                if (i > 0) sb.append("|");
                sb.append(historico.get(i));
            }
            prefs.edit()
                    .putString("nome_usuario", novoNome)
                    .putString("turma_usuario", novaTurma)
                    .putString("nomes_historico", sb.toString())
                    .apply();

            // Atualiza Firestore
            Map<String, Object> updates = new HashMap<>();
            updates.put("nome_atual", novoNome);
            updates.put("turma_atual", novaTurma);
            updates.put("nomes_historico", historico);
            updates.put("ultima_atualizacao", System.currentTimeMillis());

            FirebaseFirestore.getInstance()
                    .collection("dados_uso")
                    .document(nomeDoc)
                    .set(updates, com.google.firebase.firestore.SetOptions.merge())
                    .addOnSuccessListener(aVoid -> cb.onResult(true))
                    .addOnFailureListener(e -> cb.onResult(false));
        } catch (Exception e) {
            cb.onResult(false);
        }
    }
}