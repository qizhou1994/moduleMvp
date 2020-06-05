package com.zq.modulemvp.basemvp.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zq.modulemvp.basemvp.R;

/**
 * desc
 * author zhouqi
 * data 2020/6/3
 */
public class AppNaviBar extends RelativeLayout {

    private static final int SHADOW_HEIGHT_DEF = 4;
    private TextView mTitleTv;
    private ImageView mLeftIcon;
    private ImageView mRightIcon;

    private FrameLayout mLeftContainer;
    private FrameLayout mMiddleContainer;
    private FrameLayout mRightContainer;

    private int mLeftContentId;
    private int mMiddleContentId;
    private int mRightContentId;

    private int mShadowWidth;
    private int mShadowHeight;

    /** disable shadow */
    private final boolean shadowEnable = false;

    public AppNaviBar(Context context) {
        super(context);
        init(null, 0);
    }

    public AppNaviBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppNaviBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.app_navi_bar, this, true);

        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.AppNaviBar, defStyleAttr, 0);

        String title = ta.getString(R.styleable.AppNaviBar_android_text);
        int color = ta.getColor(R.styleable.AppNaviBar_android_textColor, Color.BLACK);
        int backgroundRes =  ta.getResourceId(R.styleable.AppNaviBar_android_background, R.color.window_background);
        Drawable leftIcon = ta.getDrawable(R.styleable.AppNaviBar_leftIcon);
        Drawable rightIcon = ta.getDrawable(R.styleable.AppNaviBar_rightIcon);
        mLeftContentId = ta.getResourceId(R.styleable.AppNaviBar_leftContent, -1);
        mMiddleContentId = ta.getResourceId(R.styleable.AppNaviBar_middleContent, -1);
        mRightContentId = ta.getResourceId(R.styleable.AppNaviBar_rightContent, -1);
        ta.recycle();

        mLeftContainer = view.findViewById(R.id.app_nav_bar_left);
        mMiddleContainer = view.findViewById(R.id.app_nav_bar_middle);
        mRightContainer = view.findViewById(R.id.app_nav_bar_right);

        mLeftIcon = mLeftContainer.findViewById(R.id.app_nav_bar_icon_left);
        mTitleTv = mMiddleContainer.findViewById(R.id.app_nav_bar_title);
        mRightIcon = mRightContainer.findViewById(R.id.app_nav_bar_icon_right);
        setTitle(title);
        if (leftIcon != null) {
            setLeftIcon(leftIcon);
        }
        if (rightIcon != null) {
            setRightIcon(rightIcon);
        }
        mShadowWidth = LayoutParams.MATCH_PARENT;
        mShadowHeight = SHADOW_HEIGHT_DEF;
        mTitleTv.setTextColor(color);
        setBackgroundColor(getContext().getResources().getColor(backgroundRes));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mLeftContentId > 0) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View left = inflater.inflate(mLeftContentId, this, false);
            setLeftView(left);
        }
        if (mMiddleContentId > 0) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View middle = inflater.inflate(mMiddleContentId, this, false);
            // View middle = findViewById(mMiddleContentId);
            setMiddleView(middle);
        }
        if (mRightContentId > 0) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View right = inflater.inflate(mRightContentId, this, false);
            setRightView(right);
        }

        if (shadowEnable) {
            ViewParent vp = getParent();
            if (vp instanceof RelativeLayout) {
                handleRelativeLayout((RelativeLayout) vp);
            } else if (vp instanceof FrameLayout) {
                handleFrameLayout((FrameLayout) vp);
            }
        }
    }

    private void handleRelativeLayout(RelativeLayout parent) {
        // int index = getAppNaviIndex(parent);
        View shadow = getShadow();
        RelativeLayout.LayoutParams lp = new LayoutParams(mShadowWidth, mShadowHeight);
        lp.addRule(BELOW, getId());
        shadow.setLayoutParams(lp);

        parent.addView(shadow);
    }

    private void handleFrameLayout(FrameLayout parent) {
        // int index = getAppNaviIndex(parent);
        View shadow = getShadow();
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(mShadowWidth, mShadowHeight);
        lp.gravity = Gravity.START | Gravity.TOP;
        lp.topMargin = getResources().getDimensionPixelOffset(R.dimen.app_navi_bar_height);
        shadow.setLayoutParams(lp);
        parent.addView(shadow);
    }

    private int getAppNaviIndex(ViewGroup parent) {
        int count = parent.getChildCount();
        int index = -1;
        for (int i = 0;i < count; i++) {
            View v = parent.getChildAt(i);
            if (v instanceof AppNaviBar) {
                index = i;
                break;
            }
        }
        return index;
    }

    private View getShadow() {
        Drawable bottom = getResources().getDrawable(R.drawable.shadow_bottom);
        ImageView shadow = new ImageView(getContext());

        shadow.setBackground(bottom);
        shadow.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return shadow;
    }

    public void setLeftView(View left) {
        if (left != null) {
            mLeftContainer.removeAllViews();
            mLeftIcon = left.findViewById(R.id.app_nav_bar_icon_left);
            mLeftContainer.addView(left);
        }
    }

    public void setMiddleView(View middle) {
        if (middle != null) {
            if (middle.getParent() != null) {
                ViewGroup vg = (ViewGroup) middle.getParent();
                vg.removeView(middle);
            }
            ViewGroup.LayoutParams mLp = middle.getLayoutParams();
            FrameLayout.LayoutParams midLp = new FrameLayout.LayoutParams(-2, -1);
            if (mLp != null) {
                midLp.width = mLp.width;
                midLp.height = mLp.height;
            }
            midLp.gravity = Gravity.CENTER;
            middle.setLayoutParams(midLp);
            mMiddleContainer.removeAllViews();
            mMiddleContainer.addView(middle);
            // update title if need
            final CharSequence title = getTitle();
            mTitleTv = middle.findViewById(R.id.app_nav_bar_title);
            setTitle(title);
        }
    }

    public void setRightView(View right) {
        if (right != null) {
            mRightContainer.removeAllViews();
            mRightIcon = right.findViewById(R.id.app_nav_bar_icon_right);
            mRightContainer.addView(right);
        }
    }

    public void setTitle(CharSequence title) {
        if (mTitleTv != null) {
            mTitleTv.setText(title);
        }
    }

    public CharSequence getTitle() {
        if (mTitleTv != null) {
            mTitleTv.getText();
        }
        return null;
    }

    public void setLeftIcon(Drawable leftIcon) {
        if (mLeftIcon != null) {
            mLeftIcon.setImageDrawable(leftIcon);
        }
    }

    public void setRightIcon(Drawable rightIcon) {
        if (mRightIcon != null) {
            mRightIcon.setImageDrawable(rightIcon);
        }
    }

    public void setLeftIcon(int leftIconId) {
        Drawable drawable = getResources().getDrawable(leftIconId, getContext().getTheme());
        setLeftIcon(drawable);
    }

    public void setRightIcon(int rightIconId) {
        Drawable drawable = getResources().getDrawable(rightIconId, getContext().getTheme());
        setRightIcon(drawable);
    }

    public TextView getTitleView() {
        return mTitleTv;
    }

    public ImageView getLeftIcon() {
        return mLeftIcon;
    }

    public ImageView getRightIcon() {
        return mRightIcon;
    }

    public View getLeftView() {
        if (mLeftContentId > 0 && mLeftContainer.getChildCount() > 0) {
            return mLeftContainer.getChildAt(0);
        }
        return mLeftContainer;
    }

    public View getMiddleView() {
        if (mMiddleContentId > 0 && mMiddleContainer.getChildCount() > 0) {
            return mMiddleContainer.getChildAt(0);
        }
        return mMiddleContainer;
    }

    public View getRightView() {
        if (mRightContentId > 0 && mRightContainer.getChildCount() > 0) {
            return mRightContainer.getChildAt(0);
        }
        return mRightContainer;
    }
}