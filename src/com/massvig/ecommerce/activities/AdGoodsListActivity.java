package com.massvig.ecommerce.activities;

import java.util.ArrayList;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.Goods;
import com.massvig.ecommerce.location.LocationManage;
import com.massvig.ecommerce.location.LocationManage.MyLocationListener;
import com.massvig.ecommerce.managers.GoodsListManager;
import com.massvig.ecommerce.managers.GoodsListManager.LoadListener;
import com.massvig.ecommerce.service.MassVigService;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.widgets.CategoryDialogView;
import com.massvig.ecommerce.widgets.CategoryDialogView.CateGoryDialogCallBack;
import com.massvig.ecommerce.widgets.GoodsAdapter;
import com.massvig.ecommerce.widgets.NetImageView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.TextView;

public class AdGoodsListActivity extends BaseActivity implements OnClickListener {

	private GoodsListManager mManager;
	private ListView listview;
	private GoodsAdapter mAdapter;
	private LinearLayout cateGoryBarBtn1, cateGoryBarBtn2;
	public CategoryDialogView categoryDialogView;
	public String subCategoryName;
	private Double latitude = 0.0, longitude = 0.0;
	private LocationManage mLocationManage;
	private int categoryID = -1, subCategoryID = -1;
	private boolean isScolling;
	private String params = "";
	private BaseApplication app;
	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adgoodslist);
		setTitle(getString(R.string.goodslist));
//		initParam();
		app = (BaseApplication) getApplication();
		params = this.getIntent().getStringExtra("PARAMS");
		initView();
		initCateGoryDialog();
//		initLocation();
	}


	// 初始化定位
//	private void initLocation() {
//		mLocationManage = LocationManage.getInstance();
//		mLocationManage.setAllow(this, true);
//		latitude = mLocationManage.getLatitude();
//		longitude = mLocationManage.getLongitude();
//		if (latitude != null && latitude != 0.0) {
//			mLocationManage.getAddress(mmMyLocationListener, false);
//		} else {
//			Boolean isSucceed = mLocationManage.startLocation(this, false,
//					mmMyLocationListener);
//			ShowMessage(isSucceed);
//		}
//
//	}

