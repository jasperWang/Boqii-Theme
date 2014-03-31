package com.massvig.ecommerce.exception;

import org.json.JSONException;
import org.json.JSONObject;

import com.massvig.ecommerce.service.MassVigService;
import com.massvig.ecommerce.utilities.MassVigSystemSetting;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

/**
 * 
 * 异步发送错误日志
 * 
 * @author DuJun
 * 
 */
public class MassVigPushCrashLogAsync extends AsyncTask<String, Void, String> {

	private Context mContext;
	public MassVigPushCrashLogAsync(Context context){
		this.mContext = context;
	}
	
	private long time;
	@Override
	protected String doInBackground(String... params) {
		MassVigService service = MassVigService.getInstance();
		time = Long.valueOf(params[1]);
		return service.pushCrashLog(params[0]);
	}

	@Override
	protected void onPostExecute(String result) {
		try {
			if(!TextUtils.isEmpty(result)){
				JSONObject o = new JSONObject(result);
				int resultCode = o.getInt("ResponseStatus");
				if(resultCode == 0){
					MassVigSystemSetting.getInstance().setCrashLogLastModifiedTime(mContext, time);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
