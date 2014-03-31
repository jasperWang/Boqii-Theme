package com.massvig.ecommerce.logic.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MassVigSystemBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
	        Intent i = new Intent();
	        i.setAction("com.massvig.ecommerce.logic.notification.MassVigPushService");
	        context.startService(i); 
	    } 
	}

}
