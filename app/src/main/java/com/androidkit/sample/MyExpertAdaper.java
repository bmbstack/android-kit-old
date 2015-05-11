package com.androidkit.sample;

import com.androidkit.util.GenericAdapter;
import com.androidkit.util.ViewHolder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * Created by wangming on 5/5/15.
 */
public class MyExpertAdaper extends GenericAdapter<Expert> {

    @Override
    public int getItemLayoutId() {
        return R.layout.item_list_my_expert;
    }

    @Override
    public void bindView(int position, ViewHolder holder, Expert data) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.ic_launcher)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(20)).build();
        holder.setImageUrl(R.id.iv_my_expert_avatars, data.getPortraitUrl(), options);
        holder.setText(R.id.tv_my_expert_name, data.getName());
        holder.setText(R.id.tv_my_expert_title, data.getTitle());
        holder.setText(R.id.tv_my_expert_hospital, data.getHospital());
        holder.setText(R.id.tv_my_expert_section, data.getSection());
    }
}
