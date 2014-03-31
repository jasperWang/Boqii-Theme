package com.massvig.ecommerce.activities;

import com.google.analytics.tracking.android.EasyTracker;
import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.Chater;
import com.massvig.ecommerce.managers.CommunityUserManager;
import com.massvig.ecommerce.managers.CommunityUserManager.Listener;
import com.massvig.ecommerce.managers.CommunityUserManager.UserInfo;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.widgets.NetImageView;

import android.app.ActivityGroup;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

public class CommunityUserInfoActivity extends ActivityGroup implements Listener, OnClickListener{
	private TabHost tabHost;
	private RadioGroup tabWidget;

	private CommunityUserManager manager;
	private NetImageView userimg;
	private ImageView sex;
	private TextView username,message_number;
	private Button attentionBtn, fansBtn, actionBtn;
	private Bitmap shopDefaultImg;
	private MyBroadCast receiver = null;
	private BaseApplication app;
	private LinearLayout myself, others;
	private RelativeLayout scan_message, ignor_message;
	private RelativeLayout chat, add_fans, remove_fans;
	private static final int UNFOLLOW = 2;
	private static final int FOLLOWING = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.community_user_detail_item);
		app = (BaseApplication) getApplication();
		manager = new CommunityUserManager();
		manager.setListener(this);
		manager.customerID = this.getIntent().getIntExtra("USERID", 0);
		shopDefaultImg = BitmapFactory.decodeResource(getResources(), R.drawable.commutity_user_icon_d);
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

	public class MyBroadCast extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("massvig.ecommerce.message")){
				int count = manager.GetAllUnread(CommunityUserInfoActivity.this, app.user.EJID);
				message_number.setVisibility(View.INVISIBLE);
				if(count > 0){
					message_number.setText(count + "");
					message_number.setVisibility(View.VISIBLE);
				}
			}
		}
		
	}

	private void initView(UserInfo user) {
		username.setText(user.NickName);
		userimg.setImageUrl(MassVigUtil.GetImageUrl(user.HeadImgUrl, 80, 80), MassVigContants.PATH, shopDefaultImg);
		if(user.Gender == 0){
			sex.setVisibility(View.GONE);
		}else{
//			sex.setVisibility(View.VISIBLE);
			sex.setBackgroundResource(user.Gender == 1 ? R.drawable.sex_m : R.drawable.sex_w);
		}
		attentionBtn.setText(getString(R.string.attention, user.FollowingCustomerCount + ""));
		fansBtn.setText(getString(R.string.fans, user.FansCustomerCount + ""));
		actionBtn.setText(getString(R.string.action, user.SharePraisedCount + ""));
		((RadioButton) findViewById(R.id.left_tab_radio))
				.setText(getString(R.string.praise_shop) + user.PraiseCount);
		((RadioButton) findViewById(R.id.right_tab_radio))
				.setText(getString(R.string.love_shop) + user.SharedCount);
		if(manager.customerID != app.user.customerID){
			if(user.Relation == UNFOLLOW){
				add_fans.setVisibility(View.VISIBLE);
				remove_fans.setVisibility(View.GONE);
			}else if(user.Relation == FOLLOWING){
				add_fans.setVisibility(View.GONE);
				remove_fans.setVisibility(View.VISIBLE);
			}else{
				add_fans.setVisibility(View.INVISIBLE);
				remove_fans.setVisibility(View.INVISIBLE);
			}
		}
	}

	public void initView() {
		myself = (LinearLayout)findViewById(R.id.myself);
		others = (LinearLayout)findViewById(R.id.others);
		if(manager.customerID == app.user.customerID){
			others.setVisibility(View.GONE);
			myself.setVisibility(View.VISIBLE);
			manager.GetAllUnread(this, app.user.EJID);
		}else{
			others.setVisibility(View.VISIBLE);
			myself.setVisibility(View.GONE);
		}
		chat = (RelativeLayout)findViewById(R.id.chat);
		add_fans = (RelativeLayout)findViewById(R.id.add_fans);
		remove_fans = (RelativeLayout)findViewById(R.id.remove_fans);
		scan_message = (RelativeLayout)findViewById(R.id.scan_message);
		ignor_message = (RelativeLayout)findViewById(R.id.igonre_message);
		message_number = (TextView)findViewById(R.id.message);
		username = (TextView)findViewById(R.id.user_info_name);
		((TextView)findViewById(R.id.back)).setOnClickListener(this);
		userimg = (NetImageView)findViewById(R.id.user_info_icon);
		sex = (ImageView)findViewById(R.id.sex);
		attentionBtn = (Button)findViewById(R.id.user_info_attention_txt);
		fansBtn = (Button)findViewById(R.id.user_info_fans_txt);
		actionBtn = (Button)findViewById(R.id.user_info_action_txt);
		attentionBtn.setOnClickListener(this);
		fansBtn.setOnClickListener(this);
		actionBtn.setOnClickListener(this);
		scan_message.setOnClickListener(this);
		ignor_message.setOnClickListener(this);
		myself.setOnClickListener(this);
		others.setOnClickListener(this);
		chat.setOnClickListener(this);
		add_fans.setOnClickListener(this);
		remove_fans.setOnClickListener(this);
//		mOnclick = new MyOnclick();
		LoadData();
		initTabHost();
	}

	@Override
	protected void onResume() {
		super.onResume();
		app = (BaseApplication) getApplication();
		app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(this, "SESSIONID", "") : app.user.sessionID;
		if(!TextUtils.isEmpty(app.user.sessionID)){
			int count = manager.GetAllUnread(this, app.user.EJID);
			message_number.setVisibility(View.INVISIBLE);
			if(count > 0){
				message_number.setText(count + "");
				message_number.setVisibility(View.VISIBLE);
			}
		}else{
			message_number.setVisibility(View.INVISIBLE);
		}
	}

	public void LoadData() {
		manager.LoadData();
	}

	
	/**
	 * 初始化 tabHost
	 * 
	 * @author zhangbp
	 */

	public void initTabHost() {

		tabHost = (TabHost) findViewById(R.id.tabhost);
		tabHost.setup(this.getLocalActivityManager());
		tabWidget = (RadioGroup) findViewById(R.id.tab_radio);

		tabWidget.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == R.id.left_tab_radio) {
					tabHost.setCurrentTab(1);
				} else if (checkedId == R.id.right_tab_radio) {
					tabHost.setCurrentTab(0);
				}

			}
		});

		Intent intent1 = new Intent(this, LoveShopActivity.class)
				.putExtra("type", LoveShopActivity.SHARE).putExtra("USERID", manager.customerID)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Intent intent2 = new Intent(this, LoveShopActivity.class)
				.putExtra("type", LoveShopActivity.PRAISE).putExtra("USERID", manager.customerID)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("tab11111111")
				.setContent(intent1));
		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("tab222222")
				.setContent(intent2));

	}

	@Override
	public void Success(int index) {
		switch (index) {
		case CommunityUserManager.LOADDATA:
			break;
		case CommunityUserManager.FANS:
			if(manager.userinfo.Relation == UNFOLLOW){
				add_fans.setVisibility(View.VISIBLE);
				remove_fans.setVisibility(View.GONE);
			}else if(manager.userinfo.Relation == FOLLOWING){
				add_fans.setVisibility(View.GONE);
				remove_fans.setVisibility(View.VISIBLE);
			}else{
				add_fans.setVisibility(View.INVISIBLE);
				remove_fans.setVisibility(View.INVISIBLE);
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void Failed(int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Dialog(boolean toShow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void SessionFailed() {
//		if(!TextUtils.isEmpty(app.user.sessionID + MassVigUtil.getPreferenceData(this, "SESSIONID", "")))
			startActivity(new Intent(this, LoginActivity.class));
		app.user.sessionID = "";
		MassVigUtil.setPreferenceStringData(this, "SESSIONID", "");
	}

	@Override
	public void LoadData(UserInfo user) {
		Message msg = new Message();
		msg.what = 1;
		msg.obj = user;
		mHandler.sendMessage(msg);
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			initView((UserInfo)msg.obj);
		}};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_fans:
			if(TextUtils.isEmpty(app.user.sessionID + MassVigUtil.getPreferenceData(this, "SESSIONID", "")))
				startActivity(new Intent(this, LoginActivity.class));
			else
				manager.AddAndRemoveFans(1, app.user.sessionID, manager.customerID);
			break;
		case R.id.remove_fans:
			if(TextUtils.isEmpty(app.user.sessionID + MassVigUtil.getPreferenceData(this, "SESSIONID", "")))
				startActivity(new Intent(this, LoginActivity.class));
			else
				manager.AddAndRemoveFans(0, app.user.sessionID, manager.customerID);
			break;
		case R.id.chat:
			if (!TextUtils.isEmpty(app.user.sessionID)) {
				Chater c = new Chater();
				UserInfo user = manager.userinfo;
				c.EJID = user.EJID;
				c.EJResource = user.EJResource;
				c.CustomerID = user.CustomerID;
				c.gender = user.Gender;
				c.headimg = user.HeadImgUrl;
				c.nickname = user.NickName;
				startActivity(new Intent(this, ChatActivity.class).putExtra("CHATER", c));
			}else{
				app.user.sessionID = "";
				MassVigUtil.setPreferenceStringData(this, "SESSIONID", "");
				startActivity(new Intent(this, LoginActivity.class));
			}
			break;
		case R.id.scan_message:
			startActivity(new Intent(this, MessagesActivity.class));
			break;
		case R.id.igonre_message:
			message_number.setText("0");
			message_number.setVisibility(View.INVISIBLE);
			manager.SetAllRead(CommunityUserInfoActivity.this);
			break;
		case R.id.back:
			CommunityUserInfoActivity.this.finish();
			break;
		case R.id.user_info_attention_txt:
			startActivity(new Intent().setClass(CommunityUserInfoActivity.this,FriendTabActivity.class)
							.putExtra("_type", 0)
							.putExtra("_userId", manager.customerID)
							.putExtra("index", 0));
			break;
		case R.id.user_info_fans_txt:
			startActivity(new Intent().setClass(CommunityUserInfoActivity.this,FriendTabActivity.class)
					.putExtra("_type", 1)
					.putExtra("_userId", manager.customerID)
					.putExtra("index", 1));
			break;
		case R.id.user_info_action_txt:
			startActivity(new Intent(CommunityUserInfoActivity.this, PraisedActivity.class).putExtra("userID", manager.customerID));
			break;

		default:
			break;
		}
	}

	@Override
	public void Already() {
		if(add_fans.isShown()){
			add_fans.setVisibility(View.GONE);
			remove_fans.setVisibility(View.VISIBLE);
		}else{
			add_fans.setVisibility(View.VISIBLE);
			remove_fans.setVisibility(View.GONE);
		}
	}

}
