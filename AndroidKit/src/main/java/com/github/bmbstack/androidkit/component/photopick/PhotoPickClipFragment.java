package com.github.bmbstack.androidkit.component.photopick;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edmodo.cropper.CropImageView;
import com.github.bmbstack.androidkit.R;
import com.github.bmbstack.androidkit.util.CommonUtils;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * author: wangming
 * desc: 正方形图片截取
 */
public class PhotoPickClipFragment extends Fragment {
    public static final String BUNDLE_KEY_IMAGE_URI = "BUNDLE_KEY_IMAGE_URI" ;
    public static final String BUNDLE_KEY_IMAGE_BYTE_ARRAY = "BUNDLE_KEY_IMAGE_BYTE_ARRAY" ;
    private static final int DEFAULT_ASPECT_RATIO_VALUES = 10;
    private String mUri;
    private CropImageView cropImageView;
    private DonutProgress circleLoading;
    private LinearLayout llBottomOptions;
    private ImageView imageLoadFail;
    private TextView done;
    private TextView cancel;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.done) {
                // 由于Bundle传递bitmap不能超过40k,此处使用二进制数组传递
                Bitmap bitmap = cropImageView.getCroppedImage();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] bitmapByteArray = baos.toByteArray();
                if(isAdded()) {
                    Intent data = new Intent();
                    data.putExtra(BUNDLE_KEY_IMAGE_BYTE_ARRAY, bitmapByteArray);
                    getActivity().setResult(Activity.RESULT_OK, data);
                    getActivity().finish();
                }
            }else if(id == R.id.cancel) {
                if(isAdded()) {
                    getActivity().finish();
                }
            }else {
                if(isAdded()) {
                    getActivity().finish();
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.fragment_photopick_clip, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cropImageView = (CropImageView) view.findViewById(R.id.cropImageView);
        cropImageView.setDrawingCacheEnabled(true);
        //cropImageView.setAspectRatio(10, 10);
        //cropImageView.setFixedAspectRatio(true);
        imageLoadFail = (ImageView)view.findViewById(R.id.imageLoadFail);
        circleLoading = (DonutProgress)view.findViewById(R.id.circleLoading);
        llBottomOptions = (LinearLayout)view.findViewById(R.id.llBottomOptions);
        done = (TextView)view.findViewById(R.id.done);
        cancel = (TextView)view.findViewById(R.id.cancel);
        done.setOnClickListener(mOnClickListener);
        cancel.setOnClickListener(mOnClickListener);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        mUri = getArguments().getString(BUNDLE_KEY_IMAGE_URI);
        if(mUri != null) {
            showPhoto();
        }
    }

    private void showPhoto() {
        if(!isAdded()) {
            return;
        }
        int widthPix = getResources().getDisplayMetrics().widthPixels;
        int heightPix = getResources().getDisplayMetrics().heightPixels;
        ImageSize size = new ImageSize(widthPix, heightPix);
        ImageLoader.getInstance().loadImage(mUri, size, CommonUtils.optionsImage, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                circleLoading.setVisibility(View.VISIBLE);
                llBottomOptions.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (!isAdded()) {
                    return;
                }
                circleLoading.setVisibility(View.GONE);
                imageLoadFail.setVisibility(View.VISIBLE);
                llBottomOptions.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(final String imageUri, View view, Bitmap loadedImage) {
                if (!isAdded()) {
                    return;
                }

                circleLoading.setVisibility(View.GONE);
                File file = ImageLoader.getInstance().getDiskCache().get(imageUri);
                if (CommonUtils.isGifByFile(file)) {
                    //TODO GIF
                } else {
                    cropImageView.setImageBitmap(loadedImage);
                    cropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES, DEFAULT_ASPECT_RATIO_VALUES);
                }
                llBottomOptions.setVisibility(View.VISIBLE);
            }
        }, new ImageLoadingProgressListener() {

            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
                if (!isAdded()) {
                    return;
                }
                int progress = current * 100 / total;
                circleLoading.setProgress(progress);
            }
        });
    }
}
