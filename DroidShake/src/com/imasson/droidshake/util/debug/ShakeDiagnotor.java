package com.imasson.droidshake.util.debug;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.imasson.droidshake.util.FileUtils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.os.Process;
import android.util.Log;

/**
 * <p>包含一些当前程序包和系统环境的一些诊断工具的类</p>
 * 
 * @see android.os.Debug
 */
public class ShakeDiagnotor {
	private static final String TAG = "ShakeDiagnotor";
	
	private Context mContext = null;
	private ActivityManager mActivityManager = null;
	
	private int[] mPid = null;
	private long mSystemTotalMemory = 0L;
	private ActivityManager.MemoryInfo mCachedMemoryInfo = null;
	private Pattern mCpuRatePattern = null;
	
	public ShakeDiagnotor(Context context) {
		mContext = context;
		mActivityManager = (ActivityManager) context.getApplicationContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
	}
	
	private void initPid() {
	    try {
	    	if (mPid == null) {
				int[] arrayOfInt = new int[1];
				arrayOfInt[0] = Process.myPid();
				mPid = arrayOfInt;
	    	}
	    } catch (Exception e) {
	    	Log.w(TAG, "Exception on initGetMemoryInfoMethod()", e);
	    }
	}
	
	/**
	 * 获取当前程序包的内存占用情况
	 * @return 包含内存情况数据的对象，如果查询失败或系统不支持，则返回null
	 */
	public Debug.MemoryInfo getOwnMemoryInfo() {
		if (mPid == null) {
			initPid();
		}
		if (mPid == null) {
			return null;
		}
		
		Debug.MemoryInfo[] infos = mActivityManager.getProcessMemoryInfo(mPid);
		return infos[0];
	}
	
	/**
	 * 获取当前系统可用的内存大小
	 * @return 可用的内存大小，单位为byte
	 */
	public long getSystemAvailableMemorySize() {
		if (mCachedMemoryInfo == null) {
			mCachedMemoryInfo = new ActivityManager.MemoryInfo();
		}
		mActivityManager.getMemoryInfo(mCachedMemoryInfo);
		return mCachedMemoryInfo.availMem;
	}
	
	/**
	 * 获取当前系统总内存大小
	 * @return 总内存大小，单位为byte
	 */
	public long getSystemTotalMemorySize() {
		if (mSystemTotalMemory <= 0L) {
			String path = "/proc/meminfo";
			String[] arrayOfString;

			FileReader fileReader = null;
			BufferedReader bufferedReader = null;
			try {
				fileReader = new FileReader(path);
				bufferedReader = new BufferedReader(fileReader, FileUtils.DEFAULT_BUFFER_SIZE);
				String result = bufferedReader.readLine(); // meminfo的第一行为系统总内存

				arrayOfString = result.split("\\s+");

				mSystemTotalMemory = Integer.parseInt(arrayOfString[1]) * 1024; // meminfo里面内存值单位为kb
				
			} catch (IOException e) {
				Log.w(TAG, "IOException at getSystemTotalMemorySize()", e);
			} catch (NumberFormatException e) {
				Log.w(TAG, "NumberFormatException at getSystemTotalMemorySize()", e);
			} finally {
				try {
					if (bufferedReader != null) bufferedReader.close();
					if (fileReader != null) fileReader.close();
				} catch (IOException e) {}
			}
		}
		
		return mSystemTotalMemory;
	}
	
	/**
	 * 获取当前系统剩余内存大小
	 * @return 剩余内存大小，单位为byte
	 */
	public long getSystemFreeMemorySize() {
		return getSystemTotalMemorySize() - getSystemAvailableMemorySize();
	}
	
	
	/**
	 * 获取当前程序包的CPU占用情况
	 * @return CPU占用的百分比，查询失败将返回-1
	 */
	public int getOwnCpuRate() {
		java.lang.Process p = null;
		try {
			p = Runtime.getRuntime().exec("top -n 1");
		} catch (IOException e) {
			Log.w(TAG, "Error while exec 'top -n 1' at getOwnCpuRate()");
		}

		int cpuRate = -1;
		InputStreamReader isr = null;
    	BufferedReader br = null;
		try {
			isr = new InputStreamReader(p.getInputStream());
			br = new BufferedReader(isr);
			
			String result = null;
			String packageName = mContext.getPackageName();
			while ((result = br.readLine()) != null) {
				result = result.trim();
				if (result.endsWith(packageName)) {
					cpuRate = fetchCpuRateFromProcessLine(result);
					break;
				}
			}
		} catch (IOException e) {
			Log.w(TAG, "IOException at getOwnCpuRate()", e);
		} finally {
			try {
				if (isr != null) isr.close();
				if (br != null) br.close();
			} catch (IOException e) {}
		}
		
		return cpuRate;
	}

	private int fetchCpuRateFromProcessLine(String result) {
		if (mCpuRatePattern == null) {
			mCpuRatePattern = Pattern.compile("\\b\\d(?=%)");
		}
		
		int cpuRate = -1;
		Matcher matcher = mCpuRatePattern.matcher(result);
		if (matcher != null && matcher.find()) {
			String cpuRateString = result.substring(matcher.start(), matcher.end());
			try {
				cpuRate = Integer.parseInt(cpuRateString);
			} catch (NumberFormatException e) {
				Log.w(TAG, "NumberFormatException at fetchCpuRateFromProcessLine()", e);
			}
			return cpuRate;
		}
		
		return cpuRate;
	}
}
