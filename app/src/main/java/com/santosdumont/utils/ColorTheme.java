package com.santosdumont.utils;

/**
 * Motor de cores do tema dinamico.
 * Gera TODAS as cores do app a partir de 3 parametros:
 *   hue (matiz 0-360), sat (saturacao 0-100), light (luminosidade 0-100)
 *
 * Se isPadrao=true, usa as 73 cores originais (comportamento atual do app).
 */
public class ColorTheme {

    private final int hue;
    private final int sat;
    private final int light;
    private final boolean isLightTheme;
    private final boolean isPadrao;
    private final boolean isFundoLiteral;

    public ColorTheme(int hue, int sat, int light, boolean isLightTheme, boolean isPadrao) {
        this(hue, sat, light, isLightTheme, isPadrao, false);
    }

    public ColorTheme(int hue, int sat, int light, boolean isLightTheme, boolean isPadrao, boolean isFundoLiteral) {
        this.hue = ((hue % 360) + 360) % 360;
        this.sat = Math.max(0, Math.min(100, sat));
        this.light = Math.max(0, Math.min(100, light));
        this.isLightTheme = isLightTheme;
        this.isPadrao = isPadrao;
        this.isFundoLiteral = isFundoLiteral;
    }

    // ============================================================
    // Cores de chrome (fundo, cards, texto)
    // ============================================================

    public int getPrimary() {
        // Usa o "light" do usuario como base, com ajuste pequeno pelo isLightTheme
        int base = isLightTheme ? (light + 5) : light;
        return hslToRgb(hue, sat, Math.max(10, Math.min(90, base)));
    }

    public int getPrimaryDark() {
        int base = isLightTheme ? (light - 10) : (light - 15);
        return hslToRgb(hue, sat, Math.max(10, Math.min(85, base)));
    }

    public int getBackground() {
        // M11: fundo literal (cor selecionada sem clarear/escurecer)
        if (isFundoLiteral) {
            return hslToRgb(hue, sat, light);
        }
        // Comportamento padrao
        if (isLightTheme) {
            int l = 75 + (int)(light * 0.20);
            return hslToRgb(hue, sat * 0.15, Math.min(96, l));
        } else {
            int l = 5 + (int)(light * 0.20);
            return hslToRgb(hue, sat * 0.20, Math.min(25, l));
        }
    }

    public int getSurface() {
        if (isFundoLiteral) {
            int l = Math.max(0, Math.min(100, light + 5));
            return hslToRgb(hue, sat, l);
        }
        if (isLightTheme) {
            int l = 70 + (int)(light * 0.18);
            return hslToRgb(hue, sat * 0.15, Math.min(92, l));
        } else {
            int l = 10 + (int)(light * 0.20);
            return hslToRgb(hue, sat * 0.20, Math.min(30, l));
        }
    }

    public int getSurfaceAlt() {
        if (isFundoLiteral) {
            int l = Math.max(0, Math.min(100, light + 10));
            return hslToRgb(hue, sat, l);
        }
        if (isLightTheme) {
            int l = 65 + (int)(light * 0.18);
            return hslToRgb(hue, sat * 0.15, Math.min(88, l));
        } else {
            int l = 15 + (int)(light * 0.20);
            return hslToRgb(hue, sat * 0.20, Math.min(35, l));
        }
    }

    public int getTextPrimary() {
        if (isFundoLiteral) {
            // Contraste por luminancia do fundo
            int bg = hslToRgb(hue, sat, light);
            return isBgEscuro(bg) ? 0xFFFFFFFF : 0xFF000000;
        }
        if (isLightTheme) return hslToRgb(hue, sat * 0.10, 10);
        return hslToRgb(hue, sat * 0.10, 95);
    }

    private boolean isBgEscuro(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        double lum = (0.299 * r + 0.587 * g + 0.114 * b) / 255.0;
        return lum < 0.5;
    }

    public int getTextSecondary() {
        if (isFundoLiteral) {
            int bg = hslToRgb(hue, sat, light);
            return isBgEscuro(bg) ? 0xFFCCCCCC : 0xFF555555;
        }
        if (isLightTheme) return hslToRgb(hue, sat * 0.10, 40);
        return hslToRgb(hue, sat * 0.10, 65);
    }

