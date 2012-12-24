package android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * @author djrain
 * 
 */
public class StopScrollView extends ScrollView {

	private boolean isStopScroll;

	public StopScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public StopScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public StopScrollView(Context context) {
		super(context);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (isStopScroll)
			return false;
		return super.onInterceptTouchEvent(ev);
	}

	public void setStopScroll(boolean b) {
		//		Log.l(b);
		isStopScroll = b;
	}
}
