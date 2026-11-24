package com.santosdumont.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.santosdumont.R;
import com.santosdumont.adapters.ProfessorAdapter;
import com.santosdumont.models.HorarioComSala;
import com.santosdumont.models.ProfessorModel;
import com.santosdumont.utils.DataHelper;
import com.santosdumont.utils.ThemeHelper;

import java.util.Calendar;
import java.util.List;

public class ProfessoresActivity extends AppCompatActivity {

    private RecyclerView rvProf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.aplicarTema(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professores);

        Spinner spDia = findViewById(R.id.sp_dia_professor);
        rvProf = findViewById(R.id.rv_professores);
        rvProf.setLayoutManager(new LinearLayoutManager(this));

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
        carregarProf(diaAtual);

        spDia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int position, long id) {
                carregarProf(dias[position]);
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    private void carregarProf(String dia) {
        List<ProfessorModel> lista = DataHelper.getProfessoresDoDia(this, dia);
        if (lista == null || lista.isEmpty()) return;
        ProfessorAdapter adapter = new ProfessorAdapter(lista, professor -> mostrarDetalhes(professor, dia));
        rvProf.setAdapter(adapter);
    }

    private void mostrarDetalhes(ProfessorModel professor, String dia) {
        // Horários já vêm ordenados (7:00, 8:00, 9:00, 9:50-10:10, 10:10, 11:00, 11:50)
        List<HorarioComSala> horarios = DataHelper.getHorariosProfessor(this, professor.getNome(), dia);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_professor_horarios, null);
        builder.setView(view);

        TextView tvTitulo = view.findViewById(R.id.tv_professor_titulo);
        TextView tvDisciplinas = view.findViewById(R.id.tv_professor_disciplinas_dialog);
        LinearLayout container = view.findViewById(R.id.ll_horarios_container);

        tvTitulo.setText(professor.getNome());
        tvDisciplinas.setText("Disciplinas: " + professor.getDisciplinasStr());

        if (horarios.isEmpty()) {
            TextView vazio = new TextView(this);
            vazio.setText("Nenhum horário neste dia");
            vazio.setPadding(16, 16, 16, 16);
            vazio.setTextColor(0xFFAAB5B2);
            container.addView(vazio);
        } else {
            for (HorarioComSala h : horarios) {
                TextView item = new TextView(this);
                item.setText(h.horario + "  " + h.disciplina + "  —  " + h.sala);
                item.setPadding(8, 8, 8, 8);
                item.setTextColor(0xFFFFFFFF);
                item.setTextSize(14);
                container.addView(item);
            }
        }

        builder.setPositiveButton("Fechar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}