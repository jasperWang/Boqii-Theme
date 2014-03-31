package com.massvig.ecommerce.activities;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.massvig.ecommerce.managers.AddressManager;
import com.massvig.ecommerce.managers.AddressManager.LoadListener;
import com.massvig.ecommerce.network.MassVigClientHelper;
import com.massvig.ecommerce.network.MassVigRequestParameters;
import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.Address;
import com.massvig.ecommerce.entities.Goods;
import com.massvig.ecommerce.entities.Product;
import com.massvig.ecommerce.entities.ProductList;
import com.massvig.ecommerce.service.MassVigService;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigExit;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.widgets.LineTextView;
import com.massvig.ecommerce.widgets.NetImageView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ShoppingCarActivity extends BaseActivity implements OnClickListener {



	private ProgressDialog dialog;
	private ListView listview;
	private ProductList productList = new ProductList();
	private LayoutInflater mInflater;
	private Bitmap shopDefaultImg;
	private TextView totalMoney;
	private TextView totalYuan;
	private CheckBox check;
	private ShoppingAdapter adapter;
	private BaseApplication app;
	private ProductList InvalidList = new ProductList();
	private LinearLayout invalidLayout;
	private TextView information;
	private MassVigExit exit = new MassVigExit();
	private LinearLayout nodata, shoppingLayout;
	private boolean isFirstIn = true,isCheck;
	private TextView discount;
	private int customerAddressID, expressage;
	private float promotionDiscount = 0;
	private ArrayList<Product> pro,deleteps;
	private int isselected = 0;
	private String coupons="", payment="", billType="", billTitle="", productSpecID="", quantity="";
	private AddressManager mManager;
	private ArrayList<Address> addresslist;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shoppoingcar);
		setTitle(getString(R.string.shoppingcar));
		app = (BaseApplication) getApplication();
		mInflater = LayoutInflater.from(this);
		shopDefaultImg = BitmapFactory.decodeResource(getResources(),
				R.drawable.default_icon);
		initView();
	}

	private void pressAgainExit(){
		if(exit.isExit()){
			finish();
		}else{
			Toast.makeText(this, getString(R.string.exit_message), Toast.LENGTH_SHORT).show();
			exit.doExitInOneSecond();
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			pressAgainExit();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onResume() {
		super.onResume();
		check.setChecked(true);
		app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(this, "SESSIONID", "") : app.user.sessionID;
		if(!TextUtils.isEmpty(app.user.sessionID)){
			new GetProductsAsync().execute(app.user.sessionID);
		}else{
			 shoppingLayout.setVisibility(View.GONE);
		}
	}

	private void initView() {
		discount = (TextView)findViewById(R.id.discount);
		nodata = (LinearLayout)findViewById(R.id.nodata);
		shoppingLayout = (LinearLayout)findViewById(R.id.shopping_layout);
		((Button)findViewById(R.id.tolook)).setOnClickListener(this);
		((Button)findViewById(R.id.gotobuy)).setOnClickListener(this);
		invalidLayout = (LinearLayout)findViewById(R.id.invalid);
		information = (TextView)findViewById(R.id.information);
		totalMoney = (TextView) findViewById(R.id.total_money);
		totalYuan = (TextView) findViewById(R.id.total_yuan);
		dialog = new ProgressDialog(this);
		listview = (ListView) findViewById(R.id.listview);
		adapter = new ShoppingAdapter();
		listview.setAdapter(adapter);
		dialog.setMessage(getString(R.string.wait));
		check = (CheckBox) findViewById(R.id.allselect);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				check.setChecked(true);
			}
		}, 100);
		pro = new ArrayList<Product>();
		deleteps = new ArrayList<Product>();
	    check.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isCheck)
					isselected = pro.size();
				else
				{
					for(int j = 0; j < deleteps.size() ; j++){
							if(deleteps.get(j).IsGift)
								productList.deleteProduct(deleteps.get(j));
					}
					isselected = 0;
					setText();
				}
				for (int i = 0; i < productList.getCount(); i++) {
 					productList.getProduct(i).isChecked = isCheck;
				}
				adapter.notifyDataSetChanged();
				getPromotion();	
			}
		});   
		check.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				  isCheck = isChecked;
			  if(isChecked)
					isselected = pro.size();
				adapter.notifyDataSetChanged();
			}
		});
		((Button) findViewById(R.id.back)).setOnClickListener(this);
		((Button) findViewById(R.id.delete)).setOnClickListener(this);
		((Button) findViewById(R.id.settleaccounts)).setOnClickListener(this);
		
	}
	
	private void getPromotion(){
		productSpecID = getIDs();
		quantity = getQuantities();
		new OrderPromotionAsync().execute(app.user.sessionID, customerAddressID +"", coupons, payment, billType, billTitle, productSpecID, quantity, expressage + "");
	}

	private void setTotal() {
		float money = 0;
		int selected = 0;
		int number = 0;
		if (productList != null && productList.getCount() > 0) {
			for (int i = 0; i < productList.getCount(); i++) {
				Product p = productList.getProduct(i);
				p.totalMoney = p.signalPrice * p.count;
				number += p.count;
				if (p.isChecked){
					money += p.signalPrice * p.count;
					selected += p.count;
				}
			}
		}
		if (money <= 0) {
			((TextView) findViewById(R.id.text)).setTextColor(Color.argb(255,
					154, 154, 154));
//			totalMoney.setTextColor(Color.argb(255, 154, 154, 154));
			((Button) findViewById(R.id.settleaccounts))
			.setBackgroundResource(R.drawable.ic_btn_shopping_03);
			((Button) findViewById(R.id.settleaccounts)).setTextColor(Color
					.argb(255, 171, 171, 171));
			((Button) findViewById(R.id.settleaccounts)).setShadowLayer(1, 0,
					2, Color.argb(255, 230, 230, 230));
		} else {
			((Button) findViewById(R.id.settleaccounts)).setEnabled(true);
			((TextView) findViewById(R.id.text)).setTextColor(Color.argb(255,
					92, 92, 92));
			totalMoney.setTextColor(Color.rgb(237, 42, 27));
			((Button) findViewById(R.id.settleaccounts))
			.setBackgroundResource(R.drawable.bg_settle);
			((Button) findViewById(R.id.settleaccounts)).setTextColor(Color
					.argb(255, 254, 254, 254));
			((Button) findViewById(R.id.settleaccounts)).setShadowLayer(1, 0,
					2, Color.argb(255, 135, 49, 7));

		}
		int pro_no = 0;
		for(int i = 0 ;i < pro.size(); i++){
			pro_no += pro.get(i).count;
		}
		    ((MainTabActivity)getParent()).setTotal(pro_no);
		((Button) findViewById(R.id.settleaccounts)).setText(getString(R.string.jiesuan, selected + ""));
		DecimalFormat df=new DecimalFormat("#.##");
		String s = df.format(money);
		totalYuan.setText(getString(R.string.pro_total_money, s));
		money -= promotionDiscount;
		String st=df.format(money);
		totalMoney.setText(getString(R.string.yuan, st));
	}

	public String getIDs(){
		ArrayList<Product> ps = new ArrayList<Product>();
		ps.clear();
		for (int i = 0; i < productList.getCount(); i++) {
			if(productList.getProduct(i).isChecked && !productList.getProduct(i).IsGift)
				ps.add(productList.getProduct(i));
		}
		String ids = "";
		if (ps.size() > 0)
			for (int i = 0; i < ps.size(); i++) {
				ids += ps.get(i).productSpecID;
				if(i != ps.size() - 1)
					ids += ",";
			}
		return ids;
	}
	
	public String getQuantities(){
		ArrayList<Product> ps = new ArrayList<Product>();
		for (int i = 0; i < productList.getCount(); i++) {
			if(productList.getProduct(i).isChecked && !productList.getProduct(i).IsGift)
				ps.add(productList.getProduct(i));
		}
		String ids = "";
		if (ps.size() > 0)
			for (int i = 0; i < ps.size(); i++) {
				ids += ps.get(i).count;
				if(i != ps.size() - 1)
					ids += ",";
			}
		return ids;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.delete:
			String ids = getIDs();
			new DeleteProductsAsync().execute(app.user.sessionID, ids);
			break;
		case R.id.settleaccounts:
			// TODO
			ProductList list = new ProductList();
			for (int i = 0; i < productList.getCount(); i++) {
				if(productList.getProduct(i).isChecked || productList.getProduct(i).IsGift)
					list.addProduct(productList.getProduct(i));
			}
				    startActivity(new Intent(this, OrderConfirmActivity.class).putExtra("Product", list).putExtra("PromotionDiscount", promotionDiscount));
			break;
		case R.id.tolook:
			String deleteIDS = "";
			if (InvalidList.getCount() > 0)
				for (int i = 0; i < InvalidList.getCount(); i++) {
					deleteIDS += InvalidList.getProduct(i).productSpecID;
					if(i != InvalidList.getCount() - 1)
						deleteIDS += ",";
				}
			//TODO
			new DeleteProductsAsync().execute(app.user.sessionID, deleteIDS);
			startActivity(new Intent(this, InvalidProductsActivity.class).putExtra("Product", InvalidList));
			break;
		case R.id.gotobuy:
			((MainTabActivity)getParent()).setTabHostIndex(0);
			break;
		default:
			break;
		}
	}

	class GetProductsAsync extends AsyncTask<String, Void, Integer> {

		@Override
		protected void onPreExecute() {
			if (dialog != null && !dialog.isShowing())
				dialog.show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (dialog != null && dialog.isShowing())
				dialog.dismiss();
			// TODO
			if(result == 0){
				if(InvalidList != null && InvalidList.getCount() > 0){
					invalidLayout.setVisibility(View.VISIBLE);
					information.setText(getString(R.string.invalid, InvalidList.getCount() + ""));
					
//					 if(productList != null && productList.getCount() > 0){
//						 shoppingLayout.setVisibility(View.VISIBLE);
//					 }else{
//						 shoppingLayout.setVisibility(View.GONE);
//					 }
				}else{
					 if(productList != null && productList.getCount() > 0){
//						 setTotal();
						getPromotion();
						 shoppingLayout.setVisibility(View.VISIBLE);
					 }else{
						 ((MainTabActivity)getParent()).setTotal(0);
						 shoppingLayout.setVisibility(View.GONE);
					 }
					invalidLayout.setVisibility(View.GONE);
					pro.clear();
					for (int i = 0; i < productList.getCount(); i++) {
						if(!productList.getProduct(i).IsGift)
						{
							pro.add(productList.getProduct(i));
							isselected = pro.size();
						}
					}
				}
				adapter.notifyDataSetChanged();
			}else if(result == MassVigContants.SESSIONVAILED){
				if(isFirstIn){
					startActivity(new Intent(ShoppingCarActivity.this, LoginActivity.class));
					isFirstIn = false;
				}
				shoppingLayout.setVisibility(View.GONE);
			}
			super.onPostExecute(result);
		}

		@Override
		protected Integer doInBackground(String... params) {
			// TODO
			productList.clearProductList();
			InvalidList.clearProductList();
			String result = MassVigService.getInstance().GetList(params[0]);

			try {
				JSONObject object = new JSONObject(result);
				int resultCode = object.getInt("ResponseStatus");
				// String message = object.getString("ResponseMsg");
				if (resultCode == 0) {
					String res = object.getString("ResponseData");
					if (!TextUtils.isEmpty(res)) {
						JSONObject obj = new JSONObject(res);
						JSONArray list = obj.getJSONArray("ValidProducts");
						if (list != null && list.length() > 0) {
							for (int i = 0; i < list.length(); i++) {
								Product p = new Product();
								JSONObject data = list.optJSONObject(i);
								p = DataAnalyse(data);
								productList.addProduct(p);
							}
						}
						JSONArray inList = obj.getJSONArray("InValidProducts");
						if (inList != null && inList.length() > 0) {
							for (int i = 0; i < inList.length(); i++) {
								Product p = new Product();
								JSONObject data = inList.optJSONObject(i);
								p = DataAnalyse(data);
								InvalidList.addProduct(p);
							}
						}
					}
				}
				return resultCode;
			} catch (Exception e) {
				return -1;
			}
		}

	}

	class ModifyProductsAsync extends AsyncTask<String, Void, Integer> {

		@Override
		protected void onPreExecute() {
//			if (dialog != null && !dialog.isShowing())
//				dialog.show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Integer result) {
//			if (dialog != null && dialog.isShowing())
//				dialog.dismiss();
			// TODO
			if(result == 0){
				adapter.notifyDataSetChanged();
//				setTotal();
				getPromotion();
			}
			super.onPostExecute(result);
		}

		@Override
		protected Integer doInBackground(String... params) {
			// TODO
			String result = MassVigService.getInstance().ModifyQuantity(params[0], params[1], params[2]);

			try {
				JSONObject object = new JSONObject(result);
				int resultCode = object.getInt("ResponseStatus");
				if (resultCode == 0) {
					JSONArray list = object.getJSONArray("ResponseData");
					if (list != null && list.length() > 0) {
						for (int i = 0; i < list.length(); i++) {
							JSONObject data = list.optJSONObject(i);
							int proID = data.optInt("ProductSpecID");
							int quantity = data.optInt("Quantity");
							A:
							for (int j = 0; j < productList.getCount(); j++) {
								if(proID == productList.getProduct(j).productSpecID){
									productList.getProduct(j).count = quantity;
									break A;
								}
							}
						}
					}
				}
				return resultCode;
			} catch (Exception e) {
				return -1;
			}
		}

	}

	class DeleteProductsAsync extends AsyncTask<String, Void, Integer> {

		@Override
		protected void onPreExecute() {
			if (dialog != null && !dialog.isShowing())
				dialog.show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (dialog != null && dialog.isShowing())
				dialog.dismiss();
			// TODO
			if(result == 0){
				if(pro.size() == 0)
				{
			    ((MainTabActivity)getParent()).setTotal(0);
			    shoppingLayout.setVisibility(View.GONE);
				}
				else{
			    ((MainTabActivity)getParent()).setTotal(pro.size());
			    shoppingLayout.setVisibility(View.VISIBLE);
				}
				adapter.notifyDataSetChanged();
				deleteps.clear();
				ShoppingCarActivity.this.check.setChecked(false);
				isselected = 0;
				setText();
//				setTotal();
			}else if(result == MassVigContants.SESSIONVAILED){
				startActivity(new Intent(ShoppingCarActivity.this, LoginActivity.class));
			}
			super.onPostExecute(result);
		}

		@Override
		protected Integer doInBackground(String... params) {
			String result = "";
			try {			
				for (int i = 0; i < productList.getCount(); i++) {
					if (productList.getProduct(i).isChecked)
					{
						deleteps.add(productList.getProduct(i));
					
					}
				}
				if(!TextUtils.isEmpty(params[1])){
					result = MassVigService.getInstance().DeleteProduct(params[0], params[1]);
					try {
						JSONObject object = new JSONObject(result);
						int resultCode = object.getInt("ResponseStatus");
						if (resultCode == 0) {
							for (int i = 0; i < deleteps.size(); i++) {
								productList.deleteProduct(deleteps.get(i));
								pro.remove(deleteps.get(i));
							}
							for(int i = 0 ; i < productList.getCount() ; i++){
								 if(productList.getProduct(i).IsGift)
									 productList.deleteProduct(productList.getProduct(i));
								 }
						}
						return resultCode;
					} catch (Exception e) {
						return -1;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return -1;
		}

	}
	public void setText(){
		((TextView)findViewById(R.id.text)).setTextColor(Color.rgb(167, 164, 164));
		totalMoney.setTextColor(Color.rgb(167, 164, 164));
		totalMoney.setText(getString(R.string.yuan,0+""));
		totalYuan.setTextColor(Color.rgb(167, 164, 164));
		totalYuan.setText(getString(R.string.pro_total_money,0+""));
		discount.setText(getString(R.string.promotion_discount, 0 + ""));
		((Button) findViewById(R.id.settleaccounts)).setEnabled(false);
		((Button) findViewById(R.id.settleaccounts)).setGravity(Gravity.CENTER);
		((Button) findViewById(R.id.settleaccounts)).setBackgroundResource(R.drawable.ic_btn_shopping_03);
		((Button) findViewById(R.id.settleaccounts)).setTextColor(Color.rgb(238, 237, 237));
		((Button) findViewById(R.id.settleaccounts)).setText(getString(R.string.jiesuan, 0 + ""));
	}

	class SettleAccountsAsync extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			if (dialog != null && !dialog.isShowing())
				dialog.show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {
			if (dialog != null && dialog.isShowing())
				dialog.dismiss();
			// TODO
			super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO
			try {
				Thread.sleep(3 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return "";
		}

	}

	class ShoppingAdapter extends BaseAdapter {
		private ArrayList<NetImageView> imageList = new ArrayList<NetImageView>();
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (productList != null && productList.getCount() > 0)
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

		class ViewHolder {
			private NetImageView image;
			private TextView title, price, detail, gift;
			private LineTextView oriprice;
			private CheckBox check;
			private Button asc, desc;
			private TextView number;
			private View itemView;
			private LinearLayout numberLayout;
			

			public ViewHolder() {
				itemView = mInflater.inflate(R.layout.shopping_item, null);
				itemView.setTag(this);
				gift = (TextView)itemView.findViewById(R.id.gift);
				numberLayout = (LinearLayout)itemView.findViewById(R.id.number_layout);
				asc = (Button) itemView.findViewById(R.id.asc);
				desc = (Button) itemView.findViewById(R.id.desc);
				number = (TextView) itemView.findViewById(R.id.number);
				check = (CheckBox) itemView.findViewById(R.id.check);
				image = (NetImageView) itemView.findViewById(R.id.imageview);
				title = (TextView) itemView.findViewById(R.id.name);
				price = (TextView) itemView.findViewById(R.id.price);
				oriprice = (LineTextView) itemView.findViewById(R.id.oriprice);
				detail = (TextView) itemView.findViewById(R.id.detail);
			}
				

			public View getView(final int position) {
				final Product p = (Product) getItem(position);
//				check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//					@Override
//					public void onCheckedChanged(CompoundButton buttonView,
//							boolean isChecked) {
//						    p.isChecked = isChecked;
//						    getPromotion();
// 	 								if(!p.isChecked)
// 	 									isselected--;
// 	 								else if(p.isChecked)
// 	 									isselected++;
// 							if(isselected == productcount)
// 								ShoppingCarActivity.this.check.setChecked(true);
// 							if(isselected == 0){
// 								ShoppingCarActivity.this.check.setChecked(false);
// 								totalMoney.setText(getString(R.string.yuan,0+""));
// 								totalYuan.setText(getString(R.string.pro_total_money,0+""));
// 								((Button) findViewById(R.id.settleaccounts)).setText(getString(R.string.jiesuan, 0 + ""));
// 							}
//					}
//				});
				check.setOnClickListener(new OnClickListener() {			
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						p.isChecked = !p.isChecked;			
							 if(!p.isChecked)	 
								 isselected--;
							 else if(p.isChecked)
								isselected++;
							 if(isselected == 0)
							 {
								 ShoppingCarActivity.this.check.setChecked(false);
								 for(int i = 0 ; i < productList.getCount() ; i++){
								 if(productList.getProduct(i).IsGift){
									 productList.deleteProduct(productList.getProduct(i));
									 notifyDataSetChanged();
								 }			
								 }
								 setText();
							 }else if(isselected > 0 && isselected < pro.size()){
								 ShoppingCarActivity.this.check.setChecked(false);
							 }
							 else if(isselected == pro.size())
								 ShoppingCarActivity.this.check.setChecked(true);
							 getPromotion();

					}
				});
//				number.addTextChangedListener(new TextWatcher() {
//					@Override
//					public void onTextChanged(CharSequence s, int start, int before, int count) {
//						// TODO Auto-generated method stub
//					}
//					
//					@Override
//					public void beforeTextChanged(CharSequence s, int start, int count,
//							int after) {
//						// TODO Auto-generated method stub
//					}
//					
//					@Override
//					public void afterTextChanged(Editable s) {
//						if (!TextUtils.isEmpty(s.toString())) {
//							p.count = Integer.valueOf(s.toString());
//							if (Integer.valueOf(s.toString()) < 1)
//								number.setText("1");
//							if (Integer.valueOf(s.toString()) > p.Inventory)
//								number.setText("" + p.Inventory);
//							new ModifyProductsAsync().execute(app.user.sessionID, p.productSpecID + "", p.count + "");
//						}
////						if (!TextUtils.isEmpty(s.toString())) {
////							p.count = Integer.valueOf(s.toString());
////							isChanged = true;
////							if (Integer.valueOf(s.toString()) < 1)
////								number.setText("1");
////							if (Integer.valueOf(s.toString()) > p.Inventory)
////								number.setText("" + p.Inventory);
////							setTotal();
////							new ModifyProductsAsync().execute(app.user.sessionID, p.productSpecID + "", p.count + "");
////							isChanged = false;
////						}
//					}
//				});
				asc.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (p.count < p.Inventory) {
							p.count++;
							number.setText(p.count + "");
//							setTotal();
							new ModifyProductsAsync().execute(app.user.sessionID, p.productSpecID + "", p.count + "");
						}
					}
				});
				desc.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (p.count > 1) {
							p.count--;
							number.setText(p.count + "");
//							setTotal();
							new ModifyProductsAsync().execute(app.user.sessionID, p.productSpecID + "", p.count + "");
						}

					}
				});
				if (p.count > 0) {
					if(TextUtils.isEmpty(number.getText().toString()))
						number.setText(p.count + "");
					else if(!TextUtils.isEmpty(number.getText().toString()) && Integer.valueOf(number.getText().toString()) != p.count)
						number.setText(p.count + "");
				} else {
					p.count = 0;
					number.setText("0");
				}
				image.setImageUrl(MassVigUtil.GetImageUrl(p.imageUrl, MassVigUtil.dip2px(ShoppingCarActivity.this, 64), MassVigUtil.dip2px(ShoppingCarActivity.this, 64)),
						MassVigContants.PATH, shopDefaultImg);
				image.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Goods good = new Goods();
						good.productID = p.id;
						good.name = p.name;
						good.minPrice = p.signalPrice;
						good.imageUrl = p.imageUrl;
						startActivity(new Intent(ShoppingCarActivity.this, GoodsDetailActivity.class).putExtra("goods", good));
					}
				});
				title.setText(p.name);
				price.setText(getString(R.string.money_text, p.signalPrice + ""));
