package com.massvig.ecommerce.activities;

import java.util.Timer;
import java.util.TimerTask;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.managers.FindPasswordManager;
import com.massvig.ecommerce.managers.FindPasswordManager.Listener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FindPasswordActivity extends BaseActivity implements OnClickListener,
		Listener {

	private Timer timer;
	private TimerTask task;
	private boolean timeout = true;
	private int timeCount = 60;
	private static final int SUCCESS = 1;
	private static final int FAILED = 2;
	private static final int EMPTY = 3;
	private static final int START = 4;
	private static final int END = 5;
	private static final int ERROR = 6;
	private static final int TIME = 7;
	private FindPasswordManager mManager;
	private View view1, view2, view3;
	private int index = 0;
	private ProgressDialog dialog;
	private TextView title;
	private Button finish;
	private String name;
	private String codeText;
	// 1
	private EditText username1;
	// 2
	private EditText code;
	private TextView mobile, send;
	// 3
	private EditText password1, password2;
	private boolean sendCode = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_password);
		setTitle(getString(R.string.findpassword));
		mManager = new FindPasswordManager(this);
		initView();
	}

	private void initView() {
		dialog = new ProgressDialog(this);
		dialog.setMessage(getString(R.string.please_wait));
		title = (TextView) findViewById(R.id.title_text);
		finish = (Button) findViewById(R.id.finish);
		finish.setOnClickListener(this);
		((Button) findViewById(R.id.back)).setOnClickListener(this);
		view1 = (View)findViewById(R.id.register_1);
		((TextView) findViewById(R.id.clear)).setOnClickListener(this);
		((TextView) findViewById(R.id.agree)).setOnClickListener(this);
		username1 = (EditText) findViewById(R.id.username1);
		view2 = (View)findViewById(R.id.register_2);
		((TextView) findViewById(R.id.clear2)).setOnClickListener(this);
		code = (EditText) findViewById(R.id.code);
		mobile = (TextView) findViewById(R.id.mobile);
		send = (TextView) findViewById(R.id.send);
		send.setOnClickListener(this);
		view3 = (View)findViewById(R.id.register_3);
		password1 = (EditText) findViewById(R.id.password1);
		password2 = (EditText) findViewById(R.id.password2);
		((TextView) findViewById(R.id.clear3)).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			if (index == 0) {
				FindPasswordActivity.this.finish();
			} else if (index == 1) {
				view2.setVisibility(View.INVISIBLE);
				view1.setVisibility(View.VISIBLE);
				title.setText(getString(R.string.step_1));
				username1.setEnabled(true);
				index --;
			} else if (index == 2) {
				title.setText(getString(R.string.step_2));
				view3.setVisibility(View.INVISIBLE);
				view2.setVisibility(View.VISIBLE);
				finish.setText(getString(R.string.next));
				index --;
			}
			break;
		case R.id.finish:
			if (index == 0) {
				name = username1.getText().toString();
				collapseSoftInputMethod(username1);
				mManager.SendCode(name);
			} else if (index == 1) {
				codeText = code.getText().toString();
				if(!TextUtils.isEmpty(codeText)){
					mManager.CheckCode(codeText);
				}else{
					Toast.makeText(FindPasswordActivity.this, getString(R.string.enter_code), Toast.LENGTH_SHORT).show();
				}
			} else if (index == 2) {
				String pass = password1.getText().toString();
				String pass2 = password2.getText().toString();
				if(TextUtils.isEmpty(pass) || TextUtils.isEmpty(pass2)){
					Toast.makeText(FindPasswordActivity.this, getString(R.string.enter_password), Toast.LENGTH_SHORT).show();
					return;
				}
				if(!pass.equals(pass2)){
					Toast.makeText(FindPasswordActivity.this, getString(R.string.diff_password), Toast.LENGTH_SHORT).show();
					return;
				}
				mManager.FindPassword(this, name, code.getText().toString(), pass);
			}
			break;
		case R.id.agree:
			break;
		case R.id.clear:
			username1.setText("");
			break;
		case R.id.clear2:
			code.setText("");
			break;
		case R.id.clear3:
			password1.setText("");
			break;
		case R.id.send:
			startTime();
			mManager.SendCode(name);
			break;
		default:
			break;
		}
	}

	private void startTime() {
		if (!timeout) {
		} else {
			timeout = false;
			timeCount = 60;
			send.setText("60" + getString(R.string.wait_second));
			send.setBackgroundResource(R.drawable.bg_big_btn_02);
			send.setEnabled(false);
			timeDecrease();
		}
		
	}

	@Override
	public void start(int index) {
		Message msg = new Message();
		msg.what = START;
		msg.arg1 = index;
		mHandler.sendMessage(msg);
	}

	@Override
	public void end(int index) {
		Message msg = new Message();
		msg.what = END;
		msg.arg1 = index;
		mHandler.sendMessage(msg);
	}

	@Override
	public void Success(int index) {
		Message msg = new Message();
		msg.what = SUCCESS;
		msg.arg1 = index;
		mHandler.sendMessage(msg);
	}

	@Override
	public void Failed(int index) {
		Message msg = new Message();
		msg.what = FAILED;
		msg.arg1 = index;
		mHandler.sendMessage(msg);
	}

	@Override
	public void empty() {
		mHandler.sendEmptyMessage(EMPTY);
	}

	@Override
	public void accountErr() {
		mHandler.sendEmptyMessage(ERROR);
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SUCCESS:
				switch (msg.arg1) {
				case FindPasswordManager.ONE:
					mobile.setText(getString(R.string.sended_mobile, username1.getText().toString()));
					sendCode = true;
					break;
				case FindPasswordManager.TWO:
					title.setText(getString(R.string.step_3));
					view2.setVisibility(View.INVISIBLE);
					view3.setVisibility(View.VISIBLE);
					finish.setText(getString(R.string.finish));
					index ++;
					break;
				case FindPasswordManager.THREE:
					FindPasswordActivity.this.finish();
					break;
				default:
					break;
				}
				break;
			case FAILED:
				switch (msg.arg1) {
				case FindPasswordManager.ONE:
					sendCode = false;
					Toast.makeText(FindPasswordActivity.this, getString(R.string.none_exist), Toast.LENGTH_SHORT).show();
					break;
				case FindPasswordManager.TWO:
					break;
				case FindPasswordManager.THREE:
					Toast.makeText(FindPasswordActivity.this, getString(R.string.refresh_failed), Toast.LENGTH_SHORT).show();
					break;

				default:
					break;
				}
				break;
			case EMPTY:
				Toast.makeText(FindPasswordActivity.this, getString(R.string.enter_mobile),
						Toast.LENGTH_SHORT).show();
				break;
			case START:
				switch (msg.arg1) {
				case FindPasswordManager.ONE:
					break;
				case FindPasswordManager.TWO:
				case FindPasswordManager.THREE:
					if (dialog != null && !dialog.isShowing())
						dialog.show();
					break;

				default:
					break;
				}
				break;
			case END:

				switch (msg.arg1) {
				case FindPasswordManager.ONE:
					if(sendCode){
						view1.setVisibility(View.INVISIBLE);
						view2.setVisibility(View.VISIBLE);
						title.setText(getString(R.string.step_2));
						index = 1;
						startTime();
						}
					break;
				case FindPasswordManager.TWO:
				case FindPasswordManager.THREE:
					if (dialog != null && dialog.isShowing())
						dialog.dismiss();
					break;
				default:
					break;
				}
				break;
			case ERROR:
				Toast.makeText(FindPasswordActivity.this, getString(R.string.isnotmobile),
						Toast.LENGTH_SHORT).show();
				break;
			case TIME:
				if (msg.arg1 > 1) {
					send.setText("" + msg.arg1 + getString(R.string.wait_second));
				} else {
					send.setBackgroundResource(R.drawable.bg_big_btn);
					send.setEnabled(true);
					send.setText("60" + getString(R.string.wait_second));
					timeout = true;
				}
				super.handleMessage(msg);
			
				break;
			default:
				break;
			}
		}

	};

	protected void timeDecrease() {
		new CountDownTimer(60000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
				timeCount--;
				send.setText(timeCount + getString(R.string.wait_second));
			}
			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				Message msg = new Message();
				msg.what = TIME;
				msg.arg1 = timeCount;
				mHandler.sendMessage(msg);
			}
		}.start();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

//		timer = new Timer();
//		task = new TimerTask() {
//
//			@Override
//			public void run() {
//				Message msg = new Message();
//				msg.what = TIME;
//				timeCount--;
//				msg.arg1 = timeCount;
//				mHandler.sendMessage(msg);
//			}
//		};
//		send.setBackgroundResource(R.drawable.bg_big_btn);
//		send.setText("60" + getString(R.string.wait_second));
//		timeout = true;
	}
	
	public void collapseSoftInputMethod(EditText inputText){
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(inputText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
	}

	@Override
	protected void onPause() {

		if (task != null) {
			task.cancel();
			task = null;
		}
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
		super.onPause();

	}

}
