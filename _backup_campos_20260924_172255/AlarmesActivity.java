package com.santosdumont.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.santosdumont.R;
import com.santosdumont.utils.NotificationReceiver;
import java.util.Calendar;
import java.util.Map;

public class AlarmesActivity extends AppCompatActivity {

    private EditText edtTitulo, edtData, edtHora;
    private LinearLayout llLista;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmes);

        edtTitulo = findViewById(R.id.edtTituloAlarme);
        edtData = findViewById(R.id.edtDataAlarme);
        edtHora = findViewById(R.id.edtHoraAlarme);
        llLista = findViewById(R.id.llListaAlarmes);
        prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);

        ImageView btnVoltar = findViewById(R.id.btnVoltarAlarmes);
        btnVoltar.setOnClickListener(v -> onBackPressed());

        // Máscara de data: DD/MM/AA
        edtData.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) return;
                isUpdating = true;
                String digits = s.toString().replaceAll("[^0-9]", "");
                String formatted = "";
                if (digits.length() >= 1) formatted += digits.substring(0, 1);
                if (digits.length() >= 2) formatted += digits.substring(1, 2);
                if (digits.length() >= 3) formatted += "/" + digits.substring(2, 3);
                if (digits.length() >= 4) formatted += digits.substring(3, 4);
                if (digits.length() >= 5) formatted += "/" + digits.substring(4, 5);
                if (digits.length() >= 6) formatted += digits.substring(5, 6);
                edtData.setText(formatted);
                edtData.setSelection(formatted.length());
                isUpdating = false;
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Máscara de hora: HH:MM
        edtHora.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) return;
                isUpdating = true;
                String digits = s.toString().replaceAll("[^0-9]", "");
                String formatted = "";
                if (digits.length() >= 1) formatted += digits.substring(0, 1);
                if (digits.length() >= 2) formatted += digits.substring(1, 2);
                if (digits.length() >= 3) formatted += ":" + digits.substring(2, 3);
                if (digits.length() >= 4) formatted += digits.substring(3, 4);
                edtHora.setText(formatted);
                edtHora.setSelection(formatted.length());
                isUpdating = false;
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        Button btnSalvar = findViewById(R.id.btnSalvarAlarme);
        btnSalvar.setOnClickListener(v -> salvarAlarme());

        listarAlarmes();
    }

    private void salvarAlarme() {
        String titulo = edtTitulo.getText().toString().trim();
        String data = edtData.getText().toString().trim();
        String hora = edtHora.getText().toString().trim();

        if (titulo.isEmpty() || data.isEmpty() || hora.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (data.length() != 8) {
            Toast.makeText(this, "Data inválida! Use DD/MM/AA", Toast.LENGTH_SHORT).show();
            return;
        }
        if (hora.length() != 5) {
            Toast.makeText(this, "Hora inválida! Use HH:MM", Toast.LENGTH_SHORT).show();
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

            if (horaInt < 0 || horaInt > 23 || minutoInt < 0 || minutoInt > 59) {
                Toast.makeText(this, "Hora inválida! Use 00:00 até 23:59", Toast.LENGTH_SHORT).show();
                return;
            }

            Calendar cal = Calendar.getInstance();
            cal.set(ano, mes, dia, horaInt, minutoInt, 0);

            if (cal.getTimeInMillis() < System.currentTimeMillis()) {
                Toast.makeText(this, "Data/hora no passado!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.putExtra("titulo", "⏰ " + titulo);
            intent.putExtra("mensagem", data + " às " + hora);

            int requestCode = (int) System.currentTimeMillis();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this, requestCode, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                }
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
            }

            prefs.edit().putString("alarme_" + requestCode, titulo + "|" + data + "|" + hora).apply();
            Toast.makeText(this, "Alarme agendado!", Toast.LENGTH_SHORT).show();
            edtTitulo.setText("");
            edtData.setText("");
            edtHora.setText("");
            listarAlarmes();
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao agendar alarme", Toast.LENGTH_SHORT).show();
        }
    }

    private void listarAlarmes() {
        llLista.removeAllViews();
        Map<String, ?> todos = prefs.getAll();
        for (Map.Entry<String, ?> entry : todos.entrySet()) {
            if (entry.getKey().startsWith("alarme_")) {
                String chave = entry.getKey();
                String[] partes = entry.getValue().toString().split("\\|");
                if (partes.length == 3) {
                    LinearLayout card = new LinearLayout(this);
                    card.setOrientation(LinearLayout.VERTICAL);
                    card.setBackgroundResource(R.drawable.bg_editor_card);
                    card.setPadding(16, 12, 16, 12);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 0, 8);
                    card.setLayoutParams(params);

                    TextView tvTitulo = new TextView(this);
                    tvTitulo.setText("⏰ " + partes[0]);
                    tvTitulo.setTextColor(0xFFFFFFFF);
                    tvTitulo.setTextSize(16);
                    tvTitulo.setTypeface(null, android.graphics.Typeface.BOLD);

                    TextView tvDataHora = new TextView(this);
                    tvDataHora.setText(partes[1] + " às " + partes[2]);
                    tvDataHora.setTextColor(0xFFAAB5B2);
                    tvDataHora.setTextSize(14);
                    tvDataHora.setPadding(0, 4, 0, 0);

                    card.addView(tvTitulo);
                    card.addView(tvDataHora);

                    // Clique longo para excluir
                    card.setOnLongClickListener(v -> {
                        new AlertDialog.Builder(this)
                                .setTitle("Excluir alarme?")
                                .setMessage("Deseja realmente excluir o alarme \"" + partes[0] + "\"?")
                                .setPositiveButton("Sim", (dialog, which) -> {
                                    excluirAlarme(chave);
                                })
                                .setNegativeButton("Não", null)
                                .show();
                        return true;
                    });

                    // Clique simples para mostrar dica
                    card.setOnClickListener(v -> {
                        Toast.makeText(this, "Segure para excluir", Toast.LENGTH_SHORT).show();
                    });

                    llLista.addView(card);
                }
            }
        }
    }

    private void excluirAlarme(String chave) {
        // Cancelar o alarme agendado no sistema
        try {
            String requestCodeStr = chave.replace("alarme_", "");
            int requestCode = Integer.parseInt(requestCodeStr);

            Intent intent = new Intent(this, NotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this, requestCode, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Remover das preferências
        prefs.edit().remove(chave).apply();
        Toast.makeText(this, "Alarme excluído!", Toast.LENGTH_SHORT).show();
        listarAlarmes();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}