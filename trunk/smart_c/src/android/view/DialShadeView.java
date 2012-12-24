package android.view;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.MathEx;

/**
 * @author djrain
 * 
 */
public class DialShadeView extends View {

	private static final int AfterImage = 20;
	private Paint linesPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

	public static ArrayList<PointF> lines = new ArrayList<PointF>();
	public static ArrayList<Float> horizontal = new ArrayList<Float>();
	public static ArrayList<Float> vertical = new ArrayList<Float>();
	private Shader mShader;

	private Paint mPaintShader = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
	private RectF oval = new RectF();
	PointF c = new PointF(480f / 2f, 800f / 2f);
	private Matrix localM = new Matrix();
	private boolean flagEnd;

	public DialShadeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		linesPaint.setColor(Color.GRAY);
	}

	public DialShadeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		linesPaint.setColor(Color.GRAY);
	}

	public DialShadeView(Context context) {
		super(context);
		linesPaint.setColor(Color.GRAY);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawShader(canvas);
	}

	public void addPointF(MotionEvent event) {
		int[] location = new int[2];
		getLocationInWindow(location);

		lines.add(new PointF(event.getRawX() - location[0], event.getRawY() - location[1]));
		flagEnd = false;
		if (lines.size() > AfterImage)
			lines.remove(0);
		if (lines.size() >= 2)
			invalidate();

		if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP) {
			end();
		}
	}

	public void end() {
		flagEnd = true;
	}

	private void drawShader(Canvas canvas) {
		if (lines.size() < 2) {
			flagEnd = false;
			return;
		}

		ArrayList<PointF> rfs = new ArrayList<PointF>(lines);
		Collections.reverse(rfs);
		float offestDegrees = MathEx.atan3sum(rfs, c);

		if (offestDegrees > 0) {
			mShader = new SweepGradient(c.x, c.y, new int[]{Color.RED, Color.TRANSPARENT}, new float[]{0.0f, (float) offestDegrees / 360F});
		} else {
			mShader = new SweepGradient(c.x, c.y, new int[]{Color.BLUE, Color.TRANSPARENT}, new float[]{0.0f, (float) -offestDegrees / 360F});
			localM.setScale(1, -1, c.x, c.y);
			mShader.setLocalMatrix(localM);
		}
		mPaintShader.setShader(mShader);

		canvas.save();
		canvas.rotate(MathEx.atan2(rfs.get(0), c), c.x, c.y);
		canvas.drawArc(oval, 0F, (float) 360, true, mPaintShader);
		canvas.restore();

		if (flagEnd) {
			lines.remove(0);
			invalidate();
		} else {
			PointF p1 = rfs.get(0);
			PointF p2 = rfs.get(1);
			final int deltaX = (int) (p2.x - p1.x);
			final int deltaY = (int) (p2.y - p1.y);
			int distance = (deltaX * deltaX) + (deltaY * deltaY);
			int slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
			if (distance < slop * slop) {
				lines.remove(0);
				invalidate();
			}
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		//		Log.l();
		horizontal.add((float) h / 2F);
		vertical.add((float) w / 2F);

		c.x = (float) w / 2F;
		c.y = (float) h / 2F;
		oval.set(c.x, c.y, c.x, c.y);
		oval.inset(-Math.min(w, h) / 2, -Math.min(w, h) / 2);

		super.onSizeChanged(w, h, oldw, oldh);
	}

}