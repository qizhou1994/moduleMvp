package com.zq.modulemvp.index;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zq.modulemvp.basemvp.base.BaseFragment;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OtherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OtherFragment extends BaseFragment {


    public static OtherFragment newInstance() {
        Bundle args = new Bundle();
        OtherFragment fragment = new OtherFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public int contentLayout() {
        return R.layout.fragment_other;
    }
}
