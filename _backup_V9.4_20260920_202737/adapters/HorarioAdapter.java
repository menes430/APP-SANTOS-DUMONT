package com.santosdumont.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.santosdumont.R;
import com.santosdumont.models.Aula;
import java.util.List;

public class HorarioAdapter extends RecyclerView.Adapter<HorarioAdapter.HorarioViewHolder> {

    private List<Aula> aulas;

    public HorarioAdapter(List<Aula> aulas) {
        this.aulas = aulas;
    }

    @NonNull
    @Override
    public HorarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_horario, parent, false);
        return new HorarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorarioViewHolder holder, int position) {
        Aula aula = aulas.get(position);
        holder.tvHorario.setText(aula.getHorario());
        holder.tvDisciplina.setText(aula.getDisciplina());
        holder.tvProfessor.setText(aula.getProfessor());
    }

    @Override
    public int getItemCount() {
        return aulas.size();
    }

    static class HorarioViewHolder extends RecyclerView.ViewHolder {
        TextView tvHorario, tvDisciplina, tvProfessor;

        HorarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHorario = itemView.findViewById(R.id.tv_horario);
            tvDisciplina = itemView.findViewById(R.id.tv_disciplina);
            tvProfessor = itemView.findViewById(R.id.tv_professor);
        }
    }
}