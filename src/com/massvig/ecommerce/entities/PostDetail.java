package com.massvig.ecommerce.entities;

import org.json.JSONObject;

import com.massvig.ecommerce.service.MassVigService;

public class PostDetail extends Post {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public CommentList commentList = new CommentList();

	
	public boolean FetchComment(int shareID, int startIndex){
		return commentList.GetCommentList(shareID, startIndex, 10);
	}
	
	public int AddPostComment(String sessionid, String atCustomerID, String comment){
		String result = MassVigService.getInstance().AddPostComment(sessionid, postID, atCustomerID, comment);
		try {
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
//			String message = object.getString("ResponseMsg");
			return resultCode;
		} catch (Exception e) {
			return -1;
		}
	}
}
