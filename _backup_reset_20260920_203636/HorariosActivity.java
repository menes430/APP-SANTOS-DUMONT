package com.santosdumont.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
    private String diaAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horarios);

        Spinner spDia = findViewById(R.id.sp_dia_horario);
        rvHorarios = findViewById(R.id.rv_horarios);
        rvHorarios.setLayoutManager(new LinearLayoutManager(this));

        String[] dias = {"Segunda", "Terça", "Quarta", "Quinta", "Sexta"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dias);
        spDia.setAdapter(adapter);

        int diaSemana = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
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
            @Override public void onItemSelected(AdapterView<?> p, View v, int position, long id) {
                diaAtual = dias[position];
                carregarHorarios(diaAtual);
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    private void carregarHorarios(String dia) {
        SharedPreferences prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String turma = prefs.getString("turma_usuario", "1A MAT");
        List<Aula> aulas = DataHelper.getHorarios(this, turma, dia);

        HorarioAdapter adapter = new HorarioAdapter(aulas, (aula, posicao) -> abrirMenuAula(aula, posicao));
        rvHorarios.setAdapter(adapter);
    }

    private void abrirMenuAula(Aula aula, int posicao) {
        new AlertDialog.Builder(this)
                .setTitle(aula.getHorario() + " — " + (aula.isSemAula() ? "Sem aula" : aula.getDisciplina()))
                .setItems(new String[]{"Editar", "Resetar"}, (d, w) -> {
                    if (w == 0) abrirDialogoEdicao(aula, posicao);
                    else resetarAula(aula, posicao);
                })
                .show();
    }

    private void abrirDialogoEdicao(Aula aula, int posicao) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 32, 48, 32);

        CheckBox cbSemAula = new CheckBox(this);
        cbSemAula.setText("Sem aula");
        cbSemAula.setChecked(aula.isSemAula());

        CheckBox cbTextoLivre = new CheckBox(this);
        cbTextoLivre.setText("Texto livre (sem professor)");
        cbTextoLivre.setChecked(aula.isTextoLivre());

        EditText edtMateria = new EditText(this);
        edtMateria.setHint("Matéria");
        edtMateria.setText(aula.getDisciplina());

        EditText edtProfessor = new EditText(this);
        edtProfessor.setHint("Professor");
        edtProfessor.setText(aula.getProfessor());

        layout.addView(cbSemAula);
        layout.addView(cbTextoLivre);
        layout.addView(edtMateria);
        layout.addView(edtProfessor);

        // Regras de habilitação
        Runnable aplicarRegras = () -> {
            boolean semAula = cbSemAula.isChecked();
            boolean textoLivre = cbTextoLivre.isChecked();
            edtMateria.setEnabled(!semAula);
            edtProfessor.setEnabled(!semAula && !textoLivre);
            if (semAula) {
                edtMateria.setText("");
                edtProfessor.setText("");
            }
        };
        aplicarRegras.run();

        // Bloquear marcar os dois juntos
        cbSemAula.setOnCheckedChangeListener((v, isChecked) -> {
            if (isChecked && cbTextoLivre.isChecked()) cbTextoLivre.setChecked(false);
            aplicarRegras.run();
        });
        cbTextoLivre.setOnCheckedChangeListener((v, isChecked) -> {
            if (isChecked && cbSemAula.isChecked()) cbSemAula.setChecked(false);
            aplicarRegras.run();
        });

        new AlertDialog.Builder(this)
                .setTitle("Editar aula — " + aula.getHorario())
                .setView(layout)
                .setPositiveButton("Salvar", (d, w) -> {
                    Aula nova = new Aula(
                            aula.getHorario(),
                            edtMateria.getText().toString().trim(),
                            edtProfessor.getText().toString().trim(),
                            cbSemAula.isChecked(),
                            cbTextoLivre.isChecked()
                    );
                    DataHelper.atualizarAula(this, diaAtual, posicao, nova);
                    carregarHorarios(diaAtual);
                    Toast.makeText(this, "Aula atualizada", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void resetarAula(Aula aula, int posicao) {
        new AlertDialog.Builder(this)
                .setTitle("Resetar aula?")
                .setMessage("Voltar \"" + aula.getHorario() + "\" ao padrão?")
                .setPositiveButton("Sim", (d, w) -> {
                    DataHelper.resetarAula(this, diaAtual, posicao);
                    carregarHorarios(diaAtual);
                    Toast.makeText(this, "Aula resetada", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Não", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}