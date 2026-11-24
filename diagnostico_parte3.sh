#!/usr/bin/env bash
# Diagnóstico Parte 3 — Santos Dumont — somente leitura
set -u
echo "============================================================"
echo " DIAGNÓSTICO PARTE 3 — $(date '+%d/%m/%Y %H:%M')"
echo "============================================================"
echo "PWD: $(pwd)"
echo

echo "== [1] ESTRUTURA RAIZ =="
ls -1 | head -30
echo

echo "== [2] GRADLE / VERSÃO =="
if [ -f app/build.gradle ]; then
  grep -nE "versionCode|versionName|minSdk|targetSdk|compileSdk" app/build.gradle || echo "(não achou campos)"
else
  echo "!! app/build.gradle NÃO encontrado"
fi
echo

echo "== [3] JAVA SOURCES (contagem por pasta) =="
if [ -d app/src/main/java ]; then
  find app/src/main/java -name "*.java" | wc -l | xargs echo "Total .java:"
  echo "-- por diretório --"
  find app/src/main/java -name "*.java" -printf "%h\n" | sort | uniq -c
else
  echo "!! app/src/main/java NÃO encontrado"
fi
echo

echo "== [4] FRAGMENTS =="
find app/src/main/java -name "*Fragment.java" 2>/dev/null | sed 's|.*/||' | sort
echo

echo "== [5] ACTIVITIES =="
find app/src/main/java -name "*Activity.java" 2>/dev/null | sed 's|.*/||' | sort
echo

echo "== [6] SERVICES / RECEIVERS =="
find app/src/main/java \( -name "*Service.java" -o -name "*Receiver.java" \) 2>/dev/null | sed 's|.*/||' | sort
echo

echo "== [7] ARQUIVOS-CHAVE PARA PARTE 3 =="
for f in \
  app/src/main/java/com/santosdumont/ThemeEditorActivity.java \
  app/src/main/java/com/santosdumont/ThemeEditorFragment.java \
  app/src/main/java/com/santosdumont/DevMenuActivity.java \
  app/src/main/java/com/santosdumont/HomeFragment.java \
  app/src/main/java/com/santosdumont/FrequenciaFragment.java \
  app/src/main/java/com/santosdumont/CardapioFragment.java \
  app/src/main/java/com/santosdumont/TutorialActivity.java \
  app/src/main/java/com/santosdumont/ChangelogActivity.java ; do
  if [ -f "$f" ]; then
    printf "OK   %-70s %s linhas\n" "$f" "$(wc -l < "$f")"
  else
    printf "FALTA %-70s\n" "$f"
  fi
done
echo

echo "== [8] LAYOUTS XML =="
find app/src/main/res/layout -name "*.xml" 2>/dev/null | sed 's|.*/||' | sort | head -80
echo "Total layouts: $(find app/src/main/res/layout -name '*.xml' 2>/dev/null | wc -l)"
echo

echo "== [9] PREFERÊNCIAS / PREFS (temas, fontes, rollback) =="
grep -rnE "getSharedPreferences|SharedPreferences" app/src/main/java 2>/dev/null | head -20
echo

echo "== [10] FLAGS DE TEMA (M11 rollback) =="
grep -rnE "fundo_literal|theme_fundo|cor_literal|rollback" app/src/main/java 2>/dev/null | head -20 || echo "(nenhuma flag encontrada)"
echo

echo "== [11] INDICADORES CHANGELOG (E7) =="
grep -rnE "indicator|quadrado|dot" app/src/main/java/com/santosdumont/ChangelogActivity.java 2>/dev/null | head -20 || echo "(arquivo não encontrado)"
echo

echo "== [12] NOTIFICAÇÕES (M13, F1, F6) =="
find app/src/main/java -name "*Notification*.java" -o -name "*Alarm*.java" -o -name "*Vaga*.java" 2>/dev/null | sed 's|.*/||' | sort
echo

echo "== [13] RESOURCES DE TEMA =="
for f in app/src/main/res/values/themes.xml app/src/main/res/values/colors.xml app/src/main/res/values/styles.xml ; do
  [ -f "$f" ] && echo "OK $f ($(wc -l < "$f") linhas)" || echo "FALTA $f"
done
echo

echo "== [14] BACKUP DEV_MENU (R56) =="
[ -d app/_bak_devmenu ] && echo "Existe app/_bak_devmenu ($(ls -1 app/_bak_devmenu | wc -l) arquivos)" || echo "!! app/_bak_devmenu NÃO existe"
echo

echo "== [15] FIREBASE / DADOS_USO =="
grep -rn "dados_uso" app/src/main/java 2>/dev/null | head -5 || echo "(não encontrado)"
echo

echo "== [16] FIM DO DIAGNÓSTICO =="
