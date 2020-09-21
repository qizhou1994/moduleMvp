package com.zq.modulemvp.index;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.zq.modulemvp.basemvp.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import me.yokeyword.fragmentation.ISupportFragment;


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

    private ISupportFragment[] mFragments = new ISupportFragment[3];

    private int mCurrentPos = 0;
    private List<MenuItem> menus = new ArrayList<>();

    public static MainFragment newInstance() {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int contentLayout() {
        return R.layout.fragment_main;
    }

    @Override
    protected void init(View view) {
        super.init(view);
        initFragments();
        initBottomNav();
    }

    @Override
    public void setStatusBar() {
//        super.setStatusBar();
    }

    private void initBottomNav() {
        disableAnim();
        bnve.setOnNavigationItemSelectedListener(item -> {
            int targetIndex = menus.indexOf(item);
            if (mCurrentPos == targetIndex) {
                return false;
            }
         /*   if (startLoginIfNeed()) {
                toast("需要登录");
                return false;
            }*/
            showHideFragment(mFragments[targetIndex], mFragments[mCurrentPos]);
            mCurrentPos = targetIndex;
            return true;
        });
        // fill menus
        menus.clear();
        Menu menu = bnve.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            menus.add(menu.getItem(i));
        }
    }

    /**
     * disable bottom navi-bar animation
     */
    private void disableAnim() {
        bnve.enableAnimation(false);
        bnve.enableItemShiftingMode(false);
        // disable ripple
        bnve.setItemBackground(null);
    }

    private void initFragments() {
        mFragments[0] = findFragment(HomeFragment.class);
        if (mFragments[0] == null) {
            mFragments[0] = HomeFragment.newInstance();
            mFragments[1] = OtherFragment.newInstance();
            mFragments[2] = MineFragment.newInstance();

            loadMultipleRootFragment(R.id.fl_index_fragment_container, 0,
                    mFragments[0],
                    mFragments[1],
                    mFragments[2]);
        } else {
            mFragments[0] = findFragment(HomeFragment.class);
            mFragments[1] = findFragment(OtherFragment.class);
            mFragments[2] = findFragment(MineFragment.class);
        }
    }

    @Override
    protected boolean disableSwipeBack() {
        return true;
    }
}
