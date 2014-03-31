package com.massvig.ecommerce.activities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.managers.UserInfoManager;
import com.massvig.ecommerce.managers.UserInfoManager.Listener;
import com.massvig.ecommerce.managers.UserInfoManager.Numbers;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.widgets.NetImageView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ModifyUserInfoActivity extends BaseActivity implements OnClickListener, Listener{

	private static final int SUCCESS = 1;
	private static final int FAILED = 2;
	private static final int SHOW = 3;
	private static final int DISMISS = 4;
	private static final int FINISH = 5;
	private static final int NICKNAME = 6;
	private UserInfoManager manager;
	private BaseApplication app;
	private NetImageView headImg;
	private ImageView head;
	private Bitmap myBitmap;
//	private InputStream in;
	private boolean isOriginalPic = true;//是不是原来的图片，即图片是否没替换过
	private EditText username;
	private Drawable drawable;
	private Drawable drawable_uncheck;
	private ProgressDialog dialog;
	private TextView man, woman, mobile, email, modify_email, modify_mobile; 
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modify_user_info);
		setTitle(getString(R.string.modifyuserinfo));
		app = (BaseApplication) getApplication();
		manager = new UserInfoManager(app.user);
		manager.setListener(this);
		initView();
		manager.GetUploadImageUrl();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(myBitmap != null && !myBitmap.isRecycled()){
			myBitmap.recycle();
			System.gc();
		}
	}

	private void initView() {
		dialog = new ProgressDialog(this);
		drawable_uncheck = getResources().getDrawable(R.drawable.ic_unchecked);  
		drawable_uncheck.setBounds(0, 0, 26, 26); 
		drawable = getResources().getDrawable(R.drawable.ic_checked);  
		drawable.setBounds(0, 0, 26, 26);  
		headImg = (NetImageView)findViewById(R.id.head_img);
		head = (ImageView)findViewById(R.id.head);
		((ImageView)findViewById(R.id.take_picture_button)).setOnClickListener(this);
		username = (EditText)findViewById(R.id.username);
		man = (TextView)findViewById(R.id.man);
		woman = (TextView)findViewById(R.id.woman);
		man.setOnClickListener(this);
		woman.setOnClickListener(this);
		mobile = (TextView)findViewById(R.id.mobile);
		email = (TextView)findViewById(R.id.email);
		((TextView)findViewById(R.id.modify_password)).setOnClickListener(this);
		modify_email = (TextView)findViewById(R.id.modify_email);
		modify_mobile = (TextView)findViewById(R.id.modify_mobile);
		((TextView)findViewById(R.id.modify_email)).setOnClickListener(this);
		((TextView)findViewById(R.id.modify_mobile)).setOnClickListener(this);
		((Button)findViewById(R.id.finish)).setOnClickListener(this);
		((Button)findViewById(R.id.back)).setOnClickListener(this);
		manager.sessionID = app.user.sessionID;
	}

	@Override
	protected void onResume() {
		super.onResume();
		app = (BaseApplication) getApplication();
		manager.sessionID = app.user.sessionID;
		headImg.setImageUrl(app.user.headImage, MassVigContants.PATH, null);
		username.setText(app.user.nickName);
		if(app.user.gender == 1){
			man.setCompoundDrawables(drawable, null, null, null);
			woman.setCompoundDrawables(drawable_uncheck, null, null, null);
		}else if(app.user.gender == 2){
			man.setCompoundDrawables(drawable_uncheck, null, null, null);
			woman.setCompoundDrawables(drawable, null, null, null);
		}
		if(!TextUtils.isEmpty(app.user.mobile) && !app.user.mobile.equals("null")){
			mobile.setText(app.user.mobile);
			modify_mobile.setText(getString(R.string.modify));
		}else{
			mobile.setText("");
			modify_mobile.setText(getString(R.string.bind));
		}
		if(!TextUtils.isEmpty(app.user.email) && !app.user.email.equals("null")){
			email.setText(app.user.email);
			modify_email.setText(getString(R.string.modify));
		}else{
			email.setText("");
			modify_email.setText(getString(R.string.bind));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.finish:
			if(manager.isCanUpload && myBitmap != null && !isOriginalPic){
				if(dialog != null && !dialog.isShowing()){
					dialog.setMessage(getString(R.string.modifying));
					dialog.show();
				}
				manager.UploadImage(myBitmap);
			}else{
				if(!TextUtils.isEmpty(username.getText().toString())){
					app.user.nickName = username.getText().toString();
					manager.ModifyInformation();
				}
			}
			break;
		case R.id.back:
			ModifyUserInfoActivity.this.finish();
			break;
		case R.id.take_picture_button:

			String[] arg0 = { getString(R.string.camera),
					getString(R.string.phone_gallery) };
			new AlertDialog.Builder(this)
					.setTitle(getString(R.string.select_head_img))
					.setItems(arg0, new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog,
								int which) {
							switch (which) {
							case 0:
								String status = Environment
										.getExternalStorageState();
								if (status
										.equals(Environment.MEDIA_MOUNTED)) {

									Intent intent = new Intent(
											MediaStore.ACTION_IMAGE_CAPTURE);
									intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(MassVigContants.PATH + "/", "camera.png")));
									startActivityForResult(intent, 1);

								} else {
									Toast.makeText(ModifyUserInfoActivity.this,getString(R.string.no_sdcard),Toast.LENGTH_SHORT).show();
								}
								break;
							case 1:
								try {
									Intent intent = new Intent(
											Intent.ACTION_GET_CONTENT);
									intent.setType("image/*");
									intent.addCategory(Intent.CATEGORY_OPENABLE);
									startActivityForResult(intent, 0);
								} catch (Exception e) {

								}
								break;
							default:
								break;
							}
						}
					}).show();

			break;
		case R.id.man:
			man.setCompoundDrawables(drawable, null, null, null);
			woman.setCompoundDrawables(drawable_uncheck, null, null, null);
			app.user.gender = 1;
			break;
		case R.id.woman:
			woman.setCompoundDrawables(drawable, null, null, null);
			man.setCompoundDrawables(drawable_uncheck, null, null, null);
			app.user.gender = 2;
			break;
		case R.id.modify_password:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			View dialogView = LayoutInflater.from(this).inflate(
					R.layout.my_modify_password, null);
			builder.setView(dialogView);
			final AlertDialog dialog = builder.create();
			Button confirm = (Button) dialogView.findViewById(R.id.comfirm);
			Button cancel = (Button) dialogView.findViewById(R.id.cancel);
			cancel.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			final EditText ori_psd = (EditText) dialogView
					.findViewById(R.id.ori_psd);
			final EditText new_psd = (EditText) dialogView
					.findViewById(R.id.new_psd);
			final EditText conf_psd = (EditText) dialogView
					.findViewById(R.id.again_psd);
			confirm.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					String passWord = null;
					try {
						passWord = MassVigUtil.getMD5(ori_psd.getText()
								.toString());
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					}

