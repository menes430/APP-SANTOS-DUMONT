package com.santosdumont.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.santosdumont.R;
import com.santosdumont.models.TrabalhoModel;
import com.santosdumont.utils.ThemeHelper;
import com.santosdumont.utils.TrabalhoAlarmeHelper;
import com.santosdumont.utils.TrabalhoStorage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TrabalhosActivity extends AppCompatActivity {

    private EditText edtNome, edtData, edtHora, edtDescricao;
    private ImageView ivPreview;
    private CheckBox cbAlarme, cbDiaSimDiaNao, cbUltimaSemana, cbPosAlarme;
    private LinearLayout llLista;
    private Uri imagemUri;
    private String caminhoImagemAtual;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galeriaLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trabalhos);

        edtNome = findViewById(R.id.edtNomeTrabalho);
        edtData = findViewById(R.id.edtDataTrabalho);
        edtHora = findViewById(R.id.edtHoraTrabalho);
        edtDescricao = findViewById(R.id.edtDescricaoTrabalho);
        ivPreview = findViewById(R.id.ivPreview);
        cbAlarme = findViewById(R.id.cbAlarme);
        cbDiaSimDiaNao = findViewById(R.id.cbDiaSimDiaNao);
        cbUltimaSemana = findViewById(R.id.cbUltimaSemana);
        cbPosAlarme = findViewById(R.id.cbPosAlarme);
        llLista = findViewById(R.id.llListaTrabalhos);

        findViewById(R.id.btnVoltarTrabalhos).setOnClickListener(v -> finish());

        configurarMascaras();

        // Launchers
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        carregarPreview(caminhoImagemAtual);
                    }
                });

        galeriaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            try {
                                getContentResolver().takePersistableUriPermission(uri,
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            } catch (Exception ignored) {}
                            imagemUri = uri;
                            ivPreview.setImageURI(uri);
                            ivPreview.setVisibility(View.VISIBLE);
                        }
                    }
                });

        ((Button) findViewById(R.id.btnCamera)).setOnClickListener(v -> abrirCamera());
        ((Button) findViewById(R.id.btnGaleria)).setOnClickListener(v -> abrirGaleria());
        ((Button) findViewById(R.id.btnSalvarTrabalho)).setOnClickListener(v -> salvarTrabalho());

        listarTrabalhos();
    }

    private void configurarMascaras() {
        edtData.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) return;
                isUpdating = true;
                String digits = s.toString().replaceAll("[^0-9]", "");
                String f = "";
                if (digits.length() >= 1) f += digits.substring(0, 1);
                if (digits.length() >= 2) f += digits.substring(1, 2);
                if (digits.length() >= 3) f += "/" + digits.substring(2, 3);
                if (digits.length() >= 4) f += digits.substring(3, 4);
                if (digits.length() >= 5) f += "/" + digits.substring(4, 5);
                if (digits.length() >= 6) f += digits.substring(5, 6);
                edtData.setText(f);
                edtData.setSelection(f.length());
                isUpdating = false;
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        edtHora.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) return;
                isUpdating = true;
                String digits = s.toString().replaceAll("[^0-9]", "");
                String f = "";
                if (digits.length() >= 1) f += digits.substring(0, 1);
                if (digits.length() >= 2) f += digits.substring(1, 2);
                if (digits.length() >= 3) f += ":" + digits.substring(2, 3);
                if (digits.length() >= 4) f += digits.substring(3, 4);
                edtHora.setText(f);
                edtHora.setSelection(f.length());
                isUpdating = false;
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

        private void abrirCamera() {
        try {
            // Cria arquivo temporário
            File pasta = new File(getExternalFilesDir(null), "Pictures");
            if (!pasta.exists()) pasta.mkdirs();

            String nomeArq = "trab_" + System.currentTimeMillis() + ".jpg";
            File arquivo = new File(pasta, nomeArq);
            caminhoImagemAtual = arquivo.getAbsolutePath();

            // Pega URI via FileProvider
            Uri uri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    arquivo
            );

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            cameraLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao abrir câmera: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        galeriaLauncher.launch(intent);
    }

    private void carregarPreview(String path) {
        try {
            Bitmap bmp = BitmapFactory.decodeFile(path);
            ivPreview.setImageBitmap(bmp);
            ivPreview.setVisibility(View.VISIBLE);
            imagemUri = Uri.fromFile(new File(path));
        } catch (Exception ignored) {}
    }

    private void salvarTrabalho() {
        String nome = edtNome.getText().toString().trim();
        String data = edtData.getText().toString().trim();
        String hora = edtHora.getText().toString().trim();
        String desc = edtDescricao.getText().toString().trim();

        if (nome.isEmpty() || data.isEmpty() || hora.isEmpty()) {
            Toast.makeText(this, getString(R.string.v9_erro_campos), Toast.LENGTH_SHORT).show();
            return;
        }

        long timestamp;
        try {
            String[] d = data.split("/");
            String[] h = hora.split(":");
            Calendar cal = Calendar.getInstance();
            int ano = Integer.parseInt(d[2]);
            if (ano < 100) ano += 2000;
            cal.set(ano, Integer.parseInt(d[1]) - 1, Integer.parseInt(d[0]),
                    Integer.parseInt(h[0]), Integer.parseInt(h[1]), 0);
            timestamp = cal.getTimeInMillis();
        } catch (Exception e) {
            Toast.makeText(this, "Data inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        if (timestamp < System.currentTimeMillis()) {
            Toast.makeText(this, getString(R.string.v9_erro_data), Toast.LENGTH_SHORT).show();
            return;
        }

        TrabalhoModel t = new TrabalhoModel();
        t.id = String.valueOf(System.currentTimeMillis());
        t.nome = nome;
        t.data = data;
        t.hora = hora;
        t.descricao = desc;
        t.imagemUri = imagemUri != null ? imagemUri.toString() : null;
        t.alarmeAtivo = cbAlarme.isChecked();
        t.notifDiaSimDiaNao = cbDiaSimDiaNao.isChecked();
        t.notifUltimaSemana = cbUltimaSemana.isChecked();
        t.notifPosAlarme = cbPosAlarme.isChecked();
        t.diasPosAlarme = 3;

        TrabalhoStorage.adicionar(this, t);

        // Agendar notificações
        if (t.notifDiaSimDiaNao) TrabalhoAlarmeHelper.agendarDiaSimDiaNao(this, t);
        if (t.notifUltimaSemana) TrabalhoAlarmeHelper.agendarUltimaSemana(this, t);
        if (t.notifPosAlarme) TrabalhoAlarmeHelper.agendarPosAlarme(this, t);

        // Alarme no app Relógio
        if (t.alarmeAtivo) {
            verificarPermissaoEAgendar(t);
        }

        Toast.makeText(this, getString(R.string.v9_salvo_ok), Toast.LENGTH_SHORT).show();
        limparFormulario();
        listarTrabalhos();
    }

    private void verificarPermissaoEAgendar(TrabalhoModel t) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (am != null && !am.canScheduleExactAlarms()) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.v9_permitir_alarme_titulo)
                        .setMessage(R.string.v9_permitir_alarme_msg)
                        .setPositiveButton(R.string.v9_permitir, (d, w) -> {
                            Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            startActivity(intent);
                        })
                        .setNegativeButton(R.string.v9_negar, (d, w) -> mostrarAlarmeNegado())
                        .show();
                return;
            }
        }
        programarAlarme(t);
    }

    private void programarAlarme(TrabalhoModel t) {
        try {
            Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
            intent.putExtra(AlarmClock.EXTRA_MESSAGE, t.nome);
            String[] h = t.hora.split(":");
            intent.putExtra(AlarmClock.EXTRA_HOUR, Integer.parseInt(h[0]));
            intent.putExtra(AlarmClock.EXTRA_MINUTES, Integer.parseInt(h[1]));
            intent.putExtra(AlarmClock.EXTRA_SKIP_UI, false);

            // Dias: seg, qua, sex (3x semana)
            intent.putExtra(AlarmClock.EXTRA_DAYS, new java.util.ArrayList<Integer>() {{
                add(Calendar.MONDAY);
                add(Calendar.WEDNESDAY);
                add(Calendar.FRIDAY);
            }});

            startActivity(intent);
            Toast.makeText(this, getString(R.string.v9_alarme_agendado), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            mostrarAlarmeNegado();
        }
    }

    private void mostrarAlarmeNegado() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.v9_alarme_negado_titulo)
                .setMessage(R.string.v9_alarme_negado_msg)
                .setPositiveButton(R.string.v9_entendi, null)
                .show();
    }

    private void limparFormulario() {
        edtNome.setText("");
        edtData.setText("");
        edtHora.setText("");
        edtDescricao.setText("");
        ivPreview.setVisibility(View.GONE);
        imagemUri = null;
        caminhoImagemAtual = null;
    }

    private void listarTrabalhos() {
        llLista.removeAllViews();
        java.util.List<TrabalhoModel> lista = TrabalhoStorage.listar(this);
        if (lista.isEmpty()) {
            TextView tv = new TextView(this);
            tv.setText(R.string.v9_trabalho_ausente);
            tv.setTextColor(0xFFAAB5B2);
            tv.setPadding(16, 16, 16, 16);
            llLista.addView(tv);
            return;
        }
        for (TrabalhoModel t : lista) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackgroundResource(R.drawable.bg_editor_card);
            card.setPadding(16, 12, 16, 12);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 12);
            card.setLayoutParams(lp);

            TextView tvTitulo = new TextView(this);
            tvTitulo.setText("📚 " + t.nome);
            tvTitulo.setTextColor(0xFFFFFFFF);
            tvTitulo.setTextSize(16);
            card.addView(tvTitulo);

            TextView tvData = new TextView(this);
            tvData.setText("Entrega: " + t.data + " às " + t.hora);
            tvData.setTextColor(0xFFAAB5B2);
            tvData.setTextSize(14);
            card.addView(tvData);

            card.setOnLongClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Excluir trabalho?")
                        .setMessage("Deseja excluir \"" + t.nome + "\"?")
                        .setPositiveButton("Sim", (d, w) -> {
                            java.util.List<TrabalhoModel> atuais = TrabalhoStorage.listar(this);
                            atuais.removeIf(x -> x.id.equals(t.id));
                            TrabalhoStorage.salvar(this, atuais);
                            listarTrabalhos();
                        })
                        .setNegativeButton("Não", null)
                        .show();
                return true;
            });

            llLista.addView(card);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}