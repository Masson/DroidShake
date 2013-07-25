package com.imasson.droidshake.test.util;

import android.os.Debug;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.imasson.droidshake.util.debug.ShakeDiagnotor;

/**
 * {@link ShakeDiagnotor}的单元测试
 */
public class ShakeDiagnotorTest extends InstrumentationTestCase {
	private static final String TAG = "ShakeDiagnotor";
	
	private ShakeDiagnotor mShakeDiagnotor;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		Log.i(TAG, "test case setUp");
		mShakeDiagnotor = new ShakeDiagnotor(getInstrumentation().getTargetContext());
	}
	

	public void testGetOwnMemoryInfo() {
		Debug.MemoryInfo memoryInfo = mShakeDiagnotor.getOwnMemoryInfo();
		assertNotNull(memoryInfo);
		
		int pss = memoryInfo.getTotalPss();
		Log.i(TAG, "testGetOwnMemoryInfo: pss=" + pss);
		assertTrue(pss > 0);
	}
	
	public void testGetSystemAvailableMemorySize() {
		long size = mShakeDiagnotor.getSystemAvailableMemorySize();
		Log.i(TAG, "testGetSystemAvailableMemorySize: system_avail_memory_size=" + size);
		assertTrue(size >= 0L);
	}
	
	public void testGetSystemTotalMemorySize() {
		long size = mShakeDiagnotor.getSystemTotalMemorySize();
		Log.i(TAG, "testGetSystemTotalMemorySize: system_total_memory_size=" + size);
		assertTrue(size > 0L);
	}
	
	public void testGetSystemFreeMemorySize() {
		long size = mShakeDiagnotor.getSystemFreeMemorySize();
		Log.i(TAG, "testGetSystemFreeMemorySize: system_free_memory_size=" + size);
		assertTrue(size >= 0L);
	}
	
	public void testGetOwnCpuRate() {
		int cpuRate = mShakeDiagnotor.getOwnCpuRate();
		Log.i(TAG, "testGetOwnCpuRate: cpuRate=" + cpuRate);
		assertTrue(cpuRate >= 0);
	}
	
	
	@Override
	protected void tearDown() throws Exception {
		Log.i(TAG, "test case tearDown");
		super.tearDown();
	}
}
