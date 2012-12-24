package android.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.common.BaseFile;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.miscellaneous.Log;
import android.os.AsyncTask;
import android.os.SystemClock;

/**
 * @author djrain
 * 
 */
public class ImageLoader {
	private static final int RETRY_COUNT = 3;

	public static ImageLoader getInstance() {
		return new ImageLoader();
	}

	private boolean mIsBackgroundEnd = false;

	private Stack<String> mStack = new Stack<String>();
	private DownloadAsyncTask downloadAsyncTask;

	private static Map<String, Bitmap> POOL = new HashMap<String, Bitmap>();

	private ImageLoadingListener mListener;

	public Object mLock = new Object();

	public static interface ImageLoadingListener {
		void onImageLoaded(String url);
	}

	public void setOnImageLoadingListener(ImageLoadingListener listener) {
		mListener = listener;
	}

	public void add(final String url) {
		if (url == null || url.length() <= 0) {
			Log.l();
			return;
		}

		if (!mStack.contains(url) && !POOL.containsKey(url)) {
			Log.l("<<", url);
			synchronized (mLock) {
				mStack.push(url);
			}
			if (downloadAsyncTask == null || mIsBackgroundEnd) {
				downloadAsyncTask = new DownloadAsyncTask();
				if (downloadAsyncTask.getStatus() != AsyncTask.Status.FINISHED)
					downloadAsyncTask.execute();
			}
		}
	}

	public Bitmap get(String url) {
		if (url == null || url.length() <= 0) {
			Log.l();
			return null;
		}

		Bitmap bmp = loadFromPool(url);
		if (bmp == null) {
			bmp = loadFromFile(url);
			if (bmp != null)
				synchronized (mLock) {
					POOL.put(url, bmp);
				}
		}
		return bmp;
	}

	public Bitmap getAdd(String url) {
		if (url == null || url.length() <= 0) {
			Log.l();
			return null;
		}
		Bitmap bmp = get(url);
		if (bmp == null)
			add(url);
		return bmp;
	}
	private class DownloadAsyncTask extends AsyncTask<Void, String, String> {
		@Override
		protected String doInBackground(Void... params) {
			mIsBackgroundEnd = false;
			while (mStack.size() > 0) {

				final String url = mStack.peek();

				final Bitmap bmp = loadFromUrl(url);
				synchronized (mLock) {
					POOL.put(url, bmp);
				}

				if (bmp != null) {
					saveFile(url, bmp);
				}

				synchronized (mLock) {
					while (mStack.remove(url))
						Log.l(">>", url, mStack.size());
				}

				if (bmp != null)
					publishProgress(url);
			}

			mIsBackgroundEnd = true;
			// Log.l("끝---");
			return null;
		}
		@Override
		protected void onProgressUpdate(String... values) {
			if (mListener != null)
				mListener.onImageLoaded(values[0]);
			super.onProgressUpdate(values);
		}
	}

	private void saveFile(String url, Bitmap bmp) {
		try {
			final File file = BaseFile.getVPathfile(url);
			Log.l("??파일저장", file.getAbsoluteFile());
			FileOutputStream out = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected Bitmap loadFromPool(String url) {
		if (url == null || url.length() <= 0) {
			Log.l();
			return null;
		}

		Bitmap bmp = null;
		if (POOL.containsKey(url)) {
			bmp = POOL.get(url);
		}

		Log.l("??풀에서", url);

		return bmp;
	}

	protected Bitmap loadFromFile(String url) {
		if (url == null || url.length() <= 0) {
			Log.l();
			return null;
		}

		Bitmap bmp = null;
		File file = BaseFile.getVPathfile(url);
		if (file.exists()) {
			bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
		}

		if (bmp != null)
			Log.l("??파일에서", url);
		return bmp;
	}

	protected Bitmap loadFromUrl(String url) {
		if (url == null || url.length() <= 0) {
			Log.l();
			return null;
		}

		Bitmap bmp = null;
		for (int i = 0; i < RETRY_COUNT; i++) {
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpGet httpget = new HttpGet(url);
				HttpResponse response = httpclient.execute(httpget);
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream instream = entity.getContent();
					bmp = BitmapFactory.decodeStream(instream);
					instream.close();
				}
			} catch (Exception e) {
				Log.l(e.getMessage());
			}

			if (bmp != null)
				break;

			Log.l("!루핑 재시도", i, url);
			SystemClock.sleep(1000);
		}

		if (bmp == null)
			Log.l("!안받음 처리", url);
		else
			Log.l("??넷에서", url);

		return bmp;

	}

}