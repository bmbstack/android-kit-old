package com.androidkit.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by wangming on 11/26/14.
 *
 * Store the item view by the static ViewHolder class
 *
 */
public class ViewHolder {
	private SparseArray<View> mViews;
	private View mConvertView;
	private int mPosition;


	private ViewHolder(Context context, int layoutId, int position) {
		mViews = new SparseArray<View>();
		mConvertView = View.inflate(context, layoutId, null);
		mPosition = position;
		mConvertView.setTag(this);
	}

	/**
	 * create a ViewHolder instance when convertView is null, otherwise use convertView.getTag().
	 *
	 * @param context
	 * @param convertView
	 * @param layoutId
	 * @return
	 */
	public static ViewHolder get(Context context, View convertView, int layoutId) {
		return getByPosition(context, convertView, layoutId, -1);
	}

	/**
	 *  This method is package private and should only be used by GenericAdapter. create ViewHolder by
	 *  position
	 *
	 * @param context
	 * @param convertView
	 * @param layoutId
	 * @param position
	 * @return
	 */
	static ViewHolder getByPosition(Context context, View convertView, int layoutId, int position) {
		if(convertView == null) {
			return new ViewHolder(context, layoutId, position);
		}
		ViewHolder viewHolder = (ViewHolder) convertView.getTag();
		viewHolder.mPosition = position;
		return viewHolder;
	}

	public View getConvertView() {
		return mConvertView;
	}

	/**
	 * Retrieve the overall position of the data in the list.
	 * @throws IllegalArgumentException If the position hasn't been set at the construction of the this helper.
	 */
	public int getPosition() {
		if (mPosition == -1)
			throw new IllegalStateException("Use BaseAdapterHelper constructor " +
					"with position if you need to retrieve the position.");
		return mPosition;
	}

	public void clickView(int viewId, View.OnClickListener listener) {
		View view = retrieveView(viewId);
		view.setOnClickListener(listener);
	}

	public void longClickView(int viewId, View.OnLongClickListener listener) {
		View view = retrieveView(viewId);
		view.setOnLongClickListener(listener);
	}

	private <T extends View> T retrieveView(int viewId) {
		View view = mViews.get(viewId);
		if (view == null) {
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view;
	}

	/**
	 * Find item's view by resource id.
	 *
	 * @param viewId
	 * @param <T>
	 * @return
	 */
	public <T extends View> T getView(int viewId) {
		return retrieveView(viewId);
	}

	/**
	 * Set the text value for TextView.
	 *
	 * @param viewId
	 * @param value
	 * @return
	 */
	public ViewHolder setText(int viewId, String value) {
		TextView view = retrieveView(viewId);
		view.setText(value);
		return this;
	}

	public ViewHolder setTextColor(int viewId, int color) {
		TextView view = retrieveView(viewId);
		view.setTextColor(color);
		return this;
	}

	/**
	 * Set imageResource for ImageView.
	 *
	 * @param viewId
	 * @param imageResId
	 * @return
	 */
	public ViewHolder setImageResource(int viewId, int imageResId) {
		ImageView view = retrieveView(viewId);
		view.setImageResource(imageResId);
		return this;
	}

	/**
	 *
	 * @param viewId
	 * @param drawable
	 * @return
	 */
	public ViewHolder setImageDrawable(int viewId, Drawable drawable) {
		ImageView view = retrieveView(viewId);
		view.setImageDrawable(drawable);
		return this;
	}

	/**
	 * Set Image with request url(use volley library's ImageLoader) / AsyncImageView
	 *
	 * @param viewId
	 * @param requestUrl
	 * @param options
	 * @return
	 */
	public ViewHolder setImageUrl(int viewId, String requestUrl, DisplayImageOptions options) {
		ImageView view = retrieveView(viewId);
		ImageLoader.getInstance().displayImage(requestUrl, view, options);
		return this;
	}

	/**
	 * Set background for View.
	 *
	 * @param viewId
	 * @param backgroundRes
	 * @return
	 */
	public ViewHolder setBackground(int viewId, int backgroundRes) {
		View view = retrieveView(viewId);
		view.setBackgroundResource(backgroundRes);
		return this;
	}
}