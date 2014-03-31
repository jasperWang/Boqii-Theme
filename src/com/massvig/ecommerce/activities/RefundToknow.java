package com.massvig.ecommerce.activities;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.utilities.MassVigContants;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.Toast;

public class RefundToknow extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.toknow);
		setTitle(getString(R.string.refundtoknow));
		((Button) findViewById(R.id.back))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				});
		((TableRow) findViewById(R.id.tele_layout))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(Intent.ACTION_CALL, Uri
								.parse("tel:" + getString(R.string.tele_num))));
					}
				});
		((TableRow) findViewById(R.id.qq_layout))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						final String[] numbers = getResources().getStringArray(
								R.array.qq_numbers);

						Builder builder = new AlertDialog.Builder(
								RefundToknow.this);
						builder.setTitle(getString(R.string.select_telephone))
								.setItems(numbers,
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {

												ClipboardManager cmb = (ClipboardManager)RefundToknow.this.getSystemService(Context.CLIPBOARD_SERVICE);
												cmb.setText(numbers[which].substring(numbers[which].indexOf("-") + 1, numbers[which].length()));
												Toast.makeText(RefundToknow.this, getString(R.string.copy), Toast.LENGTH_SHORT).show();
												String pkg, cls;
												try {
													pkg = "com.tencent.mobileqq";
													cls = "com.tencent.mobileqq.activity.SplashActivity";
													ComponentName componet = new ComponentName(
															pkg, cls);
													Intent i = new Intent();
													i.setComponent(componet);
													startActivity(i);
												} catch (Exception e) {
													try {
														pkg = "com.tencent.minihd.qq";
														cls = "com.tencent.qq.SplashActivity";
														ComponentName componet = new ComponentName(
																pkg, cls);
														Intent i = new Intent();
														i.setComponent(componet);
														startActivity(i);
													} catch (Exception e2) {
														try {
															pkg = "com.tencent.android.pad";
															cls = "com.tencent.android.pad.paranoid.desktop.DesktopActivity";
															ComponentName componet = new ComponentName(
																	pkg, cls);
															Intent i = new Intent();
															i.setComponent(componet);
															startActivity(i);
														} catch (Exception e3) {
														}
													}
												}
											}

										}).create().show();

					}
				});
	}

}
