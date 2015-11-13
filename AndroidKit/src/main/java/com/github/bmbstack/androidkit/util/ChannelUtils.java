package com.github.bmbstack.androidkit.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by wangming on 1/26/15.
 */
public class ChannelUtils {

    private static final String CHANNEL_KEY = "cztchannel";
    private static final String CHANNEL_VERSION_KEY = "cztchannel_version";
    private static String mChannel;

    public static String getChannel(Context context){
        return getChannel(context, "bqmart");
    }

    private static String getChannel(Context context, String defaultChannel) {
        if(!TextUtils.isEmpty(mChannel)){
            return mChannel;
        }
        mChannel = getChannelBySharedPreferences(context);
        if(!TextUtils.isEmpty(mChannel)){
            return mChannel;
        }
        mChannel = getChannelFromApk(context, CHANNEL_KEY);
        if(!TextUtils.isEmpty(mChannel)){
            saveChannelBySharedPreferences(context, mChannel);
            return mChannel;
        }
        return defaultChannel;
    }

    private static String getChannelFromApk(Context context, String channelKey) {
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        String key = "META-INF/" + channelKey;
        String ret = "";
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.startsWith(key)) {
                    ret = entryName;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String[] split = ret.split("_");
        String channel = "";
        if (split != null && split.length >= 2) {
            channel = ret.substring(split[0].length() + 1);
        }
        return channel;
    }

    private static void saveChannelBySharedPreferences(Context context, String channel){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CHANNEL_KEY, channel);
        editor.putInt(CHANNEL_VERSION_KEY, getVersionCode(context));
        editor.commit();
    }

    private static String getChannelBySharedPreferences(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        int currentVersionCode = getVersionCode(context);
        if(currentVersionCode == -1){
            return "";
        }
        int versionCodeSaved = sp.getInt(CHANNEL_VERSION_KEY, -1);
        if(versionCodeSaved == -1){
            return "";
        }
        if(currentVersionCode != versionCodeSaved){
            return "";
        }
        return sp.getString(CHANNEL_KEY, "");
    }

    private static int getVersionCode(Context context){
        try{
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        }catch(PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

}
