package android.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.UT;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Checkable;
import android.widget.RelativeLayout;

public class CheckedRealtiveLayout extends RelativeLayout implements Checkable {
	private boolean mChecked;
	private Drawable mCheckMarkDrawable;

	private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};

	public CheckedRealtiveLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CheckedRealtiveLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CheckedRealtiveLayout(Context context) {
		super(context);
	}
	private ArrayList<Checkable> mCheckables;
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		mCheckables = new ArrayList<Checkable>();
		UT.findChildViews(this, mCheckables);
	}
	public void toggle() {
		final ArrayList<Checkable> checkables = mCheckables;
		for (Checkable checkable : checkables) {
			checkable.toggle();
		}
		setChecked(!mChecked);
	}
	public boolean isChecked() {
		return mChecked;
	}

	public void setChecked(boolean checked) {
		final ArrayList<Checkable> checkables = mCheckables;
		for (Checkable checkable : checkables) {
			checkable.setChecked(checked);
		}

		if (mChecked != checked) {
			mChecked = checked;
			refreshDrawableState();
		}
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
		if (isChecked()) {
			mergeDrawableStates(drawableState, CHECKED_STATE_SET);
		}
		return drawableState;
	}

	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();
		if (mCheckMarkDrawable != null) {
			int[] myDrawableState = getDrawableState();
			mCheckMarkDrawable.setState(myDrawableState);
			invalidate();
		}
	}

	@Override
	public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
		super.onInitializeAccessibilityEvent(event);
		event.setClassName(CheckedRealtiveLayout.class.getName());
		event.setChecked(mChecked);
	}

	@Override
	public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
		super.onInitializeAccessibilityNodeInfo(info);
		info.setClassName(CheckedRealtiveLayout.class.getName());
		info.setCheckable(true);
		info.setChecked(mChecked);
	}

}
