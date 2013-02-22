package com.imasson.droidshake.util;

import java.io.File;

import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

/**
 * <p>用于进行文件操作的工具类</p>
 * <p>目前支持的特性如下：</p>
 * <ul>
 * <li>文件的复制、删除、重命名以及获取文件、磁盘大小等操作</li>
 * <li>基于字节数组的文件读写操作</li>
 * <li>基于字符串的文本文件读写操作</li>
 * <li>针对Zip格式的文件压缩、解压缩操作</li>
 * </ul>
 * <p>该工具类已对各种可能出现的异常作了封装和保护</p>
 * 
 * @version 1.0 包含多种通用文件操作及Zip文件操作的方法
 */
public final class FileUtils {
	private static final String TAG = "FileUtil";
	
	/**
	 * 默认的缓冲大小
	 */
	public static final int DEFAULT_BUFFER_SIZE = 1024 * 8;

	/**
	 * 获取外部存储器（一般为SD卡）的路径
	 * @return 外部存储器的绝对路径
	 */
	public static String getExternalStoragePath() {
		return android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	/**
	 * 判断当前系统中是否存在外部存储器（一般为SD卡）
	 * @return 当前系统中是否存在外部存储器
	 */
	public static boolean hasExternalStorage() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}
	
	
	/**
     * 获取外部存储器（一般为SD卡）的剩余存储空间
     * @return 外部存储器的剩余存储空间(byte)，若没有外部存储器则返回0
     */
    public static long getAvailableExternalStorageSize() {
    	if (hasExternalStorage()) {
    		return getAvailableSpace(getExternalStoragePath());
    	} else {
    		return 0L;
    	}
    }
    
    /**
     * 获取本机的剩余存储空间
     * @return 本机的剩余存储空间(byte)
     */
    public static long getAvailableStorageSize() {
    	String rootPath = android.os.Environment.getRootDirectory().getAbsolutePath();
    	return getAvailableSpace(rootPath);
    }
	
	private static long getAvailableSpace(String dirPath) {
		if (TextUtils.isEmpty(dirPath)) {
			Log.w(TAG, "Argument 'path' is null or empty at getAvailableSpace()");
			return 0;
		}
		
		long free = 0;

		File file = new File(dirPath);
		if (file.exists()) {
			if (!file.isDirectory()) {
				return 0;
			} else {
				try {
					StatFs stat = new StatFs(dirPath);
					free = ((long) stat.getAvailableBlocks()) * stat.getBlockSize();
				} catch (Exception e) {
					Log.w(TAG, "Unexpected exception at getAvailableSpace()", e);
				}
				return free;
			}
		} else {
			return 0;
		}
	}
	
	
//	public static long getTotalExternalStorageSize() {
//		
//	}
//	
//	public static long getTotalStorageSize() {
//		
//	}
//	
//	public static long getTargetPathSize(String path) {
//		
//	}
}
