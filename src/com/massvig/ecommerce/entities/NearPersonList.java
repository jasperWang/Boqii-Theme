package com.massvig.ecommerce.entities;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.massvig.ecommerce.service.MassVigService;
import com.massvig.ecommerce.utilities.MassVigContants;

public class NearPersonList {

	private ArrayList<NearPerson> nearList;
	
	public NearPersonList(){
		nearList = new ArrayList<NearPerson>();
	}
	
	/**
	 * 清空列表
	 */
	public void clearNearPersonList(){
		if(nearList !=null && nearList.size() > 0){
			nearList.clear();
		}
	}
	/**
	 * 删除一个NearPerson
	 * @param position
	 */
	public void deleteNearPerson(int position){
		nearList.remove(position);
	}
	
	/**
	 * 增加一个NearPerson
	 * @param NearPerson
	 */
	public void addNearPerson(NearPerson nearperson){
		nearList.add(nearperson);
	}
	
	/**
	 * 增加一个nearList
	 * @param list
	 */
	public void addNearPersonList(NearPersonList list){
		nearList.addAll(list.getNearPersonList());
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<NearPerson> getNearPersonList() {
		// TODO Auto-generated method stub
		return nearList;
	}

	/**
	 * 获取列表长度
	 * @return
	 */
	public int getCount(){
		if(nearList != null){
			return nearList.size();
		}else{
			return 0;
		}
	}
	
	/**
	 * 获取指定nearperson
	 * @param position
	 * @return
	 */
	public NearPerson getNearPerson(int position){
		return nearList.get(position);
	}
	
	/**
	 * 获取指定nearperson的ID
	 * @param position
	 * @return
	 */
	public String getNearPersonID(int position){
		return getNearPerson(position).CustomerID + "";
	}
	
	/**
	 * 获取NearPerson列表
	 * @param merchantID 商户id
	 * @param categoryID 分类id
	 * @return 
	 */
	public int FetchNearPersonList(String sessionID, int Gender, double lon, double lat, int startIndex, int takeNum){
		String result = MassVigService.getInstance().GetAroundPersons(sessionID, Gender, lon, lat, startIndex, takeNum);
		int resultCode = 0;
		try {
			JSONObject object = new JSONObject(result);
			resultCode = object.getInt("ResponseStatus");
//			String message = object.getString("ResponseMsg");
			if(resultCode == 0){
				JSONArray list = object.getJSONArray("ResponseData");
				if(list != null && list.length() > 0){
					for(int i = 0; i < list.length(); i++){
						NearPerson nearperson = new NearPerson();
						JSONObject merchantData = list.getJSONObject(i);
						nearperson = DataAnalyse(merchantData);
						nearList.add(nearperson);
					}
				}
				return resultCode;
			}else if(resultCode == MassVigContants.SESSIONVAILED){
				return resultCode;
			}
		} catch (Exception e) {
		}
		return resultCode;
	}
	
	/**
	 * 解析nearperson，返回nearperson对象
	 * @param data
	 * @return NoQnearperson
	 */
	private NearPerson DataAnalyse(JSONObject data){
		NearPerson n = new NearPerson();
		try {
			n.LastLocationTime = data.getString("LastLocationTime");
			n.Distance = data.getString("Distance");
			n.Gender = data.getInt("Gender");
			n.HeadImgUrl = data.getString("HeadImgUrl");
			n.CustomerID = data.getInt("CustomerID");
			n.NickName = data.getString("NickName");
			n.Relation = data.getInt("Relation");
			n.ProductID = data.getInt("ProductID");
			n.ProductName = data.getString("ProductName");
			n.FansCustomerCount = data.getInt("FansCustomerCount");
			n.FollowingCustomerCount = data.getInt("FollowingCustomerCount");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return n;
	}
}
