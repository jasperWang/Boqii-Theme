package com.massvig.ecommerce.activities;

import java.util.ArrayList;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.Goods;
import com.massvig.ecommerce.entities.Product;
import com.massvig.ecommerce.entities.ProductList;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.widgets.LineTextView;
import com.massvig.ecommerce.widgets.NetImageView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class InvalidProductsActivity extends BaseActivity implements OnClickListener{

	private ProductList list = new ProductList();
	private ListView listview;
	private GoodsAdapter adapter;
	private int valid = 1;
	private int xiajia = 2;
	private int kucun = 3;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invalid_product);
		setTitle(getString(R.string.invalidproducts));
		list = (ProductList)this.getIntent().getSerializableExtra("Product");
		initView();
	}
	private void initView() {
		listview = (ListView)findViewById(R.id.listview);
		((Button)findViewById(R.id.back)).setOnClickListener(this);
		adapter = new GoodsAdapter(this);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(list.getProduct(position).ShoppingCartProductStatus == xiajia){
					
				}else{
					Goods good = new Goods();
					Product p = list.getProduct(position);
					if (p != null) {
						good.productID = p.id;
						good.imageUrl = p.imageUrl;
						good.minPrice = p.signalPrice;
						good.name = p.name;
						good.realPrice = p.oriPrice;
						startActivity(new Intent(InvalidProductsActivity.this,
								GoodsDetailActivity.class).putExtra("goods",
								good));
					}
				}
			}
		});
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;

		default:
			break;
		}
	}

	public class GoodsAdapter extends BaseAdapter{

		private LayoutInflater mInflater;
		private ArrayList<NetImageView> imageList = new ArrayList<NetImageView>();
		private Bitmap shopDefaultImg;
		private Context mContext;
		
		public GoodsAdapter(Context context){
			this.mContext = context;
			mInflater = LayoutInflater.from(context);
			shopDefaultImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_icon);
		}
		
		@Override
		public int getCount() {
			return list.getCount();
		}

		@Override
		public Object getItem(int position) {
			return list.getProduct(position);
		}

		@Override
		public long getItemId(int position) {
			return Long.valueOf(list.getProductID(position));
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
			private TextView name,price,text,infor;
			private LineTextView realPrice;
			private LinearLayout priceLayout;
			private View itemView;
			private TextView commentCount;
			
			public ViewHolder(){
				itemView = mInflater.inflate(R.layout.invalid_item, null);
				itemView.setTag(this);
				commentCount = (TextView)itemView.findViewById(R.id.commentCount);
				image = (NetImageView)itemView.findViewById(R.id.image);
				name = (TextView)itemView.findViewById(R.id.name);
				price = (TextView)itemView.findViewById(R.id.price);
				realPrice = (LineTextView)itemView.findViewById(R.id.real_price);
				text = (TextView)itemView.findViewById(R.id.text);
				infor = (TextView)itemView.findViewById(R.id.infor);
				priceLayout = (LinearLayout)itemView.findViewById(R.id.price_layout);
			}
			
			public View getView(int position){
				Product p = list.getProduct(position);
				if (p != null) {
					image.setImageUrl(
							MassVigUtil.GetImageUrl(p.imageUrl,
									MassVigUtil.dip2px(mContext, 95),
									MassVigUtil.dip2px(mContext, 95)),
							MassVigContants.PATH, shopDefaultImg);
					name.setText(p.name);
//					commentCount.setText(""+.commentCount);
					if(p.ShoppingCartProductStatus == xiajia){
						priceLayout.setVisibility(View.GONE);
						text.setVisibility(View.VISIBLE);
						infor.setText(getString(R.string.status_invalid));
					}else{
						priceLayout.setVisibility(View.VISIBLE);
						text.setVisibility(View.GONE);
						infor.setText(getString(R.string.reselect));
						price.setText(p.signalPrice + "");
						realPrice.setText(p.oriPrice + "");	
					}
				}
				return itemView;
			}
		}

		public ArrayList<NetImageView> getImageList() {
			// TODO Auto-generated method stub
			return this.imageList;
		}
		
	}
}
