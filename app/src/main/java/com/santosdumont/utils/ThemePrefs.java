package com.santosdumont.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Salva/carrega as preferencias do tema dinamico.
 * 4 valores: hue, sat, light, isLight + flag isPadrao.
 */
public class ThemePrefs {

    private static final String PREFS = "app_prefs";
    private static final String KEY_HUE     = "theme_hue";
    private static final String KEY_SAT     = "theme_sat";
    private static final String KEY_LIGHT   = "theme_light";
    private static final String KEY_DARK    = "theme_is_dark";
    private static final String KEY_PADRAO  = "theme_padrao";
    private static final String KEY_FUNDO_LITERAL = "theme_fundo_literal";
    private static final String KEY_COR_MORTA = "theme_cor_morta";
    private static final String KEY_FONT = "theme_font";
    private static final String KEY_USE_COLOR_SQUARE = "theme_use_color_square";
    private static final String KEY_USE_CAROUSEL = "theme_use_carousel";
    private static final String KEY_CARDAPIO_AVISO_VISTO = "cardapio_aviso_visto";
    private static final String KEY_FREQUENCIA_AVISO_VISTO = "frequencia_aviso_visto";
    private static final String FONT_DEFAULT = "roboto";
    private static final boolean USE_COLOR_SQUARE_DEFAULT = true;
    private static final boolean USE_CAROUSEL_DEFAULT = true;
    private static final boolean FUNDO_LITERAL_DEFAULT = true;

    private static final int HUE_DEFAULT   = 160;   // verde Samsung Health
    private static final int SAT_DEFAULT   = 70;
    private static final int LIGHT_DEFAULT = 50;
    private static final boolean DARK_DEFAULT = true;
    private static final boolean PADRAO_DEFAULT = true;

    public static int getHue(Context ctx) {
        return prefs(ctx).getInt(KEY_HUE, HUE_DEFAULT);
    }

    public static int getSat(Context ctx) {
        return prefs(ctx).getInt(KEY_SAT, SAT_DEFAULT);
    }

    public static int getLight(Context ctx) {
        return prefs(ctx).getInt(KEY_LIGHT, LIGHT_DEFAULT);
    }

    public static boolean isLightTheme(Context ctx) {
        // FIX: modo automatico. light > 50 = claro; light <= 50 = escuro.
        return getLight(ctx) > 50;
    }

    public static boolean isPadrao(Context ctx) {
        return prefs(ctx).getBoolean(KEY_PADRAO, PADRAO_DEFAULT);
    }

    public static boolean isFundoLiteral(Context ctx) {
        // Cor morta ativa = fundo NAO literal (cor morta)
        return !isCorMorta(ctx);
    }

    public static void setFundoLiteral(Context ctx, boolean literal) {
        prefs(ctx).edit().putBoolean(KEY_FUNDO_LITERAL, literal).apply();
    }

    public static boolean isCorMorta(Context ctx) {
        return prefs(ctx).getBoolean(KEY_COR_MORTA, false);
    }

    public static void setCorMorta(Context ctx, boolean corMorta) {
        prefs(ctx).edit().putBoolean(KEY_COR_MORTA, corMorta).apply();
    }

    public static String getFont(Context ctx) {
        return prefs(ctx).getString(KEY_FONT, FONT_DEFAULT);
    }

    public static void setFont(Context ctx, String font) {
        prefs(ctx).edit().putString(KEY_FONT, font).apply();
    }

    public static boolean useColorSquare(Context ctx) {
        return prefs(ctx).getBoolean(KEY_USE_COLOR_SQUARE, USE_COLOR_SQUARE_DEFAULT);
    }

    public static void setUseColorSquare(Context ctx, boolean usar) {
        prefs(ctx).edit().putBoolean(KEY_USE_COLOR_SQUARE, usar).apply();
    }

    public static boolean useCarousel(Context ctx) {
        return prefs(ctx).getBoolean(KEY_USE_CAROUSEL, USE_CAROUSEL_DEFAULT);
    }

    public static void setUseCarousel(Context ctx, boolean usar) {
        prefs(ctx).edit().putBoolean(KEY_USE_CAROUSEL, usar).apply();
    }

    public static String getStringCustom(Context ctx, String key, String def) {
        return prefs(ctx).getString(key, def);
    }

    public static void setStringCustom(Context ctx, String key, String value) {
        prefs(ctx).edit().putString(key, value).apply();
    }

    public static void setTema(Context ctx, int hue, int sat, int light, boolean isLight) {
        prefs(ctx).edit()
                .putInt(KEY_HUE, hue)
                .putInt(KEY_SAT, sat)
                .putInt(KEY_LIGHT, light)
                .putBoolean(KEY_DARK, !isLight)
                .putBoolean(KEY_PADRAO, false)
                .apply();
    }

    public static void resetarParaPadrao(Context ctx) {
        prefs(ctx).edit()
                .putInt(KEY_HUE, HUE_DEFAULT)
                .putInt(KEY_SAT, SAT_DEFAULT)
                .putInt(KEY_LIGHT, LIGHT_DEFAULT)
                .putBoolean(KEY_DARK, DARK_DEFAULT)
                .putBoolean(KEY_PADRAO, true)
                .apply();
    }

    /** Carrega a config atual do tema */
    public static ColorTheme getColorTheme(Context ctx) {
        return new ColorTheme(
                getHue(ctx),
                getSat(ctx),
                getLight(ctx),
                isLightTheme(ctx),
                isPadrao(ctx),
                isFundoLiteral(ctx)
        );
    }

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    // F5 / F2: flags de "aviso ja visto"
    public static boolean isCardapioAvisoVisto(Context ctx) {
        return prefs(ctx).getBoolean(KEY_CARDAPIO_AVISO_VISTO, false);
    }
    public static void setCardapioAvisoVisto(Context ctx, boolean visto) {
        prefs(ctx).edit().putBoolean(KEY_CARDAPIO_AVISO_VISTO, visto).apply();
    }
    public static boolean isFrequenciaAvisoVisto(Context ctx) {
        return prefs(ctx).getBoolean(KEY_FREQUENCIA_AVISO_VISTO, false);
    }
    public static void setFrequenciaAvisoVisto(Context ctx, boolean visto) {
        prefs(ctx).edit().putBoolean(KEY_FREQUENCIA_AVISO_VISTO, visto).apply();
    }

}
