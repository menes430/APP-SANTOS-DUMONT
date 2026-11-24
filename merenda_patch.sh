#!/usr/bin/env bash
# ============================================================
# Cardapio -> Merenda (renomear titulos, sem tocar em classes)
# ============================================================
set -eu

PROJ="/home/convidado/Área de trabalho/AppSantosDumontFinal"
BK="$PROJ/app/_bak_devmenu/merenda_$(date +%Y%m%d_%H%M%S)"
LAYOUTS="$PROJ/app/src/main/res/layout"
VALUES="$PROJ/app/src/main/res/values"
JAVA="$PROJ/app/src/main/java/com/santosdumont"

cd "$PROJ"
pwd

echo "============================================================"
echo " Cardapio -> Merenda"
echo " Backup: $BK"
echo "============================================================"

# [1] Backup (R56)
mkdir -p "$BK"
cp "$LAYOUTS/fragment_cardapio.xml"           "$BK/"
cp "$LAYOUTS/activity_loading.xml"            "$BK/"
cp "$LAYOUTS/activity_cardapio.xml"           "$BK/"
cp "$VALUES/strings.xml"                      "$BK/"
cp "$JAVA/fragments/HomeFragment.java"        "$BK/"
cp "$JAVA/activities/LoadingActivity.java"   "$BK/"
echo "[OK] Backup: $(ls -1 "$BK" | wc -l) arquivos"

# [2] CRLF -> LF (R60)
for f in "$LAYOUTS/fragment_cardapio.xml" "$LAYOUTS/activity_loading.xml" "$LAYOUTS/activity_cardapio.xml" "$VALUES/strings.xml" "$JAVA/fragments/HomeFragment.java" "$JAVA/activities/LoadingActivity.java"; do
  sed -i 's/\r$//' "$f"
done
echo "[OK] CRLF normalizado"

# [3] Replace cirurgico (R48/R53) — string exata, linha exata

# 3.1 fragment_cardapio.xml — android:text="Cardápio" -> "Merenda"
sed -i 's|android:text="Cardápio"|android:text="Merenda"|' "$LAYOUTS/fragment_cardapio.xml"

# 3.2 strings.xml — str_card_pio
sed -i 's|<string name="str_card_pio">Cardápio</string>|<string name="str_card_pio">Merenda</string>|' "$VALUES/strings.xml"

# 3.3 HomeFragment.java — "Card\u00e1pio" -> "Merenda"
sed -i 's|"Card\\u00e1pio"|"Merenda"|' "$JAVA/fragments/HomeFragment.java"

# 3.4 LoadingActivity.java — "✅ Cardápio carregado!" -> "✅ Merenda carregada!"
sed -i 's|"✅ Cardápio carregado!"|"✅ Merenda carregada!"|' "$JAVA/activities/LoadingActivity.java"

# 3.5 activity_loading.xml — idem
sed -i 's|android:text="✅ Cardápio carregado!"|android:text="✅ Merenda carregada!"|' "$LAYOUTS/activity_loading.xml"

# 3.6 activity_cardapio.xml — "Cardapio EJA" -> "Merenda"
sed -i 's|android:text="Cardapio EJA"|android:text="Merenda"|' "$LAYOUTS/activity_cardapio.xml"

# [4] Verificacao
echo
echo "============================================================"
echo " VERIFICACAO"
echo "============================================================"
echo "-- 'Merenda' (deve aparecer) --"
grep -rn "Merenda" "$LAYOUTS/fragment_cardapio.xml" "$LAYOUTS/activity_loading.xml" "$LAYOUTS/activity_cardapio.xml" "$VALUES/strings.xml" "$JAVA/fragments/HomeFragment.java" "$JAVA/activities/LoadingActivity.java" 2>/dev/null
echo
echo "-- 'Cardápio'/'Cardapio' como STRING DE TELA (deve sobrar so nomes de classe) --"
grep -rn "Cardápio\|Cardapio" "$LAYOUTS" "$VALUES" "$JAVA" 2>/dev/null | grep -v "\.bak" | grep -v "CardapioFragment\|CardapioPagerAdapter\|CardapioAdapter\|CardapioItem\|R.id.navCardapio\|R.id.cardCardapio\|R.id.vp_cardapio\|R.id.btnVoltarCardapio\|txtTituloCardapio\|PREF_SEMANA\|getCardapioSemana\|setSemanaReferencia\|getSemanaAtual\|import com.santosdumont" || echo "(nenhuma string de tela 'Cardapio' restante)"
echo
echo "============================================================"
echo " CONCLUIDO. Recompile no Android Studio."
echo "============================================================"
