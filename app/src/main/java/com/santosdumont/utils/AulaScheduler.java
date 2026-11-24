package com.santosdumont.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import java.util.Calendar;

/**
 * Calcula as janelas de aula (MAT / VES) e agenda:
 *   - Inicio do servico (INICIAR_SERVICO)
 *   - Fim da janela (PARAR_SERVICO)
 *   - Envio diario (ENVIAR_DIARIO) as 23:59
 *
 * Dias ativos: Segunda a Sexta
 * Intervalos sao ignorados (servico fica parado)
 */
public class AulaScheduler {

    public static final String ACTION_INICIAR = "com.santosdumont.INICIAR_SERVICO";
    public static final String ACTION_PARAR   = "com.santosdumont.PARAR_SERVICO";
    public static final String ACTION_ENVIAR  = "com.santosdumont.ENVIAR_DIARIO";

    public static final int REQ_INICIAR = 2001;
    public static final int REQ_PARAR   = 2002;
    public static final int REQ_ENVIAR  = 2003;

    public static final String ACTION_NOTIFICAR_PRESENCA = "com.santosdumont.NOTIFICAR_PRESENCA";
    public static final int REQ_NOTIF_PRESENCA = 3001;

    public static final String ACTION_NOTIFICAR_ESCOLA = "com.santosdumont.NOTIFICAR_ESCOLA";
    public static final int REQ_NOTIF_ESCOLA = 3002;

    // Janelas em minutos desde 00:00
    private static final int[][] JANELAS_MAT = {
        {7 * 60,       9 * 60 + 40},   // 07:00 - 09:40
        {10 * 60 + 10, 11 * 60 + 50}   // 10:10 - 11:50
    };
    private static final int[][] JANELAS_VES = {
        {13 * 60,      15 * 60 + 30},  // 13:00 - 15:30
        {15 * 60 + 50, 17 * 60 + 30}   // 15:50 - 17:30
    };

    // ============================================================
    // AGENDAR TUDO (chamar ao abrir o app)
    // ============================================================
    public static void agendarTudo(Context ctx) {
        cancelarTudo(ctx);
        agendarProximosAlarmes(ctx);
    }

    // ============================================================
    // AGENDAR PROXIMOS ALARMES DO DIA
    // ============================================================
    public static void agendarProximosAlarmes(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String turno = prefs.getString("turno", "manha");

        int[][] janelas = "tarde".equals(turno) ? JANELAS_VES : JANELAS_MAT;

        // Pega agora em minutos desde 00:00
        Calendar agora = Calendar.getInstance();
        int diaSemana = agora.get(Calendar.DAY_OF_WEEK);
        int minutoAtual = agora.get(Calendar.HOUR_OF_DAY) * 60 + agora.get(Calendar.MINUTE);

        // So agenda para Seg-Sex
        boolean diaUtil = (diaSemana >= Calendar.MONDAY && diaSemana <= Calendar.FRIDAY);

        if (diaUtil) {
            for (int i = 0; i < janelas.length; i++) {
                int inicio = janelas[i][0];
                int fim    = janelas[i][1];

                // INICIAR: se ainda nao passou
                if (minutoAtual < inicio) {
                    agendarAlarme(ctx, ACTION_INICIAR, REQ_INICIAR + i, inicio);
                }
                // PARAR: se ainda nao passou
                if (minutoAtual < fim) {
                    agendarAlarme(ctx, ACTION_PARAR, REQ_PARAR + i, fim);
                }
            }
        }

        // ENVIAR DIARIO: sempre as 23:59
        agendarAlarme(ctx, ACTION_ENVIAR, REQ_ENVIAR, 23 * 60 + 59);

        // Notificacoes de presenca no fim de cada aula
        agendarNotificacoesDePresenca(ctx);

        // Reagenda os lembretes do NotificationScheduler (fora do app)
        try {
            NotificationScheduler.agendarNotificacoesDoDia(ctx);
        } catch (Exception ignored) {}

        // Notificacao "foi para a escola?" as 6:30
        agendarNotificacaoEscola(ctx);
    }

