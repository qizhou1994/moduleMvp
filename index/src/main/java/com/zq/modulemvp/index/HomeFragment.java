package com.zq.modulemvp.index;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hitomi.tilibrary.style.index.NumberIndexIndicator;
import com.hitomi.tilibrary.style.progress.ProgressBarIndicator;
import com.hitomi.tilibrary.transfer.TransferConfig;
import com.hitomi.tilibrary.transfer.Transferee;
import com.vansz.picassoimageloader.PicassoImageLoader;
import com.zq.modulemvp.basemvp.base.BaseMvpFragment;
import com.zq.modulemvp.common.adapter.common.CommonRecyclerAdapter;
import com.zq.modulemvp.common.adapter.common.ViewHolder;
import com.zq.modulemvp.common.util.ImageLoadUtil;
import com.zq.modulemvp.index.bean.RspNewsListBean;
import com.zq.modulemvp.index.contract.ContractHome;
import com.zq.modulemvp.index.presenter.HomePresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends BaseMvpFragment<HomePresenter> implements ContractHome.View {


    Transferee transferee;
    @BindView(R2.id.rv_list)
    RecyclerView rvList;

    CommonRecyclerAdapter<RspNewsListBean.ResultBean.DataBean> commonRecyclerAdapter;

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
        transferee = Transferee.getDefault(getContext());

        //todo need perf
        commonRecyclerAdapter = new CommonRecyclerAdapter<RspNewsListBean.ResultBean.DataBean>(getContext(), new ArrayList<>(), R.layout.home_rv_news_item) {
            @Override
            public void convert(ViewHolder holder, RspNewsListBean.ResultBean.DataBean item) {
                holder.setText(R.id.tv_title, item.getTitle());
                holder.setText(R.id.tv_category, item.getCategory());
                holder.setText(R.id.tv_date, item.getDate());
                holder.setText(R.id.tv_author_name, item.getAuthor_name());

                holder.setOnItemClickListener(v -> {
                    Uri uri = Uri.parse(item.getUrl());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);

                });

            }

            @Override
            public void convert(ViewHolder holder, RspNewsListBean.ResultBean.DataBean item, int position) {
                super.convert(holder, item, position);
                List<String> list = new ArrayList<>();
                if (item.getThumbnail_pic_s() != null) {
                    list.add(item.getThumbnail_pic_s());
                }
                if (item.getThumbnail_pic_s02() != null) {
                    list.add(item.getThumbnail_pic_s02());
                }
                if (item.getThumbnail_pic_s03() != null) {
                    list.add(item.getThumbnail_pic_s03());
                }
                RecyclerView recyclerView = holder.getView(R.id.rv_photos);
                GridLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 3,
                        RecyclerView.VERTICAL, false);
                recyclerView.setLayoutManager(linearLayoutManager);
                CommonRecyclerAdapter<String> commonRecyclerAdapter = new CommonRecyclerAdapter<String>(getContext(), list, R.layout.iv_item) {
                    @Override
                    public void convert(ViewHolder holder, String item) {
                        ImageView imageView = holder.getView(R.id.iv);
                        ImageLoadUtil.loadImgByUrl(getContext(), imageView, item);
                    }

                    @Override
                    public void onBindViewHolder(ViewHolder holder, int pos) {
                        super.onBindViewHolder(holder, pos);
                        holder.setOnItemClickListener(v -> {
                            transferee.apply(getBuilder(position)
                                    .setNowThumbnailIndex(pos)
                                    .setSourceUrlList(list)
                                    .bindRecyclerView(recyclerView, R.id.iv)
                            ).show();
                        });
                    }
                };
                recyclerView.setAdapter(commonRecyclerAdapter);
            }
        };
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false);
        rvList.setLayoutManager(linearLayoutManager);
        rvList.setAdapter(commonRecyclerAdapter);

        mPresenter.getNewList("top");
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
            commonRecyclerAdapter.setDataList(rspNewsListBean.getResult().getData());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (transferee != null) {
            transferee.destroy();
        }
    }
}
