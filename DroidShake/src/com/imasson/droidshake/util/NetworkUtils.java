package com.imasson.droidshake.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * <p>用于进行网络相关操作的工具类</p>
 *
 * @version 1.0 包含基本的判断网络状态的方法
 * @version 2.0 更新判断是否已连接网络的方法，并增加判断网络是否为高速网络的方法
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
	 * @see #isConnected(Context)
	 */
	public static boolean isAvaliable(Context context) {
		if (context == null) {
			Log.w(TAG, "Argument 'context' is null at isAvaliable(Context)");
			return false;
		}
		
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) {
			Log.w(TAG, "Can't not fetch ConnectivityManager at isAvaliable(Context)");
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
	
	
    private static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
        		.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
			Log.w(TAG, "Can't not fetch ConnectivityManager at getNetworkInfo(Context)");
			return null;
		}
        return connectivityManager.getActiveNetworkInfo();
    }

    /**
	 * <p>判断当前设备是否已经连接上网络</p>
	 * @param context 上下文对象，不能为空
	 * @return 当前设备是否已经连接上网络
	 * @see #isAvaliable(Context)
	 * @see #isConnectedMobile(Context)
	 * @see #isConnectedFast(Context)
	 */
    public static boolean isConnected(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected());
    }

    /**
   	 * <p>判断当前设备是否正在使用WIFI网络</p>
   	 * @param context 上下文对象，不能为空
   	 * @return 当前设备是否正在使用WIFI网络
   	 * @see #isConnectedMobile(Context)
   	 * @see #isConnectedFast(Context)
   	 */
    public static boolean isConnectedWifi(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected() && 
        		info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    /**
   	 * <p>判断当前设备是否正在使用移动网络（包括2G~4G等所有移动网络）</p>
   	 * @param context 上下文对象，不能为空
   	 * @return 当前设备是否正在使用移动网络
   	 * @see #isConnectedWifi(Context)
   	 * @see #isConnectedFast(Context)
   	 */
    public static boolean isConnectedMobile(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected() && 
        		info.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    /**
   	 * <p>判断当前设备是否正在使用高速的移动网络（包括3G~4G等移动网络）</p>
   	 * @param context 上下文对象，不能为空
   	 * @return 当前设备是否正在使用高速的移动网络
   	 * @see #isConnectedWifi(Context)
   	 * @see #isConnectedFast(Context)
   	 */
    public static boolean isConnectedFast(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected() && 
        		isConnectionFast(info.getType(), info.getSubtype()));
    }

    private static boolean isConnectionFast(int type, int subType) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                return false; // ~25 kbps 
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true; // ~ 400-7000 kbps
            /*
             * 对于 API Level >8 的网络类型，使用 TelephonyManagerCompat 类中的常量
             */
            case TelephonyManagerCompat.NETWORK_TYPE_EHRPD:
                return true; // ~ 1-2 Mbps
            case TelephonyManagerCompat.NETWORK_TYPE_EVDO_B:
                return true; // ~ 5 Mbps
            case TelephonyManagerCompat.NETWORK_TYPE_HSPAP:
                return true; // ~ 10-20 Mbps
            case TelephonyManagerCompat.NETWORK_TYPE_LTE:
                return true; // ~ 10+ Mbps
            // Unknown
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            default:
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * {@link TelephonyManager}的兼容类，包含多个 API Level >8 的常量
     */
	public static final class TelephonyManagerCompat {
		/**
		 * Current network is EVDO revision
		 */
		public static final int NETWORK_TYPE_EVDO_B		= 12; // API level 9
		
		/**
		 * Current network is LTE
		 */
		public static final int NETWORK_TYPE_LTE		= 13; // API level 11
		
		/**
		 * Current network is eHRPD
		 */
		public static final int NETWORK_TYPE_EHRPD		= 14; // API level 11 
		
		/**
		 * Current network is HSPA+
		 */
		public static final int NETWORK_TYPE_HSPAP		= 15; // API level 13
	}
	
	private NetworkUtils() { }
}
