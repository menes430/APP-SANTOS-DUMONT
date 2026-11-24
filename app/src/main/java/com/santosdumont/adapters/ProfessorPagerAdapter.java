package com.santosdumont.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.santosdumont.R;
import com.santosdumont.models.ProfessorModel;
import com.santosdumont.utils.DataHelper;

import java.util.List;

/**
 * N10: Adapter do ViewPager2 para o Professores.
 * Cada pagina = 1 dia (Seg..Sex) com RecyclerView vertical de professores.
 */
public class ProfessorPagerAdapter extends RecyclerView.Adapter<ProfessorPagerAdapter.DiaViewHolder> {

    public interface OnProfessorClickDia {
        void onClick(ProfessorModel professor, String dia);
    }

    private static final String[] DIAS = {"Segunda", "Terça", "Quarta", "Quinta", "Sexta"};

    private final ViewPager2 viewPager;
    private final com.santosdumont.utils.ColorTheme theme;
    private final OnProfessorClickDia listener;
    private final String turno;

    public ProfessorPagerAdapter(ViewPager2 viewPager, OnProfessorClickDia listener) {
        this(viewPager, null, listener, null);
    }

    public ProfessorPagerAdapter(ViewPager2 viewPager, com.santosdumont.utils.ColorTheme theme,
                                 OnProfessorClickDia listener) {
        this(viewPager, theme, listener, null);
    }

    public ProfessorPagerAdapter(ViewPager2 viewPager, OnProfessorClickDia listener, String turno) {
        this(viewPager, null, listener, turno);
    }

    public ProfessorPagerAdapter(ViewPager2 viewPager, com.santosdumont.utils.ColorTheme theme,
                                 OnProfessorClickDia listener, String turno) {
        this.viewPager = viewPager;
        this.theme = theme;
        this.listener = listener;
        this.turno = turno;
    }

    @NonNull
    @Override
    public DiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pagina_professor_dia, parent, false);
        return new DiaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaViewHolder h, int position) {
        final String dia = DIAS[position];
        List<ProfessorModel> lista = DataHelper.getProfessoresDoDia(h.itemView.getContext(), dia, turno);
        ProfessorAdapter adapter = (theme != null)
                ? new ProfessorAdapter(lista, theme, professor -> {
                    if (listener != null) listener.onClick(professor, dia);
                })
                : new ProfessorAdapter(lista, professor -> {
                    if (listener != null) listener.onClick(professor, dia);
                });
        // N14: LinearLayoutManager VERTICAL explicito (1 coluna) — evita 2 colunas acidentais
        h.rv.setLayoutManager(new LinearLayoutManager(h.itemView.getContext(),
                LinearLayoutManager.VERTICAL, false));
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
            rv = itemView.findViewById(R.id.rv_professor_dia);
        }
    }
}
