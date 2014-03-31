package com.massvig.ecommerce.activities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.massvig.ecommerce.activities.MainActivity.MyImageAdapter;
import com.massvig.ecommerce.activities.ManageCouponActivity.CreateNewCoupon;
import com.massvig.ecommerce.activities.ManageCouponActivity.LoadDataAsync;
import com.massvig.ecommerce.activities.NewActionDetailActivity.MyListAdapter.ViewHolder;
import com.massvig.ecommerce.activities.ShoppingCarActivity.GetProductsAsync;
import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.AdItem;
import com.massvig.ecommerce.entities.Coupon;
import com.massvig.ecommerce.entities.CouponList;
import com.massvig.ecommerce.entities.Product;
import com.massvig.ecommerce.entities.ProductList;
import com.massvig.ecommerce.service.MassVigService;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.widgets.ADAdapter;
import com.massvig.ecommerce.widgets.AdGallery;
import com.massvig.ecommerce.widgets.NetImageView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Gallery.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CouponManageActivity extends BaseActivity implements OnClickListener{

	private ProgressDialog dialog;
	private AdGallery gallery;
	private ListView listview;
	private Button settleaccounts;
	private TextView totalMoney, info;
	private CouponList couponList = new CouponList();
//	private CouponList tempList = new CouponList();
	private CouponAdapter mAdapter = new CouponAdapter();
	private LayoutInflater mInflater;
	private boolean isLoaddone;
	private BaseApplication app;
	private int expressage;
	private String productSpecID = "", quantity = "";
	private MyImageAdapter mImageAdapter;
	public ArrayList<AdItem> imageItemsList = new ArrayList<AdItem>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coupon_manager);
		setTitle(R.string.coupon_manager);
		app = (BaseApplication) getApplication();
		expressage = this.getIntent().getIntExtra("EXPRESSAGE", 0);
		ProductList list = (ProductList) this.getIntent().getSerializableExtra("Product");
		if(list != null && list.getCount() > 0){
			for (int i = 0; i < list.getCount(); i++) {
				Product p = list.getProduct(i);
				productSpecID += p.productSpecID + ",";
				quantity += p.count + ",";
			}
			if(!TextUtils.isEmpty(productSpecID))
			{
				productSpecID = productSpecID.substring(0, productSpecID.length() - 1);
				quantity = quantity.substring(0, quantity.length() - 1);
			}
		}
		initView();
		new GetCouponAsync().execute();
		new CheckCoupon().execute(app.user.sessionID, productSpecID, quantity, expressage + "");
	}
	
	public class CreateNewCoupon extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String result = MassVigService.getInstance().CreateNewCoupon(app.user.sessionID);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			if(!TextUtils.isEmpty(result)){
				try {
					JSONObject o = new JSONObject(result);
					int res = o.getInt("ResponseStatus");
					if (res == 0) {
						if(!TextUtils.isEmpty(app.user.sessionID)){
							if(!isLoaddone){
								couponList.clearList();
								new CheckCoupon().execute(app.user.sessionID, productSpecID, quantity, expressage + "");
							}
						}else{
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		
	}
	
	public class GetCouponAsync extends AsyncTask<Void, Void, String>{

		private AdItem ItemAnaly(JSONObject obj) {
			AdItem item = new AdItem();
			try {
				item.AdItemID = obj.optInt("AdItemID");
				item.AdTemplate = obj.optInt("AdTemplate");
				item.Position = obj.optInt("Position");
				item.Rank = obj.optInt("Rank");
				item.AdRegions = obj.optString("AdRegions");
			} catch (Exception e) {
			}
			return item;
		}
		
		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String result = MassVigService.getInstance().GetAdvertises("6");
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			if(!TextUtils.isEmpty(result)){
				try {
					JSONObject o = new JSONObject(result);

					int res = o.getInt("ResponseStatus");
					if (res == 0) {
						JSONArray array = o.getJSONArray("ResponseData");
						for (int i = 0; i < array.length(); i++) {
							JSONObject data = array.getJSONObject(i);
							int position = data.getInt("Position");
							if (position == 6) {
								JSONArray arr = data.getJSONArray("AdItems");
								imageItemsList.clear();
								for (int j = 0; j < arr.length(); j++) {
									JSONObject obj = arr.getJSONObject(j);
									imageItemsList.add(ItemAnaly(obj));
								}
//								if(imageItemsList.size() > 0)
//									gallery.setVisibility(View.VISIBLE);
							}
						}
						mImageAdapter.notifyDataSetChanged();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		
	}

	public class MyImageAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		public Object getItem(int position) {
			if(imageItemsList.size() > 0)
				return imageItemsList.get(position % imageItemsList.size());
			else
				return new AdItem();
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public String getItemUrl(int position) {
			if(imageItemsList.size() > 0){
				AdItem ad = (AdItem) getItem(position);
				JSONArray array;
					try {
						array = new JSONArray(ad.AdRegions);
						if(array != null && array.length() > 0){
							JSONObject o = array.getJSONObject(0);
							String url = o.optString("ImgUrl");
							return MassVigUtil.GetImageUrl(url, MassVigUtil.dip2px(CouponManageActivity.this, 320), MassVigUtil.dip2px(CouponManageActivity.this, 150));
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			return "";
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			NetImageView i = new NetImageView(CouponManageActivity.this);
			i.setImageUrl(getItemUrl(position), MassVigContants.PATH, null);
			i.setScaleType(ImageView.ScaleType.FIT_XY);
			i.setLayoutParams(new Gallery.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			MarginLayoutParams mlp = (MarginLayoutParams) gallery.getLayoutParams();
			mlp.setMargins(15, 0, 15, 0);
			return i;
		}

	}

	private void initView() {
		dialog = new ProgressDialog(this);
		dialog.setMessage(getString(R.string.loading_data));
		mInflater = LayoutInflater.from(this);
		gallery = (AdGallery)findViewById(R.id.gallery);
		mImageAdapter = new MyImageAdapter();
		gallery.setAdapter(mImageAdapter);
		gallery.setSpacing(20);
		gallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				new CreateNewCoupon().execute();
			}
		});
		listview = (ListView)findViewById(R.id.listview);
		listview.setAdapter(mAdapter);
		settleaccounts = (Button)findViewById(R.id.settleaccounts);
		settleaccounts.setOnClickListener(this);
		totalMoney = (TextView)findViewById(R.id.total_money);
		info = (TextView)findViewById(R.id.info);
		((Button)findViewById(R.id.back)).setOnClickListener(this);
		((Button)findViewById(R.id.finish)).setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(this, "SESSIONID", "") : app.user.sessionID;
		if(!TextUtils.isEmpty(app.user.sessionID)){
//			tempList.clearList();
//			couponList.clearList();
//			new LoadDataAsync().execute(app.user.sessionID);
		}else{
		}
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.settleaccounts:
			int count = 0;
			if(couponList.couponList.size() > 0)
				for (int i = 0; i < couponList.couponList.size(); i++) {
					Coupon c = couponList.couponList.get(i);
					if (c.isCheck) {
						count ++;
					}
				}
			if (count > 0) {
				Intent intent = new Intent();
				intent.putExtra("coupons", "");
				if (!TextUtils.isEmpty(getCoupons())) {
					intent.putExtra("coupons", getCoupons().toUpperCase());
					intent.putExtra("couponsPrice", getCouponsPrice());
				}
				setResult(RESULT_OK, intent);
				finish();
			} else {
				Toast.makeText(this, getString(R.string.select_no_coupon), Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.back:
			finish();
			break;
		case R.id.finish:
			startActivity(new Intent(this, ManageCouponActivity.class));
			break;

		default:
			break;
		}
	}
	
	private String getCouponsPrice() {
		String result = "";
		for (int i = 0; i < couponList.couponList.size(); i++) {
			Coupon c = couponList.couponList.get(i);
			if(c.isCheck){
				result += c.Discount + ",";
			}
		}
		if(!TextUtils.isEmpty(result))
			result = result.substring(0, result.length() - 1);
		return result;
	}

	private String getCoupons(){
		String result = "";
		for (int i = 0; i < couponList.couponList.size(); i++) {
			Coupon c = couponList.couponList.get(i);
			if(c.isCheck){
				result += c.CouponNo + ",";
			}
		}
		if(!TextUtils.isEmpty(result))
			result = result.substring(0, result.length() - 1);
		return result;
	}
	
	class CouponAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return couponList.couponList.size();
		}

		@Override
		public Object getItem(int position) {
			return couponList.couponList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return couponList.couponList.get(position).id;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			convertView = holder.getView(position);
			return convertView;
		}
		
		class ViewHolder {
			private View itemView;
			private CheckBox check;
			private TextView title, time, price, status, text;
			private NetImageView image;
			private LinearLayout imageView;
			
			public ViewHolder(){
				itemView = mInflater.inflate(R.layout.coupon_manager_item, null);
				itemView.setTag(this);
				image = (NetImageView)itemView.findViewById(R.id.image);
				imageView = (LinearLayout)itemView.findViewById(R.id.imageview);
				check = (CheckBox)itemView.findViewById(R.id.check);
				title = (TextView)itemView.findViewById(R.id.name);
				time = (TextView)itemView.findViewById(R.id.time);
				status = (TextView)itemView.findViewById(R.id.status);
				price = (TextView)itemView.findViewById(R.id.price);
				text = (TextView)itemView.findViewById(R.id.text);
			}
			
			public View getView(int position){
				final Coupon c = couponList.couponList.get(position);
				if(!TextUtils.isEmpty(c.imageUrl)){
					image.setVisibility(View.VISIBLE);
					imageView.setVisibility(View.GONE);
					image.setImageUrl(MassVigUtil.GetImageUrl(c.imageUrl, MassVigUtil.dip2px(CouponManageActivity.this, 120), MassVigUtil.dip2px(CouponManageActivity.this, 90)), MassVigContants.PATH, null);
				}else{
					image.setVisibility(View.GONE);
					imageView.setVisibility(View.VISIBLE);
				}
				check.setChecked(c.IsUseed);
				if(c.CanUse){
					title.setTextColor(Color.argb(255, 77, 78, 83));
					time.setTextColor(Color.argb(255, 77, 78, 83));
					check.setEnabled(true);
					itemView.setEnabled(true);
				}else{
					title.setTextColor(Color.argb(255, 186, 186, 186));
					time.setTextColor(Color.argb(255, 186, 186, 186));
					check.setEnabled(false);
					itemView.setEnabled(false);
				}
				itemView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						c.isCheck = !c.isCheck;
						check.setChecked(c.isCheck);
						setInfo();
						new CheckCoupon().execute(app.user.sessionID, productSpecID, quantity, expressage + "");
					}
				});
				check.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						c.isCheck = !c.isCheck;
						check.setChecked(c.isCheck);
						setInfo();
						new CheckCoupon().execute(app.user.sessionID, productSpecID, quantity, expressage + "");
					}
				});
//				check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//					
//					@Override
//					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
////						sessinid, productspecid ,quantity, expressage;
//						c.isCheck = isChecked;
//						setInfo();
//						new CheckCoupon().execute(app.user.sessionID, productSpecID, quantity, expressage + "");
//					}
//				});
				title.setText(c.content);
				time.setText(c.StartTime + "~" + c.EndTime);
				price.setText(getString(R.string.money_text, c.Discount + ""));
				return itemView;
			}
		}
	}
	
	class CheckCoupon extends AsyncTask<String, Void, Integer>{

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if(dialog != null && dialog.isShowing())
				dialog.dismiss();
			if(result == 0){
				setInfo();
				mAdapter.notifyDataSetChanged();
			}else if(result == MassVigContants.SESSIONVAILED){
				GotoLogin();
			}else{
				
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog != null && !dialog.isShowing())
				dialog.show();
		}

		@Override
		protected Integer doInBackground(String... params) {
			String coupons = "";
			JSONArray arr = new JSONArray();
			for (int i = 0; i < couponList.couponList.size(); i++) {
				Coupon c = couponList.couponList.get(i);
				if(c.isCheck){
					try {
						JSONObject obj = new JSONObject();
						obj.put("IsUesed", true);
						obj.put("CouponNo", c.CouponNo);
						arr.put(obj);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
//			if(!TextUtils.isEmpty(coupons))
//				coupons = coupons.substring(0, coupons.length() - 1);
			if(arr != null && arr.length() > 0)
				coupons = arr.toString();
			String result = MassVigService.getInstance().CheckCoupon2(params[0], params[1], params[2], coupons, params[3]);
			int code = -1;
			if(!TextUtils.isEmpty(result) && !result.equals("null")){
				try {
					JSONObject object = new JSONObject(result);
					code = object.optInt("ResponseStatus");
					if(code == 0){
						JSONArray array = object.optJSONArray("ResponseData");
						if(array != null && array.length() > 0){
							couponList.clearList();
							for (int i = 0; i < array.length(); i++) {
								JSONObject o = array.optJSONObject(i);
								Coupon c = new Coupon();
								c = DataAnalyse(o);
								couponList.couponList.add(c);
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return code;
		}
		
	}
	
	private void setInfo(){
		int discount = 0;
		int count = 0;
		if(couponList.couponList.size() > 0)
			for (int i = 0; i < couponList.couponList.size(); i++) {
				Coupon c = couponList.couponList.get(i);
				if (c.isCheck) {
					discount += c.Discount;
					count ++;
				}
			}
		settleaccounts.setText(getString(R.string.use_c, count + ""));
		info.setText(getString(R.string.use_coupon, count + ""));
		totalMoney.setText(getString(R.string.yuan, discount + ""));
	}

	private Coupon DataAnalyse(JSONObject data) {
		Coupon c = new Coupon();
		c.imageUrl = data.optString("Url");
		c.CouponNo  = data.optString("CouponNo");
		c.content = data.optString("ViewName");
		c.CreateTime = data.optString("CreateTime");
		c.Discount = data.optInt("Discount");
		c.EndTime = data.optString("EndTime");
		c.PromotionType = data.optString("PromotionType");
		c.StartTime = data.optString("StartTime");
		c.ViewStatus = data.optInt("ViewStaus");
		c.IsUseed = data.optBoolean("IsUesed");
		c.CanUse = data.optBoolean("CanUse");
		c.isCheck = data.optBoolean("IsUesed");
		return c;
	}
	
//	class LoadDataAsync extends AsyncTask<String, Void, Boolean>{
//
//		@Override
//		protected void onPostExecute(Boolean result) {
//			if(dialog != null && dialog.isShowing())
//				dialog.dismiss();
//			super.onPostExecute(result);
//			if(result){
//				couponList.addCouponList(tempList);
//				tempList.clearList();
//				mAdapter.notifyDataSetChanged();
//			}
//		}
//
//		@Override
//		protected void onPreExecute() {
//			if (dialog != null && !dialog.isShowing())
//				dialog.show();
//			super.onPreExecute();
//		}
//
//		@Override
//		protected Boolean doInBackground(String... params) {
//			tempList.clearList();
//			boolean result = tempList.FetchCouponList(params[0], 0);
//			if(tempList.couponList.size() == 0){
//				isLoaddone = true;
//			}
//			return result;
//		}
//		
//	}

}
