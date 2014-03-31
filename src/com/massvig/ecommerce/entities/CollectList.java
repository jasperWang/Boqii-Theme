package com.massvig.ecommerce.entities;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.massvig.ecommerce.service.MassVigService;

public class CollectList {
	private ArrayList<Goods> collectlist = new ArrayList<Goods>();
	
	public void clearCollectList(){
		if(collectlist !=null && collectlist.size() > 0){
			collectlist.clear();
		}
	}
	public void delete(int position){
		collectlist.remove(position);
	}
	public void addcollect(Goods collect){
		collectlist.add(collect);
	}
	public ArrayList<Goods> getCollectlist(){
		return collectlist;
	}
	public Goods getcollect(int position){
		if(collectlist != null && collectlist.size() > 0)
			return collectlist.get(position);
		else
			return null;
		
		
	}
	public String getCollectID(int position){
		return getcollect(position).productID+"";
	}
	public void addCollectList(CollectList list){
		collectlist.addAll(list.getCollectlist());
	}
	public int getCount(){
		if(collectlist!=null&&collectlist.size()>0){
			return collectlist.size();
		}
		else{
			return 0;
		}
	}
	public int deleteCollect(String sessionID,int productID){
		String result=MassVigService.getInstance().delteCollection(sessionID,productID);
		try{
			JSONObject object=new JSONObject(result);
			int resultCode=object.getInt("ResponseStatus");
			if(resultCode==0){
				return resultCode;
			}
			else{
				return resultCode;
			}					
		}catch(Exception e){
			e.printStackTrace();
		}
		return -1;
	}
	public int AddCollect(int productID,String sessionID){
		String result=MassVigService.getInstance().addCollection(productID, sessionID);
		try{
			JSONObject object=new JSONObject(result);
			int resultCode=object.getInt("ResponseStatus");
			if(resultCode==0){
				return resultCode;			
			}else{
				return resultCode;
			}		
		}catch(Exception e){
			e.printStackTrace();
		}
		return -1;
	}
	
	public int fetchcollectlist(String sessionID){
		String result=MassVigService.getInstance().GetCollection(sessionID);
		try{
			JSONObject object=new JSONObject(result);
			int resultCode=object.optInt("ResponseStatus");
			if(!TextUtils.isEmpty(result)&&!result.equals(null)){
				if(resultCode==0){
					JSONArray list=object.getJSONArray("ResponseData");
					for(int i=0;i<list.length();i++){
						Goods collect=new Goods();
						JSONObject merchantData=list.getJSONObject(i);
						collect=DataAnalyse(merchantData);
						collectlist.add(collect);			
					}
				}
			}
			return resultCode;
		}catch(Exception e){
			return -1;
		}
		
	}
	private Goods DataAnalyse(JSONObject data){
		Goods collect=new Goods();
		try {
			collect.productID = data.optInt("ProductID");
			collect.name = data.optString("Name");
			collect.ProductPromotionTag = data.optInt("ProductPromotionTag");
			collect.minPrice = data.optDouble("MinPrice");
			collect.volume = data.optInt("Volume");
			collect.imageUrl = data.optString("MainImgUrl");
			collect.commentCount = data.optInt("CommentCount");
			collect.realPrice = data.optDouble("MinOriginPrice");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return collect;
	}
	
	

}
