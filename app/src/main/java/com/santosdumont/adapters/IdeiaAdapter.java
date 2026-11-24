package com.santosdumont.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.santosdumont.R;
import com.santosdumont.models.IdeiaModel;

import java.util.List;

public class IdeiaAdapter extends RecyclerView.Adapter<IdeiaAdapter.IdeiaViewHolder> {

    private final Context context;
    private final List<IdeiaModel> lista;

    public IdeiaAdapter(Context context, List<IdeiaModel> lista) {
        this.context = context;
        this.lista = lista;
    }

    @NonNull
    @Override
    public IdeiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_ideia, parent, false);
        return new IdeiaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull IdeiaViewHolder h, int position) {
        IdeiaModel item = lista.get(position);
        h.txtEmoji.setText(item.getEmoji());
        h.txtTitulo.setText(item.getTitulo());
        h.txtDescricao.setText(item.getDescricao());

        h.itemView.setOnClickListener(v -> {
            String msg = "Em breve: " + item.getTitulo() + "\n" + item.getDescricao();
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class IdeiaViewHolder extends RecyclerView.ViewHolder {
        TextView txtEmoji, txtTitulo, txtDescricao;

        public IdeiaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtEmoji = itemView.findViewById(R.id.txtEmoji);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtDescricao = itemView.findViewById(R.id.txtDescricao);
        }
    }
}