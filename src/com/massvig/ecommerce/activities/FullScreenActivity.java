package com.massvig.ecommerce.activities;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.widgets.NetImageView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class FullScreenActivity extends Activity {

	private NetImageView imageView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full);
		imageView = (NetImageView)findViewById(R.id.image);
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		String imgurl = this.getIntent().getStringExtra("IMAGE");
		imageView.setImageUrl(imgurl, MassVigContants.PATH, null);
	}

}
