package com.massvig.ecommerce.activities;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.managers.AddOptionManager;
import com.massvig.ecommerce.managers.AddOptionManager.loadListener;
import com.massvig.ecommerce.utilities.MassVigUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class AddOption extends BaseActivity implements OnClickListener{
	private Button go_finish,go_back;
	private EditText option;
	private AddOptionManager mManager;
	private BaseApplication app;
	public boolean isclick=false;
	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setTitle(getString(R.string.AddOption_title));
		setContentView(R.layout.option);
		init();
		
	}
	
	public void init(){
		app = (BaseApplication) getApplication();
		mManager = new AddOptionManager();
		mManager.setListener(new loadListener() {
			
			@Override
			public void AddOptionSuccess() {
				// TODO Auto-generated method stub
				if(dialog!=null&&dialog.isShowing())
					dialog.dismiss();
				Toast.makeText(AddOption.this, getString(R.string.addoption_success),Toast.LENGTH_SHORT).show();
				finish();
			}
			
			@Override
			public void SessionVailed() {
				// TODO Auto-generated method stub
				if(dialog != null && dialog.isShowing())
					dialog.dismiss();
				app.user.sessionID = "";
				MassVigUtil.setPreferenceStringData(AddOption.this, "SESSIONID", "");
				startActivity(new Intent(AddOption.this, LoginActivity.class));
			}

			@Override
			public void AddOptionFailed() {
				// TODO Auto-generated method stub
				if(dialog!=null&&dialog.isShowing())
					dialog.dismiss();
				Toast.makeText(AddOption.this, getString(R.string.addoption_failed), Toast.LENGTH_SHORT).show();
				
			}	
		});
		dialog=new ProgressDialog(this);
		go_finish=(Button)findViewById(R.id.go_finish);
		go_back=(Button)findViewById(R.id.go_back);
		option=(EditText)findViewById(R.id.write);
		go_back.setOnClickListener(this);
		go_finish.setOnClickListener(this);
        option.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				if(actionId==EditorInfo.IME_ACTION_NEXT||actionId==EditorInfo.IME_ACTION_UNSPECIFIED){
					if(!TextUtils.isEmpty(option.getText().toString())){
					if(!isclick){
						isclick=true;
					app.user.sessionID=TextUtils.isEmpty(app.user.sessionID)?MassVigUtil.getPreferenceData(AddOption.this, "SESSIONID", ""):app.user.sessionID;
					if(TextUtils.isEmpty(app.user.sessionID)){
					app.user.sessionID="";
					MassVigUtil.setPreferenceStringData(AddOption.this, "SESSIONID", "");
					startActivity(new Intent(AddOption.this,LoginActivity.class));
					return false;
					}
					dialog.setMessage(getString(R.string.option));
					dialog.show();
					mManager.Addoption(app.user.sessionID, option.getText().toString());
					}	
					}else{
						if(dialog!=null&&dialog.isShowing())
							dialog.dismiss();
						Toast.makeText(AddOption.this, getString(R.string.addoption_failed), Toast.LENGTH_SHORT).show();
					}
				}	
				return false;
			}
		});
		
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.go_finish:
			if(!TextUtils.isEmpty(option.getText().toString())){
			dialog.setMessage(getString(R.string.option));
			dialog.show();
			mManager.Addoption(app.user.sessionID, option.getText().toString());
			}
			else{
				if(dialog!=null&&dialog.isShowing())
					dialog.dismiss();
				Toast.makeText(this, getString(R.string.addoption_failed), Toast.LENGTH_SHORT).show();		
			}
			break;
		case R.id.go_back:
			finish();
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(dialog!=null&&dialog.isShowing())
			dialog.dismiss();	
	}
	}


