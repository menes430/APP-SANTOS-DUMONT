package com.santosdumont.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.santosdumont.R;

public class LembreteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lembrete);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        String titulo = getIntent().getStringExtra("titulo");
        String mensagem = getIntent().getStringExtra("mensagem");
        int notifId = getIntent().getIntExtra("notifId", 0);

        TextView tvTitulo = findViewById(R.id.tvLembreteTitulo);
        TextView tvMensagem = findViewById(R.id.tvLembreteMensagem);
        tvTitulo.setText(titulo != null ? titulo : "Lembrete");
        tvMensagem.setText(mensagem != null ? mensagem : "");

        Button btnEntendi = findViewById(R.id.btnEntendi);
        btnEntendi.setOnClickListener(v -> {
            // Cancelar a notificação persistente
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(notifId);
            finish();
        });
    }
}