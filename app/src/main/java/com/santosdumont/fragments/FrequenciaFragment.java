package com.santosdumont.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.santosdumont.R;
import com.santosdumont.models.Aula;
import com.santosdumont.utils.DataHelper;

import java.util.Calendar;
import java.util.List;

public class FrequenciaFragment extends Fragment {

    private SharedPreferences prefs;
    private GridLayout grid;
    private TextView tvMes;
    private TextView tvTituloDia;
    private LinearLayout llAulasDia;
    private int anoAtual, mesAtual, diaSelecionado;

    private static final String[] MESES = {"JANEIRO", "FEVEREIRO", "MARCO", "ABRIL", "MAIO", "JUNHO",
            "JULHO", "AGOSTO", "SETEMBRO", "OUTUBRO", "NOVEMBRO", "DEZEMBRO"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_frequencia, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);

        // AVISO 1a ABERTURA DA FREQUENCIA
        if (!prefs.getBoolean("aviso_frequencia_visto", false)) {
            com.santosdumont.utils.AvisoHelper.mostrarAvisoBloqueado(
                requireContext(),
                "Sobre a Frequência",
                "A sua frequência que você colocar aqui não afeta diretamente o sistema de frequência da escola. " +
                "A frequência serve para tirar dúvidas envolvendo a frequência do mês para o pé-de-meia e o Escola 10."
            );
            prefs.edit().putBoolean("aviso_frequencia_visto", true).apply();
        }
        grid = view.findViewById(R.id.gridFrequencia);
        tvMes = view.findViewById(R.id.tvMesFrequencia);
        tvTituloDia = view.findViewById(R.id.tvTituloDiaFreq);
        llAulasDia = view.findViewById(R.id.llAulasDia);

        Calendar cal = Calendar.getInstance();
        anoAtual = cal.get(Calendar.YEAR);
        mesAtual = cal.get(Calendar.MONTH);
        diaSelecionado = cal.get(Calendar.DAY_OF_MONTH);

        tvMes.setOnClickListener(v -> mudarMes());

        desenharCalendario();
        carregarAulasDoDia(diaSelecionado);

