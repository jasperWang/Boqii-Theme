package com.massvig.ecommerce.activities;

import java.util.Timer;
import java.util.TimerTask;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.managers.ModifyManager;
import com.massvig.ecommerce.managers.ModifyManager.Listener;
import com.massvig.ecommerce.utilities.MassVigUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ModifyActivity extends BaseActivity implements OnClickListener,
		Listener {

	private BaseApplication app;
	public static final int MOBILE = 1;
	public static final int EMAIL = 2;
	private int flag = 0;
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
	private ModifyManager mManager;
	private View view1, view2;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		setTitle(getString(R.string.modify_ac));
		app = (BaseApplication) getApplication();
		mManager = new ModifyManager(app.user, this);
		flag = this.getIntent().getIntExtra("MODIFY", 0);
		initView();
	}

	private void initView() {
		dialog = new ProgressDialog(this);
		dialog.setMessage(getString(R.string.please_wait));
		title = (TextView) findViewById(R.id.title_text);
		title.setText(getString(R.string.step_11));
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
		if(flag == 2){
			username1.setHint(R.string.enter_email);
			username1.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		}
			
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			if (index == 0) {
				ModifyActivity.this.finish();
			} else if (index == 1) {
				view2.setVisibility(View.INVISIBLE);
				view1.setVisibility(View.VISIBLE);
				title.setText(getString(R.string.step_11));
				finish.setBackgroundResource(R.drawable.left_btn);
				finish.setText(getString(R.string.next));
				index --;
			}
			break;
		case R.id.finish:
			if (index == 0) {
				name = username1.getText().toString();
				if(flag == 1){
					if(!MassVigUtil.isMobileNO(name)){
						Toast.makeText(this, getString(R.string.isnotmobi), Toast.LENGTH_SHORT).show();
						return;
					}
				}else if(flag == 2){
					if(!MassVigUtil.isEmail(name)){
						Toast.makeText(this, getString(R.string.isnotemail), Toast.LENGTH_SHORT).show();
						return;
					}
				}
				mManager.SendCode(name);
			} else if (index == 1) {
				codeText = code.getText().toString();
				if(!TextUtils.isEmpty(codeText)){
					mManager.Modify(codeText, flag);
				}else{
					Toast.makeText(ModifyActivity.this, getString(R.string.enter_code), Toast.LENGTH_SHORT).show();
				}
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
		case R.id.send:
			if (!timeout) {
			} else {
				timeout = false;
				timeCount = 60;
				send.setText("60" + getString(R.string.wait_second));
				send.setBackgroundResource(R.drawable.bg_big_btn_02);
				send.setEnabled(false);
				timeDecrease();
				mManager.SendCode(name);
			}
			break;
		default:
			break;
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
		Message msg = new Message();
		msg.what = ERROR;
		msg.obj = getString(R.string.isnotmobile);
		mHandler.sendMessage(msg);
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SUCCESS:
				switch (msg.arg1) {
				case ModifyManager.ONE:
					break;
				case ModifyManager.TWO:
					ModifyActivity.this.finish();
					break;
				default:
					break;
				}
				break;
			case FAILED:
				switch (msg.arg1) {
				case ModifyManager.ONE:
					break;
				case ModifyManager.TWO:
					break;

				default:
					break;
				}
				break;
			case EMPTY:
				Toast.makeText(ModifyActivity.this, getString(R.string.enter_mobile),
						Toast.LENGTH_SHORT).show();
				break;
			case START:
				switch (msg.arg1) {
				case ModifyManager.ONE:
					break;
				case ModifyManager.TWO:
					if (dialog != null && !dialog.isShowing())
						dialog.show();
					break;

				default:
					break;
				}
				break;
			case END:
				switch (msg.arg1) {
				case ModifyManager.ONE:
					if (!timeout) {
					} else {
						timeout = false;
						timeCount = 60;
						send.setText("60" + getString(R.string.wait_second));
						send.setBackgroundResource(R.drawable.bg_big_btn_02);
						send.setEnabled(false);
						timeDecrease();
					}
					view1.setVisibility(View.INVISIBLE);
					view2.setVisibility(View.VISIBLE);
					title.setText(getString(R.string.step_22));
					finish.setBackgroundResource(R.drawable.right_btn);
					finish.setText(getString(R.string.finish));
					index = 1;
					mobile.setText(getString(R.string.sended_mobile, username1.getText().toString()));
					break;
				case ModifyManager.TWO:
					if (dialog != null && dialog.isShowing())
						dialog.dismiss();
					break;
				default:
					break;
				}
				break;
			case ERROR:
				Toast.makeText(ModifyActivity.this, String.valueOf(msg.obj),Toast.LENGTH_SHORT).show();
				break;
			case TIME:
				if (msg.arg1 > 1) {
					send.setText("" + msg.arg1);
				} else {
					send.setBackgroundResource(R.drawable.bg_unselected);
					task.cancel();
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

	@Override
	public void alreadyExit() {
		Message msg = new Message();
		msg.what = ERROR;
		msg.obj = getString(R.string.already_exist);
		mHandler.sendMessage(msg);
	}

}
