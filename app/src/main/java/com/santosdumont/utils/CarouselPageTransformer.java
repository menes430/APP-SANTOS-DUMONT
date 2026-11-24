package com.santosdumont.utils;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

/**
 * Carrossel: pagina central maior/opaca, laterais menores/translucidas.
 * Rollback via ThemePrefs.useCarousel(ctx).
 */
public class CarouselPageTransformer implements ViewPager2.PageTransformer {

    private static final float MIN_SCALE = 0.82f;
    private static final float MIN_ALPHA = 0.55f;
    private static final float MIN_TRANSLATION = -40f;

    @Override
    public void transformPage(@NonNull View page, float position) {
        if (position < -1f) {
            page.setAlpha(0f);
            page.setScaleX(MIN_SCALE);
            page.setScaleY(MIN_SCALE);
            page.setTranslationX(0f);
        } else if (position <= 1f) {
            float scale = MIN_SCALE + (1f - MIN_SCALE) * (1f - Math.abs(position));
            float alpha = MIN_ALPHA + (1f - MIN_ALPHA) * (1f - Math.abs(position));
            page.setScaleX(scale);
            page.setScaleY(scale);
            page.setAlpha(alpha);
            page.setTranslationX(MIN_TRANSLATION * position);
        } else {
            page.setAlpha(0f);
            page.setScaleX(MIN_SCALE);
            page.setScaleY(MIN_SCALE);
            page.setTranslationX(0f);
        }
    }
}
