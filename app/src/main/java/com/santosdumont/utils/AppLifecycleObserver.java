package com.santosdumont.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AppLifecycleObserver implements Application.ActivityLifecycleCallbacks {

    // Tempo base: 5 minutos. Cada saida diminui 1 min ate o minimo de 1 min.
    private static final long TEMPO_BASE_MS = 5 * 60 * 1000L;
    private static final long TEMPO_MIN_MS  = 1 * 60 * 1000L;
    private static final long AULA_VAGA_MS  = 60 * 60 * 1000L;  // 1 hora

    private long timestampSaiu = 0;
    private int activitiesAtivas = 0;
    private boolean mostrandoBloqueio = false;

    private final SharedPreferences prefs;

    public AppLifecycleObserver(Context ctx) {
        this.prefs = ctx.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
    }

    private long getTempoLimite() {
        int saidas = prefs.getInt("saidas_consecutivas", 0);
        long limite = TEMPO_BASE_MS - (saidas * 60 * 1000L);
        if (limite < TEMPO_MIN_MS) limite = TEMPO_MIN_MS;
        return limite;
    }

    public boolean aulaVagaAtiva() {
        long fim = prefs.getLong("aula_vaga_fim", 0);
        return System.currentTimeMillis() < fim;
    }

    /**
     * Retorna true se o horario atual esta DENTRO de uma janela de aula.
     * Janelas: 7h-9h40, 10h10-11h50, 13h-15h30, 15h50-17h30.
     * Intervalo (9h40-10h10) e saida (11h50) NAO contam.
     */
    private boolean dentroDoHorarioDeAula() {
        java.util.Calendar c = java.util.Calendar.getInstance();
        int h = c.get(java.util.Calendar.HOUR_OF_DAY);
        int m = c.get(java.util.Calendar.MINUTE);
        int minutoAtual = h * 60 + m;
        return (minutoAtual >= 7*60 && minutoAtual < 9*60+40)
                || (minutoAtual >= 10*60+10 && minutoAtual < 11*60+50)
                || (minutoAtual >= 13*60 && minutoAtual < 15*60+30)
                || (minutoAtual >= 15*60+50 && minutoAtual < 17*60+30);
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        activitiesAtivas++;

            // === APP_ABRIU: registra que o app foi aberto hoje ===
            if (activitiesAtivas == 1) {
                String hoje = com.santosdumont.utils.DataCollector.dataHoje();
                prefs.edit().putBoolean("usou_hoje_" + hoje, true).apply();
                com.santosdumont.utils.DataCollector.registrar(
                        activity.getApplicationContext(), "APP_ABRIU", "");
            }
        if (activitiesAtivas == 1 && !mostrandoBloqueio) {
            // Ignora se esta na propria BloqueioActivity
            if (activity instanceof com.santosdumont.activities.BloqueioActivity) return;
            // Ignora se aula vaga esta ativa
            if (aulaVagaAtiva()) { timestampSaiu = 0; return; }
            // So conta se estiver DENTRO do horario de aula
            if (!dentroDoHorarioDeAula()) { timestampSaiu = 0; return; }

            if (timestampSaiu > 0) {
                long tempoFora = System.currentTimeMillis() - timestampSaiu;
                long limite = getTempoLimite();

                if (tempoFora >= limite) {
                    // Aumenta o contador de saidas (proxima vez sera mais rigoroso)
                    int saidas = prefs.getInt("saidas_consecutivas", 0);
                    prefs.edit().putInt("saidas_consecutivas", saidas + 1).apply();

                    mostrandoBloqueio = true;
                    Intent i = new Intent(activity, com.santosdumont.activities.BloqueioActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(i);
                }
            }
            timestampSaiu = 0;
        }
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        activitiesAtivas--;
        if (activitiesAtivas == 0) {
            // So conta o tempo fora se estiver DENTRO do horario de aula
            if (dentroDoHorarioDeAula()) {
                timestampSaiu = System.currentTimeMillis();
            } else {
                timestampSaiu = 0;
            }

            // Coleta: registra saida do app
            try {
                com.santosdumont.utils.DataCollector.registrar(
                        activity.getApplicationContext(), "APP_SAIU", "");
            } catch (Exception ignored) {}
        }
    }

    @Override public void onActivityCreated(@NonNull Activity a, @Nullable Bundle b) { }
    @Override public void onActivityStarted(@NonNull Activity a) { }
    @Override public void onActivityPaused(@NonNull Activity a) { }
    @Override public void onActivitySaveInstanceState(@NonNull Activity a, @NonNull Bundle b) { }
    @Override public void onActivityDestroyed(@NonNull Activity a) { }

    public void liberarBloqueio() {
        mostrandoBloqueio = false;
    }

    // Reseta o contador de saidas (chamado quando o dia muda ou a aula vaga acaba)
    public void resetarSaidas() {
        prefs.edit().putInt("saidas_consecutivas", 0).apply();
    }
}