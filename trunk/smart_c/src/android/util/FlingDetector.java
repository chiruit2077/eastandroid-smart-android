package android.util;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

/**
 * @author djrain
 * 
 */
public class FlingDetector {

	private OnFlingListener onFlingListener;
	private GestureDetector gd;

	public FlingDetector(Context context) {
		gd = new GestureDetector(context, new OnFlingDetector());
	}

	public void setOnFlingListener(OnFlingListener onFlingListener) {
		this.onFlingListener = onFlingListener;
	}

	public static interface OnFlingListener {
		void onFlingRight();

		void onFlingLeft();
	}

	class OnFlingDetector implements OnGestureListener {

		private static final float SENSITIVE = 1000;

		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			//            int SENSITIVE = ViewConfiguration.getMinimumFlingVelocity();

			if (onFlingListener != null && Math.abs(velocityX) > SENSITIVE && Math.abs(velocityX) > Math.abs(velocityY)) {
				if (velocityX > 0)
					onFlingListener.onFlingLeft();
				else
					onFlingListener.onFlingRight();

				return true;
			}
			return false;
		}
	}

	public boolean onTouchEvent(MotionEvent ev) {
		return gd.onTouchEvent(ev);
	}

}