//	private void ShowMessage(boolean isSuccess) {
//		if (isSuccess) {
//			Toast.makeText(this, "正在定位", Toast.LENGTH_LONG).show();
//		} else {
//			Toast.makeText(this, "不在定位", Toast.LENGTH_LONG).show();
//		}
//	}

	MyLocationListener mmMyLocationListener = new MyLocationListener() {

		@Override
		public void location(double latitude, double longitude) {
			AdGoodsListActivity.this.latitude = latitude;
			AdGoodsListActivity.this.longitude = longitude;
			if (AdGoodsListActivity.this.latitude != 0.0
					&& AdGoodsListActivity.this.longitude != 0.0) {
				mLocationManage.getAddress(mmMyLocationListener, false);
				mLocationManage.stopLocation();
			}

		}

		@Override
		public void isAllow(Boolean isAllow) {
			if (isAllow) {
				mLocationManage.startLocation(AdGoodsListActivity.this, true,
						this);
				Toast.makeText(AdGoodsListActivity.this, getString(R.string.location_locating),
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(AdGoodsListActivity.this, getString(R.string.not_location),
						Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void address(String address) {
			if (address != null) {
				Toast.makeText(AdGoodsListActivity.this, address,
						Toast.LENGTH_LONG).show();
			}
		}
	};

	// 初始化分类，排序
	private void initCateGoryDialog() {

		if (cateGoryBarBtn1 == null) {
			cateGoryBarBtn1 = (LinearLayout) findViewById(R.id.cate_gory_bar_btn1);
			cateGoryBarBtn2 = (LinearLayout) findViewById(R.id.cate_gory_bar_btn2);
			((TextView) findViewById(R.id.cate_gory_bar_btn3))
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							startActivityForResult(new Intent(
									AdGoodsListActivity.this,
									PriceFilterActivity.class), 1);
						}
					});
			categoryDialogView = (CategoryDialogView) findViewById(R.id.cate_gory_dialog_view);
			cateGoryBarBtn1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					categoryDialogView.showDialog(1);
				}
			});
			cateGoryBarBtn2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					categoryDialogView.showDialog(2);
				}
			});

			categoryDialogView.execute(new CateGoryDialogCallBack() {

				private TextView cateName, orderName;

				private void setBarText(int type, String name) {
					if (cateName == null) {
						cateName = (TextView) cateGoryBarBtn1
								.findViewById(R.id.txt);
						orderName = (TextView) cateGoryBarBtn2
								.findViewById(R.id.txt);
					}
					if (type == 1) {
						cateName.setText(name);
					} else if (type == 2) {
						orderName.setText(name);
					}
				}

				@Override
				public void cateGorycallBack(int parentId, int childId,
						String name) {
					// TODO Auto-generated method stub
					categoryID = parentId;
					subCategoryID = childId;

					if (subCategoryID != -1) {
						categoryID = categoryID == -1 ? subCategoryID : categoryID;
						categoryDialogView.setCateGoryTopView(categoryID, subCategoryName,
								(ImageView) cateGoryBarBtn1.findViewById(R.id.img));
						((TextView) cateGoryBarBtn1.findViewById(R.id.txt))
								.setText(subCategoryName);
					}
					mManager.clearData();
					mAdapter.notifyDataSetChanged();
					mManager.setMcids(categoryID + "");
					mManager.loadData();
					Toast.makeText(AdGoodsListActivity.this,categoryID + "", Toast.LENGTH_SHORT).show();
					setBarText(1, name);
					subCategoryName = name;
				}

				@Override
				public void orderCallBack(int orderId, Drawable leftDrawable,
						String name) {
					// TODO Auto-generated method stub
					mManager.clearData();
					mAdapter.notifyDataSetChanged();
					mManager.setOrderby(orderId);
					mManager.loadData();
					Toast.makeText(AdGoodsListActivity.this,orderId + "", Toast.LENGTH_SHORT).show();
					setBarText(2, name);
				}
			});
		}

		// 设置Category-top的值
		int subCategoryId = subCategoryID;
		if (subCategoryId != -1) {
			int categoryId = categoryID;
			categoryID = categoryId == -1 ? subCategoryId : categoryId;
			categoryDialogView.setCateGoryTopView(categoryId, subCategoryName,
					(ImageView) cateGoryBarBtn1.findViewById(R.id.img));
			((TextView) cateGoryBarBtn1.findViewById(R.id.txt))
					.setText(subCategoryName);
		}

	}

	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(dialog != null && dialog.isShowing())
				dialog.dismiss();
			Toast.makeText(AdGoodsListActivity.this, getString(R.string.no_data), Toast.LENGTH_SHORT).show();
		}};
	
	private void initView() {
		dialog = new ProgressDialog(this);
		dialog.setMessage(getString(R.string.loading_data));
		mManager = new GoodsListManager();
		mManager.keyWord = MassVigUtil.getPreferenceData(this, "KEYWORD", "");
		mManager.setListener(new LoadListener() {

			@Override
			public void LoadSuccess() {
				if(dialog != null && dialog.isShowing())
					dialog.dismiss();
				mManager.refreshGoodsList();
				mAdapter.notifyDataSetChanged();
			}

			@Override
			public void LoadFailed() {
				if(dialog != null && dialog.isShowing())
					dialog.dismiss();
				Toast.makeText(AdGoodsListActivity.this, getString(R.string.no_data), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void NoData() {
				mHandler.sendEmptyMessage(1);
			}
		});
		listview = (ListView) findViewById(R.id.listview);
		mAdapter = new GoodsAdapter(this, mManager.getGoodsList());
		listview.setAdapter(mAdapter);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Goods goods = mManager.getGoods(position);
				startActivity(new Intent(AdGoodsListActivity.this,
						GoodsDetailActivity.class).putExtra("goods", goods));
			}
		});
		listview.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				

				if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
					isScolling = false;
					NetImageView.setIsAutoLoadImage(true);
					ArrayList<NetImageView> imageLists = mAdapter.getImageList();
					for(int i = 0,len = imageLists.size();i < len ;i++){
						if(isScolling == false){
							imageLists.get(i).updateImage();
						}
					}
				}else if(scrollState == OnScrollListener.SCROLL_STATE_FLING){
					isScolling = true;
					NetImageView.setIsAutoLoadImage(false);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem + visibleItemCount >= totalItemCount - 1
						&& totalItemCount > 0) {
					if (!mManager.isRefresh()) {
//						mManager.loadData();
						mManager.loadAdData(params, MassVigService.merchantid, BaseApplication.udid, app.user.sessionID);
					}
				}
			}
		});
		((Button)findViewById(R.id.back)).setOnClickListener(this);
		((Button)findViewById(R.id.finish)).setOnClickListener(this);
		if(dialog != null && !dialog.isShowing())
			dialog.show();
		mManager.loadAdData(params, MassVigService.merchantid, BaseApplication.udid, app.user.sessionID);
	}

	@Override
	protected void onResume() {
		super.onResume();
		app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(this, "SESSIONID", "") : app.user.sessionID;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.finish:
			startActivityForResult(new Intent(
					AdGoodsListActivity.this,
					PriceFilterActivity.class), 1);
			break;
		case R.id.back:
			AdGoodsListActivity.this.finish();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == RESULT_OK) {
//			initView();
			mManager.clearData();
			mAdapter.notifyDataSetChanged();
			mManager.setMaxPrice(data.getStringExtra("maxPirce"));
			mManager.setMinPrice(data.getStringExtra("minPirce"));
			if(dialog != null && !dialog.isShowing())
				dialog.show();
			mManager.loadData();
		}
	}

}
