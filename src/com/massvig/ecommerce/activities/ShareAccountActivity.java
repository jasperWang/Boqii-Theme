package com.massvig.ecommerce.activities;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.utilities.OAuthConstant;
import com.tencent.weibo.api.UserAPI;
import com.tencent.weibo.constants.OAuthConstants;
import com.tencent.weibo.oauthv2.OAuthV2;
import com.tencent.weibo.webview.OAuthV2AuthorizeWebView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

public class ShareAccountActivity extends BaseActivity implements OnClickListener{

	private static final int QQ = 1;
	private TextView text1,text2;
	private OAuthV2 oAuth;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_account);
		setTitle(getString(R.string.shareaccount));
		oAuth = new OAuthV2(OAuthConstant.callback);
		oAuth.setClientId(OAuthConstant.APP_KEY_QQ);
		oAuth.setClientSecret(OAuthConstant.APP_SECRET_QQ);
		text1 = (TextView)findViewById(R.id.text1);
		text2 = (TextView)findViewById(R.id.text2);
		((Button)findViewById(R.id.back)).setOnClickListener(this);
		((TableRow)findViewById(R.id.sina_row)).setOnClickListener(this);
		((TableRow)findViewById(R.id.qq_row)).setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(!TextUtils.isEmpty(MassVigUtil.getPreferenceData(this, OAuthConstant.SINA_ACCESS_TOKEN, ""))){
			text1.setText(getString(R.string.binded));
		}else{
			text1.setText(getString(R.string.not_bind));
		}
		if(!TextUtils.isEmpty(MassVigUtil.getPreferenceData(this, OAuthConstant.QQ_ACCESS_TOKEN, ""))){
			text2.setText(getString(R.string.binded));
		}else{
			text2.setText(getString(R.string.not_bind));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == QQ) {
			if (data != null) {
				oAuth = (OAuthV2) data.getExtras().getSerializable("oauth");
				MassVigUtil.setPreferenceStringData(this,
						OAuthConstant.QQ_ACCESS_TOKEN, oAuth.getAccessToken());
				MassVigUtil.setPreferenceStringData(this,
						OAuthConstant.QQ_CLIENT_IP, oAuth.getClientIP());
				MassVigUtil.setPreferenceStringData(this,
						OAuthConstant.QQ_OPEN_ID, oAuth.getOpenid());
				// 调用API获取用户信息
				UserAPI userAPI = new UserAPI(OAuthConstants.OAUTH_VERSION_2_A);
				try {
					// String response = userAPI.info(oAuth, "json");// 获取用户信息
					// JSONObject res = new JSONObject(response);
					// JSONObject dat = res.getJSONObject("data");
					// String nick = dat.getString("nick");
					MassVigUtil.setPreferenceBooleanData(this,
							OAuthConstant.QQ, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
				userAPI.shutdownConnection();
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.sina_row:
			if(!TextUtils.isEmpty(MassVigUtil.getPreferenceData(this, OAuthConstant.SINA_ACCESS_TOKEN, ""))){
				MassVigUtil.setPreferenceStringData(this, OAuthConstant.SINA_ACCESS_TOKEN, "");
				text1.setText(getString(R.string.not_bind));
			}else{
				startActivity(new Intent(this,ShareWebSinaActivity.class));
			}
			break;
		case R.id.qq_row:
			if(!TextUtils.isEmpty(MassVigUtil.getPreferenceData(this, OAuthConstant.QQ_ACCESS_TOKEN, ""))){
				MassVigUtil.setPreferenceStringData(this, OAuthConstant.QQ_ACCESS_TOKEN, "");
				text2.setText(getString(R.string.not_bind));
			}else{
				Intent intent = new Intent(this,OAuthV2AuthorizeWebView.class);
				intent.putExtra("oauth", oAuth);
				startActivityForResult(intent, QQ);
			}
			break;
		default:
			break;
		}
	}

}
