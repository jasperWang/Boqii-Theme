package com.massvig.ecommerce.entities;

import java.io.Serializable;

public class Message implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String FromEJID;
	public String ToEJID;
	public String MessageBody;
	public String CreateTime;
	public int IsRead = 0;
	public boolean IsComMsg = true;
	public int IsShowTime = 0;
	public boolean isFailed = false;
}
