package com.massvig.ecommerce.widgets;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.massvig.ecommerce.service.MassVigService;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

public class MassVigData {
	String content;
	private JSONArray categoryData;
	private JSONArray cityListData;
	private JSONArray hotkeyData;
	private static Context context;
	private int searchVersion;
	private static MassVigData searchKeyInstance = null;
	
	public static MassVigData getinstance(Context mcontext){
		context = mcontext;
//		if(searchKeyInstance == null){
			searchKeyInstance = new MassVigData();
//		}
		return searchKeyInstance;
	}
	
	public void getHotKey(Context context){
		MassVigData.context = context;
		getHotKey();
	}
	
	public void execute(Context context){
		MassVigData.context = context;
//		this.searchVersion = searchVersion;
//		if(searchVersion > getSearchVersion()){
			getCategoryByNet();
//			getHotKey();
//			getCityListByNet();
			
//		}
	}
	
	public String getAdCache(){
		try{
			InputStream in = context.getAssets().open("ad");
			if(in != null){
				byte[] b = new byte[1024];
				ByteArrayOutputStream outputS = new ByteArrayOutputStream();
				int len = -1;
				while ((len = in.read(b)) != -1) {
					outputS.write(b, 0, len);
				}
				return outputS.toString();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private JSONArray getCategoryByCache(){
		try{
			InputStream in = context.getAssets().open("searchData");
			if(in != null){
				byte[] b = new byte[1024];
				ByteArrayOutputStream outputS = new ByteArrayOutputStream();
				int len = -1;
				while ((len = in.read(b)) != -1) {
					outputS.write(b, 0, len);
				}
				return new JSONObject(outputS.toString()).getJSONArray("category");
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private JSONArray getHotkeyByCache(){
		try{
			InputStream in = context.getAssets().open("searchkey");
			if(in != null){
				byte[] b = new byte[1024];
				ByteArrayOutputStream outputS = new ByteArrayOutputStream();
				int len = -1;
				while ((len = in.read(b)) != -1) {
					outputS.write(b, 0, len);
				}
				return new JSONArray(outputS.toString());
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private JSONArray getCityListByCache(){
		try{
			InputStream in = context.getAssets().open("citylist");
			if(in != null){
				byte[] b = new byte[1024];
				ByteArrayOutputStream outputS = new ByteArrayOutputStream();
				int len = -1;
				while ((len = in.read(b)) != -1) {
					outputS.write(b, 0, len);
				}
				return new JSONArray(outputS.toString());
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	
	private void getCategoryByNet(){
		
		new AsyncTask<Object, Object, Object>(){

			@Override
			protected String doInBackground(Object... params) {
				// TODO Auto-generated method stub
				return MassVigService.getInstance().Getcategory();
//				return service.getCategoryListForSearchPage();
			}

			@Override
			protected void onPostExecute(Object result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				
				if(result != null){
					String data = (String)result;
					try {
						if(data != null){
							JSONObject d = new JSONObject(data);
							if(d.getInt("ResponseStatus") == 0){
								categoryData = d.getJSONArray("ResponseData");
							}
//							categoryData = new JSONArray(data);
							if(categoryData != null){
								saveCategory();
							}
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						
				}
				
			}
			
		}.execute();
		
	}
	
	private void getHotKey(){
		
		final MassVigService service = MassVigService.getInstance();
		new AsyncTask<Object, Object, Object>(){

			@Override
			protected String doInBackground(Object... params) {
				// TODO Auto-generated method stub
				return service.GetHotSearchword();
			}

			@Override
			protected void onPostExecute(Object result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				
				if(result != null){
					String data = (String)result;
					try {
						if(data != null){
							JSONObject o = new JSONObject(data);
							if (o.getInt("ResponseStatus") == 0) {
								hotkeyData = o.getJSONArray("ResponseData");
								if (hotkeyData != null) {
									saveHotkeylist();
								}
							}
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						
				}
				
			}
			
		}.execute();
		
	}
	
	private void getCityListByNet(){
		
		final MassVigService service = MassVigService.getInstance();
		new AsyncTask<Object, Object, Object>(){

			@Override
			protected String doInBackground(Object... params) {
				// TODO Auto-generated method stub
				return service.GetRegionInfo();
			}

			@Override
			protected void onPostExecute(Object result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				
				if(result != null){
					String data = (String)result;
					try {
						if(data != null){
							JSONObject o = new JSONObject(data);
							if (o.getInt("ResponseStatus") == 0) {
								cityListData = o.getJSONArray("ResponseData");
								if (cityListData != null) {
									saveCitylist();
								}
							}
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						
				}
				
			}
			
		}.execute();
		
	}

	public JSONArray getChildCategory(int index){
		if(categoryData == null){
			getAllCategory();
		} 
		try {
			return categoryData.getJSONObject(index).getJSONArray("CustomSubcategorys");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public JSONArray getHotkeyList(){
		if(hotkeyData == null){
			String categ = getKeyPreferences().getString("searchkey", "");
			if(!categ.equals("")){
				try {
					hotkeyData = new JSONArray(categ);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				hotkeyData = getHotkeyByCache();
			}
			
		}	
		return hotkeyData;
	}
	
	public JSONArray getCityList(){
		if(cityListData == null){
			String categ = getCitySharedPreferences().getString("cityList", "");
			if(!categ.equals("")){
				try {
					cityListData = new JSONArray(categ);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				cityListData = getCityListByCache();
			}
			
		}	
		return cityListData;
	}
	
	private void saveHotkeylist(){
		getKeyPreferences().edit().putString("searchkey", hotkeyData.toString()).commit();
	}
	private void saveCitylist(){
		getCitySharedPreferences().edit().putString("cityList", cityListData.toString()).commit();
	}
	
	public JSONArray getAllCategory(){
		if(categoryData == null){
			String categ = getSharedPreferences().getString("category", "");
			if(!categ.equals("")){
				try {
					categoryData = new JSONArray(categ);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				categoryData = getCategoryByCache();
			}
		}
		return categoryData;
	}
	
	private void saveCategory(){
		getSharedPreferences().edit().putString("category", categoryData.toString()).commit();
		saveSearchVersion();
	}
	
	
//	private int getSearchVersion(){
//		return getSharedPreferences().getInt("version", 0);
//	}
	
	private void saveSearchVersion(){
		getSharedPreferences().edit().putInt("version", this.searchVersion).commit();
	}
	
	private SharedPreferences getCitySharedPreferences(){
		return context.getSharedPreferences("cityList",Context.MODE_APPEND);
	}
	
	private SharedPreferences getSharedPreferences(){
		return context.getSharedPreferences("searchData",Context.MODE_APPEND);
	}
	
	private SharedPreferences getKeyPreferences(){
		return context.getSharedPreferences("Key",Context.MODE_APPEND);
	}
	
}
