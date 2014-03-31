package com.massvig.ecommerce.entities;

import java.io.Serializable;

public class Post implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int postID;
//	public int ProductID;
	public int RefID;
	public int ShareSourceType;
	public String imageUrl = "";
	public String username = "";
	public String detail = "";
	public String userimgurl = "";
	public String address = "";
	public int shared = 0;
	public int praise = 0;
	public boolean CanPraise = true;
	public boolean CanShare = true;
	public int customerID;
	public String ShareSourceMsg1 = "";
	public String ShareSourceMsg2 = "";
	public String createTime = "";
	public int CommentCount = 0;
}
