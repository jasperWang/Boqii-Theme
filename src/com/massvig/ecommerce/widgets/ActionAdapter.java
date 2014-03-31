package com.massvig.ecommerce.widgets;

import java.util.ArrayList;

import com.massvig.ecommerce.entities.Action;
import com.massvig.ecommerce.entities.ActionsList;
import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * action列表适配器
 * @author DuJun
 *
 */
public class ActionAdapter extends BaseAdapter{

	private ActionsList actionsList;
	private LayoutInflater mInflater;
	private ArrayList<NetImageView> imageList = new ArrayList<NetImageView>();
	private Bitmap shopDefaultImg;
	
	public ActionAdapter(Context context, ActionsList list){
		this.actionsList = list;
		mInflater = LayoutInflater.from(context);
		shopDefaultImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_icon);
	}
	
	public void setDataList(ActionsList list){
		this.actionsList = list;
	}
	
	@Override
	public int getCount() {
		return actionsList.getCount();
	}

	@Override
	public Object getItem(int position) {
		return actionsList.getAction(position);
	}

	@Override
	public long getItemId(int position) {
		return Long.valueOf(actionsList.getActionID(position));
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			imageList.add(holder.image);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		convertView = holder.getView(position);
		return convertView;
	}
	
	class ViewHolder{
		private NetImageView image;
		private TextView title, detail;
		private View itemView;
		
		public ViewHolder(){
			itemView = mInflater.inflate(R.layout.action_item, null);
			itemView.setTag(this);
			image = (NetImageView)itemView.findViewById(R.id.actionimg);
			title = (TextView)itemView.findViewById(R.id.title);
			detail = (TextView)itemView.findViewById(R.id.detail);
		}
		
		public View getView(int position){
			Action action = actionsList.getAction(position);
			image.setImageUrl(MassVigUtil.GetImageUrl(action.imgUrl, 64, 64), MassVigContants.PATH, shopDefaultImg);
			title.setText(action.title);
			detail.setText(action.detail);
			return itemView;
		}
	}

	public ArrayList<NetImageView> getImageList() {
		// TODO Auto-generated method stub
		return this.imageList;
	}
	
}
