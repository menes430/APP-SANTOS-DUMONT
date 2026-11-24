package com.santosdumont.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.santosdumont.R;
import com.santosdumont.activities.LembreteActivity;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "lembretes_persistentes";

    @Override
    public void onReceive(Context context, Intent intent) {
        String titulo = intent.getStringExtra("titulo");
        String mensagem = intent.getStringExtra("mensagem");
        boolean persistente = intent.getBooleanExtra("persistente", false);
        String materia = intent.getStringExtra("materia");
        String data = intent.getStringExtra("data");

        if (titulo == null) titulo = "Santos Dumont";
        if (mensagem == null) mensagem = "Hora do lembrete!";

        // Criar canal
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Lembretes Persistentes",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Lembretes que precisam ser confirmados");
            channel.enableVibration(true);
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        // Intent para abrir a tela de lembrete
        Intent telaLembrete = new Intent(context, LembreteActivity.class);
        telaLembrete.putExtra("titulo", titulo);
        telaLembrete.putExtra("mensagem", mensagem);
        telaLembrete.putExtra("notifId", (int) System.currentTimeMillis());
        telaLembrete.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent fullScreenIntent = PendingIntent.getActivity(
                context, 0, telaLembrete,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Notificação
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_plane)
                .setContentTitle(titulo)
                .setContentText(mensagem)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mensagem))
                .setContentIntent(fullScreenIntent)
                .setFullScreenIntent(fullScreenIntent, true)
                .setOngoing(persistente) // Se for persistente, não some ao deslizar
                .setAutoCancel(!persistente) // Se não for persistente, cancela ao clicar
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        if (persistente) {
            // Adiciona ação "Entendi" na notificação
            Intent intentEntendi = new Intent(context, NotificationReceiver.class);
            intentEntendi.putExtra("cancelar", true);
            intentEntendi.putExtra("notifId", (int) System.currentTimeMillis());
            PendingIntent pendingEntendi = PendingIntent.getBroadcast(
                    context, 1, intentEntendi,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            builder.addAction(R.drawable.ic_plane, "Entendi", pendingEntendi);
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}