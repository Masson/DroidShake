package com.imasson.droidshake.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

/**
 * <p>用于进行文件操作的工具类</p>
 * <p>目前支持的特性如下：</p>
 * <ul>
 * <li>文件的复制、移动、删除、重命名以及获取文件、磁盘大小等操作</li>
 * <li>基于字节数组的文件读写操作</li>
 * <li>基于字符串的文本文件读写操作</li>
 * <li>针对Zip格式的文件压缩、解压缩操作</li>
 * </ul>
 * <p>该工具类已对各种可能出现的异常作了封装和保护</p>
 * 
 * @version 1.0 包含多种通用文件操作的方法
 */
public final class FileUtils {
	private static final String TAG = "FileUtil";
	
	/**
	 * 标准换行符，一般情况下相当于 <code>\n</code>
	 */
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
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
     * 获取本机存储器的剩余存储空间
     * @return 本机存储器的剩余存储空间(byte)
     */
    public static long getAvailableStorageSize() {
    	String rootPath = android.os.Environment.getRootDirectory().getAbsolutePath();
    	return getAvailableSpace(rootPath);
    }
	
	private static long getAvailableSpace(String dirPath) {
		if (TextUtils.isEmpty(dirPath)) {
			Log.w(TAG, "Argument 'dirPath' is null or empty at getAvailableSpace()");
			return 0;
		}
		
		File file = new File(dirPath);
		if (file.exists()) {
			if (!file.isDirectory()) {
				return 0;
			} else {
				long free = 0;
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
	
	
	/**
	 * 获取外部存储器（一般为SD卡）的总空间大小
	 * @return 外部存储器的总空间大小(byte)
	 */
	public static long getTotalExternalStorageSize() {
		return getTargetPathSize(getExternalStoragePath());
	}
	
	/**
	 * 获取本机存储器的总空间大小
	 * @return 本机存储器的总空间大小(byte)
	 */
	public static long getTotalStorageSize() {
		String rootPath = android.os.Environment.getRootDirectory().getAbsolutePath();
		return getTargetPathSize(rootPath);
	}
	
	/**
	 * 获取目标文件或目录的所占空间大小
	 * @param path 文件或目录的完整路径
	 * @return 目标文件或目录的所占空间大小(byte)
	 */
	public static long getTargetPathSize(String path) {
		if (TextUtils.isEmpty(path)) {
			Log.w(TAG, "Argument 'path' is null or empty at getTargetPathSize()");
			return 0;
		}
		
		File file = new File(path);
		if (!file.exists()) {
			Log.w(TAG, "The target path doesn't exist, path=" + path);
			return 0;
		}
		
		if (file.isFile()) {
			return file.length();
		} else if (file.isDirectory()) {
			long free = 0;
			try {
				StatFs stat = new StatFs(path);
				free = ((long) stat.getBlockCount()) * stat.getBlockSize();
			} catch (Exception e) {
				Log.w(TAG, "Unexpected exception at getTargetPathSize()", e);
			}
			return free;
		} else {
			return 0;
		}
	}
	
	
	/**
     * 检查文件是否存在
     * @param path 文件的路径
     * @return 文件是否存在
     */
    public static boolean isFileExist(String path) {
        if (!TextUtils.isEmpty(path)) {
        	Log.w(TAG, "Argument 'path' is null or empty at checkExist(String)");
            return false;
        }
        
        try {
            File file = new File(path);
            return file.exists();
        } catch (Exception e) {
        	Log.w(TAG, "Unexpected exception at checkExist(String), path=" + path, e);
        	return false;
        }
    }
    
    
    /**
     * 检查并建立指定的目录
     * @param dirPath 目录的路径
     * @return 是否已经建立了目录
     */
    public static boolean mkdirIfNotFound(String dirPath) {
    	if (!TextUtils.isEmpty(dirPath)) {
        	Log.w(TAG, "Argument 'dirPath' is null or empty at mkdirIfNotFound(String)");
            return false;
        }
    	
        try {
            File dir = new File(dirPath);
            if (dir.mkdirs() == false) {
				Log.i(TAG, "The folder is already exist. path=" + dirPath);
			}
            return dir.isDirectory();
        } catch (Exception e) {
            Log.w(TAG, "Unexpected exception at mkdirIfNotFound(String). path=" + dirPath, e);
            return false;
        }
    }
    
    
    /**
     * 删除单个指定路径的文件
     * @param filePath 文件的路径
     * @return 是否成功删除
     */
    public static boolean deleteFile(String filePath) {
    	if (TextUtils.isEmpty(filePath)) {
			Log.w(TAG, "Argument 'filePath' is null or empty at deleteFile(String)");
			return false;
		}
    	
    	try {
			File file = new File(filePath);
			return file.delete();
		} catch (Exception e) {
			Log.w(TAG, "Exception at deleteFile(String). path=" + filePath, e);
			return false;
		}
    }
    
    /**
     * 递归删除指定路径的目录及其下的文件和子目录
     * @param dirPath 要删除的目录的路径
     * @return 是否成功删除
     */
    public static boolean deleteDir(String dirPath) {
    	if (TextUtils.isEmpty(dirPath)) {
			Log.w(TAG, "Argument 'dirPath' is null or empty at deleteDir(String)");
			return false;
		}
    	
    	try {
			File dir = new File(dirPath);
			if (!dir.exists() || !dir.isDirectory()) {
				Log.w(TAG, "The target path does not exist or not a directory. path=" + dirPath);
				return false;
			}
			
			return deleteDir(dir);  // 递归清空目录中的文件及子目录
		} catch (Exception e) {
			Log.w(TAG, "Exception at deleteDir(String), dirPath=" + dirPath, e);
			return false;
		}
    }
    
    private static boolean deleteDir(File dir) {
    	if (dir == null) {
    		return false;
    	}
    	
    	boolean result = true;
    	File[] files = dir.listFiles();
    	if (files != null) {
	    	for (File file : files) {
	    		boolean hasSuccess = true;
				if (file.isDirectory()) {
					hasSuccess = deleteDir(file);
				} else {
					hasSuccess = file.delete();
				}
				if (!hasSuccess) {
					result = false;
				}
			}
    	}
    	
    	if (dir.delete() == false) {
    		result = false;
    	}
    	
    	return result;
    }
    
    
    /**
	 * 复制单个指定文件到指定的路径
	 * @param srcPath 要复制的文件的路径
	 * @param dstPath 目标路径（需要包含文件名）
	 * @return 是否成功复制
	 */
	public static boolean copyFile(String srcPath, String dstPath) {
		if (TextUtils.isEmpty(srcPath)) {
			Log.w(TAG, "Argument 'srcPath' is null or empty at copyFile(String, String)");
			return false;
		}
		if (TextUtils.isEmpty(dstPath)) {
			Log.w(TAG, "Argument 'dstPath' is null or empty at copyFile(String, String)");
			return false;
		}
		
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			File srcfile = new File(srcPath);
			if (!srcfile.exists()) {
				Log.w(TAG, "The source file doesn't exist. srcPath=" + srcPath);
				return false;
			}
			
			is = new FileInputStream(srcPath);  // 读入原文件
			fos = new FileOutputStream(dstPath);
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			
			int byteread = 0;
			while ((byteread = is.read(buffer)) != -1) {
				fos.write(buffer, 0, byteread);
			}
			fos.flush();
			
			return true;
		} catch (Exception e) {
			Log.w(TAG, "Exception at copyFile(String, String), srcPath=" + srcPath, e);
			return false;
		} finally {
			try {
				if (is != null) is.close();
				if (fos != null) fos.close();
			} catch (Exception e) {}
		}
	}
	
	private static boolean copyFile(File srcFile, File dstFile) throws Exception {
		if (srcFile == null || dstFile == null) {
    		return false;
    	}
		
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			is = new FileInputStream(srcFile);  // 读入原文件
			fos = new FileOutputStream(dstFile);
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			
			int byteread = 0;
			while ((byteread = is.read(buffer)) != -1) {
				fos.write(buffer, 0, byteread);
			}
			fos.flush();
			
			return true;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (is != null) is.close();
				if (fos != null) fos.close();
			} catch (Exception e) {}
		}
	}
	
