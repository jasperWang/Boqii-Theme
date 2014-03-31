package com.massvig.ecommerce.managers;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.massvig.ecommerce.database.DatabaseHelper;
import com.massvig.ecommerce.database.DatabaseUtil;
import com.massvig.ecommerce.entities.Post;
import com.massvig.ecommerce.entities.PostList;
import com.massvig.ecommerce.service.MassVigService;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.widgets.AsyncImageLoader;
import com.massvig.ecommerce.widgets.AsyncImageLoader.ImageCallback;

public class CommunityManager {

	private LoadListener listener;
	private int maxHeight = 0;
	private int maxWidth = 0;
	private int mFromX = 0;
	private int mToX = 0;
	private int moveWidth;
	private int index = 0;
	private ArrayList<View> views = new ArrayList<View>();
	public PostList allPostList = new PostList();
	public PostList myAttentionList = new PostList();
	public PostList mySearchList = new PostList();
	public PostList tempList = new PostList();
	public boolean isLoadDoneAll = false;
	public boolean isLoadDoneAttention = false;
	public boolean isLoadDoneSearch = false;
	public boolean isLoading = false;
	public static final int LOAD = 1;
	public static final int PRAISE = 2;
	public static final int ALL = 3;
	public static final int ATTENTION = 4;
	public static final int SEARCH = 7;
	private int tempIndex = ALL;
	public static final int REFRESH = 5;
	public static final int MORE = 6;
	private int type;

	/**
	 * 获取未读数量
	 * @param context
	 * @param toEJID
	 * @return
	 */
	public int GetAllUnread(Context context, String toEJID){
		DatabaseHelper helper = new DatabaseHelper(context, null, 1);
		SQLiteDatabase database = helper.getReadableDatabase();
		int count = DatabaseUtil.getInstance().GetAllUnReadCount(database, toEJID);
		database.close();
		return count;
	}
	
	public void refreshPostList() {
		this.allPostList.addPostList(tempList);
		this.tempList.clearPostList();
	}
	
	public void refreshAttentionList(){
		this.myAttentionList.addPostList(tempList);
		this.tempList.clearPostList();
	}
	
	public void refreshSearchList(){
		this.mySearchList.addPostList(tempList);
		this.tempList.clearPostList();
	}
	
	public void loadData(String sessionid, int index, int type){
		this.type = type;
		tempIndex = index;
			if(tempIndex == ALL){
				if(type == REFRESH){
					isLoadDoneAll = false;
					new GetAllAsync().execute(sessionid, "0");
				}else{
					if(!isLoadDoneAll)
						new GetAllAsync().execute(sessionid, allPostList.getCount() + "");
					else
						listener.LoadSuccess(LOAD,ALL);
				}
			}else if (tempIndex == ATTENTION){
				if(type == REFRESH){
					isLoadDoneAttention = false;
					new GetAttentionAsync().execute(sessionid, "0");
				}else{
					if(!isLoadDoneAttention)
						new GetAttentionAsync().execute(sessionid, allPostList.getCount() + "");
					else
						listener.LoadSuccess(LOAD,ATTENTION);
				}
			}else if(tempIndex == SEARCH){
				if(type == REFRESH){
					isLoadDoneSearch = false;
					new GetSearchAsync().execute(sessionid, "0");
				}else{
					if(!isLoadDoneSearch)
						new GetSearchAsync().execute(sessionid, allPostList.getCount() + "");
					else
						listener.LoadSuccess(LOAD,SEARCH);
				}
			}
	}
	
	public void setListener(LoadListener l){
		this.listener = l;
	}
	
	public ArrayList<View> getViews() {
		return views;
	}

