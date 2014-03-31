package com.massvig.ecommerce.activities;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.analytics.tracking.android.EasyTracker;
import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.AdItem;
import com.massvig.ecommerce.entities.Advertise;
import com.massvig.ecommerce.entities.Goods;
import com.massvig.ecommerce.managers.MainActivityManager;
import com.massvig.ecommerce.managers.OrdersManager;
import com.massvig.ecommerce.managers.MainActivityManager.LoadListener;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigExit;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.utilities.Utility;
import com.massvig.ecommerce.widgets.ADAdapter;
import com.massvig.ecommerce.widgets.AdGallery;
import com.massvig.ecommerce.widgets.MassVigData;
import com.massvig.ecommerce.widgets.NetImageView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery.LayoutParams;
import android.widget.TextView;
//git test 1
public class MainActivity extends BaseActivity implements OnClickListener, LoadListener {
	private MainActivityManager mManager;
	private AdGallery gallery;
	private LinearLayout dotLayout;
	private MyImageAdapter mImageAdapter;
	private ADAdapter adAdapter;
	private int imageIndex = 0;
	private Timer mTimer;
	private TimerTask task;
	private static final int DELAY = 8000;
	private static final int IMAGE = 1;
	private EditText edit_content;
	public static final int WEBURL = 1;
	public static final int PRODUCTID = 2;
	public static final int GOODSLIST = 3;
	public static final int CAMPAIGN = 4;
	private MassVigExit exit = new MassVigExit();
	private boolean click = false;
	private BaseApplication app;
	
