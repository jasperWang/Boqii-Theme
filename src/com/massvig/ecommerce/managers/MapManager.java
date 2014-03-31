package com.massvig.ecommerce.managers;

import android.os.AsyncTask;

import com.massvig.ecommerce.entities.MerchantList;

public class MapManager {

	public double lat,lon;
	private LoadListener listener;
	public MerchantList list = new MerchantList();
	public MerchantList tempList = new MerchantList();

	public void setListener(LoadListener l){
		this.listener = l;
	}
	
	public void GetAroundMerchantStore(String sessionid){
		new MerchantAsync().execute(sessionid, list.getCount() + "");
	};
	
	private class MerchantAsync extends AsyncTask<String, Void, Boolean>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			return tempList.FetchmerchantList(params[0], lon, lat, Integer.valueOf(params[1]), 10);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(result){
				list.addmerchantList(tempList);
				tempList.clearmerchantList();
				listener.Success();
			}else{
				listener.Failed();
			}
		}
		
	}
	
	public interface LoadListener{
		void Success();
		void Failed();
	}
	
}
