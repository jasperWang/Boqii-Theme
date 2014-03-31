package com.massvig.ecommerce.activities;

import java.util.HashMap;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.ProductList;
import com.massvig.ecommerce.managers.CouponManager;
import com.massvig.ecommerce.managers.CouponManager.Listener;
import com.massvig.ecommerce.utilities.MassVigUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CouponActivity extends BaseActivity implements OnClickListener, Listener {

	private LinearLayout couponLayout;
	private LayoutInflater mInflater;
	private CouponManager manager;
	private TextView clickView, info_text;
	private int count = 1;
	private ProductList product = new ProductList();
	private BaseApplication app;
	private String ids = "", quantities = "";
	float totalMoney = 0;
	
	private String payment = "", billTitle = "";
	private int customerAddressID, billType, expressage;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coupon);
		setTitle(getString(R.string.coupon_ac));
		app = (BaseApplication) getApplication();
		payment = this.getIntent().getStringExtra("PAYMENT");
		billType = this.getIntent().getIntExtra("BILLTYPE", -1);
		billTitle = this.getIntent().getStringExtra("BILLTITLE");
		customerAddressID = this.getIntent().getIntExtra("CUSTOMERADDRESSID", -1);
		expressage = this.getIntent().getIntExtra("EXPRESSAGE", -1);
		product = (ProductList)this.getIntent().getSerializableExtra("Product");
		if(product != null && product.getCount() > 0){
			for (int i = 0; i < product.getCount(); i++) {
				if (!product.getProduct(i).IsGift) {
					ids += product.getProduct(i).productSpecID;
					quantities += product.getProduct(i).count;
					totalMoney += product.getProduct(i).totalMoney;
					ids += ",";
					quantities += ",";
				}
			}
			if(!TextUtils.isEmpty(ids)){
				ids = ids.substring(0, ids.length() - 1);
				quantities = quantities.substring(0, quantities.length() - 1);
			}
		}
		manager = new CouponManager();
		manager.setListener(this);
		mInflater = LayoutInflater.from(this);
		((Button) findViewById(R.id.back)).setOnClickListener(this);
		((Button) findViewById(R.id.finish)).setOnClickListener(this);
		initView();
		app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(this, "SESSIONID", "") : app.user.sessionID;
	}

	private void initView() {
		info_text = (TextView)findViewById(R.id.info_text);
		couponLayout = (LinearLayout)findViewById(R.id.coupon_layout);
		initCoupons();
		refreshInfoText();
		addCouponToLayout();
	}

	private void initCoupons() {
		String coups = this.getIntent().getStringExtra("coupons");
		String coupsP = this.getIntent().getStringExtra("couponsPrice");
		if (!TextUtils.isEmpty(coups)) {
			String[] coupons = coups.split(",");
			String[] couponsPrice = coupsP.split(",");
			HashMap<String,Float> map = new HashMap<String, Float>();
			for (int i = 0; i < coupons.length; i++) {
				map.put(coupons[i].toUpperCase(), Float.valueOf(couponsPrice[i]));
				View v = mInflater.inflate(R.layout.coupon_item, null);
				TextView click = (TextView) v.findViewById(R.id.click_btn);
				click.setText(getString(R.string.cancel));
				click.setTextColor(R.color.color_cancel);
				TextView couponName = (TextView) v
						.findViewById(R.id.coupon_name);
				final EditText content = (EditText) v
						.findViewById(R.id.coupon_content);
				content.setText(coupons[i]);
				MyTag tag = new MyTag();
				tag.isUsed = true;
				click.setTag(tag);
				click.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						clickView = (TextView) v;
						MyTag tag = (MyTag) v.getTag();
						if (!tag.isUsed) {// 没使用过，可使用
							if (!TextUtils
									.isEmpty(content.getText().toString())) {
								checkCoupon();
							}else{
								Toast.makeText(CouponActivity.this, getString(R.string.coupon_empty), Toast.LENGTH_SHORT).show();
							}
						} else {// 已使用，可取消
							if (clickView != null) {
								View v1 = (View) clickView.getParent()
										.getParent();
								deleteCouponFromLayout(v1);
								EditText edit = (EditText) v1.findViewById(R.id.coupon_content);
								manager.map.remove(edit.getText().toString().toUpperCase());
								refreshInfoText();
							}
						}
					}
				});
				couponLayout.addView(v, 0);
				couponName.setText(getString(R.string.coupon_name, count++));
			}
			manager.map = map;
		}
	}

	private void deleteCouponFromLayout(View v) {
		couponLayout.removeView(v);
	}
	
	private void checkCoupon(){
		manager.coupon.content = getCoupons();
		manager.CheckConpon(app.user.sessionID, customerAddressID, payment, billType, billTitle, ids, quantities, expressage);
	}
	
	private void addCouponToLayout() {
		View v = mInflater.inflate(R.layout.coupon_item, null);
		TextView click = (TextView)v.findViewById(R.id.click_btn);
		TextView couponName = (TextView)v.findViewById(R.id.coupon_name);
		final EditText content = (EditText) v.findViewById(R.id.coupon_content);
		MyTag tag = new MyTag();
		tag.isUsed = false;
		click.setTag(tag);
		click.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				clickView = (TextView) v;
				MyTag tag = (MyTag) v.getTag();
				if(!tag.isUsed){//没使用过，可使用
					if (!TextUtils
							.isEmpty(content.getText().toString())) {
						checkCoupon();
					}else{
						Toast.makeText(CouponActivity.this, getString(R.string.coupon_empty), Toast.LENGTH_SHORT).show();
					}
//					manager.CheckConpon(app.user.sessionID, ids, quantities);
				}else{//已使用，可取消
					if(clickView != null){
						View v1 = (View) clickView.getParent().getParent();
						deleteCouponFromLayout(v1);
						EditText edit = (EditText) v1.findViewById(R.id.coupon_content);
						manager.map.remove(edit.getText().toString().toUpperCase());
						refreshInfoText();
					}
				}
			}
		});
		couponLayout.addView(v, 0);
		couponName.setText(getString(R.string.coupon_name, count++));
	}
	
	public class MyTag{
		boolean isUsed = false;
		int index;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			CouponActivity.this.finish();
			break;
		case R.id.finish:
			Intent intent = new Intent();
			intent.putExtra("coupons", "");
			if(!TextUtils.isEmpty(getCoupons())){
				intent.putExtra("coupons", getCoupons().toUpperCase());
				intent.putExtra("couponsPrice", getCouponsPrice());
			}
			setResult(RESULT_OK, intent);
			CouponActivity.this.finish();
			break;

		default:
			break;
		}

	}
	
	private void refreshInfoText(){
		String text = getString(R.string.goods_price) + "<font color='#e5075b'>[" + getString(R.string.money) + totalMoney + "]</font>";
		HashMap<String, Float> map = manager.map;
//		String text = "商品金额" + "[￥" + product.totalMoney + "]";
		for (int i = 0; i < couponLayout.getChildCount(); i++) {
			View v = couponLayout.getChildAt(i);
			TextView name = (TextView)v.findViewById(R.id.coupon_name);
			EditText content = (EditText)v.findViewById(R.id.coupon_content);
			if(!TextUtils.isEmpty(content.getText().toString()) && !content.getText().toString().equals("null"))
				text +="-" + name.getText().toString().substring(0, name.getText().toString().length()-1) + "<font color='#6ac54a'>[" + getString(R.string.money) + map.get(content.getText().toString().toUpperCase()) + "]</font>";
		}
		info_text.setText(Html.fromHtml(text));
	}

	@Override
	public void Success() {
		if(clickView != null){
			MyTag t = new MyTag();
			t.isUsed = true;
			clickView.setTag(t);
			clickView.setText(getString(R.string.cancel));
			clickView.setTextColor(R.color.color_cancel);
		}
		refreshInfoText();
		addCouponToLayout();
	}

	@Override
	public void Failed(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	
	private String getCouponsPrice(){
		String result = "";
		HashMap<String, Float> map = manager.map;
		for(String key : map.keySet()){
			result += map.get(key) + ",";
		}
		if(!TextUtils.isEmpty(result))
			result = result.substring(0, result.length() - 1);
		return result;
	}
	
	private String getCoupons(){
		String result = "";
		for (int i = 0; i < couponLayout.getChildCount(); i++) {
			View v = couponLayout.getChildAt(i);
//			TextView click = (TextView)v.findViewById(R.id.click_btn);
//			MyTag tag = (MyTag) click.getTag();
//			if (tag.isUsed) {
				EditText edit = (EditText) v.findViewById(R.id.coupon_content);
				if(!TextUtils.isEmpty(edit.getText().toString()))
					result += edit.getText().toString() + ",";
//			}
		}
		if(!TextUtils.isEmpty(result))
			result = result.substring(0, result.length() - 1);
		return result;
	}

}
