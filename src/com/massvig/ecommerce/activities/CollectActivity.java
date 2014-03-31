package com.massvig.ecommerce.activities;

import java.util.ArrayList;

import com.massvig.ecommerce.activities.CollectActivity.listAdapter.ViewHolder;
import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.CollectList;
import com.massvig.ecommerce.entities.Goods;
import com.massvig.ecommerce.managers.CollectManager;
import com.massvig.ecommerce.managers.CollectManager.LoadListener;
import com.massvig.ecommerce.managers.GoodsDetailManager;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.widgets.LineTextView;
import com.massvig.ecommerce.widgets.NetImageView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CollectActivity extends BaseActivity implements OnClickListener{
	private Button edit,back;
	private ListView listview;
	private listAdapter adapter;
    private BaseApplication app;
    private CollectManager mManager;
    private boolean isScolling;
    private ProgressDialog dialog;
    private Button gotobuy;
    private RelativeLayout linear;
    private boolean isClick = false;
    private int index =  0;
    private int clickIndex;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setTitle(getString(R.string.collect_title));
		setContentView(R.layout.collect);
		init();
	}
	public void init(){	
		edit=(Button)findViewById(R.id.edit);
		back=(Button)findViewById(R.id.back);
		listview=(ListView)findViewById(R.id.collect_list);
		mManager = new CollectManager();
		app = (BaseApplication)getApplication();
		mManager.SessionID=app.user.sessionID;
		adapter=new listAdapter(this);
		listview.setAdapter(adapter);
		gotobuy = (Button) findViewById(R.id.gotobuy);
		linear = (RelativeLayout)findViewById(R.id.nodata);
		linear.setVisibility(View.GONE);
		linear.setOnClickListener(this);
		gotobuy.setOnClickListener(this);
		edit.setOnClickListener(this);
		back.setOnClickListener(this);
		dialog = new ProgressDialog(this);
		dialog.setMessage(getString(R.string.loading_data));
		mManager.setmanagerListener(new LoadListener() {
								
			@Override
			public void deleteCollectSucess() {
				// TODO Auto-generated method stub
				if(dialog!=null&&dialog.isShowing())
					dialog.dismiss();
			    mManager.collectlist.delete(clickIndex);
				adapter.notifyDataSetChanged();
				Toast.makeText(CollectActivity.this, getString(R.string.deleteCollect_success), Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void deleteCollectFailed() {
				// TODO Auto-generated method stub
				if(dialog!=null&&dialog.isShowing())
					dialog.dismiss();
			}
						
			@Override
			public void SessionVailed() {
				// TODO Auto-generated method stub
				if(dialog != null && dialog.isShowing())
					dialog.dismiss();
				    app.user.sessionID = "";
				    MassVigUtil.setPreferenceStringData(CollectActivity.this, "SESSIONID", "");
				    startActivity(new Intent(CollectActivity.this, LoginActivity.class));
				
				
			}

			@Override
			public void loadCollectSucess() {
				// TODO Auto-generated method stub
				if(dialog != null && dialog.isShowing())
					dialog.dismiss();
				mManager.refreshGoodsList();
				adapter.notifyDataSetChanged();

			}

			@Override
			public void loadCollectfailed() {
				// TODO Auto-generated method stub
				if(dialog!=null&&dialog.isShowing())
					dialog.dismiss();		
			}

			@Override
			public void noData() {
				// TODO Auto-generated method stub
              mHandler.sendEmptyMessage(1);
				
			}
		});
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Goods collect = mManager.getCollect(position);
				if(collect != null){
					startActivity(new Intent(CollectActivity.this,GoodsDetailActivity.class).putExtra("goods", collect));
				}
				
			}
			
		});
		listview.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub		
				if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
					isScolling = false;
					NetImageView.setIsAutoLoadImage(true);
					ArrayList<NetImageView> imageLists = adapter.getImageList();
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
				// TODO Auto-generated method stub
			}
		});
		

	
	}

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.edit:	
			IsShow();
			break;
		case R.id.back:
			finish();
			break;
		case R.id.gotobuy:
			startActivity(new Intent(this, MainTabActivity.class).putExtra(
					"INDEX", 0).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		default:
			break;
		}
		
	}
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				if(dialog != null && dialog.isShowing())
					dialog.dismiss();
