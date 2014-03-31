package com.massvig.ecommerce.widgets;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
/**
 * 
 * @author DuJun
 *
 */
public class ImageAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<Bitmap> imageList;

	public ImageAdapter(Context c, ArrayList<Bitmap> list) {
		mContext = c;
		imageList = list;
	}

	@Override
	public int getCount() {
		return (imageList.size() == 1) ? imageList.size() : Integer.MAX_VALUE;
	}

	@Override
	public Object getItem(int position) {
		return imageList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView i = new ImageView(mContext);
		if(imageList.size() != 0)
			i.setImageBitmap(imageList.get(position % imageList.size()));
		i.setScaleType(ImageView.ScaleType.FIT_CENTER);
		i.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		i.setBackgroundColor(Color.alpha(1));
		return i;
	}

}
