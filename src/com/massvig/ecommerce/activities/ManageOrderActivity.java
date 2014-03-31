package com.massvig.ecommerce.activities;

import java.util.ArrayList;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.MyOrder;
import com.massvig.ecommerce.managers.OrderManager;
import com.massvig.ecommerce.managers.OrdersManager;
import com.massvig.ecommerce.managers.OrdersManager.LoadListener;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.widgets.NetImageView;
import com.massvig.ecommerce.widgets.OrderAdapter;
import com.massvig.ecommerce.widgets.RefreshListView;
import com.massvig.ecommerce.widgets.RefreshListView.RefreshListener;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
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
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

public class ManageOrderActivity extends BaseActivity implements LoadListener,
		OnClickListener {

	private BaseApplication app;
	private OrdersManager mManager;
	private TranslateAnimation mAnimation;
	private ImageView m_ivMove;
	private ViewPager mViewPager;
	private LayoutInflater mInflater;
	private View view1, view2, view3, view4;
	private RefreshListView listview1, listview2, listview3, listview4;
	private OrderAdapter adapter1, adapter2, adapter3, adapter4;
	public static final int SUCCESS = 1;
	public static final int FAILED = 2;
	public static final int LOGIN = 3;
	private int orderTab = 1;
	private int tabs = 4;
	private boolean isScolling;
	private static final int DETAIL = 4;
	private boolean isLoadingMore = false;// 是否是上拉加载更多操作true:加载更多 false:刷新
	private LinearLayout nodata;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.managerorder));
		setContentView(R.layout.manager_order);
		app = (BaseApplication) getApplication();
		mManager = new OrdersManager();
		mManager.setListener(this);
		orderTab = this.getIntent().getIntExtra("orderTab", 1);
		mManager.setMaxWidth(this.getWindowManager().getDefaultDisplay()
				.getWidth());
		mManager.setMoveWidth(getWindowManager().getDefaultDisplay().getWidth()
				/ tabs);
		initView();
		app = (BaseApplication) getApplication();
		mManager.clearHuodaoData();
		mManager.clearPaidData();
		if (tabs == 4) {
			mManager.clearUnpayData();
			((TextView) findViewById(R.id.tab3)).setVisibility(View.VISIBLE);
		} else
			((TextView) findViewById(R.id.tab3)).setVisibility(View.GONE);
		if (orderTab == 3) {
			orderTab = 4;
		} else if (orderTab == 4) {
			orderTab = 3;
		}
		mViewPager.setCurrentItem(orderTab - 1);
		app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil
				.getPreferenceData(ManageOrderActivity.this, "SESSIONID", "")
				: app.user.sessionID;
		if (orderTab == 1)
			mManager.LoadData(app.user.sessionID, orderTab,
					OrdersManager.REFRESH);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// mManager.clearAll();
		// mManager.LoadData(app.user.sessionID, orderTab);
	}

	private void initView() {
		m_ivMove = (ImageView) findViewById(R.id.moveimg);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.setMargins(mManager.getMaxWidth() / (tabs * 2) - 15, 0, 0, 0);
		m_ivMove.setLayoutParams(lp);
		mViewPager = (ViewPager) findViewById(R.id.viewPager1);
		mInflater = LayoutInflater.from(this);

		view1 = mInflater.inflate(R.layout.orders, null);
		nodata = (LinearLayout) view1.findViewById(R.id.nodata);
		((Button) view1.findViewById(R.id.gotobuy))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(ManageOrderActivity.this,
								MainTabActivity.class).putExtra("INDEX", 0)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
					}
				});
		listview1 = (RefreshListView) view1.findViewById(R.id.listview);
		adapter1 = new OrderAdapter(this, mManager.unpayList,
				OrdersManager.UNPAYORDER);
		listview1.setAdapter(adapter1);
		initListView(listview1, adapter1);
		listview1
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, final int position, long id) {

						Builder builder = new android.app.AlertDialog.Builder(
								ManageOrderActivity.this);
						// 设置对话框的标题
						builder.setTitle(getString(R.string.select_opts));
						// 0: 默认第一个单选按钮被选中
						builder.setSingleChoiceItems(
								new String[] { getString(R.string.cancel_order) },
								0, new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										OrderManager manager = new OrderManager(
												ManageOrderActivity.this);
										manager.orderID = ((MyOrder) adapter1
												.getItem(which)).OrderID;
										manager.setListener(new OrderManager.LoadListener() {

											@Override
											public void Success(int index) {
												if (index == OrderManager.CANCEL) {
													mManager.unpayList
															.deleteMyOrder(position);
													adapter1.notifyDataSetChanged();
												}
											}

											@Override
											public void Failed(int index) {
												if (index == OrderManager.CANCEL) {
													Toast.makeText(
															ManageOrderActivity.this,
															"取消订单失败",
															Toast.LENGTH_SHORT)
															.show();
												}
											}

											@Override
											public void Payment(String result) {
												// TODO Auto-generated method
												// stub

											}

											@Override
											public void SessionFailed() {
												GoToLogin();
											}
										});
										app.user.sessionID = TextUtils
												.isEmpty(app.user.sessionID) ? MassVigUtil
												.getPreferenceData(
														ManageOrderActivity.this,
														"SESSIONID", "")
												: app.user.sessionID;
										manager.CancelOrder(app.user.sessionID);
									}
								});
						Dialog dialog = builder.create();
						dialog.show();

						// new AlertDialog.Builder(ManageOrderActivity.this)
						// .setCancelable(false)
						// .setTitle(getString(R.string.delete_order))
						// .setPositiveButton(getString(R.string.sure), new
						// DialogInterface.OnClickListener() {
						// @Override
						// public void onClick(DialogInterface dialog, int
						// which) {
						// OrderManager manager = new
						// OrderManager(ManageOrderActivity.this);
						// manager.orderID =
						// ((MyOrder)adapter1.getItem(position)).OrderID;
						// manager.setListener(new OrderManager.LoadListener(){
						//
						// @Override
						// public void Success(int index) {
						// if(index == OrderManager.CANCEL){
						// mManager.unpayList.deleteMyOrder(position);
						// adapter1.notifyDataSetChanged();
						// }
						// }
						//
						// @Override
						// public void Failed(int index) {
						// if(index == OrderManager.CANCEL){
						// Toast.makeText(ManageOrderActivity.this, "取消订单失败",
						// Toast.LENGTH_SHORT).show();
						// }
						// }
						//
						// @Override
						// public void Payment(String result) {
						// // TODO Auto-generated method stub
						//
						// }
						//
						// @Override
						// public void SessionFailed() {
						// GoToLogin();
						// }});
						// app.user.sessionID =
						// TextUtils.isEmpty(app.user.sessionID) ?
						// MassVigUtil.getPreferenceData(ManageOrderActivity.this,
						// "SESSIONID", "") : app.user.sessionID;
						// manager.CancelOrder(app.user.sessionID);
						// }
						// })
						// .setNegativeButton(getString(R.string.cancel), new
						// DialogInterface.OnClickListener() {
						//
						// @Override
						// public void onClick(DialogInterface dialog, int
						// which) {
						// dialog.dismiss();
						// }
						// })
						// .show();
						return false;
					}
				});
		mManager.getViews().add(view1);

		view2 = mInflater.inflate(R.layout.community_share, null);
		listview2 = (RefreshListView) view2.findViewById(R.id.listview);
		adapter2 = new OrderAdapter(this, mManager.paidList,
				OrdersManager.PAIDORDER);
		listview2.setAdapter(adapter2);
		initListView(listview2, adapter2);
		mManager.getViews().add(view2);

		view4 = mInflater.inflate(R.layout.community_share, null);
		listview4 = (RefreshListView) view4.findViewById(R.id.listview);
		adapter4 = new OrderAdapter(this, mManager.refundList,
				OrdersManager.REFUNDORDER);
		listview4.setAdapter(adapter4);
		initListView(listview4, adapter4);
		mManager.getViews().add(view4);

		if (tabs == 4) {
			view3 = mInflater.inflate(R.layout.community_share, null);
			listview3 = (RefreshListView) view3.findViewById(R.id.listview);
			adapter3 = new OrderAdapter(this, mManager.huodaoList,
					OrdersManager.HUODAOORDER);
			listview3.setAdapter(adapter3);
			initListView(listview3, adapter3);
			mManager.getViews().add(view3);
		}

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
		mViewPager.setCurrentItem(0);
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
						orderTab = arg0 + 1;
						if (orderTab == 3) {
							orderTab = 4;
						} else if (orderTab == 4) {
							orderTab = 3;
						}
						mManager.setIndex(arg0);
						app.user.sessionID = TextUtils
								.isEmpty(app.user.sessionID) ? MassVigUtil
								.getPreferenceData(ManageOrderActivity.this,
										"SESSIONID", "") : app.user.sessionID;
						mManager.LoadData(app.user.sessionID, orderTab,
								OrdersManager.REFRESH);
						startAnimation();
					}
				});
		((TextView) findViewById(R.id.tab1)).setOnClickListener(this);
		((TextView) findViewById(R.id.tab2)).setOnClickListener(this);
		((TextView) findViewById(R.id.tab3)).setOnClickListener(this);
		((TextView) findViewById(R.id.tab4)).setOnClickListener(this);
		((Button) findViewById(R.id.back)).setOnClickListener(this);
	}

	private void initListView(final RefreshListView l,
			final OrderAdapter adapter32) {
		l.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if(arg2 - 1 >= 0){
				MyOrder o = (MyOrder) adapter32.getItem(arg2 - 1);
				startActivityForResult(new Intent(ManageOrderActivity.this,
						OrderDetailActivity.class).putExtra("order", o), DETAIL);
				}
			}
		});
		l.setOnRefreshListener(new RefreshListener() {

			@Override
			public void startRefresh() {
				isLoadingMore = false;
				// mManager.clearData(orderTab);
				app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil
						.getPreferenceData(ManageOrderActivity.this,
								"SESSIONID", "") : app.user.sessionID;
				mManager.LoadData(app.user.sessionID, orderTab,
						OrdersManager.REFRESH);
			}

			@Override
			public void startLoadMore() {
				isLoadingMore = true;
				app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil
						.getPreferenceData(ManageOrderActivity.this,
								"SESSIONID", "") : app.user.sessionID;
				mManager.LoadData(app.user.sessionID, orderTab,
						OrdersManager.MORE);
				mManager.ismore = true;
			}

			@Override
			public void scrollStop() {
				isScolling = false;
				NetImageView.setIsAutoLoadImage(true);
				ArrayList<NetImageView> imageLists = adapter32.getImageList();
				for (int i = 0, len = imageLists.size(); i < len; i++) {
					if (isScolling == false) {
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
		// l.setOnScrollListener(new OnScrollListener() {
		//
		// @Override
		// public void onScrollStateChanged(AbsListView view, int scrollState) {
		//
		// if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
		// isScolling = false;
		// NetImageView.setIsAutoLoadImage(true);
		// ArrayList<NetImageView> imageLists = adapter32.getImageList();
		// for(int i = 0,len = imageLists.size();i < len ;i++){
		// if(isScolling == false){
		// imageLists.get(i).updateImage();
		// }
		// }
		// }else if(scrollState == OnScrollListener.SCROLL_STATE_FLING){
		// isScolling = true;
		// NetImageView.setIsAutoLoadImage(false);
		// }
		// }
		//
		// @Override
		// public void onScroll(AbsListView view, int firstVisibleItem,
		// int visibleItemCount, int totalItemCount) {
		// if (firstVisibleItem + visibleItemCount >= totalItemCount - 1
		// && totalItemCount > 0) {
		// if (!mManager.isLoading) {
		// mManager.LoadData(app.user.sessionID, orderTab);
		// }
		// }
		// }
		// });
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
		mAnimation.setDuration(200);
		mAnimation.setFillAfter(true);
		m_ivMove.startAnimation(mAnimation);
	}

	private void tabClickListener(int index) {
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
		case R.id.tab3:
			tabClickListener(3);
			break;
		case R.id.tab4:
			tabClickListener(2);
			break;
		case R.id.back:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void LoadSuccess(int index) {
		Message msg = new Message();
		msg.what = SUCCESS;
		msg.arg1 = index;
		mHandler.sendMessage(msg);
	}

	@Override
	public void LoadFailed(int index) {
		// TODO Auto-generated method stub
		switch (index) {
		case OrdersManager.UNPAYORDER:
			if (isLoadingMore) {
				listview1.finishFootView();
			} else {
				listview1.finishHeadView();
				listview1.setTextShow(getString(R.string.down_pull_refresh));
			}
			if (adapter1.getCount() > 0) {
				nodata.setVisibility(View.GONE);
				listview1.setVisibility(View.VISIBLE);
			} else {
				nodata.setVisibility(View.VISIBLE);
				listview1.setVisibility(View.GONE);
			}
			break;
		case OrdersManager.PAIDORDER:
			if (isLoadingMore) {
				listview2.finishFootView();
			} else {
				listview2.finishHeadView();
				listview2.setTextShow(getString(R.string.down_pull_refresh));
			}
			break;
		case OrdersManager.HUODAOORDER:
			if (isLoadingMore) {
				listview3.finishFootView();
			} else {
				listview3.finishHeadView();
				listview3.setTextShow(getString(R.string.down_pull_refresh));
			}
			break;
		case OrderManager.REFUND:
			if (isLoadingMore) {
				listview4.finishFootView();
			} else {
				listview4.finishHeadView();
				listview4.setTextShow(getString(R.string.down_pull_refresh));
			}
			break;
		default:
			break;
		}

	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {

			case SUCCESS:
				switch (msg.arg1) {
				case OrdersManager.UNPAYORDER:
					if (isLoadingMore) {
						listview1.finishFootView();
					} else {
						listview1.finishHeadView();
						listview1
								.setTextShow(getString(R.string.down_pull_refresh));
					}
					mManager.refreshUnpayList();
					if (adapter1.getCount() > 0) {
						nodata.setVisibility(View.GONE);
						listview1.setVisibility(View.VISIBLE);
					} else {
						nodata.setVisibility(View.VISIBLE);
						listview1.setVisibility(View.GONE);
					}
					adapter1.notifyDataSetChanged();
					break;
				case OrdersManager.PAIDORDER:
					if (isLoadingMore) {
						listview2.finishFootView();
					} else {
						listview2.finishHeadView();
						listview2
								.setTextShow(getString(R.string.down_pull_refresh));
					}
					mManager.refreshPaidList();
					adapter2.notifyDataSetChanged();
					break;
				case OrdersManager.HUODAOORDER:
					if (isLoadingMore) {
						listview3.finishFootView();
					} else {
						listview3.finishHeadView();
						listview3
								.setTextShow(getString(R.string.down_pull_refresh));
					}
					mManager.refreshHuodaoList();
					adapter3.notifyDataSetChanged();
					break;
				case OrderManager.REFUND:
					if (isLoadingMore) {
						listview4.finishFootView();
					} else {
						listview4.finishHeadView();
						listview4
								.setTextShow(getString(R.string.down_pull_refresh));
					}
					mManager.refreshRefundList();
					adapter4.notifyDataSetChanged();
					break;
				default:
					break;
				}

				break;
			case FAILED:
				break;
			case LOGIN:
				GoToLogin();
				break;

			default:
				break;
			}
		}
	};

	@Override
	public void SessionFailed() {
		GoToLogin();
	}

	private void GoToLogin() {
		// if(!TextUtils.isEmpty(app.user.sessionID +
		// MassVigUtil.getPreferenceData(this, "SESSIONID", "")))
		startActivity(new Intent(this, LoginActivity.class));
		app.user.sessionID = "";
		MassVigUtil.setPreferenceStringData(this, "SESSIONID", "");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == DETAIL) {
		
			app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil
					.getPreferenceData(ManageOrderActivity.this, "SESSIONID",
							"") : app.user.sessionID;
			mManager.LoadData(app.user.sessionID, orderTab,
					OrdersManager.REFRESH);
		}
	}

}
