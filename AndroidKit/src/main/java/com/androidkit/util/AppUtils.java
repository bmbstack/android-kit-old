package com.androidkit.util;

import android.content.Context;

import com.androidkit.context.AppContext;
import com.androidkit.context.AppCrashHandler;
import com.androidkit.context.DefaultFileDirectoryContext;
import com.androidkit.file.FileDirectoryContext;
import com.androidkit.file.FileSystem;
import com.androidkit.file.PreferencesManager;
import com.androidkit.log.LogSystem;
import com.androidkit.log.Logger;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by wangming on 4/7/15.
 */
public class AppUtils {

    /**
     * init androidKit
     *
     * @param applicationContext
     * @param debug
     */
    public static void initAndroidKit(Context applicationContext, boolean debug) {
        LogSystem.init(debug == true ? Logger.LOG_LEVEL_DEBUG : Logger.LOG_LEVEL_NONE);
        AppContext.init(applicationContext); // 初始化应用上下文
        FileSystem.init(new DefaultFileDirectoryContext(AppContext.getAppName())); // 初始化文件系统
        String logDirectory = "";
        if (FileSystem.available()) {
            LogSystem.i("初始化文件系统成功！");
            logDirectory = FileSystem.getDirectoryPath(FileDirectoryContext.FILE_DIRECTORY_TYPE_SDCARD_LOG); //外部存储
            if(!debug) {
                AppCrashHandler crashHandler = AppCrashHandler.getInstance();
                crashHandler.init(applicationContext);
                Thread.currentThread().setUncaughtExceptionHandler(crashHandler);
            }
        } else {
            LogSystem.e("初始化文件系统失败！请检查SD卡，和user persission");
            logDirectory = applicationContext.getFilesDir().getAbsolutePath(); //内部存储
        }
        LogSystem.persistenceLog(logDirectory, AppContext.getAppName()+".log");
        PreferencesManager.init(applicationContext); // 初始化配置文件
    }

    /**
     * initial Universal ImageLoader
     *
     * @param applicationContext
     */
    public static void initImageLoader(Context applicationContext) {
        File cacheDir = null;
        if (FileSystem.available())
            cacheDir = StorageUtils.getOwnCacheDirectory(applicationContext, FileSystem.getDirectoryPath(DefaultFileDirectoryContext.FILE_DIRECTORY_TYPE_PICTURE));
        else
            cacheDir = StorageUtils.getCacheDirectory(applicationContext);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(applicationContext)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .discCache(new UnlimitedDiscCache(cacheDir))
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }
}
