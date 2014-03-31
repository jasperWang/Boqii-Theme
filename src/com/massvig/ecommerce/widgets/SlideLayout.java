package com.massvig.ecommerce.widgets;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
/**
 * @author zhangbp
 *
 */
public class SlideLayout extends FrameLayout {
	int leftNavWidth = 0;
	int rightNavWidth = 0;
	View leftNavView,rightNavView,centerView;
	int index,maxIndex;
	String moveType;
	public String moveLeft = "LEFT";
	public String moveRight = "RIGHT";
	Boolean animationComplete = true;
	String _type;
	int baseCcount = 0;
	
	public SlideLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);
		int ccount = getChildCount();
		if(baseCcount == ccount && !changed){
			return;
		}
		baseCcount = ccount;
		int i = 0;
		for (; i < ccount; i++) {
			View childView = getChildAt(i);
			Object tag = childView.getTag();
			if (tag !=null && tag.equals("center")) {
				centerView = childView;
			} else if (tag !=null && tag.equals("left")) {
				leftNavWidth = childView.getWidth();
				leftNavView = childView;
				leftNavView.setVisibility(View.INVISIBLE);
			}else if (tag !=null && tag.equals("right")) {
				rightNavWidth = childView.getWidth();
				rightNavView = childView;
				rightNavView.setVisibility(View.INVISIBLE);
			}
		}
	}

	public void spreadNav(final String _type) {
		
		this._type = _type;
		
		if(!animationComplete){
			return;
		}
		index = centerView.getScrollX();
		
		if(_type.equals(moveLeft)){
			if(index < 0){
				moveType = "plus";
				maxIndex = 0;
			}else{
				moveType = "cut";
				maxIndex = -leftNavWidth;
			}
			leftNavView.setVisibility(View.VISIBLE);
		}else if(_type.equals(moveRight)){
			if(index > 0){
				moveType = "cut";
				maxIndex = 0;
			}else{
				moveType = "plus";
				maxIndex = rightNavWidth;
			}
			rightNavView.setVisibility(View.VISIBLE);
		}else{
			return;
		}
		
		
		
		new Thread(){
			public void run() {
				animationComplete = false;
				while (true) { 
					try {
						
						if(index == maxIndex){
							animationComplete = true;
							Message message=new Message(); 
							message.what = 999999999;
							handler.sendMessage(message);
							break;
						}
						
						Thread.sleep(1);
						int m = Math.abs(maxIndex - index);
						m = m < 2 ? m : 2;
						if(moveType.equals("plus")){
							index += m;
						}else if(moveType.equals("cut")){
							index -= m;
						}
						
						Message message=new Message(); 
						message.what = index;
						handler.sendMessage(message);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			};
		}.start();
		
		

	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.what == 999999999){
				if(index == 0){
					if(_type.equals(moveLeft)){
						leftNavView.setVisibility(View.INVISIBLE);
					}else{
						rightNavView.setVisibility(View.INVISIBLE);
					}
				}
			}else{
				centerView.scrollTo(msg.what, 0);
			}
			
		}
	};

}
