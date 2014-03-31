///**
// *  MassVig Technology 2011
// */
//package com.massvig.ecommerce.activities;
//
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.util.ArrayList;
//import java.util.List;
//
//import com.massvig.ecommerce.R;
//import com.massvig.ecommerce.entities.CityList;
//import com.massvig.ecommerce.service.MassVigService;
//import com.massvig.ecommerce.utilities.CityManage;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.location.Criteria;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.ListView;
//import android.widget.TextView;
//
///**
// * @author ZhenHong Xu <zhenhong.xu@massvig.com>
// * 
// */
//public class SelectCityActivity extends Activity {
//
//	private boolean mLocationFixed;
//
//	private ArrayList<String> listTags = new ArrayList<String>();
//	private ArrayList<String> listCityItems = new ArrayList<String>();
//	private ArrayList<String> listAutoCityItems = new ArrayList<String>();
//	private CityList cityList = new CityList();
//
//	private CityListAdapter mCityListAdapter;
//	private ListView mCitySelectIndex;
//	private ProgressDialog loadingDialog;
//	private Button btn_city;
//	private LocationListener mLocationListener;
//	private boolean isFinish = false;
//	private ImageButton refreshLocation;
//	private LocationManager locMan;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.select_city);
//
//		initTags();
//		locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//		btn_city = (Button) findViewById(R.id.current_city);
//		btn_city.setText(R.string.location_locating);
//		btn_city.setOnClickListener(new View.OnClickListener() {
//
//			public void onClick(View v) {
//				////
//				try {
//					
//					if (btn_city.getText().toString() == getResources().getString(R.string.location_locating)) {
//						return;
//					}
//					
//					BaseApplication.currentCity = cityList
//						.getCityByName(btn_city.getText().toString());
//					if(BaseApplication.getCurrentCity() == null){
//						return;
//					}
//					
//					new CityManage().SaveCurrentCity(SelectCityActivity.this,BaseApplication.currentCity.getCityName(),BaseApplication.currentCity.getCityId());
//					
//					locMan.removeUpdates(mLocationListener);
//					
//					SelectCityActivity.this.setResult(RESULT_OK, null);
//					SelectCityActivity.this.finish();
//				} catch (Exception  e) {   
//					
//					return;        
//				} 
//				
//			}
//
//		});
//		ListView cityIndex = (ListView) findViewById(R.id.city_index_list);
//		cityIndex.setAdapter(new CityIndexListAdapter(this, listTags));
//		cityIndex.setOnItemClickListener(new OnItemClickListener() {
//
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//
//				mCitySelectIndex.setSelection(listCityItems.indexOf(listTags
//						.get(position)));
//			}
//
//		});
//
//		mCitySelectIndex = (ListView) findViewById(R.id.city_select_list);
//		mCityListAdapter = new CityListAdapter(this, listCityItems);
//		mCitySelectIndex.setAdapter(mCityListAdapter);
//
//		mCitySelectIndex.setOnItemClickListener(new OnItemClickListener() {
//
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//
//				BaseApplication.currentCity = cityList
//						.getCityByName(listCityItems.get(position));
//				btn_city.setText(BaseApplication.getCurrentCity()
//						.getCityName());
//				new CityManage().SaveCurrentCity(SelectCityActivity.this,BaseApplication.currentCity.getCityName(),BaseApplication.currentCity.getCityId());
//				
//				SelectCityActivity.this.setResult(RESULT_OK, null);
//				SelectCityActivity.this.finish();
//
//			}
//
//		});
//
//		loadingDialog = ProgressDialog.show(SelectCityActivity.this, "",
//				getResources().getString(R.string.select_city_loading), true,
//				false);
//
//		new FetchCityTask().execute();
//
//		mLocationFixed = false;
//		LocationManager locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//		Criteria criteria = new Criteria();
//		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//		criteria.setAltitudeRequired(false);
//		criteria.setBearingRequired(false);
//		criteria.setCostAllowed(true);
//		criteria.setPowerRequirement(Criteria.POWER_LOW);
//		String provider = locMan.getBestProvider(criteria, true);
//
//		mLocationListener = new LocationListener() {
//
//			public void onStatusChanged(String provider, int status,
//					Bundle extras) {
//				// TODO Auto-generated method stub
//
//			}
//
//			public void onProviderEnabled(String provider) {
//				// TODO Auto-generated method stub
//
//			}
//
//			public void onProviderDisabled(String provider) {
//				// TODO Auto-generated method stub
//
//			}
//
//			public void onLocationChanged(Location location) {
//				if (location != null) {
//
//					 if (!mLocationFixed) {
//
//						mLocationFixed = true;
//						String mCurrLat = String.valueOf(location.getLatitude());
//						String mCurrLng = String.valueOf(location.getLongitude());
//	
//						new QueryCityTask().execute(mCurrLat, mCurrLng);
//
//					 }
//
//
//
//
//				} else {
//
//				}
//
//			}
//		};
//		if (provider == null) {
//			provider = "passive";
//		}
//		List<String> listProv = locMan.getAllProviders();
//		for (int intProv = 0; intProv < listProv.size(); intProv++) {
//			provider = listProv.get(intProv);
//			locMan.requestLocationUpdates(provider, 0, 0, mLocationListener);
//		}
//
//		refreshLocation = (ImageButton) findViewById(R.id.location_refresh);
//		refreshLocation.setOnClickListener(new View.OnClickListener() {
//
//			public void onClick(View v) {
//				refreshLocation.setClickable(false);
//				mCitySelectIndex.setAdapter(null);
//	    		loadingDialog = ProgressDialog.show(SelectCityActivity.this, "",
//	    				getResources().getString(R.string.select_city_loading), true,
//	    				false);
//
//	    		
//	        	new FetchCityNetTask().execute();
//	    		
//	        	
//	        	
//			}
//
//		});
//	}
//
//	private class QueryCityTask extends AsyncTask<String, Void, String> {
//
//		@Override
//		protected String doInBackground(String... params) {
//
//			String result = null;
//
//			result = MassVigService.getInstance().getCityByLocation(
//					params[0], params[1]);
//
//			if (result == null)
//				return result;
//
//			if (result.substring(result.length() - 1, result.length()).equals(
//					getResources().getString(R.string.location_shi))) {
//				result = result.substring(0, result.length() - 1);
//			}
//
//			if (!isFinish) {
//				while (listCityItems.size() == 0) {
//					try {
//						Thread.sleep(200);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//
//			return result;
//		}
//
//		@Override
//		protected void onPostExecute(String result) {
//
//			if (result == null)
//				return;
//
//			// listCityItems.set(1, result);
//			if (!isFinish) {
//				mCityListAdapter.notifyDataSetChanged();
//				btn_city.setText(result);
//				locMan.removeUpdates(mLocationListener);
//			}
//
//			super.onPostExecute(result);
//		}
//	}
//
//	private void initTags() {
//		String[] alphabetTags = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
//				"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
//				"V", "W", "X", "Y", "Z" };
//		listTags.add(getResources().getString(R.string.select_city_hot_city));
//
//		int len = alphabetTags.length;
//		for (int i = 0; i < len; i++) {
//			listTags.add(alphabetTags[i]);
//		}
//	}
//
//	private void initCityItems() {
//
//		int cityIndex = 0;
//		int cityCount = cityList.getCount();
//
//		listCityItems.add(listTags.get(0));
//
//		while (cityIndex < cityCount && cityList.getCity(cityIndex).isHot()) {
//			listCityItems.add(cityList.getCity(cityIndex).getCityName());
//			cityIndex++;
//		}
//
//		for (int tag = 1; tag < listTags.size(); tag++) {
//
//			listCityItems.add(listTags.get(tag));
//			cityIndex = 0;
//			try {
//				while (cityIndex < cityCount) {
//					if (!TextUtils.isEmpty(cityList.getCity(cityIndex)
//							.getCityNamePY())) {
//						if (cityList.getCity(cityIndex).getCityNamePY()
//								.substring(0, 1).toUpperCase()
//								.equals(listTags.get(tag))) {
//							listCityItems.add(cityList.getCity(cityIndex)
//									.getCityName());
//							listAutoCityItems.add(cityList.getCity(cityIndex)
//									.getCityName());
//						}
//					}					
//					cityIndex++;
//				
//				}
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}
//		}
//	}
//
//	private class CityIndexListAdapter extends ArrayAdapter<String> {
//
//		private LayoutInflater mInflater;
//
//		public CityIndexListAdapter(Context context, List<String> tags) {
//			super(context, 0, tags);
//
//			mInflater = (LayoutInflater) SelectCityActivity.this
//					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			TextView indexItem = null;
//
//			if (convertView == null) {
//				indexItem = (TextView) mInflater.inflate(
//						R.layout.select_city_index_item, null);
//			} else {
//				indexItem = (TextView) convertView;
//			}
//
//			indexItem.setText(getItem(position));
//
//			return indexItem;
//		}
//	}
//
//	private class CityListAdapter extends ArrayAdapter<String> {
//
//		private LayoutInflater mInflater;
//
//		public CityListAdapter(Context context, List<String> cityListItems) {
//			super(context, 0, cityListItems);
//
//			mInflater = (LayoutInflater) SelectCityActivity.this
//					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		}
//
//		@Override
//		public int getItemViewType(int position) {
//			int viewtype = 0;
//			
//			if (listTags.contains(getItem(position))) {
//				viewtype = 0;
//			} else {
//				viewtype = 1;
//			}
//
//			return viewtype;
//		}
//		
//		@Override
//		public int getViewTypeCount() {
//
//			return 2;
//		}
//		
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
////			TextView indexItem = null;
//			int viewtype = getItemViewType(position);
//			
//			if (convertView == null) {
//				if (viewtype == 0) {
//					convertView = (TextView) mInflater.inflate(
//							R.layout.select_city_tag_item, null);
//				} else {
//					convertView = (TextView) mInflater.inflate(
//							R.layout.select_city_item, null);
//				}
//			}
//			
//			((TextView)convertView).setText(getItem(position));
//			
//			return convertView;
//			
//		}
//
//		@Override
//		public boolean isEnabled(int position) {
//
//			if (listTags.contains(getItem(position))) {
//				return false;
//			}
//
//			return super.isEnabled(position);
//		}
//	}
//
//	private class FetchCityTask extends AsyncTask<Void, Void, Void> {
//
//		@Override
//		protected Void doInBackground(Void... params) {
//
//			Void result = null;
//
//			try {
//				FileInputStream fis = openFileInput("city_list_cache");
//				ObjectInputStream ois = new ObjectInputStream(fis);
//
//				cityList = (CityList) ois.readObject();
//
//
//				ois.close();
//				fis.close();
//
//			} catch (Exception e) {
//
//				cityList.fetchCities();
//
//				e.printStackTrace();
//			}
//
//			
//
//			return result;
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//
//			initCityItems();
//			
//			mCityListAdapter.notifyDataSetChanged();
//
//			loadingDialog.dismiss();
//			super.onPostExecute(result);
//			mCitySelectIndex.setAdapter(mCityListAdapter);
//			refreshLocation.setClickable(true);
//			
//		}
//	}
//	
//	private class FetchCityNetTask extends AsyncTask<Void, Void, Void> {
//
//		@Override
//		protected Void doInBackground(Void... params) {
//
//			CityList mCityList = new CityList();
//			
//        	mCityList.fetchCities();
//        	
//        	try {
//				FileOutputStream fos = openFileOutput("city_list_cache", MODE_PRIVATE);
//				ObjectOutputStream oos = new ObjectOutputStream(fos);
//				
//				oos.writeObject(mCityList);
//				
//				oos.close();
//				fos.close();
//				
//			} catch (Exception ex) {
//
//				ex.printStackTrace();
//			}
//
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//
//			new FetchCityTask().execute();
//			
//		}
//	}
//
//	@Override
//	protected void onResume() {
//		// TODO Auto-generated method stub
//		// btn_city.setText(GroupSaleGuideActivity.getCurrentCity().getCityName());
//		super.onResume();
//	}
//
//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		isFinish = true;
//		listTags = null;
//		listCityItems = null;
//		listAutoCityItems = null;
//		cityList = null;
//		mCityListAdapter = null;
//		mCitySelectIndex = null;
//		loadingDialog = null;
//		btn_city = null;
//		mLocationListener = null;
//		super.onDestroy();
//	}
//
//	@Override
//	public void finish() {
//    	String cityName = readCityName();
//		if(cityName == null){
//			try {
//				FileOutputStream file = openFileOutput("current_city",
//						MODE_PRIVATE);
//				ObjectOutputStream oos = new ObjectOutputStream(file);
//
//				oos.writeObject(BaseApplication.getCurrentCity().getCityName());
//
//				oos.close();
//				file.close();
//
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}
//		}
//
//		super.finish();
//	}
//	
//	private String readCityName(){
//		
//		String cityName = null;
//		try {
//			FileInputStream file = openFileInput("current_city");
//			ObjectInputStream ois = new ObjectInputStream(file);
//						
//			cityName = (String) ois.readObject();
//
//			ois.close();
//			file.close();
//
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return cityName;
//		
//		
//	}
//	@Override
//	protected void onPause() {
//
//		if ((locMan != null) && (mLocationListener != null)) {
//
//			locMan.removeUpdates(mLocationListener);
//
//		}
//
//		super.onPause();
//	}
//
//}
