package com.androidkit.base;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.androidkit.R;
import com.androidkit.context.AppContext;
import com.androidkit.view.LoadingView;
import com.marshalchen.ultimaterecyclerview.SwipeToDismissTouchListener;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import java.util.List;


/**
 * Created by wangming on 5/6/15.
 */
public abstract class ListHttpFragment<T, Adapter> extends Fragment implements SwipeRefreshLayout.OnRefreshListener, UltimateRecyclerView.OnLoadMoreListener, SwipeToDismissTouchListener.DismissCallbacks {
    private static final int LIST_PAGE_SIZE = 20;
    private FrameLayout mContentContainer;
    private FrameLayout mProgressContainer;
    private RelativeLayout mRetryView;
    private int mRetryLayoutRes = -1;
    private LoadingView mLoadingView;
    private boolean mListShown;
    private boolean mRefreshEnable = false;
    private boolean mLoadmoreEnable = false;
    private boolean mSwipeRemoveEnable = false;
    protected UltimateRecyclerView mUltimateRecyclerView;
    protected UltimateViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    final private Handler mHandler = new Handler();
    final private Runnable mRequestFocus = new Runnable() {
        public void run() {
            mUltimateRecyclerView.focusableViewAvailable(mUltimateRecyclerView);
        }
    };

