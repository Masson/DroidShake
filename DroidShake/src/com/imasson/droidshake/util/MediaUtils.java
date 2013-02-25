package com.imasson.droidshake.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

/**
 * 与媒体相关的工具类
 */
public class MediaUtils {
	private static final String TAG = "MediaUtils";
	
    /**
     * 调用媒体扫描服务扫描整个外部存储器</br>
     * 建议采用{@link #requestScanMediaFile(Context, String)}来扫描指定文件以提高效率
     * @param context 上下文对象
     */
    public static void requestScanMediaFile(Context context) {
		if (context == null) {
			Log.w(TAG, "Argument 'context' is null at requestScanMediaFile(Context)");
			return;
		}
        
        context.sendBroadcast(new Intent(
                Intent.ACTION_MEDIA_MOUNTED, 
                Uri.parse("file://" + Environment.getExternalStorageDirectory())));
    }
    
    /**
     * 调用媒体扫描服务扫描指定的图像文件
     * @param context 上下文对象
     * @param filePath 指定的图像文件的完全路径，不需要包含"<code>file://</code>"的前缀
     */
    public static void requestScanMediaFile(Context context, String filePath) {
		if (context == null) {
			Log.w(TAG, "Argument 'context' is null "
					+ "at requestScanMediaFile(Context, String)");
			return;
		}
		if (filePath == null || filePath.length() == 0) {
			Log.w(TAG, "Argument 'filePath' is null or empty "
					+ "at requestScanMediaFile(Context, String)");
			return;
		}
        
        Uri uri = Uri.parse("file://" + filePath);
		if (uri == null) {
			Log.w(TAG, "Error on parsing file path to URI "
					+ "at requestScanMediaFile(Context, String), filePath: " + filePath);
			return;
		}
        
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
    } 
}
