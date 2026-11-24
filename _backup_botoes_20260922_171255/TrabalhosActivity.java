package com.santosdumont.activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.santosdumont.R;
import com.santosdumont.models.TrabalhoModel;
import com.santosdumont.utils.TrabalhoAlarmeHelper;
import com.santosdumont.utils.TrabalhoStorage;

import java.io.File;
import java.util.Calendar;
import java.util.List;

public class TrabalhosActivity extends AppCompatActivity {

    private LinearLayout llLista;
    private TextView btnAdicionar;
    private Uri imagemUri;
    private String caminhoImagemAtual;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galeriaLauncher;
    private ActivityResultLauncher<String> permissaoCameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trabalhos);

        llLista = findViewById(R.id.llListaTrabalhos);
        btnAdicionar = findViewById(R.id.btnAdicionar);

        findViewById(R.id.btnVoltarTrabalhos).setOnClickListener(v -> finish());
        btnAdicionar.setOnClickListener(v -> abrirDialogoNovo());

        // Launcher de câmera
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        carregarPreview(caminhoImagemAtual);
                    }
                });

        // Launcher de galeria
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
                            if (ivPreviewDialog != null) {
                                ivPreviewDialog.setImageURI(uri);
                                ivPreviewDialog.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });

        // Launcher de permissão da câmera
        permissaoCameraLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                concedida -> {
                    if (concedida) {
                        abrirCameraDireto();
                    } else {
                        mostrarDialogoPermissaoNegada();
                    }
                });

        listarTrabalhos();
    }

    private ImageView ivPreviewDialog;

    private void abrirDialogoNovo() {
        // Infla o diálogo
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_novo_trabalho, null);

        EditText edtNome = view.findViewById(R.id.edtNomeTrabalho);
        EditText edtData = view.findViewById(R.id.edtDataTrabalho);
        EditText edtHora = view.findViewById(R.id.edtHoraTrabalho);
        EditText edtDesc = view.findViewById(R.id.edtDescricaoTrabalho);
        ivPreviewDialog = view.findViewById(R.id.ivPreview);
        CheckBox cbAlarme = view.findViewById(R.id.cbAlarme);
        CheckBox cbDia = view.findViewById(R.id.cbDiaSimDiaNao);
        CheckBox cbSemana = view.findViewById(R.id.cbUltimaSemana);
        CheckBox cbPos = view.findViewById(R.id.cbPosAlarme);
        Button btnCamera = view.findViewById(R.id.btnCamera);
        Button btnGaleria = view.findViewById(R.id.btnGaleria);

        // Máscaras
        aplicarMascaraData(edtData);
        aplicarMascaraHora(edtHora);

        // Reset
        imagemUri = null;
        caminhoImagemAtual = null;

        // Ações
        btnCamera.setOnClickListener(v -> verificarPermissaoCamera());
        btnGaleria.setOnClickListener(v -> abrirGaleria());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.v9_novo_trabalho)
                .setView(view)
                .setPositiveButton(R.string.v9_salvar_trabalho, null)
                .setNegativeButton(R.string.v9_cancelar, null)
                .create();

        dialog.show();

        // Sobrescreve o botão Salvar (pra não fechar se der erro)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            boolean ok = salvarTrabalho(
                    edtNome, edtData, edtHora, edtDesc,
                    cbAlarme, cbDia, cbSemana, cbPos);
            if (ok) {
                dialog.dismiss();
                listarTrabalhos();
            }
        });
    }

    private void aplicarMascaraData(EditText edt) {
        edt.addTextChangedListener(new TextWatcher() {
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
                edt.setText(f);
                edt.setSelection(f.length());
                isUpdating = false;
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void aplicarMascaraHora(EditText edt) {
        edt.addTextChangedListener(new TextWatcher() {
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
                edt.setText(f);
                edt.setSelection(f.length());
                isUpdating = false;
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    // ============================================================
    // PERMISSÃO DE CÂMERA
    // ============================================================
    private void verificarPermissaoCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                abrirCameraDireto();
            } else {
                permissaoCameraLauncher.launch(Manifest.permission.CAMERA);
            }
        } else {
            abrirCameraDireto();
        }
    }

    private void mostrarDialogoPermissaoNegada() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.v9_permissao_camera)
                .setMessage(R.string.v9_permissao_camera_msg)
                .setPositiveButton(R.string.v9_abrir_config, (d, w) -> {
                    Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(i);
                })
                .setNegativeButton(R.string.v9_cancelar, null)
                .show();
    }

    // ============================================================
    // CÂMERA
    // ============================================================
    private void abrirCameraDireto() {
        try {
            File pasta = new File(getExternalFilesDir(null), "Pictures");
            if (!pasta.exists()) pasta.mkdirs();

            String nomeArq = "trab_" + System.currentTimeMillis() + ".jpg";
            File arquivo = new File(pasta, nomeArq);
            caminhoImagemAtual = arquivo.getAbsolutePath();

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
            Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
            if (ivPreviewDialog != null) {
                ivPreviewDialog.setImageBitmap(bmp);
                ivPreviewDialog.setVisibility(View.VISIBLE);
            }
            imagemUri = Uri.fromFile(new File(path));
        } catch (Exception ignored) {}
    }

    // ============================================================
    // SALVAR
    // ============================================================
    private boolean salvarTrabalho(EditText edtNome, EditText edtData, EditText edtHora,
                                   EditText edtDesc, CheckBox cbAlarme,
                                   CheckBox cbDia, CheckBox cbSemana, CheckBox cbPos) {
        String nome = edtNome.getText().toString().trim();
        String data = edtData.getText().toString().trim();
        String hora = edtHora.getText().toString().trim();
        String desc = edtDesc.getText().toString().trim();

        if (nome.isEmpty() || data.isEmpty() || hora.isEmpty()) {
            Toast.makeText(this, getString(R.string.v9_erro_campos), Toast.LENGTH_SHORT).show();
            return false;
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
            return false;
        }

        if (timestamp < System.currentTimeMillis()) {
            Toast.makeText(this, getString(R.string.v9_erro_data), Toast.LENGTH_SHORT).show();
            return false;
        }

        TrabalhoModel t = new TrabalhoModel();
        t.id = String.valueOf(System.currentTimeMillis());
        t.nome = nome;
        t.data = data;
        t.hora = hora;
        t.descricao = desc;
        t.imagemUri = imagemUri != null ? imagemUri.toString() : null;
        t.alarmeAtivo = cbAlarme.isChecked();
        t.notifDiaSimDiaNao = cbDia.isChecked();
        t.notifUltimaSemana = cbSemana.isChecked();
        t.notifPosAlarme = cbPos.isChecked();
        t.diasPosAlarme = 3;

        TrabalhoStorage.adicionar(this, t);

        if (t.notifDiaSimDiaNao) TrabalhoAlarmeHelper.agendarDiaSimDiaNao(this, t);
        if (t.notifUltimaSemana) TrabalhoAlarmeHelper.agendarUltimaSemana(this, t);
        if (t.notifPosAlarme) TrabalhoAlarmeHelper.agendarPosAlarme(this, t);

        if (t.alarmeAtivo) {
            verificarPermissaoEAgendar(t);
        }

        Toast.makeText(this, getString(R.string.v9_salvo_ok), Toast.LENGTH_SHORT).show();
        return true;
    }

    private void verificarPermissaoEAgendar(TrabalhoModel t) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (am != null && !am.canScheduleExactAlarms()) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.v9_permitir_alarme_titulo)
                        .setMessage(R.string.v9_permitir_alarme_msg)
                        .setPositiveButton(R.string.v9_permitir, (d, w) -> {
                            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
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

    // ============================================================
    // LISTAR
    // ============================================================
    private void listarTrabalhos() {
        llLista.removeAllViews();
        List<TrabalhoModel> lista = TrabalhoStorage.listar(this);

        if (lista.isEmpty()) {
            TextView tv = new TextView(this);
            tv.setText(R.string.v9_nenhum_trabalho);
            tv.setTextColor(0xFFAAB5B2);
            tv.setTextSize(16);
            tv.setPadding(16, 32, 16, 16);
            tv.setGravity(android.view.Gravity.CENTER);
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
            tvTitulo.setTypeface(null, android.graphics.Typeface.BOLD);
            card.addView(tvTitulo);

            TextView tvData = new TextView(this);
            tvData.setText("⏰ " + t.data + " às " + t.hora);
            tvData.setTextColor(0xFFAAB5B2);
            tvData.setTextSize(14);
            card.addView(tvData);

            if (t.descricao != null && !t.descricao.isEmpty()) {
                TextView tvDesc = new TextView(this);
                tvDesc.setText(t.descricao);
                tvDesc.setTextColor(0xFFAAB5B2);
                tvDesc.setTextSize(13);
                tvDesc.setPadding(0, 4, 0, 0);
                card.addView(tvDesc);
            }

            card.setOnLongClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Excluir trabalho?")
                        .setMessage("Deseja excluir \"" + t.nome + "\"?")
                        .setPositiveButton("Sim", (d, w) -> {
                            List<TrabalhoModel> atuais = TrabalhoStorage.listar(this);
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