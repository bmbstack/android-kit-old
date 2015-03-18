/**
 * Copyright (c) 2013, BigBeard Team, Inc. All rights reserved. 
 */
package com.androidkit.file;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;

public class FileSystem {
	private static final String LOG_TAG = "FileSystem";
	private static HashMap<String, String> sFileDirectoryMap;
	
	/**
	 * Initial the file system, storage the file 
	 * 
	 * @return
	 */
	public static boolean init(FileDirectoryContext fileDirectoryContext) {
		boolean result = false;
		sFileDirectoryMap = new HashMap<String, String>();
		if(!isExternalStorageAvailable()) {
			return false; //initial file system fail when the external storage unavailable
		}
		fileDirectoryContext.defineVirtualFileDirectory(); //define the virtual file directory
		result = createFileDirectoryByTreewalk(fileDirectoryContext.getSDCardMainFileDirectory())  
			  && createFileDirectoryByTreewalk(fileDirectoryContext.getSDCardHiddenFileDirectory());
		return result;
	}
	
	/**
	 * Create the file directory by tree walk
	 * 
	 * @param fileDirectory
	 * @return
	 */
	private static boolean createFileDirectoryByTreewalk(FileDirectory fileDirectory) {
		boolean result = false;
		String fileAbsolutePath = null;
		FileDirectory parentFileDirectory = fileDirectory.getParent();
		if(parentFileDirectory == null) 
			fileAbsolutePath = fileDirectory.getValue(); //it is the top file directory
		else 
			fileAbsolutePath = getDirectoryPath(parentFileDirectory.getType())+File.separator+fileDirectory.getValue();
		File file = new File(fileAbsolutePath);
		if(!file.exists()) {
			result = file.mkdirs(); //create the directories,need permission
			if(result) 
				Log.d(LOG_TAG, "make directory success["+fileAbsolutePath+"]");
			else 
				return false;
		}else {
			Log.d(LOG_TAG, "the directory already exist["+fileAbsolutePath+"]");
			result = true;
		}
		sFileDirectoryMap.put(fileDirectory.getType(), fileAbsolutePath); //将创建的目录放入HashMap中
		Collection<FileDirectory> children = fileDirectory.getChildren();
		if(children != null) 
			for(FileDirectory child : children) {
				if(!createFileDirectoryByTreewalk(child)) {
					return false;
				}
			}
		return result;
	}
	
	/**
	 * Get the file that defined before
	 * 
	 * @param fileDirectoryType
	 * @param fileName
	 * @return File
	 */
	public static File getFile(String fileDirectoryType, String fileName) {
		String filePath = getFilePath(fileDirectoryType, fileName);
		if(filePath == null)
			return null;
		return new File(filePath);
	}
	
	/**
	 * Get the file defined path
	 * 
	 * @param fileDirectoryType  
	 * @param fileName
	 * @return  file path
	 */
	public static String getFilePath(String fileDirectoryType, String fileName) {
		String topFolderPath = getDirectoryPath(fileDirectoryType);
		if(topFolderPath == null) 
			return null;
		return topFolderPath+File.separator+fileName;
	}
	
	/**
	 * Get the file object with file directory type[main,hidden]
	 * 
	 * @param fileDirectoryType[main, hidden]
	 * @return File
	 */
	public static File getDirectory(String fileDirectoryType) {
		String filePath = getDirectoryPath(fileDirectoryType);
		if(filePath == null)
			return null;
		return new File(filePath);
	}
	
	/**
	 * Get the file path[main,hidden]
	 * 
	 * @param fileDirectoryType
	 * @return file absolute path
	 */
	public static String getDirectoryPath(String fileDirectoryType) {
		if(sFileDirectoryMap == null)
			return null;
		return sFileDirectoryMap.get(fileDirectoryType);
	}
	
	/**
	 * Check the network is available
	 * 
	 * @return  
	 */
	public static boolean isExternalStorageAvailable() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}
}