    // ============================================================
    // AGENDAR ALARME PARA HOJE (ou amanha se ja passou)
    // ============================================================
    private static void agendarAlarme(Context ctx, String action, int requestCode, int minutoDia) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        Intent i = new Intent(ctx, AulaAlarmReceiver.class);
        i.setAction(action);
        PendingIntent pi = PendingIntent.getBroadcast(
                ctx, requestCode, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, minutoDia / 60);
        cal.set(Calendar.MINUTE, minutoDia % 60);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        // Se ja passou, joga pra amanha
        if (cal.getTimeInMillis() <= System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Se for sabado/domingo, pula pro proximo dia util
        int diaSemana = cal.get(Calendar.DAY_OF_WEEK);
        while (diaSemana == Calendar.SATURDAY || diaSemana == Calendar.SUNDAY) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
            diaSemana = cal.get(Calendar.DAY_OF_WEEK);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (am.canScheduleExactAlarms()) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
            } else {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
            }
        } else {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
        }
    }

    // ============================================================
    // CANCELAR TUDO
    // ============================================================
    public static void cancelarTudo(Context ctx) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        int[] reqs = {
                REQ_INICIAR, REQ_INICIAR + 1,
                REQ_PARAR, REQ_PARAR + 1,
                REQ_ENVIAR
        };

        for (int req : reqs) {
            Intent i = new Intent(ctx, AulaAlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(
                    ctx, req, i,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            am.cancel(pi);
        }
    }

    // ============================================================
    // AGENDAR NOTIFICACOES DE PRESENCA (fim de cada aula)
    // ============================================================
    public static void agendarNotificacoesDePresenca(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String turma = prefs.getString("turma_usuario", "1A MAT");
        String diaSemana = getDiaSemanaAtual();
        java.util.List<com.santosdumont.models.Aula> aulas =
                DataHelper.getHorarios(ctx, turma, diaSemana);
        if (aulas == null || aulas.isEmpty()) return;

        Calendar agora = Calendar.getInstance();
        int minutoAtual = agora.get(Calendar.HOUR_OF_DAY) * 60 + agora.get(Calendar.MINUTE);

        for (int i = 0; i < aulas.size(); i++) {
            com.santosdumont.models.Aula a = aulas.get(i);
            String disc = a.getDisciplina() == null ? "" : a.getDisciplina().trim();
            if (disc.isEmpty() || disc.equals("-")) continue;
            if (disc.equalsIgnoreCase("INTERVALO") || disc.equalsIgnoreCase("SAÍDA") || disc.equalsIgnoreCase("SAIDA")) continue;
            if (a.isSemAula() || a.isTextoLivre()) continue;

            // Notifica 5 min apos o inicio da aula
            int inicioMin = parseHoraMinuto(a.getHorario());
            if (inicioMin <= 0) continue;
            int fimMin = inicioMin + 5;

            Intent it = new Intent(ctx, AulaAlarmReceiver.class);
            it.setAction(ACTION_NOTIFICAR_PRESENCA);
            it.putExtra("disciplina", disc);
            it.putExtra("professor", a.getProfessor());
            it.putExtra("horario", a.getHorario());
            java.util.Calendar calFreq = java.util.Calendar.getInstance();
            String dataFreq = calFreq.get(java.util.Calendar.YEAR) + "-"
                    + calFreq.get(java.util.Calendar.MONTH) + "-"
                    + calFreq.get(java.util.Calendar.DAY_OF_MONTH);
            it.putExtra("dataFreq", dataFreq);
            int reqCode = REQ_NOTIF_PRESENCA + i;
            PendingIntent pi = PendingIntent.getBroadcast(
                    ctx, reqCode, it,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, fimMin / 60);
            cal.set(Calendar.MINUTE, fimMin % 60);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            // Se ja passou, joga pra amanha (proximo dia util)
            if (cal.getTimeInMillis() <= System.currentTimeMillis()) {
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
            int diaSemanaCal = cal.get(Calendar.DAY_OF_WEEK);
            while (diaSemanaCal == Calendar.SATURDAY || diaSemanaCal == Calendar.SUNDAY) {
                cal.add(Calendar.DAY_OF_MONTH, 1);
                diaSemanaCal = cal.get(Calendar.DAY_OF_WEEK);
            }

            AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
            if (am == null) continue;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
            } else {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
            }
        }
    }

    private static int parseHoraMinuto(String h) {
        if (h == null) return -1;
        h = h.trim();
        // "9:50-10:10" -> pega o primeiro "9:50"
        int dash = h.indexOf("-");
        if (dash > 0) h = h.substring(0, dash).trim();
        int colon = h.indexOf(":");
        if (colon <= 0) return -1;
        try {
            int hh = Integer.parseInt(h.substring(0, colon).trim());
            int mm = Integer.parseInt(h.substring(colon + 1).trim());
            return hh * 60 + mm;
        } catch (Exception e) { return -1; }
    }

    private static String getDiaSemanaAtual() {
        int dia = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        switch (dia) {
            case Calendar.MONDAY:    return "Segunda";
            case Calendar.TUESDAY:   return "Terça";
            case Calendar.WEDNESDAY: return "Quarta";
            case Calendar.THURSDAY:  return "Quinta";
            case Calendar.FRIDAY:    return "Sexta";
            default:                 return "Segunda";
        }
    }


    // ============================================================
    // NOTIFICACAO "FOI PARA A ESCOLA?" AS 6:30
    // ============================================================
    private static void agendarNotificacaoEscola(Context ctx) {
        Calendar agora = Calendar.getInstance();
        int diaSemana = agora.get(Calendar.DAY_OF_WEEK);
        if (diaSemana < Calendar.MONDAY || diaSemana > Calendar.FRIDAY) return;
        int minutoAtual = agora.get(Calendar.HOUR_OF_DAY) * 60 + agora.get(Calendar.MINUTE);
        int alvo = 6 * 60 + 30; // 6:30

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, alvo / 60);
        cal.set(Calendar.MINUTE, alvo % 60);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        if (cal.getTimeInMillis() <= System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        int dow = cal.get(Calendar.DAY_OF_WEEK);
        while (dow == Calendar.SATURDAY || dow == Calendar.SUNDAY) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
            dow = cal.get(Calendar.DAY_OF_WEEK);
        }

        Intent i = new Intent(ctx, AulaAlarmReceiver.class);
        i.setAction(ACTION_NOTIFICAR_ESCOLA);
        PendingIntent pi = PendingIntent.getBroadcast(
                ctx, REQ_NOTIF_ESCOLA, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
        } else {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
        }
    }

}