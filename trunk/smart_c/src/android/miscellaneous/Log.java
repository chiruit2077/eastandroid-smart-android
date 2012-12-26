package android.miscellaneous;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

/**
 * @author djrain
 * 
 */
public class Log {
	private static String func = new String();
	private static String locator = new String();
	private static boolean l2t = false;
	private static String PREFIX = ">>";

	public static void l2(Object... args) {
		if (!E.LOG)
			return;
		Exception e = new Exception();
		getString2(e);
		android.util.Log.e(func, PREFIX + "f :" + message(args) + locator);
		getString(e);
		android.util.Log.e(func, PREFIX + "f :" + message(args) + locator);
	}

	public static void ln(int n, Object... args) {
		if (!E.LOG)
			return;
		Exception e = new Exception();
		getStringN(n, e);
		android.util.Log.e(func, PREFIX + "f :" + message(args) + locator);
		getString(e);
		android.util.Log.e(func, PREFIX + "f :" + message(args) + locator);
	}

	public static void e(Object... args) {
		if (!E.LOG)
			return;
		Exception e = new Exception();
		getString(e);
		android.util.Log.e(func, PREFIX + "!!:" + message(args) + locator);
		e.printStackTrace();
	}

	public static void w(Object... args) {
		if (!E.LOG)
			return;
		Exception e = new Exception();
		getString(e);
		android.util.Log.e(func, PREFIX + "! :" + message(args) + locator);
		e.printStackTrace();
	}

	public static void l() {
		if (!E.LOG)
			return;
		Exception e = new Exception();
		getString(e);
		android.util.Log.e(func, PREFIX + "f :" + locator);
	}

	public static void l(Object... args) {
		if (!E.LOG)
			return;
		Exception e = new Exception();
		getString(e);
		android.util.Log.e(func, PREFIX + "f :" + message(args) + locator);
	}

	public static void c(Object... args) {
		if (!E.LOG)
			return;
		Exception e = new Exception();
		getString(e);
		android.util.Log.e(func, PREFIX + "? :" + message(args) + locator);
		e.printStackTrace();
	}

	public static void d(Object... args) {
		if (!E.LOG)
			return;
		Exception e = new Exception();
		getString(e);
		android.util.Log.e(func, PREFIX + "d :" + message(args) + locator);
	}

	public static void v(Object... args) {
		if (!E.LOG)
			return;
		Exception e = new Exception();
		getString(e);
		android.util.Log.e(func, PREFIX + "v :" + message(args) + locator);
	}

	public static void i(Object... args) {
		if (!E.LOG)
			return;
		Exception e = new Exception();
		getString(e);
		android.util.Log.e(func, PREFIX + "i :" + message(args) + locator);
	}

	private static String message(final Object[] args) {
		StringBuffer sb = new StringBuffer();
		for (Object object : args) {
			sb.append(",");

			String string = "";
			if (object == null)
				string = "null";
			else if (object instanceof Intent)
				string = _DUMP((Intent) object);
			else if (object instanceof Uri)
				string = _DUMP((Uri) object);
			else if (l2t && object instanceof Long)
				string = _DUMP((Long) object);
			else
				string = object.toString();

			sb.append(string);
		}
		return sb.toString();
	}

