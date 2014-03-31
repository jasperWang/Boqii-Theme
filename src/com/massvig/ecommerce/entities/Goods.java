package com.massvig.ecommerce.entities;

import java.io.Serializable;
import java.util.HashMap;

public class Goods implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int productID;
	public String name = "";
	public double minPrice = 0;
	public int volume;
	public int Inventory;
	public String imageUrl = "";
	public HashMap<String, String> properties;
	public double realPrice;
	public int commentCount;
	public int ProductPromotionTag;
}
