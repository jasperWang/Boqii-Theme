package com.massvig.ecommerce.service;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.massvig.ecommerce.activities.BaseApplication;
import com.massvig.ecommerce.network.MassVigClientHelper;
import com.massvig.ecommerce.network.MassVigRequestParameters;
import com.massvig.ecommerce.network.MassVigClientHelper.TimeOutListener;
import com.massvig.ecommerce.utilities.MassVigRequestParam;
import com.massvig.ecommerce.utilities.MassVigUtil;

public class MassVigService {
	public static final String merchantid = "3";
	private static final String GOOGLE_LOC_URL = "http://maps.googleapis.com/maps/api/geocode/json?sensor=true&language=zh-CN";
//	public static final String SERVER_URL = "http://117.135.136.135:7008";
	public static final String SERVER_URL = "http://42.121.144.195/boqi";
	private static MassVigService instance;
	private static MassVigClientHelper clientHelper;
	private static timeoutListener listener;
	private static String udid;
	public static final String CHANNEL = "1030006";

	public void setListener(timeoutListener listener) {
		MassVigService.listener = listener;
	}

	public static MassVigService getInstance() {
		// if(instance == null){
//		instance = null;
		instance = new MassVigService();
		udid = BaseApplication.udid;
		// }
		clientHelper = MassVigClientHelper.getInstance();
		clientHelper.setTimeoutListener(new TimeOutListener() {

			@Override
			public void timeoutListener() {
				listener.TimeOutListener();
			}
		});
		return instance;
	}

	public String getpromotionURL(String method){
		return SERVER_URL + "/promotion/" + method + "?AdSource=" + CHANNEL;
	}
	public String getCollectURL(String method){
		return SERVER_URL+"/Collection/"+method + "?AdSource=" + CHANNEL;
	}
	
	public String getProductURL(String method) {
		return SERVER_URL + "/product/" + method + "?AdSource=" + CHANNEL;
	}
	
	public String getAccountURL(String method){
		return SERVER_URL + "/customer/" + method + "?AdSource=" + CHANNEL;
	}
	
	public String getAddressURL(String method){
		return SERVER_URL + "/address/" + method + "?AdSource=" + CHANNEL;
	}
	
	public String getPromotionURL(String method){
		return SERVER_URL + "/promotion/" + method + "?AdSource=" + CHANNEL;
	}

	public String getPaymentURL(String method){
		return SERVER_URL + "/payment/" + method + "?AdSource=" + CHANNEL;
	}
	
	public String getOrderURL(String method){
		return SERVER_URL + "/order/" + method + "?AdSource=" + CHANNEL;
	}
	
	public String getCategoryURL(String method){
		return SERVER_URL + "/category/" + method + "?AdSource=" + CHANNEL;
	}
	
	public String getSharedURL(String method){
		return SERVER_URL + "/share/" + method + "?AdSource=" + CHANNEL;
	}
	
	public String getFollowURL(String method){
		return SERVER_URL + "/CustomerFollow/" + method + "?AdSource=" + CHANNEL;
	}
	
	public String getShoppingURL(String method){
		return SERVER_URL + "/ShoppingCart/" + method + "?AdSource=" + CHANNEL;
	}
	
