package com.massvig.ecommerce.activities;

import java.util.ArrayList;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.Goods;
import com.massvig.ecommerce.entities.Post;
import com.massvig.ecommerce.managers.CommunityManager;
import com.massvig.ecommerce.managers.CommunityManager.LoadListener;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigExit;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.widgets.NetImageView;
import com.massvig.ecommerce.widgets.PostAdapter;
import com.massvig.ecommerce.widgets.RefreshListView;
import com.massvig.ecommerce.widgets.RefreshListView.RefreshListener;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class CommunityActivity extends BaseActivity implements OnClickListener,LoadListener {

	private BaseApplication app;
	private CommunityManager mManager;
	private TranslateAnimation mAnimation;
	private ImageView m_ivMove;
	private ViewPager mViewPager;
	private LayoutInflater mInflater;
	private View view1,view2;
	private RefreshListView listview1, listview2;
	private PostAdapter adapter1, adapter2;
	private LinearLayout title_layout;
	private RelativeLayout tab_layout;
	private Button notFull;
	private boolean isScolling;
	public static final int SUCCESS = 1;
	public static final int FAILED = 2;
	public static final int SHARE = 3;
	public static final int PRAISE = 4;
	public static final int LOGIN = 5;
	public static final int NOTIFY = 6;
	public static final int HEAD = 7;
	public static final int ITEM = 8;
	public static final int CONTENT = 9;
	private int tabindex = 0;
	private TextView message_number;
	private MyBroadCast receiver = null;
	private MassVigExit exit = new MassVigExit();
	private boolean isLoadingMore = false;//是否是上拉加载更多操作true:加载更多 false:刷新
	private boolean isFirst = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.community_main);
		app = (BaseApplication) getApplication();
		mManager = new CommunityManager();
		mManager.setListener(this);
		mManager.setMaxWidth(this.getWindowManager().getDefaultDisplay().getWidth());
		mManager.setMoveWidth(getWindowManager().getDefaultDisplay().getWidth() / 2);
		initView();
		receiver = new MyBroadCast();
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction("massvig.ecommerce.message");
		registerReceiver(receiver, mFilter);
		mManager.clearData();
		mManager.isLoadDoneAll = false;
		mManager.isLoadDoneAttention = false;
		if(tabindex == 0){
			isLoadingMore = false;
			mManager.loadData(app.user.sessionID, CommunityManager.ALL, CommunityManager.REFRESH);
		}
		else{
			isLoadingMore = false;
			mManager.loadData(app.user.sessionID, CommunityManager.ATTENTION, CommunityManager.REFRESH);
		}
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

	public class MyBroadCast extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("massvig.ecommerce.message")){
				int count = mManager.GetAllUnread(CommunityActivity.this, app.user.EJID);
				message_number.setVisibility(View.INVISIBLE);
				if(count > 0){
					message_number.setText(count + "");
					message_number.setVisibility(View.VISIBLE);
				}
			}
		}
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		app = (BaseApplication) getApplication();
		app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(this, "SESSIONID", "") : app.user.sessionID;
		if(!TextUtils.isEmpty(app.user.sessionID)){
			int count = mManager.GetAllUnread(this, app.user.EJID);
			message_number.setVisibility(View.INVISIBLE);
			if(count > 0){
				message_number.setText(count + "");
				message_number.setVisibility(View.VISIBLE);
			}
		}else{
			message_number.setVisibility(View.INVISIBLE);
		}
	}

	private void initView() {
		message_number = (TextView)findViewById(R.id.message);
		title_layout = (LinearLayout)findViewById(R.id.titlelayout);
		tab_layout = (RelativeLayout)findViewById(R.id.tablayout);
		m_ivMove = (ImageView) findViewById(R.id.moveimg);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.setMargins(mManager.getMaxWidth() / 4 - 15, 0, 0, 0);
		m_ivMove.setLayoutParams(lp);
		mViewPager = (ViewPager)findViewById(R.id.viewPager1);
		mInflater = LayoutInflater.from(this);
		
		view1 = mInflater.inflate(R.layout.community_share, null);
		listview1 = (RefreshListView)view1.findViewById(R.id.listview);
		adapter1 = new PostAdapter(this, mManager.allPostList, mHandler, mManager.getMaxWidth());
		listview1.setAdapter(adapter1);
		initListView(listview1, adapter1);
		
		view2 = mInflater.inflate(R.layout.community_share, null);
		listview2 = (RefreshListView)view2.findViewById(R.id.listview);
		adapter2 = new PostAdapter(this, mManager.myAttentionList, mHandler, mManager.getMaxWidth());
		listview2.setAdapter(adapter2);
		initListView(listview2, adapter2);
		mManager.getViews().add(view1);
		mManager.getViews().add(view2);
		PagerAdapter mPagerAdapter = new PagerAdapter() {
			
			@Override
			public void startUpdate(View arg0) {
				
			}
			
			@Override
			public Parcelable saveState() {
				return null;
			}
			
			@Override
			public void restoreState(Parcelable arg0, ClassLoader arg1) {
				
			}
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}
			
			@Override
			public Object instantiateItem(View arg0, int arg1) {
				((ViewPager)arg0).addView(mManager.getViews().get(arg1));
				return mManager.getViews().get(arg1);
			}
			
			@Override
			public int getCount() {
				return mManager.getViews().size();
			}
			
			@Override
			public void finishUpdate(View arg0) {
				
			}
			
			@Override
			public void destroyItem(View arg0, int arg1, Object arg2) {
				((ViewPager)arg0).removeView(mManager.getViews().get(arg1));
			}
		};
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setCurrentItem(0);
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int arg0) {
				mManager.setIndex(arg0);
				tabindex = arg0;
				if(arg0 == 0){
					isLoadingMore = true;
					mManager.loadData(app.user.sessionID, CommunityManager.ALL, CommunityManager.MORE);
				}else{
					if(TextUtils.isEmpty(app.user.sessionID)){
						mViewPager.setCurrentItem(0);
						app.user.sessionID = "";
						MassVigUtil.setPreferenceStringData(CommunityActivity.this, "SESSIONID", "");
						startActivity(new Intent(CommunityActivity.this, LoginActivity.class));
					}else{
						if(!isFirst){
							isLoadingMore = true;
							mManager.loadData(app.user.sessionID, CommunityManager.ATTENTION, CommunityManager.MORE);
						}else{
							isFirst = false;
							isLoadingMore = false;
							mManager.loadData(app.user.sessionID, CommunityManager.ATTENTION, CommunityManager.REFRESH);
						}
					}
				}
				startAnimation();
			}});
		((TextView) findViewById(R.id.tab1)).setOnClickListener(this);
		((TextView) findViewById(R.id.tab2)).setOnClickListener(this);
		((Button) findViewById(R.id.back)).setOnClickListener(this);
		((Button) findViewById(R.id.finish)).setOnClickListener(this);
		notFull = (Button) findViewById(R.id.resotre);
		notFull.setOnClickListener(this);
	}

	private void initListView(final RefreshListView l, final PostAdapter adapter22) {
		l.setOnItemClickListener(new AdapterView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
			}});
		l.setOnRefreshListener(new RefreshListener() {
			
			@Override
			public void startRefresh() {
				isLoadingMore = false;
				mManager.isLoadDoneAll = false;
				mManager.isLoadDoneAttention = false;
				if(tabindex == 0){
					isLoadingMore = false;
					mManager.loadData(app.user.sessionID, CommunityManager.ALL, CommunityManager.REFRESH);
				}
				else{
					isLoadingMore = false;
					mManager.loadData(app.user.sessionID, CommunityManager.ATTENTION, CommunityManager.REFRESH);
				}
			}
			
			@Override
			public void startLoadMore() {
				isLoadingMore = true;
				if (!mManager.isLoading) {
					if(tabindex == 0){
						isLoadingMore = true;
						mManager.loadData(app.user.sessionID, CommunityManager.ALL, CommunityManager.MORE);
					}else{
						isLoadingMore = true;
						mManager.loadData(app.user.sessionID, CommunityManager.ATTENTION, CommunityManager.MORE);
					}
				}
			}
			
			@Override
			public void scrollStop() {
				isScolling = false;
				NetImageView.setIsAutoLoadImage(true);
				ArrayList<NetImageView> imageLists = adapter22.getImageList();
				for(int i = 0,len = imageLists.size();i < len ;i++){
					if(isScolling == false){
						imageLists.get(i).updateImage();
					}
				}
			}
			
			@Override
			public void scrollStart() {
				isScolling = true;
				NetImageView.setIsAutoLoadImage(false);
			}
		});
