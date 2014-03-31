package com.massvig.ecommerce.widgets;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.activities.NearPersonAcvitity;
import com.massvig.ecommerce.entities.NearPerson;
import com.massvig.ecommerce.entities.NearPersonList;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 附近的人列表适配器
 * @author DuJun
 *
 */
public class NearPersonAdapter extends BaseAdapter{

	private NearPersonList personList;
	private LayoutInflater mInflater;
	private Context mContext;
	private ArrayList<NetImageView> imgList = new ArrayList<NetImageView>();
	private Bitmap shopDefaultImg;
	private Handler mHandler;
	
	public NearPersonAdapter(Context context, NearPersonList list, Handler h){
		this.personList = list;
		this.mContext = context;
		this.mHandler = h;
		mInflater = LayoutInflater.from(context);
		shopDefaultImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.commutity_user_icon_d);
	}
	
	public void setDataList(NearPersonList list){
		this.personList = list;
	}
	
	@Override
	public int getCount() {
		return personList.getCount();
	}

	@Override
	public Object getItem(int position) {
		return personList.getNearPerson(position);
	}

	@Override
	public long getItemId(int position) {
		return Long.valueOf(personList.getNearPersonID(position));
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			imgList.add(holder.headImg);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		convertView = holder.getView(position);
		return convertView;
	}
	
	class ViewHolder{
		private NetImageView headImg;
		private ImageView sex;
		private TextView nickName, distance, buy_info, time, fans_count, attention_count;
		private Button fans_btn;
		private LinearLayout fans_layout;
		private View itemView;
		
		public ViewHolder(){
			itemView = mInflater.inflate(R.layout.near_person_item, null);
			itemView.setTag(this);
			fans_layout = (LinearLayout)itemView.findViewById(R.id.fans_layout);
			headImg = (NetImageView)itemView.findViewById(R.id.headimg);
			sex = (ImageView)itemView.findViewById(R.id.sex);
			nickName = (TextView)itemView.findViewById(R.id.nickname);
			distance = (TextView)itemView.findViewById(R.id.distance);
			buy_info = (TextView)itemView.findViewById(R.id.buy_info);
			time = (TextView)itemView.findViewById(R.id.order_time);
			fans_count = (TextView)itemView.findViewById(R.id.fans_list_fans_count);
			attention_count = (TextView)itemView.findViewById(R.id.fans_list_attention_count);
			fans_btn = (Button)itemView.findViewById(R.id.community_fans_btn);
		}
		
		public View getView(int position){
			final NearPerson p = personList.getNearPerson(position);
			String headUrl = MassVigUtil.GetImageUrl(p.HeadImgUrl, 80, 80);
			headImg.setImageUrl(headUrl, MassVigContants.PATH, shopDefaultImg);
//			sex.setVisibility(View.INVISIBLE);
//			if(p.Gender == 1){
//				sex.setVisibility(View.VISIBLE);
//				sex.setBackgroundResource(R.drawable.sex_m);
//			}else if(p.Gender == 2){
//				sex.setVisibility(View.VISIBLE);
//				sex.setBackgroundResource(R.drawable.sex_w);
//			}
			nickName.setText(p.NickName);
			

			distance.setText(((Double.valueOf(p.Distance)) > 1000) ? mContext.getString(
							R.string.distance,
							(int)(((int)(Double.valueOf((Double.valueOf(p.Distance) / 1000.0) + "") * 10))/ 10.0) + "")
							: mContext.getString(R.string.small_distance,((int)(Double.valueOf((Double.valueOf(p.Distance)) + "") * 1)) + ""));
			
//			distance.setText(p.Distance + "");
			if(p.ProductID != 0){
				buy_info.setVisibility(View.VISIBLE);
				buy_info.setText(mContext.getString(R.string.buyed, p.ProductName));
				fans_layout.setVisibility(View.GONE);
			}else{
				buy_info.setVisibility(View.GONE);
				fans_layout.setVisibility(View.VISIBLE);
				fans_count.setText(p.FansCustomerCount + "");
				attention_count.setText(p.FollowingCustomerCount + "");
			}
			time.setText(GetTimeStamp(p.LastLocationTime));
			if(p.Relation == 2){//未关注
				fans_btn.setBackgroundResource(R.drawable.community_attention_btn_add);
			}else if(p.Relation == 3){
				fans_btn.setBackgroundResource(R.drawable.community_attention_btn_remove);
			}else{
				fans_btn.setVisibility(View.INVISIBLE);
			}
			fans_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Message msg = new Message();
					msg.what = NearPersonAcvitity.CLICK;
					msg.obj = p;
					mHandler.sendMessage(msg);
				}
			});
			buy_info.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Message msg = new Message();
					msg.what = NearPersonAcvitity.PRODUCT;
					msg.obj = p;
					mHandler.sendMessage(msg);
				}
			});
			itemView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Message msg = new Message();
					msg.what = NearPersonAcvitity.USER;
					msg.obj = p;
					mHandler.sendMessage(msg);
				}
			});
			return itemView;
		}
	}

	private String GetTimeStamp(String oldTime){
		String result = "";
		if (!TextUtils.isEmpty(oldTime) && !oldTime.equals("null")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				Date now = new Date();
				Date date = (Date) sdf.parse(oldTime);
				int distance = (int) ((now.getTime() - date.getTime()) / 1000);
				if (distance < 0)
					distance = 0;
				if (distance < 60) {
					result = mContext.getString(R.string.second_ago, distance);
				} else if (distance < 60 * 60) {
					distance = distance / 60;
					result = mContext.getString(R.string.minute_ago, distance);
				} else if (distance < 60 * 60 * 24) {
					distance = distance / 60 / 60;
					result = mContext.getString(R.string.hour_ago, distance);
				} else if (distance < 60 * 60 * 24 * 7) {
					distance = distance / 60 / 60 / 24;
					result = mContext.getString(R.string.day_ago, distance);
				} else if (distance < 60 * 60 * 24 * 7 * 4) {
					distance = distance / 60 / 60 / 24 / 7;
					result = mContext.getString(R.string.week_ago, distance);
				} else if (distance < 60 * 60 * 24 * 7 * 4 * 12) {
					distance = distance / 60 / 60 / 24 / 7 / 4;
					result = mContext.getString(R.string.month_ago, distance);
				} else {
					distance = distance / 60 / 60 / 24 / 7 / 4 / 12;
					result = mContext.getString(R.string.year_ago, distance);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public ArrayList<NetImageView> getImageList() {
		return this.imgList;
	}

}
