package com.imasson.droidshake.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * <p>用于进行网络相关操作的工具类</p>
 *
 * @version 1.0 包含基本的判断网络状态的方法
 */
public final class NetworkUtils {
	private static final String TAG = "NetworkUtils";
	
	/**
	 * <p>判断当前设备的网络是否可用</p>
	 * <p>网络可用并不表示是当前网络已经连接上，而是表示至少有一个网络连接是可用的
	 *（没有全部禁止或启用飞行模式等）。
	 * 要判断网络是否已经连接上，请使用{@link #isNetworkConnected(Context)}。</p>
	 * @param context 上下文对象，不能为空
	 * @return 当前设备的网络是否可用
	 * @see #isNetworkConnected(Context)
	 */
	public static boolean isNetworkAvaliable(Context context) {
		if (context == null) {
			Log.w(TAG, "Argument 'context' is null at isNetworkAvaliable(Context)");
			return false;
		}
		
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) {
			Log.w(TAG, "Can't not fetch ConnectivityManager at isNetworkAvaliable(Context)");
			return false;
		}
		
		boolean isAvaliable = false;
		NetworkInfo[] infos = connectivityManager.getAllNetworkInfo();
		if (infos != null) {
			for (int i = infos.length - 1; i >= 0; i--) {
				NetworkInfo info = infos[i];
				if (info != null && info.isAvailable()) {
					isAvaliable = true;
					break;
				}
			}
		}

		return isAvaliable;
	}
	
	/**
	 * <p>判断当前设备是否已经连接上网络</p>
	 * @param context 上下文对象，不能为空
	 * @return 当前设备是否已经连接上网络
	 * @see #isNetworkAvaliable(Context)
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context == null) {
			Log.w(TAG, "Argument 'context' is null at isNetworkConnected(Context)");
			return false;
		}
		
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) {
			Log.w(TAG, "Can't not fetch ConnectivityManager at isNetworkConnected(Context)");
			return false;
		}
		
		boolean isConnected = false;
		NetworkInfo[] infos = connectivityManager.getAllNetworkInfo();
		if (infos != null) {
			for (int i = infos.length - 1; i >= 0; i--) {
				NetworkInfo info = infos[i];
				if (info != null && info.isConnected()) {
					isConnected = true;
					break;
				}
			}
		}

		return isConnected;
	}
	
	
	private NetworkUtils() { }
}
