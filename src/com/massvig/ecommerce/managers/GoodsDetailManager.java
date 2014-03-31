package com.massvig.ecommerce.managers;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.CollectList;
import com.massvig.ecommerce.entities.CommentList;
import com.massvig.ecommerce.entities.GoodsDetail;
import com.massvig.ecommerce.service.MassVigService;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.widgets.AsyncImageLoader;
import com.massvig.ecommerce.widgets.AsyncImageLoader.ImageCallback;

public class GoodsDetailManager {

	private LoadListener listener;
	public String atCustomerID = "";
	private int maxHeight = 0;
	private int maxWidth = 0;
	private int mFromX = 0;
	private int mToX = 0;
	private int moveWidth;
	private int index = 1;
	private GoodsDetail goodsDetail;
	private GoodsDetail tempGoodsDetail;
	private int pid;
	private ArrayList<View> views = new ArrayList<View>();
	public boolean isLoadDone = false;
	public static final int PRAISE = 1;
	public static final int STAMP = 2;
	private CollectList collectlist;


	public void Praise(String sessionid){
		new PraiseAsync().execute(sessionid);
	}
	
	private class PraiseAsync extends AsyncTask<String, Void, Integer>{

		@Override
		protected Integer doInBackground(String... params) {

			String result = MassVigService.getInstance().PraiseProduct(params[0], goodsDetail.productID);
			if(!TextUtils.isEmpty(result)){
				JSONObject o;
				try {
					o = new JSONObject(result);
					int res = o.getInt("ResponseStatus");
					return res;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
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

	public void Stamp(String sessionid){
		new StampAsync().execute(sessionid);
	}
	
	private class StampAsync extends AsyncTask<String, Void, Integer>{

		@Override
		protected Integer doInBackground(String... params) {

			String result = MassVigService.getInstance().StampProduct(params[0], goodsDetail.productID);
			if(!TextUtils.isEmpty(result)){
				JSONObject o;
				try {
					o = new JSONObject(result);
					int res = o.getInt("ResponseStatus");
					return res;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
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
				listener.PraiseSuccess(STAMP);
			}else if(result == MassVigContants.SESSIONVAILED){
				listener.SessionVailed();
			}else if(result == 1){
				listener.Already();
			}else{
				listener.PraiseFailed(STAMP);
			}
		}
		
	}
	
	public void refreshCommentsList() {
		this.goodsDetail.commentList.addCommentList(tempGoodsDetail.commentList);
		this.tempGoodsDetail.commentList.clearCommentList();
	}
	
	public CommentList getComments() {
		return goodsDetail.commentList;
	}

	public GoodsDetailManager() {
		goodsDetail = new GoodsDetail();
		tempGoodsDetail = new GoodsDetail();
		collectlist = new CollectList();
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

	public int getPid() {
		if(pid == 0){
			return goodsDetail.productID;
		}
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public GoodsDetail getGoodsDetail() {
		return goodsDetail;
	}

	public void setGoodsDetail(GoodsDetail goodsDetail) {
		this.goodsDetail = goodsDetail;
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

//	public void setmFromX(int mFromX) {
//		this.mFromX = mFromX;
//	}

	public int getmToX() {
		return mToX;
	}

//	public void setmToX(int mToX) {
//		this.mToX = mToX;
//	}

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
		mToX = (index - 1) * moveWidth;
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

	public void LoadImg(ProgressBar pb, ImageView imageView, String picUrl2) {
		imageView.setLayoutParams(compare(maxWidth, maxHeight));
		imageView.setBackgroundResource(R.drawable.default_icon);
		if(!TextUtils.isEmpty(picUrl2)){
			Bitmap bitmap = AsyncImageLoader.loadBitmap(MassVigUtil.GetImageUrl(picUrl2, maxWidth, maxHeight),
					new ImageloadCallback(pb, imageView));
			if (bitmap != null) {
				int width = bitmap.getWidth();
				int height = bitmap.getHeight();
				imageView.setLayoutParams(compare(width, height));
				imageView.setImageBitmap(bitmap);
				if(pb != null)
					pb.setVisibility(View.GONE);
			}
		}
		
	}
	
	class ImageloadCallback implements ImageCallback{

		ImageView imageView;
		ProgressBar pb;
		public ImageloadCallback(ProgressBar pb, ImageView imageView2) {
			this.imageView = imageView2;
			this.pb = pb;
		}

		@Override
		public void imageLoaded(Bitmap imageDrawable, String imageUrl) {
			int width = imageDrawable.getWidth();
			int height = imageDrawable.getHeight();
			imageView.setLayoutParams(compare(width, height));
			imageView.setImageBitmap(imageDrawable);
			if(pb != null)
				pb.setVisibility(View.GONE);
		}
		
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
			boolean result = tempGoodsDetail.FetchComment(goodsDetail.productID, goodsDetail.commentList.getCount()); 
			if(tempGoodsDetail.commentList.getCount() == 0){
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
			int result = goodsDetail.AddComment(params[0], params[1], params[2]); 
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

	private class FetchDataAsync extends AsyncTask<String, Void, Integer>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			listener.StartLoading();
		}

		@Override
		protected Integer doInBackground(String... params) {
			return goodsDetail.FetchData(params[0]);
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			listener.StopLoading();
			if(result == 0){
				listener.LoadSuccess();
			}else if(result == -1 || result == -6){
				listener.GoodsDown();
			}else{
				listener.LoadFailed();
			}
		}
	}
	
	public void FetchData(String sessionid) {
		new FetchDataAsync().execute(sessionid);
	}

	private class GetProdcutImgsAsync extends AsyncTask<String, Void, Boolean>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			listener.StartLoading();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			return goodsDetail.FetchImgs();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			listener.StopLoading();
			if(result){
				listener.LoadSuccess();
			}else{
				listener.LoadFailed();
			}
		}
	}
	
	public void FetchImgs() {
		new GetProdcutImgsAsync().execute();
	}

	private class FetchParamsAsync extends AsyncTask<String, Void, Boolean>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			listener.StartLoading();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			return goodsDetail.FetchParams();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			listener.StopLoading();
			if(result){
				listener.LoadParamsSuccess();
			}else{
				listener.LoadParamsFailed();
			}
		}
	}
	public void AddCollect(String sessionID){
		new addCollectasync().execute(goodsDetail.productID+"",sessionID);
	}
	public class addCollectasync extends AsyncTask<String, Void, Integer>{

		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			int result=collectlist.AddCollect(Integer.valueOf(params[0]), params[1]);
			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result==0){
				listener.addCollectsucess();
			}else if(result==MassVigContants.SESSIONVAILED){
				listener.SessionVailed();
			}else{
				listener.addCollectfailed();
			}
		}
		
	}

	public void FetchParams() {
		new FetchParamsAsync().execute();
	}
	public interface LoadListener{
		void GoodsDown();
		void LoadSuccess();
		void AddCommentFailed();
		void AddCommentSuccess();
		void LoadFailed();
		void LoadParamsSuccess();
		void LoadParamsFailed();
		void LoadCommentSuccess();
		void LoadCommentFailed();
		void StartLoading();
		void StopLoading();
		void PraiseSuccess(int tag);
		void PraiseFailed(int tag);
		void SessionVailed();
		void Already();
		void addCollectsucess();
		void addCollectfailed();
	}

}
