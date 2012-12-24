package android.miscellaneous;

import android.miscellaneous.Log;

/**
 * @author djrain
 * 
 */
public class Tic {
	static long s = 0;
	private static long e;

	public static void _s() {
		s = System.currentTimeMillis();
		e = s;
		Log.e("TIC", String.format("%20d%20d%20d", s, e, e - s));
	}

	public static void _tic() {
		e = System.currentTimeMillis();
		Log.e("TIC", String.format("%20d%20d%20d", s, e, e - s));
		s = e;
	}

	public static void _tic(String log) {
		e = System.currentTimeMillis();
		Log.e("TIC" + log, String.format("%20d%20d%20d", s, e, e - s));
		s = e;
	}

	public static long _length() {
		e = System.currentTimeMillis();
		Log.e("TIC", String.format("%20d%20d%20d", s, e, e - s));
		long _tic_length = e - s;
		s = e;
		return _tic_length;
	}

}
