package com.massvig.ecommerce.utilities;

import android.util.Log;

public class MassvigLogger {
	//TODO change DEBUG to false when release
	private static final boolean DEBUG = true;
	private static MassvigLogger instance = null;
	
	public synchronized static MassvigLogger getInstance(){
		if(instance == null){
			instance = new MassvigLogger();
		}
		return instance;
	}
	
	public void v(String tag, String message){
		if(DEBUG){
			Log.v(tag, message);
		}
	}

}
