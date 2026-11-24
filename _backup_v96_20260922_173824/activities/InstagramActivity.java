package com.santosdumont.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.santosdumont.R;

public class InstagramActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_instagram);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button btnInsta = findViewById(R.id.btn_insta_escola);

        btnInsta.setOnClickListener(v -> abrir("https://www.instagram.com/ee.santosdumont?igsi=bnN0d3ZvYWxhdnI1"));
    }

    private void abrir(String url) {
        try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))); } catch (Exception e) {}
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}