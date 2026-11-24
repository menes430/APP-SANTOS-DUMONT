package com.santosdumont.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.santosdumont.R;

public class MapaActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        View btnVoltar = findViewById(R.id.btnVoltarMapa);
        if (btnVoltar != null) btnVoltar.setOnClickListener(v -> finish());
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}