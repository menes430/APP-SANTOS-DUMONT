package com.santosdumont.utils;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.santosdumont.R;
import com.santosdumont.activities.ShellActivity;
import java.util.Calendar;

public class NotificationHelper {

    public static final String CHANNEL_ID = "aulas_channel";

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Lembretes de Aulas",
                NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public static void enviarNotificacao(Context context, String titulo, String mensagem) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, ShellActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_plane)
                .setContentTitle(titulo)
                .setContentText(mensagem)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    public static void enviarNotificacaoComAcoes(Context context, String disciplina, String professor, String horario, String dataFreq) {
        String canal = CHANNEL_ID;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            android.app.NotificationManager nm = context.getSystemService(android.app.NotificationManager.class);
            if (nm != null && nm.getNotificationChannel(canal) == null) {
                createNotificationChannel(context);
            }
        }
        String corpo;
        if (professor == null || professor.trim().isEmpty() || "-".equals(professor.trim())) {
            corpo = disciplina == null ? "" : disciplina;
        } else {
            corpo = (disciplina == null ? "" : disciplina) + " — " + professor;
        }
        android.content.Intent iSim = new android.content.Intent(context, NotificationReceiver.class);
        iSim.setAction("PARTICIPEI");
        iSim.putExtra("notifId", 9101);
        iSim.putExtra("horario", horario);
        iSim.putExtra("dataFreq", dataFreq);
        android.app.PendingIntent pSim = android.app.PendingIntent.getBroadcast(
                context, 9101, iSim,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);
        android.content.Intent iNao = new android.content.Intent(context, NotificationReceiver.class);
        iNao.setAction("NAO_PARTICIPEI");
        iNao.putExtra("notifId", 9101);
        iNao.putExtra("horario", horario);
        iNao.putExtra("dataFreq", dataFreq);
        android.app.PendingIntent pNao = android.app.PendingIntent.getBroadcast(
                context, 9102, iNao,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);
        android.content.Intent iProf = new android.content.Intent(context, NotificationReceiver.class);
        iProf.setAction("PROFESSOR_FALTOU");
        iProf.putExtra("notifId", 9101);
        iProf.putExtra("horario", horario);
        iProf.putExtra("dataFreq", dataFreq);
        android.app.PendingIntent pProf = android.app.PendingIntent.getBroadcast(
                context, 9103, iProf,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);
        androidx.core.app.NotificationCompat.Builder b = new androidx.core.app.NotificationCompat.Builder(context, canal)
                .setSmallIcon(com.santosdumont.R.drawable.ic_plane)
                .setContentTitle(context.getString(com.santosdumont.R.string.notif_presenca_titulo))
                .setContentText(corpo)
                .setStyle(new androidx.core.app.NotificationCompat.BigTextStyle().bigText(corpo))
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .addAction(0, context.getString(com.santosdumont.R.string.notif_presenca_sim), pSim)
                .addAction(0, context.getString(com.santosdumont.R.string.notif_presenca_nao), pNao)
                .addAction(0, context.getString(com.santosdumont.R.string.notif_presenca_professor), pProf);
        android.app.NotificationManager nm = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(9101, b.build());
    }

    public static void enviarNotificacaoComAcoes(Context context) {
        String canal = CHANNEL_ID;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            android.app.NotificationManager nm = context.getSystemService(android.app.NotificationManager.class);
            if (nm != null && nm.getNotificationChannel(canal) == null) {
                createNotificationChannel(context);
            }
        }
        android.content.Intent iSim = new android.content.Intent(context, NotificationReceiver.class);
        iSim.setAction("PARTICIPEI");
        iSim.putExtra("notifId", 9001);
        android.app.PendingIntent pSim = android.app.PendingIntent.getBroadcast(
                context, 9001, iSim,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);
        android.content.Intent iNao = new android.content.Intent(context, NotificationReceiver.class);
        iNao.setAction("PROFESSOR_FALTOU");
        iNao.putExtra("notifId", 9001);
        android.app.PendingIntent pNao = android.app.PendingIntent.getBroadcast(
                context, 9002, iNao,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);
        androidx.core.app.NotificationCompat.Builder b = new androidx.core.app.NotificationCompat.Builder(context, canal)
                .setSmallIcon(com.santosdumont.R.drawable.ic_plane)
                .setContentTitle(context.getString(com.santosdumont.R.string.notif_presenca_titulo))
                .setContentText(context.getString(com.santosdumont.R.string.notif_presenca_corpo))
                .setStyle(new androidx.core.app.NotificationCompat.BigTextStyle().bigText(context.getString(com.santosdumont.R.string.notif_presenca_corpo)))
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .addAction(0, context.getString(com.santosdumont.R.string.notif_presenca_sim), pSim)
                .addAction(0, context.getString(com.santosdumont.R.string.notif_presenca_nao), pNao);
        android.app.NotificationManager nm = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(9001, b.build());
    }

    public static void enviarNotificacaoEscola(Context context) {
        String canal = CHANNEL_ID;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            android.app.NotificationManager nm = context.getSystemService(android.app.NotificationManager.class);
            if (nm != null && nm.getNotificationChannel(canal) == null) {
                createNotificationChannel(context);
            }
        }
        android.content.Intent iSim = new android.content.Intent(context, NotificationReceiver.class);
        iSim.setAction("FOI_ESCOLA");
        iSim.putExtra("notifId", 9201);
        android.app.PendingIntent pSim = android.app.PendingIntent.getBroadcast(
                context, 9201, iSim,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);
        android.content.Intent iNao = new android.content.Intent(context, NotificationReceiver.class);
        iNao.setAction("NAO_FOI_ESCOLA");
        iNao.putExtra("notifId", 9201);
        android.app.PendingIntent pNao = android.app.PendingIntent.getBroadcast(
                context, 9202, iNao,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);
        androidx.core.app.NotificationCompat.Builder b = new androidx.core.app.NotificationCompat.Builder(context, canal)
                .setSmallIcon(com.santosdumont.R.drawable.ic_plane)
                .setContentTitle(context.getString(com.santosdumont.R.string.notif_escola_titulo))
                .setContentText("Toque para confirmar")
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .addAction(0, context.getString(com.santosdumont.R.string.notif_escola_sim), pSim)
                .addAction(0, context.getString(com.santosdumont.R.string.notif_escola_nao), pNao);
        android.app.NotificationManager nm = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(9201, b.build());
    }

    public static void agendarNotificacao(Context context, int requestCode, String titulo, String mensagem, int hora, int minuto) {
        NotificationHelper.createNotificationChannel(context);

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("titulo", titulo);
        intent.putExtra("mensagem", mensagem);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hora);
        cal.set(Calendar.MINUTE, minuto);
        cal.set(Calendar.SECOND, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
            }
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        }
    }
}