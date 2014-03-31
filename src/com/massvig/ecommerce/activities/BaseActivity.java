package com.massvig.ecommerce.activities;

import com.google.analytics.tracking.android.EasyTracker;
import com.massvig.ecommerce.utilities.MassvigConfig;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;

public class BaseActivity extends Activity {

	public void GotoLogin(){
		startActivity(new Intent(this, LoginActivity.class));
	};

	@SuppressWarnings("deprecation")
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if(!MassvigConfig.DEBUG){
			MobclickAgent.onResume(this);
//			EasyTracker.getInstance().activityStart(this);
//			EasyTracker.getTracker().setAppName("BoQi");
//			EasyTracker.getTracker().trackView((String) getTitle());
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onStop();
		if(!MassvigConfig.DEBUG)
			MobclickAgent.onPause(this);
//			EasyTracker.getInstance().activityStop(this);
	}

}
