package com.santosdumont.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Logcat do app: captura erros (Log.e) e crashes (UncaughtExceptionHandler).
 * Salva em: Android/data/com.santosdumont/files/logcat.txt
 * Limite: 512 KB (apaga o mais antigo quando passa).
 */
public class AppLogger {

    public static final String NOME_ARQUIVO = "logcat.txt";
    public static final long TAMANHO_MAX = 512 * 1024L; // 512 KB

    private static boolean instalado = false;

    public static void instalar(Context ctx) {
        if (instalado) return;
        instalado = true;

        final Context app = ctx.getApplicationContext();

        // Captura crashes
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            try {
                StringWriter sw = new StringWriter();
                throwable.printStackTrace(new PrintWriter(sw));
                registrar(app, "CRASH", thread.getName() + " | " + sw.toString());
            } catch (Exception ignored) {}
            // Deixa o Android tratar (fecha o app)
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        });
    }

    public static void registrar(Context ctx, String tipo, String mensagem) {
        try {
            File f = getArquivo(ctx);
            if (f.exists() && f.length() > TAMANHO_MAX) {
                // Apaga metade
                java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(f));
                java.util.List<String> linhas = new java.util.ArrayList<>();
                String l;
                while ((l = br.readLine()) != null) linhas.add(l);
                br.close();
                int inicio = linhas.size() / 2;
                FileWriter fw = new FileWriter(f, false);
                for (int i = inicio; i < linhas.size(); i++) fw.write(linhas.get(i) + "\n");
                fw.close();
            }

            String ts = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());
            FileWriter fw = new FileWriter(f, true);
            fw.write(ts + " | " + tipo + " | " + mensagem + "\n");
            fw.close();
        } catch (Exception ignored) {}
    }

    public static void e(Context ctx, String tag, String msg) {
        Log.e(tag, msg);
        registrar(ctx, "ERRO", tag + ": " + msg);
    }

    public static void e(Context ctx, String tag, String msg, Throwable t) {
        Log.e(tag, msg, t);
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        registrar(ctx, "ERRO", tag + ": " + msg + " | " + sw.toString());
    }

    public static File getArquivo(Context ctx) {
        File dir = ctx.getExternalFilesDir(null);
        if (dir == null) dir = ctx.getFilesDir();
        return new File(dir, NOME_ARQUIVO);
    }

    public static String lerTudo(Context ctx) {
        try {
            File f = getArquivo(ctx);
            if (!f.exists()) return "(sem erros)";
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(f));
            StringBuilder sb = new StringBuilder();
            String l;
            int cont = 0;
            while ((l = br.readLine()) != null) {
                sb.append(l).append("\n");
                cont++;
                if (cont > 200) { sb.append("... (truncado)\n"); break; }
            }
            br.close();
            return sb.toString();
        } catch (Exception e) {
            return "Erro: " + e.getMessage();
        }
    }

    public static void limpar(Context ctx) {
        try {
            File f = getArquivo(ctx);
            if (f.exists()) f.delete();
        } catch (Exception ignored) {}
    }
}
