package com.santosdumont.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.santosdumont.R;
import com.santosdumont.models.CardHomeModel;

import java.util.List;

public class CardHomeAdapter extends RecyclerView.Adapter<CardHomeAdapter.VH> {

    public interface OnCardClick {
        void onClick(CardHomeModel card);
    }

    private final Context ctx;
    private final List<CardHomeModel> lista;
    private final OnCardClick listener;

    public CardHomeAdapter(Context ctx, List<CardHomeModel> lista, OnCardClick listener) {
        this.ctx = ctx;
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_card_home, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        CardHomeModel c = lista.get(position);
        h.txtTitulo.setText(c.getTitulo());
        h.txtSub.setText(c.getSubtitulo());
        h.card.setCardBackgroundColor(c.getCorFundo());

        int resId = ctx.getResources().getIdentifier(
                c.getIcone(), "drawable", ctx.getPackageName());
        if (resId != 0) h.img.setImageResource(resId);

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(c);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        MaterialCardView card;
        ImageView img;
        TextView txtTitulo, txtSub;

        VH(@NonNull View v) {
            super(v);
            card = v.findViewById(R.id.cardItemHome);
            img = v.findViewById(R.id.imgIconeCard);
            txtTitulo = v.findViewById(R.id.txtTituloCard);
            txtSub = v.findViewById(R.id.txtSubtituloCard);
        }
    }
}