package com.massvig.ecommerce.activities;

import org.json.JSONObject;

import com.google.analytics.tracking.android.EasyTracker;
import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.service.MassVigService;
import com.massvig.ecommerce.utilities.MassVigSystemSetting;
import com.massvig.ecommerce.utilities.MassVigUtil;

import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

/**
 * 5个主界面的父容器Tabhost
 * @author DuJun
 *
 */
public class MainTabActivity extends ActivityGroup implements OnClickListener{
	/** Called when the activity is first created. */
	private TabHost tabHost;
	private Button tab1,tab2,tab3,tab4,tab5;
	private LinearLayout tabHostLayout;
	private int tabindex = 0;
	private BaseApplication app;
	private TextView total;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maintab);
		app = (BaseApplication)getApplication();
		BaseApplication.udid = MassVigUtil.id(this);
		tabindex = this.getIntent().getIntExtra("INDEX", 0);
		initTabHost();
		new GetCountAsync().execute();
	}

	class GetCountAsync extends AsyncTask<String, Void, Integer> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO
			if(result > 0){
				total.setVisibility(View.VISIBLE);
				total.setText(result + "");
				app.user.ShoppingCartTotalNum = result;
				setTotal(app.user.ShoppingCartTotalNum);
			}
			super.onPostExecute(result);
		}

		@Override
		protected Integer doInBackground(String... params) {
			String result = "";
			result = MassVigService.getInstance().TotalNum(app.user.sessionID);
			try {
				if(!TextUtils.isEmpty(result)){
					try {
						JSONObject object = new JSONObject(result);
						int resultCode = object.getInt("ResponseStatus");
						if (resultCode == 0) {
							JSONObject obj = object.getJSONObject("ResponseData");
							int number = obj.optInt("TotalNum");
							return number;
						}
						return resultCode;
					} catch (Exception e) {
						return -1;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return -1;
		}

	}
	public void setTotal(int number){
		total.setVisibility(View.GONE);
		if(number > 0){
			total.setText(number + "");
			if(number > 99){
				total.setText("N");
			}
			total.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 初始化tabhost
	 */
	public void initTabHost() {
		total = (TextView)findViewById(R.id.total);
		tabHost = (TabHost) findViewById(R.id.tabhost);
		tabHostLayout = (LinearLayout) findViewById(R.id.tab_layout);
		tabHost.setup(this.getLocalActivityManager());
		tab1 = (Button)findViewById(R.id.tab_radio_1);
		tab2 = (Button)findViewById(R.id.tab_radio_2);
		tab3 = (Button)findViewById(R.id.tab_radio_3);
		tab4 = (Button)findViewById(R.id.tab_radio_4);
		tab5 = (Button)findViewById(R.id.tab_radio_5);
		tab1.setOnClickListener(this);
		tab2.setOnClickListener(this);
		tab3.setOnClickListener(this);
		tab4.setOnClickListener(this);
		tab5.setOnClickListener(this);
		
		//FIXME
		Intent intent1 = new Intent(this, MainActivity.class);
		Intent intent2 = new Intent(this, SortListActivity.class);
//		Intent intent3 = new Intent(this, NearPersonAcvitity.class);
		Intent intent3 = new Intent(this, ShoppingCarActivity.class);
//		Intent intent3 = new Intent(this, RefundActivity.class);
		Intent intent4 = new Intent(this, CommunityGroupActivity.class);
		Intent intent5 = new Intent(this, UserInformationActivity.class);

		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("tab11")
				.setContent(intent1));
		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("tab22")
				.setContent(intent2));
		tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("tab33")
				.setContent(intent3));
		tabHost.addTab(tabHost.newTabSpec("tab4").setIndicator("tab44")
				.setContent(intent4));
		tabHost.addTab(tabHost.newTabSpec("tab5").setIndicator("tab55")
				.setContent(intent5));
		setTabHostIndex(tabindex);
	}

	public void setBottomGone(){
		tabHostLayout.setVisibility(View.GONE);
	}
	
	public void setBottomVisible(){
		tabHostLayout.setVisibility(View.VISIBLE);
	}
	
	public void setTabHostIndex(int index){
		if(index != 1)
			MassVigSystemSetting.getInstance().setTabHostFlag(MainTabActivity.this, index);

		tab1.setBackgroundResource(R.drawable.tab_1);
		tab2.setBackgroundResource(R.drawable.tab_2);
		tab3.setBackgroundResource(R.drawable.tab_3);
		tab4.setBackgroundResource(R.drawable.tab_4);
		tab5.setBackgroundResource(R.drawable.tab_5);
		tabHost.setCurrentTab(index);
		switch (index) {
		case 0:
			tab1.setBackgroundResource(R.drawable.tab_1_p);
			break;
		case 1:
			tab2.setBackgroundResource(R.drawable.tab_2_p);
			break;
		case 2:
			tab3.setBackgroundResource(R.drawable.tab_3_p);
			break;
		case 3:
			tab4.setBackgroundResource(R.drawable.tab_4_p);
			break;
		case 4:
			tab5.setBackgroundResource(R.drawable.tab_5_p);
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tab_radio_1:
			tabHost.setCurrentTab(0);
			setTabHostIndex(0);
			break;
		case R.id.tab_radio_2:
			tabHost.setCurrentTab(1);
			setTabHostIndex(1);
			break;
		case R.id.tab_radio_3:
			tabHost.setCurrentTab(2);
			setTabHostIndex(2);
			break;
		case R.id.tab_radio_4:
			tabHost.setCurrentTab(3);
			setTabHostIndex(3);
			break;
		case R.id.tab_radio_5:
			tabHost.setCurrentTab(4);
			setTabHostIndex(4);
			break;

		default:
			break;
		}
		
	}
}
