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
import com.santosdumont.models.Aula;
import com.santosdumont.models.ProfessorModel;
import com.santosdumont.utils.DataHelper;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class ProfessoresActivity extends AppCompatActivity {

    private Spinner spDiaProfessor;
    private RecyclerView rvProfessores;
    private Map<String, List<ProfessorModel>> professoresPorDia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professores);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_professores);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Professores");

        spDiaProfessor = findViewById(R.id.sp_dia_professor);
        rvProfessores = findViewById(R.id.rv_professores);

        rvProfessores.setLayoutManager(new LinearLayoutManager(this));

        professoresPorDia = DataHelper.getProfessoresPorDia();

        String[] dias = {"Segunda", "Terça", "Quarta", "Quinta", "Sexta"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDiaProfessor.setAdapter(adapter);

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

        int posicao = 0;
        for (int i = 0; i < dias.length; i++) {
            if (dias[i].equals(diaAtual)) {
                posicao = i;
                break;
            }
        }
        spDiaProfessor.setSelection(posicao);
        carregarProfessores(diaAtual);

        spDiaProfessor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                carregarProfessores(dias[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void carregarProfessores(String dia) {
        List<ProfessorModel> lista = professoresPorDia.get(dia);
        if (lista != null && !lista.isEmpty()) {
            ProfessorAdapter adapter = new ProfessorAdapter(lista, professor -> {
                mostrarDetalhesProfessor(professor, dia);
            });
            rvProfessores.setAdapter(adapter);
        }
    }

    private void mostrarDetalhesProfessor(ProfessorModel professor, String dia) {
        List<Aula> aulas = DataHelper.getHorariosProfessorPorDia(professor.getNome(), dia);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_professor_horarios, null);
        builder.setView(view);

        TextView tvTitulo = view.findViewById(R.id.tv_professor_titulo);
        TextView tvDisciplinas = view.findViewById(R.id.tv_professor_disciplinas_dialog);
        LinearLayout container = view.findViewById(R.id.ll_horarios_container);

        tvTitulo.setText(professor.getNome());
        tvDisciplinas.setText("Disciplinas: " + professor.getDisciplinasStr());

        if (aulas.isEmpty()) {
            TextView vazio = new TextView(this);
            vazio.setText("Nenhum horário encontrado para este dia");
            vazio.setTextColor(getColor(R.color.text_secondary));
            vazio.setPadding(16, 16, 16, 16);
            container.addView(vazio);
        } else {
            for (Aula aula : aulas) {
                TextView item = new TextView(this);
                item.setText(aula.getHorario() + " - " + aula.getDisciplina());
                item.setTextColor(getColor(R.color.text_primary));
                item.setPadding(8, 8, 8, 8);
                container.addView(item);
            }
        }

        builder.setPositiveButton("Fechar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}