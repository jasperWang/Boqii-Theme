package com.massvig.ecommerce.managers;

import org.json.JSONObject;


import com.massvig.ecommerce.managers.GoodsDetailManager.LoadListener;
import com.massvig.ecommerce.service.MassVigService;
import com.massvig.ecommerce.utilities.MassVigContants;

import android.os.AsyncTask;
import android.util.Log;

public class AddOptionManager {
	private loadListener listener;
	public int CustomerID;
	public void Addoption(String sessionID,String option){
		new AddOptionAnsyc().execute(sessionID,option);
	}
	public void setListener(loadListener l){
		this.listener = l;
	}
	public  class AddOptionAnsyc extends AsyncTask<String, Void, Integer>{

		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			int result = AddOption(params[0], params[1]);
			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result==0){
				listener.AddOptionSuccess();	
			}else if(result==MassVigContants.SESSIONVAILED){
				listener.SessionVailed();	
			}else{
				listener.AddOptionFailed();
			}
		  
		}


		
		
	}
	public int AddOption(String sessionID, String option){
		String result=MassVigService.getInstance().AddOption(sessionID,option);
		try{
			JSONObject object = new JSONObject(result);
			int resultCode = object.optInt("ResponseStatus");
			if(resultCode==0){
				return resultCode;
			}
			else{
				return resultCode;
			}
		}catch(Exception e){
			return -1;
		}
		
		
	}
	public interface loadListener{
		 void AddOptionSuccess();
		 void SessionVailed();
		 void AddOptionFailed();
	}
}

		


