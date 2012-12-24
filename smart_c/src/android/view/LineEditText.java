package android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * @author djrain
 * 
 */
public class LineEditText extends EditText {

	private int mW;
	private int mH;
	private Paint Line1 = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
	private Paint Line2 = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
	private int BgColor = 0xffffeeb3;
	private int mLineHeight;
	private int mBaseline;

	public LineEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		create();
	}

	public LineEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		create();
	}

	public LineEditText(Context context) {
		super(context);
		create();

	}

	private void create() {

		Line1.setColor(0xffaacea8);
		Line1.setStyle(Paint.Style.STROKE);
		Line1.setStrokeWidth(1);

		Line2.setColor(0xffeca67d);
		Line2.setStyle(Paint.Style.STROKE);
		Line2.setStrokeWidth(2);

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mW = w;
		mH = h;
		mLineHeight = getLineHeight();
		mBaseline = getBaseline();
	}
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(BgColor);

		int y = 0;
		int line = 0;
		while (y < mH || line < getLineCount()) {
			y = mBaseline + (mLineHeight * line) + 1 + (mLineHeight / 4);
			canvas.drawLine(0, y, mW, y, Line1);
			line++;
		}

		canvas.drawLine(50, 0, 50, y, Line2);
		canvas.drawLine(60, 0, 60, y, Line2);

		super.onDraw(canvas);
	}
}