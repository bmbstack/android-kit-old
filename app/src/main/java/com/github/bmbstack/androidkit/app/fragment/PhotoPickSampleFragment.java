package com.github.bmbstack.androidkit.app.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.bmbstack.androidkit.app.R;
import com.github.bmbstack.androidkit.base.BackPageActivity;
import com.github.bmbstack.androidkit.component.photopick.PhotoPickClipFragment;
import com.github.bmbstack.androidkit.component.photopick.PhotoPickFragment;
import com.github.bmbstack.androidkit.component.photopick.model.PhotoInfo;
import com.github.bmbstack.androidkit.util.DensityUtils;
import com.github.bmbstack.androidkit.util.PhotoOperate;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * PhotoPick 使用案例(支持单选,多选)
 */
public class PhotoPickSampleFragment extends Fragment {
    public static final int PHOTO_MAX_COUNT = 6;
    private static final int REQUEST_CODE_PICK_SINGLE = 0x21;
    private static final int REQUEST_CODE_PICK_MULTI = 0x22;
    private ImageView singleImageView;
    private GridView gridView;
    private ArrayList<PhotoData> mData = new ArrayList();
    private ImageSize mSize;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_PICK_SINGLE) {
            if(resultCode == PhotoPickFragment.RESULT_CODE_OK_PHOTOPICK_CLIP) {
                byte[] bitmapByteArray = data.getByteArrayExtra(PhotoPickClipFragment.BUNDLE_KEY_IMAGE_BYTE_ARRAY);
                if (bitmapByteArray != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapByteArray, 0, bitmapByteArray.length);
                    if (bitmap != null) {
                        singleImageView.setImageBitmap(bitmap);
                    }
                }
            }
        }else if(requestCode == REQUEST_CODE_PICK_MULTI) {
            if(resultCode == PhotoPickFragment.RESULT_CODE_OK_PHOTOPICK_CAMEAR) {
                PhotoInfo photoInfo = (PhotoInfo) data.getSerializableExtra(PhotoPickFragment.BUNDLE_KEY_PHOTOPICK_CAMERA);
                try {
                    PhotoOperate photoOperate = new PhotoOperate(getActivity());
                    Uri uri = Uri.parse(photoInfo.path);
                    File outputFile = photoOperate.scal(uri);
                    mData.add(new PhotoData(outputFile));
                    mAdapter.notifyDataSetChanged();

                } catch (Exception e) {

                }

            }else if(resultCode == PhotoPickFragment.RESULT_CODE_OK_PHOTOPICK_PICKED_DATA) {
                List<PhotoInfo>  pickedPhotos = (List<PhotoInfo>) data.getSerializableExtra(PhotoPickFragment.BUNDLE_KEY_PHOTOPICK_PICKED_DATA);
                try {
                    PhotoOperate photoOperate = new PhotoOperate(getActivity());
                    for (PhotoInfo item : pickedPhotos) {
                        Uri uri = Uri.parse(item.path);
                        File outputFile = photoOperate.scal(uri);
                        mData.add(new PhotoPickSampleFragment.PhotoData(outputFile));
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "缩放图片失败", Toast.LENGTH_SHORT).show();
                }
                mAdapter.notifyDataSetChanged();
            }
        }else {

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.singleImageView:
                    Bundle args = new Bundle();
                    args.putInt(BackPageActivity.BUNDLE_KEY_BACK_PAGE, BackPageActivity.BackPage.PHOTOPICK.getValue());
                    args.putBoolean(PhotoPickFragment.BUNDLE_KEY_PHOTOPICK_MODE_SINGLE, true);
                    BackPageActivity.startActivityForResult(getActivity(), REQUEST_CODE_PICK_SINGLE, args);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.fragment_photopick_sample, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        singleImageView = (ImageView) view.findViewById(R.id.singleImageView);
        gridView = (GridView) view.findViewById(R.id.gridView);
        singleImageView.setOnClickListener(mOnClickListener);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSize = new ImageSize(DensityUtils.dip2px(62), DensityUtils.dip2px(62));
        gridView.setAdapter(mAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == mData.size()) {
                    int count = PHOTO_MAX_COUNT - mData.size();
                    if (count <= 0) {
                        return;
                    }

                    Bundle args = new Bundle();
                    args.putInt(BackPageActivity.BUNDLE_KEY_BACK_PAGE, BackPageActivity.BackPage.PHOTOPICK.getValue());
                    args.putInt(PhotoPickFragment.EXTRA_MAX, count);
                    args.putBoolean(PhotoPickFragment.BUNDLE_KEY_PHOTOPICK_MODE_SINGLE, false);
                    BackPageActivity.startActivityForResult(getActivity(), REQUEST_CODE_PICK_MULTI, args);

                } else {
                }
            }
        });
    }

    BaseAdapter mAdapter = new BaseAdapter() {

        public int getCount() {
            return mData.size() + 1;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        ArrayList<ViewHolder> holderList = new ArrayList<ViewHolder>();

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                holder.image = (ImageView) View.inflate(getActivity(), R.layout.item_photopick_sample_grideview, null);
                holderList.add(holder);
                holder.image.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position == getCount() - 1) {
                if (getCount() == (PHOTO_MAX_COUNT + 1)) {
                    holder.image.setVisibility(View.INVISIBLE);
                } else {
                    holder.image.setVisibility(View.VISIBLE);
                    holder.image.setImageResource(R.drawable.photopick_add);
                    holder.uri = "";
                }
            } else {
                holder.image.setVisibility(View.VISIBLE);
                PhotoData photoData = mData.get(position);
                Uri data = photoData.uri;
                holder.uri = data.toString();
                ImageLoader.getInstance().loadImage(data.toString(), mSize, new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        for (ViewHolder viewHolder : holderList) {
                            if (viewHolder.uri.equals(imageUri)) {
                                int width = viewHolder.image.getMeasuredWidth();
                                GridView.LayoutParams params = new GridView.LayoutParams(width, width);
                                viewHolder.image.setLayoutParams(params);
                                viewHolder.image.setImageBitmap(loadedImage);
                            }
                        }
                    }
                });

            }

            return holder.image;
        }

        class ViewHolder {
            ImageView image;
            String uri = "";
        }

    };

    public static class PhotoData {
        Uri uri = Uri.parse("");
        String serviceUri = "";

        public PhotoData(File file) {
            uri = Uri.fromFile(file);
        }

        public PhotoData(PhotoDataSerializable data) {
            uri = Uri.parse(data.uriString);
            serviceUri = data.serviceUri;
        }
    }

    // 因为PhotoData包含Uri，不能直接序列化
    public static class PhotoDataSerializable implements Serializable {
        String uriString = "";
        String serviceUri = "";

        public PhotoDataSerializable(PhotoData data) {
            uriString = data.uri.toString();
            serviceUri = data.serviceUri;
        }
    }

}