    public int getTextMuted() {
        if (isLightTheme) return hslToRgb(hue, sat * 0.10, 55);
        return hslToRgb(hue, sat * 0.10, 45);
    }

    public int getDivider() {
        if (isLightTheme) return hslToRgb(hue, sat * 0.15, 80);
        return hslToRgb(hue, sat * 0.20, 25);
    }

    // N6b: contraste automatico texto-vs-fundo (modo claro inverte)
    /** Texto principal: preto em tema claro, branco em tema escuro. */
    public int getTextOnBackground() {
        return isLightTheme ? 0xFF1A1A1A : 0xFFFFFFFF;
    }

    /** Texto secundario: cinza escuro em tema claro, cinza claro em tema escuro. */
    public int getTextSecondaryOnBackground() {
        return isLightTheme ? 0xFF555555 : 0xFFCCCCCC;
    }

    // ============================================================
    // Cores de estado (sucesso, erro, neutro)
    // ============================================================

    /** Sucesso (positivo) - tom claro do hue */
    public int getSuccess() {
        int base = isLightTheme ? (light + 15) : (light + 15);
        return hslToRgb(hue, Math.min(100, sat * 0.7), Math.max(20, Math.min(90, base)));
    }

    /** Erro (negativo) - tom escuro do hue */
    public int getError() {
        int base = isLightTheme ? (light - 15) : (light - 15);
        return hslToRgb(hue, Math.min(100, sat * 0.8), Math.max(15, Math.min(70, base)));
    }

    /** Neutro - meio termo */
    public int getNeutral() {
        return hslToRgb(hue, Math.min(100, sat * 0.4), light);
    }

    // ============================================================
    // Cores por materia (so usadas quando isPadrao=false)
    // Formula: offset de hue ±30 + variacao de lightness
    // ============================================================

    public int getCorMateria(String materia, String professor) {
        if (isPadrao) return getCorMateriaPadrao(materia, professor);
        if (materia == null) return hslToRgb(hue, sat * 0.2, 20);

        String m = materia.toUpperCase().trim();
        // Normaliza PDV / PROJVIDA / PROJETO DE VIDA
        if (m.equals("PROJVIDA") || m.equals("PROJETO DE VIDA")) m = "PDV";

        String p = (professor == null) ? "" : professor.trim();

        // Offset + variacao de lightness por materia
        int offsetHue = 0;
        int variacaoLight = 0;

        if      (m.equals("MAT"))   { offsetHue =  0; variacaoLight =  0; }
        else if (m.equals("PORT"))  { offsetHue = 15; variacaoLight =  5; }
        else if (m.equals("HIS"))   { offsetHue =-15; variacaoLight = -5; }
        else if (m.equals("GEO"))   { offsetHue = 30; variacaoLight = 10; }
        else if (m.equals("BIO"))   { offsetHue =-30; variacaoLight =  8; }
        else if (m.equals("FIS") || m.equals("F\u00cdS")) { offsetHue = 20; variacaoLight = -10; }
        else if (m.equals("QUI") || m.equals("QU\u00cd")) { offsetHue =-20; variacaoLight =  3; }
        else if (m.equals("ART"))   { offsetHue = 10; variacaoLight = 12; }
        else if (m.equals("ING"))   { offsetHue =-10; variacaoLight = -8; }
        else if (m.equals("EF"))    { offsetHue = 25; variacaoLight =  6; }
        else if (m.equals("FIL"))   { offsetHue =-25; variacaoLight = -3; }
        else if (m.equals("SOC"))   { offsetHue =  5; variacaoLight = 15; }
        else if (m.equals("PDV"))   { offsetHue = -5; variacaoLight = -12; }
        else if (m.equals("PRAT"))  { offsetHue = 35; variacaoLight =  4; }
        else if (m.equals("TAC"))   { offsetHue =-35; variacaoLight =  7; }
        else if (m.equals("PIT"))   { offsetHue = 18; variacaoLight = -15; }
        else if (m.equals("APROF")) { offsetHue =-18; variacaoLight = 10; }

        // Variacao extra por professor (dentro da materia)
        int varProf = variacaoPorProfessor(p);

        int h = ((hue + offsetHue) % 360 + 360) % 360;
        int l = Math.max(15, Math.min(85, (isLightTheme ? 55 : 50) + variacaoLight + varProf));

        return hslToRgb(h, sat, l);
    }