	public String HttpConnect(URL url) {
		String result = "";
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			conn.setConnectTimeout(20000);
			InputStream inputStream = conn.getInputStream();
			if (conn.getResponseCode() == 408) {
				listener.TimeOutListener();
			}
			byte[] b = new byte[1024];
			int readedLength = -1;
			ByteArrayOutputStream outputS = new ByteArrayOutputStream();
			while ((readedLength = inputStream.read(b)) != -1) {
				outputS.write(b, 0, readedLength);
			}
			result = outputS.toString();
			conn.disconnect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//TODO
	public String getCityCbdList(int cityId) {
		String result = "";
		URL u;
		try {
			u = new URL(SERVER_URL + "/GetCityCBDList?cityID=" + cityId);
			result = HttpConnect(u);
		} catch (Exception e) {
		}
		return result;
	}

	// TODO
	public String getCityByLocation(String latitude, String longitude) {
		String address = "";
		String result = "";
		URL u;
		try {
			u = new URL(GOOGLE_LOC_URL + "&latlng=" + latitude + ","
					+ longitude);
			result = HttpConnect(u);
			if (!TextUtils.isEmpty(result)) {
				try {
					JSONObject data = new JSONObject(result);
					JSONArray array = data.getJSONArray("results");
					if (array.length() > 0) {
						JSONObject o = array.getJSONObject(0);
						JSONArray a = o.getJSONArray("address_components");
						for (int i = 0; i < a.length(); i++) {
							JSONObject dat = a.getJSONObject(i);
							String type = dat.getString("types");
							if (!TextUtils.isEmpty(type)
									&& type.equals("[\"sublocality\",\"political\"]")) {
								String area = dat.getString("long_name");
								address += area;
							}
						}
						return address;
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return address;
	}

	public String getAddByLocation(String latitude, String longitude,
			boolean isShort) {
		String address = "";
		String result = "";
		URL u;
		try {
			u = new URL(GOOGLE_LOC_URL + "&latlng=" + latitude + ","
					+ longitude);
			result = HttpConnect(u);
			if (!TextUtils.isEmpty(result)) {
				try {
					JSONObject data = new JSONObject(result);
					JSONArray array = data.getJSONArray("results");
					if (array.length() > 0) {
						if (!isShort) {
							JSONObject o = array.getJSONObject(0);
							address = o.getString("formatted_address");
							return address;
						} else {
							JSONObject o = array.getJSONObject(0);
							JSONArray a = o.getJSONArray("address_components");
							for (int i = 0; i < a.length(); i++) {
								JSONObject dat = a.getJSONObject(i);
								String type = dat.getString("types");
								if (!TextUtils.isEmpty(type)
										&& type.equals("[\"locality\",\"political\"]")) {
									String city = dat.getString("long_name");
									address += city;
								}
							}
							address += " ";
							for (int i = 0; i < a.length(); i++) {
								JSONObject dat = a.getJSONObject(i);
								String type = dat.getString("types");
								if (!TextUtils.isEmpty(type)
										&& type.equals("[\"sublocality\",\"political\"]")) {
									String area = dat.getString("long_name");
									address += area;
								}
							}
							return address;
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return address;
	}
	
	/**
	 * 粉丝相关
	 */
	/**
	 * 关注某人
	 * @param sessionid
	 * @param customerID
	 * @return
	 */
	public String Follow(String sessionid, int customerID){
		String result = "";
		String url = getFollowURL("Follow");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("customerID", customerID + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 取消关注某人
	 * @param sessionid
	 * @param customerID
	 * @return
	 */
	public String CancelFollow(String sessionid, int customerID){
		String result = "";
		String url = getFollowURL("CancelFollow");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("customerID", customerID + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 查看特定用户所关注的人
	 * @param sessionid
	 * @param customerID 特定用户ID
	 * @param startIndex
	 * @param takeNum
	 * @return
	 */
	public String GetFollows(String sessionid, int customerID, int startIndex,int takeNum){
		String result = "";
		String url = getFollowURL("GetFollows");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("customerID", customerID + "");
		params.add("startIndex", startIndex + "");
		params.add("takeNum", takeNum + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 查看特定用户的粉丝
	 * @param sessionid
	 * @param customerID 特定用户ID
	 * @param startIndex
	 * @param takeNum
	 * @return
	 */
	public String GetFans(String sessionid, int customerID, int startIndex,int takeNum){
		String result = "";
		String url = getFollowURL("GetFans");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("customerID", customerID + "");
		params.add("startIndex", startIndex + "");
		params.add("takeNum", takeNum + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	
	/**
	 * 社区相关
	 */
	/**
	 * 分享一个商品
	 * @param productID
	 * @param comment
	 * @param imgUrl
	 * @param lon
	 * @param lat
	 * @param address
	 * @return
	 */
	public String ShareProduct(String sessionid, int productID, String comment, String imgUrl, double lon, double lat, String address){
		String result = "";
		String url = getSharedURL("ShareProduct");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		
		params.add("sessionid", sessionid);
		params.add("productID", productID + "");
		try {
			params.add("comment", URLEncoder.encode(comment, "UTF-8"));
			params.add("address", URLEncoder.encode(address, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		params.add("imgUrl", imgUrl);
		params.add("lon", lon + "");
		params.add("lat", lat + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 分享一个帖子
	 * @param sessionid
	 * @param shareID
	 * @param comment
	 * @param imgUrl
	 * @param lon
	 * @param lat
	 * @param address
	 * @return
	 */
	public String Share(String sessionid, int shareID, String comment, String imgUrl, double lon, double lat, String address){
		String result = "";
		String url = getSharedURL("Share");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("shareID", shareID + "");
		try {
			params.add("comment", URLEncoder.encode(comment, "UTF-8"));
			params.add("address", URLEncoder.encode(address, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		params.add("imgUrl", imgUrl);
		params.add("lon", lon + "");
		params.add("lat", lat + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 赞一个帖子
	 * @param sessionid
	 * @param shareID
	 * @return
	 */
	public String Praise(String sessionid, int shareID){
		String result = "";
		String url = getSharedURL("Praise");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("shareID", shareID + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 社区探索
	 * @param sessionid
	 * @param startIndex
	 * @param takeNum
	 * @return
	 */
	public String GetAll(String sessionid, int startIndex, int takeNum){
		String result = "";
		String url = getSharedURL("GetAll");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("startIndex", startIndex + "");
		params.add("takeNum", takeNum + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 查找我关注的人的分享
	 * @param sessionid
	 * @param startIndex
	 * @param takeNum
	 * @return
	 */
	public String GetFollowShares(String sessionid, int startIndex, int takeNum){
		String result = "";
		String url = getSharedURL("GetFollowShares");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("startIndex", startIndex + "");
		params.add("takeNum", takeNum + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 查找特定的人的分享
	 * @param sessionid
	 * @param customerID
	 * @param startIndex
	 * @param takeNum
	 * @return
	 */
	public String GetShares(String sessionid, int customerID, int startIndex, int takeNum){
		String result = "";
		String url = getSharedURL("GetShares");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("customerID", customerID + "");
		params.add("startIndex", startIndex + "");
		params.add("takeNum", takeNum + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 查找特定用户赞过的分享
	 * @param sessionid
	 * @param customerID
	 * @param startIndex
	 * @param takeNum
	 * @return
	 */
	public String GetHavePaisedShares(String sessionid, int customerID, int startIndex, int takeNum){
		String result = "";
		String url = getSharedURL("GetHavePaisedShares");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("customerID", customerID + "");
		params.add("startIndex", startIndex + "");
		params.add("takeNum", takeNum + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 查找特定用户被人赞的记录
	 * @param sessionid
	 * @param customerID
	 * @param startIndex
	 * @param takeNum
	 * @return
	 */
	public String GetPraisedShareRecords(String sessionid, int customerID, int startIndex, int takeNum){
		String result = "";
		String url = getSharedURL("GetPraisedShareRecords");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("customerID", customerID + "");
		params.add("startIndex", startIndex + "");
		params.add("takeNum", takeNum + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 分类相关
	 */
	/**
	 * 获取分类数据
	 * @param sessionid
	 * @return
	 */
	public String Getcategory(){
		String result = "";
		String url = getCategoryURL("Getcategory");
		URL u;
		try {
			u = new URL(url + "&" + "merchantid=" + merchantid + "&udid=" + udid);
			result = HttpConnect(u);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
//		MassVigRequestParameters params = new MassVigRequestParameters();
//		params.add("merchantid", merchantid);
//		params.add("udid", udid);
//		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
//		return result;
	}
	
	/**
	 * 订单相关
	 */

	/**
	 * 计算运费
	 * @param sessionid
	 * @param customerAddressID
	 * @param coupons
	 * @param payment
	 * @param billType
	 * @param billTitle
	 * @param productSpecID
	 * @param quantity
	 * @return
	 */
	public String CalculateFreight(String sessionid, String customerAddressID, String coupons, String payment, String billType, String billTitle, String productSpecID, String quantity, int expressage){

		String result = "";
		String url = getOrderURL("CalculateFreight");
		URL u;
		try {
			try {
				u = new URL(url + "&" + "merchantid=" + merchantid + "&udid=" + udid
						+"&sessionid=" + sessionid
						+"&expressage=" + expressage + ""
						+"&customerAddressID=" + customerAddressID
						+"&coupons=" + coupons
						+"&payment=" + payment
						+"&billType=" + billType
						+"&billTitle=" + URLEncoder.encode(billTitle, "UTF-8")
						+"&productSpecID=" + productSpecID
						+"&quantity=" + quantity);
				result = HttpConnect(u);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		MassVigRequestParameters params = new MassVigRequestParameters();
//		params.add("merchantid", merchantid);
//		params.add("udid", udid);
//		params.add("sessionid", sessionid);
//		params.add("expressage", expressage + "");
//		params.add("customerAddressID", customerAddressID);
//		params.add("coupons", coupons);
//		params.add("payment", payment);
//		params.add("billType", billType);
//		try {
//			params.add("billTitle", URLEncoder.encode(billTitle, "UTF-8"));
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		params.add("productSpecID", productSpecID);
//		params.add("quantity", quantity);
//		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	
//		String result = "";
//		String url = getOrderURL("CalculateFreight");
//		try {
//			URL u = null;
//			u = new URL(url + "?" + "merchantid=" + merchantid + "&udid="
//					+ udid + "&expressage=" + expressage + "&sessionid="
//					+ sessionid + "&customerAddressID=" + customerAddressID
//					+ "&coupons=" + coupons + "&payment=" + payment
//					+ "&billType=" + billType + "&billTitle="
//					+ URLEncoder.encode(billTitle, "UTF-8") + "&productSpecID="
//					+ productSpecID + "&quantity=" + quantity);
//			result = HttpConnect(u);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return result;
		
	}
	
	/**
	 * 获取快递信息
	 * @param sessionid
	 * @param regionid
	 * @return
	 */
	public String GetSpecificRegionExpress(String sessionid, String regionid){
		String result = "";
		String url = getOrderURL("GetSpecificRegionExpress");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("regionid", regionid);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 提交未登录订单
	 * @param customerName
	 * @param mobile
	 * @param regionID
	 * @param address
	 * @param zipCode
	 * @param email
	 * @param coupons
	 * @param payment
	 * @param billType
	 * @param billTitle
	 * @param productSpecID
	 * @param quantity
	 * @return
	 */
	public String SubmitUnloginOrder(String customerName, String mobile, int regionID, String address, String zipCode, String email, String coupons, int payment, String billType, String billTitle, String productSpecID, String quantity){
		String result = "";
		String url = getOrderURL("SubmitUnloginOrder");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		try {
			params.add("customerName", URLEncoder.encode(customerName, "UTF-8"));
			params.add("mobile", mobile);
			params.add("regionID", regionID + "");
			params.add("address", URLEncoder.encode(address,"UTF-8"));
			params.add("zipCode", zipCode);
			params.add("email", URLEncoder.encode(email, "UTF-8"));
			params.add("coupons", coupons);
			params.add("payment", payment + "");
			params.add("billType", billType + "");
			params.add("billTitle", URLEncoder.encode(billTitle,"UTF-8"));
			params.add("productSpecID", productSpecID + "");
			params.add("quantity", quantity + "");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	/**
	 * 提交登录订单
	 * @param sessionid
	 * @param customerAddressID
	 * @param coupons
	 * @param payment
	 * @param billType
	 * @param billTitle
	 * @param productSpecID
	 * @param quantity
	 * @return
	 */
	public String SubmitLoginOrder(String sessionid,int customerAddressID, String coupons, int payment, String billType, String billTitle, String productSpecID, String quantity, int expressage){
		String result = "";
		String url = getOrderURL("SubmitLoginOrder");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("customerAddressID", customerAddressID + "");
		params.add("coupons", coupons);
		params.add("payment", payment + "");
		params.add("billType", billType + "");
		params.add("expressage", expressage + "");
		try {
			params.add("billTitle", URLEncoder.encode(billTitle,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		params.add("productSpecID", productSpecID + "");
		params.add("quantity", quantity + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 查看订单列表
	 * @param sessionid
	 * @param orderTab 1:未支付订单;2:已支付订单;3:货到付款订单
	 * @param startIndex
	 * @param takeNum
	 * @return
	 */
	public String GetOrders(String sessionid, int orderTab,int startIndex,int takeNum){
		String result = "";
		String url = getOrderURL("GetOrders");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("orderTab", orderTab + "");
		params.add("boqi", "true");
		params.add("startIndex", startIndex + "");
		params.add("takeNum", takeNum + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 重新提交订单支付
	 * @param sessionid
	 * @param orderID
	 * @param payment
	 * @return
	 */
	public String DefrayOrder(String sessionid, int orderID, int payment){
		String result = "";
		String url = getOrderURL("DefrayOrder");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("orderID", orderID + "");
		params.add("payment", payment + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 用户确认收货
	 * @param sessionid
	 * @return
	 */
	public String ConfirmShiped(String sessionid, int orderID){
		String result = "";
		String url = getOrderURL("ConfirmShiped");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("orderID", orderID + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 用户申请退款
	 * @param sessionid
	 * @return
	 */
	/**
	 * 用户申请退款
	 * @param sessionid
	 * @param orderID
	 * @param orderDetailIDs
	 * @param refundAmount
	 * @param refundReason
	 * @return
	 */
	public String ApplyForRefund(String sessionid, String orderID, String orderDetailIDs, String refundAmount, String refundReason){
		String result = "";
		String url = getOrderURL("ApplyForRefund");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("orderDetailIDs", orderDetailIDs);
		params.add("refundAmount", refundAmount);
		try {
			params.add("refundReason", URLEncoder.encode(refundReason, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		params.add("orderID", orderID + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 用户取消订单
	 * @param sessionid
	 * @param orderID
	 * @return
	 */
	public String CancelOrder(String sessionid, int orderID){
		String result = "";
		String url = getOrderURL("CancelOrder");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("orderID", orderID + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 支付相关
	 */
	/**
	 * 获取支付相关
	 * @param sessionid
	 * @return
	 */
	public String GetPaymentMethod(){
		String result = "";
		String url = getOrderURL("GetPaymentMethod");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 优惠券相关
	 */
	
	/**
	 * 获取优惠券列表
	 * @param sessionid
	 * @return
	 */
	public String getCouponList(String sessionid){
		String result = "";
		String url = getpromotionURL("GetManagementCouponList");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("sessionid", sessionid);
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 点击领取优惠券
	 * @param sessionid
	 * @return
	 */
	public String CreateNewCoupon(String sessionid){
		String result = "";
		String url = getpromotionURL("CreateNewCoupon");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("sessionid", sessionid);
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 弃用
	 * @param sessionid
	 * @param productSpecID
	 * @param quantity
	 * @param counpons
	 * @return
	 */
	public String CheckCoupon(String sessionid, int productSpecID, int quantity, String counpons){
		String result = "";
		String url = getPromotionURL("CheckCoupon");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("productSpecID", productSpecID + "");
		params.add("quantity", quantity + "");
		params.add("coupons", counpons);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 订单优惠策略
	 * @param sessionid
	 * @param customerAddressID
	 * @param coupons
	 * @param payment
	 * @param billType
	 * @param billTitle
	 * @param productSpecID
	 * @param quantity
	 * @param expressage
	 * @return
	 */
	public String OrderPromotion(String sessionid, int customerAddressID, String coupons, String payment, String billType, String billTitle, String productSpecID, String quantity, int expressage){
		String result = "";
		String url = getPromotionURL("OrderPromotion");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("customerAddressID", customerAddressID + "");
		params.add("payment", payment);
		params.add("coupons", coupons);
		params.add("billType", billType);
		params.add("billTitle", billTitle);
		params.add("productSpecID", productSpecID);
		params.add("quantity", quantity);
		params.add("expressage", expressage + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 优惠券使用页，检查可用优惠券
	 * @param sessionid
	 * @param productSpecID
	 * @param quantity
	 * @param coupons
	 * @param expressage
	 * @return
	 */
	public String CheckCoupon2(String sessionid, String productSpecID, String quantity, String coupons, String expressage){
		String result = "";
		String url = getPromotionURL("CheckCoupon2");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("coupons", coupons);
		params.add("quantity", quantity + "");
		params.add("productSpecID", productSpecID + "");
		params.add("expressage", expressage + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 检查优惠券是否可用
	 * @param sessionid
	 * @param customerAddressID
	 * @param coupons
	 * @param payment
	 * @param billType
	 * @param billTitle
	 * @param productSpecID
	 * @param quantity
	 * @return
	 */
	public String CheckCouponLogin(String sessionid, int customerAddressID, String coupons, String payment, int billType, String billTitle, String productSpecID, String quantity, int expressage){
		String result = "";
		String url = getPromotionURL("CheckCouponLogin");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("customerAddressID", customerAddressID + "");
		params.add("coupons", coupons);
		params.add("payment", payment);
		params.add("billType", billType + "");
		params.add("billTitle", billTitle);
		params.add("quantity", quantity + "");
		params.add("productSpecID", productSpecID + "");
		params.add("expressage", expressage + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 删除优惠券
	 * @param sessionid
	 * @param couponNO
	 * @return
	 */
	public String DeleteCoupon(String sessionid, String couponNO){
		String result = "";
		String url = getPromotionURL("DeleteCoupon");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("couponNO", couponNO);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 创建优惠券
	 * @param sessionid
	 * @param couponNO
	 * @return
	 */
	public String CreateCoupon(String sessionid, String couponNO){
		String result = "";
		String url = getPromotionURL("CreateCoupon");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("couponNO", couponNO);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 地址管理
	 */
	/**
	 * 获取全部省市区信息
	 * @param sessionid
	 * @return
	 */
	public String GetRegionInfo(){
		String result = "";
		String url = getAddressURL("GetRegionInfo");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}

	/**
	 * 获取地址列表(不返回运费)
	 * 
	 * @return
	 */
	public String GetCustomerAddresses(String sessionid) {
//		return "{ResponseStatus: 0,ResponseMsg: null,ResponseData: [{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"}]}";
		String result = "";
		String url = getAddressURL("GetCustomerAddresses");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("sessionid", sessionid);
		params.add("udid", udid);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 获取地址列表（包含运费）
	 * @param productSpecID
	 * @param quantity
	 * @return
	 */
	public String GetCustomerAddressesWithFreight(String sessionid, int productSpecID, int quantity){
		String result = "";
		String url = getAddressURL("GetCustomerAddressesWithFreight");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("productSpecID", productSpecID + "");
		params.add("quantity", quantity + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 添加收货地址
	 * @param sessionid
	 * @param customerName
	 * @param mobile
	 * @param regionID
	 * @param address
	 * @param zipCode
	 * @param email
	 * @return
	 */
	public String AddAddress(String sessionid, String customerName, String mobile, int regionID, String address, String zipCode, String email){
		String result = "";
		String url = getAddressURL("AddAddress");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		try {
			params.add("customerName", URLEncoder.encode(customerName, "UTF-8"));
			params.add("mobile", URLEncoder.encode(mobile, "UTF-8"));
			params.add("regionID", URLEncoder.encode(regionID + "", "UTF-8"));
			params.add("address", URLEncoder.encode(address, "UTF-8"));
			params.add("zipCode", URLEncoder.encode(zipCode, "UTF-8"));
			params.add("email", URLEncoder.encode(email, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	
	}
	
	/**
	 * 修改收货地址
	 * @param sessionid
	 * @param customerAddressID
	 * @param customerName
	 * @param mobile
	 * @param regionID
	 * @param address
	 * @param zipCode
	 * @param email
	 * @return
	 */
	public String ModifyAddress(String sessionid, int customerAddressID, String customerName, String mobile, int regionID, String address, String zipCode, String email){
		String result = "";
		String url = getAddressURL("ModifyAddress");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("customerAddressID", customerAddressID + "");
		try {
			params.add("customerName", URLEncoder.encode(customerName, "UTF-8"));
			params.add("mobile", URLEncoder.encode(mobile, "UTF-8"));
			params.add("address", URLEncoder.encode(address, "UTF-8"));
			params.add("zipCode", URLEncoder.encode(zipCode, "UTF-8"));
			params.add("email", URLEncoder.encode(email, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		params.add("regionID", regionID + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 设置默认地址
	 * @param sessionid
	 * @param customerAddressID
	 * @return
	 */
	public String SetDefaultAddress(String sessionid, int customerAddressID){
		String result = "";
		String url = getAddressURL("SetDefaultAddress");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("customerAddressID", customerAddressID + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 计算运费
	 * @param sessionid
	 * @return
	 */
	public String CalculateFreight(String sessionid, int productSpecID, int quantity , int regionID){
		String result = "";
		String url = getAddressURL("CalculateFreight2");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("productSpecID", productSpecID + "");
		params.add("quantity", quantity + "");
		params.add("regionID", regionID + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 删除收货地址
	 * @param sessionid
	 * @return
	 */
	public String DeleteAddress(String sessionID, int customerAddressID){
		String result = "";
		String url = getAddressURL("DeleteAddress");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionID", sessionID);
		params.add("customerAddressID", customerAddressID + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 店铺分布
	 * @param sessionid
	 * @return
	 */
	public String GetAroundMerchantStore(String sessionID, double lon, double lat, int startIndex, int takeNum){
		String result = "";
		String url = getAddressURL("GetAroundMerchantStore");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionID", sessionID);
		params.add("lon", lon + "");
		params.add("lat", lat + "");
		params.add("startIndex", startIndex + "");
		params.add("takeNum", takeNum + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 商品
	 */
	/**
	 * 获取商品列表
	 * 
	 * @return
	 */
	public String GetProducts(String mcids, String minprice, String maxprice, String startIndex, String orderby, int takeNum) {
//		return "{ResponseStatus: 0,ResponseMsg: null,ResponseData: [{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"}]}";
		String result = "";
		String url = getProductURL("GetProducts");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("mcid", mcids);
		params.add("minprice", minprice);
		params.add("maxprice", maxprice);
		params.add("startindex", startIndex);
		params.add("takeNum", takeNum + "");
		params.add("orderby", orderby);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}

	public String SearchActivityProduct(String mcids, String minprice, String maxprice, String startIndex, String orderby, int takeNum) {
//		return "{ResponseStatus: 0,ResponseMsg: null,ResponseData: [{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"}]}";
		String result = "";
		String url = getProductURL("SearchActivityProduct");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("mcid", mcids);
		params.add("minprice", minprice);
		params.add("maxprice", maxprice);
		params.add("startindex", startIndex);
		params.add("takeNum", takeNum + "");
		params.add("orderby", orderby);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	public String addCollection(int productID,String sessionID){
		String result="";
		String url=getCollectURL("AddCollection");
		MassVigRequestParameters params=new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid",udid);
		params.add("productID", productID+"");
		params.add("sessionID", sessionID);
		result=clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	public String GetCollection(String sessionID){
		String result="";
		String url=getCollectURL("GetCollectionList");
		MassVigRequestParameters params=new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionID", sessionID);
		result=clientHelper.execute(url, params, MassVigClientHelper.GET);	
		return result;	
	}
	public String delteCollection(String sessionID,int productID){
		String result="";
		String url=getCollectURL("DeleteCollection");
		MassVigRequestParameters params=new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid",udid);
		params.add("sessionID", sessionID);
		params.add("productID", productID+"");
		result=clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	public String AddOption(String sessionID,String Option){
		String result="";
		String url=getAccountURL("AddOption");
		MassVigRequestParameters params=new MassVigRequestParameters();
		params.add("sessionID", sessionID);
		try {
			params.add("Option", URLEncoder.encode(Option, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		result=clientHelper.execute(url, params, MassVigClientHelper.POST);
		return result;	
	}
	
	/**
	 * 添加评论
	 * @param sessionid
	 * @param productID
	 * @param comment 254个字符内
	 * @return
	 */
	public String AddAdvice(String sessionid, int productID, String comment){
		String result = "";
		String url = getProductURL("AddAdvice");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("productID", productID + "");
		try {
			params.add("comment", URLEncoder.encode(comment, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 获取评论
	 * @param sessionid
	 * @param productID
	 * @param startIndex
	 * @return
	 */
	public String GetAdvices(String sessionid, String productID, String startIndex){
		String result = "";
		String url = getProductURL("GetAdvices");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("productID", productID + "");
		params.add("startIndex", startIndex + "");
		params.add("takeNum", "10");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 获取热门搜索
	 * @param sessionID
	 * @return
	 */
	public String GetHotSearchword(){
		String result = "";
		String url = getProductURL("GetHotSearchword");
		URL u;
		try {
			u = new URL(url + "&" + "merchantid=" + merchantid + "&udid=" + udid);
			result = HttpConnect(u);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
//		MassVigRequestParameters params = new MassVigRequestParameters();
//		params.add("merchantid", merchantid);
//		params.add("udid", udid);
//		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
//		return result;
	}

	/**
	 * 搜索商品列表
	 * 
	 * @return
	 */
	public String SearchProduct(String searchWord, String mcids, String minprice, String maxprice, String startIndex, String orderby) {
//		return "{ResponseStatus: 0,ResponseMsg: null,ResponseData: [{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"}]}";
		String result = "";
		String url = getProductURL("SearchProduct");
		MassVigRequestParameters params = new MassVigRequestParameters();
		try {
			params.add("searchWord", URLEncoder.encode(searchWord, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("mcid", mcids);
		params.add("minprice", minprice);
		params.add("maxprice", maxprice);
		params.add("startindex", startIndex);
		params.add("takeNum", "10");
		params.add("orderby", orderby);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}

	/**
	 * 获取商品详情
	 * 
	 * @return
	 */
	public String GetProduct(String sessionID, String pid) {
//		return "{ResponseStatus: 0,ResponseMsg: null,ResponseData: [{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"}]}";
		String result = "";
		String url = getProductURL("GetProduct");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("sessionID", sessionID);
		params.add("udid", udid);
		params.add("pid", pid);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}

	/**
	 * 获取商品规格
	 * 
	 * @return
	 */
	public String GetProductSpecInfo(String pid) {
//		return "{ResponseStatus: 0,ResponseMsg: null,ResponseData: {ProductSpecs: [{ProductSpecID: 2,Price: 299.37,ProductProperties: \"2,5,8\"},{ProductSpecID: 3,Price: 299.37,ProductProperties: \"3,4,7\"},{ProductSpecID: 5,Price: 239.27,ProductProperties: \"1,4,7\"},{ProductSpecID: 6,Price: 239.27,ProductProperties: \"3,5,7\"}],ProductProperties: [{PropertyID: 1,Name: \"颜色分类\",ppvaluesDyn: [{ValueID: 1,Name: \"黄色黄色黄色黄色黄色黄色黄色黄色黄色黄色黄色黄色\"},{ValueID: 2,Name: \"红色红色红色红色红色\"},{ValueID: 3,Name: \"蓝色\"}]},{PropertyID: 2,Name: \"适用床尺寸\",ppvaluesDyn: [{ValueID: 4,Name: \"1.8m\"},{ValueID: 5,Name: \"1.5m\"}]},{PropertyID: 3,Name: \"适用123床尺寸\",ppvaluesDyn: [{ValueID: 7,Name: \"1.8m\"},{ValueID: 8,Name: \"1.5m\"}]}]}}";
		String result = "";
		String url = getProductURL("GetProductSpecInfo");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("pid", pid);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}

	/**
	 * 获取商品图片
	 * 
	 * @return
	 */
	public String GetProdcutImgs(String pid) {
//		return "{ResponseStatus: 0,ResponseMsg: null,ResponseData: [{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"},{ProductID: 1,Name: \"红富士家纺 床上用品 四件套 婚庆 提花床上四件套全棉包邮纯棉\",MinPrice: 299.37,Volume: 23,MainImgUrl: \"http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg\"}]}";
		String result = "";
		String url = getProductURL("GetProdcutImgs");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("pid", pid);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}

	/**
	 * 添加商品评论
	 * 
	 * @return
	 */
	public String AddComment(String sessionid, int productID, String atCustomerID, String comment) {
//		return "{ResponseStatus: 0,ResponseMsg: null,ResponseData: {ProductSpecs: [{ProductSpecID: 2,Price: 299.37,ProductProperties: \"2,5,8\"},{ProductSpecID: 3,Price: 299.37,ProductProperties: \"3,4,7\"},{ProductSpecID: 5,Price: 239.27,ProductProperties: \"1,4,7\"},{ProductSpecID: 6,Price: 239.27,ProductProperties: \"3,5,7\"}],ProductProperties: [{PropertyID: 1,Name: \"颜色分类\",ppvaluesDyn: [{ValueID: 1,Name: \"黄色黄色黄色黄色黄色黄色黄色黄色黄色黄色黄色黄色\"},{ValueID: 2,Name: \"红色红色红色红色红色\"},{ValueID: 3,Name: \"蓝色\"}]},{PropertyID: 2,Name: \"适用床尺寸\",ppvaluesDyn: [{ValueID: 4,Name: \"1.8m\"},{ValueID: 5,Name: \"1.5m\"}]},{PropertyID: 3,Name: \"适用123床尺寸\",ppvaluesDyn: [{ValueID: 7,Name: \"1.8m\"},{ValueID: 8,Name: \"1.5m\"}]}]}}";
		String result = "";
		String url = getProductURL("AddComment");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("productID", productID + "");
		params.add("sessionid", sessionid);
		params.add("atCustomerID", atCustomerID);
		try {
			params.add("comment", URLEncoder.encode(comment, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}

	/**
	 * 获取商品评论
	 * 
	 * @return
	 */
	public String GetComment(int productID, int startIndex, int takeNum) {
//		return "{ResponseStatus: 0,ResponseMsg: null,ResponseData: {ProductSpecs: [{ProductSpecID: 2,Price: 299.37,ProductProperties: \"2,5,8\"},{ProductSpecID: 3,Price: 299.37,ProductProperties: \"3,4,7\"},{ProductSpecID: 5,Price: 239.27,ProductProperties: \"1,4,7\"},{ProductSpecID: 6,Price: 239.27,ProductProperties: \"3,5,7\"}],ProductProperties: [{PropertyID: 1,Name: \"颜色分类\",ppvaluesDyn: [{ValueID: 1,Name: \"黄色黄色黄色黄色黄色黄色黄色黄色黄色黄色黄色黄色\"},{ValueID: 2,Name: \"红色红色红色红色红色\"},{ValueID: 3,Name: \"蓝色\"}]},{PropertyID: 2,Name: \"适用床尺寸\",ppvaluesDyn: [{ValueID: 4,Name: \"1.8m\"},{ValueID: 5,Name: \"1.5m\"}]},{PropertyID: 3,Name: \"适用123床尺寸\",ppvaluesDyn: [{ValueID: 7,Name: \"1.8m\"},{ValueID: 8,Name: \"1.5m\"}]}]}}";
		String result = "";
		String url = getProductURL("GetComment");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("productID", productID + "");
		params.add("startIndex", startIndex + "");
		params.add("takeNum", takeNum + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}

	/**
	 * 赞一个商品
	 * 
	 * @return
	 */
	public String PraiseProduct(String sessionid, int productID) {
//		return "{ResponseStatus: 0,ResponseMsg: null,ResponseData: {ProductSpecs: [{ProductSpecID: 2,Price: 299.37,ProductProperties: \"2,5,8\"},{ProductSpecID: 3,Price: 299.37,ProductProperties: \"3,4,7\"},{ProductSpecID: 5,Price: 239.27,ProductProperties: \"1,4,7\"},{ProductSpecID: 6,Price: 239.27,ProductProperties: \"3,5,7\"}],ProductProperties: [{PropertyID: 1,Name: \"颜色分类\",ppvaluesDyn: [{ValueID: 1,Name: \"黄色黄色黄色黄色黄色黄色黄色黄色黄色黄色黄色黄色\"},{ValueID: 2,Name: \"红色红色红色红色红色\"},{ValueID: 3,Name: \"蓝色\"}]},{PropertyID: 2,Name: \"适用床尺寸\",ppvaluesDyn: [{ValueID: 4,Name: \"1.8m\"},{ValueID: 5,Name: \"1.5m\"}]},{PropertyID: 3,Name: \"适用123床尺寸\",ppvaluesDyn: [{ValueID: 7,Name: \"1.8m\"},{ValueID: 8,Name: \"1.5m\"}]}]}}";
		String result = "";
		String url = getProductURL("PraiseProduct");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("productID", productID + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}

	/**
	 * 踩一个商品
	 * 
	 * @return
	 */
	public String StampProduct(String sessionid, int productID) {
//		return "{ResponseStatus: 0,ResponseMsg: null,ResponseData: {ProductSpecs: [{ProductSpecID: 2,Price: 299.37,ProductProperties: \"2,5,8\"},{ProductSpecID: 3,Price: 299.37,ProductProperties: \"3,4,7\"},{ProductSpecID: 5,Price: 239.27,ProductProperties: \"1,4,7\"},{ProductSpecID: 6,Price: 239.27,ProductProperties: \"3,5,7\"}],ProductProperties: [{PropertyID: 1,Name: \"颜色分类\",ppvaluesDyn: [{ValueID: 1,Name: \"黄色黄色黄色黄色黄色黄色黄色黄色黄色黄色黄色黄色\"},{ValueID: 2,Name: \"红色红色红色红色红色\"},{ValueID: 3,Name: \"蓝色\"}]},{PropertyID: 2,Name: \"适用床尺寸\",ppvaluesDyn: [{ValueID: 4,Name: \"1.8m\"},{ValueID: 5,Name: \"1.5m\"}]},{PropertyID: 3,Name: \"适用123床尺寸\",ppvaluesDyn: [{ValueID: 7,Name: \"1.8m\"},{ValueID: 8,Name: \"1.5m\"}]}]}}";
		String result = "";
		String url = getProductURL("StampProduct");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("productID", productID + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 获取广告列表
	 * @param position
	 * @return
	 */
	public String GetAdvertises(String positions) {
//		return "{ResponseStatus: 0,ResponseMsg: null,ResponseData: {ProductSpecs: [{ProductSpecID: 2,Price: 299.37,ProductProperties: \"2,5,8\"},{ProductSpecID: 3,Price: 299.37,ProductProperties: \"3,4,7\"},{ProductSpecID: 5,Price: 239.27,ProductProperties: \"1,4,7\"},{ProductSpecID: 6,Price: 239.27,ProductProperties: \"3,5,7\"}],ProductProperties: [{PropertyID: 1,Name: \"颜色分类\",ppvaluesDyn: [{ValueID: 1,Name: \"黄色黄色黄色黄色黄色黄色黄色黄色黄色黄色黄色黄色\"},{ValueID: 2,Name: \"红色红色红色红色红色\"},{ValueID: 3,Name: \"蓝色\"}]},{PropertyID: 2,Name: \"适用床尺寸\",ppvaluesDyn: [{ValueID: 4,Name: \"1.8m\"},{ValueID: 5,Name: \"1.5m\"}]},{PropertyID: 3,Name: \"适用123床尺寸\",ppvaluesDyn: [{ValueID: 7,Name: \"1.8m\"},{ValueID: 8,Name: \"1.5m\"}]}]}}";
		String result = "";
		String url = getProductURL("GetAdvertises");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("positions", positions);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}

	/**
	 * 社区promotion
	 */
	/**
	 * 获取用户参加过的活动
	 * @param sessionid
	 * @return
	 */
	public String GetUserCampaigns(String sessionid) {
		String result = "";
		String url = getpromotionURL("GetUserCampaigns");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 参加分享活动
	 * @param sessionid
	 * @return
	 */
	public String ShareCampaign(String sessionid,int campaignID, String comment, String imgUrl, double lon, double lat, String address) {
		String result = "";
		String url = getSharedURL("ShareCampaign");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("campaignID", campaignID + "");
		try {
			params.add("comment", URLEncoder.encode(comment, "UTF-8"));
			params.add("address", URLEncoder.encode(address, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		params.add("imgUrl", imgUrl);
		params.add("lon", lon + "");
		params.add("lat", lat + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 发表原始分享
	 * @param sessionid
	 * @return
	 */
	public String ShareOrigin(String sessionid,String comment, String imgUrl, double lon, double lat, String address) {
		String result = "";
		String url = getSharedURL("ShareOrigin");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		try {
			params.add("comment", URLEncoder.encode(comment, "UTF-8"));
			params.add("address", URLEncoder.encode(address, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		params.add("imgUrl", imgUrl);
		params.add("lon", lon + "");
		params.add("lat", lat + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 删除分享
	 * @param sessionid
	 * @return
	 */
	public String DeleteShare(String sessionid, int shareID) {
		String result = "";
		String url = getSharedURL("DeleteShare");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("shareID", shareID + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 获取活动详情
	 * @param sessionid
	 * @return
	 */
	public String GetCampaignInfo(String sessionid, int campaignID) {
		String result = "";
		String url = getpromotionURL("GetCampaignInfo");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("campaignID", campaignID + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 得到关联到活动的分享列表
	 * @param sessionid
	 * @return
	 */
	public String GetCampaignShare(String sessionid, int campaignID, int startIndex, int takeNum) {
		String result = "";
		String url = getSharedURL("GetCampaignShare");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("campaignID", campaignID + "");
		params.add("startIndex", startIndex + "");
		params.add("takeNum", takeNum + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 添加分享评论
	 * @param sessionid
	 * @return
	 */
	public String AddPostComment(String sessionid,int shareID, String atCustomerID, String comment) {
		String result = "";
		String url = getSharedURL("AddComment");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("sessionid", sessionid);
		params.add("shareID", shareID + "");
		params.add("atCustomerID", atCustomerID + "");
		try {
			params.add("comment", URLEncoder.encode(comment, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 得到分享评论列表
	 * @param sessionid
	 * @param shareID
	 * @param startIndex
	 * @param takeNum
	 * @return
	 */
	public String GetPostComment(int shareID, int startIndex, int takeNum) {
		String result = "";
		String url = getSharedURL("GetComment");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		params.add("shareID", shareID + "");
		params.add("startIndex", startIndex + "");
		params.add("takeNum", takeNum + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 搜索分享列表
	 * @param keyWord
	 * @param startIndex
	 * @param takeNum
	 * @return
	 */
	public String SearchShare(String keyWord, int startIndex, int takeNum) {
		String result = "";
		String url = getSharedURL("SearchShare");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		try {
			params.add("keyWord", URLEncoder.encode(keyWord, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		params.add("startIndex", startIndex + "");
		params.add("takeNum", takeNum + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	public interface timeoutListener {
		void TimeOutListener();
	}
	
	
	/**
	 * 用户
	 */
	/**
	 * 发送验证码
	 * @param verifyBody
	 * @return
	 */
	public String CheckLoginNameAndSendVerifyCode(String verifyBody) {
		String result = "";
		String url = getAccountURL("CheckLoginNameAndSendVerifyCode");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("verifyBody", verifyBody);
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	/**
	 * 用户
	 */
	/**
	 * @param verifyBody
	 * @return
	 */
	public String SendVerifyCodeWithCheck(String verifyBody) {
		String result = "";
		String url = getAccountURL("SendVerifyCodeWithCheck");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("verifyBody", verifyBody);
		params.add("merchantid", merchantid);
		params.add("udid", udid);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	/**
	 * 
	 * @param verifyBody
	 * @return t or f
	 */
	public String NeedRegisterCode(String verifyBody){
		String result = "";
		String url = getAccountURL("NeedRegisterCode");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("udid", udid);
		params.add("verifyBody", verifyBody);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	/**
	 * 注册
	 * @param verifyCode
	 * @param password
	 * @return
	 */
	public String Register(String loginName, String verifyCode, String password) {
		String result = "";
		String url = getAccountURL("Register");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("verifyCode", verifyCode);
		params.add("merchantid", merchantid);
		params.add("loginName", loginName);
		params.add("password", password);
		params.add("udid", udid);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 登录
	 * @param loginName
	 * @param password
	 * @return
	 */
	public String Login(String loginName, String password) {
		String result = "";
		String url = getAccountURL("Login");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("loginName", loginName);
		params.add("password", password);
		params.add("udid", udid);
		params.add("merchantid", merchantid);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 验证session
	 * @param sessionID
	 * @return
	 */
	public String VerifySession(String sessionID) {
		String result = "";
		String url = getAccountURL("VerifySession");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("sessionID", sessionID);
		params.add("udid", udid);
		params.add("merchantid", merchantid);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 找回密码
	 * @param loginName
	 * @return
	 */
	public String FindPassword(String loginName, String verifyCode, String password) {
		String result = "";
		String url = getAccountURL("FindPassword");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("loginName", loginName);
		params.add("verifyCode", verifyCode);
		params.add("newPassword", password);
		params.add("udid", udid);
		params.add("merchantid", merchantid);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 修改密码
	 * @param sessionID
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 */
	public String ModifyPassword(String sessionID, String oldPassword, String newPassword) {
		String result = "";
		String url = getAccountURL("ModifyPassword");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("sessionID", sessionID);
		params.add("udid", udid);
		params.add("merchantid", merchantid);
		params.add("oldPassword", oldPassword);
		params.add("newPassword", newPassword);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 修改用户信息
	 * @param sessionID
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 */
	public String ModifyCustomInfo(String sessionID, String nickName, String headImgUrl, int gender) {
		String result = "";
		String url = getAccountURL("ModifyCustomInfo");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("sessionID", sessionID);
		try {
			params.add("nickName", URLEncoder.encode(nickName, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		params.add("udid", udid);
		params.add("merchantid", merchantid);
		params.add("headImgUrl", headImgUrl);
		params.add("gender", gender + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 修改手机号
	 * @param sessionID
	 * @param verifyCode
	 * @return
	 */
	public String ModifyMobile(String sessionID, String verifyCode) {
		String result = "";
		String url = getAccountURL("ModifyMobile");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("sessionID", sessionID);
		params.add("udid", udid);
		params.add("merchantid", merchantid);
		params.add("verifyCode", verifyCode);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 修改邮箱
	 * @param sessionID
	 * @param verifyCode
	 * @return
	 */
	public String ModifyEmail(String sessionID, String verifyCode) {
		String result = "";
		String url = getAccountURL("ModifyEmail");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("sessionID", sessionID);
		params.add("udid", udid);
		params.add("merchantid", merchantid);
		params.add("verifyCode", verifyCode);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 验证验证码
	 * @param sessionID
	 * @param verifyCode
	 * @return
	 */
	public String CheckVerifyCode(String verifyBody, String verifyCode) {
		String result = "";
		String url = getAccountURL("CheckVerifyCode");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("verifyBody", verifyBody);
		params.add("udid", udid);
		params.add("merchantid", merchantid);
		params.add("verifyCode", verifyCode);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 社区用户详情一些统计信息
	 * @param customerID
	 * @return
	 */
	public String GetCustomerStatistics(int customerID) {
		String result = "";
		String url = getAccountURL("GetCustomerStatistics");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("udid", udid);
		params.add("merchantid", merchantid);
		params.add("customerID", customerID + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 提交当前用户地址
	 * @param sessionID
	 * @param lon
	 * @param lat
	 * @return
	 */
	public String SubmitLocation(String sessionID, double lon, double lat) {
		String result = "";
		String url = getAccountURL("SubmitLocation");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("udid", udid);
		params.add("merchantid", merchantid);
		params.add("sessionID", sessionID);
		params.add("lon", lon + "");
		params.add("lat", lat + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 查看附近的人
	 * @param sessionID
	 * @param lon
	 * @param lat
	 * @param startIndex
	 * @param takeNum
	 * @return
	 */
	public String GetAroundPersons(String sessionID, int Gender, double lon, double lat, int startIndex, int takeNum) {
		String result = "";
		String url = getAccountURL("GetAroundPersons");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("udid", udid);
		params.add("merchantid", merchantid);
		params.add("sessionID", sessionID);
		params.add("Gender", Gender + "");
		params.add("lon", lon + "");
		params.add("lat", lat + "");
		params.add("startIndex", startIndex + "");
		params.add("takeNum", takeNum + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 找人
	 * @param sessionID
	 * @param keyWord
	 * @param startIndex
	 * @param takeNum
	 * @return
	 */
	public String FindPeople(String sessionID, String keyWord, int startIndex, int takeNum) {
		String result = "";
		String url = getAccountURL("FindPeople");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("udid", udid);
		params.add("merchantid", merchantid);
		params.add("sessionID", sessionID);
		try {
			params.add("keyWord", URLEncoder.encode(keyWord, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		params.add("startIndex", startIndex + "");
		params.add("takeNum", takeNum + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 获取聊天界面的用户信息
	 * @param sessionID
	 * @param customerIDs
	 * @param lon
	 * @param lat
	 * @return
	 */
	public String GetMessagePageCustomerInfo(String sessionID, String customerIDs, double lon, double lat){
		String result = "";
		String url = getAccountURL("GetMessagePageCustomerInfo");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("udid", udid);
		params.add("merchantid", merchantid);
		params.add("sessionID", sessionID);
		params.add("customerIDs", customerIDs);
		params.add("lon", lon + "");
		params.add("lat", lat + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 获取聊天服务器信息
	 * @param sessionID
	 * @return
	 */
	public String GetEJServerInfo(String sessionID){
		String result = "";
		String url = getAccountURL("GetEJServerInfo");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("udid", udid);
		params.add("merchantid", merchantid);
		params.add("sessionID", sessionID);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 社区用户中心一些统计信息
	 * @param customerID
	 * @return
	 */
	public String GetCustomerCenter(String sessionID) {
		String result = "";
		String url = getAccountURL("CustomerCenter");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("udid", udid);
		params.add("merchantid", merchantid);
		params.add("sessionID", sessionID);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 获取图片上传地址
	 * @param sessionID
	 * @param verifyCode
	 * @return
	 */
	public String GetUploadImageUrl() {
		String result = "";
		String url = getAccountURL("GetUploadImageUrl");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("udid", udid);
		params.add("merchantid", merchantid);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 添加购物车
	 * @param sessionID
	 * @param productSpecID
	 * @param quantity
	 * @return
	 */
	public String AddProduct(String sessionID, String productSpecID, String quantity) {
		String result = "";
		String url = getShoppingURL("AddProduct");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("udid", udid);
		params.add("merchantid", merchantid);
		params.add("sessionID", sessionID);
		params.add("productSpecID", productSpecID);
		params.add("quantity", quantity);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 删除商品
	 * @param sessionID
	 * @param productSpecID
	 * @return
	 */
	public String DeleteProduct(String sessionID, String productSpecID) {
		String result = "";
		String url = getShoppingURL("DeleteProduct");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("udid", udid);
		params.add("merchantid", merchantid);
		params.add("sessionID", sessionID);
		params.add("productSpecID", productSpecID);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 获取购物车数量
	 * @param sessionID
	 * @return
	 */
	public String TotalNum(String sessionID) {
		String result = "";
		String url = getShoppingURL("TotalNum");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("udid", udid);
		params.add("merchantid", merchantid);
		params.add("sessionID", sessionID);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 获取购物车列表
	 * @param sessionID
	 * @return
	 */
	public String GetList(String sessionID) {
		String result = "";
		String url = getShoppingURL("GetList");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("udid", udid);
		params.add("merchantid", merchantid);
		params.add("sessionID", sessionID);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}
	
	/**
	 * 修改购物车商品数量
	 * @param sessionID
	 * @param productSpecID
	 * @param quantity
	 * @return
	 */
	public String ModifyQuantity(String sessionID, String productSpecID, String quantity) {
		String result = "";
		String url = getShoppingURL("ModifyQuantity");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("udid", udid);
		params.add("merchantid", merchantid);
		params.add("sessionID", sessionID);
		params.add("productSpecID", productSpecID);
		params.add("quantity", quantity);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}

	public String getWebContent(String url){
		String result = "";
		MassVigRequestParameters params = new MassVigRequestParameters();
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}

	public String PicUpload(String imageUrl, String fileKey, Bitmap bitmap) {
		String result = "";
		String actionUrl = imageUrl;
		String boundary="--------";
		if(!TextUtils.isEmpty(imageUrl)){

			try {
				URL url = new URL(actionUrl);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setDoInput(true);
				con.setDoOutput(true);
				con.setUseCaches(false);
				con.setRequestMethod("POST");
				con.addRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);
		        con.setRequestProperty("Charset", "UTF-8");
		        con.setRequestProperty("Connection", "Keep-Alive");
				String headerStr="--";
				headerStr=headerStr+boundary;
				headerStr=headerStr+"\r\n";
				headerStr=headerStr+"Content-Disposition: form-data; name=\"";
				headerStr=headerStr+fileKey;
				headerStr+="\"; filename=\"";
				headerStr+="1.jpg";
				headerStr+="\"";
				headerStr+="\r\n";
				headerStr+="Content-Type:image/jpeg";
				headerStr+="\r\n\r\n";
				
				byte[] postHeaderBytes = headerStr.getBytes("UTF-8");
				String bb = "\r\n--"+boundary+"\r\n";
				byte[] boundaryBytes = bb.getBytes("ASCII");
				
				DataOutputStream ds = new DataOutputStream(con.getOutputStream());

//				int width = bitmap.getWidth();
//				int height = bitmap.getHeight();
//				int w = bitmap.getWidth();
//				int h = bitmap.getHeight();
//				Bitmap tempBitmap = null;
//				if (width > 490 || height > 670) {
//					if (height / width > 670 / 490) {
//						h = 670;
//						w = 490 * h / 670;
//					} else {
//						w = 490;
//						h = 670 * w / 490;
//					}
//				}
//				tempBitmap = MassVigUtil.setBitmapSize(bitmap, 100, 100);

				File fold = new File("/sdcard/massvig");
				if (!fold.exists()) {
					fold.mkdir();
				}
				File file = new File("/sdcard/massvig/temp.png");
//				file = convertBitmapToFile(file, tempBitmap);
				file = convertBitmapToFile(file, bitmap);
				
				FileInputStream fStream = new FileInputStream(file);
				
				ds.write(postHeaderBytes);
				
				int bufferSize = 1024;
				byte[] buffer = new byte[bufferSize];
				int length = -1;
				while ((length = fStream.read(buffer)) != -1) {
					ds.write(buffer, 0, length);
				}
				
				ds.write(boundaryBytes);
				fStream.close();
				ds.flush();
				InputStream is = con.getInputStream();

				int ch;
				StringBuffer b = new StringBuffer();
				while ((ch = is.read()) != -1) {
					b.append((char) ch);
				}
				result = new String(b);
				ds.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	public String PicUpload1(String imageUrl, Bitmap bitmap) {
		String result = "";
//		String actionUrl = imageUrl;
		String actionUrl = "http://192.168.1.137:4700/UpLoadStream.ashx?uploadfiletype=1&folder=customer";
		try {
			URL url = new URL(actionUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestMethod("POST");
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());

			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			Bitmap tempBitmap = null;
			if (width > 490 || height > 670) {
				if (height / width > 670 / 490) {
					h = 670;
					w = 490 * h / 670;
				} else {
					w = 490;
					h = 670 * w / 490;
				}
			}
			tempBitmap = MassVigUtil.setBitmapSize(bitmap, w, h);

			File fold = new File("/sdcard/massvig");
			if (!fold.exists()) {
				fold.mkdir();
			}
			File file = new File("/sdcard/massvig/temp.png");
			file = convertBitmapToFile(file, tempBitmap);
			FileInputStream fStream = new FileInputStream(file);
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			int length = -1;
			while ((length = fStream.read(buffer)) != -1) {
				ds.write(buffer, 0, length);
			}
			fStream.close();
			ds.flush();
			InputStream is = con.getInputStream();

			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			result = new String(b);
			ds.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private File convertBitmapToFile(File file, Bitmap bitmap) {
		// File f= new File("/sdcard/temp.png");
		try {
			FileOutputStream out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.PNG, 0, out)) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return file;
	}

	/**
	 * 记录错误日志
	 * 
	 * @param udid
	 * @param crashLog
	 * @return
	 */
	public String pushCrashLog(String crashLog) {
		String result = "";
		String url = MassVigService.getInstance().getProductURL("RecordError");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("UDID", udid);
		params.add("ErrorMsg", crashLog + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.POST);
		return result;
	}

	/**
	 * 提交内容获取html
	 * @param content
	 * @return
	 */
	public String putWebContent(String content) {
		String result = "";
		String url = MassVigService.getInstance().getAddressURL("AnalyzeLogisticsData");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("UDID", udid);
		params.add("postData", content + "");
		result = clientHelper.execute(url, params, MassVigClientHelper.POST);
		return result;
	}
	
	/**
	 * 获取android客户端版本号
	 * @param udid
	 * @return
	 */
	public String getAndroidVersion(String sessionid,String udi){
		String result = "";
		String url = MassVigService.getInstance().getProductURL("GetAndroidVersion");
		MassVigRequestParameters params = new MassVigRequestParameters();
		params.add("udid", udi);
		params.add("merchantid", merchantid);
		params.add("sessionid", sessionid);
		result = clientHelper.execute(url, params, MassVigClientHelper.GET);
		return result;
	}

	public String getWebUrl(String url){
		String s = url + "&merchantid=" + merchantid + "&udid=" + udid;
		return s;
	}

	//TODO
	public void getCities(ContentHandler contentHandler) {
		URL url;
		try {
			// url = new
			// URL(NET_DEAL_APP_URLgetCities");

			url = new URL(SERVER_URL + "/getCities");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			conn.setConnectTimeout(20000);
			InputStream inputStream = conn.getInputStream();

			// DataInputStream s = new DataInputStream(inputStream);
			// System.out.println(s.readUTF());

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader xmlReader = parser.getXMLReader();

			xmlReader.setContentHandler(contentHandler);
			xmlReader.parse(new InputSource(inputStream));
			xmlReader.setContentHandler(null);

			inputStream.close();
			conn.disconnect();

		} catch (Exception e) {
			// TODO Debug
			e.printStackTrace();
		}
	}

	public String GetWebUrl(String descUrl) {
		String s = descUrl + "&merchantid=" + merchantid + "&udid=" + udid;
		return s;
	}
	
	//TODO delete
//	public String getCategoryListForSearchPage() {
//		URL url;
//		String result = "";
//		try {
//			url = new URL("http://service.ibuy001.com:18080/secureservice/secureservice.asmx/" + "CategoryListForSearchPageQuery");
//			result = HttpConnect(url);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return result;
//	}
	//TODO delete
//	public String getWebSiteListForSearchPage() {
//		URL url;
//		String result = "";
//		try {
//			url = new URL("http://service.ibuy001.com:18080/secureservice/secureservice.asmx/" + "WebSiteListForSearchPageQuery");
//			result = HttpConnect(url);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return result;
//	}

}
