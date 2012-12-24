package android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.Button;

public class DoppelgangerButton extends Button {

	private View mView;

	public DoppelgangerButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public DoppelgangerButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DoppelgangerButton(Context context) {
		super(context);
	}

	public void setView(View v) {
		mView = v;
	}

	@Override
	public void draw(Canvas canvas) {
		if (mView != null)
			mView.draw(canvas);
		else
			super.draw(canvas);
	}
}
