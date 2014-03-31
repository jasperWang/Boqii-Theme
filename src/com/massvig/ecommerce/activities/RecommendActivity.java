package com.massvig.ecommerce.activities;

import com.massvig.ecommerce.boqi.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class RecommendActivity extends BaseActivity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recommend);
		setTitle(getString(R.string.recommend));
		((Button)findViewById(R.id.back)).setOnClickListener(this);
		((LinearLayout)findViewById(R.id.aimai)).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.aimai:
			downApk();
			break;

		default:
			break;
		}
	}

	/**
	 * 下载apk
	 */
	protected void downApk() {
		//TODO MODITY
		String uri = new String("http://www.noq.cc/aimai.apk");
		Uri u = Uri.parse(uri);
		Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(u);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
	}
}
