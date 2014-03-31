package com.massvig.ecommerce.entities;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.massvig.ecommerce.service.MassVigService;

public class GoodsList {

	private ArrayList<Goods> goodsList;
	
	public GoodsList(){
		goodsList = new ArrayList<Goods>();
	}
	
	/**
	 * 清空列表
	 */
	public void clearGoodsList(){
		if(goodsList !=null && goodsList.size() > 0){
			goodsList.clear();
		}
	}
	/**
	 * 删除一个Goods
	 * @param position
	 */
	public void deleteGoods(int position){
		goodsList.remove(position);
	}
	
	/**
	 * 增加一个Goods
	 * @param Goods
	 */
	public void addGoods(Goods goods){
		goodsList.add(goods);
	}
	
	/**
	 * 增加一个goodsList
	 * @param list
	 */
	public void addGoodsList(GoodsList list){
		goodsList.addAll(list.getGoodsList());
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Goods> getGoodsList() {
		// TODO Auto-generated method stub
		return goodsList;
	}

	/**
	 * 获取列表长度
	 * @return
	 */
	public int getCount(){
		if(goodsList != null){
			return goodsList.size();
		}else{
			return 0;
		}
	}
	
	/**
	 * 获取指定goods
	 * @param position
	 * @return
	 */
	public Goods getGoods(int position){
		if(goodsList != null && position < goodsList.size())
			return goodsList.get(position);
		else
			return null;
	}
	
	/**
	 * 获取指定goods的ID
	 * @param position
	 * @return
	 */
	public String getGoodsID(int position){
		return getGoods(position).productID + "";
	}
	
	/**
	 * 获取Goods列表(没有关键字)
	 * @param merchantID 商户id
	 * @param categoryID 分类id
	 * @return 
	 */
	public boolean FetchGoodsList(String mcids, String minprice, String maxprice, String startIndex, String orderby, int takeNum){
		String result = MassVigService.getInstance().GetProducts(mcids, minprice, maxprice, startIndex, orderby, takeNum);
		try {
			if(!TextUtils.isEmpty(result) && !result.equals("null")){
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
//			String message = object.getString("ResponseMsg");
			if(resultCode == 0){
				JSONArray list = object.getJSONArray("ResponseData");
				if(list != null && list.length() > 0){
					for(int i = 0; i < list.length(); i++){
						Goods goods = new Goods();
						JSONObject merchantData = list.getJSONObject(i);
						goods = DataAnalyse(merchantData);
						goodsList.add(goods);
					}
				}
				return true;
			}else{
			}
			}
		} catch (Exception e) {
		}
		return false;
	}
	
	public int FetchActivityGoodsNumber(String mcids, String minprice, String maxprice, String startIndex, String orderby, int takeNum){
		String result = MassVigService.getInstance().SearchActivityProduct(mcids, minprice, maxprice, startIndex, orderby, takeNum);
		try {
			if(!TextUtils.isEmpty(result) && !result.equals("null")){
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
//			String message = object.getString("ResponseMsg");
			if(resultCode == 0){
				int number = object.optInt("ResponseData");
				return number;
			}else{
				return -1;
			}
			}
		} catch (Exception e) {
			return -1;
		}
		return -1;
	}
	
	/**
	 * 获取Goods列表(有关键字)
	 * @param merchantID 商户id
	 * @param categoryID 分类id
	 * @return 
	 */
	public boolean SearchProduct(String key, String mcids, String minprice, String maxprice, String startIndex, String orderby){
		String result = MassVigService.getInstance().SearchProduct(key, mcids, minprice, maxprice, startIndex, orderby);
		try {
			JSONObject object = new JSONObject(result);
			int resultCode = object.getInt("ResponseStatus");
//			String message = object.getString("ResponseMsg");
			if(resultCode == 0){
				JSONArray list = object.getJSONArray("ResponseData");
				if(list != null && list.length() > 0){
					for(int i = 0; i < list.length(); i++){
						Goods goods = new Goods();
						JSONObject merchantData = list.getJSONObject(i);
						goods = DataAnalyse(merchantData);
						goodsList.add(goods);
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
	 * 解析goods，返回goods对象
	 * @param data
	 * @return NoQgoods
	 */
	private Goods DataAnalyse(JSONObject data){
		Goods goods = new Goods();
		try {
			goods.productID = data.getInt("ProductID");
			goods.name = data.getString("Name");
			goods.ProductPromotionTag = data.optInt("ProductPromotionTag");
			goods.minPrice = data.getDouble("MinPrice");
			goods.volume = data.getInt("Volume");
			goods.imageUrl = data.getString("MainImgUrl");
			goods.commentCount = data.getInt("CommentCount");
			goods.realPrice = data.getDouble("MinOriginPrice");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return goods;
	}
}
