package com.santosdumont.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.santosdumont.R;
import com.santosdumont.utils.ThemeHelper;

public class AlarmeTelaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.aplicarTema(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarme_tela);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        String titulo = getIntent().getStringExtra("titulo");
        String mensagem = getIntent().getStringExtra("mensagem");
        int notifId = getIntent().getIntExtra("notifId", 0);

        TextView tvTitulo = findViewById(R.id.tvAlarmeTitulo);
        TextView tvMensagem = findViewById(R.id.tvAlarmeMensagem);
        Button btnParar = findViewById(R.id.btnPararAlarme);

        if (titulo != null) tvTitulo.setText(titulo);
        if (mensagem != null) tvMensagem.setText(mensagem);

        btnParar.setOnClickListener(v -> {
            NotificationManager nm = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) nm.cancel(notifId);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        // Não deixa sair pelo botão voltar
    }
}