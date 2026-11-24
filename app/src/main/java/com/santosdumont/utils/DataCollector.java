package com.santosdumont.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Coleta dados de uso do app em formato legível (texto).
 * Salva em: Android/data/com.santosdumont/files/dados_uso.txt
 * Formato: "yyyy-MM-dd HH:mm:ss | TIPO | extra"
 * Limite: 2 MB (apaga o mais antigo quando passa)
 */
public class DataCollector {

    public static final String NOME_ARQUIVO = "dados_uso.txt";
    public static final long TAMANHO_MAX = 2 * 1024 * 1024L;  // 2 MB

    // ============================================================
    // ARQUIVO
    // ============================================================
    public static File getArquivo(Context ctx) {
        File dir = ctx.getExternalFilesDir(null);
        if (dir == null) dir = ctx.getFilesDir();
        return new File(dir, NOME_ARQUIVO);
    }

    public static File getArquivoMensal(Context ctx) {
        File dir = ctx.getExternalFilesDir(null);
        if (dir == null) dir = ctx.getFilesDir();
        String mes = new SimpleDateFormat("yyyy-MM", Locale.US).format(new Date());
        return new File(dir, "dados_uso_" + mes + ".txt");
    }

    // ============================================================
    // REGISTRAR EVENTO
    // ============================================================
    public static void registrar(Context ctx, String tipo, String extra) {
        try {
            SharedPreferences prefs = ctx.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
            String chaveDia = "info_dia_" + dataHoje();
            if (!prefs.getBoolean(chaveDia, false)) {
                prefs.edit().putBoolean(chaveDia, true).apply();
                anexar(ctx, agora() + " | INFO_DISPOSITIVO | " + infoDispositivo(ctx));
                anexarMensal(ctx, agora() + " | INFO_DISPOSITIVO | " + infoDispositivo(ctx));
            }

            String linha = agora() + " | " + tipo + " | " + (extra != null ? extra : "");
            anexar(ctx, linha);
            anexarMensal(ctx, linha);
        } catch (Exception ignored) {}
    }

    // ============================================================
    // INFO DO DISPOSITIVO (legível)
    // ============================================================
    private static String infoDispositivo(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String deviceId = DeviceIdHelper.getId(ctx);
        String nome = prefs.getString("nome_usuario", "-");
        String turma = prefs.getString("turma_usuario", "-");
        String versao = prefs.getString("versao_app_cache", "-");

        String historicoStr = prefs.getString("nomes_historico", "");
        String historico = historicoStr.isEmpty() ? "-" : historicoStr.replace("|", ", ");

        long ramTotal = 0;
        try {
            android.app.ActivityManager am = (android.app.ActivityManager)
                    ctx.getSystemService(Context.ACTIVITY_SERVICE);
            android.app.ActivityManager.MemoryInfo mi = new android.app.ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);
            ramTotal = mi.totalMem / (1024 * 1024); // MB
        } catch (Exception ignored) {}

        long armazenamentoTotal = 0;
        try {
            File dir = Environment.getDataDirectory();
            armazenamentoTotal = dir.getTotalSpace() / (1024 * 1024 * 1024); // GB
        } catch (Exception ignored) {}

        // Notas
        StringBuilder notas = new StringBuilder();
        java.util.Map<String, ?> all = prefs.getAll();
        for (java.util.Map.Entry<String, ?> e : all.entrySet()) {
            if (e.getKey().startsWith("nota_")) {
                if (notas.length() > 0) notas.append(", ");
                notas.append(e.getKey().substring(5)).append("=").append(e.getValue());
            }
        }

        return "device_id=" + deviceId
                + " | nome=" + nome
                + " | turma=" + turma
                + " | historico=" + historico
                + " | modelo=" + Build.MODEL
                + " | fabricante=" + Build.MANUFACTURER
                + " | android=" + Build.VERSION.RELEASE
                + " | sdk=" + Build.VERSION.SDK_INT
                + " | ram=" + ramTotal + "MB"
                + " | armazenamento=" + armazenamentoTotal + "GB"
                + " | versao_app=" + versao
                + " | notas=[" + notas + "]";
    }

    // ============================================================
    // ANEXAR LINHA
    // ============================================================
    private static void anexarMensal(Context ctx, String linha) {
        try {
            File f = getArquivoMensal(ctx);
            FileWriter fw = new FileWriter(f, true);
            fw.write(linha + "\n");
            fw.close();
        } catch (Exception ignored) {}
    }

    private static void anexar(Context ctx, String linha) {
        try {
            File f = getArquivo(ctx);
            if (f.exists() && f.length() > TAMANHO_MAX) {
                limparMetade(ctx, f);
            }
            FileWriter fw = new FileWriter(f, true);
            fw.write(linha + "\n");
            fw.close();
        } catch (Exception ignored) {}
    }

    // ============================================================
    // LIMPAR METADE (mantem os mais recentes)
    // ============================================================
    private static void limparMetade(Context ctx, File f) {
        try {
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(f));
            java.util.List<String> linhas = new java.util.ArrayList<>();
            String l;
            while ((l = br.readLine()) != null) linhas.add(l);
            br.close();

            int inicio = linhas.size() / 2;
            FileWriter fw = new FileWriter(f, false);
            for (int i = inicio; i < linhas.size(); i++) {
                fw.write(linhas.get(i) + "\n");
            }
            fw.close();

            FileWriter fw2 = new FileWriter(f, true);
            fw2.write(agora() + " | LIMPEZA | Arquivo atingiu 2MB\n");
            fw2.close();
        } catch (Exception ignored) {}
    }

    // ============================================================
    // HELPERS
    // ============================================================
    public static String agora() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());
    }

    public static String dataHoje() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }

    // ============================================================
    // VER DADOS (retorna tudo como string)
    // ============================================================
    public static String lerTudo(Context ctx) {
        try {
            File f = getArquivo(ctx);
            if (!f.exists()) return "(sem dados)";
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(f));
            StringBuilder sb = new StringBuilder();
            String l;
            int cont = 0;
            while ((l = br.readLine()) != null) {
                sb.append(l).append("\n");
                cont++;
                if (cont > 500) { sb.append("... (truncado)\n"); break; }
            }
            br.close();
            return sb.toString();
        } catch (Exception e) {
            return "Erro: " + e.getMessage();
        }
    }

    // ============================================================
    // LIMPAR TUDO
    // ============================================================
    public static void limpar(Context ctx) {
        try {
            File f = getArquivo(ctx);
            if (f.exists()) f.delete();
        } catch (Exception ignored) {}
    }

    // ============================================================
    // TAMANHO
    // ============================================================
    public static long tamanho(Context ctx) {
        File f = getArquivo(ctx);
        return f.exists() ? f.length() : 0;
    }
}
