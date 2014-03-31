package com.massvig.ecommerce.widgets;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.entities.MyOrder;
import com.massvig.ecommerce.entities.MyOrderList;
import com.massvig.ecommerce.managers.OrdersManager;
import com.massvig.ecommerce.utilities.MassVigContants;
import com.massvig.ecommerce.utilities.MassVigUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * order列表适配器
 * @author DuJun
 *
 */
public class OrderAdapter extends BaseAdapter{

	private MyOrderList myorderList;
	private LayoutInflater mInflater;
	private ArrayList<NetImageView> imageList = new ArrayList<NetImageView>();
	private Bitmap shopDefaultImg;
	private Context mContext;
	
	public OrderAdapter(Context context, MyOrderList list, int orderTab){
		this.myorderList = list;
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
		shopDefaultImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_icon);
	}
	
	public void setDataList(MyOrderList list){
		this.myorderList = list;
	}
	
	@Override
	public int getCount() {
		return myorderList.getCount();
	}

	@Override
	public Object getItem(int position) {
		return myorderList.getMyOrder(position);
	}

	@Override
	public long getItemId(int position) {
		return Long.valueOf(myorderList.getMyOrderID(position));
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			imageList.add(holder.imageview);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		convertView = holder.getView(position);
		return convertView;
	}
	
	class ViewHolder{
		private NetImageView imageview;
		private TextView name,data,no,price,number,status;
		private View itemView;
		
		public ViewHolder(){
			itemView = mInflater.inflate(R.layout.order_item, null);
			itemView.setTag(this);
			imageview = (NetImageView)itemView.findViewById(R.id.imageview);
			name = (TextView)itemView.findViewById(R.id.name);
			data = (TextView)itemView.findViewById(R.id.date);
			no = (TextView)itemView.findViewById(R.id.no);
			price = (TextView)itemView.findViewById(R.id.price);
			number = (TextView)itemView.findViewById(R.id.number);
			status = (TextView)itemView.findViewById(R.id.status);
		}
		
		public View getView(int position){
			final MyOrder o = myorderList.getMyOrder(position);
			String detail = o.OrderDetails;
			if(!TextUtils.isEmpty(detail)){
				JSONArray array;
				JSONObject data;
				try {
					array = new JSONArray(detail);
					data = array.getJSONObject(0);
					if(!o.IsWebOrder)
						imageview.setImageUrl(MassVigUtil.GetImageUrl(data.optString("MainImgUrl"), 70, 70), MassVigContants.PATH, shopDefaultImg);
					else
						imageview.setImageUrl(data.optString("MainImgUrl"), MassVigContants.PATH, shopDefaultImg);
					name.setText(data.optString("ProductName"));
					number.setText(mContext.getString(R.string.total_goods, data.optString("Quantity")));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			data.setText(mContext.getString(R.string.order_data, o.CreateTime));
			no.setText(mContext.getString(R.string.order_no, o.OrderNo));
			switch (o.OrderStatus) {
			case OrdersManager.Unpay:
				status.setText(mContext.getString(R.string.unpay_text));
				break;
			case OrdersManager.WaitingMerchantDeliver:
				status.setText(mContext.getString(R.string.waitingMerchantDeliver_text));
				break;
			case OrdersManager.Shipped:
				status.setText(mContext.getString(R.string.shipped_text));
				break;
			case OrdersManager.Accomplished:
				status.setText(mContext.getString(R.string.accomplished_text));
				break;
			case OrdersManager.WaitingMerchantRefund:
				status.setText(mContext.getString(R.string.waitingMerchantRefund_text));
				break;
			case OrdersManager.WaitingCustomerReturn:
				status.setText(mContext.getString(R.string.waitingCustomerReturn_text));
				break;
			case OrdersManager.Refunded:
				status.setText(mContext.getString(R.string.refunded_text));
				break;
			case OrdersManager.Closed:
				status.setText(mContext.getString(R.string.closed_text));
				break;
			case OrdersManager.Deleted:
				status.setText(mContext.getString(R.string.deleted_text));
				break;

			default:
				break;
			}
			price.setText(o.PayAmount + "");
			return itemView;
		}
	}
	
	public ArrayList<NetImageView> getImageList() {
		return this.imageList;
	}
	
}
