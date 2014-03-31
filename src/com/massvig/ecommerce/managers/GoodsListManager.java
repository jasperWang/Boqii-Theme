package com.massvig.ecommerce.managers;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.massvig.ecommerce.entities.Goods;
import com.massvig.ecommerce.entities.GoodsList;
import com.massvig.ecommerce.service.MassVigService;

public class GoodsListManager {

	private LoadListener listener;
	private GoodsList goodsList;
	private GoodsList tempList;
	private boolean isLoading = false;
	private String mcids = "", minPrice = "0", maxPrice = "1000000000";
	private int orderby = 6;
	public boolean isLoadDone = false;
	public String keyWord = "";
	private boolean isFirst = true;
	public boolean isScroll = false;
	public static final int Price_ASC = 1, Price_DESC = 2, Volume_ASC = 3, Volume_DESC = 4, OnlineTime_ASC = 5, OnlineTime_DESC = 6, Rebate_ASC = 7, Rebate_DESC = 8, Comment_ASC=9, Comment_DESC=10;

	public GoodsListManager() {
		initData();
	}
	
	public void setListener(LoadListener l){
		this.listener = l;
	}

	private void initData() {
		goodsList = new GoodsList();
		tempList = new GoodsList();
	}

	public boolean isRefresh() {
		return isLoading;
	}

	public void setRefresh(boolean isRefresh) {
		this.isLoading = isRefresh;
	}

	public String getMcids() {
		return mcids;
	}

	public void setMcids(String mcids) {
		this.mcids = mcids;
	}

	public String getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(String minPrice) {
		this.minPrice = minPrice;
	}

	public String getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(String maxPrice) {
		this.maxPrice = maxPrice;
	}

	public int getOrderby() {
		return orderby;
	}

	public void setOrderby(int orderby) {
		this.orderby = orderby;
	}

	public GoodsList getGoodsList() {
		return goodsList;
	}

	public void setGoodsList(GoodsList goodsList) {
		this.goodsList = goodsList;
	}

	public GoodsList getTempList() {
		return tempList;
	}

	public void setTempList(GoodsList tempList) {
		this.tempList = tempList;
	}

	public Goods getGoods(int position) {
		return goodsList.getGoods(position);
	}

	public void refreshGoodsList() {
		this.goodsList.addGoodsList(this.tempList);
		this.tempList.clearGoodsList();
	}
	
	public void loadAdData(String params, String merchantid, String udid, String sessionid){
		if(!isLoadDone)
			new FetchAdGoodsListAsync().execute(params, goodsList.getCount() + "", merchantid, udid, sessionid);
	}
	
	public void loadData(){
		if(!isLoadDone)
			new fetchGoodsListAsync().execute(mcids, minPrice, maxPrice, goodsList.getCount() + "", orderby + "");
	}
	
	public void clearData(){
		this.goodsList.clearGoodsList();
	}

	/**
	 * 获取goods列表(不带关键字)
	 * @param startLoadIndex 从第几条数据开始
	 */
	private boolean FetchGoodsList(String mcids, String minprice, String maxprice, String startIndex, String orderby) {
		boolean result = tempList.FetchGoodsList(mcids, minprice, maxprice, startIndex, orderby, 10);
//		clearData();
		if(tempList.getCount() == 0){
			listener.NoData();
			isLoadDone = true;
		}
		return result;
	}
	
	/**
	 * 获取广告goods列表
	 * @param params
	 * @return
	 */
	private String FetchAdList(String params, int startIndex, String merchantid, String udid, String sessionid){
		String s = MassVigService.SERVER_URL + params;
		URL url;
		String result = "";
		try {
			url = new URL(s + "&startindex=" + startIndex + "&merchantid=" + merchantid + "&udid=" + udid + "&sessionid=" + sessionid);
			result = MassVigService.getInstance().HttpConnect(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取goods列表(带关键字)
	 * @param startLoadIndex 从第几条数据开始
	 */
	private boolean FetchGoodsListWithKey(String key, String mcids, String minprice, String maxprice, String startIndex, String orderby) {
		boolean result = tempList.SearchProduct(key, mcids, minprice, maxprice, startIndex, orderby);
		if(tempList.getCount() == 0){
			listener.NoData();
			isLoadDone = true;
		}
		return result;
	}
	
	/**
	 * 解析goods，返回goods对象
	 * @param data
	 * @return NoQgoods
	 */
	private Goods DataAnalyse(JSONObject data){
		Goods goods = new Goods();
		try {
			goods.productID = data.getInt("ProductID");
			goods.name = data.getString("Name");
			goods.minPrice = data.getDouble("MinPrice");
			goods.volume = data.getInt("Volume");
			goods.imageUrl = data.getString("MainImgUrl");
			goods.realPrice = data.getDouble("MinOriginPrice");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return goods;
	}
	
	private class FetchAdGoodsListAsync extends AsyncTask<String, Void, Boolean>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			isLoading = true;
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			Boolean result = false;
			String response = FetchAdList(params[0], Integer.valueOf(params[1]), params[2], params[3], params[4]);
			if(!TextUtils.isEmpty(response)){
				JSONObject object;
				try {
					object = new JSONObject(response);
					int resultCode = object.getInt("ResponseStatus");
//					String message = object.getString("ResponseMsg");
					if(resultCode == 0){
						JSONArray list = object.getJSONArray("ResponseData");
						if(list != null && list.length() > 0){
							isFirst = false;
							for(int i = 0; i < list.length(); i++){
								Goods goods = new Goods();
								JSONObject merchantData = list.getJSONObject(i);
								goods = DataAnalyse(merchantData);
								tempList.addGoods(goods);
							}
						}else{
							isLoadDone = true;
							if(isFirst)
								listener.NoData();
							isFirst = false;
						}
						return true;
					}else{
						listener.NoData();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			isLoading = false;
			if(result){
				listener.LoadSuccess();
			}else{
				listener.LoadFailed();
			}
		}
		
	}
	
	/**
	 * 异步获取goods列表
	 * @author DuJun
	 *
	 */
	private class fetchGoodsListAsync extends AsyncTask<String, Void, Boolean>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			isLoading = true;
		}

		@Override
		protected Boolean doInBackground(String... params) {
			Boolean result = false;
			if(TextUtils.isEmpty(keyWord)){
				result = FetchGoodsList(params[0], params[1], params[2], params[3], params[4]);
				if(isScroll)
					refreshGoodsList();
				else{
					clearData();
					refreshGoodsList();
				}
			}
			else{
				result = FetchGoodsListWithKey(keyWord, params[0], params[1], params[2], params[3], params[4]);
				if(isScroll)
					refreshGoodsList();
				else{
					clearData();
					refreshGoodsList();
				}
			}
			isScroll = false;
			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			isLoading = false;
			if(result){
				listener.LoadSuccess();
			}else{
				listener.LoadFailed();
			}
		}
	}
	public interface LoadListener{
		void LoadSuccess();
		void LoadFailed();
		void NoData();
	}
}
