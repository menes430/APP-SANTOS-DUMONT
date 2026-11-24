package com.santosdumont.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.santosdumont.R;

public class AvisoHelper {

    public static final int SEGUNDOS_BLOQUEIO = 10;

    private static boolean emAndamento = false;

    public static boolean estahAtivo() {
        return emAndamento;
    }

    public static void mostrarAvisoBloqueado(Context ctx, String titulo, String mensagem) {
        mostrarInterno(ctx, titulo, mensagem, SEGUNDOS_BLOQUEIO);
    }

    public static void mostrarAvisoBloqueado(Context ctx, String titulo, String mensagem, int segundos) {
        // Forcado em 10s para consistencia dos avisos
        mostrarInterno(ctx, titulo, mensagem, SEGUNDOS_BLOQUEIO);
    }

    private static void mostrarInterno(final Context ctx, String titulo, String mensagem, final int segundos) {
        if (ctx == null) return;
        if (emAndamento) return;
        emAndamento = true;

        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_aviso_bloqueado);
        dialog.setCancelable(false);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        TextView txtTitulo = dialog.findViewById(R.id.txtTituloAviso);
        TextView txtMensagem = dialog.findViewById(R.id.txtMensagemAviso);
        final Button btnEntendi = dialog.findViewById(R.id.btnEntendiAviso);

        if (txtTitulo != null) txtTitulo.setText(titulo);
        if (txtMensagem != null) txtMensagem.setText(mensagem);

        dialog.setOnDismissListener(d -> emAndamento = false);

        if (btnEntendi != null) {
            btnEntendi.setEnabled(false);
            final Handler handler = new Handler(Looper.getMainLooper());
            final int[] restantes = { segundos };
            btnEntendi.setText("Entendi (" + restantes[0] + "s)");

            Runnable tick = new Runnable() {
                @Override
                public void run() {
                    restantes[0]--;
                    if (restantes[0] > 0) {
                        btnEntendi.setText("Entendi (" + restantes[0] + "s)");
                        handler.postDelayed(this, 1000);
                    } else {
                        btnEntendi.setText("Entendi");
                        btnEntendi.setEnabled(true);
                        btnEntendi.setOnClickListener(v -> dialog.dismiss());
                    }
                }
            };
            handler.postDelayed(tick, 1000);
        }

        dialog.show();
    }
}
