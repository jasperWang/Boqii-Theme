package com.massvig.ecommerce.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.Friend;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.widgets.CommunityMode;
import com.massvig.ecommerce.widgets.FilterButton;
import com.massvig.ecommerce.widgets.LoadView;
import com.massvig.ecommerce.widgets.LoadView.Onload;
import com.massvig.ecommerce.widgets.NetImageView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class FansListActivity extends BaseActivity {
	private int type,userId,count = 10;
	private MyOnclick mMyOnclick;
	private UserInfoAdapter userInfoAdapter;
	private ListView userListView;
	private EditText editText;
	private ImageButton subBtn;
//	private ProgressDialog dialog;
	private LoadView loadView;
//	private int requestCode = 10000;
	private int loginUserId = 0;
	private int operateAttentionCount = 0;
	private LinearLayout searchLayout;
	private BaseApplication app;
//	private static final int SELF = 1;
	private static final int UNFOLLOW = 2;
	private static final int FOLLOWING = 3;
	private String keyWord = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.community_fans_list);
		setTitle(getString(R.string.fanslist));
		app = (BaseApplication) getApplication();
		
		type = getIntent().getIntExtra("_type", 0);
		loginUserId = app.user.customerID;
		userId = getIntent().getIntExtra("_userId", loginUserId);
		
		mMyOnclick = new MyOnclick();
		searchLayout = (LinearLayout)findViewById(R.id.searchLayout);
		userListView = (ListView)findViewById(R.id.fans_user_list);
		editText = (EditText)findViewById(R.id.search_fans_edit);
		subBtn = (ImageButton)findViewById(R.id.search_fans_sub_btn);
		subBtn.setOnClickListener(mMyOnclick);
		
		userInfoAdapter = new UserInfoAdapter();
		
		loadView = (LoadView)getLayoutInflater().inflate(R.layout.community_load_view, null);
		loadView.setBackgroundColor(Color.parseColor("#00000000"));
		loadView.setOnloadCallBack(new Onload() {
			
			@Override
			public void callBack() {
				// TODO Auto-generated method stub
				LoadData();
			}
		});
		userListView.addFooterView(loadView, null, false);
		userListView.setAdapter(userInfoAdapter);
		loadView.showLoading();
		
		if(type == 2){
			searchLayout.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(FansListActivity.this, "SESSIONID", "") : app.user.sessionID;
		LoadData();
	}

	public void LoadData(){
		
		new AsyncTask<Object, Object, Object>() {

			@Override
			protected Map<String, Object> doInBackground(Object... params) {
				// TODO Auto-generated method stub
				Map<String, Object> map = null;
				if(type == 0){
					map = CommunityMode.getInstance().GetFollows(app.user.sessionID, userId, userInfoAdapter.getCount(), 10);
				}else if(type == 1){
					map = CommunityMode.getInstance().GetFans(app.user.sessionID, userId, userInfoAdapter.getCount(), 10);
				}else if(type == 2){
					map = CommunityMode.getInstance().getSearchFriends(app.user.sessionID, keyWord, userInfoAdapter.getCount(), 10);
				}
				
				return map;
				
			}
			
			@Override
			protected void onPostExecute(Object result) {
				// TODO Auto-generated method stub
				@SuppressWarnings("unchecked")
				Map<String,Object> map = (Map<String, Object>)result;
				if(map == null) return;
				int resultCode = (Integer)map.get("resultCode");
				if(resultCode == 0){
					loadView.showLoad();
					@SuppressWarnings("unchecked")
					List<Friend> lists = (List<Friend>)map.get("userList");
					if(lists != null){
						userInfoAdapter.setData(lists);
						if(lists.size() < count){
							loadView.setVisibility(View.GONE);
						}
					}
					if(userInfoAdapter.getCount() == 0){
						loadView.showNoData();
					}else{
						loadView.setVisibility(View.GONE);
					}
				}else if(resultCode == MassVigContants.SESSIONVAILED){
					GoToLogin();
				}
				
			}
			
		}.execute();
		
	}
	
	protected void GoToLogin() {
//		if(!TextUtils.isEmpty(app.user.sessionID + MassVigUtil.getPreferenceData(this, "SESSIONID", "")))
			startActivity(new Intent(this, LoginActivity.class));
		Toast.makeText(FansListActivity.this, getString(R.string.account_failed), Toast.LENGTH_SHORT).show();
		app.user.sessionID = "";
		MassVigUtil.setPreferenceStringData(FansListActivity.this, "SESSIONID", "");
	}

	class UserInfoAdapter extends BaseAdapter{
		private List<Friend> lists;
		private ListMyOnclick listMyOnclick;
		private Bitmap defaultUserIcon;
		public UserInfoAdapter(){
			lists = new ArrayList<Friend>();
			listMyOnclick = new ListMyOnclick();
			defaultUserIcon = BitmapFactory.decodeResource(getResources(), R.drawable.commutity_user_icon_new_d);
		}
		public void setData(List<Friend> list){
			lists.addAll(list);
			notifyDataSetChanged();
		}
		
		public void clearData(){
			if(lists != null){
				lists.clear();
				notifyDataSetChanged();
			}
		}
		
		public void clearData(int position){
			if(lists != null){
				lists.remove(position);
				notifyDataSetChanged();
			}
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return lists == null ? 0 : lists.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return lists.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@SuppressWarnings("unchecked")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			final Friend userinfo = (Friend)getItem(position); 
			ArrayList<View> alist;
			if(convertView == null){
				alist = new ArrayList<View>();
				convertView = getLayoutInflater().inflate(R.layout.community_fans_list_item, null);
				alist.add(convertView.findViewById(R.id.fans_list_img));
				alist.add(convertView.findViewById(R.id.fans_list_name));
				alist.add(convertView.findViewById(R.id.fans_list_attention_count));
				alist.add(convertView.findViewById(R.id.fans_list_fans_count));
				alist.add(convertView.findViewById(R.id.community_fans_btn));
				alist.add(convertView.findViewById(R.id.loveshop_count));
				alist.add(convertView.findViewById(R.id.sex));
				
				convertView.setTag(alist);
			}
			alist = (ArrayList<View>)convertView.getTag();
			((TextView)alist.get(1)).setText(userinfo.NickName);
			((TextView)alist.get(2)).setText(String.valueOf(userinfo.FollowingCustomerCount));
			((TextView)alist.get(3)).setText(String.valueOf(userinfo.FansCustomerCount));
			((TextView)alist.get(5)).setText(getString(R.string.love_shop) +" "+userinfo.SharedCount);
			
			NetImageView userIcon = (NetImageView)alist.get(0);
			userIcon.setImageUrl(MassVigUtil.GetImageUrl(userinfo.HeadImgUrl, 100, 100), MassVigContants.PATH, defaultUserIcon);
			userIcon.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					startActivity(new Intent().setClass(FansListActivity.this,
							CommunityUserInfoActivity.class).putExtra("USERID",
									userinfo.CustomerID));
				}
			});
			Button fansBtn = ((Button)alist.get(4));
			
			int action = 0;
			
			if(userinfo.Relation == UNFOLLOW){
				fansBtn.setBackgroundResource(R.drawable.community_attention_btn_add);
				fansBtn.setVisibility(View.VISIBLE);
				action = 1;
			}else if(userinfo.Relation == FOLLOWING){
				fansBtn.setVisibility(View.VISIBLE);
				fansBtn.setBackgroundResource(R.drawable.community_attention_btn_remove);
				action = 0;
			}else {
				fansBtn.setVisibility(View.INVISIBLE);
			}
			fansBtn.setTag(new int[]{action,userinfo.CustomerID,position});
			fansBtn.setOnClickListener(listMyOnclick);
			//Gender,2-女,1-男
			ImageView sexImage = ((ImageView)alist.get(6));
