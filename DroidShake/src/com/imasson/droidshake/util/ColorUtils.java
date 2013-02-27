package com.imasson.droidshake.util;

import android.graphics.Color;

/**
 * <p>用于进行颜色值换算的工具类</p>
 * 
 * @version 1.0 包含透明度修改和调整颜色亮度的工具方法
 */
public final class ColorUtils {
	
	/**
	 * 更改颜色值的透明度
	 * @param color 原来的颜色值
	 * @param transparent 透明度<code>[0,255]</code>，
	 * 0表示完全透明，255表示完全不透明
	 * @return 设置透明度后的颜色值
	 */
	public static int alterColorTransparnet(int color, int transparent) {
		int transValue = (transparent & 0x000000ff) << 24;
		int ret = color & 0x00ffffff | transValue;
		return ret;
	}
	
	/**
	 * 更改颜色值的透明度
	 * @param color 原来的颜色值
	 * @param ratio 透明度比例<code>[0.0,1.0]</code>，
	 * 0表示完全透明，1表示完全不透明
	 * @return 设置透明度后的颜色值
	 */
	public static int alterColorTransparnet(int color, float ratio) {
		int transValue = (int)(0x000000ff * ratio) << 24;
		int ret = color & 0x00ffffff | transValue;
		return ret;
	}
	
	/**
	 * 增加颜色值的亮度，亮度为负值即为减少亮度
	 * @param color 原来的颜色值
	 * @param delta 要增加的亮度值[-255,255]
	 * @return 修改后的颜色值
	 */
	public static int addColorBrightness(int color, int delta) {
		int a = Color.alpha(color);
		int r = Color.red(color);
		int g = Color.green(color);
		int b = Color.blue(color);
		
		if (r >= 0x000000ff - delta) r = 255;
		else if (r <= 0 - delta) r = 0;
		else r += delta;
		
		if (g >= 0x000000ff - delta) g = 255;
		else if (g <= 0 - delta) r = 0;
		else g += delta;
		
		if (b >= 0x000000ff - delta) b = 255;
		else if (b <= 0 - delta) r = 0;
		else b += delta;
		
		return Color.argb(a, r, g, b);
	}
}
