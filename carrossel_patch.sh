#!/usr/bin/env bash
# ============================================================
# Carrossel (Horarios + Professores) + remocao do degrade (Horario)
# ============================================================
set -eu

PROJ="/home/convidado/Área de trabalho/AppSantosDumontFinal"
BK="$PROJ/app/_bak_devmenu/carrossel_$(date +%Y%m%d_%H%M%S)"

UTILS="$PROJ/app/src/main/java/com/santosdumont/utils"
FRAGS="$PROJ/app/src/main/java/com/santosdumont/fragments"
LAYOUTS="$PROJ/app/src/main/res/layout"

cd "$PROJ"
pwd

echo "============================================================"
echo " Carrossel Patch"
echo " Backup: $BK"
echo "============================================================"

# [1] Backup (R56)
mkdir -p "$BK"
cp "$FRAGS/HorariosFragment.java"    "$BK/"
cp "$FRAGS/ProfessoresFragment.java" "$BK/"
cp "$LAYOUTS/fragment_horarios.xml"  "$BK/"
cp "$UTILS/ThemePrefs.java"          "$BK/"
echo "[OK] Backup: $(ls -1 "$BK" | wc -l) arquivos"

# [2] CRLF -> LF (R60)
sed -i 's/\r$//' "$FRAGS/HorariosFragment.java" "$FRAGS/ProfessoresFragment.java" "$LAYOUTS/fragment_horarios.xml" "$UTILS/ThemePrefs.java"
echo "[OK] CRLF normalizado"

# [3] Criar CarouselPageTransformer.java
cat > "$UTILS/CarouselPageTransformer.java" << 'JAVA_EOF'
package com.santosdumont.utils;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

/**
 * Carrossel: pagina central maior/opaca, laterais menores/translucidas.
 * Rollback via ThemePrefs.useCarousel(ctx).
 */
public class CarouselPageTransformer implements ViewPager2.PageTransformer {

    private static final float MIN_SCALE = 0.82f;
    private static final float MIN_ALPHA = 0.55f;
    private static final float MIN_TRANSLATION = -40f;

    @Override
    public void transformPage(@NonNull View page, float position) {
        if (position < -1f) {
            page.setAlpha(0f);
            page.setScaleX(MIN_SCALE);
            page.setScaleY(MIN_SCALE);
            page.setTranslationX(0f);
        } else if (position <= 1f) {
            float scale = MIN_SCALE + (1f - MIN_SCALE) * (1f - Math.abs(position));
            float alpha = MIN_ALPHA + (1f - MIN_ALPHA) * (1f - Math.abs(position));
            page.setScaleX(scale);
            page.setScaleY(scale);
            page.setAlpha(alpha);
            page.setTranslationX(MIN_TRANSLATION * position);
        } else {
            page.setAlpha(0f);
            page.setScaleX(MIN_SCALE);
            page.setScaleY(MIN_SCALE);
            page.setTranslationX(0f);
        }
    }
}
JAVA_EOF
echo "[OK] CarouselPageTransformer.java criado"

# [4] ThemePrefs — flag use_carousel
TP="$UTILS/ThemePrefs.java"
if ! grep -q "KEY_USE_CAROUSEL" "$TP"; then
  sed -i 's|^    private static final String KEY_USE_COLOR_SQUARE = "theme_use_color_square";|    private static final String KEY_USE_COLOR_SQUARE = "theme_use_color_square";\n    private static final String KEY_USE_CAROUSEL = "theme_use_carousel";|' "$TP"
  sed -i 's|^    private static final boolean USE_COLOR_SQUARE_DEFAULT = true;|    private static final boolean USE_COLOR_SQUARE_DEFAULT = true;\n    private static final boolean USE_CAROUSEL_DEFAULT = true;|' "$TP"
  python3 - "$TP" << 'PY_EOF'