	private ListView listview;
	
//	private ViewPager viewPager; // android-support-v4中的滑动组件
//	private List<ImageView> imageViews; // 滑动的图片集合
//	private int currentItem = 0; // 当前图片的索引号
//	private ScheduledExecutorService scheduledExecutorService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setTitle(getString(R.string.main));
		mManager = new MainActivityManager(this);
		mManager.setListener(this);
		initView();
	}

	private void pressAgainExit(){
		if(exit.isExit()){
			finish();
		}else{
			Toast.makeText(this, getString(R.string.exit_message), Toast.LENGTH_SHORT).show();
			exit.doExitInOneSecond();
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			pressAgainExit();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case IMAGE:
//				if(!isUserTouch)
//					viewPager.setCurrentItem(currentItem);// 切换当前显示的图片
				

				if (!gallery.isTouch) {
					imageIndex++;
//					gallery.setAnimationDuration(500);
					gallery.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
//					if (mManager.imageItemsList.size() > 0)
//						setDotImage(imageIndex % mManager.imageItemsList.size());
				}
				break;

			default:
				break;
			}
		}

	};

	@Override
	protected void onResume() {
		super.onResume();
		app = (BaseApplication) getApplication();
		click = false;
		mTimer = new Timer();
		task = new TimerTask() {
			@Override
			public void run() {
				mHandler.sendEmptyMessage(IMAGE);
			}
		};
		mTimer.schedule(task, DELAY, DELAY);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (task != null) {
			task.cancel();
			task = null;
		}
		if (mTimer != null) {
			mTimer.purge();
			mTimer.cancel();
			mTimer = null;
		}
	}

	public void collapseSoftInputMethod(EditText inputText){
		InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
		imm.hideSoftInputFromWindow(inputText.getWindowToken(), 0); 
	}
	
	private void initView() {
//		imageViews = new ArrayList<ImageView>();
//		viewPager = (ViewPager) findViewById(R.id.vp);
//		viewPager.setOnTouchListener(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				// TODO Auto-generated method stub
//				switch (event.getAction()) {
//				case MotionEvent.ACTION_DOWN:
//					scheduledExecutorService.shutdown();
//					isUserTouch = true;
//					break;
//				case MotionEvent.ACTION_MOVE:
//					isUserTouch = true;
//					break;
//				case MotionEvent.ACTION_UP:
//					isUserTouch = false;
//					scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
//					// 当Activity显示出来后，每两秒钟切换一次图片显示
//					scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 8, 8, TimeUnit.SECONDS);
//					break;
//
//				default:
//					break;
//				}
//				return false;
//			}
//		});
		listview = (ListView)findViewById(R.id.listview);
		((TextView)findViewById(R.id.fc_01)).setOnClickListener(this);
		((TextView)findViewById(R.id.fc_02)).setOnClickListener(this);
		((TextView)findViewById(R.id.fc_03)).setOnClickListener(this);
		((TextView)findViewById(R.id.fc_09)).setOnClickListener(this);
		((TextView)findViewById(R.id.fc_05)).setOnClickListener(this);
		((TextView)findViewById(R.id.fc_06)).setOnClickListener(this);
		((TextView)findViewById(R.id.fc_07)).setOnClickListener(this);
		((TextView)findViewById(R.id.fc_08)).setOnClickListener(this);
		
//		edit_content = (EditText)findViewById(R.id.search_edit);
		((LinearLayout)findViewById(R.id.search_layout)).setOnClickListener(this);
//		edit_content.setOnClickListener(this);
//		edit_content.setOnEditorActionListener(new OnEditorActionListener() {
//			
//			@Override
//			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//				// TODO Auto-generated method stub
//				if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED){
//					if(!TextUtils.isEmpty(edit_content.getText().toString()))
//					{
//						if (!click) {
//							click = true;
//							collapseSoftInputMethod(edit_content);
//							MassVigUtil.setPreferenceStringData(
//									MainActivity.this, "KEYWORD", edit_content
//											.getText().toString());
//							startActivity(new Intent(MainActivity.this,
//									GoodsListActivity.class));
//						}
////						((MainTabActivity)getParent()).setTabHostIndex(1);
//						return false;
//					}else{
//						Toast.makeText(MainActivity.this, getString(R.string.no_key), Toast.LENGTH_SHORT).show();
//					}
//				}
//				return false;
//			}
//		});
		((Button)findViewById(R.id.search_btn)).setOnClickListener(this);
		listview = (ListView)findViewById(R.id.listview);
		((Button) findViewById(R.id.shopmap)).setOnClickListener(this);
		((Button) findViewById(R.id.more)).setOnClickListener(this);
		dotLayout = (LinearLayout) findViewById(R.id.dot_images);
		gallery = (AdGallery) findViewById(R.id.gallery);
		mImageAdapter = new MyImageAdapter();
		adAdapter = new ADAdapter(this, mManager.adList);
		listview.setAdapter(adAdapter);
		gallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mManager != null && mManager.imageItemsList.size() > 0) {
					AdItem a = mManager.imageItemsList.get(position % mManager.imageItemsList.size());
					JSONArray array;
					try {
						array = new JSONArray(a.AdRegions);
						if(array != null && array.length() > 0){
							for (int i = 0; i < array.length(); i++) {
								JSONObject o = array.getJSONObject(i);
								Advertise ad = new Advertise();
								ad.Link = o.optString("Link");
								ad.LinkType = o.optInt("LinkType");
								if(ad.LinkType == WEBURL){
									startActivity(new Intent(MainActivity.this,
											WebViewActivity.class).putExtra("URL", ad.Link));
								}else if(ad.LinkType == PRODUCTID){
									Goods good = new Goods();
									good.productID = Integer.valueOf(ad.Link);
									startActivity(new Intent(MainActivity.this,
											GoodsDetailActivity.class).putExtra("goods", good));
								}else if(ad.LinkType == GOODSLIST){
									String params = ad.Link;
									startActivity(new Intent(MainActivity.this, AdGoodsListActivity.class).putExtra("PARAMS", params));
								}else if(ad.LinkType == CAMPAIGN){
									try {
										int camID = Integer.valueOf(ad.Link);
										if(camID > 0)
											startActivity(new Intent(MainActivity.this, ActionDetailActivity.class).putExtra("CAMPAIGNID", camID));
									} catch (Exception e) {
									}
								}
							}
						}
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					
				}
			}
		});
		gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
