package com.massvig.ecommerce.activities;

import java.util.List;
import java.util.Map;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.Praised;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.widgets.CommunityMode;
import com.massvig.ecommerce.widgets.LoadView;
import com.massvig.ecommerce.widgets.LoadView.Onload;
import com.massvig.ecommerce.widgets.PraisedListView;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PraisedActivity extends BaseActivity implements OnClickListener{
	private PraisedListView mPraisedListView;
	private LoadView mLoadView;
	private BaseApplication app;
	private int customerID;
	private boolean isFirst = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.community_praised);
		setTitle(getString(R.string.praised));
		customerID = this.getIntent().getIntExtra("userID", 0);
		mPraisedListView = (PraisedListView)findViewById(R.id.praised_list_view);
		app = (BaseApplication) getApplication();
		((Button)findViewById(R.id.back)).setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getPraiseList();
	}

	public void initFooterView(){
		mLoadView = (LoadView)getLayoutInflater().inflate(R.layout.community_load_view, null);
		mPraisedListView.addFooterView(mLoadView);
		mLoadView.setOnloadCallBack(new Onload() {
			
			@Override
			public void callBack() {
				// TODO Auto-generated method stub
				getPraiseList();
			}
		});
	}
	
	public void getPraiseList(){
		if(!isFirst)
			mLoadView.showLoading();
		
		new AsyncTask<Object, Object, Object>() {

			@Override
			protected Map<String, Object> doInBackground(Object... params) {
				// TODO Auto-generated method stub
				Map<String, Object> map = null;
				app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(PraisedActivity.this, "SESSIONID", "") : app.user.sessionID;
				map = CommunityMode.getInstance().getUserReceivePraisePostList(app.user.sessionID, customerID, mPraisedListView.getItemsCount(), 10);
				return map;
				
			}
			
			@Override
			protected void onPostExecute(Object result) {
				// TODO Auto-generated method stub
				@SuppressWarnings("unchecked")
				Map<String,Object> map = (Map<String, Object>)result;
				if(map == null) return;
				if(mLoadView != null){
					mLoadView.showLoad();
				}
				int resultCode = (Integer)map.get("resultCode");
				
				if(resultCode == 0){
					isFirst = false;
						@SuppressWarnings("unchecked")
						List<Praised> list = (List<Praised>)map.get("userList");
						if(mPraisedListView.getFooterViewsCount() == 0){
							initFooterView();
							mLoadView.showLoad();
						}
						mPraisedListView.setData(list);
				}else if(resultCode == MassVigContants.SESSIONVAILED){
					GoToLogin();
				}
				
			}
			
		}.execute();
		
	}

	protected void GoToLogin() {
//		if(!TextUtils.isEmpty(app.user.sessionID + MassVigUtil.getPreferenceData(this, "SESSIONID", "")))
			startActivity(new Intent(this, LoginActivity.class));
		app.user.sessionID = "";
		MassVigUtil.setPreferenceStringData(PraisedActivity.this, "SESSIONID", "");
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
