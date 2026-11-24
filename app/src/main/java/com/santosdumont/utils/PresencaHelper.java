package com.santosdumont.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.santosdumont.models.Aula;

import java.util.Calendar;
import java.util.List;

/**
 * Helper que controla a presenca do aluno.
 * Fluxo: ao abrir o app, pergunta se o aluno foi a escola.
 * Se nao, marca falta nas aulas que ainda vao acontecer.
 */
public class PresencaHelper {

    // MODO TESTE: se true, sempre mostra o dialog (ignora horario/dia)
    public static boolean MODO_TESTE = false;


    public static final String PREF_RESPOSTA_DIA = "presenca_resposta_";
    public static final String PREF_FALTOU_DIA  = "presenca_faltou_";
    public static final String PREF_PROFESSOR_FALTOU_DIA = "presenca_professor_faltou_";

    /**
     * Retorna true se hoje tem aula (segunda a sexta).
     */
    public static boolean hojeTemAula() {
        if (MODO_TESTE) return true;
        int dow = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        return dow >= Calendar.MONDAY && dow <= Calendar.FRIDAY;
    }

    /**
     * Retorna true se o horario atual esta dentro do horario escolar (07:00-12:00).
     */
    public static boolean dentroDoHorarioEscolar() {
        if (MODO_TESTE) return true;
        Calendar c = Calendar.getInstance();
        int h = c.get(Calendar.HOUR_OF_DAY);
        return h >= 7 && h < 12;
    }

    /**
     * Retorna true se o aluno ja respondeu hoje.
     */
    public static boolean jaRespondeuHoje(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String chave = PREF_RESPOSTA_DIA + dataHoje();
        return prefs.getBoolean(chave, false);
    }

    /**
     * Salva a resposta do aluno.
     * @param faltou true = aluno nao foi a escola
     */
    public static void salvarResposta(Context ctx, boolean faltou) {
        SharedPreferences prefs = ctx.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String chaveResp = PREF_RESPOSTA_DIA + dataHoje();
        String chaveFaltou = PREF_FALTOU_DIA + dataHoje();
        prefs.edit()
                .putBoolean(chaveResp, true)
                .putBoolean(chaveFaltou, faltou)
                .apply();
    }

    /**
     * Retorna true se o aluno faltou hoje.
     */
    public static void salvarRespostaAula(Context ctx, String data, String horario, String status) {
        SharedPreferences prefs = ctx.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String chave = "freq_" + data + "_" + horario;
        prefs.edit().putString(chave, status).apply();
    }

    public static boolean faltouHoje(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String chave = PREF_FALTOU_DIA + dataHoje();
        return prefs.getBoolean(chave, false);
    }

    /**
     * Marca falta nas aulas APOS o horario atual.
     * Se estiver fora do horario escolar (antes de comecar), marca todas.
     */
    public static void marcarFaltaNasRestantes(Context ctx, String turma) {
        SharedPreferences prefs = ctx.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);

        String diaSemana = getDiaSemana();
        List<Aula> aulas = DataHelper.getHorarios(ctx, turma, diaSemana);
        if (aulas == null || aulas.isEmpty()) return;

        Calendar c = Calendar.getInstance();
        int horaAtual = c.get(Calendar.HOUR_OF_DAY);
        int minAtual = c.get(Calendar.MINUTE);

        Calendar cal = Calendar.getInstance();
        String chaveBase = "freq_" + cal.get(Calendar.YEAR) + "-"
                + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH);

        for (Aula aula : aulas) {
            String horario = aula.getHorario();
            if (horario == null || horario.isEmpty()) continue;
            if (aula.getDisciplina() != null &&
                    (aula.getDisciplina().equals("INTERVALO") ||
                     aula.getDisciplina().equals("SAIDA"))) continue;

            // Compara horario da aula com o atual
            try {
                String[] partes = horario.split(":");
                int hAula = Integer.parseInt(partes[0].trim());
                int mAula = Integer.parseInt(partes[1].trim());

                // Se a aula comecou depois do horario atual, marca falta
                if (hAula > horaAtual || (hAula == horaAtual && mAula >= minAtual)) {
                    String chave = chaveBase + "_" + horario;
                    prefs.edit().putString(chave, "falta").apply();
                }
            } catch (Exception ignored) {}
        }
    }

    /**
     * Aluno presente, professor faltou.
     * Grava que o aluno respondeu hoje e o flag do professor.
     * NAO marca falta do aluno.
     */
    public static void salvarRespostaProfessorFaltou(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String chaveResp = PREF_RESPOSTA_DIA + dataHoje();
        String chaveProf = PREF_PROFESSOR_FALTOU_DIA + dataHoje();
        prefs.edit()
                .putBoolean(chaveResp, true)
                .putBoolean(chaveProf, true)
                .apply();
    }

    private static String dataHoje() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.DAY_OF_MONTH);
    }

    private static String getDiaSemana() {
        int dia = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        switch (dia) {
            case Calendar.MONDAY:    return "Segunda";
            case Calendar.TUESDAY:   return "Terca";
            case Calendar.WEDNESDAY: return "Quarta";
            case Calendar.THURSDAY:  return "Quinta";
            case Calendar.FRIDAY:    return "Sexta";
            default:                 return "Segunda";
        }
    }
}