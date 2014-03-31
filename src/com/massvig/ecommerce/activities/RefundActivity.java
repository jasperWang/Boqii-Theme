package com.massvig.ecommerce.activities;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.Product;
import com.massvig.ecommerce.entities.ProductList;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.widgets.NetImageView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface; 
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class RefundActivity extends BaseActivity implements OnClickListener {

	private ProgressDialog dialog;
	private ListView listview;
	private ProductList productList = new ProductList();
	private LayoutInflater mInflater;
	private Bitmap shopDefaultImg;
	private EditText totalMoney;
	private CheckBox check;
	private ShoppingAdapter adapter;
	private String orderID;
	private String orderNO;
	private double freight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.refund);
		setTitle(getString(R.string.refund));
		mInflater = LayoutInflater.from(this);
		shopDefaultImg = BitmapFactory.decodeResource(getResources(), R.drawable.default_icon);
		productList = (ProductList)this.getIntent().getSerializableExtra("Product");
		orderID = this.getIntent().getStringExtra("ORDERID");
		orderNO = this.getIntent().getStringExtra("ORDERNO");
		freight = this.getIntent().getDoubleExtra("REFUND", 0);
		initView();
	}

	private void initView() {
		totalMoney = (EditText)findViewById(R.id.total_money);
		dialog = new ProgressDialog(this);
		dialog.setMessage(getString(R.string.wait));
		check = (CheckBox)findViewById(R.id.allselect);
		check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				for (int i = 0; i < productList.getCount(); i++) {
					productList.getProduct(i).isChecked = isChecked;
				}
				adapter.notifyDataSetChanged();
			}
		});
		((Button) findViewById(R.id.back)).setOnClickListener(this);
		((Button) findViewById(R.id.finish)).setOnClickListener(this);
		((Button) findViewById(R.id.settleaccounts)).setOnClickListener(this);
		listview = (ListView) findViewById(R.id.listview);
		adapter = new ShoppingAdapter();
		listview.setAdapter(adapter);
		setTotal();
	}

	private void setTotal(){

		float money = 0;
		int selected = 0;
		if (productList != null && productList.getCount() > 0) {
			for (int i = 0; i < productList.getCount(); i++) {
				Product p = productList.getProduct(i);
				p.totalMoney = p.signalPrice * p.count;
				if (p.isChecked){
					money += p.signalPrice * p.count;
					selected ++;
				}
			}
		}
		
		if(money <= 0){
			((Button) findViewById(R.id.settleaccounts)).setBackgroundResource(R.drawable.ic_btn_shopping_03);
			((Button) findViewById(R.id.settleaccounts)).setTextColor(Color.argb(255, 171, 171, 171));
			((Button) findViewById(R.id.settleaccounts)).setShadowLayer(1, 0, 2, Color.argb(255, 230, 230, 230));
		}else{
			((Button) findViewById(R.id.settleaccounts)).setBackgroundResource(R.drawable.bg_settle);
			((Button) findViewById(R.id.settleaccounts)).setTextColor(Color.argb(255, 254, 254, 254));
			((Button) findViewById(R.id.settleaccounts)).setShadowLayer(1, 0, 2, Color.argb(255, 135, 49, 7));
			
		}
		((Button) findViewById(R.id.settleaccounts)).setText(getString(R.string.confirm_goods, selected + ""));
		DecimalFormat df=new DecimalFormat("#.##");
		double d = money + freight;
		String st=df.format(d);
		totalMoney.setText(st);
	}

