package com.santosdumont.activities;

import android.app.Dialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.santosdumont.R;
import com.santosdumont.models.Sala;
import com.santosdumont.utils.DataHelper;
import com.santosdumont.utils.FirebaseUploader;

import java.util.ArrayList;
import java.util.List;

public class BoasVindasActivity extends AppCompatActivity {

    private MaterialAutoCompleteTextView spTurma;
    private EditText edtNome;
    private Button btnContinuar;
    private Button btnVoltarTurno;
    private SharedPreferences prefs;
    private AlertDialog dialogNomeExiste;
    private Dialog dialogConfirmacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boas_vindas);

        prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);

        // Se o usuário já fez o login anteriormente, pula direto para a próxima tela
        String nomeJaSalvo = prefs.getString("nome_usuario", null);
        String turmaJaSalva = prefs.getString("turma_usuario", null);
        if (nomeJaSalvo != null && !nomeJaSalvo.isEmpty() && turmaJaSalva != null && !turmaJaSalva.isEmpty()) {
            irParaProximaTela();
            return;
        }

        View cardLogin = findViewById(R.id.card_login);
        if (cardLogin != null) {
            cardLogin.setAlpha(0f);
            cardLogin.setTranslationY(40f);
            cardLogin.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(600)
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .start();
        }

        spTurma = findViewById(R.id.sp_turma);
        edtNome = findViewById(R.id.edt_nome);
        btnContinuar = findViewById(R.id.btn_continuar);
        btnVoltarTurno = findViewById(R.id.btn_voltar_turno);

        String turno = prefs.getString("turno", "manha");
        List<Sala> turmas = DataHelper.getTurmasPorTurno(turno);

        List<String> nomesTurmas = new ArrayList<>();
        for (Sala s : turmas) {
            nomesTurmas.add(s.getNome());
        }

        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, nomesTurmas);
        spTurma.setAdapter(adapter);

        String nomeSalvo = prefs.getString("nome_usuario", null);
        if (nomeSalvo != null) edtNome.setText(nomeSalvo);

        btnVoltarTurno.setOnClickListener(v -> {
            Intent intent = new Intent(this, TurnoActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        // Consentimento
        android.widget.CheckBox cbConsentimento = findViewById(R.id.cb_consentimento);
        android.widget.TextView txtSaibaMais = findViewById(R.id.txt_saiba_mais);
        if (txtSaibaMais != null) {
            txtSaibaMais.setOnClickListener(v -> {
                android.app.Dialog dialog = new android.app.Dialog(this);
                dialog.setContentView(R.layout.dialog_consentimento);
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.getWindow().setLayout(
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                android.view.View btnEntendi = dialog.findViewById(R.id.btn_entendi_consentimento);
                if (btnEntendi != null) btnEntendi.setOnClickListener(x -> dialog.dismiss());
                dialog.show();
            });
        }

        btnContinuar.setOnClickListener(v -> {
            // Valida consentimento
            android.widget.CheckBox cb = findViewById(R.id.cb_consentimento);
            if (cb == null || !cb.isChecked()) {
                Toast.makeText(this, "E necessario aceitar a coleta de dados para continuar", Toast.LENGTH_SHORT).show();
                return;
            }
            prefs.edit().putBoolean("consentimento_aceito", true).apply();

            mostrarLoading(); // Mostra o loading antes de verificar no Firestore

            String nome = edtNome.getText().toString().trim();
            Sala turmaSelecionada = buscarTurmaSelecionada(spTurma.getText().toString());

            if (nome.isEmpty()) {
                esconderLoading();
                Toast.makeText(this, "Digite seu nome", Toast.LENGTH_SHORT).show();
                return;
            }
            if (turmaSelecionada == null) {
                esconderLoading();
                Toast.makeText(this, "Selecione uma turma válida", Toast.LENGTH_SHORT).show();
                return;
            }

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(edtNome.getWindowToken(), 0);

            FirebaseUploader.verificarNomeExistente(
                    this, nome, turmaSelecionada.getNome(),
                    (existeOutro, deviceIdExistente) -> runOnUiThread(() -> {
                        if (isFinishing() || isDestroyed()) return;
                        
                        esconderLoading(); // Esconde o loading antes de abrir o popup

                        if (existeOutro) {
                            if (dialogNomeExiste != null && dialogNomeExiste.isShowing()) return;
                            dialogNomeExiste = new AlertDialog.Builder(this)
                                    .setTitle("Nome já existe")
                                    .setMessage("Esse nome já está em uso em outro dispositivo.\nEscolha outro nome.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", (d, w) -> {
                                        edtNome.setText("");
                                        edtNome.requestFocus();
                                    })
                                    .show();
                        } else {
                            mostrarPopupConfirmacao(nome, turmaSelecionada);
                        }
                    }));
        });
    }

    private Sala buscarTurmaSelecionada(String nomeDigitado) {
        if (nomeDigitado == null) return null;
        String alvo = nomeDigitado.trim();
        if (alvo.isEmpty()) return null;
        String turno = prefs.getString("turno", "manha");
        List<Sala> turmas = DataHelper.getTurmasPorTurno(turno);
        for (Sala s : turmas) {
            if (s.getNome().equals(alvo)) return s;
        }
        return null;
    }

    private void mostrarPopupConfirmacao(String nome, Sala turma) {
        if (isFinishing() || isDestroyed()) return;
        if (dialogConfirmacao != null && dialogConfirmacao.isShowing()) return;
        String turno = prefs.getString("turno", "manha");
        String turnoNome = turno.equals("manha") ? "Manh\u00e3" : "Tarde";

        dialogConfirmacao = new Dialog(this);
        dialogConfirmacao.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        dialogConfirmacao.setContentView(R.layout.dialog_confirmacao_info);
        if (dialogConfirmacao.getWindow() != null) {
            dialogConfirmacao.getWindow().setBackgroundDrawable(
                new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        android.widget.TextView txtTurno = dialogConfirmacao.findViewById(R.id.txtTurno);
        android.widget.TextView txtSala  = dialogConfirmacao.findViewById(R.id.txtSala);
        android.widget.TextView txtNome  = dialogConfirmacao.findViewById(R.id.txtNome);
        android.widget.Button btnConfirmar = dialogConfirmacao.findViewById(R.id.btnConfirmarInfo);
        android.widget.Button btnVoltar    = dialogConfirmacao.findViewById(R.id.btnVoltarInfo);

        if (txtTurno != null) txtTurno.setText(turnoNome);
        if (txtSala  != null) txtSala.setText(turma.getNome());
        if (txtNome  != null) txtNome.setText(nome);

        if (btnConfirmar != null) btnConfirmar.setOnClickListener(v -> {
            dialogConfirmacao.dismiss();
            salvarELogar(nome, turma);
        });
        if (btnVoltar != null) btnVoltar.setOnClickListener(v -> dialogConfirmacao.dismiss());

        dialogConfirmacao.setCancelable(true);
        dialogConfirmacao.show();
    }


    private void salvarELogar(String nome, Sala turma) {
        mostrarLoading();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("nome_usuario", nome);
        editor.putString("turma_usuario", turma.getNome());
        editor.putString("nomes_historico", nome);
        editor.apply();

        try {
            String turno = prefs.getString("turno", "manha");
            com.santosdumont.utils.DataCollector.registrar(this, "LOGIN", nome + "|" + turma.getNome() + "|" + turno);
            FirebaseUploader.enviarAgora(this);
        } catch (Exception ignored) {}

        // Tutorial na primeira vez?
        boolean tutorialVisto = prefs.getBoolean("tutorial_visto", false);
        if (!tutorialVisto) {
            android.content.Intent iTut = new android.content.Intent(this, TutorialActivity.class);
            iTut.putExtra("aberto_automatico", true);
            startActivity(iTut);
            finish();
        } else {
            irParaProximaTela();
        }
    }

    private void mostrarLoading() {
        ViewGroup root = (ViewGroup) getWindow().getDecorView();
        android.widget.FrameLayout overlay = new android.widget.FrameLayout(this);
        overlay.setBackgroundColor(0xCC000000);
        overlay.setClickable(true);
        overlay.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        ProgressBar spinner = new ProgressBar(this);
        android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = android.view.Gravity.CENTER;
        spinner.setLayoutParams(params);

        overlay.addView(spinner);
        root.addView(overlay);
    }

    private void esconderLoading() {
        ViewGroup root = (ViewGroup) getWindow().getDecorView();
        // O overlay é sempre o último filho adicionado
        if (root.getChildCount() > 1) {
            View last = root.getChildAt(root.getChildCount() - 1);
            if (last instanceof android.widget.FrameLayout) {
                root.removeView(last);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();
        if (v != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText && !v.getClass().getName().startsWith("android.webkit.")) {
            int[] scrcoords = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];
            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void irParaProximaTela() {
        SharedPreferences p = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String versaoAtual = getString(R.string.app_version);
        String versaoVista = p.getString("versao_vista", "");

        if (!versaoAtual.equals(versaoVista)) {
            startActivity(new Intent(this, ChangelogActivity.class));
            finish();
        } else {
            startActivity(new Intent(this, ShellActivity.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (dialogNomeExiste != null && dialogNomeExiste.isShowing()) dialogNomeExiste.dismiss();
        if (dialogConfirmacao != null && dialogConfirmacao.isShowing()) dialogConfirmacao.dismiss();
        super.onDestroy();
    }
}
