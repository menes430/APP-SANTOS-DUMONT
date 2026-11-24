package com.santosdumont.activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.santosdumont.R;

public class ImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        ImageView ivFull = findViewById(R.id.ivFull);
        View btnClose = findViewById(R.id.btnClose);

        if (btnClose != null) {
            btnClose.setOnClickListener(v -> finish());
        }

        String uriStr = getIntent().getStringExtra("imagemUri");
        if (uriStr != null) {
            try {
                Uri uri = Uri.parse(uriStr);
                ivFull.setImageURI(uri);
            } catch (Exception e) {
                Toast.makeText(this, "Erro ao carregar imagem", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        // Tap na imagem -> fechar
        ivFull.setOnClickListener(v -> finish());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}