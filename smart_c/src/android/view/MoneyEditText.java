package android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * @author djrain
 * 
 */
public class MoneyEditText extends EditText {

	Context mContext;
	private int mTouchSlopSquare;
	private String mFormat = "%,d";

	private int mW;
	private int mH;
	private boolean mIsInnerEdit;

	private long maxMoney = Long.MAX_VALUE;
	private long minMoney = Long.MIN_VALUE;

	public void setMaxMoney(int maxMoney) {
		this.maxMoney = maxMoney;
	}

	public void setMinMoney(int minMoney) {
		this.minMoney = minMoney;
	}

	public long getMaxMoney() {
		return maxMoney;
	}

	public long getMinMoney() {
		return minMoney;
	}

	public void setFormat(String format) {
		mFormat = format;
	}

	public MoneyEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		create(context);
	}

	public MoneyEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		create(context);
	}

	public MoneyEditText(Context context) {
		super(context);
		create(context);
	}

	private void create(Context context) {
		mContext = context;
		setInputType(EditorInfo.TYPE_CLASS_NUMBER);
		setSingleLine();
		setSelectAllOnFocus(true);

		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mTouchSlopSquare = configuration.getScaledTouchSlop() * configuration.getScaledTouchSlop();

		createPaint();
	}

	public void setMoney(long money) {
		if (money > maxMoney) {
			money = maxMoney;
		}
		if (money < minMoney) {
			money = minMoney;
		}

		try {
			setText(String.format(mFormat, money));
		} catch (Exception e) {
			setText("0");
		}
	}

	public long getMoney() {
		try {
			return Long.parseLong(getText().toString().replaceAll("[^0-9-]", ""));
		} catch (Exception e) {
			return 0L;
		}
	}

	@Override
	protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
		if (mIsInnerEdit)
			return;

		mIsInnerEdit = true;

		int lastSelectionEnd = getText().length() - getSelectionEnd();

		try {
			setMoney(getMoney());
		} catch (Exception e) {
			setMoney(0);
		}

		try {
			int selection = getText().length() - lastSelectionEnd;
			if (selection < 0)
				selection = 0;
			if (selection > getText().length())
				selection = getText().length();
			setSelection(selection);
		} catch (IndexOutOfBoundsException e) {
			setSelection(getText().length());
		}

		super.onTextChanged(text, start, lengthBefore, lengthAfter);

		mIsInnerEdit = false;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mW = w;
		mH = h;

		Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		x = display.getWidth();
		y = display.getHeight();
		c.set(x / 2f, y / 2f);
		p3.set(x / 2f, y / 2f);
		p2.set(x / 2f, y / 2f);
		p1.set(x / 2f, y / 2f);

		getLocationOnScreen(location);
		setProgressStrokeWidth();
	}

	//Roll
	private boolean isRoll = false;

	private static final float UNIT = 10f;

	private float x = 480;
	private float y = 800;

	PointF c = new PointF(x / 2f, y / 2f);
	PointF p3 = new PointF(x / 2f, y / 2f);
	PointF p2 = new PointF(x / 2f, y / 2f);
	PointF p1 = new PointF(x / 2f, y / 2f);

	Matrix mMatrix = new Matrix();
	private float[] src = new float[2];
	private float[] dst = new float[2];
	private float offestSum;
	private long offestSumLast;
	private boolean mIsMove = false;
	private long mOrgMoney = 0;

	/**
	 * 드래그 회전으로 금액변경 100원단위 이동하고 이하절삭
	 * 
	 * @param b
	 *            true:사용함 false:사용안함
	 */
	public void setRoll(boolean b) {
		isRoll = b;
	}

	public void setRoll(boolean b, OnChangePoint onChangePointListener) {
		isRoll = b;
		setOnChangePoint(onChangePointListener);
	}

	public interface OnChangePoint {
		public void onChangePoint(MotionEvent event);
	}

	OnChangePoint onChangePointListener;

	/**
	 * 그래그 회전시 이동 포인터를 리턴 받을 리슨어
	 * 
	 * @param onChangePointListener
	 */
	public void setOnChangePoint(OnChangePoint onChangePointListener) {
		this.onChangePointListener = onChangePointListener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// MotionEventTrecer.onTouchEvent(event);
		if (!isRoll || !this.isEnabled())
			return super.onTouchEvent(event);

		p3.set(p2);
		p2.set(p1);
		p1.set(event.getRawX(), event.getRawY());

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			ViewParent parent = getParent();
			while (parent != null) {
				if (parent instanceof StopScrollView) {
					((StopScrollView) parent).setStopScroll(true);
				}
				parent = parent.getParent();
			}

			mIsMove = false;
			mOrgMoney = getMoney();
			offestSumLast = 0;
			offestSum = 0;
		}

		if (event.getAction() != MotionEvent.ACTION_DOWN) {
			if (mIsMove) {
				mMatrix.setRotate((float) -Math.toDegrees(Math.atan2(p2.y - c.y, p2.x - c.x)), c.x, c.y);
				src[0] = p1.x;
				src[1] = p1.y;
				mMatrix.mapPoints(dst, src);
				offestSum += Math.toDegrees(Math.atan2(dst[1] - c.y, dst[0] - c.x));

				if (offestSumLast != (long) (offestSum / UNIT)) {
					offestSumLast = (long) (offestSum / UNIT);
					playSoundEffect(SoundEffectConstants.CLICK);
					setMoney(mOrgMoney / 100 * 100 + offestSumLast * 100);
				}

				if (onChangePointListener != null) {
					//					final int deltaX = (int) (p2.x - p1.x);
					//					final int deltaY = (int) (p2.y - p1.y);
					//					int distance = (deltaX * deltaX) + (deltaY * deltaY);
					//					if (distance > mTouchSlopSquare)
					onChangePointListener.onChangePoint(event);

				}
			} else {
				final int deltaX = (int) (p2.x - p1.x);
				final int deltaY = (int) (p2.y - p1.y);
				int distance = (deltaX * deltaX) + (deltaY * deltaY);
				if (distance > mTouchSlopSquare) {
					mIsMove = true;
					InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.hideSoftInputFromWindow(getWindowToken(), 0);
				}
			}
		}
		if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {

			p3.set(c);
			p2.set(c);
			p1.set(c);

			ViewParent parent = getParent();
			while (parent != null) {
				if (parent instanceof StopScrollView) {
					((StopScrollView) parent).setStopScroll(false);
				}
				parent = parent.getParent();
			}
		}

		if (mIsMove)
			event.setAction(MotionEvent.ACTION_CANCEL);
		return super.onTouchEvent(event);
	}

	//Seek
	private long mProgressMin = 0;
	private long mProgressMax = 0;

	Paint progressPaint;
	private float mProgressStrokeWidth;
	private int[] location = new int[2];
	float[] values = new float[9];

	private void createPaint() {
		progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
		progressPaint.setColor(0x55ff0000);
		progressPaint.setStrokeCap(Cap.ROUND);
		progressPaint.setStyle(Style.STROKE);
	}

	private void setProgressStrokeWidth() {
		mProgressStrokeWidth = (float) mH / 10f < 1f ? 1 : mH / 10f;
		//		Log.l(mProgressStrokeWidth);
		progressPaint.setStrokeWidth(mProgressStrokeWidth);
	}

	/**
	 * 하단 프로그래스바 설정
	 * 
	 * @param max
	 */
	public void setProgressMax(long max) {
		mProgressMax = max;
		invalidate();
	}

	/**
	 * 하단 프로그래스바 설정
	 * 
	 * @param min
	 */
	public void setProgressMin(long min) {
		mProgressMin = min;
		invalidate();
	}

	/**
	 * 하단 프로그래스바 설정
	 * 
	 * @param min
	 * @param max
	 */
	public void setProgressMinMax(long min, long max) {
		setProgressMin(min);
		setProgressMax(max);
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawProgress(canvas);
	}

	private void drawProgress(Canvas canvas) {
		if (mProgressMax - mProgressMin <= 0)
			return;

		canvas.save();

		canvas.getMatrix().getValues(values);
		canvas.translate(-values[2] + location[0], 0);

		float c = (float) mW * ((float) getMoney() / (float) (mProgressMax - mProgressMin));
		canvas.drawLine(0, mH - mProgressStrokeWidth, c, mH - mProgressStrokeWidth, progressPaint);

		canvas.restore();
	}
}