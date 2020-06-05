package com.zq.modulemvp.index;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zq.modulemvp.basemvp.base.BaseActivity;
import com.zq.modulemvp.basemvp.base.Constants;

@Route(path = Constants.Index.Route.MAIN)
public class IndexMainActivity extends BaseActivity {

    @Override
    protected int contentLayout() {
        return R.layout.activity_index_main;
    }
}
