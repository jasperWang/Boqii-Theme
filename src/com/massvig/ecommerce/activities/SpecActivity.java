package com.massvig.ecommerce.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.massvig.ecommerce.activities.ShoppingCarActivity.ModifyProductsAsync;
import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.Goods;
import com.massvig.ecommerce.entities.GoodsDetail;
import com.massvig.ecommerce.entities.Product;
import com.massvig.ecommerce.entities.ProductList;
import com.massvig.ecommerce.managers.DealActivityManager;
import com.massvig.ecommerce.managers.DealActivityManager.Item;
import com.massvig.ecommerce.managers.DealActivityManager.ProductProperty;
import com.massvig.ecommerce.service.MassVigService;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.widgets.NetImageView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SpecActivity extends BaseActivity implements OnClickListener{

	private EditText count;
	private TextView money;
	private TextView total;
	
	private DealActivityManager mManager;
	private LinearLayout params_layout;
//	private HashMap<String, Integer> map;
	private int LayoutID = 0;
	private ArrayList<MyButton> buttons = new ArrayList<SpecActivity.MyButton>();
	private GoodsDetail goodsdetail;
	private static final int textSize = 16;
	private int imgsize = 75;
	private NetImageView product_img;
	private BaseApplication app;
	private ProgressDialog dialog;
	private Button asc, desc;
	
	private TextView detail;
//	private HashMap<TextView, Integer> views = new HashMap<TextView, Integer>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spec);
		setTitle(getString(R.string.spec));
		app = (BaseApplication) getApplication();
		app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(this, "SESSIONID", "") : app.user.sessionID;
		mManager = new DealActivityManager();
		Goods goods = new Goods();
		goodsdetail = (GoodsDetail)this.getIntent().getSerializableExtra("goodsdetail");
		goods.minPrice = goodsdetail.minPrice;
//		goods.Inventory = goodsdetail.volume;
		mManager.setGoods(goods);
		mManager.setParamsInfo(goodsdetail.specInfo);
		int width = this.getWindowManager().getDefaultDisplay().getWidth();
		mManager.setWidth(width - MassVigUtil.dip2px(this, 40));
		initLayout();
//		map = mManager.HasNotContains();
		refreshViews(-1);
	}
	
	public class MyButton{
		TextView view;
		int categoryID;
		boolean enable;
		boolean selected;
		String imgurl;
	}
	
	private void initLayout() {
		asc = (Button) findViewById(R.id.asc);
		desc = (Button) findViewById(R.id.desc);
		asc.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int num = Integer.valueOf(count.getText().toString());
				if (num < mManager.getVolumn()) {
					count.setText(++num + "");
					CalculatePrice();
				}
			}
		});
		desc.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int num = Integer.valueOf(count.getText().toString());
				if (num > 1) {
					count.setText(--num + "");
					CalculatePrice();
				}

			}
		});
		dialog = new ProgressDialog(this);
		dialog.setMessage(getString(R.string.wait));
		detail = (TextView)findViewById(R.id.detail);
		detail.setText(goodsdetail.name);
		product_img = (NetImageView)findViewById(R.id.product_img);
		product_img.setImageUrl(MassVigUtil.GetImageUrl(goodsdetail.imageUrl, MassVigUtil.dip2px(this, imgsize), MassVigUtil.dip2px(this, imgsize)), MassVigContants.PATH, null);
		((Button)findViewById(R.id.confirm)).setOnClickListener(this);
		((Button)findViewById(R.id.add)).setOnClickListener(this);
		((Button)findViewById(R.id.back)).setOnClickListener(this);
		count = (EditText)findViewById(R.id.count);
		count.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				String number = s.toString();
				if(!TextUtils.isEmpty(number)){
					mManager.setCount(Integer.valueOf(number));
				}else{
					mManager.setCount(0);
				}
				money.setText("￥" + mManager.getMoney());
			}
		});
		money = (TextView)findViewById(R.id.money);
		total = (TextView)findViewById(R.id.total);
		money.setText("￥" + mManager.getMoney());
		total.setText(getString(R.string.total, mManager.getVolumn() + ""));
		params_layout = (LinearLayout)findViewById(R.id.params_layout);
		ArrayList<ProductProperty> propertyItems = mManager.getPropertyItems();
		if(propertyItems != null && propertyItems.size() > 0){
			for (int i = 0; i < propertyItems.size(); i++) {
				ProductProperty item = propertyItems.get(i);
				addTitle(item.name);
				LinearLayout l = new LinearLayout(this);
				l.setOrientation(LinearLayout.VERTICAL);
				l.setTag(LayoutID++);
				params_layout.addView(l);
				for (int j = 0; j < item.items.size(); j++) {
					Item it = item.items.get(j);
					addProper(it);
				}
				TextView view = new TextView(this);
				view.setHeight(4);
				view.setWidth(mManager.getWidth());
				view.setBackgroundResource(R.drawable.ic_xuxian);
				addLayoutToParent(params_layout, view);
			}
		}
	}
	
	private void addProper(Item item) {
		TextView proper = new TextView(this);
		proper.setTextColor(R.color.color_black_text);
		proper.setBackgroundResource(R.drawable.bg_unselected);
		proper.setTextSize(textSize);
		proper.setTag(item.id);
		proper.setOnClickListener(new MyClick());
		proper.setSingleLine(true);
		proper.setText(item.name);
		proper.setGravity(Gravity.CENTER);
		proper.setMinWidth(mManager.getWidth() / 3);
		addItemToLayout(proper, item.imgurl);
		
	}
	
	private void addLayoutToParent(ViewGroup parent, View child){
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, MassVigUtil.dip2px(this, 10), 0, 0); 
		parent.addView(child, lp);
	}
	
	private void addTextViewToParent(ViewGroup parent, TextView child, String imgurl){
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.setMargins(MassVigUtil.dip2px(this, 5), 0, MassVigUtil.dip2px(this, 5), 0); 
		parent.addView(child, lp);
		MyButton btn = new MyButton();
		btn.selected = false;
		btn.enable = true;
		btn.view = child;
		btn.imgurl = imgurl;
		btn.categoryID = (Integer) ((LinearLayout)parent.getParent()).getTag();
		buttons.add(btn);
//		views.put(child, (Integer) ((LinearLayout)parent.getParent()).getTag());
	}

	//imgurl:对应的图片地址
	private void addItemToLayout(TextView proper, String imgurl) {
		int number = params_layout.getChildCount();
		if(number > 0){
			LinearLayout l = (LinearLayout) params_layout.getChildAt(number - 1);
			int num = l.getChildCount();
			LinearLayout linearLayout;
			if(num > 0){
				linearLayout = (LinearLayout) l.getChildAt(num - 1);
			}else{
				linearLayout = new LinearLayout(this);
				addLayoutToParent(l, linearLayout);
			}
			int width = 0;
			Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPaint.setTextAlign(Align.LEFT);
			mPaint.setTextSize(textSize);
			mPaint.setColor(R.color.color_black_text);
			for (int i = 0; i < linearLayout.getChildCount(); i++) {
				TextView v = (TextView)linearLayout.getChildAt(i);
				int measureWidth = MassVigUtil.dip2px(SpecActivity.this, (int) mPaint.measureText(v.getText().toString())) + 30;
				width += measureWidth > mManager.getWidth() / 3 ? measureWidth : mManager.getWidth() / 3;
			}
			width += MassVigUtil.dip2px(SpecActivity.this, (int) mPaint.measureText(proper.getText().toString())) + 30;
			if(linearLayout.getChildCount() == 0){
				addTextViewToParent(linearLayout, proper, imgurl);
				return;
			}
			if(width < mManager.getWidth()){
				addTextViewToParent(linearLayout, proper, imgurl);
			}else{
				LinearLayout l1 = new LinearLayout(SpecActivity.this);
				addLayoutToParent(l, l1);
				addTextViewToParent(l1, proper, imgurl);
			}
		}
	}

	private void addTitle(String text) {
		TextView name = new TextView(this);
		name.setTextColor(R.color.color_black_text);
		name.setText(text);
		name.setTextSize(16);
		params_layout.addView(name);
		
	}
	
	/**
	 * 刷新按钮状态
	 */
	private void refreshViews(int categoryIndex){
//		ArrayList<MyButton> tempbuttons = ScanButtons(categoryIndex);
		for (int i = 0; i < buttons.size(); i++) {
			MyButton btn = buttons.get(i);
			if (!btn.selected) {
				switch (IsUseful(btn, categoryIndex)) {
				case -1:
					btn.enable = false;
					SetEnable(btn.view, false);
					break;
				case 0:
					btn.enable = true;
					SetEnable(btn.view, true);
					break;
				case 1:
					btn.enable = true;
					setSelected(btn.view);
					break;

				default:
					break;
				}
//				if (IsUseful(btn, categoryIndex)) {
//					btn.enable = true;
//					SetEnable(btn.view, true);
//				} else {
//					btn.enable = false;
//					SetEnable(btn.view, false);
//				}
			}
		}
	}
	
	private int IsUseful(MyButton btn, int categoryIndex){
		int result = -1;
		String select = mManager.getSelectProperties();
		String[] selected = select.split(",");
		selected[btn.categoryID] = String.valueOf(btn.view.getTag());
		List<String> list = mManager.getDescartes(selected);
		int count = 0;
		for (int i = 0; i < buttons.size(); i++) {
			MyButton button = buttons.get(i);
			if(btn.categoryID == button.categoryID)
				count ++;
		}
		if(count == 1){
			return 1;
		}
		for (int i = 0; i < list.size(); i++) {
			if(mManager.isUseful(list.get(i))){
				result = 0;
				return result;
			}
		}
		return result;
	}
	
	/**
	 * 判断MyButton是否可点击
	 * @param btn
	 */
