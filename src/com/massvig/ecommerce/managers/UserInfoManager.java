package com.massvig.ecommerce.managers;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.massvig.ecommerce.database.DatabaseHelper;
import com.massvig.ecommerce.database.DatabaseUtil;
import com.massvig.ecommerce.entities.User;
import com.massvig.ecommerce.service.MassVigService;
import com.massvig.ecommerce.utilities.MassVigContants;

public class UserInfoManager {

	private Listener listener;
	private User user;
	public static final int PASSWORD = 1;
	public static final int MODIFY = 2;
	public static final int UPLOADURL = 3;
	public static final int UPLOADIMAGE = 4;
	public String uploadUrl = "";
	public String fileKey = "";
	public boolean isCanUpload;
	private String filePath = "";
	public String sessionID;

	public UserInfoManager(User u) {
		this.user = u;
	}

	public void ModifyPassword(String oriPassword, String newPass) {
		int result = user.ModifyPassword(oriPassword, newPass);
		if (result == 0) {
			listener.Success(PASSWORD);
		} else if (result == MassVigContants.SESSIONVAILED) {
			listener.SessionFailed();
		} else {
			listener.Failed(PASSWORD);
		}
	}

	private class GetUploadAsync extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			String result = MassVigService.getInstance().GetUploadImageUrl();
			if (!TextUtils.isEmpty(result) && !result.equals("null")) {
				try {
					JSONObject o = new JSONObject(result);
					int status = o.getInt("ResponseStatus");
					if (status == 0) {
						JSONObject data = o.getJSONObject("ResponseData");
						uploadUrl = data.getString("UploadImageUrl");
						fileKey = data.getString("FileKey");
						isCanUpload = true;
						return null;
					} else {
						isCanUpload = false;
					}
				} catch (JSONException e) {
					e.printStackTrace();
					isCanUpload = false;
				}
			}
			isCanUpload = false;
			return null;
		}

	}

	public void GetUploadImageUrl() {
		new GetUploadAsync().execute();
	}

	public class Numbers {
		public int OnlineUnpayCount;
		public int OnlinePaidCount;
		public int OnlineRefundCount;
		public int OfflineCashCount;
		public int FollowingCustomerCount;
		public int FansCustomerCount;
	}

	private class GetCustomerCenter extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			String result = MassVigService.getInstance().GetCustomerCenter(
					user.sessionID);
			Numbers num = new Numbers();
			try {
				if (!TextUtils.isEmpty(result)) {
					JSONObject o = new JSONObject(result);
					int status = o.getInt("ResponseStatus");
					if (status == 0) {
						JSONObject data = o.getJSONObject("ResponseData");
						num.OnlineUnpayCount = data.optInt("OnlineUnpayCount");
						num.OnlinePaidCount = data.optInt("OnlinePaidCount");
						num.OnlineRefundCount = data.optInt("RefundingCount");
						num.OfflineCashCount = data.optInt("OfflineCashCount");
						if (data.has("FollowingCustomerCount"))
							num.FollowingCustomerCount = data
									.getInt("FollowingCustomerCount");
						if (data.has("FansCustomerCount"))
							num.FansCustomerCount = data
									.getInt("FansCustomerCount");
						listener.LoadData(num);
						return null;
					} else {
						listener.LoadData(num);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			listener.LoadData(num);
			return null;
		}

	}

	public void GetCustomerCenter() {
		new GetCustomerCenter().execute();
	}

	private class UploadAsync extends AsyncTask<Void, Void, Void> {

		private Bitmap bitmap;

		public UploadAsync(Bitmap bitmap) {
			this.bitmap = bitmap;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			listener.Dialog(false);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			listener.Dialog(true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			String result = MassVigService.getInstance().PicUpload(uploadUrl,
					fileKey, bitmap);
			try {
				JSONObject data = new JSONObject(result);
				filePath = data.getString("filePath");
				String error = data.getString("error");
				if (TextUtils.isEmpty(error)) {
					listener.Success(UPLOADIMAGE);
				} else {
					listener.Failed(UPLOADIMAGE);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

	}

	public void UploadImage(Bitmap bitmap) {
		new UploadAsync(bitmap).execute();
	}

	private class ModifyAsync extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			listener.Dialog(false);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			listener.Dialog(true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			int result = user.ModifyCustomInfo(sessionID,user.password, user.nickName,
					filePath, user.gender);
			if (result == 0) {
				listener.Success(MODIFY);
			} else if (result == MassVigContants.SESSIONVAILED) {
				listener.SessionFailed();
			} else if (result == 1) {
				listener.NickName();
			} else{
				listener.Failed(MODIFY);
			}
			return null;
		}

	}

	public int GetAllUnread(Context context, String toEJID) {
		DatabaseHelper helper = new DatabaseHelper(context, null, 1);
		SQLiteDatabase database = helper.getReadableDatabase();
		int count = DatabaseUtil.getInstance().GetAllUnReadCount(database, toEJID);
		database.close();
		return count;
	}

	public void ModifyInformation() {
		new ModifyAsync().execute();
	}

	public void setListener(Listener l) {
		this.listener = l;
	}

	public interface Listener {
		void Success(int index);

		void Failed(int index);

		void SessionFailed();

		void Dialog(boolean toShow);

		void LoadData(Numbers n);
		
		void NickName();
	}
}
