package com.santosdumont.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.santosdumont.R;
import java.util.ArrayList;
import java.util.List;

public class NotasActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "app_prefs";
    private SharedPreferences prefs;
    private LinearLayout llMaterias;
    private TextView tvMaiorMateria, tvMaiorNota, tvPiorMateria, tvPiorNota;

    private static final String[] MATERIAS = {
        "Português", "Matemática", "História",
        "Biologia", "Física", "Química",
        "Artes", "Inglês", "Educação Física",
        "Filosofia", "Sociologia", "Geografia",
        "Projeto de Vida", "Aprofundamento", "Práticas",
        "TAC", "PIT", "PDV"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas);

        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        llMaterias = findViewById(R.id.llMaterias);
        tvMaiorMateria = findViewById(R.id.tvMaiorMateria);
        tvMaiorNota = findViewById(R.id.tvMaiorNota);
        tvPiorMateria = findViewById(R.id.tvPiorMateria);
        tvPiorNota = findViewById(R.id.tvPiorNota);

        ImageView btnVoltar = findViewById(R.id.btnVoltarNotas);
        btnVoltar.setOnClickListener(v -> onBackPressed());

        for (String materia : MATERIAS) {
            criarItemMateria(materia);
        }

        calcularExtremos();
    }

    private void criarItemMateria(String materia) {
        View item = LayoutInflater.from(this).inflate(R.layout.item_nota, llMaterias, false);

        TextView tvNome = item.findViewById(R.id.tvMateriaNome);
        EditText edt1 = item.findViewById(R.id.edtBi1);
        EditText edt2 = item.findViewById(R.id.edtBi2);
        EditText edt3 = item.findViewById(R.id.edtBi3);
        EditText edt4 = item.findViewById(R.id.edtBi4);
        TextView tvSoma = item.findViewById(R.id.tvSomaMateria);
        TextView tvMedia = item.findViewById(R.id.tvMediaMateria);

        tvNome.setText(materia);

        edt1.setText(prefs.getString("nota_" + materia + "_1", ""));
        edt2.setText(prefs.getString("nota_" + materia + "_2", ""));
        edt3.setText(prefs.getString("nota_" + materia + "_3", ""));
        edt4.setText(prefs.getString("nota_" + materia + "_4", ""));

        aplicarMascara(edt1, materia, tvSoma, tvMedia, edt1, edt2, edt3, edt4);
        aplicarMascara(edt2, materia, tvSoma, tvMedia, edt1, edt2, edt3, edt4);
        aplicarMascara(edt3, materia, tvSoma, tvMedia, edt1, edt2, edt3, edt4);
        aplicarMascara(edt4, materia, tvSoma, tvMedia, edt1, edt2, edt3, edt4);

        llMaterias.addView(item);
    }

    // ============================================
    // MÁSCARA: ponto automático (com proteção contra vazio)
    // ============================================
    private void aplicarMascara(EditText edt, String materia, TextView tvSoma, TextView tvMedia,
                                 EditText edt1, EditText edt2, EditText edt3, EditText edt4) {
        edt.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) return;
                isUpdating = true;

                String texto = s.toString().replaceAll("[^0-9]", "");

                // ============================================
                // PROTEÇÃO: se estiver vazio, deixa vazio
                // ============================================
                if (texto.isEmpty()) {
                    edt.setText("");
                    edt.setSelection(0);
                    isUpdating = false;
                    atualizarMateria(materia, edt1, edt2, edt3, edt4, tvSoma, tvMedia);
                    return;
                }

                if (texto.length() == 1) {
                    edt.setText(texto);
                    edt.setSelection(1);
                } else if (texto.length() == 2) {
                    String primeiro = texto.substring(0, 1);
                    String segundo = texto.substring(1, 2);
                    String formatado = primeiro + "." + segundo;
                    edt.setText(formatado);
                    edt.setSelection(3);
                } else if (texto.length() == 3) {
                    if (texto.equals("100")) {
                        edt.setText("10.0");
                        edt.setSelection(4);
                    } else {
                        String primeiro = texto.substring(0, 1);
                        String segundo = texto.substring(1, 2);
                        String formatado = primeiro + "." + segundo;
                        edt.setText(formatado);
                        edt.setSelection(3);
                    }
                } else {
                    edt.setText(texto.substring(0, 3));
                    edt.setSelection(3);
                }

                isUpdating = false;

                // Atualizar soma/média
                atualizarMateria(materia, edt1, edt2, edt3, edt4, tvSoma, tvMedia);
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void atualizarMateria(String materia, EditText edt1, EditText edt2, EditText edt3, EditText edt4, TextView tvSoma, TextView tvMedia) {
        double n1 = parseNota(edt1.getText().toString());
        double n2 = parseNota(edt2.getText().toString());
        double n3 = parseNota(edt3.getText().toString());
        double n4 = parseNota(edt4.getText().toString());

        List<Double> notas = new ArrayList<>();
        if (n1 > 0) notas.add(n1);
        if (n2 > 0) notas.add(n2);
        if (n3 > 0) notas.add(n3);
        if (n4 > 0) notas.add(n4);

        double soma = 0;
        for (double n : notas) soma += n;

        tvSoma.setText("Soma: " + String.format("%.1f", soma).replace(".", ","));

        if (notas.size() >= 2) {
            double media = soma / notas.size();
            double arredondada = Math.round(media * 10.0) / 10.0;
            tvMedia.setText("Média: " + String.format("%.1f", arredondada).replace(".", ","));
            if (arredondada < 6.0) {
                tvMedia.setTextColor(Color.parseColor("#F87171"));
            } else {
                tvMedia.setTextColor(Color.parseColor("#4ADE80"));
            }
        } else {
            tvMedia.setText("Média: --");
            tvMedia.setTextColor(Color.parseColor("#AAB5B2"));
        }

        prefs.edit()
                .putString("nota_" + materia + "_1", edt1.getText().toString())
                .putString("nota_" + materia + "_2", edt2.getText().toString())
                .putString("nota_" + materia + "_3", edt3.getText().toString())
                .putString("nota_" + materia + "_4", edt4.getText().toString())
                .apply();

        calcularExtremos();
    }

    private double parseNota(String texto) {
        if (texto == null || texto.isEmpty()) return 0;
        try {
            return Double.parseDouble(texto.replace(",", "."));
        } catch (Exception e) {
            return 0;
        }
    }

    private void calcularExtremos() {
        double maiorNota = -1;
        List<String> maioresMaterias = new ArrayList<>();
        double piorNota = 11;
        List<String> pioresMaterias = new ArrayList<>();

        for (String materia : MATERIAS) {
            double n1 = parseNota(prefs.getString("nota_" + materia + "_1", ""));
            double n2 = parseNota(prefs.getString("nota_" + materia + "_2", ""));
            double n3 = parseNota(prefs.getString("nota_" + materia + "_3", ""));
            double n4 = parseNota(prefs.getString("nota_" + materia + "_4", ""));

            double maiorBimestre = Math.max(Math.max(n1, n2), Math.max(n3, n4));
            if (maiorBimestre <= 0) continue;

            if (maiorBimestre > maiorNota) {
                maiorNota = maiorBimestre;
                maioresMaterias.clear();
                maioresMaterias.add(materia);
            } else if (maiorBimestre == maiorNota) {
                maioresMaterias.add(materia);
            }

            double menorBimestre = 11;
            if (n1 > 0) menorBimestre = Math.min(menorBimestre, n1);
            if (n2 > 0) menorBimestre = Math.min(menorBimestre, n2);
            if (n3 > 0) menorBimestre = Math.min(menorBimestre, n3);
            if (n4 > 0) menorBimestre = Math.min(menorBimestre, n4);

            if (menorBimestre < piorNota && menorBimestre < 11) {
                piorNota = menorBimestre;
                pioresMaterias.clear();
                pioresMaterias.add(materia);
            } else if (menorBimestre == piorNota) {
                pioresMaterias.add(materia);
            }
        }

        if (maiorNota < 0) maiorNota = 0;
        if (piorNota > 10) piorNota = 0;

        tvMaiorMateria.setText(String.join(" e ", maioresMaterias));
        tvMaiorNota.setText(String.format("%.1f", maiorNota).replace(".", ","));
        tvPiorMateria.setText(String.join(" e ", pioresMaterias));
        tvPiorNota.setText(String.format("%.1f", piorNota).replace(".", ","));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}