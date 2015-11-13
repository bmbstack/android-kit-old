/**
 * Copyright (c) 2013, BigBeard Team, Inc. All rights reserved. 
 */
package cn.bmbstack.androidkit.log;

import java.io.File;

public class LogSystem {
	private static Logger sLogger = null;
	
	/**
	 * You must initial the LogSystem by Calling this method
	 * 
	 * @param logLevel
	 */
	public static void init(int logLevel) {
		if(sLogger == null)
			sLogger = Logger.getInstance();
		sLogger.setLogLevel(logLevel);
	}

	/**
	 *
	 * @param logDirectory
	 * @param logFileName
	 */
	public static void persistenceLog(String logDirectory, String logFileName) {
		sLogger.persistenceLog(logDirectory+File.separator+logFileName);
	}

	/**
	 * Get the stack track information
	 * 
	 * @param tr
	 * @return 
	 */
	public static String getStackTraceString(Throwable tr) {
		if(sLogger == null)
			return null;
		return sLogger.getStackTraceString(tr);
	}
	
	/**
	 * 
	 * @param msg
	 * @return
	 */
	public static int v(String msg) {
		if(sLogger == null)
			return Logger.LOG_ERROR;
		return sLogger.v(msg);
	}

	/**
	 * 
	 * @param msg
	 * @return
	 */
	public static int d(String msg) {
		if(sLogger == null)
			return Logger.LOG_ERROR;
		return sLogger.d(msg);
	}

	/**
	 * 
	 * @param msg
	 * @return
	 */
	public static int i(String msg) {
		if(sLogger == null)
			return Logger.LOG_ERROR;
		return sLogger.i(msg);
	}

	/**
	 * 
	 * @param msg
	 * @return
	 */
	public static int w(String msg) {
		if(sLogger == null)
			return Logger.LOG_ERROR;
		return sLogger.w(msg);
	}

	/**
	 *
	 * @param msg
	 * @return
	 */
	public static int e(String msg) {
		if(sLogger == null)
			return Logger.LOG_ERROR;
		return sLogger.e(msg);
	}
	
	public static int printStackTrace(Exception e) {
		if (sLogger== null) 
			return Logger.LOG_ERROR;
		else
			return sLogger.e(sLogger.getStackTraceString(e));
	}
}