//			sexImage.setVisibility(View.INVISIBLE);
//			if(userinfo.Gender == 2){
//				sexImage.setImageResource(R.drawable.sex_w);
//				sexImage.setVisibility(View.VISIBLE);
//			}else if(userinfo.Gender == 1){
//				sexImage.setImageResource(R.drawable.sex_m);
//				sexImage.setVisibility(View.VISIBLE);
//			}else
//				sexImage.setVisibility(View.INVISIBLE);
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					startActivity(new Intent().setClass(FansListActivity.this,
							CommunityUserInfoActivity.class).putExtra("USERID",
									userinfo.CustomerID));
				}
			});
			return convertView;
		}
		
		class ListMyOnclick implements OnClickListener{
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AddAndRemoveFans(v);
			}
			
		}
		
		
		
		private void AddAndRemoveFans(final View v){
			
			
			final int[] action = (int[])v.getTag();
			new AsyncTask<Object, Object, Object>() {

				
				@Override
				protected void onPreExecute() {
					// TODO Auto-generated method stub
					super.onPreExecute();
//					dialog = new ProgressDialog(FansListActivity.this);
//					dialog.setMessage(getString(R.string.please_wait));
//					dialog.show();
				}

				@Override
				protected Integer doInBackground(Object... params) {
					// TODO Auto-generated method stub
					return CommunityMode.getInstance().AddAndRemoveFans(action[0], app.user.sessionID, action[1]);
				}

				@Override
				protected void onPostExecute(Object result) {
					// TODO Auto-generated method stub
//					if(dialog != null){
//						dialog.dismiss();
//					}
					int resultCode = (Integer)result;
					if (action[0] == 0) {
						if (resultCode == 0) {
							Toast.makeText(FansListActivity.this,getString(R.string.remove_fans_succeed), Toast.LENGTH_SHORT).show();
							v.setBackgroundResource(R.drawable.community_attention_btn_add);
							if(loginUserId == userId && type == 0){
								clearData(action[2]);
							}else{
								Friend userinfo = (Friend)getItem(action[2]);
								if(loginUserId != userId)
									userinfo.FansCustomerCount -= 1;
								else
									userinfo.FollowingCustomerCount -= 1;
								//TODO 移除粉丝
								userinfo.Relation = UNFOLLOW;
								//userinfo.IsFocus = userinfo.IsFocus == 2 ? 3 : 1;
								notifyDataSetChanged();
							}
						}else if(resultCode == MassVigContants.SESSIONVAILED){
							GoToLogin();
						}else{
							Toast.makeText(FansListActivity.this,getString(R.string.remove_fans_failed), Toast.LENGTH_SHORT).show();
							
						}
					}else{
						if(resultCode == 0){
							Toast.makeText(FansListActivity.this,getString(R.string.add_fans_succeed), Toast.LENGTH_SHORT).show();
							v.setBackgroundResource(R.drawable.community_attention_btn_remove);
							Friend userinfo = (Friend)getItem(action[2]);
							if(loginUserId != userId)
								userinfo.FansCustomerCount += 1;
							else
								userinfo.FollowingCustomerCount += 1;
							//TODO
							userinfo.Relation = 3;
//							userinfo.IsFocus = userinfo.IsFocus == 3 ? 2 : 0;
							notifyDataSetChanged();
						}else if(resultCode == MassVigContants.SESSIONVAILED){
							GoToLogin();
						}else{
							Toast.makeText(FansListActivity.this,getString(R.string.add_fans_fail), Toast.LENGTH_SHORT).show();
						}
					}
				}

			}.execute();
			
		}
	}
	
	
	class MyOnclick implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v == subBtn){
				String str = editText.getText().toString().trim();
				if(!str.equals("")){
					keyWord = str;
					userInfoAdapter.clearData();
					loadView.setVisibility(View.VISIBLE);
					loadView.showLoading();
					LoadData();
				}
			}
			
		}
		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		operateAttentionCount = loginUserId == userId ? operateAttentionCount : 0;
		if(operateAttentionCount != 0){
			setResult(RESULT_OK, getIntent().putExtra("operateAttentionCount", operateAttentionCount));
		}
		
		finish();
		super.onBackPressed();
	}
	
}
