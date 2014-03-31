package com.massvig.ecommerce.activities;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.Address;
import com.massvig.ecommerce.entities.AddressList;
import com.massvig.ecommerce.managers.AddressManager;
import com.massvig.ecommerce.managers.AddressManager.LoadListener;
import com.massvig.ecommerce.utilities.MassVigUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ManageAddressActivity extends BaseActivity implements LoadListener,
		OnClickListener {

	private AddressManager mManager;
	private ListView listView;
	private LayoutInflater mInflater;
	private AddressAdapter mAdapter;
	private BaseApplication app;
	private static final int ADD = 1;
	private static final int LOGIN = 2;
	private static final int MODIFY = 3;
	private int tempID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manage_address);
		setTitle(getString(R.string.manageraddress));
		app = (BaseApplication) getApplication();
		mManager = new AddressManager();
		mManager.setListener(this);
		mManager.setAddressList((AddressList) this.getIntent()
				.getSerializableExtra("addressList"));
		initView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(this, "SESSIONID", "") : app.user.sessionID;
		if (!TextUtils.isEmpty(app.user.sessionID)) {
			mAdapter.notifyDataSetChanged();
		} else {
			app.user.sessionID = "";
			MassVigUtil.setPreferenceStringData(this, "SESSIONID", "");
			startActivityForResult(new Intent(this, LoginActivity.class), LOGIN);
		}
	}

	private void initView() {
		((Button) findViewById(R.id.back)).setOnClickListener(this);
		((Button) findViewById(R.id.finish)).setOnClickListener(this);
		mInflater = LayoutInflater.from(this);
		listView = (ListView) findViewById(R.id.listview);
		mAdapter = new AddressAdapter();
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Address address = mManager.getAddressList().getAddress(position);
				tempID = address.customerAddressID;
				startActivityForResult(new Intent(ManageAddressActivity.this, AddressDetailActivity.class).putExtra("address", address.toString()), MODIFY);
			}});
	}

	public class AddressAdapter extends BaseAdapter {

		class ViewHolder {
			TextView userName;
			TextView detailText;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mManager.getAddressList().getCount();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mManager.getAddressList().getAddress(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return Long.valueOf(mManager.getAddressList()
					.getAddressID(position));
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.address_item, null);
				holder.userName = (TextView) convertView
						.findViewById(R.id.name);
				holder.detailText = (TextView) convertView
						.findViewById(R.id.address);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Address address = (Address) getItem(position);
			holder.userName.setText(address.name);
			holder.detailText.setText(address.shengshiqu.replace(",", " ") + address.address);
			return convertView;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			ManageAddressActivity.this.finish();
			break;
		case R.id.finish:
			startActivityForResult(new Intent(this, AddressDetailActivity.class), ADD);
			break;

		default:
			break;
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
		// TODO Auto-generated method stub

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == ADD) {
				String text = data.getStringExtra("address");
				Address addr = new Address();
				addr.StringToAddress(text);
				mManager.getAddressList().addAddress(addr);
				mAdapter.notifyDataSetChanged();
			} else if(requestCode == MODIFY) {
				String text = data.getStringExtra("address");
				if(TextUtils.isEmpty(text)){
					for (int i = 0; i < mManager.getAddressList().getCount(); i++) {
						if(tempID == Integer.valueOf(mManager.getAddressList().getAddressID(i))){
							mManager.getAddressList().deleteAddress(i);
						}
					}
				}else{
					Address addr = new Address();
					addr.StringToAddress(text);
					for (int i = 0; i < mManager.getAddressList().getCount(); i++) {
						if(addr.customerAddressID == Integer.valueOf(mManager.getAddressList().getAddressID(i))){
							mManager.getAddressList().getAddress(i).address = addr.address;
							mManager.getAddressList().getAddress(i).customerAddressID = addr.customerAddressID;
							mManager.getAddressList().getAddress(i).email = addr.email;
							mManager.getAddressList().getAddress(i).isDefault = addr.isDefault;
							mManager.getAddressList().getAddress(i).mobile = addr.mobile;
							mManager.getAddressList().getAddress(i).name = addr.name;
							mManager.getAddressList().getAddress(i).regionID = addr.regionID;
							mManager.getAddressList().getAddress(i).shengshiqu = addr.shengshiqu;
							mManager.getAddressList().getAddress(i).zipcode = addr.zipcode;
						}
					}
				}
				mAdapter.notifyDataSetChanged();
			} else if (requestCode == LOGIN) {
				mManager.FetchAddress(app.user.sessionID);
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
