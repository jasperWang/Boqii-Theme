package com.massvig.ecommerce.widgets;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 自定义通用Adapter
 * @author zhangbp
 * 
 */
public abstract class CommonAdapter<T> extends BaseAdapter{
	public List<T> mListData = new ArrayList<T>();
	private Context mContext;
	private int itemLayoutId;
	public CommonAdapter(Context mContext,int itemLayoutId){
		this.mContext = mContext;
		this.itemLayoutId = itemLayoutId;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mListData.size();
	}

	@Override
	public T getItem(int position) {
		// TODO Auto-generated method stub
		return mListData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		  ViewHolderModel mHolder = null;
		  if (convertView == null) {
	            convertView = LayoutInflater.from(mContext).inflate(itemLayoutId,null);
	            try {
					mHolder = getViewHolderModel();
					mHolder.initViewHoler(convertView);
		            convertView.setTag(mHolder);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            
	        } else {
	        	mHolder = (ViewHolderModel) convertView.getTag();
	        }
		  if(mHolder != null){
			  T itemData = getItem(position);
			  mHolder.setViewHolerValues(convertView, position, itemData);
		  }
	      return convertView;
	}
	
	/**
	 * 添加数据
	 * @author zhangbp
	 */
	public void appendData(List<T> list){
		if (list!= null && list.size() > 0) {
			mListData.addAll(list);
			notifyDataSetChanged();
		}
	}
	/**
	 * 添加单条数据
	 * @author zhangbp
	 */
	public void appendData(T itemData){
		mListData.add(itemData);
		notifyDataSetChanged();
	}
	/**
	 * 移除单条数据
	 * @author zhangbp
	 */
	public void removeData(int index){
		mListData.remove(index);
		notifyDataSetChanged();
	}
	/**
	 * 清除所有数据
	 * @author zhangbp
	 */
	public void clearData(int index){
		mListData.clear();
		notifyDataSetChanged();
	}

	public abstract ViewHolderModel getViewHolderModel();
}
