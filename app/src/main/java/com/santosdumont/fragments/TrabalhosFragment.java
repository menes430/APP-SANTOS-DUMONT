package com.santosdumont.fragments;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.santosdumont.R;
import com.santosdumont.models.TrabalhoModel;
import com.santosdumont.utils.LogHelper;
import com.santosdumont.utils.TrabalhoAlarmeHelper;
import com.santosdumont.utils.TrabalhoStorage;

import java.io.File;
import java.util.Calendar;
import java.util.List;

public class TrabalhosFragment extends Fragment {

    private LinearLayout llLista;
    private FloatingActionButton fabAdicionar;
    private Uri imagemUri;
    private String caminhoImagemAtual;
    private ImageView ivPreviewDialog;
    private int anoAtual, mesAtual, diaAtual;
    private int horaAtual, minutoAtual;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galeriaLauncher;
    private ActivityResultLauncher<String> permissaoCameraLauncher;
    // Cache para evitar lag
    private android.app.Dialog dialogCache;
    private View viewCache;
    private android.widget.EditText edtNomeCache, edtDataCache, edtHoraCache, edtDescricaoCache;
    private android.widget.CheckBox cbAlarmeCache, cbDiaCache, cbSemanaCache, cbPosCache;
    private android.widget.Button btnCameraCache, btnGaleriaCache;
    private android.widget.ImageView ivPreviewCache;
    private android.widget.TextView txtTituloCache;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trabalhos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        llLista = view.findViewById(R.id.llListaTrabalhos);
        fabAdicionar = view.findViewById(R.id.fabAdicionar);

        fabAdicionar.setOnClickListener(v -> abrirDialogoEdicao(null));

