package com.santosdumont.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.santosdumont.R;
import com.santosdumont.activities.ShellActivity;

/**
 * Servico em primeiro plano que roda APENAS durante as janelas de aula.
 *
 * Funcoes:
 *   - Mantem o app vivo (evita kill do Android)
 *   - Notificacao persistente "Aulas em andamento"
 *   - WakeLock parcial (economia de bateria)
 *
 * Iniciado/parado por AulaAlarmReceiver nos horarios definidos pelo AulaScheduler.
 */
public class AulaForegroundService extends Service {

    private static final String CHANNEL_ID = "aulas_foreground";
    private static final int NOTIF_ID = 1001;

    private PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate() {
        super.onCreate();
        criarCanal();
        adquirirWakeLock();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Notificacao persistente (obrigatoria Android 12+)
        Notification notif = criarNotificacao();
        startForeground(NOTIF_ID, notif);

        // Agenda parada automatica no fim da janela (fallback)
        // (o AulaAlarmReceiver tambem envia PARAR_SERVICO)
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        liberarWakeLock();
        stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // ============================================================
    // NOTIFICACAO PERSISTENTE
    // ============================================================
    private void criarCanal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    CHANNEL_ID,
                    "Aulas em andamento",
                    NotificationManager.IMPORTANCE_LOW);
            canal.setDescription("Mantem o app ativo durante as aulas");
            canal.setShowBadge(false);
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(canal);
        }
    }

    private Notification criarNotificacao() {
        Intent abrir = new Intent(this, ShellActivity.class);
        abrir.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(
                this, 0, abrir,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_book)
                .setContentTitle("\uD83D\uDCDA Aulas em andamento")
                .setContentText("Santos Dumont esta ativo durante as aulas")
                .setContentIntent(pi)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();
    }

    // ============================================================
    // WAKELOCK (parcial, so durante o servico)
    // ============================================================
    private void adquirirWakeLock() {
        try {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (pm == null) return;
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SantosDumont::AulaLock");
            wakeLock.setReferenceCounted(false);
            wakeLock.acquire(2 * 60 * 60 * 1000L); // 2h max (auto-libera)
        } catch (Exception ignored) {}
    }

    private void liberarWakeLock() {
        try {
            if (wakeLock != null && wakeLock.isHeld()) {
                wakeLock.release();
            }
        } catch (Exception ignored) {}
        wakeLock = null;
    }
}