package com.massvig.ecommerce.widgets;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * 自定义按钮（带点击效果）
 * @author DuJun
 *
 */
public class FilterButton extends LinearLayout{
	
	PorterDuffColorFilter porter = new PorterDuffColorFilter(Color.parseColor("#ff999999"), PorterDuff.Mode.MULTIPLY);
	public FilterButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setClickable(true);
		
		Drawable drawable = getBackground();
		if(drawable != null){
			super.setBackgroundDrawable(getStateListDrawable(drawable));
			drawable = null;
		}
		
	}
	
	@Override
	public void setBackgroundDrawable(Drawable d) {
		// TODO Auto-generated method stub
		super.setBackgroundDrawable(getStateListDrawable(d));
	}

	public StateListDrawable getStateListDrawable(Drawable drawable) {
        StateListDrawable bg = new StateListDrawable();
        Drawable normal = drawable;
        drawable.setColorFilter(porter);
        Bitmap bitmap = drawableToBitmap(drawable);
        BitmapDrawable bd= new BitmapDrawable(bitmap); 
        bg.addState(View.PRESSED_ENABLED_STATE_SET,bd);
        bg.addState(View.EMPTY_STATE_SET, normal);
        return bg;
    }
	
	
    public static Bitmap drawableToBitmap(Drawable drawable) {  
        int w = drawable.getIntrinsicWidth();  
        int h = drawable.getIntrinsicHeight();  
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888  : Bitmap.Config.RGB_565;  
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);  
        Canvas canvas = new Canvas(bitmap);  
        drawable.setBounds(0, 0, w, h);  
        drawable.draw(canvas);  
        return bitmap;  
    }  
    
	
	
	

}
