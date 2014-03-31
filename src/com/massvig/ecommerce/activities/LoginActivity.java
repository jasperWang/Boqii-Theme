package com.massvig.ecommerce.activities;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.managers.LoginManager;
import com.massvig.ecommerce.managers.LoginManager.Listener;
import com.massvig.ecommerce.service.EcommercePushService;
import com.massvig.ecommerce.utilities.MassVigUtil;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends BaseActivity implements OnClickListener,
		Listener {

	private EditText username, password;
	private LoginManager mManager;
	private ProgressDialog dialog;
	private static final int SUCCESS = 1;
	private static final int FAILED = 2;
	private static final int EMPTY = 3;
	private static final int START = 4;
	private static final int END = 5;
	private static final int ERROR = 6;
	private static final int INFO_SUCC = 7;
	private static final int INFO_FAIL = 8;
	private BaseApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.login));
		setContentView(R.layout.login);
		app = (BaseApplication) getApplication();
		mManager = new LoginManager(this);
		app.user = mManager.user;
		initView();
	}

	private void initView() {
		dialog = new ProgressDialog(this);
		dialog.setMessage(getString(R.string.please_wait));
		((Button) findViewById(R.id.back)).setOnClickListener(this);
		((Button) findViewById(R.id.finish)).setOnClickListener(this);
		((TextView) findViewById(R.id.clear)).setOnClickListener(this);
		((LinearLayout) findViewById(R.id.register)).setOnClickListener(this);
		((LinearLayout) findViewById(R.id.find_password))
				.setOnClickListener(this);
		username = (EditText) findViewById(R.id.username);
		username.setText(MassVigUtil.getPreferenceData(this, "USERNAME", ""));
		password = (EditText) findViewById(R.id.password);
		password.setText(MassVigUtil.getPreferenceData(this, "PASSWORD", ""));
		password.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(!TextUtils.isEmpty(s.toString())){
					((TextView) findViewById(R.id.clear)).setVisibility(View.VISIBLE);
				}else{
					((TextView) findViewById(R.id.clear)).setVisibility(View.INVISIBLE);
				}
			}
		});
	}

	private void startService() {
//		String serviceName = "com.massvig.ecommerce.service.EcommercePushService";
//		ActivityManager mActiviryManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//		List<ActivityManager.RunningServiceInfo> mServiceList = mActiviryManager
//				.getRunningServices(30);
//		boolean isRunning = MassVigUtil.ServiceIsStart(mServiceList,serviceName);
			stopService(new Intent(LoginActivity.this,
					EcommercePushService.class));
			startService(new Intent(LoginActivity.this,
					EcommercePushService.class));
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SUCCESS:
				mManager.GetEJServerInfo();
				break;
			case FAILED:
				Toast.makeText(LoginActivity.this, getString(R.string.login_fail), Toast.LENGTH_SHORT)
						.show();
				if (dialog != null && dialog.isShowing())
					dialog.dismiss();
				break;
			case EMPTY:
				Toast.makeText(LoginActivity.this, getString(R.string.enter_mobile),
						Toast.LENGTH_SHORT).show();
				break;
			case START:
				if (dialog != null && !dialog.isShowing())
					dialog.show();
				break;
			case END:
				if (dialog != null && dialog.isShowing())
					dialog.dismiss();
				break;
			case ERROR:
				Toast.makeText(LoginActivity.this, getString(R.string.isnotmobi),
						Toast.LENGTH_SHORT).show();
				break;
			case INFO_SUCC:
				Toast.makeText(LoginActivity.this, getString(R.string.login_succ), Toast.LENGTH_SHORT)
						.show();
				app.user = mManager.user;
				MassVigUtil.setPreferenceStringData(LoginActivity.this, "USERNAME", username.getText().toString());
				MassVigUtil.setPreferenceStringData(LoginActivity.this, "PASSWORD", password.getText().toString());
				MassVigUtil.setPreferenceStringData(LoginActivity.this, "CUSTOMERID", app.user.customerID + "");
				MassVigUtil.setPreferenceStringData(LoginActivity.this, "HEADIMG", app.user.headImage);
				MassVigUtil.setPreferenceStringData(LoginActivity.this, "GENDER", app.user.gender + "");
				MassVigUtil.setPreferenceStringData(LoginActivity.this, "NICKNAME", app.user.nickName);
				MassVigUtil.setPreferenceStringData(LoginActivity.this, "SESSIONID", app.user.sessionID);
				MassVigUtil.setPreferenceStringData(LoginActivity.this, "IP", app.user.EJServerIP);
				MassVigUtil.setPreferenceStringData(LoginActivity.this, "PORT", app.user.EJServerPort);
				MassVigUtil.setPreferenceStringData(LoginActivity.this, "Domain", app.user.EJServerDomaim);
				MassVigUtil.setPreferenceStringData(LoginActivity.this, "EJID", app.user.EJID);
				MassVigUtil.setPreferenceStringData(LoginActivity.this, "EJPASS", app.user.EJPassword);
				MassVigUtil.setPreferenceStringData(LoginActivity.this, "EJRESOURCE", app.user.EJResource);
				startService();
				LoginActivity.this.finish();
				break;
			case INFO_FAIL:
				Toast.makeText(LoginActivity.this, getString(R.string.login_fail), Toast.LENGTH_SHORT)
						.show();
				break;
			default:
				break;
			}
		}

	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			LoginActivity.this.finish();
			break;
		case R.id.finish:
			mManager.Login(username.getText().toString(), password.getText()
					.toString());
			break;
		case R.id.clear:
			password.setText("");
			break;
		case R.id.register:
			startActivity(new Intent(this, RegisterActivity.class));
			LoginActivity.this.finish();
			break;
		case R.id.find_password:
			startActivity(new Intent(this, FindPasswordActivity.class));
			break;
		default:
			break;
		}
	}

	@Override
	public void start() {
		mHandler.sendEmptyMessage(START);
	}

	@Override
	public void end() {
		mHandler.sendEmptyMessage(END);
	}

	@Override
	public void LoginSuccess() {
		mHandler.sendEmptyMessage(SUCCESS);
	}

	@Override
	public void LoginFailed() {
		mHandler.sendEmptyMessage(FAILED);
	}

	@Override
	public void empty() {
		mHandler.sendEmptyMessage(EMPTY);
	}

	@Override
	public void accountErr() {
		mHandler.sendEmptyMessage(ERROR);
	}

	@Override
	public void GetInfoSuccess() {
		mHandler.sendEmptyMessage(INFO_SUCC);
	}

	@Override
	public void GetInfoFailed() {
		mHandler.sendEmptyMessage(INFO_FAIL);
	}

}