import sys
path = sys.argv[1]
s = open(path, "r").read()
marker = """    public static void setUseColorSquare(Context ctx, boolean usar) {
        prefs(ctx).edit().putBoolean(KEY_USE_COLOR_SQUARE, usar).apply();
    }"""
if marker not in s:
    print("[ERRO] setUseColorSquare nao encontrado")
    sys.exit(1)
adicao = """

    public static boolean useCarousel(Context ctx) {
        return prefs(ctx).getBoolean(KEY_USE_CAROUSEL, USE_CAROUSEL_DEFAULT);
    }

    public static void setUseCarousel(Context ctx, boolean usar) {
        prefs(ctx).edit().putBoolean(KEY_USE_CAROUSEL, usar).apply();
    }"""
s = s.replace(marker, marker + adicao, 1)
open(path, "w").write(s)
print("[OK] useCarousel adicionado")
PY_EOF
fi

# [5] HorariosFragment — substituir N17 pelo carrossel condicional + remover degrade
HF="$FRAGS/HorariosFragment.java"

# 5.1 Remover declaracao do gradiente (linha 31)
sed -i '/private View gradienteEsquerdo, gradienteDireito;/d' "$HF"

# 5.2 Remover findViewById do gradiente
sed -i '/gradienteEsquerdo = view.findViewById(R.id.gradiente_esquerdo);/d' "$HF"
sed -i '/gradienteDireito = view.findViewById(R.id.gradiente_direito);/d' "$HF"

# 5.3 Remover aplicacao do degrade (bloco de ~14 linhas)
python3 - "$HF" << 'PY_EOF'
import sys, re
path = sys.argv[1]
s = open(path, "r").read()
# Remove o bloco do degrade (do "int corFundo = t.getBackground();" com "corFundo50" ate o fechamento)
# Estrategia: remove da linha "// N9v5: degradê lateral" ate o ultimo "}" antes de fechar o metodo.
idx = s.find("// N9v5: degradê lateral")
if idx >= 0:
    # Acha o fim: o bloco termina com "        }\n    }\n}" do metodo aplicarTemaNaTela
    # Vamos remover ate o proximo "\n    }\n}" (fim do metodo)
    end = s.find("\n    }\n}", idx)
    if end >= 0:
        s = s[:idx] + s[end+len("\n    }\n}"):]
        print("[OK] Bloco degrade removido")
    else:
        print("[AVISO] Fim do bloco nao encontrado — pulando")
else:
    print("[AVISO] Bloco degrade nao encontrado — pulando")
open(path, "w").write(s)
PY_EOF

# 5.4 Substituir o PageTransformer N17 pelo carrossel condicional
python3 - "$HF" << 'PY_EOF'
import sys
path = sys.argv[1]
s = open(path, "r").read()
old = """        // N17: Animacao nativa Slide + Fade (sem scale, sem lag)
        vpHorarios.setPageTransformer((page, position) -> {
            if (position < -1) { // [-Infinity,-1)
                page.setAlpha(0f);
            } else if (position <= 1) { // [-1,1]
                // Modifica o alpha padrao (fade)
                page.setAlpha(1f - Math.abs(position));
                // Translada o conteudo na horizontal para o efeito de slide
                page.setTranslationX(page.getWidth() * -position);
            } else { // (1,+Infinity]
                page.setAlpha(0f);
            }
        });"""
new = """        // Carrossel (rollback via ThemePrefs.useCarousel)
        if (com.santosdumont.utils.ThemePrefs.useCarousel(requireContext())) {
            vpHorarios.setPageTransformer(new com.santosdumont.utils.CarouselPageTransformer());
            vpHorarios.setOffscreenPageLimit(3);
        } else {
            // N17: Slide + Fade (fallback)
            vpHorarios.setPageTransformer((page, position) -> {
                if (position < -1) {
                    page.setAlpha(0f);
                } else if (position <= 1) {
                    page.setAlpha(1f - Math.abs(position));
                    page.setTranslationX(page.getWidth() * -position);
                } else {
                    page.setAlpha(0f);
                }
            });
        }"""
