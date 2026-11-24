package com.santosdumont.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.santosdumont.R;
import com.santosdumont.models.Aula;
import com.santosdumont.utils.DataHelper;

import java.util.List;

public class HorarioAdapter extends RecyclerView.Adapter<HorarioAdapter.HorarioViewHolder> {

    public interface OnAulaClick {
        void onClick(Aula aula, int posicao);
    }

    private final List<Aula> aulas;
    private final OnAulaClick listener;
    private final com.santosdumont.utils.ColorTheme theme;

    public HorarioAdapter(List<Aula> aulas, OnAulaClick listener) {
        this.aulas = aulas;
        this.listener = listener;
        this.theme = null;
    }

    public HorarioAdapter(List<Aula> aulas, com.santosdumont.utils.ColorTheme theme, OnAulaClick listener) {
        this.aulas = aulas;
        this.listener = listener;
        this.theme = theme;
    }

    @NonNull
    @Override
    public HorarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_horario, parent, false);
        return new HorarioViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HorarioViewHolder h, int position) {
        Aula a = aulas.get(position);
        boolean useTema = (theme != null);
        int corTexto     = useTema ? theme.getTextPrimary()   : Color.WHITE;
        int corTextoSec  = useTema ? theme.getTextSecondary() : Color.parseColor("#888888");
        
        // Como o card de INTERVALO e SAÍDA estava com problema de corte
        // Vamos tratar com o fundo escuro padrão e cores adaptadas
        boolean isEspecial = a.isSemAula() || "INTERVALO".equalsIgnoreCase(a.getDisciplina()) || "SAÍDA".equalsIgnoreCase(a.getDisciplina());

        if (isEspecial) {
            h.tvDisciplina.setText(a.isSemAula() ? "Sem aula" : a.getDisciplina());
            h.tvDisciplina.setTextColor(corTextoSec);
            h.tvHorario.setText(a.getHorario());
            h.tvHorario.setTextColor(corTextoSec);
            h.tvProfessor.setText(""); 
            
            // Card escuro padrão para o visual ficar limpo
            ((com.google.android.material.card.MaterialCardView) h.itemView).setCardBackgroundColor(Color.parseColor("#151B1A")); // card_dark
            h.ivEditar.setVisibility(View.GONE);
        } else {
            h.tvDisciplina.setText(DataHelper.getNomeMateriaCompleto(a.getDisciplina()));
            h.tvHorario.setText(a.getHorario());
            // Como você pediu, tiramos a exibição do 2C (sala)
            h.tvProfessor.setText(a.getProfessor());

            int corMateria;
            if (useTema) {
                corMateria = theme.getCorMateria(a.getDisciplina(), a.getProfessor());
            } else {
                corMateria = DataHelper.getCorMateria(a.getDisciplina(), a.getProfessor());
            }
            int corFundo = (corMateria & 0x00FFFFFF) | 0xFF000000;
            int corTextoAuto = corTextoContraste(corFundo);
            int corTextoSecAuto = (corTextoAuto == 0xFFFFFFFF) ? 0xCCFFFFFF : 0xCC000000;

            h.tvDisciplina.setTextColor(corTextoAuto);
            h.tvHorario.setTextColor(corTextoSecAuto);
            h.tvProfessor.setTextColor(corTextoSecAuto);
            
            ((com.google.android.material.card.MaterialCardView) h.itemView).setCardBackgroundColor(corFundo);

            h.ivEditar.setVisibility(View.VISIBLE);
            h.ivEditar.setColorFilter(corTextoSecAuto);
            int bgCirculo = (corTextoAuto == 0xFFFFFFFF) ? 0x33FFFFFF : 0x33000000;
            android.graphics.drawable.Drawable bg2 = h.ivEditar.getBackground();
            if (bg2 instanceof android.graphics.drawable.GradientDrawable) {
                ((android.graphics.drawable.GradientDrawable) bg2.mutate()).setColor(bgCirculo);
            }
        }

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(a, position);
        });
    }

    @Override
    public int getItemCount() { return aulas == null ? 0 : aulas.size(); }

    // N5: retorna branco ou preto conforme a luminancia da cor de fundo
    // (WCAG-ish: luminancia relativa simplificada)
    private static int corTextoContraste(int corFundo) {
        int r = (corFundo >> 16) & 0xFF;
        int g = (corFundo >> 8) & 0xFF;
        int b = corFundo & 0xFF;
        double lum = (0.299 * r + 0.587 * g + 0.114 * b) / 255.0;
        return lum < 0.6 ? 0xFFFFFFFF : 0xFF000000;
    }

    static class HorarioViewHolder extends RecyclerView.ViewHolder {
        TextView tvHorario, tvDisciplina, tvProfessor, tvSala;
        ImageView ivEditar;
        HorarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHorario = itemView.findViewById(R.id.tv_horario);
            tvDisciplina = itemView.findViewById(R.id.tv_disciplina);
            tvProfessor = itemView.findViewById(R.id.tv_professor);
            tvSala = itemView.findViewById(R.id.tv_sala);
            ivEditar = itemView.findViewById(R.id.iv_editar_aula);
        }
    }
}