package android.view;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.miscellaneous.Log;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @author djrain
 * 
 */
public class NetImageView extends ImageView {

	private String mUrl;

	public NetImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public NetImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NetImageView(Context context) {
		super(context);
	}

	public void setImageURI(Uri uri) {
		String url = uri.toString();
		if (url == null || url.length() <= 0 || url.equals(mUrl))
			return;
		mUrl = url;
		new DownloadAsyncTask().execute();
	}

	private class DownloadAsyncTask extends AsyncTask<Void, Void, Bitmap> {
		private static final int RETRY_COUNT = 3;

		@Override
		protected Bitmap doInBackground(Void... params) {
			final String url = mUrl;
			if (url == null || url.equals(""))
				throw new IllegalArgumentException();

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
				Log.l("!못받음 처리", url);

			return bmp;
		}

		@Override
		protected void onPostExecute(Bitmap bmp) {
			NetImageView.this.setImageBitmap(bmp);
		}
	}
}