package com.santosdumont.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.santosdumont.R;

/**
 * Editor de cor. Quadrado HSV + barra de brilho + 6 campos numericos + preview.
 * Sem cores basicas/personalizadas (removidas a pedido).
 */
public class ThemeEditor {

    public interface OnAplicarListener {
        void onAplicar();
    }

    private static final String[] FONTES_VALORES = {"roboto", "poppins", "inter"};
    private static final String[] FONTES_NOMES = {"Roboto", "Poppins", "Inter"};

    // Flag para bloquear TextWatcher durante setText programatico
    private static boolean atualizando = false;

    public static void abrir(Context ctx, OnAplicarListener callback) {
        final int[] estado = new int[] {
                ThemePrefs.getHue(ctx),
                ThemePrefs.getSat(ctx),
                ThemePrefs.getLight(ctx),
                ThemePrefs.isLightTheme(ctx) ? 1 : 0
        };

        View root = LayoutInflater.from(ctx).inflate(R.layout.dialog_theme_editor, null);

        final ColorSquareView square = root.findViewById(R.id.squareColor);
        final BrightnessBarView bar = root.findViewById(R.id.barBrightness);
        final EditText edtH = root.findViewById(R.id.edtH);
        final EditText edtS = root.findViewById(R.id.edtS);
        final EditText edtB = root.findViewById(R.id.edtB);
        final EditText edtR = root.findViewById(R.id.edtR);
        final EditText edtG = root.findViewById(R.id.edtG);
        final EditText edtAzul = root.findViewById(R.id.edtAzul);
        final View viewCorAtual = root.findViewById(R.id.viewCorAtual);
        final com.google.android.material.card.MaterialCardView cardPreview = root.findViewById(R.id.cardPreview);
        final LinearLayout llPreview = root.findViewById(R.id.llPreview);
        final TextView txtPreviewTitulo = root.findViewById(R.id.txtPreviewTitulo);
        final TextView txtPreviewSecundario = root.findViewById(R.id.txtPreviewSecundario);
        final Spinner spFonte = root.findViewById(R.id.spFonte);
        final android.widget.CheckBox cbCorMorta = root.findViewById(R.id.cbCorMorta);
        if (cbCorMorta != null) {
            cbCorMorta.setChecked(ThemePrefs.isCorMorta(ctx));
        }
        square.setHue(estado[0]);
        square.setSat(estado[1]);
        square.setBrilho(estado[2]);
        bar.setHueSat(estado[0], estado[1]);
        bar.setBrightness(estado[2]);

        ArrayAdapter<String> adp = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_item, FONTES_NOMES);
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFonte.setAdapter(adp);
        String fonteAtual = ThemePrefs.getFont(ctx);
        for (int i = 0; i < FONTES_VALORES.length; i++) {
            if (FONTES_VALORES[i].equals(fonteAtual)) { spFonte.setSelection(i); break; }
        }

        final Runnable syncUI = () -> {
            int cor = Color.HSVToColor(new float[]{estado[0], estado[1], estado[2]});

            atualizando = true;
            if (!edtH.hasFocus()) edtH.setText(String.valueOf(estado[0]));
            if (!edtS.hasFocus()) edtS.setText(String.valueOf(estado[1]));
            if (!edtB.hasFocus()) edtB.setText(String.valueOf(estado[2]));
            if (!edtR.hasFocus()) edtR.setText(String.valueOf((cor >> 16) & 0xFF));
            if (!edtG.hasFocus()) edtG.setText(String.valueOf((cor >> 8) & 0xFF));
            if (!edtAzul.hasFocus()) edtAzul.setText(String.valueOf(cor & 0xFF));
            atualizando = false;

            GradientDrawable bg = new GradientDrawable();
            bg.setColor(cor);
            bg.setCornerRadius(12);
            viewCorAtual.setBackground(bg);

            ColorTheme t = new ColorTheme(estado[0], estado[1], estado[2], com.santosdumont.utils.ThemePrefs.isLightTheme(ctx), false, !cbCorMorta.isChecked());
            cardPreview.setCardBackgroundColor(t.getSurface());
            llPreview.setBackgroundColor(t.getSurface());
            txtPreviewTitulo.setTextColor(t.getTextPrimary());
            txtPreviewSecundario.setTextColor(t.getTextSecondary());
        };

