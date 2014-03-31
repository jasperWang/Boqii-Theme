package com.massvig.ecommerce.widgets;


import com.massvig.ecommerce.boqi.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * @author zhangbp
 *
 */

public class SeekBar extends RelativeLayout implements OnTouchListener{
	private Button leftBtn,rightBtn,middelBtn;
	private int defaultMinValue = 0,defaultMaxValue = 0;
	private int minValue = 0,maxValue = 0;
	private float x = 0;
	private int movespace = 0;
	private int vleft = 0;
	private int vright = 0;
	private int l = 0;
	private int r = 0;
	private int width = 0;
	private final static int size = 7;
	private float itemScope = 0;
	private int leftIndex = 0,rightIdex = size;
	private SeekBarChange mSeekBarChange;
	
	private String[] data = new String[]{"0","20","50","100","200","500","1000","∞"};
	
	public SeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		leftBtn = (Button)findViewById(R.id.tao_seek_left_btn);
		rightBtn = (Button)findViewById(R.id.tao_seek_Right_btn);
		middelBtn = (Button)findViewById(R.id.tao_seek_middel_btn);
		leftBtn.bringToFront();
		rightBtn.bringToFront();
		leftBtn.setOnTouchListener(this);
		rightBtn.setOnTouchListener(this);
		
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
         if(event.getAction() == MotionEvent.ACTION_DOWN){
        	 	
        		x = event.getRawX();
        		vleft = v.getLeft();
        		vright = v.getRight();
        		width = v.getWidth();
        		
        		if(defaultMaxValue == 0){
        			defaultMinValue = leftBtn.getLeft();
        			defaultMaxValue = rightBtn.getRight();
        			itemScope = (defaultMaxValue - defaultMinValue - width)/size;
        	 	}
        		if(v == leftBtn){
        			minValue = defaultMinValue;
        			maxValue = (int) (rightBtn.getLeft() - itemScope/2);
        		}else{
        			minValue = (int) (leftBtn.getRight() + itemScope/2);
        			maxValue = defaultMaxValue;
        		}
        		
        		
         }else if(event.getAction() == MotionEvent.ACTION_MOVE){
        	 movespace = (int)(event.getRawX() - x);
        	 l = movespace + vleft;
        	 r = movespace + vright;
	 		 
	 		 if(v == leftBtn){
	 			 if(l < minValue){
	 				 l = minValue;
	 				 r = l + width;
	 			 }else if(r > maxValue){
	 				 r = maxValue;
	 				 l = r - width;
	 			 }
	 			v.layout(l, v.getTop(), r, v.getBottom());
		 		middelBtn.layout(l + width/2, middelBtn.getTop(), middelBtn.getRight(), middelBtn.getBottom());
	 			
	 		 }else{
	 			 if(r > maxValue){
	 				 r = maxValue;
	 				 l = r - width;
	 			 }else if(l < minValue){
	 				 l = minValue;
	 				 r = l + width;
	 			 }
	 			v.layout(l, v.getTop(), r, v.getBottom());
 				middelBtn.layout(middelBtn.getLeft(), middelBtn.getTop(), r - width/2, middelBtn.getBottom());
	 			
	 		 }
	 			
         }else if(event.getAction() == MotionEvent.ACTION_UP){
        	float i =  v.getLeft() % itemScope;
        	int m = (int) (i >= itemScope / 2 ? v.getLeft()/itemScope + 1 : v.getLeft()/itemScope);
        	int tl = (int) (m * itemScope  + defaultMinValue);
        	v.layout(tl, v.getTop(), tl + width, v.getBottom());
        	if(v == leftBtn){
		 		middelBtn.layout(tl + width/2, middelBtn.getTop(), middelBtn.getRight(), middelBtn.getBottom());
		 		leftIndex = m;
	 		 }else{
	 			middelBtn.layout(middelBtn.getLeft(), middelBtn.getTop(), tl + width - width/2, middelBtn.getBottom());
	 			rightIdex = m;
	 		 }
        	if(mSeekBarChange != null){
        		mSeekBarChange.callBack(getLeftValue(), getRightValue());
        	}
         }

         return false;
	}
	
	public void SetBarValue(int minValue, int maxValue){
		width = leftBtn.getWidth();
		if(defaultMaxValue == 0){
			defaultMinValue = leftBtn.getLeft();
			defaultMaxValue = rightBtn.getRight();
			itemScope = (defaultMaxValue - defaultMinValue - width)/size;
	 	}
		int lIndex = 0, rIndex = 7;
		for (int i = 0; i < data.length; i++) {
			if(data[i].equals(minValue + "")){
				lIndex = i;
				leftIndex = lIndex;
			}
			if(data[i].equals(maxValue + "")){
				rIndex = i;
				rightIdex = rIndex;
			}
		}
		int t1 = (int) (lIndex * itemScope  + defaultMinValue);
		int t2 = (int) (rIndex * itemScope  + defaultMinValue);
		leftBtn.layout(t1, leftBtn.getTop(), t1 + width, leftBtn.getBottom());
		rightBtn.layout(t2, rightBtn.getTop(), t2 + width, rightBtn.getBottom());
		middelBtn.layout(t1 + width/2, middelBtn.getTop(), t2 + width - width/2, middelBtn.getBottom());
    	if(mSeekBarChange != null){
    		mSeekBarChange.callBack(minValue + "", maxValue + "");
    	}
    	invalidate();
	}
	
	private String getLeftValue(){
		return data[leftIndex];
	}
	
	private String  getRightValue(){
		return data[rightIdex];
	}
	
	public interface SeekBarChange{
		public void callBack(String leftValue,String rightValue);
	}
	public void setSeekBarChangeListener(SeekBarChange mSeekBarChange){
		this.mSeekBarChange = mSeekBarChange;
	}
	
	

}
