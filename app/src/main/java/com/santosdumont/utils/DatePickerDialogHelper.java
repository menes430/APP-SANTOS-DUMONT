package com.santosdumont.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.santosdumont.R;

import java.util.Calendar;

/**
 * DatePicker customizado estilo Samsung.
 * - Grid de dias
 * - Setas de navegacao
 * - Tap no mes -> seletor de ano
 */
public class DatePickerDialogHelper {

    public interface OnDateSelected {
        void onDateSelected(int dia, int mes, int ano);
    }

    private static final String[] MESES = {
            "JANEIRO", "FEVEREIRO", "MARCO", "ABRIL", "MAIO", "JUNHO",
            "JULHO", "AGOSTO", "SETEMBRO", "OUTUBRO", "NOVEMBRO", "DEZEMBRO"
    };

    private static final int ANO_MIN = 2020;
    private static final int ANO_MAX = 2030;

    public static void mostrar(Context ctx, int anoInicial, int mesInicial, int diaInicial,
                               OnDateSelected callback) {
        // Estado local
        final int[] estado = {anoInicial, mesInicial, diaInicial};

        View view = LayoutInflater.from(ctx).inflate(R.layout.dialog_date_picker, null);

        GridLayout grid = view.findViewById(R.id.gridDias);
        TextView tvMesAno = view.findViewById(R.id.tvMesAno);
        View btnAnterior = view.findViewById(R.id.btnMesAnterior);
        View btnProximo = view.findViewById(R.id.btnMesProximo);

        final AlertDialog[] dialogRef = new AlertDialog[1];

        Runnable desenhar = new Runnable() {
            @Override
            public void run() {
                tvMesAno.setText(MESES[estado[1]] + " " + estado[0]);
                grid.removeAllViews();

                Calendar cal = Calendar.getInstance();
                cal.set(estado[0], estado[1], 1);
                int primeiroDia = cal.get(Calendar.DAY_OF_WEEK) - 1;
                int diasNoMes = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

                for (int i = 0; i < primeiroDia; i++) {
                    grid.addView(new TextView(ctx), params());
                }

                for (int d = 1; d <= diasNoMes; d++) {
                    TextView tv = new TextView(ctx);
                    tv.setText(String.valueOf(d));
                    tv.setGravity(Gravity.CENTER);
                    tv.setTextSize(16);
                    tv.setTypeface(null, Typeface.BOLD);

                    Calendar dc = Calendar.getInstance();
                    dc.set(estado[0], estado[1], d);
                    int dow = dc.get(Calendar.DAY_OF_WEEK);

                    if (dow == Calendar.SUNDAY || dow == Calendar.SATURDAY) {
                        tv.setTextColor(Color.parseColor("#FF6B6B"));
                    } else {
                        tv.setTextColor(Color.WHITE);
                    }

                    if (d == estado[2] && estado[0] == anoInicial && estado[1] == mesInicial) {
                        tv.setBackgroundResource(R.drawable.bg_button);
                    }

                    final int diaFinal = d;
                    tv.setOnClickListener(v -> {
                        callback.onDateSelected(diaFinal, estado[1], estado[0]);
                        if (dialogRef[0] != null) dialogRef[0].dismiss();
                    });

                    grid.addView(tv, params());
                }
            }
        };

        btnAnterior.setOnClickListener(v -> {
            estado[1]--;
            if (estado[1] < 0) {
                estado[1] = 11;
                estado[0]--;
                if (estado[0] < ANO_MIN) estado[0] = ANO_MIN;
            }
            desenhar.run();
        });

        btnProximo.setOnClickListener(v -> {
            estado[1]++;
            if (estado[1] > 11) {
                estado[1] = 0;
                estado[0]++;
                if (estado[0] > ANO_MAX) estado[0] = ANO_MAX;
            }
            desenhar.run();
        });

        tvMesAno.setOnClickListener(v -> {
            mostrarSeletorAno(ctx, estado[0], anoEscolhido -> {
                estado[0] = anoEscolhido;
                desenhar.run();
            });
        });

        desenhar.run();

        AlertDialog dialog = new AlertDialog.Builder(ctx)
                .setView(view)
                .setNegativeButton("Cancelar", null)
                .create();
        dialogRef[0] = dialog;
        dialog.show();
    }

    public interface OnAnoSelected {
        void onAnoSelected(int ano);
    }

    private static void mostrarSeletorAno(Context ctx, int anoAtual, OnAnoSelected callback) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.dialog_ano_picker, null);
        LinearLayout llAnos = view.findViewById(R.id.llAnos);

        final AlertDialog[] dialogRef = new AlertDialog[1];

        for (int ano = ANO_MIN; ano <= ANO_MAX; ano++) {
            final int a = ano;
            TextView tv = new TextView(ctx);
            tv.setText(String.valueOf(ano));
            tv.setPadding(16, 16, 16, 16);
            tv.setTextSize(16);
            tv.setGravity(Gravity.CENTER);

            if (ano == anoAtual) {
                tv.setTextColor(Color.parseColor("#0D99FF"));
                tv.setTypeface(null, Typeface.BOLD);
            } else {
                tv.setTextColor(Color.WHITE);
            }

            tv.setClickable(true);
            tv.setFocusable(true);
            tv.setOnClickListener(v -> {
                callback.onAnoSelected(a);
                if (dialogRef[0] != null) dialogRef[0].dismiss();
            });

            llAnos.addView(tv);
        }

        AlertDialog dialog = new AlertDialog.Builder(ctx)
                .setView(view)
                .setNegativeButton("Cancelar", null)
                .create();
        dialogRef[0] = dialog;
        dialog.show();
    }

    private static GridLayout.LayoutParams params() {
        GridLayout.LayoutParams p = new GridLayout.LayoutParams();
        p.width = 0;
        p.height = 70;
        p.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        p.setMargins(2, 2, 2, 2);
        return p;
    }
}