package com.santosdumont.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.santosdumont.R;
import com.santosdumont.models.ProfessorModel;
import com.santosdumont.utils.DataHelper;
import java.util.List;

public class ProfessorAdapter extends RecyclerView.Adapter<ProfessorAdapter.ProfessorViewHolder> {

    private List<ProfessorModel> professores;
    private OnProfessorClickListener listener;
    private final com.santosdumont.utils.ColorTheme theme;

    public interface OnProfessorClickListener {
        void onProfessorClick(ProfessorModel professor);
    }

    public ProfessorAdapter(List<ProfessorModel> professores, OnProfessorClickListener listener) {
        this.professores = professores;
        this.listener = listener;
        this.theme = null;
    }

    public ProfessorAdapter(List<ProfessorModel> professores,
                            com.santosdumont.utils.ColorTheme theme,
                            OnProfessorClickListener listener) {
        this.professores = professores;
        this.listener = listener;
        this.theme = theme;
    }

    @NonNull
    @Override
    public ProfessorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_professor, parent, false);
        return new ProfessorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfessorViewHolder holder, int position) {
        ProfessorModel prof = professores.get(position);
        holder.tvNomeProfessor.setText(prof.getNome());
        holder.tvDisciplinasProfessor.setText(prof.getDisciplinasStr());
        holder.tvTurmasProfessor.setText(prof.getTurmas().size() + " turmas");

        // Pega a primeira disciplina para definir a cor do card
        String primeiraDisciplina = "";
        if (prof.getDisciplinas() != null && !prof.getDisciplinas().isEmpty()) {
            primeiraDisciplina = prof.getDisciplinas().get(0);
        }

        // Usa a cor da matéria exatamente como no Horário
        int corMateria = (theme != null) ? theme.getCorMateria(primeiraDisciplina, prof.getNome()) : DataHelper.getCorMateria(primeiraDisciplina, prof.getNome());
        int corFundo = (corMateria & 0x00FFFFFF) | 0xFF000000; // 100% opaco

        // FORÇANDO TEXTO BRANCO CONFORME PEDIDO
        int corTextoAuto = 0xFFFFFFFF; 
        int corTextoSecAuto = 0xCCFFFFFF; // Branco com 80% de opacidade

        if (holder.itemView instanceof com.google.android.material.card.MaterialCardView) {
            ((com.google.android.material.card.MaterialCardView) holder.itemView)
                    .setCardBackgroundColor(corFundo);
        }
        
        // Icone branco
        if (holder.ivIcone != null) {
            holder.ivIcone.setColorFilter(corTextoAuto);
        }
        
        // Textos brancos
        holder.tvNomeProfessor.setTextColor(corTextoAuto);
        holder.tvDisciplinasProfessor.setTextColor(corTextoSecAuto);
        holder.tvTurmasProfessor.setTextColor(corTextoAuto);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProfessorClick(prof);
            }
        });
    }

    // Mesma logica de contraste do HorarioAdapter
    private static int corTextoContraste(int corFundo) {
        int r = (corFundo >> 16) & 0xFF;
        int g = (corFundo >> 8) & 0xFF;
        int b = corFundo & 0xFF;
        double lum = (0.299 * r + 0.587 * g + 0.114 * b) / 255.0;
        return lum < 0.6 ? 0xFFFFFFFF : 0xFF000000;
    }

    @Override
    public int getItemCount() {
        return professores.size();
    }

    static class ProfessorViewHolder extends RecyclerView.ViewHolder {
        TextView tvNomeProfessor, tvDisciplinasProfessor, tvTurmasProfessor;
        ImageView ivIcone;

        ProfessorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNomeProfessor = itemView.findViewById(R.id.tv_nome_professor);
            tvDisciplinasProfessor = itemView.findViewById(R.id.tv_disciplinas_professor);
            tvTurmasProfessor = itemView.findViewById(R.id.tv_turmas_professor);
            ivIcone = itemView.findViewById(R.id.iv_icone_professor);
        }
    }
}
