package com.santosdumont.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.santosdumont.R;
import com.santosdumont.utils.ColorTheme;
import com.santosdumont.utils.ThemePrefs;

public class MapaFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mapa, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        aplicarTema(view);
    }

    private void aplicarTema(View view) {
        if (ThemePrefs.isPadrao(requireContext())) return;
        ColorTheme t = ThemePrefs.getColorTheme(requireContext());

        view.setBackgroundColor(t.getBackground());

        android.widget.TextView titulo = view.findViewById(R.id.txtTituloMapa);
        if (titulo != null) titulo.setTextColor(t.getTextPrimary());

        // Percorre textos
        if (view instanceof android.view.ViewGroup) {
            percorrer((android.view.ViewGroup) view, t);
        }
    }

    private void percorrer(android.view.ViewGroup vg, ColorTheme t) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View filho = vg.getChildAt(i);
            if (filho instanceof android.widget.TextView) {
                android.widget.TextView tv = (android.widget.TextView) filho;
                CharSequence txt = tv.getText();
                if (txt != null) {
                    String s = txt.toString();
                    if (s.contains("Mapa em construcao")) {
                        tv.setTextColor(t.getPrimary());
                    } else if (s.contains("Envie a imagem")) {
                        tv.setTextColor(t.getTextPrimary());
                    }
                }
            } else if (filho instanceof android.view.ViewGroup) {
                percorrer((android.view.ViewGroup) filho, t);
            }
        }
    }
}
