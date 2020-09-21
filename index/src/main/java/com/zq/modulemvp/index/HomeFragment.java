package com.zq.modulemvp.index;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.zq.modulemvp.basemvp.base.BaseMvpFragment;
import com.zq.modulemvp.index.bean.RspNewsListBean;
import com.zq.modulemvp.index.contract.ContractHome;
import com.zq.modulemvp.index.presenter.HomePresenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends BaseMvpFragment<HomePresenter> implements ContractHome.View {


    @BindView(R2.id.tl_contract)
    TabLayout tlContract;
    @BindView(R2.id.vp_list)
    ViewPager vpList;

    private FragmentStatePagerAdapter fragmentStatePagerAdapter;

    private List<String> listTitle;
    private String[] titlesType = new String[]{"top","shehui","guonei","guoji","yule","tiyu","junshi","keji","caijing","shishang"};
    private String[] titles = new String[]{"推荐","社会","国内","国际","娱乐","体育","军事","科技","财经","时尚"};
    private List<HomeItemFragment> muTypeFragments;

    public static HomeFragment newInstance() {
        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int contentLayout() {
        return R.layout.fragment_home;
    }

    @Override
    protected void attachView(HomePresenter presenter) {
        presenter.attachView(this);
    }

    @Override
    protected void init(View view) {
        super.init(view);
        initVp();
    }

    private void initVp() {
        muTypeFragments = new ArrayList<>();
        listTitle = new ArrayList<>();
        listTitle.addAll(Arrays.asList(titles));
        for (String type: titles){
            muTypeFragments.add(HomeItemFragment.newInstance(type));
        }
        fragmentStatePagerAdapter = new FragmentStatePagerAdapter(getChildFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return muTypeFragments.get(position);
            }

            @Override
            public int getCount() {
                return muTypeFragments.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return listTitle.get(position);
            }

            @Override
            public int getItemPosition(@NonNull Object object) {
                return POSITION_NONE;
            }


        };
        vpList.setAdapter(fragmentStatePagerAdapter);
        tlContract.setupWithViewPager(vpList);

    }

    @Override
    public void setNewListView(RspNewsListBean rspNewsListBean) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected boolean disableSwipeBack() {
        return true;
    }
}
