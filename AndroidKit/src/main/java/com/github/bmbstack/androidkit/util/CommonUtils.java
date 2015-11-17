/**
 * Copyright (c) 2005, Kuwo, Inc. All rights reserved. 
 */
package com.github.bmbstack.androidkit.util;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CommonUtils {
	
	public static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				} else {
					imageView.setImageBitmap(loadedImage);
				}
			}
		}
	}

	public static void popSoftkeyboard(Context ctx, View view, boolean wantPop) {
		InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (wantPop) {
			view.requestFocus();
			imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
		} else {
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public static DisplayImageOptions optionsImage = new DisplayImageOptions
			.Builder()
			.showImageOnLoading(0)
			.showImageForEmptyUri(0)
			.showImageOnFail(0)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.cacheOnDisk(true)
			.resetViewBeforeLoading(true)
			.cacheInMemory(false)
			.considerExifParams(true)
			.imageScaleType(ImageScaleType.EXACTLY)
			.build();

	public static void copy(Context context, String content) {
		ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(content);
	}

	// 通过文件头来判断是否gif
	public static boolean isGifByFile(File file) {
		try {
			int length = 10;
			InputStream is = new FileInputStream(file);
			byte[] data = new byte[length];
			is.read(data);
			String type = getType(data);
			is.close();

			if (type.equals("gif")) {
				return true;
			}
		} catch (IOException e) {
		}

		return false;
	}

	private static String getType(byte[] data) {
		String type = "";
		if (data[1] == 'P' && data[2] == 'N' && data[3] == 'G') {
			type = "png";
		} else if (data[0] == 'G' && data[1] == 'I' && data[2] == 'F') {
			type = "gif";
		} else if (data[6] == 'J' && data[7] == 'F' && data[8] == 'I'
				&& data[9] == 'F') {
			type = "jpg";
		}
		return type;
	}


	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}


	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context       The context.
	 * @param uri           The Uri to query.
	 * @param selection     (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection,
									   String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {
				column
		};

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}


	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}

	public static boolean isGif(String uri) {
		return uri.toLowerCase().endsWith(".gif");
	}

	public static boolean isImageUri(String s1) {
		s1 = s1.toLowerCase();
		return s1.endsWith(".png")
				|| s1.endsWith(".jpg")
				|| s1.endsWith(".jpeg")
				|| s1.endsWith(".bmp")
				|| s1.endsWith(".gif");
	}

	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[]{
						split[1]
				};

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {

			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}
}
