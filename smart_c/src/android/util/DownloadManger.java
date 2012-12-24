package android.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.miscellaneous.Log;
import android.os.AsyncTask;

/**
 * @author djrain
 * 
 */
public class DownloadManger {

	private DownloadMangerCallback mCallback;

	public static interface DownloadMangerCallback {
		void start();

		void readed(Integer readed);

		void end(String pathfile);
	}

	public DownloadManger(DownloadMangerCallback callback) {
		mCallback = callback;
	}

	public void add(String url, String pathfile) {
		DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask();
		if (downloadAsyncTask.getStatus() != AsyncTask.Status.FINISHED)
			downloadAsyncTask.execute(url, pathfile);
	}

	public class DownloadAsyncTask extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... params) {

			String url = params[0];
			String pathfile = params[1];

			Log.l(url);
			Log.l(pathfile);

			if (url == null || url.equals(""))
				return null;

			File file = new File(pathfile);

			try {

				InputStream is = null;

				//1-1
				//					HttpClient httpclient = new DefaultHttpClient();
				//					HttpGet httpget = new HttpGet(url);
				//					HttpResponse response;
				//					response = httpclient.execute(httpget);
				//					HttpEntity entity = response.getEntity();
				//					if (entity != null)
				//						return (Void) null;
				//					is = entity.getContent();

				//1-2
				URL urlServer = new URL(url);
				URLConnection ucon = urlServer.openConnection();
				is = ucon.getInputStream();

				BufferedInputStream bis = new BufferedInputStream(is);
				ByteArrayBuffer baf = new ByteArrayBuffer(1024 * 1024);

				byte[] buf = new byte[1024];
				int read;
				do {
					read = bis.read(buf, 0, buf.length); // (3)
					if (read > 0)
						baf.append(buf, 0, read);
					publishProgress(baf.length());
				} while (read >= 0);

				FileOutputStream fos = new FileOutputStream(file);
				fos.write(baf.toByteArray());
				fos.close();
				is.close();

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return pathfile;
		}

		@Override
		protected void onPreExecute() {
			if (mCallback != null)
				mCallback.start();
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			if (mCallback != null)
				mCallback.readed(values[0]);
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(String result) {
			if (mCallback != null)
				mCallback.end(result);
			super.onPostExecute(result);
		}
	}

	public static void DownloadFromUrl(String serverUrl, String fileName) {
		try {
			URL url = new URL(serverUrl);
			File file = new File(fileName);

			long startTime = System.currentTimeMillis();
			Log.d("ImageManager", "download begining");
			Log.d("ImageManager", "download url:" + url);
			Log.d("ImageManager", "downloaded file name:" + fileName);
			/* Open a connection to that URL. */
			URLConnection ucon = url.openConnection();

			/*
			 * Define InputStreams to read from the URLConnection.
			 */
			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);

			/*
			 * Read bytes to the Buffer until there is nothing more to read(-1).
			 */
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}

			/* Convert the Bytes read to a String. */
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baf.toByteArray());
			fos.close();
			Log.d("ImageManager", "download ready in" + ((System.currentTimeMillis() - startTime) / 1000) + " sec");

		} catch (IOException e) {
			Log.d("ImageManager", "Error: " + e);
		}

	}
}