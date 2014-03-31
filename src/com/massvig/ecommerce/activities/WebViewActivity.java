package com.massvig.ecommerce.activities;

import com.massvig.ecommerce.boqi.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.Button;

public class WebViewActivity extends Activity {

	private WebView mWebView;
	private String url = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web);
		url = this.getIntent().getStringExtra("URL");
		initView();
	}

	private void initView() {

		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		mWebView.loadUrl(url);
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
				WebViewActivity.this.finish();
			}
		});
		
	}

}
