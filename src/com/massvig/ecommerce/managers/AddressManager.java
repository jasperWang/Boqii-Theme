package com.massvig.ecommerce.managers;

import java.io.Serializable;
import java.util.ArrayList;

import android.os.AsyncTask;

import com.massvig.ecommerce.entities.Address;
import com.massvig.ecommerce.entities.AddressList;
import com.massvig.ecommerce.entities.City;
import com.massvig.ecommerce.utilities.MassVigContants;

public class AddressManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int LOAD = 1;
	public static final int ADD = 2;
	public static final int MODIFY = 3;
	public static final int DEFAULT = 4;
	public static final int DELETE = 5;
	private LoadListener listener;
	private AddressList addressList = new AddressList();
	private Address address = new Address();
	public ArrayList<City> allCities = new ArrayList<City>();
	public ArrayList<City> provinceList = new ArrayList<City>();
	public ArrayList<City> cityList = new ArrayList<City>();
	public ArrayList<City> townList = new ArrayList<City>();
	
	public void setListener(LoadListener l){
		this.listener = l;
	}
	
	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public AddressList getAddressList() {
		return addressList;
	}

	public void setAddressList(AddressList addressList) {
		this.addressList = addressList;
	}

	/**
	 * 获取地址（不带运费）
	 * @param sessionid
	 */
	public void FetchAddress(String sessionid){
		new FetchAddressAsync().execute(sessionid);
	}
	
	/**
	 * 获取地址（带运费）
	 * @param sessionid
	 */
	public void FetchAddressListWithFreight(String sessionid, int productSpecID, int quantity){
		new FetchAddressListWithFreightAsync().execute(sessionid, productSpecID + "", quantity + "");
	}
	
	public void addAddress(String sessionid){
		new AddAddressAsync().execute(sessionid);
	}
	
	public void modifyAddress(String sessionid){
		new ModifyAddressAsync().execute(sessionid);
	}
	
	public void setDefaultAddress(String sessionid){
		new SetDefaultAddressAsync().execute(sessionid);
	}
	
	public void deleteAddress(String sessionid){
		new DeleteAddressAsync().execute(sessionid);
	}

	/**
	 * 异步获取address（不带运费）
	 * @author DuJun
	 *
	 */
	private class FetchAddressAsync extends AsyncTask<String, Void, Integer>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(String... params) {
			return addressList.FetchAddressList(params[0]);
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if(result == 0){
				listener.Success(LOAD);
			}else if(result == MassVigContants.SESSIONVAILED){
				listener.SessionidFail();
			}else{
				listener.Failed(LOAD);
			}
		}
	}
	
	/**
	 * 异步获取address(带运费）
	 * @author DuJun
	 *
	 */
	private class FetchAddressListWithFreightAsync extends AsyncTask<String, Void, Boolean>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			return addressList.FetchAddressListWithFreight(params[0], Integer.valueOf(params[1]), Integer.valueOf(params[2]));
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(result){
				listener.Success(LOAD);
			}else{
				listener.Failed(LOAD);
			}
		}
	}
	
	/**
	 * 异步添加address
	 * @author DuJun
	 *
	 */
	private class AddAddressAsync extends AsyncTask<String, Void, Integer>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(String... params) {
			int result = address.AddAddress(params[0]);
			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if(result == 0){
				listener.Success(ADD);
			}else if(result == MassVigContants.SESSIONVAILED){
				listener.SessionidFail();
			}else{
				listener.Failed(ADD);
			}
		}
	}
	
	/**
	 * 异步删除address
	 * @author DuJun
	 *
	 */
	private class DeleteAddressAsync extends AsyncTask<String, Void, Integer>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(String... params) {
			return address.DeleteAddress(params[0]);
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if(result == 0){
				listener.Success(DELETE);
			}else if(result == MassVigContants.SESSIONVAILED){
				listener.SessionidFail();
			}else{
				listener.Failed(DELETE);
			}
		}
	}
	
	/**
	 * 异步设置默认address
	 * @author DuJun
	 *
	 */
	private class SetDefaultAddressAsync extends AsyncTask<String, Void, Integer>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(String... params) {
			return address.SetDefaultAddress(params[0]);
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if(result == 0){
				listener.Success(DEFAULT);
			}else if(result == MassVigContants.SESSIONVAILED){
				listener.SessionidFail();
			}else{
				listener.Failed(DEFAULT);
			}
		}
	}
	
	/**
	 * 异步获取address列表
	 * @author DuJun
	 *
	 */
	private class ModifyAddressAsync extends AsyncTask<String, Void, Integer>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(String... params) {
			return address.ModifyAddress(params[0]);
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if(result == 0){
				listener.Success(MODIFY);
			}else if(result == MassVigContants.SESSIONVAILED){
				listener.SessionidFail();
			}else{
				listener.Failed(MODIFY);
			}
		}
	}
	
	public interface LoadListener{
		void Success(int index);
		void Failed(int index);
		void SessionidFail();
	}
}
