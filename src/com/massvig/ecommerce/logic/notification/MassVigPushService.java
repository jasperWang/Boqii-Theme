package com.massvig.ecommerce.logic.notification;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.utilities.MassvigLogger;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class MassVigPushService extends Service {
	private static final String HOST = "117.135.136.135";
//	private static final String HOST = "notification.massvig.com";
	private static final int PORT = 13582;
	private static final long DELAY = 60 * 1000;
	private static final int TIME_OUT = 10 * 60000;
	private MassVigNetworkBroadcastReceiver receiver = null;
//	private SocketChannel client = null;
//	private InetSocketAddress isa = null;
	private Socket socket = null;
	private String message = "";
	private boolean isConnectToServer = false;//是否和服务器建立通信
	private boolean isConnecting = false;//是否正在连接
	private MyTask mTask = null;
	private boolean isFirstStart = true;//第一次启动service的时候同时系统会广播网络状态改变，所以加个flag判断一下
	private final static String ACTION = "com.massvig.broadcast";
	private MyBroadcast receive = null;
	private WakeLock mWakeLock = null;
	@Override
	public void onCreate() {
		super.onCreate();
		acquireWakeLock();
		receive = new MyBroadcast();
		receiver = new MassVigNetworkBroadcastReceiver(new MassVigNetworkBroadcastReceiver.NetworkListener(){

			@Override
			public void available() {
            	if(isFirstStart){
            		MassvigLogger.getInstance().v("dujun", "first use");
            		isFirstStart = false;
            		return;
            	}
            	MassvigLogger.getInstance().v("dujun", "restartConnect");
            	restartConnectToServer();
            }

			@Override
			public void notAvailable() {
				DisConnectToServer();
			}

			@Override
			public void timesChanged() {
				if(!isConnectToServer){
					restartConnectToServer();
				}
			}

			@Override
			public void bootComplete() {
				MassvigLogger.getInstance().v("dujun-1", "boot complete");
				if(!isConnectToServer){
					restartConnectToServer();
				}
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
		
		message = MassVigUtil.id(MassVigPushService.this);
		ConnectToServer();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		releaseWakeLock();
		MassvigLogger.getInstance().v("dujun", "ondestory");
		unregisterReceiver(receiver);
		DisConnectToServer();
		MassvigLogger.getInstance().v("dujun-1", "send broad cast");
		sendBroadcast(new Intent(ACTION));
		unregisterReceiver(receive);
	}

	private void DisConnectToServer() {
		try {
			MassvigLogger.getInstance().v("dujun-1", "disconnect to server");
//			client.close();
			socket.close();
		} catch (Exception e) {
		}
	}

	private void StartServerListener() {
		MassvigLogger.getInstance().v("dujun", "start listener");
		ServerListener a = new ServerListener();
		a.start();
	}


	private void ConnectToServer() {
		if (!isConnecting) {
			ConnectToServer c = new ConnectToServer();
			c.start();
		}
	}
	
	Timer timer = new Timer();
	
	class MyTask extends TimerTask{

		@Override
		public void run() {
			if (!isConnectToServer) {
				MassvigLogger.getInstance().v("dujun", "reconnect");
				DisConnectToServer();
				ConnectToServer();
			}
		}
		
	}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	/**
	 * 测试socket是否还保持链接
	 * @param message2
	 */
	private void SendMessageToServer(String message2) {
		try {
			MassvigLogger.getInstance().v("dujun", "start send message");
//			ByteBuffer bytebuf = ByteBuffer.allocate(1024);
//			String message3 = message2 + "\r\n";
//			bytebuf = ByteBuffer.wrap(message3.getBytes("UTF-8"));
//			client.write(bytebuf);
//			bytebuf.flip();
			String message3 = message2 + "\r\n";
			OutputStream ous = socket.getOutputStream();
			byte[] bytes = message3.getBytes("UTF-8");
			ous.write(bytes);
			ous.flush();
			
		} catch (Exception e) {
			MassvigLogger.getInstance().v("dujun", "send message failed");
			isConnectToServer = false;
		}
	}
	
	/**
	 * 连接服务器
	 * @author DuJun
	 *
	 */
	private class ConnectToServer extends Thread{

		@Override
		public void run() {
			try {
				isConnecting = true;
				MassvigLogger.getInstance().v("dujun", "start to connect to server");			
//				client = SocketChannel.open();
//				isa = new InetSocketAddress(InetAddress.getByName(HOST), PORT);
//				client.configureBlocking(true);
//				client.connect(isa);
				socket = new Socket(InetAddress.getByName(HOST), PORT);
				socket.setSoTimeout(TIME_OUT);
				
				MassvigLogger.getInstance().v("dujun", "connect to server success");
				StartServerListener();
			} catch (Exception e) {
				MassvigLogger.getInstance().v("dujun", "connect to server failed");
				isConnectToServer = false;
				isConnecting = false;
				restartConnectToServer();
			}
		}
		
	}
	
	/**
	 * 监听服务器端push消息
	 */
	private class ServerListener extends Thread{

		@Override
		public void run() {
			try {
				while(true){
					String result = "";
					BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					if (socket.isConnected()) {
						if (!socket.isInputShutdown()) {
							if ((result = in.readLine()) != null) {
								result += "\n";
							} else {
							}
						}
					}
					
//					InputStream ins = socket.getInputStream();
//					byte[] bytes = new byte[ins.available()];
//					ins.read(bytes);
//					String result = new String(bytes, "UTF-8");
					
//					String result = new String(bytes, "UTF-8");
//					ByteBuffer buf = ByteBuffer.allocate(1024);
//					client.read(buf);
//					buf.flip();
//					Charset chatset = Charset.forName("UTF-8");
//					CharsetDecoder decoder = chatset.newDecoder();
//					CharBuffer charBuffer;
//					charBuffer = decoder.decode(buf);
//					String result = charBuffer.toString();
					if(result.length() > 0){
						MassvigLogger.getInstance().v("dujun", "get message:" + result);
						if (result.startsWith("[NOQCO]")) {
							// TODO
							if (result.contains("Authentication")) {
								SendMessageToServer("[NOQCU]" + message);
								MassvigLogger.getInstance().v("dujun", "authernication");
							} else if(result.contains("Authenticated")){
								MassvigLogger.getInstance().v("dujun", "Authorized success");
								isConnecting = false;
								isConnectToServer = true;
							} else if(result.contains("NotAuthorized")){
								MassvigLogger.getInstance().v("dujun", "Authorized failed");
							}else {
								MassVigNotificationManager nm = new MassVigNotificationManager(
										getBaseContext());
//								nm.notify(result);
							}
						}
					}
				}
			} catch (Exception e) {
				isConnectToServer = false;
				MassvigLogger.getInstance().v("dujun", "exception");
				restartConnectToServer();
			}
		}
		
	}
	
	/**
	 * 重新联网或者连接失败后每隔一分钟重新尝试连接
	 */
	private void restartConnectToServer(){
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

	class MyBroadcast extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.equals(ACTION)){
				MassvigLogger.getInstance().v("dujun-1", "receive_broadcast");
				Timer time = new Timer();
				TimerTask task = new TimerTask() {
					@Override
					public void run() {
						MassvigLogger.getInstance().v("dujun-1", "start service");
						Intent intent = new Intent();
						intent.setClass(MassVigPushService.this, MassVigPushService.class);
						MassVigPushService.this.startService(intent);
					}
				};
				MassvigLogger.getInstance().v("dujun-1", "delay task");
				time.schedule(task, DELAY);
			}
		}
		
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
