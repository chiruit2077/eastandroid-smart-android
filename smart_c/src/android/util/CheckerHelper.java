package android.util;

import java.util.ArrayList;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

/**
 * @author djrain
 * 
 */
public class CheckerHelper {
	private ArrayList<View> mChilds = new ArrayList<View>();
	private ViewGroup mParent;
	private OnItemClickListener onItemClickListener;
	private OnItemSelectedListener onItemSelectedListener;

	public static interface OnItemClickListener {
		public void onItemClick(ViewGroup parent, View view, int position, long id);
	}

	public static interface OnItemSelectedListener {
		public void onItemSelected(ViewGroup parent, View view, int position, long id);
		public void onNothingSelected(ViewGroup parent);
	};

	public enum MODE {
		NONE, SINGLESELECTED, TOGGLE_SINGLESELECTED
	}
	private MODE mMode = MODE.SINGLESELECTED;
	public void setMode(MODE mode) {
		mMode = mode;
	}

	public static final String TAG_FILTER = "checkable";
	private String mTagFilter;
	public void setFilter(String tag) {
		mTagFilter = tag;
		if (mParent != null)
			updateListener();
	}

	public void updateListener() {
		setParent(mParent);
	}

	public void setParent(ViewGroup parent) {
		mParent = parent;
		mChilds.clear();
		if (mTagFilter == null || mTagFilter.length() <= 0)
			updateEvent();
		else
			updateEventR(parent);
		for (View child : mChilds)
			child.setOnClickListener(onSelectedClickListener);
	}

	private void updateEvent() {
		int N = mParent.getChildCount();
		for (int i = 0; i < N; i++) {
			final View child = mParent.getChildAt(i);
			mChilds.add(child);
		}
	}

	//재귀함수
	private void updateEventR(ViewGroup parent) {
		View[] childViews = getChildViews(parent);
		for (View view : childViews) {
			//ViewGroup일경우는 재귀호출    
			if (view instanceof ViewGroup) {
				updateEventR((ViewGroup) view);
			} else if (mTagFilter.equals(view.getTag())) {
				mChilds.add(view);
			}
		}
	}
	private View[] getChildViews(ViewGroup group) {
		int childCount = group.getChildCount();
		final View[] childViews = new View[childCount];
		for (int index = 0; index < childCount; index++) {
			childViews[index] = group.getChildAt(index);
		}
		return childViews;
	}

	private OnClickListener onSelectedClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mMode != MODE.NONE) {
				if (mMode == MODE.TOGGLE_SINGLESELECTED && v.isSelected()) {
					v.setSelected(false);
					fireNothingSelected();
				} else {
					for (View child : mChilds) {
						child.setSelected(child == v);
						if (child == v)
							fireItemSelected(v);
					}
				}
			}

			fireItemClick(v);
		}
	};

	private void fireNothingSelected() {
		if (onItemSelectedListener != null) {
			onItemSelectedListener.onNothingSelected(mParent);
		}
	}
	private void fireItemSelected(View v) {
		if (onItemSelectedListener != null) {
			onItemSelectedListener.onItemSelected(mParent, v, mChilds.indexOf(v), v.getId());
		}
	}
	private void fireItemClick(View v) {
		if (onItemClickListener != null) {
			onItemClickListener.onItemClick(mParent, v, mChilds.indexOf(v), v.getId());
		}
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		onItemClickListener = listener;
	}

	public void setOnOnItemSelectedListener(OnItemSelectedListener listener) {
		onItemSelectedListener = listener;
	}

	public void setCheck(int i, boolean b) {
		if (i < 0 || i >= getCount())
			return;

		mChilds.get(i).setSelected(b);
	}

	public int getCheck() {
		for (View child : mChilds) {
			if (child.isSelected()) {
				return mChilds.indexOf(child);
			}
		}
		return -1;
	}

	public View getCheckView() {
		for (View child : mChilds) {
			if (child.isSelected()) {
				return child;
			}
		}
		return null;
	}

	public int getCount() {
		return mChilds.size();
	}

	public View getView(int index) {
		return mChilds.get(index);
	}

}