        aplicarTemaNaTela(view);

    }

    private void mudarMes() {
        mesAtual++;
        if (mesAtual > 11) {
            mesAtual = 0;
            anoAtual++;
        }
        desenharCalendario();
        carregarAulasDoDia(diaSelecionado);
    }

    private void desenharCalendario() {
        tvMes.setText(MESES[mesAtual] + " " + anoAtual);
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
            tv.setTextSize(14);
            tv.setTypeface(null, Typeface.BOLD);

            Calendar diaCal = Calendar.getInstance();
            diaCal.set(anoAtual, mesAtual, dia);
            int diaSemana = diaCal.get(Calendar.DAY_OF_WEEK);

            // Cor de fundo baseada em presenca/falta
            String chaveDia = anoAtual + "-" + mesAtual + "-" + dia;
            String status = prefs.getString("freq_dia_" + chaveDia, null);

            if (status != null) {
                if (status.equals("presente")) {
                    tv.setBackgroundColor(corSucesso());
                } else if (status.equals("falta")) {
                    tv.setBackgroundColor(corErro());
                } else {
                    tv.setBackgroundResource(R.drawable.bg_calendar_cell);
                }
            } else if (diaSemana == Calendar.SUNDAY || diaSemana == Calendar.SATURDAY) {
                int corFim = Color.parseColor("#FF6B6B");
                if (!com.santosdumont.utils.ThemePrefs.isPadrao(requireContext())) {
                    corFim = com.santosdumont.utils.ThemePrefs.getColorTheme(requireContext()).getPrimary();
                }
                tv.setTextColor(corFim);
                tv.setBackgroundResource(R.drawable.bg_calendar_cell);
            } else {
                int corTexto = Color.WHITE;
                if (!com.santosdumont.utils.ThemePrefs.isPadrao(requireContext())) {
                    corTexto = com.santosdumont.utils.ThemePrefs.getColorTheme(requireContext()).getTextPrimary();
                }
                tv.setTextColor(corTexto);
                tv.setBackgroundResource(R.drawable.bg_calendar_cell);
            }

            if (dia == diaSelecionado) {
                tv.setBackgroundResource(R.drawable.bg_button);
            }

            final int d = dia;
            tv.setOnClickListener(v -> {
                diaSelecionado = d;
                desenharCalendario();
                carregarAulasDoDia(d);
            });

            grid.addView(tv, params());
        }
    }

    private void carregarAulasDoDia(int dia) {
        llAulasDia.removeAllViews();
        tvTituloDia.setText("Aulas do dia " + dia + "/" + (mesAtual + 1) + "/" + anoAtual);

        String turma = prefs.getString("turma_usuario", "1A MAT");

        Calendar cal = Calendar.getInstance();
        cal.set(anoAtual, mesAtual, dia);
        int diaSemana = cal.get(Calendar.DAY_OF_WEEK);

        String nomeDia;
        switch (diaSemana) {
            case Calendar.MONDAY: nomeDia = "Segunda"; break;
            case Calendar.TUESDAY: nomeDia = "Terça"; break;
            case Calendar.WEDNESDAY: nomeDia = "Quarta"; break;
            case Calendar.THURSDAY: nomeDia = "Quinta"; break;
            case Calendar.FRIDAY: nomeDia = "Sexta"; break;
            default: nomeDia = null;
        }

        if (nomeDia == null) {
            TextView tv = new TextView(requireContext());
            tv.setText("Fim de semana - sem aulas");
            tv.setTextColor(com.santosdumont.utils.ThemePrefs.isPadrao(requireContext()) ? 0xFFAAB5B2 : com.santosdumont.utils.ThemePrefs.getColorTheme(requireContext()).getTextSecondary());
            tv.setPadding(16, 16, 16, 16);
            llAulasDia.addView(tv);
            return;
        }

        List<Aula> aulas = DataHelper.getHorarios(requireContext(), turma, nomeDia);
        if (aulas == null || aulas.isEmpty()) {
            TextView tv = new TextView(requireContext());
            tv.setText("Sem aulas neste dia");
            tv.setTextColor(com.santosdumont.utils.ThemePrefs.isPadrao(requireContext()) ? 0xFFAAB5B2 : com.santosdumont.utils.ThemePrefs.getColorTheme(requireContext()).getTextSecondary());
            tv.setPadding(16, 16, 16, 16);
            llAulasDia.addView(tv);
            return;
        }

        for (Aula a : aulas) {
            if (a.isSemAula()) continue;
            if (a.getDisciplina().equalsIgnoreCase("INTERVALO")) continue;
            if (a.getDisciplina().equalsIgnoreCase("SAÍDA")) continue;

            adicionarCardAula(a, dia);
        }
    }

    private void adicionarCardAula(final Aula a, final int dia) {
        View item = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_aula_frequencia, llAulasDia, false);

        TextView tvHorario = item.findViewById(R.id.tvHorarioAulaFreq);
        TextView tvDisc = item.findViewById(R.id.tvDisciplinaAulaFreq);
        TextView tvProf = item.findViewById(R.id.tvProfessorAulaFreq);
        MaterialButton btnP = item.findViewById(R.id.btnMarcarPresenca);
        MaterialButton btnF = item.findViewById(R.id.btnMarcarFalta);

        tvHorario.setText(a.getHorario());
        tvDisc.setText(a.getDisciplina());
        tvProf.setText("Prof. " + a.getProfessor());

        final String chave = "freq_" + anoAtual + "-" + mesAtual + "-" + dia + "_" + a.getHorario();
        String status = prefs.getString(chave, null);

        // Estado inicial: cores
        if ("presente".equals(status)) {
            btnP.setBackgroundTintList(android.content.res.ColorStateList.valueOf(corSucesso()));
            btnF.setBackgroundTintList(android.content.res.ColorStateList.valueOf((corErro() & 0x00FFFFFF) | 0x44000000));
        } else if ("falta".equals(status)) {
            btnP.setBackgroundTintList(android.content.res.ColorStateList.valueOf((corSucesso() & 0x00FFFFFF) | 0x44000000));
            btnF.setBackgroundTintList(android.content.res.ColorStateList.valueOf(corErro()));
        } else {
            btnP.setBackgroundTintList(android.content.res.ColorStateList.valueOf((corSucesso() & 0x00FFFFFF) | 0x66000000));
            btnF.setBackgroundTintList(android.content.res.ColorStateList.valueOf((corErro() & 0x00FFFFFF) | 0x66000000));
        }

        btnP.setOnClickListener(v -> {
            String atual = prefs.getString(chave, null);
            if ("presente".equals(atual)) {
                prefs.edit().remove(chave).apply();
                btnP.setBackgroundTintList(android.content.res.ColorStateList.valueOf((corSucesso() & 0x00FFFFFF) | 0x66000000));
                btnF.setBackgroundTintList(android.content.res.ColorStateList.valueOf((corErro() & 0x00FFFFFF) | 0x66000000));
                Toast.makeText(requireContext(), "Desmarcado", Toast.LENGTH_SHORT).show();
            } else {
                prefs.edit().putString(chave, "presente").apply();
                btnP.setBackgroundTintList(android.content.res.ColorStateList.valueOf(corSucesso()));
                btnF.setBackgroundTintList(android.content.res.ColorStateList.valueOf((corErro() & 0x00FFFFFF) | 0x44000000));
                Toast.makeText(requireContext(), "Presente!", Toast.LENGTH_SHORT).show();
            }
        });

        btnF.setOnClickListener(v -> {
            String atual = prefs.getString(chave, null);
            if ("falta".equals(atual)) {
                prefs.edit().remove(chave).apply();
                btnP.setBackgroundTintList(android.content.res.ColorStateList.valueOf((corSucesso() & 0x00FFFFFF) | 0x66000000));
                btnF.setBackgroundTintList(android.content.res.ColorStateList.valueOf((corErro() & 0x00FFFFFF) | 0x66000000));
                Toast.makeText(requireContext(), "Desmarcado", Toast.LENGTH_SHORT).show();
            } else {
                prefs.edit().putString(chave, "falta").apply();
                btnP.setBackgroundTintList(android.content.res.ColorStateList.valueOf((corSucesso() & 0x00FFFFFF) | 0x44000000));
                btnF.setBackgroundTintList(android.content.res.ColorStateList.valueOf(corErro()));
                Toast.makeText(requireContext(), "Falta registrada", Toast.LENGTH_SHORT).show();
            }
        });

        llAulasDia.addView(item);
    }

    private GridLayout.LayoutParams params() {
        GridLayout.LayoutParams p = new GridLayout.LayoutParams();
        p.width = 0;
        p.height = 60;
        p.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        p.setMargins(4, 4, 4, 4);
        return p;
    }

    // ============================================================
    // APLICAR TEMA NA TELA DE FREQUENCIA
    // ============================================================
    private void aplicarTemaNaTela(View view) {
        if (com.santosdumont.utils.ThemePrefs.isPadrao(requireContext())) return;
        com.santosdumont.utils.ColorTheme t = com.santosdumont.utils.ThemePrefs.getColorTheme(requireContext());

        view.setBackgroundColor(t.getBackground());

        android.widget.TextView titulo = view.findViewById(R.id.txtTituloFrequencia);
        if (titulo != null) titulo.setTextColor(t.getTextPrimary());

        if (tvMes != null) tvMes.setTextColor(t.getPrimary());
        if (tvTituloDia != null) tvTituloDia.setTextColor(t.getTextPrimary());

        // Reconstroi o grid e a lista
        desenharCalendario();
        if (tvTituloDia != null) carregarAulasDoDia(diaSelecionado);
    }

    // Retorna cor de sucesso adaptada ao tema (ou padrao)
    private int corSucesso() {
        return com.santosdumont.utils.ThemePrefs.isPadrao(requireContext())
                ? 0xFF4ADE80
                : com.santosdumont.utils.ThemePrefs.getColorTheme(requireContext()).getSuccess();
    }

    private int corErro() {
        return com.santosdumont.utils.ThemePrefs.isPadrao(requireContext())
                ? 0xFFF87171
                : com.santosdumont.utils.ThemePrefs.getColorTheme(requireContext()).getError();
    }
}