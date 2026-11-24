package com.santosdumont.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.santosdumont.utils.PresencaHelper;

public class DialogPresencaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String turma = prefs.getString("turma_usuario", "1A MAT");

        new AlertDialog.Builder(this)
                .setTitle("Voc\u00ea foi \u00e0 escola hoje?")
                .setMessage("Isso ajuda a manter sua frequ\u00eancia atualizada.")
                .setCancelable(false)
                .setPositiveButton("Sim, fui!", (d, w) -> {
                    PresencaHelper.salvarResposta(this, false);
                    irParaShell();
                })
                .setNegativeButton("N\u00e3o fui", (d, w) -> {
                    PresencaHelper.salvarResposta(this, true);
                    PresencaHelper.marcarFaltaNasRestantes(this, turma);
                    irParaShell();
                })
                .show();
    }

    private void irParaShell() {
        startActivity(new Intent(this, ShellActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        // Nao permite voltar sem responder
    }
}