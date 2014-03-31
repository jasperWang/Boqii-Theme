package com.massvig.ecommerce.entities;

import java.io.Serializable;

public class Order implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int productID;//商品id
	public int payType;//支付方式
	public String billTitle;//发票title
	public Coupon[] couponList;//优惠券
	public Address currentAddress;
	public AddressList addressList;
	public int transCost;//运费
	public Product product;

//	//TODO
//	public boolean Pay(){
//		return true;
//	}
//
//	//TODO
//	public boolean FetchAddressList(){
//		return true;
//	}
//	
//	//TODO
//	public boolean FetchPayType(){
//		return true;
//	}
}
