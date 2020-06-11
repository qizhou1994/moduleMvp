package com.zq.modulemvp.index;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.zq.modulemvp.basemvp.base.BaseFragment;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MineFragment extends BaseFragment {

    public static MineFragment newInstance() {
        Bundle args = new Bundle();
        MineFragment fragment = new MineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int contentLayout() {
        return R.layout.fragment_mine;
    }
}
