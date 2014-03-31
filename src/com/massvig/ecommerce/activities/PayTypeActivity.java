package com.massvig.ecommerce.activities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.massvig.ecommerce.boqi.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PayTypeActivity extends BaseActivity implements OnClickListener {

	private int payType = 2;
	public static final int ALIPAY = 3;
	public static final int UNION_PAY = 2;
	public static final int ALIWEB = 1;
	public static final int HUODAO = 4;
	private TextView icon1, icon2, icon3, icon4;
	private RelativeLayout layout1, layout2, layout3, layout4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.paytype);
		setTitle(getString(R.string.paytype));
		icon1 = (TextView) findViewById(R.id.check1);
		icon2 = (TextView) findViewById(R.id.check2);
		icon3 = (TextView) findViewById(R.id.check3);
		icon4 = (TextView) findViewById(R.id.check4);
		((Button) findViewById(R.id.back)).setOnClickListener(this);
		layout1 = (RelativeLayout) findViewById(R.id.layout1);
		layout2 = (RelativeLayout) findViewById(R.id.layout2);
		layout3 = (RelativeLayout) findViewById(R.id.layout3);
		layout4 = (RelativeLayout) findViewById(R.id.layout4);
		layout1.setOnClickListener(this);
		layout2.setOnClickListener(this);
		layout3.setOnClickListener(this);
		layout4.setOnClickListener(this);
		payType = this.getIntent().getIntExtra("PAYTYPE", 2);
		initIcon();
		String payment = this.getIntent().getStringExtra("PAYMENT");
		initView(payment);
	}

	private void initView(String payment) {
		if (!TextUtils.isEmpty(payment) && !payment.equals("null")) {
			try {
				JSONObject o = new JSONObject(payment);
				int res = o.getInt("ResponseStatus");
				if (res == 0) {
					JSONArray array = o.getJSONArray("ResponseData");
					for (int i = 0; i < array.length(); i++) {
						JSONObject data = array.getJSONObject(i);
						int paytype = data.getInt("PaymentMethod");
						switch (paytype) {
						case ALIPAY:
							layout2.setVisibility(View.VISIBLE);
							break;
						case UNION_PAY:
							layout1.setVisibility(View.VISIBLE);
							break;
						case ALIWEB:
							layout3.setVisibility(View.VISIBLE);
							break;
						case HUODAO:
							layout4.setVisibility(View.VISIBLE);
							break;

						default:
							break;
						}
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
	}

	private void initIcon() {
		clear();
		switch (payType) {
		case 1:
			icon2.setVisibility(View.VISIBLE);
			break;
		case 2:
			icon1.setVisibility(View.VISIBLE);
			break;
		case 3:
			icon3.setVisibility(View.VISIBLE);
			break;
		case 4:
			icon4.setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		clear();
		switch (v.getId()) {
		case R.id.back:
			PayTypeActivity.this.finish();
			return;
		case R.id.layout1:
			payType = UNION_PAY;
			icon1.setVisibility(View.VISIBLE);
			break;
		case R.id.layout2:
			payType = ALIPAY;
			icon2.setVisibility(View.VISIBLE);
			break;
		case R.id.layout3:
			payType = ALIWEB;
			icon3.setVisibility(View.VISIBLE);
			break;
		case R.id.layout4:
			payType = HUODAO;
			icon4.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
		intent.putExtra("PAYTYPE", payType);
		setResult(RESULT_OK, intent);
		PayTypeActivity.this.finish();
	}

	private void clear() {
		icon1.setVisibility(View.GONE);
		icon2.setVisibility(View.GONE);
		icon3.setVisibility(View.GONE);
		icon4.setVisibility(View.GONE);
	}

}
