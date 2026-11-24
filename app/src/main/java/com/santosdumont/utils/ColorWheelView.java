package com.santosdumont.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Roda de cores (circulo cromatico).
 * - Desenha um arco-iris circular via SweepGradient.
 * - Permite arrastar o dedo para escolher um Hue (0-360).
 * - Mostra um indicador circular na posicao atual.
 */
public class ColorWheelView extends View {

    public interface OnHueChangeListener {
        void onHueChange(float hue);
    }

    private float hueAtual = 0f;              // 0-360
    private OnHueChangeListener listener;

    private final Paint paintRoda = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintIndicador = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintBorda = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float cx, cy, raioExterno, raioInterno, raioIndicador;

    public ColorWheelView(Context ctx) {
        super(ctx);
        init();
    }

    public ColorWheelView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        init();
    }

    public ColorWheelView(Context ctx, AttributeSet attrs, int defStyle) {
        super(ctx, attrs, defStyle);
        init();
    }

    private void init() {
        // Cores do arco-iris (6 paradas: vermelho -> amarelo -> verde -> ciano -> azul -> magenta -> vermelho)
        int[] cores = new int[] {
                Color.RED,
                Color.YELLOW,
                Color.GREEN,
                Color.CYAN,
                Color.BLUE,
                Color.MAGENTA,
                Color.RED
        };

        paintRoda.setStyle(Paint.Style.FILL);

        paintIndicador.setStyle(Paint.Style.FILL);
        paintIndicador.setColor(Color.WHITE);

        paintBorda.setStyle(Paint.Style.STROKE);
        paintBorda.setStrokeWidth(6f);
        paintBorda.setColor(0xFF000000);

        // SweepGradient (sera configurado em onSizeChanged quando soubermos o centro)
        setSweepGradient(cores);
    }

    private int[] coresCache;

    private void setSweepGradient(int[] cores) {
        this.coresCache = cores;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cx = w / 2f;
        cy = h / 2f;
        raioExterno = Math.min(w, h) / 2f - 10f;
        raioInterno = raioExterno - 60f;    // espessura da roda
        raioIndicador = 18f;

        // Configura SweepGradient com o centro calculado
        SweepGradient sg = new SweepGradient(cx, cy, coresCache, null);
        paintRoda.setShader(sg);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Desenha a roda colorida (circulo cheio)
        canvas.drawCircle(cx, cy, raioExterno, paintRoda);

        // Desenha o miolo (furo central - fundo escuro)
        Paint furo = new Paint(Paint.ANTI_ALIAS_FLAG);
        furo.setStyle(Paint.Style.FILL);
        furo.setColor(0xFF1A1A1A);
        canvas.drawCircle(cx, cy, raioInterno, furo);

        // Desenha o indicador na posicao do hue atual
        float anguloRad = (float) Math.toRadians(hueAtual);
        float raioMedio = (raioExterno + raioInterno) / 2f;
        float ix = cx + (float) Math.cos(anguloRad) * raioMedio;
        float iy = cy + (float) Math.sin(anguloRad) * raioMedio;

        // Borda preta do indicador
        canvas.drawCircle(ix, iy, raioIndicador + 3f, paintBorda);
        // Indicador branco
        canvas.drawCircle(ix, iy, raioIndicador, paintIndicador);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();

                float dx = x - cx;
                float dy = y - cy;

                // Calcula o angulo em graus (0-360)
                double angulo = Math.toDegrees(Math.atan2(dy, dx));
                if (angulo < 0) angulo += 360;

                hueAtual = (float) angulo;
                if (listener != null) listener.onHueChange(hueAtual);
                invalidate();
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                performClick();
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    // ============================================================
    // API publica
    // ============================================================

    public float getHue() {
        return hueAtual;
    }

    public void setHue(float hue) {
        this.hueAtual = ((hue % 360) + 360) % 360;
        invalidate();
    }

    public void setOnHueChangeListener(OnHueChangeListener l) {
        this.listener = l;
    }

    // F3/C1: permite que subclasses (ex.: ColorSquareView) disparem o listener
    protected void notifyHue(float hue) {
        if (listener != null) listener.onHueChange(hue);
    }
}