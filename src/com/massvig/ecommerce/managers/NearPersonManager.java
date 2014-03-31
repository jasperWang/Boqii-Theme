package com.massvig.ecommerce.managers;

import android.os.AsyncTask;

import com.massvig.ecommerce.entities.NearPerson;
import com.massvig.ecommerce.entities.NearPersonList;
import com.massvig.ecommerce.service.MassVigService;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.widgets.CommunityMode;

public class NearPersonManager {

	private LoadListener listener;
	public int gender = 0;
	public NearPersonList list = new NearPersonList();
	public NearPersonList tempList = new NearPersonList();
	private boolean isLoadDone = false;
	public double lon = 0.0,lat = 0.0;
	private static final int TAKENUM = 10;
	public static final int LOADDATA = 1;
	public static final int FANS = 2;
	public static final int REFRESH = 3;
	public static final int MORE = 4;
	private int type;
	public void loadDate(String sessionID, int type){
		this.type = type;
		if(type == REFRESH){
			isLoadDone = false;
			new fetchPersonListAsync().execute(sessionID, gender + "", lon + "", lat + "", "0", TAKENUM + "");
		}else{
			if(!isLoadDone)
				new fetchPersonListAsync().execute(sessionID, gender + "", lon + "", lat + "", list.getCount() + "", TAKENUM + "");
			else
				listener.LoadSuccess(LOADDATA);
		}
	};
	
	public void setListener(LoadListener l){
		this.listener = l;
	}
	
	public void SubmitLocation(String sessionid){
		new AsyncTask<String, Void, Void>(){

			@Override
			protected Void doInBackground(String... params) {
				MassVigService.getInstance().SubmitLocation(params[0], lon, lat);
				return null;
			}}.execute(sessionid);
	}
	
	/**
	 * 添加移除粉丝
	 * @param tag 0:移除粉丝 1：添加粉丝
	 * @param sessionId
	 * @param customerID
	 */
	public void AddAndRemoveFans(int tag, String sessionId, int customerID){
		new AddAndRemoveFansAsync().execute(tag + "", sessionId, customerID + "");
	}
	
	/**
	 * 添加删除粉丝
	 * @author DuJun
	 *
	 */
	private class AddAndRemoveFansAsync extends AsyncTask<String, Void, Integer>{

		private int customerID;
		private int type;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(String... params) {
			type = Integer.valueOf(params[0]);
			customerID = Integer.valueOf(params[2]);
			return CommunityMode.getInstance().AddAndRemoveFans(Integer.valueOf(params[0]), params[1], Integer.valueOf(params[2]));
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if(result == 0){
				for (int i = 0; i < list.getCount(); i++) {
					NearPerson p = list.getNearPerson(i);
					if(p.CustomerID == customerID){
						if(type == 0)
							p.Relation = 2;
						else if(type == 1)
							p.Relation = 3;
						listener.LoadSuccess(FANS);
						return;
					}
				}
			}else if(result == MassVigContants.SESSIONVAILED){
				listener.SessionVailed();
			}else{
				listener.LoadFailed(FANS);
			}
			listener.LoadFailed(FANS);
		}
	}
	
	/**
	 * 异步获取nearperson列表
	 * @author DuJun
	 *
	 */
	private class fetchPersonListAsync extends AsyncTask<String, Void, Boolean>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			return FetchPersonList(params[0], Integer.valueOf(params[1]), Double.valueOf(params[2]), Double.valueOf(params[3]), Integer.valueOf(params[4]), Integer.valueOf(params[5]));
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(result){
				if(type == REFRESH)
					list.clearNearPersonList();
				list.addNearPersonList(tempList);
				tempList.clearNearPersonList();
				listener.LoadSuccess(LOADDATA);
			}else{
				listener.LoadFailed(LOADDATA);
			}
		}
	}

	/**
	 * 获取goods列表
	 * @param startLoadIndex 从第几条数据开始
	 */
	private boolean FetchPersonList(String sessionID, int Gender, double lon, double lat, int startIndex, int takeNum) {
		boolean result = false;
		int resultCode = tempList.FetchNearPersonList(sessionID, Gender, lon, lat, startIndex, takeNum);
		if (resultCode == 0) {
			result = true;
			if (tempList.getCount() == 0) {
				isLoadDone = true;
				result = true;
			}
		} else if(resultCode == MassVigContants.SESSIONVAILED){
			result = true;
		} else {
			result = false;
		}
		return result;
	}
	
	public interface LoadListener{
		void LoadSuccess(int tag);
		void LoadFailed(int tag);
		void SessionVailed();
	}
}
