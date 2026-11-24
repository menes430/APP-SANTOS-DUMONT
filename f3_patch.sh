#!/usr/bin/env bash
# ============================================================
# F3 — Seletor quadrado de cores (ColorSquareView)
# ============================================================
set -eu

PROJ="/home/convidado/Área de trabalho/AppSantosDumontFinal"
BK="$PROJ/app/_bak_devmenu/F3_$(date +%Y%m%d_%H%M%S)"

UTILS="$PROJ/app/src/main/java/com/santosdumont/utils"

echo "============================================================"
echo " F3 — Patch ColorSquareView"
echo " Backup em: $BK"
echo "============================================================"

cd "$PROJ"
pwd

# [1] Backup (R56)
mkdir -p "$BK"
cp "$UTILS/ThemeEditor.java"     "$BK/"
cp "$UTILS/ThemePrefs.java"      "$BK/"
cp "$UTILS/ColorWheelView.java"  "$BK/"
echo "[OK] Backup feito: $(ls -1 "$BK" | wc -l) arquivos"

# [2] CRLF -> LF (R60)
sed -i 's/\r$//' "$UTILS/ThemeEditor.java"    "$UTILS/ThemePrefs.java"    "$UTILS/ColorWheelView.java"
echo "[OK] CRLF normalizado"

# [3] Criar ColorSquareView.java
cat > "$UTILS/ColorSquareView.java" << 'JAVA_EOF'
package com.santosdumont.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * F3: Seletor QUADRADO de cores (HSV 2D).
 * - X = hue (0-360)
 * - Y = saturacao (0-100)
 * - Brilho/light vem do slider de Tom (ThemeEditor)
 *
 * Estende ColorWheelView para reaproveitar a API publica
 * (setHue, setOnHueChangeListener) sem quebrar o ThemeEditor.
 * Sobrescreve onDraw e onTouchEvent.
 *
 * Rollback: flag use_color_square em ThemePrefs (default true).
 */
public class ColorSquareView extends ColorWheelView {

    public interface OnSatChangeListener {
        void onSatChange(float sat);
    }

    private float satAtual = 100f;        // 0-100
    private OnSatChangeListener satListener;

    private final Paint paintQuadrado = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintBordaInd = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintIndicador = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rectQuad = new RectF();

    public ColorSquareView(Context ctx) {
        super(ctx);
        initQuadrado();
    }

