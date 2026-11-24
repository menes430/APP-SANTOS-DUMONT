package com.santosdumont.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.santosdumont.R;
import com.santosdumont.models.Aula;
import com.santosdumont.models.Sala;
import com.santosdumont.utils.DataHelper;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView txtNomeAluno, txtTurma;
    private TextView txtMateriaProxima, txtProfessorProxima, txtHorarioAula;
    private TextView txtTituloProxima;
    private TextView txtFrequencia;
    private TextView txtSalaProxima;

    private SharedPreferences prefs;
    private String turmaAtual;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable atualizador = new Runnable() {
        @Override
        public void run() {
            atualizarProximaAula();
            handler.postDelayed(this, 60000); // 60s
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);

        // Views
        txtNomeAluno = findViewById(R.id.txtNomeAluno);
        txtTurma = findViewById(R.id.txtTurma);
        txtMateriaProxima = findViewById(R.id.txtMateriaProxima);
        txtProfessorProxima = findViewById(R.id.txtProfessorProxima);
        txtHorarioAula = findViewById(R.id.txtHorarioAula);
        txtFrequencia = findViewById(R.id.txtFrequencia);
        txtSalaProxima = findViewById(R.id.txtSalaProxima);

        // "Próxima aula" — pode estar como TextView ou como título do card.
        // Tentamos pegar; se não achar, ignoramos.
        int idTitulo = getResources().getIdentifier("txtTituloProxima", "id", getPackageName());
        if (idTitulo != 0) txtTituloProxima = findViewById(idTitulo);

        // Dados do usuário
        String nome = prefs.getString("nome_usuario", "Visitante");
        turmaAtual = prefs.getString("turma_usuario", "1A MAT");
        txtNomeAluno.setText(nome);
        txtTurma.setText(turmaAtual);

        // Frequência
        int presencas = prefs.getInt("presencas_total", 0);
        int faltas = prefs.getInt("faltas_total", 0);
        int total = presencas + faltas;
        int porcentagem = total > 0 ? (presencas * 100) / total : 100;
        txtFrequencia.setText(porcentagem + "%");

        // Navegação
        configurarNavegacao();

        // Editar perfil
        FrameLayout btnPerfil = findViewById(R.id.btnPerfil);
        btnPerfil.setOnClickListener(v -> abrirDialogoEditarPerfil());

        // Primeira atualização
        atualizarProximaAula();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(atualizador);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(atualizador);
    }

    // ============================================================
    // LÓGICA PRINCIPAL: qual é a próxima aula?
    // ============================================================
    private void atualizarProximaAula() {
        // Atualiza turma (pode ter mudado no perfil)
        turmaAtual = prefs.getString("turma_usuario", "1A MAT");
        txtTurma.setText(turmaAtual);

        Calendar agora = Calendar.getInstance();
        int diaSemana = agora.get(Calendar.DAY_OF_WEEK);

        // Fim de semana?
        if (diaSemana == Calendar.SATURDAY || diaSemana == Calendar.SUNDAY) {
            mostrarSemAula();
            return;
        }

        String nomeDia = getNomeDia(diaSemana);
        List<Aula> aulas = DataHelper.getHorarios(turmaAtual, nomeDia);

        if (aulas == null || aulas.isEmpty()) {
            mostrarSemAula();
            return;
        }

        int minutoAtual = agora.get(Calendar.HOUR_OF_DAY) * 60 + agora.get(Calendar.MINUTE);

        // 1) Estamos DENTRO de alguma aula?
        for (Aula a : aulas) {
            int inicio = parseMinuto(a.getHorario());
            if (inicio < 0) continue;
            int fim = inicio + 50; // aula padrão de 50 min
            if (minutoAtual >= inicio && minutoAtual < fim) {
                mostrarAula(a, true);
                return;
            }
        }

        // 2) Qual é a PRÓXIMA aula?
        Aula proxima = null;
        for (Aula a : aulas) {
            int inicio = parseMinuto(a.getHorario());
            if (inicio < 0) continue;
            if (inicio > minutoAtual) {
                proxima = a;
                break;
            }
        }

        if (proxima != null) {
            mostrarAula(proxima, false);
        } else {
            mostrarAulasEncerradas();
        }
    }

    private void mostrarAula(Aula a, boolean agora) {
        if (txtTituloProxima != null) {
            txtTituloProxima.setText(agora ? R.string.str_agora : R.string.str_proxima);
        }
        txtMateriaProxima.setText(a.getDisciplina());
        txtProfessorProxima.setText("Prof. " + a.getProfessor());
        txtHorarioAula.setText(a.getHorario());
        if (txtSalaProxima != null) txtSalaProxima.setText("");
    }

    private void mostrarSemAula() {
        if (txtTituloProxima != null) txtTituloProxima.setText(R.string.str_proxima);
        txtMateriaProxima.setText(R.string.str_sem_aulas_hoje);
        txtProfessorProxima.setText(R.string.str_bom_descanso);
        txtHorarioAula.setText("--:--");
        if (txtSalaProxima != null) txtSalaProxima.setText("");
    }

    private void mostrarAulasEncerradas() {
        if (txtTituloProxima != null) txtTituloProxima.setText(R.string.str_proxima);
        txtMateriaProxima.setText(R.string.str_aulas_encerradas);
        txtProfessorProxima.setText(R.string.str_bom_descanso);
        txtHorarioAula.setText("--:--");
        if (txtSalaProxima != null) txtSalaProxima.setText("");
    }

    /** Converte "7h", "8h", "10:10", "9:40-10:10", "11:40" em minutos desde 00:00 */
    private int parseMinuto(String horario) {
        if (horario == null) return -1;
        try {
            String h = horario.trim().toLowerCase();
            if (h.contains("intervalo") || h.contains("saída") || h.contains("saida")) return -1;

            // Remove "h" e normaliza
            h = h.replace("h", ":00").replace("::", ":");
            if (h.endsWith(":")) h = h.substring(0, h.length() - 1);

            String[] partes = h.split(":");
            int hora = Integer.parseInt(partes[0].trim());
            int min = partes.length > 1 ? Integer.parseInt(partes[1].trim()) : 0;
            return hora * 60 + min;
        } catch (Exception e) {
            return -1;
        }
    }

    private String getNomeDia(int dia) {
        switch (dia) {
            case Calendar.MONDAY: return "Segunda";
            case Calendar.TUESDAY: return "Terça";
            case Calendar.WEDNESDAY: return "Quarta";
            case Calendar.THURSDAY: return "Quinta";
            case Calendar.FRIDAY: return "Sexta";
            default: return "Segunda";
        }
    }

    // ============================================================
    // Navegação
    // ============================================================
    private void configurarNavegacao() {
        findViewById(R.id.cardCardapio).setOnClickListener(v -> startActivity(new Intent(this, CardapioActivity.class)));
        findViewById(R.id.cardHorarios).setOnClickListener(v -> startActivity(new Intent(this, HorariosActivity.class)));
        findViewById(R.id.cardTrabalhos).setOnClickListener(v -> startActivity(new Intent(this, TrabalhosActivity.class)));
        findViewById(R.id.cardCalendario).setOnClickListener(v -> startActivity(new Intent(this, CalendarioActivity.class)));
        findViewById(R.id.cardProfessores).setOnClickListener(v -> startActivity(new Intent(this, ProfessoresActivity.class)));
        findViewById(R.id.cardInstagram).setOnClickListener(v -> startActivity(new Intent(this, InstagramActivity.class)));
        findViewById(R.id.cardNotas).setOnClickListener(v -> startActivity(new Intent(this, NotasActivity.class)));

        LinearLayout navTrabalhos = findViewById(R.id.navTrabalhos);
        LinearLayout navHorarios = findViewById(R.id.navHorarios);
        LinearLayout navInicio = findViewById(R.id.navInicio);
        LinearLayout navCardapio = findViewById(R.id.navCardapio);
        LinearLayout navProfessores = findViewById(R.id.navProfessores);

        if (navTrabalhos != null) navTrabalhos.setOnClickListener(v -> startActivity(new Intent(this, TrabalhosActivity.class)));
        if (navHorarios != null) navHorarios.setOnClickListener(v -> startActivity(new Intent(this, HorariosActivity.class)));
        if (navInicio != null) navInicio.setOnClickListener(v -> { /* já estamos aqui */ });
        if (navCardapio != null) navCardapio.setOnClickListener(v -> startActivity(new Intent(this, CardapioActivity.class)));
        if (navProfessores != null) navProfessores.setOnClickListener(v -> startActivity(new Intent(this, ProfessoresActivity.class)));
    }

    private void abrirDialogoEditarPerfil() {
        String nomeAtual = prefs.getString("nome_usuario", "");
        String turmaAtualPref = prefs.getString("turma_usuario", "");

        android.widget.EditText inputNome = new android.widget.EditText(this);
        inputNome.setText(nomeAtual);
        inputNome.setHint("Digite seu nome");

        android.widget.Spinner spinnerTurma = new android.widget.Spinner(this);
        List<Sala> turmas = DataHelper.getTurmas();
        android.widget.ArrayAdapter<Sala> adapter = new android.widget.ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, turmas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTurma.setAdapter(adapter);

        for (int i = 0; i < turmas.size(); i++) {
            if (turmas.get(i).getNome().equals(turmaAtualPref)) {
                spinnerTurma.setSelection(i);
                break;
            }
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
                    prefs.edit()
                            .putString("nome_usuario", novoNome)
                            .putString("turma_usuario", novaTurma.getNome())
                            .apply();
                    txtNomeAluno.setText(novoNome);
                    txtTurma.setText(novaTurma.getNome());
                    atualizarProximaAula();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}