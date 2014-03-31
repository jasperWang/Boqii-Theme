package com.massvig.ecommerce.widgets;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.activities.CommunitySignalActivity;
import com.massvig.ecommerce.entities.Post;
import com.massvig.ecommerce.entities.PostList;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * post列表适配器
 * @author DuJun
 *
 */
public class PostAdapter extends BaseAdapter{

	private PostList postList;
	private LayoutInflater mInflater;
	private ArrayList<NetImageView> imageList = new ArrayList<NetImageView>();
	private ArrayList<NetImageView> headImageList = new ArrayList<NetImageView>();
	private Bitmap shopDefaultImg, headImg;
	private Context mContext;
	private Handler mHandler;
	private int width;
	
	public PostAdapter(Context context, PostList list, Handler h, int w){
		this.postList = list;
		this.mContext = context;
		this.width = w;
		this.mHandler = h;
		mInflater = LayoutInflater.from(context);
		headImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.commutity_user_icon_d);
		shopDefaultImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_icon);
	}
	
	public void setDataList(PostList list){
		this.postList = list;
	}
	
	@Override
	public int getCount() {
		return postList.getCount();
	}

	@Override
	public Object getItem(int position) {
		return postList.getPost(position);
	}

	@Override
	public long getItemId(int position) {
		return Long.valueOf(postList.getPostID(position));
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			imageList.add(holder.image);
			headImageList.add(holder.headimg);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		convertView = holder.getView(position);
		return convertView;
	}
	
	private String GetTimeStamp(String oldTime){
		String result = "";
		if (!TextUtils.isEmpty(oldTime) && !oldTime.equals("null")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				Date now = new Date();
				Date date = (Date) sdf.parse(oldTime);
				int distance = (int) (now.getTime() - date.getTime()) / 1000;
				if (distance < 0)
					distance = 0;
				if (distance < 60) {
					result = mContext.getString(R.string.campaign_second_ago, distance);
				} else if (distance < 60 * 60) {
					distance = distance / 60;
					result = mContext.getString(R.string.campaign_minute_ago, distance);
				} else if (distance < 60 * 60 * 24) {
					distance = distance / 60 / 60;
					result = mContext.getString(R.string.campaign_hour_ago, distance);
				} else if (distance < 60 * 60 * 24 * 7) {
					distance = distance / 60 / 60 / 24;
					result = mContext.getString(R.string.campaign_day_ago, distance);
				} else if (distance < 60 * 60 * 24 * 7 * 4) {
					distance = distance / 60 / 60 / 24 / 7;
					result = mContext.getString(R.string.campaign_week_ago, distance);
				} else if (distance < 60 * 60 * 24 * 7 * 4 * 12) {
					distance = distance / 60 / 60 / 24 / 7 / 4;
					result = mContext.getString(R.string.campaign_month_ago, distance);
				} else {
					distance = distance / 60 / 60 / 24 / 7 / 4 / 12;
					result = mContext.getString(R.string.campaign_year_ago, distance);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	class ViewHolder{
		private NetImageView image,headimg;
		private TextView detail,name,address,praise,share,text1,text2,time;
		private ImageView icon, share_icon;
		private LinearLayout share_layout;
		private View itemView;
		
		public ViewHolder(){
			itemView = mInflater.inflate(R.layout.post_item, null);
			itemView.setTag(this);
			share_layout = (LinearLayout)itemView.findViewById(R.id.share_layout);
			image = (NetImageView)itemView.findViewById(R.id.image);
			headimg = (NetImageView)itemView.findViewById(R.id.headimg);
			detail = (TextView)itemView.findViewById(R.id.detail);
			name = (TextView)itemView.findViewById(R.id.name);
			address = (TextView)itemView.findViewById(R.id.address);
			praise = (TextView)itemView.findViewById(R.id.praise);
			share = (TextView)itemView.findViewById(R.id.share);
			text1 = (TextView)itemView.findViewById(R.id.text1);
			text2 = (TextView)itemView.findViewById(R.id.text2);
			time = (TextView)itemView.findViewById(R.id.time);
			icon = (ImageView)itemView.findViewById(R.id.icon);
			share_icon = (ImageView)itemView.findViewById(R.id.share_icon);
		}
		
		public View getView(int position){
			final Post post = postList.getPost(position);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
			image.setLayoutParams(params);
			image.setImageUrl(MassVigUtil.GetImageUrl(post.imageUrl, width, width/*MassVigUtil.dip2px(mContext, 200)*/), MassVigContants.PATH, shopDefaultImg);
			headimg.setImageUrl(MassVigUtil.GetImageUrl(post.userimgurl, MassVigUtil.dip2px(mContext, 40), MassVigUtil.dip2px(mContext, 40)), MassVigContants.PATH, headImg);
			detail.setVisibility(View.GONE);
			if(!TextUtils.isEmpty(post.detail)){
				detail.setVisibility(View.VISIBLE);
			}
			detail.setText(post.detail);
			name.setText(post.username);
			address.setText(post.address);
			if(post.praise > 0){
				praise.setText(post.praise + "");	
			}else{
				praise.setText("");
			}
			if(post.CommentCount > 0){
				share.setText(post.CommentCount + "");
			}else{
				share.setText("");
			}
			text1.setText(post.ShareSourceMsg1);
			text2.setText(post.ShareSourceMsg2);
			time.setText(GetTimeStamp(post.createTime));
			if(!post.CanPraise){
				icon.setBackgroundResource(R.drawable.ic_praise_02);
				icon.setEnabled(false);
				praise.setEnabled(false);
			}else{
				praise.setEnabled(true);
				icon.setEnabled(true);
				icon.setBackgroundResource(R.drawable.ic_praise_01);
			}
//			if(!post.CanShare){
//				share.setEnabled(false);
//				share_icon.setEnabled(false);
//				share_icon.setBackgroundResource(R.drawable.ic_share_02);
//			}else{
//				share.setEnabled(true);
//				share_icon.setEnabled(true);
//				share_icon.setBackgroundResource(R.drawable.ic_share_01);
//			}
			headimg.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Message msg = new Message();
					msg.what = CommunitySignalActivity.HEAD;
					msg.obj = post;
					mHandler.sendMessage(msg);
				}
			});
			icon.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(!post.CanPraise){
					}else{
						Message msg = new Message();
						msg.what = CommunitySignalActivity.PRAISE;
						msg.obj = post;
						mHandler.sendMessage(msg);
					}
				}
			});
			praise.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(!post.CanPraise){
					}else{
						Message msg = new Message();
						msg.what = CommunitySignalActivity.PRAISE;
						msg.obj = post;
						mHandler.sendMessage(msg);
					}
				}
			});
			share_layout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					share(post);
				}
			});
			share_icon.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					share(post);
				}
			});
			share.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					share(post);
				}
			});
			image.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Message msg = new Message();
					msg.what = CommunitySignalActivity.ITEM;
					msg.obj = post;
					mHandler.sendMessage(msg);
				}
			});
			text2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Message msg = new Message();
					msg.what = CommunitySignalActivity.CONTENT;
					msg.obj = post;
					mHandler.sendMessage(msg);
				}
			});
			return itemView;
		}

		protected void share(Post post) {
			Message msg = new Message();
			msg.what = CommunitySignalActivity.SHARE;
			msg.obj = post;
			mHandler.sendMessage(msg);
		}
	}

	public ArrayList<NetImageView> getHeadImageList(){
		return this.headImageList;
	}
	
	public ArrayList<NetImageView> getImageList() {
		return this.imageList;
	}
	
}
