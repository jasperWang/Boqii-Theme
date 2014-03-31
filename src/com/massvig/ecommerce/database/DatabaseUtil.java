package com.massvig.ecommerce.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.massvig.ecommerce.entities.Item;
import com.massvig.ecommerce.entities.Message;

public class DatabaseUtil {
	
	public final static int time = 3 * 60 * 1000;
	public long timestamp;//上条消息时间
	public long lastTimestamp;//上上条消息时间
	private static DatabaseUtil instance = new DatabaseUtil();

	public static DatabaseUtil getInstance(){
		if(instance == null)
			instance = new DatabaseUtil();
		return instance;
	}
	
	private int getIntFromCursor(Cursor cursor, String strColumn) {
		int nRet = -1;
		int idx = cursor.getColumnIndex(strColumn);
		if (idx >= 0) {
			nRet = cursor.getInt(idx);
		}
		return nRet;
	}
	
	private String getStringFromCursor(Cursor cursor, String strColumn) {
		String strRet = null;
		int idx = cursor.getColumnIndex(strColumn);
		if (idx >= 0) {
			strRet = cursor.getString(idx);
		}
		return strRet;
	}
	
	private Message GetOneMessageFromCursor(Cursor cursor){
		Message message = new Message();
		message.CreateTime = getStringFromCursor(cursor, DatabaseHelper.CreateTime);
		message.FromEJID = getStringFromCursor(cursor, DatabaseHelper.FromEJID);
		message.IsRead = getIntFromCursor(cursor, DatabaseHelper.IsRead);
		message.IsShowTime = getIntFromCursor(cursor, DatabaseHelper.IsShowTime);
		message.MessageBody = getStringFromCursor(cursor, DatabaseHelper.MessageBody);
		message.ToEJID = getStringFromCursor(cursor, DatabaseHelper.ToEJID);
		return message;
	}
	
