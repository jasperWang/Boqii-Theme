package com.massvig.ecommerce.activities;

import android.app.Application;
//import com.massvig.ecommerce.entities.NoQAccount;
import com.massvig.ecommerce.entities.City;
import com.massvig.ecommerce.entities.User;
import com.massvig.ecommerce.exception.MassVigCrashLog;
import com.massvig.ecommerce.utilities.MassvigConfig;

public class BaseApplication extends Application {

	public static City currentCity;
	public static String udid;
	public static String ip;
	public static int port;
	public static String service;
	public User user = new User();
	public static boolean isChating = false;
	private final static float TARGET_HEAP_UTILIZATION = 0.75f;
	private final static int CWJ_HEAP_SIZE = 6*1024*1024;
	
	@Override
	public void onCreate() {

		super.onCreate();
		//comment this before release
		if(MassvigConfig.DEBUG)
			Thread.setDefaultUncaughtExceptionHandler(new MassVigCrashLog(
					this.getApplicationContext()));
	}
	
	public static City getCurrentCity() {
		
		City result = currentCity;
		
		if (result == null) {
			result = new City();
			
//			result.setCityId(339);
//			result.setCityNamePY("s");
//			result.setIsHot(true);
//			result.setCityName("上海");
		}
		
		return result;
	}

}
