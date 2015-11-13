/**
 * Copyright (c) 2013, BigBeard Team, Inc. All rights reserved. 
 */
package com.github.bmbstack.androidkit.file;

import android.os.Environment;

import java.io.File;

public abstract class FileDirectoryContext {
	public static final String FILE_DIRECTORY_TYPE_SDCARD_MAIN = "MAIN";
	public static final String FILE_DIRECTORY_TYPE_SDCARD_HIDDEN = "HIDDEN";
	public static final String FILE_DIRECTORY_TYPE_SDCARD_LOG = "LOG";
	public static final String FILE_DIRECTORY_VALUE_SDCARD_LOG = "log";
	public static final String FILE_DIRECTORY_TYPE_SDCARD_CACHE = "CACHE";
	public static final String FILE_DIRECTORY_VALUE_SDCARD_CACHE = "cache";
	
	private FileDirectory mSDCardMainFileDirectory; //afford a default file directory with "appName"
	private FileDirectory mSDCardHiddenFileDirectory; //afford a default hidden file directory with ".appName"
	private FileDirectory mSDCardLogFileDirectory; //afford a default file directory with ".appName/log"
	private FileDirectory mSDCardCacheFileDirectory; //afford a default file directory with ".appName/cache"
	
	/**
	 * Make the default virtual file directory with "appName" and ".appName", and make a 
	 * log directory for persistence the log information
	 * @param appName
	 */
	public FileDirectoryContext(String appName) {
		//Define the default file directory main and hidden
		mSDCardMainFileDirectory = new FileDirectory();
		mSDCardMainFileDirectory.setType(FILE_DIRECTORY_TYPE_SDCARD_MAIN); 
		mSDCardMainFileDirectory.setValue(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+appName);
		
		mSDCardHiddenFileDirectory = new FileDirectory();
		mSDCardHiddenFileDirectory.setType(FILE_DIRECTORY_TYPE_SDCARD_HIDDEN);
		mSDCardHiddenFileDirectory.setValue(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"."+appName);
		
		//Add a child file directory to main type file directory
		mSDCardLogFileDirectory = addChildFileDirectory(mSDCardMainFileDirectory, FILE_DIRECTORY_TYPE_SDCARD_LOG, FILE_DIRECTORY_VALUE_SDCARD_LOG);
		mSDCardCacheFileDirectory = addChildFileDirectory(mSDCardMainFileDirectory, FILE_DIRECTORY_TYPE_SDCARD_CACHE, FILE_DIRECTORY_VALUE_SDCARD_CACHE);
	}
	
	/**
	 * On the basis of main and hidden file directory, to define the virtual file directory
	 */
	protected abstract void defineVirtualFileDirectory();
	
	/**
	 * Add the child file directory
	 * 
	 * @param parent use getSDCardMainFileDirectory，getSDCardHiddenFileDirectory get the parent file directory
	 * @param childType
	 * @param childValue
	 * @return child folder directory.
	 */
	protected FileDirectory addChildFileDirectory(FileDirectory parent, String childFileDirectoryType, String childFileDirectoryValue) {
		FileDirectory child = new FileDirectory();
		child.setType(childFileDirectoryType);
		child.setValue(childFileDirectoryValue);
		child.setParent(parent);
		parent.addChild(child);
		return child;
	}
	
	public FileDirectory getSDCardMainFileDirectory() {
		return mSDCardMainFileDirectory;
	}
	
	public FileDirectory getSDCardHiddenFileDirectory() {
		return mSDCardHiddenFileDirectory;
	}
	
	public FileDirectory getSDCardLogFileDirectory() {
		return mSDCardLogFileDirectory;
	}
	
	public FileDirectory getSDCardCacheFileDirectory() {
		return mSDCardCacheFileDirectory;
	}
}
