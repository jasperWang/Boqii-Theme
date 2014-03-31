package com.massvig.ecommerce.activities;

import java.io.File;
import java.io.FileNotFoundException;

import com.massvig.ecommerce.boqi.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class CropActivity extends Activity implements OnClickListener{

	private ImageView image;
	private Button not,yes;
	private Uri u;
	private Bitmap bitmap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crop);
		String uri = this.getIntent().getStringExtra("URI");
		u = Uri.fromFile(new File(uri));
		image = (ImageView)findViewById(R.id.image);
		if(u != null){
			bitmap = decodeUriAsBitmap(u);
			image.setImageBitmap(bitmap);
		}
		not = (Button)findViewById(R.id.back);
		yes = (Button)findViewById(R.id.finish);
		((Button)findViewById(R.id.cancel)).setOnClickListener(this);
		not.setOnClickListener(this);
		yes.setOnClickListener(this);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(bitmap != null)
			bitmap.recycle();
		bitmap = null;
		System.gc();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			setResult(RESULT_OK);
			finish();
			break;
		case R.id.finish:
			cropImageUri(u, 500, 500, 2);
			break;
		case R.id.cancel:
			finish();
			break;

		default:
			break;
		}
	}
	
	private Bitmap decodeUriAsBitmap(Uri uri){
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inSampleSize = 4;
			bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri),null, opt);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 2 && resultCode == RESULT_OK){
			setResult(RESULT_OK, data);
			finish();
		}
	}
	private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(u, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, requestCode);
	}

}
