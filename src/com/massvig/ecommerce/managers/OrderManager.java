package com.massvig.ecommerce.managers;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.massvig.ecommerce.entities.Address;
import com.massvig.ecommerce.entities.AddressList;
import com.massvig.ecommerce.entities.Order;
import com.massvig.ecommerce.entities.Product;
import com.massvig.ecommerce.service.MassVigService;
import com.massvig.ecommerce.utilities.MassVigContants;

public class OrderManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int FREIGHT = 1;
	public static final int GETADDRESS = 2;
	public static final int COMFIRM = 3;
	public static final int REFUND = 4;
	public static final int CANCEL = 5;
	public String coupons = "";
	private ArrayList<Product> products = new ArrayList<Product>();
//	private Product product;
	private Order order;
	private LoadListener listener;
	public AddressList addressList = new AddressList();
	private Context context;
	public Address address = new Address();;
	public double freight = 0;
	public int payType = 0;
	public String billText = "";
	public String billType = "";
	public int orderID = 0;
	public int status;
	public String orderNO;
	public String LogisticsInfoList;
	public String payment = "";

	public String billTitle;
	public int expressage = -1;
	public boolean isWebOrder = false;

	public Address GetDefalultAddress(){
		if(addressList.getCount() > 0){
			for (int i = 0; i < addressList.getCount(); i++) {
				Address add = addressList.getAddress(i);
				if(add.isDefault){
					this.address = add;
					return address;
				}
			}
		}
		return address;
	}
	
	public void setListener(LoadListener l){
		this.listener = l;
	}
	
	public OrderManager(Context c){
		this.context = c;
	}
	
	public Order getOrder() {
		return order;
	}
	public void setOrder(Order order) {
		this.order = order;
	}
	
	public void addProduct(Product p){
		if(products != null)
			products.add(p);
	}
	
	public ArrayList<Product> getProducts(){
		return products;
	}
//	public Product getProduct() {
//		return product;
//	}
	public void setProducts(ArrayList<Product> p){
		this.products = p;
	}
//	public void setProduct(Product product) {
//		this.product = product;
//	}
	
	public void CalculateFreight(String sessionid, String ids, String quantities){
		String orderDetailIDs = ids;
//		String quantities = quantities;
//		if(products != null && products.size() > 0){
//			for (int i = 0; i < products.size(); i++) {
//				orderDetailIDs += products.get(i).productSpecID;
//				quantities += products.get(i).count;
//				if(i != products.size() - 1){
//					orderDetailIDs += ",";
//					quantities += ",";
//				}
//			}
//		}
		new CalculateFreightAsync().execute(sessionid, address.customerAddressID + "", coupons, payType + "", billType, billText, orderDetailIDs, quantities, expressage + "");
//		new CalculateFreightAsync().execute(sessionid, product.productSpecID + "", product.count + "", address.regionID + "");
	}
	
	private class CalculateFreightAsync extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			return MassVigService.getInstance().CalculateFreight(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7], Integer.valueOf(params[8]));
