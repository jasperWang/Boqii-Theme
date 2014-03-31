package com.massvig.ecommerce.managers;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.massvig.ecommerce.database.DatabaseHelper;
import com.massvig.ecommerce.database.DatabaseUtil;
import com.massvig.ecommerce.service.MassVigService;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.widgets.CommunityMode;

public class CommunityUserManager {

	public static final int LOADDATA = 1;
	public static final int FANS = 2;
	private Listener listener;
	public int customerID;
	public UserInfo userinfo = new UserInfo();
	public void setListener(Listener l){
		this.listener = l;
	}
	public void LoadData(){
		new LoadDataAsync().execute(customerID);
	};
	
	/**
	 * 添加移除粉丝
	 * @param tag 0:移除粉丝 1：添加粉丝
	 * @param sessionId
	 * @param customerID
	 */
	public void AddAndRemoveFans(int tag, String sessionId, int customerID){
		new AddAndRemoveFansAsync().execute(tag + "", sessionId, customerID + "");
	}
	
	/**
	 * 添加删除粉丝
	 * @author DuJun
	 *
	 */
	private class AddAndRemoveFansAsync extends AsyncTask<String, Void, Integer>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(String... params) {
			return CommunityMode.getInstance().AddAndRemoveFans(Integer.valueOf(params[0]), params[1], Integer.valueOf(params[2]));
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if(result == 0){
				if(userinfo.Relation == 2){
					userinfo.Relation = 3;
				}else{
					userinfo.Relation = 2;
				}
				listener.Success(FANS);
				return;
			}else if(result == MassVigContants.SESSIONVAILED){
				listener.SessionFailed();
			}else if(result == 1){
				listener.Already();
			}else{
				listener.Failed(FANS);
			}
		}
	}

	/**
	 * 获取未读数量
	 * @param context
	 * @param toEJID
	 * @return
	 */
	public int GetAllUnread(Context context, String toEJID){
		DatabaseHelper helper = new DatabaseHelper(context, null, 1);
		SQLiteDatabase database = helper.getReadableDatabase();
		int count = DatabaseUtil.getInstance().GetAllUnReadCount(database, toEJID);
		database.close();
		return count;
	}
	
	/**
	 * 忽略所有消息
	 * @param context
	 */
	public void SetAllRead(Context context){
		DatabaseHelper helper = new DatabaseHelper(context, null, 1);
		SQLiteDatabase database = helper.getWritableDatabase();
		DatabaseUtil.getInstance().UpdateAllUnreadMessage(database);
		database.close();
	}
	
	private class LoadDataAsync extends AsyncTask<Integer, Void, Void>{

		@Override
		protected Void doInBackground(Integer... params) {
			String result = MassVigService.getInstance().GetCustomerStatistics(params[0]);
			try {
				JSONObject o = new JSONObject(result);
				int status = o.getInt("ResponseStatus");
				if(status == 0){
					JSONObject data = o.getJSONObject("ResponseData");
//					UserInfo userinfo = new UserInfo();
					userinfo.CustomerID = data.getInt("CustomerID");
					userinfo.FansCustomerCount = data.getInt("FansCustomerCount");
					userinfo.FollowingCustomerCount = data.getInt("FollowingCustomerCount");
					userinfo.Gender = data.getInt("Gender");
					userinfo.HeadImgUrl = data.getString("HeadImgUrl");
					userinfo.NickName = data.getString("NickName");
					userinfo.PraiseCount = data.getInt("PraiseCount");
					userinfo.Relation = data.getInt("Relation");
					userinfo.SharedCount = data.getInt("SharedCount");
					userinfo.SharePraisedCount = data.getInt("SharePraisedCount");
					userinfo.EJID = data.getString("EJID");
					userinfo.EJResource = data.getString("EJResource");
					listener.LoadData(userinfo);
					return null;
				}else{
					listener.Failed(LOADDATA);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				listener.Failed(LOADDATA);
			}
			listener.Failed(LOADDATA);
			return null;
		}
		
	}
	
	public class UserInfo{
		public int CustomerID;
		public String NickName;
		public String HeadImgUrl;
		public int Gender;
		public int FollowingCustomerCount;
		public int FansCustomerCount;
		public int SharePraisedCount;
		public int SharedCount;
		public int PraiseCount;
		public int Relation;
		public String EJID;
		public String EJResource;
	}
	
	public interface Listener{
		void LoadData(UserInfo user);
		void Success(int index);
		void Failed(int index);
		void Dialog(boolean toShow);
		void SessionFailed();
		void Already();
	}
}
