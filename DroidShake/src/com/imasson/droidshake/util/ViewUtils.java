package com.imasson.droidshake.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.Window;

/**
 * <p>提供与视图相关的操作的工具类</p>
 * <p>目前支持的特性如下：</p>
 * <ul>
 * <li>进行dp、px等尺寸单位的数值转换</li>
 * <li>对视图或窗口进行截图</li>
 * </ul>
 * @version 1.0 包含尺寸单位转换以及对视图进行截图的方法
 */
public class ViewUtils {
	private static final String TAG = "ViewUtils";
	
	/**
	 * 根据当前设备的像素密度，把dip单位的值转换为像素值。
	 * 该值用于表示偏移或距离，换算时舍去小数点后的值
	 * @param dip 需要转换的dip单位的值
	 * @param context 上下文对象，不能为空
	 * @return 对应的像素值，换算时舍去小数点后的值
	 */
	public static int dipToPixelOffset(float dip, Context context) {
		return dimensionToPixelOffset(TypedValue.COMPLEX_UNIT_DIP, dip, context);
	}
	
	/**
	 * 根据当前设备的像素密度，把dip单位的值转换为像素值。
	 * 该值用于表示宽高或大小，换算时对小数点后的值四舍五入
	 * @param dip 需要转换的dip单位的值
	 * @param context 上下文对象，不能为空
	 * @return 对应的像素值，换算时对小数点后的值四舍五入
	 */
	public static int dipToPixelSize(float dip, Context context) {
		return dimensionToPixelSize(TypedValue.COMPLEX_UNIT_DIP, dip, context);
	}
	
	/**
	 * 根据当前设备的像素密度，把像素值转换为dip值
	 * @param pixel 需要转换的像素值
	 * @param context 上下文对象，不能为空
	 * @return 对应的dip单位的值
	 */
	public static float pixelToDip(int pixel, Context context) {
		if (context == null) {
			Log.w(TAG, "Argument 'context' is null at pixelToDip(int, Context)");
			return 0f;
		}
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		
		return pixelToDimensionStrictly(TypedValue.COMPLEX_UNIT_DIP, pixel, metrics);
	}
	
	/**
	 * 根据当前设备的像素密度，把指定单位的值转换为像素值。
	 * 该值用于表示偏移或距离，换算时舍去小数点后的值
	 * @param unit 输入值的单位，参考{@link TypedValue}中 <code>COMPLEX_UNIT_</code> 开头的常量
	 * @param dimension 需要转换的该单位下的值
	 * @param context 上下文对象，不能为空
	 * @return 对应的像素值，换算时舍去小数点后的值
	 */
	public static int dimensionToPixelOffset(int unit, float dimension, Context context) {
		if (context == null) {
			Log.w(TAG, "Argument 'context' is null at dimensionToPixelOffset(int, int, Context)");
			return 0;
		}
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		
		float strictValue = dimensionToPixelStrictly(unit, dimension, metrics);
		return (int) strictValue;
	}
	
	/**
	 * 根据当前设备的像素密度，把指定单位的值转换为像素值。
	 * 该值用于表示宽高或大小，换算时对小数点后的值四舍五入
	 * @param unit 输入值的单位，参考{@link TypedValue}中 <code>COMPLEX_UNIT_</code> 开头的常量
	 * @param dimension 需要转换的该单位下的值
	 * @param context 上下文对象，不能为空
	 * @return 对应的像素值，换算时对小数点后的值四舍五入
	 */
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
	
	/**
	 * 根据当前设备的像素密度，把指定单位的值转换为另一个单位的值
	 * @param unitFrom 输入值的单位，参考{@link TypedValue}中 <code>COMPLEX_UNIT_</code> 开头的常量
	 * @param unitTo 输出值的单位，参考{@link TypedValue}中 <code>COMPLEX_UNIT_</code> 开头的常量
	 * @param value 需要转换的输入单位下的值
	 * @param context 上下文对象，不能为空
	 * @return 对应的输出单位下的值
	 */
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
	
	
	/**
	 * <p>对指定的视图进行截图，输出位图</p>
	 * <p>该方法针对已经嵌入到界面，并且已经布局完毕的视图进行截图。
	 * 若视图是单独初始化的，则需要使用{@link #makeSnapshot(View, int, int)}。</p>
	 * @param view 需要截图的视图，该试图必须已经布局完毕
	 * @return 该视图当前状态的截图
	 */
	public static Bitmap makeSnapshot(View view) {
		if (view == null) {
			Log.w(TAG, "Argument 'view' is null at makeSnapshot(View)");
			return null;
		}
		
		int width = view.getWidth();
		int height = view.getHeight();
		
		if (width <= 0 || height <= 0) {
			Log.w(TAG, "The size of the view is invalid at makeSnapshot(View)");
			return null;
		}
		
		Bitmap snapshot = null;
		try {
			snapshot = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(snapshot);
            view.draw(canvas);
		} catch (OutOfMemoryError er) {
			Log.w(TAG, "OutOfMemoryError at makeSnapshot(View)", er);
		} catch (Exception e) {
			Log.w(TAG, "Exception at makeSnapshot(View)", e);
		}
		
		return snapshot;
	}
	
	/**
	 * <p>对指定的视图进行截图，输出位图</p>
	 * <p>对于单独初始化，且没有加入到其他界面的视图使用此方法。需要指定其宽度和高度，
	 *本方法会自动对视图进行布局，再进行截图。</p>
	 * <p>但对于已经嵌入到界面中的视图，不推荐使用本方法，调用本方法后该试图可能因为
	 *布局大小变化导致界面异常。对于此类视图请使用{@link #makeSnapshot(View)}。</p>
	 * @param view 需要截图的视图
	 * @param expectedWidht 视图的期望宽度，截图时会根据此宽度布局目标视图
	 * @param expectedHeight 视图的期望高度，截图时会根据此高度布局目标视图
	 * @return 该视图当前状态的截图
	 */
	public static Bitmap makeSnapshot(View view, int expectedWidht, int expectedHeight) {
		if (view == null) {
			Log.w(TAG, "Argument 'view' is null at makeSnapshot(View, int, int)");
			return null;
		}
		if (expectedWidht <= 0 || expectedHeight <= 0) {
			Log.w(TAG, "Arguemnt 'expectedWidht' and 'expectedHeight' must > 0");
			return null;
		}
		
		int widthSpec = MeasureSpec.makeMeasureSpec(expectedWidht, MeasureSpec.EXACTLY);
		int heightSpec = MeasureSpec.makeMeasureSpec(expectedHeight, MeasureSpec.EXACTLY);
		view.measure(widthSpec, heightSpec);
		view.layout(0, 0, expectedWidht, expectedHeight);
		
		return makeSnapshot(view);
	}
	
	/**
	 * 对指定的{@link Activity}进行截图，输出位图
	 * @param activity 需要截图的{@link Activity}，该界面必须已经布局完毕
	 * @return 该视图当前状态的截图
	 */
	public static Bitmap makeSnapshot(Activity activity) {
		if (activity == null) {
			Log.w(TAG, "Argument 'activity' is null at makeSnapshot(Activity)");
			return null;
		}
		return makeSnapshot(activity.getWindow());
	}
	
	/**
	 * 对指定的{@link Window}进行截图，输出位图
	 * @param window 需要截图的{@link Window}，该窗口必须已经布局完毕
	 * @return 该窗口当前状态的截图
	 */
	public static Bitmap makeSnapshot(Window window) {
		if (window == null) {
			Log.w(TAG, "Argument 'window' is null at makeSnapshot(Window)");
			return null;
		}
		return makeSnapshot(window.getDecorView());
	}
}