//			return MassVigService.getInstance().CalculateFreight(params[0], Integer.valueOf(params[1]), Integer.valueOf(params[2]), Integer.valueOf(params[3]));
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(!TextUtils.isEmpty(result)){
				try {
					JSONObject data = new JSONObject(result);
					if(data.getInt("ResponseStatus") == 0){
						JSONObject o = data.getJSONObject("ResponseData");
						freight = o.optDouble("Freight");
						listener.Success(FREIGHT);
					}else
						listener.Failed(FREIGHT);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					listener.Failed(FREIGHT);
				}
			}else
				listener.Failed(FREIGHT);
		}
		
	}
	
	private class GetAddressAsync extends AsyncTask<String, Void, Integer>{

		@Override
		protected Integer doInBackground(String... params) {
			return addressList.FetchAddressList(params[0]);
		}

		@Override
		protected void onPostExecute(Integer result) {
			if(result == 0){
				listener.Success(GETADDRESS);
			}else if(result == MassVigContants.SESSIONVAILED){
				listener.SessionFailed();
			}else{
				listener.Failed(GETADDRESS);
			}
		}
		
	}
	
	private SharedPreferences getSharedPreferences(){
		return context.getSharedPreferences("AddressData",Context.MODE_APPEND);
	}
	
	public Address GetLocalAddress() {
		String add = getSharedPreferences().getString("address", "");
		if (!TextUtils.isEmpty(add)) {
			address.StringToAddress(add);
			return address;
		} else {
			return null;
		}
	}
	
	public void SaveLocalAddress(Address address){
		getSharedPreferences().edit().putString("address", address.toString()).commit();
	}
	
	public void GetAddressList(String sessionid){
		new GetAddressAsync().execute(sessionid);
	}
	
	public void GetPayType(){
		new AsyncTask<Void, Void, String>() {

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				payment = result;
				try {
					JSONObject obj = new JSONObject(payment);
					String respondata =  obj.optString("ResponseData");
					JSONArray array = new JSONArray(respondata);
					for(int i = 0 ; i < array.length(); i++){
						JSONObject object = array.getJSONObject(i);
						if(object.optBoolean("IsDefault")){
							payType = object.optInt("PaymentMethod");
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				listener.Payment(result);
			}

			@Override
			protected String doInBackground(Void... params) {
				return MassVigService.getInstance().GetPaymentMethod();
			}
		}.execute();
	}
	
	public void ConfirmShiped(final String sessionid){
		new AsyncTask<Void, Void, Integer>(){

			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(result == 0){
					listener.Success(COMFIRM);
				}else if(status == MassVigContants.SESSIONVAILED){
					listener.SessionFailed();
				}else{
					listener.Failed(COMFIRM);
				}
			}

			@Override
			protected Integer doInBackground(Void... params) {
				int status = -1;
				String result = MassVigService.getInstance().ConfirmShiped(sessionid, orderID);
				if(!TextUtils.isEmpty(result)){
					try {
						JSONObject data = new JSONObject(result);
						status = data.getInt("ResponseStatus");
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				return status;
			}}.execute();
	};
	
//	public void ApplyForRefund(final String sessionid){
//		new AsyncTask<Void, Void, Integer>(){
//
//			@Override
//			protected void onPostExecute(Integer result) {
//				super.onPostExecute(result);
//				if(result == 0){
//					listener.Success(REFUND);
//				}else if(status == MassVigContants.SESSIONVAILED){
//					listener.SessionFailed();
//				}else{
//					listener.Failed(REFUND);
//				}
//			}
//
//			@Override
//			protected Integer doInBackground(Void... params) {
//				int status = -1;
//				String orderDetailIDs = "";
//				if(products != null && products.size() > 0){
//					for (int i = 0; i < products.size(); i++) {
//						orderDetailIDs += products.get(i).OrderDetailID;
//						if(i != products.size() - 1)
//							orderDetailIDs += ",";
//					}
//				}
//				String result = MassVigService.getInstance().ApplyForRefund(sessionid, orderID + "", orderDetailIDs);
//				if(!TextUtils.isEmpty(result)){
//					try {
//						JSONObject data = new JSONObject(result);
//						status = data.getInt("ResponseStatus");
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//				}
//				return status;
//			}}.execute();
//	};
	
	public void CancelOrder(final String sessionid){
		new AsyncTask<Void, Void, Integer>(){

			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(result == 0){
					listener.Success(CANCEL);
				}else if(status == MassVigContants.SESSIONVAILED){
					listener.SessionFailed();
				}else{
					listener.Failed(CANCEL);
				}
			}

			@Override
			protected Integer doInBackground(Void... params) {
				int status = -1;
				String result = MassVigService.getInstance().CancelOrder(sessionid, orderID);
				if(!TextUtils.isEmpty(result)){
					try {
						JSONObject data = new JSONObject(result);
						status = data.getInt("ResponseStatus");
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				return status;
			}}.execute();
	}
	
	public void FetchData(){};
	public void Pay(){};
	
	public interface LoadListener{
		void Success(int index);
		void Failed(int index);
		void Payment(String result);
		void SessionFailed();
	}
	
}
