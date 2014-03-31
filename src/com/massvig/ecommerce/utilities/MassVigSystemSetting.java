package com.massvig.ecommerce.utilities;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MassVigSystemSetting {
	
	private static MassVigSystemSetting instance;
	
	public static MassVigSystemSetting getInstance(){
		if(instance == null){
			instance = new MassVigSystemSetting();
		}
		return instance;
	}
	
	public void setLocationAllow(Context context,Boolean isAllow){
		Editor mEditor = getSharedPreferences(context).edit();
		mEditor.putBoolean("allowLocation", isAllow);
		mEditor.commit();
	}
	
	public Boolean getLocationAllow(Context context){
		return getSharedPreferences(context).getBoolean("allowLocation", false);
	}
	
	public void setCityName(Context context,String cityName){
		Editor mEditor = getSharedPreferences(context).edit();
		mEditor.putString("cityName", cityName);
		mEditor.commit();
	}
	
	public String getCityName(Context context){
		return getSharedPreferences(context).getString("cityName", "");
	}
	
	public void setCityId(Context context,int cityId){
		Editor mEditor = getSharedPreferences(context).edit();
		mEditor.putInt("cityId", cityId);
		mEditor.commit();
	}
	
	public int getCityId(Context context){
		return getSharedPreferences(context).getInt("cityId", 339);
	}
	
	/**
	 * 保存日志最后修改时间
	 * @param context
	 * @param time measured in milliseconds since January 1st, 1970
	 */
	public void setCrashLogLastModifiedTime(Context context, long time){
		Editor edit = getSharedPreferences(context).edit();
		edit.putLong("LAST_MODIFIED", time).commit();
	}
	
	/**
	 * 获取错误日志最后修改时间，避免重复提交错误日志
	 * @param context
	 * @return
	 */
	public long getCrashLogLastModifiedTime(Context context){
		return getSharedPreferences(context).getLong("LAST_MODIFIED", 0);
	}

	/**
	 * 保存补到店ticketID
	 * @param context
	 * @param flag
	 */
	public void setArrivedTicketID(Context context, String ticketID){
		Editor edit = getSharedPreferences(context).edit();
		edit.putString("TICKET_ID", ticketID).commit();
	}
	
	/**
	 * 获取补到店ticketID
	 * @param context
	 * @return
	 */
	public String getArrivedTicketID(Context context){
		return getSharedPreferences(context).getString("TICKET_ID", "");
	}
	
	/**
	 * 保存补到店seatTypeID
	 * @param context
	 * @param flag
	 */
	public void setArrivedSeattypeID(Context context, String seatTypeID){
		Editor edit = getSharedPreferences(context).edit();
		edit.putString("SEATTYPE_ID", seatTypeID).commit();
	}
	
	/**
	 * 获取补到店seatTypeID
	 * @param context
	 * @return
	 */
	public String getArrivedSeattypeID(Context context){
		return getSharedPreferences(context).getString("SEATTYPE_ID", "");
	}
	
	/**
	 * 保存是否是补到店
	 * @param context
	 * @param flag
	 */
	public void setArrivedFlag(Context context, int flag){
		Editor edit = getSharedPreferences(context).edit();
		edit.putInt("TICKET_FLAG", flag).commit();
	}
	
	/**
	 * 获取是否补到店
	 * @param context
	 * @return
	 */
	public int getArrivedFlag(Context context){
		return getSharedPreferences(context).getInt("TICKET_FLAG", 0);
	}
	
	/**
	 * 保存是否是领号
	 * @param context
	 * @param flag
	 */
	public void setGetTicketFlag(Context context, int flag){
		Editor edit = getSharedPreferences(context).edit();
		edit.putInt("GET_TICKET_FLAG", flag).commit();
	}
	
	/**
	 * 获取是否是领号
	 * @param context
	 * @return
	 */
	public int getGetTicketFlag(Context context){
		return getSharedPreferences(context).getInt("GET_TICKET_FLAG", 0);
	}
	
	/**
	 * 保存tabhost状态
	 * @param context
	 * @param flag
	 */
	public void setTabHostFlag(Context context, int flag){
		Editor edit = getSharedPreferences(context).edit();
		edit.putInt("TABHOST_FLAG", flag).commit();
	}
	
	/**
	 * 获取tabhost状态
	 * @param context
	 * @return
	 */
	public int getTabHostFlag(Context context){
		return getSharedPreferences(context).getInt("TABHOST_FLAG", 0);
	}
	
	/**
	 * 缓存积分
	 * @param context
	 * @param coin
	 */
	public void setCoin(Context context, int coin){
		Editor edit = getSharedPreferences(context).edit();
		edit.putInt("COIN", coin).commit();
	}
	
	/**
	 * 读取缓存中积分
	 * @param context
	 * @return
	 */
	public int getCoin(Context context){
		return getSharedPreferences(context).getInt("COIN", 0);
	}
	
	public void setFirstUse(Context context,Boolean isFirstUse){
		Editor mEditor = getSharedPreferences(context).edit();
		mEditor.putBoolean("isFirstUse", isFirstUse);
		mEditor.commit();
	}
	
	public Boolean getFirstUse(Context context){
		return getSharedPreferences(context).getBoolean("isFirstUse", true);
	}
	
	public void setLocalVersionName(Context context,String versionName){
		Editor mEditor = getSharedPreferences(context).edit();
		mEditor.putString("LocalVersionName", versionName);
		mEditor.commit();
	}
	
	public String getLocalVersionName(Context context){
		return getSharedPreferences(context).getString("LocalVersionName", "1.0.0");
	}
	
	/**
	 * 根据PrefsFileName 读取 SharedPreferences
	 * @param JSONObject requestData
	 * @return SharedPreferences 
	 * @author DuJun
	 */
	
	public SharedPreferences getSharedPreferences(Context context){
		return context.getSharedPreferences("SysSettingPrefsFile", 0);
	}
	
}
