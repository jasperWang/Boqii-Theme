package com.massvig.ecommerce.activities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.massvig.ecommerce.boqi.R;
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

public class SubSortListActivity extends Activity implements OnClickListener{

	private ListView listView;
	private LayoutInflater mInflater;
	private JSONArray categoryData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subsort);
		mInflater = LayoutInflater.from(this);
		initView();
	}

	private void initView() {
		if(!TextUtils.isEmpty(this.getIntent().getStringExtra("CHILDREN"))){
			try {
				categoryData = new JSONArray(this.getIntent().getStringExtra("CHILDREN"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		((Button)findViewById(R.id.back)).setOnClickListener(this);
		listView = (ListView)findViewById(R.id.listview);
		if(categoryData != null){
			MyAdapter adapter = new MyAdapter();
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					if(categoryData.length() >= position){
						try {
							int categoryid = categoryData.getJSONObject(position).optInt("CategoryID");
							startActivity(new Intent(SubSortListActivity.this, GoodsListActivity.class).putExtra("MCIDS", categoryid + ""));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}});
		}
	}

	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return categoryData.length();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			try {
				return categoryData.get(position);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.subsort_item, null);
				holder.name = (TextView)convertView.findViewById(R.id.name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			try {
				JSONObject o = categoryData.getJSONObject(position);
				holder.name.setText(o.optString("Name"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return convertView;
		}
		
		class ViewHolder{
			TextView name;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;

		default:
			break;
		}
	}
	
}
