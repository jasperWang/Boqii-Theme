package com.massvig.ecommerce.utilities;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONObject;

import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.constants.OAuthConstants;
import com.tencent.weibo.oauthv2.OAuthV2;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboParameters;

import android.content.Context;
import android.text.TextUtils;

public class ShareManager{

	private Context mContext;

	public ShareManager(Context context) {
		this.mContext = context;
	}
	
	public boolean shareToQQ(String content, String picpath, double lng, double lat){
	
		boolean success = false;
		TAPI t = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
		String result;
		try {
			OAuthV2 oAuth = new OAuthV2();
			oAuth.setAccessToken(MassVigUtil.getPreferenceData(mContext, OAuthConstant.QQ_ACCESS_TOKEN, ""));
			oAuth.setClientIP(MassVigUtil.getPreferenceData(mContext, OAuthConstant.QQ_CLIENT_IP, ""));
			oAuth.setOpenid(MassVigUtil.getPreferenceData(mContext, OAuthConstant.QQ_OPEN_ID, ""));
			oAuth.setClientId(OAuthConstant.APP_KEY_QQ);
			result = t.addPic(oAuth, "json", content, oAuth.getClientIP(), lng + "", lat + "", picpath, "0");
			JSONObject o = new JSONObject(result);
			if(o.getString("msg").equals("ok")){
				success = true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			success = false;
		}
		return success;
	}
	
	public boolean shareToSina(String content, String picPath, double lng, double lat){
		boolean success = false;
		Weibo mWeibo = Weibo.getInstance();
		WeiboParameters params = new WeiboParameters();
    	try {
			params.add("status", URLEncoder.encode(content, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	//TODO
    	params.add("pic", picPath);
    	params.add("source", OAuthConstant.APP_KEY_SINA);
    	params.add("access_token", MassVigUtil.getPreferenceData(mContext, OAuthConstant.SINA_ACCESS_TOKEN, ""));
    	params.add("lat", Float.valueOf(lat + "") + "");
    	params.add("lng", Float.valueOf(lng + "") + "");
		try{
			String res = mWeibo.request(mContext, Weibo.SERVER + "statuses/upload.json", params, "POST", null);
			if(!new JSONObject(res).isNull("created_at")){
				success = true;
			}else if(res.indexOf("21319") != -1){ //授权关系已经被解除
				MassVigUtil.outLogin(mContext);
				success = false;
			}else{
				success = false;
			}
		}catch(Exception e){
			if(!TextUtils.isEmpty(e.getMessage()) && e.getMessage().contains("repeat"))
				success = true;
			e.printStackTrace();
		}
		return success;
	}
	

}
