package com.massvig.ecommerce.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.massvig.ecommerce.entities.Goods;

import android.text.TextUtils;

public class DealActivityManager {
	private int count = 1;
	private String paramsInfo;
	private ArrayList<ProductSpec> specItems = new ArrayList<DealActivityManager.ProductSpec>();
	private ArrayList<ProductProperty> propertyItems = new ArrayList<DealActivityManager.ProductProperty>();
	private int width;
	private int[] selectProperties;
//	private ArrayList<String> hasNotContains;
	private HashMap<String, Integer> hasNotContains = new HashMap<String, Integer>();
	private Goods goods;
	
	public Goods getGoods() {
		return goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}

	public boolean isUseful(String select){
		if(hasNotContains != null && hasNotContains.containsKey(select)){
			return false;
		}else{
			return true;
		}
	}
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public int getVolumn(){
		int count = goods.Inventory;
		if(specItems.size() > 0){
			String selected = getSelectProperties();
			if(!selected.contains("-1")){
				for(int i=0;i<specItems.size();i++){
					ProductSpec spec = specItems.get(i);
					if(!TextUtils.isEmpty(spec.properties) && !spec.properties.equals("null")){
						if(spec.properties.equals(selected)){
							count = spec.volumn;
						}
					}
				}
			}
		}
		return count;
	}
	
	public float getMoney() {
		float money = (float) (count * goods.minPrice);
		if(count != 0 && specItems.size() > 0){
			String selected = getSelectProperties();
			if(!selected.contains("-1")){
				for (int i = 0; i < specItems.size(); i++) {
					ProductSpec spec = specItems.get(i);
					if(!TextUtils.isEmpty(spec.properties) && !spec.properties.equals("null")){
						if(spec.properties.equals(selected)){
							double sig = spec.price;
							money = (float) (count * sig);
						}
					}
				}
			}else{
			}
		}else{
			money = (float) (count * goods.minPrice);
		}
		return money;
	}

	public HashMap<String, String> getSelectedPropertiesString(){
		HashMap<String,String>  map = new HashMap<String, String>();
		if(selectProperties == null || getMoney() <= 0){
			return map;
		}
		for (int i = 0; i < propertyItems.size(); i++) {
			ProductProperty p = propertyItems.get(i);
			String key = p.name;
			String value = "";
			ArrayList<Item> items = p.items;
			for (int j = 0; j < items.size(); j++) {
				Item item = items.get(j);
				if(item.id == selectProperties[i]){
					value = item.name;
					break;
				}
			}
			map.put(key, value);
		}
		return map;
	}

	public String getSelectProperties() {
		if(selectProperties == null){
			return "";
		}
		String result = "";
		for(int i = 0;i<selectProperties.length;i++){
			result += selectProperties[i] + ",";
		}
		if(!TextUtils.isEmpty(result))
			result = result.substring(0, result.length() - 1);
		return result;
	}

	public void setSelected(int index, int value){
		if(selectProperties.length >= index){
			selectProperties[index] = value;
		}
	}
	
	public List<String> getDescartes(String[] selectProperties){
		List<String[]> list = new ArrayList<String[]>();
		for (int i = 0; i < selectProperties.length; i++) {
			if(Integer.valueOf(selectProperties[i]) == -1){
				ArrayList<Item> item = propertyItems.get(i).items;
				String[] str = new String[item.size()];
				for (int j = 0; j < str.length; j++) {
					str[j] = item.get(j).id + "";
				}
				list.add(str);
			}else{
				String[] str = new String[]{selectProperties[i]};
				list.add(str);
			}
		
			
//			if(Integer.valueOf(selectProperties[i]) != -1){
//				String[] str = new String[]{selectProperties[i]};
//				list.add(str);
//			}else{
//				ArrayList<Item> item = propertyItems.get(i).items;
//				String[] str = new String[item.size()];
//				for (int j = 0; j < str.length; j++) {
//					str[j] = item.get(j).id + "";
//				}
//				list.add(str);
//			}
		}
		List<String> result = new ArrayList<String>();
		Descartes(list, 0, result, "");
		return result;
	}
	
	private HashMap<String, Integer> HasNotContains(){
		if(hasNotContains == null || hasNotContains.size() == 0){
			//生成笛卡尔积
			hasNotContains = new HashMap<String, Integer>();
//			hasNotContains = new ArrayList<String>();
			List<String[]> list = new ArrayList<String[]>();
			for (int i = 0; i < propertyItems.size(); i++) {
				ArrayList<Item> item = propertyItems.get(i).items;
				String[] str = new String[item.size()];
				for (int j = 0; j < str.length; j++) {
					str[j] = item.get(j).id + "";
				}
				list.add(str);
			}
			List<String> result = new ArrayList<String>();
			Descartes(list, 0, result, "");
			//建立hash表
//			HashMap<String, Integer> map = new HashMap<String, Integer>();
			for (int i = 0; i < result.size(); i++) {
				String s = result.get(i);
				hasNotContains.put(s, 0);
			}
			//遍历
			for (int i = 0; i < specItems.size(); i++) {
				ProductSpec spec = specItems.get(i);
				if(hasNotContains.containsKey(spec.properties))
						hasNotContains.remove(spec.properties);
			}
			return hasNotContains;
			
//			HashMap<String, Boolean> map = new HashMap<String, Boolean>();
//			for (int i = 0; i < specItems.size(); i++) {
//				ProductSpec spec = specItems.get(i);
//				map.put(spec.properties, spec.isEnable);
//			}
//			//遍历找不存在的
//			for (int i = 0; i < result.size(); i++) {
//				String res = result.get(i);
//				if(!map.containsKey(res)){
//					hasNotContains.add(res);
//				}
//			}
//			for(
//			return hasNotContains;
		}else
			return hasNotContains;
	}

