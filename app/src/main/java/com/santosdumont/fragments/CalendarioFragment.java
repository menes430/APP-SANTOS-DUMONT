package com.santosdumont.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.santosdumont.R;
import com.santosdumont.models.TrabalhoModel;
import com.santosdumont.utils.ColorTheme;
import com.santosdumont.utils.DatePickerDialogHelper;
import com.santosdumont.utils.FeriadosHelper;
import com.santosdumont.utils.ThemePrefs;
import com.santosdumont.utils.TrabalhoAlarmeHelper;
import com.santosdumont.utils.TrabalhoStorage;

import java.util.Calendar;
import java.util.List;

public class CalendarioFragment extends Fragment {

    private GridLayout grid;
    private TextView tvMes;
    private TextView tvTituloDia;
    private LinearLayout llTrabalhos;
    private int anoAtual;
    private int mesAtual;
    private int diaSelecionado;

    private static final String[] MESES = {"JAN", "FEV", "MAR", "ABR", "MAI", "JUN",
            "JUL", "AGO", "SET", "OUT", "NOV", "DEZ"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendario, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        grid = view.findViewById(R.id.grid_calendario);
        tvMes = view.findViewById(R.id.tv_mes_atual);
        tvMes.setOnClickListener(v -> {
            DatePickerDialogHelper.mostrar(requireContext(), anoAtual, mesAtual, diaSelecionado,
                    (dia, mes, ano) -> {
                        anoAtual = ano;
                        mesAtual = mes;
                        diaSelecionado = dia;
                        desenhar();
                        mostrarTrabalhosDoDia(dia);
                    });
        });
        tvTituloDia = view.findViewById(R.id.tv_titulo_dia);
        llTrabalhos = view.findViewById(R.id.ll_trabalhos_dia);

        Calendar cal = Calendar.getInstance();
        anoAtual = cal.get(Calendar.YEAR);
        mesAtual = cal.get(Calendar.MONTH);
        diaSelecionado = cal.get(Calendar.DAY_OF_MONTH);

        view.findViewById(R.id.btnMesAnterior).setOnClickListener(v -> { if (mesAtual > 0) { mesAtual--; } else { mesAtual = 11; anoAtual--; } desenhar(); });
        view.findViewById(R.id.btnProximoMes).setOnClickListener(v -> { if (mesAtual < 11) { mesAtual++; } else { mesAtual = 0; anoAtual++; } desenhar(); });
        tvMes.setOnClickListener(v -> abrirGradeMeses());

        aplicarTema(view);
        desenhar();
        mostrarTrabalhosDoDia(diaSelecionado);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (grid != null) {
            aplicarTema(getView());
            desenhar();
            mostrarTrabalhosDoDia(diaSelecionado);
        }
    }

    private boolean padrao() { return ThemePrefs.isPadrao(requireContext()); }
    private ColorTheme tema() { return ThemePrefs.getColorTheme(requireContext()); }

    private void aplicarTema(View view) {
        if (view == null || padrao()) return;
        ColorTheme t = tema();
        view.setBackgroundColor(t.getBackground());
        if (tvMes != null) tvMes.setTextColor(t.getPrimary());
        if (tvTituloDia != null) tvTituloDia.setTextColor(t.getTextPrimary());
    }

    private void desenhar() {
        if (tvMes == null || grid == null) return;

        boolean isPadrao = padrao();
        ColorTheme t = isPadrao ? null : tema();

        int corDiaSemana = isPadrao ? Color.WHITE : t.getTextPrimary();
        int corFimSemana = isPadrao ? Color.parseColor("#FF6B6B") : t.getPrimary(); // M10: cor do app

        tvMes.setText(MESES[mesAtual] + " " + anoAtual);
        if (!isPadrao) tvMes.setTextColor(t.getPrimary());

        grid.removeAllViews();

        Calendar cal = Calendar.getInstance();
        cal.set(anoAtual, mesAtual, 1);
        int primeiroDia = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int diasNoMes = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < primeiroDia; i++) grid.addView(new TextView(requireContext()), params());

