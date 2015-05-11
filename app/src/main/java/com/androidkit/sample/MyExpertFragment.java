package com.androidkit.sample;

import android.os.Handler;

import com.androidkit.base.ListHttpFragment;
import com.androidkit.util.GenericAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GYJC on 2015/4/30.
 */
public class MyExpertFragment extends ListHttpFragment<Expert, MyExpertAdaper> {
    private boolean usedRetry = false;
    private int mIndex = 1;

    @Override
    protected GenericAdapter<Expert> initGenericAdapter() {
        return new MyExpertAdaper();
    }

    @Override
    protected void doService() {
        mAdapter.setData(createList(mIndex));
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
        mAdapter.getData().add(0, createData());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onLoadMoreListData() {
        mAdapter.addData(createList(mIndex));
    }

    @Override
    protected void onClickEmptyView() {

    }

    @Override
    protected void onClickRetryView() {

    }

    private List<Expert> createList(int index) {
        ArrayList<Expert> list = new ArrayList<Expert>();
        for(int i = index; i <= index+19; i++) {
            Expert expert = new Expert();
            expert.setId(i+"");
            expert.setPortraitUrl("https://avatars3.githubusercontent.com/u/30177?v=3&s=200");
            expert.setName("张仲景" + i);
            expert.setTitle("主治医生" + i);
            expert.setHospital("北京协和医院" + i);
            expert.setSection("呼吸道" + i);
            list.add(expert);
        }
        mIndex = mIndex + list.size();
        return list;
    }

    private Expert createData() {
        int index = 0;
        Expert expert = new Expert();
        expert.setId(index+"");
        expert.setPortraitUrl("https://avatars3.githubusercontent.com/u/30177?v=3&s=200");
        expert.setName("张仲景" + index);
        expert.setTitle("主治医生" + index);
        expert.setHospital("北京协和医院" + index);
        expert.setSection("呼吸道" + index);
        return expert;
    }

}
