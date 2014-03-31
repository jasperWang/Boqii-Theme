package com.massvig.ecommerce.entities;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

import com.massvig.ecommerce.service.MassVigService;

public class MyOrderList {

	private ArrayList<MyOrder> myorderList;
	
	public MyOrderList(){
		myorderList = new ArrayList<MyOrder>();
	}
	
	/**
	 * 清空列表
	 */
	public void clearMyOrderList(){
		if(myorderList !=null && myorderList.size() > 0){
			myorderList.clear();
		}
	}
	/**
	 * 删除一个MyOrder
	 * @param position
	 */
	public void deleteMyOrder(int position){
		if(position < myorderList.size())
			myorderList.remove(position);
	}
	
	/**
	 * 增加一个MyOrder
	 * @param MyOrder
	 */
	public void addMyOrder(MyOrder myorder){
		myorderList.add(myorder);
	}
	
	/**
	 * 增加一个myorderList
	 * @param list
	 */
	public void addMyOrderList(MyOrderList list){
		myorderList.addAll(list.getMyOrderList());
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<MyOrder> getMyOrderList() {
		// TODO Auto-generated method stub
		return myorderList;
	}

	/**
	 * 获取列表长度
	 * @return
	 */
	public int getCount(){
		if(myorderList != null){
			return myorderList.size();
		}else{
			return 0;
		}
	}
	
	/**
	 * 获取指定myorder
	 * @param position
	 * @return
	 */
	public MyOrder getMyOrder(int position){
		if(myorderList.size() > position)
			return myorderList.get(position);
		else
			return new MyOrder();
	}
	
	/**
	 * 获取指定myorder的ID
	 * @param position
	 * @return
	 */
	public String getMyOrderID(int position){
		return getMyOrder(position).OrderID + "";
	}
	
	/**
	 * 获取MyOrder列表
	 * @param merchantID 商户id
	 * @param categoryID 分类id
	 * @return 
	 */
	public boolean FetchMyOrderList(String sessionid,int orderTab, int startindex){
		String result = MassVigService.getInstance().GetOrders(sessionid, orderTab, startindex, 10);
		try {
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
//			String message = object.getString("ResponseMsg");
			if(resultCode == 0){
				JSONArray list = object.getJSONArray("ResponseData");
				if(list != null && list.length() > 0){
					for(int i = 0; i < list.length(); i++){
						MyOrder myorder = new MyOrder();
						JSONObject data = list.getJSONObject(i);
						myorder = DataAnalyse(data);
						myorderList.add(myorder);
					}
				}
				return true;
			}else{
			}
		} catch (Exception e) {
		}
		return false;
	}
	
	/**
	 * 解析myorder，返回myorder对象
	 * @param data
	 * @return NoQmyorder
	 */
	private MyOrder DataAnalyse(JSONObject data){
		MyOrder o = new MyOrder();
		o.IsWebOrder = data.optBoolean("IsWebOrder");
		o.Address = data.optString("Address");
		o.BillTitle = data.optString("BillTitle");
		o.BillType = data.optInt("BillType");
		o.Consignee = data.optString("Consignee");
		o.CreateTime = data.optString("CreateTime");
		o.DiscountAmount = data.optInt("DiscountAmount");
		o.Email = data.optString("Email");
		o.FreightCharges = data.optInt("FreightCharges");
		o.Mobile = data.optString("Mobile");
		o.OrderCoupons = data.optString("OrderCoupons");
		o.OrderDetails = data.optString("OrderDetails");
		o.OrderID = data.optInt("OrderID");
		o.OrderNo = data.optString("OrderNo");
		o.OrderStatus = data.optInt("OrderCustomerStatus");
		o.LogisticsStatus = data.optInt("LogisticsStatus");
		o.PayStatus = data.optInt("PayStatus");
		o.PayAmount = (float) data.optDouble("PayAmount");
		o.PaymentMethod = data.optInt("PaymentMethod");
		o.ProductAmount = data.optInt("ProductAmount");
		o.RegionID = data.optInt("RegionID");
		o.ZipCode = data.optString("ZipCode");
		o.OrderAction = data.optString("OrderActions");
		o.LogisticsInfoList = data.optString("LogisticsInfoList");
		o.PromotionDiscount = data.optDouble("PromotionDiscount");
		o.DiscountAmount = data.optDouble("DiscountAmount");
		return o;
	}
}
