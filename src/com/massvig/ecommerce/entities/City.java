package com.massvig.ecommerce.entities;

import java.io.Serializable;

public class City implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1092560578541761126L;
	public int ResionID;
	public String RegionName;
	public int ParentID;
	public int Depth;
	public String ZipCode;
	
}
