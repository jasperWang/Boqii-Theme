package com.massvig.ecommerce.activities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.massvig.ecommerce.activities.CouponManageActivity.CouponAdapter.ViewHolder;
import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.Coupon;
import com.massvig.ecommerce.entities.GoodComment;
import com.massvig.ecommerce.entities.GoodCommentList;
import com.massvig.ecommerce.entities.Goods;
import com.massvig.ecommerce.entities.GoodsDetail;
import com.massvig.ecommerce.managers.CollectManager;
import com.massvig.ecommerce.managers.GoodsDetailManager;
import com.massvig.ecommerce.managers.GoodsDetailManager.LoadListener;
import com.massvig.ecommerce.service.MassVigService;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.widgets.CommentListAdapter;
import com.massvig.ecommerce.widgets.LineTextView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.TextView.OnEditorActionListener;

public class GoodsDetailActivity extends BaseActivity implements
		OnClickListener, OnGestureListener, LoadListener {
	private GoodsDetailManager mManager;
	private TranslateAnimation mAnimation;
	private ImageView m_ivMove;
	private ProgressDialog dialog;
	private BaseApplication app;

	private static final int INSERT_POST = 1;

	private ViewPager mViewPager;
	private LayoutInflater mInflater;
	private View view1, view2, view3;
	private TextView total;

	// 0
	private ListView listView;
	private boolean isLoadAdvise = false;
	private boolean isRefresh = false;
	// private LinearLayout bottomLayout;
	private CommentListAdapter mAdapter;
	private EditText commentText;
	private GestureDetector detector;
	private TextView tab1, tab2, tab3;
	private TextView no_comment;

	// 1
	private GoodCommentAdapter commentAdapter;
	private ListView goodListView;
	private ImageView imageView;
	private TextView price;
	private TextView praise_text, stamp_text;
	private TextView collect_text;
	private ProgressBar pb;
	private EditText goodComment;
	private TextView goodCommentSend;

	private WebView webview;
	private boolean isLoad = false;
	private boolean isLoaddone;
	private boolean click = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.goods_detail);
		setTitle(getString(R.string.goodsdetail));
		app = (BaseApplication) getApplication();
		dialog = new ProgressDialog(this);
		dialog.setMessage(getString(R.string.loading_data));
		detector = new GestureDetector(this);
		mManager = new GoodsDetailManager();
		mManager.setListener(this);
		Goods goods = (Goods) this.getIntent().getSerializableExtra("goods");
		mManager.getGoodsDetail().productID = goods.productID;
		mManager.getGoodsDetail().name = goods.name;
		mManager.getGoodsDetail().minPrice = goods.minPrice;
		mManager.getGoodsDetail().volume = goods.volume;
		mManager.getGoodsDetail().imageUrl = goods.imageUrl;
		Rect rect = new Rect();
		this.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		int statusBarHeight = rect.top;
		// TODO modify 205
		mManager.setMaxHeight(this.getWindowManager().getDefaultDisplay()
				.getHeight()
				- statusBarHeight - MassVigUtil.dip2px(this, 225));
		mManager.setMaxWidth(this.getWindowManager().getDefaultDisplay()
				.getWidth()
				- MassVigUtil.dip2px(this, 20));
		mManager.setMoveWidth(getWindowManager().getDefaultDisplay().getWidth() / 3);
		initView();
		mManager.FetchData(app.user.sessionID);
		mManager.FetchParams();
		mManager.FetchImgs();
		isRefresh = true;
		isLoadAdvise = true;
		mManager.FetchComment();
	}

	class GetCountAsync extends AsyncTask<String, Void, Integer> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO
			if (result > 0) {
				total.setVisibility(View.VISIBLE);
				total.setText(result + "");
				if (result > 9)
					total.setText("N");
				app.user.ShoppingCartTotalNum = result;
			}
			super.onPostExecute(result);
		}

		@Override
		protected Integer doInBackground(String... params) {
			String result = "";
			result = MassVigService.getInstance().TotalNum(app.user.sessionID);
			try {
				if (!TextUtils.isEmpty(result)) {
					try {
						JSONObject object = new JSONObject(result);
						int resultCode = object.getInt("ResponseStatus");
						if (resultCode == 0) {
							JSONObject obj = object
									.getJSONObject("ResponseData");
							int number = obj.optInt("TotalNum");
							return number;
						}
						return resultCode;
					} catch (Exception e) {
						return -1;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return -1;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		click = false;
		app = (BaseApplication) getApplication();
		app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil
				.getPreferenceData(GoodsDetailActivity.this, "SESSIONID", "")
				: app.user.sessionID;

		if (!TextUtils.isEmpty(app.user.sessionID)) {
			tempList.clearList();
			commentList.clearList();
			new LoadCommentAsync().execute(app.user.sessionID, mManager.getGoodsDetail().productID + "", "0");
		}
		// if(!TextUtils.isEmpty(app.user.sessionID) &&
		// app.user.ShoppingCartTotalNum > 0){
		// total.setVisibility(View.VISIBLE);
		// total.setText(app.user.ShoppingCartTotalNum + "");
		// }
		new GetCountAsync().execute();
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				String addText = getString(R.string.atone,
						String.valueOf(msg.obj));
				commentText.setText(addText);
				mManager.atCustomerID = msg.arg1 + "";
				commentText.setFocusable(true);
				commentText.requestFocus();
				commentText.setSelection(addText.length());
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.showSoftInput(commentText,
						InputMethodManager.SHOW_FORCED);

				// setVisible();
				break;
			case 2:
				startActivity(new Intent(GoodsDetailActivity.this,
						CommunityUserInfoActivity.class).putExtra("USERID",
						msg.arg1));
				break;
			default:
				break;
			}
		}

	};

	private void initView() {
		total = (TextView) findViewById(R.id.total);
		total.setVisibility(View.GONE);
		m_ivMove = (ImageView) findViewById(R.id.moveimg);
		mViewPager = (ViewPager) findViewById(R.id.viewPager1);
		mInflater = LayoutInflater.from(this);
		view1 = mInflater.inflate(R.layout.goods_comment, null);
		commentText = (EditText) view1.findViewById(R.id.commentedit);
		commentText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_ACTION_SEND
						|| actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {

					if (!click) {
						click = true;
						if (TextUtils.isEmpty(app.user.sessionID)) {
							app.user.sessionID = "";
							MassVigUtil.setPreferenceStringData(
									GoodsDetailActivity.this, "SESSIONID", "");
							Toast.makeText(GoodsDetailActivity.this,
									getString(R.string.login_first),
									Toast.LENGTH_SHORT).show();
							startActivity(new Intent(GoodsDetailActivity.this,
									LoginActivity.class));
							return false;
						}
						dialog.setMessage(getString(R.string.commenting));
						dialog.show();
						mManager.AddComment(app.user.sessionID, commentText
								.getText().toString());
					}
				}
				return false;
			}
		});
		no_comment = (TextView) view1.findViewById(R.id.no_comment);
		((TextView) view1.findViewById(R.id.commentbtn))
				.setOnClickListener(this);
		listView = (ListView) view1.findViewById(R.id.listview);
		commentText = (EditText) view1.findViewById(R.id.commentedit);
		mAdapter = new CommentListAdapter(this, mManager.getComments());
		mAdapter.setHandler(mHandler);
		listView.setAdapter(mAdapter);
		// mImageAdapter = new TaobaoImageAdapter(this, screenWidth, imageList);
		// mGallery.setAdapter(mImageAdapter);
		// mGallery.setSelection(1000);
		listView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return detector.onTouchEvent(event);
			}
		});
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				if (firstVisibleItem + visibleItemCount == totalItemCount
						&& totalItemCount > 0) {
					if (!isRefresh) {
						isRefresh = true;
						mManager.FetchComment();
					}
				}
			}
		});
		// bottomLayout = (LinearLayout)view1.findViewById(R.id.bottomlayout);
		view2 = mInflater.inflate(R.layout.goods_info, null);
		View headView = mInflater.inflate(R.layout.goods_detail_head, null);
		goodComment = (EditText)headView.findViewById(R.id.goodcommentedit);
		goodCommentSend = (TextView)headView.findViewById(R.id.goodcommentbtn);
		goodCommentSend.setOnClickListener(this);
		goodListView = (ListView) view2.findViewById(R.id.listview);
		goodListView.addHeaderView(headView);
		commentAdapter = new GoodCommentAdapter();
		goodListView.setAdapter(commentAdapter);
		goodListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return detector.onTouchEvent(event);
			}
		});
		goodListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				if (firstVisibleItem + visibleItemCount == totalItemCount
						&& totalItemCount > 0) {
					if (!isLoadAdvise) {
						isLoadAdvise = true;
						new LoadCommentAsync().execute(app.user.sessionID, mManager.getGoodsDetail().productID + "", commentList.commentList.size() + "");
					}
				}
			}
		});
		// TODO
		pb = (ProgressBar) headView.findViewById(R.id.pb);
		imageView = (ImageView) headView.findViewById(R.id.image);
		price = (TextView) headView.findViewById(R.id.price);
		praise_text = (TextView) view2.findViewById(R.id.praise);
