package com.massvig.ecommerce.activities;

import java.util.ArrayList;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.Goods;
import com.massvig.ecommerce.location.LocationManage;
import com.massvig.ecommerce.location.LocationManage.MyLocationListener;
import com.massvig.ecommerce.managers.GoodsListManager;
import com.massvig.ecommerce.managers.GoodsListManager.LoadListener;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.widgets.CategoryDialogView;
import com.massvig.ecommerce.widgets.GoodsAdapter;
import com.massvig.ecommerce.widgets.NetImageView;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.TextView;

public class GoodsListActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener {

	private GoodsListManager mManager;
	private ListView listview;
	private GoodsAdapter mAdapter;
//	private LinearLayout cateGoryBarBtn1, cateGoryBarBtn2;
	public CategoryDialogView categoryDialogView;
	public String subCategoryName;
	private Double latitude = 0.0, longitude = 0.0;
	private LocationManage mLocationManage;
//	private int categoryID = -1, subCategoryID = -1;
	private boolean isScolling;
	private ProgressDialog dialog;
	private TextView price_order;
	private int priceOrder = 0;
	private RadioGroup radioGroup;
	private boolean isClickPrice = false;
	private boolean isFirst = true;
	private LinearLayout layout;
	private TextView text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.goodslist);
		setTitle(getString(R.string.goodslist));
//		initParam();
		initView();
