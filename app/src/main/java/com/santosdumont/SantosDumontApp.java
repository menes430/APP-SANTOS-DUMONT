package com.santosdumont;

import android.app.Application;

import com.santosdumont.utils.AppLifecycleObserver;
import com.santosdumont.utils.AppLogger;

public class SantosDumontApp extends Application {

    private AppLifecycleObserver lifecycleObserver;

    @Override
    public void onCreate() {
        super.onCreate();

        // Instala o AppLogger (captura erros + crashes)
        AppLogger.instalar(this);

        // Mantem o handler original (para o Android tratar o crash)
        final Thread.UncaughtExceptionHandler handlerOriginal =
                Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            try {
                AppLogger.registrar(getApplicationContext(), "CRASH_GLOBAL",
                        "App fechou inesperadamente: " + throwable.toString());
            } catch (Exception ignored) {}
            if (handlerOriginal != null) {
                handlerOriginal.uncaughtException(thread, throwable);
            }
        });

        // Registra o observador de ciclo de vida (bloqueio educativo)
        lifecycleObserver = new AppLifecycleObserver(this);
        registerActivityLifecycleCallbacks(lifecycleObserver);

        // Coleta: registra abertura do app
        com.santosdumont.utils.DataCollector.registrar(this, "APP_ABRIU", "");

        // Envia dados pro Firebase (a cada 6 horas)
        com.santosdumont.utils.FirebaseUploader.enviarSeNecessario(this);
    }

    public void liberarBloqueio() {
        if (lifecycleObserver != null) {
            lifecycleObserver.liberarBloqueio();
        }
    }

    public void resetarSaidas() {
        if (lifecycleObserver != null) {
            lifecycleObserver.resetarSaidas();
        }
    }
}