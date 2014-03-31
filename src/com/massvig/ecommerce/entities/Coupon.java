package com.massvig.ecommerce.entities;

import java.io.Serializable;

import com.massvig.ecommerce.service.MassVigService;

public class Coupon implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static int USE = 0;
	public final static int USED = 1;
	public final static int UNUSE = 2;
	public String CouponNo;
	public String PromotionType;
	public String StartTime, EndTime, CreateTime;
	public int ViewStatus = 0, Discount;
	public String imageUrl = "";
	
	public int id;
	public String content;
	public int price;
	public boolean IsUseed, CanUse;
	public boolean isCheck = false;

	public String CheckCoupon(String sessionid, int customerAddressID, String coupons, String payment, int billType, String billTitle, String productSpecID, String quantity, int expressage){
		return MassVigService.getInstance().CheckCouponLogin(sessionid, customerAddressID, content, payment, billType, billTitle, productSpecID, quantity, expressage);
	}
	
}
