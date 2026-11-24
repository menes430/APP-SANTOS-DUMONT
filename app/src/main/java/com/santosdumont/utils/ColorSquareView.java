package com.santosdumont.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Seletor QUADRADO de cores (HSV 2D).
 * X = hue (0-360), Y = saturacao (0-100). Brilho vem da BrightnessBarView.
 *
 * PERF: usa Bitmap cache. So regenera quando brilho muda.
 * Sem isso, 2400 drawRect/frame = ANR (visto no log de 24/10).
 */
public class ColorSquareView extends ColorWheelView {

    public interface OnSatChangeListener {
        void onSatChange(float sat);
    }

    private float satAtual = 100f;
    private float brilhoAtual = 100f;
    private OnSatChangeListener satListener;

    private final Paint paintQuadrado = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rectQuad = new RectF();

    private Bitmap cacheBitmap;
    private int cacheBrilho = -1;
    private int cacheW = -1, cacheH = -1;

    public ColorSquareView(Context ctx) { super(ctx); }
    public ColorSquareView(Context ctx, AttributeSet attrs) { super(ctx, attrs); }
    public ColorSquareView(Context ctx, AttributeSet attrs, int defStyle) { super(ctx, attrs, defStyle); }

    @Override
    protected void onDraw(Canvas canvas) {
        // NAO chama super.onDraw: evita a roda e o indicador do ColorWheelView
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        float pad = 12f;
        rectQuad.set(pad, pad, w - pad, h - pad);

        int brInt = Math.round(brilhoAtual);
        if (cacheBitmap == null || cacheBrilho != brInt || cacheW != w || cacheH != h) {
            regerarCache(w, h);
        }

        // Desenha o bitmap com cantos arredondados
        android.graphics.Path path = new android.graphics.Path();
        float raio = 16f;
        path.addRoundRect(rectQuad, raio, raio, android.graphics.Path.Direction.CW);
        canvas.save();
        canvas.clipPath(path);
        canvas.drawBitmap(cacheBitmap, null, rectQuad, paintQuadrado);
        canvas.restore();
    }

    private void regerarCache(int w, int h) {
        int bw = Math.max(1, (int) rectQuad.width());
        int bh = Math.max(1, (int) rectQuad.height());
        if (cacheBitmap != null && !cacheBitmap.isRecycled()) cacheBitmap.recycle();
        cacheBitmap = Bitmap.createBitmap(bw, bh, Bitmap.Config.ARGB_8888);
        cacheBrilho = Math.round(brilhoAtual);
        cacheW = w;
        cacheH = h;

        final int passosHue = 60;
        final int passosSat = 40;
        float largHue = bw / (float) passosHue;
        float largSat = bh / (float) passosSat;

        Canvas c = new Canvas(cacheBitmap);
        Paint p = new Paint();
        for (int i = 0; i < passosHue; i++) {
            float hue = (i / (float) passosHue) * 360f;
            for (int j = 0; j < passosSat; j++) {
                float sat = (1f - j / (float) passosSat) * 100f;
                int cor = ColorTheme.hslToRgb(hue, sat, brilhoAtual);
                p.setColor(cor);
                c.drawRect(
                        i * largHue,
                        j * largSat,
                        (i + 1) * largHue,
                        (j + 1) * largSat,
                        p);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE: {
                int w = getWidth();
                int h = getHeight();
                if (w <= 0 || h <= 0) return true;
                float pad = 12f;
                float hx = Math.max(pad, Math.min(w - pad, event.getX()));
                float hy = Math.max(pad, Math.min(h - pad, event.getY()));
                float novoHue = ((hx - pad) / (w - 2 * pad)) * 360f;
                novoHue = ((novoHue % 360) + 360) % 360;
                float novaSat = (1f - (hy - pad) / (h - 2 * pad)) * 100f;
                novaSat = Math.max(0, Math.min(100, novaSat));
                setHue(novoHue);
                satAtual = novaSat;
                notifyHue(novoHue);
                if (satListener != null) satListener.onSatChange(novaSat);
                invalidate();
                return true;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                performClick();
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() { super.performClick(); return true; }

    public float getSat() { return satAtual; }
    public void setSat(float sat) { this.satAtual = Math.max(0, Math.min(100, sat)); invalidate(); }
    public void setBrilho(float b) {
        int novo = Math.round(Math.max(0, Math.min(100, b)));
        if (novo != cacheBrilho) {
            this.brilhoAtual = novo;
            invalidate();
        }
    }
    public void setOnSatChangeListener(OnSatChangeListener l) { this.satListener = l; }
}
