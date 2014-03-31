package com.massvig.ecommerce.widgets;

import java.util.ArrayList;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.Goods;
import com.massvig.ecommerce.entities.GoodsList;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.unionpay.upomp.yidatec.usermanage.getsecuritquestion.GetSecuritQuestionActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * goods列表适配器
 * @author DuJun
 *
 */
public class NewActionAdapter extends BaseAdapter{

	private GoodsList goodsList;
	private LayoutInflater mInflater;
	private ArrayList<NetImageView> imageList = new ArrayList<NetImageView>();
	private Bitmap shopDefaultImg;
	private Context mContext;
	
	public NewActionAdapter(Context context, GoodsList list){
		this.goodsList = list;
		this.mContext = context;
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
			itemView = mInflater.inflate(R.layout.new_action_item, null);
			itemView.setTag(this);
			image = (NetImageView)itemView.findViewById(R.id.image);
			name = (TextView)itemView.findViewById(R.id.name);
			price = (TextView)itemView.findViewById(R.id.price);
			realPrice = (LineTextView)itemView.findViewById(R.id.real_price);
			volume = (TextView)itemView.findViewById(R.id.volume);
		}
		
		public View getView(int position){
			Goods goods = goodsList.getGoods(position);
			if (goods != null) {
				image.setImageUrl(
						MassVigUtil.GetImageUrl(goods.imageUrl,
								MassVigUtil.dip2px(mContext, 133),
								MassVigUtil.dip2px(mContext, 133)),
						MassVigContants.PATH, shopDefaultImg);
				name.setText(goods.name);
				price.setText(goods.minPrice + "");
				realPrice.setText(goods.realPrice + "");
				volume.setText(mContext.getString(R.string.already_buy, goods.volume + ""));
			}
			return itemView;
		}
	}

	public ArrayList<NetImageView> getImageList() {
		return this.imageList;
	}
	
}
