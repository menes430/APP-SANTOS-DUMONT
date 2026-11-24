package com.santosdumont.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.santosdumont.R;
import com.santosdumont.models.CardapioItem;
import java.util.List;

public class CardapioAdapter extends RecyclerView.Adapter<CardapioAdapter.CardapioViewHolder> {

    private List<CardapioItem> itens;

    public CardapioAdapter(List<CardapioItem> itens) {
        this.itens = itens;
    }

    @NonNull
    @Override
    public CardapioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cardapio, parent, false);
        return new CardapioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardapioViewHolder holder, int position) {
        CardapioItem item = itens.get(position);
        holder.tvDiaCardapio.setText(item.getDiaSemana());
        holder.tvComida.setText(item.getNomePrato());
        holder.tvEnergia.setText(String.valueOf(item.getEnergiaKcal()));
        holder.tvProteinas.setText(String.valueOf(item.getProteinas()));
        holder.tvLipidos.setText(String.valueOf(item.getLipidos()));
        holder.tvCarboidratos.setText(String.valueOf(item.getCarboidratos()));
        holder.tvSodio.setText(String.valueOf(item.getSodioMg()));
        holder.tvSemanaCardapio.setText(item.getSemana() + "ª Semana");
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    static class CardapioViewHolder extends RecyclerView.ViewHolder {
        TextView tvDiaCardapio, tvSemanaCardapio, tvComida,
                 tvEnergia, tvProteinas, tvLipidos, tvCarboidratos, tvSodio;

        CardapioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDiaCardapio = itemView.findViewById(R.id.tv_dia_cardapio);
            tvSemanaCardapio = itemView.findViewById(R.id.tv_semana_cardapio);
            tvComida = itemView.findViewById(R.id.tv_comida);
            tvEnergia = itemView.findViewById(R.id.tv_energia);
            tvProteinas = itemView.findViewById(R.id.tv_proteinas);
            tvLipidos = itemView.findViewById(R.id.tv_lipidos);
            tvCarboidratos = itemView.findViewById(R.id.tv_carboidratos);
            tvSodio = itemView.findViewById(R.id.tv_sodio);
        }
    }
}