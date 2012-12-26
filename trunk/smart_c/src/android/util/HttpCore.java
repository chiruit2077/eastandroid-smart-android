package android.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import android.miscellaneous.E;
import android.miscellaneous.Log;

public class HttpCore {

	private static final int CONNECT_TIMEOUT = 10 * 1000;
	private static final int READ_TIMEOUT = 60 * 1000;

	public synchronized String connectUrlPost(String urlhttp) throws UnsupportedEncodingException, MalformedURLException, ProtocolException, IOException, HttpStatusException {

		if (E.NETLOG)
			Log.l(urlhttp);

		urlhttp = UT.URLEncoding(urlhttp);

		if (E.NETLOG)
			Log.l(urlhttp);

		String server = "";
		String param = "";
		int pos = urlhttp.indexOf("?");
		if (pos == -1) {
			server = urlhttp;
		} else {
			server = urlhttp.substring(0, pos);
			if (urlhttp.length() >= pos + 1)
				param = urlhttp.substring(pos + 1).trim();
		}

		trustHttpsCertificates();

		String string = "";
		HttpURLConnection httpURLConnection = null;
		System.setProperty("http.keepAlive", "true");
		try {
			URL url = new URL(server);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setConnectTimeout(CONNECT_TIMEOUT);
			httpURLConnection.setReadTimeout(READ_TIMEOUT);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setUseCaches(false);

			httpURLConnection.setRequestProperty("Cache-Control", "no-cache");

			setRequestProperty(httpURLConnection);

			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);

			OutputStream os = httpURLConnection.getOutputStream();
			os.write(param.toString().getBytes());
			os.flush();

			int responseCode = httpURLConnection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				httpURLConnectionSuccess(httpURLConnection, param);
			} else {
				httpURLConnectionFail(httpURLConnection);
				if (E.NETLOG)
					Log.l("!http error : ", httpURLConnection.getResponseMessage(), responseCode);
				throw new HttpStatusException(httpURLConnection.getResponseMessage() + "(" + responseCode + ")");
			}

			final InputStream is = httpURLConnection.getInputStream();
			string = UT.convertStreamToString(is);
			if (E.NETLOG)
				Log.l(string);

			is.close();
			os.close();
		} finally {
			httpURLConnection.disconnect();
		}

		return string;
	}

	public static class HttpStatusException extends Exception {
		private static final long serialVersionUID = 1862488443421973331L;

		public HttpStatusException() {
			super();
		}

		public HttpStatusException(String detailMessage, Throwable throwable) {
			super(detailMessage, throwable);
		}

		public HttpStatusException(String detailMessage) {
			super(detailMessage);
		}

		public HttpStatusException(Throwable throwable) {
			super(throwable);
		}
	}

	public void trustHttpsCertificates() {
		Log.l();
	}

	public void httpURLConnectionSuccess(HttpURLConnection httpURLConnection, String param) {
		Log.l();
	}

	public void setRequestProperty(HttpURLConnection httpURLConnection) {
		Log.l();
	}

	public void httpURLConnectionFail(HttpURLConnection httpURLConnection) {
		Log.l();
	}
}