//	private boolean IsUseful(MyButton btn, int categoryIndex) {
//		String select = mManager.getSelectProperties();
//		String[] selected = select.split(",");
//		selected[btn.categoryID] = String.valueOf(btn.view.getTag());
//		List<String> list = mManager.getDescartes(selected);
//		boolean enable = false;
//		for (int i = 0; i < list.size(); i++) {
//			if(mManager.isUseful(list.get(i))){
//				enable = true;
//				return enable;
//			}
//		}
//		return enable;
//		
////		String result = "";
////		for (int i = 0; i < selected.length; i++) {
////			result += selected[i];
////		}
////		if(!TextUtils.isEmpty(result))
////			result = result.substring(0, result.length() - 1);
////		return mManager.isUseful(result);
//	}

	
	/**
	 * 移除不需检查的buttons
	 * @param tempButtons
	 * @param value
	 */
//	@SuppressWarnings("unchecked")
//	private void removeWithValue(ArrayList<MyButton> tempButtons,int value){
//		ArrayList<MyButton> temp = new ArrayList<SpecActivity.MyButton>();
//		temp = (ArrayList<MyButton>) tempButtons.clone();
//		for (int i = tempButtons.size() - 1; i >= 0; i--) {
//			MyButton btn = temp.get(i);
//			if(btn.categoryID == value){
//				tempButtons.remove(i);
//			}
//		}
//	}
	
	/**
	 * 遍历需检查的buttons
	 * @param categoryIndex
	 */