        if (cbCorMorta != null) {
            cbCorMorta.setOnCheckedChangeListener((v, checked) -> syncUI.run());
        }

        square.setOnHueChangeListener(h -> { estado[0] = (int) h; syncUI.run(); });
        square.setOnSatChangeListener(sat -> { estado[1] = (int) sat; bar.setHueSat(estado[0], (int) sat); syncUI.run(); });
        bar.setOnBrightnessChangeListener(b -> { estado[2] = b; square.setBrilho(b); syncUI.run(); });

        // Bloqueia ScrollView enquanto mexe no quadrado/barra
        square.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                case android.view.MotionEvent.ACTION_MOVE:
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return false;
        });
        bar.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                case android.view.MotionEvent.ACTION_MOVE:
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return false;
        });

        TextWatcher tw = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void afterTextChanged(Editable s) {
                if (atualizando) return;
                try {
                    int h = parseIntSafe(edtH, estado[0]);
                    int sat = parseIntSafe(edtS, estado[1]);
                    int br = parseIntSafe(edtB, estado[2]);
                    int r = parseIntSafe(edtR, 0);
                    int g = parseIntSafe(edtG, 0);
                    int b = parseIntSafe(edtAzul, 0);

                    if (s == edtH.getEditableText() || s == edtS.getEditableText() || s == edtB.getEditableText()) {
                        int cor = Color.HSVToColor(new float[]{h, sat, br});
                        r = (cor >> 16) & 0xFF;
                        g = (cor >> 8) & 0xFF;
                        b = cor & 0xFF;
                    } else {
                        int cor = Color.rgb(r, g, b);
                        float[] hsv = new float[3];
                        Color.colorToHSV(cor, hsv);
                        h = Math.round(hsv[0]);
                        sat = Math.round(hsv[1]);
                        br = Math.round(hsv[2]);
                    }
                    estado[0] = h; estado[1] = sat; estado[2] = br;
                    square.setHue(h);
                    square.setSat(sat);
                    square.setBrilho(br);
                    bar.setHueSat(h, sat);
                    bar.setBrightness(br);
                    syncUI.run();
                } catch (Exception ignored) {}
            }
        };
        edtH.addTextChangedListener(tw);
        edtS.addTextChangedListener(tw);
        edtB.addTextChangedListener(tw);
        edtR.addTextChangedListener(tw);
        edtG.addTextChangedListener(tw);
        edtAzul.addTextChangedListener(tw);


        final android.app.Dialog dialog = new android.app.Dialog(ctx);
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setLayout(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        View btnOk = root.findViewById(R.id.btnOkTema);
        View btnCancelar = root.findViewById(R.id.btnCancelarTema);
        if (btnOk != null) {
            btnOk.setOnClickListener(v -> {
                ThemePrefs.setTema(ctx, estado[0], estado[1], estado[2], com.santosdumont.utils.ThemePrefs.isLightTheme(ctx));
                int idx = spFonte.getSelectedItemPosition();
                if (idx >= 0 && idx < FONTES_VALORES.length) ThemePrefs.setFont(ctx, FONTES_VALORES[idx]);
                if (cbCorMorta != null) ThemePrefs.setCorMorta(ctx, cbCorMorta.isChecked());
                if (callback != null) callback.onAplicar();
                dialog.dismiss();
            });
        }
        if (btnCancelar != null) btnCancelar.setOnClickListener(v -> dialog.dismiss());

        syncUI.run();
        dialog.show();
    }

    private static int parseIntSafe(EditText e, int def) {
        try { return Integer.parseInt(e.getText().toString().trim()); }
        catch (Exception ex) { return def; }
    }
}
