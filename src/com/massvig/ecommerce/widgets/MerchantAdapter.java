package com.massvig.ecommerce.widgets;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.Merchant;
import com.massvig.ecommerce.entities.MerchantList;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 附近商家列表适配器
 * @author DuJun
 *
 */
public class MerchantAdapter extends BaseAdapter{

	private MerchantList merchantList;
	private LayoutInflater mInflater;
	private Context mContext;
	
	public MerchantAdapter(Context context, MerchantList list){
		this.merchantList = list;
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
	}
	
	public void setDataList(MerchantList list){
		this.merchantList = list;
	}
	
	@Override
	public int getCount() {
		return merchantList.getCount();
	}

	@Override
	public Object getItem(int position) {
		return merchantList.getMerchant(position);
	}

	@Override
	public long getItemId(int position) {
		return Long.valueOf(merchantList.getMerchantID(position));
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		convertView = holder.getView(position);
		return convertView;
	}
	
	class ViewHolder{
		private TextView color,name,description,distance;
		private View itemView;
		
		public ViewHolder(){
			itemView = mInflater.inflate(R.layout.merchant_item, null);
			itemView.setTag(this);
			color = (TextView)itemView.findViewById(R.id.color);
			name = (TextView)itemView.findViewById(R.id.storename);
			description = (TextView)itemView.findViewById(R.id.description);
			distance = (TextView)itemView.findViewById(R.id.distance);
		}
		
		public View getView(int position){
			Merchant merchant = merchantList.getMerchant(position);
			name.setText(merchant.StoreName);
			if(position % 3 == 0){
				color.setBackgroundColor(Color.RED);
			}else if(position % 3 == 1){
				color.setBackgroundColor(Color.GREEN);
			}else{
				color.setBackgroundColor(Color.GRAY);
			}
			description.setText("");
			if(!TextUtils.isEmpty(merchant.Description) && !merchant.Description.equals("null"))
				description.setText(merchant.Description);
			distance.setText(mContext.getString(R.string.distance, ((int)(Double.valueOf((((int)merchant.Distance) / 1000.0) + "") * 10)) / 10.0 + ""));
			return itemView;
		}
	}

}
