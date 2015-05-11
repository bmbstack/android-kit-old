package com.androidkit.base;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.androidkit.R;
import com.androidkit.context.AppContext;
import com.androidkit.util.GenericAdapter;
import com.androidkit.view.LoadingView;
import com.devspark.appmsg.AppMsg;
import com.quentindommerc.superlistview.OnMoreListener;
import com.quentindommerc.superlistview.SuperListview;
import com.quentindommerc.superlistview.SwipeDismissListViewTouchListener;



/**
 * Created by wangming on 5/6/15.
 */
public abstract class ListHttpFragment<T, Adapter> extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        OnMoreListener, SwipeDismissListViewTouchListener.DismissCallbacks {
    private static final int LIST_PAGE_SIZE = 20;
    protected GenericAdapter mAdapter;
    protected SuperListview mListView;
    private FrameLayout mContentContainer;
    private FrameLayout mProgressContainer;
    private RelativeLayout mRetryView;
    private int mRetryLayoutRes = -1;
    private LoadingView mLoadingView;
    private boolean mListShown;
    private boolean mRefreshEnable = false;
    private boolean mLoadmoreEnable = false;
    private boolean mSwipeRemoveEnable = false;

    final private Handler mHandler = new Handler();
    final private Runnable mRequestFocus = new Runnable() {
        public void run() {
            mListView.focusableViewAvailable(mListView);
        }
    };

    public ListHttpFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_http_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRetryView(R.layout.layout_retry_view);
        ensureList();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getRetryView().findViewById(R.id.btRetry).setOnClickListener(mOnClickListener);
        mListView = (SuperListview)getActivity().findViewById(R.id.listView);
        if(mRefreshEnable) {
            mListView.getSwipeToRefresh().setColorSchemeResources(R.color.toolbar_bg);
            mListView.setRefreshListener(this);
        }
        if(mLoadmoreEnable) {
            mListView.setupMoreListener(this, LIST_PAGE_SIZE);
        }
        if(mSwipeRemoveEnable) {
            mListView.setupSwipeToDismiss(this, true);
        }

        mAdapter = initGenericAdapter();
        mListView.setAdapter(mAdapter);
        doService();
    }

    protected void setRefreshEnable(boolean enable) {
        mRefreshEnable = enable;
    }

    protected void setLoadmoreEnable(boolean enable) {
        mLoadmoreEnable = enable;
    }

    protected  void setmSwipeRemoveEnable(boolean enable) {
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

    private void checkView() {
        ensureList();
        if (mListView == null) {
            throw new IllegalStateException("Content view must be initialized before");
        }
    }

    protected void showLoadingView() {
        checkView();
        mRetryView.setVisibility(View.GONE);
        mListView.setVisibility(View.GONE);
        setContentContainerShown(false);
    }

    protected void showListView() {
        checkView();
        mRetryView.setVisibility(View.GONE);
        mListView.setVisibility(View.VISIBLE);
        setContentContainerShown(true);
    }

    protected void showRetryView() {
        checkView();
        mListView.setVisibility(View.GONE);
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
            if(getActivity() != null) {
                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
            }
            if(getActivity() != null) {
                mContentContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
            }
            mLoadingView.hide();
            mProgressContainer.setVisibility(View.GONE);
            mContentContainer.setVisibility(View.VISIBLE);
        } else {
            if(getActivity() != null) {
                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
            }
            if(getActivity() != null) {
                mContentContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
            }
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

        mListView = (SuperListview)root.findViewById(R.id.listView);
        if(mListView == null) {
            throw new RuntimeException("Your content must have a ListView");
        }
        mListShown = true;
        if(mAdapter != null) {
            mListView.setAdapter(mAdapter);
        }else {
            if(mProgressContainer != null) {
                setContentContainerShown(false);
            }
        }
        mHandler.post(mRequestFocus);
    }

    protected abstract GenericAdapter<T> initGenericAdapter();
    protected abstract void doService();
    protected abstract void onRefreshListData();
    protected abstract void onLoadMoreListData();
    protected abstract void onClickEmptyView();
    protected abstract void onClickRetryView();

    @Override
    public boolean canDismiss(int i) {
        return false;
    }

    @Override
    public void onDismiss(ListView listView, int[] ints) {

    }
}
