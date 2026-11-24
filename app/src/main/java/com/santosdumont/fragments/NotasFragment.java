package com.santosdumont.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.santosdumont.R;
import com.santosdumont.utils.DataHelper;

import java.util.ArrayList;
import java.util.List;

public class NotasFragment extends Fragment {

    private static final String PREFS_NAME = "app_prefs";
    private SharedPreferences prefs;
    private LinearLayout llMaterias;
    private TextView tvMaiorMateria, tvMaiorNota, tvPiorMateria, tvPiorNota;
    private String[] MATERIAS = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        llMaterias = view.findViewById(R.id.llMaterias);
        tvMaiorMateria = view.findViewById(R.id.tvMaiorMateria);
        tvMaiorNota = view.findViewById(R.id.tvMaiorNota);
        tvPiorMateria = view.findViewById(R.id.tvPiorMateria);
        tvPiorNota = view.findViewById(R.id.tvPiorNota);

        MATERIAS = gerarMateriasParaTurma();

        for (String materia : MATERIAS) {
            criarItemMateria(materia);
        }

        calcularExtremos();

        // M17: esconde/mostra barra inferior com o teclado
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            boolean imeAberto = insets.isVisible(androidx.core.view.WindowInsetsCompat.Type.ime());
            if (getActivity() instanceof com.santosdumont.activities.ShellActivity) {
                com.santosdumont.activities.ShellActivity shell =
                        (com.santosdumont.activities.ShellActivity) getActivity();
                if (imeAberto) {
                    shell.esconderBottomNav();
                } else {
                    shell.mostrarBottomNav();
                }
            }
            return insets;
        });

        aplicarTemaNaTela(view);
    }

    private String[] gerarMateriasParaTurma() {
        String turma = prefs.getString("turma_usuario", "1A MAT");
        boolean primeiroAno = turma.startsWith("1");

        List<String> lista = new ArrayList<>();
        lista.add("Portugues");
        lista.add("Matematica");
        lista.add("Historia");
        lista.add("Biologia");
        lista.add("Fisica");
        lista.add("Quimica");
        lista.add("Artes");
        lista.add("Ingles");
        lista.add("Educacao Fisica");
        lista.add("Filosofia");
        lista.add("Sociologia");
        lista.add("Geografia");
        lista.add("Projeto de Vida");
        lista.add("Praticas");

        if (primeiroAno) {
            lista.add("TAC");
            lista.add("PIT");
        } else {
            String profAprof = descobrirProfessorAprof(turma);
            String nome = (profAprof != null && !profAprof.isEmpty())
                    ? "Aprofundamento (" + profAprof + ")"
                    : "Aprofundamento";
            lista.add(nome);
        }

        return lista.toArray(new String[0]);
    }

    private String descobrirProfessorAprof(String turma) {
        String[] dias = {"Segunda", "Terça", "Quarta", "Quinta", "Sexta"};
        for (String dia : dias) {
            List<com.santosdumont.models.Aula> aulas = DataHelper.getHorarios(requireContext(), turma, dia);
            for (com.santosdumont.models.Aula a : aulas) {
                if ("APROF".equalsIgnoreCase(a.getDisciplina())) {
                    String p = a.getProfessor();
                    if (p != null && !p.isEmpty() && !"-".equals(p)) {
                        return p;
                    }
                }
            }
        }
        return "";
    }

    private void criarItemMateria(String materia) {
        View item = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_nota, llMaterias, false);

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

        atualizarMateria(materia, edt1, edt2, edt3, edt4, tvSoma, tvMedia);

        llMaterias.addView(item);
    
        // Aplica cores do tema no item
        if (!com.santosdumont.utils.ThemePrefs.isPadrao(requireContext())) {
            com.santosdumont.utils.ColorTheme tt = com.santosdumont.utils.ThemePrefs.getColorTheme(requireContext());
            if (item instanceof com.google.android.material.card.MaterialCardView) {
                ((com.google.android.material.card.MaterialCardView) item).setCardBackgroundColor(tt.getSurface());
            }
            if (tvNome != null) tvNome.setTextColor(tt.getTextPrimary());
            if (tvSoma != null) tvSoma.setTextColor(tt.getTextSecondary());
            android.widget.EditText[] eds = { edt1, edt2, edt3, edt4 };
            for (android.widget.EditText ed : eds) {
                if (ed != null) ed.setTextColor(tt.getTextPrimary());
            }
        }
}

    private void aplicarMascara(EditText edt, String materia, TextView tvSoma, TextView tvMedia, EditText edt1, EditText edt2, EditText edt3, EditText edt4) {
        edt.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) return;
                isUpdating = true;

                String texto = s.toString().replaceAll("[^0-9]", "");

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
                    edt.setText(primeiro + "." + segundo);
                    edt.setSelection(3);
                } else if (texto.length() == 3) {
                    if (texto.equals("100")) {
                        edt.setText("10.0");
                        edt.setSelection(4);
                    } else {
                        edt.setText(texto.substring(0, 1) + "." + texto.substring(1, 2));
                        edt.setSelection(3);
                    }
                } else {
                    edt.setText(texto.substring(0, 3));
                    edt.setSelection(3);
                }

                isUpdating = false;
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
            tvMedia.setText("Media: " + String.format("%.1f", arredondada).replace(".", ","));
            if (arredondada < 6.0) {
                tvMedia.setTextColor(Color.parseColor("#F87171"));
            } else {
                tvMedia.setTextColor(Color.parseColor("#4ADE80"));
            }
        } else {
            tvMedia.setText("Media: --");
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

    // ============================================================
    // APLICAR TEMA NA TELA DE NOTAS
    // ============================================================
    private void aplicarTemaNaTela(View view) {
        if (com.santosdumont.utils.ThemePrefs.isPadrao(requireContext())) return;
        com.santosdumont.utils.ColorTheme t = com.santosdumont.utils.ThemePrefs.getColorTheme(requireContext());

        // Fundo
        view.setBackgroundColor(t.getBackground());

        // Títulos
        android.widget.TextView[] titulos = {
                view.findViewById(R.id.txtTituloNotas),
                view.findViewById(R.id.txtTituloMaterias)
        };
        for (android.widget.TextView tv : titulos) {
            if (tv != null) tv.setTextColor(t.getTextPrimary());
        }

        // Card Maior Nota -> success (tom claro do tema)
        View cardMaior = view.findViewById(R.id.cardMaiorNota);
        if (cardMaior instanceof com.google.android.material.card.MaterialCardView) {
            ((com.google.android.material.card.MaterialCardView) cardMaior)
                    .setCardBackgroundColor(t.getSuccess());
        }

        // Card Pior Nota -> error (tom escuro do tema)
        View cardPior = view.findViewById(R.id.cardPiorNota);
        if (cardPior instanceof com.google.android.material.card.MaterialCardView) {
            ((com.google.android.material.card.MaterialCardView) cardPior)
                    .setCardBackgroundColor(t.getError());
        }
    }
}
