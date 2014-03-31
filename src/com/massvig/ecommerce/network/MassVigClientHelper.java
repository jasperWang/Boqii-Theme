package com.massvig.ecommerce.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpStatus;

import android.accounts.NetworkErrorException;
import android.util.Log;

import com.massvig.ecommerce.utilities.MassVigUtil;

/**
 * ClientHelper
 * 请求数据(GET/POST方式)
 * @author DuJun
 */

public class MassVigClientHelper {
	
	 private String httpUrl;
	 private String method;
	 public static String POST = "POST";
	 public static String GET = "GET";
	 private MassVigRequestParameters mRequestParameters;
	 private static MassVigClientHelper instance;
	 private TimeOutListener listener;
	 
	 
	 public static MassVigClientHelper getInstance(){
//		 if(instance == null){
			 instance = new MassVigClientHelper();
//		 }
		 return instance;
	 }
	 
	 public void setTimeoutListener(TimeOutListener listener){
		 this.listener = listener;
	 }
	
	 public synchronized String set(String httpUrl,MassVigRequestParameters mRequestParameters,String method){
			this.httpUrl = httpUrl;
			this.method = method;
			this.mRequestParameters = mRequestParameters;
			return mRequestParameters.getRquestParam();
	 }
	 
	 /**
	  * 
	  * @param httpUrl
	  * @param mRequestParameters 参数
	  * @param method post/get
	  * @return String
	  */
	 public String execute(String httpUrl,MassVigRequestParameters mRequestParameters,String method){
			String params = set(httpUrl, mRequestParameters, method);
			String responseData = null;
			HttpURLConnection urlConnection = null;
			try{
				urlConnection = getURLConnection(params);
				Log.v("dujun", urlConnection + "");
				InputStream stream = urlConnection.getInputStream(); 
				if(urlConnection.getResponseCode() == HttpStatus.SC_OK){
					byte[] b = new byte[1024];
					int readedLength = -1;
					ByteArrayOutputStream outputS = new ByteArrayOutputStream();
					while ((readedLength = stream.read(b)) != -1) {
						outputS.write(b, 0, readedLength);
					}
					responseData = outputS.toString();
					Log.v("dujun", responseData);
					urlConnection.disconnect();
				}else if(urlConnection.getResponseCode() == HttpStatus.SC_REQUEST_TIMEOUT){
					listener.timeoutListener();
					urlConnection.disconnect();
				}else{
					urlConnection.disconnect();
				}
				
			}catch(Exception e){
				if(urlConnection != null){
					urlConnection.disconnect();
				}
				e.printStackTrace();
				responseData = null;
			}
			return responseData;	
		} 
	 
	 /**
	  * 
	  * 返回HttpURLConnection对象
	  * @return HttpURLConnection
	  * @throws IOException
	  */
	private HttpURLConnection getURLConnection(String params) throws IOException{
		HttpURLConnection conn = null;
		if(method.equals(POST)){
			URL url = new URL(httpUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(20000);
			conn.setDoOutput(true);
			conn.getOutputStream().write(params.getBytes());
		}else if(method.equals(GET)){
			if(mRequestParameters != null){
				String param = params;
				httpUrl = (httpUrl.indexOf("?") != -1) ? httpUrl + "&"  + param  : httpUrl +"?" + param;
			}
			URL url = new URL(httpUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.addRequestProperty("User-Agent", MassVigUtil.getHeader());
			conn.setConnectTimeout(20000);
			conn.connect();
		}
		return conn;
	}
	
	public interface TimeOutListener{
		void timeoutListener();
	}
	
}

