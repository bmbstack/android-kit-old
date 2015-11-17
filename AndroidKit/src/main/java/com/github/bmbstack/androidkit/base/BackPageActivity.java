package com.github.bmbstack.androidkit.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.github.bmbstack.androidkit.R;
import com.github.bmbstack.androidkit.component.photopick.PhotoPickClipFragment;
import com.github.bmbstack.androidkit.component.photopick.PhotoPickDetailFragment;
import com.github.bmbstack.androidkit.component.photopick.PhotoPickFragment;
import com.github.bmbstack.androidkit.component.photopick.PhotoPickPagerFragment;

import java.lang.ref.WeakReference;

/**
 * Created by wangming on 5/4/15.
 */
public class BackPageActivity extends BaseActivity {
    public final static String BUNDLE_KEY_BACK_PAGE = "BUNDLE_KEY_BACK_PAGE";
    private static final String TAG = "FLAG_TAG";
    private WeakReference<Fragment> mFragment;
    private BackPage mBackPage;

    public enum BackPage {
        //photo pick
        PHOTOPICK(1, R.string.photopick, PhotoPickFragment.class),
        PHOTOPICKCLIP(2, R.string.photopick_clip, PhotoPickClipFragment.class),
        PHOTOPICKDETAIL(3, R.string.photopick_detail, PhotoPickDetailFragment.class),
        PHOTOPICKPAGER(4, R.string.photopick_pager, PhotoPickPagerFragment.class);

        private int value;
        private int titleRes;
        private Class<?> clazz;

        BackPage(int value, int titleRes, Class<?> clazz) {
            this.value = value;
            this.titleRes = titleRes;
            this.clazz = clazz;
        }

        public int getTitleRes() {
            return titleRes;
        }

        public void setTitleRes(int titleRes) {
            this.titleRes = titleRes;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public void setClazz(Class<?> clazz) {
            this.clazz = clazz;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public static BackPage getPageByValue(int val) {
            for (BackPage p : values()) {
                if (p.getValue() == val)
                    return p;
            }
            return null;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back);
    }

    @Override
    protected void initData() {
        if (getIntent() == null) {
            throw new RuntimeException("you must provide a page info to display");
        }
        int pageValue = getIntent().getIntExtra(BUNDLE_KEY_BACK_PAGE, 0);
        mBackPage = BackPage.getPageByValue(pageValue);
        if(mBackPage == null) {
            throw new IllegalArgumentException("can not find page by BackPage:" + mBackPage);
        }
    }

    @Override
    protected void initTitleView() {
        setTitle(mBackPage.getTitleRes());
        addTitleLeftBackView();
    }

    @Override
    protected void initContentView() {
        try {
            Fragment fragment = (Fragment) mBackPage.getClazz().newInstance();
            Bundle args = getIntent().getExtras();
            if (args != null) {
                fragment.setArguments(args);
            }

            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.replace(R.id.container, fragment, TAG);
            trans.commit();

            mFragment = new WeakReference<Fragment>(fragment);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("generate fragment error. by BackPage:" + mBackPage);
        }
    }

    public static void startActivity(Activity activity, Bundle args) {
        Intent intent = new Intent(activity, BackPageActivity.class);
        intent.putExtras(args);
        activity.startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void startActivityForResult(Activity activity, int requestCode, Bundle args) {
        Intent intent = new Intent(activity, BackPageActivity.class);
        intent.putExtras(args);
        activity.startActivityForResult(intent, requestCode, args);
    }
}
