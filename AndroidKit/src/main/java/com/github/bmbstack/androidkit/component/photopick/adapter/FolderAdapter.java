package com.github.bmbstack.androidkit.component.photopick.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.bmbstack.androidkit.R;
import com.github.bmbstack.androidkit.component.photopick.model.FolderPhotoInfo;
import com.github.bmbstack.androidkit.component.photopick.model.PhotoInfo;
import com.github.bmbstack.androidkit.util.CommonUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class FolderAdapter extends BaseAdapter {

    private String mSelect = "";
    ArrayList<FolderPhotoInfo> mFolderData = new ArrayList<FolderPhotoInfo>();

    public FolderAdapter(ArrayList<FolderPhotoInfo> mFolderData) {
        this.mFolderData = mFolderData;
    }

    public String getSelect() {
        return mSelect;
    }

    public void setSelect(int pos) {
        if (pos >= getCount()) {
            return;
        }

        mSelect = mFolderData.get(pos).getmName();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mFolderData.size();
    }

    @Override
    public Object getItem(int position) {
        return mFolderData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_photopick_listview_item, parent, false);
            holder = new ViewHolder();
            holder.foldIcon = (ImageView) convertView.findViewById(R.id.foldIcon);
            holder.foldName = (TextView) convertView.findViewById(R.id.foldName);
            holder.photoCount = (TextView) convertView.findViewById(R.id.photoCount);
            holder.check = convertView.findViewById(R.id.check);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FolderPhotoInfo data = mFolderData.get(position);
        String uri = data.getPath();
        int count = data.getCount();

        holder.foldName.setText(data.getmName());
        holder.photoCount.setText(String.format("%d张", count));

        ImageLoader.getInstance().displayImage(PhotoInfo.pathAddPreFix(uri), holder.foldIcon,
                CommonUtils.optionsImage);

        if (data.getmName().equals(mSelect)) {
            holder.check.setVisibility(View.VISIBLE);
        } else {
            holder.check.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView foldIcon;
        TextView foldName;
        TextView photoCount;
        View check;
    }
}
