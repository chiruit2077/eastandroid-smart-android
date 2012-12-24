package android.miscellaneous;

import android.view.MotionEvent;

/**
 * @author djrain
 * 
 */
public class MotionLog {

	private static long mCurrentTimeMillis;

	public static void onTouchEvent(MotionEvent event) {

		int action = event.getAction() & MotionEvent.ACTION_MASK;
		if (action != MotionEvent.ACTION_MOVE || System.currentTimeMillis() - mCurrentTimeMillis > 1000) {
			Log.l(String.format("0x%08X", event.getAction()) //
					+ "," + event.getPointerCount() //
					+ "," + event.getActionIndex() //
					+ "," + event.getPointerId(event.getActionIndex()) //
					+ ":at " + new Exception().getStackTrace()[1].toString());

			mCurrentTimeMillis = System.currentTimeMillis();
		}
	}

}