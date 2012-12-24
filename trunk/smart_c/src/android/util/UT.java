package android.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.miscellaneous.Assert;
import android.miscellaneous.E;
import android.miscellaneous.Log;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * @author djrain
 * 
 *         <pre>
 * public static View findWebview(final ViewGroup parent) {
 * 	if (parent instanceof WebView)
 * 		return parent;
 * 
 * 	int N = parent.getChildCount();
 * 	for (int i = 0; i &lt; N; i++) {
 * 		View child = parent.getChildAt(i);
 * 
 * 		if (child instanceof ViewGroup)
 * 			child = findWebview((ViewGroup) child);
 * 
 * 		if (child instanceof WebView)
 * 			return child;
 * 	}
 * 
 * 	return null;
 * }
 * public static &lt;T&gt; boolean instanceOf(Object o, T t) {
 * 	return o.getClass().isInstance((T) null);
 * }
 * </pre>
 */
public final class UT {

	//	public static String STARMARK(String string) {
	//		return string.replaceAll(".", "●");
	//	}

	public static String STARMARK(int length) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++)
			sb.append("●");
		return sb.toString();
	}
	static TypedValue out = new TypedValue();
	public static int resid(Context c, int resid_attr) {
		c.getTheme().resolveAttribute(resid_attr, out, true);
		return out.resourceId;
	}
	public static int ver() {
		return Build.VERSION.SDK_INT;
	}
	/**
	 * @param VERSION_CODES
	 *            etc.. Build.VERSION_CODES.HONEYCOMB
	 * @return
	 */
	public static boolean isOverFroyo() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}
	public static boolean isOverHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}
	public static boolean isOverJellybean() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}

	@TargetApi(11)
	@SuppressWarnings("deprecation")
	public static void copy(Context context, String msg) {

		if (isOverHoneycomb()) {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(msg);
		} else {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setPrimaryClip(ClipData.newPlainText(null, msg));
		}
		Toast.makeText(context, "복사 하였습니다.", Toast.LENGTH_SHORT).show();
	}

	public static class LimitByteTextWatcher implements TextWatcher {
		private int bytelength;
		private String format;
		private Runnable what;

		public LimitByteTextWatcher(int bytelength, String incode_format) {
			this.bytelength = bytelength;
			this.format = incode_format;
		}

		public LimitByteTextWatcher(int bytelength, String incode_format, Runnable what) {
			this(bytelength, incode_format);
			this.what = what;
		}

		public void afterTextChanged(Editable s) {
			try {
				byte[] byteArray = s.toString().getBytes(format);
				int count = byteArray.length;
				if (count > bytelength) {
					int utfcount = new String(byteArray, 0, bytelength, format).length();
					s.delete(utfcount, s.length());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (what != null)
				what.run();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
	};

	/**
	 * 유효 비밀번호 확인
	 * 
	 * @param userPWD
	 * @return
	 */
	public static boolean validPass(String password) {
		return Pattern.compile("[a-zA-Z]").matcher(password).find();
	}

	/**
	 * 계좌번호 포멧.
	 * 
	 * @param strValue
	 * @return
	 */
	public static final String formatHanaAcctNo(String strValue) {

		String str = "";

		if (strValue == null) {
			return str;
		}

		if (strValue.length() == 14) {
			str = strValue.substring(0, 3) + "-" + strValue.substring(3, 9) + "-" + strValue.substring(9, strValue.length());
		} else {
			return strValue;
		}

		return str;
	}

	public static final String getString(String strValue, String regularExpression, int group) {
		Pattern pattern = Pattern.compile(regularExpression);
		Matcher matcher = pattern.matcher(strValue);
		if (matcher.find()) {
			Assert.T(matcher.groupCount() >= group);
			return matcher.group(group);
		}
		return null;
	}

	//"KSC5601"
	public static int lengthByte(String string, String format) {
		try {
			return string.toString().getBytes(format).length;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;

	}

	public static String limitByte(String string, int length, String format) {
		try {
			byte[] byteArray = string.toString().getBytes(format);
			int count = byteArray.length;
			if (count > length) {
				int utfcount = new String(byteArray, 0, length, format).length();
				string = string.substring(0, utfcount);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return string;
	}

	public static String getUUID() {
		return UUID.randomUUID().toString();
	}

	private static ShapeDrawable _DRAWABLE = new ShapeDrawable(new OvalShape());
	public static void drawBall(Canvas canvas, Rect bounds, int color) {
		_DRAWABLE.setBounds(bounds);

		int red = Color.red(color);
		int green = Color.green(color);
		int blue = Color.blue(color);
		int darkColor = Color.rgb(red / 4, green / 4, blue / 4);

		final float width = bounds.width();
		final float height = bounds.height();
		RadialGradient gradient = new RadialGradient(width / 4f, height / 4f, (width + height), color, darkColor, Shader.TileMode.CLAMP);

		final Paint paint = _DRAWABLE.getPaint();
		paint.setShader(gradient);
		_DRAWABLE.draw(canvas);
	}

	public static Uri removeQuery(Uri uri, String removeQuery) {

		//		Set<String> set = uri.getQueryParameterNames();

		String query = uri.getQuery();
		if (query == null)
			return uri;

		Set<String> set = new HashSet<String>();
		String[] params = query.split("&");

		for (int i = 0; i < params.length; i++) {
			final String keyvalue = params[i];
			int po = keyvalue.indexOf("=");
			if (-1 == po)
				continue;
			set.add(keyvalue.substring(0, po).trim());
		}

		set.remove("x");

		//		Builder builder = uri.buildUpon().clearQuery();
		Builder builder = uri.buildUpon();
		builder.query(null);
		for (String key : set)
			builder.appendQueryParameter(key, uri.getQueryParameter(key));

		return builder.build();
	}

	public static String URLEncoding(String url) {

		int pos = url.indexOf("?");
		if (pos == -1) {
			return url;
		}

		String server = "";
		String param = "";
		if (pos != -1) {
			server = url.substring(0, pos);

			if (url.length() >= pos + 1)
				param = url.substring(pos + 1).trim();
		}

		StringBuffer body = new StringBuffer();
		String[] params = param.split("&");
		for (int i = 0; i < params.length; i++) {
			String key = "";
			String value = "";
			final String keyvalue = params[i];

			int po = keyvalue.indexOf("=");
			if (-1 == po)
				continue;

			key = keyvalue.substring(0, po).trim();
			body.append(key);
			body.append("=");

			if (keyvalue.length() <= po)
				continue;
			value = keyvalue.substring(po + 1).trim();
			try {
				body.append(URLEncoder.encode(value, "UTF-8").trim());
			} catch (UnsupportedEncodingException e) {
			}
			body.append("&");
		}
		param = body.toString();

		return server + (param.length() <= 0 ? "" : "?" + param);
	}

	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}
		}
		if (E.NETLOG)
			Log.l(sb.toString());

		return sb.toString();
	}
	public static Bitmap loadFromUrl(String url) {
		if (url == null || url.length() <= 0) {
			Log.l();
			return null;
		}

		Bitmap bmp = null;
		for (int i = 0; i < 3; i++) {
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
	public static void trustHttpsCertificates() throws Exception {
		// Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

		TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
				return;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
				return;
			}
		}};

		// SSLContext sc = SSLContext.getInstance("SSL");
		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(null, trustAllCerts, new SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String urlHostName, SSLSession session) {
				if (!urlHostName.equalsIgnoreCase(session.getPeerHost())) {
				}
				return true;
			}
		};
		HttpsURLConnection.setDefaultHostnameVerifier(hv);
	}

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	public static void vibrator(Context context, long milliseconds) {
		((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(milliseconds);
	}
	public static void vibrator(Context context) {
		vibrator(context, 500);
	}

	@SuppressWarnings("unchecked")
	public static <T> void findChildViews(final ViewGroup parent, ArrayList<T> result) {
		int N = parent.getChildCount();
		for (int i = 0; i < N; i++) {
			final View child = parent.getChildAt(i);

			if (child.getClass().isInstance((T) null))
				result.add((T) child);

			if (child instanceof ViewGroup)
				findChildViews((ViewGroup) child, result);
		}
	}

//	<uses-permission android:name="android.permission.WAKE_LOCK" />
	public static boolean isScreenOn(Context context) {
		PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		return powerManager.isScreenOn();
	}
	public static void screenOn(Activity activity) {
		if (!isScreenOn(activity))
			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
	}

	/**
	 * 
	 * @param context
	 * @param icon
	 * @param title
	 * @param text
	 * @param pendingIntent
	 * @return Notification Notification
	 * 
	 *         <pre>
	 * java.lang.SecurityException: Requires VIBRATE permission
	 * </pre>
	 */
	@SuppressWarnings("deprecation")
	public static Notification getNotification(Context context, int icon, CharSequence title, String text, PendingIntent pendingIntent) {
		Notification notification;
		if (UT.isOverJellybean()) {
			notification = new Notification.Builder(context)//
					.setContentTitle(title)//
					.setSmallIcon(icon)//
					.setContentText(text)//
					.setWhen(System.currentTimeMillis())//
					.setContentIntent(pendingIntent)//
					.setAutoCancel(true)//
					.setDefaults(Notification.DEFAULT_SOUND)//					
					.setTicker(title + "\r\n" + text)//
					.build();

		} else if (UT.isOverHoneycomb()) {
			notification = new Notification.Builder(context)//
					.setContentTitle(title)//
					.setSmallIcon(icon)//
					.setContentText(text)//
					.setWhen(System.currentTimeMillis())//
					.setContentIntent(pendingIntent)//
					.setAutoCancel(true)//
					.setDefaults(Notification.DEFAULT_SOUND)//
					.setTicker(title + "\r\n" + text)//
					.getNotification();
		} else {
			notification = new Notification();
			notification.contentIntent = pendingIntent;
			notification.icon = icon;
			notification.tickerText = title + "\r\n" + text;
			notification.when = System.currentTimeMillis();
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			notification.defaults = Notification.DEFAULT_SOUND;
			notification.setLatestEventInfo(context, title, text, pendingIntent);
		}
		return notification;
	}
//	<uses-permission android:name="android.permission.GET_TASKS" />
//	try {
//		PackageInfo activity = null;
//		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
//		List<RunningTaskInfo> appTasks = activityManager.getRunningTasks(50);
//		if (appTasks == null)
//			return false;
//
//		PackageManager packageManager = (PackageManager) mContext.getPackageManager();
//		try {
//			activity = packageManager.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
//
//		} catch (NameNotFoundException e) {
//			e.printStackTrace();
//		}
//
//		List<String> activityNames = new ArrayList<String>();
//		for (ActivityInfo activityInfo : activity.activities) {
//			activityNames.add(activityInfo.applicationInfo.className);
//		}
//
//		Collections.sort(activityNames);
//
//		return Collections.binarySearch(activityNames, appTasks.get(0).topActivity.getClassName()) != -1;
//	} catch (Exception e) {
//		foreGround = false;
//	}

}
