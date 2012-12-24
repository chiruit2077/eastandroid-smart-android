/*
 * Copyright (C) 2008 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

public class DragList extends ListView {

	private OnDragDropListener onDragDropListener;

	public void setOnDragDropListener(OnDragDropListener l) {
		onDragDropListener = l;
	}

	public interface OnDragDropListener {
		boolean acceptDrag(DragList list, int x, int y);

		Bitmap getDrager(DragList list, int x, int y);

		Drawable getMovePointer(DragList dragList);

		void onDrag(DragList list, int x, int y);

		void onEnter(DragList list, int x, int y);

		void onOver(DragList list, int x, int y);

		void onExit(DragList list, int x, int y, int position_exit, View view_exit);

		boolean acceptDrop(DragList list, int x, int y);

		void onDrop(DragList list, int x, int y);

		void onRemove(DragList list, int position_drag);
	}

	private REMOVEMODE mRemoveMode = REMOVEMODE.NONE;

	public static enum REMOVEMODE {
		NONE, FLING, SLIDE, TRASH_AUTO, TRASH
	}

	public interface TrashListener {
		void onEnter(DragList list, View view_drag);

		void onExit(DragList list, View view_drag);
	}

	private WindowManager mWindowManager;

	private Bitmap mDragBitmap;
	private ImageView mDragView;
	private WindowManager.LayoutParams mDragerParams;
	private boolean mMoveX = false;//drager x축 이동

	// private Drawable mMovePointer;
	private ImageView mMovePointerView;
	private WindowManager.LayoutParams mMovePointerParam;

	private int mDragPosition = AdapterView.INVALID_POSITION;
	private int mDragPointX; // at what x offset inside the item did the user grab it
	private int mDragPointY; // at what y offset inside the item did the user grab it
	private int mXOffset; // the difference between screen coordinates and coordinates in this view
	private int mYOffset; // the difference between screen coordinates and coordinates in this view

	private int mPositionOver = AdapterView.INVALID_POSITION;
	private View mPositionOverView;

	private int mHeight;
	private GestureDetector mGestureDetector;
	private Rect mTempRect = new Rect();

	private View mTrashView;
	private boolean mIsTrash;

	// about scroll
	private float DISTANCE_LEVEL = 5f;// 스크롤 단계
	private float DISTANCE_LEVEL_MIN = 1f;// 최소스크롤 단계
	private int DISTANCE_UNIT = 10;// 단위스크롤 크기

	public DragList(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
	}

	public DragList(Context context, AttributeSet attrs) {
		super(context, attrs);
		mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
	}

	public DragList(Context context) {
		super(context);
		mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (onDragDropListener != null && mGestureDetector == null) {
			if (mRemoveMode == REMOVEMODE.FLING) {
				mGestureDetector = new GestureDetector(getContext(), new SimpleOnGestureListener() {
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
						if (mDragView != null) {
							if (velocityX > 1000) {
								if (e2.getX() > getWidth() * 3 / 4) {
									removeDrager();
									removeMovePointer();
									onDragDropListener.onRemove(DragList.this, mDragPosition);
								}
							}
							return true;
						}
						return false;
					}
				});
			}
		}

		if (onDragDropListener != null) {
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN :
				int x = (int) ev.getX();
				int y = (int) ev.getY();
				int po = pointToPosition(x, y);
				final View v = getChildAt(po - getFirstVisiblePosition());

				if (po == AdapterView.INVALID_POSITION) {
					break;
				}

				mDragPointX = x - v.getLeft();
				mDragPointY = y - v.getTop();
				mXOffset = ((int) ev.getRawX()) - x;
				mYOffset = ((int) ev.getRawY()) - y;

				if (onDragDropListener.acceptDrag(this, x, y)) {

					setDrager(x, y);
					setMovePointer(x, y);

					if (mRemoveMode == REMOVEMODE.TRASH_AUTO)
						mTrashView.setVisibility(View.VISIBLE);

					mDragPosition = po;
					mHeight = getHeight();
					return false;
				}
				removeDrager();
				break;
			}
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mGestureDetector != null) {
			mGestureDetector.onTouchEvent(ev);
		}
		if (onDragDropListener == null || mDragView == null)
			return super.onTouchEvent(ev);

		int action = ev.getAction();
		int x = (int) ev.getX();
		int y = (int) ev.getY();
		int po = pointToPosition(x, y);
		final View v = getChildAt(po - getFirstVisiblePosition());

		switch (action) {
		case MotionEvent.ACTION_DOWN :
			onDragDropListener.onDrag(this, x, y);

		case MotionEvent.ACTION_MOVE :
			drawDrager(x, y);
			if (po != AdapterView.INVALID_POSITION)
				drawMovePointer(x, y);
			smoothScroll(y);

			if (po != mPositionOver) {
				if (mPositionOver != AdapterView.INVALID_POSITION)
					onDragDropListener.onExit(this, x, y, mPositionOver, mPositionOverView);

				if (po != AdapterView.INVALID_POSITION)
					onDragDropListener.onEnter(this, x, y);

				mPositionOver = po;
				mPositionOverView = v;
			}

			if (po != AdapterView.INVALID_POSITION)
				onDragDropListener.onOver(this, x, y);

			if (mRemoveMode == REMOVEMODE.TRASH_AUTO) {
				mTrashView.setPressed(isTrash(x, y));
			}
			if (mRemoveMode == REMOVEMODE.TRASH && mIsTrash != isTrash(x, y)) {
				if (isTrash(x, y))
					((TrashListener) mTrashView).onEnter(this, mDragView);
				else
					((TrashListener) mTrashView).onExit(this, mDragView);
				mIsTrash = isTrash(x, y);
			}

			break;

		case MotionEvent.ACTION_UP :
			if (mPositionOver != AdapterView.INVALID_POSITION)
				onDragDropListener.onExit(this, x, y, mPositionOver, mPositionOverView);

			if (onDragDropListener.acceptDrop(this, x, y)) {
				if ((mRemoveMode == REMOVEMODE.SLIDE && x > getWidth() * 3 / 4) //
						|| (mRemoveMode == REMOVEMODE.TRASH_AUTO && isTrash(x, y))//
						|| (mRemoveMode == REMOVEMODE.TRASH && isTrash(x, y))//
				) {
					onDragDropListener.onRemove(this, mDragPosition);
					if (mRemoveMode == REMOVEMODE.TRASH) {
						((TrashListener) mTrashView).onExit(this, mDragView);
					}
				} else {
					onDragDropListener.onDrop(this, x, y);
				}
			}

			if (mRemoveMode == REMOVEMODE.TRASH_AUTO)
				mTrashView.setVisibility(View.GONE);

			removeDrager();
			removeMovePointer();
			break;
		}
		return true;
	}

	private void smoothScroll(int y) {
		float distancef = ((float) y / ((float) mHeight / DISTANCE_LEVEL) - (DISTANCE_LEVEL / 2f));
		int distance = Math.abs(distancef) > DISTANCE_LEVEL_MIN ? (int) (distancef * DISTANCE_UNIT) : 0;
		int duration = 30;
		smoothScrollBy(distance, duration);
	}

	private void setDrager(int x, int y) {
		removeDrager();
		final int po = pointToPosition(x, y);
		final View v = getChildAt(po - getFirstVisiblePosition());

		Bitmap bitmap = onDragDropListener.getDrager(this, x, y);
		if (bitmap == null) {
			v.setDrawingCacheEnabled(true);
			bitmap = Bitmap.createBitmap(v.getDrawingCache());
		}
		mDragerParams = new WindowManager.LayoutParams();
		mDragerParams.gravity = Gravity.TOP | Gravity.LEFT;
		mDragerParams.x = x - mDragPointX + mXOffset;
		mDragerParams.y = y - mDragPointY + mYOffset;
		mDragerParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mDragerParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mDragerParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE//
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE //
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON//
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN//
				| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
		mDragerParams.format = PixelFormat.TRANSLUCENT;
		mDragerParams.alpha = 1.0f;
		mDragerParams.windowAnimations = 0;
		Context context = getContext();
		ImageView iv = new ImageView(context);
		iv.setImageBitmap(bitmap);
		mDragBitmap = bitmap;

		mWindowManager.addView(iv, mDragerParams);
		mDragView = iv;
	}

	private void setMovePointer(int x, int y) {
		removeMovePointer();
		Drawable drawable = onDragDropListener.getMovePointer(this);
		if (drawable == null)
			return;

		mMovePointerParam = new WindowManager.LayoutParams();
		mMovePointerParam.gravity = Gravity.TOP | Gravity.LEFT;
		mMovePointerParam.x = x - mDragPointX + mXOffset;
		mMovePointerParam.y = y - mDragPointY + mYOffset;
		mMovePointerParam.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mMovePointerParam.width = WindowManager.LayoutParams.MATCH_PARENT;
		mMovePointerParam.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE//
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE //
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON//
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN//
				| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
		mMovePointerParam.format = PixelFormat.TRANSLUCENT;
		mMovePointerParam.alpha = 0.7f;
		mMovePointerParam.windowAnimations = 0;

		Context context = getContext();
		ImageView iv = new ImageView(context);
		iv.setMinimumHeight(getDividerHeight());
		iv.setImageDrawable(drawable);

		mWindowManager.addView(iv, mMovePointerParam);
		mMovePointerView = iv;
	}

	private void drawDrager(int x, int y) {

		if (mRemoveMode == REMOVEMODE.SLIDE) {
			float alpha = 1.0f;
			int width = getWidth();
			if (x > width / 2) {
				alpha = ((float) (width - x)) / (width / 2);
			}
			mDragerParams.alpha = alpha;
		}

		if (mMoveX) {
			mDragerParams.x = x - mDragPointX + mXOffset;
		} else {
			mDragerParams.x = 0 + mXOffset + getPaddingLeft();
		}
		mDragerParams.y = y - mDragPointY + mYOffset;
		mWindowManager.updateViewLayout(mDragView, mDragerParams);
	}

	private void drawMovePointer(int x, int y) {
		View v = getView(x, y);
		if (v == null || mMovePointerView == null)
			return;
		Rect r = mTempRect;
		v.getHitRect(r);
		int dividerHeight = getDividerHeight();
		if (y <= r.centerY()) {// 위
			mMovePointerParam.y = r.top + mYOffset - dividerHeight;
		}
		if (y > r.centerY()) {// 아래
			mMovePointerParam.y = r.bottom + mYOffset;
		}
		mMovePointerParam.x = 0 + mXOffset + getPaddingLeft();
		mWindowManager.updateViewLayout(mMovePointerView, mMovePointerParam);

	}

	private void removeDrager() {
		final WindowManager wm = mWindowManager;

		if (mDragView != null) {
			mDragView.setVisibility(GONE);
			wm.removeView(mDragView);
			mDragView.setImageDrawable(null);
			mDragView = null;
		}
		if (mDragBitmap != null) {
			mDragBitmap.recycle();
			mDragBitmap = null;
		}

	}

	private void removeMovePointer() {
		final WindowManager wm = mWindowManager;
		if (mMovePointerView != null) {
			mMovePointerView.setVisibility(GONE);
			wm.removeView(mMovePointerView);
			mMovePointerView.setImageDrawable(null);
			mMovePointerView = null;
		}
	}

	/**
	 * @param removemode
	 *            REMOVEMODE
	 * @param trashView
	 *            if removemode==REMOVEMODE.TRASH use this other null
	 */
	public void setRemoveMode(REMOVEMODE removemode, View trashView) {
		mRemoveMode = removemode;
		if (mRemoveMode == REMOVEMODE.TRASH_AUTO) {
			if (trashView == null)
				throw new IllegalArgumentException();
			mTrashView = trashView;
			trashView.setVisibility(View.GONE);
		}

		if (mRemoveMode == REMOVEMODE.TRASH) {
			if (trashView == null)
				throw new IllegalArgumentException();
			if (!(trashView instanceof DragList.TrashListener))
				throw new IllegalArgumentException("must trashView be DragList.TrashListener");
			mTrashView = trashView;
		}

	}

	public int getDragPosition() {
		return mDragPosition;
	}

	public View getDragView() {
		return mDragView;
	}

	public void setMoveX(boolean b) {
		mMoveX = b;
	}

	// util
	public boolean containView(int resid, int x, int y) {
		View view_drag = getView(x, y);

		final View v = view_drag.findViewById(resid);
		if (v == null || v.getVisibility() == View.GONE)
			return false;

		final Rect outRect = mTempRect;
		v.getHitRect(outRect);
		outRect.offset(view_drag.getLeft(), view_drag.getTop());
		return outRect.contains(x, y);
	}

	public View getView(int x, int y) {
		return getView(pointToPosition(x, y));
	}

	public View getView(int position) {
		return getChildAt(position - getFirstVisiblePosition());
	}

	public int getPosition(int x, int y) {
		return super.pointToPosition(x, y);
	}

	public int getPosition(View view) {
		return super.getPositionForView(view);
	}

	public boolean isTrash(int x, int y) {
		final Rect r = mTempRect;
		getHitRect(r);
		int xx = x + r.left;
		int yy = y + r.top;
		mTrashView.getHitRect(r);
		return r.contains(xx, yy);
	}

}
