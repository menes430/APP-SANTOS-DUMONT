package com.santosdumont;

import android.app.Application;

import com.santosdumont.utils.LogHelper;

public class SantosDumontApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Captura QUALQUER crash do app e salva em .txt
        final Thread.UncaughtExceptionHandler handlerOriginal =
                Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            try {
                LogHelper.salvarErro(
                        getApplicationContext(),
                        "CRASH_GLOBAL",
                        "App fechou inesperadamente",
                        throwable
                );
            } catch (Exception ignored) {}

            // Repassa pro handler original (o Android mostra o "app parou")
            if (handlerOriginal != null) {
                handlerOriginal.uncaughtException(thread, throwable);
            }
        });
    }
}