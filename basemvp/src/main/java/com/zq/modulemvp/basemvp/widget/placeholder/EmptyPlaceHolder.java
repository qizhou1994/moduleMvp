package com.zq.modulemvp.basemvp.widget.placeholder;

import android.content.Context;
import android.view.View;

import com.zq.modulemvp.basemvp.R;

/**
 * desc
 * author zhouqi
 * data 2020/6/4
 */
public class EmptyPlaceHolder extends PlaceHolder {
    @Override
    public int onCreateView() {
        return R.layout.placeholder_empty_layout;
    }

    @Override
    public void onViewCreated(Context context, View view) {

    }

    @Override
    public void onAttach() {

    }

    @Override
    public void onDetach() {

    }
}
