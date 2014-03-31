package com.massvig.ecommerce.managers;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.massvig.ecommerce.entities.User;
import com.massvig.ecommerce.utilities.MassVigUtil;

public class ModifyManager {

	public User user;
	private Listener listener;
	public static final int ONE = 1;
	public static final int TWO = 2;

	public ModifyManager(User u, Listener l) {
		this.user = u;
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
	public void Modify(String code, int flag) {
		new ModifyAsync().execute(code, flag + "");
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

	private class ModifyAsync extends AsyncTask<String, Void, Void> {

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
			int flag = Integer.valueOf(params[1]);
			int result = -1;
			if(flag == 1){
				result = user.ModifyMobile(params[0]);
			}else{
				result = user.ModifyEmail(params[0]);
			}
			if(result == 0){
				listener.Success(TWO);
			}else{
				listener.Failed(TWO);
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
	}
}
