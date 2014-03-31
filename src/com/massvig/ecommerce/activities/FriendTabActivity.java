package com.massvig.ecommerce.activities;

import com.google.analytics.tracking.android.EasyTracker;
import com.massvig.ecommerce.boqi.R;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class FriendTabActivity extends ActivityGroup implements OnClickListener{
	private TabHost tabHost;
	private RadioGroup tabWidget;
	private int userId,index;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.community_friend_tab);
		userId = getIntent().getIntExtra("_userId", 0);
		index = getIntent().getIntExtra("index", 0);
		initTabHost();
		((Button)findViewById(R.id.back)).setOnClickListener(this);
		if(this.getIntent().getIntExtra("COMMUNITY", 0) == 1){
			((LinearLayout)findViewById(R.id.layout)).setVisibility(View.GONE);
		}
	}
	
	public void initTabHost(){
		
		tabHost = (TabHost) findViewById(R.id.tabhost);
		tabHost.setup(this.getLocalActivityManager());
		tabWidget = (RadioGroup)findViewById(R.id.tab_radio);
		
		tabWidget.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(checkedId == R.id.friend_tab1){
					setCurrentTab(0);
				}else if(checkedId == R.id.friend_tab2){
					setCurrentTab(1);
				}else if(checkedId == R.id.friend_tab3){
					setCurrentTab(2);
				}
				
			}
		});
		
		Intent intent1 = new Intent().setClass(FriendTabActivity.this,FansListActivity.class).putExtra("_type", 0).putExtra("_userId", userId);
		Intent intent2 = new Intent().setClass(FriendTabActivity.this,FansListActivity.class).putExtra("_type", 1).putExtra("_userId", userId);
		Intent intent3 =new Intent().setClass(FriendTabActivity.this,FansListActivity.class).putExtra("_type", 2).putExtra("_userId", userId);
		
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("tab1").setContent(intent1));
		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("tab2").setContent(intent2));
		tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("tab3").setContent(intent3));
		setCurrentTab(index);
		
	}
	
	public void setCurrentTab(int index){
		((RadioButton)tabWidget.getChildAt(index)).setChecked(true);
		tabHost.setCurrentTab(index);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		 Activity subActivity = getLocalActivityManager().getCurrentActivity();
		 if (subActivity instanceof OnActivityGroupResultListener) {
			 OnActivityGroupResultListener listener = (OnActivityGroupResultListener) subActivity;
			 listener.OnActivityGroupResult(requestCode, resultCode, data);
		 }
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * 解决子Activity无法接收Activity回调的问题
	 * @author zhp
	 *
	 */
	public interface OnActivityGroupResultListener {
	    public void OnActivityGroupResult(int requestCode, int resultCode, Intent data);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;

		default:
			break;
		}
	}

}