	public void setViews(ArrayList<View> views) {
		this.views = views;
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

	public int getmFromX() {
		return mFromX;
	}

	public int getmToX() {
		return mToX;
	}

	public int getMoveWidth() {
		return moveWidth;
	}

	public void setMoveWidth(int moveWidth) {
		this.moveWidth = moveWidth;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		mFromX = mToX;
		this.index = index;
		mToX = index * moveWidth;
	}

	public void Praise(String sessionid, int postid){
		new PraiseAsync().execute(sessionid, postid + "");
	}
	
	private class PraiseAsync extends AsyncTask<String, Void, Void>{

		@Override
		protected Void doInBackground(String... params) {
			String result = MassVigService.getInstance().Praise(params[0], Integer.valueOf(params[1]));
			if(!TextUtils.isEmpty(result)){
				JSONObject o;
				try {
					o = new JSONObject(result);
					if(o.getInt("ResponseStatus") == 0){
						int postid = Integer.valueOf(params[1]);
//						PostList lists = (tempIndex == ALL) ? allPostList : myAttentionList ;
						PostList lists = allPostList;
						for (int i = 0; i < lists.getCount(); i++) {
							Post p = lists.getPost(i);
							if(p.postID == postid){
								if (p.CanPraise) {
									p.CanPraise = false;
									p.praise += 1;
								}
								listener.LoadSuccess(PRAISE,0);
								return null;
							}
						}
					}else if(o.getInt("ResponseStatus") == MassVigContants.SESSIONVAILED){
						listener.SessionFailed();
					}else{
						listener.LoadFailed(PRAISE);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}
		
	}
	
	private class GetAttentionAsync extends AsyncTask<String, Void, Integer>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			isLoading = true;
		}

		@Override
		protected Integer doInBackground(String... params) {
			int result = tempList.FetchAttentionPostList(params[0], Integer.valueOf(params[1]));
			if(tempList.getCount() == 0){
				isLoadDoneAttention = true;
			}
			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			isLoading = false;
			if(result == 0){
				if(type == REFRESH){
					allPostList.clearPostList();
				}
				listener.LoadSuccess(LOAD,ATTENTION);
			}else if(result == MassVigContants.SESSIONVAILED){
				listener.SessionFailed();
			}else{
				listener.LoadFailed(LOAD);
			}
		}
		
	}
	
	private class GetAllAsync extends AsyncTask<String, Void, Boolean>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			isLoading = true;
		}

		@Override
		protected Boolean doInBackground(String... params) {
			boolean result = tempList.FetchPostList(params[0], Integer.valueOf(params[1]));
			if(tempList.getCount() == 0){
				isLoadDoneAll = true;
			}
			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			isLoading = false;
			if(result){
				if(type == REFRESH){
					allPostList.clearPostList();
				}
				listener.LoadSuccess(LOAD,ALL);
			}else{
				listener.LoadFailed(LOAD);
			}
		}
	
	}
	
	private class GetSearchAsync extends AsyncTask<String, Void, Boolean>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			isLoading = true;
		}

		@Override
		protected Boolean doInBackground(String... params) {
			boolean result = tempList.FetchSearchPostList(params[0], Integer.valueOf(params[1]));
			if(tempList.getCount() == 0){
				isLoadDoneSearch = true;
			}
			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			isLoading = false;
			if(result){
				if(type == REFRESH){
					allPostList.clearPostList();
				}
				listener.LoadSuccess(LOAD,SEARCH);
			}else{
				listener.LoadFailed(LOAD);
			}
		}
	
	}
	
	public void clearData(){
		this.allPostList.clearPostList();
		this.myAttentionList.clearPostList();
	}
	
	public LinearLayout.LayoutParams compare(int width, int height) {
//		float w1 = width;
//		float h1 = height;
//		float w2 = maxWidth;
//		float h2 = maxHeight;
		int w = width, h = height;
		w = maxWidth;
		h = height * w / width;
		LinearLayout.LayoutParams params = new LayoutParams(w, h);
		params.gravity = Gravity.CENTER_HORIZONTAL;
		return params;
	}

	public void LoadImg(ImageView imageView, String picUrl2) {
		if(!TextUtils.isEmpty(picUrl2)){
			Bitmap bitmap = AsyncImageLoader.loadBitmap(picUrl2,
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

	public interface LoadListener{
		void LoadSuccess(int index, int tag);
		void SessionFailed();
		void LoadFailed(int index);
	}
	
}
