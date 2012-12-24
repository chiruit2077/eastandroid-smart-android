package android.view;

import android.common.BaseV;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.UT;

/**
 * @mix djrain
 * 
 */
public class HorizontalPagerPointer {

	public interface HorizontalPagerPointerViewGroup {
		int getChildCount();
		int getCurrentScreen();
		Rect getRect();
	}

	private Rect bounds = new Rect();
	private Rect sum = new Rect();

	private HorizontalPagerPointerViewGroup mParent;

	public HorizontalPagerPointer(HorizontalPagerPointerViewGroup parent) {
		mParent = parent;
	}

	private int mPointerGravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
	private float mPointerDx = 0f;
	private float mPointerDy = 0f;
	private float mPointerSize = BaseV.px(6);
	private int mColorSelected = Color.RED;
	private int mColorNomal = Color.GRAY;

	public HorizontalPagerPointer setSize(float size) {
		mPointerSize = size;
		return this;
	}
	public HorizontalPagerPointer setColor(int color_selected, int color_normal) {
		mColorSelected = color_selected;
		mColorNomal = color_normal;
		return this;
	}
	public HorizontalPagerPointer setOffset(int dx, int dy) {
		mPointerDx = dx;
		mPointerDy = dy;
		return this;
	}
	public HorizontalPagerPointer setGravity(int gravity) {
		mPointerGravity = gravity;
		return this;
	}

	public void draw(Canvas canvas) {
		int N = mParent.getChildCount();
		bounds.set(0, 0, (int) mPointerSize, (int) mPointerSize);
		sum.setEmpty();
		for (int i = 0; i < N; i++) {
			sum.union(bounds);
			bounds.offset(bounds.width() + bounds.width() / 2, 0);
		}
		canvas.save();

		final Rect outRect = new Rect();
		final Rect container = mParent.getRect();
		final int h = sum.height();
		final int w = sum.width();
		final int gravity = mPointerGravity;
		Gravity.apply(gravity, w, h, container, outRect);

		canvas.translate(outRect.left + mPointerDx, outRect.top + mPointerDy);

		bounds.set(0, 0, (int) mPointerSize, (int) mPointerSize);
		for (int i = 0; i < N; i++) {
			if (mParent.getCurrentScreen() == i)
				UT.drawBall(canvas, bounds, mColorSelected);
			else
				UT.drawBall(canvas, bounds, mColorNomal);
			bounds.offset(bounds.width() + bounds.width() / 2, 0);
		}
		canvas.restore();
	}
}