//		stamp_text = (TextView) view2.findViewById(R.id.collect);
		collect_text=(TextView) view2.findViewById(R.id.collect);
		collect_text.setOnClickListener(this);
		praise_text.setOnClickListener(this);
//		stamp_text.setOnClickListener(this);

		((TextView) view2.findViewById(R.id.buy)).setOnClickListener(this);
		imageView.setOnClickListener(this);
		if (!TextUtils.isEmpty(mManager.getGoodsDetail().imageUrl)) {
			mManager.LoadImg(pb, imageView, mManager.getGoodsDetail().imageUrl);
		}
		view3 = mInflater.inflate(R.layout.goods_params, null);
		webview = (WebView) view3.findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setBuiltInZoomControls(true);
		webview.getSettings().setUseWideViewPort(true);
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		mManager.getViews().add(view1);
		mManager.getViews().add(view2);
		mManager.getViews().add(view3);
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
				((ViewPager) arg0).addView(mManager.getViews().get(arg1));
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
				((ViewPager) arg0).removeView(mManager.getViews().get(arg1));
			}
		};
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setCurrentItem(1);
		mViewPager
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

					@Override
					public void onPageScrollStateChanged(int arg0) {
					}

					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {
					}

					@Override
					public void onPageSelected(int arg0) {
						mManager.setIndex(arg0);
						tab1.setTextSize(14);
						tab2.setTextSize(14);
						tab3.setTextSize(14);
						switch (arg0) {
						case 0:
							tab1.setTextSize(16);
							break;
						case 1:
							collapseSoftInputMethod(commentText);
							tab2.setTextSize(16);
							break;
						case 2:
							collapseSoftInputMethod(commentText);
							tab3.setTextSize(16);
							break;
						default:
							break;
						}
						startAnimation();
						if (arg0 == 2
								&& !isLoad
								&& !TextUtils.isEmpty(mManager.getGoodsDetail().DescUrl)) {
							isLoad = true;
							// webview.loadUrl("http://m.kuaidi100.com/query?type=tiantian&postid=550000468643&id=1&valicode=&temp=0.046675739577040076");
							webview.loadUrl(MassVigService.getInstance()
									.getWebUrl(
											mManager.getGoodsDetail().DescUrl));
						}
					}
				});
		tab1 = (TextView) findViewById(R.id.tab1);
		tab2 = (TextView) findViewById(R.id.tab2);
		tab3 = (TextView) findViewById(R.id.tab3);
		tab1.setOnClickListener(this);
		tab2.setOnClickListener(this);
		tab3.setOnClickListener(this);
		((Button) findViewById(R.id.back)).setOnClickListener(this);
		((Button) findViewById(R.id.share)).setOnClickListener(this);
		price.setText(mManager.getGoodsDetail().minPrice + "");
		((TextView) view2.findViewById(R.id.name)).setText(mManager
				.getGoodsDetail().name);
	}

	private void showText() {
		GoodsDetail detail = mManager.getGoodsDetail();
		if (!TextUtils.isEmpty(detail.imageUrl)) {
			mManager.LoadImg(pb, imageView, detail.imageUrl);
		}
		tab1.setText(getString(R.string.comment, detail.CommentCount + ""));
		price.setText(mManager.getGoodsDetail().minPrice + "");
		((LineTextView) view2.findViewById(R.id.origin_price))
				.setText(detail.originPrice + "");
		((TextView) view2.findViewById(R.id.share)).setText(detail.ShareCount
				+ getString(R.string.ci));
		((TextView) view2.findViewById(R.id.collect)).setText(getString(R.string.collect));
		((TextView) view2.findViewById(R.id.praise)).setText(detail.PraiseCount
				+ getString(R.string.zan));
		((TextView) view3.findViewById(R.id.name)).setText(detail.name);
		if (TextUtils.isEmpty(detail.description))
			((TextView) view2.findViewById(R.id.name)).setText(detail.name);
		else
			((TextView) view2.findViewById(R.id.name))
					.setText(detail.description);
		String data = detail.productData;
		String text = "";
		if (!TextUtils.isEmpty(data)) {
			JSONArray a;
			try {
				a = new JSONArray(data);
				for (int i = 0; i < a.length(); i++) {
					JSONObject o = a.getJSONObject(i);
					text += o.getString("key") + ":" + o.getString("val")
							+ "\n";
				}
				((TextView) view2.findViewById(R.id.detail_text)).setText(text);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void startAnimation() {
		mAnimation = new TranslateAnimation(mManager.getmFromX(),
				mManager.getmToX(), 0, 0);
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
		mAnimation.setDuration(600);
		mAnimation.setFillAfter(true);
		m_ivMove.startAnimation(mAnimation);
	}

	private void tabClickListener(int index) {
		tab1.setTextSize(14);
		tab2.setTextSize(14);
		tab3.setTextSize(14);
		switch (index) {
		case 0:
			tab1.setTextSize(16);
			break;
		case 1:
			tab2.setTextSize(16);
			break;
		case 2:
			tab3.setTextSize(16);
			break;
		default:
			break;
		}
		collapseSoftInputMethod(commentText);
		mViewPager.setCurrentItem(index);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.commentbtn:
			if (TextUtils.isEmpty(app.user.sessionID)) {
				app.user.sessionID = "";
				MassVigUtil.setPreferenceStringData(this, "SESSIONID", "");
				Toast.makeText(GoodsDetailActivity.this,
						getString(R.string.login_first), Toast.LENGTH_SHORT)
						.show();
				startActivity(new Intent(GoodsDetailActivity.this,
						LoginActivity.class));
				return;
			}
			dialog.setMessage(getString(R.string.commenting));
			dialog.show();
			mManager.AddComment(app.user.sessionID, commentText.getText()
					.toString());
			break;
		case R.id.goodcommentbtn:
			if(!TextUtils.isEmpty(goodComment.getText().toString())){
				if(!TextUtils.isEmpty(app.user.sessionID))
					new AddAdviseAsync().execute(app.user.sessionID, mManager.getGoodsDetail().productID + "", goodComment.getText().toString());
				else
					GotoLogin();
			}else{
				Toast.makeText(this, getString(R.string.question), Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.tab1:
			tabClickListener(0);
			break;
		case R.id.tab2:
			tabClickListener(1);
			break;
		case R.id.tab3:
			tabClickListener(2);
			break;
		case R.id.back:
			commentText.clearFocus();
			InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
			imm.hideSoftInputFromWindow(commentText.getWindowToken(), 0); 
			GoodsDetailActivity.this.finish();
			break;
		case R.id.share:
			startActivity(new Intent(this, MainTabActivity.class).putExtra(
					"INDEX", 2).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			// ((MainTabActivity)getParent()).setTabHostIndex(2);
			// startActivity(new Intent(this, ShoppingCarActivity.class));
			// startActivityForResult(new Intent(this,
			// InsertPost.class).putExtra("image",
			// mManager.getGoodsDetail().imageUrl).putExtra("productID",
			// mManager.getGoodsDetail().productID), INSERT_POST);
			break;
		case R.id.buy:
			GoodsDetail detail = mManager.getGoodsDetail();
			if (!TextUtils.isEmpty(detail.specInfo)
					&& !detail.specInfo.equals("null")) {
				startActivity(new Intent(this, SpecActivity.class).putExtra(
						"goodsdetail", mManager.getGoodsDetail()));
			} else {
				Toast.makeText(this, getString(R.string.loading_data),
						Toast.LENGTH_SHORT).show();
				return;
			}
			break;
		case R.id.image:
			GoodsDetail det = mManager.getGoodsDetail();
			if (!TextUtils.isEmpty(det.imageLists)
					&& !det.imageLists.equals("null")) {
				startActivity(new Intent(GoodsDetailActivity.this,
						GalleryActivity.class).putExtra("IMAGES",
						det.imageLists));
			} else {
				Toast.makeText(this, getString(R.string.loading_data),
						Toast.LENGTH_SHORT).show();
			}
			break;
//		case R.id.stamp:
//			mManager.Stamp(app.user.sessionID);
//			break;
		case R.id.collect:
			mManager.AddCollect(app.user.sessionID);
			break;
		case R.id.praise:
			mManager.Praise(app.user.sessionID);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == INSERT_POST && resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		}
	}

	// private void setVisible() {
	// if (bottomLayout != null && !bottomLayout.isShown()) {
	// TranslateAnimation animation = new TranslateAnimation(0, 0, 80, 0);
	// animation.setDuration(500);
	// animation.setAnimationListener(new AnimationListener() {
	//
	// @Override
	// public void onAnimationStart(Animation animation) {
	// bottomLayout.setVisibility(View.VISIBLE);
	//
	// }
	//
	// @Override
	// public void onAnimationRepeat(Animation animation) {
	//
	// }
	//
	// @Override
	// public void onAnimationEnd(Animation animation) {
	// }
	// });
	// bottomLayout.startAnimation(animation);
	// }
	// }

	// private void setGone() {
	// if (bottomLayout != null && bottomLayout.isShown()) {
	// TranslateAnimation animation = new TranslateAnimation(0, 0, 0, 80);
	// animation.setDuration(500);
	// animation.setAnimationListener(new AnimationListener() {
	//
	// @Override
	// public void onAnimationStart(Animation animation) {
	//
	// }
	//
	// @Override
	// public void onAnimationRepeat(Animation animation) {
	//
	// }
	//
	// @Override
	// public void onAnimationEnd(Animation animation) {
	// bottomLayout.setVisibility(View.GONE);
	// }
	// });
	// bottomLayout.startAnimation(animation);
	// }
	// }

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// if (e1 != null && e2 != null) {
		// if (e1.getY() - e2.getY() > FLING_MIN_DISTANCE) {
		// setVisible();
		// } else if (e2.getY() - e1.getY() > FLING_MIN_DISTANCE) {
		// setGone();
		// }
		// }
		return false;
	}

	@Override
	public void LoadSuccess() {
		showText();
	}

	@Override
	public void LoadFailed() {

	}

	@Override
	public void LoadParamsSuccess() {
		showText();
	}

	@Override
	public void LoadParamsFailed() {

	}

	@Override
	public void LoadCommentSuccess() {
		isRefresh = false;
		commentText.setText("");
		mManager.refreshCommentsList();
		if (mManager.getComments().getCount() > 0) {
			no_comment.setVisibility(View.GONE);
		} else {
			no_comment.setVisibility(View.VISIBLE);
		}
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void LoadCommentFailed() {
		isRefresh = false;
	}

	@Override
	public void StartLoading() {
	}

	@Override
	public void StopLoading() {
	}

	@Override
	public void AddCommentFailed() {
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();
		Toast.makeText(this, getString(R.string.comment_failed),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void AddCommentSuccess() {
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();
		Toast.makeText(this, getString(R.string.comment_success),
				Toast.LENGTH_SHORT).show();
		mManager.isLoadDone = false;
		mManager.getComments().clearCommentList();
		mManager.FetchComment();
	}

	@Override
	public void PraiseSuccess(int index) {
		GoodsDetail gd = mManager.getGoodsDetail();
		switch (index) {
		case GoodsDetailManager.PRAISE:
			praise_text.setText((gd.PraiseCount + 1) + getString(R.string.zan));
			break;
		case GoodsDetailManager.STAMP:
			stamp_text.setText((gd.StampCount + 1) + getString(R.string.cai));
			break;

		default:
			break;
		}
	}

	public void collapseSoftInputMethod(EditText inputText) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(inputText.getWindowToken(), 0);
	}

	@Override
	public void PraiseFailed(int index) {
		Toast.makeText(this, getString(R.string.already), Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void SessionVailed() {
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();
		// if(!TextUtils.isEmpty(app.user.sessionID +
		// MassVigUtil.getPreferenceData(this, "SESSIONID", "")))
		startActivity(new Intent(this, LoginActivity.class));
		app.user.sessionID = "";
		MassVigUtil.setPreferenceStringData(this, "SESSIONID", "");
	}

	@Override
	public void Already() {
		Toast.makeText(this, getString(R.string.already), Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void GoodsDown() {
		Toast.makeText(this, getString(R.string.already_down),
				Toast.LENGTH_SHORT).show();
	}

	private GoodCommentList commentList = new GoodCommentList();
	private GoodCommentList tempList = new GoodCommentList();

	class GoodCommentAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return commentList.commentList.size();
		}

		@Override
		public Object getItem(int position) {
			return commentList.commentList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			convertView = holder.getView(position);
			return convertView;
		}

		class ViewHolder {
			private View itemView;
			private TextView question, answer, information;

			public ViewHolder() {
				itemView = mInflater
						.inflate(R.layout.good_comment_item, null);
				itemView.setTag(this);
				question = (TextView) itemView.findViewById(R.id.question);
				answer = (TextView) itemView.findViewById(R.id.answer);
				information = (TextView) itemView
						.findViewById(R.id.information);
			}

			public View getView(int position) {
				GoodComment gc = commentList.commentList.get(position);
				question.setText(gc.Question);
				answer.setText(gc.Answer);
				information.setText(gc.NickName
						+ getString(R.string.comment_infor) + gc.CreateTime);
				return itemView;
			}
		}
	}

	class LoadCommentAsync extends AsyncTask<String, Void, Boolean> {

		@Override
		protected void onPostExecute(Boolean result) {
//			if (dialog != null && dialog.isShowing())
//				dialog.dismiss();
			super.onPostExecute(result);
			isLoadAdvise = false;
			if (result) {
				commentList.addCommentList(tempList);
				tempList.clearList();
				commentAdapter.notifyDataSetChanged();
			}
		}

		@Override
		protected void onPreExecute() {
//			if (dialog != null && !dialog.isShowing())
//				dialog.show();
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			tempList.clearList();
			boolean result = tempList.FetchCommentList(params[0], params[1],
					params[2]);
			if (tempList.commentList.size() == 0) {
				isLoaddone = true;
			}
			return result;
		}

	}

	class AddAdviseAsync extends AsyncTask<String, Void, Integer> {

		@Override
		protected void onPostExecute(Integer result) {
			if (dialog != null && dialog.isShowing())
				dialog.dismiss();
			super.onPostExecute(result);
			if (result == 0) {
				Toast.makeText(GoodsDetailActivity.this, getString(R.string.question_commit), Toast.LENGTH_SHORT).show();
			}else if(result == MassVigContants.SESSIONVAILED){
				GotoLogin();
			}
		}

		@Override
		protected void onPreExecute() {
			if (dialog != null && !dialog.isShowing())
				dialog.show();
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(String... params) {
			String result = MassVigService.getInstance().AddAdvice(params[0], Integer.parseInt(params[1]), params[2]);
			int resultCode = -1;
			if(!TextUtils.isEmpty(result) && !result.equals("null")){
				JSONObject o;
				try {
					o = new JSONObject(result);
					resultCode = o.optInt("ResponseStatus");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return resultCode;
		}

	}

	@Override
	public void addCollectsucess() {
		// TODO Auto-generated method stub
		if(dialog!=null&&dialog.isShowing())
			dialog.dismiss();
		Toast.makeText(this, getString(R.string.addCollect_success), Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void addCollectfailed() {
		// TODO Auto-generated method stub
		if(dialog != null && dialog.isShowing())
			dialog.dismiss();
		Toast.makeText(this, getString(R.string.addCollect_failed), Toast.LENGTH_SHORT).show();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(dialog != null && dialog.isShowing())
			dialog.dismiss();
	}

}
