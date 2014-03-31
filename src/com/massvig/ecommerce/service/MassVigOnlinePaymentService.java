package com.massvig.ecommerce.service;

import org.json.JSONException;
import org.json.JSONObject;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.activities.OrderConfirmActivity;
import com.massvig.ecommerce.activities.PayTypeActivity;
import com.massvig.ecommerce.sevice.purchase.AlixId;
import com.massvig.ecommerce.sevice.purchase.BaseHelper;
import com.massvig.ecommerce.sevice.purchase.MobileSecurePayHelper;
import com.massvig.ecommerce.sevice.purchase.MobileSecurePayer;
import com.massvig.ecommerce.sevice.purchase.ResultChecker;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.unionpay.upomp.yidatec.transactionmanage.SplashActivity;
/*
import com.unionpay.upomp.yidatec.transactionmanage.SplashActivity;*/
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

public class MassVigOnlinePaymentService {
	private PayListener listener;
	private Context context;
	
	public MassVigOnlinePaymentService(Context context, PayListener payListener){
		this.context = context;
		this.listener = payListener;
	}
	
	public void payUnion(String xml){
		if(TextUtils.isEmpty(xml))
			return;
		Intent intent = new Intent(this.context,SplashActivity.class);
		Bundle mBundle = new Bundle();
		mBundle.putString("xml", xml);
		mBundle.putString("sign",context.getString(R.string.unionkey));
		intent.putExtras(mBundle);
		context.startActivity(intent);
	}
	
	public void rePayUnion(String sessionid, int orderid, int paytype){
		String result = MassVigService.getInstance().DefrayOrder(sessionid, orderid, paytype);
		UnionDataAnaly(result);
	}
	
	public void rePayAli(String sessionid, int orderid, int paytype){
		String result = MassVigService.getInstance().DefrayOrder(sessionid, orderid, paytype);
		AliDataAnaly(result);
	}
	
	/**
	 * 已登录银联支付
	 * @param xml
	 */
	public void startLoginUnionPay(String sessionid,int customerAddressID,String coupons,String billType,String billTitle,String productSpecID,String quantity, int express){
		String result = MassVigService.getInstance().SubmitLoginOrder(sessionid, customerAddressID, coupons, PayTypeActivity.UNION_PAY, billType, billTitle, productSpecID, quantity, express);
		UnionDataAnaly(result);
	}
	
