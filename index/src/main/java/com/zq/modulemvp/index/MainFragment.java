package com.zq.modulemvp.index;

import android.view.View;
import android.widget.FrameLayout;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.zq.modulemvp.basemvp.base.BaseFragment;

import butterknife.BindView;


/**
 * desc
 * author zhouqi
 * data 2020/6/5
 */
public class MainFragment extends BaseFragment {

    @BindView(R2.id.fl_index_fragment_container)
    FrameLayout flIndexFragmentContainer;
    @BindView(R2.id.bnve)
    BottomNavigationViewEx bnve;

    @Override
    public int contentLayout() {
        return R.layout.fragment_main;
    }

    @Override
    protected void init(View view) {
        super.init(view);
    }
}
