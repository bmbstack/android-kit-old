package com.androidkit.base;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.androidkit.R;
import com.androidkit.util.DensityUtils;
import com.androidkit.view.LoadingView;

/**
 * 
 * @Description 有进度提示的Fragment，适用于有网络请求的页面，有内容为空提示和网络错误提示。
 */
public abstract class ProgressFragment extends Fragment {
	private FrameLayout mContentContainer;
	private FrameLayout mProgressContainer;
	private View mContentView;
	private RelativeLayout mEmptyView;
	private RelativeLayout mRetryView;
	private boolean mContentShown;
	private int mEmptyLayoutRes;
	private int mRetryLayoutRes;
	private LoadingView mLoadingView;

	public ProgressFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_progress, container, false);
	}

	protected void setContentView(int layoutResId) {
		View contentView = View.inflate(getActivity(), layoutResId, null);
		setContentView(contentView);
	}

	protected View getContentView() {
		return mContentView;
	}

	protected RelativeLayout getRetryView() {
		return mRetryView;
	}

	protected RelativeLayout getEmptyView() {
		return mEmptyView;
	}

	private void setContentView(View view) {
		ensureContent();
		if (view == null) {
			throw new IllegalArgumentException("Content view can't be null");
		}
		if (mContentContainer instanceof ViewGroup) {
			ViewGroup contentContainer = (ViewGroup) mContentContainer;
			if (mContentView == null) {
				contentContainer.addView(view);
			} else {
				int index = contentContainer.indexOfChild(mContentView);
				// replace content view
				contentContainer.removeView(mContentView);
				contentContainer.addView(view, index);
			}
			mContentView = view;
		} else {
			throw new IllegalStateException("Can't be used with a custom content view");
		}
	}

	public void setEmptyView(int emptyLayout) {
		mEmptyLayoutRes = emptyLayout;
	}

	public void setRetryView(int retryLayout) {
		mRetryLayoutRes = retryLayout;
	}

	private void checkView() {
		ensureContent();
		if (mContentView == null) {
			throw new IllegalStateException("Content view must be initialized before");
		}
	}

	protected void showLoadingView() {
		checkView();
		mEmptyView.setVisibility(View.GONE);
		mRetryView.setVisibility(View.GONE);
		mContentView.setVisibility(View.GONE);
		setContentContainerShown(false);
	}

	protected void showContentView() {
		checkView();
		mEmptyView.setVisibility(View.GONE);
		mEmptyView.setVisibility(View.GONE);

		mRetryView.setVisibility(View.GONE);
		mRetryView.setVisibility(View.GONE);

		mContentView.setVisibility(View.VISIBLE);

		setContentContainerShown(true);
	}

	protected void showEmptyView() {
		checkView();
		mRetryView.setVisibility(View.GONE);
		mRetryView.setVisibility(View.GONE);

		mContentView.setVisibility(View.GONE);

		mEmptyView.setVisibility(View.VISIBLE);
		setContentContainerShown(true);
	}

	protected void showRetryView() {

		checkView();
		mEmptyView.setVisibility(View.GONE);
		mEmptyView.setVisibility(View.GONE);

		mContentView.setVisibility(View.GONE);

		mRetryView.setVisibility(View.VISIBLE);

		setContentContainerShown(true);
	}

	/**
	 * 要么显示ContentContainer要么显示ProgressContainer
	 * 
	 * @param shown
	 */
	private void setContentContainerShown(boolean shown) {

		ensureContent();
		if (mContentShown == shown)
			return;

		mContentShown = shown;
		if (shown) {
			mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
			mContentContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
			mLoadingView.hide();
			mProgressContainer.setVisibility(View.GONE);
			mContentContainer.setVisibility(View.VISIBLE);
		} else {
			mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
			mContentContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
			mContentContainer.setVisibility(View.GONE);
			mLoadingView.show(100);
			mProgressContainer.setVisibility(View.VISIBLE);
		}
	}


	@Override
	public void onDestroyView() {
		mContentShown = false;
		super.onDestroyView();
	}

	private void ensureContent() {
		if (mContentContainer != null && mProgressContainer != null)
			return;

		View root = getView();
		if (root == null)
			throw new IllegalStateException("Content view not yet created");

		mProgressContainer = (FrameLayout) root.findViewById(R.id.flProgressContainer);
		if (mProgressContainer == null)
			throw new RuntimeException("Your content must have a ViewGroup whose id attribute is 'R.id.progress_container'");

		//add loading view
		mLoadingView = new LoadingView(getActivity(), DensityUtils.dip2px(40.0f));
		mLoadingView.setAnimBgImage(R.drawable.ic_loading_yuan_gray);
		mLoadingView.setAnimForeImage(R.drawable.ic_loadiing_zhen);
		mLoadingView.setBackgroundResource(R.drawable.bg_loading);
		mLoadingView.hide();
		mProgressContainer.addView(
				mLoadingView,
				new FrameLayout.LayoutParams(DensityUtils.dip2px(80.0f), DensityUtils.dip2px(80.0f), Gravity.CENTER));

		mContentContainer = (FrameLayout)root.findViewById(R.id.flContentContainer);
		if (mContentContainer == null)
			throw new RuntimeException("Your content must have a ViewGroup whose id attribute is 'R.id.content_container'");

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		mEmptyView = (RelativeLayout) root.findViewById(R.id.rlContentEmpty);
		if (mEmptyView != null) {
			mEmptyView.addView(View.inflate(getActivity(), mEmptyLayoutRes, null), params);
			mEmptyView.setVisibility(View.GONE);
		}

		mRetryView = (RelativeLayout) root.findViewById(R.id.rlContentRetry);
		if (mRetryView != null) {
			mRetryView.addView(View.inflate(getActivity(), mRetryLayoutRes, null), params);
			mRetryView.setVisibility(View.GONE);
		}

		mContentShown = true;
		if (mContentView == null)
			setContentContainerShown(false);
	}
}
