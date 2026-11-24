package com.santosdumont.activities;

import android.os.Bundle;
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

    private TabLayout tabSemanas;
    private RecyclerView rvCardapio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardapio);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_cardapio);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cardápio EJA");

        tabSemanas = findViewById(R.id.tab_semanas);
        rvCardapio = findViewById(R.id.rv_cardapio);
        rvCardapio.setLayoutManager(new LinearLayoutManager(this));

        carregarSemana(1);

        tabSemanas.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                carregarSemana(tab.getPosition() + 1);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void carregarSemana(int semana) {
        List<CardapioItem> itens = DataHelper.getCardapioSemana(semana);
        CardapioAdapter adapter = new CardapioAdapter(itens);
        rvCardapio.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}