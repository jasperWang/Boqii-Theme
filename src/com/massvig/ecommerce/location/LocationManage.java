package com.massvig.ecommerce.location;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.amap.mapapi.location.LocationManagerProxy;
import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.service.MassVigService;
import com.massvig.ecommerce.utilities.MassVigSystemSetting;

/**
 * @author Dujun
 * 
 */
// 定位
public class LocationManage {
	private LocationManagerProxy locationManager = null;
	private LocationListenerProxy mLocationListenerProxy;
	private MapAbcLocationListener mMapAbcLocationListener;
	public Double latitude = 0.0,longitude = 0.0;
	private static LocationManage mLocationManage;
	private String address;
	private MyLocationListener mMyLocationListener;
	

	private LocationManage() {
		// TODO Auto-generated constructor stub
	}
	
	public static LocationManage getInstance() {
		if (mLocationManage == null) {
			mLocationManage = new LocationManage();
		}
		return mLocationManage;
	}
	
   public Boolean startLocation(Context mContext,Boolean isMust,MyLocationListener mMyLocationListener){
	   try{
		   this.mMyLocationListener = mMyLocationListener;
			Boolean isaow = isAllow(mContext);
			if(!isaow && isMust){
				showIsAllowDailog(mContext);
			}else if(isaow){
				stopLocation();
				if(locationManager == null){
					locationManager = LocationManagerProxy.getInstance(mContext);
					mLocationListenerProxy = new LocationListenerProxy(locationManager);
					mMapAbcLocationListener = new MapAbcLocationListener();
				}
				mLocationListenerProxy.startListening(mMapAbcLocationListener, 2000, 10);
				return true;
			}
			return false;
	   }catch(Exception e){
		   
	   }
	   return false;
	}

	
	public void stopLocation(){
		if (locationManager != null) {
			mLocationListenerProxy.stopListening();
			locationManager.destory();
		}
		locationManager = null;
	}
	
	
	public double getLatitude() {
		return latitude;
	}

	
	public double getLongitude() {
		return longitude;
	}

	public void getAddress(MyLocationListener mMyLocationListener, final boolean isShort) {
		if(latitude == 0.0 || longitude == 0.0) return;
		
		new AsyncTask<Object, Object, String>() {
			
			private MyLocationListener mMyLocationListener;
			
			@Override
			protected String doInBackground(Object... params) {
				this.mMyLocationListener = (MyLocationListener)params[2];
				String result = null;
				result = MassVigService.getInstance().getAddByLocation(
						(String)params[0], (String)params[1], isShort);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				address = result;
				if (address == null || address.equals("")) {
					getAddress(this.mMyLocationListener, isShort);
				} else {
					if (this.mMyLocationListener != null) {
						this.mMyLocationListener.address(address);
						this.mMyLocationListener = null;
					}
				}
				super.onPostExecute(result);
			}

		}.execute(String.valueOf(latitude), String.valueOf(longitude),mMyLocationListener);
	}

	
	public Boolean isAllow(Context context) {
		return new MassVigSystemSetting().getLocationAllow(context);
	}

	public void setAllow(Context context, Boolean value) {
		new MassVigSystemSetting().setLocationAllow(context, value);
	}


	public void showIsAllowDailog(final Context context) {
		Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(context.getString(R.string.tips)).setMessage(
				context.getString(R.string.location_permission));
		dialog.setPositiveButton(context.getString(R.string.sure),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						setAllow(context, true);
						if (mMyLocationListener != null) {
							mMyLocationListener.isAllow(true);
						}
					}

				});
		dialog.setNegativeButton(context.getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						if (mMyLocationListener != null) {
							setAllow(context, false);
							mMyLocationListener.isAllow(false);
						}
						return;
					}
				}).setCancelable(false);
		dialog.show();
	}

	class MapAbcLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			if (location != null && latitude == 0.0) {
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				Message msg = new Message();
				if (handler != null) {
					handler.sendMessage(msg);
				}
			}

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			
			if (mMyLocationListener != null) {
				mMyLocationListener.location(latitude, longitude);
			}
			if (locationManager != null) {
				mMyLocationListener = null;
				stopLocation();
			}

		}

	};

	public interface MyLocationListener {
		public void isAllow(Boolean isAllow);

		public void location(double latitude, double longitude);

		public void address(String address);

	}

	

}
