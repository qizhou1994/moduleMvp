package com.zq.modulemvp.index;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zq.modulemvp.basemvp.base.BaseActivity;
import com.zq.modulemvp.basemvp.base.Constants;

import me.yokeyword.fragmentation.SupportFragment;

@Route(path = Constants.Index.Route.MAIN)
public class IndexMainActivity extends BaseActivity {

    private SupportFragment rootFrag;


    @Override
    protected int contentLayout() {
        return R.layout.activity_index_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            loadRootFragment(R.id.fl, rootFrag = MainFragment.newInstance());
        } else {
            if (rootFrag == null) {
                rootFrag = findFragment(MainFragment.class);
            }
        }
    }

    @Override
    protected boolean disableSwipeBack() {
        return true;
    }
}
