/**
 * Copyright (c) 2013, BigBeard Team, Inc. All rights reserved. 
 */
package com.github.bmbstack.androidkit.context;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.github.bmbstack.androidkit.file.FileDirectoryContext;
import com.github.bmbstack.androidkit.file.FileSystem;
import com.github.bmbstack.androidkit.log.LogSystem;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class AppCrashHandler implements Thread.UncaughtExceptionHandler {
	private static final String LOG_TAG = "AppCrashHandler";
	private static AppCrashHandler instance;
	private Context mContext;

	/**
	 * Make the constructor to private
	 */
	private AppCrashHandler() {
	}

	/**
	 * Use the single pattern to create the AppCrashHandler object
	 * 
	 * @return AppCrashHandler
	 */
	public static synchronized AppCrashHandler getInstance() {
		if (null == instance) {
			instance = new AppCrashHandler();
		}
		return instance;
	}

	/**
	 * Initial the AppCrashHandler with the Context object
	 * 
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable throwable) {
		LogSystem.e("The application is crashing...");
		if (Environment.getExternalStorageDirectory() != null) {
			String crashFileName = FileSystem.getFilePath(FileDirectoryContext.FILE_DIRECTORY_TYPE_SDCARD_LOG, "crash.log");
			try {
				FileWriter fw = new FileWriter(crashFileName, true);
				fw.write(new Date() + "\n");
				StackTraceElement[] stackTrace = throwable.getStackTrace();
				fw.write(throwable.getMessage() + "\n");
				for (int i = 0; i < stackTrace.length; i++) {
					fw.write("file:" + stackTrace[i].getFileName() + " class:"
							+ stackTrace[i].getClassName() + " method:"
							+ stackTrace[i].getMethodName() + " line:"
							+ stackTrace[i].getLineNumber() + "\n");
				}
				fw.write("\n");
				fw.close();
			} catch (IOException e) {
				Log.e("crash handler", "load file failed...", e.getCause());
			}
		}
		throwable.printStackTrace();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}
