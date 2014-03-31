package com.massvig.ecommerce.widgets;

import java.io.Serializable;

import android.content.res.AssetManager;
/**
 * 
 * @author DuJun
 *
 */
public class FlowTag implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private int flowId;
	private String fileName;
	public final static int what = 1;
	public final static int CLICK = 2;
	public final static int USER = 3;
	public final static int LOCATION = 4;
	public final static int SHARE = 5;
	public final static int PRAISE = 6;


	public int ShareID;
//	public int ProductID;
	public int RefID;
	public int ShareSourceType;
	public String Address;
	public String Content;
	public int CustomerID;
	public String ImgUrl;
	public int PraiseCount;
	public int SharedCount;
	public String NickName;
	public String HeadImgUrl;
	public boolean CanPraise;
	public String createTime;
	public int Height;
	public int Width;
	
//	private int itemId;
//	private long NumIid;
//	private String TbkURL;
//	private String TbkShopURL;
//	private int TbkShopCredit;
//	
//	private String detail;
//	private String userName;
//	private String userImg;
//	private String address;
//	private int share;
//	private int praise;
//	private float lat;
//	private float lng;
//	private int type;
//	private int userId;
//	private int postId;
	
//	public int getPostId() {
//		return postId;
//	}
//
//	public void setPostId(int postId) {
//		this.postId = postId;
//	}
//
//	public int getUserId() {
//		return userId;
//	}
//
//	public void setUserId(int userId) {
//		this.userId = userId;
//	}
//
//	public int getType() {
//		return type;
//	}
//
//	public void setType(int type) {
//		this.type = type;
//	}
//
//	public String getDetail() {
//		return detail;
//	}
//
//	public void setDetail(String detail) {
//		this.detail = detail;
//	}
//
//	public String getUserName() {
//		return userName;
//	}
//
//	public void setUserName(String userName) {
//		this.userName = userName;
//	}
//
//	public String getUserImg() {
//		return userImg;
//	}
//
//	public void setUserImg(String userImg) {
//		this.userImg = userImg;
//	}
//
//	public String getAddress() {
//		return address;
//	}
//
//	public void setAddress(String address) {
//		this.address = address;
//	}
//
//	public int getShare() {
//		return share;
//	}
//
//	public void setShare(int share) {
//		this.share = share;
//	}
//
//	public int getPraise() {
//		return praise;
//	}
//
//	public void setPraise(int praise) {
//		this.praise = praise;
//	}
//
//	public float getLat() {
//		return lat;
//	}
//
//	public void setLat(float lat) {
//		this.lat = lat;
//	}
//
//	public float getLng() {
//		return lng;
//	}
//
//	public void setLng(float lng) {
//		this.lng = lng;
//	}
//
//	public int getItemId() {
//		return itemId;
//	}
//
//	public void setItemId(int itemId) {
//		this.itemId = itemId;
//	}
//
//	public long getNumIid() {
//		return NumIid;
//	}
//
//	public void setNumIid(long numIid) {
//		NumIid = numIid;
//	}
//
//	public String getTbkURL() {
//		return TbkURL;
//	}
//
//	public void setTbkURL(String tbkURL) {
//		TbkURL = tbkURL;
//	}
//
//	public String getTbkShopURL() {
//		return TbkShopURL;
//	}
//
//	public void setTbkShopURL(String tbkShopURL) {
//		TbkShopURL = tbkShopURL;
//	}
//
//	public int getTbkShopCredit() {
//		return TbkShopCredit;
//	}
//
//	public void setTbkShopCredit(int tbkShopCredit) {
//		TbkShopCredit = tbkShopCredit;
//	}

	public int getFlowId() {
		return flowId;
	}

	public void setFlowId(int flowId) {
		this.flowId = flowId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	private AssetManager assetManager;
	private int ItemWidth;

	public AssetManager getAssetManager() {
		return assetManager;
	}

	public void setAssetManager(AssetManager assetManager) {
		this.assetManager = assetManager;
	}

	public int getItemWidth() {
		return ItemWidth;
	}

	public void setItemWidth(int itemWidth) {
		ItemWidth = itemWidth;
	}
}
