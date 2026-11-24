#!/usr/bin/env bash
set -eu
PROJ="/home/convidado/Área de trabalho/AppSantosDumontFinal"
BK="$PROJ/app/_bak_devmenu/patch_$(date +%Y%m%d_%H%M%S)"
UTILS="$PROJ/app/src/main/java/com/santosdumont/utils"
FRAGS="$PROJ/app/src/main/java/com/santosdumont/fragments"
ACTS="$PROJ/app/src/main/java/com/santosdumont/activities"
LAYOUTS="$PROJ/app/src/main/res/layout"
VALUES="$PROJ/app/src/main/res/values"
cd "$PROJ"
echo "============================================================"
echo " Patch: Carrossel + Remover degradê + Merenda"
echo " Backup: $BK"
echo "============================================================"
mkdir -p "$BK"
cp "$FRAGS/HorariosFragment.java"    "$BK/"
cp "$FRAGS/ProfessoresFragment.java" "$BK/"
cp "$FRAGS/HomeFragment.java"        "$BK/"
cp "$UTILS/ThemePrefs.java"          "$BK/"
cp "$LAYOUTS/fragment_horarios.xml"  "$BK/"
cp "$LAYOUTS/fragment_cardapio.xml"  "$BK/"
cp "$LAYOUTS/activity_loading.xml"   "$BK/"
cp "$LAYOUTS/activity_cardapio.xml"  "$BK/"
cp "$VALUES/strings.xml"             "$BK/"
cp "$ACTS/LoadingActivity.java"      "$BK/"
echo "[OK] Backup: $(ls -1 "$BK" | wc -l) arquivos"
for f in "$FRAGS/HorariosFragment.java" "$FRAGS/ProfessoresFragment.java" "$FRAGS/HomeFragment.java" "$UTILS/ThemePrefs.java" "$LAYOUTS/fragment_horarios.xml" "$LAYOUTS/fragment_cardapio.xml" "$LAYOUTS/activity_loading.xml" "$LAYOUTS/activity_cardapio.xml" "$VALUES/strings.xml" "$ACTS/LoadingActivity.java"; do sed -i 's/\r$//' "$f"; done
echo "[OK] CRLF normalizado"
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
else
  echo "[SKIP] useCarousel ja existia"
fi
HF="$FRAGS/HorariosFragment.java"
sed -i '/private View gradienteEsquerdo, gradienteDireito;/d' "$HF"
sed -i '/gradienteEsquerdo = view.findViewById(R.id.gradiente_esquerdo);/d' "$HF"
sed -i '/gradienteDireito = view.findViewById(R.id.gradiente_direito);/d' "$HF"
python3 - "$HF" << 'PY_EOF'
import sys
path = sys.argv[1]
s = open(path, "r").read()
idx = s.find("// N9v5: degradê lateral")
if idx >= 0:
    end = s.find("\n    }\n}", idx)
    if end >= 0:
        s = s[:idx] + s[end+len("\n    }\n}"):]
        print("[OK] Bloco degrade removido do Java")
    else:
        print("[AVISO] Fim do bloco degrade nao encontrado")
else:
    print("[AVISO] Bloco degrade nao encontrado")
open(path, "w").write(s)
PY_EOF
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
    print("[ERRO] Bloco N17 Horarios nao encontrado")
    sys.exit(1)
s = s.replace(old, new, 1)
open(path, "w").write(s)
print("[OK] Horarios: N17 -> carrossel condicional")
PY_EOF
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
    print("[ERRO] Bloco N17 Professores nao encontrado")
    sys.exit(1)
s = s.replace(old, new, 1)
open(path, "w").write(s)
print("[OK] Professores: N17 -> carrossel condicional")
PY_EOF
python3 - "$LAYOUTS/fragment_horarios.xml" << 'PY_EOF'
import sys, re
path = sys.argv[1]
s = open(path, "r").read()
s = re.sub(r'\s*<!-- N9v4: degrade lateral esquerdo.*?/>', '', s, flags=re.DOTALL)
s = re.sub(r'\s*<!-- N9v4: degrade lateral direito.*?/>', '', s, flags=re.DOTALL)
open(path, "w").write(s)
print("[OK] XML: <View> do degrade removidos")
PY_EOF
sed -i 's|android:text="Cardápio"|android:text="Merenda"|' "$LAYOUTS/fragment_cardapio.xml"
sed -i 's|<string name="str_card_pio">Cardápio</string>|<string name="str_card_pio">Merenda</string>|' "$VALUES/strings.xml"
sed -i 's|"Card\\u00e1pio"|"Merenda"|' "$FRAGS/HomeFragment.java"
sed -i 's|"✅ Cardápio carregado!"|"✅ Merenda carregada!"|' "$ACTS/LoadingActivity.java"
sed -i 's|android:text="✅ Cardápio carregado!"|android:text="✅ Merenda carregada!"|' "$LAYOUTS/activity_loading.xml"
sed -i 's|android:text="Cardapio EJA"|android:text="Merenda"|' "$LAYOUTS/activity_cardapio.xml"
echo "[OK] Merenda aplicada nos 6 pontos"
echo
echo "============================================================"
echo " VERIFICACAO"
echo "============================================================"
echo "-- [1] CarouselPageTransformer --"
ls -la "$UTILS/CarouselPageTransformer.java" 2>/dev/null && echo "OK" || echo "!! FALTA"
echo "-- [2] ThemePrefs useCarousel --"
grep -n "useCarousel" "$TP" || echo "!! NAO TEM"
echo "-- [3] HorariosFragment --"
grep -n "CarouselPageTransformer\|useCarousel\|gradienteEsquerdo\|gradienteDireito" "$HF" || echo "!! NADA"
echo "-- [4] ProfessoresFragment --"
grep -n "CarouselPageTransformer\|useCarousel" "$PF" || echo "!! NADA"
echo "-- [5] fragment_horarios.xml gradiente --"
grep -n "gradiente" "$LAYOUTS/fragment_horarios.xml" || echo "OK — sem gradiente"
echo "-- [6] Merenda --"
grep -rn "Merenda" "$LAYOUTS/fragment_cardapio.xml" "$VALUES/strings.xml" "$FRAGS/HomeFragment.java" 2>/dev/null | head -5 || echo "!! NAO APLICADO"
echo "-- [7] Cardápio em strings de tela --"
grep -rn "Cardápio" "$LAYOUTS" "$VALUES" 2>/dev/null | grep -v "\.bak" || echo "OK — nenhum"
echo
echo "============================================================"
echo " CONCLUIDO. Agora compile no Android Studio e reinstale."
echo " Rollback: ThemePrefs.setUseCarousel(ctx, false)"
echo "============================================================"
