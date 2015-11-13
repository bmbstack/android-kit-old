/**
 * Copyright (c) 2013, BigBeard Team, Inc. All rights reserved. 
 */
package cn.bmbstack.androidkit.log;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	public static final int LOG_LEVEL_VERBOSE = 5;
	public static final int LOG_LEVEL_DEBUG = 4;
	public static final int LOG_LEVEL_INFO = 3;
	public static final int LOG_LEVEL_WARN = 2;
	public static final int LOG_LEVEL_ERROR = 1;
	public static final int LOG_LEVEL_NONE = 0;
	public static final int LOG_ERROR = -1;

	private int mLogLevel = LOG_LEVEL_VERBOSE; // 默认打印所有
	private static String sLogFilePath;

	private static Logger instance = null;
	
	/**
	 * Make the constructor to private
	 */
	private Logger() {
	}
	
	/**
	 * Use the single pattern to create the Logger object
	 * 
	 * @return
	 */
	public static Logger getInstance() {
		if(instance == null)
			instance = new Logger();
		return instance;
	}
	
	/**
	 * Set the log level
	 * 
	 * @param logLevel
	 */
	public void setLogLevel(int logLevel) {
		if(logLevel > 5 || logLevel < 0) 
			throw new IllegalArgumentException("invalid parameter logLevel, please use Logger's static level constant.");
		mLogLevel = logLevel;
	}
	
	/**
	 * Whether need persistence the log information to SDCard
	 * 
	 * @param logFilePath
	 */
	public static void persistenceLog(String logFilePath) {
		sLogFilePath = logFilePath;
	}
	
	/**
	 * Get Log information
	 * 
	 * @return
	 */
	private String getLogTag() {
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		if (elements == null) 
			return null;
		StackTraceElement caller = elements[5];
		String tag = "%s#%s(Line:%d)";
		String callerClazzName = caller.getClassName();
		callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
		tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
		return tag;
	}
	
	private final Object mLogLock = new Object();
	
	/**
	 * Persistence the log information to the SDCard
	 * 
	 * @param logLevelStr
	 * @param logTag
	 * @param msg
	 * @return 0: success, -1: fail
	 */
	private int persistenceLog(String logLevelStr, String logTag, String msg) {
		if(TextUtils.isEmpty(sLogFilePath)) {
			return -1;
		}
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String time = dateFormat.format(new Date());
    	StringBuilder sb = new StringBuilder();
    	sb.append(time);
    	sb.append("\t");
    	sb.append(logLevelStr);
    	sb.append("\t");
    	sb.append(logTag);
    	sb.append("\t");
    	sb.append(msg);
    	sb.append("\n");
    	FileWriter writer = null;
    	
    	synchronized (mLogLock) {    		
			try {
				File file = new File(sLogFilePath);
				if (!file.exists()) {
					file.createNewFile();
				}
				writer = new  FileWriter(file, true);
				writer.write(sb.toString());
			} catch (FileNotFoundException e) {
				return -1;
			} catch (IOException e) {
				return -1;
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e) {
						return -1;
					}
				}				
			}    		
		}
    	return 0;	
	}
	
	/**
	 * Get the Stack trace information
	 * 
	 * @param tr
	 * @return
	 */
	public String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        return sw.toString();
    }
	
	/**
	 * 
	 * @param msg
	 * @return
	 */
	public int v(String msg) {
		String logTag = getLogTag();
		persistenceLog("verbose", logTag, msg);
		if (mLogLevel >= LOG_LEVEL_VERBOSE)
			return Log.v(logTag, msg);
		else
			return LOG_ERROR;
	}

	/**
	 * 
	 * @param msg
	 * @return
	 */
	public int d(String msg) {
		String logTag = getLogTag();
		persistenceLog("debug", logTag, msg);
		if (mLogLevel >= LOG_LEVEL_DEBUG)
			return Log.d(logTag, msg);
		else
			return LOG_ERROR;
	}

	/**
	 * 
	 * @param msg
	 * @return
	 */
	public int i(String msg) {
		String logTag = getLogTag();
		persistenceLog("info", logTag, msg);
		if (mLogLevel >= LOG_LEVEL_INFO)
			return Log.i(logTag, msg);
		else
			return LOG_ERROR;
	}

	/**
	 * 
	 * @param msg
	 * @return
	 */
	public int w(String msg) {
		String logTag = getLogTag();
		persistenceLog("warn", logTag, msg);
		if (mLogLevel >= LOG_LEVEL_WARN)
			return Log.w(logTag, msg);
		else
			return LOG_ERROR;
	}

	/**
	 * 
	 * @param msg
	 * @return
	 */
	public int e(String msg) {
		String logTag = getLogTag();
		persistenceLog("error", logTag, msg);
		if (mLogLevel >= LOG_LEVEL_ERROR)
			return Log.e(logTag, msg);
		else
			return LOG_ERROR;
	}
}
