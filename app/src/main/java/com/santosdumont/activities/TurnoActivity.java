package com.santosdumont.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.santosdumont.R;

public class TurnoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turno);

        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);

        View cardManha = findViewById(R.id.cardManha);
        View cardTarde = findViewById(R.id.cardTarde);

        cardManha.setOnClickListener(v -> {
            prefs.edit().putString("turno", "manha").apply();
            irParaLogin();
        });

        cardTarde.setOnClickListener(v -> {
            prefs.edit().putString("turno", "tarde").apply();
            irParaLogin();
        });
    }

    private void irParaLogin() {
        startActivity(new Intent(this, BoasVindasActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }
}