	private static String Descartes(List<String[]> list, int count, List<String> result, String data){
		String temp =data;
		// 获取当前数组
		String[] astr = list.get(count);
		for (int i = 0; i < astr.length; i++) {
			if(count + 1 < list.size()){
				temp += Descartes(list, count + 1, result, data + astr[i] + ",");
			}else{
				result.add(data + astr[i]);
			}
		}
		return temp;
	}
	
	public class ProductSpec {
		public int id;
		public double price;
		public String properties;
		public int volumn;
	}

	public class ProductProperty {
		public int id;
		public String name;
		public ArrayList<Item> items;
	}

	public class Item {
		public int id;
		public String name;
		public String imgurl;
	}

	public DealActivityManager() {
	}

	public String getParamsInfo() {
		return paramsInfo;
	}

	public void setParamsInfo(String paramsInfo) {
		this.paramsInfo = paramsInfo;
		if (!TextUtils.isEmpty(paramsInfo) && !paramsInfo.equals("null")) {
			DataAnaly(paramsInfo);
			if (propertyItems != null && propertyItems.size() > 0) {
				selectProperties = new int[propertyItems.size()];
				for (int i = 0; i < selectProperties.length; i++) {
					selectProperties[i] = -1;
				}
				hasNotContains = HasNotContains();
			}
		}
	}

	public ArrayList<ProductSpec> getSpecItems() {
		return specItems;
	}

	public void setSpecItems(ArrayList<ProductSpec> specItems) {
		this.specItems = specItems;
	}

	public ArrayList<ProductProperty> getPropertyItems() {
		return propertyItems;
	}

	public void setPropertyItems(ArrayList<ProductProperty> propertyItems) {
		this.propertyItems = propertyItems;
	}

	private void DataAnaly(String paramsInfo2) {
		try {
			if(!TextUtils.isEmpty(paramsInfo2)){
			JSONObject data = new JSONObject(paramsInfo2);
			JSONArray array = null;
			if(data != null){
				array = data.getJSONArray("ProductSpecs");
			}
			if (array != null && array.length() > 0) {
				specItems = new ArrayList<DealActivityManager.ProductSpec>();
				for (int i = 0; i < array.length(); i++) {
					ProductSpec item = new ProductSpec();
					JSONObject o = array.getJSONObject(i);
					item.id = o.getInt("ProductSpecID");
					item.price = o.getDouble("Price");
					item.volumn = o.optInt("Inventory");
					goods.Inventory = o.optInt("Inventory");
					item.properties = o.getString("ProductProperties");
					specItems.add(item);
				}
			}
			JSONArray pArray = data.getJSONArray("ProductProperties");
			if (pArray != null && pArray.length() > 0) {
				propertyItems = new ArrayList<DealActivityManager.ProductProperty>();
				for (int i = 0; i < pArray.length(); i++) {
					ProductProperty item = new ProductProperty();
					JSONObject o = pArray.getJSONObject(i);
					item.id = o.getInt("PropertyID");
					item.name = o.getString("Name");
					if (!TextUtils.isEmpty(o.getString("ppvaluesDyn"))) {
						JSONArray iArray = new JSONArray(
								o.getString("ppvaluesDyn"));
						item.items = new ArrayList<DealActivityManager.Item>();
						for (int j = 0; j < iArray.length(); j++) {
							Item it = new Item();
							JSONObject io = iArray.getJSONObject(j);
							it.id = io.getInt("ValueID");
							it.name = io.getString("Name");
							it.imgurl = io.optString("ImgUrl");
							item.items.add(it);
						}
					}
					propertyItems.add(item);
				}
			}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 获取规格组合ID
	 * @return
	 */
	public int getProductSpecID(){
		int result = -1;
		if(selectProperties == null || selectProperties.length == 0){
			if(specItems != null && specItems.size() > 0)
				result = specItems.get(0).id;
			return result;
		}
			
		for (int i = 0; i < selectProperties.length; i++) {
			if(selectProperties[i] == -1){
				return result;
			}
		}
		String value = getSelectProperties();
		if(!TextUtils.isEmpty(value))
			for (int i = 0; i < specItems.size(); i++) {
				String pro = specItems.get(i).properties;
				if(value.equals(pro)){
					result = specItems.get(i).id;
					return result;
				}
				
			}
		return result;
	}
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

}
