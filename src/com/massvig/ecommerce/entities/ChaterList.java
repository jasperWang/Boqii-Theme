package com.massvig.ecommerce.entities;

import java.util.ArrayList;

public class ChaterList {

	private ArrayList<Chater> chaterList;
	
	public ChaterList(){
		chaterList = new ArrayList<Chater>();
	}
	
	/**
	 * 清空列表
	 */
	public void clearchaterList(){
		if(chaterList !=null && chaterList.size() > 0){
			chaterList.clear();
		}
	}
	/**
	 * 删除一个Chater
	 * @param position
	 */
	public void deleteChater(int position){
		chaterList.remove(position);
	}
	
	/**
	 * 增加一个Chater
	 * @param Chater
	 */
	public void addChater(Chater Chater){
		chaterList.add(Chater);
	}
	
	/**
	 * 增加一个chaterList
	 * @param list
	 */
	public void addchaterList(ChaterList list){
		chaterList.addAll(list.getchaterList());
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Chater> getchaterList() {
		// TODO Auto-generated method stub
		return chaterList;
	}

	/**
	 * 获取列表长度
	 * @return
	 */
	public int getCount(){
		if(chaterList != null){
			return chaterList.size();
		}else{
			return 0;
		}
	}
	
	/**
	 * 获取指定Chater
	 * @param position
	 * @return
	 */
	public Chater getChater(int position){
		return chaterList.get(position);
	}
	
}
