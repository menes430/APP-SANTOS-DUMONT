package com.santosdumont.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.santosdumont.R;
import com.santosdumont.adapters.HorarioAdapter;
import com.santosdumont.models.Aula;
import com.santosdumont.utils.DataHelper;

import java.util.Calendar;
import java.util.List;

public class HorariosFragment extends Fragment {

    private androidx.viewpager2.widget.ViewPager2 vpHorarios;
    private com.google.android.material.tabs.TabLayout tabDias;
    private String diaAtual;

    private static final String SEGUNDA = "Segunda";
    private static final String TERCA = "Terça";
    private static final String QUARTA = "Quarta";
    private static final String QUINTA = "Quinta";
    private static final String SEXTA = "Sexta";
    private static final String[] DIAS = {SEGUNDA, TERCA, QUARTA, QUINTA, SEXTA};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_horarios, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabDias = view.findViewById(R.id.tab_dias_horario);
        vpHorarios = view.findViewById(R.id.vp_horarios);

        int diaSemana = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        switch (diaSemana) {
            case Calendar.MONDAY:    diaAtual = SEGUNDA; break;
            case Calendar.TUESDAY:   diaAtual = TERCA;   break;
            case Calendar.WEDNESDAY: diaAtual = QUARTA;  break;
            case Calendar.THURSDAY:  diaAtual = QUINTA;  break;
            case Calendar.FRIDAY:    diaAtual = SEXTA;   break;
            default:                 diaAtual = SEGUNDA;
        }

        final int posInicial = com.santosdumont.adapters.HorarioPagerAdapter.posicaoPorDia(diaAtual);

        final com.santosdumont.utils.ColorTheme tema = com.santosdumont.utils.ThemePrefs.getColorTheme(requireContext());
        com.santosdumont.adapters.HorarioPagerAdapter pagerAdapter =
                com.santosdumont.utils.ThemePrefs.isPadrao(requireContext())
                        ? new com.santosdumont.adapters.HorarioPagerAdapter(vpHorarios, turmaAtual(),
                            (aula, pos, dia) -> abrirMenuAula(aula, pos, dia))
                        : new com.santosdumont.adapters.HorarioPagerAdapter(vpHorarios, tema, turmaAtual(),
                            (aula, pos, dia) -> abrirMenuAula(aula, pos, dia));
        vpHorarios.setSaveEnabled(false);
        vpHorarios.setAdapter(pagerAdapter);

        final String[] ABREV = {"Seg", "Ter", "Qua", "Qui", "Sex"};
        new com.google.android.material.tabs.TabLayoutMediator(tabDias, vpHorarios, (tab, position) -> {
            tab.setText(ABREV[position]);
        }).attach();

        vpHorarios.setCurrentItem(posInicial, false);
        vpHorarios.setOverScrollMode(android.view.View.OVER_SCROLL_NEVER);
        vpHorarios.setSaveEnabled(false); // Força recriação ao voltar da RAM

