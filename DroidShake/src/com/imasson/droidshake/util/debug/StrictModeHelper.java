package com.imasson.droidshake.util.debug;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.util.Log;

/**
 * <p>用于启用Android系统提供的StrictMode检测工具的工具类</p>
 * <p>本类主要是供在 API Level < 9的编译环境下使用的，否则不用引入或使用该类，直接使用 
 * {@link android.os.StrictMode StrictMode} 类即可。</p>
 * 
 * @see <a href="http://developer.android.com/reference/android/os/StrictMode.html">
 * official reference of StrictMode</a>
 */
public final class StrictModeHelper {
	private static final String TAG = "StrictModeHelper";
	
	private StrictModeHelper() {}
	
	public static final int OPTION_SYSTEM_DEFAULT                  = 0x0000;
    
    public static final int OPTION_THREAD_POLICY_DISK_READS        = 0x0001;
    public static final int OPTION_THREAD_POLICY_DISK_WRITES       = 0x0002;
    public static final int OPTION_THREAD_POLICY_NETWORK           = 0x0004;
    public static final int OPTION_THREAD_POLICY_POLICY_SLOW_CALLS = 0x0008;
    
    public static final int OPTION_VM_POLICY_ACTIVITY_LEAKS        = 0x0100;
    public static final int OPTION_VM_POLICY_CLOSABLE_LEAKS        = 0x0200;
    public static final int OPTION_VM_POLICY_REGISTRATION_LEAKS    = 0x0400;
    public static final int OPTION_VM_POLICY_SQLLITE_LEAKS         = 0x0800;
    
    public static final int OPTION_VM_POLICY_ALL = 
            OPTION_VM_POLICY_ACTIVITY_LEAKS |
            OPTION_VM_POLICY_CLOSABLE_LEAKS |
            OPTION_VM_POLICY_REGISTRATION_LEAKS |
            OPTION_VM_POLICY_SQLLITE_LEAKS;
    
    public static final int OPTION_THREAD_POLICY_ALL = 
            OPTION_THREAD_POLICY_DISK_READS | 
            OPTION_THREAD_POLICY_DISK_WRITES |
            OPTION_THREAD_POLICY_NETWORK |
            OPTION_THREAD_POLICY_POLICY_SLOW_CALLS;
    
    public static final int OPTION_ALL = 
            OPTION_THREAD_POLICY_ALL |
            OPTION_VM_POLICY_ALL;
    
    
    /**
     * 采用系统默认模式启用StrictMode
     */
    public static void enableDefault() {
        if (!checkSdkVersion(9)) {
            Log.w(TAG, "StrictMode is not supported at this android version.");
            return;
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            Class<?> klass = Class.forName("android.os.StrictMode");
            Method method = klass.getDeclaredMethod("enableDefaults");
            method.invoke(null);
            Log.v(TAG, "StrictMode enabled, mode=default");
            
        } catch (ClassNotFoundException e) {
            Log.w(TAG, "ClassNotFoundException at enableDefault()", e);
        } catch (NoSuchMethodException e) {
            Log.w(TAG, "NoSuchMethodException at enableDefault()", e);
        } catch (RuntimeException e) {
            Log.w(TAG, "Unexpected exception at enableDefault()", e);
        } catch (IllegalAccessException e) {
            Log.w(TAG, "IllegalAccessException at enableDefault()", e);
        } catch (InvocationTargetException e) {
            Log.w(TAG, "InvocationTargetException at enableDefault()", e.getCause());
        }
        
        long costTime = System.currentTimeMillis() - startTime;
        Log.v(TAG, "enableDefault, cost time: " + costTime + " ms");
    }
    
    /**
     * 启用StrictMode，并尽可能采用所有已知的模式进行检测
     */
    public static void enableAll() {
        enable(OPTION_ALL);
    }
    
    /**
     * 启用StrictMode，并使用指定的选项
     * @param options 检测模式选项，标记位，请参考 <code>OPTION_</code> 开头的常量
     */
    public static void enable(int options) {
        if (!checkSdkVersion(9)) {
            Log.w(TAG, "StrictMode is not supported at this android version.");
            return;
        }
        
        if (options == OPTION_SYSTEM_DEFAULT) {
            enableDefault();
        }
        
        long startTime = System.currentTimeMillis();
        
        Class<?> threadPolicyBuilderClass = null;
        Class<?> vmPolicyBuilderClass = null;
        Object threadPolicyBuilder = null;
        Object vmPolicyBuilder = null;
        
        try {
            threadPolicyBuilderClass = Class.forName(
                    "android.os.StrictMode$ThreadPolicy$Builder");
            vmPolicyBuilderClass = Class.forName(
                    "android.os.StrictMode$VmPolicy$Builder");
            threadPolicyBuilder = threadPolicyBuilderClass.newInstance();
            vmPolicyBuilder = vmPolicyBuilderClass.newInstance();
        } catch (ClassNotFoundException e) {
            Log.w(TAG, "ClassNotFoundException at applyStrictMode()", e);
        } catch (IllegalAccessException e) {
            Log.w(TAG, "IllegalAccessException at applyStrictMode()", e);
        } catch (InstantiationException e) {
            Log.w(TAG, "InstantiationException at applyStrictMode()", e);
        }
        
        int mask = 0x0001;
        for (int i = 0; i < 16; i++) {
            mask = mask << i;
            int optionItem = (options & mask);
            if (optionItem != 0) {
                if (optionItem <= OPTION_THREAD_POLICY_ALL) {
                    enableOption(threadPolicyBuilderClass, threadPolicyBuilder, optionItem);
                } else if (optionItem <= OPTION_VM_POLICY_ALL) {
                    enableOption(vmPolicyBuilderClass, vmPolicyBuilder, optionItem);
                } else {
                    Log.w(TAG, "Unknow option at enable(), option=" + optionItem);
                }
            }
        }
        
        boolean isSuccess = applyStrictMode(
                threadPolicyBuilderClass, vmPolicyBuilderClass, 
                threadPolicyBuilder, vmPolicyBuilder);
        if (isSuccess) {
            Log.v(TAG, "StrictMode enabled, options=" + options);
        } else {
            Log.w(TAG, "Fail to enable StrictMode, options=" + options);
        }
        
        long costTime = System.currentTimeMillis() - startTime;
        Log.v(TAG, "enable, cost time: " + costTime + " ms");
    }

