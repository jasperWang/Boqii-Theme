package com.massvig.ecommerce.logic.notification;

import java.util.Random;

import com.massvig.ecommerce.activities.BaseApplication;
import com.massvig.ecommerce.activities.MessagesActivity;
import com.massvig.ecommerce.boqi.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * push notification
 * @author DuJun
 *
 */
public class MassVigNotificationManager{
	private Notification m_Notification;
	private NotificationManager m_NotificationManager;
	public Context context;
	
	public MassVigNotificationManager(Context context){
		this.context=context;
		m_NotificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		m_Notification=new Notification();
		m_Notification.flags = Notification.FLAG_AUTO_CANCEL;
		m_Notification.defaults = Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS;
		m_Notification.icon=R.drawable.ic_launcher;
	}
	
	/**
	 * 根据通知类型不同发送不同通知
	 * @param key
	 * @param message
	 */
	public void notify(String title, String message){
		if (!BaseApplication.isChating) {
			PendingIntent contentIntent = null;
			m_Notification.tickerText = message;
			contentIntent = PendingIntent.getActivity(
					context,
					0,
					new Intent(context, MessagesActivity.class).putExtra(
							"message", message).setFlags(
							Intent.FLAG_ACTIVITY_CLEAR_TOP),
					Notification.FLAG_AUTO_CANCEL);
			m_Notification.setLatestEventInfo(context, title, message,
					contentIntent);
			m_NotificationManager.notify(0, m_Notification);
		}
	}
	
}
