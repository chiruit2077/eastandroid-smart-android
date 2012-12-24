package android.view;

import android.common.BaseV;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.HorizontalPagerPointer.HorizontalPagerPointerViewGroup;
import android.widget.Scroller;

/**
 * @mix djrain
 * 
 */
public class HorizontalPager extends ViewGroup implements HorizontalPagerPointerViewGroup {
	//화면의 크기 비율 가로크기에 대한 새로 비율
	private float mWHRatio = -1;

	public void setWHRatio(float ratio) {
		mWHRatio = ratio;
	}

	private static final int SPACE = 3;

	public HorizontalPagerPointer mHorizontalPagerPointer = new HorizontalPagerPointer(this);

	/*
	 * How long to animate between screens when programmatically setting with
	 * setCurrentScreen using the animate parameter
	 */
	private static final int ANIMATION_SCREEN_SET_DURATION_MILLIS = 500;
	// What fraction (1/x) of the screen the user must swipe to indicate a page change
	private static final int FRACTION_OF_SCREEN_WIDTH_FOR_SWIPE = 4;
	private static final int INVALID_SCREEN = -1;
	/*
	 * Velocity of a swipe (in density-independent pixels per second) to force a
	 * swipe to the next/previous screen. Adjusted into
	 * mDensityAdjustedSnapVelocity on init.
	 */
	private static final int SNAP_VELOCITY_DIP_PER_SECOND = 600;
	// Argument to getVelocity for units to give pixels per second (1 = pixels per millisecond).
	private static final int VELOCITY_UNIT_PIXELS_PER_SECOND = 1000;

	enum TOUCH_STATE {
		NONE, HORIZONTAL_SCROLLING, VERTICAL_SCROLLING
	};

	private TOUCH_STATE mTouchState = TOUCH_STATE.NONE;

