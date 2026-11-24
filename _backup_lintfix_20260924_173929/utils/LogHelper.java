package com.santosdumont.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Sistema de log de erros do app.
 * Salva arquivos .txt em Documentos/SantosDumont/Logs/
 */
public class LogHelper {

    private static final String PASTA = "SantosDumont/Logs";

    /** Salva um erro genérico */
    public static void salvarErro(Context ctx, String tag, String mensagem, Throwable erro) {
        try {
            String data = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.getDefault())
                    .format(new Date());

            StringBuilder sb = new StringBuilder();
            sb.append("============================================================\n");
            sb.append("LOG DE ERRO - APP SANTOS DUMONT\n");
            sb.append("============================================================\n");
            sb.append("Data:        ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date())).append("\n");
            sb.append("Tag:         ").append(tag).append("\n");
            sb.append("Mensagem:    ").append(mensagem).append("\n");
            sb.append("\n--- DISPOSITIVO ---\n");
            sb.append("Modelo:      ").append(Build.MANUFACTURER).append(" ").append(Build.MODEL).append("\n");
            sb.append("Android:     ").append(Build.VERSION.RELEASE).append(" (API ").append(Build.VERSION.SDK_INT).append(")\n");
            sb.append("\n--- STACK TRACE ---\n");
            if (erro != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                erro.printStackTrace(pw);
                sb.append(sw.toString());
            } else {
                sb.append("(sem stack trace)\n");
            }
            sb.append("\n============================================================\n");

            File arquivo = new File(getPastaLogs(), "erro_" + data + ".txt");
            FileWriter fw = new FileWriter(arquivo, false);
            fw.write(sb.toString());
            fw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Salva um evento/aviso (não é erro) */
    public static void salvarEvento(Context ctx, String tag, String mensagem) {
        try {
            String data = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

            StringBuilder sb = new StringBuilder();
            sb.append(new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()));
            sb.append("  [").append(tag).append("]  ").append(mensagem).append("\n");

            File arquivo = new File(getPastaLogs(), "eventos_" + data + ".txt");
            FileWriter fw = new FileWriter(arquivo, true);
            fw.write(sb.toString());
            fw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Pasta de logs em Documentos/SantosDumont/Logs */
    private static File getPastaLogs() {
        File docs;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ — usa getExternalFilesDir (mais seguro)
            docs = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOCUMENTS);
        } else {
            docs = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        }

        File pasta = new File(docs, PASTA);
        if (!pasta.exists()) pasta.mkdirs();
        return pasta;
    }

    /** Caminho da pasta (pra mostrar ao usuário) */
    public static String getCaminhoLogs() {
        return getPastaLogs().getAbsolutePath();
    }
}