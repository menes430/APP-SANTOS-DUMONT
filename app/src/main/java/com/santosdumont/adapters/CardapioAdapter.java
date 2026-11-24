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

    private final List<CardapioItem> itens;
    private final com.santosdumont.utils.ColorTheme theme;

    public CardapioAdapter(List<CardapioItem> itens) {
        this.itens = itens;
        this.theme = null;
    }

    public CardapioAdapter(List<CardapioItem> itens, com.santosdumont.utils.ColorTheme theme) {
        this.itens = itens;
        this.theme = theme;
    }

    @NonNull
    @Override
    public CardapioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cardapio, parent, false);
        return new CardapioViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CardapioViewHolder h, int position) {
        CardapioItem i = itens.get(position);
        boolean useTema = (theme != null);

        h.tvDia.setText(i.getDiaSemana());
        h.tvSemana.setText(i.getSemana() + "° Semana");
        h.tvComida.setText(i.getNomePrato());
        if (h.tvComplementos != null) {
            h.tvComplementos.setText(i.getComplementos());
        }
        h.tvEnergia.setText(String.valueOf(i.getEnergiaKcal()));
        h.tvProteinas.setText(String.valueOf(i.getProteinas()));
        h.tvLipidos.setText(String.valueOf(i.getLipidos()));
        h.tvCarboidratos.setText(String.valueOf(i.getCarboidratos()));
        h.tvSodio.setText(String.valueOf(i.getSodioMg()));

        if (useTema) {
            int corPrimaria = theme.getPrimary();
            int corTextoPrim = theme.getTextPrimary();
            int corTextoSec = theme.getTextSecondary();

            // Card
            if (h.itemView instanceof com.google.android.material.card.MaterialCardView) {
                ((com.google.android.material.card.MaterialCardView) h.itemView)
                        .setCardBackgroundColor(theme.getSurface());
            }

            // Textos com cores do tema (semanticas mantidas)
            if (h.tvDia != null) h.tvDia.setTextColor(corPrimaria);
            if (h.tvSemana != null) h.tvSemana.setTextColor(corTextoSec);
            if (h.tvComida != null) h.tvComida.setTextColor(corTextoPrim);
            if (h.tvComplementos != null) h.tvComplementos.setTextColor(corTextoSec);
            if (h.tvEnergia != null) h.tvEnergia.setTextColor(corPrimaria);
            // tvProteinas, tvLipidos, tvCarboidratos, tvSodio -> mantem semanticas (success/warning/info/danger)
        }
    }

    @Override
    public int getItemCount() { return itens == null ? 0 : itens.size(); }

    static class CardapioViewHolder extends RecyclerView.ViewHolder {
        TextView tvDia, tvSemana, tvComida, tvComplementos;
        TextView tvEnergia, tvProteinas, tvLipidos, tvCarboidratos, tvSodio;

        CardapioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDia = itemView.findViewById(R.id.tv_dia_cardapio);
            tvSemana = itemView.findViewById(R.id.tv_semana_cardapio);
            tvComida = itemView.findViewById(R.id.tv_comida);
            tvComplementos = itemView.findViewById(R.id.tv_complementos);
            tvEnergia = itemView.findViewById(R.id.tv_energia);
            tvProteinas = itemView.findViewById(R.id.tv_proteinas);
            tvLipidos = itemView.findViewById(R.id.tv_lipidos);
            tvCarboidratos = itemView.findViewById(R.id.tv_carboidratos);
            tvSodio = itemView.findViewById(R.id.tv_sodio);
        }
    }
}
