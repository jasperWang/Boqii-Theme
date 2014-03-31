package com.massvig.ecommerce.widgets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
//import java.lang.ref.SoftReference;
import java.net.URL;
//import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.massvig.ecommerce.utilities.MassVigContants;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

/**
 * 
 * @author DuJun
 */
public class AsyncImageLoader {
	/*
	 * private static class FetchImageParam {
	 * 
	 * public FetchImageParam(String imageUrl, ImageCallback imageCallback) {
	 * 
	 * this.imageCallback = imageCallback; this.imageUrl = imageUrl;
	 * 
	 * }
	 * 
	 * public String imageUrl; public ImageCallback imageCallback; }
	 * 
	 * 
	 * private static class FetchImageTask extends AsyncTask<FetchImageParam,
	 * Void, Bitmap> {
	 * 
	 * private ImageCallback imageCallback; private String imageUrl;
	 * 
	 * @Override protected Bitmap doInBackground(FetchImageParam... params) {
	 * 
	 * Bitmap result = null;
	 * 
	 * imageCallback = params[0].imageCallback; imageUrl = params[0].imageUrl;
	 * 
	 * result = loadImageFromUrl(params[0].imageUrl); imageCache.put(imageUrl,
	 * new SoftReference<Bitmap>(result));
	 * 
	 * return result; }
	 * 
	 * @Override protected void onPostExecute(Bitmap result) {
	 * 
	 * //imageCallback.imageLoaded(result, imageUrl);
	 * super.onPostExecute(result); } }
	 */
	
//	private static HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();
	private static ExecutorService executorService = Executors
			.newFixedThreadPool(5);

	public static Bitmap loadBitmap(final String imageUrl,
			final ImageCallback imageCallback) {

		if (!(new File(MassVigContants.PATH).exists())) {
			new File(MassVigContants.PATH).mkdirs();
		}
//		String imgUrl = imageUrl.replace("\\", "/");
		String imgUrl = imageUrl.replaceAll("\\\\", "/");
		String imgType = imgUrl.indexOf(".png") != -1 ? "PNG" : "";
		
		int index = imgUrl.lastIndexOf("/");
		String url = imgUrl.substring(index + 1, imgUrl.length());
		File file = null;
		if(imgType.equals("PNG")){
			 file = new File(MassVigContants.PATH + "/" + url + "_pics.pn");
		}else{
			 file = new File(MassVigContants.PATH + "/" + url + "_pics.jp");
		}
		
		if (file.exists()) {
			Bitmap bitmap = null;
			try {
				if(imgType.equals("PNG")){
//					bitmap = BitmapFactory.decodeFile(MassVigContants.PATH + "/" + url + "_pics.png");
					bitmap = BitmapFactory.decodeFile(MassVigContants.PATH + "/" + url + "_pics.pn");
				}else{
//					bitmap = BitmapFactory.decodeFile(MassVigContants.PATH + "/" + url + "_pics.jpg");
					bitmap = BitmapFactory.decodeFile(MassVigContants.PATH + "/" + url + "_pics.jp");
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			if (bitmap != null) {
				return bitmap;
			}
		}

		/*
		 * if (imageCache.containsKey(imageUrl)) { SoftReference<Bitmap>
		 * softReference = imageCache.get(imageUrl); Bitmap drawable =
		 * softReference.get(); if (drawable != null) { return drawable; } }
		 */

		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				if (imageCallback != null) {
					imageCallback.imageLoaded((Bitmap) message.obj, imageUrl);
				}
			}
		};

		executorService.submit(new Runnable() {
			public void run() {
				Bitmap drawable = loadImageFromUrl(imageUrl);
				if (drawable != null) {
					// imageCache.put(imageUrl, new
					// SoftReference<Bitmap>(drawable));
					String imgUrl = imageUrl.replaceAll("\\\\", "/");
					String imgType = imgUrl.indexOf(".png") != -1 ? "PNG" : "";
					int index = imgUrl.lastIndexOf("/");
					String url = imgUrl.substring(index + 1,
							imgUrl.length());
					File file = null;
					if(imgType.equals("PNG")){
						file = new File(MassVigContants.PATH + "/" + url + "_pics.pn");
					}else{
						file = new File(MassVigContants.PATH + "/" + url + "_pics.jp");
					}
					
					file = convertBitmapToFile(file, drawable,imgType);
					Message message = handler.obtainMessage(0, drawable);
					handler.sendMessage(message);
					
				}
			}
		});

		// new FetchImageTask().execute(new FetchImageParam(imageUrl,
		// imageCallback));

		return null;
	}

	public static Bitmap loadImageFromUrl(String url) {
		URL m;
		InputStream i = null;
		Bitmap d = null;
		try {
			m = new URL(url);
			i = (InputStream) m.getContent();
			d = BitmapFactory.decodeStream(i);
		} catch (Exception e1) {
			e1.printStackTrace();
		} catch (OutOfMemoryError e) {
			System.gc();
		}

		return d;
	}

	public interface ImageCallback {
		public void imageLoaded(Bitmap imageDrawable, String imageUrl);
	}

	private static File convertBitmapToFile(File file, Bitmap bitmap,String imgType) {
		try {
			
			FileOutputStream out = new FileOutputStream(file);
			if(imgType.equals("PNG")){
				if (bitmap.compress(Bitmap.CompressFormat.PNG, 50, out)) {
					out.flush();
					out.close();
				}
			}else{
				if (bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out)) {
					out.flush();
					out.close();
				}
			}
			
		} catch (Exception e) {

			e.printStackTrace();
		}
		return file;
	}
}
