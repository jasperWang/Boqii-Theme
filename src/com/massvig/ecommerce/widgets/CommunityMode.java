package com.massvig.ecommerce.widgets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.massvig.ecommerce.entities.Friend;
import com.massvig.ecommerce.entities.Praised;
import com.massvig.ecommerce.service.MassVigService;

/**
 * CommunityMode 社区
 */
public class CommunityMode {
	private static CommunityMode communityMode = null;
	/**
	 * 获取用户关注列表
	 * @param sessionId,userId,startId,count
	 * @author zhangbp
	 * @return  Map-resultCode-data
	 */
	public Map<String,Object> GetFollows(String sessionid, int customerID, int startIndex,int takeNum){
		List<Friend> lists = null;
		int result = -10001;
		String data = MassVigService.getInstance().GetFollows(sessionid, customerID, startIndex, takeNum);
		if(data != null){
			try {
				JSONObject json = new JSONObject(data);
				result = json.getInt("ResponseStatus");
				if(result == 0){
					JSONArray userItms = json.getJSONArray("ResponseData");
					int counts = userItms.length();
					if(counts > 0){
						lists = Friend.getInstance().UserInfoJSONToBean(userItms);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("resultCode", result);
		map.put("userList", lists);
		
		return map;
	}
	
	/**
	 * 获取用户粉丝列表
	 * @param sessionId,userId,startId,count
	 * @author zhangbp
	 * @return  Map-resultCode-data
	 */

	public Map<String,Object> GetFans(String sessionid, int customerID, int startIndex,int takeNum){
		List<Friend> lists = null;
		int result = -10001;
		String data = MassVigService.getInstance().GetFans(sessionid, customerID, startIndex, takeNum);
		if(data != null){
			try {
				JSONObject json = new JSONObject(data);
				result = json.getInt("ResponseStatus");
				if(result == 0){
					JSONArray userItms = json.getJSONArray("ResponseData");
					int counts = userItms.length();
					if(counts > 0){
						lists = Friend.getInstance().UserInfoJSONToBean(userItms);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("resultCode", result);
		map.put("userList", lists);
		
		return map;
	}
	
	/**
	 * 加载收获的赞
	 * @param sessionId,userId,startId,count
	 * @author zhangbp
	 * @return  Map-resultCode-data
	 */

	public Map<String,Object> getUserReceivePraisePostList(String sessionid, int customerID, int startIndex,int takeNum){
		
		List<Praised> lists = null;
		int result = -10001;
		String data = MassVigService.getInstance().GetPraisedShareRecords(sessionid, customerID, startIndex, takeNum);
		if(data != null){
			try {
				JSONObject json = new JSONObject(data);
				result = json.getInt("ResponseStatus");
				if(result == 0){
					JSONArray userItms = json.getJSONArray("ResponseData");
					int counts = userItms.length();
					if(counts > 0){
						lists = Praised.getInstance().UserInfoJSONToBean(userItms);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("resultCode", result);
		map.put("userList", lists);
		
		return map;
	}
	
	/**
	 * 找人
	 * @param sessionId,keyword,startId,count,flag
	 * @author zhangbp
	 * @return  Map-resultCode-data
	 */
	public Map<String,Object> getSearchFriends(String sessionID, String keyWord, int startIndex, int takeNum){
		
		List<Friend> lists = null;
		int result = -10001;
		String data = MassVigService.getInstance().FindPeople(sessionID, keyWord, startIndex, takeNum);
		if(data != null){
			try {
				JSONObject json = new JSONObject(data);
				result = json.getInt("ResponseStatus");
				if(result == 0){
					JSONArray userItms = json.getJSONArray("ResponseData");
					int counts = userItms.length();
					if(counts > 0){
						lists = Friend.getInstance().UserInfoJSONToBean(userItms);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("resultCode", result);
		map.put("userList", lists);
		
		return map;
	}
	
//	public Map<String,Object> getSearchFriends(String sessionId,String keyword,int startId,int count,int flag){
//		List<UserInfo> lists = null;
//		int result = -10001;
//		int minID = 0;
//		
//		String data = GroupSaleService.getInstance().getSearchFriends(sessionId, keyword, startId, count,flag);
//		
//		if(data != null){
//			
//			try {
//				JSONObject json = new JSONObject(data);
//				result = json.getInt("result");
//				if(result == 0){
//					JSONArray userItms = json.getJSONArray("usersList");
//					int counts = userItms.length();
//					if(counts > 0){
//						lists = JSONToBean.getInstance().UserInfoJSONToBean(userItms);
//					}
//					minID = json.getInt("minID");
//				}
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("resultCode", result);
//		map.put("userList", lists);
//		map.put("minID", minID);
//		return map;
//		
//	}

	/**
	 * 添加或移除粉丝
	 * @param type 0:移除粉丝 1：添加粉丝
	 * @param type,sessionId,subscribedUserID
	 * @author zhangbp
	 * @return 
	 */
	
	public int AddAndRemoveFans(int type,String sessionId,int customerID){
		String data = null;
		int result = -10001;
		if(type == 0){
			data = MassVigService.getInstance().CancelFollow(sessionId, customerID);
		}else{
			data = MassVigService.getInstance().Follow(sessionId, customerID);
		}
		if(data != null){
			try{
				JSONObject json = new JSONObject(data);
				result = json.getInt("ResponseStatus");
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		return result;
	}
	
	public interface CommunityModeCallBack{
		public void callBack(int resutlCode,int minId,int maxId,Object data);
	}
	
	public static CommunityMode getInstance(){
		if(communityMode == null){
			communityMode = new CommunityMode();
		}
		return communityMode;
	}

}
