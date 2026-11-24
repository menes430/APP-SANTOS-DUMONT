package com.santosdumont.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.santosdumont.R;
import com.santosdumont.utils.DataHelper;
import com.santosdumont.utils.NotificationScheduler;

public class LoadingActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_NOTIFICACAO = 100;
    private static final String PREFS_NAME = "app_prefs";
    private static final String PREF_BOAS_VINDAS = "boas_vindas_mostrada";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        NotificationScheduler.criarCanal(this);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // ============================================
        // LER A FLAG (COM LOG PARA DEBUG)
        // ============================================
        boolean jaMostrou = prefs.getBoolean(PREF_BOAS_VINDAS, false);
        android.util.Log.d("SANTOS_DUMONT", "Boas-vindas já mostrada? " + jaMostrou);

        // Pedir permissão (se ainda não tiver)
        pedirPermissaoNotificacao();

        TextView tv1 = findViewById(R.id.tv_funcao_1);
        TextView tv2 = findViewById(R.id.tv_funcao_2);
        TextView tv3 = findViewById(R.id.tv_funcao_3);
        TextView tv4 = findViewById(R.id.tv_funcao_4);
        TextView tv5 = findViewById(R.id.tv_funcao_5);

        new Handler().postDelayed(() -> {
            tv1.setText("✅ Turmas carregadas!");
            try { DataHelper.getTurmas(); } catch (Exception e) { return; }
            tv2.setText("✅ Cardápio carregado!");
            try { DataHelper.getCardapioSemana(1); } catch (Exception e) { return; }
            tv3.setText("✅ Horários carregados!");
            try { DataHelper.getHorarios(this, "1A MAT", "Terça"); } catch (Exception e) { return; }
            tv4.setText("✅ Professores carregados!");
            try { DataHelper.getProfessoresDoDia(this, "Segunda"); } catch (Exception e) { return; }
            tv5.setText("✅ Notificações agendadas!");

            try {
                NotificationScheduler.agendarNotificacoesDoDia(this);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // ============================================
            // BOAS-VINDAS: SÓ APARECE SE NUNCA MOSTROU
            // ============================================
            boolean jaMostrouAgora = prefs.getBoolean(PREF_BOAS_VINDAS, false);
            if (!jaMostrouAgora) {
                try {
                    NotificationScheduler.enviarBoasVindas(this);
                    // Salvar de forma SÍNCRONA (commit)
                    prefs.edit().putBoolean(PREF_BOAS_VINDAS, true).commit();
                    android.util.Log.d("SANTOS_DUMONT", "Boas-vindas enviada e flag salva!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            new Handler().postDelayed(() -> {
                startActivity(new Intent(this, BoasVindasActivity.class));
                finish();
            }, 1000);
        }, 1200);
    }

    private void pedirPermissaoNotificacao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_CODE_NOTIFICACAO);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_NOTIFICACAO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                if (!prefs.getBoolean(PREF_BOAS_VINDAS, false)) {
                    NotificationScheduler.enviarBoasVindas(this);
                    prefs.edit().putBoolean(PREF_BOAS_VINDAS, true).commit();
                }
            }
        }
    }
}