//				gallery.isTouch = false;
				if(mManager.imageItemsList.size() > 0)
					setDotImage(position % mManager.imageItemsList.size());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		gallery.setAdapter(mImageAdapter);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String ad = MassVigUtil.getPreferenceData(MainActivity.this, "AD", "");
				if(TextUtils.isEmpty(ad))
					MassVigUtil.setPreferenceStringData(MainActivity.this, "AD", MassVigData.getinstance(MainActivity.this).getAdCache());
				mManager.SetData();
			}
		}, 1000);
//		if(TextUtils.isEmpty(MassVigUtil.getPreferenceData(this, "AD", "")))
//			MassVigUtil.setPreferenceStringData(MainActivity.this, "AD", MassVigData.getinstance(MainActivity.this).getAdCache());
//		mManager.SetData();
//		viewPager.setAdapter(new MyAdapter());// 设置填充ViewPager页面的适配器
//		// 设置一个监听器，当ViewPager中的页面改变时调用
//		viewPager.setOnPageChangeListener(new MyPageChangeListener());
	}

//	private int oldIndex;
//	private void setDotImage(int arg){
//		ArrayList<TextView> views = new ArrayList<TextView>();
//		for (int i = 0; i < dotLayout.getChildCount(); i++) {
//			views.add((TextView) dotLayout.getChildAt(i));
//		}
//		views.get(oldIndex).setBackgroundResource(R.drawable.ic_dot_black);
//		views.get(arg).setBackgroundResource(R.drawable.ic_dot_red);
//		oldIndex = arg;
//	}
	
	public void setDotImage(int arg0) {
		for (int i = 0; i < dotLayout.getChildCount(); i++) {
			if (i == arg0 % dotLayout.getChildCount()) {
				((ImageView) (dotLayout.getChildAt(i)))
						.setImageResource(R.drawable.ic_dot_red);

			} else {
				((ImageView) (dotLayout.getChildAt(i)))
						.setImageResource(R.drawable.ic_dot_black);

			}
		}
		imageIndex = arg0 % dotLayout.getChildCount();
	}

	public class MyImageAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		public Object getItem(int position) {
			if(mManager.imageItemsList.size() > 0)
				return mManager.imageItemsList.get(position % mManager.imageItemsList.size());
			else
				return new AdItem();
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public String getItemUrl(int position) {
			if(mManager.imageItemsList.size() > 0){
				AdItem ad = (AdItem) getItem(position);
				JSONArray array;
					try {
						array = new JSONArray(ad.AdRegions);
						if(array != null && array.length() > 0){
							JSONObject o = array.getJSONObject(0);
							String url = o.optString("ImgUrl");
							return MassVigUtil.GetImageUrl(url, MassVigUtil.dip2px(MainActivity.this, 320), MassVigUtil.dip2px(MainActivity.this, 150));
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			return "";
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			NetImageView i = new NetImageView(MainActivity.this);
			i.setImageUrl(getItemUrl(position), MassVigContants.MAINPATH, null);
			i.setScaleType(ImageView.ScaleType.FIT_XY);
			i.setLayoutParams(new Gallery.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			return i;
		}

	}

	public String getItemUrl(int position) {
		if(mManager.imageItemsList.size() > 0){
			AdItem ad = (AdItem) mManager.imageItemsList.get(position % mManager.imageItemsList.size());
			JSONArray array;
				try {
					array = new JSONArray(ad.AdRegions);
					if(array != null && array.length() > 0){
						JSONObject o = array.getJSONObject(0);
						String url = o.optString("ImgUrl");
						return MassVigUtil.GetImageUrl(url, MassVigUtil.dip2px(MainActivity.this, 320), MassVigUtil.dip2px(MainActivity.this, 150));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return "";
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.shopmap:
//			startActivity(new Intent(this, CouponManageActivity.class));
//			startActivity(new Intent(this, NewActionDetailActivity.class).putExtra("TYPE", 2));
			mManager.RefreshImageItemsList();
			break;
		case R.id.search_btn:
			if(!TextUtils.isEmpty(edit_content.getText().toString()))
			{MassVigUtil.setPreferenceStringData(MainActivity.this, "KEYWORD", edit_content.getText().toString());
			((MainTabActivity)getParent()).setTabHostIndex(1);
			}else{
				Toast.makeText(MainActivity.this, getString(R.string.no_key), Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.more:
			((MainTabActivity)getParent()).setTabHostIndex(1);
			break;
		case R.id.fc_01:
			startActivity(new Intent(this, NewActionDetailActivity.class).putExtra("TYPE", NewActionDetailActivity.HOT));
//			startActivity(new Intent(this, GoodsListActivity.class).putExtra("ORDERID", 4));
			break;
		case R.id.fc_02:
			startActivity(new Intent(this, NewActionDetailActivity.class).putExtra("TYPE", NewActionDetailActivity.NEW));
//			startActivity(new Intent(this, GoodsListActivity.class).putExtra("ORDERID", 6));
			break;
		case R.id.fc_03:
			startActivity(new Intent(this, NewActionDetailActivity.class).putExtra("TYPE", NewActionDetailActivity.REBATE));
//			startActivity(new Intent(this, GoodsListActivity.class).putExtra("ORDERID", 8));
			break;
		case R.id.fc_05:
			((MainTabActivity)getParent()).setTabHostIndex(1);
			break;
		case R.id.fc_06:
			app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(this, "SESSIONID", "") : app.user.sessionID;
			//TODO
			if(!TextUtils.isEmpty(app.user.sessionID)){
				startActivity(new Intent(this, CollectActivity.class));
			}else{
				app.user.sessionID = "";
				MassVigUtil.setPreferenceStringData(this, "SESSIONID", "");
				startActivity(new Intent(this, LoginActivity.class));
			}
			break;
		case R.id.fc_07:
			startActivity(new Intent(this, ManageOrderActivity.class).putExtra("orderTab", OrdersManager.UNPAYORDER));
			break;
		case R.id.fc_08:
			((MainTabActivity)getParent()).setTabHostIndex(4);
			break;
		case R.id.fc_09:

			final String[] numbers = getResources().getStringArray(R.array.numbers);

			Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.select_telephone))
					.setItems(numbers, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(which == 0){
								startActivity(new Intent(Intent.ACTION_CALL, Uri
										.parse("tel:" + getString(R.string.tele_num))));
							}else{
								ClipboardManager cmb = (ClipboardManager)MainActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
								cmb.setText(numbers[which].substring(numbers[which].indexOf("-") + 1, numbers[which].length()));
								Toast.makeText(MainActivity.this, getString(R.string.copy), Toast.LENGTH_SHORT).show();
								String pkg, cls;
						        try {
						            pkg = "com.tencent.mobileqq";
						            cls = "com.tencent.mobileqq.activity.SplashActivity";
						            ComponentName componet = new ComponentName(pkg, cls);
						            Intent i = new Intent();
						            i.setComponent(componet);
						            startActivity(i);
								} catch (Exception e) {
									try {
							            pkg = "com.tencent.minihd.qq";
							            cls = "com.tencent.qq.SplashActivity";
							            ComponentName componet = new ComponentName(pkg, cls);
							            Intent i = new Intent();
							            i.setComponent(componet);
							            startActivity(i);
									} catch (Exception e2) {
										try {
								            pkg = "com.tencent.android.pad";
								            cls = "com.tencent.android.pad.paranoid.desktop.DesktopActivity";
								            ComponentName componet = new ComponentName(pkg, cls);
								            Intent i = new Intent();
								            i.setComponent(componet);
								            startActivity(i);
										} catch (Exception e3) {
										}
									}
								}
							}
						}

					}).create().show();
			
			break;
		case R.id.search_layout:
			startActivity(new Intent(this, SearchActivity.class));
			break;
		case R.id.search_edit:
			startActivity(new Intent(this, SearchActivity.class));
			break;
		default:
			break;
		}
	}

	@Override
	public void Success() {
		if(mManager.imageItemsList.size() > 0){
			initDotLayout(mManager.imageItemsList.size());

			// 初始化图片资源
//			for (int i = 0; i < mManager.imageItemsList.size(); i++) {
//				NetImageView imageView = new NetImageView(this);
//				imageView.setImageUrl(getItemUrl(i), MassVigContants.PATH, null);
//				imageView.setScaleType(ScaleType.FIT_XY);
//				imageViews.add(imageView);
//			}
			
		}
		mImageAdapter.notifyDataSetChanged();
		adAdapter.setDataList(mManager.adList);
		listview.setAdapter(adAdapter);
		new Utility().setListViewHeightBasedOnChildren(this,listview, adAdapter);
		adAdapter.notifyDataSetChanged();
	}

	private void initDotLayout(int size) {
		dotLayout.removeAllViews();
		for (int i = 0; i < size; i++) {
			ImageView dot = new ImageView(this);
//			TextView dot = new TextView(this);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(11, 11);
			lp.leftMargin = MassVigUtil.dip2px(this, 5);
			dot.setLayoutParams(lp);
			dot.setImageResource(R.drawable.ic_dot_black);
//			dot.setBackgroundResource(R.drawable.ic_dot_black);
			dotLayout.addView(dot);
		}
		((ImageView)dotLayout.getChildAt(0)).setImageResource(R.drawable.ic_dot_red);
	}

	@Override
	public void Failed() {
		
	}
//	private class MyAdapter extends PagerAdapter {
//
//		@Override
//		public int getCount() {
//			return mManager.imageItemsList.size();
//		}
//
//		@Override
//		public Object instantiateItem(View arg0, int arg1) {
//			((ViewPager) arg0).addView(imageViews.get(arg1));
//			return imageViews.get(arg1);
//		}
//
//		@Override
//		public void destroyItem(View arg0, int arg1, Object arg2) {
//			((ViewPager) arg0).removeView((View) arg2);
//		}
//
//		@Override
//		public boolean isViewFromObject(View arg0, Object arg1) {
//			return arg0 == arg1;
//		}
//
//		@Override
//		public void restoreState(Parcelable arg0, ClassLoader arg1) {
//
//		}
//
//		@Override
//		public Parcelable saveState() {
//			return null;
//		}
//
//		@Override
//		public void startUpdate(View arg0) {
//
//		}
//
//		@Override
//		public void finishUpdate(View arg0) {
//
//		}
//	}

//	@Override
//	protected void onStart() {
//		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
//		// 当Activity显示出来后，每两秒钟切换一次图片显示
//		scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 8, 8, TimeUnit.SECONDS);
//		super.onStart();
//	}
//
//	@Override
//	protected void onStop() {
//		// 当Activity不可见的时候停止切换
//		scheduledExecutorService.shutdown();
//		super.onStop();
//	}

//	/**
//	 * 换行切换任务
//	 * 
//	 * @author Administrator
//	 * 
//	 */
//	private class ScrollTask implements Runnable {
//
//		public void run() {
//			synchronized (viewPager) {
//				System.out.println("currentItem: " + currentItem);
//				currentItem = (currentItem + 1) % imageViews.size();
//				mHandler.obtainMessage().sendToTarget(); // 通过Handler切换图片
//			}
//		}
//
//	}
//	private class MyPageChangeListener implements OnPageChangeListener {
//
//		/**
//		 * This method will be invoked when a new page becomes selected.
//		 * position: Position index of the new selected page.
//		 */
//		public void onPageSelected(int position) {
//			currentItem = position;
//			setDotImage(position);
//		}
//
//		public void onPageScrollStateChanged(int arg0) {
//
//		}
//
//		public void onPageScrolled(int arg0, float arg1, int arg2) {
//
//		}
//	}
}