	private static String _DUMP(Uri uri) {
		StringBuffer sb = new StringBuffer();
		sb.append("\r\n Uri                       ");
		sb.append(uri.toString());
		sb.append("\r\n Scheme                    ");
		sb.append(uri.getScheme() != null ? uri.getScheme().toString() : "null");
		sb.append("\r\n Host                      ");
		sb.append(uri.getHost() != null ? uri.getHost().toString() : "null");
		sb.append("\r\n Port                      ");
		sb.append(uri.getPort());
		sb.append("\r\n Path                      ");
		sb.append(uri.getPath() != null ? uri.getPath().toString() : "null");
		sb.append("\r\n Query                     ");
		sb.append(uri.getQuery() != null ? uri.getQuery().toString() : "null");
		sb.append("\r\n");
//		sb.append("\r\n Fragment                  ");
//		sb.append(uri.getFragment() != null ? uri.getFragment().toString() : "null");
//		sb.append("\r\n LastPathSegment           ");
//		sb.append(uri.getLastPathSegment() != null ? uri.getLastPathSegment().toString() : "null");
//		sb.append("\r\n SchemeSpecificPart        ");
//		sb.append(uri.getSchemeSpecificPart() != null ? uri.getSchemeSpecificPart().toString() : "null");
//		sb.append("\r\n UserInfo                  ");
//		sb.append(uri.getUserInfo() != null ? uri.getUserInfo().toString() : "null");
//		sb.append("\r\n PathSegments              ");
//		sb.append(uri.getPathSegments() != null ? uri.getPathSegments().toString() : "null");
//		sb.append("\r\n Authority                 ");
//		sb.append(uri.getAuthority() != null ? uri.getAuthority().toString() : "null");
//		sb.append("\r\n");
//		sb.append("\r\n EncodedAuthority          ");
//		sb.append(uri.getEncodedAuthority() != null ? uri.getEncodedAuthority().toString() : "null");
//		sb.append("\r\n EncodedPath               ");
//		sb.append(uri.getEncodedPath() != null ? uri.getEncodedPath().toString() : "null");
//		sb.append("\r\n EncodedQuery              ");
//		sb.append(uri.getEncodedQuery() != null ? uri.getEncodedQuery().toString() : "null");
//		sb.append("\r\n EncodedFragment           ");
//		sb.append(uri.getEncodedFragment() != null ? uri.getEncodedFragment().toString() : "null");
//		sb.append("\r\n EncodedSchemeSpecificPart ");
//		sb.append(uri.getEncodedSchemeSpecificPart() != null ? uri.getEncodedSchemeSpecificPart().toString() : "null");
//		sb.append("\r\n EncodedUserInfo           ");
//		sb.append(uri.getEncodedUserInfo() != null ? uri.getEncodedUserInfo().toString() : "null");
//		sb.append("\r\n");
		return sb.toString();
	}

	public static String _DUMP(long milliseconds) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return sf.format(new Date(milliseconds));
	}

	public static String _DUMP(Intent intent) {
		StringBuffer sb = new StringBuffer();

		sb.append("\r\n Action     ");
		sb.append(intent.getAction() != null ? intent.getAction().toString() : "null");
		sb.append("\r\n Data       ");
		sb.append(intent.getData() != null ? intent.getData().toString() : "null");
		sb.append("\r\n Categories ");
		sb.append(intent.getCategories() != null ? intent.getCategories().toString() : "null");
		sb.append("\r\n Type       ");
		sb.append(intent.getType() != null ? intent.getType().toString() : "null");
		sb.append("\r\n Scheme     ");
		sb.append(intent.getScheme() != null ? intent.getScheme().toString() : "null");
		sb.append("\r\n Package    ");
		sb.append(intent.getPackage() != null ? intent.getPackage().toString() : "null");
		sb.append("\r\n Component  ");
		sb.append(intent.getComponent() != null ? intent.getComponent().toString() : "null");

		if (intent.getExtras() != null) {
			final Bundle bundle = intent.getExtras();
			final Set<String> keys = bundle.keySet();
			String type = null;
			String value = null;
			for (String key : keys) {
				final Object o = bundle.get(key);
				if (o == null) {
					type = "null";
					value = "null";
				} else {
					type = o.getClass().getSimpleName();
					value = o.toString();
				}
				sb.append("\r\n");
				sb.append(key + "," + type + "," + value);
			}
		}
		sb.append("\r\n");
		return sb.toString();
	}

	private static void getStringN(int n, final Exception e) {
		String funcStack = e.getStackTrace()[n].toString();

		int posJava = funcStack.lastIndexOf("(");
		int posFunc = funcStack.lastIndexOf(".", posJava - 1);
		int posClass = funcStack.lastIndexOf(".", posFunc - 1);
		func = funcStack.substring(posClass + 1, posJava);// classfuncName
		locator = ":at " + funcStack.substring(posJava);// javaName
	}

	private static void getString(final Exception e) {
		getStringN(1, e);
	}

	private static void getString2(final Exception e) {
		getStringN(2, e);
	}

	public static void t(Context context, Object... args) {
		Log.l(args);
		Toast.makeText(context, message(args), Toast.LENGTH_SHORT).show();
	}

	public static void da(Object... args) {
		l2t = true;
		Log.l2(args);
		l2t = false;
	}

	public static void h(byte[] bytearray) {
		try {
			Log.l2("<" + bytearray.length + ">", HexUtil.h2s(bytearray));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}
