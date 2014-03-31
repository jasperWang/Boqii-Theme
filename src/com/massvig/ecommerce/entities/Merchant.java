package com.massvig.ecommerce.entities;

import java.io.Serializable;

public class Merchant implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int StoreID;
	public int MerchantID;
	public String StoreName;
	public String Description;
	public double Distance;
	public double Lon = 0.0;
	public double Lat = 0.0;
}
