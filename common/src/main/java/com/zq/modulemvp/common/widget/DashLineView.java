package com.zq.modulemvp.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.zq.modulemvp.common.R;

/**
 * desc
 * author zhouqi
 * data 2020/6/23
 */
public class DashLineView extends View {
    private Paint mDashPaint;
    private Rect mRect;
    private Path path;
    private int mWidth;
    private int mHeight;

    public DashLineView(Context context) {
        super(context);
        init(null, 0);
    }

    public DashLineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DashLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    @SuppressLint({"InlinedApi", "NewApi"})
    private void init(AttributeSet attrs, int defStyleAttr) {
        if (Build.VERSION.SDK_INT >= 11) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.DashLineView, defStyleAttr, 0);
        int dashColor = ta.getColor(R.styleable.DashLineView_dashlineColor, Color.BLACK);
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, metrics);
        float dashGap = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, metrics);
        float dashWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, metrics);
        mDashPaint = new Paint();
        mDashPaint.setColor(dashColor);
        mDashPaint.setStyle(Paint.Style.STROKE);
        mDashPaint.setStrokeWidth(width);
        mDashPaint.setAntiAlias(true);
        //DashPathEffect是Android提供的虚线样式API，具体的使用可以参考下面的介绍
        mDashPaint.setPathEffect(new DashPathEffect(new float[]{dashWidth, dashGap}, 0));
        mRect = new Rect();
        path = new Path();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //取出线条的位置（位置的定义放在XML的layout中，具体如下xml文件所示）
        mRect.left = left;
        mRect.top = top;
        mRect.right = right;
        mRect.bottom = bottom;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        path.reset();
        path.moveTo(0, mHeight / 10);
        path.lineTo(mWidth, mHeight / 10);
        canvas.drawPath(path, mDashPaint);
    }
}

