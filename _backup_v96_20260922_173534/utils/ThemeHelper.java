package com.santosdumont.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Helper para aplicar tema (Dark Mode fixo - estilo Samsung Health)
 */
public class ThemeHelper {

    private static final String PREFS = "tema_prefs";
    private static final String KEY = "tema";

    public static final int TEMA_CLARO = 0;
    public static final int TEMA_ESCURO = 1;

    /** Aplica o tema em qualquer Activity */
    public static void aplicarTema(Context ctx) {
        // App é Dark Mode fixo (Samsung Health style)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    public static int lerTema(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return sp.getInt(KEY, TEMA_ESCURO);
    }

    public static void salvarTema(Context ctx, int tema) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        sp.edit().putInt(KEY, tema).apply();
    }
}