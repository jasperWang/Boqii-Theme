package com.massvig.ecommerce.widgets;

import android.view.View;

public abstract class ViewHolderModel{
	public ViewHolderModel(){}
	public abstract void initViewHoler(View convertView);
	public abstract void setViewHolerValues(View convertView, int position,Object itemData);
}
