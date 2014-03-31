package com.massvig.ecommerce.managers;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.massvig.ecommerce.entities.AdItem;
import com.massvig.ecommerce.entities.Advertise;
import com.massvig.ecommerce.service.MassVigService;
import com.massvig.ecommerce.utilities.MassVigUtil;

public class MainActivityManager {
	public ArrayList<AdItem> imageItemsList = new ArrayList<AdItem>();
//	public ArrayList<Advertise> mainImageList = new ArrayList<Advertise>();
//	public ArrayList<Advertise> secondImageList = new ArrayList<Advertise>();
	public ArrayList<AdItem> adList = new ArrayList<AdItem>();
	private LoadListener listener;
	private Context mContext;
	
	public void setListener(LoadListener l){
		this.listener = l;
	}
	
	public MainActivityManager(Context c){
		this.mContext = c;
		GetImageItemsList();
	}
	
	private Advertise DataAnaly(JSONObject d){
		Advertise a = new Advertise();
		try {
			a.AdvertisingID = d.getInt("AdRegionID");
			a.ImgUrl = d.getString("ImgUrl");
			a.Link = d.getString("Link");
			a.LinkType = d.getInt("LinkType");
			a.Name = d.getString("Name");
//			a.Position = d.getInt("Position");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return a;
	}

	private AdItem ItemAnaly(JSONObject obj) {
		AdItem item = new AdItem();
		try {
			item.AdItemID = obj.optInt("AdItemID");
			item.AdTemplate = obj.optInt("AdTemplate");
			item.Position = obj.optInt("Position");
			item.Rank = obj.optInt("Rank");
			item.AdRegions = obj.optString("AdRegions");
		} catch (Exception e) {
		}
		return item;
	}
	
	public void SetData(){
		String result = MassVigUtil.getPreferenceData(mContext, "AD", "");
		if(!TextUtils.isEmpty(result)){
			try {
				JSONObject o = new JSONObject(result);
				int res = o.getInt("ResponseStatus");
				if (res == 0) {
					JSONArray array = o.getJSONArray("ResponseData");
					for (int i = 0; i < array.length(); i++) {
						JSONObject data = array.getJSONObject(i);
						int position = data.getInt("Position");
						if (position == 3) {
							JSONArray arr = data.getJSONArray("AdItems");
							imageItemsList.clear();
							for (int j = 0; j < arr.length(); j++) {
								JSONObject obj = arr.getJSONObject(j);
								imageItemsList.add(ItemAnaly(obj));
							}
						} else if (position == 2) {
//							JSONArray arr = data
//									.getJSONArray("Advertises");
//							secondImageList.clear();
//							for (int j = 0; j < arr.length(); j++) {
//								JSONObject d = arr.getJSONObject(j);
//								secondImageList.add(DataAnaly(d));
//							}
						} else if(position == 4){
//							mainImageList.clear();
//							JSONArray ar = data.getJSONArray("Advertises");
//							for (int j = 0; j < ar.length(); j++) {
//								JSONObject ob = ar.getJSONObject(j);
//								mainImageList.add(DataAnaly(ob));
//							}
						} else if(position == 5){
							JSONArray arr = data.getJSONArray("AdItems");
							adList.clear();
							for (int j = 0; j < arr.length(); j++) {
								JSONObject obj = arr.getJSONObject(j);
								adList.add(ItemAnaly(obj));
							}
						}
					}
					listener.Success();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				listener.Failed();
			}
		}
	}

	public void GetImageItemsList(){
		new AsyncTask<Void, Void, String>(){

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if(!TextUtils.isEmpty(result)){
					MassVigUtil.setPreferenceStringData(mContext, "AD", result);
//					try {
//						JSONObject o = new JSONObject(result);
//						int res = o.getInt("ResponseStatus");
//						if (res == 0) {
//							JSONArray array = o.getJSONArray("ResponseData");
//							for (int i = 0; i < array.length(); i++) {
//								JSONObject data = array.getJSONObject(i);
//								int position = data.getInt("Position");
//								if (position == 3) {
//									JSONArray arr = data
//											.getJSONArray("Advertises");
//									imageItemsList.clear();
//									for (int j = 0; j < arr.length(); j++) {
//										JSONObject d = arr.getJSONObject(j);
//										imageItemsList.add(DataAnaly(d));
//									}
//								} else if (position == 2) {
//									JSONArray arr = data
//											.getJSONArray("Advertises");
//									secondImageList.clear();
//									for (int j = 0; j < arr.length(); j++) {
//										JSONObject d = arr.getJSONObject(j);
//										secondImageList.add(DataAnaly(d));
//									}
//								} else if(position == 4){
//									mainImageList.clear();
//									JSONArray ar = data.getJSONArray("Advertises");
//									for (int j = 0; j < ar.length(); j++) {
//										JSONObject ob = ar.getJSONObject(j);
//										mainImageList.add(DataAnaly(ob));
//									}
//								}
//							}
//							listener.Success();
//						}
//					} catch (JSONException e) {
						// TODO Auto-generated catch block
//						e.printStackTrace();
//						listener.Failed();
//					}
				}
			}

			@Override
			protected String doInBackground(Void... params) {
				return MassVigService.getInstance().GetAdvertises("3,5");
			}}.execute();
	};
	public void RefreshImageItemsList(){
		new AsyncTask<Void, Void, String>(){

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if(!TextUtils.isEmpty(result)){
					MassVigUtil.setPreferenceStringData(mContext, "AD", result);
					try {
						JSONObject o = new JSONObject(result);
						int res = o.getInt("ResponseStatus");
						if (res == 0) {
							JSONArray array = o.getJSONArray("ResponseData");
							for (int i = 0; i < array.length(); i++) {
								JSONObject data = array.getJSONObject(i);
								int position = data.getInt("Position");
								if (position == 3) {
									JSONArray arr = data.getJSONArray("AdItems");
									imageItemsList.clear();
									for (int j = 0; j < arr.length(); j++) {
										JSONObject obj = arr.getJSONObject(j);
										imageItemsList.add(ItemAnaly(obj));
									}
								} else if (position == 2) {
//									JSONArray arr = data
//											.getJSONArray("Advertises");
//									secondImageList.clear();
//									for (int j = 0; j < arr.length(); j++) {
//										JSONObject d = arr.getJSONObject(j);
//										secondImageList.add(DataAnaly(d));
//									}
								} else if(position == 4){
//									mainImageList.clear();
//									JSONArray ar = data.getJSONArray("Advertises");
//									for (int j = 0; j < ar.length(); j++) {
//										JSONObject ob = ar.getJSONObject(j);
//										mainImageList.add(DataAnaly(ob));
//									}
								} else if(position == 5){
									JSONArray arr = data.getJSONArray("AdItems");
									adList.clear();
									for (int j = 0; j < arr.length(); j++) {
										JSONObject obj = arr.getJSONObject(j);
										adList.add(ItemAnaly(obj));
									}
								}
							}
							listener.Success();
						}
					} catch (JSONException e) {
						e.printStackTrace();
						listener.Failed();
					}
				}
			}

			@Override
			protected String doInBackground(Void... params) {
				return MassVigService.getInstance().GetAdvertises("3,5");
			}}.execute();
	};
	
	public interface LoadListener{
		void Success();
		void Failed();
	}
	
}
