package com.massvig.ecommerce.activities;

import java.util.ArrayList;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.Goods;
import com.massvig.ecommerce.entities.NearPerson;
import com.massvig.ecommerce.location.LocationManage;
import com.massvig.ecommerce.location.LocationManage.MyLocationListener;
import com.massvig.ecommerce.managers.NearPersonManager;
import com.massvig.ecommerce.managers.NearPersonManager.LoadListener;
import com.massvig.ecommerce.utilities.MassVigExit;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.widgets.NearPersonAdapter;
import com.massvig.ecommerce.widgets.NearRefreshListView;
import com.massvig.ecommerce.widgets.NearRefreshListView.RefreshListener;
import com.massvig.ecommerce.widgets.NetImageView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class NearPersonAcvitity extends BaseActivity implements OnClickListener, LoadListener{

	private NearRefreshListView listView;
	private boolean isScrolling = false;//判断listview是否在滚动
	private boolean isLoadingMore = false;//是否是上拉加载更多操作true:加载更多 false:刷新
	private NearPersonManager manager;
	private NearPersonAdapter adapter;
	private BaseApplication app;
	protected static final int REFRESH = 1;
	public static final int CLICK = 0;
	public static final int PRODUCT = 2;
	public static final int USER = 3;
	public LocationManage mLocationManage;
	private MassVigExit exit = new MassVigExit();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.near_person);
		setTitle(getString(R.string.nearperson));
		manager = new NearPersonManager();
		app = (BaseApplication) getApplication();
		manager.setListener(this);
		((Button)findViewById(R.id.back)).setOnClickListener(this);
		((Button)findViewById(R.id.finish)).setOnClickListener(this);
		listView = (NearRefreshListView)findViewById(R.id.listview);
		adapter = new NearPersonAdapter(this, manager.list, mHandler);
		listView.setAdapter(adapter);
		listView.setTextShow(getString(R.string.location_locating));
		setRefreshListener();
	}

	private void pressAgainExit(){
		if(exit.isExit()){
			finish();
		}else{
			Toast.makeText(this, getString(R.string.exit_message), Toast.LENGTH_SHORT).show();
			exit.doExitInOneSecond();
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			pressAgainExit();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		app = (BaseApplication) getApplication();
		initView();
	}

	private void initView() {
		initLocation();
	}

	// 初始化定位
	private void initLocation() {
		mLocationManage = LocationManage.getInstance();
		mLocationManage.setAllow(this, true);
		manager.lat = mLocationManage.getLatitude();
		manager.lon = mLocationManage.getLongitude();
		if (manager.lon != 0.0) {
			isLoadingMore = false;
			app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(NearPersonAcvitity.this, "SESSIONID", "") : app.user.sessionID;
			if(!TextUtils.isEmpty(app.user.sessionID)){
				manager.SubmitLocation(app.user.sessionID);
			}
			manager.loadDate(app.user.sessionID, NearPersonManager.REFRESH);
		} else {
			mLocationManage.startLocation(this, false, mmMyLocationListener);
		}

	}

	MyLocationListener mmMyLocationListener = new MyLocationListener() {

		@Override
		public void location(double latitude, double longitude) {
			manager.lat = latitude;
			manager.lon = longitude;
			if (manager.lat != 0.0 && manager.lon != 0.0) {
				mLocationManage.stopLocation();
				isLoadingMore = false;
				app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(NearPersonAcvitity.this, "SESSIONID", "") : app.user.sessionID;
				if(!TextUtils.isEmpty(app.user.sessionID)){
					manager.SubmitLocation(app.user.sessionID);
				}
				manager.loadDate(app.user.sessionID, NearPersonManager.REFRESH);
			}

		}

		@Override
		public void isAllow(Boolean isAllow) {
			if (isAllow) {
				mLocationManage.startLocation(NearPersonAcvitity.this, true,
						this);
				Toast.makeText(NearPersonAcvitity.this, getString(R.string.location_locating),
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(NearPersonAcvitity.this, getString(R.string.not_location),
						Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void address(String address) {
			if (address != null) {
				Toast.makeText(NearPersonAcvitity.this, address, Toast.LENGTH_SHORT).show();
			}
		}
	};

	/**
	 * 对listview监听
	 */
	private void setRefreshListener() {
		listView.setOnRefreshListener(new RefreshListener() {
			@Override
			public void startRefresh() {
				initLocation();
			}
			
			@Override
			public void startLoadMore() {
				isLoadingMore = true;
				app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(NearPersonAcvitity.this, "SESSIONID", "") : app.user.sessionID;
				manager.loadDate(app.user.sessionID, NearPersonManager.MORE);
			}
			
			@Override
			public void scrollStop() {
				isScrolling = false;
				NetImageView.setIsAutoLoadImage(true);
				ArrayList<NetImageView>  imageLists = adapter.getImageList();
				
				for(int i = 0,len = imageLists.size();i < len ;i++){
					if(isScrolling == false){
						imageLists.get(i).updateImage();
					}
				}
			}
			
			@Override
			public void scrollStart() {
				isScrolling = true;
				NetImageView.setIsAutoLoadImage(false);
			}
			
		});
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back://找人
			startActivity(new Intent(this, FindPeopleActivity.class));
			break;
		case R.id.finish://筛选
			AlertDialog.Builder builder = new Builder(NearPersonAcvitity.this);
			builder.setSingleChoiceItems(new String[]{getString(R.string.woman),getString(R.string.man)}, -1, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(which == 0){
						manager.gender = 2;
					}else{
						manager.gender = 1;
					}
					app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(NearPersonAcvitity.this, "SESSIONID", "") : app.user.sessionID;
					manager.loadDate(app.user.sessionID, NearPersonManager.REFRESH);
					dialog.dismiss();
				}});
			builder.create().show();
			break;

		default:
			break;
		}
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case REFRESH:
				if (isLoadingMore) {
					listView.finishFootView();
				}else{
					listView.finishHeadView();
					listView.setTextShow(getString(R.string.down_pull_refresh));
				}
				adapter.notifyDataSetChanged();
				break;
			case CLICK:
				app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(NearPersonAcvitity.this, "SESSIONID", "") : app.user.sessionID;
				NearPerson p = (NearPerson) msg.obj;
				if(p.Relation == 2){
					manager.AddAndRemoveFans(1, app.user.sessionID, p.CustomerID);
				}else if(p.Relation == 3){
					manager.AddAndRemoveFans(0, app.user.sessionID, p.CustomerID);
				}
				break;
			case PRODUCT:
				NearPerson p1 = (NearPerson) msg.obj;
				Goods good = new Goods();
				good.productID = p1.ProductID;
				good.name = p1.ProductName;
				startActivity(new Intent(NearPersonAcvitity.this, GoodsDetailActivity.class).putExtra("goods", good));
				break;
			case USER:
				NearPerson p2 = (NearPerson) msg.obj;
				startActivity(new Intent(NearPersonAcvitity.this, CommunityUserInfoActivity.class).putExtra("USERID", p2.CustomerID));
				break;
			default:
				break;
			}
		}};

	@Override
	public void LoadSuccess(int tag) {
		mHandler.sendEmptyMessage(REFRESH);
//		switch (tag) {
//		case NearPersonManager.LOADDATA:
//			break;
//		case NearPersonManager.FANS:
//			break;
//
//		default:
//			break;
//		}
	}

	@Override
	public void LoadFailed(int tag) {
		
	}

	@Override
	public void SessionVailed() {
//		if(!TextUtils.isEmpty(app.user.sessionID + MassVigUtil.getPreferenceData(this, "SESSIONID", "")))
			startActivity(new Intent(this, LoginActivity.class));
		app.user.sessionID = "";
		MassVigUtil.setPreferenceStringData(this, "SESSIONID", "");
	}

}
