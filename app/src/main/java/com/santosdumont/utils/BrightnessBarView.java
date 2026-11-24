package com.santosdumont.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class BrightnessBarView extends View {

    public interface OnBrightnessChangeListener {
        void onBrightnessChange(int brightness);
    }

    private int hue = 0;
    private int sat = 100;
    private int brightness = 100;
    private OnBrightnessChangeListener listener;

    private final Paint paintBarra = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintIndicador = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rectBarra = new RectF();

    public BrightnessBarView(Context ctx) { super(ctx); init(); }
    public BrightnessBarView(Context ctx, AttributeSet attrs) { super(ctx, attrs); init(); }
    public BrightnessBarView(Context ctx, AttributeSet attrs, int defStyle) { super(ctx, attrs, defStyle); init(); }

    private void init() {
        paintBarra.setStyle(Paint.Style.FILL);
        paintIndicador.setStyle(Paint.Style.FILL);
        paintIndicador.setColor(Color.WHITE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        atualizarGradiente();
    }

    private void atualizarGradiente() {
        if (getWidth() <= 0 || getHeight() <= 0) return;
        float pad = 8f;
        rectBarra.set(pad, pad, getWidth() - pad, getHeight() - pad);
        int corTopo = ColorTheme.hslToRgb(hue, sat, 100);
        int corBase = ColorTheme.hslToRgb(hue, sat, 0);
        paintBarra.setShader(new LinearGradient(rectBarra.left, rectBarra.top, rectBarra.left, rectBarra.bottom, corTopo, corBase, Shader.TileMode.CLAMP));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getWidth() <= 0 || getHeight() <= 0) return;
        canvas.drawRoundRect(rectBarra, 12f, 12f, paintBarra);
        float fy = rectBarra.top + (1f - brightness / 100f) * rectBarra.height();
        float pad = 4f;
        canvas.drawRect(rectBarra.left - pad, fy - 3f, rectBarra.right + pad, fy + 3f, paintIndicador);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE: {
                if (getHeight() <= 0) return true;
                float y = Math.max(rectBarra.top, Math.min(rectBarra.bottom, event.getY()));
                float prop = (y - rectBarra.top) / rectBarra.height();
                int novo = Math.round((1f - prop) * 100f);
                novo = Math.max(0, Math.min(100, novo));
                brightness = novo;
                if (listener != null) listener.onBrightnessChange(novo);
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

    public int getBrightness() { return brightness; }
    public void setBrightness(int b) { this.brightness = Math.max(0, Math.min(100, b)); invalidate(); }
    public void setHueSat(int hue, int sat) { this.hue = ((hue % 360) + 360) % 360; this.sat = Math.max(0, Math.min(100, sat)); atualizarGradiente(); invalidate(); }
    public void setOnBrightnessChangeListener(OnBrightnessChangeListener l) { this.listener = l; }
}
