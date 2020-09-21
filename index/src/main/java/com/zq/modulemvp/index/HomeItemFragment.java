package com.zq.modulemvp.index;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.utils.TextUtils;
import com.hitomi.tilibrary.style.index.NumberIndexIndicator;
import com.hitomi.tilibrary.style.progress.ProgressBarIndicator;
import com.hitomi.tilibrary.transfer.TransferConfig;
import com.vansz.picassoimageloader.PicassoImageLoader;
import com.zq.modulemvp.basemvp.base.BaseMvpFragment;
import com.zq.modulemvp.common.adapter.common.CommonRecyclerAdapter;
import com.zq.modulemvp.common.adapter.common.ViewHolder;
import com.zq.modulemvp.common.util.ImageLoadUtil;
import com.zq.modulemvp.index.bean.RspNewsListBean;
import com.zq.modulemvp.index.contract.ContractHomeItem;
import com.zq.modulemvp.index.presenter.HomeItemPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author zhouqi
 * @desc
 * @data 2020/9/21
 */
public class HomeItemFragment extends BaseMvpFragment<HomeItemPresenter> implements ContractHomeItem.View {

    @BindView(R2.id.rv_list)
    RecyclerView rvList;
    @BindView(R2.id.tv_result)
    TextView tvResult;
    private List<RspNewsListBean.ResultBean> datas;
    CommonRecyclerAdapter<RspNewsListBean.ResultBean> commonRecyclerAdapter;

    public static final String HOME_ITEM_TYPE = "home_item_type";

    public static HomeItemFragment newInstance(String type) {
        Bundle args = new Bundle();
        HomeItemFragment fragment = new HomeItemFragment();
        args.putString(HOME_ITEM_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void attachView(HomeItemPresenter presenter) {
        presenter.attachView(this);
    }

    @Override
    public int contentLayout() {
        return R.layout.fragment_home_item;
    }

    @Override
    protected void init(View view) {
        super.init(view);
        if (datas == null) {
            datas = new ArrayList<>();
        }

        //todo need perf
        commonRecyclerAdapter = new CommonRecyclerAdapter<RspNewsListBean.ResultBean>(getContext(), datas, R.layout.home_rv_news_item) {
            @Override
            public void convert(ViewHolder holder, RspNewsListBean.ResultBean item) {
                holder.setText(R.id.tv_title, item.getTitle());
                holder.setText(R.id.tv_content, item.getContent());
                holder.setText(R.id.tv_date, item.getPdate_src());
                holder.setText(R.id.tv_author_name, item.getSrc());
                ImageView imageView = holder.getView(R.id.iv_show);
                if (!TextUtils.isEmpty(item.getImg())) {
                    imageView.setVisibility(View.VISIBLE);
                    ImageLoadUtil.loadImgByUrl(getContext(), imageView, item.getImg());
                } else {
                    imageView.setVisibility(View.GONE);
                }
            }
        };
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false);
        rvList.setLayoutManager(linearLayoutManager);
        rvList.setAdapter(commonRecyclerAdapter);
        String type = getArguments().getString(HOME_ITEM_TYPE);
        mPresenter.getNewList(type + "新闻");
    }


    private TransferConfig.Builder getBuilder(int pos) {
        TransferConfig.Builder builder = TransferConfig.build()
                .setProgressIndicator(new ProgressBarIndicator())
                .setIndexIndicator(new NumberIndexIndicator())
                .setImageLoader(PicassoImageLoader.with(getContext()));
        if (pos == 4) {
            builder.enableHideThumb(false);
        } else if (pos == 5) {
            builder.enableJustLoadHitPage(true);
        } else if (pos == 6) {
            builder.enableDragPause(true);
        }
        return builder;
    }

    @Override
    public void setNewListView(RspNewsListBean rspNewsListBean) {
        if (commonRecyclerAdapter != null && rspNewsListBean != null) {
            tvResult.setVisibility(View.GONE);
            rvList.setVisibility(View.VISIBLE);
            commonRecyclerAdapter.setDataList(rspNewsListBean.getResult());
        } else {
            tvResult.setVisibility(View.VISIBLE);
            tvResult.setText("暂无数据");
            rvList.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected boolean disableSwipeBack() {
        return true;
    }

    @Override
    public void setStatusBar() {
//        super.setStatusBar();
    }
}
