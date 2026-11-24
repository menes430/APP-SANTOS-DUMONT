package com.santosdumont.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.santosdumont.R;
import com.santosdumont.models.CardapioItem;
import com.santosdumont.utils.DataHelper;

import java.util.List;

/**
 * Adapter do ViewPager2 para o Cardapio.
 * Cada pagina = 1 semana (5 dias em RecyclerView rolavel vertical).
 */
public class CardapioPagerAdapter extends RecyclerView.Adapter<CardapioPagerAdapter.SemanaViewHolder> {

    private final ViewPager2 viewPager;
    private final com.santosdumont.utils.ColorTheme theme;

    public CardapioPagerAdapter(ViewPager2 viewPager) {
        this.viewPager = viewPager;
        this.theme = null;
    }

    public CardapioPagerAdapter(ViewPager2 viewPager, com.santosdumont.utils.ColorTheme theme) {
        this.viewPager = viewPager;
        this.theme = theme;
    }

    @NonNull
    @Override
    public SemanaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pagina_cardapio_semana, parent, false);
        return new SemanaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SemanaViewHolder h, int position) {
        int semana = position + 1;
        List<CardapioItem> itens = DataHelper.getCardapioSemana(semana);
        CardapioAdapter adapter = (theme != null)
                ? new CardapioAdapter(itens, theme)
                : new CardapioAdapter(itens);
        h.rv.setLayoutManager(new LinearLayoutManager(h.itemView.getContext()));
        h.rv.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    static class SemanaViewHolder extends RecyclerView.ViewHolder {
        RecyclerView rv;
        SemanaViewHolder(@NonNull View itemView) {
            super(itemView);
            rv = itemView.findViewById(R.id.rv_semana);
        }
    }
}
