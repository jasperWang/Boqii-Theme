package com.massvig.ecommerce.service;


import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Presence;
import org.json.JSONObject;

import com.massvig.ecommerce.activities.BaseApplication;
import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.database.DatabaseHelper;
import com.massvig.ecommerce.database.DatabaseUtil;
import com.massvig.ecommerce.logic.notification.MassVigNotificationManager;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.utilities.MassvigLogger;
import com.massvig.ecommerce.utilities.TimeRender;
import com.massvig.ecommerce.utilities.XmppTool;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils;
import android.util.Log;

public class EcommercePushService extends Service {
	private static final long DELAY = 60 * 1000;
	private EcommerceNetworkBroadcastReceiver receiver = null;
	private boolean isFirstStart = true;//第一次启动service的时候同时系统会广播网络状态改变，所以加个flag判断一下
	private final static String ACTION = "com.massvig.broadcast";
	private MyBroadcast receive = null;
	private WakeLock mWakeLock = null;
	private BaseApplication app;
	private MyTask mTask = null;
	@Override
	public void onCreate() {
		super.onCreate();
		acquireWakeLock();
		app = (BaseApplication) getApplication();
		receive = new MyBroadcast();
		receiver = new EcommerceNetworkBroadcastReceiver(new EcommerceNetworkBroadcastReceiver.NetworkListener(){

			@Override
			public void available() {
            	if(isFirstStart){
            		isFirstStart = false;
            		return;
            	}
				restart();
            }

			@Override
			public void notAvailable() {
				DisConnectToServer();
			}

			@Override
			public void bootComplete() {
				restart();
			}
			
		});
		isFirstStart = true;
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		mFilter.addAction(Intent.ACTION_TIME_TICK);
		registerReceiver(receiver, mFilter);
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION);
	    registerReceiver(receive, intentFilter);
		
	    restartConnectToServer();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		releaseWakeLock();
		DisConnectToServer();
		unregisterReceiver(receive);
		unregisterReceiver(receiver);
//		sendBroadcast(new Intent(ACTION));
//		unregisterReceiver(receive);
	}

	private void DisConnectToServer() {
		try {
			XmppTool.closeConnection();
		} catch (Exception e) {
		}
	}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
//	private Handler mHandler = new Handler(){
//
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			Toast.makeText(EcommercePushService.this, String.valueOf(msg.obj), Toast.LENGTH_SHORT).show();
//		}};

	/**
	 * 重新联网或者连接失败后重新尝试连接
	 */
	private void restartConnectToServer(){

		new Thread(new Runnable() {				
			@Override
			public void run() {
				try {
					String result = MassVigService.getInstance().VerifySession(app.user.sessionID);
					JSONObject o = new JSONObject(result);
					if(o.optInt("ResponseStatus") == 0){
					ChatManager cm = XmppTool.getConnection(app.user.EJServerIP, Integer.valueOf(app.user.EJServerPort), app.user.EJServerDomaim).getChatManager();
					cm.addChatListener(new ChatManagerListener() {
						@Override
						public void chatCreated(Chat chat, boolean able) {
							chat.addMessageListener(new MessageListener() {

								@Override
								public void processMessage(Chat arg0,org.jivesoftware.smack.packet.Message message) {
									//TODO 处理接收消息
									DatabaseHelper helper = new DatabaseHelper(EcommercePushService.this, null, 1);
									SQLiteDatabase database = helper.getWritableDatabase();
									String from = message.getFrom();
									int index = from.indexOf("@"); 
									String fromEJID = message.getFrom().substring(0, index);
									DatabaseUtil.getInstance().InsertMessage(database, fromEJID, app.user.EJID, message.getBody(), TimeRender.getDate(), 0);
									database.close();
									EcommercePushService.this.sendBroadcast(new Intent("massvig.ecommerce.message").putExtra("FROM", message.getFrom()).putExtra("BODY", message.getBody()).putExtra("TEMP", DatabaseUtil.getInstance().lastTimestamp));

									MassVigNotificationManager nm = new MassVigNotificationManager(
											getBaseContext());
									nm.notify(getString(R.string.receive), getString(R.string.receive_message));
								}
							});
						}
					});
					try {
						if(XmppTool.getConnection(app.user.EJServerIP, Integer.valueOf(app.user.EJServerPort), app.user.EJServerDomaim) != null && (!TextUtils.isEmpty(app.user.sessionID) || !TextUtils.isEmpty(MassVigUtil.getPreferenceData(EcommercePushService.this, "SESSIONID", ""))))
						{
							XmppTool.getConnection(app.user.EJServerIP, Integer.valueOf(app.user.EJServerPort), app.user.EJServerDomaim).login(app.user.EJID, app.user.EJPassword, app.user.EJResource);
						}
							
						Log.i("XMPPClient", "Logged in as " + XmppTool.getConnection(app.user.EJServerIP, Integer.valueOf(app.user.EJServerPort), app.user.EJServerDomaim).getUser());
						// status
						Presence presence = new Presence(Presence.Type.available);
						XmppTool.getConnection(app.user.EJServerIP, Integer.valueOf(app.user.EJServerPort), app.user.EJServerDomaim).sendPacket(presence);						
					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
						String s = e.getMessage();
						if(s.contains("Already logged in to server")){
							return;
						}
						restart();
					}
					}
				} catch (Exception e) {
				}					
			}
		}).start();
		
	}

	class MyBroadcast extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.equals(ACTION)){
				Timer time = new Timer();
				TimerTask task = new TimerTask() {
					@Override
					public void run() {
						restart();
					}
				};
				time.schedule(task, DELAY);
			}
		}
		
	}

	Timer timer = new Timer();
	class MyTask extends TimerTask{

		@Override
		public void run() {
			reStart();
		}
		
	}
	
	private void restart(){
		if(timer != null){
			if(mTask != null){
				mTask.cancel();
			}
			mTask = new MyTask();
			timer.schedule(mTask, DELAY);
		}else{
			MassvigLogger.getInstance().v("dujun", "timer is null");
		}
	}
	
	private void reStart(){
		DisConnectToServer();
		restartConnectToServer();
//		Intent intent = new Intent();
//		intent.setClass(EcommercePushService.this, EcommercePushService.class);
//		EcommercePushService.this.startService(intent);
	}
	
	/**
	 * 申请设备电源锁
	 */
	private void acquireWakeLock()
    {
        if (null == mWakeLock)
        {
            PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE,"");
            if (null != mWakeLock)
            {
                mWakeLock.acquire();
            }
        }
    }

	/**
	 * 释放设备电源锁
	 */
    private void releaseWakeLock()
    {
        if (null != mWakeLock)
        {
            mWakeLock.release();
            mWakeLock = null;
        }
    }
}
