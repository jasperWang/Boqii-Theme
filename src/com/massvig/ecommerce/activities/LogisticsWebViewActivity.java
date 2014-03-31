package com.massvig.ecommerce.activities;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.service.MassVigService;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.Button;

public class LogisticsWebViewActivity extends BaseActivity {

	private WebView mWebView;
	private String url = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web);
		setTitle(getString(R.string.logisticswebview));
		url = this.getIntent().getStringExtra("URL");
//		url = "http://m.kuaidi100.com/query?type=tiantian&postid=550000468643&id=1&valicode=&temp=8609";
		initView();
		new GetContentAsync().execute();
	}

	private void initView() {

		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
//		mWebView.loadUrl(url);
		mWebView.setWebViewClient(new WebViewClient(){

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
			
		});
		((Button)findViewById(R.id.left_button)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LogisticsWebViewActivity.this.finish();
			}
		});
		
	}
	
	class GetContentAsync extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
//			if (dialog != null && !dialog.isShowing())
//				dialog.show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO
			new PutContentAsync().execute(result);
			super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO
			String content = MassVigService.getInstance().getWebContent(url);
			return content;
		}

	}
	
	class PutContentAsync extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO
			mWebView.loadDataWithBaseURL(null, result, "text/html", "UTF-8", null);
//			mWebView.loadData(result, "text/html", "utf-8");
//			mWebView.loadUrl(result);
			super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO
			String content = MassVigService.getInstance().putWebContent(params[0]);
			return content;
		}

	}
}
