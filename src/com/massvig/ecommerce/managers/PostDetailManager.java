package com.massvig.ecommerce.managers;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.massvig.ecommerce.entities.CommentList;
import com.massvig.ecommerce.entities.PostDetail;
import com.massvig.ecommerce.service.MassVigService;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.widgets.AsyncImageLoader;
import com.massvig.ecommerce.widgets.AsyncImageLoader.ImageCallback;

public class PostDetailManager {

	private LoadListener listener;
	public String atCustomerID = "";
	private int maxHeight = 0;
	private int maxWidth = 0;
	public PostDetail postDetail;
	public PostDetail tempPostDetail;
	public int pid;
	public boolean isLoadDone = false;
	public static final int PRAISE = 1;
	public static final int STAMP = 2;

	public void Praise(String sessionid){
		new PraiseAsync().execute(sessionid);
	}
	
	private class PraiseAsync extends AsyncTask<String, Void, Integer>{

		@Override
		protected Integer doInBackground(String... params) {

			String result = MassVigService.getInstance().Praise(params[0], postDetail.postID);
			if(!TextUtils.isEmpty(result)){
				JSONObject o;
				try {
					o = new JSONObject(result);
					int res = o.getInt("ResponseStatus");
					return res;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return -1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result == 0){
				listener.PraiseSuccess(PRAISE);
			}else if(result == MassVigContants.SESSIONVAILED){
				listener.SessionVailed();
			}else if(result == 1){
				listener.Already();
			}else{
				listener.PraiseFailed(PRAISE);
			}
		}
		
	}

	public void refreshCommentsList() {
		this.postDetail.commentList.addCommentList(tempPostDetail.commentList);
		this.tempPostDetail.commentList.clearCommentList();
	}
	
	public CommentList getComments() {
		return postDetail.commentList;
	}

	public PostDetailManager() {
		postDetail = new PostDetail();
		tempPostDetail = new PostDetail();
	}
	
	public void setListener(LoadListener l){
		this.listener = l;
	}
	
	public int getMaxHeight() {
		return maxHeight;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}

	public int getMaxWidth() {
		return maxWidth;
	}

	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	public RelativeLayout.LayoutParams compare(int width, int height) {
		float w1 = width;
		float h1 = height;
		float w2 = maxWidth;
		float h2 = maxHeight;
		int w = width, h = height;
		if (h1 / w1 > h2 / w2) {
			h = maxHeight;
			w = width * h / height;
		} else {
			w = maxWidth;
			h = height * w / width;
		}
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, h);
//		params.gravity = Gravity.CENTER_HORIZONTAL;
		return params;
	}

	public void LoadImg(ImageView imageView, String picUrl2) {
		if(!TextUtils.isEmpty(picUrl2)){
			Bitmap bitmap = AsyncImageLoader.loadBitmap(MassVigUtil.GetImageUrl(picUrl2, maxWidth, maxHeight),
					new ImageloadCallback(imageView));
			if (bitmap != null) {
				int width = bitmap.getWidth();
				int height = bitmap.getHeight();
				imageView.setLayoutParams(compare(width, height));
				imageView.setImageBitmap(bitmap);
			}
		}
		
	}
	
	class ImageloadCallback implements ImageCallback{

		ImageView imageView;
		public ImageloadCallback(ImageView imageView2) {
			this.imageView = imageView2;
		}

		@Override
		public void imageLoaded(Bitmap imageDrawable, String imageUrl) {
			int width = imageDrawable.getWidth();
			int height = imageDrawable.getHeight();
			imageView.setLayoutParams(compare(width, height));
			imageView.setImageBitmap(imageDrawable);
		}
		
	}
	
	public void DeletePost(final String sessionid){
		new AsyncTask<Void, Void, Integer>(){

			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(result == 0){
					listener.DeletePost(0);
				}else if(result == MassVigContants.SESSIONVAILED){
					listener.SessionVailed();
				}else{
					listener.DeletePost(-1);
				}
			}

			@Override
			protected Integer doInBackground(Void... params) {
				String result = MassVigService.getInstance().DeleteShare(sessionid, postDetail.postID);
				if(!TextUtils.isEmpty(result)){
					JSONObject o;
					try {
						o = new JSONObject(result);
						int res = o.getInt("ResponseStatus");
						return res;
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				return -1;
			}}.execute();
	}
	

	private class FetchCommentAsync extends AsyncTask<String, Void, Boolean>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			listener.StartLoading();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			boolean result = tempPostDetail.FetchComment(postDetail.postID, postDetail.commentList.getCount()); 
			if(tempPostDetail.commentList.getCount() == 0){
				isLoadDone = true;
			}
			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			listener.StopLoading();
			if(result){
				listener.LoadCommentSuccess();
			}else{
				listener.LoadCommentFailed();
			}
		}
	}

	private class AddCommentAsync extends AsyncTask<String, Void, Integer>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			listener.StartLoading();
		}

		@Override
		protected Integer doInBackground(String... params) {
			int result = postDetail.AddPostComment(params[0], params[1], params[2]); 
			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			listener.StopLoading();
			if(result == 0){
				listener.AddCommentSuccess();
			}else if(result == MassVigContants.SESSIONVAILED){
				listener.SessionVailed();
			}else{
				listener.AddCommentFailed();
			}
		}
	}
	
	public void FetchComment(){
		if(!isLoadDone)
			new FetchCommentAsync().execute();
	}
	
	public void AddComment(String sessionid, String comment){
		new AddCommentAsync().execute(sessionid, atCustomerID, comment);
	}

	public interface LoadListener{
		void AddCommentFailed();
		void AddCommentSuccess();
		void LoadCommentSuccess();
		void LoadCommentFailed();
		void StartLoading();
		void StopLoading();
		void PraiseSuccess(int tag);
		void PraiseFailed(int tag);
		void SessionVailed();
		void Already();
		void DeletePost(int result);
	}

}
