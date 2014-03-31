package com.massvig.ecommerce.activities;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.Chater;
import com.massvig.ecommerce.managers.MessageManager;
import com.massvig.ecommerce.managers.MessageManager.LoadListener;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.widgets.MessageAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

public class MessagesActivity extends BaseActivity implements OnClickListener,LoadListener {

	private ListView listview;
	private MessageAdapter adapter;
	private MessageManager manager;
	private BaseApplication app;
	public static final int CLICK = 1;
	private LinearLayout nodata;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message);
		setTitle(getString(R.string.messages));
		app = (BaseApplication) getApplication();
		((Button)findViewById(R.id.back)).setOnClickListener(this);
		listview = (ListView)findViewById(R.id.listview);
		manager = new MessageManager(this);
		nodata = (LinearLayout)findViewById(R.id.nodata);
		nodata.setVisibility(View.GONE);
		((Button)findViewById(R.id.gotobtn)).setOnClickListener(this);
		manager.setListener(this);
		adapter = new MessageAdapter(this, manager.chatList, mHandler);
		listview.setAdapter(adapter);
		if(this.getIntent().getIntExtra("COMMUNITY", 0) == 1){
			((LinearLayout)findViewById(R.id.layout)).setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		manager.GetAllChatList(app.user.EJID);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.gotobtn:
			startActivity(new Intent(this, MainTabActivity.class).putExtra(
					"INDEX", 3).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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
			case CLICK:
				Chater c = (Chater) msg.obj;
				startActivity(new Intent(MessagesActivity.this, ChatActivity.class).putExtra("CHATER", c));
				break;

			default:
				break;
			}
		}};
	
	@Override
	public void LoadSuccess(int index) {
		switch (index) {
		case MessageManager.ALLCHAT:
			adapter.notifyDataSetChanged();	
			if(adapter.getCount() > 0){
				nodata.setVisibility(View.GONE);
			}else{
				nodata.setVisibility(View.VISIBLE);
			}
			app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(this, "SESSIONID", "") : app.user.sessionID;
			manager.GetMessagePageCustomerInfo(app.user.sessionID, 0, 0);
			break;
		case MessageManager.LOADUSER:
			adapter.notifyDataSetChanged();	
			break;

		default:
			break;
		}
		
	}

	@Override
	public void LoadFailed(int index) {
		if(index == MessageManager.ALLCHAT){
//			Toast.makeText(this, getString(R.string.no_message), Toast.LENGTH_LONG).show();
		}
		nodata.setVisibility(View.VISIBLE);
	}

	@Override
	public void SessionVailed() {
//		if(!TextUtils.isEmpty(app.user.sessionID + MassVigUtil.getPreferenceData(this, "SESSIONID", "")))
			startActivity(new Intent(this, LoginActivity.class));
		app.user.sessionID = "";
		MassVigUtil.setPreferenceStringData(this, "SESSIONID", "");
		
	}

}
