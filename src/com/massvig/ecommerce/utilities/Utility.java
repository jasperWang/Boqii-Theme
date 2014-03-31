package com.massvig.ecommerce.utilities;

import com.massvig.ecommerce.widgets.ADAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class Utility {

	public void setGridView1HeightBasedOnChildren(GridView gridView) {

		// 获取ListView对应的Adapter

		BaseAdapter listAdapter = (BaseAdapter) gridView.getAdapter();

		if (listAdapter == null) {

			return;

		}

		int totalHeight = 0;

		for (int i = 0, len = 2; i < len; i++) { // listAdapter.getCount()返回数据项的数目

			View listItem = listAdapter.getView(i, null, gridView);

			listItem.measure(0, 0); // 计算子项View 的宽高

			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度

		}

		ViewGroup.LayoutParams params = gridView.getLayoutParams();

		params.height = totalHeight;

		// listView.getDividerHeight()获取子项间分隔符占用的高度

		// params.height最后得到整个ListView完整显示需要的高度

		gridView.setLayoutParams(params);

	}

	public void setListViewHeightBasedOnChildren(Context context, ListView listView, ADAdapter adapter) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = adapter;
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + MassVigUtil.dip2px(context, 10) +
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	public void setListViewHeightBasedOnChildren2(Context context, ListView listView, ListAdapter listAdapter) {
		// 获取ListView对应的Adapter
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + 
//				MassVigUtil.dip2px(context, 10) +
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	public void setGridView2HeightBasedOnChildren(GridView gridView, int height) {

		// 获取ListView对应的Adapter

		BaseAdapter listAdapter = (BaseAdapter) gridView.getAdapter();

		if (listAdapter == null) {

			return;

		}

		int totalHeight = 0;
		int len = gridView.getAdapter().getCount() / 2;
		
		for (int i = 0; i < len; i++) { // listAdapter.getCount()返回数据项的数目

//			View listItem = listAdapter.getView(i, null, gridView);
//			listItem.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//			listItem.measure(0, 0); // 计算子项View 的宽高

//			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
			totalHeight += height;

		}

		ViewGroup.LayoutParams params = gridView.getLayoutParams();

		params.height = totalHeight;

		// listView.getDividerHeight()获取子项间分隔符占用的高度

		// params.height最后得到整个ListView完整显示需要的高度

		gridView.setLayoutParams(params);

	}

}
