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

import com.massvig.ecommerce.activities.ShoppingCarActivity.OrderPromotionAsync;
import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.Address;
import com.massvig.ecommerce.entities.AddressList;
import com.massvig.ecommerce.entities.Product;
import com.massvig.ecommerce.entities.ProductList;
import com.massvig.ecommerce.managers.OrderManager;
import com.massvig.ecommerce.managers.OrderManager.LoadListener;
import com.massvig.ecommerce.managers.OrdersManager;
import com.massvig.ecommerce.service.MassVigOnlinePaymentService;
import com.massvig.ecommerce.service.MassVigOnlinePaymentService.PayListener;
import com.massvig.ecommerce.service.MassVigService;
import com.massvig.ecommerce.sevice.purchase.MobileSecurePayHelper;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigUtil;
import com.massvig.ecommerce.utilities.Utility;
import com.massvig.ecommerce.widgets.OrderItemAdapter;
import com.unionpay.upomp.yidatec.controller.UPOMP;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class OrderConfirmActivity extends BaseActivity implements OnClickListener,LoadListener,PayListener{

	private static final int tiantian = 1;
	private static final int quanfeng = 2;
	private static final int ems = 3;
	private static final int jiaji = 4;
	private static final int huodao = 5;
	
	private static final int COUPON = 1;
	private static final int MODIFY = 2;
	private static final int ADD = 3;
	private static final int PAYTYPE = 5;
	private static final int BILL = 6;
	private static final int SELECT = 7;
	private static final int ORDERDONE = 8;
	private static final int ORDERFAILED = 9;
	private static final int ORDERFAILED1 = 10;
	private static final int LOGIN = 11;
	private OrderManager manager;
	private String prices = "";
	private BaseApplication app;
	private TextView username, address, mobile, zipcode, coupon, paytype, carriage, bill, name, detail, price, info, realpay, saved;
	private TextView defaultText, express;
	private LinearLayout addressLayout;
	private ProgressDialog mDialog;
	private MassVigOnlinePaymentService pay;
	private boolean isUnionPaying = false; 
	private static final int ALIWEB = 10;
//	private LinearLayout layout;
	private ListView layout;
	private LayoutInflater mInflater;
	private String ids = "", quantities = "";
	float totalMoney = 0, PromotionDiscount = 0;
	private boolean isHuodao = false;//上次选择的支付方式是否是货到付款
	
	private OrderItemAdapter adapter;
	private ArrayList<Product> pList;
	private ProductList pl = new ProductList();
	private ScrollView scroll;
	private boolean isDefault = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_confirm);
		setTitle(getString(R.string.orderconfirm));
		mDialog = new ProgressDialog(this);
		pay = new MassVigOnlinePaymentService(OrderConfirmActivity.this, this);
		app = (BaseApplication) getApplication();
		manager = new OrderManager(this);
		manager.setListener(this);
		pl = ((ProductList)this.getIntent().getSerializableExtra("Product"));
		PromotionDiscount = this.getIntent().getFloatExtra("PromotionDiscount", 0);
		for (int i = 0; i < pl.getCount(); i++) {
			manager.addProduct(pl.getProduct(i));
		}
		if(pl != null && pl.getCount() > 0){
			for (int i = 0; i < pl.getCount(); i++) {
				if (!pl.getProduct(i).IsGift) {
					ids += pl.getProduct(i).productSpecID;
					quantities += pl.getProduct(i).count;
					totalMoney += pl.getProduct(i).totalMoney;
					ids += ",";
					quantities += ",";
				}
			}
			if(!TextUtils.isEmpty(ids)){
				ids = ids.substring(0, ids.length() - 1);
				quantities = quantities.substring(0, quantities.length() - 1);
			}
		}
		initView();
		app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(OrderConfirmActivity.this, "SESSIONID", "") : app.user.sessionID;
		if(!TextUtils.isEmpty(app.user.sessionID)){//已登录
			manager.GetAddressList(app.user.sessionID);
		}else{//未登录
			Address address = manager.GetLocalAddress();
			if(address != null){//有地址，可以修改地址
				addressLayout.setVisibility(View.VISIBLE);
				defaultText.setVisibility(View.INVISIBLE);
				initAddressLayout(address);
			}else{//无地址，可以新增地址
				addressLayout.setVisibility(View.GONE);
				defaultText.setVisibility(View.VISIBLE);
//				((TableRow)findViewById(R.id.select_express)).setEnabled(false);
//				((TableRow)findViewById(R.id.select_paytype)).setEnabled(false);
			}
		}
		SetInfoText();
		manager.GetPayType();
