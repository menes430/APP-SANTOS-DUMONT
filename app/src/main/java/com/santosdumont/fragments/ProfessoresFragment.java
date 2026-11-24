package com.santosdumont.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.santosdumont.R;
import com.santosdumont.adapters.ProfessorAdapter;
import com.santosdumont.models.HorarioComSala;
import com.santosdumont.models.ProfessorModel;
import com.santosdumont.utils.DataHelper;
import com.santosdumont.utils.ThemePrefs;

import java.util.Calendar;
import java.util.List;

public class ProfessoresFragment extends Fragment {

    private androidx.viewpager2.widget.ViewPager2 vpProf;
    private com.google.android.material.tabs.TabLayout tabDias;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_professores, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (vpProf != null && vpProf.getAdapter() == null) {
        }
        super.onViewCreated(view, savedInstanceState);

        tabDias = view.findViewById(R.id.tab_dias_professor);
        vpProf = view.findViewById(R.id.vp_professores);

        int diaSemana = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        String diaInicial = "Segunda";
        switch (diaSemana) {
            case Calendar.MONDAY: diaInicial = "Segunda"; break;
            case Calendar.TUESDAY: diaInicial = "Terça"; break;
            case Calendar.WEDNESDAY: diaInicial = "Quarta"; break;
            case Calendar.THURSDAY: diaInicial = "Quinta"; break;
            case Calendar.FRIDAY: diaInicial = "Sexta"; break;
        }
        final int posInicial = com.santosdumont.adapters.ProfessorPagerAdapter.posicaoPorDia(diaInicial);

        final com.santosdumont.utils.ColorTheme tema = ThemePrefs.getColorTheme(requireContext());
        final String turnoAtual = requireContext().getSharedPreferences("app_prefs", 0).getString("turno", "manha");
        com.santosdumont.adapters.ProfessorPagerAdapter pagerAdapter =
                ThemePrefs.isPadrao(requireContext())
                        ? new com.santosdumont.adapters.ProfessorPagerAdapter(vpProf,
                            (professor, dia) -> mostrarDetalhes(professor, dia, turnoAtual), turnoAtual)
                        : new com.santosdumont.adapters.ProfessorPagerAdapter(vpProf, tema,
                            (professor, dia) -> mostrarDetalhes(professor, dia, turnoAtual), turnoAtual);
        vpProf.setSaveEnabled(false);
        vpProf.setAdapter(pagerAdapter);
        vpProf.setSaveEnabled(false); // Força recriação ao voltar da RAM

        final String[] ABREV = {"Seg", "Ter", "Qua", "Qui", "Sex"};
        new com.google.android.material.tabs.TabLayoutMediator(tabDias, vpProf, (tab, position) -> {
            tab.setText(ABREV[position]);
        }).attach();

        vpProf.setCurrentItem(posInicial, false);
        vpProf.setOverScrollMode(android.view.View.OVER_SCROLL_NEVER);

        
        // Carrossel (rollback via ThemePrefs.useCarousel)
        if (ThemePrefs.useCarousel(requireContext())) {
            vpProf.setPageTransformer(new com.santosdumont.utils.CarouselPageTransformer());
            vpProf.setOffscreenPageLimit(3);
        } else {
            // N17: Slide + Fade (fallback)
            vpProf.setPageTransformer((page, position) -> {
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

    // N10: carregarProf removido — agora o ProfessorPagerAdapter cuida

    private void mostrarDetalhes(ProfessorModel professor, String dia, String turno) {
        List<HorarioComSala> horarios = DataHelper.getHorariosProfessor(requireContext(), professor.getNome(), dia, turno);

        boolean padrao = ThemePrefs.isPadrao(requireContext());
        int corTexto = padrao ? 0xFFFFFFFF : ThemePrefs.getColorTheme(requireContext()).getTextPrimary();
        int corSec   = padrao ? 0xFFAAB5B2 : ThemePrefs.getColorTheme(requireContext()).getTextSecondary();

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_professor_horarios, null);
        builder.setView(view);

        TextView tvTitulo = view.findViewById(R.id.tv_professor_titulo);
        TextView tvDisciplinas = view.findViewById(R.id.tv_professor_disciplinas_dialog);
        LinearLayout container = view.findViewById(R.id.ll_horarios_container);

        tvTitulo.setText(professor.getNome());
        tvDisciplinas.setText("Disciplinas: " + professor.getDisciplinasStr());

        if (!padrao) {
            tvTitulo.setTextColor(corTexto);
            tvDisciplinas.setTextColor(corSec);
        }

        if (horarios.isEmpty()) {
            TextView vazio = new TextView(requireContext());
            vazio.setText("Nenhum horario neste dia");
            vazio.setPadding(16, 16, 16, 16);
            vazio.setTextColor(corSec);
            container.addView(vazio);
        } else {
            for (HorarioComSala h : horarios) {
                TextView item = new TextView(requireContext());
                item.setText(h.horario + "  " + h.disciplina + "  -  " + h.sala);
                item.setPadding(8, 8, 8, 8);
                item.setTextColor(corTexto);
                item.setTextSize(14);
                container.addView(item);
            }
        }

        builder.setPositiveButton("Fechar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    public void onResume() {
        super.onResume();
        if (vpProf != null && vpProf.getAdapter() == null) {
            // Se a memória matou o adapter, recria a activity
            if (getActivity() != null) {
                getActivity().recreate();
            }
        } else if (vpProf == null) {
            // Se a view foi destruída, também recria
            if (getActivity() != null) {
                getActivity().recreate();
            }
        }

        if (vpProf != null && vpProf.getAdapter() == null) {
            View view = getView();
            if (view != null) {
                onCreateView(getLayoutInflater(), null, null);
            }
        }

        if (vpProf != null && vpProf.getAdapter() != null) {
            vpProf.getAdapter().notifyDataSetChanged();
        }
        // N13: re-aplica tema (cores das tabs) ao voltar pro fragment
        if (getView() != null) aplicarTemaNaTela(getView());
    }

    // ============================================================
    // APLICAR TEMA NA TELA DE PROFESSORES
    // ============================================================
    @Override
    public void onDestroyView() {
        if (vpProf != null) {
            vpProf.setAdapter(null); // Limpa o adapter para evitar conflito ao voltar da RAM
        }
        super.onDestroyView();
    }

    private void aplicarTemaNaTela(View view) {
        if (ThemePrefs.isPadrao(requireContext())) return;
        com.santosdumont.utils.ColorTheme t = ThemePrefs.getColorTheme(requireContext());

        view.setBackgroundColor(t.getBackground());

        android.widget.TextView titulo = view.findViewById(R.id.txtTituloProfessores);
        if (titulo != null) titulo.setTextColor(t.getTextPrimary());

        // N10: TabLayout colorido com o tema
        if (tabDias != null) {
            tabDias.setSelectedTabIndicatorColor(t.getPrimary());
            tabDias.setTabTextColors(t.getTextSecondary(), t.getTextPrimary());
        }

        // N12: degradê lateral removido (páginas coladas durante o swipe)
    }
}