        for (int dia = 1; dia <= diasNoMes; dia++) {
            TextView tv = new TextView(requireContext());
            tv.setText(String.valueOf(dia));
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(16);
            tv.setTypeface(null, Typeface.BOLD);

            Calendar diaCal = Calendar.getInstance();
            diaCal.set(anoAtual, mesAtual, dia);
            int diaSemana = diaCal.get(Calendar.DAY_OF_WEEK);

            // Feriado?
            String feriado = FeriadosHelper.getFeriado(anoAtual, mesAtual, dia);
            if (feriado != null) {
                tv.setTextColor(0xFFFFD700); // dourado
                tv.setTextSize(12); // diminui para caber o nome
            } else if (diaSemana == Calendar.SUNDAY || diaSemana == Calendar.SATURDAY) {
                tv.setTextColor(corFimSemana);
            } else {
                tv.setTextColor(corDiaSemana);
            }

            if (dia == diaSelecionado) tv.setBackgroundResource(R.drawable.bg_button);

            final int d = dia;
            tv.setOnClickListener(v -> {
                diaSelecionado = d;
                desenhar();
                mostrarTrabalhosDoDia(d);
            });

            grid.addView(tv, params());
        }
    }

    private void mostrarTrabalhosDoDia(int dia) {
        if (llTrabalhos == null) return;
        llTrabalhos.removeAllViews();
        String feriado = FeriadosHelper.getFeriado(anoAtual, mesAtual, dia);
        if (feriado != null) {
            tvTituloDia.setText("Dia " + dia + "/" + (mesAtual + 1) + "/" + anoAtual + " - " + feriado);
        } else {
            tvTituloDia.setText("Dia " + dia + "/" + (mesAtual + 1) + "/" + anoAtual);
        }

        boolean isPadrao = padrao();
        ColorTheme t = isPadrao ? null : tema();
        int corSec = isPadrao ? 0xFFAAB5B2 : t.getTextSecondary();

        List<TrabalhoModel> todos = TrabalhoStorage.listar(requireContext());
        boolean achou = false;

        for (final TrabalhoModel trab : todos) {
            String[] partes = trab.data.split("/");
            if (partes.length < 3) continue;
            try {
                int dTrab = Integer.parseInt(partes[0]);
                int mTrab = Integer.parseInt(partes[1]) - 1;
                int aTrab = Integer.parseInt(partes[2]);
                if (aTrab < 100) aTrab += 2000;
                if (dTrab == dia && mTrab == mesAtual && aTrab == anoAtual) {
                    achou = true;
                    adicionarCardTrabalho(trab);
                }
            } catch (Exception ignored) {}
        }

        if (!achou) {
            TextView tv = new TextView(requireContext());
            tv.setText("Nenhum trabalho neste dia");
            tv.setTextColor(corSec);
            tv.setTextSize(14);
            tv.setPadding(16, 16, 16, 16);
            llTrabalhos.addView(tv);
        }
    }

    private void adicionarCardTrabalho(final TrabalhoModel trab) {
        boolean isPadrao = padrao();
        ColorTheme t = isPadrao ? null : tema();

        int corTexto = isPadrao ? 0xFFFFFFFF : t.getTextPrimary();
        int corSec   = isPadrao ? 0xFFAAB5B2 : t.getTextSecondary();
        int corMuted = isPadrao ? 0xFF888888 : t.getTextMuted();

        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.bg_editor_card);
        card.setPadding(16, 12, 16, 12);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 8);
        card.setLayoutParams(lp);

        boolean passado = false;
        try {
            long ts = TrabalhoAlarmeHelper.parseDataHora(trab.data, trab.hora);
            passado = ts < System.currentTimeMillis();
        } catch (Exception ignored) {}

        int cor = passado ? corMuted : corTexto;

        TextView tvTitulo = new TextView(requireContext());
        tvTitulo.setText(trab.nome);
        tvTitulo.setTextColor(cor);
        tvTitulo.setTextSize(15);
        tvTitulo.setTypeface(null, Typeface.BOLD);
        card.addView(tvTitulo);

        TextView tvHora = new TextView(requireContext());
        tvHora.setText(trab.hora + (passado ? " (passado)" : ""));
        tvHora.setTextColor(passado ? corMuted : corSec);
        tvHora.setTextSize(13);
        card.addView(tvHora);

        llTrabalhos.addView(card);
    }

    private GridLayout.LayoutParams params() {
        GridLayout.LayoutParams p = new GridLayout.LayoutParams();
        p.width = 0;
        p.height = 70;
        p.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        p.setMargins(6, 6, 6, 6);
        return p;
    }

    private void abrirGradeMeses() {
        android.app.Dialog dialog = new android.app.Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        android.view.View v = getLayoutInflater().inflate(R.layout.dialog_grade_meses, null);
        dialog.setContentView(v);
        android.widget.TextView tvAno = v.findViewById(R.id.tv_ano_meses);
        android.widget.GridLayout grid = v.findViewById(R.id.grid_meses);
        tvAno.setText(String.valueOf(anoAtual));
        for (int i = 0; i < 12; i++) {
            final int mesIndex = i;
            android.widget.TextView tv = new android.widget.TextView(requireContext());
            tv.setText(MESES[mesIndex]);
            tv.setGravity(android.view.Gravity.CENTER);
            tv.setTextSize(16);
            tv.setTextColor(mesIndex == mesAtual ? 0xFFFFFFFF : 0xFFAAB5B2);
            
            android.widget.GridLayout.LayoutParams p = new android.widget.GridLayout.LayoutParams();
            p.width = 0; p.height = 120; p.columnSpec = android.widget.GridLayout.spec(mesIndex % 4, 1f);
            p.rowSpec = android.widget.GridLayout.spec(mesIndex / 4);
            tv.setLayoutParams(p);
            
            tv.setOnClickListener(view -> {
                mesAtual = mesIndex;
                dialog.dismiss();
                desenhar();
            });
            grid.addView(tv);
        }
        tvAno.setOnClickListener(view -> {
            android.animation.ObjectAnimator anim = android.animation.ObjectAnimator.ofFloat(v, "rotationY", 0f, 90f);
            anim.setDuration(250);
            anim.addListener(new android.animation.AnimatorListenerAdapter() {
                @Override public void onAnimationEnd(android.animation.Animator animation) { dialog.dismiss(); abrirGradeAnos(); }
            }); anim.start();
        });
        dialog.show();
    }
    private void abrirGradeAnos() {
        android.app.Dialog dialog = new android.app.Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        android.view.View v = getLayoutInflater().inflate(R.layout.dialog_grade_anos, null);
        dialog.setContentView(v);
        v.setRotationY(-90f); v.animate().rotationY(0f).setDuration(300).start();
        android.widget.GridLayout grid = v.findViewById(R.id.grid_anos);
        for (int i = -5; i <= 6; i++) {
            final int ano = anoAtual + i;
            final int pos = i + 5;
            android.widget.TextView tv = new android.widget.TextView(requireContext());
            tv.setText(String.valueOf(ano));
            tv.setGravity(android.view.Gravity.CENTER);
            tv.setTextSize(16);
            tv.setTextColor(ano == anoAtual ? 0xFFFFFFFF : 0xFFAAB5B2);
            
            android.widget.GridLayout.LayoutParams p = new android.widget.GridLayout.LayoutParams();
            p.width = 0; p.height = 120; p.columnSpec = android.widget.GridLayout.spec(pos % 4, 1f);
            p.rowSpec = android.widget.GridLayout.spec(pos / 4);
            tv.setLayoutParams(p);
            
            tv.setOnClickListener(view -> {
                anoAtual = ano;
                dialog.dismiss();
                desenhar();
            });
            grid.addView(tv);
        }
        dialog.show();
    }
}
