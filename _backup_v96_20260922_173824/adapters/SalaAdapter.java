package com.santosdumont.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.santosdumont.models.Sala;
import java.util.List;

public class SalaAdapter extends ArrayAdapter<Sala> {

    public SalaAdapter(Context context, List<Sala> salas) {
        super(context, 0, salas);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(android.R.layout.simple_spinner_item, parent, false);
        }
        Sala sala = getItem(position);
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(sala.getNome());
        return convertView;
    }
}