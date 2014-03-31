package com.massvig.ecommerce.activities;

import java.util.Timer;
import java.util.TimerTask;

import com.google.analytics.tracking.android.EasyTracker;
import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.managers.ActionManager;
import com.massvig.ecommerce.managers.ActionManager.LoadListener;
import com.massvig.ecommerce.utilities.MassVigExit;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.widgets.AsyncImageLoader;
import com.massvig.ecommerce.widgets.AsyncImageLoader.ImageCallback;
import com.massvig.ecommerce.widgets.ContentView;
import com.massvig.ecommerce.widgets.ContentView.ContentViewClickCallBack;
import com.massvig.ecommerce.widgets.ActionAdapter;
import com.massvig.ecommerce.widgets.LeftMenuView;
import com.massvig.ecommerce.widgets.LeftMenuView.LeftMeunCallBack;
import com.massvig.ecommerce.widgets.SlideLayout;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CommunityGroupActivity extends ActivityGroup implements LoadListener {
	private ImageButton showLeftMenuBtn;
	private SlideLayout slideLayout;
	private LeftMenuView leftMenuView;
	private TextView titleTxt;
	private MyViewOnclick myViewOnclick;
	private ContentView contentView;
	private Button rightBtn,findPeo;
	private ImageView userIcon;
	private TextView userName;
	public static final int ADDPOST = 101;
	public static final int ADDACTIVITY = 102;
	private BaseApplication app;
	private MassVigExit exit = new MassVigExit();
	private ActionManager manager;
	private ActionAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		app = (BaseApplication) getApplication();
		app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(this, "SESSIONID", "") : app.user.sessionID;
		app.user.customerID = (app.user.customerID == -1) ? Integer.valueOf(MassVigUtil.getPreferenceData(this, "CUSTOMERID", "-1")) : app.user.customerID ;
		app.user.nickName = TextUtils.isEmpty(app.user.nickName) ? MassVigUtil.getPreferenceData(this, "NICKNAME", "") : app.user.nickName;
		app.user.headImage = TextUtils.isEmpty(app.user.headImage) ? MassVigUtil.getPreferenceData(this, "HEADIMG", "") : app.user.headImage;
		app.user.gender = (app.user.gender == 0) ? Integer.valueOf(MassVigUtil.getPreferenceData(this, "GENDER", "0")) : app.user.gender ;
		myViewOnclick = new MyViewOnclick();
		setContentView(R.layout.community_main_group);
		manager = new ActionManager();
		manager.setListener(this);
		showLeftMenuBtn = (ImageButton) findViewById(R.id.show_community_left_menu);
		slideLayout = (SlideLayout) findViewById(R.id.community_slidelayout);

		leftMenuView = (LeftMenuView) findViewById(R.id.community_left_menu);
		contentView = (ContentView) findViewById(R.id.community_content);
		titleTxt = (TextView) findViewById(R.id.title_text);
		rightBtn = (Button) findViewById(R.id.community_group_right_btn);
		findPeo = (Button)findViewById(R.id.find_peo);
		userIcon = (ImageView) findViewById(R.id.left_meun_user_icon);
		userName = (TextView) findViewById(R.id.left_meun_user_name);
		userName.setOnClickListener(myViewOnclick);
		userIcon.setOnClickListener(myViewOnclick);

		contentView.setContentViewClickCallBack(new ContentViewClickCallBack() {

			@Override
			public void callBack() {
				// TODO Auto-generated method stub
				slideLayout.spreadNav(slideLayout.moveLeft);
			}
		});
		
		leftMenuView.setOnclickCallBack(new MyLeftMeunCallBack());

//		ActionsList list = new ActionsList();
//		for (int i = 0; i < 5; i++) {
//			Action a = new Action();
//			a.actionID = i;
//			a.imgUrl = "";
//			a.detail = "action" + i;
//			a.title = "A" + i;
//			list.addAction(a);
//		}
		adapter = new ActionAdapter(this, manager.actionList);
		leftMenuView.setAdapter(adapter);
		
		showLeftMenuBtn.setOnClickListener(myViewOnclick);
		rightBtn.setOnClickListener(myViewOnclick);
		findPeo.setOnClickListener(myViewOnclick);
		if(!TextUtils.isEmpty(app.user.sessionID)){
			manager.FetchActions(app.user.sessionID);
		}
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				mHandler.sendEmptyMessage(1);
			}
		};
		timer.schedule(task, 300);
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			drawView(2, getString(R.string.community_main));
		}};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initUserInfo();
	}

	private void initUserInfo() {
		userName.setText(app.user.nickName);
		if (!TextUtils.isEmpty(app.user.headImage)&& !TextUtils.isEmpty(app.user.sessionID)) {
			String url = MassVigUtil.GetImageUrl(app.user.headImage, 64, 64);
			setImage(url, userIcon, R.drawable.commutity_user_icon_d);
		}else{
			userIcon.setImageResource(R.drawable.commutity_user_icon_d);
		}
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

	public Intent getIntent(int code) {
		Intent intent = null;
		switch (code) {
		case 1:
			intent = new Intent(this, CommunitySignalActivity.class).putExtra("TAB", 1);
			break;
		case 2:
			intent = new Intent(this, CommunitySignalActivity.class).putExtra("TAB", 0);
			break;
		case 3:
			intent = new Intent(this, NearPersonAcvitity.class);
			break;
		case 4:
			intent = new Intent(this, FriendTabActivity.class).putExtra("_userId", app.user.customerID).putExtra("COMMUNITY", 1);
			break;
		case 5:
			intent = new Intent(this, MessagesActivity.class).putExtra("COMMUNITY", 1);
			break;
		case 6:
			intent = new Intent(this, CommunitySignalActivity.class).putExtra("TAB", 2);
			break;
		}

		return intent;
	}

	public View getView(int code) {
		Intent intent = getIntent(code);
		return getLocalActivityManager().startActivity(String.valueOf(code),
				intent).getDecorView();
	}

	public void drawView(int code, String name) {
		if(code == 1 || code == 2 || code == 6){
			rightBtn.setVisibility(View.VISIBLE);
			findPeo.setVisibility(View.GONE);
		}else{
			rightBtn.setVisibility(View.INVISIBLE);
			if(code == 3){
				findPeo.setVisibility(View.VISIBLE);
			}else{
				findPeo.setVisibility(View.GONE);
			}
		}
		titleTxt.setText(name);
		contentView.removeAllViews();
		contentView.addView(getView(code));
	}

	private void setImage(String url, final ImageView img, int defaultPic) {
		try {
			if (!TextUtils.isEmpty(url)) {
				img.setTag(url);
				Bitmap bitmap = AsyncImageLoader.loadBitmap(url,
						new ImageCallback() {
							@Override
							public void imageLoaded(Bitmap imageDrawable,
									String imageUrl) {
								// TODO Auto-generated method stub
								img.setImageBitmap(imageDrawable);
							}
						});

				if (bitmap != null) {
					img.setImageBitmap(bitmap);
				} else if (defaultPic != 0) {
					img.setImageResource(defaultPic);
				}
			} else if (defaultPic != 0) {
				img.setImageResource(defaultPic);
			}

		} catch (OutOfMemoryError oom) {
			oom.printStackTrace();
		}
	}

	class MyViewOnclick implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v == showLeftMenuBtn) {
				slideLayout.spreadNav(slideLayout.moveLeft);
			} else if (v == rightBtn) {
				startActivity(new Intent(CommunityGroupActivity.this, InsertPost.class).putExtra("FLAG", InsertPost.ORIGIN));
			} else if (v == userIcon || v == userName) {
				if(!TextUtils.isEmpty(app.user.sessionID)){
					slideLayout.spreadNav(slideLayout.moveLeft);
					startActivity(new Intent(CommunityGroupActivity.this,CommunityUserInfoActivity.class).putExtra("USERID", app.user.customerID));
				}
			} else if (v == findPeo){
				startActivity(new Intent(CommunityGroupActivity.this, FindPeopleActivity.class));
			}
		}

	}

	class MyLeftMeunCallBack implements LeftMeunCallBack {

		@Override
		public void callBack(int index, String name) {
			app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(CommunityGroupActivity.this, "SESSIONID", "") : app.user.sessionID;
			if(index != 2 || index != 3){
				if (TextUtils.isEmpty(app.user.sessionID)) {
					app.user.sessionID = "";
					MassVigUtil.setPreferenceStringData(CommunityGroupActivity.this, "SESSIONID", "");
					startActivity(new Intent(CommunityGroupActivity.this,
							LoginActivity.class));
					return;
				} else if (index == 4) {
					startActivity(new Intent()
							.setClass(CommunityGroupActivity.this,
									FriendTabActivity.class)
							.putExtra("_type", 0)
							.putExtra("_userId", app.user.customerID)
							.putExtra("index", 0));
					return;
				}
			}else if(index == 3){
				startActivity(new Intent(CommunityGroupActivity.this, NearPersonAcvitity.class));
				return;
			}
			drawView(index, name);
			slideLayout.spreadNav(slideLayout.moveLeft);
		}

		@Override
		public void listCallBack(int actionID) {
//			slideLayout.spreadNav(slideLayout.moveLeft);
			startActivity(new Intent(CommunityGroupActivity.this, ActionDetailActivity.class).putExtra("CAMPAIGNID", actionID));
		}

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
	 * 
	 * @author zhp
	 * 
	 */
	public interface OnActivityGroupResultListener {
		public void OnActivityGroupResult(int requestCode, int resultCode,
				Intent data);
	}

	@Override
	public void Success(int index) {
		adapter.notifyDataSetChanged();
	}

	@Override
	public void Failed(int index) {
	}

}
