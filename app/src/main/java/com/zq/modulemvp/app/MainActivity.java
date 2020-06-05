package com.zq.modulemvp.app;

import com.alibaba.android.arouter.launcher.ARouter;
import com.zq.modulemvp.basemvp.base.BaseActivity;
import com.zq.modulemvp.basemvp.base.Constants;


public class MainActivity extends BaseActivity {

    @Override
    protected int contentLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        super.init();
//        navigate(Constants.Index.Route.MAIN).navigation();
        ARouter.getInstance().build(Constants.Index.Route.MAIN).navigation();
    }
}