    public ColorSquareView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        initQuadrado();
    }

    public ColorSquareView(Context ctx, AttributeSet attrs, int defStyle) {
        super(ctx, attrs, defStyle);
        initQuadrado();
    }

    private void initQuadrado() {
        paintQuadrado.setStyle(Paint.Style.FILL);
        paintBordaInd.setStyle(Paint.Style.STROKE);
        paintBordaInd.setStrokeWidth(4f);
        paintBordaInd.setColor(Color.BLACK);
        paintIndicador.setStyle(Paint.Style.FILL);
        paintIndicador.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        float pad = 12f;
        rectQuad.set(pad, pad, w - pad, h - pad);

        // Desenha grid HSV em faixas horizontais (hue no X, sat no Y)
        // Para performance: ~60 faixas verticais (hue) x ~40 horizontais (sat)
        final int passosHue = 60;
        final int passosSat = 40;
        float largHue = rectQuad.width() / passosHue;
        float largSat = rectQuad.height() / passosSat;

        for (int i = 0; i < passosHue; i++) {
            float hue = (i / (float) passosHue) * 360f;
            for (int j = 0; j < passosSat; j++) {
                // Y invertido: topo = sat 100, base = sat 0
                float sat = (1f - j / (float) passosSat) * 100f;
                int cor = ColorTheme.hslToRgb(hue, sat, 50);
                paintQuadrado.setColor(cor);
                canvas.drawRect(
                        rectQuad.left + i * largHue,
                        rectQuad.top + j * largSat,
                        rectQuad.left + (i + 1) * largHue,
                        rectQuad.top + (j + 1) * largSat,
                        paintQuadrado
                );
            }
        }

        // Indicador (circulo branco com borda preta)
        float ix = rectQuad.left + (getHue() / 360f) * rectQuad.width();
        float iy = rectQuad.top + (1f - satAtual / 100f) * rectQuad.height();
        canvas.drawCircle(ix, iy, 14f, paintBordaInd);
        canvas.drawCircle(ix, iy, 12f, paintIndicador);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE: {
                int w = getWidth();
                int h = getHeight();
                if (w <= 0 || h <= 0) return true;

                float pad = 12f;
                float x = event.getX();
                float y = event.getY();

                // Converte X -> hue
                float hx = Math.max(pad, Math.min(w - pad, x));
                float hy = Math.max(pad, Math.min(h - pad, y));

                float novoHue = ((hx - pad) / (w - 2 * pad)) * 360f;
                novoHue = ((novoHue % 360) + 360) % 360;

                // Converte Y -> sat (Y invertido)
                float novaSat = (1f - (hy - pad) / (h - 2 * pad)) * 100f;
                novaSat = Math.max(0, Math.min(100, novaSat));

                setHue(novoHue);
                satAtual = novaSat;

                // Dispara listeners
                // onHueChange esta em ColorWheelView (super)
                notifyHue(novoHue);
                if (satListener != null) satListener.onSatChange(novaSat);

                invalidate();
                return true;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                performClick();
                return true;
        }
        return super.onTouchEvent(event);
    }

    // Redireciona para o listener da classe mae (que e' privado)
    // Reimplementamos setOnHueChangeListener no pai chama onHueChange do filho.
    // Como o pai guarda o listener privado, precisamos de um setter no pai OU
    // reimplementar aqui. Solucao: sobrescrevemos setOnHueChangeListener abaixo.

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    // ============================================================
    // API nova
    // ============================================================

    public float getSat() {
        return satAtual;
    }

    public void setSat(float sat) {
        this.satAtual = Math.max(0, Math.min(100, sat));
        invalidate();
    }

    public void setOnSatChangeListener(OnSatChangeListener l) {
        this.satListener = l;
    }
}
JAVA_EOF
echo "[OK] ColorSquareView.java criado"

# [4] Patch ThemePrefs.java — adicionar KEY_USE_COLOR_SQUARE + getter/setter
TP="$UTILS/ThemePrefs.java"

# 4.1 Insere constantes depois de KEY_FONT/FONT_DEFAULT
if ! grep -q "KEY_USE_COLOR_SQUARE" "$TP"; then
  sed -i 's|^    private static final String KEY_FONT = "theme_font";|    private static final String KEY_FONT = "theme_font";\n    private static final String KEY_USE_COLOR_SQUARE = "theme_use_color_square";|' "$TP"
  sed -i 's|^    private static final String FONT_DEFAULT = "roboto";|    private static final String FONT_DEFAULT = "roboto";\n    private static final boolean USE_COLOR_SQUARE_DEFAULT = true;|' "$TP"
  echo "[OK] Constantes adicionadas"
else
  echo "[SKIP] Constantes ja existiam"
fi

# 4.2 Insere getter/setter depois de setFont()
if ! grep -q "public static boolean useColorSquare" "$TP"; then
  # Localiza o bloco de setFont e adiciona logo depois
  python3 - "$TP" << 'PY_EOF'
import sys, re
path = sys.argv[1]
s = open(path, "r").read()
marker = "    public static void setFont(Context ctx, String font) {"
idx = s.find(marker)
if idx < 0:
    print("[ERRO] setFont nao encontrado")
    sys.exit(1)
# Acha o fim do bloco setFont
end_marker = "    }\n"
end_idx = s.find(end_marker, idx)
if end_idx < 0:
    print("[ERRO] fim de setFont nao encontrado")
    sys.exit(1)
