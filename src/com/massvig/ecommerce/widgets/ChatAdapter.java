package com.massvig.ecommerce.widgets;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.Message;
import com.massvig.ecommerce.entities.MessageList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 消息内容适配器
 * @author DuJun
 *
 */
public class ChatAdapter extends BaseAdapter{

	private MessageList messageList;
	private LayoutInflater mInflater;
	private Bitmap fromImg, toImg;
	
	public static interface IMsgViewType{
		int MSG_TYPE_COME = 0;
		int MSG_TYPE_TO = 1;
	}
	
	public ChatAdapter(Context context, MessageList list, Bitmap fromImg, Bitmap toImg){
		this.messageList = list;
		this.fromImg = fromImg;
		this.toImg = toImg;
		mInflater = LayoutInflater.from(context);
	}
	
	public void setDataList(MessageList list){
		this.messageList = list;
	}
	
	@Override
	public int getCount() {
		return messageList.getCount();
	}

	@Override
	public Object getItem(int position) {
		return messageList.getMessage(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		Message msg = messageList.getMessage(position);
		if(msg.IsComMsg){
			return IMsgViewType.MSG_TYPE_COME;
		}else{
			return IMsgViewType.MSG_TYPE_TO;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		Message msg = (Message) getItem(position);
		if (convertView == null) {
			if(msg.IsComMsg)
				holder = new ViewHolder(IMsgViewType.MSG_TYPE_COME);
			else
				holder = new ViewHolder(IMsgViewType.MSG_TYPE_TO);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		convertView = holder.getView(position);
		return convertView;
	}
	
	class ViewHolder{
		private ImageView headimg;
		private TextView messageContent;
		private View itemView;
		private TextView timeText;
		private ImageView image;
		public ViewHolder(int type){
			if(type == IMsgViewType.MSG_TYPE_COME)
				itemView = mInflater.inflate(R.layout.chat_msg_item_left, null);
			else
				itemView = mInflater.inflate(R.layout.chat_msg_item_right, null);
			itemView.setTag(this);
			image = (ImageView)itemView.findViewById(R.id.image);
			headimg = (ImageView)itemView.findViewById(R.id.headimg);
			timeText = (TextView)itemView.findViewById(R.id.time);
			messageContent = (TextView)itemView.findViewById(R.id.message_content);
		}
		
		public View getView(int position){
			Message c = (Message) getItem(position);
			if(c.IsComMsg){
				if(fromImg != null)
					headimg.setImageBitmap(fromImg);
			}else{
				if(toImg != null)
					headimg.setImageBitmap(toImg);
			}
			if(c.IsShowTime == 1){
				timeText.setText(c.CreateTime + "");
				timeText.setVisibility(View.VISIBLE);
			}else{
				timeText.setVisibility(View.GONE);
			}
			if(c.isFailed){
				image.setVisibility(View.VISIBLE);
			}else{
				image.setVisibility(View.GONE);
			}
			messageContent.setText(c.MessageBody);
			return itemView;
		}
	}

}
