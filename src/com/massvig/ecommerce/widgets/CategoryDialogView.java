package com.massvig.ecommerce.widgets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.massvig.ecommerce.boqi.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * @author zhangbp
 * 
 */


public class CategoryDialogView extends RelativeLayout {
	private LinearLayout linearLayout1, linearLayout2, linearLayout3;
	private JSONArray cateGoryData;
	private Context context;
	private LayoutInflater mLayoutInflater;
	private CateGoryDialogCallBack callBack = null;

	public CategoryDialogView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		bringToFront();
		closeDiaLog();
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		closeDiaLog();
		return true;
	}

	public void execute(CateGoryDialogCallBack callBack) {
		this.callBack = callBack;
		mLayoutInflater = LayoutInflater.from(context);
		linearLayout1 = (LinearLayout) findViewById(R.id.linear_layout1);
		linearLayout2 = (LinearLayout) findViewById(R.id.linear_layout2);
		linearLayout3 = (LinearLayout) findViewById(R.id.linear_layout3);
		int width = ((Activity)context).getWindowManager().getDefaultDisplay().getWidth();  
		linearLayout1.getLayoutParams().width = width/2;
		linearLayout2.getLayoutParams().width = width/2;
		linearLayout3.getLayoutParams().width = width/2;
		
		
		linearLayout2.bringToFront();
		cateGoryData = getCategory();
		createCategoryBaseView();
		createOrderView();
	}

	public void createCategoryBaseView() {
		linearLayout2.getChildAt(0).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				closeDiaLog();
			}
		});
		View childview1 = linearLayout2.getChildAt(1);
		childview1.setOnClickListener(new CategoryOnclick(-1,-1, -1, false, context.getString(R.string.all)));
		
		if (cateGoryData != null) {
			if(linearLayout2.getChildCount() > 2){
				linearLayout2.removeViews(2, linearLayout2.getChildCount() - 2);
			}
			for (int i = 0, len = cateGoryData.length(); i < len; i++) {
				createBaseItemView(i);
			}
		}
	}

	private void createBaseItemView(int i) {
		try {
			JSONObject json = cateGoryData.getJSONObject(i);
			mLayoutInflater.inflate(R.layout.category_dialog_view_item,
					linearLayout2);
			LinearLayout view = (LinearLayout) linearLayout2
					.getChildAt(linearLayout2.getChildCount() - 1);
			
//			setImage("http://img.ibuy001.com/pictures/20129/26/4112302_ismall.jpg",
//					(ImageView) view.findViewById(R.id.img), 
//					this, 0);
			
			((TextView) view.findViewById(R.id.txt)).setText(json
					.getString("Name"));

			((TextView) view.findViewById(R.id.count)).setText(json.getInt("ProductCount") + "");
			
			if (json.getJSONArray("Children").length() > 0) {
				ImageView img = ((ImageView) view.findViewById(R.id.img2));
				img.setBackgroundResource(R.drawable.category_dialog_view_icon);
				img.setVisibility(GONE);
			}
			view.setOnClickListener(new CategoryOnclick(i,-1, json
						.getInt("CategoryID"), (json.getJSONArray("Children").length() > 0),
						json.getString("Name")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createCategoryChildView(int index) {
		JSONObject json;
		TextView view;
		try {
			JSONArray jona = cateGoryData.getJSONObject(index).getJSONArray("Children");
			linearLayout1.removeAllViews();
			mLayoutInflater.inflate(R.layout.category_dialog_view_item2,
					linearLayout1);
			LinearLayout l = (LinearLayout) linearLayout1.getChildAt(linearLayout1.getChildCount() - 1);
			view = (TextView) l.findViewById(R.id.name);
			view.setText(R.string.all);
			
			for (int i = 0, len = jona.length(); i < len; i++) {
				json = jona.getJSONObject(i);
				
				if(i == 0){
					view.setOnClickListener(new CategoryOnclick(-1, json.getInt("ParentID"), -1, false, cateGoryData.getJSONObject(index).getString("Name")));
				}
				
				mLayoutInflater.inflate(R.layout.category_dialog_view_item2,
						linearLayout1);
//				view = (TextView) linearLayout1.getChildAt(linearLayout1
//						.getChildCount() - 1);
				LinearLayout l1 = (LinearLayout) linearLayout1.getChildAt(linearLayout1.getChildCount() - 1);
				TextView count = (TextView)l1.findViewById(R.id.count);
				count.setText(json.getInt("ProductCount") + "");
				view = (TextView) l1.findViewById(R.id.name);
				view.setText(json.getString("Name"));
				view.setOnClickListener(new CategoryOnclick(i,json
						.getInt("ParentID"), json
						.getInt("CategoryID"), false,
						json.getString("Name")));
			}
			linearLayout1.setVisibility(VISIBLE);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void createOrderView() {
		LinearLayout llayout;
		TextView textv;
		int orderid = 0;
		linearLayout3.getChildAt(0).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				closeDiaLog();
			}
		});
		
		for (int i = 1, len = linearLayout3.getChildCount(); i < len; i++) {
			llayout = (LinearLayout) linearLayout3.getChildAt(i);
			textv = (TextView) llayout.findViewById(R.id.txt);
			orderid = textv.getTag() != null ? Integer.parseInt(textv.getTag().toString()) : 0;
			llayout.setOnClickListener(new orderOnclick(orderid, ((ImageView) llayout
					.findViewById(R.id.img)).getBackground(), textv.getText()
					.toString()));
		}
		
	}

	private JSONArray getCategory() {
		JSONArray cate1 = new JSONArray();
		JSONArray cate2 = new JSONArray();
		
		JSONArray allcategory = MassVigData.getinstance(context).getAllCategory();
		JSONObject item;
		for(int i = 0,len = allcategory.length();i < len ;i++){
			try{
				item = allcategory.getJSONObject(i);
				if(item.getJSONArray("Children").length() > 0){
//				if(item.getBoolean("IsParent")){
					cate1.put(item);
				}else{
					cate2.put(item);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		try {
			return new JSONArray((cate1.toString() + cate2.toString()).replace("][",","));
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return null;
	}

	public void showDialog(int dailogType) {
		setVisibility(VISIBLE);
		linearLayout1.setVisibility(GONE);
		linearLayout2.setVisibility(GONE);
		linearLayout3.setVisibility(GONE);

		if (dailogType == 1) {
			linearLayout2.setVisibility(VISIBLE);
		} else if (dailogType == 2) {
			linearLayout2.setVisibility(INVISIBLE);
			linearLayout3.setVisibility(VISIBLE);
		}

	}

	public void closeDiaLog() {
		setVisibility(GONE);
	}

	
	public void setCateGoryTopView(int parentId,String name,ImageView img){
		
		View view = linearLayout2.getChildAt(0);
		((TextView) view.findViewById(R.id.txt)).setText(name);
		
		if(cateGoryData != null){
			JSONObject json;
			try{
				for(int i = 0,len = cateGoryData.length();i < len;i++){
					json = cateGoryData.getJSONObject(i);
					ImageView img1 = ((ImageView) view.findViewById(R.id.img));
					if(json.getInt("CustomCategoryID") == parentId){
						setImage("http://img.ibuy001.com/pictures/20129/26/4112302_ismall.jpg",
								img1, 
								this, 0);
						setImage("http://img.ibuy001.com/pictures/20129/26/4112302_ismall.jpg",
								img, 
								this, 0);
//						setImage(GroupSaleService.getInstance().getUrl()+ "/pictures/"+ json.getString("CategoryImageUrl").replace(".png", "_small_android.png"),
//								img1, 
//								this, 0);
//						setImage(GroupSaleService.getInstance().getUrl()+ "/pictures/"+ json.getString("CategoryImageUrl").replace(".png", "_small_android.png"),
//								img, 
//								this, 0);
						
						break;
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
	}

	
	class CategoryOnclick implements OnClickListener {
		private int index;
		private Boolean isParent;
		private int parentId = -1, childId = -1;
//		private ImageView leftIcon;
		private String name;

		public CategoryOnclick(int index,int parentId, int childId,
				Boolean isParent, String name) {
			this.index = index;
			this.isParent = isParent;
			this.parentId = parentId;
			this.childId = childId;
//			this.leftIcon = leftIcon;
			this.name = name;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			if (parentId == -1) {
				linearLayout1.setVisibility(INVISIBLE);
				if (isParent) {
					createCategoryChildView(index);
				}
				setState(isParent);
			}
			if (callBack != null && !isParent) {
				View view = linearLayout2.getChildAt(0);
//				((ImageView) view.findViewById(R.id.img)).setBackgroundDrawable(leftIcon.getBackground());
				((TextView) view.findViewById(R.id.txt)).setText(name);
				
				callBack.cateGorycallBack(parentId, childId,
						name);
				setState(false);
				closeDiaLog();
			}
		}

		private void setState(Boolean isParent) {
			for (int i = 1, len = linearLayout2.getChildCount() - 1; i < len; i++) {
//				vi.setBackgroundResource(R.drawable.cate_dialog_item_bg2);
			}
			if (isParent) {
//				v.setBackgroundResource(R.drawable.category_dialog_view_item_bg1);
			}
		}

	}

	
	// 图片加载
		public void setImage(String url, View img,View parentView, int type) {
			img.setTag(url);
			url = url.replaceAll("\\\\", "/");
			String name = url.substring(url.lastIndexOf("/") + 1,url.length()).replace(".png", "");
			try {
				Bitmap bitmap = null;
				int id = getResources().getIdentifier("i"+name, "raw", context.getPackageName());
				if(id != 0){
					Options opts = new Options();
					opts.inPreferredConfig = Bitmap.Config.ARGB_4444;
					opts.inSampleSize = 2;
					bitmap = BitmapFactory.decodeResource(getResources(), id, opts);
//					bitmap = BitmapFactory.decodeResource(getResources(), id);
				}else{
					bitmap = AsyncImageLoader.loadBitmap(url, new ImageCallback(url,
							parentView, type));
				}
				if (bitmap != null) {
					if (type == 0) {
						img.setBackgroundDrawable(new BitmapDrawable(bitmap));
						
					} else if (type == 1) {
						((ImageView) img).setImageBitmap(bitmap);
					}
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private class ImageCallback implements AsyncImageLoader.ImageCallback {
			String httpurl;
			View parentViews;
			int type = 0;

			public ImageCallback(String url, View parentView, int type) {
				this.httpurl = url;
				this.parentViews = parentView;
				this.type = type;
			}

			public void imageLoaded(Bitmap imageBitmap, String imageUrl) {
				if (imageBitmap != null) {
					View view = parentViews.findViewWithTag(httpurl);
					if (view != null) {
						if (type == 0) {
							view.setBackgroundDrawable(new BitmapDrawable(
									imageBitmap));
						} else if (type == 1) {
							((ImageView) view).setImageBitmap(imageBitmap);
						}

					}
				}

			}

		}
		
		// 图片加载-end
		
	class orderOnclick implements OnClickListener {
		private int orderId;
		private Drawable drawable;
		private String name;
		public orderOnclick(int orderId, Drawable drawable, String name) {
			this.orderId = orderId;
			this.drawable = drawable;
			this.name = name;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			LinearLayout llayout = (LinearLayout) linearLayout3.getChildAt(0);
			TextView textv = (TextView) llayout.findViewById(R.id.txt);
			textv.setText(name);
			textv.setTag(String.valueOf(orderId));
			((ImageView) llayout.findViewById(R.id.img)).setBackgroundDrawable(drawable);
			
			callBack.orderCallBack(orderId, drawable, name);
			closeDiaLog();
		}
	}

	public interface CateGoryDialogCallBack {
		public void cateGorycallBack(int parentId, int childId, String name);

		public void orderCallBack(int orderId, Drawable leftDrawable, String name);
	}

}
