package com.massvig.ecommerce.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class ContentView extends FrameLayout{
	private ContentViewClickCallBack contentViewClickCallBack;
	public ContentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if(((View) getParent()).getScrollX() != 0 && this.contentViewClickCallBack != null){
			contentViewClickCallBack.callBack();
			return false;
		 }
		return super.dispatchTouchEvent(ev);
	}
	
	public interface ContentViewClickCallBack{
		public void callBack();
	}
	
	public void setContentViewClickCallBack(ContentViewClickCallBack contentViewClickCallBack){
		this.contentViewClickCallBack = contentViewClickCallBack;
	}
	

	

}
