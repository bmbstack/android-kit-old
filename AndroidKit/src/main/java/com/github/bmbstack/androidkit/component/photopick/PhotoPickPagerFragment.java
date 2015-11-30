package com.github.bmbstack.androidkit.component.photopick;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.github.bmbstack.androidkit.R;
import com.github.bmbstack.androidkit.util.CommonUtils;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;

import pl.droidsonroids.gif.GifImageView;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoPickPagerFragment extends Fragment {
    public static final String BUNDLE_KEY_IMAGE_URI = "BUNDLE_KEY_IMAGE_URI" ;
    private DonutProgress circleLoading;
    private ImageView imageLoadFail;
    private String mUri;
    private View image;
    private FrameLayout rootLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.fragment_photopick_pager, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootLayout = (FrameLayout) view.findViewById(R.id.rootLayout);
        circleLoading = (DonutProgress)view.findViewById(R.id.circleLoading);
        imageLoadFail = (ImageView) view.findViewById(R.id.imageLoadFail);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        PhotoPickDetailFragment parentFragment = (PhotoPickDetailFragment) getParentFragment();
        parentFragment.updateDataPickCount();
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
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (!isAdded()) {
                    return;
                }
                circleLoading.setVisibility(View.GONE);
                imageLoadFail.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingComplete(final String imageUri, View view, Bitmap loadedImage) {
                if (!isAdded()) {
                    return;
                }

                circleLoading.setVisibility(View.GONE);
                File file = ImageLoader.getInstance().getDiskCache().get(imageUri);
                if (CommonUtils.isGifByFile(file)) {
                    image = getActivity().getLayoutInflater().inflate(R.layout.imageview_gif, null);
                    rootLayout.addView(image);
                    image.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            getActivity().onBackPressed();
                        }
                    });
                } else {
                    PhotoView photoView = (PhotoView) getActivity().getLayoutInflater().inflate(R.layout.imageview_touch, null);
                    image = photoView;
                    rootLayout.addView(image);
                    photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                        @Override
                        public void onPhotoTap(View view, float x, float y) {
                            getActivity().onBackPressed();
                        }
                    });
                }

                try {
                    if (image instanceof GifImageView) {
                        Uri uri1 = Uri.fromFile(file);
                        ((GifImageView) image).setImageURI(uri1);
                    } else if (image instanceof PhotoView) {
                        ((PhotoView) image).setImageBitmap(loadedImage);

                    }
                } catch (Exception e) {
                }
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
