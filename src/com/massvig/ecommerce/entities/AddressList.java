package com.massvig.ecommerce.entities;

import java.io.Serializable;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.massvig.ecommerce.service.MassVigService;

public class AddressList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Address> addressList;
	
	public AddressList(){
		addressList = new ArrayList<Address>();
	}
	
	/**
	 * 清空列表
	 */
	public void clearAddressList(){
		if(addressList !=null && addressList.size() > 0){
			addressList.clear();
		}
	}
	/**
	 * 删除一个Address
	 * @param position
	 */
	public void deleteAddress(int position){
		addressList.remove(position);
	}
	
	/**
	 * 增加一个Address
	 * @param Address
	 */
	public void addAddress(Address address){
		addressList.add(address);
	}
	
	/**
	 * 增加一个addressList
	 * @param list
	 */
	public void addAddressList(AddressList list){
		addressList.addAll(list.getAddressList());
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Address> getAddressList() {
		return addressList;
	}

	/**
	 * 获取列表长度
	 * @return
	 */
	public int getCount(){
		if(addressList != null){
			return addressList.size();
		}else{
			return 0;
		}
	}
	
	/**
	 * 获取指定address
	 * @param position
	 * @return
	 */
	public Address getAddress(int position){
		return addressList.get(position);
	}
	
	/**
	 * 获取指定address的ID
	 * @param position
	 * @return
	 */
	public String getAddressID(int position){
		return getAddress(position).customerAddressID + "";
	}
	
	/**
	 * 获取Address列表（不带运费）
	 * @param merchantID 商户id
	 * @param categoryID 分类id
	 * @return 
	 */
	public int FetchAddressList(String sessionid){
		String result = MassVigService.getInstance().GetCustomerAddresses(sessionid);
		try {
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
//			String message = object.getString("ResponseMsg");
			if(resultCode == 0){
				JSONArray list = object.getJSONArray("ResponseData");
				if(list != null && list.length() > 0){
					for(int i = 0; i < list.length(); i++){
						Address address = new Address();
						JSONObject data = list.getJSONObject(i);
						address = DataAnalyse(data);
						addressList.add(address);
					}
				}
				return resultCode;
			}else{
				return resultCode;
			}
		} catch (Exception e) {
		}
		return -1;
	}
	
	/**
	 * 获取Address列表（带运费）
	 * @param merchantID 商户id
	 * @param categoryID 分类id
	 * @return 
	 */
	public boolean FetchAddressListWithFreight(String sessionid, int productSpecID, int quantity){
		String result = MassVigService.getInstance().GetCustomerAddresses(sessionid);
//		String result = MassVigService.getInstance().GetCustomerAddressesWithFreight(sessionid, productSpecID, quantity);
		try {
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
//			String message = object.getString("ResponseMsg");
			if(resultCode == 0){
				JSONArray list = object.getJSONArray("ResponseData");
				if(list != null && list.length() > 0){
					for(int i = 0; i < list.length(); i++){
						Address address = new Address();
						JSONObject data = list.getJSONObject(i);
						address = DataAnalyse(data);
						addressList.add(address);
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
	 * 解析address，返回address对象
	 * @param data
	 * @return NoQaddress
	 */
	//TODO
	private Address DataAnalyse(JSONObject data){
		Address address = new Address();
		try {
			address.name = data.getString("CustomerName");
			address.address = data.getString("Address");
			address.customerAddressID = data.getInt("CustomerAddressID");
			address.email = data.getString("Email");
			address.isDefault = data.getBoolean("IsDefault");
			address.mobile = data.getString("Mobile");
			address.zipcode = data.getString("ZipCode");
			JSONArray arr = data.getJSONArray("Regions");
			for (int i = 0; i < arr.length(); i++) {
				address.shengshiqu += arr.getJSONObject(i).getString("RegionName") + ",";
				address.regionID = arr.getJSONObject(i).getInt("RegionID");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return address;
	}
	
	//TODO
	public void updateAddress(){
		
	}
}
