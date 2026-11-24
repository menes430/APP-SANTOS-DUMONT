package com.santosdumont.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * Recebe alarmes do AulaScheduler:
 *   - INICIAR_SERVICO -> inicia AulaForegroundService
 *   - PARAR_SERVICO   -> para AulaForegroundService
 *   - ENVIAR_DIARIO   -> envia dados pro Firebase (23:59)
 *
 * Sempre reagenda o proximo alarme no fim.
 */
public class AulaAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;
        String action = intent.getAction();
        if (action == null) return;

        try {
            switch (action) {
                case AulaScheduler.ACTION_INICIAR:
                    iniciarServico(context);
                    break;

                case AulaScheduler.ACTION_PARAR:
                    pararServico(context);
                    // Envia ao Firebase ao fim de cada aula
                    try {
                        FirebaseUploader.enviarAgora(context);
                    } catch (Exception ignored) {}
                    break;

                case AulaScheduler.ACTION_ENVIAR:
                    enviarDiario(context);
                    break;

                case AulaScheduler.ACTION_NOTIFICAR_PRESENCA:
                    String disc = intent.getStringExtra("disciplina");
                    String prof = intent.getStringExtra("professor");
                    String horario = intent.getStringExtra("horario");
                    String dataFreq = intent.getStringExtra("dataFreq");
                    NotificationHelper.enviarNotificacaoComAcoes(context, disc, prof, horario, dataFreq);
                    break;

                case AulaScheduler.ACTION_NOTIFICAR_ESCOLA:
                    NotificationHelper.enviarNotificacaoEscola(context);
                    break;
            }
        } catch (Exception ignored) {}

        // Reagenda os proximos alarmes
        // So reagenda se nao for notificacao de presenca
        if (!AulaScheduler.ACTION_NOTIFICAR_PRESENCA.equals(action)) {
            try {
                AulaScheduler.agendarProximosAlarmes(context);
            } catch (Exception ignored) {}
        }
    }

    // ============================================================
    // INICIAR SERVICO EM PRIMEIRO PLANO
    // ============================================================
    private void iniciarServico(Context ctx) {
        Intent i = new Intent(ctx, AulaForegroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ctx.startForegroundService(i);
        } else {
            ctx.startService(i);
        }
    }

    // ============================================================
    // PARAR SERVICO
    // ============================================================
    private void pararServico(Context ctx) {
        Intent i = new Intent(ctx, AulaForegroundService.class);
        ctx.stopService(i);
    }

    // ============================================================
    // ENVIAR DIARIO PRO FIREBASE (23:59)
    // So envia se o app foi usado no dia (flag)
    // ============================================================
    private void enviarDiario(Context ctx) {
        try {
            android.content.SharedPreferences prefs =
                    ctx.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
            boolean usouHoje = prefs.getBoolean("usou_hoje_" + DataCollector.dataHoje(), false);
            if (!usouHoje) return;

            FirebaseUploader.enviarAgora(ctx);
        } catch (Exception ignored) {}
    }
}