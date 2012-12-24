package android.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.common.BaseFile;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.miscellaneous.Log;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * @param activity
 *            set next code *
 * 
 *            <pre>
 * &#064;Override
 * protected void onActivityResult(int requestCode, int resultCode, Intent data) {
 * 	switch (requestCode) {
 * 		case GalleryLoader.REQUEST_CAMERA :
 * 		case GalleryLoader.REQUEST_CAMERA_CROP :
 * 		case GalleryLoader.REQUEST_CROP :
 * 		case GalleryLoader.REQUEST_GALLERY :
 * 		case GalleryLoader.REQUEST_GALLERY_CROP :
 * 			if (mGalleryLoader != null)
 * 				mGalleryLoader.onActivityResult(requestCode, resultCode, data);
 * 			return;
 * 	}
 * 	super.onActivityResult(requestCode, resultCode, data);
 * }
 * </pre>
 * @param pathfile
 * @param what
 */
public class GalleryLoader {
	static GalleryLoader INSTANCE = new GalleryLoader();
	public static GalleryLoader c() {
		return INSTANCE;
	}

	public static final String ACTION_CROP = "com.android.camera.action.CROP";
	public static final int REQUEST_CAMERA = 901;
	public static final int REQUEST_GALLERY = 902;

	public static final int REQUEST_CAMERA_CROP = 911;
	public static final int REQUEST_GALLERY_CROP = 912;
	public static final int REQUEST_CROP = 913;

	private Activity mActivity;
	private ContentResolver cr;
	private String mPathfile;
	private int w = 480;
	private int h = 800;
	private Runnable what;

	private GalleryLoader() {
	}

	public GalleryLoader set(Activity activity, String pathfile, Runnable what) {
		this.mActivity = activity;
		this.mPathfile = pathfile;
		this.what = what;
		cr = activity.getContentResolver();
		return this;
	}

	public void startCameraCrop(int w, int h) {
		this.w = w;
		this.h = h;
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
		mActivity.startActivityForResult(intent, REQUEST_CAMERA_CROP);
	}

	public void startGalleryCrop(int w, int h) {
		this.w = w;
		this.h = h;
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
		mActivity.startActivityForResult(intent, REQUEST_GALLERY_CROP);
	}

	public void startCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
		mActivity.startActivityForResult(intent, REQUEST_CAMERA);
	}

	public void startPhotoAlbum() {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
		mActivity.startActivityForResult(intent, REQUEST_GALLERY);
	}

	public void startCrop(String pathfile, int w, int h) {
		this.w = w;
		this.h = h;
//		Intent intent = new Intent(C.ACTION_CROP).setDataAndType(NoteFile.getTempUri(), "image/*");

		Intent intent = new Intent(ACTION_CROP);
		intent.setDataAndType(Uri.fromFile(new File(pathfile)), "image/*");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", w);
		intent.putExtra("aspectY", h);
		intent.putExtra("outputX", w);
		intent.putExtra("outputY", h);
		try {
			mActivity.startActivityForResult(intent, REQUEST_CROP);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}

	}

	public String getRealPathFromURI(ContentResolver cr, Uri uri) {
		String[] projection = {MediaStore.Images.Media.DATA};
		Cursor c = cr.query(uri, projection, null, null, null);
		int column_index = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		c.moveToFirst();
		return c.getString(column_index);
	}

	public void saveImage(Bitmap bitmap, String pathfile) {
		try {
			FileOutputStream fos = new FileOutputStream(pathfile);
			bitmap.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
			bitmap.recycle();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Bitmap getSampleSizeBitmap(String pathfile, int max_w, int max_h) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inSampleSize = 1;

		BitmapFactory.decodeFile(pathfile, options);
		int w = options.outWidth;
		int h = options.outHeight;
		while (w > max_w || h > max_h) {
			options.inSampleSize++;
			BitmapFactory.decodeFile(pathfile, options);
			w = options.outWidth;
			h = options.outHeight;
		}
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(pathfile, options);
	}

	public Bitmap reSampleSizeBitmap(String pathfile, int max_w, int max_h) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inSampleSize = 1;

		BitmapFactory.decodeFile(pathfile, options);
		int w = options.outWidth;
		int h = options.outHeight;
		while (w > max_w || h > max_h) {
			options.inSampleSize++;
			BitmapFactory.decodeFile(pathfile, options);
			w = options.outWidth;
			h = options.outHeight;
		}

		options.inSampleSize--;
		options.inJustDecodeBounds = false;
		Bitmap src = BitmapFactory.decodeFile(pathfile, options);
		Bitmap dst = Bitmap.createScaledBitmap(src, max_w, max_h, true);
		return dst;
	}

	private File mTempFile;

	public Uri getTempUri() {
		try {
			mTempFile = File.createTempFile("image_", ".jpg", new File(BaseFile.getRoot()));
			return Uri.fromFile(mTempFile);
		} catch (IOException e) {
			return null;
		}
	}

	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.l(requestCode, resultCode, data);

		String pathfile = null;
		Bitmap bitmap;
		switch (requestCode) {
			case GalleryLoader.REQUEST_CAMERA_CROP :
				pathfile = mTempFile.getAbsolutePath();
				startCrop(pathfile, w, h);
				break;
			case GalleryLoader.REQUEST_GALLERY_CROP :
				pathfile = getRealPathFromURI(cr, data.getData());
				startCrop(pathfile, w, h);
				break;
			case GalleryLoader.REQUEST_CROP :
				pathfile = mTempFile.getAbsolutePath();
				bitmap = reSampleSizeBitmap(pathfile, w, h);
				saveImage(bitmap, mPathfile);
				what.run();
				break;
			case GalleryLoader.REQUEST_CAMERA :
				pathfile = mTempFile.getAbsolutePath();
				bitmap = getSampleSizeBitmap(pathfile, w, h);
				saveImage(bitmap, mPathfile);
				what.run();
				break;
			case GalleryLoader.REQUEST_GALLERY :
				pathfile = getRealPathFromURI(cr, data.getData());
				bitmap = getSampleSizeBitmap(pathfile, w, h);
				saveImage(bitmap, mPathfile);
				what.run();
				break;
			default :
				return false;
		}
		return true;
	}

}