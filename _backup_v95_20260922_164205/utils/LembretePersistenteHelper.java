package com.santosdumont.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import java.util.Calendar;

public class LembretePersistenteHelper {

    public static final String CHANNEL_ID = "lembretes_persistentes";

    // ============================================
    // AGENDAR LEMBRETE (DIA SIM, DIA NÃO)
    // ============================================
    public static void agendarLembrete(Context context, String titulo, String data, String materia, int requestCode) {
        try {
            String[] dataPartes = data.split("/");
            int dia = Integer.parseInt(dataPartes[0]);
            int mes = Integer.parseInt(dataPartes[1]) - 1;
            int ano = Integer.parseInt(dataPartes[2]);
            if (ano < 100) ano += 2000;

            Calendar hoje = Calendar.getInstance();
            Calendar entrega = Calendar.getInstance();
            entrega.set(ano, mes, dia, 8, 0, 0);

            long diffMillis = entrega.getTimeInMillis() - hoje.getTimeInMillis();
            int diasFaltantes = (int) (diffMillis / (1000 * 60 * 60 * 24));

            // ============================================
            // DECIDIR SE NOTIFICA HOJE
            // REGRA: DIA SIM, DIA NÃO (INDEPENDENTE DE PAR OU ÍMPAR)
            // ============================================
            int diaHoje = hoje.get(Calendar.DAY_OF_MONTH);
            boolean notificarHoje;

            if (dia % 2 == 0) {
                // Dia de entrega é PAR -> notifica nos dias pares
                notificarHoje = (diaHoje % 2 == 0);
            } else {
                // Dia de entrega é ÍMPAR -> notifica nos dias ímpares
                notificarHoje = (diaHoje % 2 != 0);
            }

            // Se não for para notificar hoje, agenda para amanhã
            Calendar agendamento = Calendar.getInstance();
            if (!notificarHoje) {
                agendamento.add(Calendar.DAY_OF_MONTH, 1);
            }
            agendamento.set(Calendar.HOUR_OF_DAY, 8);
            agendamento.set(Calendar.MINUTE, 0);
            agendamento.set(Calendar.SECOND, 0);

            // Se já passou das 8h de hoje, agenda para amanhã
            if (agendamento.getTimeInMillis() < System.currentTimeMillis()) {
                agendamento.add(Calendar.DAY_OF_MONTH, 1);
            }

            // Intent para a notificação
            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.putExtra("titulo", "📚 Trabalho de " + materia);
            intent.putExtra("mensagem", "Faltam " + diasFaltantes + " dias para a entrega (" + data + ")");
            intent.putExtra("persistente", true);
            intent.putExtra("materia", materia);
            intent.putExtra("data", data);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, requestCode, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, agendamento.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, agendamento.getTimeInMillis(), pendingIntent);
                }
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, agendamento.getTimeInMillis(), pendingIntent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============================================
    // CANCELAR TODOS OS LEMBRETES
    // ============================================
    public static void cancelarTodos(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        for (int i = 5000; i < 5100; i++) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, i, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            alarmManager.cancel(pendingIntent);
        }
    }
}