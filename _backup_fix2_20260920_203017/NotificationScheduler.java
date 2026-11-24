package com.santosdumont.utils;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.santosdumont.R;
import com.santosdumont.activities.MainActivity;
import com.santosdumont.models.Aula;
import java.util.Calendar;
import java.util.List;

public class NotificationScheduler {

    public static final String CHANNEL_ID = "aulas_channel";

    // ============================================
    // CRIAR CANAL DE NOTIFICAÇÃO
    // ============================================
    public static void criarCanal(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Lembretes de Aulas",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Avisos de aulas, intervalo e saída");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    // ============================================
    // NOTIFICAÇÃO DE BOAS-VINDAS (IMEDIATA)
    // ============================================
    public static void enviarBoasVindas(Context context) {
        criarCanal(context);

        Intent abrirApp = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, abrirApp,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_plane)
                .setContentTitle("🎉 Bem-vindo ao Santos Dumont!")
                .setContentText("Seu app está pronto. As notificações de aula vão aparecer aqui.")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Seu app está pronto. As notificações de aula, intervalo e saída vão aparecer aqui."))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(999, builder.build());
    }

    // ============================================
    // AGENDAR NOTIFICAÇÕES DO DIA
    // ============================================
    public static void agendarNotificacoesDoDia(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String turma = prefs.getString("turma_usuario", "1A MAT");

        int diaSemana = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        String nomeDia;
        switch (diaSemana) {
            case Calendar.MONDAY: nomeDia = "Segunda"; break;
            case Calendar.TUESDAY: nomeDia = "Terça"; break;
            case Calendar.WEDNESDAY: nomeDia = "Quarta"; break;
            case Calendar.THURSDAY: nomeDia = "Quinta"; break;
            case Calendar.FRIDAY: nomeDia = "Sexta"; break;
            default: return;
        }

        cancelarTodosAlarmes(context);

        List<Aula> aulas = DataHelper.getHorarios(turma, nomeDia);
        if (aulas == null || aulas.isEmpty()) return;

        int requestCode = 1000;
        for (Aula aula : aulas) {
            String horario = aula.getHorario();
            if (horario == null || horario.isEmpty()) continue;
            if (aula.getDisciplina().equals("Sem Aula")) continue;

            int hora = 0;
            int minuto = 0;
            try {
                String h = horario.replace("h", ":").replace("m", "");
                String[] partes = h.split(":");
                hora = Integer.parseInt(partes[0]);
                if (partes.length > 1) minuto = Integer.parseInt(partes[1]);
            } catch (Exception e) {
                continue;
            }

            String titulo;
            String mensagem;
            if (aula.getDisciplina().equals("Intervalo")) {
                titulo = "☕ Intervalo";
                mensagem = "Hora do intervalo! (9h40)";
            } else if (aula.getDisciplina().equals("Saída")) {
                titulo = "🏫 Saída";
                mensagem = "Fim das aulas! Até amanhã.";
            } else {
                titulo = "📚 " + aula.getDisciplina();
                mensagem = aula.getHorario() + " - Prof. " + aula.getProfessor();
            }

            agendarAlarme(context, requestCode, titulo, mensagem, hora, minuto);
            requestCode++;
        }
    }

    private static void agendarAlarme(Context context, int requestCode, String titulo, String mensagem, int hora, int minuto) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("titulo", titulo);
        intent.putExtra("mensagem", mensagem);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hora);
        cal.set(Calendar.MINUTE, minuto);
        cal.set(Calendar.SECOND, 0);

        if (cal.getTimeInMillis() < System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

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

    private static void cancelarTodosAlarmes(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        for (int i = 1000; i < 1100; i++) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, i, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            alarmManager.cancel(pendingIntent);
        }
    }
}