        vpHorarios.registerOnPageChangeCallback(new androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position >= 0 && position < DIAS.length) {
                    diaAtual = DIAS[position];
                }
            }
        });

        // Carrossel (rollback via ThemePrefs.useCarousel)
        if (com.santosdumont.utils.ThemePrefs.useCarousel(requireContext())) {
            vpHorarios.setPageTransformer(new com.santosdumont.utils.CarouselPageTransformer());
            vpHorarios.setOffscreenPageLimit(3);
        } else {
            // N17: Slide + Fade (fallback)
            vpHorarios.setPageTransformer((page, position) -> {
                if (position < -1) {
                    page.setAlpha(0f);
                } else if (position <= 1) {
                    page.setAlpha(1f - Math.abs(position));
                    page.setTranslationX(page.getWidth() * -position);
                } else {
                    page.setAlpha(0f);
                }
            });
        }

        aplicarTemaNaTela(view);
    }

    public void onResume() {
        super.onResume();
        if (vpHorarios != null && vpHorarios.getAdapter() == null) {
            if (getActivity() != null) {
                getActivity().recreate();
            }
        } else if (vpHorarios == null) {
            if (getActivity() != null) {
                getActivity().recreate();
            }
        }

        if (vpHorarios != null && vpHorarios.getAdapter() == null) {
            // Recria o ViewPager se voltou da memória e perdeu o adapter
            View view = getView();
            if (view != null) {
                // O setup do ViewPager está no onViewCreated, então chamamos ele de novo
                onCreateView(getLayoutInflater(), null, null);
            }
        }

        if (vpHorarios != null && vpHorarios.getAdapter() != null) {
            vpHorarios.getAdapter().notifyDataSetChanged();
        }
        // N13: re-aplica tema ao voltar pro fragment
        if (getView() != null) aplicarTemaNaTela(getView());
    }

    private String turmaAtual() {
        SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE);
        return prefs.getString("turma_usuario", "1A MAT");
    }

    @Override
    public void onDestroyView() {
        if (vpHorarios != null) {
            vpHorarios.setAdapter(null); // Limpa o adapter
        }
        super.onDestroyView();
    }

    private void abrirMenuAula(Aula aula, int posicao, String dia) {
        String titulo = aula.isSemAula()
                ? aula.getHorario() + " - Sem aula"
                : aula.getHorario() + " - " + aula.getDisciplina();

        new AlertDialog.Builder(requireContext())
                .setTitle(titulo)
                .setItems(new String[]{"Editar", "Resetar"}, (d, w) -> {
                    if (w == 0) abrirDialogoEdicao(aula, posicao, dia);
                    else resetarAula(aula, posicao, dia);
                })
                .show();
    }

    private void abrirDialogoEdicao(Aula aula, int posicao, String dia) {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 32, 48, 32);

        CheckBox cbSemAula = new CheckBox(requireContext());
        cbSemAula.setText("Sem aula");
        cbSemAula.setChecked(aula.isSemAula());

        CheckBox cbTextoLivre = new CheckBox(requireContext());
        cbTextoLivre.setText("Texto livre (sem professor)");
        cbTextoLivre.setChecked(aula.isTextoLivre());

        EditText edtMateria = new EditText(requireContext());
        edtMateria.setHint("Materia");
        edtMateria.setText(aula.getDisciplina());

        EditText edtProfessor = new EditText(requireContext());
        edtProfessor.setHint("Professor");
        edtProfessor.setText(aula.getProfessor());

        layout.addView(cbSemAula);
        layout.addView(cbTextoLivre);
        layout.addView(edtMateria);
        layout.addView(edtProfessor);

        Runnable aplicarRegras = () -> {
            boolean semAula = cbSemAula.isChecked();
            boolean textoLivre = cbTextoLivre.isChecked();
            edtMateria.setEnabled(!semAula);
            edtProfessor.setEnabled(!semAula && !textoLivre);
            if (semAula) { edtMateria.setText(""); edtProfessor.setText(""); }
        };
        aplicarRegras.run();

        cbSemAula.setOnCheckedChangeListener((v, isChecked) -> {
            if (isChecked && cbTextoLivre.isChecked()) cbTextoLivre.setChecked(false);
            aplicarRegras.run();
        });
        cbTextoLivre.setOnCheckedChangeListener((v, isChecked) -> {
            if (isChecked && cbSemAula.isChecked()) cbSemAula.setChecked(false);
            aplicarRegras.run();
        });

        new AlertDialog.Builder(requireContext())
                .setTitle("Editar aula - " + aula.getHorario())
                .setView(layout)
                .setPositiveButton("Salvar", (d, w) -> {
                    Aula nova = new Aula(
                            aula.getHorario(),
                            edtMateria.getText().toString().trim(),
                            edtProfessor.getText().toString().trim(),
                            aula.getSala(),
                            cbSemAula.isChecked(),
                            cbTextoLivre.isChecked()
                    );
                    DataHelper.atualizarAula(requireContext(), turmaAtual(), dia, posicao, nova);
                    if (vpHorarios != null && vpHorarios.getAdapter() != null) vpHorarios.getAdapter().notifyDataSetChanged();
                    Toast.makeText(requireContext(), "Aula atualizada", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void resetarAula(Aula aula, int posicao, String dia) {
        Aula padrao = DataHelper.getAulaPadrao(turmaAtual(), dia, posicao);

        String detalhes;
        if (padrao != null) {
            if (padrao.isSemAula()) detalhes = "Sem aula";
            else if (padrao.getProfessor() == null || padrao.getProfessor().isEmpty() || padrao.getProfessor().equals("-"))
                detalhes = padrao.getDisciplina();
            else detalhes = padrao.getDisciplina() + " - " + padrao.getProfessor();
        } else {
            detalhes = "Sem padrao definido";
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Resetar aula?")
                .setMessage("\"" + aula.getHorario() + "\"\n" + detalhes + "\n\nVoltar ao padrao?")
                .setPositiveButton("Sim", (d, w) -> {
                    DataHelper.resetarAula(requireContext(), turmaAtual(), dia, posicao);
                    if (vpHorarios != null && vpHorarios.getAdapter() != null) vpHorarios.getAdapter().notifyDataSetChanged();
                    Toast.makeText(requireContext(), "Aula resetada", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Nao", null)
                .show();
    }

    // ============================================================
    // APLICAR TEMA NA TELA DE HORARIOS (degradê REMOVIDO)
    // ============================================================
    private void aplicarTemaNaTela(View view) {
        if (com.santosdumont.utils.ThemePrefs.isPadrao(requireContext())) return;
        com.santosdumont.utils.ColorTheme t = com.santosdumont.utils.ThemePrefs.getColorTheme(requireContext());
        view.setBackgroundColor(t.getBackground());
        android.widget.TextView titulo = view.findViewById(R.id.txtTituloHorarios);
        if (titulo != null) titulo.setTextColor(t.getTextPrimary());

        if (tabDias != null) {
            tabDias.setSelectedTabIndicatorColor(t.getPrimary());
            tabDias.setTabTextColors(t.getTextSecondary(), t.getTextPrimary());
        }
    }
}
