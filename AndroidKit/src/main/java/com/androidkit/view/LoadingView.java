package com.androidkit.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.androidkit.R;

/**
 * 全局的loading动画View
 * @author yhb
 *
 */
public class LoadingView extends FrameLayout{

	private ImageView mIvFgIcon;//背景icon
	private RotateAnimation mFgRotateAnim;//前景icon旋转动画

	private int mIvFgIconResId;
	private int mIvBgIconResId;

	private ImageView mIvBgIcon;//前景icon
	private ScaleAnimation mBgSmallAnim;//背景icon缩小动画
	private ScaleAnimation mBgBigAnim;//背景icon放大动画

	private boolean mAnimRunning;

	public LoadingView(Context context, int iconSizePx) {

		super(context);
		initImageViews(iconSizePx);
		initAnimations();
	}

	public LoadingView(Context context, AttributeSet attrs){

		super(context, attrs);
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.QaLoadingView);
		int iconSizePx = ta.getDimensionPixelSize(R.styleable.QaLoadingView_iconSize, -100);
		mIvBgIconResId = ta.getResourceId(R.styleable.QaLoadingView_iconBg, 0);
		mIvFgIconResId = ta.getResourceId(R.styleable.QaLoadingView_iconFg, 0);
		ta.recycle();

		initImageViews(iconSizePx);
		initAnimations();
	}

	private void initImageViews(int iconSizePx) {

		mIvBgIcon = new ImageView(getContext());
		mIvBgIcon.setScaleType(ScaleType.CENTER_INSIDE);
		int layoutSize = iconSizePx == -100 ? FrameLayout.LayoutParams.WRAP_CONTENT : iconSizePx;
		FrameLayout.LayoutParams fllp = new FrameLayout.LayoutParams(layoutSize, layoutSize);
		fllp.gravity = Gravity.CENTER;
		addView(mIvBgIcon, fllp);

		mIvFgIcon = new ImageView(getContext());
		mIvFgIcon.setScaleType(ScaleType.CENTER_INSIDE);
		fllp = new FrameLayout.LayoutParams(layoutSize, layoutSize);
		fllp.gravity = Gravity.CENTER;
		addView(mIvFgIcon, fllp);
	}

	private void initAnimations() {

		mBgSmallAnim = new ScaleAnimation(1f,0.9f,1f,0.9f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		mBgSmallAnim.setDuration(450);
//		mBgSmallAnim.setStartOffset(200);//500毫秒好像也可以
		mBgSmallAnim.setFillAfter(true);
		mBgSmallAnim.setInterpolator(new BgDecelerateInterpolator());
		mBgSmallAnim.setAnimationListener(mBgSmallAnimListener);

		mBgBigAnim = new ScaleAnimation(0.9f,1f,0.9f,1f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		mBgBigAnim.setDuration(450);
		mBgBigAnim.setFillAfter(true);
		mBgBigAnim.setInterpolator(new BgDecelerateInterpolator());
		mBgBigAnim.setAnimationListener(mBgBigAnimListener);

		mFgRotateAnim = new RotateAnimation(0f,360f,Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		mFgRotateAnim.setInterpolator(new FgAnticipateInterpolator(1.5f));
		mFgRotateAnim.setDuration(900);
		//mFgRotateAnim.setStartOffset(50);//原300正好
		mFgRotateAnim.setFillAfter(true);
		mFgRotateAnim.setAnimationListener(mFgAnimListener);
	}

	public void setAnimForeImage(int resId){

		mIvFgIconResId = resId;
	}

	public void setAnimBgImage(int resId){

		mIvBgIconResId = resId;
	}

	public void startLoadingAnim(long delayMillis){

		if(!mAnimRunning){

			if(mIvFgIconResId > 0)
				mIvFgIcon.setImageResource(mIvFgIconResId);

			if(mIvBgIconResId > 0)
				mIvBgIcon.setImageResource(mIvBgIconResId);

			removeCallbacks(mDelayStartAnimRunnable);

			if(delayMillis > 0){

				postDelayed(mDelayStartAnimRunnable, delayMillis);
			}else{

				mDelayStartAnimRunnable.run();
			}
			mAnimRunning = true;
		}
	}

	public void stopLoadingAnim(){

		if(mAnimRunning){

			removeCallbacks(mDelayStartAnimRunnable);
			mIvFgIcon.clearAnimation();
			mIvBgIcon.clearAnimation();

			mIvFgIcon.setImageDrawable(null);
			mIvBgIcon.setImageDrawable(null);
			mAnimRunning = false;
		}
	}

	@Override
	public void setVisibility(int visibility) {

		if(getVisibility() != visibility)
			super.setVisibility(visibility);

		if(visibility == View.VISIBLE){

			show(0);
		}else{

			stopLoadingAnim();
		}
	}

	public void show(long delayStartAnim){

		startLoadingAnim(delayStartAnim);
		if(getVisibility() != View.VISIBLE)
			setVisibility(View.VISIBLE);
	}

	public void hide(){

		if(getVisibility() != View.INVISIBLE)
			setVisibility(View.INVISIBLE);

		stopLoadingAnim();
	}

	public boolean isLoadingAnimRunning(){

		return mAnimRunning;
	}

	private Runnable mDelayStartAnimRunnable = new Runnable() {

		@Override
		public void run() {

			mIvFgIcon.startAnimation(mFgRotateAnim);
			mIvBgIcon.startAnimation(mBgSmallAnim);
		}
	};

	private AnimationListener mBgSmallAnimListener = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {

			mIvBgIcon.startAnimation(mBgBigAnim);
		}
	};

	public boolean mBgBigAnimEnd;
	public AnimationListener mBgBigAnimListener = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {

			mBgBigAnimEnd = true;
			if(mFgAnimEnd){

				mIvFgIcon.startAnimation(mFgRotateAnim);
				mIvBgIcon.startAnimation(mBgSmallAnim);
				mBgBigAnimEnd = false;
			}
		}
	};

	private boolean mFgAnimEnd = true;
	public AnimationListener mFgAnimListener = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {

			mFgAnimEnd = true;
			if(mBgBigAnimEnd){

				mIvFgIcon.startAnimation(mFgRotateAnim);
				mIvBgIcon.startAnimation(mBgSmallAnim);
				mFgAnimEnd = false;
			}else{

				//nothing todo wait
			}
		}
	};

	/**
	 * 前景icon动画插值器
	 */
	public class FgAnticipateInterpolator implements Interpolator {

		private final float mTension;

		public FgAnticipateInterpolator() {
			mTension = 2.0f;
		}

		public FgAnticipateInterpolator(float tension) {
			mTension = tension;
		}

		public float getInterpolation(float t) {
			// a(t) = t * t * ((tension + 1) * t - tension)

			if (t>=0.0f && t<0.5f)
				return (float)(t * t * (2 * t - 1));
			else
				return (float)(0.5f + 0.5f * Math.cos(2.0f * Math.PI * t));

//	        return t * t * ((mTension + 1) * t - mTension);
		}
	}

	/**
	 * 背景icon动画插值器
	 */
	private static class BgDecelerateInterpolator implements Interpolator {

		//private float mFactor = 1.0f;

		public BgDecelerateInterpolator() {
		}

		public float getInterpolation(float input) {

			// float result;
			// if (mFactor == 1.0f) {
			// result = (float)(1.0f - (1.0f - input) * (1.0f - input));
			// } else {
			// result = (float)(1.0f - Math.pow((1.0f - input), 2 * mFactor));
			// }
			// return result;
			return (float)(1.0f - (1.0f - input) * (1.0f - input));
		}
	}
}