//				price.setText(p.totalMoney + "");
				oriprice.setText(p.oriPrice + "");
				String text = p.specInfo;

				if(!TextUtils.isEmpty(text)){
					text = text.substring(0, text.length() -1);
					String[] specs = text.split(";");
					HashMap<String,String>  map = new HashMap<String, String>();
					for (int i = 0; i < specs.length; i++) {
						String[] key_values = specs[i].split(":");
						map.put(key_values[0], key_values[1]);
					}
					p.selectedProperties = map;
				}

				HashMap<String, String> map = p.selectedProperties;
				String detailText = "";
				if(map != null && map.size() > 0){
					for (String key : map.keySet()) {
						detailText += key + ":" + map.get(key) + "\n";
					}
				}
				detailText.replaceFirst("\n", "");
				if(p.IsGift){
					detailText += "\n" + "赠品x1";
				}else{
					detailText += "\n" + getString(R.string.number) + p.count;	
				}
				detail.setText(detailText);
				check.setChecked(p.isChecked);
				
				if(p.IsGift){
					check.setVisibility(View.INVISIBLE);
					numberLayout.setVisibility(View.INVISIBLE);
					oriprice.setVisibility(View.INVISIBLE);
					gift.setVisibility(View.INVISIBLE);
					price.setVisibility(View.INVISIBLE);
				}else{
					price.setVisibility(View.VISIBLE);
					gift.setVisibility(View.INVISIBLE);
					check.setVisibility(View.VISIBLE);
					oriprice.setVisibility(View.VISIBLE);
					numberLayout.setVisibility(View.VISIBLE);
				}
				return itemView;
			}
		}
	}
	
	public class OrderPromotionAsync extends AsyncTask<String, Void, Integer>{

		@Override
		protected void onPostExecute(Integer result) {
//			if(dialog != null && dialog.isShowing())
//				dialog.dismiss();
			if(result == 0){
				discount.setText(getString(R.string.promotion_discount, promotionDiscount + ""));
				adapter.notifyDataSetChanged();
				setTotal();
			}else if(result == MassVigContants.SESSIONVAILED){
				GotoLogin();
			}else{
				
			}
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
//			if(dialog != null && !dialog.isShowing())
//				dialog.show();
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(String... params) {
			String result = MassVigService.getInstance().OrderPromotion(params[0], Integer.valueOf(params[1]), params[2], params[3], params[4], params[5], params[6], params[7], Integer.valueOf(params[8]));
			int status = -1;
			if(!TextUtils.isEmpty(result)){
				try {
					JSONObject o = new JSONObject(result);
					status = o.optInt("ResponseStatus");
					if (status == 0) {
						ProductList list = new ProductList();
						JSONObject obj = o.optJSONObject("ResponseData");
						JSONArray array = obj.optJSONArray("OrderDetails");
						promotionDiscount = (float) obj.optDouble("PromotionDiscount");
						for (int i = 0; i < array.length(); i++) {
							JSONObject object = array.optJSONObject(i);
							Product p = GiftAnalyse(object);
							if(p.IsGift){
								list.addProduct(p);	
								deleteps.add(p);
							}
						}
						ArrayList<Product> ps = new ArrayList<Product>();
						for (int i = 0; i < productList.getCount(); i++) {
							if (productList.getProduct(i).IsGift)
								ps.add(productList.getProduct(i));
						}
						if(!ps.isEmpty()){
							for (int i = 0; i < ps.size(); i++) {
								productList.deleteProduct(ps.get(i));
							}
						}
						productList.addProductList(list);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
			return status;
		}
		
	}
	
	public Product GiftAnalyse(JSONObject data) {
		Product p = new Product();
		p.OrderDetailID = data.optInt("OrderDetailID");
		p.id = data.optInt("ProductID");
		p.name = data.optString("ProductName");
		p.specInfo = data.optString("ProductSpecDesc");
		p.imageUrl = data.optString("MainImgUrl");
		p.count = data.optInt("Quantity");
		p.signalPrice = (float) data.optDouble("UnitPrice");
		p.OrderDetailStatus = data.optInt("OrderDetailStatus");
		p.IsGift = data.optBoolean("IsGift");
		p.isChecked = false;
		return p;
	}

	public Product DataAnalyse(JSONObject data) {
		Product p = new Product();
		p.count = data.optInt("Quantity");
		p.id = data.optInt("ProductID");
		p.imageUrl = data.optString("MainImgUrl");
		p.isChecked = true;
		p.name = data.optString("Name");
		p.oriPrice = (float) data.optDouble("OriginalPrice");
		p.productSpecID = data.optInt("ProductSpecID");
		p.Inventory = data.optInt("Inventory");
		p.specInfo = data.optString("ProductSpecDesc");
		p.signalPrice = (float) data.optDouble("Price");
		p.totalMoney = (float) data.optDouble("Price");
		p.ShoppingCartProductStatus = data.optInt("ShoppingCartProductStatus");
		return p;
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(dialog !=null && dialog.isShowing())
			dialog.dismiss();
		super.onDestroy();
	}

}
