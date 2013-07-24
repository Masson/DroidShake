package com.imasson.droidshake.util;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.util.Log;

/**
 * 用于进行程序包相关操作的工具类
 * 
 * @version 1.0 包含程序包检测、查询的几个基本方法
 */
public final class PackageUtils {
	private static final String TAG = "PackageUtils";
	
	private PackageUtils() {}
	
	/**
     * 获取系统中可以接收特定action的程序列表
     * @param context 上下文对象
     * @param actionString action字符串
     * @param type 指定的类型，可以为空
     * @return 可以接收该action的程序列表
     */
    public static List<ResolveInfo> getInstalledActivityInfos(
            Context context, String actionString, String type) {
    	if (context == null) {
			Log.w(TAG, "Argument 'context' is null at getInstalledActivityInfos()");
			return Collections.emptyList();
		}
    	
        Intent intent = new Intent(actionString);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        if (type != null) {
        	intent.setType(type);
        }
        
        List<ResolveInfo> infos = context.getPackageManager().queryIntentActivities(
                intent, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        return infos;
    }

	/**
	 * 检测是否存在某个包
	 * @param context 上下文对象
	 * @param packageName 要检测的包名
	 * @return 是否存在某个包
	 */
	public static boolean existPackage(Context context, String packageName) {
		if (context == null) {
			Log.w(TAG, "Argument 'context' is null at existPackage()");
			return false;
		}
		if (packageName == null) {
			Log.w(TAG, "Argument 'packageName' is null at existPackage()");
			return false;
		}
		
		boolean hasFound = false;
		try {
			context.getPackageManager().getPackageInfo(packageName, 0);
			hasFound = true;
		} catch (NameNotFoundException e) {
		}
		return hasFound;
	}

	/**
	 * 创建目标包名对应的上下文对象
	 * @param context 当前的上下文对象
	 * @param packageName 目标包名，不能为空
	 * @return 目标包名对应的上下文对象，如果找不到则返回null
	 */
	public static Context createPackageContext(Context context, String packageName) {
		if (context == null) {
			Log.w(TAG, "Argument 'context' is null at createPackageContext()");
			return null;
		}
		if (packageName == null) {
			Log.w(TAG, "Argument 'packageName' is null at createPackageContext()");
			return null;
		}
		
		Context targetContext = null;
		try {
			targetContext = context.createPackageContext(packageName, 
					Context.CONTEXT_IGNORE_SECURITY);
		} catch (NameNotFoundException e) {
		}
		return targetContext;
	}

}