//				Toast.makeText(CollectActivity.this, getString(R.string.no_collects), Toast.LENGTH_SHORT).show();	
				linear.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
						

		}};
		public void IsShow(){
			index++;
			if(index % 2 == 0)
			{
			isClick = false;
			adapter.notifyDataSetChanged();
			}else{
				isClick = true;
				adapter.notifyDataSetChanged();
			}
		} 
	public class listAdapter extends BaseAdapter{
		private LayoutInflater mInflater;
		private ArrayList<NetImageView> imageList = new ArrayList<NetImageView>();
		private Bitmap shopDefaultImg;
		private Context mContext;
		public listAdapter(Context context){
			this.mContext=context;
			mInflater=LayoutInflater.from(context);
			shopDefaultImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_icon);			
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mManager.collectlist.getCount();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mManager.collectlist.getcollect(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return Long.valueOf(mManager.collectlist.getCollectID(position));
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if(convertView==null){
				holder=new ViewHolder(position);
				imageList.add(holder.image);  
			}
			else{
				holder=(ViewHolder)convertView.getTag();
			}
			convertView=holder.getView(position);
			return convertView;
		}
		class ViewHolder{
			private NetImageView image;
			private TextView name,price,volume;
			private LineTextView realPrice;
			private View itemView;
			private TextView commentCount;
			private Button del;
			
			public ViewHolder(final int position){
				itemView = mInflater.inflate(R.layout.collect_item, null);
				itemView.setTag(this);
				commentCount = (TextView)itemView.findViewById(R.id.commentCount);
				image = (NetImageView)itemView.findViewById(R.id.image);
				name = (TextView)itemView.findViewById(R.id.name);
				price = (TextView)itemView.findViewById(R.id.price);
				realPrice = (LineTextView)itemView.findViewById(R.id.real_price);
				volume = (TextView)itemView.findViewById(R.id.volume);
				del=(Button)itemView.findViewById(R.id.del);
			}
			
			public View getView(final int position){
				final Goods collect = mManager.collectlist.getcollect(position);
				if (collect != null) {
					image.setImageUrl(
							MassVigUtil.GetImageUrl(collect.imageUrl,
									MassVigUtil.dip2px(mContext, 95),
									MassVigUtil.dip2px(mContext, 95)),
							MassVigContants.PATH, shopDefaultImg);
					name.setText(collect.name);
					commentCount.setText(""+collect.commentCount);
					price.setText(collect.minPrice + "");
					realPrice.setText(collect.realPrice + "");
					volume.setText(collect.volume + "");
					if(isClick == true){
						del.setVisibility(View.VISIBLE);
					}else{
						del.setVisibility(View.INVISIBLE);
					}
					del.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub		
							clickIndex = position;
							if(dialog != null && !dialog.isShowing())
								dialog.show();
							mManager.deleteCollect(collect.productID);	
						}
					});
				}
				return itemView;
			}
		}
		public ArrayList<NetImageView> getImageList() {
			// TODO Auto-generated method stub
			return this.imageList;
		}	
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mManager.clearData();
		app.user.sessionID=TextUtils.isEmpty(app.user.sessionID)?MassVigUtil.getPreferenceData(this, "SESSIONID", ""):app.user.sessionID;
		mManager.SessionID = app.user.sessionID;
		if(!TextUtils.isEmpty(mManager.SessionID)){
		if(dialog != null && !dialog.isShowing())
			dialog.show();
		mManager.getCollectList();
		}
	}

	
}
