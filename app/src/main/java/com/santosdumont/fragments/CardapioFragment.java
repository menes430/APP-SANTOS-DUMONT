package com.santosdumont.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.santosdumont.R;
import com.santosdumont.adapters.CardapioPagerAdapter;
import com.santosdumont.utils.DataHelper;

public class CardapioFragment extends Fragment {

    private TabLayout tabSemanas;
    private ViewPager2 vpCardapio;
    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cardapio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);

        // AVISO 1a ABERTURA DA MERENDA
        if (!prefs.getBoolean("aviso_merenda_visto", false)) {
            com.santosdumont.utils.AvisoHelper.mostrarAvisoBloqueado(
                requireContext(),
                "Sobre a Merenda",
                "Pode haver inconsistência no cardápio por problemas internos na gerência, " +
                "como a quantia de alunos na escola ou falta de algum material alimentar.",
                com.santosdumont.utils.AvisoHelper.SEGUNDOS_BLOQUEIO
            );
            prefs.edit().putBoolean("aviso_merenda_visto", true).apply();
        }

        tabSemanas = view.findViewById(R.id.tab_semanas);
        vpCardapio = view.findViewById(R.id.vp_cardapio);
        ImageButton btnMenu = view.findViewById(R.id.btnMenuSemana);

        com.santosdumont.utils.ColorTheme t = com.santosdumont.utils.ThemePrefs.getColorTheme(requireContext());
        CardapioPagerAdapter pagerAdapter = com.santosdumont.utils.ThemePrefs.isPadrao(requireContext())
                ? new CardapioPagerAdapter(vpCardapio)
                : new CardapioPagerAdapter(vpCardapio, t);
        vpCardapio.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabSemanas, vpCardapio, (tab, position) -> {
            tab.setText((position + 1) + "°");
        }).attach();

        // Rola pra semana atual
        sincronizarSemana();

        // Botao 3 pontinhos
        if (btnMenu != null) {
            btnMenu.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(requireContext(), v);
                popup.getMenu().add(0, 1, 0, "Semana 1 (atual)");
                popup.getMenu().add(0, 2, 1, "Semana 2");
                popup.getMenu().add(0, 3, 2, "Semana 3");
                popup.getMenu().add(0, 4, 3, "Semana 4");

                popup.setOnMenuItemClickListener(item -> {
                    int semana = item.getItemId();
                    DataHelper.setSemanaReferencia(requireContext(), semana);
                    vpCardapio.setCurrentItem(semana - 1, true);
                    Toast.makeText(requireContext(),
                            "Semana " + semana + " definida como atual",
                            Toast.LENGTH_SHORT).show();
                    return true;
                });

                popup.show();
            });
        }
        aplicarTemaNaTela(view);

    }

    @Override
    public void onResume() {
        super.onResume();
        sincronizarSemana();
    }

    private void sincronizarSemana() {
        if (vpCardapio == null) return;
        int semanaAtual = DataHelper.getSemanaAtual(requireContext());
        if (vpCardapio.getCurrentItem() != semanaAtual - 1) {
            vpCardapio.setCurrentItem(semanaAtual - 1, true);
        }
    }


    // ============================================================
    // APLICAR TEMA NA TELA DE CARDAPIO
    // ============================================================
    private void aplicarTemaNaTela(View view) {
        if (com.santosdumont.utils.ThemePrefs.isPadrao(requireContext())) return;
        com.santosdumont.utils.ColorTheme t = com.santosdumont.utils.ThemePrefs.getColorTheme(requireContext());

        view.setBackgroundColor(t.getBackground());

        android.widget.TextView titulo = view.findViewById(R.id.txtTituloCardapio);
        if (titulo != null) titulo.setTextColor(t.getTextPrimary());

        if (tabSemanas != null) {
            tabSemanas.setSelectedTabIndicatorColor(t.getPrimary());
            tabSemanas.setTabTextColors(t.getTextSecondary(), t.getTextPrimary());
        }

        if (vpCardapio != null && vpCardapio.getAdapter() != null) {
            vpCardapio.getAdapter().notifyDataSetChanged();
        }
    }
}
