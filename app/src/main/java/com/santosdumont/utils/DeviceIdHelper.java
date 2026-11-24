package com.santosdumont.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Gera um ID fixo e legível para o dispositivo (ex.: DEV-7A3F9).
 * Salvo em SharedPreferences, nunca muda.
 */
public class DeviceIdHelper {

    private static final String KEY_ID = "device_id_fixo";

    public static String getId(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String id = prefs.getString(KEY_ID, null);
        if (id != null && !id.isEmpty()) return id;

        // Gera um ID legível: DEV- + 5 chars (sem letras confusas: I, O, 0, 1)
        String alfabeto = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder("DEV-");
        java.util.Random r = new java.util.Random(System.currentTimeMillis());
        for (int i = 0; i < 5; i++) {
            sb.append(alfabeto.charAt(r.nextInt(alfabeto.length())));
        }
        id = sb.toString();
        prefs.edit().putString(KEY_ID, id).apply();
        return id;
    }
}
