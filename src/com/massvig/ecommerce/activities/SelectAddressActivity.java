package com.massvig.ecommerce.activities;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.Address;
import com.massvig.ecommerce.entities.AddressList;
import com.massvig.ecommerce.managers.AddressManager;
import com.massvig.ecommerce.managers.AddressManager.LoadListener;
import com.massvig.ecommerce.utilities.MassVigUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class SelectAddressActivity extends BaseActivity implements LoadListener, OnClickListener{

	private AddressManager mManager;
	private ListView listView;
	private LayoutInflater mInflater;
	private AddressAdapter mAdapter;
	private BaseApplication app;
	private static final int LOGIN = 2;
	public static final int ORDER = 3;
	public static final int NORMAL = 4;
	private int flag = -1;
//	private int productSpecID = -1;
//	private int quantity = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_address);
		setTitle(getString(R.string.selectaddress));
		app = (BaseApplication) getApplication();
		mManager = new AddressManager();
		mManager.setListener(this);
		flag = this.getIntent().getIntExtra("FLAG", -1);
//		productSpecID = this.getIntent().getIntExtra("productSpecID", -1);
//		quantity = this.getIntent().getIntExtra("quantity", -1);
//		mManager.setAddressList((AddressList)this.getIntent().getSerializableExtra("addressList"));
		initView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(this, "SESSIONID", "") : app.user.sessionID;
		if(!TextUtils.isEmpty(app.user.sessionID)){
			mManager.setAddressList(new AddressList());
//			if(flag == ORDER)
//				mManager.FetchAddressListWithFreight(app.user.sessionID, productSpecID, quantity);
//			else
				mManager.FetchAddress(app.user.sessionID);
		}else{
			app.user.sessionID = "";
			MassVigUtil.setPreferenceStringData(this, "SESSIONID", "");
			startActivityForResult(new Intent(this, LoginActivity.class), LOGIN);
		}
	}

	private void initView() {
		((Button) findViewById(R.id.back)).setOnClickListener(this);
		((Button) findViewById(R.id.finish)).setOnClickListener(this);
		mInflater = LayoutInflater.from(this);
		listView = (ListView)findViewById(R.id.listview);
		mAdapter = new AddressAdapter();
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				for (int i = 0; i < mManager.getAddressList().getCount(); i++) {
					mManager.getAddressList().getAddress(i).isDefault = false;
				}
				mManager.getAddressList().getAddress(position).isDefault = true;
				mAdapter.notifyDataSetChanged();
				new setDefault(mManager.getAddressList().getAddress(position)).execute();
				Intent intent = new Intent();
				intent.putExtra("addressList", mManager.getAddressList());
				setResult(RESULT_OK, intent);
				SelectAddressActivity.this.finish();
			}});
	}

	public class setDefault extends AsyncTask<Integer, Void, Integer>{

		private Address a;
		public setDefault(Address address){
			a = address;
		}
		
		@Override
		protected Integer doInBackground(Integer... params) {
			return a.SetDefaultAddress(app.user.sessionID);
		}
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			SelectAddressActivity.this.finish();
			break;
		case R.id.finish:
			startActivity(new Intent(this, ManageAddressActivity.class).putExtra("addressList", mManager.getAddressList()));
			break;

		default:
			break;
		}
	}
	
	public class AddressAdapter extends BaseAdapter{

		class ViewHolder {
			TextView userName;
			TextView detailText;
			TextView selected;
		}

		@Override
		public int getCount() {
			return mManager.getAddressList().getCount();
		}

		@Override
		public Object getItem(int position) {
			return mManager.getAddressList().getAddress(position);
		}

		@Override
		public long getItemId(int position) {
			return Long.valueOf(mManager.getAddressList().getAddressID(position));
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.select_address_item, null);
				holder.userName = (TextView) convertView.findViewById(R.id.name);
				holder.detailText = (TextView) convertView.findViewById(R.id.address);
				holder.selected = (TextView) convertView.findViewById(R.id.selected);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Address address = (Address) getItem(position);
			holder.userName.setText(address.name);
			holder.detailText.setText(address.shengshiqu.replace(",", " ") + address.address);
			//TODO
			holder.selected.setVisibility(address.isDefault ? View.VISIBLE : View.INVISIBLE);
			return convertView;
		}
		
	}

	@Override
	public void Success(int index) {
		switch (index) {
		case AddressManager.LOAD:
			mAdapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
	}

	@Override
	public void Failed(int index) {
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			if(requestCode == LOGIN){
//				if(flag == ORDER){
//					app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(this, "SESSIONID", "") : app.user.sessionID;
//					mManager.FetchAddressListWithFreight(app.user.sessionID, productSpecID, quantity);
//				}else{
					app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(this, "SESSIONID", "") : app.user.sessionID;
					mManager.FetchAddress(app.user.sessionID);
//				}
			}
		}
	}

	@Override
	public void SessionidFail() {
//		if(!TextUtils.isEmpty(app.user.sessionID + MassVigUtil.getPreferenceData(this, "SESSIONID", "")))
			startActivity(new Intent(this, LoginActivity.class));
		app.user.sessionID = "";
		MassVigUtil.setPreferenceStringData(this, "SESSIONID", "");
	}

}
