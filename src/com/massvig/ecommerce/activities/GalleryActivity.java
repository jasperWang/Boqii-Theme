package com.massvig.ecommerce.activities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.massvig.ecommerce.boqi.R;
import com.massvig.ecommerce.widgets.AsyncImageLoader;
import com.massvig.ecommerce.widgets.AsyncImageLoader.ImageCallback;
import com.massvig.ecommerce.widgets.ImageAdapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;

public class GalleryActivity extends BaseActivity {

	private ArrayList<Bitmap> imageList;
	private Gallery mGallery;
	private ImageAdapter imageAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallerylayout);
		mGallery = (Gallery) findViewById(R.id.gallery);
		mGallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				GalleryActivity.this.finish();
			}
		});
		imageList = new ArrayList<Bitmap>();
//		for(int i=0;i<5;i++){
//			String url = "http://img01.taobaocdn.com/bao/uploaded/i1/184207774/T2X_zdXh0aXXXXXXXX_!!184207774.jpg_310x310.jpg";
//			Bitmap bitmap = AsyncImageLoader.loadBitmap(url,
//					new ImageCallback() {
//						@Override
//						public void imageLoaded(
//								Bitmap imageDrawable,
//								String imageUrl) {
//							// TODO
//							imageList.add(imageDrawable);
//							mGallery.setSelection(10000 * imageList.size());
//							imageAdapter.notifyDataSetChanged();
//						}
//					});
//			if (bitmap != null) {
//				// TODO
//				imageList.add(bitmap);
//			}
//		}
		String list = this.getIntent().getStringExtra("IMAGES");
		if (!TextUtils.isEmpty(list)) {
			try {
				JSONArray array = new JSONArray(list);
				if (array != null && array.length() > 0) {
					for (int i = 0; i < array.length(); i++) {
						String url = array.getString(i);
						if (!TextUtils.isEmpty(url)) {
							Bitmap bitmap = AsyncImageLoader.loadBitmap(url,
									new ImageCallback() {
										@Override
										public void imageLoaded(
												Bitmap imageDrawable,
												String imageUrl) {
											// TODO
											imageList.add(imageDrawable);
											mGallery.setSelection(10000 * imageList.size());
											imageAdapter.notifyDataSetChanged();
										}
									});
							if (bitmap != null) {
								// TODO
								imageList.add(bitmap);
							}
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		imageAdapter = new ImageAdapter(this, imageList);
		mGallery.setAdapter(imageAdapter);
		mGallery.setSelection(10000 * imageList.size());
		imageAdapter.notifyDataSetChanged();
	}

}
