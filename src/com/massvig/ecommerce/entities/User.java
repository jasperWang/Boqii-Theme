package com.massvig.ecommerce.entities;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.massvig.ecommerce.service.MassVigService;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigUtil;

public class User{

	public String sessionID = "";
	public String mobile = "";
	public String email = "";
	public String password;
	public String nickName = "";
	public String headImage = "";
	public int gender = 0;
	public String EJID;
	public String EJPassword;
	public String EJResource;
	public int customerID = -1;
	public String EJServerIP = "112.65.247.173";
	public String EJServerPort = "5222";
	public String EJServerDomaim = "es";
	public int ShoppingCartTotalNum;

	public String newMobile,newEmail,newPassword;

	/**
	 * 发送验证码（不存在才发送）
	 * @return
	 */
	public int CheckLoginNameAndSendVerifyCode(){
		String result = MassVigService.getInstance().CheckLoginNameAndSendVerifyCode(!TextUtils.isEmpty(newMobile) ? newMobile : newEmail);
		try {
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
			if(resultCode == 0){
				return 0;
			}else{
				return resultCode;
			}
		} catch (Exception e) {
			return -1;
		}
	}
	/**
	 * 发送验证码（存在才发送）
	 * @return
	 */
	public boolean SendVerifyCodeWithCheck(){
		String result = MassVigService.getInstance().SendVerifyCodeWithCheck(!TextUtils.isEmpty(newMobile) ? newMobile : newEmail);
		try {
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
			if(resultCode == 0){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 * 注册
	 * @return
	 */
	public boolean Register(String verifyCode){
		String result = MassVigService.getInstance().Register(!TextUtils.isEmpty(newMobile) ? newMobile : newEmail, verifyCode,password);
		try {
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
			if(resultCode == 0){
				mobile = newMobile;
				email = newEmail;
				JSONObject o = object.getJSONObject("ResponseData");
				this.headImage = o.getString("HeadImgUrl");
				this.nickName = o.getString("NickName");
				this.sessionID = o.getString("SessionID");
				this.customerID = o.getInt("CustomerID");
				this.gender = o.getInt("Gender");
				this.EJID = o.getString("EJID");
				this.EJPassword = o.getString("EJPassword");
				this.EJResource = o.getString("EJResource");
				if(o.has("Email")){
					this.email = o.getString("Email");
				}
				if(o.has("Mobile")){
					this.mobile = o.getString("Mobile");
				}
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 * 登录
	 * @return
	 */
	public boolean Login(String loginName, String pass){
		String result = MassVigService.getInstance().Login(loginName, pass);
		try {
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
			if(resultCode == 0){
				password = pass;
				if(!MassVigUtil.isEmail(loginName)){
					mobile = loginName;
				}else{
					email = loginName;
				}
				JSONObject o = object.getJSONObject("ResponseData");
				this.headImage = o.getString("HeadImgUrl");
				this.nickName = o.getString("NickName");
				this.ShoppingCartTotalNum = o.optInt("ShoppingCartTotalNum");
				this.sessionID = o.getString("SessionID");
				this.customerID = o.getInt("CustomerID");
				this.gender = o.getInt("Gender");
				this.EJID = o.getString("EJID");
				this.EJPassword = o.getString("EJPassword");
				this.EJResource = o.getString("EJResource");
				if(o.has("Email")){
					this.email = o.getString("Email");
				}
				if(o.has("Mobile")){
					this.mobile = o.getString("Mobile");
				}
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 * 检测session是否失效
	 * @return
	 */
	public boolean VerifySession(){
		String result = MassVigService.getInstance().VerifySession(this.sessionID);
		try {
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
			if(resultCode == 0){
				return true;
			}else{
				sessionID = "";
				customerID = -1;
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 * 找回密码
	 * @return
	 */
	public boolean FindPassword(String verifyCode){
		String result = MassVigService.getInstance().FindPassword(newMobile, verifyCode, newPassword);
		try {
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
			if(resultCode == 0){
				password = newPassword;
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 * 修改密码
	 * @return
	 */
	public int ModifyPassword(String oriPassword, String newPassword){
		String result = MassVigService.getInstance().ModifyPassword(this.sessionID, oriPassword, newPassword);
		try {
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
			if(resultCode == 0){
				password = newPassword;
				return 0;
			}else if(resultCode == MassVigContants.SESSIONVAILED){
				sessionID = "";
				customerID = -1;
				return resultCode;
			}else{
				return resultCode;
			}
		} catch (Exception e) {
			return -8;
		}
	}
	/**
	 * 修改用户基本信息
	 * @return
	 */
	public int ModifyCustomInfo(String sessionid,String newPassword, String nick, String headimg, int gender){
		String result = MassVigService.getInstance().ModifyCustomInfo(sessionid, nick, headimg, gender);
		try {
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
			if(resultCode == 0){
				nickName = nick;
				JSONObject data = object.getJSONObject("ResponseData");
				this.headImage = data.getString("HeadImgUrl");
				this.gender = gender;
				return 0;
			}else if(resultCode == MassVigContants.SESSIONVAILED){
				sessionID = "";
				customerID = -1;
				return resultCode;
			}else{
				return resultCode;
			}
		} catch (Exception e) {
			return -8;
		}
	}
	/**
	 * 修改手机
	 * @return
	 */
	public int ModifyMobile(String verifyCode){
		String result = MassVigService.getInstance().ModifyMobile(sessionID, verifyCode);
		try {
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
			if(resultCode == 0){
				mobile = newMobile;
				return 0;
			}else if(resultCode == MassVigContants.SESSIONVAILED){
				sessionID = "";
				customerID = -1;
				return resultCode;
			}else{
				return resultCode;
			}
		} catch (Exception e) {
			return -8;
		}
	}
	/**
	 * 修改邮箱
	 * @return
	 */
	public int ModifyEmail(String verifyCode){
		String result = MassVigService.getInstance().ModifyEmail(sessionID, verifyCode);
		try {
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
			if(resultCode == 0){
				email = newEmail;
				return 0;
			}else if(resultCode == MassVigContants.SESSIONVAILED){
				sessionID = "";
				customerID = -1;
				return resultCode;
			}else{
				return resultCode;
			}
		} catch (Exception e) {
			return -8;
		}
	}
	/**
	 * 验证验证码
	 * @return
	 */
	public boolean CheckVerifyCode(String verifyCode){
		String result = MassVigService.getInstance().CheckVerifyCode(!TextUtils.isEmpty(newMobile) ? newMobile : newEmail, verifyCode);
		try {
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
			if(resultCode == 0){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 获取聊天服务器信息
	 * @return
	 */
	public boolean GetEJServerInfo(){
		if(!TextUtils.isEmpty(sessionID)){
			String result = MassVigService.getInstance().GetEJServerInfo(sessionID);
			if(!TextUtils.isEmpty(result))
			try {
				JSONObject o = new JSONObject(result);
				int code = o.getInt("ResponseStatus");
				if(code == 0){
					JSONObject data = o.getJSONObject("ResponseData");
					this.EJServerIP = data.getString("EJServerIP");
					this.EJServerPort = data.getString("EJServerPort");
					this.EJServerDomaim = data.getString("EJServerDomaim");
					return true;
				}
				return false;
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
}
