package com.massvig.ecommerce.activities;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.massvig.ecommerce.entities.Address;
import com.massvig.ecommerce.entities.City;
import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.managers.AddressManager;
import com.massvig.ecommerce.managers.OrderManager;
import com.massvig.ecommerce.managers.AddressManager.LoadListener;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.widgets.MassVigData;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddressDetailActivity extends BaseActivity implements OnClickListener,LoadListener {

	private int customerAddressID = -1;
	private boolean isEditing = false;
	private EditText customerName, mobile, address, zipcode, email;
	private TextView region_1, region_2,region_3,finish, delete;
	private AddressManager mAddressManager;
	private BaseApplication app;
	private Builder dialog;
	private int firstID = -1,secondID = -1;
	private JSONArray array;
	private final static int SUCCESS = 1;
	private final static int FAIL = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.address_detail);
		setTitle(getString(R.string.addressdetail));
		mAddressManager = new AddressManager();
		mAddressManager.setListener(this);
		app = (BaseApplication) getApplication();
		array = getCityList();
		initView();
	}

	private void initView() {
		dialog = new AlertDialog.Builder(this);
		String text = this.getIntent().getStringExtra("address");
		((Button) findViewById(R.id.back)).setOnClickListener(this);
		finish = ((TextView) findViewById(R.id.finish));
		finish.setOnClickListener(this);
		delete = (TextView) findViewById(R.id.delete);
		delete.setOnClickListener(this);
		customerName = (EditText)findViewById(R.id.customerName);
		mobile = (EditText)findViewById(R.id.mobile);
		address = (EditText)findViewById(R.id.address);
		zipcode = (EditText)findViewById(R.id.zipcode);
		email = (EditText)findViewById(R.id.email);
		region_1 = (TextView)findViewById(R.id.region_1);
		region_2 = (TextView)findViewById(R.id.region_2);
		region_3 = (TextView)findViewById(R.id.region_3);
		region_1.setOnClickListener(this);
		region_2.setOnClickListener(this);
		region_3.setOnClickListener(this);
		if(!TextUtils.isEmpty(text)){
			Address addr = mAddressManager.getAddress();
			addr.StringToAddress(text);
			customerAddressID = addr.customerAddressID;
			customerName.setText(addr.name);
			mobile.setText(addr.mobile);
			address.setText(addr.address);
			zipcode.setText(addr.zipcode);
			email.setText(addr.email);
			mAddressManager.setAddress(addr);
			int regionID = addr.regionID;
			HashMap<Integer, City> map = getCityArray();
			int f,s;
			if (regionID != -1 && !map.isEmpty()) {
				region_3.setText(map.get(regionID).RegionName);
				regionID = map.get(regionID).ParentID;
				s = regionID;
				region_2.setText(map.get(regionID).RegionName);
				regionID = map.get(regionID).ParentID;
				f = regionID;
				region_1.setText(map.get(regionID).RegionName);
				for (int i = 0; i < array.length(); i++) {
					JSONObject o;
					try {
						o = array.getJSONObject(i);
						if(o.getInt("RegionID") == f){
							firstID = i;
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				JSONObject object;
				try {
					object = array.getJSONObject(firstID);
					JSONArray arr = object.getJSONArray("Children");
					for (int i = 0; i < arr.length(); i++) {
						JSONObject o = arr.getJSONObject(i);
						if(o.getInt("RegionID") == s){
							secondID = i;
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			setEnable(false);
		}else{
			customerAddressID = -1;
			setEnable(true);
		}
		if(customerAddressID == -1){
			delete.setVisibility(View.GONE);
		}else{
			delete.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 设置是否可编辑
	 * @param b
	 */
	private void setEnable(boolean b){
		if(!b){
			isEditing = false;
			finish.setText(getString(R.string.modify_text));
			customerName.setEnabled(false);
			mobile.setEnabled(false);
			address.setEnabled(false);
			zipcode.setEnabled(false);
			email.setEnabled(false);
			region_1.setEnabled(false);
			region_2.setEnabled(false);
			region_3.setEnabled(false);
		}else{
			isEditing = true;
			finish.setText(getString(R.string.finish));
			customerName.setEnabled(true);
			mobile.setEnabled(true);
			address.setEnabled(true);
			zipcode.setEnabled(true);
			email.setEnabled(true);
			region_1.setEnabled(true);
			region_2.setEnabled(true);
			region_3.setEnabled(true);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			AddressDetailActivity.this.finish();
			break;
		case R.id.finish:
			if(isEditing){
				//TODO finish region
				Address address = new Address();
				address.customerAddressID = customerAddressID;
				address.regionID = mAddressManager.getAddress().regionID;
				address.name = customerName.getText().toString();
				address.mobile = mobile.getText().toString();
				address.address = this.address.getText().toString();
				address.zipcode = zipcode.getText().toString();
				address.email = email.getText().toString();
				mAddressManager.setAddress(address);
				if (!TextUtils.isEmpty(app.user.sessionID)) {
					if (customerAddressID == -1) {
						mAddressManager.addAddress(app.user.sessionID);
					} else {
						mAddressManager.modifyAddress(app.user.sessionID);
					}
				}else{
					Intent intent = new Intent();
					intent.putExtra("address", mAddressManager.getAddress().toString());
					setResult(RESULT_OK, intent);
					OrderManager manager = new OrderManager(this);
					manager.SaveLocalAddress(mAddressManager.getAddress());
					AddressDetailActivity.this.finish();
				}
			}else{
				setEnable(true);
			}
			break;
		case R.id.delete:
			mAddressManager.deleteAddress(app.user.sessionID);
			break;
		case R.id.region_1:
			final String[] items = new String[array.length()];
			for (int i = 0; i < items.length; i++) {
				JSONObject o;
				try {
					o = array.getJSONObject(i);
					items[i] = o.getString("RegionName");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			dialog.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					firstID = which;
					secondID = -1;
					region_1.setText(items[which]);
					region_2.setText(getString(R.string.shi));
					region_3.setText(getString(R.string.qu));
					dialog.dismiss();
				}}).show();
			break;
		case R.id.region_2:
			if(firstID == -1)
				return;
			JSONObject object;
			try {
				object = array.getJSONObject(firstID);
				JSONArray arr = object.getJSONArray("Children");
				final String[] cities = new String[arr.length()];
				for (int i = 0; i < cities.length; i++) {
					JSONObject o;
					try {
						o = arr.getJSONObject(i);
						cities[i] = o.getString("RegionName");
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				dialog.setSingleChoiceItems(cities, -1, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						secondID = which;
						region_2.setText(cities[which]);
						region_3.setText(getString(R.string.qu));
						dialog.dismiss();
					}}).show();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			break;
		case R.id.region_3:
			if(secondID == -1)
				return;
			JSONObject obj;
			try {
				obj = array.getJSONObject(firstID).getJSONArray("Children").getJSONObject(secondID);
				JSONArray a = obj.getJSONArray("Children");
				final String[] cities = new String[a.length()];
				final int[] cityIDs = new int[a.length()];
				for (int i = 0; i < cities.length; i++) {
					JSONObject o;
					try {
						o = a.getJSONObject(i);
						cities[i] = o.getString("RegionName");
						cityIDs[i] = o.getInt("RegionID");
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				dialog.setSingleChoiceItems(cities, -1, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						region_3.setText(cities[which]);
						int regionID = cityIDs[which];
						mAddressManager.getAddress().shengshiqu = region_1.getText().toString() + " " + region_2.getText().toString() + " " + region_3.getText().toString() + " "; 
						mAddressManager.getAddress().regionID = regionID;
						dialog.dismiss();
					}}).show();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}

	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case SUCCESS:
				switch (msg.arg1) {
				case AddressManager.ADD:
					Intent intent = new Intent();
					intent.putExtra("address", mAddressManager.getAddress().toString());
					setResult(RESULT_OK, intent);
					AddressDetailActivity.this.finish();
					break;
				case AddressManager.DELETE:
					Intent intent2 = new Intent();
					intent2.putExtra("address", "");
					setResult(RESULT_OK, intent2);
					AddressDetailActivity.this.finish();
					break;
				case AddressManager.MODIFY:
					Intent intent1 = new Intent();
					intent1.putExtra("address", mAddressManager.getAddress().toString());
					setResult(RESULT_OK, intent1);
					AddressDetailActivity.this.finish();
					break;

				default:
					break;
				}
				break;
			case FAIL:
				switch (msg.arg1) {
				case AddressManager.ADD:
					Toast.makeText(AddressDetailActivity.this, getString(R.string.add_fail), Toast.LENGTH_SHORT).show();
					break;
				case AddressManager.DELETE:
					Toast.makeText(AddressDetailActivity.this, getString(R.string.delete_fail), Toast.LENGTH_SHORT).show();
					break;
				case AddressManager.MODIFY:
					Toast.makeText(AddressDetailActivity.this, getString(R.string.modify_fail), Toast.LENGTH_SHORT).show();
					break;

				default:
					break;
				}
				break;

			default:
				break;
			}
		}};

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
		msg.what = FAIL;
		msg.arg1 = index;
		mHandler.sendMessage(msg);
	}
	
	private HashMap<Integer, City> getCityArray(){
		HashMap<Integer, City> map = new HashMap<Integer, City>(); 
//		ArrayList<City> allCities = new ArrayList<City>();
		for (int i = 0; i < array.length(); i++) {
			try {
				JSONObject item = array.getJSONObject(i);
				City c = new City();
				c.Depth = item.getInt("Depth");
				c.ParentID = item.getInt("ParentID");
				c.RegionName = item.getString("RegionName");
				c.ResionID = item.getInt("RegionID");
//				c.ZipCode = item.getString("ZipCode");
				map.put(c.ResionID, c);
				JSONArray arr = item.getJSONArray("Children");
				for (int j = 0; j < arr.length(); j++) {
					JSONObject it = arr.getJSONObject(j);
					City c1 = new City();
					c1.Depth = it.getInt("Depth");
					c1.ParentID = it.getInt("ParentID");
					c1.RegionName = it.getString("RegionName");
					c1.ResionID = it.getInt("RegionID");
//					c1.ZipCode = it.getString("ZipCode");
					map.put(c1.ResionID, c1);
					JSONArray a = it.getJSONArray("Children");
					for (int k = 0; k < a.length(); k++) {
						JSONObject ite = a.getJSONObject(k);
						City c2 = new City();
						c2.Depth = ite.getInt("Depth");
						c2.ParentID = ite.getInt("ParentID");
						c2.RegionName = ite.getString("RegionName");
						c2.ResionID = ite.getInt("RegionID");
//						c2.ZipCode = ite.getString("ZipCode");
						map.put(c2.ResionID, c2);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return map;
		
	}

	private JSONArray getCityList() {
		JSONArray allcity = MassVigData.getinstance(this).getCityList();
		return allcity;
	}

	@Override
	public void SessionidFail() {
		Toast.makeText(this, getString(R.string.account_failed), Toast.LENGTH_SHORT).show();
//		if(!TextUtils.isEmpty(app.user.sessionID + MassVigUtil.getPreferenceData(this, "SESSIONID", "")))
			startActivity(new Intent(this, LoginActivity.class));
		app.user.sessionID = "";
		MassVigUtil.setPreferenceStringData(this, "SESSIONID", "");
		
	}
}
