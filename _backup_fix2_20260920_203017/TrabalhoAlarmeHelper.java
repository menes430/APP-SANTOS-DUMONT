package com.santosdumont.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.santosdumont.models.TrabalhoModel;

public class TrabalhoAlarmeHelper {

    public static final String EXTRA_ID = "trabalho_id";
    public static final String EXTRA_NOME = "trabalho_nome";

    public static void agendarDiaSimDiaNao(Context ctx, TrabalhoModel t) {
        if (!t.notifDiaSimDiaNao) return;
        long[] datas = calcularDiasAlternados(t);
        for (int i = 0; i < datas.length; i++) agendarNotificacao(ctx, t, datas[i], 100 + i);
    }

    public static void agendarUltimaSemana(Context ctx, TrabalhoModel t) {
        if (!t.notifUltimaSemana) return;
        long[] datas = calcularUltimos7Dias(t);
        for (int i = 0; i < datas.length; i++) agendarNotificacao(ctx, t, datas[i], 200 + i);
    }

    public static void agendarPosAlarme(Context ctx, TrabalhoModel t) {
        if (!t.notifPosAlarme) return;
        long[] datas = calcularPosAlarme(t);
        for (int i = 0; i < datas.length; i++) agendarNotificacao(ctx, t, datas[i], 300 + i);
    }

    private static void agendarNotificacao(Context ctx, TrabalhoModel t, long timestamp, int requestCode) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        Intent intent = new Intent(ctx, NotificationReceiver.class);
        intent.putExtra(EXTRA_ID, t.id);
        intent.putExtra(EXTRA_NOME, t.nome);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) flags |= PendingIntent.FLAG_IMMUTABLE;
        PendingIntent pi = PendingIntent.getBroadcast(ctx, requestCode, intent, flags);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (am.canScheduleExactAlarms()) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timestamp, pi);
            } else {
                am.set(AlarmManager.RTC_WAKEUP, timestamp, pi);
            }
        } else {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timestamp, pi);
        }
    }

    private static long[] calcularDiasAlternados(TrabalhoModel t) {
        long alvo = parseDataHora(t.data, t.hora);
        long agora = System.currentTimeMillis();
        long intervalo = 48L * 60 * 60 * 1000;
        int total = (int) ((alvo - agora) / intervalo);
        if (total <= 0) return new long[0];
        long[] r = new long[total];
        for (int i = 0; i < total; i++) r[i] = agora + (i + 1) * intervalo;
        return r;
    }

    private static long[] calcularUltimos7Dias(TrabalhoModel t) {
        long alvo = parseDataHora(t.data, t.hora);
        long dia = 24L * 60 * 60 * 1000;
        long[] r = new long[7];
        for (int i = 0; i < 7; i++) r[i] = alvo - (7 - i) * dia;
        return r;
    }

    private static long[] calcularPosAlarme(TrabalhoModel t) {
        long alvo = parseDataHora(t.data, t.hora);
        long dia = 24L * 60 * 60 * 1000;
        int n = Math.max(1, t.diasPosAlarme);
        long[] r = new long[n];
        for (int i = 0; i < n; i++) r[i] = alvo + (i + 1) * dia;
        return r;
    }

    private static long parseDataHora(String data, String hora) {
        try {
            String[] d = data.split("/");
            String[] h = hora.split(":");
            java.util.Calendar cal = java.util.Calendar.getInstance();
            int ano = Integer.parseInt(d[2]);
            if (ano < 100) ano += 2000;
            cal.set(java.util.Calendar.YEAR, ano);
            cal.set(java.util.Calendar.MONTH, Integer.parseInt(d[1]) - 1);
            cal.set(java.util.Calendar.DAY_OF_MONTH, Integer.parseInt(d[0]));
            cal.set(java.util.Calendar.HOUR_OF_DAY, Integer.parseInt(h[0]));
            cal.set(java.util.Calendar.MINUTE, Integer.parseInt(h[1]));
            cal.set(java.util.Calendar.SECOND, 0);
            cal.set(java.util.Calendar.MILLISECOND, 0);
            return cal.getTimeInMillis();
        } catch (Exception e) {
            return System.currentTimeMillis() + 86400000L;
        }
    }
}