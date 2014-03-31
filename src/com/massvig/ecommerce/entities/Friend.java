package com.massvig.ecommerce.entities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Friend {
	public int CustomerID;
	public String HeadImgUrl;
	public String NickName;
	public int Gender;
	public int SharedCount;
	public int FollowingCustomerCount;
	public int FansCustomerCount;
	public int Relation;
	public static Friend friend;

	public static Friend getInstance(){
		if(friend == null){
			friend = new Friend();
		}
		return friend;
	}
	
	public Friend UserInfoJSONToBean(JSONObject o){
		try{
			Friend u = new Friend();
			u.CustomerID = o.getInt("CustomerID");
			u.FansCustomerCount = o.getInt("FansCustomerCount");
			u.FollowingCustomerCount = o.getInt("FollowingCustomerCount");
			u.Gender = o.getInt("Gender");
			u.HeadImgUrl = o.getString("HeadImgUrl");
			u.NickName = o.getString("NickName");
			u.Relation = o.getInt("Relation");
			u.SharedCount = o.getInt("SharedCount");
			return u;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
		
	}
	
	public List<Friend> UserInfoJSONToBean(JSONArray userinfoJsons){
		List<Friend> lists = new ArrayList<Friend>();
		for(int i = 0; i < userinfoJsons.length() ;i++){
			try {
				JSONObject json = userinfoJsons.getJSONObject(i);
				Friend userinfo = UserInfoJSONToBean(json);
				lists.add(userinfo);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return lists;
	}
}
