package android.util;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.miscellaneous.Log;

/**
 * <pre>
 * &lt;permission
 *     android:name="{packagename}.permission.C2D_MESSAGE"
 *     android:protectionLevel="signature" />
 * &lt;permission
 *         android:name="{permission}"
 *         android:protectionLevel="signature" />
 * 
 * &lt;uses-permission android:name="{packagename}.permission.C2D_MESSAGE" />
 * &lt;uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
 * &lt;uses-permission android:name="android.permission.GET_ACCOUNTS" />
 * &lt;uses-permission android:name="android.permission.INTERNET" />
 * &lt;uses-permission android:name="android.permission.WAKE_LOCK" />
 * 
 * &lt;receiver
 *     android:name="android.util.GCMReceiver"
 *     android:permission="com.google.android.c2dm.permission.SEND" >
 *     &lt;intent-filter>
 *         &lt;action android:name="com.google.android.c2dm.intent.RECEIVE" />
 *         &lt;action android:name="com.google.android.c2dm.intent.REGISTRATION" />
 * 
 *         &lt;category android:name="{packagename}" />
 *     &lt;/intent-filter>
 * &lt;/receiver>
 * 
 * &lt;service android:name="{classname extend GCMIntentService}"
 *             android:permission="{permission}" >
 *     &lt;intent-filter>
 *         &lt;action android:name="com.google.android.c2dm.intent.RECEIVE" />
 *         &lt;action android:name="com.google.android.c2dm.intent.REGISTRATION" />
 * 
 *         &lt;category android:name="{packagename}" />
 *     &lt;/intent-filter>
 * &lt;/service>
 * </pre>
 */
public class GCMReceiver extends BroadcastReceiver {

	interface GCMConstants {

		public static final String EXTRA_APPLICATION_PENDING_INTENT = "app";
		public static final String INTENT_TO_GCM_REGISTRATION = "com.google.android.c2dm.intent.REGISTER";
		public static final String INTENT_TO_GCM_UNREGISTRATION = "com.google.android.c2dm.intent.UNREGISTER";
		public static final String EXTRA_SENDER = "sender";
	}

	public static void register(Context context, String project_id) {
		Log.l();
		Intent service = new Intent(GCMConstants.INTENT_TO_GCM_REGISTRATION);
		service.putExtra(GCMConstants.EXTRA_APPLICATION_PENDING_INTENT, PendingIntent.getBroadcast(context, 0, new Intent(), 0));
		service.putExtra(GCMConstants.EXTRA_SENDER, project_id);
		context.startService(service);
	}

	public static void unregister(Context context) {
		Log.l();
		Intent service = new Intent(GCMConstants.INTENT_TO_GCM_UNREGISTRATION);
		service.putExtra(GCMConstants.EXTRA_APPLICATION_PENDING_INTENT, PendingIntent.getBroadcast(context, 0, new Intent(), 0));
		context.startService(service);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.l("OnReceive:", intent);
		GCMIntentService.wakeLockStartService(context, intent);
		setResult(Activity.RESULT_OK, null, null);
	}

}
