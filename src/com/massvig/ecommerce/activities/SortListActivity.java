package com.massvig.ecommerce.activities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigExit;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.widgets.MassVigData;
import com.massvig.ecommerce.widgets.NetImageView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SortListActivity extends BaseActivity {

	private ListView listView;
	private JSONArray cateGoryData;
	private MassVigExit exit = new MassVigExit();
	private LayoutInflater mInflater;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sort);
		setTitle(getString(R.string.sortlist));
		mInflater = LayoutInflater.from(this);
		
	}

	private void pressAgainExit(){
		if(exit.isExit()){
			finish();
		}else{
			Toast.makeText(this, getString(R.string.exit_message), Toast.LENGTH_SHORT).show();
			exit.doExitInOneSecond();
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			pressAgainExit();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initView();
	}

	private void initView() {
		cateGoryData = getCategory();
		listView = (ListView)findViewById(R.id.listview);
		if(cateGoryData != null){
			MyAdapter adapter = new MyAdapter();
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					if(cateGoryData.length() >= position){
						try {
							String children = cateGoryData.getJSONObject(position).optString("Children");
							startActivity(new Intent(SortListActivity.this, SubSortListActivity.class).putExtra("CHILDREN", children));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}});
		}
	}

	private JSONArray getCategory() {
		JSONArray cate1 = new JSONArray();
		JSONArray cate2 = new JSONArray();
		
		JSONArray allcategory = MassVigData.getinstance(this).getAllCategory();
		JSONObject item;
		for(int i = 0,len = allcategory.length();i < len ;i++){
			try{
				item = allcategory.getJSONObject(i);
				if(item.getJSONArray("Children").length() > 0){
//				if(item.getBoolean("IsParent")){
					cate1.put(item);
				}else{
					cate2.put(item);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		try {
			if(cate2.length() > 0)
				return new JSONArray((cate1.toString() + cate2.toString()).replace("][",","));
			else
				return cate1;
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return null;
	}

	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return cateGoryData.length();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			try {
				return cateGoryData.get(position);
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
				convertView = mInflater.inflate(R.layout.sort_item, null);
				holder.image = (NetImageView)convertView.findViewById(R.id.image);
				holder.name = (TextView)convertView.findViewById(R.id.name);
				holder.detail = (TextView)convertView.findViewById(R.id.detail);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			try {
				JSONObject o = cateGoryData.getJSONObject(position);
				holder.name.setText(o.optString("Name"));
//				holder.image.setImageUrl(MassVigUtil.GetImageUrl(o.optString("CategoryIcon"), 60, 60), MassVigContants.PATH, null);
				holder.image.setImageUrl(o.optString("CategoryIcon"), MassVigContants.PATH, null);
				String detail = "";
				JSONArray array = o.getJSONArray("Children");
				for (int i = 0; i < ((array.length() >= 4) ? 4 : array.length()); i++) {
					JSONObject obj = array.getJSONObject(i);
					detail += obj.optString("Name") + " ";
				}
				holder.detail.setText(detail);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return convertView;
		}
		
		class ViewHolder{
			NetImageView image;
			TextView detail;
			TextView name;
		}
		
	}
	
}
