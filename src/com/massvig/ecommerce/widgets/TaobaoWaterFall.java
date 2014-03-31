package com.massvig.ecommerce.widgets;

import java.util.ArrayList;
import java.util.HashMap;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.widgets.LazyScrollView.OnScrollListener;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class TaobaoWaterFall extends LinearLayout{

	private LazyScrollView waterfall_scroll;
	private LinearLayout waterfall_container;
	private ArrayList<LinearLayout> waterfall_items;
	private AssetManager asset_manager;
	private ArrayList<String> image_filenames;
	private Handler handler;
	private int item_width;
	private int column_count = 2;// 显示列数
	private int page_count = -1;// 每次加载30张图片
//	private int[] topIndex;
	private int[] bottomIndex;
	private int[] lineIndex;
	private int[] column_height;// 每列的高度
	private HashMap<Integer, String> pins;
	public int loaded_count = 0;// 已加载数量
	private HashMap<Integer, Integer>[] pin_mark = null;
	private Context context;
	private HashMap<Integer, GoodsView> iviews;
	int scroll_height;
	private int loadedNum = -1;
//	private boolean isClick = false;//是否点击了某个图片
	
	public TaobaoWaterFall(Context context) {
		super(context);
		this.context = context;
		initView();
	}

	public TaobaoWaterFall(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initView();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initView() {
		LayoutInflater.from(context).inflate(R.layout.waterfall, this);
		item_width = (((Activity) context).getWindowManager().getDefaultDisplay().getWidth() - 16) / column_count;// 根据屏幕大小计算每列大小
		asset_manager = this.context.getAssets();
		column_height = new int[column_count];
		iviews = new HashMap<Integer, GoodsView>();
		pins = new HashMap<Integer, String>();
		pin_mark = new HashMap[column_count];
		this.lineIndex = new int[column_count];
		this.bottomIndex = new int[column_count];
//		this.topIndex = new int[column_count];
		for (int i = 0; i < column_count; i++) {
			lineIndex[i] = -1;
			bottomIndex[i] = -1;
			pin_mark[i] = new HashMap();
		}

		InitLayout();
	}
	
	private void InitLayout() {
		waterfall_scroll = (LazyScrollView) findViewById(R.id.waterfall_scroll);

		waterfall_scroll.getView();
		waterfall_scroll.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onTop() {
				// 滚动到最顶端
				Log.d("LazyScroll", "Scroll to top");
			}

			@Override
			public void onScroll() {

			}

			@Override
			public void onBottom() {
				onLoadListener.onScrollBottom();
			}
			
			@Override
			public void onAutoScroll(int l, int t, int oldl, int oldt) {

			}


		});

		waterfall_container = (LinearLayout) this
				.findViewById(R.id.waterfall_container);
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {

				// super.handleMessage(msg);

				switch (msg.what) {
				case FlowTag.what:

					GoodsView v = (GoodsView) msg.obj;
					int h = msg.arg2;
					String f = v.getFlowTag().getFileName();

					// 此处计算列值
					int columnIndex = GetMinValue(column_height);

					v.setColumnIndex(columnIndex);
					column_height[columnIndex] += h;

					pins.put(v.getId(), f);
					iviews.put(v.getId(), v);
					
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(v.getFlowTag().getItemWidth(), h);
					lp.setMargins(0, 6, 0, 0);
					v.setLayoutParams(lp);
					v.setPadding(2, 0, 2, 2);
					waterfall_items.get(columnIndex).addView(v);

					lineIndex[columnIndex]++;

					pin_mark[columnIndex].put(lineIndex[columnIndex], column_height[columnIndex]);
					bottomIndex[columnIndex] = lineIndex[columnIndex];
					break;
				case FlowTag.CLICK:
					if(image_filenames!=null&&image_filenames.size() > msg.arg1)
						onLoadListener.onClickItemImage(image_filenames.get(msg.arg1), (FlowTag) msg.obj);
					break;
				case FlowTag.LOCATION:
					onLoadListener.onClickLocation((FlowTag) msg.obj);
					break;
				case FlowTag.PRAISE:
					onLoadListener.onClickPraise((FlowTag) msg.obj);
					break;
				case FlowTag.SHARE:
					onLoadListener.onClickShare((FlowTag) msg.obj);
					break;
				case FlowTag.USER:
					onLoadListener.onClickUserImg((FlowTag) msg.obj);
					break;
				}
			}

			@Override
			public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
				return super.sendMessageAtTime(msg, uptimeMillis);
			}
		};

		waterfall_items = new ArrayList<LinearLayout>();
		image_filenames = new ArrayList<String>();

		for (int i = 0; i < column_count; i++) {
			LinearLayout itemLayout = new LinearLayout(this.context);
			LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(
					0, LayoutParams.WRAP_CONTENT);
			itemParam.gravity = Gravity.CENTER_HORIZONTAL;
			itemParam.weight = 1.0f;
			itemLayout.setPadding(2, 2, 2, 2);
			itemLayout.setOrientation(LinearLayout.VERTICAL);
			itemLayout.setLayoutParams(itemParam);
			waterfall_items.add(itemLayout);
			waterfall_container.addView(itemLayout);
		}

	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void clearViews(){
		for (int i = 0; i < column_count; i++) {
			((LinearLayout)waterfall_items.get(i)).removeAllViews();
		}
		column_height = new int[column_count];
		iviews = new HashMap<Integer, GoodsView>();
		pins = new HashMap<Integer, String>();
		pin_mark = new HashMap[column_count];
		this.lineIndex = new int[column_count];
		this.bottomIndex = new int[column_count];
//		this.topIndex = new int[column_count];
		for (int i = 0; i < column_count; i++) {
			lineIndex[i] = -1;
			bottomIndex[i] = -1;
			pin_mark[i] = new HashMap();
		}
		
	}
	
	private ArrayList<TaobaoImages> list = new ArrayList<TaobaoImages>();
	
	public void addShare(int id){
		if(iviews.containsKey(id)){
			iviews.get(id).addShare();
		}
	}
	
	public void addItems(ArrayList<TaobaoImages> items){
		int number = list.size();
		list.addAll(items);
		page_count = page_count==-1 ? list.size() : page_count;
		loadedNum = list.size() - number;

		if (loadedNum != 0){
			for (int i = number; i < number + loadedNum; i++) {
				image_filenames.add(list.get(i).ImgUrl);
			}
			AddItemToContainer(number, list.size());
		}else{
//			isLoadDone = true;
		}
//		isLoading = false;
	}
	
	private void AddItemToContainer(int currentIndex, int size) {
		int imagecount = 10000;// image_filenames.size();
		if(currentIndex < size)
		for (int i = currentIndex; i < size
				&& i < imagecount; i++) {
			loaded_count++;
			AddImage(list.get(i),
					(int) Math.ceil(loaded_count / (double) column_count),
					loaded_count);
		}

	}
	
	private void AddImage(TaobaoImages goods, int rowIndex, int id) {
		//FIXME
		GoodsView item = new GoodsView(context);
		item.setRowIndex(rowIndex);
		item.setId(id);
		item.setViewHandler(this.handler);
		// 多线程参数
		FlowTag param = new FlowTag();
		param.setFlowId(id);
		param.setAssetManager(asset_manager);
//		param.setFileName(filename);
		
		param.Address = goods.Address;
		param.createTime = goods.createTime;
//		param.ProductID = goods.ProductID;
		param.RefID = goods.RefID;
		param.ShareSourceType = goods.ShareSourceType;
		param.CanPraise = goods.CanPraise;
		param.Content = goods.Content;
		param.CustomerID = goods.CustomerID;
		param.HeadImgUrl = goods.HeadImgUrl;
		param.ImgUrl = goods.ImgUrl;
		param.NickName = goods.NickName;
		param.PraiseCount = goods.PraiseCount;
		param.SharedCount = goods.SharedCount;
		param.ShareID = goods.ShareID;
		param.Height = goods.Height;
		param.Width = goods.Width;
		
		param.setFileName(goods.ImgUrl);
		param.setItemWidth(item_width);
		item.setFlowTag(param);
		item.LoadImage();

	}
	
	public void addPraise(int id){
		if(iviews.containsKey(id)){
			iviews.get(id).addPraise();
		}
	}
	
	private int GetMinValue(int[] array) {
		int m = 0;
		int length = array.length;
		for (int i = 0; i < length; ++i) {

			if (array[i] < array[m]) {
				m = i;
			}
		}
		return m;
	}
	
	public interface onLoadListener {
		void onScrollBottom();
		void onClickItemImage(String picurl, FlowTag tag);
		void onClickPraise(FlowTag tag);
		void onClickShare(FlowTag tag);
		void onClickUserImg(FlowTag tag);
		void onClickLocation(FlowTag tag);
	}
	
	private onLoadListener onLoadListener;
	
	public void setOnLoadListener(onLoadListener onLoadListener){
		this.onLoadListener = onLoadListener;
	}

}
