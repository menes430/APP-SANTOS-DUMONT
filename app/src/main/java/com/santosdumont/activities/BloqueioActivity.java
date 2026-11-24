package com.santosdumont.activities;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.santosdumont.R;

import java.util.Random;

public class BloqueioActivity extends AppCompatActivity {

    private static final String[] MENSAGENS = {
            "Ei! Voc\u00ea saiu do app. Foque na aula!",
            "Sua educa\u00e7\u00e3o \u00e9 mais importante que o celular.",
            "Volte a prestar aten\u00e7\u00e3o na aula!",
            "O celular pode esperar. Sua nota, n\u00e3o.",
            "Estudar agora, conquistar depois.",
            "Cada minuto de aula faz diferen\u00e7a.",
            "O professor est\u00e1 explicando. Voc\u00ea est\u00e1 aqui?",
            "Foco! O futuro agradece."
    };

    private static final String[] EMOJIS = {
            "\uD83D\uDCDA", "\uD83C\uDF93", "\u270F\uFE0F",
            "\uD83D\uDCA1", "\uD83C\uDFAF", "\uD83D\uDCDD"
    };

    private static final long AULA_VAGA_MS = 60 * 60 * 1000L;
    private static final String CANAL_ID = "aula_vaga_canal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloqueio);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        Random r = new Random();
        TextView txtMsg = findViewById(R.id.txtBloqueioMsg);
        TextView txtEmoji = findViewById(R.id.txtBloqueioEmoji);
        if (txtMsg != null) txtMsg.setText(MENSAGENS[r.nextInt(MENSAGENS.length)]);
        if (txtEmoji != null) txtEmoji.setText(EMOJIS[r.nextInt(EMOJIS.length)]);

        // Botao "Voltar a estudar"
        findViewById(R.id.btnBloqueioOk).setOnClickListener(v -> finish());

        // Botao "Aula Vaga" (+1h)
        findViewById(R.id.btnAulaVaga).setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
            long fim = System.currentTimeMillis() + AULA_VAGA_MS;
            prefs.edit()
                    .putLong("aula_vaga_fim", fim)
                    .putInt("saidas_consecutivas", 0)   // reseta as saidas
                    .apply();

            // Agenda notificacao de aviso (5 min antes do fim)
            agendarNotificacaoAviso(fim - 3 * 60 * 1000L); // M13: 5 -> 3 min

            Toast.makeText(this, "Aula Vaga ativada por 1 hora. Bom descanso!",
                    Toast.LENGTH_LONG).show();
            finish();
        });

        // Bloqueia botao "voltar"
        getOnBackPressedDispatcher().addCallback(this,
                new androidx.activity.OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() { }
                });
    }

    private void agendarNotificacaoAviso(long quandoMs) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, com.santosdumont.utils.AulaVagaReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 100,
                i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        if (am != null) {
            am.set(AlarmManager.RTC_WAKEUP, quandoMs, pi);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getApplication() instanceof com.santosdumont.SantosDumontApp) {
            ((com.santosdumont.SantosDumontApp) getApplication()).liberarBloqueio();
        }
    }
}