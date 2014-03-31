package com.massvig.ecommerce.entities;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.massvig.ecommerce.service.MassVigService;

public class ActionsList {

	private ArrayList<Action> actionsList;
	
	public ActionsList(){
		actionsList = new ArrayList<Action>();
	}
	
	/**
	 * 清空列表
	 */
	public void clearActionList(){
		if(actionsList !=null && actionsList.size() > 0){
			actionsList.clear();
		}
	}
	/**
	 * 删除一个Action
	 * @param position
	 */
	public void deleteAction(int position){
		actionsList.remove(position);
	}
	
	/**
	 * 增加一个Action
	 * @param Action
	 */
	public void addAction(Action action){
		actionsList.add(action);
	}
	
	/**
	 * 增加一个actionsList
	 * @param list
	 */
	public void addActionList(ActionsList list){
		actionsList.addAll(list.getActionList());
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Action> getActionList() {
		// TODO Auto-generated method stub
		return actionsList;
	}

	/**
	 * 获取列表长度
	 * @return
	 */
	public int getCount(){
		if(actionsList != null){
			return actionsList.size();
		}else{
			return 0;
		}
	}
	
	/**
	 * 获取指定action
	 * @param position
	 * @return
	 */
	public Action getAction(int position){
		return actionsList.get(position);
	}
	
	/**
	 * 获取指定action的ID
	 * @param position
	 * @return
	 */
	public String getActionID(int position){
		return getAction(position).actionID + "";
	}
	
	/**
	 * 获取Action列表
	 * @param merchantID 商户id
	 * @param categoryID 分类id
	 * @return 
	 */
	public boolean GetUserCampaigns(String sessionid){
		String result = MassVigService.getInstance().GetUserCampaigns(sessionid);
		try {
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
//			String message = object.getString("ResponseMsg");
			if(resultCode == 0){
				JSONArray list = object.getJSONArray("ResponseData");
				if(list != null && list.length() > 0){
					for(int i = 0; i < list.length(); i++){
						Action action = new Action();
						JSONObject merchantData = list.getJSONObject(i);
						action = DataAnalyse(merchantData);
						actionsList.add(action);
					}
				}
				return true;
			}else{
			}
		} catch (Exception e) {
		}
		return false;
	}
	
	/**
	 * 解析action，返回action对象
	 * @param data
	 * @return NoQaction
	 */
	private Action DataAnalyse(JSONObject data){
		Action action = new Action();
		try {
			action.actionID = data.getInt("CampaignID");
			action.detail = data.getString("Description");
			action.imgUrl = data.getString("CampaignImgUrl");
			action.title = data.getString("CampaignTitle");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return action;
	}
}
