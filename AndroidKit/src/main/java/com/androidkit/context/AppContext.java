/**
 * Copyright (c) 2013, BigBeard Team, Inc. All rights reserved. 
 */
package com.androidkit.context;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;

import com.androidkit.log.LogSystem;

import java.util.Locale;

public class AppContext {
	private static boolean sInited = false;
	private static NetworkSensor sNetworkSensor;
	public static Context context;
	
	/**
	 *  The unique device ID, for example, 
	 *  the IMEI for GSM and the MEID or ESN for CDMA phones
	 */
	public static String DEVICE_ID;

	/**
	 * The application's version code
	 */
	public static int VERSION_CODE;

	/**
	 * The application's version name
	 */
	public static String VERSION_NAME;

	/**
	 * The screen width of the android device
	 */
	public static int SCREEN_WIDTH;

	/**
	 * The screen height of the android device
	 */
	public static int SCREEN_HEIGHT;

	/**
	 * The source of install
	 */
	public static String INSTALL_SOURCE;

	public synchronized static boolean init(Context context) {
		if (sInited)
			return true;

		AppContext.context = context;

		LogSystem.i("MODEL: " + Build.MODEL);
		LogSystem.i("BOARD: " + Build.BOARD);
		LogSystem.i("BRAND: " + Build.BRAND);
		LogSystem.i("DEVICE: " + Build.DEVICE);
		LogSystem.i("PRODUCT: " + Build.PRODUCT);
		LogSystem.i("DISPLAY: " + Build.DISPLAY);
		LogSystem.i("HOST: " + Build.HOST);
		LogSystem.i("ID: " + Build.ID);
		LogSystem.i("USER: " + Build.USER);

		try {
			String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
			if (deviceId == null) {
				WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				WifiInfo info = wifi.getConnectionInfo();
				deviceId = info.getMacAddress(); // if device id is null, use the mac address.
			}
			if (deviceId == null)
				deviceId = "000000";  //if device id and mac address is null, use the string value "000000"
			DEVICE_ID = deviceId;
			LogSystem.i("DEVICE_ID:" + DEVICE_ID);

			// Get the version code and version code from the PackageInfo object
			PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			VERSION_CODE = pi.versionCode;
			VERSION_NAME = pi.versionName;
			LogSystem.i("install VERSION_CODE:" + VERSION_CODE);

			// Get the screen width and height from Display object
			WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			SCREEN_WIDTH = display.getWidth(); //deprecated Use getSize(Point) instead.
			SCREEN_HEIGHT = display.getHeight(); //deprecated Use getSize(Point) instead.
			LogSystem.i(String.format("Screen width: %s  height: %s", SCREEN_WIDTH, SCREEN_HEIGHT));

			//Initial the network environment
			sNetworkSensor = new NetworkSensor(context);

		} catch (Exception e) {
			return false;
		}

		sInited = true;
		return true;
	}

	public static String getAppName() {
		return context.getPackageManager().getApplicationLabel(context.getApplicationInfo()).toString();
	}

	public static NetworkSensor getNetworkSensor() {
		return sNetworkSensor;
	}

	public static boolean isChinese(Context context) {
		Locale locale = context.getResources().getConfiguration().locale;
		return locale.toString().equals("zh_CN");
	}

	public static boolean isScreenPortrait(Context context) {
		return context.getResources().getConfiguration().orientation == 1;
	}
}
