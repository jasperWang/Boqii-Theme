package com.massvig.ecommerce.managers;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.view.View;

import com.massvig.ecommerce.entities.MyOrderList;

public class OrdersManager {
	public static final int None = 0;//无显示
	public static final int ConfirmPay = 1;//确认支付
	public static final int ConfirmReceive = 2;//确认收货
	public static final int ApplyRefunding = 4;//申请退款
	public static final int ApplyReturn = 8;//申请退货
	public static final int CancelApplyRefunding = 5;
	public static final int CancelOrder = 6;

	public static final int Unpay = 1;//未支付
	public static final int WaitingMerchantDeliver = 2;//等待卖家发货
	public static final int Shipped = 3;//已发货
	public static final int Accomplished = 4;//已完成
	public static final int WaitingMerchantRefund = 5;//等待卖家退款
	public static final int WaitingCustomerReturn = 6;//等待买家退货
	public static final int Refunded = 7;//已退款
	public static final int Closed = 8;//已关闭
	public static final int Deleted = 9;//已删除

	private LoadListener listener;
	private int mFromX = 0;
	private int mToX = 0;
	private int moveWidth;
	private int maxWidth = 0;
	private int index = 0;
	private ArrayList<View> views = new ArrayList<View>();
	public MyOrderList unpayList = new MyOrderList();
	public MyOrderList paidList = new MyOrderList();
	public MyOrderList huodaoList = new MyOrderList();
	public MyOrderList refundList = new MyOrderList();
	public MyOrderList unpaytempList = new MyOrderList();
	public MyOrderList paidtempList = new MyOrderList();
	public MyOrderList refundtempList = new MyOrderList();
	public MyOrderList huodaotempList = new MyOrderList();
	private boolean unpay_isLoadDone = false;
	private boolean paid_isLoadDone = false;
	private boolean huodao_isLoadDone = false;
	private boolean refund_isLoadDone = false;
	public boolean isLoading = false;
	public static final int UNPAYORDER = 1;
	public static final int PAIDORDER = 2;
	public static final int HUODAOORDER = 3;
	public static final int REFUNDORDER = 4;

	public static final int REFRESH = 5;
	public static final int MORE = 6;
	public boolean ismore = false;
	
	public void refreshUnpayList(){
		this.unpayList.addMyOrderList(unpaytempList);
		unpaytempList.clearMyOrderList();
	}
	
	public void refreshPaidList(){
		this.paidList.addMyOrderList(paidtempList);
		paidtempList.clearMyOrderList();
	}
	
	public void refreshHuodaoList(){
		this.huodaoList.addMyOrderList(huodaotempList);
		huodaotempList.clearMyOrderList();
	}
	
	public void refreshRefundList(){
		this.refundList.addMyOrderList(refundtempList);
		refundtempList.clearMyOrderList();
	}
	
	public void setListener(LoadListener l){
		this.listener = l;
	}
	
	public ArrayList<View> getViews() {
		return views;
	}

	public void setViews(ArrayList<View> views) {
		this.views = views;
	}

	public int getmFromX() {
		return mFromX;
	}

	public int getmToX() {
		return mToX;
	}

	public int getMoveWidth() {
		return moveWidth;
	}

	public void setMoveWidth(int moveWidth) {
		this.moveWidth = moveWidth;
	}

	public int getIndex() {
		return index;
	}

	public int getMaxWidth() {
		return maxWidth;
	}

	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	public void setIndex(int index) {
		mFromX = mToX;
		this.index = index;
		mToX = index * moveWidth;
	}
	
	public void LoadData(String sessionid,int orderTab, int type){
		switch (orderTab) {
		case UNPAYORDER:
			if(!unpay_isLoadDone)
				new GetOrdersAsync(orderTab, type).execute(sessionid);
			break;
		case PAIDORDER:
			if(!paid_isLoadDone)
				new GetOrdersAsync(orderTab, type).execute(sessionid);
			break;
		case HUODAOORDER:
			if(!huodao_isLoadDone)
				new GetOrdersAsync(orderTab, type).execute(sessionid);
			break;
		case REFUNDORDER:
			if(!refund_isLoadDone)
				new GetOrdersAsync(orderTab, type).execute(sessionid);
			break;
		default:
			break;
		}
	}

	private class GetOrdersAsync extends AsyncTask<String, Void, Boolean>{

		private int orderTab;
		private int type;
		public GetOrdersAsync(int orderTab, int type){
			this.orderTab = orderTab;
			this.type = type;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			isLoading = true;
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			int count = 0;
			boolean result = false;
			switch (orderTab) {
			case UNPAYORDER:
				count = unpayList.getCount();
				if(type == REFRESH){
					result = unpaytempList.FetchMyOrderList(params[0], orderTab, 0);
				}else
					result = unpaytempList.FetchMyOrderList(params[0], orderTab, count);
				if(unpaytempList.getCount() == 0)
					unpay_isLoadDone = true;
				break;
			case PAIDORDER:
				count = paidList.getCount();
				if(type == REFRESH){
					result = paidtempList.FetchMyOrderList(params[0], orderTab, 0);
				}else
					result = paidtempList.FetchMyOrderList(params[0], orderTab, count);
				if(paidtempList.getCount() == 0)
					paid_isLoadDone = true;
				break;
			case HUODAOORDER:
				count = huodaoList.getCount();
				if(type == REFRESH){
					result = huodaotempList.FetchMyOrderList(params[0], orderTab, 0);
				}else
					result = huodaotempList.FetchMyOrderList(params[0], orderTab, count);
				if(huodaotempList.getCount() == 0)
					huodao_isLoadDone = true;
				break;
			case REFUNDORDER:
				count = refundList.getCount();
				if(type == REFRESH){
					result = refundtempList.FetchMyOrderList(params[0], orderTab, 0);
				}else
					result = refundtempList.FetchMyOrderList(params[0], orderTab, count);
				if(refundtempList.getCount() == 0)
					refund_isLoadDone = true;
				break;
			default:
				break;
			}
			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			isLoading = false;
			if(result){
				if(type == REFRESH){
					clearData(orderTab);
					listener.LoadSuccess(orderTab);
				}else
					listener.LoadSuccess(orderTab);
			}else{
				listener.LoadFailed(orderTab);
			}
		}
		
	}
	
	public void clearAll(){
		clearUnpayData();
		clearPaidData();
		clearHuodaoData();
		clearRefundData();
	};
	
	public void clearData(int orderTab){
		switch (orderTab) {
		case 1:
			clearUnpayData();
			break;
		case 2:
			clearPaidData();
			break;
		case 3:
			clearHuodaoData();
			break;
		case 4:
			clearRefundData();
		default:
			break;
		}
	}
	
	public void clearUnpayData(){
		unpay_isLoadDone = false;
		this.unpayList.clearMyOrderList();
	}
	
	public void clearPaidData(){
		paid_isLoadDone = false;
		this.paidList.clearMyOrderList();
	}
	
	public void clearHuodaoData(){
		huodao_isLoadDone = false;
		this.huodaoList.clearMyOrderList();
	}
	
	public void clearRefundData(){
		refund_isLoadDone = false;
		this.refundList.clearMyOrderList();
	}
	
	public interface LoadListener{
		void LoadSuccess(int index);
		void SessionFailed();
		void LoadFailed(int index);
	}
	
}
