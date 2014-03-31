package com.massvig.ecommerce.entities;

import java.io.Serializable;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.massvig.ecommerce.service.MassVigService;

public class CommentList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Comment> commentList;
	
	public CommentList(){
		commentList = new ArrayList<Comment>();
	}
	
	/**
	 * 清空列表
	 */
	public void clearCommentList(){
		if(commentList !=null && commentList.size() > 0){
			commentList.clear();
		}
	}
	/**
	 * 删除一个Comment
	 * @param position
	 */
	public void deleteComment(int position){
		commentList.remove(position);
	}
	
	/**
	 * 增加一个Comment
	 * @param Comment
	 */
	public void addComment(Comment comment){
		commentList.add(comment);
	}
	
	/**
	 * 增加一个commentList
	 * @param list
	 */
	public void addCommentList(CommentList list){
		commentList.addAll(list.getCommentList());
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Comment> getCommentList() {
		return commentList;
	}

	/**
	 * 获取列表长度
	 * @return
	 */
	public int getCount(){
		if(commentList != null){
			return commentList.size();
		}else{
			return 0;
		}
	}
	
	/**
	 * 获取指定comment
	 * @param position
	 * @return
	 */
	public Comment getComment(int position){
		return commentList.get(position);
	}
	
	/**
	 * 获取指定comment的ID
	 * @param position
	 * @return
	 */
	public String getCommentID(int position){
		return getComment(position).CommentID + "";
	}
	
	/**
	 * 获取Comment列表
	 * @return 
	 */
	public boolean FetchCommentList(int productID,int startIndex,int takeNum){
		String result = MassVigService.getInstance().GetComment(productID, startIndex, takeNum);
		try {
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
//			String message = object.getString("ResponseMsg");
			if(resultCode == 0){
				JSONArray list = object.getJSONArray("ResponseData");
				if(list != null && list.length() > 0){
					for(int i = 0; i < list.length(); i++){
						Comment comment = new Comment();
						JSONObject data = list.getJSONObject(i);
						comment = DataAnalyse(data);
						commentList.add(comment);
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
	 * 获取帖子Comment列表
	 * @return 
	 */
	public boolean GetCommentList(int shareID,int startIndex,int takeNum){
		String result = MassVigService.getInstance().GetPostComment(shareID, startIndex, takeNum);
		try {
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
//			String message = object.getString("ResponseMsg");
			if(resultCode == 0){
				JSONArray list = object.getJSONArray("ResponseData");
				if(list != null && list.length() > 0){
					for(int i = 0; i < list.length(); i++){
						Comment comment = new Comment();
						JSONObject data = list.getJSONObject(i);
						comment = DataAnalyse(data);
						commentList.add(comment);
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
	 * 解析comment，返回comment对象
	 * @param data
	 * @return NoQcomment
	 */
	//TODO
	private Comment DataAnalyse(JSONObject data){
		Comment comment = new Comment();
		try {
			comment.CommentID = data.getInt("CommentID");
			comment.CustomerID = data.getInt("CustomerID");
			comment.NickName = data.getString("NickName");
			comment.HeadImgUrl = data.getString("HeadImgUrl");
			if(data.has("ProductID"))
				comment.ProductID = data.getInt("ProductID");
			comment.Content = data.getString("Content");
			comment.CreateTime = data.getString("CreateTime");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return comment;
	}
	
	//TODO
	public void updateComment(){
		
	}
}
