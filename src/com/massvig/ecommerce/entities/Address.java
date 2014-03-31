package com.massvig.ecommerce.entities;

import java.io.Serializable;

import org.json.JSONObject;

import com.massvig.ecommerce.service.MassVigService;

public class Address implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int customerAddressID = -1;
	public String name = "";
	public String mobile = "";
	public int regionID = -1;
	public String address = "";
	public String zipcode = "";
	public String email = "";
	public String shengshiqu = "";
	public boolean isDefault = false;
	
	public void StringToAddress(String text){
		String[] elements = text.split(",");
		this.customerAddressID = Integer.valueOf(elements[0]);
		this.name = elements[1];
		this.mobile = elements[2];
		this.regionID = Integer.valueOf(elements[3]);
		this.shengshiqu = elements[4];
		this.address = elements[5];
		this.zipcode = elements[6];
		this.email = elements[7];
		this.isDefault = (Integer.valueOf(elements[8]) == 0) ;
	}
	
	@Override
	public String toString() {
		return customerAddressID + "," + name + "," + mobile + ","+ regionID + "," + shengshiqu.replace(",", " ") + "," + address.replace(",", " ") + ","+ zipcode + "," + email + "," + (isDefault ? "0" : "-1");
	}

	/**
	 * 添加地址
	 * @param sessionid
	 * @return
	 */
	public int AddAddress(String sessionid){
		String result = MassVigService.getInstance().AddAddress(sessionid, name, mobile, regionID, address, zipcode, email);
		try {
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
			JSONObject o = object.getJSONObject("ResponseData");
			customerAddressID = o.optInt("CustomerAddressID");
			return resultCode;
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * 删除地址
	 * @param sessionid
	 * @return
	 */
	public int DeleteAddress(String sessionid){
		String result = MassVigService.getInstance().DeleteAddress(sessionid, customerAddressID);
		try {
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
			return resultCode;
		} catch (Exception e) {
			return -1;
		}
	}
	
	/**
	 * 修改地址
	 * @param sessionid
	 * @return
	 */
	public int ModifyAddress(String sessionid){
		String result = MassVigService.getInstance().ModifyAddress(sessionid, customerAddressID, name, mobile, regionID, address, zipcode, email);
		try {
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
			return resultCode;
		} catch (Exception e) {
			return -1;
		}
	}
	
	/**
	 * 设置默认地址
	 * @param sessionid
	 * @return
	 */
	public int SetDefaultAddress(String sessionid){
		String result = MassVigService.getInstance().SetDefaultAddress(sessionid, customerAddressID);
		try {
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
			return resultCode;
		} catch (Exception e) {
			return -1;
		}
	}
}
