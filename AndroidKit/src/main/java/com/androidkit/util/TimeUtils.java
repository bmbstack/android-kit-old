package com.androidkit.util;

public class TimeUtils {
	/**
	 * 一秒的毫秒数
	 */
	public static final long SECOND = 1000;
	public static final long MINUTE = SECOND * 60;
	public static final long HOUR = MINUTE * 60;
	/**
	 * 一天的毫秒数
	 */
	public static final long DAY = 1000 * 60 * 60 * 24L;
	public static final long WEEK = DAY * 7;
	public static final long MONTH = DAY * 30;
	public static final long SEASON = MONTH * 3;
	public static final long YEAR = DAY * 365;

	/**
	 * 生成要显示的时间字符串
	 * 
	 * @param milliSecs
	 * @return
	 */
	public static String toString(long milliSecs) {
		StringBuffer sb = null;
		sb = new StringBuffer();
		long second = milliSecs / 1000;
		long m = second / 60;
		sb.append(m < 10 ? "0" : "").append(m);
		sb.append(":");
		long s = second % 60;
		sb.append(s < 10 ? "0" : "").append(s);
		return sb.toString();
	}
}
