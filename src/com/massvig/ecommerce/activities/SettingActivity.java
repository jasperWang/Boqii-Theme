package com.massvig.ecommerce.activities;

import org.json.JSONObject;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.User;
import com.massvig.ecommerce.service.EcommercePushService;
import com.massvig.ecommerce.service.MassVigService;
import com.massvig.ecommerce.service.MassVigService.timeoutListener;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.Toast;

public class SettingActivity extends BaseActivity implements OnClickListener{

	private BaseApplication app;
	private ProgressDialog mDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		setTitle(getString(R.string.setting));
		mDialog = new ProgressDialog(this);
		app = (BaseApplication) getApplication();
		((Button)findViewById(R.id.back)).setOnClickListener(this);
		((Button)findViewById(R.id.logout)).setOnClickListener(this);
		((TableRow)findViewById(R.id.location_row)).setOnClickListener(this);
		((TableRow)findViewById(R.id.refund_row)).setOnClickListener(this);
//		((TableRow)findViewById(R.id.notify_row)).setOnClickListener(this);
		((TableRow)findViewById(R.id.share_row)).setOnClickListener(this);
		((TableRow)findViewById(R.id.clearcache_row)).setOnClickListener(this);
		((TableRow)findViewById(R.id.touchus_row)).setOnClickListener(this);
		((TableRow)findViewById(R.id.checkversion_row)).setOnClickListener(this);
		((TableRow)findViewById(R.id.feedback_row)).setOnClickListener(this);
		((TableRow)findViewById(R.id.score_row)).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.logout:
			User user = new User();
			app.user = user;
			MassVigUtil.setPreferenceStringData(SettingActivity.this, "CUSTOMERID", "-1");
			MassVigUtil.setPreferenceStringData(SettingActivity.this, "HEADIMG", "");
			MassVigUtil.setPreferenceStringData(SettingActivity.this, "GENDER", "0");
			MassVigUtil.setPreferenceStringData(SettingActivity.this, "NICKNAME", "");
			MassVigUtil.setPreferenceStringData(SettingActivity.this, "SESSIONID", "");
			MassVigUtil.setPreferenceStringData(SettingActivity.this, "IP", "");
			MassVigUtil.setPreferenceStringData(SettingActivity.this, "PORT", "");
			MassVigUtil.setPreferenceStringData(SettingActivity.this, "Domain", "");
			MassVigUtil.setPreferenceStringData(SettingActivity.this, "EJID", "");
			MassVigUtil.setPreferenceStringData(SettingActivity.this, "EJPASS", "");
			MassVigUtil.setPreferenceStringData(SettingActivity.this, "EJRESOURCE", "");
			new logoutAsync().execute();
			finish();
			break;
		case R.id.feedback_row:
			app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(this, "SESSIONID", "") : app.user.sessionID;
			if(!TextUtils.isEmpty(app.user.sessionID)){
				startActivity(new Intent(this,AddOption.class));
			}else{
				app.user.sessionID="";
				MassVigUtil.setPreferenceStringData(this, "SESSIONID", "");
				startActivity(new Intent(this,LoginActivity.class));
			}
			//TODO
			break;
		case R.id.location_row:
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
			break;
		case R.id.refund_row:
			startActivity(new Intent(this, RefundToknow.class));
			break;
//		case R.id.notify_row:
//			
//			break;
		case R.id.share_row:
			startActivity(new Intent(this, ShareAccountActivity.class));
			break;
		case R.id.clearcache_row:
			if (mDialog != null && !mDialog.isShowing()) {
				mDialog.setMessage(getString(R.string.clearcache));
				mDialog.show();
			}
			new clearCacheAsync().execute();
			break;
		case R.id.touchus_row:
			startActivity(new Intent(this, TouchUsActivity.class));
			break;
		case R.id.checkversion_row:
			new checkVersionAsync().execute();
			break;
		case R.id.score_row:
			try {
				Intent market = new Intent(Intent.ACTION_VIEW);
				market.setData(Uri.parse("market://details?id=" + getPackageName()));
				startActivity(market);
			} catch (Exception e) {
				Toast.makeText(SettingActivity.this, getString(R.string.market), Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 获取当前安装版本号
	 * @return
	 * @throws Exception
	 */
	private String getVersionName() throws Exception {
		PackageManager packageManager = getPackageManager();
		PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),
				0);
		String version = packInfo.versionName;
		return version;
	}