	/**
     * 复制单个指定文件到指定的目录下
     * @param srcPath 要复制的文件的路径
     * @param dstDirPath 目标目录的路径，如不存在会自动创建
     * @return 是否成功复制
     */
    public static boolean copyFileTo(String srcPath, String dstDirPath) {
    	if (TextUtils.isEmpty(dstDirPath)) {
			Log.w(TAG, "Argument 'dstDirPath' is null or empty at copyFileTo(String, String)");
			return false;
		}
    	
    	File srcFile = new File(srcPath);
    	if (!srcFile.exists()) {
    		Log.w(TAG, "The source file doesn't exist. srcPath=" + srcPath);
			return false;
		}
    	
    	if (mkdirIfNotFound(dstDirPath) == false) {
    		Log.w(TAG, "The target dir can't be created. dstDirPath=" + dstDirPath);
    	}
    	
    	try {
    		File dstFile = new File(dstDirPath, srcFile.getName());
    		return copyFile(srcFile, dstFile);
		} catch (Exception e) {
			Log.w(TAG, "Exception at copyFileTo(String, String)", e);
			return false;
		}
    }
    
	
	/**
	 * 递归复制指定目录中的所有文件和子目录（不包括自己）到指定的路径
	 * @param srcPath 被复制的目录的路径
	 * @param dstPath 目标目标的路径（需要包含目录的名称）
	 * @return 是否成功复制
	 */
	public static boolean copyDir(String srcPath, String dstPath) {
		if (TextUtils.isEmpty(srcPath)) {
			Log.w(TAG, "Argument 'srcPath' is null or empty at copyDir(String, String)");
			return false;
		}
		if (TextUtils.isEmpty(dstPath)) {
			Log.w(TAG, "Argument 'dstPath' is null or empty at copyDir(String, String)");
			return false;
		}
		
		try {
			File srcDir = new File(srcPath);
			if (!srcDir.exists() || !srcDir.isDirectory()) {
				Log.w(TAG, "The source path doesn't exist or not a directory. srcPath=" + srcPath);
				return false;
			}
			
			return copyDir(srcDir, new File(dstPath));  // 递归复制目录中的文件及子目录
		} catch (Exception e) {
			Log.w(TAG, "Exception at copyDir(String, String), srcPath=" + srcPath, e);
			return false;
		}
	}
	