    private static boolean applyStrictMode(
            Class<?> threadPolicyBuilderClass, Class<?> vmPolicyBuilderClass,
            Object threadPolicyBuilder, Object vmPolicyBuilder) {
        boolean isSuccess = false;
        try {
            Object threadPolicy = buildPolicy(threadPolicyBuilderClass, threadPolicyBuilder);
            Object vmPolicy = buildPolicy(vmPolicyBuilderClass, vmPolicyBuilder);
            
            Class<?> klass = Class.forName("android.os.StrictMode");
            if (threadPolicy != null) {
                Method setThreadPolicyMethod = klass.getDeclaredMethod("setThreadPolicy");
                setThreadPolicyMethod.invoke(null, threadPolicy);
            }
            if (vmPolicy != null) {
                Method setVmPolicyMethod = klass.getDeclaredMethod("setVmPolicy");
                setVmPolicyMethod.invoke(null, vmPolicy);
            }
            isSuccess = true;
            
        } catch (ClassNotFoundException e) {
            Log.w(TAG, "ClassNotFoundException at applyStrictMode()", e);
        } catch (NoSuchMethodException e) {
            Log.w(TAG, "NoSuchMethodException at applyStrictMode()", e);
        } catch (RuntimeException e) {
            Log.w(TAG, "Unexpected exception at applyStrictMode()", e);
        } catch (IllegalAccessException e) {
            Log.w(TAG, "IllegalAccessException at applyStrictMode()", e);
        } catch (InvocationTargetException e) {
            Log.w(TAG, "InvocationTargetException at applyStrictMode()", e.getCause());
        }
        return isSuccess;
    }

    private static Object buildPolicy(Class<?> builderClass, Object builder) 
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (builderClass != null && builder != null) {
            Method method = builderClass.getDeclaredMethod("penaltyLog");
            method.invoke(builder);
            Method buildMethod = builderClass.getDeclaredMethod("build");
            return buildMethod.invoke(builder);
        }
        return null;
    }
    
    private static boolean checkSdkVersion(int sdk) {
        return android.os.Build.VERSION.SDK_INT >= sdk;
    }
    
    
    private static void enableOption(Class<?> builderClass, Object builder, int option) {
        if (builderClass == null || builder == null) {
            Log.w(TAG, "Target option not support at enableOption(), option=" + option);
            return;
        }
        
        try {
            enableOptionOrThrows(builderClass, builder, option);
        } catch (RuntimeException e) {
            Log.w(TAG, "Unexpected exception at enableOption(), option=" + option, e);
        } catch (NoSuchMethodException e) {
            Log.w(TAG, "NoSuchMethodException at enableOption(), option=" + option, e);
        } catch (IllegalAccessException e) {
            Log.w(TAG, "IllegalAccessException at enableOption(), option=" + option, e);
        } catch (InvocationTargetException e) {
            Log.w(TAG, "InvocationTargetException at enableOption(), option=" + option, e.getCause());
        }
    }
    
    private static void enableOptionOrThrows(Class<?> builderClass, Object builder, int option) 
            throws SecurityException, NoSuchMethodException, IllegalArgumentException, 
            IllegalAccessException, InvocationTargetException {
        Method method = null;
        switch (option) {
            case OPTION_THREAD_POLICY_DISK_READS:
                method = builderClass.getDeclaredMethod("detectDiskReads");
                break;
                
            case OPTION_THREAD_POLICY_DISK_WRITES:
                method = builderClass.getDeclaredMethod("detectDiskWrites");
                break;
                
            case OPTION_THREAD_POLICY_NETWORK:
                method = builderClass.getDeclaredMethod("detectDiskWrites");
                break;
                
            case OPTION_THREAD_POLICY_POLICY_SLOW_CALLS:
                if (checkSdkVersion(11)) {
                    method = builderClass.getDeclaredMethod("detectCustomSlowCalls");
                }
                break;
                
            case OPTION_VM_POLICY_ACTIVITY_LEAKS:
                if (checkSdkVersion(11)) {
                    method = builderClass.getDeclaredMethod("detectActivityLeaks");
                }
                break;
                
            case OPTION_VM_POLICY_CLOSABLE_LEAKS:
                if (checkSdkVersion(11)) {
                    method = builderClass.getDeclaredMethod("detectLeakedClosableObjects");
                }
                break;
                
            case OPTION_VM_POLICY_REGISTRATION_LEAKS:
                if (checkSdkVersion(16)) {
                    method = builderClass.getDeclaredMethod("detectLeakedRegistrationObjects");
                }
                break;
                
            case OPTION_VM_POLICY_SQLLITE_LEAKS:
                method = builderClass.getDeclaredMethod("detectLeakedSqlLiteObjects");
                break;
                
            default:
                break;
        }
        
        if (method == null) {
            Log.w(TAG, "The method not found at enableOption(), option=" + option);
            return;
        }
        
        method.invoke(builder);
    }
}
