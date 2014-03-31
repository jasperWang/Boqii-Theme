package com.massvig.ecommerce.activities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.widgets.MassVigData;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class SearchActivity extends Activity implements OnClickListener{

	private ListView listview;
	private ArrayList<String> hotKeys = new ArrayList<String>();
	private boolean isLocal = true;
	private EditText keyEdit;
	private LayoutInflater mInflater;
	private SearchAdapter adapter;
	private ArrayList<String> history = new ArrayList<String>();
	private boolean click = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		JSONArray array = MassVigData.getinstance(this).getHotkeyList();
		if(array != null && array.length() > 0){
			for (int i = 0; i < array.length(); i++) {
				try {
					hotKeys.add((String) array.get(i).toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		history = getHistory();
		if(history == null || history.size() == 0)
			isLocal = false;
		initView();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		click = false;
		super.onResume();
	}

	private void saveHistory() {
		String text = keyEdit.getText().toString();
		if (history != null && history.size() > 0) {
			if (history.contains(text)) {
				return;
			} else {
				history.add(0, text);
				adapter.notifyDataSetChanged();
				editCommit(text);
			}
		} else {
			history.add(0, text);
			adapter.notifyDataSetChanged();
			editCommit(text);
		}
	}

	private void editCommit(String text) {
		SharedPreferences sp = getSharedPreferences("history_strs", 0);
		String save_Str = sp.getString("history", "");
		StringBuilder sb = new StringBuilder(save_Str);
		sb.append(text + ",");
		sp.edit().putString("history", sb.toString()).commit();
	}

	private ArrayList<String> getHistory() {
		ArrayList<String> list = new ArrayList<String>();
		SharedPreferences sp = getSharedPreferences("history_strs",
				MODE_PRIVATE);
		String save_history = sp.getString("history", null);
		if (!TextUtils.isEmpty(save_history)) {
			String[] hisArrays = save_history.split(",");
			if (hisArrays != null && hisArrays.length > 0) {
				for (int i = hisArrays.length; i > 0; i--) {
					list.add(hisArrays[i - 1]);
				}
			}
		}
		return list;
	}

	public void collapseSoftInputMethod(EditText inputText){
		InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
		imm.hideSoftInputFromWindow(inputText.getWindowToken(), 0); 
	}
	
	private void initView() {
		((Button)findViewById(R.id.search_btn)).setOnClickListener(this);
		mInflater = LayoutInflater.from(this);
		keyEdit = (EditText)findViewById(R.id.search_edit);
		keyEdit.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_ACTION_SEARCH
						|| actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
					if (!TextUtils.isEmpty(keyEdit.getText().toString())) {
						if (!click) {
//							Intent it = new Intent("com.massvig.close");
//							sendBroadcast(it);
							click = true;
							collapseSoftInputMethod(keyEdit);
							startActivity(new Intent(SearchActivity.this,GoodsListActivity.class).putExtra("KEYWORD", keyEdit.getText().toString()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//							MassVigUtil.setPreferenceStringData(SearchActivity.this, "KEYWORD", keyEdit.getText().toString());
						}
						return false;
					} else {
						Toast.makeText(SearchActivity.this,
								getString(R.string.no_key), Toast.LENGTH_SHORT)
								.show();
					}
				}
				return false;
			}
		});
		listview = (ListView)findViewById(R.id.listview);
		adapter = new SearchAdapter();
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String s = isLocal ? history.get(position) : hotKeys.get(position);
				startActivity(new Intent(SearchActivity.this, GoodsListActivity.class).putExtra("KEYWORD", s).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			}
		});
		((Button)findViewById(R.id.cancel)).setOnClickListener(this);
		keyEdit.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
//				if(count > 0){
//					isLocal = false;
//					adapter.notifyDataSetChanged();
//				}else{
//					isLocal = true;
//					adapter.notifyDataSetChanged();
//				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				String ss=keyEdit.getText().toString();
					if(ss.length()>0){	
					isLocal = false;
				adapter.notifyDataSetChanged();
			}else{
				isLocal = true;
				adapter.notifyDataSetChanged();
			}				
			}		
			
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cancel:
			finish();
			break;
		case R.id.search_btn:
			if(!TextUtils.isEmpty(keyEdit.getText().toString())){
				saveHistory();
				startActivity(new Intent(SearchActivity.this, GoodsListActivity.class).putExtra("KEYWORD", keyEdit.getText().toString()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//				MassVigUtil.setPreferenceStringData(SearchActivity.this, "KEYWORD", keyEdit.getText().toString());
			}else{
				Toast.makeText(SearchActivity.this, getString(R.string.no_key), Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
		
	}
	
	class SearchAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return isLocal && history.size() > 0 ? history.size() : hotKeys.size() ;
		}

		@Override
		public Object getItem(int position) {
			return isLocal && history.size() > 0 ? history.get(position) : hotKeys.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			convertView = holder.getView(position);
			return convertView;
		}
		
		class ViewHolder{
			private TextView key;
			private View itemView;
			
			public ViewHolder(){
				itemView = mInflater.inflate(R.layout.search_item, null);
				itemView.setTag(this);
				key = (TextView)itemView.findViewById(R.id.key);
			}
			
			public View getView(int position){
				key.setText((CharSequence) getItem(position));
				return itemView;
			}
		}
	}

}
