package com.github.bmbstack.androidkit.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.github.bmbstack.androidkit.R;

import java.util.Stack;

/**
 * Created by wangming on 4/30/15.
 */
public abstract class BaseActivity extends ActionBarActivity {
    public static final int ANIM_ACTION_NO_ANIM = 0;
    public static final int ANIM_ACTION_IN_BELOW_OUT_ABOVE = 1;
    public static final int ANIM_ACTION_IN_RIGHT_OUT_LEFT = 2;
    private static Stack<Integer> animationStack = new Stack<Integer>();
    private Toolbar mToolbar;

    @Override
    public void setContentView(int layoutResID) {
        setContentView(getLayoutInflater().inflate(layoutResID, null));
    }

    @Override
    public void setContentView(View view) {
        setRootView(view, false);
    }

    public void setContentViewWithFloatToolbar(View view) {
        setRootView(view, true);
    }

    private void setRootView(View view, boolean floatToolbar) {
        View rootView = null;
        if (floatToolbar) {
            rootView = View.inflate(this, R.layout.activity_base_toolbar_float, null);
        } else {
            rootView = View.inflate(this, R.layout.activity_base_toolbar, null);
        }
        FrameLayout flContentView = (FrameLayout) rootView.findViewById(R.id.flContentView);
        flContentView.addView(view);
        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setSubtitleTextColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.statusbar_bg));
        }
        super.setContentView(rootView);

        initData();
        initTitleView();
        initContentView();
    }

    protected Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startAnimation();
    }

    @Override
    public void finish() {
        super.finish();
        endAnimation();
    }

    protected void setAnimationWhat(int animationWhat) {
        animationStack.push(animationWhat);
    }
    
    private void startAnimation() {
        if (animationStack.isEmpty()) {
            return;
        }
        switch (animationStack.peek()) {
            case ANIM_ACTION_NO_ANIM:
                break;
            case ANIM_ACTION_IN_BELOW_OUT_ABOVE:
                overridePendingTransition(R.anim.slide_in_below, R.anim.slide_stay_above);
                break;
            case ANIM_ACTION_IN_RIGHT_OUT_LEFT:
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_stay_left);
                break;
            default:
                break;
        }
    }

    private void endAnimation() {
        if (animationStack.isEmpty()) {
            return;
        }
        switch (animationStack.pop()) {
            case ANIM_ACTION_NO_ANIM:
                break;
            case ANIM_ACTION_IN_BELOW_OUT_ABOVE:
                overridePendingTransition(R.anim.slide_stay_above, R.anim.slide_out_above);
                break;
            case ANIM_ACTION_IN_RIGHT_OUT_LEFT:
                overridePendingTransition(R.anim.slide_stay_left, R.anim.slide_out_left);
                break;
            default:
                break;
        }
    }

    public void addTitleLeftBackView() {
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void addTitleLeftBackView(View.OnClickListener listener) {
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(listener);
    }


    /**
     * Initial the intent's extra data.
     */
    protected abstract void initData();

    /**
     * Initial the Toolbar optional menu
     */
    protected abstract void initTitleView();

    /**
     * Find view
     */
    protected abstract void initContentView();
}
