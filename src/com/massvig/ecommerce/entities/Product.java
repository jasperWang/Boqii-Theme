package com.massvig.ecommerce.entities;

import java.io.Serializable;
import java.util.HashMap;

public class Product implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int id = 0;
	public String name = "";
	public HashMap<String, String> selectedProperties = new HashMap<String, String>();//选好的规格
	public float totalMoney = 0;
	public String imageUrl = "";//图片地址
	public int count = 0;
	public int productSpecID = 0;//规格id
	public boolean isChecked = true;
	public int Inventory = 0;//库存
	public float oriPrice = 0;
	public float signalPrice = 0;
	public int ShoppingCartProductStatus = 0;
	public String specInfo = "";//规格
	public int OrderDetailStatus = 0;
	public int OrderDetailID = 0;
	public boolean IsGift = false;
}
