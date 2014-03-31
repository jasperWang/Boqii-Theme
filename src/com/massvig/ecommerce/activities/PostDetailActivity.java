package com.massvig.ecommerce.activities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.Post;
import com.massvig.ecommerce.entities.PostDetail;
import com.massvig.ecommerce.managers.PostDetailManager;
import com.massvig.ecommerce.managers.PostDetailManager.LoadListener;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.utilities.OAuthConstant;
import com.massvig.ecommerce.utilities.ShareManager;
import com.massvig.ecommerce.widgets.CommentListAdapter;
import com.massvig.ecommerce.widgets.NetImageView;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXFileObject;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.weibo.api.UserAPI;
import com.tencent.weibo.constants.OAuthConstants;
import com.tencent.weibo.oauthv2.OAuthV2;
import com.tencent.weibo.webview.OAuthV2AuthorizeWebView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class PostDetailActivity extends BaseActivity implements OnClickListener, LoadListener{

	private LinearLayout dialogLayout;
	private ListView listview;
	private PostDetailManager manager;
	private CommentListAdapter mAdapter;
	private EditText commentText;
	private ProgressDialog dialog;
	private BaseApplication app;

	private NetImageView headImg, postImg;
	private ImageView img_praise;
	private TextView name,text1,text2,time,description;
	private boolean isRefresh = false;
	private Post p;
	private LinearLayout hideLayout;
	private static final int QQ = 1;
	private OAuthV2 oAuth;
	private ShareManager sm;
	private IWXAPI api;
	private LinearLayout frame_layout;
	private static final int THUMB_SIZE = 150;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_detail);
		regToWx();
		setTitle(getString(R.string.postdetail));
		sm = new ShareManager(this);
		oAuth = new OAuthV2(OAuthConstant.callback);
		oAuth.setClientId(OAuthConstant.APP_KEY_QQ);
		oAuth.setClientSecret(OAuthConstant.APP_SECRET_QQ);
		app = (BaseApplication) getApplication();
		manager = new PostDetailManager();
		manager.setListener(this);
		p = (Post)this.getIntent().getSerializableExtra("POST");
		initPostDetail(p);
		dialog = new ProgressDialog(this);
		dialog.setMessage(getString(R.string.loading_data));
		initView();
		isRefresh = true;
		manager.FetchComment();
	}
	
	private void regToWx(){
		api = WXAPIFactory.createWXAPI(this, OAuthConstant.WX_APP_ID, true);
		api.registerApp(OAuthConstant.WX_APP_ID);
	}
	
	private void shareToWeixin(String text, boolean pengyou){

		String path = "";
		int width = this.getWindowManager().getDefaultDisplay().getWidth();
		String pa = MassVigUtil.GetImageUrl(p.imageUrl, width, width);
		if(pa != null){
			int index = pa.lastIndexOf("/");
			if(index != -1){
				path = MassVigContants.PATH + pa.substring(index, pa.length());
			}
		}

		WXFileObject fileObj = new WXFileObject(path);
		
		WXMediaMessage msg = new WXMediaMessage();
		msg.description = text;
		msg.mediaObject = fileObj;
		if (!TextUtils.isEmpty(path)) {
			Bitmap bmp = BitmapFactory.decodeFile(path);
			if (bmp != null) {
				Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE,
						THUMB_SIZE, true);
				bmp.recycle();
				msg.setThumbImage(thumbBmp);
			}
//			msg.thumbData = MassVigUtil.bmpToByteArray(thumbBmp, true);
		}
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("image");
		req.message = msg;
		if(pengyou){
			req.scene = SendMessageToWX.Req.WXSceneTimeline;
		}
		api.sendReq(req);
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
//	private void shareToWeixin(String text, boolean pengyou){
//		Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
//		WXTextObject textObj = new WXTextObject();
//		textObj.text = text;
//		WXMediaMessage msg = new WXMediaMessage();
//		msg.mediaObject = textObj;
//		msg.description = text;
//		SendMessageToWX.Req req = new SendMessageToWX.Req();
//		req.transaction = buildTransaction("text");
////		req.transaction = String.valueOf(System.currentTimeMillis());
//		req.message = msg;
//		if(pengyou){
//			req.scene = SendMessageToWX.Req.WXSceneTimeline;
//		}
//		api.sendReq(req);
//	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == QQ) {
			if (data != null) {
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
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		app = (BaseApplication) getApplication();
		app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(PostDetailActivity.this, "SESSIONID", "") : app.user.sessionID;
	}

	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				String addText = getString(R.string.atone, String.valueOf(msg.obj));
				commentText.setText(addText);
				manager.atCustomerID = msg.arg1 + "";
				openInput(addText);
				break;
			case 2:
				startActivity(new Intent(PostDetailActivity.this, CommunityUserInfoActivity.class).putExtra("USERID", msg.arg1));
				break;
			default:
				break;
			}
		}
		
	};
	
	private void openInput(String text){
		commentText.setFocusable(true);
		commentText.requestFocus();
		commentText.setSelection(text.length());
		InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.showSoftInput(commentText,InputMethodManager.SHOW_FORCED);
	}

	private void initView() {
		LayoutInflater inflater = LayoutInflater.from(this);
		hideLayout = (LinearLayout)findViewById(R.id.hidelayout);
		img_praise = (ImageView)findViewById(R.id.img_praise);
		View headView = (View)inflater.inflate(R.layout.post_headview, null);
		listview = (ListView)findViewById(R.id.listview);
		frame_layout = (LinearLayout)findViewById(R.id.frame_layout);
		((Button)findViewById(R.id.weixin)).setOnClickListener(this);
		((Button)findViewById(R.id.pengyou)).setOnClickListener(this);
		((Button)findViewById(R.id.sina)).setOnClickListener(this);
		((Button)findViewById(R.id.tencent)).setOnClickListener(this);
		((Button)findViewById(R.id.yes)).setOnClickListener(this);
		((Button)findViewById(R.id.no)).setOnClickListener(this);
		((Button)findViewById(R.id.back)).setOnClickListener(this);
		((Button)findViewById(R.id.edit)).setOnClickListener(this);
		((LinearLayout)findViewById(R.id.share)).setOnClickListener(this);
		((LinearLayout)findViewById(R.id.praise)).setOnClickListener(this);
		((LinearLayout)findViewById(R.id.comment)).setOnClickListener(this);
		((LinearLayout)findViewById(R.id.share_new)).setOnClickListener(this);
		((Button)findViewById(R.id.hide)).setOnClickListener(this);
		((Button)findViewById(R.id.fram_button)).setOnClickListener(this);
		Drawable draw_share = getResources().getDrawable(R.drawable.ic_share_01);  
		draw_share.setBounds(0, 0, 35, 35); 
		Drawable draw_praise = getResources().getDrawable(R.drawable.ic_praise_01);  
		draw_praise.setBounds(0, 0, 35, 35);
		if(!manager.postDetail.CanShare)
//			((TextView)findViewById(R.id.text_share)).setBackgroundResource(R.drawable.ic_share_detail_02);
//			((TextView)findViewById(R.id.text_share)).setCompoundDrawables(draw_share, null, null, null);
		if(!manager.postDetail.CanPraise)
			img_praise.setBackgroundResource(R.drawable.ic_praise_02);
//			((TextView)findViewById(R.id.text_praise)).setCompoundDrawables(draw_praise, null, null, null);
		commentText = (EditText)headView.findViewById(R.id.commentedit);
		boolean comment = this.getIntent().getBooleanExtra("COMMENT", false);
		if(comment){
			commentText.requestFocus();
	        Timer timer=new Timer();  
	        timer.schedule(new TimerTask() {  
	            @Override  
	            public void run() {
	                InputMethodManager inputMethodManager=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
	                inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  
	            }  
	        }, 500);  
		}
		headImg = (NetImageView)headView.findViewById(R.id.headimg);
		headImg.setOnClickListener(this);
		postImg = (NetImageView)headView.findViewById(R.id.imageview);
		postImg.setOnClickListener(this);
		name = (TextView)headView.findViewById(R.id.name);
		text1 = (TextView)headView.findViewById(R.id.text1);
		text2 = (TextView)headView.findViewById(R.id.text2);
		time = (TextView)headView.findViewById(R.id.time);
		description = (TextView)headView.findViewById(R.id.detail);
		((TextView)headView.findViewById(R.id.commentbtn)).setOnClickListener(this);
		dialogLayout = (LinearLayout)findViewById(R.id.layout);
		listview.addHeaderView(headView);
		mAdapter = new CommentListAdapter(this, manager.getComments());
		mAdapter.setHandler(mHandler);
		listview.setAdapter(mAdapter);
		listview.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem + visibleItemCount == totalItemCount
						&& totalItemCount > 0) {
					if(!isRefresh){
						isRefresh = true;
						manager.FetchComment();
					}
				}
			}
		});
		
		initView(manager.postDetail);
	}

	private void initView(PostDetail p) {
		headImg.setImageUrl(p.userimgurl, MassVigContants.PATH, null);
		int width = this.getWindowManager().getDefaultDisplay().getWidth();
		postImg.setImageUrl(MassVigUtil.GetImageUrl(p.imageUrl, width, width), MassVigContants.PATH, null);
		name.setText(p.username);
		text1.setText(p.ShareSourceMsg1);
		text2.setText(p.ShareSourceMsg2);
		time.setText(GetTimeStamp(p.createTime));
		description.setText(p.detail);
		if(app.user.customerID != p.customerID){
			((Button)findViewById(R.id.edit)).setVisibility(View.INVISIBLE);
		}
	}

	private void initPostDetail(Post p) {
		manager.postDetail.address = p.address;
		manager.postDetail.CanPraise = p.CanPraise;
		manager.postDetail.CanShare = p.CanShare;
		manager.postDetail.createTime = p.createTime;
		manager.postDetail.customerID = p.customerID;
		manager.postDetail.detail = p.detail;
		manager.postDetail.imageUrl = p.imageUrl;
		manager.postDetail.postID = p.postID;
		manager.postDetail.praise = p.praise;
		manager.postDetail.RefID = p.RefID;
		manager.postDetail.shared = p.shared;
		manager.postDetail.ShareSourceMsg1 = p.ShareSourceMsg1;
		manager.postDetail.ShareSourceMsg2 = p.ShareSourceMsg2;
		manager.postDetail.ShareSourceType = p.ShareSourceType;
		manager.postDetail.userimgurl = p.userimgurl;
		manager.postDetail.username = p.username;
	}
	
	private String GetTimeStamp(String oldTime){
		String result = "";
		if (!TextUtils.isEmpty(oldTime) && !oldTime.equals("null")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				Date now = new Date();
				Date date = (Date) sdf.parse(oldTime);
				int distance = (int) (now.getTime() - date.getTime()) / 1000;
				if (distance < 0)
					distance = 0;
				if (distance < 60) {
					result = getString(R.string.campaign_second_ago, distance);
				} else if (distance < 60 * 60) {
					distance = distance / 60;
					result = getString(R.string.campaign_minute_ago, distance);
				} else if (distance < 60 * 60 * 24) {
					distance = distance / 60 / 60;
					result = getString(R.string.campaign_hour_ago, distance);
				} else if (distance < 60 * 60 * 24 * 7) {
					distance = distance / 60 / 60 / 24;
					result = getString(R.string.campaign_day_ago, distance);
				} else if (distance < 60 * 60 * 24 * 7 * 4) {
					distance = distance / 60 / 60 / 24 / 7;
					result = getString(R.string.campaign_week_ago, distance);
				} else if (distance < 60 * 60 * 24 * 7 * 4 * 12) {
					distance = distance / 60 / 60 / 24 / 7 / 4;
					result = getString(R.string.campaign_month_ago, distance);
				} else {
					distance = distance / 60 / 60 / 24 / 7 / 4 / 12;
					result = getString(R.string.campaign_year_ago, distance);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public void onClick(View v) {
		String content = "";
		if(!TextUtils.isEmpty(p.detail))
			content = p.detail;
		switch (v.getId()) {
		case R.id.weixin:
			shareToWeixin(content, false);
			break;
		case R.id.pengyou:
			if(api.getWXAppSupportAPI() >= 0x21020001){
				shareToWeixin(content, true);
			}else{
				Toast.makeText(this, getString(R.string.notsupport), Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.sina:
			if(!TextUtils.isEmpty(MassVigUtil.getPreferenceData(this, OAuthConstant.SINA_ACCESS_TOKEN, ""))){
				sm.shareToSina(content, manager.postDetail.imageUrl, 0, 0);
			}else{
				startActivity(new Intent(this,ShareWebSinaActivity.class));
			}
			break;
		case R.id.tencent:
			if(!TextUtils.isEmpty(MassVigUtil.getPreferenceData(this, OAuthConstant.QQ_ACCESS_TOKEN, ""))){
				sm.shareToQQ(content, manager.postDetail.imageUrl, 0, 0);
			}else{
				Intent intent = new Intent(this,OAuthV2AuthorizeWebView.class);
				intent.putExtra("oauth", oAuth);
				startActivityForResult(intent, QQ);
			}
			break;
		case R.id.fram_button:
			hideLayout.setVisibility(View.GONE);
			frame_layout.setVisibility(View.GONE);
			((TextView)findViewById(R.id.text_share)).setBackgroundResource(R.drawable.ic_share_new_01);
			break;
		case R.id.hide:
			((TextView)findViewById(R.id.text_share)).setBackgroundResource(R.drawable.ic_share_new_01);
			hideLayout.setVisibility(View.GONE);
			frame_layout.setVisibility(View.GONE);
			break;
		case R.id.share_new:
			((TextView)findViewById(R.id.text_share)).setBackgroundResource(R.drawable.ic_share_new_02);
			hideLayout.setVisibility(View.VISIBLE);
			frame_layout.setVisibility(View.VISIBLE);
			break;
		case R.id.imageview:
			startActivity(new Intent(PostDetailActivity.this, FullScreenActivity.class).putExtra("IMAGE", p.imageUrl));
			break;
		case R.id.headimg:
			startActivity(new Intent(PostDetailActivity.this, CommunityUserInfoActivity.class).putExtra("USERID", p.customerID));
			break;
		case R.id.yes:
			if(dialog != null && !dialog.isShowing()){
				dialog.setMessage(getString(R.string.delete_post));
				dialog.show();
			}
			manager.DeletePost(app.user.sessionID);
			break;
		case R.id.no:
			dialogLayout.setVisibility(View.GONE);
			break;
		case R.id.back:
			finish();
			break;
		case R.id.edit:
			if(dialogLayout.isShown())
				dialogLayout.setVisibility(View.GONE);
			else
				dialogLayout.setVisibility(View.VISIBLE);
			break;
		case R.id.share:
			if(TextUtils.isEmpty(app.user.sessionID))
				startActivity(new Intent(this, LoginActivity.class));
			if(manager.postDetail.CanShare)
				startActivity(new Intent(PostDetailActivity.this, InsertPost.class).putExtra("image", manager.postDetail.imageUrl).putExtra("FLAG", InsertPost.POST).putExtra("shareID", manager.postDetail.postID));
			break;
		case R.id.praise:
			if(manager.postDetail.CanPraise)
				manager.Praise(app.user.sessionID);
			else
				Toast.makeText(this, getString(R.string.already), Toast.LENGTH_SHORT).show();
			break;
		case R.id.comment:
			listview.setSelection(0);
			openInput("");
			break;
		case R.id.commentbtn:
			if(TextUtils.isEmpty(app.user.sessionID)){
				app.user.sessionID = "";
				MassVigUtil.setPreferenceStringData(this, "SESSIONID", "");
				Toast.makeText(PostDetailActivity.this, getString(R.string.login_first), Toast.LENGTH_SHORT).show();
				startActivity(new Intent(PostDetailActivity.this, LoginActivity.class));
				return;
			}
			if(TextUtils.isEmpty(commentText.getText().toString()))
				return;
			dialog.setMessage(getString(R.string.commenting));
			dialog.show();
			manager.AddComment(app.user.sessionID, commentText.getText().toString());
			collapseSoftInputMethod();
			break;
		default:
			break;
		}
	}
	
	public void collapseSoftInputMethod(){
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(commentText.getWindowToken(), 0);
	}
	
	@Override
	public void AddCommentFailed() {
		if(dialog != null && dialog.isShowing())
			dialog.dismiss();
		Toast.makeText(this, getString(R.string.comment_failed), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void AddCommentSuccess() {
		if(dialog != null && dialog.isShowing())
			dialog.dismiss();
		Toast.makeText(this, getString(R.string.comment_success), Toast.LENGTH_SHORT).show();
		manager.isLoadDone = false;
		manager.getComments().clearCommentList();
		manager.FetchComment();
	}

	@Override
	public void LoadCommentSuccess() {
		isRefresh = false;
		commentText.setText("");
		manager.refreshCommentsList();
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void LoadCommentFailed() {
		isRefresh = false;
	}

	@Override
	public void StartLoading() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void StopLoading() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void PraiseSuccess(int tag) {
		img_praise.setImageResource(R.drawable.ic_praise_02);
	}

	@Override
	public void PraiseFailed(int tag) {
		Toast.makeText(this, getString(R.string.already), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void SessionVailed() {
		if(dialog != null && dialog.isShowing())
			dialog.dismiss();
//		if(!TextUtils.isEmpty(app.user.sessionID + MassVigUtil.getPreferenceData(this, "SESSIONID", "")))
			startActivity(new Intent(this, LoginActivity.class));
		app.user.sessionID = "";
		MassVigUtil.setPreferenceStringData(this, "SESSIONID", "");
	}

	@Override
	public void Already() {
		img_praise.setImageResource(R.drawable.ic_praise_02);
		Toast.makeText(this, getString(R.string.already), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void DeletePost(int result) {
		if(dialog != null && dialog.isShowing())
			dialog.dismiss();
		if(result == 0){
			finish();
		}else{
			Toast.makeText(this, getString(R.string.delete_fail), Toast.LENGTH_SHORT).show();
		}
	}

}