	private String mTitle = "";
	private static final float ITEM_TEXT_CX = 0.5f;
	private static final float ITEM_TEXT_CY = BaseV.px(10f);
	Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);

	private int mCurrentScreen;
	private int mDensityAdjustedSnapVelocity;
	private boolean mFirstLayout = true;
	private float mLastMotionX;
	private float mLastMotionY;

	private OnScreenSwitchListener mOnScreenSwitchListener;

	public static interface OnScreenSwitchListener {
		void onScreenSwitched(int screen);
	}

	private int mMaximumVelocity;
	private int mNextScreen = INVALID_SCREEN;
	private Scroller mScroller;
	private int mTouchSlop;

	private VelocityTracker mVelocityTracker;

	Rect bounds = new Rect();
	Rect sum = new Rect();

	private boolean mIsForground = false;

	private boolean mIsDescription = false;

	private boolean mIsPointer = false;

	private boolean mIsNotMove = false;

	private Rect mRect = new Rect();

	public HorizontalPager(final Context context) {
		super(context);
		init();
	}

	public HorizontalPager(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mScroller = new Scroller(getContext());

		DisplayMetrics displayMetrics = new DisplayMetrics();
		((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
		mDensityAdjustedSnapVelocity = (int) (displayMetrics.density * SNAP_VELOCITY_DIP_PER_SECOND);

		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

		textPaint.setTextAlign(Align.CENTER);
		textPaint.setColor(0xff000000);
		textPaint.setTextSize(BaseV.px(14));
	}

	@Override
	protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
		int specWidthMode = MeasureSpec.getMode(widthMeasureSpec);
		int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);
		int specHeightMode = MeasureSpec.getMode(heightMeasureSpec);

		if (specHeightMode == MeasureSpec.AT_MOST && specWidthMode == MeasureSpec.AT_MOST) {
			throw new IllegalArgumentException("not allow both AT_MOST");
		}

		if (0 < mWHRatio) {
			if (specHeightMode == MeasureSpec.AT_MOST && specWidthMode == MeasureSpec.EXACTLY) {
				measuredHeight = (int) ((float) measuredWidth * (1f / mWHRatio));
			}

			if (specWidthMode == MeasureSpec.AT_MOST && specHeightMode == MeasureSpec.EXACTLY) {
				measuredWidth = (int) ((float) measuredHeight * mWHRatio);
			}
		}

		int _widthMeasureSpec = MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY);
		int _heightMeasureSpec = MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY);

		super.onMeasure(_widthMeasureSpec, _heightMeasureSpec);

		measureChildren(_widthMeasureSpec, _heightMeasureSpec);

		if (mFirstLayout) {
			scrollTo(getScrollX(mCurrentScreen), 0);
			mFirstLayout = false;
		}
	}

	@Override
	protected void onLayout(final boolean changed, final int l, final int t, final int r, final int b) {

		int left_sum = getPaddingLeft();
		final int count = getChildCount();

		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE) {
				final int cw = child.getMeasuredWidth();
				final int ch = child.getMeasuredHeight();

				int ll = left_sum;
				int tt = getPaddingTop();
				int rr = ll + cw;
				int bb = tt + ch;
				child.layout(ll, tt, rr, bb);
				left_sum += cw + SPACE;
			}
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mRect.set(0, 0, w, h);
		setCurrentScreen(mCurrentScreen, false);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (mIsNotMove)
			return super.onInterceptTouchEvent(event);

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN :

				mTouchState = TOUCH_STATE.NONE;
				mLastMotionX = event.getX();
				mLastMotionY = event.getY();
				if (!mScroller.isFinished()) {
					mScroller.abortAnimation();
					mTouchState = TOUCH_STATE.HORIZONTAL_SCROLLING;
				}

				break;
			case MotionEvent.ACTION_MOVE :
				if (mTouchState == TOUCH_STATE.NONE) {
					if (Math.abs(mLastMotionX - event.getX()) > mTouchSlop) {
						mTouchState = TOUCH_STATE.HORIZONTAL_SCROLLING;
					} else if (Math.abs(mLastMotionY - event.getY()) > mTouchSlop) {
						mTouchState = TOUCH_STATE.VERTICAL_SCROLLING;
					}
				}
				break;
			case MotionEvent.ACTION_CANCEL :
			case MotionEvent.ACTION_UP :
				mTouchState = TOUCH_STATE.NONE;
				break;
			default :
				break;
		}

		return mTouchState == TOUCH_STATE.HORIZONTAL_SCROLLING;
	}

	@Override
	public boolean onTouchEvent(final MotionEvent event) {

		if (getChildCount() < 2)
			return super.onTouchEvent(event);

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);

		final int action = event.getAction();
		switch (action) {
			case MotionEvent.ACTION_DOWN :
				mTouchState = TOUCH_STATE.NONE;
				mLastMotionX = event.getX();
				mLastMotionY = event.getY();
				if (!mScroller.isFinished()) {
					mScroller.abortAnimation();
					mTouchState = TOUCH_STATE.HORIZONTAL_SCROLLING;
				}
				break;
			case MotionEvent.ACTION_MOVE :
				if (mTouchState == TOUCH_STATE.NONE) {
					if (Math.abs(mLastMotionX - event.getX()) > mTouchSlop) {
						mTouchState = TOUCH_STATE.HORIZONTAL_SCROLLING;
					} else if (Math.abs(mLastMotionY - event.getY()) > mTouchSlop) {
						mTouchState = TOUCH_STATE.VERTICAL_SCROLLING;
					}
				}

				if (mTouchState == TOUCH_STATE.HORIZONTAL_SCROLLING) {
					// Scroll to follow the motion event
					final int dx = (int) (event.getX() - mLastMotionX);
					final int scrollX = getScrollX();
					final int scrollMax = getChildAt(getChildCount() - 1).getLeft() - getPaddingLeft();
					final int scrollMin = 0;
					if (dx < 0 && scrollX < scrollMax) {//next
						scrollTo(Math.min(scrollMax, scrollX - dx), 0);
					} else if (dx > 0 && scrollX > scrollMin) { //prev
						scrollTo(Math.max(scrollMin, scrollX - dx), 0);
					}
					mLastMotionX = event.getX();
				}

				break;

			case MotionEvent.ACTION_UP :
				if (mTouchState == TOUCH_STATE.HORIZONTAL_SCROLLING) {
					final VelocityTracker velocityTracker = mVelocityTracker;
					velocityTracker.computeCurrentVelocity(VELOCITY_UNIT_PIXELS_PER_SECOND, mMaximumVelocity);
					int velocityX = (int) velocityTracker.getXVelocity();
					if (velocityX > mDensityAdjustedSnapVelocity && mCurrentScreen > 0) {
						snapToScreen(mCurrentScreen - 1);
					} else if (velocityX < -mDensityAdjustedSnapVelocity && mCurrentScreen < getChildCount() - 1) {
						snapToScreen(mCurrentScreen + 1);
					} else {
						snapToDestination();
					}
				}

				mTouchState = TOUCH_STATE.NONE;
				if (mVelocityTracker != null) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}

				break;
			case MotionEvent.ACTION_CANCEL :

				mTouchState = TOUCH_STATE.NONE;
				if (mVelocityTracker != null) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}

				break;
			default :
				break;
		}

		if (mTouchState == TOUCH_STATE.HORIZONTAL_SCROLLING)
			event.setAction(MotionEvent.ACTION_CANCEL);
		return super.onTouchEvent(event);
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		} else if (mNextScreen != INVALID_SCREEN) {
			mCurrentScreen = Math.max(0, Math.min(mNextScreen, getChildCount() - 1));

			// Notify observer about screen change
			if (mOnScreenSwitchListener != null) {
				mOnScreenSwitchListener.onScreenSwitched(mCurrentScreen);
			}

			mNextScreen = INVALID_SCREEN;
		}
	}

	private int getScrollX(int screen) {
		return screen * (getWidth() - getPaddingLeft() - getPaddingRight()) + (screen * SPACE);
	}

	/**
	 * Snaps to the screen we think the user wants (the current screen for very
	 * small movements; the next/prev screen for bigger movements).
	 */
	private void snapToDestination() {
		final int screenWidth = (getWidth() - getPaddingLeft() - getPaddingRight());
		int whichScreen = mCurrentScreen;
		int deltaX = getScrollX() - getScrollX(mCurrentScreen);
		if (deltaX == 0)
			return;

		// Check if they want to go to the prev. screen
		if ((deltaX < 0) && mCurrentScreen != 0 && ((screenWidth / FRACTION_OF_SCREEN_WIDTH_FOR_SWIPE) < -deltaX)) {
			whichScreen--;
			// Check if they want to go to the next screen
		} else if ((deltaX > 0) && (mCurrentScreen + 1 != getChildCount()) && ((screenWidth / FRACTION_OF_SCREEN_WIDTH_FOR_SWIPE) < deltaX)) {
			whichScreen++;
		}

		snapToScreen(whichScreen);
	}

	/**
	 * Snap to a specific screen, animating automatically for a duration
	 * proportional to the distance left to scroll.
	 * 
	 * @param whichScreen
	 *            Screen to snap to
	 */
	private void snapToScreen(final int whichScreen) {
		snapToScreen(whichScreen, -1);
	}

	/**
	 * Snaps to a specific screen, animating for a specific amount of time to
	 * get there.
	 * 
	 * @param whichScreen
	 *            Screen to snap to
	 * @param duration
	 *            -1 to automatically time it based on scroll distance; a
	 *            positive number to make the scroll take an exact duration.
	 */
	private void snapToScreen(final int whichScreen, final int duration) {
		/*
		 * Modified by Yoni Samlan: Allow new snapping even during an ongoing
		 * scroll animation. This is intended to make HorizontalPager work as
		 * expected when used in conjunction with a RadioGroup used as "tabbed"
		 * controls.
		 */
		mNextScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		final int newX = getScrollX(mNextScreen);
		final int delta = newX - getScrollX();

		if (duration < 0) {
			mScroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta) * 2);
		} else {
			mScroller.startScroll(getScrollX(), 0, delta, 0, duration);
		}

		invalidate();
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);

		canvas.translate(getScrollX(), getScrollY());

		if (mIsForground && getBackground() != null)
			getBackground().draw(canvas);

		if (mIsDescription)
			drawText(canvas);

		if (mIsPointer)
			mHorizontalPagerPointer.draw(canvas);

		canvas.translate(-getScrollX(), -getScrollY());
	}

	private void drawText(Canvas canvas) {
		final String text = mTitle;
		if (text != null && text.length() <= 0)
			return;
		canvas.drawText(text, getWidth() * ITEM_TEXT_CX, getHeight() - ITEM_TEXT_CY, textPaint);
	}

	public void orientationSnapToScreen(Context ctx) {
		mNextScreen = Math.max(0, Math.min(getCurrentScreen(), getChildCount() - 1));
		final int newX = getScrollX(mNextScreen);
		final int delta = newX - getScrollX();

		mScroller.startScroll(getScrollX(), 0, delta, 0, 0);
	}

	public int getCurrentScreen() {
		return mCurrentScreen;
	}

	@Override
	public Rect getRect() {
		return mRect;
	}

	public void setCurrentScreen(final int currentScreen, final boolean animate) {
		mCurrentScreen = Math.max(0, Math.min(currentScreen, getChildCount() - 1));
		if (animate) {
			snapToScreen(currentScreen, ANIMATION_SCREEN_SET_DURATION_MILLIS);
		} else {
			scrollTo(getScrollX(mCurrentScreen), 0);
		}
		invalidate();
	}

	public void setOnScreenSwitchListener(final OnScreenSwitchListener onScreenSwitchListener) {
		mOnScreenSwitchListener = onScreenSwitchListener;
	}

	public void setTitle(String text) {
		this.mTitle = text;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setForground(boolean b) {
		mIsForground = b;
	}

	public void setDescription(boolean b) {
		mIsDescription = b;
	}

	public void setNoSlide(boolean b) {
		mIsNotMove = b;
	}

	public HorizontalPagerPointer setPointer(boolean b) {
		mIsPointer = b;
		return mHorizontalPagerPointer;
	}

	public HorizontalPagerPointer getPointer() {
		return mHorizontalPagerPointer;
	}

}