	private static boolean copyDir(File srcDir, File dstDir) throws Exception {
		if (srcDir == null || dstDir == null) {
    		return false;
    	}
		
		if (!dstDir.exists()) {
			dstDir.mkdirs();
		}
		if (!dstDir.isDirectory()) {
			return false;
		}
		
		boolean result = true;
    	File[] files = srcDir.listFiles();
    	if (files != null) {
	    	for (File file : files) {
	    		boolean hasSuccess = true;
	    		File targetFile = new File(dstDir, file.getName());
				if (file.isDirectory()) {
					hasSuccess = copyDir(file, targetFile);
				} else {
					hasSuccess = copyFile(file, targetFile);
				}
				if (!hasSuccess) {
					result = false;
				}
			}
    	}
    	
    	return result;
	}
	
	/**
     * 递归复制指定的整个目录（包含自己及其所有文件和子目录）到指定的目录下
     * @param srcPath 要复制的目录的路径
     * @param dstDirPath 目标目录的路径，如不存在会自动创建
     * @return 是否成功复制
     */
    public static boolean copyDirTo(String srcDirPath, String dstDirPath) {
    	if (TextUtils.isEmpty(srcDirPath)) {
			Log.w(TAG, "Argument 'srcDirPath' is null or empty at copyDirTo(String, String)");
			return false;
		}
    	if (TextUtils.isEmpty(dstDirPath)) {
			Log.w(TAG, "Argument 'dstDirPath' is null or empty at copyDirTo(String, String)");
			return false;
		}
    	
    	File srcFile = new File(srcDirPath);
    	if (!srcFile.isDirectory()) {
    		Log.w(TAG, "The source dir doesn't exist. srcDirPath=" + srcDirPath);
			return false;
		}
    	
    	File dstFile = new File(dstDirPath, srcFile.getName());
    	return copyDir(srcDirPath, dstFile.getAbsolutePath());
    }
    
    
    /**
	 * 移动单个指定文件到指定的路径
	 * @param srcPath 要移动的文件的路径
	 * @param dstPath 目标路径（需要包含文件名）
	 * @return 是否成功移动
	 */
    public static boolean moveFile(String srcPath, String dstPath) {
    	boolean hasSuccess = false;
    	hasSuccess = copyFile(srcPath, dstPath);
    	if (hasSuccess) {
    		hasSuccess = deleteFile(srcPath);
    	}
    	return hasSuccess;
	}
    
    /**
     * 移动单个指定文件到指定的目录下
     * @param srcPath 要移动的文件的路径
     * @param dstDirPath 目标目录的路径，如不存在会自动创建
     * @return 是否成功移动
     */
    public static boolean moveFileTo(String srcPath, String dstDirPath) {
    	boolean hasSuccess = false;
    	hasSuccess = copyFileTo(srcPath, dstDirPath);
    	if (hasSuccess) {
    		hasSuccess = deleteFile(srcPath);
    	}
    	return hasSuccess;
	}
    
