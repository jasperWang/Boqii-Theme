package com.massvig.ecommerce.activities;

import com.massvig.ecommerce.boqi.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BillActivity extends Activity implements OnClickListener{

	private TextView person,compa;
	private EditText company;
	private String billText = "";
	private TextView bill1,bill2,bill3,bill4,bill5,bill6;
	private int billType = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bill);
		billText = this.getIntent().getStringExtra("BILL");
		if(!TextUtils.isEmpty(billText) && !billText.equals("null"))
			billText = getString(R.string.person);
		initView();
		clear();
		initBill();
	}
	
	private void initBill() {
		switch (billType) {
		case 1:
			bill1.setBackgroundResource(R.drawable.bg_selected);
			break;
		case 2:
			bill2.setBackgroundResource(R.drawable.bg_selected);
			break;
		case 3:
			bill3.setBackgroundResource(R.drawable.bg_selected);
			break;
		case 4:
			bill4.setBackgroundResource(R.drawable.bg_selected);
			break;
		case 5:
			bill5.setBackgroundResource(R.drawable.bg_selected);
			break;
		case 6:
			bill6.setBackgroundResource(R.drawable.bg_selected);
			break;

		default:
			break;
		}
	}

	private void clear(){
		bill1.setBackgroundResource(R.drawable.bg_unselected);
		bill2.setBackgroundResource(R.drawable.bg_unselected);
		bill3.setBackgroundResource(R.drawable.bg_unselected);
		bill4.setBackgroundResource(R.drawable.bg_unselected);
		bill5.setBackgroundResource(R.drawable.bg_unselected);
		bill6.setBackgroundResource(R.drawable.bg_unselected);
	}

	private void initView() {
		bill1 = (TextView)findViewById(R.id.bill1);
		bill2 = (TextView)findViewById(R.id.bill2);
		bill3 = (TextView)findViewById(R.id.bill3);
		bill4 = (TextView)findViewById(R.id.bill4);
		bill5 = (TextView)findViewById(R.id.bill5);
		bill6 = (TextView)findViewById(R.id.bill6);
		bill1.setOnClickListener(this);
		bill2.setOnClickListener(this);
		bill3.setOnClickListener(this);
		bill4.setOnClickListener(this);
		bill5.setOnClickListener(this);
		bill6.setOnClickListener(this);
		person = (TextView)findViewById(R.id.person);
		compa = (TextView)findViewById(R.id.compa);
		company = (EditText)findViewById(R.id.company);
		((LinearLayout)findViewById(R.id.person_layout)).setOnClickListener(this);
		((Button)findViewById(R.id.back)).setOnClickListener(this);
		((Button)findViewById(R.id.finish)).setOnClickListener(this);
		company.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(!TextUtils.isEmpty(s.toString())){
					person.setVisibility(View.INVISIBLE);
					compa.setVisibility(View.VISIBLE);
				}else{
					compa.setVisibility(View.INVISIBLE);
					person.setVisibility(View.VISIBLE);
				}
				billText = s.toString();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			BillActivity.this.finish();
			return;
		case R.id.finish:
			Intent intent = new Intent();
			intent.putExtra("BILL", TextUtils.isEmpty(billText) ? getString(R.string.person) : billText);
			intent.putExtra("BILLTYPE", billType + "");
			setResult(RESULT_OK, intent);
			BillActivity.this.finish();
			return;
		case R.id.person_layout:
			person.setVisibility(View.VISIBLE);
			company.setText("");
			billText = getString(R.string.person);
			return;
		case R.id.bill1:
			billType = 1;
			break;
		case R.id.bill2:
			billType = 2;
			break;
		case R.id.bill3:
			billType = 3;
			break;
		case R.id.bill4:
			billType = 4;
			break;
		case R.id.bill5:
			billType = 5;
			break;
		case R.id.bill6:
			billType = 6;
			break;
		default:
			break;
		}
		clear();
		initBill();
	}

}
