package com.santosdumont.activities;

import android.content.Intent;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.santosdumont.R;
import com.santosdumont.models.Aula;
import com.santosdumont.utils.DataHelper;
import java.util.Calendar;
import java.util.List;

public class CalendarioActivity extends AppCompatActivity {
    private GridLayout grid;
    private TextView tvMes;
    private LinearLayout llAulas;
    private int mesAtual, anoAtual = 2026;
    private int diaSelecionado;
    private SharedPreferences prefs;
    private GestureDetector gestureDetector;
    private EditText edtData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        grid = findViewById(R.id.grid_calendario);
        tvMes = findViewById(R.id.tv_mes_atual);
        llAulas = findViewById(R.id.ll_aulas_dia);
        prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);

        // Botão Voltar
        ImageView btnVoltar = findViewById(R.id.btnVoltar);
        ImageView btnAlarmes = findViewById(R.id.btnAlarmes);
        btnAlarmes.setOnClickListener(v -> startActivity(new Intent(CalendarioActivity.this, AlarmesActivity.class)));
        btnVoltar.setOnClickListener(v -> onBackPressed());

        // Navegação por gesto (Swipe)
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1.getX() - e2.getX() > 100) {
                    // Deslizar para a esquerda = próximo mês
                    mesAtual++;
                    if (mesAtual > 11) mesAtual = 0;
                    desenhar();
                    return true;
                } else if (e2.getX() - e1.getX() > 100) {
                    // Deslizar para a direita = voltar mês
                    mesAtual--;
                    if (mesAtual < 0) mesAtual = 11;
                    desenhar();
                    return true;
                }
                return false;
            }
        });

        // Aplicar gesto na tela inteira
        findViewById(android.R.id.content).setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return false;
        });

        Calendar cal = Calendar.getInstance();
        mesAtual = cal.get(Calendar.MONTH);
        desenhar();
    }

    private void desenhar() {
        String[] nomes = {"JAN", "FEV", "MAR", "ABR", "MAI", "JUN", "JUL", "AGO", "SET", "OUT", "NOV", "DEZ"};
        tvMes.setText(nomes[mesAtual].toUpperCase() + ".");
        grid.removeAllViews();

        Calendar cal = Calendar.getInstance();
        cal.set(2026, mesAtual, 1);
        int primeiroDia = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int diasNoMes = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < primeiroDia; i++) grid.addView(new TextView(this), params());

        for (int dia = 1; dia <= diasNoMes; dia++) {
            TextView tv = new TextView(this);
            tv.setText(String.valueOf(dia));
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(16);
            tv.setTypeface(null, Typeface.BOLD);

            Calendar diaCal = Calendar.getInstance();
            diaCal.set(2026, mesAtual, dia);
            int diaSemana = diaCal.get(Calendar.DAY_OF_WEEK);
            if (diaSemana == Calendar.SUNDAY || diaSemana == Calendar.SATURDAY) {
                tv.setTextColor(Color.parseColor("#FF6B6B"));
            } else {
                tv.setTextColor(Color.WHITE);
            }

            final int d = dia;
            tv.setOnClickListener(v -> mostrarAulas(d));
            grid.addView(tv, params());
        }
    }

    private void mostrarAulas(int dia) {
        diaSelecionado = dia;
        llAulas.removeAllViews();
        tvMes.setText("1 " + getMesAbreviado() + ".");

        Calendar cal = Calendar.getInstance();
        cal.set(2026, mesAtual, dia);
        int diaSemana = cal.get(Calendar.DAY_OF_WEEK);
        String nomeDia;
        switch (diaSemana) {
            case Calendar.MONDAY: nomeDia = "SEG."; break;
            case Calendar.TUESDAY: nomeDia = "TER."; break;
            case Calendar.WEDNESDAY: nomeDia = "QUA."; break;
            case Calendar.THURSDAY: nomeDia = "QUI."; break;
            case Calendar.FRIDAY: nomeDia = "SEX."; break;
            case Calendar.SATURDAY: nomeDia = "SÁB."; break;
            case Calendar.SUNDAY: nomeDia = "DOM."; break;
            default: nomeDia = ""; break;
        }

        String turma = prefs.getString("turma_usuario", "1A MAT");
        List<Aula> aulas = DataHelper.getHorarios(this, turma, nomeDia);

        // Mostrar trabalho salvo do dia (se existir)
        String trabalhoSalvo = prefs.getString("trabalho_dia_" + dia + "_" + (mesAtual + 1) + "_" + anoAtual, "");
        if (!trabalhoSalvo.isEmpty()) {
            TextView tvTrabalho = new TextView(this);
            tvTrabalho.setText("📌 Trabalho: " + trabalhoSalvo);
            tvTrabalho.setTextColor(Color.parseColor("#FFD700"));
            tvTrabalho.setTextSize(18);
            tvTrabalho.setTypeface(null, Typeface.BOLD);
            tvTrabalho.setGravity(Gravity.CENTER);
            tvTrabalho.setPadding(0, 16, 0, 16);
            llAulas.addView(tvTrabalho);
        }

        // Mostrar aulas do dia
        if (aulas.isEmpty()) {
            TextView tvVazio = new TextView(this);
            tvVazio.setText("Sem aulas neste dia");
            tvVazio.setTextColor(Color.GRAY);
            tvVazio.setTextSize(16);
            tvVazio.setGravity(Gravity.CENTER);
            tvVazio.setPadding(0, 24, 0, 24);
            llAulas.addView(tvVazio);
            return;
        }

        TextView tvTitulo = new TextView(this);
        tvTitulo.setText("Aulas do dia:");
        tvTitulo.setTextColor(Color.WHITE);
        tvTitulo.setTextSize(18);
        tvTitulo.setTypeface(null, Typeface.BOLD);
        tvTitulo.setPadding(0, 16, 0, 8);
        llAulas.addView(tvTitulo);

        for (Aula aula : aulas) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(16, 12, 16, 12);
            card.setBackgroundResource(R.drawable.bg_editor_card);

            TextView tvAula = new TextView(this);
            tvAula.setText(aula.getHorario() + " - " + aula.getDisciplina() + " - " + aula.getProfessor());
            tvAula.setTextColor(Color.WHITE);
            tvAula.setTextSize(16);
            tvAula.setTypeface(null, Typeface.BOLD);
            card.addView(tvAula);

            LinearLayout botoes = new LinearLayout(this);
            botoes.setOrientation(LinearLayout.HORIZONTAL);
            botoes.setPadding(0, 12, 0, 0);

            Button btnPresente = new Button(this);
            btnPresente.setText("Presente");
            btnPresente.setTextColor(Color.WHITE);
            btnPresente.setBackgroundColor(Color.parseColor("#167B55"));
            btnPresente.setAllCaps(false);
            botoes.addView(btnPresente);

            Button btnFalta = new Button(this);
            btnFalta.setText("Falta");
            btnFalta.setTextColor(Color.WHITE);
            btnFalta.setBackgroundColor(Color.parseColor("#A84068"));
            btnFalta.setAllCaps(false);
            botoes.addView(btnFalta);

            card.addView(botoes);
            llAulas.addView(card);
        }
    }

    private String getMesAbreviado() {
        String[] nomes = {"JAN", "FEV", "MAR", "ABR", "MAI", "JUN", "JUL", "AGO", "SET", "OUT", "NOV", "DEZ"};
        return nomes[mesAtual];
    }

    private GridLayout.LayoutParams params() {
        GridLayout.LayoutParams p = new GridLayout.LayoutParams();
        p.width = 0;
        p.height = 80;
        p.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        p.setMargins(4, 4, 4, 4);
        return p;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}