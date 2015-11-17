package com.github.bmbstack.androidkit.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.github.bmbstack.androidkit.app.R;
import com.github.bmbstack.androidkit.app.fragment.DynamicFragment;
import com.github.bmbstack.androidkit.app.fragment.MyFragment;
import com.github.bmbstack.androidkit.base.BaseActivity;
import com.github.bmbstack.androidkit.base.FragmentTabHost;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private FragmentTabHost tabhost;

    public enum MainTab {

        DYNAMIC(R.string.main_tab_name_dynamic, R.drawable.ic_main_tab_dynamic, DynamicFragment.class),
        MY(R.string.main_tab_name_my, R.drawable.ic_main_tab_dynamic, MyFragment.class);

        private int tag;
        private int resIcon;
        private Class<?> clazz;

        MainTab(int resName, int resIcon, Class<?> clz) {
            this.tag = resName;
            this.resIcon = resIcon;
            this.clazz = clz;
        }

        public int getTag() {
            return tag;
        }

        public void setTag(int tag) {
            this.tag = tag;
        }

        public int getResIcon() {
            return resIcon;
        }

        public void setResIcon(int resIcon) {
            this.resIcon = resIcon;
        }

        public Class<?> getClazz() {
            return clazz;
        }
    }

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initTitleView() {
    }

    @Override
    protected void initContentView() {
        tabhost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        tabhost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        MainTab[] tabs = MainTab.values();
        for(int i = 0; i < tabs.length; i++) {
            MainTab tab = tabs[i];
            TabHost.TabSpec tabSpec = tabhost.newTabSpec(getString(tab.getTag()));

            View tabItemView = View.inflate(this, R.layout.item_main_tab, null);
            ImageView iv_main_tab_item = (ImageView) tabItemView.findViewById(R.id.iv_main_tab_item);
            iv_main_tab_item.setImageResource(tab.getResIcon());
            TextView tv_main_tab_item = (TextView) tabItemView.findViewById(R.id.tv_main_tab_item);
            tv_main_tab_item.setText(tab.getTag());

            tabSpec.setIndicator(tabItemView);
            tabhost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
            tabhost.addTab(tabSpec, tab.getClazz(), null);
        }
    }
}
