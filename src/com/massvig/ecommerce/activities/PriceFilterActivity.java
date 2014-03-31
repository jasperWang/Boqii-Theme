package com.massvig.ecommerce.activities;

import java.util.Timer;
import java.util.TimerTask;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.widgets.SeekBar;
import com.massvig.ecommerce.widgets.SeekBar.SeekBarChange;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
public class PriceFilterActivity extends BaseActivity{
	private SeekBar mSeekBar;
	private TextView seekLeftValue,seekRightValue;
	private Button finishButton;
	private MyClick mMyClick;
	private int minPirce = 0,maxPirec = 1000000000;
	private Timer timer;
	private TimerTask task;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.price_filter);
		setTitle(getString(R.string.pricefilter));
		minPirce = this.getIntent().getIntExtra("min", 0);
		maxPirec = this.getIntent().getIntExtra("max", 1000000000);
		mSeekBar = (SeekBar)findViewById(R.id.SeekBar);
		seekLeftValue = (TextView)findViewById(R.id.seek_left_value);
		seekRightValue = (TextView)findViewById(R.id.seek_right_value);
		seekLeftValue.setText(minPirce + "");
		if(maxPirec == 1000000000)
			seekRightValue.setText("∞");
		else
			seekRightValue.setText(maxPirec + "");
		finishButton = (Button)findViewById(R.id.finish);
		mSeekBar.setSeekBarChangeListener(new SeekBarChange() {
			
			@Override
			public void callBack(String leftValue, String rightValue) {
				// TODO Auto-generated method stub
				seekLeftValue.setText(leftValue);
				seekRightValue.setText(rightValue);
				if(rightValue.equals("∞")){
					seekRightValue.setText("∞");
				}
				if(!rightValue.equals("∞") && Integer.valueOf(rightValue) == 1000000000){
					seekRightValue.setText("∞");
				}
				minPirce = Integer.parseInt(leftValue);
				if(!rightValue.equals("∞")){
					maxPirec = Integer.parseInt(rightValue);
				}else{
					maxPirec = 1000000000;
				}
			}
		});
		mMyClick = new MyClick();
		finishButton.setOnClickListener(mMyClick);
		((Button)findViewById(R.id.back)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PriceFilterActivity.this.finish();
			}
		});
		timer = new Timer();
		task = new TimerTask() {
			
			@Override
			public void run() {
				mHandler.sendEmptyMessage(1);
			}
		};
		timer.schedule(task, 300);
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			mSeekBar.SetBarValue(minPirce, maxPirec);
		}};
	
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	class MyClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(v == finishButton){
				PriceFilterActivity.this.setResult(RESULT_OK,new Intent().putExtra("minPirce", minPirce + "").putExtra("maxPirce", maxPirec + ""));
				PriceFilterActivity.this.finish();
			}
		}
		
	}
}
