package com.massvig.ecommerce.managers;

import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.massvig.ecommerce.entities.User;
import com.massvig.ecommerce.utilities.MassVigUtil;

public class FindPasswordManager {

	public User user;
	private Listener listener;
	public static final int ONE = 1;
	public static final int TWO = 2;
	public static final int THREE = 3;

	public FindPasswordManager(Listener l) {
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
	 * 验证验证码
	 * @param code
	 */
	public void CheckCode(String code) {
		new CheckCodeAsync().execute(code);
	}

	/**
	 * 修改密码
	 * @param username
	 * @param verifyCode
	 * @param password
	 */
	public void FindPassword(Context context, String username, String verifyCode, String password) {
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
			user.newPassword = pass;
			if (MassVigUtil.isEmail(username)) {
				user.newEmail = username;
			} else {
				user.newMobile = username;
			}
			new FindPasswordAsync().execute(verifyCode);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			listener.Failed(THREE);
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
			boolean success = user.SendVerifyCodeWithCheck();
			if (success) {
				listener.Success(ONE);
			} else {
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

	private class FindPasswordAsync extends AsyncTask<String, Void, Void> {

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
			boolean success = user.FindPassword(params[0]);
			if (success) {
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
	}
}
