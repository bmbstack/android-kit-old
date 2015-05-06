package com.androidkit.util;

import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.androidkit.context.AppContext;

public class DensityUtils {
	
	
	/**
	 * Complex unit is sp
	 * 
	 * @return
	 */
	public static float convertToSpUnit(int dimenId) {
		DisplayMetrics metrics = AppContext.context.getResources().getDisplayMetrics();
		float fontSize = AppContext.context.getResources().getDimension(dimenId);
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, fontSize, metrics);
	}

	/**
	 * change dip to px
	 * 
	 * @param dpValue
	 * @return
	 */
	public static int dip2px(float dpValue) {
		final float density = AppContext.context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * density + 0.5f);
	}

	/**
	 * change px to dip
	 * 
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(float pxValue) {
		final float density = AppContext.context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / density + 0.5f);
	}
	
	 /**
	  * 将px值转换为sp值，保证文字大小不变
	  * 
	  * @param pxValue
	  * @return
	  */
	 public static int px2sp(float pxValue) {
		 final float fontScale = AppContext.context.getResources().getDisplayMetrics().scaledDensity;
	  return (int) (pxValue / fontScale + 0.5f);
	 }


}