        Calendar cal = Calendar.getInstance();
        anoAtual = cal.get(Calendar.YEAR);
        mesAtual = cal.get(Calendar.MONTH);
        diaAtual = cal.get(Calendar.DAY_OF_MONTH);
        horaAtual = cal.get(Calendar.HOUR_OF_DAY);
        minutoAtual = cal.get(Calendar.MINUTE);

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK) carregarPreview(caminhoImagemAtual);
                });

        galeriaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            try {
                                requireContext().getContentResolver().takePersistableUriPermission(uri,
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

        permissaoCameraLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                concedida -> {
                    if (concedida) abrirCameraDireto();
                    else mostrarDialogoPermissaoNegada();
                });


        // PRE-CARREGAMENTO DO DIALOG E DA VIEW (Evita lag)
        viewCache = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_novo_trabalho, null);
        dialogCache = new android.app.Dialog(requireContext());
        dialogCache.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        dialogCache.setContentView(R.layout.dialog_geral);
        if (dialogCache.getWindow() != null) {
            dialogCache.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        edtNomeCache = viewCache.findViewById(R.id.edtNomeTrabalho);
        edtDataCache = viewCache.findViewById(R.id.edtDataTrabalho);
        edtHoraCache = viewCache.findViewById(R.id.edtHoraTrabalho);
        edtDescricaoCache = viewCache.findViewById(R.id.edtDescricaoTrabalho);
        cbAlarmeCache = viewCache.findViewById(R.id.cbAlarme);
        cbDiaCache = viewCache.findViewById(R.id.cbDiaSimDiaNao);
        cbSemanaCache = viewCache.findViewById(R.id.cbUltimaSemana);
        cbPosCache = viewCache.findViewById(R.id.cbPosAlarme);
        btnCameraCache = viewCache.findViewById(R.id.btnCamera);
        btnGaleriaCache = viewCache.findViewById(R.id.btnGaleria);
        ivPreviewCache = viewCache.findViewById(R.id.ivPreview);

        txtTituloCache = dialogCache.findViewById(R.id.txtTituloGeral);
        android.widget.FrameLayout frameConteudoCache = dialogCache.findViewById(R.id.frameConteudo);
        
        if (frameConteudoCache != null) {
            if (viewCache.getParent() != null) {
                ((android.view.ViewGroup) viewCache.getParent()).removeView(viewCache);
            }
            frameConteudoCache.addView(viewCache);
        }

        if (dialogCache.getWindow() != null) {
            int width = (int) (requireContext().getResources().getDisplayMetrics().widthPixels * 0.95);
            int maxHeight = (int) (requireContext().getResources().getDisplayMetrics().heightPixels * 0.85);
            dialogCache.getWindow().setLayout(width, maxHeight);
        }

        // Carrega a lista após montar o cache (evita travar a abertura da aba)
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(this::listarTrabalhos, 50);

        aplicarTemaNaTela(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (llLista != null) listarTrabalhos();
    }

    private void abrirDialogoEdicao(TrabalhoModel trabalho) {
        if (dialogCache == null) {
            Toast.makeText(requireContext(), "Aguarde...", Toast.LENGTH_SHORT).show();
            return;
        }

        // Limpa estado anterior
        edtNomeCache.setText("");
        edtDataCache.setText("");
        edtHoraCache.setText("");
        edtDescricaoCache.setText("");
        ivPreviewCache.setVisibility(View.GONE);
        imagemUri = null;

        txtTituloCache.setText(trabalho == null ? "Novo Trabalho" : "Editar Trabalho");

        if (trabalho != null) {
            edtNomeCache.setText(trabalho.nome);
            edtDataCache.setText(trabalho.data);
            edtHoraCache.setText(trabalho.hora);
            edtDescricaoCache.setText(trabalho.descricao);
            cbAlarmeCache.setChecked(trabalho.alarmeAtivo);
            cbDiaCache.setChecked(trabalho.notifDiaSimDiaNao);
            cbSemanaCache.setChecked(trabalho.notifUltimaSemana);
            cbPosCache.setChecked(trabalho.notifPosAlarme);

            if (trabalho.imagemUri != null && !trabalho.imagemUri.isEmpty()) {
                try {
                    imagemUri = Uri.parse(trabalho.imagemUri);
                    ivPreviewCache.setImageURI(imagemUri);
                    ivPreviewCache.setVisibility(View.VISIBLE);
                } catch (Exception ignored) {}
            }
        } else {
            cbAlarmeCache.setChecked(true);
            cbDiaCache.setChecked(true);
            cbSemanaCache.setChecked(true);
            cbPosCache.setChecked(false);
        }

        // Listeners de Data e Hora
        edtDataCache.setOnClickListener(v -> {
            DatePickerDialog dpd = new DatePickerDialog(requireContext(),
                    (dp, year, month, day) -> {
                        edtDataCache.setText(String.format("%02d/%02d/%04d", day, month + 1, year));
                        anoAtual = year; mesAtual = month; diaAtual = day;
                    }, anoAtual, mesAtual, diaAtual);
            dpd.show();
        });

        edtHoraCache.setOnClickListener(v -> {
            TimePickerDialog tpd = new TimePickerDialog(requireContext(),
                    (tp, hour, minute) -> {
                        edtHoraCache.setText(String.format("%02d:%02d", hour, minute));
                        horaAtual = hour; minutoAtual = minute;
                    }, horaAtual, minutoAtual, true);
            tpd.show();
        });

        ivPreviewCache.setOnClickListener(v -> {
            if (imagemUri != null) ampliarImagem(imagemUri);
        });

        btnCameraCache.setOnClickListener(v -> verificarPermissaoCamera());
        btnGaleriaCache.setOnClickListener(v -> abrirGaleria());

        // Configura os botões do dialog_geral
        com.google.android.material.button.MaterialButton btnCancelar = dialogCache.findViewById(R.id.btnCancelarGeral);
        com.google.android.material.button.MaterialButton btnSalvar = dialogCache.findViewById(R.id.btnSalvarGeral);

        if (btnCancelar != null) btnCancelar.setOnClickListener(v -> dialogCache.dismiss());
        if (btnSalvar != null) {
            btnSalvar.setOnClickListener(v -> {
                boolean ok = salvarTrabalho(trabalho, edtNomeCache, edtDataCache, edtHoraCache, edtDescricaoCache,
                        cbAlarmeCache, cbDiaCache, cbSemanaCache, cbPosCache);
                if (ok) {
                    dialogCache.dismiss();
                    listarTrabalhos();
                }
            });
        }

        dialogCache.show();
    }

    private void ampliarImagem(Uri uri) {
        try {
            Intent intent = new Intent(requireContext(), com.santosdumont.activities.ImageViewActivity.class);
            intent.putExtra("imagemUri", uri.toString());
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Erro ao ampliar", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean salvarTrabalho(TrabalhoModel existente, EditText edtNome, EditText edtData,
                                   EditText edtHora, EditText edtDescricao,
                                   CheckBox cbAlarme, CheckBox cbDia,
                                   CheckBox cbSemana, CheckBox cbPos) {
        String nome = edtNome.getText().toString().trim();
        String data = edtData.getText().toString().trim();
        String hora = edtHora.getText().toString().trim();
        String desc = edtDescricao.getText().toString().trim();

        if (nome.isEmpty() || data.isEmpty() || hora.isEmpty()) {
            Toast.makeText(requireContext(), "Preencha nome, data e hora!", Toast.LENGTH_SHORT).show();
            return false;
        }

        long timestamp = TrabalhoAlarmeHelper.parseDataHora(data, hora);
        if (timestamp < System.currentTimeMillis()) {
            Toast.makeText(requireContext(), "Data/hora no passado!", Toast.LENGTH_SHORT).show();
            return false;
        }

        TrabalhoModel t;
        boolean edicao = (existente != null);
        if (edicao) {
            TrabalhoAlarmeHelper.cancelarNotificacoes(requireContext(), existente);
            t = existente;
        } else {
            t = new TrabalhoModel();
            t.id = String.valueOf(System.currentTimeMillis());
        }

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

        if (edicao) TrabalhoStorage.atualizar(requireContext(), t);
        else TrabalhoStorage.adicionar(requireContext(), t);

        if (t.notifDiaSimDiaNao) TrabalhoAlarmeHelper.agendarDiaSimDiaNao(requireContext(), t);
        if (t.notifUltimaSemana) TrabalhoAlarmeHelper.agendarUltimaSemana(requireContext(), t);
        if (t.notifPosAlarme) TrabalhoAlarmeHelper.agendarPosAlarme(requireContext(), t);
        if (t.alarmeAtivo) verificarPermissaoEAgendar(t);

        return true;
    }

    private void verificarPermissaoCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
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
        new AlertDialog.Builder(requireContext())
                .setTitle("Permissao necessaria")
                .setMessage("O app precisa da camera para tirar foto.")
                .setPositiveButton("Abrir config", (d, w) -> {
                    Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.setData(Uri.parse("package:" + requireContext().getPackageName()));
                    startActivity(i);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void abrirCameraDireto() {
        try {
            File pasta = new File(requireContext().getExternalFilesDir(null), "Pictures");
            if (!pasta.exists()) pasta.mkdirs();
            String nomeArq = "trab_" + System.currentTimeMillis() + ".jpg";
            File arquivo = new File(pasta, nomeArq);
            caminhoImagemAtual = arquivo.getAbsolutePath();
            Uri uri = FileProvider.getUriForFile(requireContext(),
                    requireContext().getPackageName() + ".fileprovider", arquivo);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cameraLauncher.launch(intent);
        } catch (Exception e) {
            LogHelper.salvarErro(requireContext(), "CAMERA", "Erro", e);
        }
    }

    private void abrirGaleria() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            galeriaLauncher.launch(intent);
        } catch (Exception e) {
            LogHelper.salvarErro(requireContext(), "GALERIA", "Erro", e);
        }
    }

    private void carregarPreview(String path) {
        try {
            Bitmap bmp = BitmapFactory.decodeFile(path);
            if (ivPreviewDialog != null) {
                ivPreviewDialog.setImageBitmap(bmp);
                ivPreviewDialog.setVisibility(View.VISIBLE);
            }
            imagemUri = Uri.fromFile(new File(path));
        } catch (Exception e) {
            LogHelper.salvarErro(requireContext(), "PREVIEW", "Erro", e);
        }
    }

    private void verificarPermissaoEAgendar(TrabalhoModel t) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager am = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
            if (am != null && !am.canScheduleExactAlarms()) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Permitir alarmes exatos?")
                        .setMessage("Para programar o alarme, o app precisa de permissao.")
                        .setPositiveButton("Permitir", (d, w) -> {
                            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                            intent.setData(Uri.parse("package:" + requireContext().getPackageName()));
                            startActivity(intent);
                        })
                        .setNegativeButton("Agora nao", (d, w) -> mostrarAlarmeNegado())
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
        } catch (Exception e) {
            LogHelper.salvarErro(requireContext(), "ALARME", "Erro", e);
            mostrarAlarmeNegado();
        }
    }

    private void mostrarAlarmeNegado() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Alarme nao programado")
                .setMessage("O trabalho foi salvo, mas o alarme NAO foi programado.")
                .setPositiveButton("Entendi", null)
                .show();
    }

    private void listarTrabalhos() {
        if (llLista == null) return;
        llLista.removeAllViews();
        List<TrabalhoModel> lista = TrabalhoStorage.listar(requireContext());

        if (lista.isEmpty()) {
            TextView tv = new TextView(requireContext());
            tv.setText("Nenhum trabalho cadastrado. Toque no + para adicionar.");
            tv.setTextColor(0xFFAAB5B2);
            tv.setTextSize(16);
            tv.setPadding(16, 32, 16, 16);
            tv.setGravity(android.view.Gravity.CENTER);
            llLista.addView(tv);
            return;
        }

        for (final TrabalhoModel t : lista) {
            LinearLayout card = new LinearLayout(requireContext());
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackgroundResource(R.drawable.bg_editor_card);
            card.setPadding(16, 12, 16, 12);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 12);
            card.setLayoutParams(lp);
            card.setClickable(true);
            card.setFocusable(true);

            // M6: miniatura da imagem no cartao
            if (t.imagemUri != null && !t.imagemUri.isEmpty()) {
                android.widget.ImageView ivThumb = new android.widget.ImageView(requireContext());
                ivThumb.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        (int) (140 * requireContext().getResources().getDisplayMetrics().density)));
                ivThumb.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
                ivThumb.setAdjustViewBounds(true);
                LinearLayout.LayoutParams ivParams = (LinearLayout.LayoutParams) ivThumb.getLayoutParams();
                ivParams.bottomMargin = (int) (8 * requireContext().getResources().getDisplayMetrics().density);
                ivThumb.setLayoutParams(ivParams);
                try {
                    ivThumb.setImageURI(android.net.Uri.parse(t.imagemUri));
                } catch (Exception ignored) {}
                card.addView(ivThumb);
            }

            TextView tvTitulo = new TextView(requireContext());
            tvTitulo.setText(t.nome);
            tvTitulo.setTextColor(com.santosdumont.utils.ThemePrefs.isPadrao(requireContext())
                    ? 0xFFFFFFFF
                    : com.santosdumont.utils.ThemePrefs.getColorTheme(requireContext()).getTextPrimary());
            tvTitulo.setTextSize(16);
            tvTitulo.setTypeface(null, android.graphics.Typeface.BOLD);
            card.addView(tvTitulo);

            TextView tvData = new TextView(requireContext());
            tvData.setText(t.data + " as " + t.hora);
            tvData.setTextColor(com.santosdumont.utils.ThemePrefs.isPadrao(requireContext())
                    ? 0xFFAAB5B2
                    : com.santosdumont.utils.ThemePrefs.getColorTheme(requireContext()).getTextSecondary());
            tvData.setTextSize(14);
            card.addView(tvData);

            if (t.descricao != null && !t.descricao.isEmpty()) {
                TextView tvDesc = new TextView(requireContext());
                tvDesc.setText(t.descricao);
                tvDesc.setTextColor(com.santosdumont.utils.ThemePrefs.isPadrao(requireContext())
                    ? 0xFFAAB5B2
                    : com.santosdumont.utils.ThemePrefs.getColorTheme(requireContext()).getTextSecondary());
                tvDesc.setTextSize(13);
                tvDesc.setPadding(0, 4, 0, 0);
                card.addView(tvDesc);
            }

            card.setOnClickListener(v -> abrirDialogoEdicao(t));

            card.setOnLongClickListener(v -> {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Excluir trabalho?")
                        .setMessage("Deseja excluir \"" + t.nome + "\"?")
                        .setPositiveButton("Sim", (d, w) -> {
                            TrabalhoAlarmeHelper.cancelarNotificacoes(requireContext(), t);
                            TrabalhoStorage.remover(requireContext(), t.id);
                            listarTrabalhos();
                        })
                        .setNegativeButton("Nao", null)
                        .show();
                return true;
            });

            llLista.addView(card);
        }
    }

    // ============================================================
    // APLICAR TEMA NA TELA DE TRABALHOS
    // ============================================================
    private void aplicarTemaNaTela(View view) {
        if (com.santosdumont.utils.ThemePrefs.isPadrao(requireContext())) return;
        com.santosdumont.utils.ColorTheme t = com.santosdumont.utils.ThemePrefs.getColorTheme(requireContext());

        view.setBackgroundColor(t.getBackground());

        android.widget.TextView titulo = view.findViewById(R.id.txtTituloTrabalhos);
        if (titulo != null) titulo.setTextColor(t.getTextPrimary());

        // FAB (+)
        View fab = view.findViewById(R.id.fabAdicionar);
        if (fab instanceof com.google.android.material.floatingactionbutton.FloatingActionButton) {
            ((com.google.android.material.floatingactionbutton.FloatingActionButton) fab)
                    .setBackgroundTintList(android.content.res.ColorStateList.valueOf(t.getPrimary()));
        }

        // Reconstroi a lista com cores do tema
        if (llLista != null) listarTrabalhos();
    }
}