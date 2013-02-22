package com.imasson.droidshake.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

/**
 * <p>提供与视图相关的操作的工具类</p>
 * <p>目前支持的特性如下：</p>
 * <ul>
 * <li>进行dp、px等单位的数值转换</li>
 * <li>对视图或窗口进行截图</li>
 * </ul>
 */
public class ViewUtils {
	private static final String TAG = "ViewUtils";
	
	public static int dipToPixelOffset(float dip, Context context) {
		return dimensionToPixelOffset(TypedValue.COMPLEX_UNIT_DIP, dip, context);
	}
	
	public static int dipToPixelSize(float dip, Context context) {
		return dimensionToPixelSize(TypedValue.COMPLEX_UNIT_DIP, dip, context);
	}
	
	public static float pixelToDip(int pixel, Context context) {
		if (context == null) {
			Log.w(TAG, "Argument 'context' is null at pixelToDip(int, Context)");
			return 0f;
		}
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		
		return pixelToDimensionStrictly(TypedValue.COMPLEX_UNIT_DIP, pixel, metrics);
	}
	
	public static int dimensionToPixelOffset(int unit, float dimension, Context context) {
		if (context == null) {
			Log.w(TAG, "Argument 'context' is null at dimensionToPixelOffset(int, int, Context)");
			return 0;
		}
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		
		float strictValue = dimensionToPixelStrictly(unit, dimension, metrics);
		return (int) strictValue;
	}
	
	public static int dimensionToPixelSize(int unit, float dimension, Context context) {
		if (context == null) {
			Log.w(TAG, "Argument 'context' is null at dimensionToPixelOffset(int, int, Context)");
			return 0;
		}
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		
		float strictValue = dimensionToPixelStrictly(unit, dimension, metrics);
		final int res = (int)(strictValue + 0.5f);
        if (res != 0) return res;
        if (dimension == 0) return 0;
        if (dimension > 0) return 1;
        return -1;
	}
	
	public static float convertDimension(int unitFrom, int unitTo, float value, Context context) {
		if (context == null) {
			Log.w(TAG, "Argument 'context' is null at convertDimension(int, int, float, Context)");
			return 0;
		}
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		
		float pixelValue = dimensionToPixelStrictly(unitFrom, value, metrics);
		return pixelToDimensionStrictly(unitTo, pixelValue, metrics);
	}
	
	private static float dimensionToPixelStrictly(int unit, float value, DisplayMetrics metrics) {
		switch (unit) {
		case TypedValue.COMPLEX_UNIT_PX:
			return value;
		case TypedValue.COMPLEX_UNIT_DIP:
			return value * metrics.density;
		case TypedValue.COMPLEX_UNIT_SP:
			return value * metrics.scaledDensity;
		case TypedValue.COMPLEX_UNIT_PT:
			return value * metrics.xdpi * (1.0f / 72);
		case TypedValue.COMPLEX_UNIT_IN:
			return value * metrics.xdpi;
		case TypedValue.COMPLEX_UNIT_MM:
			return value * metrics.xdpi * (1.0f / 25.4f);
		}
		return 0;
	}
	
	private static float pixelToDimensionStrictly(int unit, float value, DisplayMetrics metrics) {
		switch (unit) {
		case TypedValue.COMPLEX_UNIT_PX:
			return value;
		case TypedValue.COMPLEX_UNIT_DIP:
			return value / metrics.density;
		case TypedValue.COMPLEX_UNIT_SP:
			return value / metrics.scaledDensity;
		case TypedValue.COMPLEX_UNIT_PT:
			return value / (metrics.xdpi * (1.0f / 72));
		case TypedValue.COMPLEX_UNIT_IN:
			return value / metrics.xdpi;
		case TypedValue.COMPLEX_UNIT_MM:
			return value / (metrics.xdpi * (1.0f / 25.4f));
		}
		return 0;
	}
}
