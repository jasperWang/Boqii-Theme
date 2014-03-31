//package com.massvig.ecommerce.activities;
//dsa
//import java.util.Timer;
//import java.util.TimerTask;
//dsadsa
//import com.massvig.ecommerce.boqi.R;
//import com.massvig.ecommerce.entities.Advertise;
//import com.massvig.ecommerce.entities.Goods;
//import com.massvig.ecommerce.managers.MainActivityManager;
//import com.massvig.ecommerce.managers.MainActivityManager.LoadListener;
//import com.massvig.ecommerce.utilities.MassVigContants;
//import com.massvig.ecommerce.utilities.MassVigExit;
//import com.massvig.ecommerce.utilities.MassVigUtil;
//import com.massvig.ecommerce.utilities.Utility;
//import com.massvig.ecommerce.widgets.AdGallery;
//import com.massvig.ecommerce.widgets.MassVigData;
//import com.massvig.ecommerce.widgets.NetImageView;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.os.SystemClock;
//import android.text.TextUtils;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.inputmethod.EditorInfo;
//import android.view.inputmethod.InputMethodManager;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Gallery;
//import android.widget.GridView;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView.OnEditorActionListener;
//import android.widget.Toast;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.Gallery.LayoutParams;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//public class CopyOfMainActivity extends Activity implements OnClickListener, LoadListener {
//	private MainActivityManager mManager;
//	private AdGallery gallery;
//	private LinearLayout dotLayout;
//	private MyImageAdapter mImageAdapter;
//	private MainAdapter mMainAdapter;
//	private SecondAdapter mSecondAdapter;
//	private int imageIndex = 0;
//	private Timer mTimer;
//	private TimerTask task;
//	private static final int DELAY = 8000;
//	private static final int IMAGE = 1;
//	private GridView grid1, grid2;
//	private EditText edit_content;
//	public static final int WEBURL = 1;
//	public static final int PRODUCTID = 2;
//	public static final int GOODSLIST = 3;
//	public static final int CAMPAIGN = 4;
//	private LayoutInflater mInflater;
//	private MassVigExit exit = new MassVigExit();
////	private TranslateAnimation mAnimation;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.main);
//		mInflater = LayoutInflater.from(this);
//		mManager = new MainActivityManager(this);
//		mManager.setListener(this);
//		initView();
//	}
//
//	private void pressAgainExit(){
//		if(exit.isExit()){
//			finish();
//		}else{
//			Toast.makeText(this, getString(R.string.exit_message), Toast.LENGTH_SHORT).show();
//			exit.doExitInOneSecond();
//		}
//	}
//
//	@Override
//	public boolean onKeyUp(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			pressAgainExit();
//			return true;
//		}
//		return super.onKeyUp(keyCode, event);
//	}
//
//	MotionEvent e1 = MotionEvent.obtain(  
//	SystemClock.uptimeMillis(),
//	SystemClock.uptimeMillis(),
//	MotionEvent.ACTION_DOWN, 322.25406f, 108.34271f, 0);
//	MotionEvent e2 = MotionEvent.obtain(
//	SystemClock.uptimeMillis(),
//	SystemClock.uptimeMillis(),
//	MotionEvent.ACTION_MOVE, 146.52338f, 122.55939f, 0);
//
//	private Handler mHandler = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			switch (msg.what) {
//			case IMAGE:
//
//				if(!gallery.isTouch){
//				imageIndex++;
////				gallery.setSelection(imageIndex);
//				gallery.setAnimationDuration(500);
//				gallery.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
////				gallery.onFling(e1, e2, -945, 0);
////		        int keyCode = KeyEvent.KEYCODE_DPAD_RIGHT; 
////		        onKeyDown(keyCode, null); 
//				if(mManager.imageItemsList.size() > 0)
//					setDotImage(imageIndex % mManager.imageItemsList.size());
//				}
//				break;
//
//			default:
//				break;
//			}
//		}
//
//	};
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//		mTimer = new Timer();
//		task = new TimerTask() {
//			@Override
//			public void run() {
//				mHandler.sendEmptyMessage(IMAGE);
//			}
//		};
//		mTimer.schedule(task, DELAY, DELAY);
//	}
//
//	@Override
//	protected void onPause() {
//		super.onPause();
//		if (task != null) {
//			task.cancel();
//			task = null;
//		}
//		if (mTimer != null) {
//			mTimer.purge();
//			mTimer.cancel();
//			mTimer = null;
//		}
//	}
//
//	public void collapseSoftInputMethod(EditText inputText){
//		InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
//		imm.hideSoftInputFromWindow(inputText.getWindowToken(), 0); 
//	}
//	
//	private void initView() {
////		mAnimation = new TranslateAnimation(0, -getWindowManager().getDefaultDisplay().getWidth(), 0, 0);
////		mAnimation.setAnimationListener(new AnimationListener() {
////
////			@Override
////			public void onAnimationEnd(Animation animation) {
////
////			}
////
////			@Override
////			public void onAnimationStart(Animation animation) {
////
////			}
////
////			@Override
////			public void onAnimationRepeat(Animation animation) {
////				
////			}
////		});
////		mAnimation.setDuration(5000);
////		mAnimation.setFillAfter(true);
//		edit_content = (EditText)findViewById(R.id.search_edit);
//		edit_content.setOnEditorActionListener(new OnEditorActionListener() {
//			
//			@Override
//			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//				// TODO Auto-generated method stub
//				if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED){
//					if(!TextUtils.isEmpty(edit_content.getText().toString()))
//					{
//						collapseSoftInputMethod(edit_content);
//						MassVigUtil.setPreferenceStringData(CopyOfMainActivity.this, "KEYWORD", edit_content.getText().toString());
//						((MainTabActivity)getParent()).setTabHostIndex(1);
//						return true;
//					}else{
//						Toast.makeText(CopyOfMainActivity.this, getString(R.string.no_key), Toast.LENGTH_SHORT).show();
//					}
//				}
//				return false;
//			}
//		});
//		((Button)findViewById(R.id.search_btn)).setOnClickListener(this);
//		grid1 = (GridView) findViewById(R.id.grid1);
//		grid2 = (GridView) findViewById(R.id.grid2);
//		((Button) findViewById(R.id.shopmap)).setOnClickListener(this);
//		((Button) findViewById(R.id.more)).setOnClickListener(this);
//		dotLayout = (LinearLayout) findViewById(R.id.dot_images);
//		gallery = (AdGallery) findViewById(R.id.gallery);
//		mImageAdapter = new MyImageAdapter();
//		mMainAdapter = new MainAdapter();
//		mSecondAdapter = new SecondAdapter();
//		gallery.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				if (mManager != null && mManager.imageItemsList.size() > 0) {
//					Advertise ad = mManager.imageItemsList.get(position % mManager.imageItemsList.size());
//					if(ad.LinkType == WEBURL){
//						startActivity(new Intent(CopyOfMainActivity.this,
//								WebViewActivity.class).putExtra("URL", ad.Link));
//					}else if(ad.LinkType == PRODUCTID){
//						Goods good = new Goods();
//						good.productID = Integer.valueOf(ad.Link);
//						startActivity(new Intent(CopyOfMainActivity.this,
//								GoodsDetailActivity.class).putExtra("goods", good));
//					}else if(ad.LinkType == GOODSLIST){
//						String params = ad.Link;
//						startActivity(new Intent(CopyOfMainActivity.this, AdGoodsListActivity.class).putExtra("PARAMS", params));
//					}else if(ad.LinkType == CAMPAIGN){
//						try {
//							int camID = Integer.valueOf(ad.Link);
//							startActivity(new Intent(CopyOfMainActivity.this, ActionDetailActivity.class).putExtra("CAMPAIGNID", camID));
//						} catch (Exception e) {
//							// TODO: handle exception
//						}
//					}
//					
//				}
//			}
//		});
//		gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view,
//					int position, long id) {
//				gallery.isTouch = false;
//				if(mManager.imageItemsList.size() > 0)
//					setDotImage(position % mManager.imageItemsList.size());
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> parent) {
//
//			}
//		});
//		gallery.setAdapter(mImageAdapter);
//		grid1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				if (mManager.mainImageList.size() > 0) {
//					Advertise ad = mManager.mainImageList.get(position % mManager.mainImageList.size());
//					if (ad.LinkType == WEBURL) {
//						startActivity(new Intent(CopyOfMainActivity.this,
//								WebViewActivity.class).putExtra("URL", ad.Link));
//					} else if (ad.LinkType == PRODUCTID) {
//						Goods good = new Goods();
//						good.productID = Integer.valueOf(ad.Link);
//						startActivity(new Intent(CopyOfMainActivity.this,
//								GoodsDetailActivity.class).putExtra("goods",
//								good));
//					}else if(ad.LinkType == GOODSLIST){
//						String params = ad.Link;
//						startActivity(new Intent(CopyOfMainActivity.this, AdGoodsListActivity.class).putExtra("PARAMS", params));
//					}else if(ad.LinkType == CAMPAIGN){
//						int camID = Integer.valueOf(ad.Link);
//						startActivity(new Intent(CopyOfMainActivity.this, ActionDetailActivity.class).putExtra("CAMPAIGNID", camID));
//					}
//				}
//			}
//		});
//		grid1.setAdapter(mMainAdapter);
//		new Utility().setGridView1HeightBasedOnChildren(grid1);
//		grid2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				if (mManager.secondImageList.size() > 0) {
//					Advertise ad = mManager.secondImageList.get(position % mManager.secondImageList.size());
//					if (ad.LinkType == WEBURL) {
//						startActivity(new Intent(CopyOfMainActivity.this,
//								WebViewActivity.class).putExtra("URL", ad.Link));
//					} else if (ad.LinkType == PRODUCTID) {
//						Goods good = new Goods();
//						good.productID = Integer.valueOf(ad.Link);
//						startActivity(new Intent(CopyOfMainActivity.this,
//								GoodsDetailActivity.class).putExtra("goods",
//								good));
//					}else if(ad.LinkType == GOODSLIST){
//						String params = ad.Link;
//						startActivity(new Intent(CopyOfMainActivity.this, AdGoodsListActivity.class).putExtra("PARAMS", params));
//					}else if(ad.LinkType == CAMPAIGN){
//						int camID = Integer.valueOf(ad.Link);
//						startActivity(new Intent(CopyOfMainActivity.this, ActionDetailActivity.class).putExtra("CAMPAIGNID", camID));
//					}
//				}
//			}
//		});
//		grid2.setAdapter(mSecondAdapter);
//		new Utility().setGridView2HeightBasedOnChildren(grid2);
//		MassVigData.getinstance(CopyOfMainActivity.this).execute(CopyOfMainActivity.this);
//		MassVigUtil.setPreferenceStringData(CopyOfMainActivity.this, "AD", MassVigData.getinstance(CopyOfMainActivity.this).getAdCache());
//		mManager.SetData();
//	}
//
//	protected void setDotImage(int arg0) {
//		if (arg0 < dotLayout.getChildCount()) {
//			for (int i = 0; i < dotLayout.getChildCount(); i++) {
//				dotLayout.getChildAt(i).setBackgroundResource(R.drawable.ic_dot_black);
//			}
//			dotLayout.getChildAt(arg0).setBackgroundResource(R.drawable.ic_dot_red);
//			imageIndex = arg0;
//		}
//	}
//
//	public class MyImageAdapter extends BaseAdapter {
//
//		@Override
//		public int getCount() {
//			return Integer.MAX_VALUE;
//		}
//
//		@Override
//		public Object getItem(int position) {
//			if(mManager.imageItemsList.size() > 0)
//				return mManager.imageItemsList.get(position % mManager.imageItemsList.size());
//			else
//				return new Advertise();
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return position;
//		}
//
//		public String getItemUrl(int position) {
//			if(mManager.imageItemsList.size() > 0){
//				String url = mManager.imageItemsList.get(position % mManager.imageItemsList.size()).ImgUrl;
//				return MassVigUtil.GetImageUrl(url, MassVigUtil.dip2px(CopyOfMainActivity.this, 320), MassVigUtil.dip2px(CopyOfMainActivity.this, 150));
//			}
//			else
//				return "";
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			NetImageView i = new NetImageView(CopyOfMainActivity.this);
//			i.setImageUrl(getItemUrl(position), MassVigContants.PATH, null);
//			i.setScaleType(ImageView.ScaleType.FIT_XY);
//			i.setLayoutParams(new Gallery.LayoutParams(
//					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
//			return i;
//		}
//
//	}
//
//	public class MainAdapter extends BaseAdapter {
//
//		@Override
//		public int getCount() {
//			if(mManager.mainImageList.size() > 0)
//				return mManager.mainImageList.size();
//			else
//				return 0;
//		}
//
//		@Override
//		public Object getItem(int position) {
//			if(mManager.mainImageList.size() > 0)
//				return mManager.mainImageList.get(position % mManager.mainImageList.size());
//			return new Advertise();
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return position;
//		}
//
//		public String getItemUrl(int position) {
//			if(mManager.mainImageList.size() > 0){
//				String url = mManager.mainImageList.get(position % mManager.mainImageList.size()).ImgUrl;
//				return MassVigUtil.GetImageUrl(url, MassVigUtil.dip2px(CopyOfMainActivity.this, 160), MassVigUtil.dip2px(CopyOfMainActivity.this, 71));
//			}
//			return "";
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			convertView = mInflater.inflate(R.layout.ad_item1, null);
//			NetImageView i = (NetImageView)convertView.findViewById(R.id.imageview);
//			i.setImageUrl(getItemUrl(position), MassVigContants.PATH, null);
//			i.setScaleType(ImageView.ScaleType.FIT_XY);
//			i.setLayoutParams(new RelativeLayout.LayoutParams(
//					LayoutParams.FILL_PARENT, MassVigUtil.dip2px(
//							CopyOfMainActivity.this, 71)));
//			return convertView;
//		}
//
//	}
//
//	public class SecondAdapter extends BaseAdapter {
//
//		@Override
//		public int getCount() {
//			if(mManager.secondImageList.size() > 0)
//				return mManager.secondImageList.size();
//			else
//				return 0;
//		}
//
//		@Override
//		public Object getItem(int position) {
//			if(mManager.secondImageList.size() > 0)
//				return mManager.secondImageList.get(position % mManager.secondImageList.size());
//			else 
//				return new Advertise();
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return position;
//		}
//
//		public String getItemUrl(int position) {
//			if(mManager.secondImageList.size() > 0){
//				String url = mManager.secondImageList.get(position % mManager.secondImageList.size()).ImgUrl;
//				return MassVigUtil.GetImageUrl(url, MassVigUtil.dip2px(CopyOfMainActivity.this, 75), MassVigUtil.dip2px(CopyOfMainActivity.this, 75));
//			}
//			else
//				return "";
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			convertView = mInflater.inflate(R.layout.ad_item, null);
//			NetImageView i = (NetImageView)convertView.findViewById(R.id.imageview);
//			i.setImageUrl(getItemUrl(position), MassVigContants.PATH, null);
//			i.setScaleType(ImageView.ScaleType.FIT_XY);
//			i.setLayoutParams(new RelativeLayout.LayoutParams(
//					LayoutParams.FILL_PARENT, MassVigUtil.dip2px(
//							CopyOfMainActivity.this, 100)));
//			TextView price = (TextView)convertView.findViewById(R.id.price);
//			Advertise ad = (Advertise) getItem(position);
//			price.setText(getString(R.string.real_money, ad.Name));
//			return convertView;
//		}
//
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.shopmap:
//			startActivity(new Intent(CopyOfMainActivity.this, ShopMapActivity.class));
//			break;
//		case R.id.search_btn:
//			if(!TextUtils.isEmpty(edit_content.getText().toString()))
//			{MassVigUtil.setPreferenceStringData(CopyOfMainActivity.this, "KEYWORD", edit_content.getText().toString());
//			((MainTabActivity)getParent()).setTabHostIndex(1);
//			}else{
//				Toast.makeText(CopyOfMainActivity.this, getString(R.string.no_key), Toast.LENGTH_SHORT).show();
//			}
//			break;
//		case R.id.more:
//			((MainTabActivity)getParent()).setTabHostIndex(1);
//			break;
//		default:
//			break;
//		}
//	}
//
//	@Override
//	public void Success() {
//		if(mManager.imageItemsList.size() > 0)
//			initDotLayout(mManager.imageItemsList.size());
//		mImageAdapter.notifyDataSetChanged();
//		mMainAdapter.notifyDataSetChanged();
//		grid2.setAdapter(mSecondAdapter);
//		new Utility().setGridView2HeightBasedOnChildren(grid2);
//		mSecondAdapter.notifyDataSetChanged();
//	}
//
//	private void initDotLayout(int size) {
//		dotLayout.removeAllViews();
//		for (int i = 0; i < size; i++) {
//			TextView dot = new TextView(this);
//			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(11, 11);
//			lp.leftMargin = MassVigUtil.dip2px(this, 5);
//			dot.setLayoutParams(lp);
//			dot.setBackgroundResource(R.drawable.ic_dot_black);
//			dotLayout.addView(dot);
//		}
//		dotLayout.getChildAt(0).setBackgroundResource(R.drawable.ic_dot_red);
//	}
//
//	@Override
//	public void Failed() {
//		// TODO Auto-generated method stub
//		
//	}
//
//}
