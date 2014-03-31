package com.massvig.ecommerce.entities;

import java.io.Serializable;

/**
 * 
 * @author DuJun
 *
 */
public class Comment implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int CommentID;
	public int CustomerID;
	public String NickName;
	public String HeadImgUrl;
	public int ProductID;
	public String Content;
	public String CreateTime;
}
