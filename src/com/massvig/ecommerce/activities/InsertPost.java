package com.massvig.ecommerce.activities;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.location.LocationManage;
import com.massvig.ecommerce.location.LocationManage.MyLocationListener;
import com.massvig.ecommerce.managers.InsertManager;
import com.massvig.ecommerce.managers.InsertManager.Listener;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.utilities.OAuthConstant;
import com.massvig.ecommerce.utilities.ShareManager;
import com.massvig.ecommerce.widgets.NetImageView;
import com.tencent.weibo.api.UserAPI;
import com.tencent.weibo.constants.OAuthConstants;
import com.tencent.weibo.oauthv2.OAuthV2;
import com.tencent.weibo.webview.OAuthV2AuthorizeWebView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class InsertPost extends BaseActivity implements Listener, OnClickListener {
	private final int sinaOauthRequestCode = 10001;
	private final int qqOauthRequestCode = 10002;
	private final int loginRequestCode = 10003;
	private EditText editContent;
	private TextView mtxtCount;
	private Button mCamera;
	private CheckBox mShareQQCheckBox, mShareSianCheckBox,
			mShareLocationCheckBox;
	private NetImageView uploadImg;
	private MyOnclick mMyOnclick;
	private SelectPic mSelectPic;
	private Button addPostBtn;
	private Bitmap muploadImgBitmap;
	private BaseApplication app;
	private LocationManage mLocationManage;
	private InsertManager manager;
	private String imageUrl = "";
	private String uploadImgUrl = "";
	private String uploadImgUrlSd = "";
//	private static final int width = 720;
	private OAuthV2 oAuth;
	private static final int QQ = 101;
	private Boolean isShareSina, isShareQQ;

	private static final int SHARE = 1;
	private static final int FAILED = 2;
	private static final int UPLOAD = 3;
	private static final int SUBMIT = 4;
	public static final int PRODUCT = 111;
	public static final int POST = 222;
	public static final int ACTION = 333;
	public static final int ORIGIN = 444;
	private ProgressDialog progress;
	private int index = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.insert_post);
		setTitle(getString(R.string.insertpost));
		oAuth = new OAuthV2(OAuthConstant.callback);
		oAuth.setClientId(OAuthConstant.APP_KEY_QQ);
		oAuth.setClientSecret(OAuthConstant.APP_SECRET_QQ);
		app = (BaseApplication) getApplication();
		manager = new InsertManager();
		manager.setListener(this);
		imageUrl = this.getIntent().getStringExtra("image");
		manager.shareID = this.getIntent().getIntExtra("shareID", -1);
		manager.campaignID = this.getIntent().getIntExtra("CAMPAIGNID", -1);
		manager.productID = this.getIntent().getIntExtra("productID", 0);
		manager.flag = this.getIntent().getIntExtra("FLAG", PRODUCT);
		app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(InsertPost.this, "SESSIONID", "") : app.user.sessionID;
		if (TextUtils.isEmpty(app.user.sessionID)) {
			app.user.sessionID = "";
			MassVigUtil.setPreferenceStringData(this, "SESSIONID", "");
			startActivity(new Intent(this, LoginActivity.class));
		}
		initView();
		initLocation();
		MassVigUtil.setPreferenceBooleanData(InsertPost.this, OAuthConstant.QQ,
				false);
		MassVigUtil.setPreferenceBooleanData(InsertPost.this,
				OAuthConstant.SINA, false);
		manager.GetUploadImageUrl();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mShareQQCheckBox.setChecked(MassVigUtil.getPreferenceData(this,
				OAuthConstant.QQ, false));
		mShareSianCheckBox.setChecked(MassVigUtil.getPreferenceData(this,
				OAuthConstant.SINA, false));
		isShareSina = mShareSianCheckBox.isChecked();
		isShareQQ = mShareQQCheckBox.isChecked();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(muploadImgBitmap!= null && !muploadImgBitmap.isRecycled()){
			muploadImgBitmap.recycle();
			System.gc();
		}
	}

	// 初始化定位
	private void initLocation() {
		mLocationManage = LocationManage.getInstance();
		mLocationManage.setAllow(this, true);
		manager.lat = mLocationManage.getLatitude();
		manager.lon = mLocationManage.getLongitude();
		if (manager.lat != 0.0) {
			mLocationManage.getAddress(mmMyLocationListener, true);
		} else {
			Boolean isSucceed = mLocationManage.startLocation(this, false,
					mmMyLocationListener);
			ShowMessage(isSucceed);
		}

	}

	private void ShowMessage(boolean isSuccess) {
		if (isSuccess) {
		} else {
		}
	}

	MyLocationListener mmMyLocationListener = new MyLocationListener() {

		@Override
		public void location(double latitude, double longitude) {
			manager.lat = latitude;
			manager.lon = longitude;
			if (manager.lat != 0.0 && manager.lat != 0.0) {
				mLocationManage.getAddress(mmMyLocationListener, true);
				mLocationManage.stopLocation();
			}

		}

		@Override
		public void isAllow(Boolean isAllow) {
			if (isAllow) {
				mLocationManage.startLocation(InsertPost.this, true, this);
			} else {
			}
			ShowMessage(isAllow);
		}

		@Override
		public void address(String address) {
			if (address != null) {
				manager.address = address;
			}
		}
	};

	private void initView() {

		progress = new ProgressDialog(InsertPost.this);
		progress.setMessage(getString(R.string.upload_img));
		editContent = (EditText) findViewById(R.id.edit_content);
		mtxtCount = (TextView) findViewById(R.id.text_count);
		mCamera = (Button) findViewById(R.id.add_post_btn1);
		mShareSianCheckBox = (CheckBox) findViewById(R.id.add_post_share_sina);
		mShareQQCheckBox = (CheckBox) findViewById(R.id.add_post_share_qq);
		mShareLocationCheckBox = (CheckBox) findViewById(R.id.add_post_share_location);
		uploadImg = (NetImageView) findViewById(R.id.add_post_img);
		addPostBtn = (Button) findViewById(R.id.finish);
		if (!TextUtils.isEmpty(imageUrl)) {
			uploadImg.setImageUrl(imageUrl, MassVigContants.PATH, null);
			uploadImgUrl = imageUrl;
			manager.imgUrl = imageUrl;
			if (uploadImgUrl.lastIndexOf("/") != -1) {
				uploadImgUrlSd = MassVigContants.PATH
						+ uploadImgUrl.substring(uploadImgUrl.lastIndexOf("/"),
								uploadImgUrl.length());
			}
		}
		((Button) findViewById(R.id.back)).setOnClickListener(this);
		initAction();
	}

	private void initAction() {
		mMyOnclick = new MyOnclick();
		mSelectPic = new SelectPic(InsertPost.this);
		editContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				mtxtCount.setText(s.length() + "/140");

			}
		});
		uploadImg.setOnClickListener(mMyOnclick);
		mCamera.setOnClickListener(mMyOnclick);
		mShareLocationCheckBox.setOnClickListener(mMyOnclick);
		mShareQQCheckBox.setOnClickListener(mMyOnclick);
		mShareSianCheckBox.setOnClickListener(mMyOnclick);
		addPostBtn.setOnClickListener(mMyOnclick);
	}

	public void subMitForm() {

		String content = editContent.getText().toString()
				.replaceAll("/(^\\\\s*)|(\\\\s*$)/g", "");
		manager.comment = content;
		app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(InsertPost.this, "SESSIONID", "") : app.user.sessionID;
//		manager.Share(app.user.sessionID);
		String shareWeiboContent = "";
		if (!content.equals("")) {
			shareWeiboContent = content;
		}
		shareWeibo(shareWeiboContent.replaceAll("null", ""));
	}

	public void shareWeibo(final String content) {
		new AsyncTask<Object, Object, Object>() {
			// private Boolean isShareSina,isShareQQ;

			@Override
			protected Object doInBackground(Object... params) {
				// TODO Auto-generated method stub
				File mFile = new File(MassVigContants.TEMPPATH
						+ "/weibo_share_pic.jpg");
				File pFile = mFile.getParentFile();
				if (pFile != null && !pFile.exists()) {
					pFile.mkdirs();
				}

				Options opts = new Options();
				opts.inPreferredConfig = Bitmap.Config.ARGB_4444;
				opts.inSampleSize = 2;
				Bitmap myBitmap = BitmapFactory.decodeFile(uploadImgUrlSd, opts);
//				Bitmap myBitmap = BitmapFactory.decodeFile(uploadImgUrlSd);
				if (myBitmap != null) {
					Bitmap thumbBmp = Bitmap.createScaledBitmap(myBitmap, 490,
							490, true);
					myBitmap.recycle();
					System.gc();

					try {
						BufferedOutputStream bos = new BufferedOutputStream(
								new FileOutputStream(mFile));
						thumbBmp.compress(Bitmap.CompressFormat.JPEG, 70, bos);
						bos.flush();
						bos.close();
						uploadImgUrlSd = MassVigContants.TEMPPATH
								+ "/weibo_share_pic.jpg";
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				ShareManager sm = new ShareManager(InsertPost.this);
				if (isShareQQ) {
					sm.shareToQQ(content, uploadImgUrlSd, manager.lon,
							manager.lat);
				}
				if (isShareSina) {
					sm.shareToSina(content, uploadImgUrlSd, manager.lon,
							manager.lat);
				}
				// sm.shareToSina(content,
				// (GroupSaleService.getInstance().getUrl() +
				// "/pictures/"+uploadImgUrl).replace(".jpg","_medium.jpg"));
				return null;
			}

			protected void onPreExecute() {
				// isShareSina = mShareSianCheckBox.isChecked();
				// isShareQQ = mShareQQCheckBox.isClickable();
			};
		}.execute();

	}

	class MyOnclick implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v == mCamera || v == uploadImg) {
				mSelectPic.showDialog();
			} else if (v == mShareLocationCheckBox) {
				if (((CheckBox) v).isChecked()) {
					LocationManage.getInstance().startLocation(InsertPost.this,
							true, new MMyLocationListener());
				}
			} else if (v == mShareQQCheckBox) {

				MassVigUtil.setPreferenceBooleanData(InsertPost.this,
						OAuthConstant.QQ, false);
				if (((CheckBox) v).isChecked()) {
					Intent intent = new Intent(InsertPost.this,
							OAuthV2AuthorizeWebView.class);
					intent.putExtra("oauth", oAuth);
					startActivityForResult(intent, QQ);
				}
			} else if (v == mShareSianCheckBox) {
				MassVigUtil.setPreferenceBooleanData(InsertPost.this,
						OAuthConstant.SINA, false);
				if (((CheckBox) v).isChecked()) {
					startActivity(new Intent(InsertPost.this,
							ShareWebSinaActivity.class));
				}
			} else if (v == addPostBtn) {
				if (uploadImgUrl != null && !uploadImgUrl.equals("")
						|| muploadImgBitmap != null) {
					if(index == 0)
					new ShareAsync().execute();
				} else {
					Toast.makeText(InsertPost.this,
							getString(R.string.please_upload_pic),
							Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	private Uri imageUri;
	class SelectPic extends AlertDialog.Builder {
		protected SelectPic(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		public void selectFromSd() {
			try {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				startActivityForResult(intent, 0);
			} catch (Exception e) {

			}
		}

		public void selectFormCamera() {
			String status = Environment.getExternalStorageState();
			if (status.equals(Environment.MEDIA_MOUNTED)) {

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
						MassVigContants.TEMPPATH + "/",
						"camera_insert_post.png")));
				startActivityForResult(intent, 1);

			} else {
				Toast.makeText(InsertPost.this, getString(R.string.no_sdcard),
						Toast.LENGTH_SHORT).show();
			}
		}

		public void showDialog() {
			String[] arg0 = { getString(R.string.camera),
					getString(R.string.phone_gallery) };
			setTitle(R.string.insert_pic);
			setItems(arg0, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						selectFormCamera();
						break;
					case 1:
						selectFromSd();
						break;
					default:
						break;
					}
				}
			});
			show();
		}

		public void completeFromCamera(Intent intent) {
			Bitmap myBitmap = null;
			String status = Environment.getExternalStorageState();
			File file = new File(MassVigContants.TEMPPATH + "/"
					+ "camera_insert_post.png");
			if (status.equals(Environment.MEDIA_MOUNTED) && file.exists()) {
//				Options opts = new Options();
//				opts.inPreferredConfig = Bitmap.Config.ARGB_4444;
//				opts.inSampleSize = 2;
				Uri u = Uri.fromFile(new File(MassVigContants.TEMPPATH + "/", "camera_insert_post.png"));
				imageUri = u;
				startActivityForResult(new Intent(InsertPost.this, CropActivity.class).putExtra("URI", MassVigContants.TEMPPATH
						+ "/" + "camera_insert_post.png"), 2);
//				myBitmap = BitmapFactory.decodeFile(MassVigContants.TEMPPATH
//						+ "/" + "camera_insert_post.png");
			} else if (intent != null) {
				Bundle extras = intent.getExtras();
				myBitmap = (Bitmap) extras.get("data");
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			}
//			if (myBitmap != null) {
//				Bitmap thumbBmp = Bitmap.createScaledBitmap(myBitmap, width,
//						myBitmap.getHeight() * width / myBitmap.getWidth(), true);
//				myBitmap.recycle();
//				uploadImg.setImageBitmap(thumbBmp);
				uploadImgUrl = "";
//				muploadImgBitmap = thumbBmp;
				uploadImgUrlSd = MassVigContants.TEMPPATH + "/"
						+ "camera_insert_post.png";
				manager.imgUrl = "";
//			}

		}

		public void completeFromSd(Intent intent) {
			try {
				if (intent != null) {
					Uri mUri = intent.getData();
					String filePath = getFilePath(mUri);
//					Options opts = new Options();
//					opts.inPreferredConfig = Bitmap.Config.ARGB_4444;
//					opts.inSampleSize = 2;
//					Bitmap myBitmap = BitmapFactory.decodeFile(filePath, opts);
					imageUri = mUri;
					startActivityForResult(new Intent(InsertPost.this, CropActivity.class).putExtra("URI", filePath), 2);
//					Bitmap myBitmap = BitmapFactory.decodeFile(filePath);
//					Bitmap thumbBmp = Bitmap.createScaledBitmap(myBitmap,
//							width, myBitmap.getHeight() * width / myBitmap.getWidth(), true);
//					myBitmap.recycle();
//					uploadImg.setImageBitmap(thumbBmp);
					uploadImgUrl = "";
//					muploadImgBitmap = thumbBmp;
					uploadImgUrlSd = filePath;
					manager.imgUrl = "";
				}

			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}
		}

		public String getFilePath(Uri uri) {
			Cursor actualimagecursor = managedQuery(uri,
					new String[] { MediaStore.Images.Media.DATA }, null, null,
					null);
			int actual_image_column_index = actualimagecursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			actualimagecursor.moveToFirst();
			String img_path = actualimagecursor
					.getString(actual_image_column_index);
			return img_path;
		}
	}

	class MMyLocationListener implements MyLocationListener {

		@Override
		public void isAllow(Boolean isAllow) {
			// TODO Auto-generated method stub
			if (isAllow) {
				LocationManage.getInstance().startLocation(InsertPost.this,
						true, this);
			}
		}

		@Override
		public void location(double latitude, double longitude) {
			// TODO Auto-generated method stub
			manager.lat = latitude;
			manager.lon = longitude;
			if (InsertPost.this.manager.lat != 0.0 && manager.lon != 0.0) {
				LocationManage.getInstance().getAddress(this, true);
			}
		}

		@Override
		public void address(String address) {
			// TODO Auto-generated method stub
			manager.address = address;
		}
	}

	private class ShareAsync extends AsyncTask<Object, Void, String> {
		@Override
		protected String doInBackground(Object... params) {
			String result = "";
			index++;
			if (!TextUtils.isEmpty(manager.imgUrl)) {
				manager.Share(app.user.sessionID);
				subMitForm();
			} else {
				manager.UploadImage(muploadImgBitmap);
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
//			if (!TextUtils.isEmpty(result)) {
//				try {
//					JSONObject data = new JSONObject(result);
//					String url = data.getString("filePath");
//					String error = data.getString("error");
//					if (TextUtils.isEmpty(error)) {
//						uploadImgUrl = url.replaceAll("\\\\", "/");
//						subMitForm();
//					} else {
//					}
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
//			if(progress != null && !progress.isShowing()){
//				progress.dismiss();
//				progress.show();
//			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {
				mSelectPic.completeFromCamera(data);
			} else if (requestCode == 0) {
				mSelectPic.completeFromSd(data);
			} else if (requestCode == 2) {
				if(imageUri != null){
					Bitmap bitmap = decodeUriAsBitmap(imageUri);
					muploadImgBitmap = bitmap;
					uploadImg.setImageBitmap(bitmap);
				}
			} else if (requestCode == sinaOauthRequestCode) {
				mShareSianCheckBox.setChecked(true);
			} else if (requestCode == qqOauthRequestCode) {
				mShareQQCheckBox.setChecked(true);
			}
		}
		if (requestCode == loginRequestCode) {
			if (resultCode == RESULT_OK) {
			} else {
				finish();
			}
		}

	}
	
	private Bitmap decodeUriAsBitmap(Uri uri){
		Bitmap bitmap = null;
		Bitmap newBitmap = null;
		try {
			Options opts = new Options();
			opts.outWidth = 600;
			opts.outHeight = 800;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, opts);
			int height = bitmap.getHeight() * 600 / bitmap.getWidth();
			newBitmap = Bitmap.createScaledBitmap(bitmap, 600, height, false);
			bitmap.recycle();
			bitmap = null;
//			bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return newBitmap;
	}

	@Override
	public void Success(int index) {
		switch (index) {
		case InsertManager.UPLOADIMAGE:
			mHandler.sendEmptyMessage(UPLOAD);
			break;
		case InsertManager.SHARE:
			mHandler.sendEmptyMessage(SHARE);
			break;

		default:
			break;
		}
	}

	@Override
	public void Failed(int index) {
		Message msg = new Message();
		msg.what = FAILED;
		switch (index) {
		case InsertManager.UPLOADIMAGE:
			msg.obj = getString(R.string.upload_img_failed);
			break;
		case InsertManager.SHARE:
			msg.obj = getString(R.string.share_failed);
			break;

		default:
			break;
		}
		mHandler.sendMessage(msg);

	}

	@Override
	public void Dialog(boolean toShow) {

	}

	@Override
	public void SessionFailed() {
		startActivity(new Intent(this, LoginActivity.class));
		Toast.makeText(this, getString(R.string.account_failed), Toast.LENGTH_SHORT).show();
		app.user.sessionID = "";
		MassVigUtil.setPreferenceStringData(this, "SESSIONID", "");
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SHARE:
				setResult(RESULT_OK);
				InsertPost.this.finish();
				break;
			case FAILED:
				if (progress != null && progress.isShowing()) {
					progress.dismiss();
				}
				break;
			case UPLOAD:
				if (progress != null && progress.isShowing()) {
					progress.dismiss();
				}
				String content = editContent.getText().toString()
						.replaceAll("/(^\\\\s*)|(\\\\s*$)/g", "");
				manager.comment = content;
				app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(InsertPost.this, "SESSIONID", "") : app.user.sessionID;
				manager.Share(app.user.sessionID);
				if (!TextUtils.isEmpty(manager.imgUrl)) {
					subMitForm();
				}
				break;
			case SUBMIT:
				if (!TextUtils.isEmpty(manager.imgUrl)) {
					subMitForm();
				} else {
					manager.UploadImage(muploadImgBitmap);
				}
				break;

			default:
				break;
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;

		default:
			break;
		}
	}

}
