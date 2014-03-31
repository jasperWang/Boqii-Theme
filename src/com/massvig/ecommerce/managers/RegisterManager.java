package com.massvig.ecommerce.managers;

import java.security.NoSuchAlgorithmException;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.massvig.ecommerce.entities.User;
import com.massvig.ecommerce.service.MassVigService;
import com.massvig.ecommerce.utilities.MassVigUtil;

public class RegisterManager {

	public User user;
	private Listener listener;
	public static final int ONE = 1;
	public static final int TWO = 2;
	public static final int THREE = 3;
	public String responsedata;

	public RegisterManager(Listener l) {
		user = new User();
		listener = l;
	}

	/**
	 * 发送验证码
	 * @param username
	 */
	public void SendCode(String username) {
		if (TextUtils.isEmpty(username)) {
			listener.empty();
			return;
		}
		if (!MassVigUtil.isEmail(username) && !MassVigUtil.isMobileNO(username)) {
			listener.accountErr();
			return;
		}
		if (MassVigUtil.isEmail(username)) {
			user.newEmail = username;
		} else {
			user.newMobile = username;
		}
		new SendCodeAsync().execute();
	}

	/**
	 * check验证码
	 * @param code
	 */
	public void CheckCode(String code) {
		new CheckCodeAsync().execute(code);
	}

	/**
	 * 注册
	 * @param username
	 * @param verifyCode
	 * @param password
	 */
	public void Register(String username, String verifyCode, String password) {
		if (TextUtils.isEmpty(username)) {
			listener.empty();
			return;
		}
		if (!MassVigUtil.isEmail(username) && !MassVigUtil.isMobileNO(username)) {
			listener.accountErr();
			return;
		}
		try {
			String pass = MassVigUtil.getMD5(password);
			user.password = pass;
			if (MassVigUtil.isEmail(username)) {
				user.newEmail = username;
			} else {
				user.newMobile = username;
			}
			new RegisterAsync().execute(verifyCode);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			listener.Failed(THREE);
		}
	}
	public void needregistercode(String v){
		if (TextUtils.isEmpty(v)) {
			listener.empty();
			return;
		}
		if (!MassVigUtil.isEmail(v) && !MassVigUtil.isMobileNO(v)) {
			listener.accountErr();
			return;
		}
		if (MassVigUtil.isEmail(v)) {
			user.newEmail = v;
		} else {
			user.newMobile = v;
		}
		new Needcodeasync(v).execute();
	}
	private  class Needcodeasync extends AsyncTask<Void, Void, Integer>{
		String v;
		public Needcodeasync(String v){
			this.v = v;
		}
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result == 0){
				listener.anotherRegister();
			}else if(result == 1){
				listener.alreadyExit();
			}
		}

		@Override
		protected Integer doInBackground(Void... params) {
			// TODO Auto-generated method stub
			int resultCode = 0;
			String result = "";
			result = MassVigService.getInstance().NeedRegisterCode(v);
			try {
				JSONObject o = new JSONObject(result);
				resultCode = o.optInt("ResponseStatus");
				responsedata = o.optString("ResponseData");
				return resultCode;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
	}

	private class SendCodeAsync extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			listener.start(ONE);
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			listener.end(ONE);
		}

		@Override
		protected Void doInBackground(String... params) {
			int result = user.CheckLoginNameAndSendVerifyCode();
			if (result == 0) {
				listener.Success(ONE);
			} else if(result ==1){
				listener.alreadyExit();
			}else{
				listener.Failed(ONE);
			}
			return null;
		}

	}

	private class CheckCodeAsync extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			listener.start(TWO);
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			listener.end(TWO);
		}

		@Override
		protected Void doInBackground(String... params) {
			boolean success = user.CheckVerifyCode(params[0]);
			if (success) {
				listener.Success(TWO);
			} else {
				listener.Failed(TWO);
			}
			return null;
		}

	}

	private class RegisterAsync extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			listener.start(THREE);
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			listener.end(THREE);
		}

		@Override
		protected Void doInBackground(String... params) {
			boolean success = user.Register(params[0]);
			if (success) {
				listener.register(user);
				listener.Success(THREE);
			} else {
				listener.Failed(THREE);
			}
			return null;
		}

	}

	public interface Listener {
		void start(int index);

		void end(int index);

		void Success(int index);

		void Failed(int index);

		void empty();

		void accountErr();
		
		void alreadyExit();
		void register(User user);
		void anotherRegister();
	}
}
