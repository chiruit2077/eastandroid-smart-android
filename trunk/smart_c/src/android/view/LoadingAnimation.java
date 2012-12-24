package android.view;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation.AnimationListener;

public class LoadingAnimation {

	public static void startOpenAnimation(View v) {
		v.setVisibility(View.VISIBLE);
		final Rotate3dAnimation rotation = new Rotate3dAnimation(360, 270, 0, v.getHeight() / 2, 2400.0f);
		rotation.setDuration(1500);
		rotation.setInterpolator(new AccelerateInterpolator());
		rotation.setAnimationListener(new EndhideAnimationListener(v));
		v.startAnimation(rotation);
	}

	public static void startCloseAnimation(View v, AnimationListener animationListener) {
		v.setVisibility(View.VISIBLE);
		final Rotate3dAnimation rotation = new Rotate3dAnimation(270, 360, 0, v.getHeight() / 2, 2400.0f);
		rotation.setDuration(1500);
		rotation.setAnimationListener(animationListener);
		v.startAnimation(rotation);
	}

	public static void startOpenRAnimation(View v) {
		v.setVisibility(View.VISIBLE);
		final Rotate3dAnimation rotation = new Rotate3dAnimation(0, 90, v.getWidth(), v.getHeight() / 2, 2400.0f);
		rotation.setDuration(1500);
		rotation.setInterpolator(new AccelerateInterpolator());
		rotation.setAnimationListener(new EndhideAnimationListener(v));
		v.startAnimation(rotation);
	}

	public static void startCloseRAnimation(View v, AnimationListener animationListener) {
		v.setVisibility(View.VISIBLE);
		final Rotate3dAnimation rotation = new Rotate3dAnimation(90, 0, v.getWidth(), v.getHeight() / 2, 2400.0f);
		rotation.setDuration(1500);
		rotation.setAnimationListener(animationListener);
		v.startAnimation(rotation);
	}
}
