package android.view;

import android.common.BaseV;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

public class StitchTextView extends TextView {
	Rect bounds = new Rect();
	Paint b_Paint = new Paint();
	Paint w_Paint = new Paint();

	public StitchTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		create(context);
	}

	public StitchTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		create(context);
	}

	public StitchTextView(Context context) {
		super(context);
		create(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		this.getPaint().getTextBounds((String) getText(), 0, getText().length(), bounds);

		float width = this.getWidth();
		float b_width = bounds.width();
		float b_height = this.getHeight() / 2;

		canvas.drawLine(0, b_height, startX(width, b_width), b_height, b_Paint);
		canvas.drawLine(width, b_height, endX(width, b_width), b_height, b_Paint);

		b_height += BaseV.px(1.5f);
		canvas.drawLine(0, b_height, startX(width, b_width), b_height, w_Paint);
		canvas.drawLine(width, b_height, endX(width, b_width), b_height, w_Paint);
	}

	private float startX(float width, float b_width) {
		return (width - b_width) / 2;
	}

	private float endX(float width, float b_width) {
		return width - (width - b_width) / 2;
	}

	private void create(Context context) {
		b_Paint.setStrokeWidth(BaseV.px(1.5f));
		b_Paint.setColor(0xff8cadab);
		b_Paint.setPathEffect(new DashPathEffect(new float[]{BaseV.px(5f), BaseV.px(5f)}, 0));

		w_Paint.setStrokeWidth(BaseV.px(1.5f));
		w_Paint.setColor(Color.WHITE);
		w_Paint.setPathEffect(new DashPathEffect(new float[]{BaseV.px(5f), BaseV.px(5f)}, 0));
	}

}
