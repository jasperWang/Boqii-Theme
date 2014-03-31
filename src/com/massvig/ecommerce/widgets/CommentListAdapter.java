package com.massvig.ecommerce.widgets;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.Comment;
import com.massvig.ecommerce.entities.CommentList;
import com.massvig.ecommerce.utilities.MassVigUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 
 * @author DuJun
 *
 */
public class CommentListAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private CommentList commentList;
	private Handler mHandler;
	
	public void setHandler(Handler handler){
		mHandler = handler;
	}
	
	private Handler getHandler(){
		return mHandler;
	}

	public CommentListAdapter(Context c, CommentList list) {
		mContext = c;
		commentList = list;
		mInflater = LayoutInflater.from(mContext);
	}

	class ViewHolder {
		ImageView userImg;
		TextView userName;
		TextView detailText;
		TextView publish_time;
		ImageButton reply;
	}

	@Override
	public int getCount() {
		return commentList.getCount();
	}

	@Override
	public Object getItem(int position) {
		return commentList.getComment(position);
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
			convertView = mInflater.inflate(R.layout.comment_item, null);
			holder.userImg = (ImageView) convertView.findViewById(R.id.userimg);
			holder.userName = (TextView) convertView
					.findViewById(R.id.username);
			holder.detailText = (TextView) convertView
					.findViewById(R.id.comment);
			holder.publish_time = (TextView) convertView
					.findViewById(R.id.publish_time);
			holder.reply = (ImageButton) convertView
					.findViewById(R.id.comment_reply);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final Comment comment = commentList.getComment(position);
		if (!TextUtils.isEmpty(comment.HeadImgUrl)) {
			Bitmap bitmap = AsyncImageLoader.loadBitmap(MassVigUtil.GetImageUrl(comment.HeadImgUrl, 50, 50), new ImageCallback(
					holder.userImg));
			if (bitmap != null) {
				holder.userImg
						.setBackgroundDrawable(new BitmapDrawable(bitmap));
			}
		}
		final String name = comment.NickName;
		holder.reply.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Message msg = new Message();
				msg.what = 1;
				msg.arg1 = comment.CustomerID;
				msg.obj = name;
				Handler handler = getHandler();
				handler.sendMessage(msg);
			}
		});
		holder.userImg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Message msg = new Message();
				msg.what = 2;
				msg.arg1 = comment.CustomerID;
				msg.obj = name;
				Handler handler = getHandler();
				handler.sendMessage(msg);
			}
		});
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Message msg = new Message();
				msg.what = 1;
				msg.arg1 = comment.CustomerID;
				msg.obj = name;
				Handler handler = getHandler();
				handler.sendMessage(msg);
			}
		});
		holder.userName.setText(comment.NickName);
		holder.detailText.setText(comment.Content);
		String data = comment.CreateTime;
		if(!TextUtils.isEmpty(data)){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			try {
				Date d = sdf.parse(data);
				String time = (d.getMonth() + 1) + mContext.getString(R.string.month)
						+ d.getDate() + mContext.getString(R.string.day) + " "
						+ d.getHours() + ":" + (d.getMinutes() < 10 ? "0" : "")  +d.getMinutes();
				holder.publish_time.setText(time);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
		}
		return convertView;
	}

	private class ImageCallback implements AsyncImageLoader.ImageCallback {
		ImageView imageView;

		public ImageCallback(ImageView image) {
			imageView = image;
		}

		public void imageLoaded(Bitmap imageBitmap, String imageUrl) {
			if (imageView != null) {
				imageView.setBackgroundDrawable(null);
				imageView.setImageBitmap(imageBitmap);
			}

		}
	}

}
