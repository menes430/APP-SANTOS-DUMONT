package com.santosdumont.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.animation.ObjectAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.santosdumont.R;
import com.santosdumont.utils.DataHelper;
import com.santosdumont.utils.NotificationScheduler;

public class LoadingActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "app_prefs";
    private static final String PREF_BOAS_VINDAS = "boas_vindas_mostrada";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);



        NotificationScheduler.criarCanal(this);

        // Migra JSON antigo (Terça -> Terça)
        DataHelper.migrarChavesAntigas(this);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // ============================================
        // LER A FLAG (COM LOG PARA DEBUG)
        // ============================================
        boolean jaMostrou = prefs.getBoolean(PREF_BOAS_VINDAS, false);
        android.util.Log.d("SANTOS_DUMONT", "Boas-vindas já mostrada? " + jaMostrou);

        // Pedir permissão (se ainda não tiver)

        TextView tv1 = findViewById(R.id.tv_funcao_1);
        TextView tv2 = findViewById(R.id.tv_funcao_2);
        TextView tv3 = findViewById(R.id.tv_funcao_3);
        TextView tv4 = findViewById(R.id.tv_funcao_4);
        TextView tv5 = findViewById(R.id.tv_funcao_5);

        // Animacao um por um: cada texto aparece com fade
        final TextView[] textos = {tv1, tv2, tv3, tv4, tv5};
        final String[] labels = {
                "Turmas carregadas",
                "Merenda carregada",
                "Horários carregados",
                "Professores carregados",
                "Notificações prontas"
        };
        // Seta os textos
        for (int i = 0; i < textos.length; i++) {
            if (textos[i] != null) textos[i].setText("✅ " + labels[i]);
        }

        // Executa as cargas + anima cada texto com delay
        new Handler().postDelayed(() -> {
            try { DataHelper.getTurmas(); } catch (Exception e) {}
            animarTexto(textos[0], 0);
            new Handler().postDelayed(() -> {
                try { DataHelper.getCardapioSemana(1); } catch (Exception e) {}
                animarTexto(textos[1], 0);
            }, 300);
            new Handler().postDelayed(() -> {
                try { DataHelper.getHorarios(this, "1A MAT", "Terça"); } catch (Exception e) {}
                animarTexto(textos[2], 0);
            }, 600);
            new Handler().postDelayed(() -> {
                try { DataHelper.getProfessoresDoDia(this, "Segunda"); } catch (Exception e) {}
                animarTexto(textos[3], 0);
            }, 900);
            new Handler().postDelayed(() -> {
                try { NotificationScheduler.agendarNotificacoesDoDia(this); } catch (Exception e) {}
                animarTexto(textos[4], 0);
            }, 1200);

            // ============================================
            // BOAS-VINDAS: SÓ APARECE SE NUNCA MOSTROU
            // ============================================
            boolean jaMostrouAgora = prefs.getBoolean(PREF_BOAS_VINDAS, false);
            if (!jaMostrouAgora) {
                try {
                    NotificationScheduler.enviarBoasVindas(this);
                    // Salvar de forma SÍNCRONA (commit)
                    prefs.edit().putBoolean(PREF_BOAS_VINDAS, true).apply();
                    android.util.Log.d("SANTOS_DUMONT", "Boas-vindas enviada e flag salva!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            new Handler().postDelayed(() -> {
                // Se ja escolheu o turno -> BoasVindas direto
                // Senao -> TurnoActivity primeiro
                String turno = prefs.getString("turno", "");
                if (turno == null || turno.isEmpty()) {
                    startActivity(new Intent(this, TurnoActivity.class));
                } else {
                    startActivity(new Intent(this, BoasVindasActivity.class));
                }
                finish();
            }, 1000);
        }, 1200);
    }

    private void animarTexto(android.view.View v, int delayMs) {
        if (v == null) return;
        v.setAlpha(0f);
        v.setTranslationY(12f);
        v.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(delayMs)
                .setDuration(400)
                .start();
    }

}