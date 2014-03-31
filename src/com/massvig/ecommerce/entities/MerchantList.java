package com.massvig.ecommerce.entities;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.massvig.ecommerce.service.MassVigService;

public class MerchantList {

	private ArrayList<Merchant> merchantList;
	
	public MerchantList(){
		merchantList = new ArrayList<Merchant>();
	}
	
	/**
	 * 清空列表
	 */
	public void clearmerchantList(){
		if(merchantList !=null && merchantList.size() > 0){
			merchantList.clear();
		}
	}
	/**
	 * 删除一个Merchant
	 * @param position
	 */
	public void deleteMerchant(int position){
		merchantList.remove(position);
	}
	
	/**
	 * 增加一个Merchant
	 * @param Merchant
	 */
	public void addMerchant(Merchant Merchant){
		merchantList.add(Merchant);
	}
	
	/**
	 * 增加一个merchantList
	 * @param list
	 */
	public void addmerchantList(MerchantList list){
		merchantList.addAll(list.getmerchantList());
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Merchant> getmerchantList() {
		// TODO Auto-generated method stub
		return merchantList;
	}

	/**
	 * 获取列表长度
	 * @return
	 */
	public int getCount(){
		if(merchantList != null){
			return merchantList.size();
		}else{
			return 0;
		}
	}
	
	/**
	 * 获取指定Merchant
	 * @param position
	 * @return
	 */
	public Merchant getMerchant(int position){
		if(merchantList.size() > position)
			return merchantList.get(position);
		else return new Merchant();
	}
	
	/**
	 * 获取指定Merchant的ID
	 * @param position
	 * @return
	 */
	public String getMerchantID(int position){
		return getMerchant(position).StoreID + "";
	}
	
	/**
	 * 获取Merchant列表
	 * @param merchantID 商户id
	 * @param categoryID 分类id
	 * @return 
	 */
	public boolean FetchmerchantList(String sessionID, double lon, double lat, int startIndex, int takeNum){
		String result = MassVigService.getInstance().GetAroundMerchantStore(sessionID, lon, lat, startIndex, takeNum);
		try {
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
//			String message = object.getString("ResponseMsg");
			if(resultCode == 0){
				JSONArray list = object.getJSONArray("ResponseData");
				if(list != null && list.length() > 0){
					for(int i = 0; i < list.length(); i++){
						Merchant Merchant = new Merchant();
						JSONObject merchantData = list.getJSONObject(i);
						Merchant = DataAnalyse(merchantData);
						merchantList.add(Merchant);
					}
				}
				return true;
			}else{
			}
		} catch (Exception e) {
		}
		return false;
	}
	
	/**
	 * 解析Merchant，返回Merchant对象
	 * @param data
	 * @return NearPerson
	 */
	private Merchant DataAnalyse(JSONObject data){
		Merchant m = new Merchant();
		try {
			m.Description = data.getString("Description");
			m.Distance = data.getDouble("Distance");
			m.MerchantID = data.getInt("MerchantID");
			m.StoreID = data.getInt("StoreID");
			m.StoreName = data.getString("StoreName");
			m.Lat = data.getDouble("Lat");
			m.Lon = data.getDouble("Lon");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return m;
	}
}
