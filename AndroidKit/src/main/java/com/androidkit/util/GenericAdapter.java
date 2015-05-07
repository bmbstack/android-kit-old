package com.androidkit.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangming on 11/25/14.
 */
public abstract class GenericAdapter<T> extends BaseAdapter {
	private List<T> mDataList = new ArrayList<T>();
	private final int mItemLayoutId;

	public GenericAdapter() {
		mItemLayoutId = getItemLayoutId();
	}


	/**
	 * reset dataList
	 *
	 * @param dataList
	 */
	public void setData(List<T> dataList) {
		mDataList.clear();
		addData(dataList);
	}

	public List<T> getData() {
		return mDataList;
	}

	/**
	 * append data to list
	 *
	 * @param dataList
	 */
	public void addData(List<T> dataList) {
		mDataList.addAll(dataList);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mDataList.size();
	}


	@Override
	public Object getItem(int position) {
		return mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = ViewHolder.get(parent.getContext(), convertView, mItemLayoutId);
		bindView(position, holder, (T)getItem(position));
		return holder.getConvertView();

	}

	public abstract int getItemLayoutId();
	public abstract void bindView(int position, ViewHolder holder, T data);
}
