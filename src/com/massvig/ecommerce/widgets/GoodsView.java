package com.massvig.ecommerce.widgets;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

/**
 * 
 * @author DuJun
 *
 */
public class GoodsView extends RelativeLayout implements View.OnClickListener{

	private FlowTag flowTag;
	private Context context;
	public Bitmap bitmap,headBitmap;
	private int columnIndex;// 图片属于第几列
	private int rowIndex;// 图片属于第几行
	private Handler viewHandler;
	private NetImageView imageView;
	private TextView name;
	private TextView detail;
	private TextView shareNum;
	private TextView praiseNum;
	private TextView address, time;
	private ImageView icon;
	private NetImageView userImg;
	private ImageView location;
	private LinearLayout layout_share;
	private LinearLayout layout_praise;
	
	private int TAG = 0;
	

	public GoodsView(Context c, AttributeSet attrs, int defStyle) {
		super(c, attrs, defStyle);
		this.context = c;
		Init();
	}

	public GoodsView(Context c, AttributeSet attrs) {
		super(c, attrs);
		this.context = c;
		Init();
	}

	public GoodsView(Context c) {
		super(c);
		this.context = c;
		Init();
	}

	private void Init() {
		LayoutInflater.from(this.context).inflate(R.layout.sharegoodsview, this);
		imageView = (NetImageView)findViewById(R.id.imageview);
		name = (TextView)findViewById(R.id.name);
		detail = (TextView)findViewById(R.id.detail);

		shareNum = (TextView)findViewById(R.id.sharenum);
		praiseNum = (TextView)findViewById(R.id.praisenum);
		address = (TextView)findViewById(R.id.address);
		time = (TextView)findViewById(R.id.time);
		icon = (ImageView)findViewById(R.id.icon);
		userImg = (NetImageView)findViewById(R.id.userimg);
		location = (ImageView)findViewById(R.id.location);
		layout_share = (LinearLayout)findViewById(R.id.share);
		layout_praise = (LinearLayout)findViewById(R.id.praise);
		
		setOnClickListener(this);
		imageView.setOnClickListener(this);
		userImg.setOnClickListener(this);
		location.setOnClickListener(this);
		layout_share.setOnClickListener(this);
		layout_praise.setOnClickListener(this);
		
		imageView.setAdjustViewBounds(true);

	}
	
	public void addShare(){
		TextView share = (TextView)findViewById(R.id.sharenum);
		int number = Integer.valueOf(share.getText().toString());
		share.setText(++number + "");
	}
	
	@Override
	public void onClick(View v) {

		if (this.isEnabled()) {
			Message msg = new Message();
			Handler h = getViewHandler();
			msg.arg1 = this.getId() - 1;
			switch (v.getId()) {
			case R.id.imageview:
				TAG = FlowTag.CLICK;
				msg.what = TAG;
				msg.obj = flowTag;
//				startScaleAnimation();
				h.sendMessage(msg);
				TAG = 0;
				break;
			case R.id.praise:
				if (flowTag.CanPraise) {
					TAG = FlowTag.PRAISE;
					msg.what = TAG;
					msg.obj = flowTag;
					h.sendMessage(msg);
					TAG = 0;
				}
				break;
			case R.id.share:
				TAG = FlowTag.SHARE;
				msg.what = TAG;
				msg.obj = flowTag;
				h.sendMessage(msg);
				TAG = 0;
				break;
			case R.id.userimg:
				TAG = FlowTag.USER;
				msg.what = TAG;
				msg.obj = flowTag;
				h.sendMessage(msg);
				TAG = 0;
				break;
			case R.id.location:
				TAG = FlowTag.LOCATION;
				msg.what = TAG;
				msg.obj = flowTag;
				h.sendMessage(msg);
				TAG = 0;
				break;

			default:
				return;
			}
		}
	}

	/**
	 * 加载图片
	 */
	public void LoadImage() {
		if (getFlowTag() != null) {
			setTextNotNull();
			setBitmap(false);
		}
	}

	/**
	 * 重新加载图片
	 */
	public void Reload() {
		if (this.bitmap == null && getFlowTag() != null) {
			setBitmap(true);
		}

	}

