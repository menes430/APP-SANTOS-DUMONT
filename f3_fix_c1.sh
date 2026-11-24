#!/usr/bin/env bash
# ============================================================
# F3 — FIX C1: adiciona notifyHue() no ColorWheelView
# ============================================================
set -eu

PROJ="/home/convidado/Área de trabalho/AppSantosDumontFinal"
UTILS="$PROJ/app/src/main/java/com/santosdumont/utils"
CW="$UTILS/ColorWheelView.java"

cd "$PROJ"
pwd

echo "============================================================"
echo " F3 — Fix C1 (notifyHue)"
echo "============================================================"

# [1] Backup rapido do ColorWheelView (R56)
BK="$PROJ/app/_bak_devmenu/F3fix_$(date +%Y%m%d_%H%M%S)"
mkdir -p "$BK"
cp "$CW" "$BK/"
echo "[OK] Backup: $BK"

# [2] CRLF -> LF (R60)
sed -i 's/\r$//' "$CW"

# [3] Adiciona notifyHue(float) protected
if grep -q "protected void notifyHue" "$CW"; then
  echo "[SKIP] notifyHue ja existe"
else
  python3 - "$CW" << 'PY_EOF'
import sys
path = sys.argv[1]
s = open(path, "r").read()

# Insere notifyHue logo DEPOIS do metodo setOnHueChangeListener (que esta no fim)
marker = """    public void setOnHueChangeListener(OnHueChangeListener l) {
        this.listener = l;
    }"""
if marker not in s:
    print("[ERRO] setOnHueChangeListener nao encontrado")
    sys.exit(1)

adicao = """

    // F3/C1: permite que subclasses (ex.: ColorSquareView) disparem o listener
    protected void notifyHue(float hue) {
        if (listener != null) listener.onHueChange(hue);
    }"""
s = s.replace(marker, marker + adicao, 1)
open(path, "w").write(s)
print("[OK] notifyHue adicionado")
PY_EOF
fi

# [4] Verificacao
echo
echo "-- ColorWheelView.java --"
grep -n "notifyHue\|listener != null" "$CW"
echo
echo "-- ColorSquareView.java (chamada) --"
grep -n "notifyHue" "$UTILS/ColorSquareView.java"
echo
echo "============================================================"
echo " C1 OK. Recompile no Android Studio."
echo "============================================================"