    /**
	 * 移动指定目录中的有文件和子目录（不包括自己）到指定的路径，移动后该目录也会被删除
	 * @param srcPath 要移动的目录的路径
	 * @param dstPath 目标目标的路径（需要包含目录的名称）
	 * @return 是否成功移动
	 */
    public static boolean moveDir(String srcPath, String dstPath) {
    	boolean hasSuccess = false;
    	hasSuccess = copyDir(srcPath, dstPath);
    	if (hasSuccess) {
    		hasSuccess = deleteDir(srcPath);
    	}
    	return hasSuccess;
	}
    
    /**
     * 移动指定的整个目录（包含自己及其所有文件和子目录）到指定的目录下
     * @param srcPath 要移动的目录的路径
     * @param dstDirPath 目标目录的路径，如不存在会自动创建
     * @return 是否成功复制
     */
    public static boolean moveDirTo(String srcPath, String dstPath) {
    	boolean hasSuccess = false;
    	hasSuccess = copyDirTo(srcPath, dstPath);
    	if (hasSuccess) {
    		hasSuccess = deleteDir(srcPath);
    	}
    	return hasSuccess;
	}
	
	
	/**
	 * <p>读取指定路径的文本文件，转换成整块字符串，包含换行符</p>
	 * <p><b>注意：</b>仅可以读取小文件，读取大文件的话有可能会造成OOM</p>
	 * @param path 文件的完整路径
	 * @return 文件内容生成的字符串，读取失败时返回null
	 */
	public static String readString(String path) {
		if (TextUtils.isEmpty(path)) {
			Log.w(TAG, "Argument 'path' is null or empty at readStringLines()");
			return null;
		}
		
		File file = new File(path);
		if (!file.exists() || !file.isFile()) {
			Log.w(TAG, "The target file not exist at readStringLines(), path=" + path);
			return null;
		}
		
		FileReader fr = null;
		BufferedReader br = null;
		StringBuilder result = new StringBuilder();
		
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line = null;
			while ((line = br.readLine()) != null) {
				result.append(line).append(LINE_SEPARATOR);
			}
		} catch (Exception e) {
			Log.w(TAG, "Exception at readStringLines(), path=" + path, e);
			return null;
		} finally {
			try {
				if (null != br) br.close();
				if (null != fr) fr.close();
			} catch (IOException e) {}
		}
		
		return result.toString();
	}
	
	/**
	 * <p>读取指定路径的文本文件，转换成字符串列表</p>
	 * <p><b>注意：</b>仅可以读取小文件，读取大文件的话有可能会造成OOM</p>
	 * @param path 文件的完整路径
	 * @return 文件内容生成的字符串列表，读取失败时返回null
	 */
	public static List<String> readStringLines(String path) {
		if (TextUtils.isEmpty(path)) {
			Log.w(TAG, "Argument 'path' is null or empty at readStringLines()");
			return null;
		}
		
		File file = new File(path);
		if (!file.exists() || !file.isFile()) {
			Log.w(TAG, "The target file not exist at readStringLines(), path=" + path);
			return null;
		}
		
		FileReader fr = null;
		BufferedReader br = null;
		List<String> result = new ArrayList<String>();
		
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line = null;
			while ((line = br.readLine()) != null) {
				result.add(line);
			}
		} catch (Exception e) {
			Log.w(TAG, "Exception at readStringLines(), path=" + path, e);
			return null;
		} finally {
			try {
				if (null != br) br.close();
				if (null != fr) fr.close();
			} catch (IOException e) {}
		}
		
		return result;
	}

	/**
	 * <p>读取指定路径的文件，转换成字节数组</p>
	 * <p><b>注意：</b>仅可以读取小文件，读取大文件的话有可能会造成OOM</p>
	 * @param path 文件的完整路径
	 * @return 文件的完整数据的字节数组，读取失败时返回null
	 */
	public static byte[] readBytes(String path) {
		if (TextUtils.isEmpty(path)) {
			Log.w(TAG, "Argument 'path' is null or empty at readBytes()");
			return null;
		}
		
		File file = new File(path);
		if (!file.exists() || !file.isFile()) {
			Log.w(TAG, "The target file not exist at readBytes(), path=" + path);
			return null;
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			int byteread;
			while ((byteread = fis.read(buffer)) != -1) {
				baos.write(buffer, 0, byteread);
			}
			baos.flush();
		} catch (Exception e) {
			Log.w(TAG, "Exception at readBytes(), path=" + path, e);
			return null;
		} finally {
			try {
				baos.close();
			} catch (IOException e) { }
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) { }
			}
		}
		
		return baos.toByteArray();
	}
	
	
	private FileUtils() { }
}