    /** Diferencia professores dentro da mesma materia */
    private int variacaoPorProfessor(String p) {
        if (p.isEmpty()) return 0;
        int hash = p.hashCode();
        return ((hash % 21) - 10);  // -10 a +10
    }

    // ============================================================
    // 73 cores originais (mantidas iguais quando isPadrao=true)
    // ============================================================

    public int getCorMateriaPadrao(String materia, String professor) {
        if (materia == null) return 0xFF2A2A2A;
        if (professor == null) professor = "";

        String m = materia.toUpperCase().trim();
        String p = professor.trim();

        // Normaliza PDV / PROJVIDA
        if (m.equals("PROJVIDA") || m.equals("PROJETO DE VIDA")) m = "PDV";

        // BIO
        if (m.equals("BIO")) {
            if (p.equals("Dayse"))   return 0xFFFFDC64;
            if (p.equals("Thays"))   return 0xFFFFBE8C;
            if (p.equals("Eraldo"))  return 0xFFE1A58C;
        }
        // MAT
        if (m.equals("MAT")) {
            if (p.equals("Ana Paula")) return 0xFF4BA5DC;
            if (p.equals("Jailson"))   return 0xFF3769BE;
            if (p.equals("C\u00e1ssia")) return 0xFF5AB4DC;
            if (p.equals("C\u00c1SSIA")) return 0xFF5AB4DC;
            if (p.equals("Widjane"))   return 0xFFAFB9B4;
            if (p.equals("Emerson"))   return 0xFF4B9BCD;
            if (p.equals("Josenildo")) return 0xFF82AF7D;
            if (p.equals("\u00c1lvaro")) return 0xFF377DB9;
        }
        // FIS
        if (m.equals("F\u00cdS") || m.equals("FIS")) {
            if (p.equals("Camila"))  return 0xFFC87391;
            if (p.equals("Herbert")) return 0xFFAF788C;
            if (p.equals("Widjane")) return 0xFFB47D91;
        }
        // ART
        if (m.equals("ART")) {
            if (p.equals("C\u00edcera"))   return 0xFFDC9173;
            if (p.equals("Thayse"))    return 0xFFF5AF87;
            if (p.equals("Henrique"))  return 0xFFE1A050;
        }
        // QUI
        if (m.equals("QUI") || m.equals("QU\u00cd")) {
            if (p.equals("Helo\u00edza")) return 0xFFBED7A0;
            if (p.equals("Jhosi"))    return 0xFFA5C869;
            if (p.equals("Caio"))     return 0xFF419BB4;
            if (p.equals("Erivaldo")) return 0xFF9BBE7D;
        }
        // APROF
        if (m.equals("APROF")) {
            if (p.equals("Niedja"))   return 0xFF8CBE7D;
            if (p.equals("Dayse"))    return 0xFFCDD27D;
            if (p.equals("Moral"))    return 0xFF2D64AF;
            if (p.equals("C\u00e1ssia") || p.equals("C\u00c1SSIA")) return 0xFF5F9BD2;
            if (p.equals("J\u00e9ssica")) return 0xFFAACDA5;
            if (p.equals("Jailson"))  return 0xFF647DCD;
            if (p.equals("Erivaldo")) return 0xFF87B46E;
        }
        // PORT
        if (m.equals("PORT")) {
            if (p.equals("Judy"))     return 0xFFAFAFDC;
            if (p.equals("Luciano"))  return 0xFFDC9187;
            if (p.equals("Rozi"))     return 0xFFD77D73;
            if (p.equals("Danilo"))   return 0xFFCD7D73;
            if (p.equals("Silvana"))  return 0xFF5A9BC8;
        }
        // PDV
        if (m.equals("PDV")) {
            if (p.equals("Herbert"))   return 0xFF8796CD;
            if (p.equals("Judy"))      return 0xFFDCAAB9;
            if (p.equals("Thayse"))    return 0xFFB4A5D7;
            if (p.equals("Ana Paula")) return 0xFF91A5D7;
            if (p.equals("D\u00e9bora"))  return 0xFFAF82CD;
            if (p.equals("Douglas"))   return 0xFF649BD2;
            if (p.equals("Roberto"))   return 0xFF64AFDC;
            if (p.equals("Silvana"))   return 0xFF509BD2;
            if (p.equals("\u00c1lvaro"))  return 0xFF2873AF;
            if (p.equals("Cl\u00e1udia"))  return 0xFFCDAA64;
        }
        // PRAT
        if (m.equals("PRAT")) {
            if (p.equals("Helo\u00edza")) return 0xFFAFD2AF;
            if (p.equals("Robson"))   return 0xFFEB9B69;
            if (p.equals("Josenildo") || p.equals("Jos\u00e9 Nildo")) return 0xFF96BE87;
            if (p.equals("Camila"))   return 0xFFC8879B;
            if (p.equals("Ana Luiza")) return 0xFFB47DAF;
            if (p.equals("\u00c1lvaro"))  return 0xFF69A0C8;
            if (p.equals("Cl\u00e1udia"))  return 0xFFE1B978;
        }
        // TAC
        if (m.equals("TAC")) {
            if (p.equals("Dayse"))   return 0xFFF5CD55;
            if (p.equals("Danilo"))  return 0xFFCD9182;
            if (p.equals("Moral"))   return 0xFF2D87B9;
        }
        // ING
        if (m.equals("ING")) {
            if (p.equals("Cl\u00e1udia") || p.equals("Claudia")) return 0xFFEBC364;
            if (p.equals("Robson"))  return 0xFFDC9173;
        }
        // GEO
        if (m.equals("GEO")) {
            if (p.equals("Moral"))   return 0xFF649BBE;
            if (p.equals("J\u00e9ssica")) return 0xFF9BCDA0;
        }
        // EF
        if (m.equals("EF")) {
            if (p.equals("Adriano"))  return 0xFFBEC3B4;
            if (p.equals("Suyllane") || p.equals("Suylane")) return 0xFF7DC3CD;
            if (p.equals("Douglas"))  return 0xFF739BC3;
        }
        // PIT
        if (m.equals("PIT")) {
            if (p.equals("Adriano")) return 0xFFAFB9AA;
            if (p.equals("D\u00e9bora"))  return 0xFFC89BB9;
            if (p.equals("Robson"))  return 0xFFC37D87;
            if (p.equals("Douglas")) return 0xFF417DBE;
            if (p.equals("Suyllane")) return 0xFF73A5CD;
        }
        // FIL
        if (m.equals("FIL")) {
            if (p.equals("M\u00e1rcia")) return 0xFFDCCD9B;
        }
        // HIS
        if (m.equals("HIS")) {
            if (p.equals("Roberto")) return 0xFF78A569;
            if (p.equals("Moral"))   return 0xFF507DAF;
            if (p.equals("Niedja"))  return 0xFF78A569;
        }
        // SOC
        if (m.equals("SOC")) {
            if (p.equals("Ana Luiza")) return 0xFFAF7DB9;
        }

        // Fallback
        if (m.equals("SEM AULA") || m.equals("INTERVALO") || m.equals("SAIDA") || m.equals("SA\u00cdDA")) {
            return 0xFF2A2A2A;
        }

        return 0xFF3A3A3A;
    }

    // ============================================================
    // HSL -> RGB (Android ARGB int)
    // ============================================================

    public static int hslToRgb(double h, double s, double l) {
        h = ((h % 360) + 360) % 360;
        s = Math.max(0, Math.min(100, s)) / 100.0;
        l = Math.max(0, Math.min(100, l)) / 100.0;

        double c = (1 - Math.abs(2 * l - 1)) * s;
        double x = c * (1 - Math.abs((h / 60.0) % 2 - 1));
        double m = l - c / 2;

        double r = 0, g = 0, b = 0;
        if      (h <  60) { r = c; g = x; b = 0; }
        else if (h < 120) { r = x; g = c; b = 0; }
        else if (h < 180) { r = 0; g = c; b = x; }
        else if (h < 240) { r = 0; g = x; b = c; }
        else if (h < 300) { r = x; g = 0; b = c; }
        else              { r = c; g = 0; b = x; }

        int ri = (int) Math.round((r + m) * 255);
        int gi = (int) Math.round((g + m) * 255);
        int bi = (int) Math.round((b + m) * 255);

        return 0xFF000000 | (ri << 16) | (gi << 8) | bi;
    }
}
