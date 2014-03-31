/**
 *  MassVig Technology 2011
 */
package com.massvig.ecommerce.activities;

import org.json.JSONObject;

import com.google.analytics.tracking.android.EasyTracker;
import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.service.EcommercePushService;
import com.massvig.ecommerce.service.MassVigService;
import com.massvig.ecommerce.service.MassVigService.timeoutListener;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigSystemSetting;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.widgets.MassVigData;
import com.unionpay.upomp.yidatec.transactionmanage.SplashActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Toast;

/**
 * 
 * @author DuJun
 *
 */
public class LaunchingActivity extends Activity {
	
	private BaseApplication app;
	Handler delayCall = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Toast.makeText(LaunchingActivity.this, msg.obj+"", Toast.LENGTH_LONG).show();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.launching);

		EasyTracker.getInstance().setContext(this);
		EasyTracker.getTracker().setCampaign("utm_source=" + getString(R.string.channel));
//		EasyTracker.getTracker().setCampaign("utm_source=" + getString(R.string.channel));
//		Intent intent = new Intent(LaunchingActivity.this,SplashActivity.class);
//		Bundle mBundle = new Bundle();
//		String s = "<?xml version='1.0' encoding='UTF-8'?><upomp application=\"UpPay.Req\"><merchantName>爱买</merchantName><merchantId>301310048990006</merchantId><merchantOrderId>201303268583124067459184442</merchantOrderId><merchantOrderTime>20130326184419</merchantOrderTime><merchantOrderAmt>8900</merchantOrderAmt><merchantOrderDesc>无</merchantOrderDesc><transTimeout/><backEndUrl>http://service.ibuy001.com:18080/secureservice/secureservice.asmx/orderPayFromUnionPay</backEndUrl><sign>zee4x8BeVPynynbGJSAdr8MHIdqhVHfJU0e9BrbkMUCBql71xmSA6DWfgn90wBAIu7lbVSfyul629DpGR87uYEolLz6Qmm4RghFAHazEPaWyqoW3BpgW5oRWu4swgLLwru8eTsQ9bl4DF5Zem1W7VrvUjCBxdm/IOVpjSt3iFZA=</sign><merchantPublicCert>MIID3jCCA0egAwIBAgIQTUZlMAn+K/QIh5rK25jgHjANBgkqhkiG9w0BAQUFADAqMQswCQYDVQQGEwJDTjEbMBkGA1UEChMSQ0ZDQSBPcGVyYXRpb24gQ0EyMB4XDTExMTIwMTA3NTE0M1oXDTEzMTIwMTA3NTE0M1owgZAxCzAJBgNVBAYTAkNOMRswGQYDVQQKExJDRkNBIE9wZXJhdGlvbiBDQTIxETAPBgNVBAsTCExvY2FsIFJBMRQwEgYDVQQLEwtFbnRlcnByaXNlczE7MDkGA1UEAxQyMDQxQDgzMTAwMDAwMDAwODMwNDBAMzAxMzEwMDQ4OTkwMDA2OlNJR05AMDAwMDAxNjcwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBANXAF5d0ZqKKVs6aQiqIw4SKxKM4GvtI80rXRErHK5a03BELGPv1c0Bi/xuyRO0Lo62cIn6g1aBePsdAFLbyH3WM5Uc63N+iludODlArv3GXd9T2ObFx6sZqX8Ro5gzAWX5chBytmvQJAW9r7vFu9H2VIvzvaI5/460nFV4mIn0fAgMBAAGjggGcMIIBmDAfBgNVHSMEGDAWgBTwje2zQbv77wgeVQLDMTfvPBROzTAdBgNVHQ4EFgQU8STrUJWpT1Hihk8JwR1EoVDlc7swCwYDVR0PBAQDAgTwMAwGA1UdEwQFMAMBAQAwOwYDVR0lBDQwMgYIKwYBBQUHAwEGCCsGAQUFBwMCBggrBgEFBQcDAwYIKwYBBQUHAwQGCCsGAQUFBwMIMIH9BgNVHR8EgfUwgfIwVqBUoFKkUDBOMQswCQYDVQQGEwJDTjEbMBkGA1UEChMSQ0ZDQSBPcGVyYXRpb24gQ0EyMQwwCgYDVQQLEwNDUkwxFDASBgNVBAMTC2NybDEwNF80NDA1MIGXoIGUoIGRhoGObGRhcDovL2NlcnQ4NjMuY2ZjYS5jb20uY246Mzg5L0NOPWNybDEwNF80NDA1LE9VPUNSTCxPPUNGQ0EgT3BlcmF0aW9uIENBMixDPUNOP2NlcnRpZmljYXRlUmV2b2NhdGlvbkxpc3Q/YmFzZT9vYmplY3RjbGFzcz1jUkxEaXN0cmlidXRpb25Qb2ludDANBgkqhkiG9w0BAQUFAAOBgQBx+2P6QBtG/xVHBLawmWkNbtrvToWaUN54X5Wbgv4dhlrAqEE2cLtdV0bB4sY95uu3M4GR4r/H9WZC3Im7te9ct9dH5zG7uZlWdzdReHE4OuBCstm1SDI1GwS7jU+Kq0Hq279dB8bWVtNY8+Ii7660rtn6TDxK5QcysSJG2IvpgA==</merchantPublicCert></upomp>";
////		mBundle.putString("xml", pluginXmlParameter());
//		mBundle.putString("xml", s);
//		/**签名证书信息---用于防篡改*/
//		// deploy
//		mBundle.putString(
//				"sign",
//				"F8B28875869127CDB26F2D20D64CC566C2418E9B833F7476C2543676E36009C8D658B662D2937F39DEBF884FD854545F650A44D300368265B41808DC12375E85E7597A47B24FEE928A187EE3440B87B8A829F3E65C0DC7980DC23C7DFC726C2F63FF63CDC3874CBA458E8E2C54E7F2F2AAFD847AD0C71EF7072DEF5A1D9113E3C065693E531B054151CE7B8F5BCE75ABFA7C8B7B6B0821072A9D6E1BDC7F5D369B46E9BD87F4FA76619BD5727CC34B3C");
////		mBundle.putString("sign", "583A0CB87683E3B4F36348AF48CE20B32F5BE1AC875A9CCD0AF4977F37C09F9E0ABCA823244333AB721F44B1F7CB2BC5FE792933459FCBDA9DC3C3730CFB577F60FD388A5ACA2E4DB4EC429B3E439043C84A07EA4EF3C61D994C8D237CEEE17F6E5A903D2F460325E4EE78216074FD63E15AFDBDAFBBA39768F2BB4F0B667202CCE4BDD48ADAB90B7222EBD2E7DBB9088331DA1DF75EED5E197DC94A7F359E8A489B2D028A65736A01654B331913EB32");
//		// debug
////		mBundle.putString("sign", "FF0E04C8452251B0556306AD4D0DA56EEF45C0424C3EEBBA6093AB8FCBFF2817973A1B947E12B4AEFB435AAF8F7EF7E947ED6F5A86EBC6FABEFF1AFCE54D12C8ACF1E8E30D091CF8497A5A815D5AB16C5260BFA993B3F9AABF723F4612B8246CF524D258541629C4D51ECE1372A0B5A67B5D5597D32B280AEBDBD1E3C1448EF666B627BE153E672636B59B6138B8C53567726C43860A0EAB7564E7FC9DF5027BA7E34536A05A4AB912090A721EAE9E12");
//		intent.putExtras(mBundle);
//		startActivity(intent);
//		return;
		app = (BaseApplication) getApplication();
		app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(LaunchingActivity.this, "SESSIONID", "") : app.user.sessionID;
		app.user.customerID = (app.user.customerID == -1) ? Integer.valueOf(MassVigUtil.getPreferenceData(LaunchingActivity.this, "CUSTOMERID", "-1")) : app.user.customerID ;
		app.user.nickName = TextUtils.isEmpty(app.user.nickName) ? MassVigUtil.getPreferenceData(LaunchingActivity.this, "NICKNAME", "") : app.user.nickName;
		app.user.headImage = TextUtils.isEmpty(app.user.headImage) ? MassVigUtil.getPreferenceData(LaunchingActivity.this, "HEADIMG", "") : app.user.headImage;
		app.user.gender = (app.user.gender == 0) ? Integer.valueOf(MassVigUtil.getPreferenceData(LaunchingActivity.this, "GENDER", "0")) : app.user.gender ;
		app.user.EJID = TextUtils.isEmpty(app.user.EJID) ? MassVigUtil.getPreferenceData(LaunchingActivity.this, "EJID", "") : app.user.EJID;
		app.user.EJPassword = TextUtils.isEmpty(app.user.EJPassword) ? MassVigUtil.getPreferenceData(LaunchingActivity.this, "EJPASS", "") : app.user.EJPassword;
		app.user.EJResource = TextUtils.isEmpty(app.user.EJResource) ? MassVigUtil.getPreferenceData(LaunchingActivity.this, "EJRESOURCE", "") : app.user.EJResource;
		app.user.EJServerIP = TextUtils.isEmpty(app.user.EJServerIP) ? MassVigUtil.getPreferenceData(LaunchingActivity.this, "IP", "") : app.user.EJServerIP;
		app.user.EJServerPort = TextUtils.isEmpty(app.user.EJServerPort) ? MassVigUtil.getPreferenceData(LaunchingActivity.this, "PORT", "") : app.user.EJServerPort;
		app.user.EJServerDomaim = TextUtils.isEmpty(app.user.EJServerDomaim) ? MassVigUtil.getPreferenceData(LaunchingActivity.this, "Domain", "") : app.user.EJServerDomaim;
		MassVigUtil.CopyAssets(this, "pics", MassVigContants.PATH );
		MassVigUtil.CopyAssets(this, "pics", MassVigContants.MAINPATH );
		MassVigData.getinstance(this).execute(this);
		MassVigData.getinstance(this).getHotKey(this);
		if(MassVigUtil.isNetworkAvailable(this) || MassVigUtil.isWiFiActive(this)){
//			pushCrashLog();
			new checkVersionAsync().execute();
		}else{
			startActivity(new Intent(LaunchingActivity.this, MainTabActivity.class));
			Toast.makeText(LaunchingActivity.this, getString(R.string.network_unable), Toast.LENGTH_LONG).show();
			LaunchingActivity.this.finish();
		}
		
	}
	private void startService() {
		stopService(new Intent(LaunchingActivity.this,
				EcommercePushService.class));
		startService(new Intent(LaunchingActivity.this,
				EcommercePushService.class));
	}

	private class checkVersionAsync extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			MassVigService service = MassVigService.getInstance();
			service.setListener(new timeoutListener() {
				
				@Override
				public void TimeOutListener() {
					Message message = new Message();
					message.obj = getString(R.string.network_unable);
					delayCall.sendMessage(message);
					LaunchingActivity.this.finish();
					
				}
			});
			return service.getAndroidVersion(app.user.sessionID,MassVigUtil.id(LaunchingActivity.this));
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
						startActivity(new Intent(LaunchingActivity.this, MainTabActivity.class));
						startService();
		        		LaunchingActivity.this.finish();
					}
				}else{
					startActivity(new Intent(LaunchingActivity.this, MainTabActivity.class));
					Toast.makeText(LaunchingActivity.this, getString(R.string.network_unconnect), Toast.LENGTH_SHORT).show();
	        		LaunchingActivity.this.finish();
				}
			} catch (Exception e) {
				startActivity(new Intent(LaunchingActivity.this, MainTabActivity.class));
				Toast.makeText(LaunchingActivity.this, getString(R.string.network_unconnect), Toast.LENGTH_SHORT).show();
        		LaunchingActivity.this.finish();
			}
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
	
	/**
	 * 显示可选升级dialog
	 */
	public void showSoftDialog() {
		new AlertDialog.Builder(LaunchingActivity.this)
		.setTitle(getString(R.string.update_tips))
		.setMessage(getString(R.string.update_soft))
		.setPositiveButton(getString(R.string.sure), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				downApk();
			}
		})
		.setNegativeButton(getString(R.string.cancel), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				String rememberVersion = MassVigSystemSetting.getInstance().getLocalVersionName(LaunchingActivity.this);
	        	boolean isNewVersion = false;
	        	try {
					String currentVersion = getVersionName();
					if(rememberVersion.equals(currentVersion)){
						isNewVersion = false;
					}else{
						isNewVersion = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
	        	
	    		if(!isNewVersion){//不是新版本，不显示storyboard
	        		startActivity(new Intent(LaunchingActivity.this, MainTabActivity.class));
	        		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	        		LaunchingActivity.this.finish();
	        	}else{//是新版本，显示storyboard
	        		startActivity(new Intent(LaunchingActivity.this, MainTabActivity.class));
	        		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	        		LaunchingActivity.this.finish();
	        	}
				startService();
			}
		})
		.show();
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
        LaunchingActivity.this.finish();
	}

	/**
	 * 显示强制升级dialog
	 */
	public void showHardDialog() {
		new AlertDialog.Builder(LaunchingActivity.this)
		.setCancelable(false)
		.setTitle(getString(R.string.update_tips))
		.setMessage(getString(R.string.update_hard))
		.setPositiveButton(getString(R.string.sure), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				downApk();
			}
		})
		.setNegativeButton(getString(R.string.cancel), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				LaunchingActivity.this.finish();
			}
		})
		.show();
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
	
