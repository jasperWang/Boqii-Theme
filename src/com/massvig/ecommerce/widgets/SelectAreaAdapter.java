package com.massvig.ecommerce.widgets;

import java.util.ArrayList;

import com.massvig.ecommerce.boqi.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SelectAreaAdapter extends BaseAdapter {

	private ArrayList<String> mList;
	private LayoutInflater mInflater;
	private boolean isShow = true;

	public SelectAreaAdapter(Context c, ArrayList<String> list, boolean b) {
		this.mList = list;
		this.mInflater = LayoutInflater.from(c);
		this.isShow = b;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	class ViewHolder {
		TextView text;
		ImageView image;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.area_item, null);
			holder.text = (TextView) convertView.findViewById(R.id.text);
			holder.image = (ImageView) convertView.findViewById(R.id.img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (!isShow) {
			holder.image.setVisibility(View.GONE);
		} else {
			holder.image.setVisibility(View.VISIBLE);
		}
		holder.text.setText(mList.get(position));
		return convertView;
	}

}
