/**
 * Copyright (c) 2013, wangmingjob.com, Inc. All rights reserved.
 */
package cn.bmbstack.androidkit.context;

import cn.bmbstack.androidkit.file.FileDirectory;
import cn.bmbstack.androidkit.file.FileDirectoryContext;

public class DefaultFileDirectoryContext extends FileDirectoryContext {
	public static String FILE_DIRECTORY_TYPE_CONFIG = "CONFIG";
	public static String FILE_DIRECTORY_VALUE_CONFIG = "config";
	public static String FILE_DIRECTORY_TYPE_PICTURE = "PICTURE";
	public static String FILE_DIRECTORY_VALUE_PICTURE = "picture";

	public DefaultFileDirectoryContext(String appName) {
		super(appName);
	}

	@Override
	protected void defineVirtualFileDirectory() {
		FileDirectory sdcardMain = getSDCardMainFileDirectory();
		FileDirectory sdcardHidden = getSDCardHiddenFileDirectory();
		addChildFileDirectory(sdcardMain, FILE_DIRECTORY_TYPE_CONFIG, FILE_DIRECTORY_VALUE_CONFIG); //先初始化一个file目录到非隐藏目录
		addChildFileDirectory(sdcardMain, FILE_DIRECTORY_TYPE_PICTURE, FILE_DIRECTORY_VALUE_PICTURE); //先初始化一个file目录到非隐藏目录
	}

}
