package com.androidkit.sample;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangming on 5/12/15.
 */
public class MyExpertAdapter extends UltimateViewAdapter {
    private List<Expert> mDataList = new ArrayList<Expert>();

    public MyExpertAdapter(List<Expert> dataList) {
        mDataList = dataList;
    }

    @Override
    public UltimateRecyclerviewViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        return new ViewHolder(View.inflate(viewGroup.getContext(), R.layout.item_list_my_expert, null));
    }

    public void add(int location, Expert expert) {
        insert(mDataList, expert, location);
    }

    public void addAll(List<Expert> dataList) {
        mDataList.addAll(dataList);
        notifyDataSetChanged();
    }

    @Override
    public int getAdapterItemCount() {
        return mDataList.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < getItemCount()
                && (customHeaderView != null ? position <= mDataList.size() : position < mDataList.size())
                && (customHeaderView != null ? position > 0 : true)) {

            ViewHolder viewHolder = (ViewHolder) holder;
            Expert expert = mDataList.get(position);
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnFail(R.drawable.ic_launcher)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .displayer(new RoundedBitmapDisplayer(20)).build();
            ImageLoader.getInstance().displayImage(expert.getPortraitUrl(), viewHolder.iv_my_expert_avatars, options);
            viewHolder.tv_my_expert_name.setText(expert.getName());
            viewHolder.tv_my_expert_title.setText(expert.getTitle());
            viewHolder.tv_my_expert_hospital.setText(expert.getHospital());
            viewHolder.tv_my_expert_section.setText(expert.getSection());
        }
    }

    private class ViewHolder extends UltimateRecyclerviewViewHolder {

        ImageView iv_my_expert_avatars;
        TextView tv_my_expert_title;
        TextView tv_my_expert_name;
        TextView tv_my_expert_hospital;
        TextView tv_my_expert_section;

        public ViewHolder(View itemView) {
            super(itemView);

            iv_my_expert_avatars = (ImageView) itemView.findViewById(R.id.iv_my_expert_avatars);
            tv_my_expert_title = (TextView) itemView.findViewById(R.id.tv_my_expert_title);
            tv_my_expert_name = (TextView) itemView.findViewById(R.id.tv_my_expert_name);
            tv_my_expert_hospital = (TextView)itemView.findViewById(R.id.tv_my_expert_hospital);
            tv_my_expert_section = (TextView) itemView.findViewById(R.id.tv_my_expert_section);
        }
    }

}
