package com.massvig.ecommerce.activities;

import org.json.JSONObject;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.service.MassVigService;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RefundReasonActivity extends BaseActivity implements OnClickListener{

	private TextView money,refund_type;
	private String type = "";
	private EditText refund_reason;
	private String ids, orderID, orderNO;
	private String totalMoney;
	private BaseApplication app;
	private ProgressDialog dialog;
	private Builder signalDialog;
	private TextView know;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.refund_reason);
		setTitle(getString(R.string.refundreason));
		signalDialog = new AlertDialog.Builder(this);
		dialog = new ProgressDialog(this);
		dialog.setMessage(getString(R.string.wait));
		app = (BaseApplication)getApplication();
		ids = this.getIntent().getStringExtra("IDS");
		orderID = this.getIntent().getStringExtra("ORDERID");
		orderNO = this.getIntent().getStringExtra("ORDERNO");
		totalMoney = this.getIntent().getStringExtra("TOTAL");
		initView();
	}

	private void initView() {
		know = (TextView)findViewById(R.id.know);
		String knowText = getString(R.string.know);
		know.setText(MassVigUtil.setCustomText(knowText, Color.argb(255, 61, 80, 178), knowText.indexOf(getString(R.string.know_left)), knowText.indexOf(getString(R.string.know_right)) + 1, 11));
		money = (TextView)findViewById(R.id.refund_money);
		money.setText(getString(R.string.yuan, totalMoney));
		refund_type = (TextView)findViewById(R.id.refund_type);
		refund_type.setOnClickListener(this);
		refund_reason = (EditText)findViewById(R.id.reason);
		((TextView)findViewById(R.id.know)).setOnClickListener(this);
		((TextView)findViewById(R.id.orderid)).setText(orderNO);
		((Button)findViewById(R.id.back)).setOnClickListener(this);
		((Button)findViewById(R.id.finish)).setOnClickListener(this);
		((Button)findViewById(R.id.confirmRefund)).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.know:
			startActivity(new Intent(this, RefundToknow.class));
			break;
		case R.id.finish:
			final String[] numbers = getResources().getStringArray(R.array.numbers);

			Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.select_telephone))
					.setItems(numbers, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(which == 0){
								startActivity(new Intent(Intent.ACTION_CALL, Uri
										.parse("tel:" + getString(R.string.tele_num))));
							}else{
								ClipboardManager cmb = (ClipboardManager)RefundReasonActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
								cmb.setText(numbers[which].substring(numbers[which].indexOf("-") + 1, numbers[which].length()));
								Toast.makeText(RefundReasonActivity.this, getString(R.string.copy), Toast.LENGTH_SHORT).show();
								String pkg, cls;
						        try {
						            pkg = "com.tencent.mobileqq";
						            cls = "com.tencent.mobileqq.activity.SplashActivity";
						            ComponentName componet = new ComponentName(pkg, cls);
						            Intent i = new Intent();
						            i.setComponent(componet);
						            startActivity(i);
								} catch (Exception e) {
									try {
							            pkg = "com.tencent.minihd.qq";
							            cls = "com.tencent.qq.SplashActivity";
							            ComponentName componet = new ComponentName(pkg, cls);
							            Intent i = new Intent();
							            i.setComponent(componet);
							            startActivity(i);
									} catch (Exception e2) {
										try {
								            pkg = "com.tencent.android.pad";
								            cls = "com.tencent.android.pad.paranoid.desktop.DesktopActivity";
								            ComponentName componet = new ComponentName(pkg, cls);
								            Intent i = new Intent();
								            i.setComponent(componet);
								            startActivity(i);
										} catch (Exception e3) {
										}
									}
								}
							}
						}

					}).create().show();
			break;
		case R.id.confirmRefund:
			new RefundAsync().execute();
			break;
		case R.id.refund_type:
			final String[] items = getResources().getStringArray(R.array.refund_reason);
			signalDialog.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					type = items[which];
					refund_type.setText(type);
					dialog.dismiss();
				}}).show();
			break;

		default:
			break;
		}
	}

	class RefundAsync extends AsyncTask<String, Void, Integer> {

		@Override
		protected void onPreExecute() {
			if (dialog != null && !dialog.isShowing())
				dialog.show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (dialog != null && dialog.isShowing())
				dialog.dismiss();
			// TODO
			if(result == 0){
				//TODO
				setResult(RESULT_OK);
				finish();
			}else if(result == MassVigContants.SESSIONVAILED){
				startActivity(new Intent(RefundReasonActivity.this, LoginActivity.class));
			}
			super.onPostExecute(result);
		}

		@Override
		protected Integer doInBackground(String... params) {
			// TODO
			String result = MassVigService.getInstance().ApplyForRefund(app.user.sessionID, orderID, ids, totalMoney, type + ";" + refund_reason.getText().toString());

			try {
				JSONObject object = new JSONObject(result);
				int resultCode = object.getInt("ResponseStatus");
				if (resultCode == 0) {
					
				}
				return resultCode;
			} catch (Exception e) {
				return -1;
			}
		}

	}

}
