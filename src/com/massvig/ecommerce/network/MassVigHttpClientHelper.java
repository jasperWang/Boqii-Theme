package com.massvig.ecommerce.network;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;


/**
 * HttpClientHelper
 * 请求数据(HTTPGET/HTTPPOST方式)
 * @author DuJun
 */

public class MassVigHttpClientHelper {
	
	 private String httpUrl;
	 private static HttpClient customerHttpClient;
	 private String method;
	 public static String HTTPPOST = "HTTPPOST";
	 public static String HTTPGET = "HTTPGET";
	 private MassVigRequestParameters mRequestParameters;
	
	/**
	 * 返回加载的内容
	 * @Param httpUrl,RequestParameters,method
	 * @author DuJun
	 * @return byte[]
	 */
	 
	public byte[] execute(String httpUrl,MassVigRequestParameters mRequestParameters,String method){
		this.httpUrl = httpUrl;
		this.method = method;
		this.mRequestParameters = mRequestParameters;
		
		byte[] responseData = null;
		HttpClient httpclient = getHttpClient();
		HttpUriRequest mHttpUriRequest = null;
	
		try{
			mHttpUriRequest = getRequestEntity();
			HttpResponse response = httpclient.execute(mHttpUriRequest);
			
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				
				HttpEntity entity = response.getEntity();
				InputStream inputStream = entity.getContent();
				byte[] b = new byte[1024];
				int readedLength = -1;
				ByteArrayOutputStream outputS = new ByteArrayOutputStream();
				
				while((readedLength = inputStream.read(b)) != -1){
					outputS.write(b, 0, readedLength);
				}
				responseData = outputS.toByteArray();
				inputStream.close();
				outputS.close();
			}else if(response.getStatusLine().getStatusCode() == HttpStatus.SC_REQUEST_TIMEOUT){
				mHttpUriRequest.abort();
			}else{
				mHttpUriRequest.abort();
			}
			
		}catch(Exception e){
			if(mHttpUriRequest != null){
				mHttpUriRequest.abort();
			}
			e.printStackTrace();
			responseData = null;
		}
		return responseData;	
	}
	
	
	/**
	 * 返回HttpUriRequest对象
	 * @author DuJun 
	 * @throws UnsupportedEncodingException 
	 */
	private HttpUriRequest getRequestEntity() throws UnsupportedEncodingException{
		HttpUriRequest request = null;
		if(method.equals(HTTPPOST)){
			HttpPost httpPost = new HttpPost(URLEncoder.encode(httpUrl));
			if(mRequestParameters != null){
				StringEntity entity = new StringEntity(mRequestParameters.getRquestParam(),HTTP.UTF_8);
				entity.setContentType("application/x-www-form-urlencoded");
				httpPost.setEntity(entity);
			}
			request = (HttpUriRequest)httpPost;
		}else if(method.equals(HTTPGET)){
			if(mRequestParameters != null){
				String param = mRequestParameters.getRquestParam();
				httpUrl = (httpUrl.indexOf("?") != -1) ? httpUrl + "&"  + param  : httpUrl +"?" + param;
			}
			HttpGet httpGet = new HttpGet(URLEncoder.encode(httpUrl));
			request = (HttpUriRequest)httpGet;
		}
		return request;
	}
	
	
	private static HttpClient getHttpClient(){
        if (null== customerHttpClient) {
            HttpParams params =new BasicHttpParams();
            // 设置一些基本参数
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params,HTTP.UTF_8);
            HttpProtocolParams.setUseExpectContinue(params, true);
            HttpProtocolParams.setUserAgent(params,"Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
            ConnManagerParams.setTimeout(params, 5000); /* 从连接池中取连接的超时时间 */
            HttpConnectionParams.setConnectionTimeout(params, 5000); /* 连接超时 */
            HttpConnectionParams.setSoTimeout(params, 10000); /* 请求超时 */
            ConnManagerParams.setMaxTotalConnections(params, 800);
            
            // 设置我们的HttpClient支持HTTP和HTTPS两种模式
            SchemeRegistry schReg =new SchemeRegistry();
            schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
            // 使用线程安全的连接管理来创建HttpClient
            ClientConnectionManager conMgr =new ThreadSafeClientConnManager(params, schReg);
            customerHttpClient =new DefaultHttpClient(conMgr, params);
        }
        return customerHttpClient;
    }
	
	public String getRequestUrl(){
		return this.httpUrl;
	}
	
}

