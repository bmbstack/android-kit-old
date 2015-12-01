package com.github.bmbstack.androidkit.component.photopick;

import android.annotation.TargetApi;
import android.app.Activity;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import com.github.bmbstack.androidkit.R;
import com.github.bmbstack.androidkit.base.BackPageActivity;
import com.github.bmbstack.androidkit.component.photopick.model.PhotoInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class PhotoPickDetailFragment extends Fragment {

    public static final String PICK_DATA = "PICK_DATA";
    public static final String ALL_DATA = "ALL_DATA";
    public static final String FOLDER_NAME = "FOLDER_NAME";
    public static final String PHOTO_BEGIN = "PHOTO_BEGIN";
    public static final String EXTRA_MAX = "EXTRA_MAX";

    private ArrayList<PhotoInfo> mPickPhotos;
    private ArrayList<PhotoInfo> mAllPhotos;

    private ViewPager mViewPager;
    private CheckBox mCheckBox;

    private int mMaxPick = PhotoPickFragment.PHOTO_MAX_COUNT;
    private final String actionbarTitle = "%d/%d";
    private ImagesAdapter mAdapter;
    private Cursor mCursor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_photopick_detail, null);
        mCheckBox = (CheckBox) view.findViewById(R.id.checkbox);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                updateDisplay(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = mViewPager.getCurrentItem();
                String uri = getImagePath(pos);
                if (((CheckBox) v).isChecked()) {
                    if (mPickPhotos.size() >= mMaxPick) {
                        ((CheckBox) v).setChecked(false);
                        String s = String.format("最多只能选择%d张", mMaxPick);
                        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    addPicked(uri);
                } else {
                    removePicked(uri);
                }
                updateDataPickCount();
                mAdapter.notifyDataSetChanged();
            }
        });
        return view;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle extras = getArguments();
        mPickPhotos = (ArrayList<PhotoInfo>) extras.getSerializable(PICK_DATA);
        //深copy
        ArrayList<PhotoInfo> allPhotos = (ArrayList<PhotoInfo>) extras.getSerializable(ALL_DATA);
        if(allPhotos != null) {
            mAllPhotos = new ArrayList<PhotoInfo>(Arrays.asList(new PhotoInfo[allPhotos.size()]));
            Collections.copy(mAllPhotos, allPhotos);
        }

        int mBegin = extras.getInt(PHOTO_BEGIN, 0);
        mMaxPick = extras.getInt(EXTRA_MAX, 5);
        if (mAllPhotos == null) {
            String folderName = extras.getString(FOLDER_NAME, "");
            String where = folderName;
            if (!folderName.isEmpty()) {
                where = String.format("%s='%s'",
                        MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                        folderName);
            }
            mCursor = getActivity().getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{
                            MediaStore.Images.ImageColumns._ID,
                            MediaStore.Images.ImageColumns.DATA},
                    where,
                    null,
                    MediaStore.MediaColumns.DATE_ADDED + " DESC");
        }
        mAdapter = new ImagesAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mBegin);

        BackPageActivity backPageActivity = (BackPageActivity) getActivity();
        backPageActivity.addTitleLeftBackView(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(isAdded()) {
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                }
            }
        });

        updateDisplay(mBegin);
        updateDataPickCount();
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        super.onDestroy();
    }

    private void updateDisplay(int pos) {
        String uri = getImagePath(pos);
        mCheckBox.setChecked(isPicked(uri));
        getActivity().setTitle(String.format(actionbarTitle, pos + 1, getImageCount()));
    }

    private boolean isPicked(String path) {
        for (PhotoInfo item : mPickPhotos) {
            if (item.path.equals(path)) {
                return true;
            }
        }
        return false;
    }

    private void addPicked(String path) {
        if (!isPicked(path)) {
            mPickPhotos.add(new PhotoInfo(path));
        }
    }

    private void removePicked(String path) {
        for (int i = 0; i < mPickPhotos.size(); ++i) {
            if (mPickPhotos.get(i).path.equals(path)) {
                mPickPhotos.remove(i);
                return;
            }
        }
    }

    public void updateDataPickCount() {
        String selectedTitle = String.format("已选(%d/%d)", mPickPhotos.size(), mMaxPick);
        mCheckBox.setText(selectedTitle);
    }

    /**
     * Pager adapter
     */
    private class ImagesAdapter extends FragmentStatePagerAdapter {

        ImagesAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            PhotoPickPagerFragment fragment = new PhotoPickPagerFragment();
            Bundle bundle = new Bundle();
            String path = getImagePath(position);
            bundle.putString(PhotoPickPagerFragment.BUNDLE_KEY_IMAGE_URI, PhotoInfo.pathAddPreFix(path));
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return getImageCount();
        }
    }

    private String getImagePath(int pos) {
        if (mAllPhotos != null) {
            return mAllPhotos.get(pos).path;
        } else {
            String path = "";
            if (mCursor.moveToPosition(pos)) {
                path = PhotoInfo.pathAddPreFix(mCursor.getString(1));
            }
            return path;
        }
    }

    private int getImageCount() {
        if (mAllPhotos != null) {
            return mAllPhotos.size();
        } else if (mCursor != null){
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

}
