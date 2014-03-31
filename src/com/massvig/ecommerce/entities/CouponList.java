package com.massvig.ecommerce.entities;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

import com.massvig.ecommerce.service.MassVigService;

public class CouponList implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ArrayList<Coupon> couponList = new ArrayList<Coupon>();

	/**
	 * 清空列表
	 */
	public void clearList(){
		if(couponList !=null && couponList.size() > 0){
			couponList.clear();
		}
	}
	
	/**
	 * 增加一个List
	 * @param list
	 */
	public void addCouponList(CouponList list){
		couponList.addAll(list.couponList);
	}
	
	/**
	 * 获取优惠券
	 * @param sessionid
	 * @param type 0：所有的 1：有效的
	 * @return
	 */
	public boolean FetchCouponList(String sessionid, int type){
		String result = MassVigService.getInstance().getCouponList(sessionid);
		try {
			if(!TextUtils.isEmpty(result) && !result.equals("null")){
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
//			String message = object.getString("ResponseMsg");
			if(resultCode == 0){
				JSONArray list = object.getJSONArray("ResponseData");
				if(list != null && list.length() > 0){
					for(int i = 0; i < list.length(); i++){
						Coupon coupon = new Coupon();
						JSONObject merchantData = list.getJSONObject(i);
						coupon = DataAnalyse(merchantData);
						if(type == 1){
							if(coupon.ViewStatus == Coupon.USE)
								couponList.add(coupon);
						}else{
							couponList.add(coupon);	
						}
					}
				}
				return true;
			}else{
			}
			}
		} catch (Exception e) {
		}
		return false;
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
		return c;
	}
}
