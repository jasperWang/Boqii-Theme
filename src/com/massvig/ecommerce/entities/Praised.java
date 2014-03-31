package com.massvig.ecommerce.entities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Praised {
	public int CustomerID;
	public String NickName;
	public String HeadImgUrl;
//	public int ProductID;
	public int RefID;
	public int ShareSourceType;
	public String ImgUrl;
	public int Gender;
	public String Content;
	public String CreateTime;
	
	public static Praised praised;

	public static Praised getInstance(){
		if(praised == null){
			praised = new Praised();
		}
		return praised;
	}
	
	public Praised UserInfoJSONToBean(JSONObject o){
		try{
			Praised p = new Praised();
			p.CustomerID = o.getInt("CustomerID");
			p.HeadImgUrl = o.getString("HeadImgUrl");
			p.ImgUrl = o.getString("ImgUrl");
			p.NickName = o.getString("NickName");
//			p.ProductID = o.getInt("ProductID");
			p.RefID = o.getInt("RefID");
			p.ShareSourceType = o.getInt("ShareSourceType");
			p.Gender = o.getInt("Gender");
			p.Content = o.getString("Content");
			p.CreateTime = o.getString("CreateTime");
			return p;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
		
	}
	
	public List<Praised> UserInfoJSONToBean(JSONArray userinfoJsons){
		List<Praised> lists = new ArrayList<Praised>();
		for(int i = 0; i < userinfoJsons.length() ;i++){
			try {
				JSONObject json = userinfoJsons.getJSONObject(i);
				Praised userinfo = UserInfoJSONToBean(json);
				lists.add(userinfo);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return lists;
	}
	
}
