package com.santosdumont.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.santosdumont.R;

public class MapaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_mapa);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}