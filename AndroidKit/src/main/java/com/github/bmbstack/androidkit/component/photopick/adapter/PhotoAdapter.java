package com.github.bmbstack.androidkit.component.photopick.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.github.bmbstack.androidkit.R;
import com.github.bmbstack.androidkit.component.photopick.PhotoPickFragment;
import com.github.bmbstack.androidkit.component.photopick.model.PhotoInfo;
import com.github.bmbstack.androidkit.util.CommonUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PhotoAdapter extends CursorAdapter {

    final int itemWidth;
    LayoutInflater mInflater;
    PhotoPickFragment mFragment;
    private boolean mIsSingleMode = false;
    //
//    enum Mode { All, Folder }
//    private Mode mMode = Mode.All;
//
//    void setmMode(Mode mMode) {
//        this.mMode = mMode;
//    }
    View.OnClickListener mClickItem = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mFragment.clickPhotoItem(v);
        }
    };

    public PhotoAdapter(Context context, Cursor c, boolean autoRequery, boolean isSingleMode, PhotoPickFragment fragment) {
        super(context, c, autoRequery);
        mInflater = LayoutInflater.from(context);
        mFragment = fragment;
        mIsSingleMode = isSingleMode;
        int spacePix = context.getResources().getDimensionPixelSize(R.dimen.space_4);
        int widthPix = context.getResources().getDisplayMetrics().widthPixels;
        itemWidth = (widthPix - spacePix * 4) / 3;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View convertView = mInflater.inflate(R.layout.layout_photopick_gridview_item, parent, false);
        ViewGroup.LayoutParams layoutParams = convertView.getLayoutParams();
        layoutParams.height = itemWidth;
        layoutParams.width = itemWidth;
        convertView.setLayoutParams(layoutParams);


        GridViewHolder holder = new GridViewHolder();
        holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
        holder.imageViewForeground = (ImageView) convertView.findViewById(R.id.iconFore);
        holder.check = (CheckBox) convertView.findViewById(R.id.check);
        if(mIsSingleMode) {
            holder.check.setVisibility(View.GONE);
        }else {
            holder.check.setVisibility(View.VISIBLE);
        }
        PhotoPickFragment.GridViewCheckTag checkTag = new PhotoPickFragment.GridViewCheckTag(holder.imageViewForeground);
        holder.check.setTag(checkTag);
        holder.check.setOnClickListener(mClickItem);
        convertView.setTag(holder);

        ViewGroup.LayoutParams iconParam = holder.imageView.getLayoutParams();
        iconParam.width = itemWidth;
        iconParam.height = itemWidth;
        holder.imageView.setLayoutParams(iconParam);

        return convertView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        GridViewHolder holder;
        holder = (GridViewHolder) view.getTag();

        ImageLoader imageLoader = ImageLoader.getInstance();

        String path = PhotoInfo.pathAddPreFix(cursor.getString(1));
        imageLoader.displayImage(path, holder.imageView, CommonUtils.optionsImage);

        ((PhotoPickFragment.GridViewCheckTag) holder.check.getTag()).path = path;

        boolean picked = mFragment.isPicked(path);
        holder.check.setChecked(picked);
        holder.imageViewForeground.setVisibility(picked ? View.VISIBLE : View.INVISIBLE);
    }

    static class GridViewHolder {
        ImageView imageView;
        ImageView imageViewForeground;
        CheckBox check;
    }
}