//		initCateGoryDialog();
//		initLocation();
	}

	@Override
	protected void onResume() {
		super.onResume();
//		if(!isFirst){
//			mManager.keyWord = MassVigUtil.getPreferenceData(this, "KEYWORD", "");
//			mManager.clearData();
//			mManager.isLoadDone = false;
//			if(dialog != null && !dialog.isShowing())
//				dialog.show();
//			mManager.loadData();
//		}
//		isFirst = false;
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
			GoodsListActivity.this.latitude = latitude;
			GoodsListActivity.this.longitude = longitude;
			if (GoodsListActivity.this.latitude != 0.0
					&& GoodsListActivity.this.longitude != 0.0) {
				mLocationManage.getAddress(mmMyLocationListener, false);
				mLocationManage.stopLocation();
			}

		}

		@Override
		public void isAllow(Boolean isAllow) {
			if (isAllow) {
				mLocationManage.startLocation(GoodsListActivity.this, true,
						this);
				Toast.makeText(GoodsListActivity.this, getString(R.string.location_locating),
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(GoodsListActivity.this, getString(R.string.not_location),
						Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void address(String address) {
			if (address != null) {
				Toast.makeText(GoodsListActivity.this, address,
						Toast.LENGTH_LONG).show();
			}
		}
	};

	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(dialog != null && dialog.isShowing())
				dialog.dismiss();
//			Toast.makeText(GoodsListActivity.this, getString(R.string.no_data), Toast.LENGTH_SHORT).show();
		}};
	
	private void initView() {
		((TextView)findViewById(R.id.search)).setOnClickListener(this);
		price_order = (TextView)findViewById(R.id.price_order);
		price_order.setOnClickListener(this);
		radioGroup = (RadioGroup)findViewById(R.id.radio);
		radioGroup.setOnCheckedChangeListener(this);
		dialog = new ProgressDialog(this);
		dialog.setMessage(getString(R.string.loading_data));
		layout = (LinearLayout)findViewById(R.id.no_search_data);
        text = (TextView)findViewById(R.id.search_text);
		mManager = new GoodsListManager();
		mManager.setMcids(this.getIntent().getStringExtra("MCIDS"));
		int orderid = this.getIntent().getIntExtra("ORDERID", GoodsListManager.Comment_DESC);
		int index = 0;
		switch (orderid) {
		case GoodsListManager.Volume_DESC:
			index = 1;
			break;
		case GoodsListManager.Comment_DESC:
			index = 0;
			break;

		default:
			break;
		}
		if(index >= 0)
			((RadioButton)radioGroup.getChildAt(index)).setChecked(true);
		mManager.setOrderby(this.getIntent().getIntExtra("ORDERID", GoodsListManager.Comment_DESC));
		mManager.keyWord = this.getIntent().getStringExtra("KEYWORD");
		mManager.setListener(new LoadListener() {

			@Override
			public void LoadSuccess() {
				if(dialog != null && dialog.isShowing())
					dialog.dismiss();
//				mManager.refreshGoodsList();
				if(mManager.getGoodsList().getCount()>0){
					layout.setVisibility(View.GONE);
					
				}else{
					layout.setVisibility(View.VISIBLE);
					if(!TextUtils.isEmpty(mManager.keyWord) && (mManager.keyWord.length() > 0 && mManager.keyWord.length()<5)){
						text.setText("\""+mManager.keyWord+"\"");
					}else if(!TextUtils.isEmpty(mManager.keyWord) && mManager.keyWord.length() >= 5){
						String s = mManager.keyWord.substring(0, 5);
						text.setText("\""+s+"..."+"\"");
					}
				}
				mAdapter.notifyDataSetChanged();
			}

			@Override
			public void LoadFailed() {
				if(dialog != null && dialog.isShowing())
					dialog.dismiss();
//				Toast.makeText(GoodsListActivity.this, getString(R.string.no_data), Toast.LENGTH_SHORT).show();
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
				if(goods != null)
				startActivityForResult(new Intent(GoodsListActivity.this,
						GoodsDetailActivity.class).putExtra("goods", goods), 2);
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
					if(view.getLastVisiblePosition() == (view.getCount() - 1)){
						if (!mManager.isRefresh()) {
							mManager.isScroll = true;
							mManager.loadData();
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
//				if (firstVisibleItem + visibleItemCount >= totalItemCount - 1
//						&& totalItemCount > 0) {
//					if (!mManager.isRefresh()) {
//						mManager.loadData();
//					}
//				}
			}
		});
		((Button)findViewById(R.id.finish)).setOnClickListener(this);
		((Button)findViewById(R.id.back)).setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MassVigUtil.setPreferenceStringData(GoodsListActivity.this, "KEYWORD", "");
		mManager.keyWord = "";
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.search:
			startActivity(new Intent(this, SearchActivity.class));
			break;
		case R.id.back:
			finish();
			break;
		case R.id.finish:
			startActivityForResult(new Intent(
					GoodsListActivity.this,
					PriceFilterActivity.class).putExtra("min", Integer.valueOf(mManager.getMinPrice())).putExtra("max", Integer.valueOf(mManager.getMaxPrice())), 1);
			break;
		case R.id.price_order:
//			for (int i = 0; i < radioGroup.getChildCount(); i++) {
//				RadioButton b = (RadioButton) radioGroup.getChildAt(i);
//				b.setChecked(false);
//			}
			isClickPrice = true;
			radioGroup.clearCheck();
//			mManager.clearData();
			mManager.isLoadDone = false;
			if(priceOrder != 1){
				priceOrder = 1;
				price_order.setBackgroundResource(R.drawable.price_01);
				mManager.setOrderby(1);
			}else{
				priceOrder = 2;
				price_order.setBackgroundResource(R.drawable.price_02);
				mManager.setOrderby(2);
			}
			if(dialog != null && !dialog.isShowing())
				dialog.show();
			mManager.loadData();
			isClickPrice = false;
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == RESULT_OK) {
//			mManager.clearData();
//			mManager.isLoadDone = false;
			mManager.setMaxPrice(data.getStringExtra("maxPirce"));
			mManager.setMinPrice(data.getStringExtra("minPirce"));
			mManager.keyWord = MassVigUtil.getPreferenceData(this, "KEYWORD", "");
//			mManager.clearData();
			mManager.isLoadDone = false;
			if(dialog != null && !dialog.isShowing())
				dialog.show();
			mManager.loadData();
//			if(dialog != null && !dialog.isShowing())
//				dialog.show();
//			mManager.loadData();
		}else if(requestCode == 2 && resultCode == RESULT_OK){
			((MainTabActivity)getParent()).setTabHostIndex(3);
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (radioGroup.getCheckedRadioButtonId() != -1 && !isClickPrice) {
//			mManager.clearData();
			mManager.isLoadDone = false;
			switch (checkedId) {
			case R.id.newest:
				mManager.setOrderby(GoodsListManager.Comment_DESC);
				break;
			case R.id.saled:
				mManager.setOrderby(GoodsListManager.Volume_DESC);
				break;

			default:
				break;
			}
			price_order.setBackgroundResource(R.drawable.price_default);
			if (dialog != null && !dialog.isShowing())
				dialog.show();
			mManager.loadData();
		}
	}

}
