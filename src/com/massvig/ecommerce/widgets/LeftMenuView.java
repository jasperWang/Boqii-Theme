package com.massvig.ecommerce.widgets;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.utilities.MassVigUtil;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;
/**
 * @author zhangbp
 *
 */
public class LeftMenuView extends LinearLayout implements OnClickListener{
	private int index = 0,mLastChildCount;
	private LeftMeunCallBack leftMeunCallBack;
	private ListView listview;
	private Button search_btn;
	private Context mContext;
	public LeftMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	
	public void setAdapter(ActionAdapter adapter){
		listview.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				leftMeunCallBack.listCallBack((int) id);
			}});
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);
		int count = getChildCount();
		if (changed||mLastChildCount!=count) {
			mLastChildCount = count;
			int i = 0;
			index = 0;
			for(;i<count;i++){
				if(i != 0 && i != 1 && i != 2 && i != 8 && i != 9){
					index += 1;
					View chv = getChildAt(i);
					chv.setTag(index);
					chv.setOnClickListener(this);
				}
			}
			LinearLayout linear = (LinearLayout)getChildAt(0);
			search_btn = (Button)linear.findViewById(R.id.search_btn);
			final EditText search_edit = (EditText)linear.findViewById(R.id.search_edit);
			search_edit.setOnEditorActionListener(new OnEditorActionListener() {
				
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					// TODO Auto-generated method stub
					if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED){
						if(!TextUtils.isEmpty(search_edit.getText().toString()))
						{
							MassVigUtil.setPreferenceStringData(mContext, "SEARCH_KEYWORD", search_edit.getText().toString());
							leftMeunCallBack.callBack(6, "社区动态");
						}else{
							Toast.makeText(mContext, mContext.getString(R.string.no_key), Toast.LENGTH_SHORT).show();
						}
					}
					return false;
				}
			});
			search_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					MassVigUtil.setPreferenceStringData(mContext, "SEARCH_KEYWORD", search_edit.getText().toString());
					leftMeunCallBack.callBack(6, "社区动态");
				}
			});
			((View)getChildAt(8)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					listview.setVisibility(listview.isShown() ? View.INVISIBLE : View.VISIBLE);
				}
			});
		}
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int tag = (Integer)v.getTag();
		System.out.println(tag);
		if(leftMeunCallBack != null){
			leftMeunCallBack.callBack(tag,((TextView)v).getText().toString());
		}
	}
	public void setOnclickCallBack(final LeftMeunCallBack leftMeunCallBack){
		this.leftMeunCallBack = leftMeunCallBack;
		listview = (ListView)findViewById(R.id.listview);
	}
	public interface LeftMeunCallBack{
		public void callBack(int index,String name);
		public void listCallBack(int actionID);
	}
	
	
	

}
