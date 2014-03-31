package com.massvig.ecommerce.managers;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.massvig.ecommerce.activities.InsertPost;
import com.massvig.ecommerce.service.MassVigService;
import com.massvig.ecommerce.utilities.MassVigContants;

public class InsertManager {

	private Listener listener;
	public int productID;
	public int campaignID;
	public int shareID;
	public String comment = "";
	public String imgUrl;
	public double lat = 0.0;
	public double lon = 0.0;
	public String address = "";
	public String uploadUrl = "";
	public String fileKey = "";
	public boolean isCanUpload;
	public static final int UPLOADIMAGE = 4;
	public static final int SHARE = 5;
	public int flag = InsertPost.PRODUCT;

	public void setListener(Listener l){
		this.listener = l;
	}
	public void Share(String sessionid){
		String result = "";
		if(flag == InsertPost.PRODUCT)
			result = MassVigService.getInstance().ShareProduct(sessionid, productID, comment, imgUrl, lon, lat, address);
		else if(flag == InsertPost.ACTION)
			result = MassVigService.getInstance().ShareCampaign(sessionid, campaignID, comment, imgUrl, lon, lat, address);
		else if(flag == InsertPost.ORIGIN)
			result = MassVigService.getInstance().ShareOrigin(sessionid, comment, imgUrl, lon, lat, address);
		else
			result = MassVigService.getInstance().Share(sessionid, shareID, comment, imgUrl, lon, lat, address);
		if(!TextUtils.isEmpty(result)){
			JSONObject o;
			try {
				o = new JSONObject(result);
				if(o.getInt("ResponseStatus") == 0){
					listener.Success(SHARE);
				}else if(o.getInt("ResponseStatus") == MassVigContants.SESSIONVAILED){
					listener.SessionFailed();
				}else{
					listener.Failed(SHARE);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private class UploadAsync extends AsyncTask<Void, Void, Boolean>{

		private Bitmap bitmap;
		public UploadAsync(Bitmap bitmap){
			this.bitmap = bitmap;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			listener.Dialog(false);
			if(result){
				listener.Success(UPLOADIMAGE);
			}else{
				listener.Failed(UPLOADIMAGE);
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			listener.Dialog(true);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			String result = MassVigService.getInstance().PicUpload(uploadUrl, fileKey, bitmap);
			try {
				JSONObject data = new JSONObject(result);
				imgUrl = data.getString("filePath");
				String error = data.getString("error");
				if(TextUtils.isEmpty(error)){
					return true;
				}else{
					return false;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		
	}
	
	public void UploadImage(Bitmap bitmap){
		new UploadAsync(bitmap).execute();
	}
	
	private class GetUploadAsync extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			String result = MassVigService.getInstance().GetUploadImageUrl();
			try {
				JSONObject o = new JSONObject(result);
				int status = o.getInt("ResponseStatus");
				if(status == 0){
					JSONObject data = o.getJSONObject("ResponseData");
					uploadUrl = data.getString("UploadImageUrl");
					fileKey = data.getString("FileKey");
					isCanUpload = true;
					return null;
				}else{
					isCanUpload = false;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				isCanUpload = false;
			}
			isCanUpload = false;
			return null;
		}
		
	}
	
	public void GetUploadImageUrl(){
		new GetUploadAsync().execute();
	}
	
	public interface Listener{
		void Success(int index);
		void Failed(int index);
		void Dialog(boolean toShow);
		void SessionFailed();
	}
}