	private class checkVersionAsync extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			MassVigService service = MassVigService.getInstance();
			service.setListener(new timeoutListener() {
				
				@Override
				public void TimeOutListener() {
					//TODO
				}
			});
			return service.getAndroidVersion(app.user.sessionID,MassVigUtil.id(SettingActivity.this));
		}

		@Override
		protected void onPostExecute(String result) {
//			startActivity(new Intent(LaunchingActivity.this, MainTabActivity.class));
			String minVersion = "";
			String currentVersion = "";
			try {
				String installVersion = getVersionName();
				JSONObject response = new JSONObject(result);
				int resultCode = response.getInt("ResponseStatus");
//				String message = object.getString("ResponseMsg");
				if(resultCode == 0){
					JSONObject data = response.getJSONObject("ResponseData");
					minVersion = data.getString("MinVersion");
					currentVersion = data.getString("CurrentVersion");
					if(TextUtils.isEmpty(minVersion)){
						return;
					}
					if(versionCheck(minVersion, installVersion)){
						//hard
						showHardDialog();
					}else if(versionCheck(currentVersion, installVersion)){
						//soft
						showSoftDialog();
					}else{
						Toast.makeText(SettingActivity.this, getString(R.string.already_newest), Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(SettingActivity.this, getString(R.string.network_unconnect), Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				Toast.makeText(SettingActivity.this, getString(R.string.network_unconnect), Toast.LENGTH_SHORT).show();
			}
		}

	}

	private class logoutAsync extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				stopService(new Intent(SettingActivity.this,EcommercePushService.class));
			} catch (Exception e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			
		}

	}

	/**
	 * 下载apk
	 */
	protected void downApk() {
		//TODO MODITY
		String uri = new String(MassVigContants.DOWNLOAD);
		Uri u = Uri.parse(uri);
		Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(u);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
	}

	/**
	 * 版本判断
	 * @param remote
	 * @param current
	 * @return
	 */
	private boolean versionCheck(String remote, String current){//true:remote > current
		boolean result = false;
		String[] remoteNum = new String[3];
		String[] currentNum = new String[3];
		remoteNum = remote.split("[.]");
		currentNum = current.split("[.]");
		if(Integer.valueOf(remoteNum[0]) < Integer.valueOf(currentNum[0])){
			return false;
		}else if(Integer.valueOf(remoteNum[0]) > Integer.valueOf(currentNum[0])){
			return true;
		}
		if(Integer.valueOf(remoteNum[1]) < Integer.valueOf(currentNum[1])){
			return false;
		}else if(Integer.valueOf(remoteNum[1]) > Integer.valueOf(currentNum[1])){
			return true;
		}
		if(Integer.valueOf(remoteNum[2]) < Integer.valueOf(currentNum[2])){
			return false;
		}else if(Integer.valueOf(remoteNum[2]) > Integer.valueOf(currentNum[2])){
			return true;
		}
		return result;
	}
	
	/**
	 * 显示可选升级dialog
	 */
	public void showSoftDialog() {
		new AlertDialog.Builder(this)
		.setTitle(getString(R.string.update_tips))
		.setMessage(getString(R.string.update_soft))
		.setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				downApk();
			}
		})
		.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		})
		.show();
	}

	/**
	 * 显示强制升级dialog
	 */
	public void showHardDialog() {
		new AlertDialog.Builder(this)
		.setCancelable(false)
		.setTitle(getString(R.string.update_tips))
		.setMessage(getString(R.string.update_hard))
		.setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				downApk();
			}
		})
		.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		})
		.show();
	}

	
	public class clearCacheAsync extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			MassVigUtil.deleteFiles(MassVigContants.PATH);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (mDialog != null && mDialog.isShowing()) {
				mDialog.dismiss();
			}
			super.onPostExecute(result);
		}

	}

}