	/**
	 * 获取未读消息数量
	 * @param database
	 * @param ToEJID
	 * @return
	 */
	public int GetAllUnReadCount(SQLiteDatabase database, String ToEJID){
		int result = -1;
		try {
			if(database.isOpen()){
				String UnReadCount = "UnReadCount";
				String unreadCount = "SELECT COUNT(1) AS " + UnReadCount + " FROM " + DatabaseHelper.name + " WHERE "+ DatabaseHelper.ToEJID + "='" + ToEJID + "' AND " + DatabaseHelper.IsRead + "=0";
				Cursor cursor = database.rawQuery(unreadCount, null);
				if (cursor.moveToFirst()) {
					result = getIntFromCursor(cursor, UnReadCount);
					cursor.close();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 获取和某人的未读消息数量
	 * @param database
	 * @param ToEJID
	 * @return
	 */
	public int GetOneUnReadCount(SQLiteDatabase database, String FromEJID, String ToEJID){
		int result = -1;
		try {
			if(database.isOpen()){
				String UnReadCount = "UnReadCount";
				String unreadCount = "SELECT COUNT(1) AS " + UnReadCount + " FROM " + DatabaseHelper.name + " WHERE "+ DatabaseHelper.ToEJID + "='" + ToEJID + "' AND " + DatabaseHelper.FromEJID + "='" + FromEJID + "' AND " + DatabaseHelper.IsRead + "=0";
				Cursor cursor = database.rawQuery(unreadCount, null);
				if (cursor.moveToFirst()) {
					result = getIntFromCursor(cursor, UnReadCount);
					cursor.close();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return result;
	}
	
//	public static HashMap<String, Integer> GetOneUnReadMessage(SQLiteDatabase database, String tableName, String ToEJID){
//		HashMap<String, Integer> map = new HashMap<String, Integer>();
//		try {
//			if(database.isOpen()){
//				String UnReadCount = "UnReadCount";
//				String unreadCount = "SELECT " + DatabaseHelper.FromEJID + ",COUNT(1) AS " + UnReadCount + " FROM " + tableName + " WHERE "+ DatabaseHelper.ToEJID + "=" + ToEJID + " AND " + DatabaseHelper.IsRead + "=0 GROUP BY " + DatabaseHelper.FromEJID;
//				Cursor cursor = database.rawQuery(unreadCount, null);
//				int count = cursor.getCount();
//				if (cursor.moveToFirst()) {
//					int i = 0;
//					do {
//						if (i < count) {
//							String ejid = getStringFromCursor(cursor,
//									DatabaseHelper.FromEJID);
//							int Count = getIntFromCursor(cursor, UnReadCount);
//							map.put(ejid, Count);
//							i++;
//						} else
//							break;
//					} while (cursor.moveToNext());
//					cursor.close();
//				}
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//		return map;
//	}

	/**
	 * 获取和每个人的未读消息
	 * @param database
	 * @param ToEJID
	 * @return
	 */
	public ArrayList<Item> GetOneUnReadMessage(SQLiteDatabase database, String ToEJID){
		ArrayList<Item> items = new ArrayList<Item>();
		try {
			if(database.isOpen()){
				String UnReadCount = "UnReadCount";
				String unreadCount = "SELECT " + DatabaseHelper.FromEJID + ",COUNT(1) AS " + UnReadCount + " FROM " + DatabaseHelper.name + " WHERE "+ DatabaseHelper.ToEJID + "='" + ToEJID + "' AND " + DatabaseHelper.IsRead + "=0 GROUP BY " + DatabaseHelper.FromEJID;
				Cursor cursor = database.rawQuery(unreadCount, null);
				int count = cursor.getCount();
				if (cursor.moveToFirst()) {
					int i = 0;
					do {
						if (i < count) {
							String ejid = getStringFromCursor(cursor,
									DatabaseHelper.FromEJID);
							int Count = getIntFromCursor(cursor, UnReadCount);
							Item it = new Item();
							it.EJID = ejid;
							it.unread = Count;
							items.add(it);
							i++;
						} else
							break;
					} while (cursor.moveToNext());
					cursor.close();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return items;
	}

	/**
	 * 获取和每个人的消息
	 * @param database
	 * @param ToEJID
	 * @return
	 */
	public ArrayList<Item> GetOneMessage(SQLiteDatabase database, String ToEJID){
		ArrayList<Item> items = new ArrayList<Item>();
		try {
			if(database.isOpen()){
				String UnReadCount = "UnReadCount";
				String unreadCount = "SELECT " + DatabaseHelper.FromEJID + ",COUNT(1) AS " + UnReadCount + " FROM " + DatabaseHelper.name + " WHERE "+ DatabaseHelper.ToEJID + "='" + ToEJID + "' GROUP BY " + DatabaseHelper.FromEJID;
				Cursor cursor = database.rawQuery(unreadCount, null);
				int count = cursor.getCount();
				if (cursor.moveToFirst()) {
					int i = 0;
					do {
						if (i < count) {
							String ejid = getStringFromCursor(cursor,
									DatabaseHelper.FromEJID);
							int Count = getIntFromCursor(cursor, UnReadCount);
							Item it = new Item();
							it.EJID = ejid;
							it.unread = Count;
							items.add(it);
							i++;
						} else
							break;
					} while (cursor.moveToNext());
					cursor.close();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return items;
	}
	
	/**
	 * 查找和某人的会话记录
	 * @param database
	 * @param ToEJID
	 * @param FromEJID
	 * @return
	 */
	public ArrayList<Message> GetChat(SQLiteDatabase database, String FromEJID, String ToEJID){
		ArrayList<Message> messages = new ArrayList<Message>();
		try {
			if(database.isOpen()){
//				String chatHistory = "SELECT EJID,MAX(LASTUPDATETIME) AS LASTUPDATETIME,MIN("+DatabaseHelper.IsRead+") AS ISREAD FROM (SELECT "+DatabaseHelper.FromEJID+" AS EJID,MAX("+DatabaseHelper.CreateTime+") AS LASTUPDATETIME,MIN("+DatabaseHelper.IsRead+") AS "+DatabaseHelper.IsRead+" FROM "+DatabaseHelper.name+" WHERE "+DatabaseHelper.ToEJID+"="+ToEJID+" GROUP BY "+DatabaseHelper.FromEJID+" UNION SELECT "+DatabaseHelper.ToEJID+" AS EJID,MAX("+DatabaseHelper.CreateTime+") AS LASTUPDATETIME,MIN("+DatabaseHelper.IsRead+") AS ISREAD FROM "+DatabaseHelper.name+" WHERE "+DatabaseHelper.FromEJID+"="+ FromEJID +" GROUP BY "+DatabaseHelper.ToEJID+" ) AS T1 GROUP BY EJID ORDER BY ISREAD,LASTUPDATETIME DESC";
				String chatHistory = "SELECT * FROM "+DatabaseHelper.name+" WHERE "+DatabaseHelper.FromEJID+" IN('"+ToEJID+"','"+FromEJID+"') AND "+DatabaseHelper.ToEJID+" IN('"+ToEJID+"','"+FromEJID+"') ORDER BY "+DatabaseHelper.CreateTime+" ASC";
				Cursor cursor = database.rawQuery(chatHistory, null);
				int count = cursor.getCount();
				if (cursor.moveToFirst()) {
					int i = 0;
					do {
						if (i < count) {
							Message m = GetOneMessageFromCursor(cursor);
							m.IsComMsg = m.FromEJID.equals(FromEJID) ? true : false;
							messages.add(m);
							i++;
						} else
							break;
					} while (cursor.moveToNext());
					cursor.close();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return messages;
	}
	
	/**
	 * 更新某人的未读消息为已读
	 * @param database
	 * @param FromEJID
	 * @param ToEJID
	 */
	public void UpdateUnreadMessage(SQLiteDatabase database, String FromEJID, String ToEJID){
		try {
			if (database.isOpen()) {
//				ContentValues values = new ContentValues();
//				values.put(DatabaseHelper.IsRead, 0);
//				database.update(DatabaseHelper.name, values, null, null);
				
				String update = "UPDATE " + DatabaseHelper.name + " SET " + DatabaseHelper.IsRead + "=1 WHERE " + DatabaseHelper.FromEJID + "='" + FromEJID + "' AND " + DatabaseHelper.ToEJID + "='" + ToEJID + "'";
				database.execSQL(update);
			}	
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 更新所有未读消息为已读
	 * @param database
	 * @param FromEJID
	 * @param ToEJID
	 */
	public void UpdateAllUnreadMessage(SQLiteDatabase database){
		try {
			if (database.isOpen()) {
				String update = "UPDATE " + DatabaseHelper.name + " SET " + DatabaseHelper.IsRead + "=1";
				database.execSQL(update);
			}	
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 写入消息记录
	 * @param database
	 * @param FromEJID
	 * @param ToEJID
	 * @param MessageBody
	 * @param CreateTime
	 * @param IsRead
	 */
	public void InsertMessage(SQLiteDatabase database, String FromEJID, String ToEJID, String MessageBody, String CreateTime, int IsRead){
		try {
			if(database.isOpen()){
				ContentValues values = new ContentValues();
				values.put(DatabaseHelper.FromEJID, FromEJID);
				values.put(DatabaseHelper.ToEJID, ToEJID);
				values.put(DatabaseHelper.MessageBody, MessageBody);
				values.put(DatabaseHelper.CreateTime, CreateTime);
				values.put(DatabaseHelper.IsRead, IsRead);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = (Date) sdf.parse(CreateTime);
				long timetemp = date.getTime();
				values.put(DatabaseHelper.IsShowTime, ((timetemp - timestamp) > time) ? "1" : "0");
				database.insert(DatabaseHelper.name, null, values);
				lastTimestamp = timestamp;
				timestamp = timetemp;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
