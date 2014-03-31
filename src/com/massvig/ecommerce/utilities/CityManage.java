//package com.massvig.ecommerce.utilities;
//
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//
//import com.massvig.ecommerce.R;
//import com.massvig.ecommerce.activities.BaseApplication;
//import com.massvig.ecommerce.activities.SelectCityActivity;
//import com.massvig.ecommerce.entities.CityList;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.DialogInterface.OnCancelListener;
//import android.content.Intent;
//import android.os.AsyncTask;
//
///**
// * @author zhangbp
// *
// */
//
//public class CityManage {
//	public static final int ACTIVITY_REQUEST_SELECT_CITY = 1001;
//	private CityList mCityList;
//	public CityList getCityListByCache(Context context){
//		try{
//			FileInputStream fis = context.openFileInput("city_list_cache");
//			ObjectInputStream ois = new ObjectInputStream(fis);
//			mCityList = (CityList) ois.readObject();
//			ois.close();
//			fis.close();
//			return mCityList;
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		return null;
//	}
//	
//	public void saveCityListToCache(Context context,CityList mCityList){
//		try{
//			FileOutputStream fos = context.openFileOutput("city_list_cache", Context.MODE_PRIVATE);
//			ObjectOutputStream oos = new ObjectOutputStream(fos);
//			oos.writeObject(mCityList);
//			oos.close();
//			fos.close();
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}
//	
//	public void getCityListByNet(final Context context){
//		mCityList = new CityList();
//		new AsyncTask<Object, Object, Integer>(){
//
//			@Override
//			protected Integer doInBackground(Object... params) {
//				// TODO Auto-generated method stub
//				mCityList.fetchCities();
//				return 1;
//			}
//
//			@Override
//			protected void onPostExecute(Integer result) {
//				// TODO Auto-generated method stub
//				if(result == 1){
//					saveCityListToCache(context,mCityList);
//				}
//				super.onPostExecute(result);
//			}
//			
//		}.execute();
//	}
//	
//	public String getUserSetttingCityName(Context context){
//		/*String cityName = null;
//		try {
//			FileInputStream file = context.openFileInput("current_city");
//			ObjectInputStream ois = new ObjectInputStream(file);
//			cityName = (String) ois.readObject();
//			ois.close();
//			file.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}*/
//		return new MassVigSystemSetting().getCityName(context);
//	}
//	
//	public int getUserSettingCityId(Context context){
//		if(mCityList == null){
//			mCityList = getCityListByCache(context);
//		}
//		String cityName = getUserSetttingCityName(context);
//		if(cityName != null){
//			return mCityList.getCityByName(cityName).getCityId();
//		}
//		return 0;
//	}
//	
//	public Boolean SaveCurrentCity(Context context,String cityName,int cityId){
//		/*try{
//			FileOutputStream file = context.openFileOutput("current_city",Context.MODE_PRIVATE);
//			ObjectOutputStream oos = new ObjectOutputStream(file);
//			oos.writeObject(name);
//			oos.close();
//			file.close();
//			return true;
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		return false;*/
//		MassVigSystemSetting mSystemSetting = new MassVigSystemSetting();
//		mSystemSetting.setCityId(context, cityId);
//		mSystemSetting.setCityName(context, cityName);
//		return true;
//	}
//	
//	private void clickEvent(Context context, String cityName,SetCityDailogCallBack setCityDailogCallBack,DialogInterface dialog){
//		mCityList = getCityListByCache(context);
//		if(mCityList == null){
//			mCityList = new CityList();
//		}
//		BaseApplication.currentCity = mCityList.getCityByName(cityName);
//		SaveCurrentCity(context,BaseApplication.currentCity.getCityName(),BaseApplication.currentCity.getCityId());
//		dialog.dismiss();
//		if(setCityDailogCallBack != null){
//			setCityDailogCallBack.callback(true);
//		}
//	}
//	
//	public void showLocationCityDialog(final Context context,final String cityName,final SetCityDailogCallBack setCityDailogCallBack){
//		new AlertDialog.Builder(context)
//		.setTitle(context.getString(R.string.tips))
//		.setMessage(context.getString(R.string.your_current_city)+cityName+context.getString(R.string.location_shi))
//		.setPositiveButton(context.getString(R.string.sure),
//				new DialogInterface.OnClickListener() {
//
//					public void onClick(DialogInterface dialog,
//							int which) {
//						clickEvent(context, cityName, setCityDailogCallBack, dialog);
////						mCityList = getCityListByCache(context);
////						if(mCityList == null){
////							mCityList = new CityList();
////						}
////						GroupSaleGuideActivity.currentCity = mCityList.getCityByName(cityName);
////						SaveCurrentCity(context,GroupSaleGuideActivity.currentCity.getCityName(),GroupSaleGuideActivity.currentCity.getCityId());
////						dialog.dismiss();
////						if(setCityDailogCallBack != null){
////							setCityDailogCallBack.callback(true);
////						}
//
//					}
//				})
//		.setNegativeButton(context.getString(R.string.change),
//				new DialogInterface.OnClickListener() {
//
//					public void onClick(DialogInterface dialog,
//							int which) {
//						Intent intent = new Intent(context,SelectCityActivity.class);
//						((Activity)context).startActivityForResult(intent,ACTIVITY_REQUEST_SELECT_CITY);
//						dialog.dismiss();
//					}
//
//				}).setOnCancelListener(new OnCancelListener() {
//					
//					@Override
//					public void onCancel(DialogInterface dialog) {
//						// TODO Auto-generated method stub
//						clickEvent(context, cityName, setCityDailogCallBack, dialog);
////						mCityList = getCityListByCache(context);
////						if(mCityList == null){
////							mCityList = new CityList();
////						}
////						GroupSaleGuideActivity.currentCity = mCityList.getCityByName(cityName);
////						SaveCurrentCity(context,GroupSaleGuideActivity.currentCity.getCityName(),GroupSaleGuideActivity.currentCity.getCityId());
////						dialog.dismiss();
////						if(setCityDailogCallBack != null){
////							setCityDailogCallBack.callback(true);
////						}
//					}
//				}).setCancelable(false).show();
//	}
//	
//	public void showDefaultCityDialog(final Context context,final SetCityDailogCallBack setCityDailogCallBack){
//		new AlertDialog.Builder(context)
//		.setTitle(context.getString(R.string.tips))
//		.setMessage(context.getString(R.string.default_city))
//		.setPositiveButton(context.getString(R.string.sure),
//				new DialogInterface.OnClickListener() {
//
//					public void onClick(DialogInterface dialog,
//							int which) {
//						clickEvent(context, context.getString(R.string.shanghai), setCityDailogCallBack, dialog);
////						mCityList = getCityListByCache(context);
////						if(mCityList == null){
////							mCityList = new CityList();
////						}
////						GroupSaleGuideActivity.currentCity = mCityList.getCityByName(context.getString(R.string.shanghai));
////						SaveCurrentCity(context,GroupSaleGuideActivity.currentCity.getCityName(),GroupSaleGuideActivity.currentCity.getCityId());
////						
////						dialog.dismiss();
////						if(setCityDailogCallBack != null){
////							setCityDailogCallBack.callback(true);
////						}
//
//					}
//				})
//		.setNegativeButton(context.getString(R.string.change),
//				new DialogInterface.OnClickListener() {
//
//					public void onClick(DialogInterface dialog,
//							int which) {
//						Intent intent = new Intent(context,SelectCityActivity.class);
//						((Activity)context).startActivityForResult(intent,ACTIVITY_REQUEST_SELECT_CITY);
//						dialog.dismiss();
//					}
//
//				}).setCancelable(false).setOnCancelListener(new OnCancelListener() {
//					
//					@Override
//					public void onCancel(DialogInterface dialog) {
//						// TODO Auto-generated method stub
//						clickEvent(context, context.getString(R.string.shanghai), setCityDailogCallBack, dialog);
////						mCityList = getCityListByCache(context);
////						if(mCityList == null){
////							mCityList = new CityList();
////						}
////						GroupSaleGuideActivity.currentCity = mCityList.getCityByName(context.getString(R.string.shanghai));
////						SaveCurrentCity(context,GroupSaleGuideActivity.currentCity.getCityName(),GroupSaleGuideActivity.currentCity.getCityId());
////						dialog.dismiss();
////						if(setCityDailogCallBack != null){
////							setCityDailogCallBack.callback(true);
////						}
//					}
//				}).show();
//	}
//	
//	public void showCutCityDialog(final Context context,String cityName,final SetCityDailogCallBack setCityDailogCallBack){
//		final String cityNamen = cityName == null ? context.getString(R.string.shanghai) : cityName; 
//		new AlertDialog.Builder(context)
//		.setTitle(context.getString(R.string.tips))
//		.setMessage(context.getString(R.string.current_city_tips))
//		.setPositiveButton(context.getString(R.string.cut_city),
//				new DialogInterface.OnClickListener() {
//
//					public void onClick(DialogInterface dialog,
//							int which) {
//						clickEvent(context, cityNamen, setCityDailogCallBack, dialog);
////						mCityList = getCityListByCache(context);
////						if(mCityList == null){
////							mCityList = new CityList();
////						}
////						GroupSaleGuideActivity.currentCity = mCityList.getCityByName(cityNamen);
////						SaveCurrentCity(context,GroupSaleGuideActivity.currentCity.getCityName(),GroupSaleGuideActivity.currentCity.getCityId());
////						dialog.dismiss();
////						if(setCityDailogCallBack != null){
////							setCityDailogCallBack.callback(true);
////						}
//
//					}
//				})
//		.setNegativeButton(context.getString(R.string.cancel),null).show();
//	}
//	
//	
//	public interface SetCityDailogCallBack{
//		public void callback(Boolean isusered);
//	}
//	
//}
