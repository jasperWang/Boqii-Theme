package com.massvig.ecommerce.activities;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.utilities.OAuthConstant;
import com.weibo.net.Utility;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;
import com.weibo.net.WeiboParameters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class ShareWebSinaActivity extends Activity{
	WebView webView;
	Weibo mWeibo;
//	ShareManage sm;
	ProgressDialog pd;
//	ImageButton backBtn;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.share_web_view);
		webView = (WebView) findViewById(R.id.web);
//		backBtn = (ImageButton)findViewById(R.id.navi_back_button);
		mWeibo = Weibo.getInstance();
		webView.setVerticalScrollBarEnabled(false);
		webView.setHorizontalScrollBarEnabled(false);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);
		webView.setWebViewClient(new WeiboWebViewClient());
	    
		mWeibo.setupConsumerConfig(OAuthConstant.APP_KEY_SINA, OAuthConstant.APP_SECRET_SINA);
		mWeibo.setRedirectUrl(OAuthConstant.callback_sina);
		webView.loadUrl(Weibo.URL_OAUTH2_ACCESS_AUTHORIZE+"?client_id="+OAuthConstant.APP_KEY_SINA+"&response_type=token&redirect_uri="+mWeibo.getRedirectUrl()+"&display=mobile");
		((Button) findViewById(R.id.back))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						ShareWebSinaActivity.this.finish();
					}
				});
//		sm = new ShareManage(this);
//		backBtn.setOnClickListener(new OnClickListener() {
//			
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				onBackPressed();
//			}
//		});
	}
	
	private class WeiboWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.indexOf("#access_token") != -1) {
                handleRedirectUrl(view, url);
                return true;
            }
            // launch non-dialog URLs in a full browser
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description,
                String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // google issue. shouldOverrideUrlLoading not executed
            if (url.indexOf("#access_token") != -1) {
                handleRedirectUrl(view, url);
                view.stopLoading();
            }
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

        }

    }
	
	private void handleRedirectUrl(WebView view, String url) {
        Bundle values = Utility.parseUrl(url);

        String error = values.getString("error");
        String error_code = values.getString("error_code");

        if (error == null && error_code == null) {
        	webView.setVisibility(View.INVISIBLE);

//        	sm.setToKen(OAuthConstant.SINA, values.getString("access_token"));
//        	long expires = Long.parseLong(values.getString("expires_in"));
//        	Long finalExpires = System.currentTimeMillis() + expires * 1000;
//        	sm.setTokenValidity(OAuthConstant.SINA, finalExpires);
        	
        	final WeiboParameters params = new WeiboParameters();
        	params.add("uid", values.getString("uid"));
        	params.add("source", Weibo.getAppKey());
        	params.add("access_token", values.getString("access_token"));
        	MassVigUtil.setPreferenceStringData(this, OAuthConstant.SINA_ACCESS_TOKEN, values.getString("access_token"));
        	new AsyncOauth(new ResultCallBack() {
				
				@Override
				public Object runFuncation() {
					// TODO Auto-generated method stub
					try {
						return mWeibo.request(ShareWebSinaActivity.this, Weibo.SERVER + "users/show.json", params, "GET", null);
					} catch (WeiboException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return "";
				}
				
				@Override
				public void resutl(Object result) {
					// TODO Auto-generated method stub
//					try {
//						String nick = new JSONObject(result.toString()).getString("screen_name");
			        	MassVigUtil.setPreferenceBooleanData(ShareWebSinaActivity.this, OAuthConstant.SINA, true);
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					ShareWebSinaActivity.this.setResult(Activity.RESULT_OK);
					finish();
				}
			}).execute();
        } else if (error.equals("access_denied")) {
        	
        } else {
            //mListener.onWeiboException(new WeiboException(error, Integer.parseInt(error_code)));
        }
    }
	
	
	class AsyncOauth extends AsyncTask<Object, Object, Object>{
		ProgressDialog pd;
		private ResultCallBack callback;
		public AsyncOauth(ResultCallBack callback){
			this.callback = callback;
		}
		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			return callback.runFuncation();
		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			if(pd != null && !ShareWebSinaActivity.this.isFinishing()){
				pd.dismiss();
			}
			callback.resutl(result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pd = new ProgressDialog(ShareWebSinaActivity.this);
			pd.setMessage(getString(R.string.please_wait));
			pd.show();
		}
		
	}
	
	public interface ResultCallBack{
		public Object runFuncation();
		public void resutl(Object result);
	}
}
