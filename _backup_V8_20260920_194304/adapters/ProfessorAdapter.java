package com.santosdumont.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.santosdumont.R;
import com.santosdumont.models.ProfessorModel;
import java.util.List;

public class ProfessorAdapter extends RecyclerView.Adapter<ProfessorAdapter.ProfessorViewHolder> {

    private List<ProfessorModel> professores;
    private OnProfessorClickListener listener;

    public interface OnProfessorClickListener {
        void onProfessorClick(ProfessorModel professor);
    }

    public ProfessorAdapter(List<ProfessorModel> professores, OnProfessorClickListener listener) {
        this.professores = professores;
        this.listener = listener;
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
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProfessorClick(prof);
            }
        });
    }

    @Override
    public int getItemCount() {
        return professores.size();
    }

    static class ProfessorViewHolder extends RecyclerView.ViewHolder {
        TextView tvNomeProfessor, tvDisciplinasProfessor, tvTurmasProfessor;

        ProfessorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNomeProfessor = itemView.findViewById(R.id.tv_nome_professor);
            tvDisciplinasProfessor = itemView.findViewById(R.id.tv_disciplinas_professor);
            tvTurmasProfessor = itemView.findViewById(R.id.tv_turmas_professor);
        }
    }
}