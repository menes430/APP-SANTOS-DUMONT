package com.santosdumont.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.santosdumont.R;
import com.santosdumont.adapters.HorarioAdapter;
import com.santosdumont.models.Aula;
import com.santosdumont.utils.DataHelper;
import java.util.Calendar;
import java.util.List;

public class HorariosActivity extends AppCompatActivity {
    private RecyclerView rvHorarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horarios);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_horarios);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Spinner spDia = findViewById(R.id.sp_dia_horario);
        rvHorarios = findViewById(R.id.rv_horarios);

        rvHorarios.setLayoutManager(new LinearLayoutManager(this));
        String[] dias = {"Segunda", "Terça", "Quarta", "Quinta", "Sexta"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dias);
        spDia.setAdapter(adapter);

        int diaSemana = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        String diaAtual;
        switch (diaSemana) {
            case Calendar.MONDAY: diaAtual = "Segunda"; break;
            case Calendar.TUESDAY: diaAtual = "Terça"; break;
            case Calendar.WEDNESDAY: diaAtual = "Quarta"; break;
            case Calendar.THURSDAY: diaAtual = "Quinta"; break;
            case Calendar.FRIDAY: diaAtual = "Sexta"; break;
            default: diaAtual = "Segunda";
        }
        int pos = 0;
        for (int i = 0; i < dias.length; i++) if (dias[i].equals(diaAtual)) pos = i;
        spDia.setSelection(pos);
        carregarHorarios(diaAtual);

        spDia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int position, long id) { carregarHorarios(dias[position]); }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    private void carregarHorarios(String dia) {
        SharedPreferences prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String turma = prefs.getString("turma_usuario", "1A MAT");
        List<Aula> aulas = DataHelper.getHorarios(turma, dia);
        rvHorarios.setAdapter(new HorarioAdapter(aulas));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}