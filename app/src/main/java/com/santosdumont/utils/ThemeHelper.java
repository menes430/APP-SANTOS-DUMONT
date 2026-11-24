package com.santosdumont.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Helper para aplicar tema (Dark Mode fixo - estilo Samsung Health)
 * + Tema dinamico (cores configuraveis pelo usuario) - v19.0.0
 */
public class ThemeHelper {

    private static final String PREFS = "tema_prefs";
    private static final String KEY = "tema";

    public static final int TEMA_CLARO = 0;
    public static final int TEMA_ESCURO = 1;

    /** Aplica o tema em qualquer Activity (dark mode fixo) */
    public static void aplicarTema(Context ctx) {
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

    // ============================================================
    // v10.0.0 - Feedback de toque (scale bounce)
    // ============================================================
    public static void scaleClick(final View v) {
        if (v == null) return;
        v.setOnTouchListener((view, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    view.animate().scaleX(0.96f).scaleY(0.96f)
                        .setDuration(120)
                        .setInterpolator(new android.view.animation.DecelerateInterpolator())
                        .start();
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    view.animate().scaleX(1f).scaleY(1f)
                        .setDuration(180)
                        .setInterpolator(new android.view.animation.OvershootInterpolator(2f))
                        .start();
                    break;
            }
            return false;
        });
    }

    // ============================================================
    // v19.0.0 - Tema dinamico
    // ============================================================

    /** Retorna o ColorTheme configurado pelo usuario */
    public static ColorTheme getTheme(Context ctx) {
        return ThemePrefs.getColorTheme(ctx);
    }

    /**
     * Aplica o tema dinamico em uma arvore de Views.
     * Etapa 1: ESQUELETO. Etapa 4 preenche a logica real.
     */
    public static void aplicarTema(View raiz, Context ctx) {
        // TODO: Etapa 4
    }

    /** Utilitario: aplica cor de fundo */
    public static void aplicarFundo(View v, int cor) {
        if (v != null) v.setBackgroundColor(cor);
    }
}