if old not in s:
    print("[ERRO] Bloco N17 nao encontrado para substituir")
    sys.exit(1)
s = s.replace(old, new, 1)
open(path, "w").write(s)
print("[OK] N17 substituido por carrossel condicional")
PY_EOF

# [6] ProfessoresFragment — substituir PageTransformer
PF="$FRAGS/ProfessoresFragment.java"
python3 - "$PF" << 'PY_EOF'
import sys
path = sys.argv[1]
s = open(path, "r").read()
old = """        // N17: Animacao nativa Slide + Fade (sem scale, sem lag)
        vpProf.setPageTransformer((page, position) -> {
            if (position < -1) { // [-Infinity,-1)
                page.setAlpha(0f);
            } else if (position <= 1) { // [-1,1]
                // Modifica o alpha padrao (fade)
                page.setAlpha(1f - Math.abs(position));
                // Translada o conteudo na horizontal para o efeito de slide
                page.setTranslationX(page.getWidth() * -position);
            } else { // (1,+Infinity]
                page.setAlpha(0f);
            }
        });"""
new = """        // Carrossel (rollback via ThemePrefs.useCarousel)
        if (ThemePrefs.useCarousel(requireContext())) {
            vpProf.setPageTransformer(new com.santosdumont.utils.CarouselPageTransformer());
            vpProf.setOffscreenPageLimit(3);
        } else {
            // N17: Slide + Fade (fallback)
            vpProf.setPageTransformer((page, position) -> {
                if (position < -1) {
                    page.setAlpha(0f);
                } else if (position <= 1) {
                    page.setAlpha(1f - Math.abs(position));
                    page.setTranslationX(page.getWidth() * -position);
                } else {
                    page.setAlpha(0f);
                }
            });
        }"""
if old not in s:
    print("[ERRO] Bloco N17 em Professores nao encontrado")
    sys.exit(1)
s = s.replace(old, new, 1)
open(path, "w").write(s)
print("[OK] Professores: N17 substituido por carrossel condicional")
PY_EOF

# [7] Remover as 2 <View> do degrade no fragment_horarios.xml
python3 - "$LAYOUTS/fragment_horarios.xml" << 'PY_EOF'
import sys, re
path = sys.argv[1]
s = open(path, "r").read()
# Remove o bloco do degradê esquerdo
s = re.sub(r'\s*<!-- N9v4: degrade lateral esquerdo.*?/>', '', s, flags=re.DOTALL)
# Remove o bloco do degradê direito
s = re.sub(r'\s*<!-- N9v4: degrade lateral direito.*?/>', '', s, flags=re.DOTALL)
open(path, "w").write(s)
print("[OK] XML: <View> do degrade removidos")
PY_EOF

# [8] Verificacao (R59 parcial)
echo
echo "============================================================"
echo " VERIFICACAO"
echo "============================================================"
echo "-- CarouselPageTransformer.java --"
ls -la "$UTILS/CarouselPageTransformer.java" 2>/dev/null && echo "OK" || echo "!! FALTA"
echo
echo "-- ThemePrefs --"
grep -n "KEY_USE_CAROUSEL\|USE_CAROUSEL_DEFAULT\|useCarousel\|setUseCarousel" "$TP"
echo
echo "-- HorariosFragment --"
grep -n "CarouselPageTransformer\|useCarousel\|gradienteEsquerdo\|gradienteDireito" "$HF" || echo "!! nenhuma ref (bom: degrade removido)"
echo
echo "-- ProfessoresFragment --"
grep -n "CarouselPageTransformer\|useCarousel" "$PF"
echo
echo "-- fragment_horarios.xml --"
grep -n "gradiente" "$LAYOUTS/fragment_horarios.xml" || echo "OK — sem gradiente"
echo
echo "============================================================"
echo " CONCLUIDO. Recompile no Android Studio."
echo " Rollback: ThemePrefs.setUseCarousel(ctx, false)"
echo "============================================================"
