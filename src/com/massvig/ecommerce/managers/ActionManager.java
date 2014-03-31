package com.massvig.ecommerce.managers;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.massvig.ecommerce.entities.Action;
import com.massvig.ecommerce.entities.ActionDetail;
import com.massvig.ecommerce.entities.ActionsList;
import com.massvig.ecommerce.service.MassVigService;

public class ActionManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int LOAD = 1;
	public static final int LOADDATA = 2;
	private LoadListener listener;
	public ActionsList actionList = new ActionsList();
	public Action action = new Action();
	public ActionDetail detail = new ActionDetail();
	public void setListener(LoadListener l){
		this.listener = l;
	}
	
	public void FetchActions(String sessionid){
		new FetchActionsAsync().execute(sessionid);
	}

	/**
	 * 异步获取actions
	 * @author DuJun
	 *
	 */
	private class FetchActionsAsync extends AsyncTask<String, Void, Boolean>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			return actionList.GetUserCampaigns(params[0]);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(result){
				listener.Success(LOAD);
			}else{
				listener.Failed(LOAD);
			}
		}
	}
	
	public interface LoadListener{
		void Success(int index);
		void Failed(int index);
	}

	public void LoadData(final String sessionid) {
		new AsyncTask<Void, Void, Boolean>(){

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if(result){
					listener.Success(LOADDATA);
				}else{
					listener.Failed(LOADDATA);
				}
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				String result = MassVigService.getInstance().GetCampaignInfo(sessionid, detail.CampaignID);
				if(!TextUtils.isEmpty(result) && !result.equals("null")){
					try {
						JSONObject o = new JSONObject(result);
						int code = o.getInt("ResponseStatus");
						if(code == 0){
							JSONObject data = o.getJSONObject("ResponseData");
							detail = new ActionDetail();
							detail.AllowShare = data.getBoolean("AllowShare");
							detail.CampaignID = data.getInt("CampaignID");
							detail.CampaignImgUrl = data.getString("CampaignImgUrl");
							detail.CampaignRule = data.getString("CampaignRule");
							detail.CampaignSubTitle = data.getString("CampaignSubTitle");
							detail.CampaignTitle = data.getString("CampaignTitle");
							detail.Description = data.getString("Description");
							return true;
						}else{
							return false;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				return false;
			}}.execute();
	}
}
