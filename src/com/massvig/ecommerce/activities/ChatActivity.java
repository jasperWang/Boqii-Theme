package com.massvig.ecommerce.activities;

import java.util.Date;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.database.DatabaseHelper;
import com.massvig.ecommerce.database.DatabaseUtil;
import com.massvig.ecommerce.entities.Chater;
import com.massvig.ecommerce.managers.ChatManager;
import com.massvig.ecommerce.service.EcommercePushService;
import com.massvig.ecommerce.service.MassVigService;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.utilities.TimeRender;
import com.massvig.ecommerce.utilities.XmppTool;
import com.massvig.ecommerce.widgets.AsyncImageLoader;
import com.massvig.ecommerce.widgets.ChatAdapter;
import com.massvig.ecommerce.widgets.AsyncImageLoader.ImageCallback;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class ChatActivity extends BaseActivity implements OnClickListener{

	private ListView listView;
	private ChatManager manager;
	private ChatAdapter adapter;
	private Bitmap fromImg, toImg;
	private String fromEJID = "",toEJID = "";
	private BaseApplication app;
	private EditText content_edit;
//	private List<Msg> listMsg = new ArrayList<Msg>();
	private Chat newchat;
	private MyBroadCast receiver = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_activity);
		setTitle(getString(R.string.chat));
		app = (BaseApplication) getApplication();
		manager = new ChatManager();
		Chater c = (Chater) this.getIntent().getSerializableExtra("CHATER");
		manager.customerID = c.CustomerID;
		fromEJID = c.EJID;
		toEJID = app.user.EJID;
		String fromurl = "", tourl = "";
		fromurl = c.headimg;
		tourl = app.user.headImage;
		if(!TextUtils.isEmpty(fromurl)){
			fromImg = AsyncImageLoader.loadBitmap(MassVigUtil.GetImageUrl(fromurl, 60, 60),new ImageCallback() {
					@Override
					public void imageLoaded(Bitmap imageDrawable,String imageUrl) {
						fromImg = imageDrawable;
						if(fromImg != null && toImg != null){
							adapter.notifyDataSetChanged();
						}
					}
				});
		}else{
			new AsyncTask<String, Void, String>(){
				
				@Override
				protected void onPostExecute(String result) {
					super.onPostExecute(result);
					if(!TextUtils.isEmpty(result)){
						String url = "";
						try {
							JSONObject o = new JSONObject(result);
							JSONArray array = o.getJSONArray("ResponseData");
							for (int i = 0; i < array.length(); i++) {
								JSONObject data = array.getJSONObject(i);
								url = data.getString("HeadImgUrl");
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						fromImg = AsyncImageLoader.loadBitmap(MassVigUtil.GetImageUrl(url, 60, 60),new ImageCallback() {
							@Override
							public void imageLoaded(Bitmap imageDrawable,String imageUrl) {
								fromImg = imageDrawable;
								if(fromImg != null && toImg != null){
									adapter.notifyDataSetChanged();
								}
							}
						});
					}
				}

				@Override
				protected String doInBackground(String... params) {
					String result = MassVigService.getInstance().GetMessagePageCustomerInfo(params[0], fromEJID, Double.valueOf(params[1]), Double.valueOf(params[2]));
					return result;
				}}.execute(app.user.sessionID, "0", "0");
		
		}
		if(!TextUtils.isEmpty(tourl))
			toImg = AsyncImageLoader.loadBitmap(MassVigUtil.GetImageUrl(tourl, 60, 60),new ImageCallback() {
					@Override
					public void imageLoaded(Bitmap imageDrawable,String imageUrl) {
						toImg = imageDrawable;
						if(fromImg != null && toImg != null){
							adapter.notifyDataSetChanged();
						}
					}
				});
		initView();
		receiver = new MyBroadCast();
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction("massvig.ecommerce.message");
		registerReceiver(receiver, mFilter);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		BaseApplication.isChating = false;
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
				String from = intent.getStringExtra("FROM");
				String body = intent.getStringExtra("BODY");
				long tamp = intent.getLongExtra("TEMP", 0);
				if (!TextUtils.isEmpty(from) && from.contains(fromEJID + "@es")) {
					com.massvig.ecommerce.entities.Message msg = new com.massvig.ecommerce.entities.Message();
					msg.CreateTime = TimeRender.getDate();
					msg.FromEJID = fromEJID;
					msg.ToEJID = toEJID;
					msg.IsComMsg = true;
					msg.IsRead = 1;
					Date date = new Date();
					long timetemp = date.getTime();
					msg.IsShowTime = ((timetemp - tamp) > DatabaseUtil.time) ? 1 : 0;
					msg.MessageBody = body;
					manager.list.addMessage(msg);
					adapter.notifyDataSetChanged();
					listView.setSelection(adapter.getCount());
					UpdateMessage(msg);
				}
			}
		}
		
	}
	
	private void InsertMessage(com.massvig.ecommerce.entities.Message msg){
		DatabaseHelper helper = new DatabaseHelper(ChatActivity.this, null, 1);
		SQLiteDatabase database = helper.getWritableDatabase();
		DatabaseUtil.getInstance().InsertMessage(database, msg.FromEJID, msg.ToEJID, msg.MessageBody, msg.CreateTime, msg.IsRead);
		database.close();
	}
	
	public void UpdateMessage(com.massvig.ecommerce.entities.Message msg) {
		DatabaseHelper helper = new DatabaseHelper(ChatActivity.this, null, 1);
		SQLiteDatabase database = helper.getWritableDatabase();
		DatabaseUtil.getInstance().UpdateUnreadMessage(database, msg.FromEJID, toEJID);
		database.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		BaseApplication.isChating = true;
		app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(this, "SESSIONID", "") : app.user.sessionID;
		DatabaseHelper helper = new DatabaseHelper(this, null, 1);
		SQLiteDatabase database = helper.getReadableDatabase();
		manager.list.setMessageList(DatabaseUtil.getInstance().GetChat(database, fromEJID, toEJID));
		database.close();
		adapter.notifyDataSetChanged();
		listView.setSelection(manager.list.getCount());
		SQLiteDatabase databa = helper.getWritableDatabase();
		DatabaseUtil.getInstance().UpdateUnreadMessage(databa, fromEJID, toEJID);
		databa.close();
		org.jivesoftware.smack.ChatManager cm = XmppTool.getConnection(app.user.EJServerIP, Integer.valueOf(app.user.EJServerPort), app.user.EJServerDomaim).getChatManager();
		newchat = cm.createChat(fromEJID + "@es", null);
	}

	private void initView() {
		content_edit = (EditText)findViewById(R.id.contentedit);
		content_edit.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_ACTION_SEND
						|| actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
					sendMessage();
				}
				return false;
			}
		});
		listView = (ListView)findViewById(R.id.listview);
		adapter = new ChatAdapter(this, manager.list, fromImg, toImg);
		listView.setAdapter(adapter);
		((Button)findViewById(R.id.back)).setOnClickListener(this);
		((Button)findViewById(R.id.finish)).setOnClickListener(this);
		((Button)findViewById(R.id.sendbtn)).setOnClickListener(this);
	}

	protected void sendMessage() {
		String msg = content_edit.getText().toString();
		if (msg.length() > 0) {
			com.massvig.ecommerce.entities.Message msg1 = new com.massvig.ecommerce.entities.Message();
			try {
				newchat.sendMessage(msg);
			} catch (Exception e) {
				Toast.makeText(ChatActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
				startService();
				msg1.isFailed = true;
			}
			content_edit.setText("");
			msg1.CreateTime = TimeRender.getDate();
			msg1.FromEJID = toEJID;
			msg1.ToEJID = fromEJID;
			msg1.IsComMsg = false;
			msg1.IsRead = 1;
			Date date = new Date();
			long timetemp = date.getTime();
			msg1.IsShowTime = ((timetemp - DatabaseUtil.getInstance().timestamp) > DatabaseUtil.time) ? 1 : 0;
//			DatabaseUtil.getInstance().timestamp = timetemp;
			msg1.MessageBody = msg;
			manager.list.addMessage(msg1);
			adapter.notifyDataSetChanged();
			listView.setSelection(adapter.getCount());
			InsertMessage(msg1);
		}
		
	}

	private void startService() {
//		String serviceName = "com.massvig.ecommerce.service.EcommercePushService";
//		ActivityManager mActiviryManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//		List<ActivityManager.RunningServiceInfo> mServiceList = mActiviryManager
//				.getRunningServices(30);
//		boolean isRunning = MassVigUtil.ServiceIsStart(mServiceList,
//				serviceName);
			stopService(new Intent(ChatActivity.this,
					EcommercePushService.class));
			startService(new Intent(ChatActivity.this,
					EcommercePushService.class));
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.finish:
			startActivity(new Intent(this, CommunityUserInfoActivity.class).putExtra("USERID", manager.customerID));
			break;
		case R.id.sendbtn:
			sendMessage();
			break;

		default:
			break;
		}
	}

}
