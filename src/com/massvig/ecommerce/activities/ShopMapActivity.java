package com.massvig.ecommerce.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Align;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.core.OverlayItem;
import com.amap.mapapi.map.ItemizedOverlay;
import com.amap.mapapi.map.MapActivity;
import com.amap.mapapi.map.MapController;
import com.amap.mapapi.map.MapView;
import com.amap.mapapi.map.Overlay;
import com.amap.mapapi.map.Projection;
import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.Merchant;
import com.massvig.ecommerce.location.LocationManage;
import com.massvig.ecommerce.location.LocationManage.MyLocationListener;
import com.massvig.ecommerce.managers.MapManager;
import com.massvig.ecommerce.managers.MapManager.LoadListener;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.widgets.MerchantAdapter;
import com.massvig.ecommerce.widgets.NetImageView;

public class ShopMapActivity extends MapActivity implements OnClickListener, LoadListener {

	public MapView mMapView;
	public LocationManage mLocationManage;
	public Double latitude = 0.0, longitude = 0.0;
	public GoToLocationOverlay myCurrentLation, myGoToLation;
	public ArrayList<String> areas = new ArrayList<String>();
	public ArrayList<String> streets = new ArrayList<String>();
	private ListView listview;
	public RelativeLayout mapBox;
	public LinearLayout listBox;
	private MapManager manager;
	private BaseApplication app;
	private MerchantAdapter adapter;
	public MapController mMapController;
	public MyOverItem myOverItem;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.shopmap);
		app = (BaseApplication) getApplication();
		manager = new MapManager();
		manager.setListener(this);
		initView();
		initMapView();
	}

	private void initView() {
		((Button)findViewById(R.id.back)).setOnClickListener(this);
		mapBox = (RelativeLayout)findViewById(R.id.browsing_near_map_view_re);
		listBox = (LinearLayout)findViewById(R.id.browsing_map_deal_list);
		mMapView = (MapView) findViewById(R.id.browsing_near_mapview_con_id);
		listview = (ListView)findViewById(R.id.listview);
		adapter = new MerchantAdapter(this, manager.list);
		listview.setAdapter(adapter);
		mMapView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				v.requestFocus();
				return false;
			}
		});
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Merchant m = manager.list.getMerchant(arg2);
				GeoPoint gpt = new GeoPoint((int) (m.Lat * 1e6),
						(int) (m.Lon * 1e6));
				if(mMapController == null)
					mMapController = mMapView.getController();
				mMapController.setZoom(14);
				mMapController.animateTo(gpt);
			}});
		listview.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				v.requestFocus();
				return false;
			}
		});
		listview.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus){
					((LinearLayout.LayoutParams)mapBox.getLayoutParams()).weight = 3;
					((LinearLayout.LayoutParams)listBox.getLayoutParams()).weight = 5;
				}else{
					((LinearLayout.LayoutParams)mapBox.getLayoutParams()).weight = 5;
					((LinearLayout.LayoutParams)listBox.getLayoutParams()).weight = 3;
				}
			}
		});
		((Button) findViewById(R.id.my_location_button_id)).setOnClickListener(this);
		initLocation();
	}

	// 初始化定位
	private void initLocation() {
		mLocationManage = LocationManage.getInstance();
		mLocationManage.setAllow(this, true);
		latitude = mLocationManage.getLatitude();
		longitude = mLocationManage.getLongitude();
		if (latitude != null && latitude != 0.0) {
//			mLocationManage.getAddress(mmMyLocationListener, false);
			GeoPoint gpt = new GeoPoint((int) (latitude * 1e6),
					(int) (longitude * 1e6));
			drawMycurrentLation(gpt);
			mLocationManage.stopLocation();
			manager.lat = latitude;
			manager.lon = longitude;
			app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(this, "SESSIONID", "") : app.user.sessionID;
			manager.GetAroundMerchantStore(app.user.sessionID);
		} else {
			Boolean isSucceed = mLocationManage.startLocation(this, false,
					mmMyLocationListener);
			ShowMessage(isSucceed);
		}

	}

	MyLocationListener mmMyLocationListener = new MyLocationListener() {

		@Override
		public void location(double latitude, double longitude) {
			ShopMapActivity.this.latitude = latitude;
			ShopMapActivity.this.longitude = longitude;
			if (ShopMapActivity.this.latitude != 0.0
					&& ShopMapActivity.this.longitude != 0.0) {
//				mLocationManage.getAddress(mmMyLocationListener, false);
				GeoPoint gpt = new GeoPoint((int) (latitude * 1e6),
						(int) (longitude * 1e6));
				drawMycurrentLation(gpt);
				mLocationManage.stopLocation();
				manager.lat = latitude;
				manager.lon = longitude;
				app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(ShopMapActivity.this, "SESSIONID", "") : app.user.sessionID;
				manager.GetAroundMerchantStore(app.user.sessionID);
			}

		}

		@Override
		public void isAllow(Boolean isAllow) {
			if (isAllow) {
				mLocationManage.startLocation(ShopMapActivity.this, true, this);
				Toast.makeText(ShopMapActivity.this, getString(R.string.location_locating), Toast.LENGTH_LONG)
						.show();
			} else {
				Toast.makeText(ShopMapActivity.this, getString(R.string.not_location), Toast.LENGTH_LONG)
						.show();
			}
		}

		@Override
		public void address(String address) {
			if (address != null) {
				Toast.makeText(ShopMapActivity.this, address, Toast.LENGTH_LONG)
						.show();
			}
		}
	};

	/**
	 * 多个overlay显示时通过ItemizedOverlay
	 * @author zbp
	 * 
	 */
	class MyOverItem extends ItemizedOverlay<OverlayItem> {
		private List<OverlayItem> overlayItems = new ArrayList<OverlayItem>();
		private Drawable marker;
		private int drawableHeight;
		private ShopMapActivity mContext;
		public LinearLayout viewPop;
		private TextView  mPopTextView_local,shopName_local;

		public MyOverItem(Drawable marker, Context context) {
			super(boundCenterBottom(marker));
			this.marker = marker;
			
			BitmapFactory.Options opts = new BitmapFactory.Options(); 
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeResource(getResources(),R.drawable.deal_map_view_location,opts);
			drawableHeight = opts.outHeight;
			
			this.mContext = (ShopMapActivity) context;
			
			viewPop = (LinearLayout) getLayoutInflater().inflate(
					R.layout.browsing_deals_near_map_item_content, null);
			mPopTextView_local = (TextView) viewPop.findViewById(R.id.deal_map_item_content);
			shopName_local = (TextView) viewPop.findViewById(R.id.shop_name_content);
			
			mMapView.addView(viewPop, new MapView.LayoutParams(
					MapView.LayoutParams.WRAP_CONTENT,
					MapView.LayoutParams.WRAP_CONTENT, null,
					MapView.LayoutParams.BOTTOM_CENTER));
			
			MapView.LayoutParams geoLP = (MapView.LayoutParams)viewPop.getLayoutParams();
			geoLP.y -= drawableHeight;
			viewPop.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					listview.performItemClick(null, Integer.parseInt(v.getTag().toString()), 0);
					v.setVisibility(View.INVISIBLE);
				}
			});
			
		}
		public void clearOverlay(){
			overlayItems.removeAll(overlayItems);
			populate();
			setLastFocusedIndex(-1);
		}
		public void setOverlayItem(List<OverlayItem> overlayItems){
			if(overlayItems != null && overlayItems.size() > 0){	
				this.overlayItems = overlayItems;
			}else{
				this.overlayItems = new ArrayList<OverlayItem>();
			}
			populate();
			setLastFocusedIndex(-1);
		}
		
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			if(!shadow){
	            super.draw(canvas, mapView, false);
	        }			
			boundCenterBottom(marker);
			Projection projection = mapView.getProjection();
			for (int index = size() - 1; index >= 0; index--) { // 遍历GeoList
				OverlayItem overLayItem = getItem(index); // 得到给定索引的item
				String title = overLayItem.getTitle();
				// 把经纬度变换到相对于MapView左上角的屏幕像素坐标
				Point point = projection.toPixels(overLayItem.getPoint(), null);
				Paint paintText = new Paint();
				paintText.setColor(Color.WHITE);
				paintText.setTextSize(14);
				paintText.setTextAlign(Align.CENTER);
				canvas.drawText(title.length() > 5 ? title.substring(0, 5) : title, point.x, point.y - 25, paintText); // 绘制文本
			}
			
		}

		@Override
		protected OverlayItem createItem(int i) {
			return overlayItems.get(i);
		}

		@Override
		public int size() {
			return overlayItems.size();
		}

		@Override
		// 处理当点击事件
		// mapview的onTouch事件会传播到overlay的 onTouch方法 通过点击范围可以确定触发哪个overlay的onTap
		protected boolean onTap(int i) {
			
				setFocus(overlayItems.get(i));
				MapView.LayoutParams geoLP = (MapView.LayoutParams)viewPop.getLayoutParams();
				geoLP.point = overlayItems.get(i).getPoint();
				mContext.mMapView.updateViewLayout(viewPop, geoLP);
				viewPop.setVisibility(View.VISIBLE);
				viewPop.setTag(String.valueOf(i));
				mMapController.setZoom(14);
				mMapController.animateTo(geoLP.point);
				Merchant merchant = manager.list.getMerchant(i);
				shopName_local.setText(merchant.StoreName);
				mPopTextView_local.setText(mContext.getString(R.string.distance, ((int)(Double.valueOf((((int)merchant.Distance) / 1000.0) + "") * 10)) / 10.0 + ""));
			
			return false;
		}
		@Override
		public boolean onTouchEvent(MotionEvent event, MapView mapView) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
	public class LongPressOverlay extends Overlay{
		private GestureDetector gestureScanner = new GestureDetector(new OnGestureListener() {
			
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				// TODO Auto-generated method stub
				if(myOverItem != null){
					myOverItem.viewPop.setVisibility(View.GONE);
				}
				return false;
			}
			
			@Override
			public void onShowPress(MotionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
					float distanceY) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onLongPress(MotionEvent e) {
				// TODO Auto-generated method stub
				
				
				if(mMapView != null){
					List<Overlay> mapOverlays = mMapView.getOverlays();
					if(mapOverlays.contains(myCurrentLation)){
						mapOverlays.remove(myCurrentLation);
					}
					
					if(myOverItem != null){
						myOverItem.clearOverlay();
						myOverItem.viewPop.setVisibility(View.GONE);
					}
				}
				
				GeoPoint gpt = mMapView.getProjection().fromPixels((int) e.getX(), (int) e.getY());
				drawgoToOverlay(gpt);
				latitude = gpt.getLatitudeE6()/1e6;
				longitude = gpt.getLongitudeE6()/1e6;
				manager.lat = gpt.getLatitudeE6()/1e6;
				manager.lon = gpt.getLongitudeE6()/1e6;
//				mLocationManage.getAddress(mmMyLocationListener, false);
				manager.list.clearmerchantList();
				app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(ShopMapActivity.this, "SESSIONID", "") : app.user.sessionID;
				manager.GetAroundMerchantStore(app.user.sessionID);
			}
			
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
					float velocityY) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean onDown(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		
		
		@Override
		public boolean onTouchEvent(MotionEvent event, MapView mapView) {
			return gestureScanner.onTouchEvent(event);
		}
		
	}

	//绘制商家位图
	private void drawOverlayItem() {
		if(manager.list != null){
			
			List<OverlayItem> overlayItems = new ArrayList<OverlayItem>();
			for(int i = 0,len = manager.list.getCount();i < len;i++){
				Merchant m = manager.list.getMerchant(i);
				if (m != null) {
					//
					GeoPoint lgeopoint;
					lgeopoint = new GeoPoint((int) (Float.valueOf(m.Lat + "") * 1000000),(int) (Float.valueOf(m.Lon + "") * 1000000));
					OverlayItem oi = new OverlayItem(lgeopoint, "", "");
					overlayItems.add(oi);
				}
			}
			if(myOverItem == null){
				myOverItem = new MyOverItem(getResources().getDrawable(R.drawable.deal_map_view_location), this);
				if(mMapView != null && mMapView.getOverlays() != null)
					mMapView.getOverlays().add(myOverItem); // 添加ItemizedOverlay实例到mMapView
			}
			myOverItem.setOverlayItem(overlayItems);
			mMapView.invalidate();
			
		}
	
	}
	
	//初始化地图
	private void initMapView(){
		mMapController = mMapView.getController();
		double lat = mLocationManage.getLatitude();
		double lng = mLocationManage.getLongitude();
		if(mLocationManage != null && lat > 0.0 && lng > 0.0){
			GeoPoint gpt = new GeoPoint((int) (lat * 1e6),(int) (lng * 1e6));
			drawMycurrentLation(gpt);
		}
		mMapView.getOverlays().add(new LongPressOverlay());
	}
	
	// 绘制当前位置
	private void drawMycurrentLation(GeoPoint geoPoint) {
		List<Overlay> mapOverlays = mMapView.getOverlays();
		if (myCurrentLation == null) {
			myCurrentLation = new GoToLocationOverlay(
					BitmapFactory.decodeResource(getResources(),
							R.drawable.browsing_deals_near_map_location_bg1));
			mapOverlays.add(myCurrentLation);
		} else if (!mapOverlays.contains(myCurrentLation)) {
			mapOverlays.add(myCurrentLation);
		}

		myCurrentLation.setGeoPoint(geoPoint);
		mMapView.getController().setZoom(14);
		mMapView.getController().animateTo(geoPoint);
	}

	// 绘制单个点
	class GoToLocationOverlay extends Overlay {
		private GeoPoint geoPointTemp = null;
		private Bitmap bmpCompany;

		public GoToLocationOverlay(Bitmap bmpCompany) {
			this.bmpCompany = bmpCompany;
		}

		public void setGeoPoint(GeoPoint geoPoint) {
			this.geoPointTemp = geoPoint;
			mMapView.invalidate();
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);

			Paint companypaint = new Paint();
			Point companyScreenCoords = new Point();
			if (geoPointTemp != null) {
				mapView.getProjection().toPixels(geoPointTemp,
						companyScreenCoords);
			}
			companypaint.setStrokeWidth(1);
			companypaint.setARGB(255, 255, 0, 0);
			companypaint.setStyle(Paint.Style.STROKE);

			canvas.drawBitmap(bmpCompany,
					companyScreenCoords.x - bmpCompany.getWidth() / 2,
					companyScreenCoords.y - bmpCompany.getHeight(),
					companypaint);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		app = (BaseApplication) getApplication();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.my_location_button_id:
			
			if(mMapView != null){
				List<Overlay> mapOverlays = mMapView.getOverlays();
				if(mapOverlays.contains(myGoToLation)){
					mapOverlays.remove(myGoToLation);
				}
				
				if(myOverItem != null){
					myOverItem.clearOverlay();
				}
			}
			manager.list.clearmerchantList();
			Boolean isSucceed = mLocationManage.startLocation(
					ShopMapActivity.this, true, mmMyLocationListener);
			ShowMessage(isSucceed);
			break;
		case R.id.back:
			finish();
			break;
		default:
			break;
		}
	}

	// 绘制gotoLation 的位置
	private void drawgoToOverlay(GeoPoint geoPoint) {
		List<Overlay> mapOverlays = mMapView.getOverlays();
		if (myGoToLation == null) {
			myGoToLation = new GoToLocationOverlay(
					BitmapFactory.decodeResource(getResources(),
							R.drawable.browsing_deals_near_map_location_bg));
			mapOverlays.add(myGoToLation);
		} else if (!mapOverlays.contains(myGoToLation)) {
			mapOverlays.add(myGoToLation);
		}

		myGoToLation.setGeoPoint(geoPoint);
		mMapView.getController().setZoom(14);
		mMapView.getController().animateTo(geoPoint);
	}

	// 初始化商圈
