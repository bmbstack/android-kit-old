package cn.bmbstack.androidkit.view.tab;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public abstract class SlidingTabPagerAdapter extends FragmentPagerAdapter
    {//implements TabChangedListener {//}, OnTabClickListener {

	protected final Context context;
	protected final Fragment[] fragments;
	private final ViewPager pager;
	private int lastPostion;
    protected int mScrollY;

    public SlidingTabPagerAdapter(FragmentManager mgr, int i, Context context,
			ViewPager vp) {
		super(mgr);
		lastPostion = 0;
		fragments = new Fragment[i];
		this.context = context;
		pager = vp;
		lastPostion = 0;
	}

	private Fragment getFragmentByPosition(int i) {
		Fragment fragment = null;
		if (i >= 0 && i < fragments.length)
			fragment = fragments[i];
		return fragment;
	}

	private void onLeave(int i) {
		Fragment fragment = getFragmentByPosition(lastPostion);
		lastPostion = i;
		//if (fragment != null)
		//	fragment.g();
	}

	public abstract int getCacheCount();

	@Override
	public abstract int getCount();

	@Override
	public Fragment getItem(int i) {
		return fragments[i];
	}

	@Override
	public abstract CharSequence getPageTitle(int i);

	public boolean isCurrent(Fragment fragment) {
		boolean flag = false;
		int i = pager.getCurrentItem();
		int j = 0;
		do {
			label0: {
				if (j < fragments.length) {
					if (fragment != fragments[j] || j != i)
						break label0;
					flag = true;
				}
				return flag;
			}
			j++;
		} while (true);
	}

	public void onPageScrolled(int i) {
		Fragment fragment = getFragmentByPosition(i);
		if (fragment != null) {
			//fragment.h();
			onLeave(i);
		}
	}

	public void onPageSelected(int i) {
		Fragment fragment = getFragmentByPosition(i);
		if (fragment != null) {
			//fragment.f();
			onLeave(i);
		}
	}

    public void setScrollY(int scrollY) {
        mScrollY = scrollY;
    }

    public Fragment getItemAt(int index){
        return fragments[index];
    }
}