	private void setTextNotNull() {
		name.setText(flowTag.NickName);
		detail.setText(flowTag.Content);
		shareNum.setText(flowTag.SharedCount + "");
		praiseNum.setText(flowTag.PraiseCount + "");
		address.setText(flowTag.Address);
		time.setText(GetTimeStamp(flowTag.createTime));
		if(TextUtils.isEmpty(flowTag.Address))
			address.setText(context.getString(R.string.unknown));
//		if(flowTag.ShareSourceType == MassVigContants.Campaign){
			address.setVisibility(View.GONE);
//		}else{
//			time.setVisibility(View.GONE);
//		}
		if(!flowTag.CanPraise){
			icon.setBackgroundResource(R.drawable.ic_praise_02);
		}else{
			icon.setBackgroundResource(R.drawable.ic_praise_01);
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
					result = context.getString(R.string.campaign_second_ago, distance);
				} else if (distance < 60 * 60) {
					distance = distance / 60;
					result = context.getString(R.string.campaign_minute_ago, distance);
				} else if (distance < 60 * 60 * 24) {
					distance = distance / 60 / 60;
					result = context.getString(R.string.campaign_hour_ago, distance);
				} else if (distance < 60 * 60 * 24 * 7) {
					distance = distance / 60 / 60 / 24;
					result = context.getString(R.string.campaign_day_ago, distance);
				} else if (distance < 60 * 60 * 24 * 7 * 4) {
					distance = distance / 60 / 60 / 24 / 7;
					result = context.getString(R.string.campaign_week_ago, distance);
				} else if (distance < 60 * 60 * 24 * 7 * 4 * 12) {
					distance = distance / 60 / 60 / 24 / 7 / 4;
					result = context.getString(R.string.campaign_month_ago, distance);
				} else {
					distance = distance / 60 / 60 / 24 / 7 / 4 / 12;
					result = context.getString(R.string.campaign_year_ago, distance);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 回收内存
	 */
	public void recycle() {
		this.imageView.setImageBitmap(null);
		this.userImg.setImageBitmap(null);
		if ((this.bitmap == null) || (this.bitmap.isRecycled()))
			return;
		this.bitmap.recycle();
		this.bitmap = null;
	}

	public FlowTag getFlowTag() {
		return flowTag;
	}

	public void setFlowTag(FlowTag flowTag) {
		this.flowTag = flowTag;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public Handler getViewHandler() {
		return viewHandler;
	}

	public GoodsView setViewHandler(Handler viewHandler) {
		this.viewHandler = viewHandler;
		return this;
	}
	
	
	private void setBitmap(boolean isreload){
		if(isreload){
			userImg.setImageBitmap(headBitmap);
			imageView.setImageBitmap(bitmap);
			return;
		}
//		int width = bitmap.getWidth();// 获取真实宽高
//		int height = bitmap.getHeight();
//		int layoutHeight = (height * flowTag.getItemWidth())
//				/ width;// 调整高度
//		android.view.ViewGroup.LayoutParams l = imageView.getLayoutParams();
//		l.width = flowTag.getItemWidth() - 6;
////		l.height = layoutHeight;
//		l.height = MassVigUtil.dip2px(context, 75);
//		imageView.setLayoutParams(l);

		android.view.ViewGroup.LayoutParams l = imageView.getLayoutParams();
		l.width = flowTag.getItemWidth() - 2;
//		l.height = layoutHeight;
		if(flowTag.Width > 0)
			l.height = l.width * flowTag.Height / flowTag.Width;
		imageView.setLayoutParams(l);
		
//		startAlphaAnimation();
		String imageurl = flowTag.getFileName();
		imageView.setScaleType(ScaleType.CENTER);
		userImg.setImageUrl(MassVigUtil.GetImageUrl(flowTag.HeadImgUrl, MassVigUtil.dip2px(context, 43), MassVigUtil.dip2px(context, 43)), MassVigContants.PATH, null);
		imageView.setImageUrl(MassVigUtil.GetImageUrl(imageurl, l.width, l.height), MassVigContants.PATH, null);
//		imageView.setImageBitmap(bitmap);
		Handler h = getViewHandler();
		int totalBottomHeight = getLayoutHeight();
		Message m = h.obtainMessage(FlowTag.what, 0, l.height + totalBottomHeight, GoodsView.this);
		h.sendMessage(m);
	}

	private int getLayoutHeight() {
		int totalHeight = 0;
		int detailheight = 0;
		Paint pFont = detail.getPaint();
		Rect rect = new Rect();
		pFont.getTextBounds(detail.getText().toString(), 0, detail.getText()
				.length(), rect);
		int minHeight = rect.height() + 10;
		int maxWidth = rect.width();
		int width = flowTag.getItemWidth() - MassVigUtil.dip2px(this.context, 15);
		if (maxWidth >= width * 2) {// 3 LINES
			detailheight = minHeight * 3;
		} else if (maxWidth >= width) {// 2LINES
			detailheight = minHeight * 2;
		} else {// 1 LINE
			detailheight = minHeight;
		}
		if(TextUtils.isEmpty(detail.getText()))
			detailheight = 0;
		totalHeight = detailheight + MassVigUtil.dip2px(this.context, 95) + 8;
		return totalHeight;
	}

	public void addPraise() {
		int num = Integer.valueOf(praiseNum.getText().toString());
		praiseNum.setText(++num + "");
		icon.setBackgroundResource(R.drawable.ic_praise_02);
	}
	
}
