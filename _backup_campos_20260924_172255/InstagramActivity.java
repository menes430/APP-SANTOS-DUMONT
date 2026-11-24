package com.santosdumont.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.santosdumont.R;

public class InstagramActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram);

        View btnVoltar = findViewById(R.id.btnVoltarInstagram);
        if (btnVoltar != null) btnVoltar.setOnClickListener(v -> finish());

        Button btnInsta = findViewById(R.id.btn_insta_escola);
        btnInsta.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.instagram.com/ee.santosdumont?igsi=bnN0d3ZvYWxhdnI1")));
            } catch (Exception ignored) {}
        });
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}