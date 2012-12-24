package android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @author djrain
 * 
 */
public class LoadingImageView extends ImageView {

	private boolean isFirst = true;

	public LoadingImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public LoadingImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LoadingImageView(Context context) {
		super(context);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		if (isFirst) {
			LoadingAnimation.startOpenAnimation(LoadingImageView.this);
			isFirst = false;
		}
	}

}