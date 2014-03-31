package com.massvig.ecommerce.entities;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.massvig.ecommerce.service.MassVigService;

public class PostList {

	private ArrayList<Post> postsList;
	
	public PostList(){
		postsList = new ArrayList<Post>();
	}
	
	/**
	 * 清空列表
	 */
	public void clearPostList(){
		if(postsList !=null && postsList.size() > 0){
			postsList.clear();
		}
	}
	/**
	 * 删除一个Post
	 * @param position
	 */
	public void deletePost(int position){
		postsList.remove(position);
	}
	
	/**
	 * 增加一个Post
	 * @param Post
	 */
	public void addPost(Post post){
		postsList.add(post);
	}
	
	/**
	 * 增加一个postsList
	 * @param list
	 */
	public void addPostList(PostList list){
		postsList.addAll(list.getPostList());
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Post> getPostList() {
		// TODO Auto-generated method stub
		return postsList;
	}

	/**
	 * 获取列表长度
	 * @return
	 */
	public int getCount(){
		if(postsList != null){
			return postsList.size();
		}else{
			return 0;
		}
	}
	
	/**
	 * 获取指定post
	 * @param position
	 * @return
	 */
	public Post getPost(int position){
		if(postsList.size() > position)
			return postsList.get(position);
		else
			return new Post();
	}
	
	/**
	 * 获取指定post的ID
	 * @param position
	 * @return
	 */
	public String getPostID(int position){
		return getPost(position).postID + "";
	}
	
	/**
	 * 搜索分享Post列表
	 * @param merchantID 商户id
	 * @param categoryID 分类id
	 * @return 
	 */
	public boolean FetchSearchPostList(String keyword, int startindex){
		String result = MassVigService.getInstance().SearchShare(keyword, startindex, 10);
		try {
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
//			String message = object.getString("ResponseMsg");
			if(resultCode == 0){
				JSONArray list = object.getJSONArray("ResponseData");
				if(list != null && list.length() > 0){
					for(int i = 0; i < list.length(); i++){
						Post post = new Post();
						JSONObject data = list.getJSONObject(i);
						post = DataAnalyse(data);
						postsList.add(post);
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
	 * 获取社区Post列表
	 * @param merchantID 商户id
	 * @param categoryID 分类id
	 * @return 
	 */
	public boolean FetchPostList(String sessionid, int startindex){
		String result = MassVigService.getInstance().GetAll(sessionid, startindex, 10);
		try {
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
//			String message = object.getString("ResponseMsg");
			if(resultCode == 0){
				JSONArray list = object.getJSONArray("ResponseData");
				if(list != null && list.length() > 0){
					for(int i = 0; i < list.length(); i++){
						Post post = new Post();
						JSONObject data = list.getJSONObject(i);
						post = DataAnalyse(data);
						postsList.add(post);
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
	 * 获取我的关注Post列表
	 * @param merchantID 商户id
	 * @param categoryID 分类id
	 * @return 
	 */
	public int FetchAttentionPostList(String sessionid, int startindex){
		String result = MassVigService.getInstance().GetFollowShares(sessionid, startindex, 10);
		try {
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
//			String message = object.getString("ResponseMsg");
			if(resultCode == 0){
				JSONArray list = object.getJSONArray("ResponseData");
				if(list != null && list.length() > 0){
					for(int i = 0; i < list.length(); i++){
						Post post = new Post();
						JSONObject data = list.getJSONObject(i);
						post = DataAnalyse(data);
						postsList.add(post);
					}
				}
				return resultCode;
			}else{
			}
			return resultCode;
		} catch (Exception e) {
		}
		return -1;
	}
	
	/**
	 * 解析post，返回post对象
	 * @param data
	 * @return NoQpost
	 */
	private Post DataAnalyse(JSONObject data){
		Post post = new Post();
		try {
			post.postID = data.getInt("ShareID");
//			post.ProductID = data.getInt("ProductID");
			post.ShareSourceType = data.getInt("ShareSourceType");
			post.RefID = data.getInt("RefID");
			post.CommentCount = data.optInt("CommentCount");
			post.address = data.getString("Address");
			post.imageUrl = data.getString("ImgUrl");
			post.praise = data.getInt("PraisedCount");
			post.shared = data.getInt("SharedCount");
			post.userimgurl = data.getString("HeadImgUrl");
			post.username = data.getString("NickName");
			post.CanPraise = data.getBoolean("CanPraise");
			post.CanShare = data.getBoolean("CanShare");
			post.detail = data.getString("Content");
			post.customerID = data.getInt("CustomerID");
			if(data.has("CreateTime"))
				post.createTime = data.getString("CreateTime");
			if(data.has("ShareSourceMsg1")){
				post.ShareSourceMsg1 = data.getString("ShareSourceMsg1");
				post.ShareSourceMsg2 = data.getString("ShareSourceMsg2");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return post;
	}
}
