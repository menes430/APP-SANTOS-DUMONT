package com.santosdumont.fragments;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.santosdumont.R;
import com.santosdumont.utils.ColorTheme;
import com.santosdumont.utils.NotificationReceiver;
import com.santosdumont.utils.ThemePrefs;

import java.util.Calendar;
import java.util.Map;

public class AlarmesFragment extends Fragment {

    private EditText edtTitulo, edtData, edtHora;
    private LinearLayout llLista;
    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alarmes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edtTitulo = view.findViewById(R.id.edtTituloAlarme);
        edtData = view.findViewById(R.id.edtDataAlarme);
        edtHora = view.findViewById(R.id.edtHoraAlarme);
        llLista = view.findViewById(R.id.llListaAlarmes);
        prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);

        Button btnSalvar = view.findViewById(R.id.btnSalvarAlarme);
        btnSalvar.setOnClickListener(v -> salvarAlarme());

        aplicarTema(view);
        listarAlarmes();
    }

    private boolean padrao() { return ThemePrefs.isPadrao(requireContext()); }
    private ColorTheme tema() { return ThemePrefs.getColorTheme(requireContext()); }

    private void aplicarTema(View view) {
        if (view == null || padrao()) return;
        ColorTheme t = tema();

        view.setBackgroundColor(t.getBackground());

        android.widget.TextView titulo = view.findViewById(R.id.txtTituloMeusAlarmes);
        if (titulo != null) titulo.setTextColor(t.getTextPrimary());

        if (edtTitulo != null) {
            edtTitulo.setTextColor(t.getTextPrimary());
            edtTitulo.setHintTextColor(t.getTextSecondary());
        }
        if (edtData != null) {
            edtData.setTextColor(t.getTextPrimary());
            edtData.setHintTextColor(t.getTextSecondary());
        }
        if (edtHora != null) {
            edtHora.setTextColor(t.getTextPrimary());
            edtHora.setHintTextColor(t.getTextSecondary());
        }
    }

    private void salvarAlarme() {
        String titulo = edtTitulo.getText().toString().trim();
        String data = edtData.getText().toString().trim();
        String hora = edtHora.getText().toString().trim();

        if (titulo.isEmpty() || data.isEmpty() || hora.isEmpty()) {
            Toast.makeText(requireContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String[] dataPartes = data.split("/");
            String[] horaPartes = hora.split(":");
            int dia = Integer.parseInt(dataPartes[0]);
            int mes = Integer.parseInt(dataPartes[1]) - 1;
            int ano = Integer.parseInt(dataPartes[2]);
            if (ano < 100) ano += 2000;
            int horaInt = Integer.parseInt(horaPartes[0]);
            int minutoInt = Integer.parseInt(horaPartes[1]);

            Calendar cal = Calendar.getInstance();
            cal.set(ano, mes, dia, horaInt, minutoInt, 0);

            Intent intent = new Intent(requireContext(), NotificationReceiver.class);
            intent.putExtra("titulo", titulo);
            intent.putExtra("mensagem", data + " as " + hora);

            int requestCode = (int) System.currentTimeMillis();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    requireContext(), requestCode, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            AlarmManager am = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (am.canScheduleExactAlarms()) {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                } else {
                    am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                }
            } else {
                am.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
            }

            prefs.edit().putString("alarme_" + requestCode, titulo + "|" + data + "|" + hora).apply();
            Toast.makeText(requireContext(), "Alarme agendado!", Toast.LENGTH_SHORT).show();
            edtTitulo.setText("");
            edtData.setText("");
            edtHora.setText("");
            listarAlarmes();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Erro ao agendar", Toast.LENGTH_SHORT).show();
        }
    }

    private void listarAlarmes() {
        if (llLista == null) return;
        llLista.removeAllViews();

        boolean isPadrao = padrao();
        ColorTheme t = isPadrao ? null : tema();
        int corTexto = isPadrao ? 0xFFFFFFFF : t.getTextPrimary();
        int corSec = isPadrao ? 0xFFAAB5B2 : t.getTextSecondary();

        Map<String, ?> todos = prefs.getAll();
        for (Map.Entry<String, ?> entry : todos.entrySet()) {
            if (entry.getKey().startsWith("alarme_")) {
                String chave = entry.getKey();
                String[] partes = entry.getValue().toString().split("\\|");
                if (partes.length == 3) {
                    LinearLayout card = new LinearLayout(requireContext());
                    card.setOrientation(LinearLayout.VERTICAL);
                    card.setBackgroundResource(R.drawable.bg_editor_card);
                    card.setPadding(16, 12, 16, 12);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 0, 8);
                    card.setLayoutParams(params);

                    TextView tvTitulo = new TextView(requireContext());
                    tvTitulo.setText(partes[0]);
                    tvTitulo.setTextColor(corTexto);
                    tvTitulo.setTextSize(16);
                    tvTitulo.setTypeface(null, android.graphics.Typeface.BOLD);

                    TextView tvDataHora = new TextView(requireContext());
                    tvDataHora.setText(partes[1] + " as " + partes[2]);
                    tvDataHora.setTextColor(corSec);
                    tvDataHora.setTextSize(14);
                    tvDataHora.setPadding(0, 4, 0, 0);

                    card.addView(tvTitulo);
                    card.addView(tvDataHora);

                    card.setOnLongClickListener(v -> {
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Excluir alarme?")
                                .setPositiveButton("Sim", (d, w) -> excluirAlarme(chave))
                                .setNegativeButton("Nao", null)
                                .show();
                        return true;
                    });

                    llLista.addView(card);
                }
            }
        }
    }

    private void excluirAlarme(String chave) {
        prefs.edit().remove(chave).apply();
        Toast.makeText(requireContext(), "Alarme excluido!", Toast.LENGTH_SHORT).show();
        listarAlarmes();
    }
}