//		initPayText();
		handler.sendEmptyMessageDelayed(1, 200);
	}
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			scroll.scrollTo(0, 0);
		}};
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		app = (BaseApplication) getApplication();
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
				startActivity(new Intent(this, ManageOrderActivity.class).putExtra("orderTab", OrdersManager.PAIDORDER));
				finish();
			}
		}
		isUnionPaying = false;
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
		app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(OrderConfirmActivity.this, "SESSIONID", "") : app.user.sessionID;
//		manager.CalculateFreight(app.user.sessionID);
		setExpress();
		new getExpressAsync().execute(app.user.sessionID, manager.address.regionID + "");
	}
	
	private void setExpress() {
		if(isHuodao){
			manager.payType = -1;
			paytype.setText(getString(R.string.select_paytype));
			((TextView)findViewById(R.id.jiantou)).setVisibility(View.VISIBLE);
			((TableRow)findViewById(R.id.select_paytype)).setEnabled(true);
		}
		isHuodao = false;
		manager.expressage = -1;
		carriage.setText("0");
		express.setText(getString(R.string.select_express));
	}

	class Expressage{
		int expressage;
		String name;
		boolean isDefault = false;
	}
	private ArrayList<Expressage> expressageList = new ArrayList<OrderConfirmActivity.Expressage>();
	private class getExpressAsync extends AsyncTask<String, Void, Integer>{

		@Override
		protected void onPreExecute() {
			if(mDialog != null && !mDialog.isShowing())
				mDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(String... params) {
			String result = MassVigService.getInstance().GetSpecificRegionExpress(params[0], params[1]);
			int status = -1;
			if(!TextUtils.isEmpty(result)){
				try {
					JSONObject o = new JSONObject(result);
					status = o.optInt("ResponseStatus");
					if(status == 0){
						expressageList.clear();
						JSONArray array = o.optJSONArray("ResponseData");
						for (int i = 0; i < array.length(); i++) {
							Expressage e = new Expressage();
							JSONObject obj = array.optJSONObject(i);
							e.expressage = obj.optInt("Expressage");
							e.name = obj.optString("ExpressageName");
							e.isDefault = obj.optBoolean("isDefault");
							expressageList.add(e);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return status;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if(mDialog != null && mDialog.isShowing())
				mDialog.dismiss();
			if(result == 0){
//				((TableRow)findViewById(R.id.select_express)).setEnabled(true);
//				((TableRow)findViewById(R.id.select_paytype)).setEnabled(false);
				for(int i = 0 ; i < expressageList.size(); i ++){
					if(expressageList.get(i).isDefault){
						manager.expressage = expressageList.get(i).expressage;
						express.setText(expressageList.get(i).name);
						
					}
				}
			}else if(result == MassVigContants.SESSIONVAILED){
				GoToLogin();
			}else if(result == -6){
				Toast.makeText(OrderConfirmActivity.this, getString(R.string.address_unsupport), Toast.LENGTH_SHORT).show();
			}else{
				
			}
			super.onPostExecute(result);
		}

	}
	
	private void initView() {
		mInflater = LayoutInflater.from(this);
		scroll = (ScrollView)findViewById(R.id.scroll);
		layout = (ListView)findViewById(R.id.layout);
		defaultText = (TextView)findViewById(R.id.defaultText);
		addressLayout = (LinearLayout)findViewById(R.id.address_layout);
		username = (TextView)findViewById(R.id.username);
		address = (TextView)findViewById(R.id.address);
		mobile = (TextView)findViewById(R.id.mobile);
		zipcode = (TextView)findViewById(R.id.zipcode);
		coupon = (TextView)findViewById(R.id.coupon);
		express = (TextView)findViewById(R.id.express);
		paytype = (TextView)findViewById(R.id.paytype);
		carriage = (TextView)findViewById(R.id.carriage);
		price = (TextView)findViewById(R.id.price);
		
		pList = manager.getProducts();
		adapter = new OrderItemAdapter(this, pList, false);
		layout.setAdapter(adapter);
		new Utility().setListViewHeightBasedOnChildren2(this, layout, adapter);
		info = (TextView)findViewById(R.id.info);
		realpay = (TextView)findViewById(R.id.realpay);
		saved = (TextView)findViewById(R.id.saved);

		defaultText.setOnClickListener(this);
		addressLayout.setOnClickListener(this);
		((LinearLayout)findViewById(R.id.use_coupon)).setOnClickListener(this);
		((TableRow)findViewById(R.id.select_paytype)).setOnClickListener(this);
		((TableRow)findViewById(R.id.need_bill)).setOnClickListener(this);
		((TableRow)findViewById(R.id.select_express)).setOnClickListener(this);
		((Button)findViewById(R.id.back)).setOnClickListener(this);
		((Button)findViewById(R.id.topay)).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			OrderConfirmActivity.this.finish();
			break;
		case R.id.address_layout:
			//已登录 列表
			//未登录 修改
			if(manager.orderID == 0){
			app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(OrderConfirmActivity.this, "SESSIONID", "") : app.user.sessionID;
			if(!TextUtils.isEmpty(app.user.sessionID)){
				if(manager.addressList.getCount() > 0){
//					startActivityForResult(new Intent(this, AddressDetailActivity.class).putExtra("address", manager.address.toString()), MODIFY);
					startActivityForResult(new Intent(this, SelectAddressActivity.class).putExtra("addressList", manager.addressList).putExtra("FLAG", SelectAddressActivity.ORDER), SELECT);
				}else{
					startActivityForResult(new Intent(this, AddressDetailActivity.class), ADD);
				}
			}else{
				startActivityForResult(new Intent(this, AddressDetailActivity.class).putExtra("address", manager.address.toString()), MODIFY);
			}
			}
			break;
		case R.id.defaultText:
			//跳转新增地址
			if(manager.orderID == 0){
			startActivityForResult(new Intent(this, AddressDetailActivity.class), ADD);
			}
			break;
		case R.id.use_coupon:
			if(manager.orderID == 0){
			if(manager.address.customerAddressID == -1){
				Toast.makeText(this, getString(R.string.select_address), Toast.LENGTH_SHORT).show();
				return;
			}
			if(manager.expressage == -1){
				Toast.makeText(this, getString(R.string.select_express), Toast.LENGTH_SHORT).show();
				return;
			}
			if(manager.payType == -1){
				Toast.makeText(this, getString(R.string.select_paytype), Toast.LENGTH_SHORT).show();
				return;
			}
			startActivityForResult(new Intent(this, CouponManageActivity.class)
			.putExtra("PAYMENT", manager.payType + "")
					.putExtra("CUSTOMERADDRESSID", manager.address.customerAddressID)
					.putExtra("EXPRESSAGE", manager.expressage)
					.putExtra("coupons", manager.coupons).putExtra("couponsPrice", prices).putExtra("Product", pl), COUPON);
//			startActivityForResult(new Intent(this, CouponActivity.class)
//					.putExtra("PAYMENT", manager.payType + "")
////					.putExtra("BILLTYPE", Integer.valueOf(manager.billType))
////					.putExtra("BILLTITLE", manager.billTitle)
//					.putExtra("CUSTOMERADDRESSID", manager.address.customerAddressID)
//					.putExtra("EXPRESSAGE", manager.expressage)
//					.putExtra("coupons", manager.coupons).putExtra("couponsPrice", prices).putExtra("Product", pl), COUPON);
			}
			break;
		case R.id.select_paytype:
			if(manager.orderID == 0){
			if(manager.address.customerAddressID == -1){
				Toast.makeText(this, getString(R.string.select_address), Toast.LENGTH_SHORT).show();
				return;
			}
			if(manager.expressage == -1){
				Toast.makeText(this, getString(R.string.select_express), Toast.LENGTH_SHORT).show();
				return;
			}
			startActivityForResult(new Intent(this, PayTypeActivity.class).putExtra("PAYTYPE", manager.payType).putExtra("PAYMENT", manager.payment), PAYTYPE);
			}
			break;
		case R.id.select_express:
			if(manager.orderID == 0){
			if(manager.address.customerAddressID == -1){
				Toast.makeText(this, getString(R.string.select_address), Toast.LENGTH_SHORT).show();
				return;
			}
			if(!expressageList.isEmpty()){
				String[] numbers = new String[expressageList.size()];
				for (int i = 0; i < numbers.length; i++) {
					numbers[i] = expressageList.get(i).name;
				}
				Builder builder = new AlertDialog.Builder(OrderConfirmActivity.this);
				builder.setTitle(getString(R.string.select_telephone))
						.setItems(numbers,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										manager.expressage = expressageList.get(which).expressage;
										express.setText(expressageList.get(which).name);
										if(manager.expressage == huodao){
											manager.payType = PayTypeActivity.HUODAO;
											paytype.setText(getString(R.string.huodao));
											((TextView)findViewById(R.id.jiantou)).setVisibility(View.INVISIBLE);
											((TableRow)findViewById(R.id.select_paytype)).setEnabled(false);
											manager.CalculateFreight(app.user.sessionID, ids, quantities);
											isHuodao = true;
										}else{
											manager.CalculateFreight(app.user.sessionID, ids, quantities);
											if(isHuodao){
												manager.payType = -1;
												paytype.setText(getString(R.string.select_paytype));
											}
											isHuodao = false;
											((TextView)findViewById(R.id.jiantou)).setVisibility(View.VISIBLE);
											((TableRow)findViewById(R.id.select_paytype)).setEnabled(true);
										}
										getOrderPromotion();
									}

								});
				AlertDialog d = builder.create();
				d.show();
			}
			}
			break;
		case R.id.need_bill:
			startActivityForResult(new Intent(this, BillActivity.class).putExtra("BILL", manager.billText), BILL);
			break;
		case R.id.topay:
			if(manager.address.customerAddressID == -1){
				Toast.makeText(this, getString(R.string.select_address), Toast.LENGTH_SHORT).show();
				return;
			}
			if(manager.expressage == -1){
				Toast.makeText(this, getString(R.string.select_express), Toast.LENGTH_SHORT).show();
				return;
			}
			if(manager.payType == -1){
				Toast.makeText(this, getString(R.string.select_paytype), Toast.LENGTH_SHORT).show();
				return;
			}
			if (mDialog != null && !mDialog.isShowing()) {
				mDialog.setMessage(getString(R.string.paying));
				mDialog.show();
			}
			
			if(manager.orderID != 0){
				switch (manager.payType) {
				case PayTypeActivity.ALIPAY:
					new realipayAsync(pay).execute();
					break;
				case PayTypeActivity.ALIWEB:
					new realiwebpayAsync(pay).execute();
					break;
				case PayTypeActivity.HUODAO:
					new rehuodaoAsync().execute();
					break;
				case PayTypeActivity.UNION_PAY:
					isUnionPaying = true;
					new reunionpayAsync(pay).execute();
					break;

				default:
					break;
				}
			}else{
				switch (manager.payType) {
				case PayTypeActivity.ALIPAY:
					new alipayAsync(pay).execute();
					break;
				case PayTypeActivity.ALIWEB:
					new alipayAsync(pay).execute();
					break;
				case PayTypeActivity.HUODAO:
					new huodaoAsync().execute();
					break;
//				case PayTypeActivity.UNION_PAY:
//					isUnionPaying = true;
//					new unionpayAsync(pay).execute();
//					break;

				default:
					break;
				}
			}
			break;
		default:
			break;
		}
	}
	
	private class rehuodaoAsync extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... params) {
			String result = "";
			app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(OrderConfirmActivity.this, "SESSIONID", "") : app.user.sessionID;
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
						Toast.makeText(OrderConfirmActivity.this, message, Toast.LENGTH_SHORT).show();
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
	
	private class reunionpayAsync extends AsyncTask<Void, Void, Void>{

		private MassVigOnlinePaymentService pay;
		public reunionpayAsync(MassVigOnlinePaymentService pay){
			this.pay = pay;
		}
		@Override
		protected Void doInBackground(Void... params) {
			app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(OrderConfirmActivity.this, "SESSIONID", "") : app.user.sessionID;
			pay.rePayUnion(app.user.sessionID, manager.orderID, PayTypeActivity.UNION_PAY);
			return null;
		}
	}
	
	private class realipayAsync extends AsyncTask<Void, Void, Void>{

		private MassVigOnlinePaymentService pay;
		public realipayAsync(MassVigOnlinePaymentService pay){
			this.pay = pay;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(OrderConfirmActivity.this, "SESSIONID", "") : app.user.sessionID;
			pay.rePayAli(app.user.sessionID, manager.orderID, PayTypeActivity.ALIPAY);
			return null;
		}
		
	}
	
	private class realiwebpayAsync extends AsyncTask<Void, Void, Void>{

		private MassVigOnlinePaymentService pay;
		public realiwebpayAsync(MassVigOnlinePaymentService pay){
			this.pay = pay;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(OrderConfirmActivity.this, "SESSIONID", "") : app.user.sessionID;
			pay.rePayAli(app.user.sessionID, manager.orderID, PayTypeActivity.ALIWEB);
			return null;
		}
		
	}
	
	private class huodaoAsync extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... params) {
			String result = "";
			String billTitle = "";
			if(!TextUtils.isEmpty(manager.billText) && !manager.billText.equals("null")){
				billTitle = manager.billText;
			}else{
				billTitle = getString(R.string.person);
			}
			app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(OrderConfirmActivity.this, "SESSIONID", "") : app.user.sessionID;
			if(!TextUtils.isEmpty(app.user.sessionID)){
				result = MassVigService.getInstance().SubmitLoginOrder(app.user.sessionID, manager.address.customerAddressID, manager.coupons, manager.payType, manager.billType, billTitle, ids, quantities, manager.expressage);
			}else{
				result = MassVigService.getInstance().SubmitUnloginOrder(manager.address.name, manager.address.mobile, manager.address.regionID, manager.address.address, manager.address.zipcode, manager.address.email, manager.coupons, manager.payType, manager.billType, billTitle, ids, quantities);
			}
			return result;
				
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(!TextUtils.isEmpty(result)){
				try {
					JSONObject o = new JSONObject(result);
					if(o.getInt("ResponseStatus") == 0){
						startActivity(new Intent(OrderConfirmActivity.this, ManageOrderActivity.class).putExtra("orderTab", OrdersManager.PAIDORDER));
						finish();
						return;
					}else if(o.getInt("ResponseStatus") == MassVigContants.SESSIONVAILED){
						if(mDialog != null && mDialog.isShowing())
							mDialog.dismiss();
						GoToLogin();
						return;
					}else if(o.getInt("ResponseStatus") == -6){
						if(mDialog != null && mDialog.isShowing())
							mDialog.dismiss();
						String message = o.getString("ResponseMsg");
						Toast.makeText(OrderConfirmActivity.this, message, Toast.LENGTH_SHORT).show();
						return;
					}else{
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
			String billTitle = "";
			if(!TextUtils.isEmpty(manager.billText) && !manager.billText.equals("null")){
				billTitle = manager.billText;
			}else{
				billTitle = getString(R.string.person);
			}
			app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(OrderConfirmActivity.this, "SESSIONID", "") : app.user.sessionID;
			if(!TextUtils.isEmpty(app.user.sessionID)){
				pay.startLoginUnionPay(app.user.sessionID, manager.address.customerAddressID, manager.coupons, manager.billType, billTitle, ids, quantities, manager.expressage);
			}else{
				pay.startUnLoginUnionPay(manager.address.name, manager.address.mobile, manager.address.regionID, manager.address.address, manager.address.zipcode, manager.address.email, manager.coupons, manager.payType, manager.billType, billTitle, ids, quantities);
			}
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
			app.user.sessionID = TextUtils.isEmpty(app.user.sessionID) ? MassVigUtil.getPreferenceData(OrderConfirmActivity.this, "SESSIONID", "") : app.user.sessionID;
			if(!TextUtils.isEmpty(app.user.sessionID)){
				String billTitle = "";
				if(!TextUtils.isEmpty(manager.billText) && !manager.billText.equals("null")){
					billTitle = manager.billText;
				}else{
					billTitle = getString(R.string.person);
				}
				pay.StartLoginAliPay(app.user.sessionID, manager.address.customerAddressID, manager.coupons, manager.payType, manager.billType, billTitle, ids, quantities, manager.expressage);
			}else{
				String billTitle = "";
				if(!TextUtils.isEmpty(manager.billText) && !manager.billText.equals("null")){
					billTitle = manager.billText;
				}else{
					billTitle = getString(R.string.person);
				}
				pay.StartUnLoginAliPay(manager.address.name, manager.address.mobile, manager.address.regionID, manager.address.address, manager.address.zipcode, manager.address.email, manager.coupons, manager.payType, manager.billType, billTitle, ids, quantities);
			}
			return null;
		}
		
	}
	
	public void getOrderPromotion(){
		new OrderPromotionAsync().execute(app.user.sessionID, manager.address.customerAddressID +"", manager.coupons, manager.payType + "", manager.billType, manager.billTitle, ids, quantities, manager.expressage + "");
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			switch (requestCode) {
			case ALIWEB:
				startActivity(new Intent(this, ManageOrderActivity.class).putExtra("orderTab", OrdersManager.PAIDORDER));
				finish();
				break;
			case COUPON:
				manager.coupons = data.getStringExtra("coupons");
				prices = data.getStringExtra("couponsPrice");
//				coupon.setText(manager.coupons);
				coupon.setText("无");
				if(!TextUtils.isEmpty(manager.coupons) && !manager.coupons.equals("null")){
					String[] s = manager.coupons.split(",");
					coupon.setText(getString(R.string.already_use, s.length + ""));
				}
				manager.CalculateFreight(app.user.sessionID, ids, quantities);
				getOrderPromotion();
//				new OrderPromotionAsync().execute(app.user.sessionID, manager.address.customerAddressID +"", manager.coupons, manager.payType + "", manager.billType, manager.billTitle, ids, quantities, manager.expressage + "");
				SetInfoText();
				break;
			case LOGIN:
				manager.GetAddressList(app.user.sessionID);
				break;
			case MODIFY:
			case ADD:
				int tempID = manager.address.customerAddressID;
				addressLayout.setVisibility(View.VISIBLE);
				defaultText.setVisibility(View.INVISIBLE);
				Address addr = new Address();
				String addss = data.getStringExtra("address");
				addr.StringToAddress(addss);
				if (tempID != addr.customerAddressID) {
					initAddressLayout(addr);
					manager.CalculateFreight(app.user.sessionID, ids,quantities);
				}
				if(requestCode == ADD){
					manager.addressList.addAddress(addr);
				}
				break;
			case PAYTYPE:
				manager.CalculateFreight(app.user.sessionID, ids, quantities);
				getOrderPromotion();
//				new OrderPromotionAsync().execute(app.user.sessionID, manager.address.customerAddressID +"", manager.coupons, manager.payType + "", manager.billType, manager.billTitle, ids, quantities, manager.expressage + "");
				initPayText();
				break;
			case BILL:
				manager.billText = data.getStringExtra("BILL");
				manager.billType = data.getStringExtra("BILLTYPE");
				bill.setText(manager.billText);
				break;
			case SELECT:
				manager.addressList = (AddressList) data.getSerializableExtra("addressList");
				initAddressLayout(manager.GetDefalultAddress());
				break;
			default:
				break;
			}
		}
	}

	public void GoToLogin() {
//		if(!TextUtils.isEmpty(app.user.sessionID + MassVigUtil.getPreferenceData(this, "SESSIONID", "")))
			startActivity(new Intent(this, LoginActivity.class));
		Toast.makeText(this, getString(R.string.account_failed), Toast.LENGTH_SHORT).show();
		app.user.sessionID = "";
		MassVigUtil.setPreferenceStringData(OrderConfirmActivity.this, "SESSIONID", "");
	}

	@Override
	public void Success(int index) {
		switch (index) {
		case OrderManager.FREIGHT:
			carriage.setText(manager.freight + "");
			SetInfoText();
			break;
		case OrderManager.GETADDRESS:
			if(manager.addressList.getCount() > 0){
				manager.address = manager.addressList.getAddress(0);
				initAddressLayout(manager.GetDefalultAddress());
				addressLayout.setVisibility(View.VISIBLE);
				defaultText.setVisibility(View.INVISIBLE);
			}else{
				addressLayout.setVisibility(View.GONE);
				defaultText.setVisibility(View.VISIBLE);
			}
			break;

		default:
			break;
		}
	}

	private void SetInfoText() {
		String text = getString(R.string.goods_price) + "<font color='#f85c0f'>[" + getString(R.string.money) + totalMoney + "]</font>";
		float total = 0;
		if(!TextUtils.isEmpty(manager.coupons)){
			String[] p = prices.split(",");
			for (int i = 0; i < p.length; i++) {
				if(!TextUtils.isEmpty(p[i]))
					try {
						total += Float.valueOf(p[i]);	
					} catch (Exception e) {
						// TODO: handle exception
					}
			}
			text += "-" + getString(R.string.coupon) + "<font color='#6ac54a'>[" + getString(R.string.money) + total + "]</font>";
		}
		text += "+" + getString(R.string.trans) + "<font color='#f85c0f'>[" + getString(R.string.money) + manager.freight + "]</font>";
		text += "-" + getString(R.string.discount) + "<font color='#6ac54a'>[" + getString(R.string.money) + PromotionDiscount + "]</font>";
		info.setText(Html.fromHtml(text));
		DecimalFormat df=new DecimalFormat("#.##");
		double d = totalMoney - total + manager.freight - PromotionDiscount;
		String st=df.format(d);
		realpay.setText(getString(R.string.real_money, st));
		double dd = total + PromotionDiscount;
		String s = df.format(dd);
		saved.setText(getString(R.string.saved, s));
	}

	@Override
	public void Failed(int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void paySuccess() {
		startActivity(new Intent(this, ManageOrderActivity.class).putExtra("orderTab", OrdersManager.PAIDORDER));
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
					startActivityForResult(new Intent(OrderConfirmActivity.this, AliWebPayActivity.class).putExtra("URL", String.valueOf(msg.obj)), ALIWEB);
					break;
				case PayTypeActivity.HUODAO:
					
					break;
//				case PayTypeActivity.UNION_PAY:
//					pay.payUnion(String.valueOf(msg.obj));
//					break;

				default:
					break;
				}
				break;
			case ORDERFAILED:
				if (mDialog != null && mDialog.isShowing()) {
					mDialog.dismiss();
				}
				String message = String.valueOf(msg.obj);
				if(!TextUtils.isEmpty(message)){
					Toast.makeText(OrderConfirmActivity.this, message, Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(OrderConfirmActivity.this, getString(R.string.order_fail), Toast.LENGTH_SHORT).show();
				}
				break;
			case ORDERFAILED1:
				if(mDialog != null && mDialog.isShowing())
					mDialog.dismiss();
				Toast.makeText(OrderConfirmActivity.this, String.valueOf(msg.obj), Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}};

	@Override
	public void orderDoneSuccess(int id ,String xml) {
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
	public void Payment(String result) {
		if (!TextUtils.isEmpty(result) && !result.equals("null")) {
			try {
				JSONObject o = new JSONObject(result);
				int res = o.getInt("ResponseStatus");
				if (res == 0) {
					JSONArray array = o.getJSONArray("ResponseData");
					MobileSecurePayHelper mspHelper = new MobileSecurePayHelper(OrderConfirmActivity.this);
					boolean isHave = false;
					for (int i = 0; i < array.length(); i++) {
						JSONObject data = array.getJSONObject(i);
						int paytype = data.getInt("PaymentMethod");
						if(paytype == PayTypeActivity.ALIWEB){
							isHave = true;
						}
					}
					for (int i = 0; i < array.length(); i++) {
						JSONObject data = array.getJSONObject(i);
						int paytype = data.getInt("PaymentMethod");
						boolean isDefault = data.getBoolean("IsDefault");
						if(isDefault){
							manager.payType = paytype;
							if(paytype == PayTypeActivity.ALIPAY && !mspHelper.isMobile_spExist() && isHave){
								manager.payType = PayTypeActivity.ALIWEB;
							}
							initPayText();
						}
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void initPayText() {
		switch (manager.payType) {
		case PayTypeActivity.ALIPAY:
			paytype.setText(getString(R.string.alipay));
			break;
//		case PayTypeActivity.UNION_PAY:
//			paytype.setText(getString(R.string.unionpay));
//			break;
		case PayTypeActivity.ALIWEB:
			paytype.setText(getString(R.string.aliwappay));
			break;
		case PayTypeActivity.HUODAO:
			paytype.setText(getString(R.string.huodao));
			break;

		default:
			break;
		}
	}
	
	public class OrderPromotionAsync extends AsyncTask<String, Void, Integer>{

		@Override
		protected void onPostExecute(Integer result) {
//			if(mDialog != null && mDialog.isShowing())
//				mDialog.dismiss();
			if(result == 0){
				layout.setAdapter(adapter);
				new Utility().setListViewHeightBasedOnChildren2(OrderConfirmActivity.this, layout, adapter);
				adapter.notifyDataSetChanged();
				SetInfoText();
			}else if(result == MassVigContants.SESSIONVAILED){
				GotoLogin();
			}else{
				
			}
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
//			if(mDialog != null && !mDialog.isShowing())
//				mDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(String... params) {
			String result = MassVigService.getInstance().OrderPromotion(params[0], Integer.valueOf(params[1]), params[2], params[3], params[4], params[5], params[6], params[7], Integer.valueOf(params[8]));
			int status = -1;
			if(!TextUtils.isEmpty(result)){
				try {
					JSONObject o = new JSONObject(result);
					status = o.optInt("ResponseStatus");
					if (status == 0) {
						ProductList list = new ProductList();
						JSONObject obj = o.optJSONObject("ResponseData");
						JSONArray array = obj.optJSONArray("OrderDetails");
						PromotionDiscount = obj.optInt("PromotionDiscount");
						if(pList == null)
							pList = new ArrayList<Product>();
						pList.clear();
						if(array != null)
						for (int i = 0; i < array.length(); i++) {
							JSONObject object = array.optJSONObject(i);
							Product p = GiftAnalyse(object);
							pList.add(p);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
			return status;
		}
		
	}
	
	public Product GiftAnalyse(JSONObject data) {
		Product p = new Product();
		p.OrderDetailID = data.optInt("OrderDetailID");
		p.id = data.optInt("ProductID");
		p.name = data.optString("ProductName");
		p.specInfo = data.optString("ProductSpecDesc");
		String text = p.specInfo;
		if(!TextUtils.isEmpty(text)){
			text = text.substring(0, text.length() -1);
			String[] specs = text.split(";");
			HashMap<String,String>  map = new HashMap<String, String>();
			for (int i = 0; i < specs.length; i++) {
				String[] key_values = specs[i].split(":");
				map.put(key_values[0], key_values[1]);
			}
			p.selectedProperties = map;
		}
		p.imageUrl = data.optString("MainImgUrl");
		p.count = data.optInt("Quantity");
		p.signalPrice = (float) data.optDouble("UnitPrice");
		p.OrderDetailStatus = data.optInt("OrderDetailStatus");
		p.IsGift = data.optBoolean("IsGift");
		p.isChecked = false;
		return p;
	}

	@Override
	public void SessionFailed() {
//		if(!TextUtils.isEmpty(app.user.sessionID + MassVigUtil.getPreferenceData(this, "SESSIONID", "")))
		startActivityForResult(new Intent(this, LoginActivity.class), LOGIN);
//			startActivity(new Intent(this, LoginActivity.class));
		app.user.sessionID = "";
		MassVigUtil.setPreferenceStringData(this, "SESSIONID", "");
		
	}

	@Override
	public void sessionFailed() {
//		if(!TextUtils.isEmpty(app.user.sessionID + MassVigUtil.getPreferenceData(this, "SESSIONID", "")))
		startActivityForResult(new Intent(this, LoginActivity.class), LOGIN);
//			startActivity(new Intent(this, LoginActivity.class));
		app.user.sessionID = "";
		MassVigUtil.setPreferenceStringData(this, "SESSIONID", "");
		
	}

	@Override
	public void orderdoneFailed(String message) {
		Message msg = new Message();
		msg.what = ORDERFAILED1;
		msg.obj = message;
		mHandler.sendMessage(msg);
	}

}
