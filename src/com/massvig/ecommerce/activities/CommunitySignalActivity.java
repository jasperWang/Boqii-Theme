package com.massvig.ecommerce.activities;

import java.util.ArrayList;

import com.massvig.ecommerce.entities.Goods;
import com.massvig.ecommerce.entities.Post;
import com.massvig.ecommerce.entities.PostList;
import com.massvig.ecommerce.activities.CommunityGroupActivity.OnActivityGroupResultListener;
import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.managers.CommunityManager;
import com.massvig.ecommerce.managers.CommunityManager.LoadListener;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.widgets.NetImageView;
import com.massvig.ecommerce.widgets.PostAdapter;
import com.massvig.ecommerce.widgets.RefreshListView;
import com.massvig.ecommerce.widgets.RefreshListView.RefreshListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class CommunitySignalActivity extends BaseActivity implements
		OnClickListener, LoadListener, OnActivityGroupResultListener {

	private BaseApplication app;
	private CommunityManager mManager;
	private RefreshListView listview1;
	private PostAdapter adapter1;
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
	private boolean isLoadingMore = false;// 是否是上拉加载更多操作true:加载更多 false:刷新
	private String keyword = "";
	private boolean isNeedRefresh = true;
	private static final int INSERTPOST = 1;
	private int click_postid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.community_signal_main);
		tabindex = this.getIntent().getIntExtra("TAB", 0);
		app = (BaseApplication) getApplication();
		mManager = new CommunityManager();
		mManager.setListener(this);
		mManager.setMaxWidth(this.getWindowManager().getDefaultDisplay()
				.getWidth());
		mManager.setMoveWidth(getWindowManager().getDefaultDisplay().getWidth() / 2);
		initView();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if(tabindex == 0){
			setTitle(getString(R.string.communitysignal_1));
		}else{
			setTitle(getString(R.string.communitysignal_2));
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		app = (BaseApplication) getApplication();
		app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(this, "SESSIONID", "") : app.user.sessionID;
		if (isNeedRefresh) {
			if (TextUtils.isEmpty(app.user.sessionID) && tabindex == 1) {

			} else {
				mManager.clearData();
				mManager.isLoadDoneAll = false;
				mManager.isLoadDoneAttention = false;
				keyword = MassVigUtil.getPreferenceData(this, "SEARCH_KEYWORD",
						"");
				tabindex = this.getIntent().getIntExtra("TAB", 0);
				if (tabindex == 0) {
					isLoadingMore = false;
					mManager.loadData(app.user.sessionID, CommunityManager.ALL,
							CommunityManager.REFRESH);
				} else if (tabindex == 1) {
					isLoadingMore = false;
					mManager.loadData(app.user.sessionID,
							CommunityManager.ATTENTION,
							CommunityManager.REFRESH);
				} else {
					isLoadingMore = false;
					mManager.loadData(keyword, CommunityManager.SEARCH,
							CommunityManager.REFRESH);
				}
			}
		}
	}

	private void initView() {
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.setMargins(mManager.getMaxWidth() / 4 - 15, 0, 0, 0);

		listview1 = (RefreshListView) findViewById(R.id.listview);
		adapter1 = new PostAdapter(this, mManager.allPostList, mHandler, mManager.getMaxWidth());
		listview1.setAdapter(adapter1);
		initListView(listview1, adapter1);

		((TextView) findViewById(R.id.tab1)).setOnClickListener(this);
		((TextView) findViewById(R.id.tab2)).setOnClickListener(this);
		((Button) findViewById(R.id.back)).setOnClickListener(this);
		((Button) findViewById(R.id.finish)).setOnClickListener(this);
		notFull = (Button) findViewById(R.id.resotre);
		notFull.setOnClickListener(this);
	}

	private void initListView(final RefreshListView l,
			final PostAdapter adapter22) {
		l.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

			}
		});
		l.setOnRefreshListener(new RefreshListener() {

			@Override
			public void startRefresh() {
				isLoadingMore = false;
				mManager.isLoadDoneAll = false;
				mManager.isLoadDoneAttention = false;
				if (tabindex == 0) {
					isLoadingMore = false;
					mManager.loadData(app.user.sessionID, CommunityManager.ALL,
							CommunityManager.REFRESH);
				} else if(tabindex == 1){
					isLoadingMore = false;
					mManager.loadData(app.user.sessionID,
							CommunityManager.ATTENTION,
							CommunityManager.REFRESH);
				}else{
					isLoadingMore = false;
					mManager.loadData(keyword, CommunityManager.SEARCH, CommunityManager.REFRESH);
				}
			}

			@Override
			public void startLoadMore() {
				isLoadingMore = true;
				if (!mManager.isLoading) {
					if (tabindex == 0) {
						isLoadingMore = true;
						mManager.loadData(app.user.sessionID,
								CommunityManager.ALL, CommunityManager.MORE);
					} else if(tabindex == 1){
						isLoadingMore = true;
						mManager.loadData(app.user.sessionID,
								CommunityManager.ATTENTION,
								CommunityManager.MORE);
					}else{
						isLoadingMore = true;
						mManager.loadData(keyword, CommunityManager.SEARCH, CommunityManager.MORE);
					}
				}
			}

			@Override
			public void scrollStop() {
				isScolling = false;
				NetImageView.setIsAutoLoadImage(true);
				ArrayList<NetImageView> headLists = adapter22.getHeadImageList();
				ArrayList<NetImageView> imageLists = adapter22.getImageList();
				for (int i = 0, len = imageLists.size(); i < len; i++) {
					if (isScolling == false) {
						headLists.get(i).updateImage();
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
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			if (!TextUtils.isEmpty(app.user.sessionID)) {
				startActivity(new Intent(this, CommunityUserInfoActivity.class)
						.putExtra("USERID", app.user.customerID));
			} else {
				app.user.sessionID = "";
				MassVigUtil.setPreferenceStringData(this, "SESSIONID", "");
				startActivity(new Intent(this, LoginActivity.class));
			}
			break;
		case R.id.finish:
			notFull.setVisibility(View.VISIBLE);
			((MainTabActivity) getParent()).setBottomGone();
			break;
		case R.id.resotre:
			notFull.setVisibility(View.GONE);
			((MainTabActivity) getParent()).setBottomVisible();
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

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case SUCCESS:
				if (isLoadingMore) {
					listview1.finishFootView();
				} else {
					listview1.finishHeadView();
					listview1
							.setTextShow(getString(R.string.down_pull_refresh));
				}
				isLoadingMore = true;
				mManager.refreshPostList();
				adapter1.notifyDataSetChanged();

				break;
			case FAILED:
				break;
			case NOTIFY:
				adapter1.notifyDataSetChanged();
				break;
			case SHARE:
				Post post = (Post) msg.obj;
				if (!TextUtils.isEmpty(app.user.sessionID)){
					Post pos = (Post) msg.obj;
					startActivity(new Intent(CommunitySignalActivity.this, PostDetailActivity.class).putExtra("POST", pos).putExtra("COMMENT", true));
//					click_postid = post.postID;
//					Activity cActivity = getParent() != null ? getParent()
//							: CommunitySignalActivity.this;
//					startActivityForResult(new Intent(cActivity,
//							InsertPost.class).putExtra("FLAG", InsertPost.POST)
//							.putExtra("image", post.imageUrl)
//							.putExtra("shareID", post.postID), INSERTPOST);
				}
				else{
					app.user.sessionID = "";
					MassVigUtil.setPreferenceStringData(CommunitySignalActivity.this, "SESSIONID", "");
					startActivity(new Intent(CommunitySignalActivity.this,
							LoginActivity.class));
				}
				break;
			case PRAISE:
				Post p = (Post) msg.obj;
				if (!TextUtils.isEmpty(app.user.sessionID)){
					PostList lists = mManager.allPostList;
					for (int i = 0; i < mManager.allPostList.getCount(); i++) {
						Post p1 = lists.getPost(i);
						if(p.postID == p1.postID){
							if (p.CanPraise) {
								p.CanPraise = false;
								p.praise += 1;
							}
							mHandler.sendEmptyMessage(NOTIFY);
						}
					}	
					mManager.Praise(app.user.sessionID, p.postID);
				}else{
					app.user.sessionID = "";
					MassVigUtil.setPreferenceStringData(CommunitySignalActivity.this, "SESSIONID", "");
					startActivity(new Intent(CommunitySignalActivity.this,
							LoginActivity.class));
				}
				break;
			case LOGIN:
				app.user.sessionID = "";
				MassVigUtil.setPreferenceStringData(CommunitySignalActivity.this, "SESSIONID", "");
				startActivity(new Intent(CommunitySignalActivity.this,
						LoginActivity.class));
				break;
			case HEAD:
				Post po = (Post) msg.obj;
				startActivity(new Intent(CommunitySignalActivity.this,
						CommunityUserInfoActivity.class).putExtra("USERID",
						po.customerID));
				break;
			case ITEM:
				Post pos = (Post) msg.obj;
				startActivity(new Intent(CommunitySignalActivity.this, PostDetailActivity.class).putExtra("POST", pos));
				break;
			case CONTENT:
				Post pos1 = (Post)msg.obj;
				switch (pos1.ShareSourceType) {
				case MassVigContants.Product:
					Goods goods = new Goods();
					goods.productID = pos1.RefID;
					goods.imageUrl = pos1.imageUrl;
					startActivity(new Intent(CommunitySignalActivity.this,GoodsDetailActivity.class).putExtra("goods", goods));
					break;
				case MassVigContants.Campaign:
					startActivity(new Intent(CommunitySignalActivity.this, ActionDetailActivity.class).putExtra("CAMPAIGNID", pos1.RefID));
					break;
				case MassVigContants.Share:
				case MassVigContants.Origin:
					startActivity(new Intent(CommunitySignalActivity.this, PostDetailActivity.class).putExtra("POST", pos1));
					break;

				default:
					break;
				}
				break;

			default:
				break;
			}
		}
	};

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
	}

	@Override
	public void OnActivityGroupResult(int requestCode, int resultCode,
			Intent data) {
		// TODO Auto-generated method stub
		if(requestCode == INSERTPOST && resultCode == RESULT_OK){
			for (int i = 0; i < mManager.allPostList.getCount(); i++) {
				Post p1 = mManager.allPostList.getPost(i);
				if(click_postid == p1.postID){
					p1.CanShare = false;
					p1.shared += 1;
					mHandler.sendEmptyMessage(NOTIFY);
				}
			}	
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
