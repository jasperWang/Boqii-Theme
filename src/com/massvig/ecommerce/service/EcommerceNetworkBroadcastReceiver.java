package com.massvig.ecommerce.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class EcommerceNetworkBroadcastReceiver extends BroadcastReceiver {

	private NetworkListener listener;
	
	public EcommerceNetworkBroadcastReceiver(NetworkListener networkListener) {
		this.listener = networkListener;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            boolean isBreak = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            if(!isBreak){
            	listener.available();
            }else{
            	listener.notAvailable();
            }
        } else if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
        	listener.bootComplete();
        }

	}
	
	public interface NetworkListener {
		void available();
		void notAvailable();
		void bootComplete();
	}

}
