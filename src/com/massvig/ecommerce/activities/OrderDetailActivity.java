package com.massvig.ecommerce.activities;

import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.massvig.ecommerce.activities.OrderConfirmActivity.OrderPromotionAsync;
import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.Address;
import com.massvig.ecommerce.entities.Goods;
import com.massvig.ecommerce.entities.MyOrder;
import com.massvig.ecommerce.entities.Product;
import com.massvig.ecommerce.entities.ProductList;
import com.massvig.ecommerce.managers.OrderManager;
import com.massvig.ecommerce.managers.OrderManager.LoadListener;
import com.massvig.ecommerce.managers.OrdersManager;
import com.massvig.ecommerce.service.MassVigOnlinePaymentService;
import com.massvig.ecommerce.service.MassVigService;
import com.massvig.ecommerce.service.MassVigOnlinePaymentService.PayListener;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.utilities.Utility;
import com.massvig.ecommerce.widgets.OrderItemAdapter;
import com.massvig.ecommerce.widgets.OrderItemAdapter.ClickListener;
import com.unionpay.upomp.yidatec.controller.UPOMP;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class OrderDetailActivity extends BaseActivity implements OnClickListener,PayListener, LoadListener{

	private static final int REFUND = 1;
	private static final int PAYTYPE = 5;
	private static final int ORDERDONE = 8;
	private static final int ORDERFAILED = 9;
	private OrderManager manager;
	private String prices = "";
	private BaseApplication app;
	private TextView username, address, mobile, zipcode, coupon, paytype, info, realpay, saved;
//	private TextView name, detail, price;
	private ProgressDialog mDialog;
	private MassVigOnlinePaymentService pay;
	private TextView order_status;
	private boolean isUnionPaying = false;
	private static final int ALIWEB = 10;
	private ListView layout;
	private LayoutInflater mInflater;
	private OrderItemAdapter adapter;
	private ArrayList<Product> pList;
	float PromotionDiscount = 0, discount = 0;
	private String ids = "", quantities = "";
	private ScrollView scroll;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_detail);
		setTitle(getString(R.string.orderdetail));
		mDialog = new ProgressDialog(this);
		pay = new MassVigOnlinePaymentService(OrderDetailActivity.this, this);
		app = (BaseApplication) getApplication();
		manager = new OrderManager(this);
		manager.setListener(this);
		MyOrder o = (MyOrder) this.getIntent().getSerializableExtra("order");
		manager.freight = o.FreightCharges;
		PromotionDiscount = (float) o.PromotionDiscount;
		initData(o);
		initView(o);
		Address address = manager.address;
		initAddressLayout(address);
		SetInfoText();
		manager.GetPayType();
		handler.sendEmptyMessageDelayed(1, 200);
	}
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			scroll.scrollTo(0, 0);
		}};
		
	private void initData(MyOrder o) {
		discount = (float) o.DiscountAmount;
		manager.orderID = o.OrderID;
		manager.address.name = o.Consignee;
		manager.address.mobile = o.Mobile;
		manager.address.regionID = o.RegionID;
		manager.address.address = o.Address;
		manager.address.zipcode = o.ZipCode;
		manager.address.email = o.Email;
		manager.status = o.OrderStatus;
		manager.orderNO = o.OrderNo;
		manager.isWebOrder = o.IsWebOrder;
		manager.LogisticsInfoList = o.LogisticsInfoList;
		String coupons = o.OrderCoupons;
		if(!TextUtils.isEmpty(coupons)){
			try {
				JSONArray array = new JSONArray(coupons);
				String coups = "";
				for(int i =0;i<array.length();i++){
					JSONObject data = array.getJSONObject(i);
					coups += data.getString("CouponNo") + ",";
					prices += data.getInt("Discount") + ",";
				}
				if(!TextUtils.isEmpty(coups)){
					coups = coups.substring(0, coups.length() - 1);
					prices = prices.substring(0, prices.length() - 1);
				}
				manager.coupons = coups;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		manager.payType = o.PaymentMethod;
		manager.billType = o.BillType+"";
		manager.billText = o.BillTitle;
		String detail = o.OrderDetails;
		if(!TextUtils.isEmpty(detail)){
			JSONArray array;
			JSONObject data;
			try {
				array = new JSONArray(detail);
				for (int i = 0; i < array.length(); i++) {
					data = array.getJSONObject(i);
					Product p = new Product();
					p.OrderDetailID = data.optInt("OrderDetailID");
					p.OrderDetailStatus = data.optInt("OrderDetailStatus");
					p.count = data.getInt("Quantity");
					p.id = data.getInt("ProductID");
					p.imageUrl = data.getString("MainImgUrl");
					p.name = data.getString("ProductName");
					String specDesc = data.getString("ProductSpecDesc");
					if(!TextUtils.isEmpty(specDesc)){
						specDesc = specDesc.substring(0, specDesc.length() -1);
						String[] specs = specDesc.split(";");
						HashMap<String,String>  map = new HashMap<String, String>();
						for (int j = 0; j < specs.length; j++) {
							String[] key_values = specs[j].split(":");
							map.put(key_values[0], key_values[1]);
						}
						p.selectedProperties = map;
					}
					p.signalPrice = (float) data.optDouble("UnitPrice");
					p.totalMoney = (float) (p.count * data.getDouble("UnitPrice")); 
					manager.addProduct(p);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	private void initAddressLayout(Address addr) {
		username.setText(addr.name);
		String detail = "";
		if(!TextUtils.isEmpty(addr.shengshiqu)){
			detail += addr.shengshiqu.replace(",", " ");
		}
		detail += addr.address;
		manager.address = addr;
		address.setText(detail);
		if(!TextUtils.isEmpty(addr.mobile))
			mobile.setText(getString(R.string.mobile_text, addr.mobile));
		zipcode.setText(addr.zipcode);
		SetInfoText();
//		manager.CalculateFreight(app.user.sessionID);
	}
	
	String dataurl;
	private void initView(MyOrder o) {
		
		if(manager.isWebOrder){
			((LinearLayout)findViewById(R.id.web_address)).setVisibility(View.GONE);
			((TableRow)findViewById(R.id.select_paytype)).setVisibility(View.GONE);
			((LinearLayout)findViewById(R.id.web_info)).setVisibility(View.GONE);
		}
		
		mInflater = LayoutInflater.from(this);
		layout = (ListView)findViewById(R.id.layout);
		scroll = (ScrollView)findViewById(R.id.scroll);
		order_status = (TextView)findViewById(R.id.order_status);
		((TableRow)findViewById(R.id.LogisticsInfo)).setOnClickListener(this);
		if(!TextUtils.isEmpty(manager.LogisticsInfoList)){
			((TableRow)findViewById(R.id.LogisticsInfo)).setOnClickListener(this);
			try {
				JSONArray array = new JSONArray(manager.LogisticsInfoList);
				JSONObject obj = array.optJSONObject(0);
				if(obj != null)
				{
					dataurl = obj.optString("DataUrl");
					String text = obj.optString("LogisticsName") + "   " + obj.optString("LogisticsOrderNo");
					((TextView)findViewById(R.id.logistics_status)).setText(text);
				}else{
					((TableRow)findViewById(R.id.LogisticsInfo)).setVisibility(View.GONE);
					if(manager.isWebOrder){
						((TableLayout)findViewById(R.id.web_trans)).setVisibility(View.GONE);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			((TableRow)findViewById(R.id.LogisticsInfo)).setVisibility(View.GONE);
		}
		switch (manager.status) {
		case OrdersManager.Unpay:
			order_status.setText(getString(R.string.unpay_text));
			break;
		case OrdersManager.WaitingMerchantDeliver:
			order_status.setText(getString(R.string.waitingMerchantDeliver_text));
			break;
		case OrdersManager.Shipped:
			order_status.setText(getString(R.string.shipped_text));
			break;
		case OrdersManager.Accomplished:
			order_status.setText(getString(R.string.accomplished_text));
			break;
		case OrdersManager.WaitingMerchantRefund:
			order_status.setText(getString(R.string.waitingMerchantRefund_text));
			break;
		case OrdersManager.WaitingCustomerReturn:
			order_status.setText(getString(R.string.waitingCustomerReturn_text));
			break;
		case OrdersManager.Refunded:
			order_status.setText(getString(R.string.refunded_text));
			break;
		case OrdersManager.Closed:
			order_status.setText(getString(R.string.closed_text));
			break;
		case OrdersManager.Deleted:
			order_status.setText(getString(R.string.deleted_text));
			break;

		default:
			break;
		
		}
//		defaultText = (TextView)findViewById(R.id.defaultText);
//		addressLayout = (LinearLayout)findViewById(R.id.address_layout);
		username = (TextView)findViewById(R.id.username);
		address = (TextView)findViewById(R.id.address);
		mobile = (TextView)findViewById(R.id.mobile);
		zipcode = (TextView)findViewById(R.id.zipcode);
		coupon = (TextView)findViewById(R.id.coupon);
		if(!TextUtils.isEmpty(manager.coupons))
			coupon.setText(manager.coupons);
		else
			coupon.setText(getString(R.string.none));
		paytype = (TextView)findViewById(R.id.paytype);
		initPayText();

		
		pList = manager.getProducts();
		if(!o.IsWebOrder)
			adapter = new OrderItemAdapter(this, pList, false);
		else
			adapter = new OrderItemAdapter(this, pList, true);
		adapter.setListener(new ClickListener() {
			
			@Override
			public void clickImg(Goods good) {
				startActivity(new Intent(OrderDetailActivity.this, GoodsDetailActivity.class).putExtra("goods", good));
			}
		});
		layout.setAdapter(adapter);
		new Utility().setListViewHeightBasedOnChildren2(this, layout, adapter);
//		for (int i = 0; i < ps.size(); i++) {
//			Product p = ps.get(i);
//			View view = mInflater.inflate(R.layout.order_item_layout, null);
//			TextView name = (TextView)view.findViewById(R.id.name);
//			TextView detail = (TextView)view.findViewById(R.id.detail);
//			TextView price = (TextView)view.findViewById(R.id.price);
//			
//			name.setText(p.name);
//			HashMap<String, String> map = p.selectedProperties;
//			String detailText = "";
//			if (map != null && map.size() > 0) {
//				for (String key : map.keySet()) {
//					detailText += key + ":" + map.get(key) + "\n";
//				}
//			}
////			detailText += getString(R.string.number) + p.count;
//			detail.setText(detailText);
//			price.setText(getString(R.string.money) + p.totalMoney);
//			((NetImageView)findViewById(R.id.imageview)).setImageUrl(p.imageUrl, MassVigContants.PATH, null);
//			layout.addView(view);
//		}
		
		
		info = (TextView)findViewById(R.id.info);
		realpay = (TextView)findViewById(R.id.realpay);
		saved = (TextView)findViewById(R.id.saved);

//		defaultText.setOnClickListener(this);
//		addressLayout.setOnClickListener(this);
//		((TableRow)findViewById(R.id.use_coupon)).setOnClickListener(this);
//		((TableRow)findViewById(R.id.select_paytype)).setOnClickListener(this);
//		((TableRow)findViewById(R.id.need_bill)).setOnClickListener(this);
		((Button)findViewById(R.id.back)).setOnClickListener(this);
		((Button)findViewById(R.id.topay)).setVisibility(View.GONE);
		((Button)findViewById(R.id.comfirm)).setVisibility(View.GONE);
		((Button)findViewById(R.id.applyforrefund)).setVisibility(View.GONE);
		((Button)findViewById(R.id.CancelApplyRefunding)).setVisibility(View.GONE);
		((Button)findViewById(R.id.CancelOrder)).setVisibility(View.GONE);
		String orderAction = o.OrderAction;
		if(!TextUtils.isEmpty(orderAction)){
			try {
				JSONArray array = new JSONArray(orderAction);
				if (array.length() > 0) {
					for (int i = 0; i < array.length(); i++) {
						int action = array.getInt(i);
						switch (action) {
						case OrdersManager.ConfirmPay://确认支付
							((Button)findViewById(R.id.topay)).setVisibility(View.VISIBLE);
							break;
						case OrdersManager.ConfirmReceive://确认收货
							((Button)findViewById(R.id.comfirm)).setVisibility(View.VISIBLE);
							break;
						case OrdersManager.ApplyRefunding://申请退款
							((Button)findViewById(R.id.applyforrefund)).setVisibility(View.VISIBLE);
							break;
						case OrdersManager.CancelApplyRefunding://撤销申请退款
							((Button)findViewById(R.id.CancelApplyRefunding)).setVisibility(View.VISIBLE);
							break;
						case OrdersManager.CancelOrder://取消订单
							((Button)findViewById(R.id.CancelOrder)).setVisibility(View.VISIBLE);
							break;

						default:
							break;
						}
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(((Button)findViewById(R.id.topay)).getVisibility() != View.VISIBLE){
			((TableRow)findViewById(R.id.select_paytype)).setEnabled(false);
		}
		((Button)findViewById(R.id.topay)).setOnClickListener(this);
		((Button)findViewById(R.id.comfirm)).setOnClickListener(this);
		((Button)findViewById(R.id.applyforrefund)).setOnClickListener(this);
		((Button)findViewById(R.id.CancelApplyRefunding)).setOnClickListener(this);
		((Button)findViewById(R.id.CancelOrder)).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			OrderDetailActivity.this.finish();
			break;
		case R.id.LogisticsInfo:
			startActivity(new Intent(this, LogisticsWebViewActivity.class).putExtra("URL", dataurl));
			break;
//		case R.id.address_layout:
//			//已登录 列表
//			//未登录 修改
//			if(!TextUtils.isEmpty(app.user.sessionID)){
//				if(manager.addressList.getCount() > 0){
////					startActivityForResult(new Intent(this, AddressDetailActivity.class).putExtra("address", manager.address.toString()), MODIFY);
//					startActivityForResult(new Intent(this, SelectAddressActivity.class).putExtra("quantity", manager.getProduct().count).putExtra("productSpecID", manager.getProduct().productSpecID).putExtra("addressList", manager.addressList).putExtra("FLAG", SelectAddressActivity.ORDER), SELECT);
//				}else{
//					startActivityForResult(new Intent(this, AddressDetailActivity.class), ADD);
//				}
//			}else{
//				startActivityForResult(new Intent(this, AddressDetailActivity.class).putExtra("address", manager.address.toString()), MODIFY);
//			}
//			break;
//		case R.id.defaultText:
//			//跳转新增地址
//			startActivityForResult(new Intent(this, AddressDetailActivity.class), ADD);
//			break;
//		case R.id.use_coupon:
//			startActivityForResult(new Intent(this, CouponActivity.class).putExtra("coupons", manager.coupons).putExtra("couponsPrice", prices).putExtra("Product", manager.getProduct()), COUPON);
//			break;
		case R.id.select_paytype:
			startActivityForResult(new Intent(this, PayTypeActivity.class).putExtra("PAYTYPE", manager.payType).putExtra("PAYMENT", manager.payment), PAYTYPE);
			break;
//		case R.id.need_bill:
//			startActivityForResult(new Intent(this, BillActivity.class).putExtra("BILL", manager.billText), BILL);
//			break;
		case R.id.topay:
			if (mDialog != null && !mDialog.isShowing()) {
				mDialog.setMessage(getString(R.string.paying));
				mDialog.show();
			}
			switch (manager.payType) {
			case PayTypeActivity.ALIPAY:
				new alipayAsync(pay).execute();
				break;
			case PayTypeActivity.ALIWEB:
				new aliwebpayAsync(pay).execute();
				break;
			case PayTypeActivity.HUODAO:
				new huodaoAsync().execute();
				break;
			case PayTypeActivity.UNION_PAY:
				isUnionPaying = true;
				new unionpayAsync(pay).execute();
				break;

			default:
				break;
			}
			break;
		case R.id.comfirm:
			app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(OrderDetailActivity.this, "SESSIONID", "") : app.user.sessionID;
			manager.ConfirmShiped(app.user.sessionID);
			break;
		case R.id.applyforrefund:
			ArrayList<Product> ps = manager.getProducts();
			ProductList list = new ProductList();
			for (int i = 0; i < ps.size(); i++) {
				list.addProduct(ps.get(i));
			}
			startActivityForResult(new Intent(this, RefundActivity.class).putExtra("Product", list).putExtra("REFUND", manager.freight).putExtra("ORDERNO", manager.orderNO).putExtra("ORDERID", manager.orderID + ""), REFUND);
			break;
//			ArrayList<Product> ps = manager.getProducts();
//			ProductList list = new ProductList();
//			for (int i = 0; i < ps.size(); i++) {
//				list.addProduct(ps.get(i));
//			}
//			startActivity(new Intent(this, RefundActivity.class).putExtra("Product", list).putExtra("ORDERID", manager.orderID + ""));
//			app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(OrderDetailActivity.this, "SESSIONID", "") : app.user.sessionID;
//			manager.ApplyForRefund(app.user.sessionID);
		case R.id.CancelApplyRefunding:
			break;
		case R.id.CancelOrder:
			app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(OrderDetailActivity.this, "SESSIONID", "") : app.user.sessionID;
			manager.CancelOrder(app.user.sessionID);
			break;
		default:
			break;
		}
	}
	
	private class huodaoAsync extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... params) {
			String result = "";
			app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(OrderDetailActivity.this, "SESSIONID", "") : app.user.sessionID;
			result = MassVigService.getInstance().DefrayOrder(app.user.sessionID, manager.orderID, manager.payType);
			return result;
				
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(!TextUtils.isEmpty(result)){
				try {
					JSONObject o = new JSONObject(result);
					if(o.getInt("ResponseStatus") == 0){
//						startActivity(new Intent(OrderDetailActivity.this, ManageOrderActivity.class).putExtra("orderTab", OrdersManager.PAIDORDER).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
						setResult(RESULT_OK);
						finish();
						return;
					}else if(o.getInt("ResponseStatus") == MassVigContants.SESSIONVAILED){
						GoToLogin();
						return;
					}else {
						JSONObject data = o.getJSONObject("ResponseData");
						String message = data.getString("ResponseMsg");
						Toast.makeText(OrderDetailActivity.this, message, Toast.LENGTH_SHORT).show();
						return;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
			}
		}
		
	}
	
	private class unionpayAsync extends AsyncTask<Void, Void, Void>{

		private MassVigOnlinePaymentService pay;
		public unionpayAsync(MassVigOnlinePaymentService pay){
			this.pay = pay;
		}
		@Override
		protected Void doInBackground(Void... params) {
			app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(OrderDetailActivity.this, "SESSIONID", "") : app.user.sessionID;
			pay.rePayUnion(app.user.sessionID, manager.orderID, PayTypeActivity.UNION_PAY);
			return null;
		}
	}
	
	private class alipayAsync extends AsyncTask<Void, Void, Void>{

		private MassVigOnlinePaymentService pay;
		public alipayAsync(MassVigOnlinePaymentService pay){
			this.pay = pay;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(OrderDetailActivity.this, "SESSIONID", "") : app.user.sessionID;
			pay.rePayAli(app.user.sessionID, manager.orderID, PayTypeActivity.ALIPAY);
			return null;
		}
		
	}
	
	private class aliwebpayAsync extends AsyncTask<Void, Void, Void>{

		private MassVigOnlinePaymentService pay;
		public aliwebpayAsync(MassVigOnlinePaymentService pay){
			this.pay = pay;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(OrderDetailActivity.this, "SESSIONID", "") : app.user.sessionID;
			pay.rePayAli(app.user.sessionID, manager.orderID, PayTypeActivity.ALIWEB);
			return null;
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			switch (requestCode) {
//			case COUPON:
//				manager.coupons = data.getStringExtra("coupons");
//				prices = data.getStringExtra("couponsPrice");
//				coupon.setText(manager.coupons);
//				SetInfoText();
//				break;
//			case MODIFY:
//			case ADD:
//				addressLayout.setVisibility(View.VISIBLE);
//				defaultText.setVisibility(View.INVISIBLE);
//				Address add = new Address();
//				String adds = data.getStringExtra("address");
//				add.StringToAddress(adds);
//				initAddressLayout(add);
//				break;
			case REFUND:
				setResult(RESULT_OK);
				finish();
				break;
			case PAYTYPE:
				manager.payType = data.getIntExtra("PAYTYPE", 2);
				initPayText();
//				new OrderPromotionAsync().execute(app.user.sessionID, manager.address.customerAddressID +"", manager.coupons, manager.payType + "", manager.billType, manager.billTitle, ids, quantities, manager.expressage + "");
				break;
			case ALIWEB:
//				startActivity(new Intent(this, ManageOrderActivity.class).putExtra("orderTab", OrdersManager.PAIDORDER).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
				setResult(RESULT_OK);
				finish();
				break;
//			case BILL:
//				manager.billText = data.getStringExtra("BILL");
//				manager.billType = data.getStringExtra("BILLTYPE");
//				bill.setText(manager.billText);
//				break;
//			case SELECT:
//				manager.addressList = (AddressList) data.getSerializableExtra("addressList");
//				initAddressLayout(manager.GetDefalultAddress());
//				break;
			default:
				break;
			}
		}
	}

//	@Override
//	public void Success(int index) {
//		switch (index) {
//		case OrderManager.FREIGHT:
//			carriage.setText(manager.freight + "");
//			SetInfoText();
//			break;
//		case OrderManager.GETADDRESS:
//			if(manager.addressList.getCount() > 0){
//				manager.address = manager.addressList.getAddress(0);
//				initAddressLayout(manager.GetDefalultAddress());
//				addressLayout.setVisibility(View.VISIBLE);
//				defaultText.setVisibility(View.INVISIBLE);
//			}else{
//				addressLayout.setVisibility(View.INVISIBLE);
//				defaultText.setVisibility(View.VISIBLE);
//			}
//			break;
//
//		default:
//			break;
//		}
//	}

	public void GoToLogin() {
//		if(!TextUtils.isEmpty(app.user.sessionID + MassVigUtil.getPreferenceData(this, "SESSIONID", "")))
			startActivity(new Intent(this, LoginActivity.class));
		Toast.makeText(this, getString(R.string.account_failed), Toast.LENGTH_SHORT).show();
		app.user.sessionID = "";
		MassVigUtil.setPreferenceStringData(OrderDetailActivity.this, "SESSIONID", "");
	}

	private void SetInfoText() {
		float totalMoney = 0;
		ArrayList<Product> ps = manager.getProducts();
		if(ps != null && ps.size() > 0){
			for (int i = 0; i < ps.size(); i++) {
				totalMoney += ps.get(i).totalMoney;
			}
		}
		String text = getString(R.string.goods_price) + "<font color='#f85c0f'>[" + getString(R.string.money) + totalMoney + "]</font>";
		float total = 0;
		text += "-" + getString(R.string.coupon) + "<font color='#6ac54a'>[" + getString(R.string.money) + discount + "]</font>";
//		if(!TextUtils.isEmpty(manager.coupons)){
//			String[] p = prices.split(",");
//			for (int i = 0; i < p.length; i++) {
//				total += Float.valueOf(p[i]);
//			}
//			text += "-" + getString(R.string.coupon) + "<font color='#6ac54a'>[" + getString(R.string.money) + total + "]</font>";
//		}
		text += "+" + getString(R.string.trans) + "<font color='#f85c0f'>[" + getString(R.string.money) + manager.freight + "]</font>";
		text += "-" + getString(R.string.discount) + "<font color='#6ac54a'>[" + getString(R.string.money) + PromotionDiscount + "]</font>";
		info.setText(Html.fromHtml(text));
		info.setText(Html.fromHtml(text));
		DecimalFormat df=new DecimalFormat("#.##");
		double d = totalMoney - total + manager.freight - PromotionDiscount - discount;
		String st=df.format(d);
		realpay.setText(getString(R.string.real_money, st));
		saved.setText(getString(R.string.saved, total));
	}

//	@Override
//	public void Failed(int index) {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public void paySuccess() {
//		startActivity(new Intent(this, ManageOrderActivity.class).putExtra("orderTab", OrdersManager.PAIDORDER).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		setResult(RESULT_OK);
		finish();
	}

	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ORDERDONE:
				if (mDialog != null && mDialog.isShowing()) {
					mDialog.dismiss();
				}
				switch (manager.payType) {
				case PayTypeActivity.ALIPAY:
					pay.startAlipay(String.valueOf(msg.obj));
					break;
				case PayTypeActivity.ALIWEB:
					startActivityForResult(new Intent(OrderDetailActivity.this, AliWebPayActivity.class).putExtra("URL", String.valueOf(msg.obj)), ALIWEB);
					break;
				case PayTypeActivity.HUODAO:
					
					break;
				case PayTypeActivity.UNION_PAY:
					pay.payUnion(String.valueOf(msg.obj));
					break;

				default:
					break;
				}
				break;
			case ORDERFAILED:
				String message = String.valueOf(msg.obj);
				if(!TextUtils.isEmpty(message)){
					Toast.makeText(OrderDetailActivity.this, message, Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(OrderDetailActivity.this, getString(R.string.order_fail), Toast.LENGTH_SHORT).show();
				}
				break;

			default:
				break;
			}
		}};

	@Override
	public void orderDoneSuccess(int id, String xml) {
		manager.orderID = id;
		Message msg = new Message();
		msg.what = ORDERDONE;
		msg.obj = xml;
		mHandler.sendMessage(msg);
	}

	@Override
	public void orderDoneFailed(String message) {
		Message msg = new Message();
		msg.what = ORDERFAILED;
		msg.obj = message;
		mHandler.sendMessage(msg);
	}

	@Override
	public void Success(int index) {
//		SetInfoText();
		switch (index) {
		case OrderManager.COMFIRM:
		case OrderManager.REFUND:
			OrderDetailActivity.this.finish();
			break;
		case OrderManager.CANCEL:
			OrderDetailActivity.this.finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void Failed(int index) {
		
	}

	@Override
	public void Payment(String result) {
//		if (!TextUtils.isEmpty(result) && !result.equals("null")) {
//			try {
//				JSONObject o = new JSONObject(result);
//				int res = o.getInt("ResponseStatus");
//				if (res == 0) {
//					JSONArray array = o.getJSONArray("ResponseData");
//					for (int i = 0; i < array.length(); i++) {
//						JSONObject data = array.getJSONObject(i);
//						int paytype = data.getInt("PaymentMethod");
//						boolean isDefault = data.getBoolean("IsDefault");
//						if(isDefault){
//							manager.payType = paytype;
//							initPayText();
//						}
//					}
//				}
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	}

	private void initPayText() {
		switch (manager.payType) {
		case PayTypeActivity.ALIPAY:
			paytype.setText(getString(R.string.alipay));
			break;
		case PayTypeActivity.UNION_PAY:
			paytype.setText(getString(R.string.unionpay));
			break;
		case PayTypeActivity.ALIWEB:
			paytype.setText(getString(R.string.aliwappay));
			break;
		case PayTypeActivity.HUODAO:
			paytype.setText(getString(R.string.huodao));
			((TableRow)findViewById(R.id.select_paytype)).setEnabled(false);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (isUnionPaying) {
			class UnionPayResultHandler implements ContentHandler {
				private String content;
				public String resultString;
				public void characters(char[] ch, int start, int length)
						throws SAXException {
					content = content.concat(new String(ch, start, length));
				}
				public void startDocument() throws SAXException {
					content = new String();
				}
				public void endDocument() throws SAXException {
				}
				public void startElement(String uri, String localName,
						String qName, Attributes atts) throws SAXException {
					content = new String();
				}
				public void endElement(String uri, String localName,
						String qName) throws SAXException {
					if (localName.equals("respCode")) {
						resultString = new String(content);
					} else {
					}
				}
				public void endPrefixMapping(String arg0) throws SAXException {
				}
				public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
						throws SAXException {
				}
				public void processingInstruction(String arg0, String arg1)
						throws SAXException {
				}
				public void setDocumentLocator(Locator arg0) {
				}
				public void skippedEntity(String arg0) throws SAXException {
				}
				public void startPrefixMapping(String arg0, String arg1)
						throws SAXException {
				}
			}
			;
			UnionPayResultHandler unionPayResultHandler = new UnionPayResultHandler();
			try {
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser parser = factory.newSAXParser();
				XMLReader xmlReader = parser.getXMLReader();
				String unionPayResult = UPOMP.getPayResult();
				xmlReader.setContentHandler(unionPayResultHandler);
				xmlReader.parse(new InputSource(
						new StringReader(unionPayResult)));
				xmlReader.setContentHandler(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (unionPayResultHandler.resultString != null
					&& unionPayResultHandler.resultString.compareTo("0000") == 0) {
//				startActivity(new Intent(this, ManageOrderActivity.class).putExtra("orderTab", OrdersManager.PAIDORDER).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
				setResult(RESULT_OK);
				finish();
			}
		}
		isUnionPaying = false;
	}
	
//	public class OrderPromotionAsync extends AsyncTask<String, Void, Integer>{
//
//		@Override
//		protected void onPostExecute(Integer result) {
////			if(mDialog != null && mDialog.isShowing())
////				mDialog.dismiss();
//			if(result == 0){
//				layout.setAdapter(adapter);
//				new Utility().setListViewHeightBasedOnChildren2(OrderDetailActivity.this, layout, adapter);
//				adapter.notifyDataSetChanged();
//				SetInfoText();
//			}else if(result == MassVigContants.SESSIONVAILED){
//				GotoLogin();
//			}else{
//				
//			}
//			super.onPostExecute(result);
//		}
//
//		@Override
//		protected void onPreExecute() {
////			if(mDialog != null && !mDialog.isShowing())
////				mDialog.show();
//			super.onPreExecute();
//		}
//
//		@Override
//		protected Integer doInBackground(String... params) {
//			String result = MassVigService.getInstance().OrderPromotion(params[0], Integer.valueOf(params[1]), params[2], params[3], params[4], params[5], params[6], params[7], Integer.valueOf(params[8]));
//			int status = -1;
//			if(!TextUtils.isEmpty(result)){
//				try {
//					JSONObject o = new JSONObject(result);
//					status = o.optInt("ResponseStatus");
//					if (status == 0) {
//						ProductList list = new ProductList();
//						JSONObject obj = o.optJSONObject("ResponseData");
//						JSONArray array = obj.optJSONArray("OrderDetails");
//						PromotionDiscount = obj.optInt("PromotionDiscount");
//						pList.clear();
//						for (int i = 0; i < array.length(); i++) {
//							JSONObject object = array.optJSONObject(i);
//							Product p = GiftAnalyse(object);
//							pList.add(p);
//						}
//					}
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//				
//			}
//			return status;
//		}
//		
//	}
//	
//	public Product GiftAnalyse(JSONObject data) {
//		Product p = new Product();
//		p.OrderDetailID = data.optInt("OrderDetailID");
//		p.id = data.optInt("ProductID");
//		p.name = data.optString("ProductName");
//		p.specInfo = data.optString("ProductSpecDesc");
//		p.imageUrl = data.optString("MainImgUrl");
//		p.count = data.optInt("Quantity");
//		p.signalPrice = (float) data.optDouble("UnitPrice");
//		p.OrderDetailStatus = data.optInt("OrderDetailStatus");
//		p.IsGift = data.optBoolean("IsGift");
//		p.isChecked = false;
//		return p;
//	}

	@Override
	public void SessionFailed() {
//		if(!TextUtils.isEmpty(app.user.sessionID + MassVigUtil.getPreferenceData(this, "SESSIONID", "")))
			startActivity(new Intent(this, LoginActivity.class));
		app.user.sessionID = "";
		MassVigUtil.setPreferenceStringData(this, "SESSIONID", "");
	}

	@Override
	public void sessionFailed() {
//		if(!TextUtils.isEmpty(app.user.sessionID + MassVigUtil.getPreferenceData(this, "SESSIONID", "")))
			startActivity(new Intent(this, LoginActivity.class));
		app.user.sessionID = "";
		MassVigUtil.setPreferenceStringData(this, "SESSIONID", "");
	}

	@Override
	public void orderdoneFailed(String message) {
		// TODO Auto-generated method stub
		if(mDialog != null && mDialog.isShowing())
			mDialog.dismiss();
		
	}

}
