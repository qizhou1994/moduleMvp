package com.zq.modulemvp.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.zq.modulemvp.common.R;

/**
 * @author qizhou
 */
public class ShadowImageView extends ImageView {
    private int shadowColor;
    private int shadowWidth;
    private float shadowAlpha;
    private int imgFramWidth;
    private int imgFramColor;

    public ShadowImageView(Context context) {
        super(context);
        init(context, null);
    }

    public ShadowImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ShadowImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray attributes = context.obtainStyledAttributes(attrs,
                    R.styleable.ShadowImageView);
            shadowColor = attributes.getColor(R.styleable.ShadowImageView_shadow_color, ContextCompat.getColor(context, R.color.image_fram_color));
            //dp
            shadowWidth = attributes.getInt(R.styleable.ShadowImageView_shadow_width, 2);
            imgFramWidth = attributes.getInt(R.styleable.ShadowImageView_image_fram_width, 1);
            imgFramColor = attributes.getInt(R.styleable.ShadowImageView_image_fram_color, ContextCompat.getColor(context, R.color.image_shadow_color));

            shadowAlpha = attributes.getFloat(R.styleable.ShadowImageView_shadow_alpha, 1f);

            setPadding(shadowWidth, shadowWidth, shadowWidth, shadowWidth);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
// 画边框
        Rect rect1 = getRect(canvas);
        Paint paint = new Paint();
    /*    paint.setColor(imgFramColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(imgFramWidth);
        canvas.drawRect(rect1, paint);*/
// 画边框
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(shadowColor);
        paint.setStrokeWidth(shadowWidth);
        paint.setAlpha((int) (shadowAlpha * 255));
        Rect rect = canvas.getClipBounds();

        canvas.drawRect(rect, paint);

    }

    Rect getRect(Canvas canvas) {
        Rect rect = canvas.getClipBounds();
        rect.bottom -= getPaddingBottom();
        rect.right -= getPaddingRight();
        rect.left += getPaddingLeft();
        rect.top += getPaddingTop();
        return rect;
    }

}
