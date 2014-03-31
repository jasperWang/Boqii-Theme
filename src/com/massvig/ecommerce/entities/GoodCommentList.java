package com.massvig.ecommerce.entities;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

import com.massvig.ecommerce.service.MassVigService;

public class GoodCommentList implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ArrayList<GoodComment> commentList = new ArrayList<GoodComment>();

	/**
	 * 清空列表
	 */
	public void clearList(){
		if(commentList !=null && commentList.size() > 0){
			commentList.clear();
		}
	}
	
	/**
	 * 增加一个List
	 * @param list
	 */
	public void addCommentList(GoodCommentList list){
		commentList.addAll(list.commentList);
	}

	public boolean FetchCommentList(String sessionid, String productID, String startIndex){
		String result = MassVigService.getInstance().GetAdvices(sessionid, productID, startIndex);
		try {
			if(!TextUtils.isEmpty(result) && !result.equals("null")){
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
//			String message = object.getString("ResponseMsg");
			if(resultCode == 0){
				JSONArray list = object.getJSONArray("ResponseData");
				if(list != null && list.length() > 0){
					for(int i = 0; i < list.length(); i++){
						GoodComment c = new GoodComment();
						JSONObject merchantData = list.getJSONObject(i);
						c = DataAnalyse(merchantData);
						commentList.add(c);
					}
				}
				return true;
			}else{
			}
			}
		} catch (Exception e) {
		}
		return false;
	}

	private GoodComment DataAnalyse(JSONObject data) {
		GoodComment c = new GoodComment();
		c.Answer = data.optString("Answer");
		c.NickName = data.optString("NickName");
		c.Question = data.optString("Question");
		c.CreateTime = data.optString("CreateTime");
		return c;
	}
}
