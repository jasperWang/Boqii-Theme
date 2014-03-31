package com.massvig.ecommerce.activities;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.managers.OrdersManager;
import com.massvig.ecommerce.managers.UserInfoManager;
import com.massvig.ecommerce.managers.UserInfoManager.Listener;
import com.massvig.ecommerce.managers.UserInfoManager.Numbers;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigExit;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.widgets.NetImageView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class UserInformationActivity extends BaseActivity implements OnClickListener, Listener {
	
	private UserInfoManager manager;
	private BaseApplication app;
	private NetImageView headImg;
	private ImageView sexImg;
	private LinearLayout unLogin_layout;
	private RelativeLayout login_layout;
	private TextView username, attention, fans, message_number, unpay_number, payed_number, refund_number, huodao_number;
	private static final int LOADDATA = 1;
	private MyBroadCast receiver = null;
	private MassVigExit exit = new MassVigExit();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_info);

//        ICSAPI api = CSAPIFactory.getCSAPI();
//        try {
//			api.init(this, "dujun");
//		} catch (Exception e) {
//			e.printStackTrace();
//			// TODO: handle exception
//		}
		
		setTitle(getString(R.string.userinformation));
		app = (BaseApplication) getApplication();
		manager = new UserInfoManager(app.user);
		manager.setListener(this);
		initView();
		receiver = new MyBroadCast();
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction("massvig.ecommerce.message");
		registerReceiver(receiver, mFilter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
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
	
	public class MyBroadCast extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("massvig.ecommerce.message")){
				int count = manager.GetAllUnread(UserInformationActivity.this, app.user.EJID);
				message_number.setVisibility(View.INVISIBLE);
				if(count > 0){
					message_number.setText(count + "");
					message_number.setVisibility(View.VISIBLE);
				}
			}
		}
		
	}

	private void initView() {
		unLogin_layout = (LinearLayout)findViewById(R.id.unlogin_layout);
		login_layout = (RelativeLayout)findViewById(R.id.login_layout);
		((Button)findViewById(R.id.back)).setOnClickListener(this);
		((Button)findViewById(R.id.modify)).setOnClickListener(this);
		((TextView)findViewById(R.id.login)).setOnClickListener(this);
		((TableRow)findViewById(R.id.unpay_row)).setOnClickListener(this);
		((TableRow)findViewById(R.id.payed_row)).setOnClickListener(this);
		((TableRow)findViewById(R.id.refund_row)).setOnClickListener(this);
		((TableRow)findViewById(R.id.huodao_row)).setOnClickListener(this);
		((TableRow)findViewById(R.id.address_row)).setOnClickListener(this);
		((TableRow)findViewById(R.id.coupon_row)).setOnClickListener(this);
		((TableRow)findViewById(R.id.message_row)).setOnClickListener(this);
		((TableRow)findViewById(R.id.soft_row)).setOnClickListener(this);
		((TableRow)findViewById(R.id.collect_row)).setOnClickListener(this);
//		((TableRow)findViewById(R.id.kefu)).setOnClickListener(this);
		headImg = (NetImageView)findViewById(R.id.userImg);
		headImg.setOnClickListener(this);
		sexImg = (ImageView)findViewById(R.id.sexImg);
		username = (TextView)findViewById(R.id.username);
		attention = (TextView)findViewById(R.id.follow);
		fans = (TextView)findViewById(R.id.fans);
		message_number = (TextView)findViewById(R.id.message_number);
		unpay_number = (TextView)findViewById(R.id.unpay_number);
		payed_number = (TextView)findViewById(R.id.payed_number);
		refund_number = (TextView)findViewById(R.id.refund_number);
		huodao_number = (TextView)findViewById(R.id.huodao_number);
	}

	@Override
	protected void onResume() {
		super.onResume();
		app = (BaseApplication) getApplication();
		app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(this, "SESSIONID", "") : app.user.sessionID;
		app.user.customerID = (app.user.customerID == -1) ? Integer.valueOf(MassVigUtil.getPreferenceData(this, "CUSTOMERID", "-1")) : app.user.customerID ;
		app.user.nickName = TextUtils.isEmpty(app.user.nickName) ? MassVigUtil.getPreferenceData(this, "NICKNAME", "") : app.user.nickName;
		app.user.headImage = TextUtils.isEmpty(app.user.headImage) ? MassVigUtil.getPreferenceData(this, "HEADIMG", "") : app.user.headImage;
		app.user.gender = (app.user.gender == 0) ? Integer.valueOf(MassVigUtil.getPreferenceData(this, "GENDER", "0")) : app.user.gender ;
		initLoginView();
		manager = new UserInfoManager(app.user);
		manager.setListener(this);
		manager.GetCustomerCenter();
	}

	private void initLoginView() {
		if(!TextUtils.isEmpty(app.user.sessionID)){
			unLogin_layout.setVisibility(View.GONE);
			login_layout.setVisibility(View.VISIBLE);
			initUserView();
		}else{
			unLogin_layout.setVisibility(View.VISIBLE);
			login_layout.setVisibility(View.GONE);
		}
	}

	private void initUserView() {
		headImg.setImageUrl(MassVigUtil.GetImageUrl(app.user.headImage, MassVigUtil.dip2px(this, 72), MassVigUtil.dip2px(this, 72)), MassVigContants.PATH, null);
		username.setText(app.user.nickName);
//		int gender = app.user.gender;
//		if(gender == 0){
//			sexImg.setVisibility(View.GONE);
//		}else{
			sexImg.setVisibility(View.INVISIBLE);
//			sexImg.setBackgroundResource(gender == 1 ? R.drawable.sex_m : R.drawable.sex_w);
//		}
		int count = manager.GetAllUnread(this, app.user.EJID);
		message_number.setVisibility(View.INVISIBLE);
		if(count > 0){
			message_number.setText(count + "");
			message_number.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			startActivity(new Intent(this, SettingActivity.class));
			break;
		case R.id.modify:
			startActivity(new Intent(this, ModifyUserInfoActivity.class));
			break;
		case R.id.login:
			app.user.sessionID = "";
			MassVigUtil.setPreferenceStringData(this, "SESSIONID", "");
			startActivity(new Intent(this, LoginActivity.class));
			break;
		case R.id.unpay_row:
			startActivity(new Intent(this, ManageOrderActivity.class).putExtra("orderTab", OrdersManager.UNPAYORDER));
			break;
		case R.id.payed_row:
			startActivity(new Intent(this, ManageOrderActivity.class).putExtra("orderTab", OrdersManager.PAIDORDER));
			break;
		case R.id.refund_row:
			startActivity(new Intent(this, ManageOrderActivity.class).putExtra("orderTab", OrdersManager.REFUNDORDER));
			break;
		case R.id.huodao_row:
			startActivity(new Intent(this, ManageOrderActivity.class).putExtra("orderTab", OrdersManager.HUODAOORDER));
			break;
//		case R.id.kefu:
//			ICSAPI api = CSAPIFactory.getCSAPI();
//			api.startCS(UserInformationActivity.this, "main");
//			break;
		case R.id.collect_row:
			app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(this, "SESSIONID", "") : app.user.sessionID;
			//TODO
			if(!TextUtils.isEmpty(app.user.sessionID)){
				startActivity(new Intent(this, CollectActivity.class));
			}else{
				app.user.sessionID = "";
				MassVigUtil.setPreferenceStringData(this, "SESSIONID", "");
				startActivity(new Intent(this, LoginActivity.class));
			}
			break;
		case R.id.address_row:
			app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(this, "SESSIONID", "") : app.user.sessionID;
			if(!TextUtils.isEmpty(app.user.sessionID)){
				startActivity(new Intent(this, UserManageAddressActivity.class));
			}else{
				app.user.sessionID = "";
				MassVigUtil.setPreferenceStringData(this, "SESSIONID", "");
				startActivity(new Intent(this, LoginActivity.class));
			}
			break;
		case R.id.coupon_row:
			app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(this, "SESSIONID", "") : app.user.sessionID;
			if(!TextUtils.isEmpty(app.user.sessionID)){
				startActivity(new Intent(this, ManageCouponActivity.class));
			}else{
				app.user.sessionID = "";
				MassVigUtil.setPreferenceStringData(this, "SESSIONID", "");
				GotoLogin();
			}
			break;
		case R.id.message_row:
			app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(this, "SESSIONID", "") : app.user.sessionID;
			if(!TextUtils.isEmpty(app.user.sessionID)){
				startActivity(new Intent(this, MessagesActivity.class));
			}else{
				app.user.sessionID = "";
				MassVigUtil.setPreferenceStringData(this, "SESSIONID", "");
				startActivity(new Intent(this, LoginActivity.class));
			}
			break;
		case R.id.soft_row:
			startActivity(new Intent(this, RecommendActivity.class));
			break;
		case R.id.userImg:
			startActivity(new Intent(this, FullScreenActivity.class).putExtra("IMAGE", app.user.headImage));
			break;
		default:
			break;
		}
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case LOADDATA:
				Numbers n = (Numbers) msg.obj;
				unpay_number.setText(n.OnlineUnpayCount + "");
				payed_number.setText(n.OnlinePaidCount + "");
				refund_number.setText(n.OnlineRefundCount + "");
				huodao_number.setText(n.OfflineCashCount + "");
				attention.setText(getString(R.string.attention, n.FollowingCustomerCount + ""));
				fans.setText(getString(R.string.fans, n.FansCustomerCount + ""));
				break;

			default:
				break;
			}
		}};

	@Override
	public void Success(int index) {
		
	}

	@Override
	public void Failed(int index) {
		
	}

	@Override
	public void SessionFailed() {
//		if(!TextUtils.isEmpty(app.user.sessionID + MassVigUtil.getPreferenceData(this, "SESSIONID", "")))
			startActivity(new Intent(this, LoginActivity.class));
		app.user.sessionID = "";
		MassVigUtil.setPreferenceStringData(this, "SESSIONID", "");
		
	}

	@Override
	public void Dialog(boolean toShow) {
		
	}

	@Override
	public void LoadData(Numbers n) {
		Message msg = new Message();
		msg.what = LOADDATA;
		msg.obj = n;
		mHandler.sendMessage(msg);
	}

	@Override
	public void NickName() {
		// TODO Auto-generated method stub
		
	}
	
}