	private void UnionDataAnaly(String result) {
		String xml = "";
		if(!TextUtils.isEmpty(result)){
			try {
				JSONObject o = new JSONObject(result);
				if(o.getInt("ResponseStatus") == 0){
					JSONObject data = o.getJSONObject("ResponseData");
					xml = data.getString("PaymentMessage");
					int id = data.optInt("OrderID");
					listener.orderDoneSuccess(id, xml);
					return;
				}else if(o.getInt("ResponseStatus") == MassVigContants.SESSIONVAILED){
					listener.sessionFailed();
					return;
				}else if(o.getInt("ResponseStatus") == -6){
					String message = o.getString("ResponseMsg");
					listener.orderdoneFailed(message);
					return;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				listener.orderDoneFailed("");
				return;
			}
		}
		listener.orderDoneFailed("");
		return;
		
	}

	/**
	 * 未登录银联支付
	 * @param xml
	 */
	public void startUnLoginUnionPay(String customerName, String mobile, int regionID, String address, String zipCode, String email, String coupons, int payment, String billType, String billTitle, String productSpecID, String quantity){
		String result = MassVigService.getInstance().SubmitUnloginOrder(customerName, mobile, regionID, address, zipCode, email, coupons, payment, billType, billTitle, productSpecID, quantity);
		UnionDataAnaly(result);
	}
	 
	public void startAlipay(String info){
		if(TextUtils.isEmpty(info))
			return;
//		String info = "";
//		try {
//			JSONObject o = new JSONObject(result);
//			info = o.getString("ResponseData");
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
		// check to see if the MobileSecurePay is already installed.
		MobileSecurePayHelper mspHelper = new MobileSecurePayHelper(context);
		boolean isMobile_spExist = mspHelper.detectMobile_sp();
		if (!isMobile_spExist)
			return;

		// start pay for this order.
		try {
			// prepare the order info.

			// start the pay.
			MobileSecurePayer msp = new MobileSecurePayer();
			boolean bRet = msp.pay(info, mAliHandler, AlixId.RQF_PAY, (Activity)context);

			if (bRet) {
				// show the progress bar to indicate that we have started
				// paying.
				closeProgressForAlipay();
				mAlipayProgress = BaseHelper.showProgress(context, null,
						context.getString(R.string.paying), false, true);
			} else
				;
		} catch (Exception ex) {
			Toast.makeText(context,context.getString(R.string.remote_call_failed), Toast.LENGTH_LONG).show();
		}
		
	}
	
	/**
	 * 已登录支付宝支付
	 */
	public void StartLoginAliPay(String sessionid,int customerAddressID,String coupons,int paytype, String billType,String billTitle,String productSpecID,String quantity,int express) {
		String result = MassVigService.getInstance().SubmitLoginOrder(sessionid, customerAddressID, coupons, paytype, billType, billTitle, productSpecID, quantity, express);
		AliDataAnaly(result);
	}
	
	private void AliDataAnaly(String result) {
		String info = "";
		if(!TextUtils.isEmpty(result)){
			try {
				JSONObject o = new JSONObject(result);
				if(o.getInt("ResponseStatus") == 0){
					JSONObject data = o.getJSONObject("ResponseData");
					info = data.getString("PaymentMessage");
					int id = data.optInt("OrderID");
					listener.orderDoneSuccess(id, info);
					return;
				}else if(o.getInt("ResponseStatus") == MassVigContants.SESSIONVAILED){
					listener.sessionFailed();
					return;
				}else if(o.getInt("ResponseStatus") == -6){
					String message = o.getString("ResponseMsg");
					listener.orderdoneFailed(message);
					return;
				}else{
					return;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				listener.orderDoneFailed("");
				return;
			}
		}
		listener.orderDoneFailed("");
		return;		
	}

	/**
	 * 已登录支付宝支付
	 */
	public void StartUnLoginAliPay(String customerName, String mobile, int regionID, String address, String zipCode, String email, String coupons, int payment, String billType, String billTitle, String productSpecID, String quantity) {
		String result = MassVigService.getInstance().SubmitUnloginOrder(customerName, mobile, regionID, address, zipCode, email, coupons, payment, billType, billTitle, productSpecID, quantity);
		AliDataAnaly(result);
	}
	
	private ProgressDialog mAlipayProgress = null;

	void closeProgressForAlipay() {
		try {
			if (mAlipayProgress != null) {
				mAlipayProgress.dismiss();
				mAlipayProgress = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Handler mAliHandler = new Handler() {
		public void handleMessage(Message msg) {
			try {
				String strRet = (String) msg.obj;

				String memoDisplay = null;

				switch (msg.what) {
				case AlixId.RQF_PAY: {
					closeProgressForAlipay();

					try {
						String memo = "memo={";
						int imemoStart = strRet.indexOf("memo={");
						imemoStart += memo.length();
						int imemoEnd = strRet.indexOf("};result=");
						memo = strRet.substring(imemoStart, imemoEnd);
						memoDisplay = new String(memo);

						ResultChecker resultChecker = new ResultChecker(strRet);

						if (resultChecker.isPayOk()) { // alipay success
							listener.paySuccess();
						}

					} catch (Exception e) {
						e.printStackTrace();
						BaseHelper.showDialog(context,context.getString(R.string.tips), memoDisplay, R.drawable.infoicon);
					}
				}
					break;
				}

				super.handleMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	public interface PayListener{
		void paySuccess();
		void orderDoneSuccess(int id, String xml);
		void orderDoneFailed(String message);
		void orderdoneFailed(String message);
		void sessionFailed();
	}
}
