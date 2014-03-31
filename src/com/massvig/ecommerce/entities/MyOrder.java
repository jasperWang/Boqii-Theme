package com.massvig.ecommerce.entities;

import java.io.Serializable;

public class MyOrder implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int OrderID;
	public boolean IsWebOrder;
	public String Address;
	public String BillTitle;
	public int BillType;
	public String Consignee;
	public double DiscountAmount;
	public String Email;
	public int FreightCharges;
	public String Mobile;
	public String OrderNo;
	public int OrderStatus;
	public float PayAmount;
	public int PaymentMethod;
	public int ProductAmount;
	public int RegionID;
	public String ZipCode;
	public String CreateTime;
	public String OrderDetails;
	public String OrderCoupons;
	public int LogisticsStatus;
	public int PayStatus;
	public String OrderAction = "";
	public String LogisticsInfoList;
	public double PromotionDiscount;
}