    public ListHttpFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_http_list, container, false);
    }

    @Override
    public void onViewCreated(View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRetryView(R.layout.layout_retry_view);
        ensureList();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getRetryView().findViewById(R.id.btRetry).setOnClickListener(mOnClickListener);
        mUltimateRecyclerView = (UltimateRecyclerView) getActivity().findViewById(R.id.recyclerView);
        mUltimateRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mUltimateRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = initAdapter();
        mUltimateRecyclerView.setAdapter(mAdapter);
        mUltimateRecyclerView.addItemDividerDecoration(getActivity());
        if(mLoadmoreEnable) {
            mUltimateRecyclerView.enableLoadmore();
            mAdapter.setCustomLoadMoreView(View.inflate(getActivity(), R.layout.layout_loading_more, null));
            mUltimateRecyclerView.setOnLoadMoreListener(this);
        }
        if(mRefreshEnable) {
            mUltimateRecyclerView.enableDefaultSwipeRefresh(true);
            mUltimateRecyclerView.setDefaultOnRefreshListener(this);
        }
        if(mSwipeRemoveEnable) {
            mUltimateRecyclerView.setSwipeToDismissCallback(this);
        }
        doService();
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        return mLayoutManager;
    }

    protected void setRefreshEnable(boolean enable) {
        mRefreshEnable = enable;
    }

    protected void setLoadmoreEnable(boolean enable) {
        mLoadmoreEnable = enable;
    }

    protected  void setSwipeRemoveEnable(boolean enable) {
        mSwipeRemoveEnable = enable;
    }

    public void setRetryView(int retryLayout) {
        mRetryLayoutRes = retryLayout;
    }

    protected RelativeLayout getRetryView() {
        return mRetryView;
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if(id == R.id.tvEmpty) {
                onClickEmptyView();
            }else if(id == R.id.btRetry) {
                onClickRetryView();
            }else {

            }
        }
    };

    @Override
    public final void onRefresh() {
        if (!AppContext.getNetworkSensor().hasAvailableNetwork()) {
            mUltimateRecyclerView.setRefreshing(false);
            mUltimateRecyclerView.disableLoadmore();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mUltimateRecyclerView.setRefreshing(false);
                    onRefreshListData();
                }
            }, 2000);
        }
    }

    @Override
    public void loadMore(int i, int i1) {
        final LoadingView loadingView = (LoadingView) mAdapter.getCustomLoadMoreView().findViewById(R.id.loadingViewMore);
        loadingView.show(100);
        if (!AppContext.getNetworkSensor().hasAvailableNetwork()) {
            mUltimateRecyclerView.setRefreshing(false);
            mUltimateRecyclerView.disableLoadmore();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadingView.hide();
                    onLoadMoreListData();
                }
            }, 2000);
        }
    }

    @Override
    public SwipeToDismissTouchListener.SwipeDirection dismissDirection(int i) {
        return null;
    }

    @Override
    public void onDismiss(RecyclerView recyclerView, List<SwipeToDismissTouchListener.PendingDismissData> list) {

    }

    @Override
    public void onResetMotion() {

    }

    @Override
    public void onTouchDown() {

    }

    private void checkView() {
        ensureList();
        if (mUltimateRecyclerView == null) {
            throw new IllegalStateException("Content view must be initialized before");
        }
    }

    protected void showLoadingView() {
        checkView();
        mRetryView.setVisibility(View.GONE);
        mUltimateRecyclerView.setVisibility(View.GONE);
        setContentContainerShown(false);
    }

    protected void showListView() {
        checkView();
        mRetryView.setVisibility(View.GONE);
        mUltimateRecyclerView.setVisibility(View.VISIBLE);
        setContentContainerShown(true);
    }

    protected void showRetryView() {
        checkView();
        mUltimateRecyclerView.setVisibility(View.GONE);
        mRetryView.setVisibility(View.VISIBLE);
        setContentContainerShown(true);
    }

    /**
     * 要么显示ContentContainer要么显示ProgressContainer
     *
     * @param shown
     */
    private void setContentContainerShown(boolean shown) {

        ensureList();
        if (mListShown == shown)
            return;

        mListShown = shown;
        if (shown) {
            mLoadingView.hide();
            mProgressContainer.setVisibility(View.GONE);
            mContentContainer.setVisibility(View.VISIBLE);
        } else {
            mContentContainer.setVisibility(View.GONE);
            mLoadingView.show(100);
            mProgressContainer.setVisibility(View.VISIBLE);
        }
    }

    private void ensureList() {
        if (mContentContainer != null && mProgressContainer != null)
            return;

        View root = getView();
        if (root == null)
            throw new IllegalStateException("Content view not yet created");

        mProgressContainer = (FrameLayout) root.findViewById(R.id.flProgressContainer);
        if (mProgressContainer == null)
            throw new RuntimeException("Your content must have a ViewGroup whose id attribute is 'R.id.progress_container'");

        //add loading view
        View loadingViewLayout = View.inflate(root.getContext(), R.layout.layout_loading, null);
        mLoadingView = (LoadingView) loadingViewLayout.findViewById(R.id.loadingView);
        FrameLayout.LayoutParams loadingViewParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
        );
        mProgressContainer.addView(loadingViewLayout, loadingViewParams);
        mLoadingView.hide();

        mContentContainer = (FrameLayout)root.findViewById(R.id.flContentContainer);
        if (mContentContainer == null)
            throw new RuntimeException("Your content must have a ViewGroup whose id attribute is 'R.id.content_container'");

        RelativeLayout.LayoutParams retryParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        mRetryView = (RelativeLayout) root.findViewById(R.id.rlContentRetry);
        if (mRetryView != null && mRetryLayoutRes != -1) {
            mRetryView.addView(View.inflate(root.getContext(), mRetryLayoutRes, null), retryParams);
            mRetryView.setVisibility(View.GONE);
        }

        mUltimateRecyclerView = (UltimateRecyclerView)root.findViewById(R.id.recyclerView);
        if(mUltimateRecyclerView == null) {
            throw new RuntimeException("Your content must have a ListView");
        }
        mListShown = true;
        if(mAdapter != null) {
            mUltimateRecyclerView.setAdapter(mAdapter);
        }else {
            if(mProgressContainer != null) {
                setContentContainerShown(false);
            }
        }
        mHandler.post(mRequestFocus);
    }

    protected abstract UltimateViewAdapter initAdapter();
    protected abstract void doService();
    protected abstract void onRefreshListData();
    protected abstract void onLoadMoreListData();
    protected abstract void onClickEmptyView();
    protected abstract void onClickRetryView();
}
