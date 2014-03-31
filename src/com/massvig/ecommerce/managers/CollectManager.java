package com.massvig.ecommerce.managers;

import android.os.AsyncTask;

import com.massvig.ecommerce.entities.CollectList;
import com.massvig.ecommerce.entities.Goods;
import com.massvig.ecommerce.utilities.MassVigContants;
public class CollectManager {
	public CollectList collectlist;
	private CollectList tempList;
	public boolean isLoadDone = false;
	private boolean isLoading = false;
	private LoadListener listener;
	public String SessionID="";
	public  CollectManager(){
		initData();
	}
	public void setRefresh(boolean isRefresh) {
		this.isLoading = isRefresh;
	}
	public boolean isRefresh() {
		return isLoading;
	}
	public void refreshGoodsList() {
		this.collectlist.addCollectList(this.tempList);
		this.tempList.clearCollectList();
	}
	public void initData(){
		collectlist = new CollectList();
		tempList = new CollectList();
	}

	public CollectList getcollectList(){
		return collectlist;
	}
	public Goods getCollect(int position){
		return collectlist.getcollect(position);
	}
	public void setmanagerListener(LoadListener l){
		this.listener = l;
	}
	public void clearData(){
		collectlist.clearCollectList();
	}

	public void deleteCollect(int productID){
		new deletecollect().execute(SessionID,productID+"");
	}
	public int fetchCollectlist(String sessionID){
		int result=tempList.fetchcollectlist(sessionID);
		if(tempList.getCount()==0){
			listener.noData();
		}
		return result;
	}
	public void getCollectList(){
		new getCollectListAsync().execute(SessionID);
	}
	public class getCollectListAsync extends AsyncTask<String, Void, Integer>{

		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			int result=fetchCollectlist(params[0]);
			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result==0){
				listener.loadCollectSucess();
			}else if(result==MassVigContants.SESSIONVAILED){
				listener.SessionVailed();
			}else{
				listener.loadCollectfailed();
			}
		}	
	}
	
	public class deletecollect extends AsyncTask<String,Void,Integer>{

		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			int result=collectlist.deleteCollect(params[0],Integer.valueOf(params[1]));
			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result==0){
				listener.deleteCollectSucess();
			}else if(result==MassVigContants.SESSIONVAILED){
				listener.SessionVailed();
			}else{
				listener.deleteCollectFailed();
			}
			
		}
			
	}	
	public interface LoadListener{
		void loadCollectSucess();
		void loadCollectfailed();
		void SessionVailed();
		void deleteCollectSucess();
		void deleteCollectFailed();
		void noData();
	}
	

}
