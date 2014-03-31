package com.massvig.ecommerce.utilities;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;

import org.json.JSONObject;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.base64.BASE64Decoder;
import com.massvig.ecommerce.base64.BASE64Encoder;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.widget.Toast;

public class MassVigUtil {

	private final static String NAME = "MASSVIG";

	public static void CopyAssets(final Context c, String assetDir, String dir){
		String[] files;
		try {
			files = c.getResources().getAssets().list(assetDir);
		} catch (Exception e) {
			return;
		}
		File SdPath = new File(dir);
		if (!SdPath.exists() || !SdPath.isDirectory()) {
			SdPath.mkdirs();
		}
		for(int i=0;i<files.length;i++){
			try {
				String fileName = files[i];
				//make sure file name not contains '.' to be a folder
				if(fileName.contains("jpg") || fileName.contains("png")){
					File outFile = new File(SdPath, fileName);
					if(outFile.exists())
						outFile.delete();
					InputStream in = null;
					if(assetDir.length() == 0){
						in = c.getAssets().open(fileName);
					}else{
						in = c.getAssets().open(assetDir + "/" + fileName);
					}
					OutputStream o = new FileOutputStream(outFile);
					byte[] buf = new byte[1024];
					int len;
					while((len = in.read(buf)) > 0){
						o.write(buf, 0, len);
					}
					in.close();
					o.close();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	/**
	 * round bitmap
	 * 
	 * @param bitmap
	 * @param roundPx
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

		if (bitmap == null)
			return null;
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}
	
	/**
	 * 获取指定宽高的图片
	 * @param imageurl
	 * @param width
	 * @param height
	 * @return
	 */
	public static String GetImageUrl(String imageurl, int width, int height){
		String url = imageurl;
		String image = "";
		if (!TextUtils.isEmpty(url) && !url.equals("null")) {
			String[] elements = url.split("/");
			String front = elements[0] + "/" + elements[1] + "/" + elements[2];
			String middle = "";
			for (int i = 3; i < elements.length - 1; i++) {
				middle += elements[i] + "_";
			}
			if(!TextUtils.isEmpty(middle)){
				middle = middle.substring(0, middle.length() - 1);
				String back = elements[elements.length - 1];
				back = width + "x" + height + "_1_0_80_" + back;
				image = front + "/" + middle + "/" + back;
			}
		}
		return image;
	}
	
	/**
	 * md5
	 * @param val
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String getMD5(String val) throws NoSuchAlgorithmException {
//		MessageDigest md5 = MessageDigest.getInstance("MD5");
//		md5.update(val.getBytes());
//		byte[] m = md5.digest();
//		String str = getString(m);
//		str = str.toUpperCase();
//		return str;
		
		String result = "";
		try {
			result = GetRSAString(val);
			result = URLEncoder.encode(result, "UTF-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
//		return val;
	}
	
    private static  String GetRSAString(String str_m) throws Exception {
        String str_exponent = MassVigContants.EXPONENT;
        String str_modulus = MassVigContants.MODULUS;
        //创建公钥 
        byte[] ary_exponent=(new BASE64Decoder()).decodeBuffer(str_exponent);
        byte[] ary_modulus=(new BASE64Decoder()).decodeBuffer(str_modulus);
        //注意构造函数，调用时指明正负值，1代表正值，否则报错
         BigInteger big_exponent = new BigInteger(1,ary_exponent);
        BigInteger big_modulus = new BigInteger(1,ary_modulus);
        RSAPublicKeySpec keyspec=new RSAPublicKeySpec(big_modulus,big_exponent);
        KeyFactory keyfac=KeyFactory.getInstance("RSA");
        PublicKey publicKey=keyfac.generatePublic(keyspec);
         //进行加密 
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] enBytes = cipher.doFinal(str_m.getBytes("UTF-8"));
        String s = (new BASE64Encoder()).encodeBuffer(enBytes);
        return s;
  
    }
	/**
	 * 判断邮箱是否合法
	 * @param email_string
	 * @return
	 */
	public static boolean isEmail(String email_string) {

		String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]?@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		Pattern pattern = Pattern.compile(check);
		Matcher matcher = pattern.matcher(email_string);

		return matcher.matches();
	}
	/**
	 * 判断电话号码是否合法
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		System.out.println(m.matches() + "---");
		return m.matches();
	}
	/**
	 * 通过Service的类名来判断是否启动某个服务
	 * @param mServiceList
	 * @param className
	 * @return
	 */
	public static boolean ServiceIsStart(
			List<ActivityManager.RunningServiceInfo> mServiceList,
			String className) {
		for (int i = 0; i < mServiceList.size(); i++) {
			if (className.equals(mServiceList.get(i).service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取所有启动的服务的类名
	 * @param mServiceList
	 * @return
	 */
	public static String getServiceClassName(
			List<ActivityManager.RunningServiceInfo> mServiceList) {
		String res = "";
		for (int i = 0; i < mServiceList.size(); i++) {
			res += mServiceList.get(i).service.getClassName() + " \n";
		}
		return res;
	}

	/**
	 * 获取header
	 * 
	 * @return
	 */
	public static String getHeader() {
		String result = "";
		try {
			JSONObject object = new JSONObject();
			object.accumulate("appVersion", MassVigContants.VERSION);
			object.accumulate("systemVersion", Build.VERSION.RELEASE);
			object.accumulate("model", Build.BRAND + "_" + Build.MODEL);
			object.accumulate("systemName", "Android");
			result = object.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取文件内容
	 * 
	 * @param file
	 * @return
	 */
	public static String loadFile(File file) {
		String content = "";
		try {
			StringBuffer sb = new StringBuffer();
			BufferedReader br;
			br = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			br.close();
			content = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	/**
	 * 拨打电话，当只有一个电话时不弹出对话框，直接拨打
	 * 
	 * @param teleList
	 */
	public static void callUp(final Context mContext, String teleList) {
		if (!TextUtils.isEmpty(teleList)) {
			final String[] numbers = teleList.split(";");
			if (numbers.length == 1) {
				mContext.startActivity(new Intent(Intent.ACTION_CALL, Uri
						.parse("tel:" + numbers[0])));
			} else {
				Builder builder = new AlertDialog.Builder(mContext);
				builder.setTitle(mContext.getString(R.string.select_telephone))
						.setItems(numbers,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										mContext.startActivity(new Intent(
												Intent.ACTION_CALL,
												Uri.parse("tel:"
														+ numbers[which])));
									}

								}).create().show();
			}
		} else {
			Toast mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
			MassVigUtil.doToastShow(mContext, mToast,
					mContext.getString(R.string.no_telephone),
					Toast.LENGTH_SHORT);
		}
	}

	/**
	 * 判断wifi是否可用
	 * 
	 * @param inContext
	 * @return
	 */
	public static boolean isWiFiActive(Context inContext) {
		WifiManager mWifiManager = (WifiManager) inContext
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
		int ipAddress = wifiInfo == null ? 0 : wifiInfo.getIpAddress();
		if (mWifiManager.isWifiEnabled() && ipAddress != 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断3G是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info == null) {
				return false;
			} else {
				if (info.isAvailable()) {
					return true;
				}

			}
		}
		return false;
	}

	/**
	 * 设置自定义字体颜色大小
	 * 
	 * @param source
	 *            要改变的字体源
	 * @param color
	 *            目标颜色
	 * @param start
	 *            开始index
	 * @param end
	 *            结束index
	 * @param textsize
	 *            字体大小
	 * @return
	 */
	public static SpannableString setCustomText(CharSequence source, int color,
			int start, int end, int textsize) {
		SpannableString s = new SpannableString(source);
		s.setSpan(new ForegroundColorSpan(color), start, end,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//		s.setSpan(new AbsoluteSizeSpan(textsize), start, end,
//				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return s;
	}

	/**
	 * toast显示，多次点击不重复生成
	 * 
	 * @param toast
	 * @param str
	 *            显示的信息
	 * @param len
	 *            显示时长
	 */
	public static void doToastShow(Context context, Toast toast, String str,
			int len) {
		if (toast != null) {
			toast.cancel();
			toast.setText(str);
			toast.setDuration(len);
		} else {
			toast = Toast.makeText(context, str, len);
		}
		toast.show();
	}

	/**
	 * 判断gps是否开启
	 * 
	 * @param context
	 * @return
	 */

	public static boolean isGpsOpen(Context context) {
		LocationManager lm = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		boolean GPS_status = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		return GPS_status;
	}

	/**
	 * 获取唯一设备号
	 */
	private static String sID = null;
	private static final String INSTALLATION = "INSTALLATION";

	public synchronized static String id(Context context) {
		sID = null;
		if (sID == null) {
			File installation = new File(context.getFilesDir(), INSTALLATION);
			try {
				if (!installation.exists())
					writeInstallationFile(installation, context);
					sID = readInstallationFile(installation);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			return sID;
		}
		return null;
	}

	private static String readInstallationFile(File installation)
			throws IOException {
		RandomAccessFile f = new RandomAccessFile(installation, "r");
		byte[] bytes = new byte[(int) f.length()];
		f.readFully(bytes);
		f.close();
		return new String(bytes);
	}

	private static void writeInstallationFile(File installation, Context context)
			throws IOException {
		FileOutputStream out = new FileOutputStream(installation);
		String id = getMyUUID(context);
		out.write(id.getBytes());
		out.close();
	}

	private static String getMyUUID(Context context) {
		final TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = ""
				+ android.provider.Settings.Secure.getString(
						context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);
		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String uniqueId = deviceUuid.toString();
		return uniqueId;
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}


	/**
	 * change the bitmap size
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap setBitmapSize(Bitmap bitmap, int newWidth,
			int newHeight) {

		if (bitmap == null)
			return null;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		Bitmap newbitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, false);

		return newbitmap;
	}

	/**
	 * save boolean data into sharedPreference
	 * 
	 * @param context
	 * @param name
	 * @param key
	 * @param value
	 */
	public static void setPreferenceBooleanData(Context context, String key,
			Boolean value) {
		SharedPreferences settings = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	/**
	 * get boolean data from sharedPreference
	 * 
	 * @param context
	 * @param name
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static boolean getPreferenceData(Context context, String key,
			boolean defaultValue) {
		SharedPreferences settings = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		return settings.getBoolean(key, defaultValue);

	}

	/**
	 * save string data into sharedPreference
	 * 
	 * @param context
	 * @param name
	 * @param key
	 * @param value
	 */
	public static void setPreferenceStringData(Context context, String key,
			String value) {
		SharedPreferences settings = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * get String data from sharedPreference
	 * 
	 * @param context
	 * @param name
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String getPreferenceData(Context context, String key,
			String defaultValue) {
		SharedPreferences settings = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		return settings.getString(key, defaultValue);

	}

	/**
	 * 注销新浪
	 * 
	 * @param context
	 */
	public static void outLogin(Context context) {
		SharedPreferences share = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		Editor edit = share.edit();
		edit.remove(OAuthConstant.SINA_ACCESS_TOKEN);
		edit.remove(OAuthConstant.SINA_EXPIRES_IN);
		edit.remove(OAuthConstant.SINA_NICK_NAME);
		edit.remove(OAuthConstant.SINA_TOKEN_DATE);
		edit.commit();
	}

	public static void deleteFiles(String path) {
		File dirFile = new File(path);
		try {
			if (dirFile.exists()) {
				File[] fileList = dirFile.listFiles();
				if (fileList != null) {
					for (int i = 0; i < fileList.length; i++) {
						fileList[i].delete();
					}
				}
			}
		} catch (Exception e) {
			return;
		}
	}

	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}
		
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

}
