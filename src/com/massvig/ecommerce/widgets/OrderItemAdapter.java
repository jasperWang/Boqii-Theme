package com.massvig.ecommerce.widgets;

import java.util.ArrayList;
import java.util.HashMap;

import com.massvig.ecommerce.entities.Goods;
import com.massvig.ecommerce.entities.Product;
import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 订单详情列表适配器
 * @author DuJun
 *
 */
public class OrderItemAdapter extends BaseAdapter{

	private ArrayList<Product> ps;
	private LayoutInflater mInflater;
	private ArrayList<NetImageView> imageList = new ArrayList<NetImageView>();
	private Bitmap shopDefaultImg;
	private Context mContext;
	private ClickListener listener;
	private boolean isWebOrder;
	
	public OrderItemAdapter(Context context, ArrayList<Product> p, boolean isWebOrder){
		this.ps = p;
		this.isWebOrder = isWebOrder;
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
		shopDefaultImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_icon);
	}
	
	public void setListener(ClickListener l){
		this.listener = l;
	}
	
	public void setDataList(ArrayList<Product> list){
		this.ps = list;
	}
	
	@Override
	public int getCount() {
		return ps.size();
	}

	@Override
	public Object getItem(int position) {
		return ps.get(position);
	}

	@Override
	public long getItemId(int position) {
		return Long.valueOf(ps.get(position).id);
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
		private TextView name, detail, price;
		private View itemView;
		
		public ViewHolder(){
			itemView = mInflater.inflate(R.layout.order_item_layout, null);
			itemView.setTag(this);
			image = (NetImageView)itemView.findViewById(R.id.imageview);
			name = (TextView)itemView.findViewById(R.id.name);
			detail = (TextView)itemView.findViewById(R.id.detail);
			price = (TextView)itemView.findViewById(R.id.price);
		}
		
		public View getView(int position){
			final Product p = ps.get(position);
			if(!isWebOrder)
				image.setImageUrl(MassVigUtil.GetImageUrl(p.imageUrl, 64, 64), MassVigContants.PATH, shopDefaultImg);
			else
				image.setImageUrl(p.imageUrl, MassVigContants.PATH, shopDefaultImg);
			image.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(listener != null){
						Goods good = new Goods();
						good.productID = p.id;
						good.imageUrl = p.imageUrl;
						good.minPrice = p.signalPrice;
						good.name = p.name;
						listener.clickImg(good);
					}
				}
			});
			name.setText(p.name);
			price.setText(mContext.getString(R.string.money) + p.signalPrice);
			HashMap<String, String> map = p.selectedProperties;
			String detailText = "";
			if(map != null && map.size() > 0){
				for (String key : map.keySet()) {
					detailText += key + ":" + map.get(key) + "\n";
				}
			}
			if(p.IsGift){
				detailText += "赠品x1";
			}else{
				detailText += mContext.getString(R.string.number) + p.count;
			}
			detail.setText(detailText);
			return itemView;
		}
	}

	public ArrayList<NetImageView> getImageList() {
		// TODO Auto-generated method stub
		return this.imageList;
	}
	
	public interface ClickListener{
		void clickImg(Goods good);
	}
	
}
