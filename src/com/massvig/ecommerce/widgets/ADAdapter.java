package com.massvig.ecommerce.widgets;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.massvig.ecommerce.activities.ActionDetailActivity;
import com.massvig.ecommerce.activities.AdGoodsListActivity;
import com.massvig.ecommerce.activities.GoodsDetailActivity;
import com.massvig.ecommerce.activities.MainActivity;
import com.massvig.ecommerce.activities.WebViewActivity;
import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.AdItem;
import com.massvig.ecommerce.entities.Advertise;
import com.massvig.ecommerce.entities.Goods;
import com.massvig.ecommerce.utilities.MassVigContants;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Gallery.LayoutParams;
import android.widget.LinearLayout;

/**
 * @author DuJun
 *
 */
public class ADAdapter extends BaseAdapter{

	public ArrayList<AdItem> adList = new ArrayList<AdItem>();
	private LayoutInflater mInflater;
	private int type_1 = 6;
//	private int type_2 = 5;
	private Context mContext;
	
	public static interface ViewType{
		int TYPE_ONE = 0;
		int TYPE_THREE = 1;
	}
	
	public ADAdapter(Context context, ArrayList<AdItem> list){
		this.adList = list;
		mInflater = LayoutInflater.from(context);
		this.mContext = context;
	}
	
	public void setDataList(ArrayList<AdItem> list){
		this.adList = list;
	}
	
	@Override
	public int getCount() {
		return adList.size();
	}

	@Override
	public Object getItem(int position) {
		return adList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		AdItem ad = adList.get(position);
		if(ad.AdTemplate == type_1){
			return ViewType.TYPE_ONE;
		}else{
			return ViewType.TYPE_THREE;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		AdItem it = (AdItem) getItem(position);
		if (convertView == null) {
			if(it.AdTemplate == type_1)
				holder = new ViewHolder(ViewType.TYPE_ONE);
			else
				holder = new ViewHolder(ViewType.TYPE_THREE);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		convertView = holder.getView(position);
		return convertView;
	}
	
	class ViewHolder{
		private NetImageView image;
		private NetImageView image1,image2,image3;
		private View itemView;
		
		public ViewHolder(int type){
			if(type == ViewType.TYPE_ONE){
				itemView = mInflater.inflate(R.layout.type_one, null);
				image = (NetImageView) itemView.findViewById(R.id.image);
			}else{
				itemView = mInflater.inflate(R.layout.type_three, null);
				image1 = (NetImageView) itemView.findViewById(R.id.image1);
				image2 = (NetImageView) itemView.findViewById(R.id.image2);
				image3 = (NetImageView) itemView.findViewById(R.id.image3);
			}
			itemView.setTag(this);
		}
		
		public View getView(int position){
			AdItem ad = (AdItem) getItem(position);
			String adRegions = ad.AdRegions;
			if(ad.AdTemplate == type_1){
				try {
					JSONArray array = new JSONArray(adRegions);
					if(array != null && array.length() > 0){
						JSONObject o = array.getJSONObject(0);
						final Advertise a = new Advertise();
						a.Link = o.optString("Link");
						a.LinkType = o.optInt("LinkType");
						String imgurl = o.optString("ImgUrl");
//						String imgurl = MassVigUtil.GetImageUrl(o.optString("ImgUrl"), 640, 300);
						image.setImageUrl(imgurl, MassVigContants.MAINPATH, null);
						image.setScaleType(ImageView.ScaleType.FIT_XY);
//						image.setLayoutParams(new LinearLayout.LayoutParams(
//								LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
						image.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								setClick(a);
							}
						});
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else{
				try {
					JSONArray array = new JSONArray(adRegions);
					if(array != null && array.length() > 0){
						for (int i = 0; i < array.length(); i++) {
							JSONObject o = array.getJSONObject(i);
							final Advertise a = new Advertise();
							a.Link = o.optString("Link");
							a.LinkType = o.optInt("LinkType");
							String imgurl = o.optString("ImgUrl");
//							String imgurl = "";
							
							switch (i) {
							case 0:
//								imgurl = MassVigUtil.GetImageUrl(o.optString("ImgUrl"), 300, 300);
								image1.setImageUrl(imgurl, MassVigContants.MAINPATH, null);
								image1.setScaleType(ImageView.ScaleType.FIT_XY);
//								image1.setLayoutParams(new LinearLayout.LayoutParams(
//										LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
								image1.setOnClickListener(new OnClickListener() {
									
									@Override
									public void onClick(View v) {
										setClick(a);
									}
								});
								break;
							case 1:
//								imgurl = MassVigUtil.GetImageUrl(o.optString("ImgUrl"), 300, 125);
								image2.setImageUrl(imgurl, MassVigContants.MAINPATH, null);
								image2.setScaleType(ImageView.ScaleType.FIT_XY);
								image2.setOnClickListener(new OnClickListener() {
									
									@Override
									public void onClick(View v) {
										setClick(a);
									}
								});
								break;
							case 2:
//								imgurl = MassVigUtil.GetImageUrl(o.optString("ImgUrl"), 300, 125);
								image3.setImageUrl(imgurl, MassVigContants.MAINPATH, null);
								image3.setScaleType(ImageView.ScaleType.FIT_XY);
								image3.setOnClickListener(new OnClickListener() {
									
									@Override
									public void onClick(View v) {
										setClick(a);
									}
								});
								break;

							default:
								break;
							}
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return itemView;
		}

		protected void setClick(Advertise a) {
			if (a.LinkType == MainActivity.WEBURL) {
				mContext.startActivity(new Intent(mContext,
						WebViewActivity.class).putExtra("URL", a.Link));
			} else if (a.LinkType == MainActivity.PRODUCTID) {
				Goods good = new Goods();
				good.productID = Integer.valueOf(a.Link);
				mContext.startActivity(new Intent(mContext,
						GoodsDetailActivity.class).putExtra("goods",
						good));
			}else if(a.LinkType == MainActivity.GOODSLIST){
				String params = a.Link;
				mContext.startActivity(new Intent(mContext, AdGoodsListActivity.class).putExtra("PARAMS", params));
			}else if(a.LinkType == MainActivity.CAMPAIGN){
				int camID = Integer.valueOf(a.Link);
				if(camID > 0)
					mContext.startActivity(new Intent(mContext, ActionDetailActivity.class).putExtra("CAMPAIGNID", camID));
			}
		}

	}

}
