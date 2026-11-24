package com.santosdumont.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.santosdumont.R;

public class TrabalhosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trabalhos);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_trabalhos);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EditText edtNome = findViewById(R.id.edt_nome_trabalho);
        EditText edtData = findViewById(R.id.edt_data_trabalho);
        EditText edtMateria = findViewById(R.id.edt_materia_trabalho);
        Button btnSalvar = findViewById(R.id.btn_salvar_trabalho);

        btnSalvar.setOnClickListener(v -> {
            String nome = edtNome.getText().toString();
            String data = edtData.getText().toString();
            String materia = edtMateria.getText().toString();

            Toast.makeText(this, "Trabalho salvo!\n" + nome + " - " + materia + " - " + data, Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}