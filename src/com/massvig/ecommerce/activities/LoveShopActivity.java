package com.massvig.ecommerce.activities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.Post;
import com.massvig.ecommerce.service.MassVigService;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.widgets.FlowTag;
import com.massvig.ecommerce.widgets.TaobaoImages;
import com.massvig.ecommerce.widgets.TaobaoWaterFall;
import com.massvig.ecommerce.widgets.TaobaoWaterFall.onLoadListener;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * 
 * @author zbp
 *
 */
public class LoveShopActivity extends Activity{

	private ProgressDialog dialog;
	private TaobaoWaterFall waterfall;
	private boolean isLoading = false;//是否正在加载
	private boolean isLoadDone = false;//是否全部加载完成
	private BaseApplication app;
	private ArrayList<TaobaoImages> items = new ArrayList<TaobaoImages>();
	private int customerID;
	private int campaignID;
	
	public static final int SHARE = 1;
	public static final int PRAISE = 2;
	public static final int ACTION_POST = 5;
	private int flag ;
	private static final int SESSIONID = 3;
	private static final int FAILED = 4;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.goodswater);
		app = (BaseApplication) getApplication();
		customerID = this.getIntent().getIntExtra("USERID", 0);
		flag = this.getIntent().getIntExtra("type", SHARE);
		campaignID = this.getIntent().getIntExtra("CAMPAIGNID", -1);
		InitLayout();
	}

	@Override
	protected void onResume() {
		super.onResume();
		reset();
	}

	private void reset() {
		waterfall.loaded_count = 0;
		isLoadDone = false;
		isLoading = false;
//		if(waterfall!= null && waterfall.getChildCount() > 0 && ((LazyScrollView)waterfall.getChildAt(0)).getChildCount() > 0){
//			((LinearLayout)((LazyScrollView)waterfall.getChildAt(0)).getChildAt(0)).removeAllViews();
//		}
		if(waterfall != null){
			waterfall.clearViews();
		}
		loadData(waterfall.loaded_count);
	}
	
	private void InitLayout() {
		dialog = new ProgressDialog(this);
		dialog.setMessage(getString(R.string.load_bautique));
		waterfall = (TaobaoWaterFall) findViewById(R.id.taobaowaterfall);
//		loadData(waterfall.loaded_count);
		waterfall.setOnLoadListener(new onLoadListener() {

			@Override
			public void onScrollBottom() {
				// TODO Auto-generated method stub
				if(!isLoading && !isLoadDone){
					loadData(waterfall.loaded_count);
				}
			}

			@Override
			public void onClickItemImage(String picurl, FlowTag tag) {

				Post pos = new Post();
				pos.createTime = tag.createTime;
				pos.CanPraise = tag.CanPraise;
				pos.address = tag.Address;
				pos.customerID = tag.CustomerID;
				pos.imageUrl = tag.ImgUrl;
				pos.postID = tag.ShareID;
				pos.praise = tag.PraiseCount;
				pos.RefID = tag.RefID;
				pos.shared = tag.SharedCount;
				pos.ShareSourceType = tag.ShareSourceType;
				pos.userimgurl = tag.HeadImgUrl;
				pos.username = tag.NickName;
				pos.detail = tag.Content;
				startActivity(new Intent(LoveShopActivity.this, PostDetailActivity.class).putExtra("POST", pos));
//				switch (tag.ShareSourceType) {
//				case MassVigContants.Product:
//					Goods goods = new Goods();
//					goods.productID = tag.RefID;
//					goods.name = "";
//					goods.minPrice = 0;
//					goods.volume = 0;
//					goods.imageUrl = tag.ImgUrl;
//					startActivity(new Intent(LoveShopActivity.this, GoodsDetailActivity.class).putExtra("goods", goods));
//					break;
//				case MassVigContants.Campaign:
//					startActivity(new Intent(LoveShopActivity.this, ActionDetailActivity.class).putExtra("CAMPAIGNID", tag.RefID));
//					break;
//				case MassVigContants.Share:
//				case MassVigContants.Origin:
//					Post pos = new Post();
//					pos.createTime = tag.createTime;
//					pos.CanPraise = tag.CanPraise;
//					pos.address = tag.Address;
//					pos.customerID = tag.CustomerID;
//					pos.imageUrl = tag.ImgUrl;
//					pos.postID = tag.ShareID;
//					pos.praise = tag.PraiseCount;
//					pos.RefID = tag.RefID;
//					pos.shared = tag.SharedCount;
//					pos.ShareSourceType = tag.ShareSourceType;
//					pos.userimgurl = tag.HeadImgUrl;
//					pos.username = tag.NickName;
//					startActivity(new Intent(LoveShopActivity.this, PostDetailActivity.class).putExtra("POST", pos));
//					break;
//
//				default:
//					break;
//				}
				
			}

			@Override
			public void onClickPraise(FlowTag tag) {
				app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(LoveShopActivity.this, "SESSIONID", "") : app.user.sessionID;
				if(!TextUtils.isEmpty(app.user.sessionID)){
					waterfall.addPraise(tag.getFlowId());
					Praise(app.user.sessionID, tag.ShareID, tag.getFlowId());
				}else{
					app.user.sessionID = "";
					MassVigUtil.setPreferenceStringData(LoveShopActivity.this, "SESSIONID", "");
					startActivity(new Intent(LoveShopActivity.this, LoginActivity.class));
				}
			}

			@Override
			public void onClickShare(FlowTag tag) {
				app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(LoveShopActivity.this, "SESSIONID", "") : app.user.sessionID;
				if(!TextUtils.isEmpty(app.user.sessionID))
					startActivity(new Intent(LoveShopActivity.this, InsertPost.class).putExtra("image", tag.ImgUrl).putExtra("productID", tag.RefID));
				else{
					app.user.sessionID = "";
					MassVigUtil.setPreferenceStringData(LoveShopActivity.this, "SESSIONID", "");
					startActivity(new Intent(LoveShopActivity.this, LoginActivity.class));
				}
			}

			@Override
			public void onClickUserImg(FlowTag tag) {
				startActivity(new Intent(LoveShopActivity.this, CommunityUserInfoActivity.class).putExtra("USERID", tag.CustomerID));
			}

			@Override
			public void onClickLocation(FlowTag tag) {
				// TODO Auto-generated method stub
			}
		});
	}


	private void Praise(String sessionid, int postid, int itemid){
		new PraiseAsync().execute(sessionid, postid + "", itemid + "");
	}
	
	private class PraiseAsync extends AsyncTask<String, Void, Void>{

		@Override
		protected Void doInBackground(String... params) {

			String result = MassVigService.getInstance().Praise(params[0], Integer.valueOf(params[1]));
			if(!TextUtils.isEmpty(result)){
				JSONObject o;
				try {
					o = new JSONObject(result);
					if(o.getInt("ResponseStatus") == 0){
						int postid = Integer.valueOf(params[1]);
						for (int i = 0; i < items.size(); i++) {
							TaobaoImages image = items.get(i);
							if(image.ShareID == postid){
								image.CanPraise = false;
								Message msg = new Message();
								msg.what = PRAISE;
								msg.arg1 = Integer.valueOf(params[2]);
								mHandler.sendMessage(msg);
								return null;
							}
						}
					}else if(o.getInt("ResponseStatus") == MassVigContants.SESSIONVAILED){
						GoToLogin();
					}else{
						mHandler.sendEmptyMessage(FAILED);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}
		
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case SESSIONID:
				app.user.sessionID = "";
				MassVigUtil.setPreferenceStringData(LoveShopActivity.this, "SESSIONID", "");
				startActivity(new Intent(LoveShopActivity.this, LoginActivity.class));
				break;
			case FAILED:
				
				break;
			case PRAISE:
				waterfall.addPraise(msg.arg1);
				break;
			default:
				break;
			}
		}};
	
	public void loadData(int startIndex){
		String result = "";
		app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(this, "SESSIONID", "") : app.user.sessionID;
		if(flag == SHARE){
			result = MassVigService.getInstance().GetShares(app.user.sessionID, customerID, startIndex, 10);
		}else if(flag == PRAISE){
			result = MassVigService.getInstance().GetHavePaisedShares(app.user.sessionID, customerID, startIndex, 10);
		}else if(flag == ACTION_POST){
			result = MassVigService.getInstance().GetCampaignShare(app.user.sessionID, campaignID, startIndex, 10);
		}
		if(!TextUtils.isEmpty(result)){
				try {
					JSONObject o = new JSONObject(result);
					if(o.getInt("ResponseStatus") == 0){
						JSONArray array = o.getJSONArray("ResponseData");
						items = new ArrayList<TaobaoImages>();
						for (int i = 0; i < array.length(); i++) {
							JSONObject data = array.getJSONObject(i);
							TaobaoImages image = new TaobaoImages();
							image.Address = data.getString("Address");
//							image.ProductID = data.getInt("ProductID");
							image.RefID = data.getInt("RefID");
							image.ShareSourceType = data.getInt("ShareSourceType");
							image.CanPraise = data.getBoolean("CanPraise");
							image.Content = data.getString("Content");
							image.CustomerID = data.getInt("CustomerID");
							image.HeadImgUrl = data.getString("HeadImgUrl");
							image.ImgUrl = data.getString("ImgUrl");
							image.NickName = data.getString("NickName");
							image.PraiseCount = data.getInt("PraisedCount");
							image.SharedCount = data.getInt("SharedCount");
							image.ShareID = data.getInt("ShareID");
							if(data.has("Height")){
								image.Height = data.getInt("Height");
								image.Width = data.getInt("Width");
							}
							if(data.has("CreateTime")){
								image.createTime = data.getString("CreateTime");
							}
							items.add(image);
						}
						if(items.size() > 0)
							waterfall.addItems(items);
						else{
							isLoadDone = true;
							if(startIndex == 0){
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
		}
	}
	
	public void GoToLogin() {
//		if(!TextUtils.isEmpty(app.user.sessionID + MassVigUtil.getPreferenceData(this, "SESSIONID", "")))
			mHandler.sendEmptyMessage(SESSIONID);
		Toast.makeText(this, getString(R.string.account_failed), Toast.LENGTH_SHORT).show();
		app.user.sessionID = "";
		MassVigUtil.setPreferenceStringData(LoveShopActivity.this, "SESSIONID", "");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(dialog != null){
			if(dialog.isShowing()){
				dialog.dismiss();
			}
			dialog = null;
		}
	}

	
}
