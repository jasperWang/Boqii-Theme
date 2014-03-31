package com.massvig.ecommerce.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



/**
 * RequestParameters
 * @author  DuJun
 */
public class MassVigRequestParameters {

	private HashMap<String, String> mParameters;
	private List<String> mKeys;
	
	
	public MassVigRequestParameters(){
		mParameters = new HashMap<String, String>();
		mKeys = new ArrayList<String>();
	}
	
	
	public void add(String key, String value){
		if(!this.mKeys.contains(key)){	
			this.mKeys.add(key);
		}
		this.mParameters.put(key, value);
	}
	
	
	public void remove(String key){
		mKeys.remove(key);
		this.mParameters.remove(key);
	}
	
	public void remove(int i){
		String key = this.mKeys.get(i);
		this.mParameters.remove(key);
		mKeys.remove(key);
	}
	
	
	public int getLocation(String key){
		if(this.mKeys.contains(key)){
			return this.mKeys.indexOf(key);
		}
		return -1;
	}
	
	public String getKey(int location){
		if(location >= 0 && location < this.mKeys.size()){
			return this.mKeys.get(location);
		}
		return "";
	}
	
	
	public String getValue(String key){
		String rlt = this.mParameters.get(key);
		return rlt;
	}
	
	public String getValue(int location){
		String key = this.mKeys.get(location);
		String rlt = this.mParameters.get(key);
		return rlt;
	}
	
	
	public int size(){
		return mKeys.size();
	}
	
	public void addAll(MassVigRequestParameters parameters){
		for(int i = 0; i < parameters.size(); i++){
			this.add(parameters.getKey(i), parameters.getValue(i));
		}
		
	}
	
	public String getRquestParam(){
		String key,value;
		StringBuffer sb = new StringBuffer();
		for(int i = 0,len = mKeys.size();i < len;i++){
			key = mKeys.get(i);
			value = getValue(key);
			if(i != 0){
				sb.append("&");
			}
			sb.append(key+"="+value);
		}
		return sb.toString();
	}
	
	public void clear(){
		this.mKeys.clear();
		this.mParameters.clear();
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String result = "";
		for (int i = 0; i < mKeys.size(); i++) {
			result += mKeys.get(i) + "---";
		}
		return result;
	}
	
}
