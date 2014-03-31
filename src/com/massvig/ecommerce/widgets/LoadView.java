package com.massvig.ecommerce.widgets;

import com.massvig.ecommerce.boqi.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LoadView extends FrameLayout{
	private TextView textView,textView1;
	private RelativeLayout relativeLayout;
	private Onload onload;
	public LoadView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public LoadView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
		if(relativeLayout == null){
			init();
		}
	}
	public void showLoading(){
		closeAllView();
		relativeLayout.setVisibility(View.VISIBLE);
	}
	public void showNoData(){
		closeAllView();
		textView1.setVisibility(View.VISIBLE);
	}
	public void showLoad(){
		closeAllView();
		textView.setVisibility(View.VISIBLE);
	}
	private void closeAllView(){
		if(relativeLayout == null){
			init();
		}
		relativeLayout.setVisibility(View.GONE);
		textView.setVisibility(View.GONE);
		textView1.setVisibility(View.GONE);
	}
	
	public void setOnloadCallBack(Onload onload){
		this.onload = onload;
	}
	
	public interface Onload{
		public void callBack();
	}
	
	private void init(){
		textView = (TextView)findViewById(R.id.load_btn_load);
		textView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(onload != null){
					onload.callBack();
					showLoading();
				}
			}
		});
		relativeLayout = (RelativeLayout)findViewById(R.id.load_btn_loading);
		textView1 = (TextView)findViewById(R.id.load_btn_no_data);
	}

	

}
