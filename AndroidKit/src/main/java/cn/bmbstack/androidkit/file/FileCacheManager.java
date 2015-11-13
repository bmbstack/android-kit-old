/**
 * Copyright (c) 2013, BigBeard Team, Inc. All rights reserved. 
 */
package cn.bmbstack.androidkit.file;

import org.apache.http.util.EncodingUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import cn.bmbstack.androidkit.context.AppContext;
import cn.bmbstack.androidkit.log.LogSystem;

public class FileCacheManager {

	public static String readFile(File file) {
		String res = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			LogSystem.printStackTrace(e);
		} catch (IOException e) {
			e.printStackTrace();
			LogSystem.printStackTrace(e);
		}
		return res;
	}

	public static boolean cacheString(String fileName, String content) {
		File file = getCacheFile(fileName);
		try {
			FileWriter fw = new FileWriter(file);
			fw.write(content);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			return false;
		}

		return true;
	}

	public static String loadString(String fileName, long time) {
		File file = getCacheFile(fileName);
		if (FileCacheManager.checkCacheAvailable(time, file, false)) {
			return readFile(file);
		}
		return null;
	}

	public static String loadString(String fileName) {
		File file = getCacheFile(fileName);
		return readFile(file);
	}
	
	public static boolean checkCache(String fileName, long expiredTimeMs) {
		String path = getCachePath(fileName);
		return checkCacheAvailable(expiredTimeMs, path, false);
	}

	public static boolean freshCache(String fileName) {
		File file = getCacheFile(fileName);
		return file.setLastModified(System.currentTimeMillis());
	}

	public static boolean checkCache(long expiredTimeMs, String path) {
		return checkCacheAvailable(expiredTimeMs, path, true);
	}

	public static boolean checkCacheAvailable(long expiredTimeMs, String path, boolean needDel) {
		File file = new File(path);
		return checkCacheAvailable(expiredTimeMs, file, needDel);
	}

	public static boolean checkCacheAvailable(long expiredTimeMs, File file, boolean needDel) {
		if (!file.exists()) {
			return false;
		}
		if (!expired(file, expiredTimeMs)) {
			return true;
		}

		if (needDel) {
			file.delete();
		}

		return false;
	}

	private static File getCacheFile(String fileName) {
		if (FileSystem.isExternalStorageAvailable())
			return FileSystem.getFile(FileDirectoryContext.FILE_DIRECTORY_TYPE_SDCARD_CACHE, fileName);
		else
			return new File(AppContext.context.getCacheDir().getAbsolutePath()+File.separatorChar + fileName);
	}

	private static String getCachePath(String fileName) {
		if (FileSystem.isExternalStorageAvailable())
			return FileSystem.getFilePath(FileDirectoryContext.FILE_DIRECTORY_TYPE_SDCARD_CACHE, fileName);
		else
			return AppContext.context.getCacheDir().getAbsolutePath()+File.separatorChar + fileName;
	}

	private static boolean expired(File file, long expiredTimeMs) {
		long current = System.currentTimeMillis() / 1000 * 1000
				- file.lastModified();
		return (current < 0 || current >= expiredTimeMs);
	}
}
