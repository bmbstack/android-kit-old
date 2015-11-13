package com.github.bmbstack.androidkit.util;

import android.content.Context;

import com.github.bmbstack.androidkit.context.AppContext;
import com.github.bmbstack.androidkit.context.AppCrashHandler;
import com.github.bmbstack.androidkit.context.DefaultFileDirectoryContext;
import com.github.bmbstack.androidkit.file.FileDirectoryContext;
import com.github.bmbstack.androidkit.file.FileSystem;
import com.github.bmbstack.androidkit.file.PreferencesManager;
import com.github.bmbstack.androidkit.log.LogSystem;
import com.github.bmbstack.androidkit.log.Logger;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
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
                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                .diskCacheExtraOptions(480, 800, null)
                .threadPoolSize(3) // default
                .threadPriority(Thread.NORM_PRIORITY - 2) // default
                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .diskCache(new UnlimitedDiscCache(cacheDir)) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(applicationContext)) // default
                .imageDecoder(new BaseImageDecoder(true)) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);
    }
}
