package com.massvig.ecommerce.widgets;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.activities.MessagesActivity;
import com.massvig.ecommerce.entities.Chater;
import com.massvig.ecommerce.entities.ChaterList;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigUtil;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 消息列表适配器
 * @author DuJun
 *
 */
public class MessageAdapter extends BaseAdapter{

	private ChaterList chaterList;
	private LayoutInflater mInflater;
	private Context mContext;
	private Handler mHandler;
	
	public MessageAdapter(Context context, ChaterList list, Handler h){
		this.chaterList = list;
		this.mContext = context;
		this.mHandler = h;
		mInflater = LayoutInflater.from(context);
	}
	
	public void setDataList(ChaterList list){
		this.chaterList = list;
	}
	
	@Override
	public int getCount() {
		return chaterList.getCount();
	}

	@Override
	public Object getItem(int position) {
		return chaterList.getChater(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		convertView = holder.getView(position);
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Message msg = new Message();
				msg.what = MessagesActivity.CLICK;
				Chater c = (Chater) getItem(position);
				msg.obj = c;
				mHandler.sendMessage(msg);
			}
		});
		return convertView;
	}
	
	class ViewHolder{
		private NetImageView headimg;
		private ImageView sex;
		private TextView messageCount, nickName, content, time;
		private View itemView;
		
		public ViewHolder(){
			itemView = mInflater.inflate(R.layout.message_item, null);
			itemView.setTag(this);
			headimg = (NetImageView)itemView.findViewById(R.id.headimg);
			sex = (ImageView)itemView.findViewById(R.id.sex);
			messageCount = (TextView)itemView.findViewById(R.id.message);
			nickName = (TextView)itemView.findViewById(R.id.nickname);
			content = (TextView)itemView.findViewById(R.id.content);
			time = (TextView)itemView.findViewById(R.id.time);
		}
		
		public View getView(int position){
			Chater c = (Chater) getItem(position);
			headimg.setImageUrl(MassVigUtil.GetImageUrl(c.headimg, 80, 80), MassVigContants.PATH, null);
			messageCount.setVisibility(View.INVISIBLE);
			if(c.unread > 0)
				messageCount.setVisibility(View.VISIBLE);
			messageCount.setText(c.unread + "");
			nickName.setText(c.nickname);
			content.setText(c.content);
			time.setText(GetTimeStamp(c.time));
			//TODO headimg, distance
//			distance.setText(mContext.getString(R.string.distance, ((int)(Double.valueOf((((int)c.distance) / 1000.0) + "") * 10)) / 10.0 + ""));
			sex.setVisibility(View.INVISIBLE);
//			if(c.gender == 1){
//				sex.setBackgroundResource(R.drawable.sex_m);
//				sex.setVisibility(View.VISIBLE);
//			}else if(c.gender == 2){
//				sex.setBackgroundResource(R.drawable.sex_w);
//				sex.setVisibility(View.VISIBLE);
//			}
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
				int distance = (int) (now.getTime() - date.getTime()) / 1000;
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

}
