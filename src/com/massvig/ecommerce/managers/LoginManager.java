package com.massvig.ecommerce.managers;

import java.security.NoSuchAlgorithmException;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.massvig.ecommerce.entities.User;
import com.massvig.ecommerce.utilities.MassVigUtil;

public class LoginManager {

	public User user;
	private Listener listener;
	public LoginManager(Listener l){
		user = new User();
		listener = l;
	}
	
	public void GetEJServerInfo(){
		new AsyncTask<Void, Void, Boolean>(){

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				listener.end();
				if(result){
					listener.GetInfoSuccess();
				}else{
					listener.GetInfoFailed();
				}
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				return user.GetEJServerInfo();
			}}.execute();
	}
	
	public void Login(String username, String password){
		if(TextUtils.isEmpty(username)){
			listener.empty();
			return;
		}
		if(!MassVigUtil.isEmail(username) && !MassVigUtil.isMobileNO(username)){
			listener.accountErr();
			return;
		}
		try {
			String pass = MassVigUtil.getMD5(password);
			new LoginAsync().execute(username, pass);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			listener.LoginFailed();
		}
	}
	
	private class LoginAsync extends AsyncTask<String, Void, Void>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			listener.start();
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}

		@Override
		protected Void doInBackground(String... params) {
			boolean success = user.Login(params[0], params[1]);
			if(success){
				listener.LoginSuccess();
			}else{
				listener.LoginFailed();
			}
			return null;
		}
		
	}
	
	public interface Listener{
		void start();
		void end();
		void LoginSuccess();
		void LoginFailed();
		void empty();
		void accountErr();
		void GetInfoSuccess();
		void GetInfoFailed();
	}
}
