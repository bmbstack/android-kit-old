package com.github.bmbstack.androidkit.sample.fragment;

import android.os.Bundle;
import android.os.Handler;

import com.github.bmbstack.androidkit.base.ListHttpFragment;
import com.github.bmbstack.androidkit.sample.adapter.DynamicAdapter;
import com.github.bmbstack.androidkit.sample.model.Dynamic;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GYJC on 2015/4/30.
 */
public class DynamicFragment extends ListHttpFragment<Dynamic, UltimateViewAdapter> {
    private boolean usedRetry = false;
    private int mIndex = 1;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setRefreshEnable(true);
        setLoadmoreEnable(true);
        setSwipeRemoveEnable(false);
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    protected UltimateViewAdapter initAdapter() {
        return new DynamicAdapter(createList(mIndex));
    }

    @Override
    protected void doService() {

        obtianData();
    }

    private void obtianData() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                showListView();
            }
        }, 3000);
    }

    @Override
    protected void onRefreshListData() {
        ((DynamicAdapter)mAdapter).add(0, createData());
    }

    @Override
    protected void onLoadMoreListData() {
        ((DynamicAdapter)mAdapter).addAll(createList(mIndex));
    }

    @Override
    protected void onClickEmptyView() {

    }

    @Override
    protected void onClickRetryView() {

    }

    private List<Dynamic> createList(int index) {
        ArrayList<Dynamic> list = new ArrayList<Dynamic>();
        for(int i = index; i <= index+19; i++) {
            Dynamic dynamic = new Dynamic();
            dynamic.setId(i + "");
            dynamic.setPortraitUrl("https://avatars3.githubusercontent.com/u/30177?v=3&s=200");
            dynamic.setName("张仲景" + i);
            dynamic.setTitle("主治医生" + i);
            dynamic.setHospital("北京协和医院" + i);
            dynamic.setSection("呼吸道" + i);
            list.add(dynamic);
        }
        mIndex = mIndex + list.size();
        return list;
    }

    private Dynamic createData() {
        int index = 0;
        Dynamic dynamic = new Dynamic();
        dynamic.setId(index + "");
        dynamic.setPortraitUrl("https://avatars3.githubusercontent.com/u/30177?v=3&s=200");
        dynamic.setName("张仲景" + index);
        dynamic.setTitle("主治医生" + index);
        dynamic.setHospital("北京协和医院" + index);
        dynamic.setSection("呼吸道" + index);
        return dynamic;
    }

}
