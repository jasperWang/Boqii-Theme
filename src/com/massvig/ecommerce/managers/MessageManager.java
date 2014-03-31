package com.massvig.ecommerce.managers;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.massvig.ecommerce.database.DatabaseHelper;
import com.massvig.ecommerce.database.DatabaseUtil;
import com.massvig.ecommerce.entities.Chater;
import com.massvig.ecommerce.entities.ChaterList;
import com.massvig.ecommerce.entities.Item;
import com.massvig.ecommerce.entities.Message;
import com.massvig.ecommerce.service.MassVigService;

public class MessageManager {

	private Context context;
	public ChaterList chatList;
	private LoadListener listener;
	public static final int ALLCHAT = 1;
	public static final int LOADUSER = 2;
	public void setListener(LoadListener l){
		this.listener = l;
	}
	public MessageManager(Context c){
		this.context = c;
		chatList = new ChaterList();
	}
	
	private String GetCustomerIDS(){
		String result = "";
		if(chatList.getCount() > 0){
			for (int i = 0; i < chatList.getCount(); i++) {
				Chater c = chatList.getChater(i);
				result += c.EJID + ",";
			}
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}
	
	public void GetMessagePageCustomerInfo(String sessionid, double lon, double lat){
		new AsyncTask<String, Void, String>(){
			
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if(!TextUtils.isEmpty(result)){
					try {
						JSONObject o = new JSONObject(result);
						JSONArray array = o.getJSONArray("ResponseData");
						for (int i = 0; i < array.length(); i++) {
							JSONObject data = array.getJSONObject(i);
							String EJID = data.getString("EJID");
							for (int j = 0; j < chatList.getCount(); j++) {
								Chater c = chatList.getChater(j);
								if(!TextUtils.isEmpty(EJID) && EJID.equals(c.EJID)){
									c.headimg = data.getString("HeadImgUrl");
//									c.distance = data.getDouble("Distance");
									c.nickname = data.getString("NickName");
									c.CustomerID = data.getInt("CustomerID");
									c.EJResource = data.getString("EJResource");
									c.gender = data.getInt("Gender");
								}
							}
							
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					listener.LoadSuccess(LOADUSER);
				}
			}

			@Override
			protected String doInBackground(String... params) {
				String result = MassVigService.getInstance().GetMessagePageCustomerInfo(params[0], GetCustomerIDS(), Double.valueOf(params[1]), Double.valueOf(params[2]));
				return result;
			}}.execute(sessionid, lon + "", lat + "");
	}
	
	public void GetAllChatList(String ToJ){
		String ToEJ = ToJ;
		DatabaseHelper helper = new DatabaseHelper(context, null, 1);
//		SQLiteDatabase da = helper.getWritableDatabase();
//		DatabaseUtil.InsertMessage(da, "MVEC_20", "MVEC_19", "hello world1", "2013-03-29 12:12:14", 0);
//		DatabaseUtil.InsertMessage(da, "MVEC_18", "MVEC_19", "hello world2", "2013-03-29 12:12:15", 0);
//		DatabaseUtil.InsertMessage(da, "MVEC_18", "MVEC_19", "hello world3", "2013-03-29 12:12:16", 0);
//		DatabaseUtil.InsertMessage(da, "MVEC_18", "MVEC_19", "hello world4", "2013-03-29 12:12:17", 0);
//		DatabaseUtil.InsertMessage(da, "MVEC_18", "MVEC_19", "hello world5", "2013-03-29 12:12:18", 0);
//		DatabaseUtil.InsertMessage(da, "MVEC_18", "MVEC_19", "hello world6", "2013-03-29 12:12:19", 0);
//		DatabaseUtil.InsertMessage(da, "MVEC_18", "MVEC_19", "hello world7", "2013-03-29 12:12:24", 0);
//		DatabaseUtil.InsertMessage(da, "MVEC_20", "MVEC_19", "hello world8", "2013-03-29 12:12:34", 0);
//		DatabaseUtil.InsertMessage(da, "MVEC_20", "MVEC_19", "hello world9", "2013-03-29 12:12:44", 0);
//		DatabaseUtil.InsertMessage(da, "MVEC_18", "MVEC_19", "hello world0", "2013-03-29 12:12:54", 0);
//		DatabaseUtil.InsertMessage(da, "MVEC_20", "MVEC_19", "hello world1", "2013-03-29 12:13:14", 0);
//		DatabaseUtil.InsertMessage(da, "MVEC_18", "MVEC_19", "hello world2", "2013-03-29 12:13:15", 0);
//		DatabaseUtil.InsertMessage(da, "MVEC_18", "MVEC_19", "hello world3", "2013-03-29 12:13:16", 0);
//		DatabaseUtil.InsertMessage(da, "MVEC_18", "MVEC_19", "hello world4", "2013-03-29 12:13:17", 0);
//		DatabaseUtil.InsertMessage(da, "MVEC_18", "MVEC_19", "hello world5", "2013-03-29 12:13:18", 0);
//		DatabaseUtil.InsertMessage(da, "MVEC_18", "MVEC_19", "hello world6", "2013-03-29 12:13:19", 0);
//		DatabaseUtil.InsertMessage(da, "MVEC_18", "MVEC_19", "hello world7", "2013-03-29 12:13:24", 0);
//		DatabaseUtil.InsertMessage(da, "MVEC_20", "MVEC_19", "hello world8", "2013-03-29 12:13:34", 0);
//		DatabaseUtil.InsertMessage(da, "MVEC_20", "MVEC_19", "hello world9", "2013-03-29 12:13:44", 0);
//		DatabaseUtil.InsertMessage(da, "MVEC_18", "MVEC_19", "hello world0", "2013-03-29 12:13:54", 0);
//
//		DatabaseUtil.InsertMessage(da, "MVEC_19", "MVEC_20", "hello world1", "2013-03-29 12:14:14", 0);
//		DatabaseUtil.InsertMessage(da, "MVEC_19", "MVEC_18", "hello world2", "2013-03-29 12:14:15", 0);
//		DatabaseUtil.InsertMessage(da, "MVEC_19", "MVEC_20", "hello world3", "2013-03-29 12:14:16", 0);
//		DatabaseUtil.InsertMessage(da, "MVEC_19", "MVEC_18", "hello world4", "2013-03-29 12:14:17", 0);
//		DatabaseUtil.InsertMessage(da, "MVEC_19", "MVEC_20", "hello world5", "2013-03-29 12:14:18", 0);
//		DatabaseUtil.InsertMessage(da, "MVEC_19", "MVEC_18", "hello world6", "2013-03-29 12:14:19", 0);
//		DatabaseUtil.InsertMessage(da, "MVEC_19", "MVEC_20", "hello world7", "2013-03-29 12:14:24", 0);
//		DatabaseUtil.InsertMessage(da, "MVEC_19", "MVEC_20", "hello world8", "2013-03-29 12:14:34", 0);
//		DatabaseUtil.InsertMessage(da, "MVEC_19", "MVEC_20", "hello world9", "2013-03-29 12:14:44", 0);
//		DatabaseUtil.InsertMessage(da, "MVEC_19", "MVEC_18", "hello world0", "2013-03-29 12:14:54", 0);
//		da.close();
		SQLiteDatabase database = helper.getReadableDatabase();
		ArrayList<Item> items = DatabaseUtil.getInstance().GetOneMessage(database, ToEJ);
		if (items.size() > 0) {
			chatList.clearchaterList();
			for (int i = 0; i < items.size(); i++) {
				Item it = items.get(i);
				Chater c = new Chater();
				c.EJID = it.EJID;
				ArrayList<Message> messages = DatabaseUtil.getInstance().GetChat(database, it.EJID, ToEJ);
				if(messages.size() > 0){
					int count = DatabaseUtil.getInstance().GetOneUnReadCount(database, it.EJID, ToEJ);
					c.unread = count;
					c.content = messages.get(messages.size() - 1).MessageBody;
					c.time = messages.get(messages.size() - 1).CreateTime;
				}
				chatList.addChater(c);
			}
			database.close();
			listener.LoadSuccess(ALLCHAT);
		}else{
			listener.LoadFailed(ALLCHAT);
		}
	}
	
	public interface LoadListener{
		void LoadSuccess(int index);
		void LoadFailed(int index);
		void SessionVailed();
	}
}
