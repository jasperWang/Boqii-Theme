package com.massvig.ecommerce.utilities;

import com.massvig.ecommerce.widgets.RequestParameters;


/**
 * MassVigRequestParam
 * @author zhangbp
 * 
 */

public class MassVigRequestParam {
	/**
	 * 返回团购列表参数
	 * @author zhangbp
	 * 
	 */
	public static RequestParameters getDealsParam(){
		RequestParameters mRequestParameters = new RequestParameters();
		mRequestParameters.add("cityId", "339");
		mRequestParameters.add("categoryId", "0");
		mRequestParameters.add("subCategoryId", "-1");
		mRequestParameters.add("lng", "0");
		mRequestParameters.add("lat", "0");
		mRequestParameters.add("fromNum", "1");
		mRequestParameters.add("dealCount", "20");
		mRequestParameters.add("orderType", "12");
		mRequestParameters.add("sessionid", "");
		mRequestParameters.add("webSiteKeyID", "-1");
		return mRequestParameters;
	}
	
	/**
	 * 返回社区分布的参数
	 * @author zhangbp
	 * 
	 */
	public static RequestParameters getPostInsertCldDealTaoBaoParam(){
		RequestParameters mRequestParameters = new RequestParameters();
		mRequestParameters.add("sessionID", "");
		mRequestParameters.add("activityID", "-1"); //活动ID（分享商品时可以设置为-1）
		mRequestParameters.add("content", "");
		mRequestParameters.add("device", "Android");
		mRequestParameters.add("dealIDs", "");  //the dealIDs format:[{"Value":"aadfadf","Type":2},{"Value":"123123","Type":1}]，备注： Type=1 为团购商品，Type=2 为淘宝商品
		mRequestParameters.add("lng", "0");
		mRequestParameters.add("lat", "0");
		mRequestParameters.add("addr", "");
		mRequestParameters.add("refUserID", "-1"); //来源分享的用户ID
		mRequestParameters.add("refPostID", "-1"); 
		mRequestParameters.add("flag", "1"); //1 表示分享，0表示评论
		return mRequestParameters;
	}
}