//	/**
//	 * 发送错误报告
//	 */
//	private void pushCrashLog() {
//		MassvigLogger.getInstance().v("dujun", "push crash log");
//		File file = new File(NoQCrashLog.fullName);
//		long lastTime = NoQSystemSetting.getInstance().getCrashLogLastModifiedTime(this);
//		long nowTime = file.lastModified();
//		if (file.exists() && file.isFile() && isNeedToPush(lastTime, nowTime)) {
//			String crashLog = NoQUtil.loadFile(file);
//			new NoQPushCrashLogAsync(this).execute(crashLog, nowTime + "");
//		}
//	}
//	
//	/**
//	 * 只发送第一次，重复日志不发送，所以判断是否需要发送错误日志
//	 * @return
//	 */
//	private boolean isNeedToPush(long lastTime, long nowTime){
//		boolean need = false;
//		if(nowTime == 0){
//			need = false;
//		}else if(nowTime != 0 && nowTime != lastTime){
//			need = true;
//		}
//		return need;
//	}
//	
//	/**
//	 * 开启服务
//	 */
//	private void startService(){
//		String serviceName = "com.massvig.noq.logic.notification.NoQPushService";
//		ActivityManager mActiviryManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
//		List<ActivityManager.RunningServiceInfo> mServiceList = mActiviryManager.getRunningServices(30);
//		boolean isRunning = NoQUtil.ServiceIsStart(mServiceList, serviceName);
//		if (!isRunning) {
//			stopService(new Intent(LaunchingActivity.this,
//					NoQPushService.class));
//			startService(new Intent(LaunchingActivity.this,
//					NoQPushService.class));
//		}
//	}
	
}
