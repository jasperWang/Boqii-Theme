package com.massvig.ecommerce.logic.notification;

import com.massvig.ecommerce.utilities.MassvigLogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class MassVigNetworkBroadcastReceiver extends BroadcastReceiver {

//	private ConnectivityManager connectivityManager;
//	private NetworkInfo info;
	private NetworkListener listener;
	private int times = 0;
	private static int min = 3;
	
	public MassVigNetworkBroadcastReceiver(NetworkListener networkListener) {
		// TODO Auto-generated constructor stub
		this.listener = networkListener;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
//            connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            info = connectivityManager.getActiveNetworkInfo();  
            boolean isBreak = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            if(!isBreak){
            	listener.available();
            }else{
            	listener.notAvailable();
            }
//            if(info != null && info.isAvailable()) {
//            	listener.available();
//            } else {
//            }
        } else if(action.equals(Intent.ACTION_TIME_TICK)){
        	times ++;
        	if(times % min == 0){
        		listener.timesChanged();
        		MassvigLogger.getInstance().v("dujun-1", "times changed");
        	}
        } else if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
        	listener.bootComplete();
        }

	}
	
	public interface NetworkListener {
		void available();
		void timesChanged();
		void notAvailable();
		void bootComplete();
	}

}
