package com.massvig.ecommerce.widgets;

import java.util.ArrayList;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.Goods;
import com.massvig.ecommerce.entities.GoodsList;
import com.massvig.ecommerce.utilities.MassVigContants;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * goods列表适配器
 * @author DuJun
 *
 */
public class CommunityAdapter extends BaseAdapter{

	private GoodsList goodsList;
	private LayoutInflater mInflater;
	private ArrayList<NetImageView> imageList = new ArrayList<NetImageView>();
	private Bitmap shopDefaultImg;
	
	public CommunityAdapter(Context context, GoodsList list){
		this.goodsList = list;
		mInflater = LayoutInflater.from(context);
		shopDefaultImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_icon);
	}
	
	public void setDataList(GoodsList list){
		this.goodsList = list;
	}
	
	@Override
	public int getCount() {
		return goodsList.getCount();
	}

	@Override
	public Object getItem(int position) {
		return goodsList.getGoods(position);
	}

	@Override
	public long getItemId(int position) {
		return Long.valueOf(goodsList.getGoodsID(position));
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
		private TextView name,price,volume;
		private LineTextView realPrice;
		private View itemView;
		
		public ViewHolder(){
			itemView = mInflater.inflate(R.layout.goods_item, null);
			itemView.setTag(this);
			image = (NetImageView)itemView.findViewById(R.id.image);
			name = (TextView)itemView.findViewById(R.id.name);
			price = (TextView)itemView.findViewById(R.id.price);
			realPrice = (LineTextView)itemView.findViewById(R.id.real_price);
			volume = (TextView)itemView.findViewById(R.id.volume);
		}
		
		public View getView(int position){
			Goods goods = goodsList.getGoods(position);
			image.setImageUrl(goods.imageUrl, MassVigContants.PATH, shopDefaultImg);
			name.setText(goods.name);
			price.setText(goods.minPrice + "");
			realPrice.setText(goods.realPrice + "");
			volume.setText(goods.volume + "");
			return itemView;
		}
	}

	public ArrayList<NetImageView> getImageList() {
		// TODO Auto-generated method stub
		return this.imageList;
	}
	
}