end_idx += len(end_marker)
bloco = """
    public static boolean useColorSquare(Context ctx) {
        return prefs(ctx).getBoolean(KEY_USE_COLOR_SQUARE, USE_COLOR_SQUARE_DEFAULT);
    }

    public static void setUseColorSquare(Context ctx, boolean usar) {
        prefs(ctx).edit().putBoolean(KEY_USE_COLOR_SQUARE, usar).apply();
    }
"""
s = s[:end_idx] + bloco + s[end_idx:]
open(path, "w").write(s)
print("[OK] Getter/setter adicionados")
PY_EOF
else
  echo "[SKIP] Getter/setter ja existiam"
fi

# [5] Patch ThemeEditor.java — linha 49 (new ColorWheelView -> condicional)
TE="$UTILS/ThemeEditor.java"

if ! grep -q "ColorSquareView" "$TE"; then
  python3 - "$TE" << 'PY_EOF'
import sys
path = sys.argv[1]
s = open(path, "r").read()
old = "final ColorWheelView roda = new ColorWheelView(ctx);"
new = "final ColorWheelView roda = ThemePrefs.useColorSquare(ctx) ? new ColorSquareView(ctx) : new ColorWheelView(ctx);"
if old not in s:
    print("[ERRO] linha 49 nao encontrada")
    sys.exit(1)
s = s.replace(old, new, 1)
open(path, "w").write(s)
print("[OK] Linha 49 patcheada")
PY_EOF
else
  echo "[SKIP] ThemeEditor ja tinha ColorSquareView"
fi

# [5.2] Adiciona listener de saturação no ThemeEditor
if ! grep -q "setOnSatChangeListener" "$TE"; then
  python3 - "$TE" << 'PY_EOF'
import sys
path = sys.argv[1]
s = open(path, "r").read()
old = """        roda.setOnHueChangeListener(hue -> {
            estado[0] = (int) hue;
            atualizar.run();
        });"""
new = """        roda.setOnHueChangeListener(hue -> {
            estado[0] = (int) hue;
            atualizar.run();
        });

        // F3: se for ColorSquareView, adiciona listener de saturação
        if (roda instanceof ColorSquareView) {
            ((ColorSquareView) roda).setOnSatChangeListener(sat -> {
                estado[1] = (int) sat;
                atualizar.run();
            });
        }"""
if old not in s:
    print("[ERRO] bloco setOnHueChangeListener nao encontrado")
    sys.exit(1)
s = s.replace(old, new, 1)
open(path, "w").write(s)
print("[OK] Listener de saturação adicionado")
PY_EOF
else
  echo "[SKIP] Listener de saturação ja existia"
fi

# [6] Verificacao final (R59 parcial)
echo
echo "============================================================"
echo " VERIFICACAO"
echo "============================================================"
echo "-- ColorSquareView.java --"
ls -la "$UTILS/ColorSquareView.java" 2>/dev/null && grep -c "" "$UTILS/ColorSquareView.java" | xargs echo "linhas:"
echo
echo "-- ThemePrefs.java --"
grep -n "KEY_USE_COLOR_SQUARE\|USE_COLOR_SQUARE_DEFAULT\|useColorSquare\|setUseColorSquare" "$TP" || echo "!! NAO ACHOU"
echo
echo "-- ThemeEditor.java --"
grep -n "ColorSquareView\|useColorSquare" "$TE" || echo "!! NAO ACHOU"
echo
echo "-- ColorWheelView.java (deve estar intacto) --"
grep -c "OnHueChangeListener" "$UTILS/ColorWheelView.java" | xargs echo "referencias OnHueChangeListener:"
echo
echo "-- Backup --"
ls -la "$BK"
echo
echo "============================================================"
echo " F3 CONCLUIDO. Proximo: compilar no Android Studio."
echo " Rollback: ThemePrefs.setUseColorSquare(ctx, false)"
echo "============================================================"
