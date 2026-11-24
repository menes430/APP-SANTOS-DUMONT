package com.santosdumont.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.santosdumont.R;
import com.santosdumont.utils.ChangelogHelper;

import java.util.ArrayList;
import java.util.List;

public class ChangelogActivity extends AppCompatActivity {

    private ViewPager2 pager;
    private TextView btnOk;
    private List<ChangelogHelper.Versao> versoes;
    private ChangelogHelper.Versao versaoAtual;

    private static final String[] NOMES = {"\u2728 FUN\u00c7\u00d5ES NOVAS", "\u26A1 MELHORIAS", "\uD83D\uDC1B BUGS RESOLVIDOS"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changelog);

        TextView txtVersao = findViewById(R.id.txtChangelogVersao);
        if (txtVersao != null) {
            txtVersao.setText("Vers\u00e3o " + getString(R.string.app_version));
        }

        // Le a ultima versao do changelog
        versoes = ChangelogHelper.getTodasVersoes(this);
        if (versoes.isEmpty()) {
            finish();
            return;
        }
        versaoAtual = versoes.get(versoes.size() - 1);

        // Prepara as 3 categorias
        List<List<ChangelogHelper.Item>> categorias = new ArrayList<>();
        categorias.add(filtrar(versaoAtual, "NOVO"));
        categorias.add(filtrar(versaoAtual, "MELHORIA"));
        categorias.add(filtrar(versaoAtual, "CORRECAO"));

        pager = findViewById(R.id.changelogPager);
        pager.setAdapter(new CategoriaAdapter(categorias));
        pager.setOffscreenPageLimit(1);

        btnOk = findViewById(R.id.btnChangelogOk);
        atualizarBotao(0);
        atualizarIndicadores(0);

        // Swipe / troca de pagina
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                atualizarBotao(position);
                atualizarIndicadores(position);
            }
        });

        // Botao dinamico
        btnOk.setOnClickListener(v -> {
            int atual = pager.getCurrentItem();
            if (atual < 2) {
                pager.setCurrentItem(atual + 1, true);
            } else {
                // Ultima: salva a versao vista e vai pra Shell
                android.content.SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
                prefs.edit().putString("versao_vista", getString(R.string.app_version)).apply();
                startActivity(new android.content.Intent(this, ShellActivity.class));
                finish();
            }
        });

        // Animacao de entrada
        animarEntrada(findViewById(R.id.txtChangelogTitulo), 0);
        animarEntrada(findViewById(R.id.txtChangelogVersao), 100);
        animarEntrada(pager, 200);
        animarEntrada(findViewById(R.id.indicadoresContainer), 300);
        animarEntrada(btnOk, 400);
    }

    private List<ChangelogHelper.Item> filtrar(ChangelogHelper.Versao v, String tipo) {
        List<ChangelogHelper.Item> out = new ArrayList<>();
        for (ChangelogHelper.Item it : v.itens) {
            if (tipo.equals(it.tipo)) out.add(it);
        }
        return out;
    }

    private void atualizarBotao(int pos) {
        if (btnOk == null) return;
        if (pos < 2) {
            btnOk.setText("Continuar");
        } else {
            btnOk.setText("Entendi!");
        }
    }

    private void atualizarIndicadores(int posicaoAtiva) {
        View[] quadrados = {
                findViewById(R.id.quadradoIndicador0),
                findViewById(R.id.quadradoIndicador1),
                findViewById(R.id.quadradoIndicador2)
        };
        for (int i = 0; i < quadrados.length; i++) {
            View q = quadrados[i];
            if (q == null) continue;
            if (i == posicaoAtiva) {
                q.animate().alpha(1f).setDuration(200).start();
            } else {
                q.animate().alpha(0.3f).setDuration(200).start();
            }
        }
    }

    private void animarEntrada(View v, long delay) {
        if (v == null) return;
        v.setAlpha(0f);
        v.setTranslationY(40f);
        v.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(delay)
                .setDuration(500)
                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                .start();
    }

    @Override
    public void onBackPressed() {
        // M15: Changelog -> Tutorial
        startActivity(new android.content.Intent(this, TutorialActivity.class));
        finish();
    }

    // ============================================================
    // ADAPTER DE CATEGORIAS (3 paginas)
    // ============================================================
    class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.VH> {

        private final List<List<ChangelogHelper.Item>> categorias;

        CategoriaAdapter(List<List<ChangelogHelper.Item>> categorias) {
            this.categorias = categorias;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_changelog_card, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            List<ChangelogHelper.Item> itens = categorias.get(position);

            h.txtTitulo.setText(NOMES[position]);
            h.txtQtd.setText(itens.size() + (itens.size() == 1 ? " item" : " itens"));

            h.recycler.setLayoutManager(new LinearLayoutManager(h.itemView.getContext()));
            h.recycler.setAdapter(new ItemAdapter(itens));
        }

        @Override
        public int getItemCount() { return categorias.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView txtTitulo, txtQtd;
            RecyclerView recycler;
            VH(@NonNull View v) {
                super(v);
                txtTitulo = v.findViewById(R.id.txtTituloCategoria);
                txtQtd = v.findViewById(R.id.txtQtdCategoria);
                recycler = v.findViewById(R.id.recyclerItens);
            }
        }
    }

    // ============================================================
    // ADAPTER DE ITENS
    // ============================================================
    static class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.VH> {

        private final List<ChangelogHelper.Item> itens;

        ItemAdapter(List<ChangelogHelper.Item> itens) {
            this.itens = itens;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_changelog_item, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            ChangelogHelper.Item it = itens.get(position);
            h.txtTexto.setText(it.texto);
        }

        @Override
        public int getItemCount() { return itens.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView txtBullet, txtTexto;
            VH(@NonNull View v) {
                super(v);
                txtBullet = v.findViewById(R.id.txtItemBullet);
                txtTexto = v.findViewById(R.id.txtItemTexto);
            }
        }
    }
}