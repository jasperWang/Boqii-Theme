package com.massvig.ecommerce.managers;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.massvig.ecommerce.entities.Coupon;

import android.os.AsyncTask;

public class CouponManager {

	private Listener listener;
	public Coupon coupon = new Coupon();
	public HashMap<String,Float> map = new HashMap<String, Float>();

	public void setListener(Listener l) {
		this.listener = l;
	};

	/**
	 * 检查
	 * @param sessionid
	 * @param productSpecID
	 * @param quantity
	 */
	public void CheckConpon(String sessionid, int customerAddressID, String payment, int billType, String billTitle, String productSpecID, String quantity, int expressage) {
		new CheckCouponAsync().execute(sessionid, customerAddressID + "", coupon.content, payment, billType + "", billTitle, productSpecID + "", quantity + "", expressage + "");
	};

	public interface Listener {
		void Success();
		void Failed(String message);
	}
	
	public class CheckCouponAsync extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			return coupon.CheckCoupon(params[0], Integer.valueOf(params[1]), params[2], params[3], Integer.valueOf(params[4]), params[5], params[6], params[7], Integer.valueOf(params[8]));
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				JSONObject object = new JSONObject(result);
				int resultCode = object.getInt("ResponseStatus");
				if(resultCode == 0){
					map = new HashMap<String, Float>();
					JSONArray a = object.getJSONArray("ResponseData");
					for (int i = 0; i < a.length(); i++) {
						JSONObject o = a.getJSONObject(i);
						map.put(o.getString("CouponNo"), Float.valueOf(o.getString("Discount")));
					}
					listener.Success();
				}else{
					String message = object.getString("ResponseMsg");
					listener.Failed(message);
				}
			} catch (Exception e) {
				listener.Failed("Failed");
			}
		}
		
	}

}
