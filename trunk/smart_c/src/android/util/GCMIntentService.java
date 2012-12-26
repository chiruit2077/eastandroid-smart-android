package android.util;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.miscellaneous.Log;
import android.os.PowerManager;

/**
 * <pre>
 * http://blog.naver.com/devstory/130104356488
 * 
 * wakeLock 는 cpu 상태를 유지하기 위해 사용하였다.
 * wakeLock 의 FLAG 에는 아래와 같이 6가지가 존재한다.
 * 
 * PARTIAL_WAKE_LOCK : CPU ON, 화면은 꺼짐, 키보드 꺼짐
 * SCREEN_DIM_WAKE : CPU ON, 화면 어둡게, 키보드 꺼짐
 * SCREEN_BRIGHT_WAKE_LOCK : CPU ON, 화면 밝게, 키보드 꺼짐
 * FULL_WAKE_LOCK : CPU ON, 화면 밝게, 키보드 ON
 * </pre>
 */
public class GCMIntentService extends IntentService {

	protected Context mContext;
	public GCMIntentService() {
		super("GCMIntentService");
	}

	private static PowerManager.WakeLock sWakeLock;
	private static final Object LOCK = GCMIntentService.class;

	static void wakeLockStartService(Context context, Intent intent) {
//		Log.l(intent);

		synchronized (LOCK) {
			if (sWakeLock == null) {
				PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
				sWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, context.getPackageName());
			}
		}

		sWakeLock.acquire();
		intent.setComponent(null);
		intent.setPackage(context.getPackageName());
		intent.addCategory(context.getPackageName());
		context.startService(intent);
		Log.l("startService : " + intent);
	}

	@Override
	public final void onHandleIntent(Intent intent) {
		Log.l(intent);
		mContext = this;
		try {
			String action = intent.getAction();
			if (action.equals("com.google.android.c2dm.intent.REGISTRATION")) {
				handleRegistration(intent);
			} else if (action.equals("com.google.android.c2dm.intent.RECEIVE")) {
				onMessage(intent);
			}
		} finally {
			synchronized (LOCK) {
				sWakeLock.release();
			}
		}
	}

	private void handleRegistration(Intent intent) {
		String registrationId = intent.getStringExtra("registration_id");
		String error = intent.getStringExtra("error");
		String unregistered = intent.getStringExtra("unregistered");
		if (registrationId != null) {
			onRegistrationId(registrationId);
		}

		if (unregistered != null) {
			onUnregistration();
		}

		if (error != null) {
			onError(error);
		}
	}

	protected void onError(String error) {
		if ("SERVICE_NOT_AVAILABLE".equals(error)) {
			Log.l("SERVICE_NOT_AVAILABLE");
		} else if ("ACCOUNT_MISSING".equals(error)) {
			Log.l("Google 계정이 없음 : " + error);
		} else {
			Log.l("Received error: " + error);
		}
	}
	protected void onUnregistration() {
		Log.l();
	}

	protected void onRegistrationId(String registrationId) {
		Log.l("registrationId:" + registrationId);
	}

	protected void onMessage(Intent intent) {
		Log.l(intent);
	}
}
