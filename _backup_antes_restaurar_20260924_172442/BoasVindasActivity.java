package com.santosdumont.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.santosdumont.R;
import com.santosdumont.adapters.SalaAdapter;
import com.santosdumont.models.Sala;
import com.santosdumont.utils.DataHelper;
import java.util.List;

public class BoasVindasActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boas_vindas);

        Spinner spTurma = findViewById(R.id.sp_turma);
        EditText edtNome = findViewById(R.id.edt_nome);
        Button btnContinuar = findViewById(R.id.btn_continuar);

        List<Sala> turmas = DataHelper.getTurmas();
        SalaAdapter adapter = new SalaAdapter(this, turmas);
        spTurma.setAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String nomeSalvo = prefs.getString("nome_usuario", null);
        String turmaSalva = prefs.getString("turma_usuario", null);

        if (nomeSalvo != null && turmaSalva != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        btnContinuar.setOnClickListener(v -> {
            String nome = edtNome.getText().toString().trim();
            Sala turmaSelecionada = (Sala) spTurma.getSelectedItem();

            if (nome.isEmpty()) {
                edtNome.setError("Digite seu nome");
                return;
            }
            if (turmaSelecionada == null) {
                Toast.makeText(this, "Selecione uma turma", Toast.LENGTH_SHORT).show();
                return;
            }

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtNome.getWindowToken(), 0);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("nome_usuario", nome);
            editor.putString("turma_usuario", turmaSelecionada.getNome());
            editor.apply();

            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();
        if (v != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText && !v.getClass().getName().startsWith("android.webkit.")) {
            int[] scrcoords = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];
            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}