//	class GetProductsAsync extends AsyncTask<Void, Void, String> {
//
//		@Override
//		protected void onPreExecute() {
//			if (dialog != null && !dialog.isShowing())
//				dialog.show();
//			super.onPreExecute();
//		}
//
//		@Override
//		protected void onPostExecute(String result) {
//			if (dialog != null && dialog.isShowing())
//				dialog.dismiss();
//			// TODO
//			adapter.notifyDataSetChanged();
//			super.onPostExecute(result);
//		}
//
//		@Override
//		protected String doInBackground(Void... params) {
//			// TODO
//			try {
//				productList.clearProductList();
//				for (int i = 0; i < 10; i++) {
//					Product p = new Product();
//					p.imageUrl = "http://img.noq.cc/hfs/6e5f/1c3a/750f/4477/3a-750f-4477-99a1-e8bad7bfcab7.jpg";
//					p.name = "fhfaagyufda";
//					p.count = 10;
//					p.totalMoney = 123.05f;
//					p.isChecked = false;
//					productList.addProduct(p);
//				}
//				Thread.sleep(1 * 1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			return "";
//		}
//
//	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.finish:
			final String[] numbers = getResources().getStringArray(R.array.numbers);

			Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.select_telephone))
					.setItems(numbers, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(which == 0){
								startActivity(new Intent(Intent.ACTION_CALL, Uri
										.parse("tel:" + getString(R.string.tele_num))));
							}else{
								ClipboardManager cmb = (ClipboardManager)RefundActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
								cmb.setText(numbers[which].substring(numbers[which].indexOf("-") + 1, numbers[which].length()));
								Toast.makeText(RefundActivity.this, getString(R.string.copy), Toast.LENGTH_SHORT).show();
								String pkg, cls;
						        try {
						            pkg = "com.tencent.mobileqq";
						            cls = "com.tencent.mobileqq.activity.SplashActivity";
						            ComponentName componet = new ComponentName(pkg, cls);
						            Intent i = new Intent();
						            i.setComponent(componet);
						            startActivity(i);
								} catch (Exception e) {
									try {
							            pkg = "com.tencent.minihd.qq";
							            cls = "com.tencent.qq.SplashActivity";
							            ComponentName componet = new ComponentName(pkg, cls);
							            Intent i = new Intent();
							            i.setComponent(componet);
							            startActivity(i);
									} catch (Exception e2) {
										try {
								            pkg = "com.tencent.android.pad";
								            cls = "com.tencent.android.pad.paranoid.desktop.DesktopActivity";
								            ComponentName componet = new ComponentName(pkg, cls);
								            Intent i = new Intent();
								            i.setComponent(componet);
								            startActivity(i);
										} catch (Exception e3) {
										}
									}
								}
							}
						}

					}).create().show();
			break;
		case R.id.settleaccounts:
			// TODO
			String ids = "";
			if(productList.getCount() > 0){
				for (int i = 0; i < productList.getCount(); i++) {
					Product p = productList.getProduct(i);
					if(p.isChecked){
						ids += p.OrderDetailID + ",";
					}
				}
				if(!TextUtils.isEmpty(ids)){
					ids = ids.substring(0, ids.length() - 1);
					startActivityForResult(new Intent(this, RefundReasonActivity.class).putExtra("IDS", ids).putExtra("TOTAL", this.totalMoney.getText().toString()).putExtra("ORDERNO", orderNO).putExtra("ORDERID", orderID), 1);
				}
			}
			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1 && resultCode == RESULT_OK){
			setResult(RESULT_OK);
			finish();
		}
	}

	class ShoppingAdapter extends BaseAdapter {
		private ArrayList<NetImageView> imageList = new ArrayList<NetImageView>();

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(productList != null && productList.getCount() > 0)
				return productList.getCount();
			else
				return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return productList.getProduct(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return Long.valueOf(productList.getProductID(position));
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
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

		public ArrayList<NetImageView> getImageList() {
			// TODO Auto-generated method stub
			return this.imageList;
		}
		
		class ViewHolder{
			private NetImageView image;
			private TextView title, detail;
			private TextView price,oriprice;
			private CheckBox check;
			private Button delete;
			private View itemView;
			
			public ViewHolder(){
				itemView = mInflater.inflate(R.layout.refund_item, null);
				itemView.setTag(this);
				delete = (Button)itemView.findViewById(R.id.delete_item);
				check = (CheckBox)itemView.findViewById(R.id.check);
				image = (NetImageView)itemView.findViewById(R.id.imageview);
				title = (TextView)itemView.findViewById(R.id.name);
				price = (TextView)itemView.findViewById(R.id.price);
				oriprice = (TextView)itemView.findViewById(R.id.quality);
				detail = (TextView)itemView.findViewById(R.id.detail);
			}
			
			public View getView(int position){
				final Product p = (Product) getItem(position);
				check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						p.isChecked = isChecked;
						setTotal();
					}
				});
				delete.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						productList.deleteProduct(p);
						notifyDataSetChanged();
						setTotal();
					}
				});
				image.setImageUrl(MassVigUtil.GetImageUrl(p.imageUrl, 64, 64), MassVigContants.PATH, shopDefaultImg);
				title.setText(p.name);
				price.setText(p.signalPrice + "");
				oriprice.setText("x" + p.count);
//				oriprice.setText("x2");
				HashMap<String, String> map = p.selectedProperties;
				String detailText = "";
				if(map != null)
					for(String key : map.keySet()){
						detailText += key + ":" + map.get(key) + "\n";
					}
//				detailText += getString(R.string.number) + p.count;
				detail.setText(detailText);
				check.setChecked(p.isChecked);
				return itemView;
			}
		}
	}
}
