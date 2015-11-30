/**
 * Copyright (C) 2015 The AndroidPhoneStudent Project
 */
package com.github.bmbstack.androidkit.component.photopick;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.bmbstack.androidkit.R;
import com.github.bmbstack.androidkit.base.BackPageActivity;
import com.github.bmbstack.androidkit.component.photopick.adapter.AllPhotoAdapter;
import com.github.bmbstack.androidkit.component.photopick.adapter.FolderAdapter;
import com.github.bmbstack.androidkit.component.photopick.adapter.PhotoAdapter;
import com.github.bmbstack.androidkit.component.photopick.model.FolderPhotoInfo;
import com.github.bmbstack.androidkit.component.photopick.model.PhotoInfo;
import com.github.bmbstack.androidkit.util.CameraPhotoUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PhotoPickFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String EXTRA_MAX = "EXTRA_MAX";
    public static final String BUNDLE_KEY_PHOTOPICK_PICKED_DATA = "BUNDLE_KEY_PHOTOPICK_PICKED_DATA"; // mPickData
    public static final String BUNDLE_KEY_PHOTOPICK_CAMERA = "BUNDLE_KEY_PHOTOPICK_CAMERA"; //多选模式拍照
    public static final String BUNDLE_KEY_PHOTOPICK_MODE_SINGLE = "BUNDLE_KEY_PHOTOPICK_MODE_SINGLE";
    public static final int PHOTO_MAX_COUNT = 6;
    private static final int REQUEST_CODE_CAMERA = 0x21;
    private static final int REQUEST_CODE_PHOTOPICK_CLIP = 0x31;
    private static final int REQUEST_CODE_PHOTOPICK_DETAIL = 0x32;
    public static final int RESULT_CODE_OK_PHOTOPICK_CLIP = 0x33;  //目前是单选
    public static final int RESULT_CODE_OK_PHOTOPICK_CAMEAR = 0x34; //目前是多选
    public static final int RESULT_CODE_OK_PHOTOPICK_PICKED_DATA = 0x35; //多选图片
    private final String allPhotos = "所有图片";
    private int mFolderId = 0;
    private int mMaxPick = PhotoPickFragment.PHOTO_MAX_COUNT; //
    private TextView mFoldName;
    private View mListViewGroup;
    private ListView mListView;
    private GridView mGridView;
    private TextView mPreView;
    private Uri fileUri;
    private ArrayList<PhotoInfo> mPickData = new ArrayList<PhotoInfo>();
    private FolderAdapter mFolderAdapter;
    private PhotoAdapter mPhotoAdapter;
    private boolean mIsSingleMode = false;
    private MenuItem mFinishMenu;

    private String[] projection = {
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Images.ImageColumns.WIDTH,
            MediaStore.Images.ImageColumns.HEIGHT
    };

    //folder click listener
    private View.OnClickListener mOnClick4PhotoFolder = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListViewGroup.getVisibility() == View.VISIBLE) {
                hideFolderList();
            } else {
                showFolderList();
            }
        }

    };

    //folder item click listener
    private ListView.OnItemClickListener mOnClick4PhotoFolderItem = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mFolderAdapter.setSelect((int) id);
            String folderName = mFolderAdapter.getSelect();
            mFoldName.setText(folderName);
            hideFolderList();

            if (mFolderId != position) {
                getLoaderManager().destroyLoader(mFolderId);
                mFolderId = position;
            }
            getLoaderManager().initLoader(mFolderId, null, PhotoPickFragment.this);
        }
    };

    //photo item click listener
    private GridView.OnItemClickListener mOnClick4PhotoItem = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(mIsSingleMode) {
                //单选模式
                String folderName = mFolderAdapter.getSelect();
                String where = folderName;
                if (!folderName.isEmpty()) {
                    where = String.format("%s='%s'",
                            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                            folderName);
                }
                Cursor cursor = getActivity().getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{
                                MediaStore.Images.ImageColumns._ID,
                                MediaStore.Images.ImageColumns.DATA},
                        where,
                        null,
                        MediaStore.MediaColumns.DATE_ADDED + " DESC");
                String imagePath = "";
                int targetPosition = position;
                if (isAllPhotoMode()) {
                    targetPosition = position - 1;
                }
                if (cursor.moveToPosition(targetPosition)) {
                    imagePath = PhotoInfo.pathAddPreFix(cursor.getString(1));
                }

                Bundle args = new Bundle();
                Intent intent = new Intent(getActivity(), BackPageActivity.class);
                intent.putExtra(BackPageActivity.BUNDLE_KEY_BACK_PAGE, BackPageActivity.BackPage.PHOTOPICKCLIP.getValue());
                intent.putExtra(PhotoPickClipFragment.BUNDLE_KEY_IMAGE_URI, imagePath);
                getActivity().startActivityForResult(intent, PhotoPickFragment.REQUEST_CODE_PHOTOPICK_CLIP);
            }else {
                //多选模式
                Intent intent = new Intent(getActivity(), BackPageActivity.class);
                intent.putExtra(BackPageActivity.BUNDLE_KEY_BACK_PAGE, BackPageActivity.BackPage.PHOTOPICKDETAIL.getValue());
                intent.putExtra(PhotoPickDetailFragment.PICK_DATA, (Serializable) mPickData);
                intent.putExtra(PhotoPickDetailFragment.EXTRA_MAX, mMaxPick);

                String folderParam = "";
                if (isAllPhotoMode()) {
                    // 第一个item是照相机
                    intent.putExtra(PhotoPickDetailFragment.PHOTO_BEGIN, position - 1);
                } else {
                    intent.putExtra(PhotoPickDetailFragment.PHOTO_BEGIN, position);
                    folderParam = mFolderAdapter.getSelect();
                }
                intent.putExtra(PhotoPickDetailFragment.FOLDER_NAME, folderParam);
                getActivity().startActivityForResult(intent, PhotoPickFragment.REQUEST_CODE_PHOTOPICK_DETAIL);
            }

        }
    };

    //preview button click listener
    private View.OnClickListener mOnClick4Preview = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mPickData.size() == 0) {
                return;
            }

            Intent intent = new Intent(getActivity(), BackPageActivity.class);
            intent.putExtra(BackPageActivity.BUNDLE_KEY_BACK_PAGE, BackPageActivity.BackPage.PHOTOPICKDETAIL.getValue());
            intent.putExtra(PhotoPickDetailFragment.FOLDER_NAME, mFolderAdapter.getSelect());
            intent.putExtra(PhotoPickDetailFragment.PICK_DATA, mPickData);
            intent.putExtra(PhotoPickDetailFragment.ALL_DATA, (Serializable) mPickData);
            intent.putExtra(PhotoPickDetailFragment.EXTRA_MAX, mMaxPick);
            getActivity().startActivityForResult(intent, PhotoPickFragment.REQUEST_CODE_PHOTOPICK_DETAIL);
        }
    };

    public void clickPhotoItem(View v) {
        GridViewCheckTag tag = (GridViewCheckTag) v.getTag();
        if (((CheckBox) v).isChecked()) {
            if (mPickData.size() >= mMaxPick) {
                ((CheckBox) v).setChecked(false);
                String s = String.format("最多只能选择%d张", mMaxPick);
                Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
                return;
            }

            addPicked(tag.path);
            tag.iconFore.setVisibility(View.VISIBLE);
        } else {
            removePicked(tag.path);
            tag.iconFore.setVisibility(View.INVISIBLE);
        }
        ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();

        updatePickCount();
        updateFinishState();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_photopick, menu);
        mFinishMenu = menu.findItem(R.id.action_finish);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_finish) {
            if (mListViewGroup.getVisibility() == View.VISIBLE) {
                hideFolderList();
            } else {
               if(isAdded()) {
                   mFinishMenu.setEnabled(false);
                   Intent data = new Intent();
                   data.putExtra(BUNDLE_KEY_PHOTOPICK_PICKED_DATA, mPickData);
                   getActivity().setResult(RESULT_CODE_OK_PHOTOPICK_PICKED_DATA, data);
                   getActivity().finish();
               }
            }
        }
        return true;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        mIsSingleMode = args.getBoolean(PhotoPickFragment.BUNDLE_KEY_PHOTOPICK_MODE_SINGLE);
        View view = View.inflate(getActivity(), R.layout.fragment_photopick, null);
        Object extraPicked = getActivity().getIntent().getSerializableExtra(BUNDLE_KEY_PHOTOPICK_PICKED_DATA);

        if (extraPicked != null) {
            mPickData = (ArrayList<PhotoInfo>) extraPicked;
        }

        mGridView = (GridView) view.findViewById(R.id.gridView);
        mListView = (ListView) view.findViewById(R.id.listView);
        mListViewGroup = view.findViewById(R.id.listViewParent);
        mListViewGroup.setOnClickListener(mOnClick4PhotoFolder);
        mFoldName = (TextView) view.findViewById(R.id.foldName);
        mFoldName.setText(allPhotos);

        view.findViewById(R.id.selectFold).setOnClickListener(mOnClick4PhotoFolder);
        mPreView = (TextView) view.findViewById(R.id.preView);
        if(mIsSingleMode) {
            mPreView.setVisibility(View.GONE); //单选模式隐藏 preview按钮
        }else {
            mPreView.setVisibility(View.VISIBLE);
            mPreView.setOnClickListener(mOnClick4Preview);
        }

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        initListView4PhotoFolder();
        initGridView();
    }

    private void initListView4PhotoFolder() {
        final String[] needInfos = {
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
        };

        LinkedHashMap<String, Integer> mNames = new LinkedHashMap<String, Integer>();
        LinkedHashMap<String, PhotoInfo> mData = new LinkedHashMap<String, PhotoInfo>();
        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, needInfos, "", null, MediaStore.MediaColumns.DATE_ADDED + " DESC");

        while (cursor.moveToNext()) {
            String name = cursor.getString(2);
            if (!mNames.containsKey(name)) {
                mNames.put(name, 1);
                PhotoInfo photoInfo = new PhotoInfo(cursor.getString(1));
                mData.put(name, photoInfo);
            } else {
                int newCount = mNames.get(name) + 1;
                mNames.put(name, newCount);
            }
        }

        ArrayList<FolderPhotoInfo> mFolderData = new ArrayList<FolderPhotoInfo>();
        if (cursor.moveToFirst()) {
            PhotoInfo photoInfo = new PhotoInfo(cursor.getString(1));
            int allImagesCount = cursor.getCount();
            mFolderData.add(new FolderPhotoInfo(allPhotos, photoInfo, allImagesCount));
        }

        for (String item : mNames.keySet()) {
            PhotoInfo info = mData.get(item);
            Integer count = mNames.get(item);
            mFolderData.add(new FolderPhotoInfo(item, info, count));
        }
        cursor.close();

        mFolderAdapter = new FolderAdapter(mFolderData);
        mListView.setAdapter(mFolderAdapter);
        mListView.setOnItemClickListener(mOnClick4PhotoFolderItem);
    }

    private void initGridView() {
        getLoaderManager().initLoader(0, null, this);
    }

    private void showFolderList() {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.listview_up);
        Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.listview_fade_in);

        mListView.startAnimation(animation);
        mListViewGroup.startAnimation(fadeIn);
        mListViewGroup.setVisibility(View.VISIBLE);
    }

    private void hideFolderList() {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.listview_down);
        Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.listview_fade_out);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mListViewGroup.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mListView.startAnimation(animation);
        mListViewGroup.startAnimation(fadeOut);
    }

    public void camera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = CameraPhotoUtils.getOutputMediaFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    private void addPicked(String path) {
        if (!isPicked(path)) {
            mPickData.add(new PhotoInfo(path));
        }
        mFinishMenu.setEnabled(true);
    }

    public boolean isPicked(String path) {
        for (PhotoInfo item : mPickData) {
            if (item.path.equals(path)) {
                return true;
            }
        }
        return false;
    }

    private void removePicked(String path) {
        for (int i = 0; i < mPickData.size(); ++i) {
            if (mPickData.get(i).path.equals(path)) {
                mPickData.remove(i);
                return;
            }
        }
    }

    private void updateFinishState() {
        if(mPickData.size() == 0) {
            mFinishMenu.setEnabled(false);
        }else {
            mFinishMenu.setEnabled(true);
        }
    }

    private void updatePickCount() {
        String format = "完成(%d/%d)";
        mFinishMenu.setTitle(String.format(format, mPickData.size(), mMaxPick));

        String formatPreview = "预览(%d/%d)";
        mPreView.setText(String.format(formatPreview, mPickData.size(), mMaxPick));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String where;
        if (!isAllPhotoMode()) {
            String select = ((FolderAdapter) mListView.getAdapter()).getSelect();
            where = String.format("%s='%s'",
                    MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                    select
            );
        } else {
            where = "";
        }
        return new CursorLoader(getActivity(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                where,
                null,
                MediaStore.MediaColumns.DATE_ADDED + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (isAllPhotoMode()) {
            mPhotoAdapter = new AllPhotoAdapter(getActivity(), data, false, mIsSingleMode,  PhotoPickFragment.this);
        } else {
            mPhotoAdapter = new PhotoAdapter(getActivity(), data, false, mIsSingleMode, PhotoPickFragment.this);
        }
        mGridView.setAdapter(mPhotoAdapter);
        mGridView.setOnItemClickListener(mOnClick4PhotoItem);
    }

    /*
     * 选择了listview的第一个项，gridview的第一个是照相机
     */
    private boolean isAllPhotoMode() {
        return mFolderId == 0;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPhotoAdapter.swapCursor(null);
    }

    public static class GridViewCheckTag {
        View iconFore;
        public String path = "";

        public GridViewCheckTag(View iconFore) {
            this.iconFore = iconFore;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                if(mIsSingleMode) {
                    String imagePath = fileUri.toString();
                    Intent intent = new Intent(getActivity(), BackPageActivity.class);
                    intent.putExtra(BackPageActivity.BUNDLE_KEY_BACK_PAGE, BackPageActivity.BackPage.PHOTOPICKCLIP.getValue());
                    intent.putExtra(PhotoPickClipFragment.BUNDLE_KEY_IMAGE_URI, imagePath);
                    getActivity().startActivityForResult(intent, PhotoPickFragment.REQUEST_CODE_PHOTOPICK_CLIP);
                }else {
                    PhotoInfo photoInfo = new PhotoInfo(fileUri.toString());
                    if(isAdded()) {
                        Intent intent = new Intent();
                        intent.putExtra(PhotoPickFragment.BUNDLE_KEY_PHOTOPICK_CAMERA, photoInfo);
                        getActivity().setResult(RESULT_CODE_OK_PHOTOPICK_CAMEAR, intent);
                        getActivity().finish();
                    }
                }
            }
        }else if(requestCode == REQUEST_CODE_PHOTOPICK_CLIP) {
            if(resultCode == Activity.RESULT_OK) {
                if(isAdded()) {
                    Intent intent = new Intent();
                    byte[] bitmapByteArray = data.getByteArrayExtra(PhotoPickClipFragment.BUNDLE_KEY_IMAGE_BYTE_ARRAY);
                    intent.putExtra(PhotoPickClipFragment.BUNDLE_KEY_IMAGE_BYTE_ARRAY, bitmapByteArray);
                    getActivity().setResult(RESULT_CODE_OK_PHOTOPICK_CLIP, intent);
                    getActivity().finish();
                }
            }
        }else if(requestCode == REQUEST_CODE_PHOTOPICK_DETAIL) {
            if(requestCode == Activity.RESULT_OK) {
                mPhotoAdapter.notifyDataSetChanged();
                updatePickCount();
                updateFinishState();
            }
        }else {

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