//					if (passWord.equals(app.user.password)) {
						if (new_psd.getText().toString().equals(conf_psd.getText().toString())) {
							String newPsd;
							try {
								newPsd = MassVigUtil.getMD5(new_psd.getText().toString());
								manager.ModifyPassword(passWord, newPsd);
							} catch (NoSuchAlgorithmException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							dialog.dismiss();
						} else {
							Toast.makeText(
									ModifyUserInfoActivity.this,
									getString(R.string.confirm_psd_error),
									Toast.LENGTH_SHORT).show();
							new_psd.setText("");
							new_psd.requestFocus();
							conf_psd.setText("");
						}
//					} else {
//						Toast.makeText(ModifyUserInfoActivity.this,
//								getString(R.string.ori_psd_error),
//								Toast.LENGTH_SHORT).show();
//						ori_psd.setText("");
//						ori_psd.requestFocus();
//					}
				}
			});
			dialog.show();
		
			break;
		case R.id.modify_email:
			startActivity(new Intent(this, ModifyActivity.class).putExtra("MODIFY", ModifyActivity.EMAIL));
			break;
		case R.id.modify_mobile:
			startActivity(new Intent(this, ModifyActivity.class).putExtra("MODIFY", ModifyActivity.MOBILE));
			break;
		default:
			break;
		}
	}
	/**
	 * called when come back
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode != RESULT_OK) {
			return;
		}

//		ContentResolver resolver = getContentResolver();
		if (requestCode == 0) {
			try {
				Uri uri = data.getData();

				isOriginalPic = false;
//				cropImageUri(uri, 100, 100, 2);
				imageUri = uri;
				startActivityForResult(new Intent(ModifyUserInfoActivity.this, CropActivity.class).putExtra("URI", MassVigContants.PATH + "/" + "camera.png"), 2);
//				try {
//					in = resolver.openInputStream(uri);
//					Options opts = new Options();
//					opts.inPreferredConfig = Bitmap.Config.ARGB_4444;
//					opts.inSampleSize = 2;
//					myBitmap = BitmapFactory.decodeStream(in, null, opts);
//					isOriginalPic = false;
//					Bitmap headimg = MassVigUtil.setBitmapSize(myBitmap, 99, 99);
//					head.setImageBitmap(headimg);
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (requestCode == 1) {
			
			Bitmap headimg = null;
			
			try {
				String status = Environment.getExternalStorageState();
				if (status.equals(Environment.MEDIA_MOUNTED)) {

					isOriginalPic = false;
					Uri u = Uri.fromFile(new File(MassVigContants.PATH + "/", "camera.png"));
					imageUri = u;
					startActivityForResult(new Intent(ModifyUserInfoActivity.this, CropActivity.class).putExtra("URI", MassVigContants.PATH + "/" + "camera.png"), 2);
//					cropImageUri(u, 100, 100, 2);
					
//					File file = new File(MassVigContants.PATH + "/" + "camera.png");
//					isOriginalPic = false;
//					if (file.exists()) {
//						Options opts = new Options();
//						opts.inPreferredConfig = Bitmap.Config.ARGB_4444;
//						opts.inSampleSize = 2;
//						Bitmap bitmap = BitmapFactory.decodeFile(MassVigContants.PATH + "/" + "camera.png", opts);
//						if (bitmap != null) {
//							myBitmap = bitmap;
//							headimg = MassVigUtil.setBitmapSize(myBitmap, 99, 99);
//						}
//					}
				}else if(data != null){
					Bundle extras = data.getExtras();
					myBitmap = (Bitmap) extras.get("data");
					isOriginalPic = false;
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
					headimg = MassVigUtil.setBitmapSize(myBitmap, 99, 99);
				}
				
				head.setImageBitmap(headimg);
				super.onActivityResult(requestCode, resultCode, data);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(requestCode == 2){
			if(imageUri != null){
				Bitmap bitmap = decodeUriAsBitmap(imageUri);
				myBitmap = bitmap;
				head.setImageBitmap(bitmap);
			}
		}

	}
	
	private Bitmap decodeUriAsBitmap(Uri uri){
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inSampleSize = 4;
			bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri),null, opt);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	
	private Uri imageUri;
	
//	private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode){
//		imageUri = uri;
//		 Intent intent = new Intent("com.android.camera.action.CROP");
//		 intent.setDataAndType(uri, "image/*");
//		 intent.putExtra("crop", "true");
//		 intent.putExtra("aspectX", 1);
//		 intent.putExtra("aspectY", 1);
//		 intent.putExtra("outputX", outputX);
//		 intent.putExtra("outputY", outputY);
//		 intent.putExtra("scale", true);
//		 intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//		 intent.putExtra("return-data", false);
//		 intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//		 intent.putExtra("noFaceDetection", true); // no face detection
//		 startActivityForResult(intent, requestCode);
//	}

	@Override
	public void Success(int index) {
		Message msg = new Message();
		switch (index) {
		case UserInfoManager.PASSWORD:
			msg.what = SUCCESS;
			msg.obj = getString(R.string.password_success);
			mHandler.sendMessage(msg);
			break;
		case UserInfoManager.MODIFY:
			msg.what = FINISH;
			msg.obj = getString(R.string.information_success);
			mHandler.sendMessage(msg);
			break;
		case UserInfoManager.UPLOADIMAGE:
			if(!TextUtils.isEmpty(username.getText().toString())){
				app.user.nickName = username.getText().toString();
				manager.ModifyInformation();
			}
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
		case UserInfoManager.PASSWORD:
			msg.obj = getString(R.string.password_error);
			break;
		case UserInfoManager.MODIFY:
			msg.obj = getString(R.string.information_error);
			break;
		case UserInfoManager.UPLOADIMAGE:
			msg.obj = getString(R.string.upload_img_failed);
			break;
		default:
			break;
		}
		mHandler.sendMessage(msg);
	}

	@Override
	public void SessionFailed() {
//		if(!TextUtils.isEmpty(app.user.sessionID + MassVigUtil.getPreferenceData(this, "SESSIONID", "")))
			startActivity(new Intent(this, LoginActivity.class));
		app.user.sessionID = "";
		MassVigUtil.setPreferenceStringData(this, "SESSIONID", "");
	}

	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SUCCESS:
				Toast.makeText(ModifyUserInfoActivity.this, String.valueOf(msg.obj), Toast.LENGTH_SHORT).show();
				break;
			case FINISH:
				Toast.makeText(ModifyUserInfoActivity.this, String.valueOf(msg.obj), Toast.LENGTH_SHORT).show();
				ModifyUserInfoActivity.this.finish();
				break;
			case FAILED:
				Toast.makeText(ModifyUserInfoActivity.this, String.valueOf(msg.obj), Toast.LENGTH_SHORT).show();
				break;
			case SHOW:
				if(dialog != null && !dialog.isShowing()){
					dialog.setMessage(getString(R.string.modifying));
					dialog.show();
				}
				break;
			case DISMISS:
				if(dialog != null && dialog.isShowing())
					dialog.dismiss();
				break;
			case NICKNAME:
				Toast.makeText(ModifyUserInfoActivity.this, "昵称重复", Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		}};
	
	@Override
	public void Dialog(boolean toShow) {
		if (toShow) {
			mHandler.sendEmptyMessage(SHOW);
		}else{
			mHandler.sendEmptyMessage(DISMISS);
		}
	}

	@Override
	public void LoadData(Numbers n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void NickName() {
		mHandler.sendEmptyMessage(NICKNAME);
	}

}
