package com.massvig.ecommerce.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String CREATE_TABLT = "CREATE TABLE IF NOT EXISTS ";
	public static final String FromEJID = "FromEJID";//发送方id
	public static final String ToEJID = "ToEJID";//接收方id
	public static final String MessageBody = "MessageBody";//消息内容
	public static final String CreateTime = "CreateTime";//发送时间
	public static final String IsRead = "IsRead";//是否已读 0：未读 1：已读
	public static final String IsShowTime = "IsShowTime";//是否显示消息时间 0：不显示 1：显示
	public static final String name = "MESSAGE";
	
	public DatabaseHelper(Context context, CursorFactory factory,
			int version) {
		super(context, name, factory, 2);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL(CREATE_TABLT + name 
					+ "("
					+ FromEJID + " TEXT, "
					+ ToEJID + " TEXT, "
					+ MessageBody + " TEXT, "
					+ CreateTime + " TEXT, "
					+ IsShowTime + " INTEGER DEFAULT 0, "
					+ IsRead + " INTEGER DEFAULT 0)");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + name);

			db.execSQL(CREATE_TABLT + name 
					+ "("
					+ FromEJID + " TEXT, "
					+ ToEJID + " TEXT, "
					+ MessageBody + " TEXT, "
					+ CreateTime + " TEXT, "
					+ IsShowTime + " INTEGER DEFAULT 0, "
					+ IsRead + " INTEGER DEFAULT 0)");
		}
	}

}
