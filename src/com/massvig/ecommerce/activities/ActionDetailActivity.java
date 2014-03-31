package com.massvig.ecommerce.activities;

import java.io.File;

import com.google.analytics.tracking.android.EasyTracker;
import com.massvig.ecommerce.entities.ActionDetail;
import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.managers.ActionManager;
import com.massvig.ecommerce.managers.ActionManager.LoadListener;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.utilities.OAuthConstant;
import com.massvig.ecommerce.utilities.ShareManager;
import com.massvig.ecommerce.widgets.NetImageView;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.weibo.api.UserAPI;
import com.tencent.weibo.constants.OAuthConstants;
import com.tencent.weibo.oauthv2.OAuthV2;
import com.tencent.weibo.webview.OAuthV2AuthorizeWebView;

import android.app.ActivityGroup;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class ActionDetailActivity extends ActivityGroup implements LoadListener, OnClickListener {

	private TabHost tabHost;
	private ActionManager manager;
	private BaseApplication app;
	private NetImageView imageView;
	private TextView detail;
	private ScrollView rule_layout;
	private OAuthV2 oAuth;
	private static final int QQ = 101;
	private static final int SINA = 102;
	private String uploadImgUrlSd = "";
	private IWXAPI wxapi;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.action_detail);
		setTitle(getString(R.string.new_action_detail));
		oAuth = new OAuthV2(OAuthConstant.callback);
		oAuth.setClientId(OAuthConstant.APP_KEY_QQ);
		oAuth.setClientSecret(OAuthConstant.APP_SECRET_QQ);
		app = (BaseApplication) getApplication();
		app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(this, "SESSIONID", "") : app.user.sessionID;
		manager = new ActionManager();
		manager.setListener(this);
		manager.detail.CampaignID = this.getIntent().getIntExtra("CAMPAIGNID", -1);
		initView();
		
		wxapi = WXAPIFactory.createWXAPI(this, OAuthConstant.WX_APP_ID,false);
		wxapi.registerApp(OAuthConstant.WX_APP_ID);
	}

	private void initView() {
		imageView = (NetImageView)findViewById(R.id.imageview);
		detail = (TextView)findViewById(R.id.detail);
		((Button)findViewById(R.id.sina)).setOnClickListener(this);
		((Button)findViewById(R.id.qq)).setOnClickListener(this);
		((Button)findViewById(R.id.weixin)).setOnClickListener(this);
		rule_layout = (ScrollView)findViewById(R.id.rule_layout);
		((LinearLayout)findViewById(R.id.rule)).setOnClickListener(this);
		((LinearLayout)findViewById(R.id.actin)).setOnClickListener(this);
		((Button)findViewById(R.id.back)).setOnClickListener(this);
		LoadData();
		initTabHost();
	}

	public void LoadData() {
		manager.LoadData(app.user.sessionID);
	}
	
	public void initTabHost() {

		tabHost = (TabHost) findViewById(R.id.tabhost);
		tabHost.setup(this.getLocalActivityManager());

		Intent intent1 = new Intent(this, LoveShopActivity.class)
				.putExtra("type", LoveShopActivity.ACTION_POST).putExtra("CAMPAIGNID", manager.detail.CampaignID)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("tab11")
				.setContent(intent1));

	}

	@Override
	public void Success(int index) {
		if(index == ActionManager.LOADDATA){
			initView(manager.detail);
		}
	}

	private void initView(ActionDetail detail) {
		//TODO
		imageView.setImageUrl(MassVigUtil.GetImageUrl(detail.CampaignImgUrl, 550, 270), MassVigContants.PATH, null);
		String imageUrl = MassVigUtil.GetImageUrl(detail.CampaignImgUrl, 550, 270);
		int index = imageUrl.lastIndexOf("/");
		if(index != -1){
			this.uploadImgUrlSd = MassVigContants.PATH + imageUrl.substring(index, imageUrl.length());
		}
		this.detail.setText(detail.Description);
		((TextView)findViewById(R.id.title)).setText(detail.CampaignTitle);
		((TextView)findViewById(R.id.subtitle)).setText(detail.CampaignSubTitle);
		((TextView)findViewById(R.id.rule_text)).setText(detail.CampaignRule);
		((LinearLayout)findViewById(R.id.share_layout)).setVisibility(detail.AllowShare ? View.VISIBLE : View.GONE);
	}

	@Override
	public void Failed(int index) {
		
	}

	public void ShareSina(final String content) {
		new AsyncTask<Object, Object, Object>() {
			// private Boolean isShareSina,isShareQQ;

			@Override
			protected Object doInBackground(Object... params) {
				// TODO Auto-generated method stub
				ShareManager sm = new ShareManager(ActionDetailActivity.this);
				sm.shareToSina(content, uploadImgUrlSd, 0.0, 0.0);
				return null;
			}

			@Override
			protected void onPostExecute(Object result) {
				super.onPostExecute(result);
				Toast.makeText(ActionDetailActivity.this, "success", Toast.LENGTH_SHORT).show();
			}

			protected void onPreExecute() {
			};
		}.execute();

	}

	public void ShareQQ(final String content) {
		new AsyncTask<Object, Object, Object>() {
			// private Boolean isShareSina,isShareQQ;

			@Override
			protected Object doInBackground(Object... params) {
				// TODO Auto-generated method stub
				ShareManager sm = new ShareManager(ActionDetailActivity.this);
				sm.shareToQQ(content, uploadImgUrlSd, 0.0,0.0);
				return null;
			}

			@Override
			protected void onPostExecute(Object result) {
				super.onPostExecute(result);
				Toast.makeText(ActionDetailActivity.this, "success", Toast.LENGTH_SHORT).show();
			}

			protected void onPreExecute() {
			};
		}.execute();

	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	public void ShareWeixin(final String content){
		
		File file = new File(uploadImgUrlSd);
		if (!file.exists()) {
			Toast.makeText(this, "文件不存在", Toast.LENGTH_LONG).show();
			return;
		}
		
		WXImageObject imgObj = new WXImageObject();
		imgObj.setImagePath(uploadImgUrlSd);
		
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = imgObj;
		
		Bitmap bmp = BitmapFactory.decodeFile(uploadImgUrlSd);
		Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 100, 100, true);
		bmp.recycle();
		msg.thumbData = MassVigUtil.bmpToByteArray(thumbBmp, true);
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("img");
		req.message = msg;
		wxapi.sendReq(req);
		
		
//		WXImageObject obj = new WXImageObject();
//		obj.imagePath = uploadImgUrlSd;
//		WXMediaMessage msg = new WXMediaMessage();
//		msg.mediaObject = obj;
//		msg.description = content;
//		SendMessageToWX.Req req = new SendMessageToWX.Req();
//		req.transaction = String.valueOf(System.currentTimeMillis());
//		req.message = msg;
//		wxapi.sendReq(req);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == QQ) {
			if (data != null) {
				ShareQQ("test");
				oAuth = (OAuthV2) data.getExtras().getSerializable("oauth");
				MassVigUtil.setPreferenceStringData(this,
						OAuthConstant.QQ_ACCESS_TOKEN, oAuth.getAccessToken());
				MassVigUtil.setPreferenceStringData(this,
						OAuthConstant.QQ_CLIENT_IP, oAuth.getClientIP());
				MassVigUtil.setPreferenceStringData(this,
						OAuthConstant.QQ_OPEN_ID, oAuth.getOpenid());
				// 调用API获取用户信息
				UserAPI userAPI = new UserAPI(OAuthConstants.OAUTH_VERSION_2_A);
				try {
					// String response = userAPI.info(oAuth, "json");// 获取用户信息
					// JSONObject res = new JSONObject(response);
					// JSONObject dat = res.getJSONObject("data");
					// String nick = dat.getString("nick");
					MassVigUtil.setPreferenceBooleanData(this,
							OAuthConstant.QQ, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
				userAPI.shutdownConnection();
			}

		}else if(requestCode == SINA){
			ShareSina("test");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rule:
			rule_layout.setVisibility(rule_layout.isShown() ? View.GONE : View.VISIBLE);
			break;
		case R.id.actin:
			if(!TextUtils.isEmpty(app.user.sessionID)){
				startActivity(new Intent(this, InsertPost.class).putExtra("FLAG", InsertPost.ACTION).putExtra("CAMPAIGNID", manager.detail.CampaignID));
			}else{
				app.user.sessionID = "";
				MassVigUtil.setPreferenceStringData(this, "SESSIONID", "");
				startActivity(new Intent(this, LoginActivity.class));
			}
			break;
		case R.id.sina:
			if(!TextUtils.isEmpty(MassVigUtil.getPreferenceData(ActionDetailActivity.this, OAuthConstant.SINA_ACCESS_TOKEN, ""))){
				ShareSina("test");
			}else{
				startActivityForResult(new Intent(ActionDetailActivity.this,ShareWebSinaActivity.class), SINA);
			}
			break;
		case R.id.qq:
			if(!TextUtils.isEmpty(MassVigUtil.getPreferenceData(ActionDetailActivity.this, OAuthConstant.QQ_ACCESS_TOKEN, ""))){
				ShareQQ("test");
			}else{
				Intent intent = new Intent(ActionDetailActivity.this,OAuthV2AuthorizeWebView.class);
				intent.putExtra("oauth", oAuth);
				startActivityForResult(intent, QQ);
			}
			break;
		case R.id.weixin:
			ShareWeixin("test");
			break;
		case R.id.back:
			finish();
			break;

		default:
			break;
		}
	}

}