//	private void initArea() {
//
//		new AsyncTask<Integer, Void, String>() {
//			@Override
//			protected String doInBackground(Integer... params) {
//				String result = "";
//				result = MassVigService.getInstance().getCityCbdList(params[0]);
//				return result;
//			}
//
//			@Override
//			protected void onPostExecute(String result) {
//				super.onPostExecute(result);
//				if (!TextUtils.isEmpty(result)) {
//					try {
//						areas.clear();
//						streets.clear();
//						JSONArray citylist = new JSONArray(result);
//						for (int i = 0; i < citylist.length(); i++) {
//							JSONObject object = citylist.getJSONObject(i);
//							areas.add(object.getString("SubLoc"));
//							streets.add(object.getString("CBDList"));
//						}
//
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//				} else {
//					initArea();
//				}
//			}
//
//		}.execute(new MassVigSystemSetting().getCityId(this));
//
//		((Button) findViewById(R.id.my_mapview_btn_addr))
//				.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						AlertDialog.Builder builder = new AlertDialog.Builder(
//								ShopMapActivity.this);
//						builder.create().setCanceledOnTouchOutside(true);
//						if (areas != null && areas.size() > 0) {
//							builder.setAdapter(
//									new SelectAreaAdapter(ShopMapActivity.this,
//											areas, true),
//									new DialogInterface.OnClickListener() {
//
//										@Override
//										public void onClick(
//												DialogInterface dialog,
//												int which) {// 第一层
//
//											ArrayList<String> stree = new ArrayList<String>();
//											final ArrayList<CityCbd> cbdlist = new ArrayList<CityCbd>();
//											if (streets != null
//													&& streets.size() == areas
//															.size()
//													&& !TextUtils.isEmpty(streets
//															.get(which))) {
//												try {
//													JSONArray array = new JSONArray(
//															streets.get(which));
//													for (int i = 0; i < array
//															.length(); i++) {
//														JSONObject o = array
//																.getJSONObject(i);
//														stree.add(o
//																.getString("CBD"));
//														CityCbd cbd = new CityCbd();
//														cbd.street = o
//																.getString("CBD");
//														cbd.lat = o
//																.getDouble("Latitude");
//														cbd.lng = o
//																.getDouble("Longitude");
//														cbdlist.add(cbd);
//													}
//												} catch (JSONException e) {
//													e.printStackTrace();
//												}
//												AlertDialog.Builder builder = new AlertDialog.Builder(
//														ShopMapActivity.this);
//												builder.create()
//														.setCanceledOnTouchOutside(
//																true);
//												builder.setAdapter(
//														new SelectAreaAdapter(
//																ShopMapActivity.this,
//																stree, false),
//														new DialogInterface.OnClickListener() {
//
//															@Override
//															public void onClick(
//																	DialogInterface dialog,
//																	int which) {
//																latitude = cbdlist
//																		.get(which).lat;
//																longitude = cbdlist
//																		.get(which).lng;
//																mLocationManage.latitude = latitude;
//																mLocationManage.longitude = longitude;
////																mLocationManage
////																		.getAddress(
////																				mmMyLocationListener,
////																				false);
//
//																((TextView) findViewById(R.id.browsing_map_deal_quaninty))
//																		.setText(R.string.yse_locationing);
//																GeoPoint gpt = new GeoPoint(
//																		(int) (latitude * 1e6),
//																		(int) (longitude * 1e6));
//																drawgoToOverlay(gpt);
//																// TODO
//																// getDealList();
//															}
//														}).show();
//											}
//										}
//									}).show();
//						}
//					}
//				});
//	}

	private void ShowMessage(boolean isSuccess) {
		if (isSuccess) {
			Toast.makeText(ShopMapActivity.this, getString(R.string.location_locating), Toast.LENGTH_LONG)
					.show();
		} else {
			Toast.makeText(ShopMapActivity.this, getString(R.string.not_location), Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	public void Success() {
		if (mMapView != null) {
			drawOverlayItem();
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void Failed() {
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mLocationManage.stopLocation();
		NetImageView.clearCache();
	}

}
