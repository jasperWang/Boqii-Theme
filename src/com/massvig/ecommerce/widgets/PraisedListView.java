package com.massvig.ecommerce.widgets;

import java.util.ArrayList;
import java.util.List;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.Praised;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigUtil;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PraisedListView extends ListView{
	
	private Bitmap defaultUserIcon;
	private CommonAdapter<Praised> mCommonAdapter;
	private Context mContext;
	private ArrayList<NetImageView> imgList = new ArrayList<NetImageView>();
	private boolean isScolling;
	
	public PraisedListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		// TODO Auto-generated constructor stub
	}
	
	public int getItemsCount(){
		if(mCommonAdapter == null)
			return 0;
		return mCommonAdapter.getCount();
	}
	
	public void setData(List<Praised> lists){
		if(mCommonAdapter == null){
			mCommonAdapter = new MyCommonAdapter(mContext, R.layout.community_praised_item);
			defaultUserIcon = BitmapFactory.decodeResource(getResources(), R.drawable.commutity_user_icon_new_d);
			setAdapter(mCommonAdapter);
			inScrollListener();
		}
		mCommonAdapter.appendData(lists);
	}
	
	public void inScrollListener(){
		setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
					isScolling = false;
					NetImageView.setIsAutoLoadImage(true);
					
					for(int i = 0,len = imgList.size();i < len ;i++){
						if(isScolling == false){
							imgList.get(i).updateImage();
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
	
	class MyCommonAdapter extends CommonAdapter<Praised>{

		public MyCommonAdapter(Context mContext, int itemLayoutId) {
			super(mContext, itemLayoutId);
			// TODO Auto-generated constructor stub
		}

		@Override
		public ViewHolderModel getViewHolderModel() {
			// TODO Auto-generated method stub
			return new MyViewHolderModel();
		}
		
	}

	class MyViewHolderModel extends ViewHolderModel{
		private NetImageView userIcon;
		private NetImageView pic;
		private TextView userName;
		private TextView time;
		private TextView content;
		private ImageView sex;
		
		@Override
		public void initViewHoler(View convertView) {
			// TODO Auto-generated method stub
			userIcon = (NetImageView)convertView.findViewById(R.id.fans_list_img);
			pic = (NetImageView)convertView.findViewById(R.id.fans_list_img1);
			userName = (TextView)convertView.findViewById(R.id.fans_list_name);
			time = (TextView)convertView.findViewById(R.id.fans_list_time);
			content = (TextView)convertView.findViewById(R.id.fans_list_content);
			sex = (ImageView)convertView.findViewById(R.id.sex);
			
			imgList.add(userIcon);
			imgList.add(pic);
		}

		@Override
		public void setViewHolerValues(View convertView, int position,
				Object itemData) {
			// TODO Auto-generated method stub
			Praised item = (Praised)itemData;
			String userIconUrl = MassVigUtil.GetImageUrl(item.HeadImgUrl, 50, 50);
			userIcon.setImageUrl(userIconUrl, MassVigContants.PATH, defaultUserIcon);
			String imgUrl = MassVigUtil.GetImageUrl(item.ImgUrl, 20, 20);
			pic.setImageUrl(imgUrl, MassVigContants.PATH, defaultUserIcon);
			userName.setText(item.NickName);
			time.setText(item.CreateTime);
			if(!TextUtils.isEmpty(item.Content) && !item.Content.equals("null"))
				content.setText(item.Content);
			else
				content.setText("");
			sex.setVisibility(INVISIBLE);
//			if(item.Gender == 2 ){
//				sex.setImageResource(R.drawable.sex_w);
//				sex.setVisibility(View.VISIBLE);
//			}else if(item.Gender == 1 ){
//				sex.setImageResource(R.drawable.sex_m);
//				sex.setVisibility(View.VISIBLE);
//			}
			
		}
		
	}

}
