package com.massvig.ecommerce.activities;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.Goods;
import com.massvig.ecommerce.entities.GoodsList;
import com.massvig.ecommerce.utilities.Utility;
import com.massvig.ecommerce.widgets.MassVigData;
import com.massvig.ecommerce.widgets.NetImageView;
import com.massvig.ecommerce.widgets.NewActionAdapter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SlidingDrawer;
import android.widget.TextView;

public class NewActionDetailActivity extends BaseActivity implements OnClickListener{

	private TextView title, text;
	private GridView grid;
	private SlidingDrawer drawer;
	private ListView category;
	private LinearLayout background;
	public final static int NEW = 6, HOT = 4, REBATE = 8;
	private NewActionAdapter mAdapter;
	private GoodsList goodsList = new GoodsList();
	private GoodsList tempList = new GoodsList();
	private boolean isScolling, isLoading, isLoaddone;
	private int orderby;
	private String mcids = "102", minPrice = "0", maxPrice = "1000000000";

	private JSONArray cateGoryData;
	private LayoutInflater mInflater;

	private ProgressDialog dialog;
	private ScrollView scroll;
	private Timer timer;
	private TimerTask task;
	private int height;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_action_detail);
		height = this.getWindowManager().getDefaultDisplay().getHeight();
		dialog = new ProgressDialog(this);
		dialog.setMessage(getString(R.string.loading_data));
		setTitle(R.string.new_action_detail);
		mInflater = LayoutInflater.from(this);
		initView();
		new LoadDataAsync().execute();
		new LoadNumberAsync().execute();
		timer = new Timer();
		task = new TimerTask() {
			
			@Override
			public void run() {
				mHandler.sendEmptyMessage(1);
			}
		};
		timer.schedule(task, 200, 200);
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
	protected void onResume() {

		timer = new Timer();
		task = new TimerTask() {

			@Override
			public void run() {
				mHandler.sendEmptyMessage(1);
			}
		};
		timer.schedule(task, 200, 200);
		super.onResume();
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

	private void initView() {
		scroll = (ScrollView)findViewById(R.id.scroll);
		cateGoryData = getCategory();
		grid = (GridView)findViewById(R.id.grid);
		title = (TextView)findViewById(R.id.title);
		text = (TextView)findViewById(R.id.text);
		mAdapter = new NewActionAdapter(this, goodsList);
		grid.setAdapter(mAdapter);
		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Goods goods = new Goods();
				goods = goodsList.getGoods(arg2);
				startActivity(new Intent(NewActionDetailActivity.this, GoodsDetailActivity.class).putExtra("goods", goods));
			}
		});
		grid.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
					isScolling = false;
					NetImageView.setIsAutoLoadImage(true);
					ArrayList<NetImageView> imageLists = mAdapter.getImageList();
					for(int i = 0,len = imageLists.size();i < len ;i++){
						if(isScolling == false){
							imageLists.get(i).updateImage();
						}
					}
				}else if(scrollState == OnScrollListener.SCROLL_STATE_FLING){
					isScolling = true;
					NetImageView.setIsAutoLoadImage(false);
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
//				if (firstVisibleItem + visibleItemCount >= totalItemCount - 1
//						&& totalItemCount > 0) {
//					if (!isLoading && !isLoaddone) {
//						new LoadDataAsync().execute();
//					}
//				}
			}
		});
		drawer = (SlidingDrawer)findViewById(R.id.sliding);
		category = (ListView)findViewById(R.id.category_list);
		if(cateGoryData != null){
			MyListAdapter adapter = new MyListAdapter();
			category.setAdapter(adapter);
			category.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					if(cateGoryData.length() >= arg2){
						JSONObject o;
						try {
							o = cateGoryData.getJSONObject(arg2);
							int categoryID = o.optInt("CategoryID");
							String name = o.optString("Name");
							((TextView)findViewById(R.id.slide_btn)).setText(name);
							mcids = categoryID + "";
							goodsList.clearGoodsList();
							drawer.close();
							new LoadDataAsync().execute();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});
		}
		background = (LinearLayout)findViewById(R.id.background);
		((TextView)findViewById(R.id.slide_btn)).setOnClickListener(this);
		((Button)findViewById(R.id.back)).setOnClickListener(this);
		orderby = this.getIntent().getIntExtra("TYPE", NEW);
		switch (orderby) {
		case NEW:
			title.setText(getString(R.string.new_good));
			scroll.setBackgroundColor(Color.argb(255, 99, 181, 50));
			break;
		case HOT:
			title.setText(getString(R.string.hot_good));
			scroll.setBackgroundColor(Color.argb(255, 229, 71, 29));
			break;
		case REBATE:
			title.setText(getString(R.string.rebate_good));
			scroll.setBackgroundColor(Color.argb(255, 244, 155, 0));
			break;

		default:
			break;
		}
	}
	
	class MyListAdapter extends BaseAdapter{

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
				convertView = mInflater.inflate(R.layout.simple_listitem, null);
				holder.name = (TextView)convertView;
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			try {
				JSONObject o = cateGoryData.getJSONObject(position);
				holder.name.setText(o.optString("Name"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return convertView;
		}

		
		class ViewHolder{
			TextView name;
		}
	}
	

	class LoadDataAsync extends AsyncTask<Void, Void, Boolean>{

		@Override
		protected void onPostExecute(Boolean result) {
			isLoading = false;
//			if(dialog != null && dialog.isShowing())
//				dialog.dismiss();
			super.onPostExecute(result);
			if(result){
				goodsList.addGoodsList(tempList);
				tempList.clearGoodsList();
				grid.setAdapter(mAdapter);
				Bitmap b = getBitmapFromView();
				int height = b.getHeight();
				new Utility().setGridView2HeightBasedOnChildren(grid, height);
				mAdapter.notifyDataSetChanged();
			}
		}

		@Override
		protected void onPreExecute() {
//			if (dialog != null && !dialog.isShowing())
//				dialog.show();
			isLoading = true;
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			tempList.clearGoodsList();
			boolean result = tempList.FetchGoodsList(mcids, minPrice, maxPrice, goodsList.getCount() + "", orderby + "", 8);
			if(tempList.getCount() == 0){
				isLoaddone = true;
			}
			return result;
		}
		
	}
	class LoadNumberAsync extends AsyncTask<Void, Void, Integer>{

		@Override
		protected void onPostExecute(Integer result) {
			isLoading = false;
//			if(dialog != null && dialog.isShowing())
//				dialog.dismiss();
			super.onPostExecute(result);
			if(result != -1){
				switch (orderby) {
				case NEW:
					((TextView)findViewById(R.id.text)).setText(getString(R.string.action_new, result + ""));
					break;
				case HOT:
					((TextView)findViewById(R.id.text)).setText(getString(R.string.action_hot, result + ""));
					break;
				case REBATE:
					((TextView)findViewById(R.id.text)).setText(getString(R.string.action_rebate, result + ""));
					break;

				default:
					break;
				}
			}else{
				((TextView)findViewById(R.id.text)).setVisibility(View.GONE);
			}
		}

		@Override
		protected void onPreExecute() {
//			if (dialog != null && !dialog.isShowing())
//				dialog.show();
			isLoading = true;
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			int result = tempList.FetchActivityGoodsNumber(mcids, minPrice, maxPrice, goodsList.getCount() + "", orderby + "", 10);
			return result;
		}
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.slide_btn:
			break;

		default:
			break;
		}
	}
	private int y = -1;
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(scroll.getScrollY() + height + 100 >= background.getHeight()){
				if (!isLoading && !isLoaddone) {
					new LoadDataAsync().execute();
				}
			}
//			if(scroll.getScrollY() != y){
//				((TextView)findViewById(R.id.slide_btn)).getBackground().setAlpha(100);
//			}else{
//				((TextView)findViewById(R.id.slide_btn)).getBackground().setAlpha(255);
//			}
//			y = scroll.getScrollY();
		}};

	public Bitmap getBitmapFromView(){
		RelativeLayout v = (RelativeLayout) mInflater.inflate(R.layout.new_action_item, null);
		v.setLayoutParams(new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT));
		v.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
		Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		v.draw(c);
		return b;
	}
	

}
