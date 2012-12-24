package android.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.miscellaneous.Log;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;

public final class Exec {
	public static final int ConnectivityType_NONE = 0x00;
	public static final int ConnectivityType_MOBILE = 0x01;
	public static final int ConnectivityType_WIFI = 0x02;

	public static int getConnectivityType(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null || !ni.isConnected())
			return ConnectivityType_NONE;

		switch (ni.getType()) {
			case ConnectivityManager.TYPE_MOBILE :
				return ConnectivityType_MOBILE;
			case ConnectivityManager.TYPE_WIFI :
				return ConnectivityType_WIFI;
			default :
				return ConnectivityType_NONE;
		}
	}

	public static String getLine1Number(Context context) {
		String line1Number = "";
		try {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			line1Number = tm.getLine1Number();
		} catch (Exception e) {
		}

		return getLocaleFormat(line1Number);
	}

	public static String getLocaleFormat(String phoneNumber) {
		try {
			phoneNumber = PhoneNumberUtils.stripSeparators(phoneNumber).replace("+82", "0");
		} catch (NullPointerException e) {
		}
		return phoneNumber;
	}

	public static void kill(Activity activity) {
		Log.l(">>>>>kill<<<<<");
		activity.moveTaskToBack(true);
		activity.finish();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	public static boolean isALWAYS_FINISH_ACTIVITIES(Context context, OnClickListener positiveListener, OnClickListener negativeListener) {
		int ALWAYS_FINISH_ACTIVITIES = android.provider.Settings.System.getInt(context.getContentResolver(), android.provider.Settings.System.ALWAYS_FINISH_ACTIVITIES, 0);
		if (ALWAYS_FINISH_ACTIVITIES != 1)
			return true;
		String message = "원만한 어플 실행을 위하여 환경설정에서 \"활동 보관 안 함\" 체크를 해제하여 주시기 바랍니다.";

		new AlertDialog.Builder(context)//
				.setMessage(message)//
				.setPositiveButton("지금설정", positiveListener)//
				.setNegativeButton("프로그램종료", negativeListener)//
				.create()//
				.show();

		return false;
	}
}
