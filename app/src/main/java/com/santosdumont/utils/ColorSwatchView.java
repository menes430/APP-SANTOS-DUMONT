package com.santosdumont.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ColorSwatchView extends View {

    public interface OnColorClick { void onClick(int color); }

    private int cor = Color.GRAY;
    private boolean selecionado = false;
    private OnColorClick listener;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintBorda = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();

    public ColorSwatchView(Context ctx) { super(ctx); init(); }
    public ColorSwatchView(Context ctx, AttributeSet attrs) { super(ctx, attrs); init(); }
    public ColorSwatchView(Context ctx, AttributeSet attrs, int defStyle) { super(ctx, attrs, defStyle); init(); }

    private void init() {
        paint.setStyle(Paint.Style.FILL);
        paintBorda.setStyle(Paint.Style.STROKE);
        paintBorda.setStrokeWidth(4f);
        paintBorda.setColor(0xFFFFFFFF);
        setClickable(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getWidth() <= 0 || getHeight() <= 0) return;
        float pad = 4f;
        rect.set(pad, pad, getWidth() - pad, getHeight() - pad);
        paint.setColor(cor);
        canvas.drawRoundRect(rect, 8f, 8f, paint);
        if (selecionado) canvas.drawRoundRect(rect, 8f, 8f, paintBorda);
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent event) {
        switch (event.getAction()) {
            case android.view.MotionEvent.ACTION_DOWN:
                setAlpha(0.7f);
                return true;
            case android.view.MotionEvent.ACTION_UP:
                setAlpha(1f);
                performClick();
                return true;
            case android.view.MotionEvent.ACTION_CANCEL:
                setAlpha(1f);
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        super.performClick();
        if (listener != null) listener.onClick(cor);
        return true;
    }

    public int getCor() { return cor; }
    public void setCor(int cor) { this.cor = cor; invalidate(); }
    public void setSelecionado(boolean sel) { this.selecionado = sel; invalidate(); }
    public void setOnColorClick(OnColorClick l) { this.listener = l; }
}
