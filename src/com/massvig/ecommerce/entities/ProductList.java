package com.massvig.ecommerce.entities;

import java.io.Serializable;
import java.util.ArrayList;

public class ProductList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Product> productList;
	
	public ProductList(){
		productList = new ArrayList<Product>();
	}
	
	public void deleteProduct(Product p){
		if(getCount() > 0)
			for (int i = 0; i < productList.size(); i++) {
				if(Integer.valueOf(getProductID(i)) == p.id)
					productList.remove(p);
			}
	}
	
	/**
	 * 清空列表
	 */
	public void clearProductList(){
		if(productList !=null && productList.size() > 0){
			productList.clear();
		}
	}
	/**
	 * 删除一个Product
	 * @param position
	 */
	public void deleteProduct(int position){
		productList.remove(position);
	}
	
	/**
	 * 增加一个Product
	 * @param Product
	 */
	public void addProduct(Product product){
		productList.add(product);
	}
	
	/**
	 * 增加一个productList
	 * @param list
	 */
	public void addProductList(ProductList list){
		productList.addAll(list.getProductList());
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Product> getProductList() {
		return productList;
	}

	/**
	 * 获取列表长度
	 * @return
	 */
	public int getCount(){
		if(productList != null){
			return productList.size();
		}else{
			return 0;
		}
	}
	
	/**
	 * 获取指定product
	 * @param position
	 * @return
	 */
	public Product getProduct(int position){
		if(productList != null && productList.size() > position)
			return productList.get(position);
		else return new Product();
	}
	
	/**
	 * 获取指定product的ID
	 * @param position
	 * @return
	 */
	public String getProductID(int position){
		return getProduct(position).id + "";
	}
	
	//TODO
	public void updateProduct(){
		
	}
}
