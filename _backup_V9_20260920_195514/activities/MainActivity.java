package com.santosdumont.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.santosdumont.R;
import com.santosdumont.models.Sala;
import com.santosdumont.utils.DataHelper;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView txtNomeAluno, txtTurma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String nome = prefs.getString("nome_usuario", "Visitante");
        String turma = prefs.getString("turma_usuario", "Sem turma");

        txtNomeAluno = findViewById(R.id.txtNomeAluno);
        txtTurma = findViewById(R.id.txtTurma);
        txtNomeAluno.setText(nome);
        txtTurma.setText(turma);

        TextView txtMateriaProxima = findViewById(R.id.txtMateriaProxima);
        TextView txtProfessorProxima = findViewById(R.id.txtProfessorProxima);
        TextView txtHorarioAula = findViewById(R.id.txtHorarioAula);
        TextView txtFrequencia = findViewById(R.id.txtFrequencia);

        String diaSemana = getDiaSemana();
        List<com.santosdumont.models.Aula> aulasHoje = DataHelper.getHorarios(turma, diaSemana);
        if (!aulasHoje.isEmpty()) {
            com.santosdumont.models.Aula aula = aulasHoje.get(0);
            txtMateriaProxima.setText(aula.getDisciplina());
            txtProfessorProxima.setText("Prof. " + aula.getProfessor());
            txtHorarioAula.setText(aula.getHorario());
        } else {
            txtMateriaProxima.setText("Sem aulas hoje");
            txtProfessorProxima.setText("Bom descanso!");
            txtHorarioAula.setText("--:--");
        }

        int presencas = prefs.getInt("presencas_total", 0);
        int faltas = prefs.getInt("faltas_total", 0);
        int total = presencas + faltas;
        int porcentagem = total > 0 ? (presencas * 100) / total : 100;
        txtFrequencia.setText(porcentagem + "%");

        findViewById(R.id.cardCardapio).setOnClickListener(v -> startActivity(new Intent(this, CardapioActivity.class)));
        findViewById(R.id.cardHorarios).setOnClickListener(v -> startActivity(new Intent(this, HorariosActivity.class)));
        findViewById(R.id.cardTrabalhos).setOnClickListener(v -> startActivity(new Intent(this, TrabalhosActivity.class)));
        findViewById(R.id.cardCalendario).setOnClickListener(v -> startActivity(new Intent(this, HorariosActivity.class)));
        findViewById(R.id.cardProfessores).setOnClickListener(v -> startActivity(new Intent(this, ProfessoresActivity.class)));
        findViewById(R.id.cardInstagram).setOnClickListener(v -> startActivity(new Intent(this, InstagramActivity.class)));

        FrameLayout btnPerfil = findViewById(R.id.btnPerfil);
        btnPerfil.setOnClickListener(v -> abrirDialogoEditarPerfil());

        // NOVA ORDEM DA BARRA INFERIOR
        LinearLayout navTrabalhos = findViewById(R.id.navTrabalhos);
        LinearLayout navHorarios = findViewById(R.id.navHorarios);
        LinearLayout navInicio = findViewById(R.id.navInicio);
        LinearLayout navCardapio = findViewById(R.id.navCardapio);
        LinearLayout navProfessores = findViewById(R.id.navProfessores);

        navTrabalhos.setOnClickListener(v -> startActivity(new Intent(this, TrabalhosActivity.class)));
        navHorarios.setOnClickListener(v -> startActivity(new Intent(this, HorariosActivity.class)));
        navInicio.setOnClickListener(v -> { /* Já está no Início */ });
        findViewById(R.id.cardNotas).setOnClickListener(v -> startActivity(new Intent(this, NotasActivity.class)));
        navCardapio.setOnClickListener(v -> startActivity(new Intent(this, CardapioActivity.class)));
        navProfessores.setOnClickListener(v -> startActivity(new Intent(this, ProfessoresActivity.class)));
    }

    private void abrirDialogoEditarPerfil() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String nomeAtual = prefs.getString("nome_usuario", "");
        String turmaAtual = prefs.getString("turma_usuario", "");
        android.widget.EditText inputNome = new android.widget.EditText(this);
        inputNome.setText(nomeAtual);
        inputNome.setHint("Digite seu nome");
        android.widget.Spinner spinnerTurma = new android.widget.Spinner(this);
        List<Sala> turmas = DataHelper.getTurmas();
        android.widget.ArrayAdapter<Sala> adapter = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_spinner_item, turmas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTurma.setAdapter(adapter);
        for (int i = 0; i < turmas.size(); i++) {
            if (turmas.get(i).getNome().equals(turmaAtual)) { spinnerTurma.setSelection(i); break; }
        }
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);
        layout.addView(inputNome);
        layout.addView(spinnerTurma);

        new AlertDialog.Builder(this)
                .setTitle("Editar Perfil")
                .setView(layout)
                .setPositiveButton("Salvar", (dialog, which) -> {
                    String novoNome = inputNome.getText().toString().trim();
                    Sala novaTurma = (Sala) spinnerTurma.getSelectedItem();
                    if (novoNome.isEmpty() || novaTurma == null) return;
                    prefs.edit().putString("nome_usuario", novoNome).putString("turma_usuario", novaTurma.getNome()).apply();
                    txtNomeAluno.setText(novoNome);
                    txtTurma.setText(novaTurma.getNome());
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private String getDiaSemana() {
        int dia = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        switch (dia) {
            case Calendar.MONDAY: return "Segunda";
            case Calendar.TUESDAY: return "Terça";
            case Calendar.WEDNESDAY: return "Quarta";
            case Calendar.THURSDAY: return "Quinta";
            case Calendar.FRIDAY: return "Sexta";
            default: return "Segunda";
        }
    }
}