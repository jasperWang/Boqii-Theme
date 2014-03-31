package com.massvig.ecommerce.entities;

import java.io.Serializable;

import org.json.JSONObject;

import com.massvig.ecommerce.service.MassVigService;

public class GoodsDetail implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int productID;
	public String name;
	public double minPrice;
	public int volume;
	public String imageUrl;
	public double originPrice;
	public String description;
	public String productData;
	public String specInfo = "";//规格
	public String imageLists = "";
	public int CommentCount, PraiseCount, ShareCount, StampCount;
	public boolean CanPraise,CanStamp;
	public String DescUrl = "";
	public CommentList commentList = new CommentList();
	public boolean CanShoppingCart;
	public boolean NeedLogistics;
	public int ProductPromotionTag;//0:无 1:特价
	
	public int FetchData(String sessionid){
		String result = MassVigService.getInstance().GetProduct(sessionid, productID + "");
		try {
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
//			String message = object.getString("ResponseMsg");
			if(resultCode == 0){
				JSONObject data = object.getJSONObject("ResponseData");
				this.description = data.getString("Description");
				this.imageUrl = data.getString("MainImgUrl");
				this.minPrice = data.getDouble("MinPrice");
				this.originPrice = data.getDouble("MinOriginPrice");
				this.productData = data.getString("ProductData");
				this.volume = data.getInt("Volume");
				this.name = data.getString("Name");
				this.CommentCount = data.getInt("CommentCount");
				this.PraiseCount = data.getInt("PraisedCount");
				this.ShareCount = data.getInt("SharedCount");
				this.StampCount = data.getInt("StampCount");
				this.CanPraise = data.getBoolean("CanPraise");
				this.CanStamp = data.getBoolean("CanStamp");
				this.DescUrl = data.getString("DescUrl");
				this.CanShoppingCart = data.optBoolean("CanShoppingCart");
				this.NeedLogistics = data.optBoolean("NeedLogistics");
				this.ProductPromotionTag = data.optInt("ProductPromotionTag");
				return resultCode;
			}else{
				return resultCode;
			}
		} catch (Exception e) {
			return -2;
		}
	}
	
	public boolean FetchImgs(){
		String result = MassVigService.getInstance().GetProdcutImgs(productID + "");
		try {
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
//			String message = object.getString("ResponseMsg");
			if(resultCode == 0){
				imageLists = object.getString("ResponseData");
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean FetchComment(int productID, int startIndex){
		return commentList.FetchCommentList(productID, startIndex, 10);
	}
	
	public int AddComment(String sessionid, String atCustomerID, String comment){
		String result = MassVigService.getInstance().AddComment(sessionid, productID, atCustomerID, comment);
		try {
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
//			String message = object.getString("ResponseMsg");
			if(resultCode == 0){
				specInfo = object.getString("ResponseData");
				return resultCode;
			}else{
				return resultCode;
			}
		} catch (Exception e) {
			return -1;
		}
	}
	
	
	public boolean FetchParams(){
		String result = MassVigService.getInstance().GetProductSpecInfo(productID + "");
		try {
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
//			String message = object.getString("ResponseMsg");
			if(resultCode == 0){
				specInfo = object.getString("ResponseData");
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	
		
	}
}
