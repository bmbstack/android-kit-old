package com.github.bmbstack.androidkit.component.photopick.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

import com.github.bmbstack.androidkit.R;
import com.github.bmbstack.androidkit.component.photopick.PhotoPickFragment;


public class AllPhotoAdapter extends PhotoAdapter {

    public AllPhotoAdapter(Context context, Cursor c, boolean autoRequery, boolean isSingleMode, PhotoPickFragment fragment) {
        super(context, c, autoRequery, isSingleMode, fragment);
    }

    @Override
    public int getCount() {
        return super.getCount() + 1;
    }

    @Override
    public Object getItem(int position) {
        if (position > 0) {
            return super.getItem(position - 1);
        } else {
            return super.getItem(position);
        }
    }

    @Override
    public long getItemId(int position) {
        if (position > 0) {
            return super.getItemId(position - 1);
        } else {
            return -1;
        }
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (position > 0) {
            return super.getDropDownView(position - 1, convertView, parent);
        } else {
            return getView(position, convertView, parent);
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position > 0) {
            return super.getView(position - 1, convertView, parent);
        } else {
            //position == 0  照相机
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.layout_photopick_gridview_item_camera, parent, false);
                int widthPix = convertView.getContext().getResources().getDisplayMetrics().widthPixels;
                convertView.getLayoutParams().height = widthPix / 3;
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mFragment.camera();
                    }
                });
            }

            return convertView;
        }
    }
}
