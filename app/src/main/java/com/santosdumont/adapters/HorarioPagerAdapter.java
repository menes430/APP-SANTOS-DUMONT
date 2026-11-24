package com.santosdumont.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.santosdumont.R;
import com.santosdumont.models.Aula;
import com.santosdumont.utils.DataHelper;

import java.util.List;

/**
 * N9v2: Adapter do ViewPager2 para o Horarios.
 * Cada pagina = 1 dia (Seg..Sex) com RecyclerView vertical.
 */
public class HorarioPagerAdapter extends RecyclerView.Adapter<HorarioPagerAdapter.DiaViewHolder> {

    public interface OnAulaClickDia {
        void onClick(Aula aula, int posicao, String dia);
    }

    private static final String[] DIAS = {"Segunda", "Terça", "Quarta", "Quinta", "Sexta"};

    private final ViewPager2 viewPager;
    private final com.santosdumont.utils.ColorTheme theme;
    private final String turma;
    private final OnAulaClickDia listener;

    public HorarioPagerAdapter(ViewPager2 viewPager, String turma, OnAulaClickDia listener) {
        this.viewPager = viewPager;
        this.theme = null;
        this.turma = turma;
        this.listener = listener;
    }

    public HorarioPagerAdapter(ViewPager2 viewPager, com.santosdumont.utils.ColorTheme theme,
                               String turma, OnAulaClickDia listener) {
        this.viewPager = viewPager;
        this.theme = theme;
        this.turma = turma;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pagina_horario_dia, parent, false);
        return new DiaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaViewHolder h, int position) {
        final String dia = DIAS[position];
        List<Aula> aulas = DataHelper.getHorarios(h.itemView.getContext(), turma, dia);
        HorarioAdapter adapter = (theme != null)
                ? new HorarioAdapter(aulas, theme, (aula, pos) -> {
                    if (listener != null) listener.onClick(aula, pos, dia);
                })
                : new HorarioAdapter(aulas, (aula, pos) -> {
                    if (listener != null) listener.onClick(aula, pos, dia);
                });
        h.rv.setLayoutManager(new LinearLayoutManager(h.itemView.getContext()));
        h.rv.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return DIAS.length;
    }

    public static String diaPorPosicao(int pos) {
        if (pos < 0 || pos >= DIAS.length) return DIAS[0];
        return DIAS[pos];
    }

    public static int posicaoPorDia(String dia) {
        for (int i = 0; i < DIAS.length; i++) {
            if (DIAS[i].equals(dia)) return i;
        }
        return 0;
    }

    static class DiaViewHolder extends RecyclerView.ViewHolder {
        RecyclerView rv;
        DiaViewHolder(@NonNull View itemView) {
            super(itemView);
            rv = itemView.findViewById(R.id.rv_horario_dia);
        }
    }
}
