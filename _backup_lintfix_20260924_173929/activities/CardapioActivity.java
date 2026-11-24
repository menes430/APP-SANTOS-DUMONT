package com.santosdumont.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.santosdumont.R;
import com.santosdumont.adapters.CardapioAdapter;
import com.santosdumont.models.CardapioItem;
import com.santosdumont.utils.DataHelper;

import java.util.List;

public class CardapioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardapio);

        View btnVoltar = findViewById(R.id.btnVoltarCardapio);
        if (btnVoltar != null) {
            btnVoltar.setOnClickListener(v -> finish());
        }

        TabLayout tabSemanas = findViewById(R.id.tab_semanas);
        RecyclerView rvCardapio = findViewById(R.id.rv_cardapio);
        rvCardapio.setLayoutManager(new LinearLayoutManager(this));
        carregarSemana(1);

        tabSemanas.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) { carregarSemana(tab.getPosition() + 1); }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void carregarSemana(int semana) {
        List<CardapioItem> itens = DataHelper.getCardapioSemana(semana);
        RecyclerView rvCardapio = findViewById(R.id.rv_cardapio);
        rvCardapio.setAdapter(new CardapioAdapter(itens));
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}