package com.androidkit.base;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.View;

import com.androidkit.R;
import com.androidkit.context.AppContext;
import com.androidkit.util.GenericAdapter;
import com.devspark.appmsg.AppMsg;
import com.quentindommerc.superlistview.OnMoreListener;
import com.quentindommerc.superlistview.SuperListview;

/**
 * Created by wangming on 5/6/15.
 */
public abstract class ListViewHttpFragment<T, Adapter> extends HttpFragment implements SwipeRefreshLayout.OnRefreshListener, OnMoreListener {
    private static final int LIST_PAGE_SIZE = 20;
    protected GenericAdapter mGenericAdapter;
    protected SuperListview mListView;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setRetryView(R.layout.layout_retry_view);
        setContentView(R.layout.fragment_http_list_view);

        getRetryView().findViewById(R.id.btRetry).setOnClickListener(mOnClickListener);
        mListView = (SuperListview)getContentView().findViewById(R.id.listView);
        mListView.getSwipeToRefresh().setColorSchemeResources(R.color.toolbar_bg);
        mListView.setRefreshListener(this);
        mListView.setupMoreListener(this, LIST_PAGE_SIZE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mGenericAdapter = initGenericAdapter();
        mListView.setAdapter(mGenericAdapter);
        doService();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if(id == R.id.tvEmpty) {
                onClickEmptyView();
            }else if(id == R.id.btRetry) {
                AppMsg.makeText(getActivity(), "重试", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
                onClickRetryView();
            }else {

            }
        }
    };

    @Override
    public final void onRefresh() {
        if (!AppContext.getNetworkSensor().hasAvailableNetwork()) {
            mListView.getSwipeToRefresh().setRefreshing(false);
            mListView.hideMoreProgress();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mListView.getSwipeToRefresh().setRefreshing(false);
                    mListView.hideMoreProgress();
                    onRefreshListData();
                }
            }, 2000);
        }
    }

    @Override
    public void onMoreAsked(int i, int i1, int i2) {
        if (!AppContext.getNetworkSensor().hasAvailableNetwork()) {
            mListView.getSwipeToRefresh().setRefreshing(false);
            mListView.hideMoreProgress();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mListView.getSwipeToRefresh().setRefreshing(false);
                    mListView.hideMoreProgress();
                    onLoadMoreListData();
                }
            }, 2000);
        }
    }

    protected abstract GenericAdapter<T> initGenericAdapter();
    protected abstract void doService();
    protected abstract void onRefreshListData();
    protected abstract void onLoadMoreListData();
    protected abstract void onClickEmptyView();
    protected abstract void onClickRetryView();
}
