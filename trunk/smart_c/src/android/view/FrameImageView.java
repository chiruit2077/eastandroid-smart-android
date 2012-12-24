package android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

public class FrameImageView extends ImageView {

	public FrameImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FrameImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FrameImageView(Context context) {
		super(context);
	}

//	@Override
//	public void setImageURI(Uri uri) {
//		try {
//			Bitmap bm = GalleryLoader.c().getSampleSizeBitmap(uri, getWidth(), getHeight());
//			setImageBitmap(bm);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		if (getBackground() != null) {
			getBackground().draw(canvas);
		}
	}

}