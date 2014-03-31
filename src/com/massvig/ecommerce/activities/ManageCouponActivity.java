package com.massvig.ecommerce.activities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.massvig.ecommerce.activities.CouponManageActivity.GetCouponAsync;
import com.massvig.ecommerce.activities.CouponManageActivity.MyImageAdapter;
import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.AdItem;
import com.massvig.ecommerce.entities.Coupon;
import com.massvig.ecommerce.entities.CouponList;
import com.massvig.ecommerce.service.MassVigService;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.widgets.ADAdapter;
import com.massvig.ecommerce.widgets.AdGallery;
import com.massvig.ecommerce.widgets.NetImageView;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Gallery.LayoutParams;

public class ManageCouponActivity extends BaseActivity implements OnClickListener{

	private ProgressDialog dialog;
	private AdGallery gallery;
	private ListView listview;
	private CouponList couponList = new CouponList();
	private CouponList tempList = new CouponList();
	private CouponAdapter mAdapter = new CouponAdapter();
	private LayoutInflater mInflater;
	private boolean isLoaddone;
	private BaseApplication app;
	private EditText coupon_number;
	private int delete_position;
	private boolean isEdit = false;
	private MyImageAdapter mImageAdapter;
	public ArrayList<AdItem> imageItemsList = new ArrayList<AdItem>();
	private int width, height;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manager_coupon);
		setTitle(R.string.coupon_manager);
		app = (BaseApplication) getApplication();
		width = this.getWindowManager().getDefaultDisplay().getWidth() - 30;
		height = (int) (width / 2.3);
		initView();
		new GetCouponAsync().execute();
	}

	@Override
	protected void onResume() {
		app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(this, "SESSIONID", "") : app.user.sessionID;
		if(!TextUtils.isEmpty(app.user.sessionID)){
			if(!isLoaddone){
				couponList.clearList();
				new LoadDataAsync().execute(app.user.sessionID);
			}
		}else{
		}
		super.onResume();
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
							couponList.clearList();
							new LoadDataAsync().execute(app.user.sessionID);
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
								if(imageItemsList.size() > 0)
									gallery.setVisibility(View.VISIBLE);
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
							return MassVigUtil.GetImageUrl(url, width, height);
//							return MassVigUtil.GetImageUrl(url, MassVigUtil.dip2px(ManageCouponActivity.this, 320), MassVigUtil.dip2px(ManageCouponActivity.this, 150));
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
			NetImageView i = new NetImageView(ManageCouponActivity.this);
			i.setImageUrlCorner(getItemUrl(position), MassVigContants.PATH, null);
			i.setScaleType(ImageView.ScaleType.FIT_XY);
			i.setLayoutParams(new Gallery.LayoutParams(width, height));
//			MarginLayoutParams mlp = (MarginLayoutParams) gallery.getLayoutParams();
//			mlp.setMargins(15, 15, 15, 0);
			return i;
		}

	}

	private void initView() {
		coupon_number = (EditText)findViewById(R.id.coupon_number);
		dialog = new ProgressDialog(this);
		dialog.setMessage(getString(R.string.loading_data));
		mInflater = LayoutInflater.from(this);
		gallery = (AdGallery)findViewById(R.id.gallery);
		gallery.setSpacing(20);
		gallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				new CreateNewCoupon().execute();
			}
		});
		mImageAdapter = new MyImageAdapter();
		gallery.setAdapter(mImageAdapter);
		listview = (ListView)findViewById(R.id.listview);
		listview.setAdapter(mAdapter);
		((Button)findViewById(R.id.back)).setOnClickListener(this);
		((Button)findViewById(R.id.finish)).setOnClickListener(this);
		((TextView)findViewById(R.id.create_coupon)).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.finish:
			isEdit = !isEdit;
			mAdapter.notifyDataSetChanged();
			break;
		case R.id.create_coupon:
			if(!TextUtils.isEmpty(coupon_number.getText().toString())){
				if(!TextUtils.isEmpty(app.user.sessionID))
					new CreateAsync().execute(app.user.sessionID, coupon_number.getText().toString());
				else
					GotoLogin();
			}else{
				Toast.makeText(this, getString(R.string.enter_coupon), Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
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
			private Button delete;
			private TextView title, time, price, status;
			
			public ViewHolder(){
				itemView = mInflater.inflate(R.layout.manager_coupon_item, null);
				itemView.setTag(this);
				delete = (Button)itemView.findViewById(R.id.delete);
				title = (TextView)itemView.findViewById(R.id.name);
				time = (TextView)itemView.findViewById(R.id.time);
				price = (TextView)itemView.findViewById(R.id.price);
				status = (TextView)itemView.findViewById(R.id.status);
			}
			
			public View getView(final int position){
				final Coupon c = couponList.couponList.get(position);
				title.setText(c.content);
				price.setText(getString(R.string.money_text, c.Discount + ""));
				time.setText(c.StartTime + "~" + c.EndTime);
				if(isEdit)
					delete.setVisibility(View.VISIBLE);
				else
					delete.setVisibility(View.GONE);
				delete.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(!TextUtils.isEmpty(app.user.sessionID)){
							delete_position = position;
							new DeleteAsync().execute(app.user.sessionID, c.CouponNo);
						} else
							GotoLogin();
					}
				});
				switch (c.ViewStatus) {
				case Coupon.USE:
					status.setText(getString(R.string.not_use));
					break;
				case Coupon.USED:
					status.setText(getString(R.string.used));
					break;
				case Coupon.UNUSE:
					status.setText(getString(R.string.unuse));
					break;

				default:
					break;
				}
				return itemView;
			}
		}
	}
	
	class LoadDataAsync extends AsyncTask<String, Void, Boolean>{

		@Override
		protected void onPostExecute(Boolean result) {
			if(dialog != null && dialog.isShowing())
				dialog.dismiss();
			super.onPostExecute(result);
			if(result){
				couponList.addCouponList(tempList);
				tempList.clearList();
				mAdapter.notifyDataSetChanged();
			}
		}

		@Override
		protected void onPreExecute() {
			if (dialog != null && !dialog.isShowing())
				dialog.show();
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			tempList.clearList();
			boolean result = tempList.FetchCouponList(params[0], 0);
			if(tempList.couponList.size() == 0){
				isLoaddone = true;
			}
			return result;
		}
		
	}
	
	class CreateAsync extends AsyncTask<String, Void, Boolean>{

		@Override
		protected void onPostExecute(Boolean result) {
			if(dialog != null && dialog.isShowing())
				dialog.dismiss();
			super.onPostExecute(result);
			if(result){
				couponList.clearList();
				new LoadDataAsync().execute(app.user.sessionID);
			}
		}

		@Override
		protected void onPreExecute() {
			if (dialog != null && !dialog.isShowing())
				dialog.show();
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			String result = MassVigService.getInstance().CreateCoupon(params[0], params[1]);
			try {
				if(!TextUtils.isEmpty(result) && !result.equals("null")){
					JSONObject object = new JSONObject(result);
					int resultCode = object.optInt("ResponseStatus");
					if(resultCode == 0){
						JSONObject data = object.optJSONObject("ResponseData");
						Coupon c = new Coupon();
						c.CouponNo  = data.optString("CouponNo");
						c.content = data.optString("ViewName");
						c.CreateTime = data.optString("CreateTime");
						c.Discount = data.optInt("Discount");
						c.EndTime = data.optString("EndTime");
						c.PromotionType = data.optString("PromotionType");
						c.StartTime = data.optString("StartTime");
						c.ViewStatus = data.optInt("ViewStaus");
						couponList.couponList.add(c);
						return true;
					}else if(resultCode == -6){
						String message = object.optString("ResponseMsg");
						Message msg = new Message();
						msg.obj = message;
						mHandler.sendMessage(msg);
						return false;
					}else{
						return false;
					}
				}
			} catch (Exception e) {
			}
			return false;
		}
		
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Toast.makeText(ManageCouponActivity.this, String.valueOf(msg.obj), Toast.LENGTH_SHORT).show();
		}
		
	};
	
	class DeleteAsync extends AsyncTask<String, Void, Boolean>{

		@Override
		protected void onPostExecute(Boolean result) {
			if(dialog != null && dialog.isShowing())
				dialog.dismiss();
			super.onPostExecute(result);
			if(result){
				mAdapter.notifyDataSetChanged();
			}
		}

		@Override
		protected void onPreExecute() {
			if (dialog != null && !dialog.isShowing())
				dialog.show();
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			String result = MassVigService.getInstance().DeleteCoupon(params[0], params[1]);
			try {
				if(!TextUtils.isEmpty(result) && !result.equals("null")){
					JSONObject object = new JSONObject(result);
					int resultCode = object.optInt("ResponseStatus");
					if(resultCode == 0){
						couponList.couponList.remove(delete_position);
						return true;
					}else{
						return false;
					}
				}
			} catch (Exception e) {
			}
			return false;
		}
		
	}

}