//	@SuppressWarnings("unchecked")
//	private ArrayList<MyButton> ScanButtons(int categoryIndex) {
//		ArrayList<MyButton> tempButtons = new ArrayList<SpecActivity.MyButton>();
//		tempButtons = (ArrayList<MyButton>) buttons.clone();
//		removeWithValue(tempButtons, categoryIndex);
//		return tempButtons;
//	}
	
	@SuppressWarnings("unchecked")
	private void setSelected(TextView textView){
		textView.setTextColor(R.color.color_white);
		textView.setBackgroundResource(R.drawable.bg_selected);
		textView.setEnabled(true);
		ArrayList<MyButton> tempButtons = new ArrayList<SpecActivity.MyButton>();
		tempButtons = (ArrayList<MyButton>) buttons.clone();
		setButtonsSingleChoose(tempButtons, textView);
	}

	private void SetEnable(TextView textView, boolean b) {
		if(b){
			textView.setTextColor(R.color.color_black_text);
			textView.setBackgroundResource(R.drawable.bg_unselected);
			textView.setEnabled(true);
		}else{
			textView.setBackgroundResource(R.drawable.bg_enable);
			textView.setEnabled(false);
		}
	}

	public class MyClick implements OnClickListener{

		@SuppressWarnings("unchecked")
		@Override
		public void onClick(View v) {
			TextView text = (TextView)v;
			ArrayList<MyButton> tempButtons = new ArrayList<SpecActivity.MyButton>();
			tempButtons = (ArrayList<MyButton>) buttons.clone();
			setButtonsSingleChoose(tempButtons, text);
			
//			LinearLayout l = (LinearLayout)text.getParent().getParent();
//			int layoutid = (Integer) l.getTag();
//			for (int i = 0; i < l.getChildCount(); i++) {
//				LinearLayout child = (LinearLayout) l.getChildAt(i);
//				for (int j = 0; j < child.getChildCount(); j++) {
//					TextView t = (TextView)child.getChildAt(j);
//					t.setBackgroundResource(R.drawable.bg_unselected);
//				}
//			}
//			v.setBackgroundResource(R.drawable.bg_selected);
		}
		
	}

	/**
	 * 同一个分类单选效果
	 * @param tempButtons
	 * @param text
	 */
	public void setButtonsSingleChoose(ArrayList<MyButton> tempButtons, TextView text) {
		int categoryid = -1;
		for (int i = 0; i < tempButtons.size(); i++) {
			MyButton btn = tempButtons.get(i);
			if(btn.view == text){
				categoryid = btn.categoryID;
				//如果是第一个分类则显示图片
				if(categoryid == 0){
					if(!TextUtils.isEmpty(btn.imgurl)){
						product_img.setImageUrl(MassVigUtil.GetImageUrl(btn.imgurl, MassVigUtil.dip2px(SpecActivity.this, imgsize), MassVigUtil.dip2px(SpecActivity.this, imgsize)), MassVigContants.PATH, null);
					}
				}
				break;
			}
		}
		boolean sel = false;
		for (int i = 0; i < tempButtons.size(); i++) {
			MyButton btn = tempButtons.get(i);
			if(btn.categoryID == categoryid){
//				btn.selected = false;
				if(btn.enable){
					if(btn.selected){
						btn.selected = false;
						text.setTextColor(R.color.color_black_text);
						text.setBackgroundResource(R.drawable.bg_unselected);
//						btn.view.setTextColor(R.color.color_black_text);
//						btn.view.setBackgroundResource(R.drawable.bg_unselected);
					}
					else if(!btn.selected && btn.view==text)
					{
						btn.selected=true;
						sel = true;
						text.setTextColor(R.color.color_white);
						text.setBackgroundResource(R.drawable.bg_selected);
//						btn.view.setTextColor(R.color.color_white);
//						btn.view.setBackgroundResource(R.drawable.bg_selected);
					}else if(!btn.selected && btn.view!=text)
					{
						btn.selected=false;
						text.setTextColor(R.color.color_black_text);
						text.setBackgroundResource(R.drawable.bg_unselected);
//						btn.view.setTextColor(R.color.color_black_text);
//						btn.view.setBackgroundResource(R.drawable.bg_unselected);
					}
					
				}
			}
		}
		
		if(sel){
			text.setTextColor(R.color.color_white);
			text.setBackgroundResource(R.drawable.bg_selected);
			String tag = String.valueOf(text.getTag());
			mManager.setSelected(categoryid, Integer.valueOf(tag));
		}else{
			text.setTextColor(R.color.color_black_text);
			text.setBackgroundResource(R.drawable.bg_unselected);
			mManager.setSelected(categoryid, -1);
		}
		refreshViews(categoryid);
		CalculatePrice();
	}

	private void CalculatePrice() {
		String number = count.getText().toString();
		if(!TextUtils.isEmpty(number)){
			mManager.setCount(Integer.valueOf(number));
		}else{
			mManager.setCount(0);
		}
		total.setText(getString(R.string.total, mManager.getVolumn() + ""));
		money.setText(getString(R.string.money) + mManager.getMoney());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.confirm:
			if(mManager.getCount() > mManager.getVolumn()){
				Toast.makeText(this, getString(R.string.volumn_not_enough), Toast.LENGTH_SHORT).show();
				return;
			}
			if(TextUtils.isEmpty(app.user.sessionID)){
				startActivity(new Intent(this, LoginActivity.class));
				return;
			}
			if(mManager.getMoney() <= 0 || mManager.getProductSpecID() <= 0){
				Toast.makeText(this, getString(R.string.toselect), Toast.LENGTH_SHORT).show();
				return;
			}
			Product p = new Product();
			p.id = goodsdetail.productID;
			p.name = goodsdetail.name;
			p.imageUrl = goodsdetail.imageUrl;
			p.totalMoney = mManager.getMoney();
			p.count = mManager.getCount();
			p.signalPrice = p.totalMoney / p.count;
			p.selectedProperties = mManager.getSelectedPropertiesString();
			p.productSpecID = mManager.getProductSpecID();
			ProductList pl = new ProductList();
			pl.addProduct(p);
			startActivity(new Intent(this, OrderConfirmActivity.class).putExtra("Product", pl));
			SpecActivity.this.finish();
			break;
		case R.id.add:
			if(mManager.getCount() > mManager.getVolumn()){
				Toast.makeText(this, getString(R.string.volumn_not_enough), Toast.LENGTH_SHORT).show();
				return;
			}
			if(TextUtils.isEmpty(app.user.sessionID)){
				startActivity(new Intent(this, LoginActivity.class));
				return;
			}
			if(mManager.getMoney() <= 0 || mManager.getProductSpecID() <= 0){
				Toast.makeText(this, getString(R.string.toselect), Toast.LENGTH_SHORT).show();
				return;
			}
			new AddToShoppingCarAsync().execute();
			break;
		case R.id.back:
			SpecActivity.this.finish();
			break;

		default:
			break;
		}
	}
	
	class AddToShoppingCarAsync extends AsyncTask<Void, Void, Integer>{

		@Override
		protected void onPreExecute() {
			if(dialog != null && !dialog.isShowing())
				dialog.show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Integer result) {
			if(dialog != null && dialog.isShowing())
				dialog.dismiss();
			//TODO
			if(result == 0){
				SpecActivity.this.finish();
			}else if(result == MassVigContants.SESSIONVAILED){
				startActivity(new Intent(SpecActivity.this,LoginActivity.class));
			}else{
				Toast.makeText(SpecActivity.this, "", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}

		@Override
		protected Integer doInBackground(Void... params) {
			//TODO

			String result = MassVigService.getInstance().AddProduct(app.user.sessionID, mManager.getProductSpecID() + "", mManager.getCount() + "");

			try {
				JSONObject object = new JSONObject(result);
				int resultCode = object.getInt("ResponseStatus");
				// String message = object.getString("ResponseMsg");
				return resultCode;
			} catch (Exception e) {
				return -1;
			}
		}
		
	}

}
