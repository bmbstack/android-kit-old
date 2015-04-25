/**
 * Copyright (c) 2005, Kuwo.cn, Inc. All rights reserved.
 */
package com.androidkit.sample;

import android.app.Application;

import com.androidkit.util.AppUtils;

public class App extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		AppUtils.initAndroidKit(getApplicationContext(), BuildConfig.DEBUG);
		AppUtils.initImageLoader(getApplicationContext());
	}
}
