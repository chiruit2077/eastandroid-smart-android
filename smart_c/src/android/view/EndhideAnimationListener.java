package android.view;

import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class EndhideAnimationListener implements AnimationListener {

	private final View mView;

	public EndhideAnimationListener(View view) {
		super();
		mView = view;
	}

	@Override
	public void onAnimationEnd(Animation arg0) {
		mView.setVisibility(View.GONE);
		mView.setAnimation(null);
	}

	@Override
	public void onAnimationRepeat(Animation arg0) {
	}

	@Override
	public void onAnimationStart(Animation arg0) {
	}

}