//		l.setOnScrollListener(new OnScrollListener() {
//			
//			@Override
//			public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//				if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
//					isScolling = false;
//					NetImageView.setIsAutoLoadImage(true);
//					ArrayList<NetImageView> imageLists = adapter22.getImageList();
//					for(int i = 0,len = imageLists.size();i < len ;i++){
//						if(isScolling == false){
//							imageLists.get(i).updateImage();
//						}
//					}
//				}else if(scrollState == OnScrollListener.SCROLL_STATE_FLING){
//					isScolling = true;
//					NetImageView.setIsAutoLoadImage(false);
//				}
//			}
//			
//			@Override
//			public void onScroll(AbsListView view, int firstVisibleItem,
//					int visibleItemCount, int totalItemCount) {
//				if (firstVisibleItem + visibleItemCount >= totalItemCount - 1
//						&& totalItemCount > 0) {
//					if (!mManager.isLoading) {
//						if(tabindex == 0){
//							mManager.loadData(app.user.sessionID, CommunityManager.ALL);
//						}else{
//							mManager.loadData(app.user.sessionID, CommunityManager.ATTENTION);
//						}
//					}
//				}
//			}
//		});
	}

	private void startAnimation() {
		mAnimation = new TranslateAnimation(mManager.getmFromX(), mManager.getmToX(), 0, 0);
		mAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {

			}

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
		});
		mAnimation.setDuration(200);
		mAnimation.setFillAfter(true);
		m_ivMove.startAnimation(mAnimation);
	}

	private void tabClickListener(int index){
		mViewPager.setCurrentItem(index);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tab1:
			tabClickListener(0);
			break;
		case R.id.tab2:
			tabClickListener(1);
			break;
		case R.id.back:
			if(!TextUtils.isEmpty(app.user.sessionID)){
				startActivity(new Intent(this, CommunityUserInfoActivity.class).putExtra("USERID", app.user.customerID));
			}else{
				app.user.sessionID = "";
				MassVigUtil.setPreferenceStringData(CommunityActivity.this, "SESSIONID", "");
				startActivity(new Intent(this, LoginActivity.class));
			}
			break;
		case R.id.finish:
			title_layout.setVisibility(View.GONE);
			tab_layout.setVisibility(View.GONE);
			notFull.setVisibility(View.VISIBLE);
			((MainTabActivity)getParent()).setBottomGone();
			break;
		case R.id.resotre:
			title_layout.setVisibility(View.VISIBLE);
			tab_layout.setVisibility(View.VISIBLE);
			notFull.setVisibility(View.GONE);
			((MainTabActivity)getParent()).setBottomVisible();
			break;
		default:
			break;
		}
	}

	@Override
	public void LoadSuccess(int index, int tag) {
		switch (index) {
		case CommunityManager.LOAD:
			Message msg = new Message();
			msg.what = SUCCESS;
			msg.arg1 = tag;
			mHandler.sendMessage(msg);
			break;
		case CommunityManager.PRAISE:
			mHandler.sendEmptyMessage(NOTIFY);
			break;

		default:
			break;
		}
	}

	@Override
	public void LoadFailed(int index) {
		// TODO Auto-generated method stub
		
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case SUCCESS:
				if(msg.arg1 == CommunityManager.ALL){
					if (isLoadingMore) {
						listview1.finishFootView();
					}else{
						listview1.finishHeadView();
						listview1.setTextShow(getString(R.string.down_pull_refresh));
					}
					isLoadingMore = true;
					mManager.refreshPostList();
					adapter1.notifyDataSetChanged();
				}else{
					if (isLoadingMore) {
						listview2.finishFootView();
					}else{
						listview2.finishHeadView();
						listview2.setTextShow(getString(R.string.down_pull_refresh));
					}
					isLoadingMore = true;
					mManager.refreshAttentionList();
					adapter2.notifyDataSetChanged();
				}
				
				break;
			case FAILED:
				break;
			case NOTIFY:
				adapter1.notifyDataSetChanged();
				adapter2.notifyDataSetChanged();
				break;
			case SHARE:
				Post post = (Post) msg.obj;
				if(!TextUtils.isEmpty(app.user.sessionID))
					startActivity(new Intent(CommunityActivity.this, InsertPost.class).putExtra("FLAG", InsertPost.POST).putExtra("image", post.imageUrl).putExtra("productID", post.postID));
				else{
					app.user.sessionID = "";
					MassVigUtil.setPreferenceStringData(CommunityActivity.this, "SESSIONID", "");
					startActivity(new Intent(CommunityActivity.this, LoginActivity.class));
				}
				break;
			case PRAISE:
				Post p = (Post) msg.obj;
				if(!TextUtils.isEmpty(app.user.sessionID))
					mManager.Praise(app.user.sessionID, p.postID);
				else
					app.user.sessionID = "";
					MassVigUtil.setPreferenceStringData(CommunityActivity.this, "SESSIONID", "");
					startActivity(new Intent(CommunityActivity.this, LoginActivity.class));
				break;
			case LOGIN:
				app.user.sessionID = "";
				MassVigUtil.setPreferenceStringData(CommunityActivity.this, "SESSIONID", "");
				startActivity(new Intent(CommunityActivity.this, LoginActivity.class));
				break;
			case HEAD:
				Post po = (Post) msg.obj;
				startActivity(new Intent(CommunityActivity.this, CommunityUserInfoActivity.class).putExtra("USERID", po.customerID));
				break;
			case ITEM:
				Post pos = (Post) msg.obj;
				switch (pos.ShareSourceType) {
				case MassVigContants.Product:
					Goods goods = new Goods();
					goods.productID = pos.RefID;
					goods.imageUrl = pos.imageUrl;
					startActivity(new Intent(CommunityActivity.this, GoodsDetailActivity.class).putExtra("goods", goods));
					break;

				default:
					break;
				}
				break;

			default:
				break;
			}
		}};
	@Override
	public void SessionFailed() {
//		if(!TextUtils.isEmpty(app.user.sessionID + MassVigUtil.getPreferenceData(this, "SESSIONID", "")))
			mHandler.sendEmptyMessage(LOGIN);
		app.user.sessionID = "";
		MassVigUtil.setPreferenceStringData(this, "SESSIONID", "");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		NetImageView.clearCache();
		unregisterReceiver(receiver);
	}
}
