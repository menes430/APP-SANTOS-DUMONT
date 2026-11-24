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

public class AulaVagaReceiver extends BroadcastReceiver {

    private static final String CANAL_ID = "aula_vaga_canal";

    @Override
    public void onReceive(Context context, Intent intent) {
        // FIX: se o aluno marcou "Nao fui" hoje, nao notifica "volta pra aula"
        if (com.santosdumont.utils.PresencaHelper.faltouHoje(context)) return;

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    CANAL_ID, "Aula Vaga", NotificationManager.IMPORTANCE_HIGH);
            canal.setDescription("Aviso do fim do per\u00edodo de Aula Vaga");
            nm.createNotificationChannel(canal);
        }

        Intent abrir = new Intent(context, com.santosdumont.activities.ShellActivity.class);
        abrir.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(context, 0,
                abrir, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder b = new NotificationCompat.Builder(context, CANAL_ID)
                .setSmallIcon(R.drawable.ic_book)
                .setContentTitle("Aten\u00e7\u00e3o!")
                .setContentText("A fun\u00e7\u00e3o de aten\u00e7\u00e3o \u00e0 aula voltar\u00e1 em 3 minutos.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setColor(com.santosdumont.utils.ThemePrefs.getColorTheme(context).getPrimary())
                .setAutoCancel(true)
                .setContentIntent(pi);

        nm.notify(999, b.build());
    }
}