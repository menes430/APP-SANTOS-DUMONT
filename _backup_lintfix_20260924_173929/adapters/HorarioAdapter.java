package com.santosdumont.adapters;

import android.graphics.Color;
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

    public interface OnAulaClick {
        void onClick(Aula aula, int posicao);
    }

    private final List<Aula> aulas;
    private final OnAulaClick listener;

    public HorarioAdapter(List<Aula> aulas, OnAulaClick listener) {
        this.aulas = aulas;
        this.listener = listener;
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
        int corPadrao = Color.WHITE;
        int corCinza = Color.parseColor("#888888");
        int corPrimaria = Color.parseColor("#0D99FF");

        if (a.isSemAula()) {
            h.tvHorario.setText(a.getHorario());
            h.tvHorario.setTextColor(corCinza);
            h.tvDisciplina.setText("Sem aula");
            h.tvDisciplina.setTextColor(corCinza);
            h.tvProfessor.setText("");
        } else {
            h.tvHorario.setText(a.getHorario());
            h.tvHorario.setTextColor(corPrimaria);
            h.tvDisciplina.setText(a.getDisciplina());
            h.tvDisciplina.setTextColor(corPadrao);
            if (a.isTextoLivre()) {
                h.tvProfessor.setText("");
            } else {
                h.tvProfessor.setText(a.getProfessor());
            }
        }

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(a, position);
        });
    }

    @Override
    public int getItemCount() { return aulas == null ? 0 : aulas.size(); }

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